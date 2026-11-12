package dev.celestiacraft.steam_letters.common.block.engine;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineDriveSpec;
import dev.celestiacraft.steam_letters.client.lang.SlLang;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlock;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlockEntity;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import dev.celestiacraft.steam_letters.common.register.SlParticles;
import dev.celestiacraft.steam_letters.config.common.SteamEngineConfig;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class SteamEngineBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
	public static final String NBT_TANK = "Tank";
	public static final String NBT_HEATUP = "Heatup";
	public static final String NBT_PENDING = "Pending";
	public static final String NBT_STRENGTH = "AppliedStrength";
	public static final String NBT_LIT = "Lit";
	public static final String NBT_WHEEL = "Wheel";
	public static final String NBT_STAGE = "SupplyStage";
	public static final int WHEEL_DISTANCE = 2;
	private static final int STAGES = 4;
	private static final int STARVE_GRACE_TICKS = 20;
	private static final double MIN_SUCK_EFFICIENCY = 0.001D;
	public static final double DEFAULT_STRENGTH = 16384.0D;
	public static final double DEFAULT_SPEED = 32.0D;
	public static final double DEFAULT_CONSUMPTION = 12.0D;
	public static final int DEFAULT_STORAGE = 32000;

	@Getter
	private final SteamEngineDriveSpec driveSpec;

	@Getter
	private final SteamEngineFluidTank tank;

	protected IFluidHandler fluidCapability;

	@Getter
	private int heatup;

	private double pending;
	private int supplyStage;
	private int starvedTicks;

	@Getter
	private boolean lit;

	@Getter
	private float appliedStrength;

	@Nullable
	private BlockPos wheelPos;

	private int syncedHeatup = -1;
	private float syncedStrength = -1.0F;
	private int syncedFluidAmount = -1;

	public SteamEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		driveSpec = ((SteamEngineBlock) state.getBlock()).getDriveSpec();

		int configured = driveSpec.getCapacity();
		int capacity = Math.max(configured > 0 ? configured : DEFAULT_STORAGE, 1);

		tank = new SteamEngineFluidTank(this, capacity);

		fluidCapability = new SteamEngineFluidHandler(tank);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null) {
			return;
		}
		if (level.isClientSide()) {
			clientTick();
			return;
		}
		serverTick();
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level == null || level.isClientSide()) {
			return;
		}

		refreshWheel();

		int fluidAmount = tank.getFluidAmount();
		if (syncedHeatup != heatup || syncedStrength != appliedStrength || syncedFluidAmount != fluidAmount) {
			syncedHeatup = heatup;
			syncedStrength = appliedStrength;
			syncedFluidAmount = fluidAmount;
			sendData();
		}
	}

	private void serverTick() {
		if (wheelPos == null) {
			refreshWheel();
		}

		SteamFlywheelBlockEntity wheel = getWheel();
		if (wheel == null) {
			shutdown();
			return;
		}

		int fullDemand = getDemandPerTick();
		if (fullDemand <= 0) {
			shutdown();
			return;
		}

		int demand = Math.max(1, (int) Math.round(fullDemand * getHeatupRatio()));
		int fromTank = tank.drain(demand, IFluidHandler.FluidAction.EXECUTE).getAmount();
		int remaining = demand - fromTank;
		int fromSource = remaining > 0 ? drawFromBoiler(remaining) : 0;
		int drawn = fromTank + fromSource;
		pending = 0.0D;

		if (drawn <= 0) {
			starvedTicks++;
			if (starvedTicks >= STARVE_GRACE_TICKS) {
				shutdown();
				return;
			}
			warmup(wheel);
			return;
		}
		starvedTicks = 0;

		// 应力档位只看到手蒸汽占满负荷消耗的比例: 汽源抽上来的部分只入账 suck_efficiency, 自带罐里的蒸汽全额入账.
		double supplied = fromTank + fromSource * getSuckEfficiency();
		int stage = getSupplyStage(supplied / fullDemand);
		supplyStage = stage;

		// 预热进度只看是否还有汽, 与档位无关: 进度走完之前不写应力, 也不让飞轮转起来.
		int heatupTicks = (int) Math.ceil(getHeatupTicks());
		if (heatup < heatupTicks) {
			heatup++;
		}

		// 供应不到满负荷消耗的 25%, 或还没预热完: 不旋转, 也不产生应力.
		if (stage <= 0 || heatup < heatupTicks) {
			warmup(wheel);
			return;
		}

		drive(wheel, stage / (double) STAGES);
	}

	private void warmup(SteamFlywheelBlockEntity wheel) {
		if (appliedStrength == 0.0F && !lit && wheel.isGeneratingAt(0.0F, 0.0F)) {
			return;
		}

		appliedStrength = 0.0F;
		wheel.stopImmediately();
		updateLitState(false);
	}

	private void drive(SteamFlywheelBlockEntity wheel, double ratio) {
		float capacity = (float) (getTargetStrength() * Mth.clamp(ratio, 0.0D, 1.0D));
		appliedStrength = capacity;
		wheel.setRotation((float) getTargetSpeed(), capacity);
		updateLitState(true);
	}

	private void shutdown() {
		SteamFlywheelBlockEntity wheel = getWheel();
		boolean wheelStopped = wheel == null || wheel.isGeneratingAt(0.0F, 0.0F);

		if (heatup == 0 && pending == 0.0D && appliedStrength == 0.0F && supplyStage == 0 && !lit && wheelStopped) {
			return;
		}

		heatup = 0;
		pending = 0.0D;
		appliedStrength = 0.0F;
		supplyStage = 0;
		starvedTicks = 0;

		if (wheel != null) {
			wheel.stopImmediately();
		}

		updateLitState(false);
	}

	private int drawFromBoiler(int needed) {
		if (level == null || needed <= 0) {
			return 0;
		}

		Direction back = getFacing().getOpposite();
		BlockEntity source = level.getBlockEntity(worldPosition.relative(back));
		if (source == null) {
			return 0;
		}

		IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, source.getBlockPos(), back.getOpposite());
		if (handler == null) {
			return 0;
		}

		int rawCap = Math.max((int) Math.ceil(getTargetConsumption()), 1);
		int raw = Math.min(needed, rawCap);

		if (handler.getTanks() <= 0) {
			return 0;
		}

		FluidStack steamStack = FluidStack.EMPTY;
		for (int i = 0; i < handler.getTanks(); i++) {
			FluidStack content = handler.getFluidInTank(i);
			if (!content.isEmpty() && SteamEngineFluidTank.isSteamFluid(content)) {
				steamStack = content;
				break;
			}
		}
		if (steamStack.isEmpty()) {
			return 0;
		}

		FluidStack drained = handler.drain(new FluidStack(steamStack.getFluid(), raw), IFluidHandler.FluidAction.EXECUTE);
		return drained.getAmount();
	}

	private void updateLitState(boolean value) {
		if (lit == value) {
			return;
		}
		lit = value;

		if (level == null) {
			return;
		}

		BlockState state = getBlockState();
		if (state.hasProperty(SteamEngineBlock.LIT) && state.getValue(SteamEngineBlock.LIT) != value) {
			level.setBlock(worldPosition, state.setValue(SteamEngineBlock.LIT, value), 3);
		}
	}

	private void refreshWheel() {
		BlockPos found = findWheelPos();

		if (found == null && wheelPos == null) {
			return;
		}
		if (found != null && found.equals(wheelPos)) {
			return;
		}

		wheelPos = found;

		if (found != null && level != null && level.getBlockEntity(found) instanceof SteamFlywheelBlockEntity wheel) {
			Direction engineSide = getFacing().getOpposite();
			if (SteamFlywheelBlock.getConnection(wheel.getBlockState()) != engineSide) {
				SteamFlywheelBlock.setConnection(level, found, wheel.getBlockState(), engineSide);
			}
		}

		sendData();
	}

	@Nullable
	private BlockPos findWheelPos() {
		if (level == null) {
			return null;
		}

		Direction facing = getFacing();
		BlockPos candidate = worldPosition.relative(facing, WHEEL_DISTANCE);
		if (!(level.getBlockEntity(candidate) instanceof SteamFlywheelBlockEntity wheel)) {
			return null;
		}

		Direction occupied = SteamFlywheelBlock.getConnection(wheel.getBlockState());
		if (occupied != null && occupied != facing.getOpposite()) {
			BlockPos other = candidate.relative(occupied, WHEEL_DISTANCE);
			if (level.getBlockEntity(other) instanceof SteamEngineBlockEntity) {
				return null;
			}
		}

		if (!matchesMaterial(wheel)) {
			return null;
		}

		return candidate.immutable();
	}

	/**
	 * 引擎与飞轮的材质必须一致: 青铜引擎只认青铜飞轮, 钢引擎只认钢飞轮.
	 * <p>
	 * 引擎未声明材质时不设限制.
	 *
	 * @param wheel 正对位的飞轮
	 * @return 材质是否匹配
	 */
	private boolean matchesMaterial(SteamFlywheelBlockEntity wheel) {
		if (!(getBlockState().getBlock() instanceof SteamEngineBlock engine)) {
			return true;
		}

		ResourceLocation required = engine.getDriveSpec().getMaterial();

		if (required == null) {
			return true;
		}

		if (!(wheel.getBlockState().getBlock() instanceof SteamFlywheelBlock flywheel)) {
			return false;
		}

		return required.equals(flywheel.getMaterial());
	}

	@Nullable
	private SteamFlywheelBlockEntity getWheel() {
		if (level == null || wheelPos == null) {
			return null;
		}
		return level.getBlockEntity(wheelPos) instanceof SteamFlywheelBlockEntity wheel ? wheel : null;
	}
	private Direction getFacing() {
		BlockState state = getBlockState();
		return state.hasProperty(SteamEngineBlock.FACING) ? state.getValue(SteamEngineBlock.FACING) : Direction.NORTH;
	}
	private double getSuckEfficiency() {
		return Mth.clamp(SteamEngineConfig.SUCK_EFFICIENCY.get(), MIN_SUCK_EFFICIENCY, 1.0D);
	}

	public boolean isSuperheated() {
		return !tank.isEmpty() && tank.getFluid().getFluid().isSame(SlFluids.SUPERHEATED_STEAM.get());
	}

	public double getTargetSpeed() {
		double speed = driveSpec.getSpeed();
		return speed > 0.0D ? speed : DEFAULT_SPEED;
	}

	public double getTargetConsumption() {
		double configured = driveSpec.getConsumption();
		double consumption = configured > 0.0D ? configured : DEFAULT_CONSUMPTION;
		if (isSuperheated()) {
			consumption *= SteamEngineConfig.SUPERHEATED_CONSUMPTION_MULTIPLIER.get();
		}
		return Math.max(consumption, 0.0D);
	}

	public double getHeatupTicks() {
		double seconds = SteamEngineConfig.HEATUP_SECONDS.get();
		if (seconds <= 0.0D) {
			// 0 秒表示无需预热
			return 0.0D;
		}

		double ticks = seconds * 20.0D;
		if (isSuperheated()) {
			double multiplier = SteamEngineConfig.SUPERHEATED_ACCELERATION_MULTIPLIER.get();
			if (multiplier > 0.0D) {
				ticks /= multiplier;
			}
		}
		return ticks;
	}

	public double getHeatupRatio() {
		double ticks = getHeatupTicks();
		if (ticks <= 0.0D || heatup >= ticks) {
			return 1.0D;
		}

		double segment = ticks / STAGES;
		int step = (int) Math.floor(heatup / segment);
		return Mth.clamp((step + 1) / (double) STAGES, 1.0D / STAGES, 1.0D);
	}

	public int getDemandPerTick() {
		double consumption = getTargetConsumption();
		if (consumption <= 0.0D) {
			return 0;
		}
		return Math.max(1, (int) Math.round(consumption));
	}

	public static int getSupplyStage(double supplyRatio) {
		if (supplyRatio < 0.25D) {
			return 0;
		}
		if (supplyRatio < 0.50D) {
			return 1;
		}
		if (supplyRatio < 0.75D) {
			return 2;
		}
		if (supplyRatio < 1.00D) {
			return 3;
		}
		return STAGES;
	}

	public double getTargetStrength() {
		double strength = driveSpec.getStrength();
		if (strength <= 0.0D) {
			strength = DEFAULT_STRENGTH;
		}

		if (isSuperheated()) {
			strength *= SteamEngineConfig.SUPERHEATED_OUTPUT_MULTIPLIER.get();
		}
		return strength;
	}

	private static final float EXHAUST_LEAD = 25.0F;
	private static final float EXHAUST_SPAN = 40.0F;
	private static final float EXHAUST_BURST = EXHAUST_SPAN / 3.0F;
	private int lastExhaustWindow = Integer.MIN_VALUE;

	private void clientTick() {
		if (level == null || !isRunningOnClient()) {
			lastExhaustWindow = Integer.MIN_VALUE;
			return;
		}

		SteamFlywheelBlockEntity wheel = getWheel();
		if (wheel == null) {
			return;
		}

		float angle = wheel.angle;
		int window = Mth.floor((angle + EXHAUST_LEAD) / 180.0F);

		if (window != lastExhaustWindow) {
			boolean first = lastExhaustWindow == Integer.MIN_VALUE;
			lastExhaustWindow = window;

			if (!first) {
				playExhaustSound();
			}
		}

		if (isInExhaustWindow(angle)) {
			spawnExhaustParticles(exhaustProgress(angle) < EXHAUST_BURST ? 3 : 1);
		}
	}

	private static boolean isInExhaustWindow(float angle) {
		return exhaustProgress(angle) <= EXHAUST_SPAN;
	}

	private static float exhaustProgress(float angle) {
		return Mth.positiveModulo(angle + EXHAUST_LEAD, 180.0F);
	}

	private boolean isRunningOnClient() {
		BlockState state = getBlockState();
		return state.hasProperty(SteamEngineBlock.LIT) && state.getValue(SteamEngineBlock.LIT);
	}

	private void playExhaustSound() {
		Direction front = getFacing();
		level.playLocalSound(
				portX(front),
				portY(),
				portZ(front),
				SoundEvents.FIRE_EXTINGUISH,
				SoundSource.BLOCKS,
				0.10F,
				0.25F,
				false
		);
	}

	private void spawnExhaustParticles(int count) {
		Direction front = getFacing();
		double x = portX(front);
		double y = portY();
		double z = portZ(front);

		for (int i = 0; i < count; i++) {
			double offsetX = (level.random.nextDouble() - 0.5D) * 0.2D;
			double offsetZ = (level.random.nextDouble() - 0.5D) * 0.2D;
			level.addParticle(
					SlParticles.STEAM.get(),
					x + offsetX,
					y,
					z + offsetZ,
					0.0D,
					0.02D,
					0.0D
			);
		}
	}

	private double portX(Direction front) {
		return worldPosition.getX() + 0.5D + front.getStepX() * 0.6D;
	}

	private double portY() {
		return worldPosition.getY() + 0.5D;
	}

	private double portZ(Direction front) {
		return worldPosition.getZ() + 0.5D + front.getStepZ() * 0.6D;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		SlLang.translate(SlTranKeys.GOGGLE_STEAM_ENGINE)
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);

		SlLang.translate(getStatusKey())
				.style(getStatusColor())
				.forGoggles(tooltip, 1);

		addGoggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_ENGINE_HEATUP, Component.literal(
				formatSeconds(heatup) + " / " + formatSeconds((int) Math.ceil(getHeatupTicks()))
		).withStyle(heatup > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));

		addGoggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_ENGINE_STRESS, Component.literal(
				formatNumber(appliedStrength) + " / " + formatNumber(getTargetStrength()) + " SU ("
						+ Math.round(appliedStrength / (float) getTargetStrength() * 100.0F) + "%)"
		).withStyle(appliedStrength > 0.0F ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));

		addGoggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_ENGINE_STEAM, Component.literal(
				formatNumber(tank.getFluidAmount()) + " / " + formatNumber(tank.getCapacity()) + " mB"
		).withStyle(tank.getFluidAmount() > 0 ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));

		return true;
	}

	private String getStatusKey() {
		if (appliedStrength > 0.0F) {
			return SlTranKeys.GOGGLE_STEAM_ENGINE_STATUS_RUNNING;
		}
		if (heatup > 0) {
			return SlTranKeys.GOGGLE_STEAM_ENGINE_STATUS_HEATUP;
		}
		return SlTranKeys.GOGGLE_STEAM_ENGINE_STATUS_IDLE;
	}

	private ChatFormatting getStatusColor() {
		if (appliedStrength > 0.0F) {
			return ChatFormatting.GREEN;
		}
		return heatup > 0 ? ChatFormatting.YELLOW : ChatFormatting.RED;
	}

	private static void addGoggleLine(List<Component> tooltip, String langKey, Component value) {
		SlLang.translate(langKey)
				.style(ChatFormatting.GRAY)
				.text(ChatFormatting.DARK_GRAY, ": ")
				.add(value)
				.forGoggles(tooltip, 1);
	}

	private static String formatNumber(double value) {
		return String.format(Locale.ROOT, "%.1f", value);
	}

	private static String formatSeconds(int ticks) {
		return String.format(Locale.ROOT, "%.1f", ticks / 20.0D) + "s";
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		registerCapabilities(event, SlBlockEntities.STEAM_ENGINE.get());
	}

	/**
	 * 给指定方块实体类型注册流体能力, 供静态注册与 KubeJS 动态设备共用.
	 */
	public static <T extends SteamEngineBlockEntity> void registerCapabilities(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, type, (engine, direction) -> {
			return engine.fluidCapability;
		});
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.put(NBT_TANK, tank.writeToNBT(registries, new CompoundTag()));
		tag.putInt(NBT_HEATUP, heatup);
		tag.putDouble(NBT_PENDING, pending);
		tag.putFloat(NBT_STRENGTH, appliedStrength);
		tag.putInt(NBT_STAGE, supplyStage);
		tag.putBoolean(NBT_LIT, lit);
		if (wheelPos != null) {
			tag.putLong(NBT_WHEEL, wheelPos.asLong());
		}
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		if (tag.contains(NBT_TANK)) {
			tank.readFromNBT(registries, tag.getCompound(NBT_TANK));
		}
		heatup = tag.getInt(NBT_HEATUP);
		pending = tag.getDouble(NBT_PENDING);
		appliedStrength = tag.getFloat(NBT_STRENGTH);
		supplyStage = tag.getInt(NBT_STAGE);
		lit = tag.getBoolean(NBT_LIT);
		wheelPos = tag.contains(NBT_WHEEL) ? BlockPos.of(tag.getLong(NBT_WHEEL)) : null;
	}
}
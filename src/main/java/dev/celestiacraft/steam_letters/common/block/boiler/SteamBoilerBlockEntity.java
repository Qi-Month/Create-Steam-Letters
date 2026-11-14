package dev.celestiacraft.steam_letters.common.block.boiler;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.api.machine.ISteamEngineBlock;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.client.lang.SlLang;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.config.common.SteamConfig;
import dev.celestiacraft.steam_letters.tags.SlFluidTags;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * 多方块锅炉储罐的方块实体.
 * <p>
 * 结构成形与容量规则沿用 Create 的流体储罐, 但储罐分成两个槽: 槽 0 存水且只可注入,
 * 槽 1 存蒸汽且只可抽出. 结构下方一圈烈焰人燃烧室的热量决定等级, 等级决定每 tick 的产汽量.
 */
public class SteamBoilerBlockEntity extends FluidTankBlockEntity {
	public static final int TANK_WATER = 0;
	public static final int TANK_STEAM = 1;

	/**
	 * 内置青铜蒸汽锅炉的默认参数: 每个方块的两个槽各 10000 mB, 满级时产 12 mB/tick 蒸汽.
	 */
	public static final int DEFAULT_CAPACITY = 10000;
	public static final double DEFAULT_PRODUCTION = 12.0D;

	private static final String NBT_STEAM_TANK = "SteamTank";
	private static final String NBT_TANK_CONTENT = "TankContent";
	private static final String NBT_PENDING = "Pending";
	private static final String NBT_COMPONENT = "SteamComponent";
	private static final String NBT_SUPERHEATED = "Superheated";

	/**
	 * 等级上限, 与 Create 锅炉一致; 每 4 个方块才允许 1 级, 因此满级需要 72 个方块.
	 */
	private static final double MAX_LEVEL = 18.0D;
	private static final double BLOCKS_PER_LEVEL = 4.0D;
	private static final int MIN_WIDTH = 2;
	private static final int HEAT_SAMPLE_RATE = 5;

	@Getter
	private final SmartFluidTank steamTank;
	private final IFluidHandler boilerHandler;

	private boolean hasComponent;
	private boolean superheated;
	private double pending;
	@Getter
	private int superheatedBurners;
	private int normalBurners;
	/**
	 * -- GETTER --
	 *
	 * @return 当前等级, 0 表示不产汽; 取值 0~18, 允许小数
	 */
	@Getter
	private double boilerLevel;

	public SteamBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		window = false;
		steamTank = new SteamTank(spec().getCapacity(), this::onSteamChanged);
		boilerHandler = new SteamBoilerTankHandler(this);
	}

	/**
	 * 槽 0 的水罐: 只接受水, 容量由锅炉自身的方块参数决定, 与 Create 储罐的容量配置无关.
	 */
	@Override
	protected SmartFluidTank createInventory() {
		return new WaterTank(spec().getCapacity(), this::onFluidStackChanged);
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide()) {
			return;
		}
		if (!isController()) {
			return;
		}
		if (level.getGameTime() % HEAT_SAMPLE_RATE == 0) {
			updateHeatLevel();
		}
		tickProduction();
	}

	@Override
	public void applyFluidTankSize(int blocks) {
		int capacity = Math.max(0, blocks) * spec().getCapacity();
		tankInventory.setCapacity(capacity);
		steamTank.setCapacity(capacity);
		discardOverflow(tankInventory);
		discardOverflow(steamTank);
		forceFluidLevelUpdate = true;
	}

	/**
	 * Create 的储罐在读档时会把容量按它自己的配置重算, 这里读完后立刻按锅炉容量修正并重读水罐,
	 * 否则容量较大的锅炉存档在读取时会被截断.
	 */
	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		CompoundTag tankContent = compound.getCompound(NBT_TANK_CONTENT);
		super.read(compound, registries, clientPacket);
		applyFluidTankSize(getTotalTankSize());
		if (!tankContent.isEmpty()) {
			tankInventory.readFromNBT(registries, tankContent);
		}
		if (compound.contains(NBT_STEAM_TANK)) {
			steamTank.readFromNBT(registries, compound.getCompound(NBT_STEAM_TANK));
		}
		hasComponent = compound.getBoolean(NBT_COMPONENT);
		superheated = compound.getBoolean(NBT_SUPERHEATED);
		pending = compound.getDouble(NBT_PENDING);
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		compound.put(NBT_STEAM_TANK, steamTank.writeToNBT(registries, new CompoundTag()));
		compound.putBoolean(NBT_COMPONENT, hasComponent);
		compound.putBoolean(NBT_SUPERHEATED, superheated);
		compound.putDouble(NBT_PENDING, pending);
	}

	/**
	 * Create 的锅炉体系只在贴着蒸汽引擎或蒸汽哨时激活, 本锅炉不参与那套等级,
	 * 因此结构变化后只刷新自己的热量等级.
	 */
	@Override
	public void updateBoilerState() {
		if (!isController()) {
			return;
		}
		updateHeatLevel();
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		registerCapabilities(event, SlBlockEntities.STEAM_BOILER.get());
	}

	/**
	 * KubeJS 自定义锅炉没有走内置注册链, 需要按自己的方块实体类型单独挂一次能力.
	 */
	public static void registerCapabilities(RegisterCapabilitiesEvent event, BlockEntityType<? extends SteamBoilerBlockEntity> type) {
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, type,
				(boiler, direction) -> boiler == null ? null : boiler.getBoilerHandler());
	}

	/**
	 * 结构里任何一个方块对外都暴露控制器的那一份双槽处理器, 否则贴着非控制器格的管道取不到蒸汽.
	 */
	public IFluidHandler getBoilerHandler() {
		SteamBoilerBlockEntity controller = getControllerBoiler();
		if (controller == null || controller == this) {
			return boilerHandler;
		}
		return controller.boilerHandler;
	}

	public SmartFluidTank getWaterTank() {
		return tankInventory instanceof SmartFluidTank tank ? tank : null;
	}

	public boolean hasComponent() {
		return hasComponent;
	}

	public void setComponent(boolean component) {
		hasComponent = component;
		setChanged();
		sendData();
	}

	public boolean isSuperheatedOutput() {
		return superheated;
	}

	/**
	 * @return 当前每 tick 的产汽量, 同时也是每 tick 的耗水量
	 */
	public double getProductionRate() {
		return boilerLevel * spec().getProduction();
	}

	/**
	 * 方块自身携带的机器参数, 由注册链注入; 结构里的每一格方块都能读到同一份.
	 */
	private SteamEngineSpec spec() {
		return ((ISteamEngineBlock) getBlockState().getBlock()).getEngineSpec();
	}

	private void onSteamChanged(FluidStack stack) {
		setChanged();
	}

	/**
	 * 统计结构正下方一层烈焰人燃烧室, 超级状态记正分, 其余状态(含未点燃)记负分.
	 */
	private void updateHeatLevel() {
		int hot = 0;
		int normal = 0;
		if (width >= MIN_WIDTH) {
			BlockPos base = worldPosition.below();
			for (int x = 0; x < width; x++) {
				for (int z = 0; z < width; z++) {
					BlockState state = level.getBlockState(base.offset(x, 0, z));
					if (!(state.getBlock() instanceof BlazeBurnerBlock)) {
						continue;
					}
					if (state.getValue(BlazeBurnerBlock.HEAT_LEVEL) == BlazeBurnerBlock.HeatLevel.SEETHING) {
						hot++;
					} else {
						normal++;
					}
				}
			}
		}

		if (hot == superheatedBurners && normal == normalBurners) {
			return;
		}

		superheatedBurners = hot;
		normalBurners = normal;
		boilerLevel = calculateLevel(hot, normal);
		setChanged();
	}

	/**
	 * 至少一台超级燃烧室才开工, 其余状态的燃烧室按分数抵销; 等级再受方块数限制.
	 */
	private double calculateLevel(int hot, int normal) {
		if (hot <= 0) {
			return 0.0D;
		}

		double heat = hot * 2.0D - normal;
		if (heat <= 0.0D) {
			return 0.0D;
		}

		double bySize = getTotalTankSize() / BLOCKS_PER_LEVEL;
		return Math.min(Math.min(heat, bySize), MAX_LEVEL);
	}

	/**
	 * 每 tick 把等级换算成蒸汽, 并按 1:1 扣水; 蒸汽槽放不下时只保留一个 tick 的余额.
	 */
	private void tickProduction() {
		if (boilerLevel <= 0.0D) {
			pending = 0.0D;
			return;
		}

		boolean wantSuperheated = hasComponent && superheatedBurners > 0;
		if (wantSuperheated != superheated) {
			// 两种蒸汽共用同一个槽, 切换产出时余额从 0 重新累计
			superheated = wantSuperheated;
			pending = 0.0D;
			setChanged();
		}

		double produced = getProductionRate();
		pending += produced;

		int requested = (int) Math.floor(pending);
		if (requested <= 0) {
			return;
		}

		FluidStack water = tankInventory.getFluid();
		int room = steamTank.getCapacity() - steamTank.getFluidAmount();
		if (water.isEmpty() || room <= 0) {
			pending = Math.min(pending, produced);
			return;
		}

		FluidStack target = getOutputSteam(requested);
		if (target.isEmpty()) {
			pending = 0.0D;
			return;
		}

		replaceOtherSteam(target);

		int accepted = steamTank.fill(target, IFluidHandler.FluidAction.SIMULATE);
		int usable = Math.min(Math.min(accepted, room), water.getAmount());
		if (usable <= 0) {
			pending = Math.min(pending, produced);
			return;
		}

		tankInventory.drain(usable, IFluidHandler.FluidAction.EXECUTE);
		steamTank.fill(target.copyWithAmount(usable), IFluidHandler.FluidAction.EXECUTE);
		pending -= usable;
	}

	/**
	 * 蒸汽槽是单流体容器, 压着另一种蒸汽时按配置销毁或就地转换, 两种方式都不额外消耗水.
	 */
	private void replaceOtherSteam(FluidStack target) {
		FluidStack stored = steamTank.getFluid();
		if (stored.isEmpty() || FluidStack.isSameFluidSameComponents(stored, target)) {
			return;
		}

		if (SteamConfig.DO_OVERHEATED_STEAM_REPLACE.get()) {
			steamTank.setFluid(target.copyWithAmount(stored.getAmount()));
			setChanged();
			return;
		}

		steamTank.drain(stored.getAmount(), IFluidHandler.FluidAction.EXECUTE);
	}

	/**
	 * 按当前是否过热取配置里的输出流体, 配置未指定该流体时返回空栈, 调用方据此停机.
	 */
	private FluidStack getOutputSteam(int amount) {
		net.minecraft.world.level.material.Fluid fluid = superheated
				? SteamConfig.getOutputSuperheatedSteamFluid()
				: SteamConfig.getOutputSteamFluid();
		if (fluid == null) {
			return FluidStack.EMPTY;
		}

		return new FluidStack(fluid, amount);
	}

	private static void discardOverflow(FluidTank tank) {
		int overflow = tank.getFluidAmount() - tank.getCapacity();
		if (overflow <= 0) {
			return;
		}
		tank.drain(overflow, IFluidHandler.FluidAction.EXECUTE);
	}

	private static boolean isSteamStack(FluidStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		return stack.getFluid().defaultFluidState().is(SlFluidTags.STEAM)
				|| stack.getFluid().defaultFluidState().is(SlFluidTags.SUPERHEATED_STEAM);
	}

	/**
	 * @return 结构控制器, 未成形或控制器不是本模组锅炉时返回 null
	 */
	public SteamBoilerBlockEntity getControllerBoiler() {
		FluidTankBlockEntity controller = getControllerBE();
		if (controller instanceof SteamBoilerBlockEntity boiler) {
			return boiler;
		}
		return null;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		SlLang.lang()
				.add(Component.translatable(getBlockState().getBlock().getDescriptionId()))
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);

		SteamBoilerBlockEntity boiler = getControllerBoiler();
		if (boiler == null) {
			return true;
		}

		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_SIZE,
				Component.literal(boiler.width + " x " + boiler.width + " x " + boiler.height)
						.withStyle(ChatFormatting.AQUA));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_LEVEL,
				Component.literal(formatNumber(boiler.boilerLevel) + " / " + (int) MAX_LEVEL)
						.withStyle(boiler.boilerLevel > 0.0D ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_BURNERS,
				Component.literal(boiler.superheatedBurners + " / " + (boiler.width * boiler.width))
						.withStyle(boiler.superheatedBurners > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));
		goggleLine(tooltip, SlTranKeys.GOGGLE_BOILER_STEAM_OUTPUT_RATE,
				Component.literal(formatNumber(boiler.getProductionRate()) + " mB/t")
						.withStyle(boiler.getProductionRate() > 0.0D ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));
		goggleLine(tooltip, SlTranKeys.GOGGLE_BOILER_WATER_STORED,
				Component.literal(boiler.tankInventory.getFluidAmount() + " / " + boiler.tankInventory.getCapacity() + " mB")
						.withStyle(ChatFormatting.AQUA));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_STEAM,
				Component.literal(boiler.steamTank.getFluidAmount() + " / " + boiler.steamTank.getCapacity() + " mB")
						.withStyle(ChatFormatting.AQUA));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED,
				SlLang.translateDirect(boiler.hasComponent
								? SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED_YES
								: SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED_NO)
						.withStyle(boiler.hasComponent ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));

		return true;
	}

	private static void goggleLine(List<Component> tooltip, String langKey, Component value) {
		SlLang.translate(langKey)
				.style(ChatFormatting.GRAY)
				.text(ChatFormatting.DARK_GRAY, ": ")
				.add(value)
				.forGoggles(tooltip, 1);
	}

	private static String formatNumber(double value) {
		return String.format(Locale.ROOT, "%.1f", value);
	}

	/**
	 * 槽 0 的水罐: 只接受水.
	 */
	private static class WaterTank extends SmartFluidTank {
		private WaterTank(int capacity, Consumer<FluidStack> callback) {
			super(capacity, callback);
		}

		@Override
		public boolean isFluidValid(FluidStack stack) {
			return BoilerFluidTank.isWater(stack);
		}
	}

	/**
	 * 槽 1 的蒸汽罐: 只接受蒸汽与过热蒸汽.
	 */
	private static class SteamTank extends SmartFluidTank {
		private SteamTank(int capacity, Consumer<FluidStack> callback) {
			super(capacity, callback);
		}

		@Override
		public boolean isFluidValid(FluidStack stack) {
			return isSteamStack(stack);
		}
	}
}
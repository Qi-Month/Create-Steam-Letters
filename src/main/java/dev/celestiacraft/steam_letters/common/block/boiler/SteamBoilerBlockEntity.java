package dev.celestiacraft.steam_letters.common.block.boiler;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.api.machine.ISteamEngineBlock;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.api.machine.heat.IHeatSource;
import dev.celestiacraft.steam_letters.client.lang.SlLang;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.config.common.SteamBoilerConfig;
import dev.celestiacraft.steam_letters.config.common.SteamConfig;
import dev.celestiacraft.steam_letters.tags.SlFluidTags;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * 多方块锅炉储罐的方块实体.
 * <p>
 * 结构成形与容量规则沿用原版的流体储罐, 但储罐分成两个槽: 槽 0 存水且只可注入,
 * 槽 1 存蒸汽且只可抽出. 结构下方一圈烈焰人燃烧室的热量决定等级, 等级决定每 tick 的产汽量.
 */
public class SteamBoilerBlockEntity extends FluidTankBlockEntity {
	public static final int TANK_WATER = 0;
	public static final int TANK_STEAM = 1;

	/**
	 * 内置青铜蒸汽锅炉的默认参数: 每个方块的两个槽各 10000 mB, 每级每 tick 产 12 mB 蒸汽.
	 */
	public static final int DEFAULT_CAPACITY = 10000;
	public static final double DEFAULT_PRODUCTION = 12.0D;

	private static final String NBT_STEAM_TANK = "SteamTank";
	private static final String NBT_TANK_CONTENT = "TankContent";
	private static final String NBT_PENDING = "Pending";
	private static final String NBT_COMPONENT = "SteamComponent";
	private static final String NBT_SUPERHEATED = "Superheated";
	private static final String NBT_LEVEL = "BoilerLevel";
	private static final String NBT_BURNERS = "Burners";
	private static final String NBT_NORMAL_BURNERS = "NormalBurners";

	/**
	 * 等级上限, 与原版锅炉一致; 每 4 个方块才允许 1 级, 因此满级需要 72 个方块.
	 */
	public static final double MAX_LEVEL = 18.0D;
	private static final double BLOCKS_PER_LEVEL = 4.0D;
	public static final int MIN_WIDTH = 2;
	private static final int HEAT_SAMPLE_RATE = 5;
	private static final float GAUGE_LERP = 0.25F;

	@Getter
	private final SmartFluidTank steamTank;
	private final IFluidHandler boilerHandler;

	private boolean hasComponent;
	private boolean superheated;
	private double pending;
	@Getter
	private int superheatedBurners;
	private int normalBurners;
	@Getter
	private double boilerLevel;
	private int sampledSize;
	private float gauge;
	private float previousGauge;

	public SteamBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		window = false;
		steamTank = new SteamTank(spec().getCapacity(), this::onSteamChanged);
		boilerHandler = new SteamBoilerTankHandler(this);
	}

	/**
	 * 槽 0 的水罐: 只接受水, 容量由锅炉自身的方块参数决定, 与原版储罐的容量配置无关.
	 */
	@Override
	protected SmartFluidTank createInventory() {
		return new WaterTank(spec().getCapacity(), this::onFluidStackChanged);
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null) {
			return;
		}
		if (level.isClientSide()) {
			tickGauge();
			return;
		}
		if (!isController()) {
			return;
		}
		if (!isMultiBlock()) {
			tickLegacyProduction();
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
	 * 原版储罐在读档时会把容量按它自己的配置重算, 这里读完后立刻按锅炉容量修正并重读水罐,
	 * 否则容量较大的锅炉存档在读取时会被截断.
	 */
	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider provider, boolean clientPacket) {
		CompoundTag tankContent = tag.getCompound(NBT_TANK_CONTENT);
		super.read(tag, provider, clientPacket);
		applyFluidTankSize(getTotalTankSize());
		if (!tankContent.isEmpty()) {
			tankInventory.readFromNBT(provider, tankContent);
		}
		if (tag.contains(NBT_STEAM_TANK)) {
			steamTank.readFromNBT(provider, tag.getCompound(NBT_STEAM_TANK));
		}
		hasComponent = tag.getBoolean(NBT_COMPONENT);
		superheated = tag.getBoolean(NBT_SUPERHEATED);
		pending = tag.getDouble(NBT_PENDING);
		boilerLevel = tag.getDouble(NBT_LEVEL);
		superheatedBurners = tag.getInt(NBT_BURNERS);
		normalBurners = tag.getInt(NBT_NORMAL_BURNERS);
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider provider, boolean clientPacket) {
		super.write(tag, provider, clientPacket);
		tag.put(NBT_STEAM_TANK, steamTank.writeToNBT(provider, new CompoundTag()));
		tag.putBoolean(NBT_COMPONENT, hasComponent);
		tag.putBoolean(NBT_SUPERHEATED, superheated);
		tag.putDouble(NBT_PENDING, pending);
		tag.putDouble(NBT_LEVEL, boilerLevel);
		tag.putInt(NBT_BURNERS, superheatedBurners);
		tag.putInt(NBT_NORMAL_BURNERS, normalBurners);
	}

	/**
	 * 原版的锅炉体系只在贴着蒸汽引擎或蒸汽哨时激活, 本锅炉不参与那套等级,
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
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, type, (boiler, direction) -> {
			return boiler == null ? null : boiler.getBoilerHandler();
		});
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
		if (isMultiBlock()) {
			return boilerLevel * spec().getProduction();
		}

		double production = spec().getProduction() * getLegacyEfficiency();
		if (superheated) {
			production *= SteamBoilerConfig.getSuperheatedProductionMultiplier(getLegacyHeatPercent());
		}
		return production;
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
	 * 宽度不到 2 的锅炉没有拼成结构, 沿用改造前的单块运行方式: 只看正下方一台热源,
	 * 按热量百分比分档取效率, 过热燃料直接产出过热蒸汽.
	 */
	private boolean isMultiBlock() {
		return width >= MIN_WIDTH;
	}

	/**
	 * 仪表盘指针的取值: 0 表示空位, 1 表示满级, 仅客户端插值使用.
	 */
	public float getGaugeValue(float partialTicks) {
		return Mth.lerp(partialTicks, previousGauge, gauge);
	}

	private float getGaugeTarget() {
		if (!isMultiBlock()) {
			return 0.0F;
		}
		return (float) Mth.clamp(boilerLevel / MAX_LEVEL, 0.0D, 1.0D);
	}

	private void tickGauge() {
		previousGauge = gauge;
		gauge += (getGaugeTarget() - gauge) * GAUGE_LERP;
	}

	@Nullable
	private IHeatSource getHeatSource() {
		if (level == null) {
			return null;
		}
		BlockEntity below = level.getBlockEntity(worldPosition.below());
		if (below instanceof IHeatSource source) {
			return source;
		}
		return null;
	}

	/**
	 * @return 正下方热源的热量百分比, 过热状态下会超过 100
	 */
	private double getLegacyHeatPercent() {
		IHeatSource source = getHeatSource();
		if (source == null) {
			return 0.0D;
		}

		double maxHeat = source.getMaxHeat();
		if (maxHeat <= 0.0D) {
			return 0.0D;
		}

		return source.getHeat() / maxHeat * 100.0D;
	}

	private boolean isLegacySuperheated() {
		IHeatSource source = getHeatSource();
		return source != null && source.isSuperheated();
	}

	private double getLegacyEfficiency() {
		IHeatSource source = getHeatSource();
		if (source == null) {
			return 0.0D;
		}

		return SteamBoilerConfig.getEfficiency(source.getHeatRatio() * 100.0D);
	}

	/**
	 * 单块时的产汽路径, 与多块结构共用两个槽与同一个余额字段.
	 */
	private void tickLegacyProduction() {
		double efficiency = getLegacyEfficiency();
		double production = spec().getProduction();

		boolean wantSuperheated = efficiency > 0.0D && production > 0.0D && isLegacySuperheated();
		if (wantSuperheated != superheated) {
			superheated = wantSuperheated;
			pending = 0.0D;
			setChanged();
			sendData();
		}

		if (efficiency <= 0.0D || production <= 0.0D) {
			pending = 0.0D;
			return;
		}

		if (wantSuperheated) {
			clearNormalSteamFromOutput();
		}

		double produced = production * efficiency;
		if (wantSuperheated) {
			// 过热蒸汽价值更高, 产率按热量落在过热区间里的位置打折
			produced *= SteamBoilerConfig.getSuperheatedProductionMultiplier(getLegacyHeatPercent());
		}
		pending += produced;

		int requested = (int) Math.floor(pending);
		if (requested <= 0) {
			return;
		}

		FluidStack target = getOutputSteam(requested);
		if (target.isEmpty()) {
			pending = 0.0D;
			return;
		}

		int drained = tankInventory.drain(requested, IFluidHandler.FluidAction.SIMULATE).getAmount();
		int accepted = steamTank.fill(target, IFluidHandler.FluidAction.SIMULATE);
		int producible = Math.min(drained, accepted);
		if (producible <= 0) {
			if (outputHoldsOtherSteam(wantSuperheated)) {
				// 蒸汽槽压着另一种蒸汽时既不进也不出, 这一 tick 的水照常烧掉
				tankInventory.drain(drained, IFluidHandler.FluidAction.EXECUTE);
				pending = 0.0D;
				return;
			}
			pending = Math.min(pending, produced);
			return;
		}

		tankInventory.drain(producible, IFluidHandler.FluidAction.EXECUTE);
		steamTank.fill(target.copyWithAmount(producible), IFluidHandler.FluidAction.EXECUTE);
		pending -= producible;
	}

	/**
	 * @return 蒸汽槽里是否正压着与本次产出不同的另一种蒸汽
	 */
	private boolean outputHoldsOtherSteam(boolean superheatedProduction) {
		FluidStack stored = steamTank.getFluid();
		if (stored.isEmpty()) {
			return false;
		}

		FluidStack expected = SteamConfig.getOutputSteam(stored.getAmount(), superheatedProduction);
		return !expected.isEmpty() && !FluidStack.isSameFluidSameComponents(stored, expected);
	}

	/**
	 * 转入过热产出前清掉蒸汽槽里的普通蒸汽, 销毁还是就地转换由配置决定.
	 */
	private void clearNormalSteamFromOutput() {
		FluidStack stored = steamTank.getFluid();
		if (stored.isEmpty()) {
			return;
		}

		FluidStack superheatedSteam = SteamConfig.getOutputSteam(stored.getAmount(), true);
		if (superheatedSteam.isEmpty() || FluidStack.isSameFluidSameComponents(stored, superheatedSteam)) {
			return;
		}

		if (SteamConfig.DO_OVERHEATED_STEAM_REPLACE.get()) {
			steamTank.setFluid(superheatedSteam);
			setChanged();
			return;
		}
		steamTank.drain(stored.getAmount(), IFluidHandler.FluidAction.EXECUTE);
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

		int size = getTotalTankSize();
		if (hot == superheatedBurners && normal == normalBurners && size == sampledSize) {
			return;
		}

		superheatedBurners = hot;
		normalBurners = normal;
		sampledSize = size;
		boilerLevel = calculateLevel(hot, normal);
		setChanged();
		sendData();
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
			sendData();
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
		return SteamConfig.getOutputSteam(amount, superheated);
	}

	private static void discardOverflow(FluidTank tank) {
		int overflow = tank.getFluidAmount() - tank.getCapacity();
		if (overflow <= 0) {
			return;
		}
		tank.drain(overflow, IFluidHandler.FluidAction.EXECUTE);
	}

	public static boolean isSteamStack(FluidStack stack) {
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

		if (!boiler.isMultiBlock()) {
			appendLegacyTooltip(tooltip, boiler);
			return true;
		}

		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_SIZE,
				Component.literal(boiler.width + " x " + boiler.width + " x " + boiler.height)
						.withStyle(ChatFormatting.AQUA));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_LEVEL,
				Component.literal((int) Math.floor(boiler.boilerLevel) + " / " + (int) MAX_LEVEL)
						.withStyle(boiler.boilerLevel > 0.0D ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));
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

	/**
	 * 单块锅炉的护目镜读数: 热量与效率按百分比显示, 过热状态由正下方燃烧室决定.
	 */
	private static void appendLegacyTooltip(List<Component> tooltip, SteamBoilerBlockEntity boiler) {
		IHeatSource source = boiler.getHeatSource();
		if (source == null) {
			SlLang.translate(SlTranKeys.GOGGLE_STEAM_BOILER_NO_BURNER)
					.style(ChatFormatting.RED)
					.forGoggles(tooltip, 1);
		}

		double efficiency = boiler.getLegacyEfficiency();
		double superheatedBonus = boiler.isLegacySuperheated()
				? SteamBoilerConfig.getSuperheatedProductionMultiplier(boiler.getLegacyHeatPercent())
				: 1.0D;
		double production = boiler.spec().getProduction() * efficiency * superheatedBonus;

		goggleLine(tooltip, SlTranKeys.GOGGLE_BOILER_STEAM_OUTPUT_RATE,
				Component.literal(formatNumber(production) + " mB/t")
						.withStyle(production > 0.0D ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));
		goggleLine(tooltip, SlTranKeys.GOGGLE_BOILER_WATER_STORED,
				Component.literal(boiler.tankInventory.getFluidAmount() + " / " + boiler.tankInventory.getCapacity() + " mB")
						.withStyle(ChatFormatting.AQUA));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_STEAM,
				Component.literal(boiler.steamTank.getFluidAmount() + " / " + boiler.steamTank.getCapacity() + " mB")
						.withStyle(ChatFormatting.AQUA));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_HEAT,
				Component.literal(Math.round(boiler.getLegacyHeatPercent()) + "%")
						.withStyle(boiler.getLegacyHeatPercent() > 0.0D ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_EFFICIENCY,
				Component.literal(Math.round(efficiency * 100.0D) + "%")
						.withStyle(efficiency > 0.0D ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED,
				SlLang.translateDirect(boiler.isLegacySuperheated()
								? SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED_YES
								: SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED_NO)
						.withStyle(boiler.isLegacySuperheated() ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));
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
}
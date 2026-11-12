package dev.celestiacraft.steam_letters.common.block.burner;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.api.machine.ISteamEngineBlock;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.api.machine.heat.IHeatSource;
import dev.celestiacraft.steam_letters.client.lang.SlLang;
import dev.celestiacraft.steam_letters.config.common.SteamBoilerConfig;
import dev.celestiacraft.steam_letters.config.common.SteamBurnerConfig;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Locale;

/**
 * 燃烧室的公共实现: 维护自身热量, 供正上方锅炉读取.
 * <p>
 * 升温 / 降温速率按「最高热量的百分比每秒」计算, 燃料只决定燃烧时长, 不影响升温速度;
 * 热量钳位在 [0, 热量上限], 燃料耗尽后立刻开始降温.
 * <p>
 * 燃烧过热燃料时进入过热状态: 热量上限提升到最高热量的两倍, 到达最高热量之前升温更快.
 */
public abstract class AbstractBurnerBlockEntity extends SmartBlockEntity implements IHeatSource, IHaveGoggleInformation {
	private static final String NBT_HEAT = "Heat";
	private static final String NBT_BURN_TIME = "BurnTime";
	private static final String NBT_SUPERHEATED = "Superheated";
	private static final String NBT_TEMPERATURE = "Temperature";

	/**
	 * 内置青铜燃烧室的默认热量上限.
	 */
	public static final double DEFAULT_MAX_HEAT = 100.0D;

	protected final SteamEngineSpec spec;

	/**
	 * 当前热量, 允许小数 —— 每 tick 的增量通常远小于 1, 取整会直接丢掉全部产出.
	 */
	@Getter
	@Setter
	private double heat;

	/**
	 * 当前温度, 钳位在 [0, 温度上限].
	 * <p>
	 * 只作为常规数值记录与显示, 不参与任何计算. 与热量同时开始上升, 同样在配置的升温时间内
	 * 到达注册时给定的温度上限; 燃料停止后以一半的速度回落.
	 */
	@Getter
	@Setter
	private double temperature;

	/**
	 * 当前这份燃料剩余的燃烧时间 (tick), 0 表示没有燃料在燃烧.
	 */
	@Getter
	@Setter
	private int burnTime;

	/**
	 * 当前这份燃料是否为过热燃料. 燃料烧完后不立即清除, 过热状态要保留到热量归零.
	 */
	@Getter
	private boolean superheatedFuel;

	/**
	 * 上次同步给客户端的数值, 用来避免每 10 tick 无条件发包.
	 */
	private double syncedHeat = Double.NaN;
	private int syncedBurnTime = -1;
	private boolean syncedSuperheated;
	private double syncedTemperature = Double.NaN;

	protected AbstractBurnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		spec = ((ISteamEngineBlock) state.getBlock()).getEngineSpec();
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide()) {
			return;
		}

		boolean burning = tickFuel();
		tickHeat(burning);
		tickTemperature(burning);
		updateLitState(burning);
	}

	/**
	 * 推进一 tick 的燃料消耗.
	 *
	 * @return 本 tick 是否有燃料在燃烧
	 */
	protected abstract boolean tickFuel();

	private void tickHeat(boolean burning) {
		double maxHeat = getMaxHeat();
		if (maxHeat <= 0.0D) {
			heat = 0.0D;
			return;
		}

		// 这里看的是「当前这份燃料是不是过热燃料」, 而不是「有没有进入过热状态」:
		// 过热燃料从点燃起就把容量抬到最高热量的两倍并在到达最高热量之前加速, 越过最高热量之后才算进入过热状态.
		boolean limitRaised = superheatedFuel;
		double limit = getHeatCapacity();

		if (burning) {
			// 配置的是「升到最高热量需要多少秒」, 换算成每 tick 的增量, 与实际 maxHeat 数值解耦.
			// 过热燃料只在到达最高热量之前加速, 100% 以上回到正常速度.
			double seconds = SteamBurnerConfig.HEATUP_SECONDS.get();
			if (limitRaised && heat < maxHeat) {
				seconds /= SteamBurnerConfig.SUPERHEATED_HEATUP_MULTIPLIER.get();
			}
			if (seconds <= 0.0D) {
				// 0 秒视为瞬时完成
				heat = limit;
				return;
			}

			double delta = maxHeat / (seconds * 20.0D);
			heat = Mth.clamp(heat + delta, 0.0D, limit);
			return;
		}

		double seconds = SteamBurnerConfig.COOLDOWN_SECONDS.get();
		if (seconds <= 0.0D) {
			// 0 秒视为瞬时完成
			heat = 0.0D;
			clearSuperheated();
			return;
		}

		double delta = maxHeat / (seconds * 20.0D);
		if (limitRaised && heat > maxHeat) {
			// 高出最高热量的那一段以双倍速度回落.
			delta *= SteamBurnerConfig.SUPERHEATED_COOLDOWN_MULTIPLIER;
		}

		heat = Mth.clamp(heat - delta, 0.0D, limit);

		if (limitRaised && burnTime <= 0 && heat <= 0.0D) {
			clearSuperheated();
		}
	}

	/**
	 * 推进一 tick 的温度.
	 * <p>
	 * 温度与热量同时开始上升, 并且在同一份升温时间配置内各自到达上限, 因此两者烧满的时刻一致.
	 * 温度不参与过热: 过热燃料既不加快升温, 也不抬高上限.
	 * <p>
	 * 燃料停止后以升温速度的一半回落, 降回 0 需要两倍于升温的时间.
	 */
	private void tickTemperature(boolean burning) {
		double maxTemperature = getMaxTemperature();
		if (maxTemperature <= 0.0D) {
			temperature = 0.0D;
			return;
		}

		double seconds = SteamBurnerConfig.HEATUP_SECONDS.get();
		if (seconds <= 0.0D) {
			// 0 秒视为瞬时完成
			temperature = burning ? maxTemperature : 0.0D;
			return;
		}

		double delta = maxTemperature / (seconds * 20.0D);
		temperature = burning
				? Mth.clamp(temperature + delta, 0.0D, maxTemperature)
				: Mth.clamp(temperature - delta / 2.0D, 0.0D, maxTemperature);
	}

	/**
	 * 热源当前是否处于过热状态.
	 * <p>
	 * 点燃了过热燃料只是前置条件, 只有热量真正越过最高热量之后才算进入过热状态;
	 * 因此热量在 0-100% 区间内时仍按普通蒸汽生产, 越过 100% 之后才切换成过热蒸汽.
	 */
	@Override
	public boolean isSuperheated() {
		return superheatedFuel && heat >= getMaxHeat();
	}

	/**
	 * 记录当前这份燃料是否为过热燃料, 由子类在点燃新燃料时调用.
	 */
	protected void setSuperheatedFuel(boolean superheated) {
		if (superheatedFuel == superheated) {
			return;
		}
		superheatedFuel = superheated;
		setChanged();
	}

	protected void clearSuperheated() {
		superheatedFuel = false;
	}

	@Override
	public double getMaxHeat() {
		double configured = spec.getMaxHeat();
		return configured > 0.0D ? configured : DEFAULT_MAX_HEAT;
	}

	/**
	 * 温度上限, 来自注册参数; 注册时不指定则由 {@link SteamEngineSpec} 回退到配置项.
	 */
	public double getMaxTemperature() {
		return spec.getMaxTemperature();
	}

	/**
	 * 当前热量上限: 点燃过热燃料后允许突破最高热量, 最高到它的两倍.
	 */
	public double getHeatCapacity() {
		double maxHeat = getMaxHeat();
		return superheatedFuel
				? maxHeat * SteamBurnerConfig.SUPERHEATED_MAX_HEAT_MULTIPLIER
				: maxHeat;
	}

	@Override
	public boolean isBurningFuel() {
		return burnTime > 0;
	}

	/**
	 * 当前热量占最高热量的百分比 (0-100).
	 */
	public double getHeatPercent() {
		double maxHeat = getMaxHeat();
		if (maxHeat <= 0.0D) {
			return 0.0D;
		}
		return Mth.clamp(heat / maxHeat * 100.0D, 0.0D, 100.0D);
	}

	/**
	 * 热量、温度与燃烧剩余时间每 10 tick 向客户端同步一次, 供护目镜显示.
	 * <p>
	 * 只在数值真正变化时发包, 闲置的燃烧室不产生额外网络流量.
	 */
	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level == null || level.isClientSide()) {
			return;
		}
		if (heat == syncedHeat && burnTime == syncedBurnTime && superheatedFuel == syncedSuperheated
				&& temperature == syncedTemperature) {
			return;
		}
		syncedHeat = heat;
		syncedBurnTime = burnTime;
		syncedSuperheated = superheatedFuel;
		syncedTemperature = temperature;
		sendData();
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		SlLang.translate(SlTranKeys.GOGGLE_STEAM_BURNER)
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);

		addGoggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BURNER_HEAT, Component.literal(
				formatNumber(heat) + "%" + " / " + formatNumber(getHeatCapacity()) + "%"
		).withStyle(ChatFormatting.GOLD));

		if (SteamBurnerConfig.SHOW_TEMPERATURE.get()) {
			addGoggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BURNER_TEMPERATURE, Component.literal(
					formatNumber(temperature) + " / " + formatNumber(getMaxTemperature())
			).withStyle(ChatFormatting.GOLD));
		}

		addGoggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BURNER_FUEL_TIME, Component.literal(
				formatSeconds(burnTime)
		).withStyle(burnTime > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));

		double efficiency = SteamBoilerConfig.getEfficiency(getHeatPercent());
		addGoggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BURNER_EFFICIENCY, Component.literal(
				formatPercent(efficiency)
		).withStyle(efficiency > 0.0D ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));

		return true;
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
		return String.format(Locale.ROOT, "%.1fs", ticks / 20.0D);
	}

	private static String formatPercent(double ratio) {
		return String.format(Locale.ROOT, "%.0f%%", ratio * 100.0D);
	}

	private void updateLitState(boolean lit) {
		if (level == null) {
			return;
		}
		BlockState state = getBlockState();
		if (!state.hasProperty(AbstractBurnerBlock.LIT) || state.getValue(AbstractBurnerBlock.LIT) == lit) {
			return;
		}
		level.setBlockAndUpdate(worldPosition, state.setValue(AbstractBurnerBlock.LIT, lit));
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putDouble(NBT_HEAT, heat);
		tag.putInt(NBT_BURN_TIME, burnTime);
		tag.putBoolean(NBT_SUPERHEATED, superheatedFuel);
		tag.putDouble(NBT_TEMPERATURE, temperature);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		heat = tag.getDouble(NBT_HEAT);
		burnTime = tag.getInt(NBT_BURN_TIME);
		superheatedFuel = tag.getBoolean(NBT_SUPERHEATED);
		temperature = tag.getDouble(NBT_TEMPERATURE);
	}
}
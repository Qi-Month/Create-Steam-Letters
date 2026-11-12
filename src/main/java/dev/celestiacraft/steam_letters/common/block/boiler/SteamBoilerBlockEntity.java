package dev.celestiacraft.steam_letters.common.block.boiler;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.api.machine.heat.IHeatSource;
import dev.celestiacraft.steam_letters.client.lang.SlLang;
import dev.celestiacraft.steam_letters.config.common.SteamBoilerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Locale;

/**
 * 蒸汽锅炉的方块实体.
 * <p>
 * 运行效率来自正下方燃烧室的当前热量: 热量低于第一档阈值时完全不产汽.
 * 连接状态由 {@link #lazyTick()} 维护, 热量值每 tick 现场读取, 所以燃烧室被移除/替换会立刻停产.
 */
public class SteamBoilerBlockEntity extends AbstractBoilerBlockEntity implements IHaveGoggleInformation {
	/**
	 * 内置青铜蒸汽锅炉的默认参数: 两个罐各 10000 mB, 100% 效率时产 12 mB/tick 蒸汽.
	 * <p>
	 * 最高产汽速率与同材料的蒸汽引擎最高消耗速率 (12 mB/tick) 相等, 一台锅炉刚好喂饱一台引擎.
	 */
	public static final int DEFAULT_CAPACITY = 10000;
	public static final double DEFAULT_PRODUCTION = 12.0D;

	private boolean hasValidBurner;

	public SteamBoilerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level == null || level.isClientSide()) {
			return;
		}
		hasValidBurner = getHeatSource() != null;
	}

	@Override
	protected double getCurrentEfficiency() {
		if (!hasValidBurner) {
			return 0.0D;
		}
		IHeatSource source = getHeatSource();
		if (source == null) {
			return 0.0D;
		}
		return SteamBoilerConfig.getEfficiency(source.getHeatRatio() * 100.0D);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		SlLang.lang()
				.add(Component.translatable(getBlockState().getBlock().getDescriptionId()))
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip);

		// 客户端不同步 hasValidBurner, 读数直接现场问热源, 避免护目镜恒为 0.
		IHeatSource source = getHeatSource();
		if (source == null) {
			SlLang.translate(SlTranKeys.GOGGLE_STEAM_BOILER_NO_BURNER)
					.style(ChatFormatting.RED)
					.forGoggles(tooltip, 1);
		}

		double efficiency = source == null ? 0.0D : SteamBoilerConfig.getEfficiency(source.getHeatRatio() * 100.0D);
		double production = getSpec().getProduction() * efficiency;
		if (isSuperheated()) {
			production *= SteamBoilerConfig.getSuperheatedProductionMultiplier(getRawHeatPercent());
		}

		goggleLine(tooltip, SlTranKeys.GOGGLE_BOILER_STEAM_OUTPUT_RATE,
				Component.literal(formatNumber(production) + " mB/t")
						.withStyle(production > 0.0D ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));
		goggleLine(tooltip, SlTranKeys.GOGGLE_BOILER_WATER_STORED,
				Component.literal(getInputTank().getFluidAmount() + " / " + getInputTank().getCapacity() + " mB")
						.withStyle(ChatFormatting.AQUA));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_STEAM,
				Component.literal(getOutputTank().getFluidAmount() + " / " + getOutputTank().getCapacity() + " mB")
						.withStyle(ChatFormatting.AQUA));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_HEAT,
				Component.literal(Math.round(getRawHeatPercent()) + "%")
						.withStyle(getRawHeatPercent() > 0.0D ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_EFFICIENCY,
				Component.literal(Math.round(efficiency * 100.0D) + "%")
						.withStyle(efficiency > 0.0D ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
		goggleLine(tooltip, SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED,
				SlLang.translateDirect(isSuperheated()
								? SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED_YES
								: SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED_NO)
						.withStyle(isSuperheated() ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));

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
}
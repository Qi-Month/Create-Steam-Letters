package dev.celestiacraft.steam_letters.event.item;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.item.TooltipHelper;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.api.machine.ISteamEngineBlock;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineDriveSpec;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.client.lang.SlLang;
import dev.celestiacraft.steam_letters.common.block.advanced_spout.AdvancedSpoutBlockEntity;
import dev.celestiacraft.steam_letters.common.block.engine.SteamEngineBlock;
import dev.celestiacraft.steam_letters.common.item.SteamContainerItem;
import dev.celestiacraft.steam_letters.common.register.SlBlocks;
import dev.celestiacraft.steam_letters.common.register.SlItems;
import dev.celestiacraft.steam_letters.config.common.AdvancedSpoutConfig;
import dev.celestiacraft.steam_letters.config.common.BronzeBasinConfig;
import dev.celestiacraft.steam_letters.config.common.SolarBoilerConfig;
import dev.celestiacraft.steam_letters.config.common.SteamBurnerConfig;
import dev.celestiacraft.steam_letters.config.common.SteamConfig;
import dev.celestiacraft.steam_letters.config.common.SteamEngineConfig;
import dev.celestiacraft.steam_letters.config.common.SteamHammerConfig;
import dev.celestiacraft.steam_letters.config.common.SteamMixerConfig;
import dev.celestiacraft.steam_letters.config.common.SteamSawConfig;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = CreateLettersSteam.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemTooltip {
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		List<Component> tooltip = event.getToolTip();
		ItemStack stack = event.getItemStack();

		if (stack.is(SlItemTags.STEAM_CONTAINER)) {
			addSteamContainerTooltip(tooltip, stack);
			return;
		}

		if (stack.is(SlItems.SH_STEAM_COMPONENT.get())) {
			addSHSteamComponentTooltip(tooltip);
		}
	}

	@SubscribeEvent
	public static void onBlockItemTooltip(ItemTooltipEvent event) {
		List<Component> tooltip = event.getToolTip();
		ItemStack stack = event.getItemStack();

		if (!(stack.getItem() instanceof BlockItem item)) {
			return;
		}

		Block block = item.getBlock();

		if (stack.is(SlItemTags.STEAM_HAMMER)) {
			addSteamHammerTooltip(tooltip, block);
		}
		if (stack.is(SlItemTags.STEAM_MIXER)) {
			addSteamMixerTooltip(tooltip, block);
		}
		if (stack.is(SlItemTags.STEAM_SAW)) {
			addSteamSawTooltip(tooltip, block);
		}
		if (stack.is(SlItemTags.ADVANCED_SPOUT)) {
			addAdvancedSpoutTooltip(tooltip);
		}
		if (stack.is(SlItemTags.STEAM_BOILER)) {
			addBronzeBoilerTooltip(tooltip, block);
		}
		if (stack.is(SlItemTags.STEAM_SOLAR_BOILER)) {
			addSolarBoilerTooltip(tooltip, block);
		}
		if (stack.is(SlItemTags.STEAM_SOLID_BURNER)) {
			addBurnerTooltip(tooltip, block, SlTranKeys.TOOLTIP_SOLID_BURNER_SUMMARY, false);
		}
		if (stack.is(SlItemTags.STEAM_FLUID_BURNER)) {
			addBurnerTooltip(tooltip, block, SlTranKeys.TOOLTIP_FLUID_BURNER_SUMMARY, true);
		}
		if (stack.is(SlItemTags.STEAM_ENGINE)) {
			addSteamEngineTooltip(tooltip, block);
		}
		if (stack.is(SlItemTags.BRONZE_BASIN)) {
			addBronzeBasinTooltip(tooltip);
		}
		if (stack.is(SlItemTags.BRONZE_STEAM_PUMP)) {
			addSteamPumpTooltip(tooltip);
		}
		if (stack.is(SlBlocks.BRONZE_DEPOT.asItem())) {
			addBronzeDepot(tooltip);
		}
	}

	/**
	 * 未按住 Shift 时写出提示行并返回 false.
	 */
	private static boolean requireShift(List<Component> tooltip) {
		SlLang.isShiftDown(tooltip);
		return SlLang.isShiftDown();
	}

	/**
	 * 追加一行不带缩进的说明.
	 */
	private static void line(List<Component> tooltip, String langKey, Object... args) {
		tooltip.addAll(TooltipHelper.cutStringTextComponent(SlLang.translateDirect(langKey, args).getString(), FontHelper.Palette.STANDARD_CREATE));
	}

	/**
	 * 追加一行带缩进与项目符号的行为说明.
	 */
	private static void behaviour(List<Component> tooltip, String langKey, Object... args) {
		tooltip.addAll(TooltipHelper.cutStringTextComponent(SlLang.translateDirect(langKey, args).getString(), FontHelper.Palette.STANDARD_CREATE.primary(), FontHelper.Palette.STANDARD_CREATE.highlight(), 1));
	}

	/**
	 * 追加一行灰色小标题.
	 */
	private static void section(List<Component> tooltip, String langKey) {
		SlLang.translate(langKey).style(ChatFormatting.GRAY).addTo(tooltip);
	}

	/**
	 * 三台蒸汽加工机共用的规格行: 并行, 缓存, 应力影响, 旋转需求.
	 */
	private static void steamMachineSpec(List<Component> tooltip, Block block, int parallel, int capacity) {
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_PARALLEL, parallel);
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_CAPACITY, capacity);
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_STRESS_IMPACT, stressImpact(block));
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_ROTATION);
	}

	/**
	 * 三台蒸汽加工机共用的过热折扣行.
	 */
	private static void superheatedConsumption(List<Component> tooltip) {
		behaviour(tooltip, SlTranKeys.TOOLTIP_COMMON_SUPERHEATED_CONSUMPTION, percent(SteamConfig.SUPERHEATED_STEAM_CONSUMPTION_MULTIPLIER.get()));
	}

	private static void addSteamHammerTooltip(List<Component> tooltip, Block block) {
		if (!requireShift(tooltip)) {
			return;
		}

		addEmptyLine(tooltip);
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_HAMMER_SUMMARY);
		steamMachineSpec(tooltip, block, SteamHammerConfig.STEAM_HAMMER_PARALLEL.get(), SteamHammerConfig.STEAM_CAPACITY.get());
		addEmptyLine(tooltip);
		section(tooltip, SlTranKeys.TOOLTIP_COMMON_CONDITION);
		behaviour(tooltip, SlTranKeys.TOOLTIP_COMMON_CONSUMPTION, SteamHammerConfig.STEAM_CONSUMPTION.get());
		superheatedConsumption(tooltip);
	}

	private static void addSteamMixerTooltip(List<Component> tooltip, Block block) {
		if (!requireShift(tooltip)) {
			return;
		}

		addEmptyLine(tooltip);
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_MIXER_SUMMARY);
		steamMachineSpec(tooltip, block, SteamMixerConfig.STEAM_MIXER_PARALLEL.get(), SteamMixerConfig.STEAM_CAPACITY.get());
		addEmptyLine(tooltip);
		section(tooltip, SlTranKeys.TOOLTIP_COMMON_CONDITION);
		behaviour(tooltip, SlTranKeys.TOOLTIP_COMMON_CONSUMPTION, SteamMixerConfig.STEAM_CONSUMPTION.get());
		superheatedConsumption(tooltip);
	}

	private static void addSteamSawTooltip(List<Component> tooltip, Block block) {
		if (!requireShift(tooltip)) {
			return;
		}

		addEmptyLine(tooltip);
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_SAW_SUMMARY);
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_SAW_PROCESSING_TIME, percent(SteamSawConfig.PROCESSING_TIME_MULTIPLIER.get()));
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_PARALLEL, SteamSawConfig.STEAM_SAW_PARALLEL.get());
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_CAPACITY, SteamSawConfig.STEAM_CAPACITY.get());
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_STRESS_IMPACT, stressImpact(block));
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_ROTATION);
		addEmptyLine(tooltip);
		section(tooltip, SlTranKeys.TOOLTIP_COMMON_CONDITION);
		behaviour(tooltip, SlTranKeys.TOOLTIP_STEAM_SAW_CONSUMPTION, SteamSawConfig.STEAM_CONSUMPTION.get());
		superheatedConsumption(tooltip);
	}

	private static void addAdvancedSpoutTooltip(List<Component> tooltip) {
		if (!requireShift(tooltip)) {
			return;
		}

		addEmptyLine(tooltip);
		line(tooltip, SlTranKeys.TOOLTIP_ADVANCED_SPOUT_SUMMARY, AdvancedSpoutBlockEntity.FILLING_TIME);
		line(tooltip, SlTranKeys.TOOLTIP_ADVANCED_SPOUT_PARALLEL, AdvancedSpoutConfig.PARALLEL.get());
		line(tooltip, SlTranKeys.TOOLTIP_ADVANCED_SPOUT_CAPACITY, AdvancedSpoutConfig.CAPACITY.get());
		line(tooltip, SlTranKeys.TOOLTIP_ADVANCED_SPOUT_NO_STEAM);
	}

	private static void addBronzeBoilerTooltip(List<Component> tooltip, Block block) {
		if (!requireShift(tooltip)) {
			return;
		}

		SteamEngineSpec spec = engineSpec(block);
		if (spec == null) {
			return;
		}

		addEmptyLine(tooltip);
		line(tooltip, SlTranKeys.TOOLTIP_BRONZE_BOILER_SUMMARY);
		line(tooltip, SlTranKeys.TOOLTIP_BRONZE_BOILER_NEEDS_BURNER);
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_PRODUCTION, number(spec.getProduction()));
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_WATER_STEAM_CAPACITY, spec.getCapacity(), spec.getCapacity());
		line(tooltip, SlTranKeys.TOOLTIP_BRONZE_BOILER_SUPERHEATED);
		addEmptyLine(tooltip);
		section(tooltip, SlTranKeys.TOOLTIP_BRONZE_BOILER_LARGE_HEADER);
		behaviour(tooltip, SlTranKeys.TOOLTIP_BRONZE_BOILER_LARGE_SIZE);
		behaviour(tooltip, SlTranKeys.TOOLTIP_BRONZE_BOILER_LARGE_PRODUCTION, number(spec.getLargeProduction()));
		behaviour(tooltip, SlTranKeys.TOOLTIP_BRONZE_BOILER_LARGE_HEAT);
	}

	private static void addSolarBoilerTooltip(List<Component> tooltip, Block block) {
		if (!requireShift(tooltip)) {
			return;
		}

		SteamEngineSpec spec = engineSpec(block);
		if (spec == null) {
			return;
		}

		addEmptyLine(tooltip);
		line(tooltip, SlTranKeys.TOOLTIP_SOLAR_BOILER_SUMMARY);
		line(tooltip, SlTranKeys.TOOLTIP_SOLAR_BOILER_LIGHT, percent(SolarBoilerConfig.SUNLIGHT_EFFICIENCY.get()), percent(SolarBoilerConfig.ARTIFICIAL_LIGHT_EFFICIENCY.get()), SolarBoilerConfig.ARTIFICIAL_LIGHT_THRESHOLD.get());
		line(tooltip, SlTranKeys.TOOLTIP_SOLAR_BOILER_WEATHER, percent(SolarBoilerConfig.RAIN_EFFICIENCY.get()), percent(SolarBoilerConfig.THUNDER_EFFICIENCY.get()));
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_PRODUCTION, number(spec.getProduction()));
		line(tooltip, SlTranKeys.TOOLTIP_COMMON_WATER_STEAM_CAPACITY, spec.getCapacity(), spec.getCapacity());
	}

	private static void addBurnerTooltip(List<Component> tooltip, Block block, String summaryKey, boolean fluid) {
		if (!requireShift(tooltip)) {
			return;
		}

		SteamEngineSpec spec = engineSpec(block);
		if (spec == null) {
			return;
		}

		addEmptyLine(tooltip);
		line(tooltip, summaryKey);
		line(tooltip, SlTranKeys.TOOLTIP_BURNER_MAX_HEAT, number(spec.getMaxHeat()));
		line(tooltip, SlTranKeys.TOOLTIP_BURNER_CYCLE, number(SteamBurnerConfig.HEATUP_SECONDS.get()), number(SteamBurnerConfig.COOLDOWN_SECONDS.get()));
		line(tooltip, SlTranKeys.TOOLTIP_BURNER_SUPERHEATED, number(SteamBurnerConfig.SUPERHEATED_HEATUP_MULTIPLIER.get()), percent(SteamBurnerConfig.SUPERHEATED_MAX_HEAT_MULTIPLIER));
		if (fluid) {
			line(tooltip, SlTranKeys.TOOLTIP_FLUID_BURNER_TANK, spec.getCapacity());
			line(tooltip, SlTranKeys.TOOLTIP_FLUID_BURNER_FUEL_CYCLE);
		}
	}

	private static void addSteamEngineTooltip(List<Component> tooltip, Block block) {
		if (!requireShift(tooltip)) {
			return;
		}

		if (!(block instanceof SteamEngineBlock engineBlock)) {
			return;
		}

		SteamEngineDriveSpec spec = engineBlock.getDriveSpec();

		addEmptyLine(tooltip);
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_ENGINE_SUMMARY);
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_ENGINE_OUTPUT, number(spec.getStrength()), number(spec.getSpeed()));
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_ENGINE_CONSUMPTION, number(spec.getConsumption()));
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_ENGINE_CAPACITY, spec.getCapacity());
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_ENGINE_HEATUP, number(SteamEngineConfig.HEATUP_SECONDS.get()));
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_ENGINE_DIRECT_DRAW, percent(SteamEngineConfig.SUCK_EFFICIENCY.get()));
		addEmptyLine(tooltip);
		section(tooltip, SlTranKeys.TOOLTIP_STEAM_ENGINE_SUPERHEATED_HEADER);
		behaviour(tooltip, SlTranKeys.TOOLTIP_STEAM_ENGINE_SUPERHEATED_EFFECTS, number(SteamEngineConfig.SUPERHEATED_CONSUMPTION_MULTIPLIER.get()), number(SteamEngineConfig.SUPERHEATED_OUTPUT_MULTIPLIER.get()));
	}

	private static void addSteamContainerTooltip(List<Component> tooltip, ItemStack stack) {
		if (!(stack.getItem() instanceof SteamContainerItem item)) {
			return;
		}

		boolean emptied = item.isEmptied();
		FluidStack stored = SteamContainerItem.readFluid(stack);

		// 状态行始终可见: 空容器报容量, 密封容器报当前存量.
		Component status = emptied || stored.isEmpty()
				? SlLang.translateDirect(SlTranKeys.TOOLTIP_STEAM_CONTAINER_EMPTY, SteamContainerItem.CAPACITY)
				: SlLang.translateDirect(SlTranKeys.TOOLTIP_STEAM_CONTAINER_FLUID, stored.getDisplayName(), stored.getAmount(), SteamContainerItem.CAPACITY);

		tooltip.addAll(TooltipHelper.cutStringTextComponent(status.getString(), FontHelper.Palette.STANDARD_CREATE.primary(), FontHelper.Palette.STANDARD_CREATE.highlight(), 0));

		SlLang.isShiftDown(tooltip);

		if (!SlLang.isShiftDown()) {
			return;
		}

		addEmptyLine(tooltip);
		behaviour(tooltip, SlTranKeys.TOOLTIP_STEAM_CONTAINER_ONLY_STEAM);

		if (emptied) {
			behaviour(tooltip, SlTranKeys.TOOLTIP_STEAM_CONTAINER_EXTRACT);
			return;
		}

		behaviour(tooltip, SlTranKeys.TOOLTIP_STEAM_CONTAINER_INSERT);
		behaviour(tooltip, SlTranKeys.TOOLTIP_STEAM_CONTAINER_EXTRACT_MORE);
	}

	private static void addSteamPumpTooltip(List<Component> tooltip) {
		if (!requireShift(tooltip)) {
			return;
		}

		addEmptyLine(tooltip);
		line(tooltip, SlTranKeys.TOOLTIP_STEAM_PUMP_SUMMARY);
		addEmptyLine(tooltip);
		behaviour(tooltip, SlTranKeys.TOOLTIP_STEAM_PUMP_BEHAVIOUR1);
		behaviour(tooltip, SlTranKeys.TOOLTIP_STEAM_PUMP_BEHAVIOUR2);
		behaviour(tooltip, SlTranKeys.TOOLTIP_STEAM_PUMP_BEHAVIOUR3);
	}

	private static void addBronzeBasinTooltip(List<Component> tooltip) {
		if (!requireShift(tooltip)) {
			return;
		}

		addEmptyLine(tooltip);
		line(tooltip, SlTranKeys.TOOLTIP_BRONZE_BASIN_SUMMARY, BronzeBasinConfig.SLOT_CAPACITY.get());
		addEmptyLine(tooltip);
		behaviour(tooltip, SlTranKeys.TOOLTIP_BRONZE_BASIN_BEHAVIOUR1);
		behaviour(tooltip, SlTranKeys.TOOLTIP_BRONZE_BASIN_BEHAVIOUR2);
	}

	private static void addSHSteamComponentTooltip(List<Component> tooltip) {
		if (!requireShift(tooltip)) {
			return;
		}

		addEmptyLine(tooltip);
		line(tooltip, SlTranKeys.TOOLTIP_SH_STEAM_COMPONENT_SUMMARY);
		addEmptyLine(tooltip);
		behaviour(tooltip, SlTranKeys.TOOLTIP_SH_STEAM_COMPONENT_BEHAVIOUR1);
		behaviour(tooltip, SlTranKeys.TOOLTIP_SH_STEAM_COMPONENT_BEHAVIOUR2);
		behaviour(tooltip, SlTranKeys.TOOLTIP_SH_STEAM_COMPONENT_BEHAVIOUR3);
	}

	private static void addBronzeDepot(List<Component> tooltip) {
		tooltip.add(Component.translatable(SlTranKeys.WIP_HIDE_WARNING));
	}

	private static boolean addEmptyLine(List<Component> tooltip) {
		return tooltip.add(Component.empty());
	}

	private static SteamEngineSpec engineSpec(Block block) {
		if (!(block instanceof ISteamEngineBlock engineBlock)) {
			return null;
		}

		return engineBlock.getEngineSpec();
	}

	private static String stressImpact(Block block) {
		return number(BlockStressValues.getImpact(block)) + " su";
	}

	private static String number(double value) {
		if (Math.abs(value - Math.rint(value)) < 1.0E-4D) {
			return Long.toString((long) Math.rint(value));
		}

		return String.format(Locale.ROOT, "%.1f", value);
	}

	private static String percent(double ratio) {
		return Math.round(ratio * 100.0D) + "%";
	}
}
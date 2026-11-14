package dev.celestiacraft.steam_letters.datagen.language;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.client.ponder.SlPonderPlugin;
import dev.celestiacraft.steam_letters.common.block.advanced_spout.AdvancedSpoutScrollValueBehaviour;
import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.createmod.ponder.api.registration.LangRegistryAccess;
import net.createmod.ponder.foundation.PonderIndex;

public class English {
	public static void addTranslations(RegistrateLangProvider provider) {
		addOtherLang(provider);
		addCommonLang(provider);
		addSteamHammerLang(provider);
		addSteamMixerLang(provider);
		addSteamSawLang(provider);
		addSteamPumpLang(provider);
		addSteamPipeLang(provider);
		addBronzeBasinLang(provider);
		addAdvancedSpoutLang(provider);
		addAdvancedSpoutTooltipLang(provider);
		addGoggleLang(provider);
		addBronzeBoilerLang(provider);
		addSolarBoilerLang(provider);
		addBurnerLang(provider);
		addSteamEngineLang(provider);
		addSteamEngineTooltipLang(provider);
		addJeiLang(provider);
		addSteamContainerLang(provider);
		addConfigLang(provider);
		addConfigCommentLang(provider);

		SlPonderPlugin.register();
		LangRegistryAccess access = PonderIndex.getLangAccess();
		access.provideLang(CreateLettersSteam.MODID, provider::add);
	}

	private static void addCommonLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_PARALLEL), "Processes up to _%s_ items per cycle");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_CAPACITY), "Holds _%s mB_ of steam");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_CONSUMPTION), "Consumes _%s mB_ of steam");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_ROTATION), "Requires rotational force");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_STRESS_IMPACT), "Kinetic Stress Impact: _%s_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_SUPERHEATED_CONSUMPTION), "Consumes only _%s_ of the steam when running on _superheated steam_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_CONDITION), "When working:");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_PRODUCTION), "Produces _%s mB/t_ of steam at full efficiency");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_WATER_STEAM_CAPACITY), "Holds _%s mB_ of water and _%s mB_ of steam");
	}

	private static void addOtherLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.BASIC_TAB, CreateLettersSteam.NAME);
		provider.add(SlTranKeys.THERMAL_PACK_TITLE, "§6Thermal Hue Resource Pack");
		provider.add(SlTranKeys.THERMAL_PACK_DESCRIPTION, "§6Recolors bronze casings and machines after Thermal Expansion's bronze");
		provider.add(SlTranKeys.WIP_HIDE_WARNING, "§cHidden temporarily due to numerous bugs");
	}

	private static void addSteamMixerLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_MIXER_SUMMARY), "Processes mixing recipes");
	}

	private static void addSteamSawLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_SAW_SUMMARY), "Processes cutting recipes");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_SAW_PROCESSING_TIME), "Takes only _%s_ of the time a Mechanical Saw needs");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_SAW_CONSUMPTION), "Consumes _%s mB_ of steam per output");
	}

	private static void addAdvancedSpoutTooltipLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_ADVANCED_SPOUT_SUMMARY), "Finishes a pouring cycle in _%s_ ticks");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_ADVANCED_SPOUT_PARALLEL), "Has _%s_ parallel slots (adjustable with a Wrench, 1–20)");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_ADVANCED_SPOUT_CAPACITY), "Holds _%s mB_ of fluid");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_ADVANCED_SPOUT_NO_STEAM), "Requires neither steam nor rotational force");
	}

	private static void addBronzeBoilerLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BOILER_SUMMARY), "Joins into a multi-block tank that heats water into steam");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BOILER_NEEDS_BURNER), "Requires seething blaze burners directly below");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BOILER_SUPERHEATED), "Superheated with a superheated steam component installed");
	}

	private static void addSolarBoilerLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_SOLAR_BOILER_SUMMARY), "Turns water into steam with light");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_SOLAR_BOILER_LIGHT), "Sunlight _%s_, artificial light _%s_ (light level ≥ _%s_)");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_SOLAR_BOILER_WEATHER), "Rain _%s_, thunder _%s_");
	}

	private static void addBurnerLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_SOLID_BURNER_SUMMARY), "Provides heat to the boiler above");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_FLUID_BURNER_SUMMARY), "Burns fluid fuel to heat the boiler above");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BURNER_MAX_HEAT), "Maximum heat _%s_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BURNER_CYCLE), "Takes _%s_ s to heat up and _%s_ s to cool down");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BURNER_SUPERHEATED), "Superheated fuel heats up x_%s_ and can reach _%s_ of the maximum heat");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_FLUID_BURNER_TANK), "Holds _%s mB_ of fuel");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_FLUID_BURNER_FUEL_CYCLE), "Burns one cycle per _1000 mB_ of fuel");
	}

	private static void addSteamEngineTooltipLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_SUMMARY), "Turns steam into stress");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_OUTPUT), "Outputs _%s SU_ at _%s RPM_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_CONSUMPTION), "Consumes _%s mB/t_ of steam at full load");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_CAPACITY), "Holds _%s mB_ of steam");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_HEATUP), "Takes _%s_ s to warm up to full output");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_DIRECT_DRAW), "Draws at _%s_ efficiency from an adjacent boiler");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_SUPERHEATED_HEADER), "Superheated steam:");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_SUPERHEATED_EFFECTS), "Steam use x_%s_, stress output x_%s_");
	}

	private static void addSteamContainerLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_EMPTY), "Holds up to _%s mB_ of steam");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_FLUID), "%s: _%s_ / %s mB");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_ONLY_STEAM), "Stores steam and superheated steam only");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_EXTRACT), "▸ Right-click a boiler, fluid tank or steam engine to extract steam");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_INSERT), "▸ Right-click a fluid tank or steam engine to inject steam");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_EXTRACT_MORE), "▸ Sneak-right-click to keep extracting");
	}

	private static void addGoggleLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BURNER), "Steam Burner");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BURNER_TEMPERATURE), "Temperature");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BURNER_FUEL_TIME), "Fuel Remaining");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BURNER_EFFICIENCY), "Steam Output");

		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER), "Solar Boiler");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_WORKING), "Producing Steam");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_IDLE), "No Light");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_SUNLIGHT), "Light: Sunlight");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_RAIN), "Light: Sunlight (Rain)");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_THUNDER), "Light: Sunlight (Thunder)");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_ARTIFICIAL), "Light: Artificial");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_NONE), "Light: None");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_PRODUCTION), "Steam Production");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_RATE), "Share of Full Output");

		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_BOILER_STEAM_OUTPUT_RATE), "Steam Output Rate");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_BOILER_WATER_STORED), "Water Stored");

		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_STEAM), "Steam");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_LEVEL), "Boiler Level");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_SIZE), "Structure Size");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_BURNERS), "Seething Burners");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_HEAT), "Heat");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_EFFICIENCY), "Efficiency");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED), "Superheated");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED_YES), "Yes");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED_NO), "No");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_NO_BURNER), "No burner below");

		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_MACHINE_STEAM), "Steam");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_MACHINE_OPERATIONS), "Operations");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_MACHINE_PARALLEL), "Parallel");
	}

	private static void addSteamEngineLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE), "Steam Engine");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_STATUS_IDLE), "Idle");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_STATUS_HEATUP), "Heating Up");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_STATUS_RUNNING), "Running");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_HEATUP), "Heat-up");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_STRESS), "Stress Output");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_STEAM), "Steam");
	}

	private static void addJeiLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.JEI_SOLID_FUEL), "Solid Fuel");
		provider.add(SlTranKeys.langKey(SlTranKeys.JEI_FLUID_FUEL), "Fluid Fuel");
		provider.add(SlTranKeys.langKey(SlTranKeys.JEI_HEATED), "Heated");
		provider.add(SlTranKeys.langKey(SlTranKeys.JEI_SUPERHEATED), "Superheated");
	}

	private static void addSteamHammerLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_HAMMER_SUMMARY), "Handles _Pressing_, _Compacting_ and _Automated Packing_ recipes");
	}

	private static void addSteamPumpLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PUMP_SUMMARY), "Only ever _moves steam_, and only when it is the _sole_ medium in the pipe");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PUMP_BEHAVIOUR1), "Requires _no rotational force_ and consumes _no stress");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PUMP_BEHAVIOUR2), "Works on its own once a steam pipe or tank is attached");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PUMP_BEHAVIOUR3), "Shuts down entirely if any other fluid enters the suction network");
	}

	private static void addSteamPipeLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PIPE_SUMMARY), "Only _steam_ may flow through this pipe");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PIPE_BEHAVIOUR), "Use it together with the _Bronze Steam Pump_");
	}

	private static void addBronzeBasinLang(RegistrateLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BASIN_SUMMARY), "Each fluid slot holds _%s mB_ of fluid, 4 slots in total");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BASIN_BEHAVIOUR1), "Accepts _multiple fluids_, used by mixing and compacting recipes");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BASIN_BEHAVIOUR2), "Processes more recipes before it needs to be refilled");

		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_BRONZE_DEPOT_CONTENTS), "Bronze Depot Contents");
	}

	private static void addAdvancedSpoutLang(RegistrateLangProvider provider) {
		provider.add(AdvancedSpoutScrollValueBehaviour.LANG_PARALLEL, "Parallel");
		provider.add(AdvancedSpoutScrollValueBehaviour.LANG_VALUE_PARALLEL, "x%s");
	}

	private static void addConfigLang(RegistrateLangProvider provider) {
		provider.add(ConfigModule.translationKey("general"), "General");

		provider.add(ConfigModule.translationKey("steam"), "Steam");
		provider.add(ConfigModule.translationKey("steam", "superheated_steam_consumption_multiplier"), "Steam consumption multiplier when the machine runs on superheated steam");

		provider.add(ConfigModule.translationKey("steam_hammer"), "Steam Hammer");
		provider.add(ConfigModule.translationKey("steam_hammer", "steam_hammer_parallel"), "Steam Hammer recipe parallel");
		provider.add(ConfigModule.translationKey("steam_hammer", "steam_consumption"), "Steam consumption per run of steam hammer (mB)");
		provider.add(ConfigModule.translationKey("steam_hammer", "steam_capacity"), "Steam capacity in steam hammer (mB)");

		provider.add(ConfigModule.translationKey("steam_mixer"), "Steam Mixer");
		provider.add(ConfigModule.translationKey("steam_mixer", "steam_mixer_parallel"), "Steam Mixer recipe parallel");
		provider.add(ConfigModule.translationKey("steam_mixer", "steam_consumption"), "Steam consumption per run of steam mixer (mB)");
		provider.add(ConfigModule.translationKey("steam_mixer", "steam_capacity"), "Steam capacity in steam mixer (mB)");

		provider.add(ConfigModule.translationKey("steam_saw"), "Steam Saw");
		provider.add(ConfigModule.translationKey("steam_saw", "processing_time_multiplier"), "Steam Saw working time multiplier (0.25 = 4 times faster than a mechanical saw)");
		provider.add(ConfigModule.translationKey("steam_saw", "steam_capacity"), "Steam capacity in steam saw (mB)");
		provider.add(ConfigModule.translationKey("steam_saw", "steam_consumption"), "Steam consumption per run of steam saw (mB)");
		provider.add(ConfigModule.translationKey("steam_saw", "steam_saw_parallel"), "Steam Saw recipe parallel");

		provider.add(ConfigModule.translationKey("bronze_basin"), "Bronze Basin");
		provider.add(ConfigModule.translationKey("bronze_basin", "slot_capacity"), "Fluid capacity of each bronze basin tank (mB)");

		provider.add(ConfigModule.translationKey("advanced_spout"), "Advanced Spout");
		provider.add(ConfigModule.translationKey("advanced_spout", "capacity"), "Fluid capacity in advanced spout (mB)");
		provider.add(ConfigModule.translationKey("advanced_spout", "parallel"), "Default parallel of newly placed advanced spouts");

		provider.add(ConfigModule.translationKey("steam_boiler"), "Steam Boiler");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_threshold_1"), "Heat percentage at which the boiler reaches efficiency tier 1");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_threshold_2"), "Heat percentage at which the boiler reaches efficiency tier 2");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_threshold_3"), "Heat percentage at which the boiler reaches efficiency tier 3");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_threshold_4"), "Heat percentage at which the boiler reaches efficiency tier 4");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_1"), "Steam output multiplier of efficiency tier 1");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_2"), "Steam output multiplier of efficiency tier 2");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_3"), "Steam output multiplier of efficiency tier 3");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_4"), "Steam output multiplier of efficiency tier 4");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_threshold_1"), "Heat percentage at which superheated production reaches tier 2");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_threshold_2"), "Heat percentage at which superheated production reaches tier 3");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_threshold_3"), "Heat percentage at which superheated production reaches tier 4");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_production_1"), "Superheated steam output multiplier right above the maximum heat");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_production_2"), "Superheated steam output multiplier of tier 2");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_production_3"), "Superheated steam output multiplier of tier 3");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_production_4"), "Superheated steam output multiplier of tier 4");

		provider.add(ConfigModule.translationKey("solar_boiler"), "Solar Boiler");
		provider.add(ConfigModule.translationKey("solar_boiler", "render_fluid"), "Render fluid inside the solar boiler");
		provider.add(ConfigModule.translationKey("solar_boiler", "sunlight_efficiency"), "Efficiency multiplier when the solar boiler has unobstructed sky access");
		provider.add(ConfigModule.translationKey("solar_boiler", "artificial_light_efficiency"), "Efficiency multiplier when the solar boiler runs on artificial light");
		provider.add(ConfigModule.translationKey("solar_boiler", "artificial_light_threshold"), "Minimum block light level required for artificial light operation");
		provider.add(ConfigModule.translationKey("solar_boiler", "require_daylight"), "Whether sunlight operation also requires it to be daytime");
		provider.add(ConfigModule.translationKey("solar_boiler", "rain_efficiency"), "Weather multiplier while raining");
		provider.add(ConfigModule.translationKey("solar_boiler", "thunder_efficiency"), "Weather multiplier during thunderstorms");

		provider.add(ConfigModule.translationKey("steam_burner"), "Steam Burner");
		provider.add(ConfigModule.translationKey("steam_burner", "heatup_seconds"), "Seconds required for the burner to reach maximum heat");
		provider.add(ConfigModule.translationKey("steam_burner", "cooldown_seconds"), "Seconds required for the burner to lose all of its heat");
		provider.add(ConfigModule.translationKey("steam_burner", "superheated_heatup_multiplier"), "Heat-up speed multiplier while burning superheated fuel");

		provider.add(ConfigModule.translationKey("steam_engine"), "Steam Engine");
		provider.add(ConfigModule.translationKey("steam_engine", "suck_efficiency"), "Ratio of steam gained when a flywheel engine draws steam directly from a boiler (0.75 = 25% loss)");
		provider.add(ConfigModule.translationKey("steam_engine", "superheated_consumption_multiplier"), "Steam consumption multiplier of a flywheel engine running on superheated steam");
		provider.add(ConfigModule.translationKey("steam_engine", "heatup_seconds"), "Seconds the engine needs to heat up before outputting its full stress");
		provider.add(ConfigModule.translationKey("steam_engine", "superheated_acceleration_multiplier"), "Divisor applied to the heat-up time when running on superheated steam");
		provider.add(ConfigModule.translationKey("steam_engine", "superheated_output_multiplier"), "Stress output multiplier when running on superheated steam");

		provider.add(ConfigModule.translationKey("boiler_steam"), "Create Boiler Steam");
		provider.add(ConfigModule.translationKey("boiler_steam", "base_consumption"), "Steam output per continuous boiler level per tick (mB)");
		provider.add(ConfigModule.translationKey("boiler_steam", "steam_per_water"), "Steam produced per mB of water (mB)");
		provider.add(ConfigModule.translationKey("boiler_steam", "buffer_multiplier"), "Steam buffer capacity multiplier (1000 mB per boiler block)");
		provider.add(ConfigModule.translationKey("boiler_steam", "water_per_block"), "Water buffer capacity per boiler block (mB)");
		provider.add(ConfigModule.translationKey("boiler_steam", "strict_quota"), "Use an explicit device quota instead of flow thinning");
		provider.add(ConfigModule.translationKey("boiler_steam", "superheated_at_level"), "Minimum continuous boiler level required to produce superheated steam");
		provider.add(ConfigModule.translationKey("boiler_steam", "superheated_water_multiplier"), "Water consumption multiplier while producing superheated steam");

		provider.add(ConfigModule.translationKey("steam", "output_steam_fluid"), "Fluid id produced by boilers. Existing fluid inside machines is never converted when this changes");
		provider.add(ConfigModule.translationKey("steam", "output_superheated_steam_fluid"), "Fluid id produced by boilers while superheated. Existing fluid inside machines is never converted when this changes");
		provider.add(ConfigModule.translationKey("steam", "do_overheated_steam_replace"), "Whether the normal steam left in the output tank is converted into superheated steam instead of being destroyed");
	}

	/**
	 * 配置项注释的英文键.
	 * <p>
	 * Configured 会以 {@code <界面翻译键>.comment} 查询注释, 命中即显示译文, 未命中则回退到 toml 里的原文.
	 * 因此这里的内容必须与配置文件中的英文注释逐条对应.
	 */
	private static void addConfigCommentLang(RegistrateLangProvider provider) {
		comment(provider, ConfigModule.translationKey("general"), "All settings below only take effect after restarting the server or client.");

		comment(provider, ConfigModule.translationKey("steam", "superheated_steam_consumption_multiplier"), "Global steam consumption multiplier for machines running on superheated steam.\n" + "0.25 means such a machine only needs a quarter of the normal steam.");
		comment(provider, ConfigModule.translationKey("steam", "output_steam_fluid"), "Fluid id produced by steam boilers and solar boilers.\n" + "Existing tank contents are never converted when this changes.");
		comment(provider, ConfigModule.translationKey("steam", "output_superheated_steam_fluid"), """
				Fluid id produced by boilers while they run superheated.
				Must not point to the same fluid as output_steam_fluid, otherwise the config fails to load.
				Existing tank contents are never converted when this changes.""");
		comment(provider, ConfigModule.translationKey("steam", "do_overheated_steam_replace"), "When false the normal steam left in the output tank is destroyed; when true it is converted " + "into the same amount of superheated steam in place.\n" + "Neither option consumes additional water.");

		comment(provider, ConfigModule.translationKey("steam_hammer", "steam_hammer_parallel"), "Maximum number of recipes a single steam hammer processes at once.");
		comment(provider, ConfigModule.translationKey("steam_hammer", "steam_consumption"), "Steam consumed per finished recipe (mB).");
		comment(provider, ConfigModule.translationKey("steam_hammer", "steam_capacity"), "Steam buffer capacity of a steam hammer (mB).");

		comment(provider, ConfigModule.translationKey("steam_mixer", "steam_mixer_parallel"), "Maximum number of recipes a single steam mixer processes at once.");
		comment(provider, ConfigModule.translationKey("steam_mixer", "steam_consumption"), "Steam consumed per finished recipe (mB).");
		comment(provider, ConfigModule.translationKey("steam_mixer", "steam_capacity"), "Steam buffer capacity of a steam mixer (mB).");

		comment(provider, ConfigModule.translationKey("steam_saw", "steam_saw_parallel"), "Maximum number of recipes a single steam saw processes at once.");
		comment(provider, ConfigModule.translationKey("steam_saw", "steam_consumption"), "Steam consumed per finished recipe (mB).");
		comment(provider, ConfigModule.translationKey("steam_saw", "steam_capacity"), "Steam buffer capacity of a steam saw (mB).");
		comment(provider, ConfigModule.translationKey("steam_saw", "processing_time_multiplier"), "Working time multiplier of the steam saw (0.25 = 4 times faster than a mechanical saw).");

		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_threshold_1"), "Lowest heat percentage at which the boiler starts producing steam.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_threshold_2"), "Heat percentage at which the boiler reaches efficiency tier 2.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_threshold_3"), "Heat percentage at which the boiler reaches efficiency tier 3.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_threshold_4"), "Heat percentage required for the boiler to run at full efficiency.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_1"), "Steam output multiplier of efficiency tier 1.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_2"), "Steam output multiplier of efficiency tier 2.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_3"), "Steam output multiplier of efficiency tier 3.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_4"), "Steam output multiplier of efficiency tier 4, i.e. the maximum boiler output.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_threshold_1"), "Heat percentage at which superheated production reaches tier 2.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_threshold_2"), "Heat percentage at which superheated production reaches tier 3.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_threshold_3"), "Heat percentage at which superheated production reaches its maximum rate.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_production_1"), "Superheated steam output multiplier while heat stays right above the maximum heat.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_production_2"), "Superheated steam output multiplier of tier 2.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_production_3"), "Superheated steam output multiplier of tier 3.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_production_4"), "Superheated steam output multiplier of tier 4, i.e. the maximum superheated output.");

		comment(provider, ConfigModule.translationKey("solar_boiler", "render_fluid"), "Whether or not fluids inside the solar boiler will be rendered.\n" + "Hiding the fluid does not affect steam production.");
		comment(provider, ConfigModule.translationKey("solar_boiler", "sunlight_efficiency"), "Efficiency multiplier when the boiler has direct sky light (and no weather penalty).");
		comment(provider, ConfigModule.translationKey("solar_boiler", "artificial_light_efficiency"), "Efficiency multiplier when the boiler is lit by block light instead of sky light.");
		comment(provider, ConfigModule.translationKey("solar_boiler", "artificial_light_threshold"), "Minimum block light level for the artificial light path to be usable.");
		comment(provider, ConfigModule.translationKey("solar_boiler", "require_daylight"), "Whether the sky light path additionally requires daytime.\n" + "Vanilla sky light level stays at 15 at night, so without this the boiler would " + "produce at full speed in complete darkness.");
		comment(provider, ConfigModule.translationKey("solar_boiler", "rain_efficiency"), "Weather multiplier while it is raining at the boiler.");
		comment(provider, ConfigModule.translationKey("solar_boiler", "thunder_efficiency"), "Weather multiplier while a thunder storm is raging at the boiler " + "(includes snow during thunder).");

		comment(provider, ConfigModule.translationKey("steam_burner", "heatup_seconds"), "Total time required for the burner to reach its maximum heat while fuel is burning.");
		comment(provider, ConfigModule.translationKey("steam_burner", "cooldown_seconds"), "Total time required for the burner to lose all of its heat while no fuel is burning.");
		comment(provider, ConfigModule.translationKey("steam_burner", "superheated_heatup_multiplier"), "Divisor applied to the heat-up time while burning superheated fuel and below the maximum heat.\n" + "4.0 means the burner heats up four times as fast.");

		comment(provider, ConfigModule.translationKey("steam_engine", "suck_efficiency"), "Efficiency (0~1) of steam drawn directly from an adjacent boiler.\n" + "4n mB is drawn out of the boiler per tick while only 3n mB reaches the engine, " + "so a directly fed engine caps out at 75% of its stress output.");
		comment(provider, ConfigModule.translationKey("steam_engine", "superheated_consumption_multiplier"), "Steam consumption multiplier applied while the flywheel engine runs on superheated steam.\n" + "0.5 means it only needs half the steam of a normal-steam run.");
		comment(provider, ConfigModule.translationKey("steam_engine", "heatup_seconds"), """
				Seconds required for the flywheel engine to heat up before it outputs its full stress.
				The demand climbs through 25%, 50% and 75% of the full load during the heat-up.
				0 means no heat-up at all.""");
		comment(provider, ConfigModule.translationKey("steam_engine", "superheated_acceleration_multiplier"), "Divisor applied to the heat-up time while running on superheated steam.\n" + "2.0 means the heat-up takes half as long.");
		comment(provider, ConfigModule.translationKey("steam_engine", "superheated_output_multiplier"), "Stress output multiplier applied while running on superheated steam.");

		comment(provider, ConfigModule.translationKey("boiler_steam", "base_consumption"), "Steam produced per tick for each continuous level of the boiler (mB).\n" + "Should match the full-load steam demand of one engine for the quota to line up.");
		comment(provider, ConfigModule.translationKey("boiler_steam", "steam_per_water"), """
				A boiler draws water out of its own water buffer, which is filled by whatever flows in.
				This ratio is therefore the only link between that water and the steam output.
				1.2 makes the water a level needs equal to the 10 mB per level that the level itself demands.""");
		comment(provider, ConfigModule.translationKey("boiler_steam", "water_per_block"), """
				Water buffer capacity = boiler block count * this value.
				Water is no longer consumed the moment it flows in, so a full steam buffer does not
				force the boiler to swallow water for nothing.
				0 disables the buffer, and the boiler falls back to counting only the water that flows through it.""");
		comment(provider, ConfigModule.translationKey("boiler_steam", "buffer_multiplier"), "Buffer capacity = boiler block count * 1000 * this multiplier.\n" + "0 means the boiler stores no steam.");
		comment(provider, ConfigModule.translationKey("boiler_steam", "strict_quota"), """
				When true steam engines and bronze pumps share one quota, so a boiler of level L feeds
				min(1, L / (engines + pumps)) to every attached engine.
				Adding a pump to a full quota therefore lowers the stress of every engine.
				This also covers the steam engines of Create itself, which never consume fluid and
				derive their stress from this efficiency alone.
				When false the boiler only limits the total steam flow and leaves the quota to scarcity.""");
		comment(provider, ConfigModule.translationKey("boiler_steam", "superheated_at_level"), "Level 18 requires 18 points of heat, at least 72 boiler blocks and 180 mB/t of water supply.");
		comment(provider, ConfigModule.translationKey("boiler_steam", "superheated_water_multiplier"), "Water consumption multiplier while producing superheated steam.\n" + "Kept in line with the superheated output multiplier of the engine so the switch does not create free output.");

		comment(provider, ConfigModule.translationKey("bronze_basin", "slot_capacity"), "Fluid capacity of each tank of a bronze basin (mB).\n" + "Basins have several tanks, so the total capacity is a multiple of this.");

		comment(provider, ConfigModule.translationKey("advanced_spout", "capacity"), "Fluid capacity of an advanced spout (mB).");
		comment(provider, ConfigModule.translationKey("advanced_spout", "parallel"), "Default parallel of newly placed advanced spouts.\n" + "Can still be changed per block with a wrench.");
	}

	private static void comment(RegistrateLangProvider provider, String translationKey, String comment) {
		provider.add(translationKey + ".comment", comment);
	}
}
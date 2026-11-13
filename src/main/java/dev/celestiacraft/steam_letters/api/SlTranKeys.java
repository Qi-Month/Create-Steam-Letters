package dev.celestiacraft.steam_letters.api;

import dev.celestiacraft.steam_letters.CreateLettersSteam;

public class SlTranKeys {
	public static final String
			BASIC_TAB,
			THERMAL_PACK_TITLE,
			THERMAL_PACK_DESCRIPTION,
			WIP_HIDE_WARNING;

	public static final String
			TOOLTIP_COMMON_PARALLEL,
			TOOLTIP_COMMON_CAPACITY,
			TOOLTIP_COMMON_CONSUMPTION,
			TOOLTIP_COMMON_ROTATION,
			TOOLTIP_COMMON_STRESS_IMPACT,
			TOOLTIP_COMMON_SUPERHEATED_CONSUMPTION,
			TOOLTIP_COMMON_CONDITION,
			TOOLTIP_COMMON_PRODUCTION,
			TOOLTIP_COMMON_WATER_STEAM_CAPACITY;

	public static final String
			TOOLTIP_STEAM_HAMMER_SUMMARY,
			TOOLTIP_STEAM_MIXER_SUMMARY,
			TOOLTIP_STEAM_SAW_SUMMARY,
			TOOLTIP_STEAM_SAW_PROCESSING_TIME,
			TOOLTIP_STEAM_SAW_CONSUMPTION;

	public static final String
			TOOLTIP_ADVANCED_SPOUT_SUMMARY,
			TOOLTIP_ADVANCED_SPOUT_PARALLEL,
			TOOLTIP_ADVANCED_SPOUT_CAPACITY,
			TOOLTIP_ADVANCED_SPOUT_NO_STEAM;

	public static final String
			TOOLTIP_BRONZE_BOILER_SUMMARY,
			TOOLTIP_BRONZE_BOILER_NEEDS_BURNER,
			TOOLTIP_BRONZE_BOILER_SUPERHEATED;

	public static final String
			TOOLTIP_SOLAR_BOILER_SUMMARY,
			TOOLTIP_SOLAR_BOILER_LIGHT,
			TOOLTIP_SOLAR_BOILER_WEATHER;

	public static final String
			TOOLTIP_SOLID_BURNER_SUMMARY,
			TOOLTIP_FLUID_BURNER_SUMMARY,
			TOOLTIP_BURNER_MAX_HEAT,
			TOOLTIP_BURNER_CYCLE,
			TOOLTIP_BURNER_SUPERHEATED,
			TOOLTIP_FLUID_BURNER_TANK,
			TOOLTIP_FLUID_BURNER_FUEL_CYCLE;

	public static final String
			TOOLTIP_STEAM_ENGINE_SUMMARY,
			TOOLTIP_STEAM_ENGINE_OUTPUT,
			TOOLTIP_STEAM_ENGINE_CONSUMPTION,
			TOOLTIP_STEAM_ENGINE_CAPACITY,
			TOOLTIP_STEAM_ENGINE_HEATUP,
			TOOLTIP_STEAM_ENGINE_DIRECT_DRAW,
			TOOLTIP_STEAM_ENGINE_SUPERHEATED_HEADER,
			TOOLTIP_STEAM_ENGINE_SUPERHEATED_EFFECTS;

	public static final String
			GOGGLE_STEAM_BURNER,
			GOGGLE_STEAM_BURNER_HEAT,
			GOGGLE_STEAM_BURNER_TEMPERATURE,
			GOGGLE_STEAM_BURNER_FUEL_TIME,
			GOGGLE_STEAM_BURNER_EFFICIENCY;

	public static final String
			GOGGLE_SOLAR_BOILER,
			GOGGLE_SOLAR_BOILER_WORKING,
			GOGGLE_SOLAR_BOILER_IDLE,
			GOGGLE_SOLAR_BOILER_LIGHT_SUNLIGHT,
			GOGGLE_SOLAR_BOILER_LIGHT_RAIN,
			GOGGLE_SOLAR_BOILER_LIGHT_THUNDER,
			GOGGLE_SOLAR_BOILER_LIGHT_ARTIFICIAL,
			GOGGLE_SOLAR_BOILER_LIGHT_NONE,
			GOGGLE_SOLAR_BOILER_PRODUCTION,
			GOGGLE_SOLAR_BOILER_RATE;

	public static final String
			GOGGLE_STEAM_ENGINE,
			GOGGLE_STEAM_ENGINE_STATUS_IDLE,
			GOGGLE_STEAM_ENGINE_STATUS_HEATUP,
			GOGGLE_STEAM_ENGINE_STATUS_RUNNING,
			GOGGLE_STEAM_ENGINE_HEATUP,
			GOGGLE_STEAM_ENGINE_STRESS,
			GOGGLE_STEAM_ENGINE_STEAM;

	public static final String
			GOGGLE_BOILER_STEAM_OUTPUT_RATE,
			GOGGLE_BOILER_WATER_STORED,
			GOGGLE_STEAM_BOILER_STEAM,
			GOGGLE_STEAM_BOILER_HEAT,
			GOGGLE_STEAM_BOILER_EFFICIENCY,
			GOGGLE_STEAM_BOILER_SUPERHEATED,
			GOGGLE_STEAM_BOILER_SUPERHEATED_YES,
			GOGGLE_STEAM_BOILER_SUPERHEATED_NO,
			GOGGLE_STEAM_BOILER_NO_BURNER;

	public static final String
			GOGGLE_STEAM_MACHINE_STEAM,
			GOGGLE_STEAM_MACHINE_OPERATIONS,
			GOGGLE_STEAM_MACHINE_PARALLEL;

	public static final String
			GOGGLE_BRONZE_DEPOT_CONTENTS;

	public static final String
			JEI_SOLID_FUEL,
			JEI_FLUID_FUEL,
			JEI_HEATED,
			JEI_SUPERHEATED;

	public static final String
			TOOLTIP_STEAM_CONTAINER_EMPTY,
			TOOLTIP_STEAM_CONTAINER_FLUID,
			TOOLTIP_STEAM_CONTAINER_ONLY_STEAM,
			TOOLTIP_STEAM_CONTAINER_EXTRACT,
			TOOLTIP_STEAM_CONTAINER_INSERT,
			TOOLTIP_STEAM_CONTAINER_EXTRACT_MORE;

	public static final String
			TOOLTIP_STEAM_PUMP_SUMMARY,
			TOOLTIP_STEAM_PUMP_BEHAVIOUR1,
			TOOLTIP_STEAM_PUMP_BEHAVIOUR2,
			TOOLTIP_STEAM_PUMP_BEHAVIOUR3;

	public static final String
			TOOLTIP_STEAM_PIPE_SUMMARY,
			TOOLTIP_STEAM_PIPE_BEHAVIOUR;

	public static final String
			TOOLTIP_BRONZE_BASIN_SUMMARY,
			TOOLTIP_BRONZE_BASIN_BEHAVIOUR1,
			TOOLTIP_BRONZE_BASIN_BEHAVIOUR2;

	static {
		BASIC_TAB = setCreativeTabKey("basic");

		THERMAL_PACK_TITLE = "resourcepack.steam_letters_thermal.title";
		THERMAL_PACK_DESCRIPTION = "resourcepack.steam_letters_thermal.description";

		WIP_HIDE_WARNING = "wip.hide.warning";

		TOOLTIP_COMMON_PARALLEL = "tooltip.common.parallel";
		TOOLTIP_COMMON_CAPACITY = "tooltip.common.capacity";
		TOOLTIP_COMMON_CONSUMPTION = "tooltip.common.consumption";
		TOOLTIP_COMMON_ROTATION = "tooltip.common.rotation";
		TOOLTIP_COMMON_STRESS_IMPACT = "tooltip.common.stress_impact";
		TOOLTIP_COMMON_SUPERHEATED_CONSUMPTION = "tooltip.common.superheated_consumption";
		TOOLTIP_COMMON_CONDITION = "tooltip.common.condition";
		TOOLTIP_COMMON_PRODUCTION = "tooltip.common.production";
		TOOLTIP_COMMON_WATER_STEAM_CAPACITY = "tooltip.common.water_steam_capacity";

		TOOLTIP_STEAM_HAMMER_SUMMARY = "tooltip.steam_hammer.summary";
		TOOLTIP_STEAM_MIXER_SUMMARY = "tooltip.steam_mixer.summary";
		TOOLTIP_STEAM_SAW_SUMMARY = "tooltip.steam_saw.summary";
		TOOLTIP_STEAM_SAW_PROCESSING_TIME = "tooltip.steam_saw.processing_time";
		TOOLTIP_STEAM_SAW_CONSUMPTION = "tooltip.steam_saw.consumption";

		TOOLTIP_ADVANCED_SPOUT_SUMMARY = "tooltip.advanced_spout.summary";
		TOOLTIP_ADVANCED_SPOUT_PARALLEL = "tooltip.advanced_spout.parallel";
		TOOLTIP_ADVANCED_SPOUT_CAPACITY = "tooltip.advanced_spout.capacity";
		TOOLTIP_ADVANCED_SPOUT_NO_STEAM = "tooltip.advanced_spout.no_steam";

		TOOLTIP_BRONZE_BOILER_SUMMARY = "tooltip.bronze_boiler.summary";
		TOOLTIP_BRONZE_BOILER_NEEDS_BURNER = "tooltip.bronze_boiler.needs_burner";
		TOOLTIP_BRONZE_BOILER_SUPERHEATED = "tooltip.bronze_boiler.superheated";

		TOOLTIP_SOLAR_BOILER_SUMMARY = "tooltip.solar_boiler.summary";
		TOOLTIP_SOLAR_BOILER_LIGHT = "tooltip.solar_boiler.light";
		TOOLTIP_SOLAR_BOILER_WEATHER = "tooltip.solar_boiler.weather";

		TOOLTIP_SOLID_BURNER_SUMMARY = "tooltip.solid_burner.summary";
		TOOLTIP_FLUID_BURNER_SUMMARY = "tooltip.fluid_burner.summary";
		TOOLTIP_BURNER_MAX_HEAT = "tooltip.burner.max_heat";
		TOOLTIP_BURNER_CYCLE = "tooltip.burner.cycle";
		TOOLTIP_BURNER_SUPERHEATED = "tooltip.burner.superheated";
		TOOLTIP_FLUID_BURNER_TANK = "tooltip.fluid_burner.tank";
		TOOLTIP_FLUID_BURNER_FUEL_CYCLE = "tooltip.fluid_burner.fuel_cycle";

		TOOLTIP_STEAM_ENGINE_SUMMARY = "tooltip.steam_engine.summary";
		TOOLTIP_STEAM_ENGINE_OUTPUT = "tooltip.steam_engine.output";
		TOOLTIP_STEAM_ENGINE_CONSUMPTION = "tooltip.steam_engine.consumption";
		TOOLTIP_STEAM_ENGINE_CAPACITY = "tooltip.steam_engine.capacity";
		TOOLTIP_STEAM_ENGINE_HEATUP = "tooltip.steam_engine.heatup";
		TOOLTIP_STEAM_ENGINE_DIRECT_DRAW = "tooltip.steam_engine.direct_draw";
		TOOLTIP_STEAM_ENGINE_SUPERHEATED_HEADER = "tooltip.steam_engine.superheated_header";
		TOOLTIP_STEAM_ENGINE_SUPERHEATED_EFFECTS = "tooltip.steam_engine.superheated_effects";

		GOGGLE_STEAM_BURNER = "gui.goggles.steam_burner";
		GOGGLE_STEAM_BURNER_HEAT = "gui.goggles.steam_burner.heat";
		GOGGLE_STEAM_BURNER_TEMPERATURE = "gui.goggles.steam_burner.temperature";
		GOGGLE_STEAM_BURNER_FUEL_TIME = "gui.goggles.steam_burner.fuel_time";
		GOGGLE_STEAM_BURNER_EFFICIENCY = "gui.goggles.steam_burner.efficiency";

		GOGGLE_SOLAR_BOILER = "gui.goggles.solar_boiler";
		GOGGLE_SOLAR_BOILER_WORKING = "gui.goggles.solar_boiler.working";
		GOGGLE_SOLAR_BOILER_IDLE = "gui.goggles.solar_boiler.idle";
		GOGGLE_SOLAR_BOILER_LIGHT_SUNLIGHT = "gui.goggles.solar_boiler.light.sunlight";
		GOGGLE_SOLAR_BOILER_LIGHT_RAIN = "gui.goggles.solar_boiler.light.rain";
		GOGGLE_SOLAR_BOILER_LIGHT_THUNDER = "gui.goggles.solar_boiler.light.thunder";
		GOGGLE_SOLAR_BOILER_LIGHT_ARTIFICIAL = "gui.goggles.solar_boiler.light.artificial";
		GOGGLE_SOLAR_BOILER_LIGHT_NONE = "gui.goggles.solar_boiler.light.none";
		GOGGLE_SOLAR_BOILER_PRODUCTION = "gui.goggles.solar_boiler.production";
		GOGGLE_SOLAR_BOILER_RATE = "gui.goggles.solar_boiler.rate";

		GOGGLE_STEAM_ENGINE = "gui.goggles.steam_engine";
		GOGGLE_STEAM_ENGINE_STATUS_IDLE = "gui.goggles.steam_engine.status.idle";
		GOGGLE_STEAM_ENGINE_STATUS_HEATUP = "gui.goggles.steam_engine.status.heatup";
		GOGGLE_STEAM_ENGINE_STATUS_RUNNING = "gui.goggles.steam_engine.status.running";
		GOGGLE_STEAM_ENGINE_HEATUP = "gui.goggles.steam_engine.heatup";
		GOGGLE_STEAM_ENGINE_STRESS = "gui.goggles.steam_engine.stress";
		GOGGLE_STEAM_ENGINE_STEAM = "gui.goggles.steam_engine.steam";

		GOGGLE_BOILER_STEAM_OUTPUT_RATE = "gui.goggles.boiler.steam_output_rate";
		GOGGLE_BOILER_WATER_STORED = "gui.goggles.boiler.water_stored";

		GOGGLE_STEAM_BOILER_STEAM = "gui.goggles.steam_boiler.steam";
		GOGGLE_STEAM_BOILER_HEAT = "gui.goggles.steam_boiler.heat";
		GOGGLE_STEAM_BOILER_EFFICIENCY = "gui.goggles.steam_boiler.efficiency";
		GOGGLE_STEAM_BOILER_SUPERHEATED = "gui.goggles.steam_boiler.superheated";
		GOGGLE_STEAM_BOILER_SUPERHEATED_YES = "gui.goggles.steam_boiler.superheated.yes";
		GOGGLE_STEAM_BOILER_SUPERHEATED_NO = "gui.goggles.steam_boiler.superheated.no";
		GOGGLE_STEAM_BOILER_NO_BURNER = "gui.goggles.steam_boiler.no_burner";

		GOGGLE_STEAM_MACHINE_STEAM = "gui.goggles.steam_machine.steam";
		GOGGLE_STEAM_MACHINE_OPERATIONS = "gui.goggles.steam_machine.operations";
		GOGGLE_STEAM_MACHINE_PARALLEL = "gui.goggles.steam_machine.parallel";

		GOGGLE_BRONZE_DEPOT_CONTENTS = "gui.goggles.bronze_depot.contents";

		JEI_SOLID_FUEL = "jei.solid_fuel";
		JEI_FLUID_FUEL = "jei.fluid_fuel";
		JEI_HEATED = "jei.heated";
		JEI_SUPERHEATED = "jei.superheated";

		TOOLTIP_STEAM_CONTAINER_EMPTY = "tooltip.steam_container.empty";
		TOOLTIP_STEAM_CONTAINER_FLUID = "tooltip.steam_container.fluid";
		TOOLTIP_STEAM_CONTAINER_ONLY_STEAM = "tooltip.steam_container.only_steam";
		TOOLTIP_STEAM_CONTAINER_EXTRACT = "tooltip.steam_container.extract";
		TOOLTIP_STEAM_CONTAINER_INSERT = "tooltip.steam_container.insert";
		TOOLTIP_STEAM_CONTAINER_EXTRACT_MORE = "tooltip.steam_container.extract_more";

		TOOLTIP_STEAM_PUMP_SUMMARY = "tooltip.steam_pump.summary";
		TOOLTIP_STEAM_PUMP_BEHAVIOUR1 = "tooltip.steam_pump.behaviour1";
		TOOLTIP_STEAM_PUMP_BEHAVIOUR2 = "tooltip.steam_pump.behaviour2";
		TOOLTIP_STEAM_PUMP_BEHAVIOUR3 = "tooltip.steam_pump.behaviour3";
		TOOLTIP_STEAM_PIPE_SUMMARY = "tooltip.steam_pipe.summary";
		TOOLTIP_STEAM_PIPE_BEHAVIOUR = "tooltip.steam_pipe.behaviour";
		TOOLTIP_BRONZE_BASIN_SUMMARY = "tooltip.bronze_basin.summary";
		TOOLTIP_BRONZE_BASIN_BEHAVIOUR1 = "tooltip.bronze_basin.behaviour1";
		TOOLTIP_BRONZE_BASIN_BEHAVIOUR2 = "tooltip.bronze_basin.behaviour2";
	}

	/**
	 * 返回带模组 id 前缀的完整语言键.
	 */
	public static String langKey(String key) {
		return String.format("%s.%s", CreateLettersSteam.MODID, key);
	}

	private static String setCreativeTabKey(String key) {
		return String.format("itemGroup.%s.%s", CreateLettersSteam.MODID, key);
	}
}
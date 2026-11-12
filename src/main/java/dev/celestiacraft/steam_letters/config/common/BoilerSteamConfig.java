package dev.celestiacraft.steam_letters.config.common;

import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Create 大锅炉(FluidTank 多方块 + BoilerData)自产汽的配置.
 * <p>
 * 只作用于大锅炉, 与本模组自己的蒸汽锅炉无关.
 * 各项的具体含义见仓库内 docs/大锅炉产汽算法.md.
 */
public class BoilerSteamConfig extends ConfigModule {
	public static ModConfigSpec.IntValue
			WATER_PER_BLOCK;

	public static ModConfigSpec.DoubleValue
			BASE_CONSUMPTION,
			STEAM_PER_WATER,
			BUFFER_MULTIPLIER,
			SUPERHEATED_AT_LEVEL,
			SUPERHEATED_WATER_MULTIPLIER;

	public static ModConfigSpec.BooleanValue
			STRICT_QUOTA;

	public BoilerSteamConfig(ModConfigSpec.Builder builder) {
		super(builder, "boiler_steam");
	}

	@Override
	protected void addConfigs() {
		BASE_CONSUMPTION = builder.translation(key("base_consumption"))
				.comment("Steam produced per unit of continuous boiler level, in mB per tick.",
					"One level is meant to feed exactly one fully loaded steam engine,",
					"so this should stay equal to the engine's own consumption.")
				.defineInRange("base_consumption", 12.0D, 0.0D, 1000.0D);

		STEAM_PER_WATER = builder.translation(key("steam_per_water"))
				.comment("Steam produced per mB of water drawn out of the boiler's water buffer.",
					"Create consumes the water that flows through it without storing it, so the",
					"buffer keeps the water and this ratio is the only link between that water",
					"and the steam output.",
					"1.2 keeps the water requirement of a given level exactly equal to the",
					"10 mB per tick the level itself demands.")
				.defineInRange("steam_per_water", 1.2D, 0.0D, 100.0D);

		WATER_PER_BLOCK = builder.translation(key("water_per_block"))
			.comment("Water the boiler holds per block of its structure, in mB.",
				"Water is no longer consumed the moment it flows in, so a full steam buffer",
				"does not force the boiler to swallow water for nothing.",
				"0 disables the water buffer, and the boiler falls back to counting only",
				"the water that flows through it.")
			.defineInRange("water_per_block", 1000, 0, 100000);

		BUFFER_MULTIPLIER = builder.translation(key("buffer_multiplier"))
				.comment("Steam buffer capacity, as a multiplier of 1000 mB per boiler block.",
					"0 makes the boiler unable to store any steam.")
				.defineInRange("buffer_multiplier", 1.0D, 0.0D, 100.0D);

		STRICT_QUOTA = builder.translation(key("strict_quota"))
				.comment("true: steam engines and bronze pumps share one quota, so a boiler of",
					"level L feeds min(1, L / (engines + pumps)) to every attached engine. Adding",
					"a pump to a full quota therefore lowers the stress of every engine.",
					"Also covers Create's own steam engines, which never consume fluid and",
					"derive their stress from this efficiency alone.",
					"false: the boiler only limits the total steam flow and leaves the quota to scarcity.")
				.define("strict_quota", true);

		SUPERHEATED_AT_LEVEL = builder.translation(key("superheated_at_level"))
				.comment("Continuous level required before the boiler produces superheated steam.",
					"18 needs 18 heat, at least 72 blocks of boiler and 180 mB/t of water.")
				.defineInRange("superheated_at_level", 18.0D, 0.0D, 18.0D);

		SUPERHEATED_WATER_MULTIPLIER = builder.translation(key("superheated_water_multiplier"))
				.comment("Water cost multiplier of superheated steam.",
					"Matches the engine's superheated output multiplier, so switching to",
					"superheated steam does not create free capacity.")
				.defineInRange("superheated_water_multiplier", 1.5D, 1.0D, 100.0D);
	}
}
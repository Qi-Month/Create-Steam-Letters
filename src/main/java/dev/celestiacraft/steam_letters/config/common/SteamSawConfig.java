package dev.celestiacraft.steam_letters.config.common;

import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.neoforged.neoforge.common.ModConfigSpec;

public class SteamSawConfig extends ConfigModule {
	public static ModConfigSpec.IntValue
			STEAM_SAW_PARALLEL,
			STEAM_CONSUMPTION,
			STEAM_CAPACITY;

	public static ModConfigSpec.DoubleValue
			PROCESSING_TIME_MULTIPLIER;

	public SteamSawConfig(ModConfigSpec.Builder builder) {
		super(builder, "steam_saw");
	}

	@Override
	protected void addConfigs() {
		STEAM_SAW_PARALLEL = builder.translation(key("steam_saw_parallel"))
				.comment("Maximum number of recipes a single steam saw "
						+ "processes at once.")
				.defineInRange("steam_saw_parallel", 16, 1, 64);

		STEAM_CONSUMPTION = builder.translation(key("steam_consumption"))
				.comment("Steam consumed per finished recipe (mB).")
				.defineInRange("steam_consumption", 500, 0, 1000000);

		STEAM_CAPACITY = builder.translation(key("steam_capacity"))
				.comment("Steam buffer capacity of a steam saw (mB).")
				.defineInRange("steam_capacity", 10000, 1000, 1000000);

		PROCESSING_TIME_MULTIPLIER = builder.translation(key("processing_time_multiplier"))
				.comment("Working time multiplier of the steam saw "
						+ "(0.25 = 4 times faster than a mechanical saw).")
				.defineInRange("processing_time_multiplier", 0.25D, 0.01D, 100.0D);
	}
}
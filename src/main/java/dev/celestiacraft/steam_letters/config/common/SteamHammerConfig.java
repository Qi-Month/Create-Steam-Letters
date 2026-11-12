package dev.celestiacraft.steam_letters.config.common;

import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.neoforged.neoforge.common.ModConfigSpec;

public class SteamHammerConfig extends ConfigModule {
	public static ModConfigSpec.IntValue
			STEAM_HAMMER_PARALLEL,
			STEAM_CONSUMPTION,
			STEAM_CAPACITY;

	public SteamHammerConfig(ModConfigSpec.Builder builder) {
		super(builder, "steam_hammer");
	}

	@Override
	protected void addConfigs() {
		STEAM_HAMMER_PARALLEL = builder.translation(key("steam_hammer_parallel"))
				.comment("Maximum number of recipes a single steam hammer "
						+ "processes at once.")
				.defineInRange("steam_hammer_parallel", 64, 1, 64);

		STEAM_CONSUMPTION = builder.translation(key("steam_consumption"))
				.comment("Steam consumed per working cycle (mB).")
				.defineInRange("steam_consumption", 1000, 0, 1000000);

		STEAM_CAPACITY = builder.translation(key("steam_capacity"))
				.comment("Steam buffer capacity of a steam hammer (mB).")
				.defineInRange("steam_capacity", 10000, 1000, 1000000);
	}
}
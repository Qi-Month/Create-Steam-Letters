package dev.celestiacraft.steam_letters.config;

import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import dev.celestiacraft.steam_letters.config.common.*;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfigs {
	private static final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
	public static final ModConfigSpec SPEC;

	public static final SteamConfig STEAM = new SteamConfig(builder);
	public static final BronzeBasinConfig BRONZE_BASIN = new BronzeBasinConfig(builder);
	public static final AdvancedSpoutConfig ADVANCED_SPOUT = new AdvancedSpoutConfig(builder);
	public static final SteamHammerConfig STEAM_HAMMER = new SteamHammerConfig(builder);
	public static final SteamMixerConfig STEAM_MIXER = new SteamMixerConfig(builder);
	public static final SteamSawConfig STEAM_SAW = new SteamSawConfig(builder);
	public static final SteamBoilerConfig STEAM_BOILER = new SteamBoilerConfig(builder);
	public static final SolarBoilerConfig SOLAR_BOILER = new SolarBoilerConfig(builder);
	public static final SteamBurnerConfig STEAM_BURNER = new SteamBurnerConfig(builder);
	public static final SteamEngineConfig STEAM_ENGINE = new SteamEngineConfig(builder);
	public static final BoilerSteamConfig BOILER_STEAM = new BoilerSteamConfig(builder);

	static {
		builder.comment("All settings below only take effect after restarting "
						+ "the server or client.")
				.translation(ConfigModule.translationKey("general"))
				.push("general");

		SPEC = builder.build();
		builder.pop();
	}
}
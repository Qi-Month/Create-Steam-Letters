package dev.celestiacraft.steam_letters.config.common;

import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 飞轮引擎配置.
 * <p>
 * 直抽效率对所有引擎统一生效; 其余项为过热蒸汽与预热过程的机制倍率.
 * 预热时长按秒配置, 与引擎的最高蒸汽消耗量无关.
 */
public class SteamEngineConfig extends ConfigModule {
	public static ModConfigSpec.DoubleValue
			SUCK_EFFICIENCY,
			SUPERHEATED_CONSUMPTION_MULTIPLIER,
			HEATUP_SECONDS,
			SUPERHEATED_ACCELERATION_MULTIPLIER,
			SUPERHEATED_OUTPUT_MULTIPLIER;

	public SteamEngineConfig(ModConfigSpec.Builder builder) {
		super(builder, "steam_engine");
	}

	@Override
	protected void addConfigs() {
		SUCK_EFFICIENCY = builder.translation(key("suck_efficiency"))
				.comment("Efficiency (0~1) of steam drawn directly from an adjacent boiler.",
					"4n mB is drawn out of the boiler per tick while only 3n mB reaches "
							+ "the engine, so a directly fed engine caps out at 75% of its "
							+ "stress output.")
				.defineInRange("suck_efficiency", 0.75D, 0.0D, 1.0D);

		SUPERHEATED_CONSUMPTION_MULTIPLIER = builder.translation(key("superheated_consumption_multiplier"))
				.comment("Steam consumption multiplier applied while the flywheel engine runs "
					+ "on superheated steam. 0.5 means it only needs half the steam of a "
					+ "normal-steam run.")
				.defineInRange("superheated_consumption_multiplier", 0.5D, 0.0D, 1.0D);

		HEATUP_SECONDS = builder.translation(key("heatup_seconds"))
				.comment("Seconds required for the flywheel engine to heat up before it outputs "
						+ "its full stress.",
						"The demand climbs through 25%, 50% and 75% of the full load during the "
								+ "heat-up.",
						"0 means no heat-up at all.")
				.defineInRange("heatup_seconds", 2.0D, 0.0D, 10000.0D);

		SUPERHEATED_ACCELERATION_MULTIPLIER = builder.translation(key("superheated_acceleration_multiplier"))
				.comment("Heat-up acceleration multiplier applied while running on "
					+ "superheated steam.")
				.defineInRange("superheated_acceleration_multiplier", 2.0D, 1.0D, 100.0D);

		SUPERHEATED_OUTPUT_MULTIPLIER = builder.translation(key("superheated_output_multiplier"))
				.comment("Stress output multiplier applied while running on superheated steam.")
				.defineInRange("superheated_output_multiplier", 1.5D, 1.0D, 100.0D);
	}
}
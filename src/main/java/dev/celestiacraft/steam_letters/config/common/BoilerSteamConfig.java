package dev.celestiacraft.steam_letters.config.common;

import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 原版大锅炉(FluidTank 多方块 + BoilerData)的蒸汽驱动配置.
 * <p>
 * 只作用于大锅炉, 与本模组自己的多方块锅炉无关. 大锅炉不产汽, 这里的缓冲只用来存外部注入的蒸汽.
 */
public class BoilerSteamConfig extends ConfigModule {
	public static ModConfigSpec.DoubleValue
			BUFFER_MULTIPLIER;

	public BoilerSteamConfig(ModConfigSpec.Builder builder) {
		super(builder, "boiler_steam");
	}

	@Override
	protected void addConfigs() {
		BUFFER_MULTIPLIER = builder.translation(key("buffer_multiplier"))
				.comment("Steam buffer capacity, as a multiplier of 1000 mB per boiler block.",
					"0 makes the boiler unable to accept any steam.")
				.defineInRange("buffer_multiplier", 1.0D, 0.0D, 100.0D);
	}
}
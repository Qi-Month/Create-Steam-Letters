package dev.celestiacraft.steam_letters.config.common;

import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.neoforged.neoforge.common.ModConfigSpec;

public class AdvancedSpoutConfig extends ConfigModule {
	public static ModConfigSpec.IntValue CAPACITY;
	public static ModConfigSpec.IntValue PARALLEL;

	public AdvancedSpoutConfig(ModConfigSpec.Builder builder) {
		super(builder, "advanced_spout");
	}

	@Override
	protected void addConfigs() {
		CAPACITY = builder.translation(key("capacity"))
				.comment("Fluid capacity of an advanced spout (mB).")
				.defineInRange("capacity", 5000, 2000, 1000000);

		PARALLEL = builder.translation(key("parallel"))
				.comment("Default parallel of newly placed advanced spouts.",
					"Can still be changed per block with a wrench.")
				.defineInRange("parallel", 8, 1, 20);
	}
}
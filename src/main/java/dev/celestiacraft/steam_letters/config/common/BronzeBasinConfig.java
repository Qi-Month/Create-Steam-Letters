package dev.celestiacraft.steam_letters.config.common;

import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.fluids.FluidType;

public class BronzeBasinConfig extends ConfigModule {
	public static ModConfigSpec.IntValue SLOT_CAPACITY;

	public BronzeBasinConfig(ModConfigSpec.Builder builder) {
		super(builder, "bronze_basin");
	}

	@Override
	protected void addConfigs() {
		SLOT_CAPACITY = builder.translation(key("slot_capacity"))
				.comment("Fluid capacity of each tank of a bronze basin (mB).",
					"Basins have several tanks, so the total capacity is a multiple of this.")
				.defineInRange("slot_capacity", 16 * FluidType.BUCKET_VOLUME, FluidType.BUCKET_VOLUME, 1000000);
	}
}
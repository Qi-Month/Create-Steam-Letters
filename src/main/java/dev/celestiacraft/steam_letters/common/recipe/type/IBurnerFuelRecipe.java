package dev.celestiacraft.steam_letters.common.recipe.type;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.List;

public interface IBurnerFuelRecipe {
	String SUPERHEATED_KEY = "superheated";

	int getTime();

	default boolean isSuperheated() {
		return false;
	}

	default List<ItemStack> getFuelItems() {
		return List.of();
	}

	default List<FluidStack> getFuelFluids() {
		return List.of();
	}

	/**
	 * 固定 1000
	 *
	 * @return
	 */
	default int getSteamAmount() {
		return FluidType.BUCKET_VOLUME;
	}
}
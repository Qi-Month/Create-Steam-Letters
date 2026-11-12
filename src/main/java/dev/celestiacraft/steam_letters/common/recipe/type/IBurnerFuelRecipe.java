package dev.celestiacraft.steam_letters.common.recipe.type;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

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
}
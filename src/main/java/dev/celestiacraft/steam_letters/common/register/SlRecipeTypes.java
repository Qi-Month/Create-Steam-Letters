package dev.celestiacraft.steam_letters.common.register;

import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.common.recipe.SimpleRecipeTypeInfo;
import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import dev.celestiacraft.steam_letters.common.recipe.type.SolidFuelRecipe;

public class SlRecipeTypes {
	public static final SimpleRecipeTypeInfo<SolidFuelRecipe> SOLID_FUEL;
	public static final SimpleRecipeTypeInfo<FluidFuelRecipe> FLUID_FUEL;

	static {
		SOLID_FUEL = SlApis.REGISTRATE.fuelRecipe("solid_fuel", SolidFuelRecipe::new)
				.register();

		FLUID_FUEL = SlApis.REGISTRATE.fuelRecipe("fluid_fuel", FluidFuelRecipe::new)
				.register();
	}

	public static void init() {
	}
}
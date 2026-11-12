package dev.celestiacraft.steam_letters.compat.jei.api;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import dev.celestiacraft.steam_letters.common.recipe.type.SolidFuelRecipe;
import mezz.jei.api.recipe.RecipeType;

public class JeiRecipeType {
	public static final RecipeType<FluidFuelRecipe> FLUID_FUEL;
	public static final RecipeType<SolidFuelRecipe> SOLID_FUEL;

	static {
		FLUID_FUEL = addType("fluid_fuel", FluidFuelRecipe.class);
		SOLID_FUEL = addType("solid_fuel", SolidFuelRecipe.class);
	}

	private static <T> RecipeType<T> addType(String path, Class<? extends T> recipeClass) {
		return RecipeType.create(CreateLettersSteam.MODID, path, recipeClass);
	}
}
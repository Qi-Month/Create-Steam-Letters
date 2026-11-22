package dev.celestiacraft.steam_letters.compat.kubejs.recipe.schema.schema;

import dev.celestiacraft.steam_letters.common.recipe.type.IBurnerFuelRecipe;
import dev.celestiacraft.steam_letters.compat.kubejs.recipe.schema.BurnerRecipeJS;
import dev.celestiacraft.steam_letters.compat.kubejs.recipe.schema.component.FuelFluidComponent;
import dev.celestiacraft.steam_letters.compat.kubejs.recipe.schema.component.FuelItemComponent;
import dev.celestiacraft.steam_letters.compat.kubejs.recipe.schema.component.FuelSuperheatedComponent;
import dev.celestiacraft.steam_letters.compat.kubejs.recipe.schema.component.FuelTimeComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public class BurnerSchema {
	public static final RecipeKey<Integer> TIME = new RecipeKey<>(
			new FuelTimeComponent(),
			"time"
	).optional(0);

	public static final RecipeKey<Boolean> SUPERHEATED = new RecipeKey<>(
			new FuelSuperheatedComponent(),
			IBurnerFuelRecipe.SUPERHEATED_KEY
	).optional(false);

	public static final RecipeSchema SOLID_FUEL = new RecipeSchema(
			BurnerRecipeJS.class,
			BurnerRecipeJS::new,
			new RecipeKey<>(new FuelItemComponent(), "fuel"),
			TIME,
			SUPERHEATED
	);

	public static final RecipeSchema FLUID_FUEL = new RecipeSchema(
			BurnerRecipeJS.class,
			BurnerRecipeJS::new,
			new RecipeKey<>(new FuelFluidComponent(), "fuel"),
			TIME,
			SUPERHEATED
	);
}
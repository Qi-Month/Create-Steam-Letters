package dev.celestiacraft.steam_letters.compat.kubejs.recipe;

import dev.celestiacraft.steam_letters.compat.kubejs.recipe.schema.BurnerSchema;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;

public class BurnerRecipeJS extends KubeRecipe {
	public BurnerRecipeJS superheated() {
		return superheated(Boolean.TRUE);
	}

	public BurnerRecipeJS superheated(Boolean superheated) {
		return setValue(BurnerSchema.SUPERHEATED, Boolean.TRUE.equals(superheated));
	}

	@Override
	public <T> BurnerRecipeJS setValue(RecipeKey<T> key, T value) {
		super.setValue(key, value);
		return this;
	}
}
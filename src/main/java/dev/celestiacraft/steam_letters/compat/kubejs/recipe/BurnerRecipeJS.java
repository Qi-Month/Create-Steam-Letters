package dev.celestiacraft.steam_letters.compat.kubejs.recipe;

import dev.celestiacraft.steam_letters.compat.kubejs.recipe.schema.BurnerSchema;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;

public class BurnerRecipeJS extends KubeRecipe {
	public KubeRecipe superheated() {
		return superheated(Boolean.TRUE);
	}

	public KubeRecipe superheated(Boolean superheated) {
		return setValue(BurnerSchema.SUPERHEATED, Boolean.TRUE.equals(superheated));
	}
}
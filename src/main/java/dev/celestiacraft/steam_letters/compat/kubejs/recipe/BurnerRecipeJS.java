package dev.celestiacraft.steam_letters.compat.kubejs.recipe;

import dev.celestiacraft.steam_letters.compat.kubejs.recipe.schema.BurnerSchema;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.typings.Info;

public class BurnerRecipeJS extends RecipeJS {
	@Info("Set burn duration in ticks.")
	public BurnerRecipeJS time(int time) {
		return setValue(BurnerSchema.TIME, time);
	}

	@Info("Set burn duration in seconds.")
	public BurnerRecipeJS timeSeconds(int seconds) {
		return time(20 * seconds);
	}

	@Info("Set burn duration by item count, with each item equivalent to 200 ticks.")
	public BurnerRecipeJS timeItem(int item) {
		return time(200 * item);
	}

	public BurnerRecipeJS superheated() {
		return superheated(true);
	}

	public BurnerRecipeJS superheated(boolean superheated) {
		return setValue(BurnerSchema.SUPERHEATED, superheated);
	}

	@Override
	public <T> BurnerRecipeJS setValue(RecipeKey<T> key, T value) {
		super.setValue(key, value);
		return this;
	}
}
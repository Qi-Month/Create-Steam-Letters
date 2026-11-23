package dev.celestiacraft.steam_letters.compat.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.celestiacraft.steam_letters.common.recipe.type.IBurnerFuelRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class FuelSuperheatedComponent implements RecipeComponent<Boolean> {
	@Override
	public @NotNull Class<?> componentClass() {
		return Boolean.class;
	}

	@Override
	public @NotNull ComponentRole role() {
		return ComponentRole.OTHER;
	}

	@Override
	public @NotNull JsonElement write(@NotNull RecipeJS recipe, @NotNull Boolean value) {
		return new JsonPrimitive(value);
	}

	@Override
	public @NotNull Boolean read(@NotNull RecipeJS recipe, @Nullable Object from) {
		if (from == null) {
			return Boolean.FALSE;
		}
		return BooleanComponent.BOOLEAN.read(recipe, from);
	}

	@Override
	public void writeToJson(@NotNull RecipeJS recipe, @NotNull RecipeComponentValue<Boolean> holder, @NotNull JsonObject json) {
		String key = IBurnerFuelRecipe.SUPERHEATED_KEY;
		if (Boolean.TRUE.equals(holder.value)) {
			json.addProperty(key, true);
		} else {
			json.remove(key);
		}
	}

	@Override
	public void readFromJson(@NotNull RecipeJS recipe, @NotNull RecipeComponentValue<Boolean> holder, @NotNull JsonObject json) {
		String key = IBurnerFuelRecipe.SUPERHEATED_KEY;
		holder.value = json.has(key) && json.get(key).getAsBoolean();
	}
}
package dev.celestiacraft.steam_letters.compat.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import org.jetbrains.annotations.NotNull;

public class FuelTimeComponent implements RecipeComponent<Integer> {
	@Override
	public @NotNull Class<?> componentClass() {
		return Integer.class;
	}

	@Override
	public @NotNull ComponentRole role() {
		return NumberComponent.INT.role();
	}

	@Override
	public @NotNull JsonElement write(@NotNull RecipeJS recipe, @NotNull Integer value) {
		return new JsonPrimitive(value);
	}

	@Override
	public @NotNull Integer read(@NotNull RecipeJS recipe, @NotNull Object from) {
		return NumberComponent.INT.read(recipe, from);
	}

	@Override
	public void writeToJson(@NotNull RecipeJS recipe, @NotNull RecipeComponentValue<Integer> holder, @NotNull JsonObject json) {
		json.addProperty("processingTime", holder.value);
	}

	@Override
	public void readFromJson(@NotNull RecipeJS recipe, @NotNull RecipeComponentValue<Integer> holder, @NotNull JsonObject json) {
		holder.value = json.has("processingTime") ? json.get("processingTime").getAsInt() : 0;
	}
}
package dev.celestiacraft.steam_letters.compat.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.celestiacraft.steam_letters.compat.kubejs.recipe.BurnerRecipeJson;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import org.jetbrains.annotations.NotNull;

public class FuelItemComponent implements RecipeComponent<InputItem> {
	@Override
	public @NotNull Class<?> componentClass() {
		return InputItem.class;
	}

	@Override
	public @NotNull ComponentRole role() {
		return ComponentRole.INPUT;
	}

	@Override
	public @NotNull JsonElement write(@NotNull RecipeJS recipe, @NotNull InputItem value) {
		return ItemComponents.INPUT.write(recipe, value);
	}

	@Override
	public @NotNull InputItem read(@NotNull RecipeJS recipe, @NotNull Object from) {
		if (from instanceof JsonElement element) {
			return ItemComponents.INPUT.read(recipe, element.getAsString());
		}
		return ItemComponents.INPUT.read(recipe, from);
	}

	@Override
	public void writeToJson(@NotNull RecipeJS recipe, @NotNull RecipeComponentValue<InputItem> holder, @NotNull JsonObject json) {
		JsonObject ingredient = new JsonObject();
		BurnerRecipeJson.splitItem(ItemComponents.INPUT.write(recipe, holder.value), ingredient);
		BurnerRecipeJson.writeIngredients(json, ingredient);
	}

	@Override
	public void readFromJson(@NotNull RecipeJS recipe, @NotNull RecipeComponentValue<InputItem> holder, @NotNull JsonObject json) {
		JsonObject ingredient = BurnerRecipeJson.firstIngredient(json);
		if (ingredient == null) {
			return;
		}
		if (ingredient.has("item")) {
			holder.value = read(recipe, ingredient.get("item"));
		} else if (ingredient.has("tag")) {
			holder.value = read(recipe, "#" + ingredient.get("tag").getAsString());
		}
	}
}
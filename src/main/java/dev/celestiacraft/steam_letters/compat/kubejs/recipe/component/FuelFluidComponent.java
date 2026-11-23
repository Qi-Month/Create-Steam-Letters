package dev.celestiacraft.steam_letters.compat.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import dev.celestiacraft.steam_letters.compat.kubejs.recipe.BurnerRecipeJson;
import dev.celestiacraft.steam_letters.compat.kubejs.recipe.FuelFluid;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import org.jetbrains.annotations.NotNull;

public class FuelFluidComponent implements RecipeComponent<FuelFluid> {
	@Override
	public @NotNull Class<?> componentClass() {
		return FuelFluid.class;
	}

	@Override
	public @NotNull ComponentRole role() {
		return ComponentRole.INPUT;
	}

	@Override
	public @NotNull JsonElement write(@NotNull RecipeJS recipe, @NotNull FuelFluid value) {
		return value.toJson();
	}

	@Override
	public @NotNull FuelFluid read(@NotNull RecipeJS recipe, @NotNull Object from) {
		return FuelFluid.of(from);
	}

	@Override
	public void writeToJson(@NotNull RecipeJS recipe, @NotNull RecipeComponentValue<FuelFluid> holder, @NotNull JsonObject json) {
		FuelFluid value = holder.value;
		JsonObject ingredient = new JsonObject();
		if (value.isTag()) {
			ingredient.addProperty("fluidTag", value.getTag().toString());
		} else {
			ingredient.addProperty("fluid", value.getFluid().getId());
		}
		ingredient.addProperty("amount", FluidFuelRecipe.FUEL_AMOUNT);
		BurnerRecipeJson.writeIngredients(json, ingredient);
	}

	@Override
	public void readFromJson(@NotNull RecipeJS recipe, @NotNull RecipeComponentValue<FuelFluid> holder, @NotNull JsonObject json) {
		JsonObject ingredient = BurnerRecipeJson.firstIngredient(json);
		if (ingredient == null) {
			return;
		}
		if (ingredient.has("fluid")) {
			holder.value = read(recipe, ingredient.get("fluid"));
		} else if (ingredient.has("fluidTag")) {
			holder.value = read(recipe, "#" + ingredient.get("fluidTag").getAsString());
		}
	}
}
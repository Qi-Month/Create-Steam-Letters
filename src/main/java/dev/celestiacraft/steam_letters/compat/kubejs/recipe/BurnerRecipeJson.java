package dev.celestiacraft.steam_letters.compat.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BurnerRecipeJson {
	public static void writeIngredients(@NotNull JsonObject json, @NotNull JsonObject ingredient) {
		JsonArray ingredients = new JsonArray();
		ingredients.add(ingredient);
		json.add("ingredients", ingredients);
		json.add("results", new JsonArray());
	}

	@Nullable
	public static JsonObject firstIngredient(@NotNull JsonObject json) {
		if (!json.has("ingredients")) {
			return null;
		}
		JsonElement element = json.get("ingredients");
		if (!element.isJsonArray() || element.getAsJsonArray().isEmpty()) {
			return null;
		}
		JsonElement first = element.getAsJsonArray().get(0);
		return first.isJsonObject() ? first.getAsJsonObject() : null;
	}

	public static void splitItem(@NotNull JsonElement written, @NotNull JsonObject json) {
		if (written.isJsonObject()) {
			JsonObject object = written.getAsJsonObject();
			if (isPrimitive(object, "item")) {
				json.add("item", object.get("item"));
				return;
			}
			if (isPrimitive(object, "tag")) {
				json.addProperty("tag", stripTagPrefix(object.get("tag").getAsString()));
				return;
			}
			if (object.has("ingredient")) {
				splitItem(object.get("ingredient"), json);
			}
			return;
		}

		if (written.isJsonArray() && !written.getAsJsonArray().isEmpty()) {
			splitItem(written.getAsJsonArray().get(0), json);
			return;
		}

		if (written.isJsonPrimitive()) {
			String value = written.getAsString();
			if (!value.isEmpty() && value.charAt(0) == '#') {
				json.addProperty("tag", value.substring(1));
			} else {
				json.addProperty("item", value);
			}
		}
	}

	private static boolean isPrimitive(@NotNull JsonObject object, @NotNull String name) {
		return object.has(name) && object.get(name).isJsonPrimitive();
	}

	private static @NotNull String stripTagPrefix(@NotNull String value) {
		return !value.isEmpty() && value.charAt(0) == '#' ? value.substring(1) : value;
	}
}
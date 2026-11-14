package dev.celestiacraft.steam_letters.compat.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 脚本里的固体燃料参数.
 * <p>
 * 键名固定为 {@code fuel}, 写进配方 JSON 时仍是 {@code ingredients} 数组,
 * 序列化与 KubeJS 的 {@link IngredientComponent} 一致.
 */
public class FuelItemComponent implements RecipeComponent<Ingredient> {
	/**
	 * 配方 JSON 里的真实字段名.
	 */
	public static final String JSON_KEY = "ingredients";

	public static final RecipeComponentType<Ingredient> TYPE = RecipeComponentType.unit(
			SlApis.loadResource("fuel_item"),
			FuelItemComponent::new
	);

	public static final FuelItemComponent INSTANCE = (FuelItemComponent) TYPE.instance();

	private static final RecipeComponent<Ingredient> INGREDIENT = IngredientComponent.INGREDIENT.instance();

	private final RecipeComponentType<?> type;

	public FuelItemComponent(RecipeComponentType<?> type) {
		this.type = type;
	}

	@Override
	public RecipeComponentType<?> type() {
		return type;
	}

	@Override
	public Codec<Ingredient> codec() {
		return Ingredient.CODEC_NONEMPTY;
	}

	@Override
	public TypeInfo typeInfo() {
		return INGREDIENT.typeInfo();
	}

	@Override
	public Ingredient wrap(RecipeScriptContext context, Object object) {
		return INGREDIENT.wrap(context, object);
	}

	@Override
	public void writeToJson(KubeRecipe recipe, RecipeComponentValue<Ingredient> value, JsonObject json) {
		json.remove(JSON_KEY);
		if (value.value == null) {
			return;
		}

		JsonArray ingredients = new JsonArray();
		ingredients.add(codec().encodeStart(recipe.type.event.ops.json(), value.value).getOrThrow());
		json.add(JSON_KEY, ingredients);
	}

	@Override
	public void readFromJson(KubeRecipe recipe, RecipeComponentValue<Ingredient> value, JsonObject json) {
		JsonElement element = FuelFluidComponent.firstIngredient(json);
		if (element == null) {
			return;
		}

		value.value = codec().parse(recipe.type.event.ops.json(), element).getOrThrow();
	}
}
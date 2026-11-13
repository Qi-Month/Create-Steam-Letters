package dev.celestiacraft.steam_letters.compat.kubejs.schema.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.codec.CreateCodecs;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.compat.kubejs.schema.FuelFluid;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * 脚本里的流体燃料参数.
 * <p>
 * 键名固定为 {@code fuel}, 但写进配方 JSON 时用的是 {@code ingredients} 数组, 因为
 * {@link dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe} 的 codec 只认 Create 的
 * processing recipe 结构. 序列化复用 {@link CreateCodecs#FLAT_SIZED_FLUID_INGREDIENT_WITH_TYPE},
 * 保证写出的元素带 {@code type} 字段.
 */
public class FuelFluidComponent implements RecipeComponent<FuelFluid> {
	/**
	 * 配方 JSON 里的真实字段名.
	 */
	public static final String JSON_KEY = "ingredients";

	public static final RecipeComponentType<FuelFluid> TYPE = RecipeComponentType.unit(
			SlApis.loadResource("fuel_fluid"),
			FuelFluidComponent::new
	);

	public static final FuelFluidComponent INSTANCE = (FuelFluidComponent) TYPE.instance();

	private final RecipeComponentType<?> type;

	public FuelFluidComponent(RecipeComponentType<?> type) {
		this.type = type;
	}

	@Override
	public RecipeComponentType<?> type() {
		return type;
	}

	@Override
	public Codec<FuelFluid> codec() {
		return FuelFluid.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(FuelFluid.class);
	}

	@Override
	public FuelFluid wrap(RecipeScriptContext context, Object object) {
		return FuelFluid.of(object);
	}

	@Override
	public void writeToJson(KubeRecipe recipe, RecipeComponentValue<FuelFluid> value, JsonObject json) {
		json.remove(JSON_KEY);
		if (value.value == null) {
			return;
		}

		JsonArray ingredients = new JsonArray();
		ingredients.add(CreateCodecs.FLAT_SIZED_FLUID_INGREDIENT_WITH_TYPE
				.encodeStart(recipe.type.event.ops.json(), value.value.toSized())
				.getOrThrow());
		json.add(JSON_KEY, ingredients);
	}

	@Override
	public void readFromJson(KubeRecipe recipe, RecipeComponentValue<FuelFluid> value, JsonObject json) {
		JsonElement element = firstIngredient(json);
		if (element == null) {
			return;
		}

		SizedFluidIngredient sized = CreateCodecs.FLAT_SIZED_FLUID_INGREDIENT_WITH_TYPE
				.parse(recipe.type.event.ops.json(), element)
				.getOrThrow();
		value.value = FuelFluid.fromIngredient(sized.ingredient());
	}

	/**
	 * 取 {@code ingredients} 数组的第一个元素, 没有则返回 null.
	 */
	static JsonElement firstIngredient(JsonObject json) {
		JsonElement ingredients = json.get(JSON_KEY);
		if (ingredients == null || !ingredients.isJsonArray()) {
			return null;
		}

		JsonArray array = ingredients.getAsJsonArray();
		return array.isEmpty() ? null : array.get(0);
	}
}

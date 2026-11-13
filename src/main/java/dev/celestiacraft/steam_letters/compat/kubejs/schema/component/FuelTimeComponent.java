package dev.celestiacraft.steam_letters.compat.kubejs.schema.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.rhino.type.TypeInfo;

/**
 * 脚本里的加工时间参数.
 * <p>
 * 键名固定为 {@code time}, 写进配方 JSON 时是 {@code processing_time},
 * 与 Create 的 processing recipe 结构保持一致.
 */
public class FuelTimeComponent implements RecipeComponent<Integer> {
	/**
	 * 配方 JSON 里的真实字段名.
	 */
	public static final String JSON_KEY = "processing_time";

	public static final RecipeComponentType<Integer> TYPE = RecipeComponentType.unit(
			SlApis.loadResource("fuel_time"),
			FuelTimeComponent::new
	);

	public static final FuelTimeComponent INSTANCE = (FuelTimeComponent) TYPE.instance();

	private final RecipeComponentType<?> type;

	public FuelTimeComponent(RecipeComponentType<?> type) {
		this.type = type;
	}

	@Override
	public RecipeComponentType<?> type() {
		return type;
	}

	@Override
	public Codec<Integer> codec() {
		return Codec.INT;
	}

	@Override
	public TypeInfo typeInfo() {
		return NumberComponent.INT.typeInfo();
	}

	@Override
	public Integer wrap(RecipeScriptContext context, Object object) {
		return NumberComponent.INT.wrap(context, object);
	}

	@Override
	public void writeToJson(KubeRecipe recipe, RecipeComponentValue<Integer> value, JsonObject json) {
		if (value.value == null) {
			return;
		}

		json.addProperty(JSON_KEY, value.value);
	}

	@Override
	public void readFromJson(KubeRecipe recipe, RecipeComponentValue<Integer> value, JsonObject json) {
		value.value = json.has(JSON_KEY) ? json.get(JSON_KEY).getAsInt() : 0;
	}
}

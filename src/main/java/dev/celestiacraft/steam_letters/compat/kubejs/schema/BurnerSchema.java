package dev.celestiacraft.steam_letters.compat.kubejs.schema;

import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.common.recipe.type.IBurnerFuelRecipe;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.SizedFluidIngredientComponent;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

/**
 * 两种燃料配方在 KubeJS 里的键结构, 键名与 {@link dev.celestiacraft.steam_letters.common.recipe.type.FuelRecipeParams}
 * 的 codec 字段一一对应, 否则写出的 json 游戏读不回来.
 */
public class BurnerSchema {
	public static final RecipeKey<Boolean> SUPERHEATED = BooleanComponent.BOOLEAN
			.key(IBurnerFuelRecipe.SUPERHEATED_KEY, ComponentRole.OTHER)
			.optional(false);

	public static final RecipeSchema SOLID_FUEL = new RecipeSchema(
			IngredientComponent.INGREDIENT.instance().asList().key("ingredients", ComponentRole.INPUT),
			NumberComponent.INT.key("processing_time", ComponentRole.OTHER),
			SUPERHEATED
	).factory(new KubeRecipeFactory(SlApis.loadResource("solid_fuel"), BurnerRecipeJS.class, BurnerRecipeJS::new));

	public static final RecipeSchema FLUID_FUEL = new RecipeSchema(
			SizedFluidIngredientComponent.FLAT.instance().asList().key("ingredients", ComponentRole.INPUT),
			NumberComponent.INT.key("processing_time", ComponentRole.OTHER),
			SUPERHEATED
	).factory(new KubeRecipeFactory(SlApis.loadResource("fluid_fuel"), BurnerRecipeJS.class, BurnerRecipeJS::new));

	public static class BurnerRecipeJS extends KubeRecipe {
		public KubeRecipe superheated() {
			return superheated(Boolean.TRUE);
		}

		public KubeRecipe superheated(Boolean superheated) {
			return setValue(SUPERHEATED, Boolean.TRUE.equals(superheated));
		}
	}
}
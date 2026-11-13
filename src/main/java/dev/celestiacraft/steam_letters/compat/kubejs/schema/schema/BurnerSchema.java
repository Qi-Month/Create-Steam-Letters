package dev.celestiacraft.steam_letters.compat.kubejs.schema.schema;

import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.common.recipe.type.IBurnerFuelRecipe;
import dev.celestiacraft.steam_letters.compat.kubejs.schema.BurnerRecipeJS;
import dev.celestiacraft.steam_letters.compat.kubejs.schema.component.FuelFluidComponent;
import dev.celestiacraft.steam_letters.compat.kubejs.schema.component.FuelItemComponent;
import dev.celestiacraft.steam_letters.compat.kubejs.schema.component.FuelTimeComponent;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

/**
 * 燃烧室燃料配方在脚本里的形态.
 * <p>
 * 脚本参数名与 1.20.1 保持一致: {@code fluid_fuel(fuel, time, superheated?)} 与
 * {@code solid_fuel(fuel, time, superheated?)}; 写进 JSON 的字段名由各组件自己决定
 * ({@code ingredients} / {@code processing_time} / {@code superheated}).
 */
public class BurnerSchema {
	public static final RecipeKey<Boolean> SUPERHEATED = BooleanComponent.BOOLEAN
			.key(IBurnerFuelRecipe.SUPERHEATED_KEY, ComponentRole.OTHER)
			.optional(false);

	public static final RecipeSchema SOLID_FUEL = new RecipeSchema(
			FuelItemComponent.INSTANCE.key("fuel", ComponentRole.INPUT),
			FuelTimeComponent.INSTANCE.key("time", ComponentRole.OTHER),
			SUPERHEATED
	).factory(new KubeRecipeFactory(SlApis.loadResource("solid_fuel"), BurnerRecipeJS.class, BurnerRecipeJS::new));

	public static final RecipeSchema FLUID_FUEL = new RecipeSchema(
			FuelFluidComponent.INSTANCE.key("fuel", ComponentRole.INPUT),
			FuelTimeComponent.INSTANCE.key("time", ComponentRole.OTHER),
			SUPERHEATED
	).factory(new KubeRecipeFactory(SlApis.loadResource("fluid_fuel"), BurnerRecipeJS.class, BurnerRecipeJS::new));
}

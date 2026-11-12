package dev.celestiacraft.steam_letters.common.recipe.builder;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import dev.celestiacraft.steam_letters.common.recipe.type.FuelRecipeParams;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

/**
 * 1.21 的 ProcessingRecipeBuilder 要求子类提供参数集与自身类型, 才能被 datagen 复用.
 */
public class FuelRecipeBuilder<R extends ProcessingRecipe<RecipeWrapper, FuelRecipeParams>>
		extends ProcessingRecipeBuilder<FuelRecipeParams, R, FuelRecipeBuilder<R>> {
	public FuelRecipeBuilder(ProcessingRecipe.Factory<FuelRecipeParams, R> factory, ResourceLocation recipeId) {
		super(factory, recipeId);
	}

	@Override
	protected FuelRecipeParams createParams() {
		return new FuelRecipeParams();
	}

	@Override
	public FuelRecipeBuilder<R> self() {
		return this;
	}
}

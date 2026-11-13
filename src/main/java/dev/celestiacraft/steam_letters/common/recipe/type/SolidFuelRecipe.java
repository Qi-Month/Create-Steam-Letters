package dev.celestiacraft.steam_letters.common.recipe.type;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import dev.celestiacraft.steam_letters.common.register.SlRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SolidFuelRecipe extends ProcessingRecipe<RecipeWrapper, FuelRecipeParams> implements IBurnerFuelRecipe {
	/**
	 * datagen 专用: 标记下一条被构造的配方是否过热.
	 */
	private static final ThreadLocal<Boolean> DATAGEN_SUPERHEATED = new ThreadLocal<>();

	public SolidFuelRecipe(FuelRecipeParams params) {
		super(SlRecipeTypes.SOLID_FUEL.getInfo(), params);
		params.setSuperheated(consumeSuperheated());
	}

	public static void markNextSuperheated(boolean superheated) {
		DATAGEN_SUPERHEATED.set(superheated);
	}

	private static boolean consumeSuperheated() {
		Boolean marked = DATAGEN_SUPERHEATED.get();
		if (marked == null) {
			return false;
		}

		DATAGEN_SUPERHEATED.remove();
		return marked;
	}

	@Override
	public boolean isSuperheated() {
		return params.isSuperheated();
	}

	public void setSuperheated(boolean superheated) {
		params.setSuperheated(superheated);
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 0;
	}

	@Override
	protected boolean canSpecifyDuration() {
		return true;
	}

	@Nullable
	public Ingredient ingredient() {
		return ingredients.isEmpty() ? null : ingredients.getFirst();
	}

	public boolean matches(ItemStack stack) {
		Ingredient ingredient = ingredient();
		if (ingredient == null || stack.isEmpty()) {
			return false;
		}

		return ingredient.test(stack);
	}

	@Override
	public List<ItemStack> getFuelItems() {
		Ingredient ingredient = ingredient();
		if (ingredient == null) {
			return List.of();
		}

		return List.of(ingredient.getItems());
	}

	@Override
	public int getTime() {
		return getProcessingDuration();
	}

	@Override
	public boolean matches(@NotNull RecipeWrapper wrapper, @NotNull Level level) {
		return false;
	}

	@Nullable
	public static SolidFuelRecipe find(Level level, ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}

		List<RecipeHolder<SolidFuelRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(SlRecipeTypes.SOLID_FUEL.get());
		SolidFuelRecipe best = null;
		for (RecipeHolder<SolidFuelRecipe> holder : recipes) {
			SolidFuelRecipe recipe = holder.value();
			if (!recipe.matches(stack)) {
				continue;
			}

			if (best == null || recipe.getTime() > best.getTime()) {
				best = recipe;
			}
		}

		return best;
	}
}
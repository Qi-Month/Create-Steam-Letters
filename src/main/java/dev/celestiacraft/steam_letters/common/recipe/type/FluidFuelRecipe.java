package dev.celestiacraft.steam_letters.common.recipe.type;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import dev.celestiacraft.steam_letters.common.register.SlRecipeTypes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class FluidFuelRecipe extends ProcessingRecipe<RecipeWrapper, FuelRecipeParams> implements IBurnerFuelRecipe {
	/**
	 * 一份燃料的固定量, 1000mB
	 */
	public static final int FUEL_AMOUNT = 1000;

	/**
	 * datagen 专用: 标记下一条被构造的配方是否过热, 用法同 {@link SolidFuelRecipe#markNextSuperheated(boolean)}.
	 */
	private static final ThreadLocal<Boolean> DATAGEN_SUPERHEATED = new ThreadLocal<>();

	public FluidFuelRecipe(FuelRecipeParams params) {
		super(SlRecipeTypes.FLUID_FUEL_INFO, params);
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
		return 0;
	}

	@Override
	protected int getMaxOutputCount() {
		return 0;
	}

	@Override
	protected int getMaxFluidInputCount() {
		return 1;
	}

	@Override
	protected boolean canSpecifyDuration() {
		return true;
	}

	@Nullable
	public SizedFluidIngredient ingredient() {
		return fluidIngredients.isEmpty() ? null : fluidIngredients.get(0);
	}

	public boolean matches(@Nullable FluidStack stack) {
		SizedFluidIngredient ingredient = ingredient();
		if (ingredient == null || stack == null || stack.isEmpty()) {
			return false;
		}

		return ingredient.test(stack);
	}

	/**
	 * 只比对流体类型, 不比对数量.
	 * 管道向机器灌入时每次传递的量远小于配方量, 按量比对会把有效燃料判成无效.
	 */
	public boolean matchesFluidType(@Nullable FluidStack stack) {
		SizedFluidIngredient ingredient = ingredient();
		if (ingredient == null || stack == null || stack.isEmpty()) {
			return false;
		}

		return ingredient.ingredient().test(stack);
	}

	@Override
	public List<FluidStack> getFuelFluids() {
		SizedFluidIngredient ingredient = ingredient();
		if (ingredient == null) {
			return List.of();
		}

		return List.of(ingredient.getFluids());
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
	public static FluidFuelRecipe find(Level level, FluidStack stack) {
		if (stack == null || stack.isEmpty()) {
			return null;
		}

		List<RecipeHolder<FluidFuelRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(SlRecipeTypes.FLUID_FUEL.get());
		FluidFuelRecipe best = null;
		for (RecipeHolder<FluidFuelRecipe> holder : recipes) {
			FluidFuelRecipe recipe = holder.value();
			if (!recipe.matchesFluidType(stack)) {
				continue;
			}

			if (best == null || recipe.getTime() > best.getTime()) {
				best = recipe;
			}
		}

		return best;
	}
}

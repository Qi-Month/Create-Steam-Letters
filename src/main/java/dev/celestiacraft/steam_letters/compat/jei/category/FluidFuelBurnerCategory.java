package dev.celestiacraft.steam_letters.compat.jei.category;

import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import dev.celestiacraft.steam_letters.tags.SlBlockTags;
import dev.celestiacraft.steam_letters.utils.TagAnimatedBlock;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidFuelBurnerCategory extends BurnerFuelCategory<FluidFuelRecipe> {
	public FluidFuelBurnerCategory(RecipeType<FluidFuelRecipe> type, Component title, IDrawable icon, IGuiHelper helper) {
		super(type, title, icon, helper);
	}

	@Override
	protected @Nullable IRecipeSlotBuilder addFuelSlot(@NotNull IRecipeLayoutBuilder builder, @NotNull FluidFuelRecipe recipe, int x, int y) {
		List<FluidStack> fluids = recipe.getFuelFluids();
		if (fluids.isEmpty()) {
			return null;
		}

		IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y);
		slot.addIngredients(NeoForgeTypes.FLUID_STACK, fluids);
		return slot;
	}

	@Override
	public Block getBurnerBlock() {
		return TagAnimatedBlock.get(SlBlockTags.STEAM_FLUID_BURNER);
	}
}
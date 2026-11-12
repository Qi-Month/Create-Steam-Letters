package dev.celestiacraft.steam_letters.compat.jei.category;

import dev.celestiacraft.steam_letters.common.recipe.type.SolidFuelRecipe;
import dev.celestiacraft.steam_letters.tags.SlBlockTags;
import dev.celestiacraft.steam_letters.utils.TagAnimatedBlock;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolidFuelBurnerCategory extends BurnerFuelCategory<SolidFuelRecipe> {
	public SolidFuelBurnerCategory(RecipeType<SolidFuelRecipe> type, Component title, IDrawable icon, IGuiHelper helper) {
		super(type, title, icon, helper);
	}

	@Override
	protected @Nullable IRecipeSlotBuilder addFuelSlot(@NotNull IRecipeLayoutBuilder builder, @NotNull SolidFuelRecipe recipe, int x, int y) {
		List<ItemStack> items = recipe.getFuelItems();
		if (items.isEmpty()) {
			return null;
		}

		IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y);
		slot.addItemStacks(items);
		return slot;
	}

	@Override
	public Block getBurnerBlock() {
		return TagAnimatedBlock.get(SlBlockTags.STEAM_SOLID_BURNER);
	}
}
package dev.celestiacraft.steam_letters.datagen.recipe;

import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.datagen.recipe.type.vanilla.SlCraftingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SlRecipeProvider extends RecipeProvider {
	protected static final String SHAPED = "crafting/shaped/";
	protected static final String SHAPELESS = "crafting/shapeless/";

	public SlRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
		super(output, future);
	}

	protected static ResourceLocation save(String path) {
		return SlApis.loadResource(path);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput output) {
		SlCraftingRecipeGen.addRecipes(output);
	}
}
package dev.celestiacraft.steam_letters.datagen.recipe.type.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.register.SlItems;
import dev.celestiacraft.steam_letters.common.register.SlMaterials;
import dev.celestiacraft.steam_letters.tags.SlFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class SlSequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {
	public SlSequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
		super(output, future, CreateLettersSteam.MODID);

		create("steam_mechanism_1", (builder) -> {
			return builder.require(SlMaterials.BRONZE_PLATE)
					.transitionTo(SlItems.INCOMPLETE_STEAM_MECHANISM)
					.loops(2)
					.addOutput(SlItems.STEAM_MECHANISM, 120)
					.addStep(DeployerApplicationRecipe::new, (recipe) -> {
						return recipe.require(AllBlocks.COGWHEEL);
					})
					.addStep(FillingRecipe::new, (recipe) -> {
						return recipe.require(SlFluidTags.STEAM, 500);
					});
		});

		create("steam_mechanism_2", (builder) -> {
			return builder.require(SlMaterials.BRONZE_PLATE)
					.transitionTo(SlItems.INCOMPLETE_STEAM_MECHANISM)
					.loops(2)
					.addOutput(SlItems.STEAM_MECHANISM, 120)
					.addStep(DeployerApplicationRecipe::new, (recipe) -> {
						return recipe.require(AllBlocks.COGWHEEL);
					})
					.addStep(FillingRecipe::new, (recipe) -> {
						return recipe.require(SlFluidTags.SUPERHEATED_STEAM, 125);
					});
		});
	}
}
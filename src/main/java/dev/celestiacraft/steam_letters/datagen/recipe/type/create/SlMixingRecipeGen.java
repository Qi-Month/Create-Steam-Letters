package dev.celestiacraft.steam_letters.datagen.recipe.type.create;

import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.common.register.SlMaterials;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class SlMixingRecipeGen extends MixingRecipeGen {
	public SlMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
		super(output, future, CreateLettersSteam.MODID);

		create("material/bronze/ingot", (builder) -> {
			SlApis.require(builder, Tags.Items.INGOTS_COPPER, 3);
			return builder.output(SlMaterials.BRONZE_INGOT, 4)
					.require(SlItemTags.TIN_INGOT)
					.requiresHeat(HeatCondition.HEATED);
		});
	}
}
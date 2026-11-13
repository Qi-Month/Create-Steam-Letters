package dev.celestiacraft.steam_letters.datagen.recipe.type.create;

import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.register.SlMaterials;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class SlPressRecipeGen extends PressingRecipeGen {
	public SlPressRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
		super(output, future, CreateLettersSteam.MODID);

		create("material/bronze/plate", (builder) -> {
			return builder.output(SlMaterials.BRONZE_PLATE)
					.require(SlItemTags.BRONZE_INGOT);
		});
	}
}
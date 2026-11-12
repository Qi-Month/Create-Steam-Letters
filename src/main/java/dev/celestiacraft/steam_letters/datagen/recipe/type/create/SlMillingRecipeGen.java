package dev.celestiacraft.steam_letters.datagen.recipe.type.create;

import com.simibubi.create.api.data.recipe.MillingRecipeGen;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.register.SlMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class SlMillingRecipeGen extends MillingRecipeGen {
	public SlMillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
		super(output, future, CreateLettersSteam.MODID);

		create("material/tin/nugget/deepslate", (builder) -> {
			return builder.output(0.25f, SlMaterials.TIN_NUGGET, 2)
					.output(0.125f, SlMaterials.TIN_NUGGET, 1)
					.require(AllPaletteStoneTypes.DEEPSLATE.materialTag);
		});
	}
}
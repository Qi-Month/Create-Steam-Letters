package dev.celestiacraft.steam_letters.datagen.recipe.type.create;

import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.register.SlMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class SlCrushingRecipeGen extends CrushingRecipeGen {
	public SlCrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
		super(output, future, CreateLettersSteam.MODID);

		create("material/tin/nugget/deepslate", (builder) -> {
			return builder.output(0.375f, SlMaterials.TIN_NUGGET, 2)
					.output(0.1875f, SlMaterials.TIN_NUGGET, 1)
					.require(AllPaletteStoneTypes.DEEPSLATE.materialTag)
					.duration(20);
		});
	}
}
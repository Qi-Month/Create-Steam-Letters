package dev.celestiacraft.steam_letters.datagen.tags;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.celestiacraft.steam_letters.tags.SlFluidTags;
import net.minecraft.world.level.material.Fluid;

public class FluidTagsGen {
	public static void addFluidTags(RegistrateTagsProvider.IntrinsicImpl<Fluid> provider) {
		provider.addTag(SlFluidTags.STEAMS)
				.addTag(SlFluidTags.STEAM)
				.addTag(SlFluidTags.SUPERHEATED_STEAM);
	}
}
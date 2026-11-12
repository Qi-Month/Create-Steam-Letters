package dev.celestiacraft.steam_letters.tags;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class SlFluidTags {
	public static final TagKey<Fluid>
			STEAM,
			SUPERHEATED_STEAM;

	static {
		STEAM = addCommon("steam");
		SUPERHEATED_STEAM = addCommon("superheated_steam");
	}

	private static TagKey<Fluid> add(String namespace, String name) {
		return FluidTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
	}

	private static TagKey<Fluid> addMod(String name) {
		return add(CreateLettersSteam.MODID, name);
	}

	private static TagKey<Fluid> addCommon(String name) {
		return add("c", name);
	}
}
package dev.celestiacraft.steam_letters.tags;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class SlFluidTags {
	public static final TagKey<Fluid>
			STEAM,
			SUPERHEATED_STEAM,
			STEAMS;

	static {
		STEAM = addForge("steam");
		SUPERHEATED_STEAM = addForge("superheated_steam");
		STEAMS = addForge("steams");
	}

	private static TagKey<Fluid> add(String namespace, String name) {
		return FluidTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
	}

	private static TagKey<Fluid> addMod(String name) {
		return add(CreateLettersSteam.MODID, name);
	}

	private static TagKey<Fluid> addForge(String name) {
		return add("forge", name);
	}
}
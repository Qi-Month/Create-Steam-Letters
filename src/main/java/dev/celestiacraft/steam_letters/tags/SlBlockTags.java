package dev.celestiacraft.steam_letters.tags;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SlBlockTags {
	public static final TagKey<Block>
			STEAM_FLYWHEEL,
			STEAM_ENGINE,
			STEAM_BOILER,
			STEAM_SOLID_BURNER,
			STEAM_FLUID_BURNER,
			STEAM_SOLAR_BOILER,
			STEAM_BURNERS,
			STEAM_PRODUCTION;

	static {
		STEAM_FLYWHEEL = addMod("steam_flywheel");
		STEAM_ENGINE = addMod("steam_engine");
		STEAM_BOILER = addMod("steam_boiler");
		STEAM_SOLID_BURNER = addMod("steam_solid_burner");
		STEAM_FLUID_BURNER = addMod("steam_fluid_burner");
		STEAM_SOLAR_BOILER = addMod("steam_solar_boiler");
		STEAM_BURNERS = addMod("steam_burners");
		STEAM_PRODUCTION = addMod("steam_production");
	}

	private static TagKey<Block> add(String namespace, String name) {
		return BlockTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
	}

	private static TagKey<Block> addMod(String name) {
		return add(CreateLettersSteam.MODID, name);
	}

	private static TagKey<Block> addCommon(String name) {
		return add("c", name);
	}
}
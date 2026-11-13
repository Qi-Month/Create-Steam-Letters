package dev.celestiacraft.steam_letters.tags;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SlItemTags {
	public static final TagKey<Item>
			STEAM_FLYWHEEL,
			STEAM_ENGINE,
			STEAM_BOILER,
			STEAM_SOLID_BURNER,
			STEAM_FLUID_BURNER,
			STEAM_SOLAR_BOILER,
			STEAM_BURNERS,
			STEAM_PRODUCTION,
			STEAM_CONTAINER,
			STEAM_HAMMER,
			STEAM_MIXER,
			STEAM_SAW,
			BRONZE_STEAM_PUMP,
			BRONZE_BASIN,
			ADVANCED_SPOUT,
			SUPERHEATED_FUEL;

	public static final TagKey<Item>
			ANDESITE_ALLOY_INGOT,
			BRONZE_INGOT,
			BRONZE_PLATES,
			BRONZE_BLOCK,
			TIN_INGOT,
			TIN_NUGGET,
			TIN_BLOCK;

	static {
		STEAM_FLYWHEEL = addMod("steam_flywheel");
		STEAM_ENGINE = addMod("steam_engine");
		STEAM_BOILER = addMod("steam_boiler");
		STEAM_SOLID_BURNER = addMod("steam_solid_burner");
		STEAM_FLUID_BURNER = addMod("steam_fluid_burner");
		STEAM_SOLAR_BOILER = addMod("steam_solar_boiler");
		STEAM_BURNERS = addMod("steam_burners");
		STEAM_PRODUCTION = addMod("steam_production");
		STEAM_CONTAINER = addMod("steam_containers");
		STEAM_HAMMER = addMod("steam_hammer");
		STEAM_MIXER = addMod("steam_mixer");
		STEAM_SAW = addMod("steam_saw");
		BRONZE_STEAM_PUMP = addMod("bronze_steam_pump");
		BRONZE_BASIN = addMod("bronze_basin");
		ADVANCED_SPOUT = addMod("advanced_spout");
		SUPERHEATED_FUEL = addMod("superheated_fuel");

		ANDESITE_ALLOY_INGOT = addCommon("ingots/andesite_alloy");
		BRONZE_INGOT = addCommon("ingots/bronze");
		BRONZE_PLATES = addCommon("plates/bronze");
		BRONZE_BLOCK = addCommon("storage_blocks/bronze");
		TIN_INGOT = addCommon("ingots/tin");
		TIN_NUGGET = addCommon("nuggets/tin");
		TIN_BLOCK = addCommon("storage_blocks/tin");
	}

	private static TagKey<Item> add(String namespace, String name) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
	}

	private static TagKey<Item> addMod(String name) {
		return add(CreateLettersSteam.MODID, name);
	}

	private static TagKey<Item> addCommon(String name) {
		return add("c", name);
	}
}
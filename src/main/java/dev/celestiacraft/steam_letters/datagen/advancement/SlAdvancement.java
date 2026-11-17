package dev.celestiacraft.steam_letters.datagen.advancement;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.common.register.SlBlocks;
import dev.celestiacraft.steam_letters.common.register.SlItems;
import dev.celestiacraft.steam_letters.common.register.SlMaterials;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SlAdvancement {
	public final String
			TIN_NUGGET = "tin_nugget",
			BRONZE_INGOT = "bronze_ingot";

	public final String
			SOLID_BURNER = "solid_burner",
			FLUID_BURNER = "fluid_burner",
			SOLAR_BOILER = "solar_boiler",
			BOILER = "boiler";

	public final String
			STEAM_FLYWHEEL = "steam_flywheel",
			STEAM_ENGINE = "steam_engine",
			STEAM_CONTAINER = "steam_container",
			STEAM_PUMP = "steam_pump",
			STEAM_SAW = "steam_saw";

	public final String
			STEAM_HAMMER = "steam_hammer",
			STEAM_MIXER = "steam_mixer";

	public final String
			STEAM_MECHANISM = "steam_mechanism",
			SH_STEAM_COMPONENT = "sh_steam_component";

	public void addAdvancement(RegistrateAdvancementProvider provider) {
		addCreateChildren(provider);
		addModChildren(provider);
	}

	private void addCreateChildren(RegistrateAdvancementProvider provider) {
		SlAdvancementBuilder.create(TIN_NUGGET)
				.parent(SlApis.loadCreate("millstone"))
				.icon(SlMaterials.TIN_NUGGET)
				.requires(SlItemTags.TIN_NUGGET)
				.save(provider);

		SlAdvancementBuilder.create(BRONZE_INGOT)
				.parent(SlApis.loadCreate("burner"))
				.icon(SlMaterials.BRONZE_INGOT)
				.requires(SlItemTags.BRONZE_INGOT)
				.save(provider);
	}

	private void addModChildren(RegistrateAdvancementProvider provider) {
		SlAdvancementBuilder.create(SOLID_BURNER)
				.parent(BRONZE_INGOT)
				.icon(SlBlocks.BRONZE_SOLID_BURNER)
				.requires(SlItemTags.STEAM_SOLID_BURNER)
				.save(provider);

		SlAdvancementBuilder.create(FLUID_BURNER)
				.parent(BRONZE_INGOT)
				.icon(SlBlocks.BRONZE_FLUID_BURNER)
				.requires(SlItemTags.STEAM_FLUID_BURNER)
				.save(provider);

		SlAdvancementBuilder.create(SOLAR_BOILER)
				.parent(BRONZE_INGOT)
				.icon(SlBlocks.BRONZE_SOLAR_BOILER)
				.requires(SlItemTags.STEAM_SOLAR_BOILER)
				.save(provider);

		SlAdvancementBuilder.create(BOILER)
				.parent(BRONZE_INGOT)
				.icon(SlBlocks.BRONZE_BOILER)
				.requires(SlItemTags.STEAM_BOILER)
				.save(provider);

		SlAdvancementBuilder.create(STEAM_FLYWHEEL)
				.parent(BOILER)
				.icon(SlBlocks.BRONZE_STEAM_FLYWHEEL)
				.requires(SlItemTags.STEAM_FLYWHEEL)
				.save(provider);

		SlAdvancementBuilder.create(STEAM_ENGINE)
				.parent(BOILER)
				.icon(SlBlocks.BRONZE_STEAM_ENGINE)
				.requires(SlItemTags.STEAM_ENGINE)
				.save(provider);

		SlAdvancementBuilder.create(STEAM_CONTAINER)
				.parent(BOILER)
				.icon(SlItems.SEALED_STEAM_CONTAINER)
				.requires(SlItemTags.STEAM_CONTAINER)
				.save(provider);

		SlAdvancementBuilder.create(STEAM_PUMP)
				.parent(BOILER)
				.icon(SlBlocks.BRONZE_STEAM_PUMP)
				.requires(SlItemTags.BRONZE_STEAM_PUMP)
				.save(provider);

		SlAdvancementBuilder.create(STEAM_SAW)
				.parent(STEAM_FLYWHEEL)
				.icon(SlBlocks.STEAM_SAW)
				.requires(SlItemTags.STEAM_SAW)
				.save(provider);

		SlAdvancementBuilder.create(STEAM_HAMMER)
				.parent(STEAM_FLYWHEEL)
				.icon(SlBlocks.STEAM_HAMMER)
				.requires(SlItemTags.STEAM_HAMMER)
				.save(provider);

		SlAdvancementBuilder.create(STEAM_MIXER)
				.parent(STEAM_FLYWHEEL)
				.icon(SlBlocks.STEAM_MIXER)
				.requires(SlItemTags.STEAM_MIXER)
				.save(provider);

		SlAdvancementBuilder.create(STEAM_MECHANISM)
				.parent(BOILER)
				.icon(SlItems.STEAM_MECHANISM)
				.requires(SlItems.STEAM_MECHANISM)
				.save(provider);

		SlAdvancementBuilder.create(SH_STEAM_COMPONENT)
				.parent(STEAM_MECHANISM)
				.icon(SlItems.SH_STEAM_COMPONENT)
				.requires(SlItems.SH_STEAM_COMPONENT)
				.save(provider);
	}

	public String titleKey(String id) {
		return String.format("advancement.%s.%s", CreateLettersSteam.MODID, id);
	}

	public String descKey(String id) {
		return titleKey(id) + ".desc";
	}
}
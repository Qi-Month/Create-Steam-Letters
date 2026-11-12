package dev.celestiacraft.steam_letters.datagen.tags;

import com.mrh0.createaddition.index.CABlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.common.register.SlBlocks;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import slimeknights.tconstruct.fluids.TinkerFluids;

public class ItemTagsGen {
	public static void addItemTags(RegistrateItemTagsProvider provider) {
		provider.addTag(Tags.Items.INGOTS)
				.addTag(SlItemTags.ANDESITE_ALLOY_INGOT);

		provider.addTag(SlItemTags.ANDESITE_ALLOY_INGOT)
				.add(AllItems.ANDESITE_ALLOY.asItem());

		// 超级加热燃料
		provider.addTag(SlItemTags.SUPERHEATED_FUEL)
				.add(AllItems.BLAZE_CAKE.asItem())
				.add(AllItems.CREATIVE_BLAZE_CAKE.asItem())
				.addOptional(CABlocks.BIOMASS_PALLET.getId())
				.addOptional(ResourceLocation.parse(TinkerFluids.blazingBlood.getId() + "_bucket"))
				.addOptionalTag(SlApis.loadCommon("storage_blocks/coal_coke"));

		// 固体燃烧室
		provider.addTag(SlItemTags.STEAM_SOLID_BURNER)
				.add(SlBlocks.BRONZE_SOLID_BURNER.asItem());

		// 流体燃烧室
		provider.addTag(SlItemTags.STEAM_FLUID_BURNER)
				.add(SlBlocks.BRONZE_FLUID_BURNER.asItem());

		// 燃烧室
		provider.addTag(SlItemTags.STEAM_BURNERS)
				.addTag(SlItemTags.STEAM_SOLID_BURNER)
				.addTag(SlItemTags.STEAM_FLUID_BURNER);

		// 太阳能锅炉
		provider.addTag(SlItemTags.STEAM_SOLAR_BOILER)
				.add(SlBlocks.BRONZE_SOLAR_BOILER.asItem());

		// 锅炉
		provider.addTag(SlItemTags.STEAM_BOILER)
				.add(SlBlocks.BRONZE_BOILER.asItem());

		// 蒸汽生产设备
		provider.addTag(SlItemTags.STEAM_PRODUCTION)
				.addTag(SlItemTags.STEAM_BURNERS)
				.addTag(SlItemTags.STEAM_SOLAR_BOILER)
				.addTag(SlItemTags.STEAM_BOILER);

		// 蒸汽引擎
		provider.addTag(SlItemTags.STEAM_ENGINE)
				.add(SlBlocks.BRONZE_STEAM_ENGINE.asItem());

		// 蒸汽飞轮
		provider.addTag(SlItemTags.STEAM_FLYWHEEL)
				.add(SlBlocks.BRONZE_STEAM_FLYWHEEL.asItem());

		// 蒸汽加工机器
		provider.addTag(SlItemTags.STEAM_HAMMER)
				.add(SlBlocks.STEAM_HAMMER.asItem());

		provider.addTag(SlItemTags.STEAM_MIXER)
				.add(SlBlocks.STEAM_MIXER.asItem());

		provider.addTag(SlItemTags.STEAM_SAW)
				.add(SlBlocks.STEAM_SAW.asItem());

		provider.addTag(SlItemTags.BRONZE_STEAM_PUMP)
				.add(SlBlocks.BRONZE_STEAM_PUMP.asItem());

		provider.addTag(SlItemTags.BRONZE_BASIN)
				.add(SlBlocks.BRONZE_BASIN.asItem());

		provider.addTag(SlItemTags.ADVANCED_SPOUT)
				.add(SlBlocks.ADVANCED_SPOUT.asItem());

		// 深板岩
		provider.addTag(AllPaletteStoneTypes.DEEPSLATE.materialTag)
				.add(Items.COBBLED_DEEPSLATE);
	}
}
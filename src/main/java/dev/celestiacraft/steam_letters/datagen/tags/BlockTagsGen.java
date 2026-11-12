package dev.celestiacraft.steam_letters.datagen.tags;

import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.celestiacraft.steam_letters.common.register.SlBlocks;
import dev.celestiacraft.steam_letters.tags.SlBlockTags;
import net.minecraft.world.level.block.Block;

public class BlockTagsGen {
	public static void addBlockTags(RegistrateTagsProvider.IntrinsicImpl<Block> provider) {
		// 固体燃烧室
		provider.addTag(SlBlockTags.STEAM_SOLID_BURNER)
				.add(SlBlocks.BRONZE_SOLID_BURNER.get());

		// 流体燃烧室
		provider.addTag(SlBlockTags.STEAM_FLUID_BURNER)
				.add(SlBlocks.BRONZE_FLUID_BURNER.get());

		// 燃烧室
		provider.addTag(SlBlockTags.STEAM_BURNERS)
				.addTag(SlBlockTags.STEAM_SOLID_BURNER)
				.addTag(SlBlockTags.STEAM_FLUID_BURNER);

		// 太阳能锅炉
		provider.addTag(SlBlockTags.STEAM_SOLAR_BOILER)
				.add(SlBlocks.BRONZE_SOLAR_BOILER.get());

		// 锅炉
		provider.addTag(SlBlockTags.STEAM_BOILER)
				.add(SlBlocks.BRONZE_BOILER.get());

		// 蒸汽生产设备
		provider.addTag(SlBlockTags.STEAM_PRODUCTION)
				.addTag(SlBlockTags.STEAM_BURNERS)
				.addTag(SlBlockTags.STEAM_SOLAR_BOILER)
				.addTag(SlBlockTags.STEAM_BOILER);

		// 蒸汽引擎
		provider.addTag(SlBlockTags.STEAM_ENGINE)
				.add(SlBlocks.BRONZE_STEAM_ENGINE.get());

		// 蒸汽飞轮
		provider.addTag(SlBlockTags.STEAM_FLYWHEEL)
				.add(SlBlocks.BRONZE_STEAM_FLYWHEEL.get());
	}
}
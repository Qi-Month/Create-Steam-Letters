package dev.celestiacraft.steam_letters.common.block.steam_mixer;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;

public class SteamMixerBlock extends MechanicalMixerBlock {
	public SteamMixerBlock(Properties properties) {
		super(properties.mapColor(MapColor.PODZOL)
				.strength(5.0F, 6.0F)
				.sound(SoundType.LANTERN)
				.noOcclusion());
	}

	@Override
	public BlockEntityType<SteamMixerBlockEntity> getBlockEntityType() {
		return SlBlockEntities.STEAM_MIXER.get();
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelProvider models = provider.models();
			provider.simpleBlock(context.get(), models.getExistingFile(provider.modLoc("block/steam_mixer/block")));
		};
	}
}
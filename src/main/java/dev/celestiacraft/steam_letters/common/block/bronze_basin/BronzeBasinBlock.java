package dev.celestiacraft.steam_letters.common.block.bronze_basin;

import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;

public class BronzeBasinBlock extends BasinBlock {
	public BronzeBasinBlock(Properties properties) {
		super(properties.noCollission()
				.sound(SoundType.LANTERN));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<BasinBlockEntity> getBlockEntityClass() {
		return (Class) BronzeBasinBlockEntity.class;
	}

	@Override
	public BlockEntityType<BronzeBasinBlockEntity> getBlockEntityType() {
		return SlBlockEntities.BRONZE_BASIN.get();
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelProvider models = provider.models();
			ModelFile block = models.getExistingFile(provider.modLoc("block/bronze_basin/block"));
			ModelFile directional = models.getExistingFile(provider.modLoc("block/bronze_basin/block_directional"));

			VariantBlockStateBuilder builder = provider.getVariantBuilder(context.get());
			builder.partialState()
					.with(BasinBlock.FACING, Direction.DOWN)
					.modelForState()
					.modelFile(block)
					.addModel();

			for (Direction direction : Direction.Plane.HORIZONTAL) {
				builder.partialState()
						.with(BasinBlock.FACING, direction)
						.modelForState()
						.modelFile(directional)
						.rotationY((int) direction.toYRot())
						.addModel();
			}
		};
	}
}
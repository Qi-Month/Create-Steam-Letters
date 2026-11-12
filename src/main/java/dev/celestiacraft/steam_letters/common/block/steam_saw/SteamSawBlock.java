package dev.celestiacraft.steam_letters.common.block.steam_saw;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import org.jetbrains.annotations.NotNull;

public class SteamSawBlock extends SawBlock {
	public SteamSawBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull BlockEntityType<SteamSawBlockEntity> getBlockEntityType() {
		return SlBlockEntities.STEAM_SAW.get();
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelProvider models = provider.models();

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						Direction direction = state.getValue(BlockStateProperties.FACING);
						boolean vertical = direction.getAxis() == Direction.Axis.Y;
						boolean axisAlongFirst = state.getValue(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
						boolean flipped = state.getValue(SawBlock.FLIPPED);

						int y = vertical
								? (axisAlongFirst ? (flipped ? 90 : 270) : (flipped ? 180 : 0))
								: (int) direction.toYRot();

						return ConfiguredModel.builder()
								.modelFile(models.getExistingFile(provider.modLoc("block/mechanical_saw/" + (vertical ? "vertical" : "horizontal"))))
								.rotationX(direction == Direction.DOWN ? 180 : 0)
								.rotationY(y)
								.build();
					});
		};
	}
}
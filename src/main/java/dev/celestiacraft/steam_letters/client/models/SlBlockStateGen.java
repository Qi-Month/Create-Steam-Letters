package dev.celestiacraft.steam_letters.client.models;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.common.block.burner.AbstractBurnerBlock;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

public class SlBlockStateGen {
	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simple(String model) {
		return (context, provider) -> {
			BlockModelProvider models = provider.models();

			provider.simpleBlock(context.get(), models.getExistingFile(provider.modLoc(model)));
		};
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> horizontalFacing(String model) {
		return horizontalFacing(BlockStateProperties.HORIZONTAL_FACING, model);
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> horizontalFacing(DirectionProperty facing, String model) {
		return (context, provider) -> {
			ModelFile file = provider.models().getExistingFile(provider.modLoc(model));

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						return ConfiguredModel.builder()
								.modelFile(file)
								.rotationY((int) state.getValue(facing).toYRot())
								.build();
					});
		};
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> flywheel(String basePath) {
		return flywheel(basePath, 0);
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> flywheel(String basePath, int yOffset) {
		return (context, provider) -> provider.getVariantBuilder(context.get()).forAllStates(state -> {
			BlockModelProvider models = provider.models();

			String connection = state.getValue(SteamFlywheelBlock.CONNECTION).getSerializedName();
			ModelFile file = models.getExistingFile(provider.modLoc(basePath + "_casing_" + connection));

			return ConfiguredModel.builder()
					.modelFile(file)
					.rotationY((int) state.getValue(SteamFlywheelBlock.HORIZONTAL_FACING).toYRot() + yOffset)
					.build();
		});
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> facingLit(String offModel, String litModel) {
		return facingLit(AbstractBurnerBlock.FACING, AbstractBurnerBlock.LIT, offModel, litModel);
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> facingLit(DirectionProperty facing, BooleanProperty lit, String offModel, String litModel) {
		return facingLit(facing, lit, offModel, litModel, 0);
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> facingLit(DirectionProperty facing, BooleanProperty lit, String offModel, String litModel, int yOffset) {
		return (context, provider) -> {
			BlockModelProvider models = provider.models();
			ModelFile off = models.getExistingFile(provider.modLoc(offModel));
			ModelFile litFile = models.getExistingFile(provider.modLoc(litModel));

			provider.getVariantBuilder(context.get()).forAllStates(state -> ConfiguredModel.builder()
					.modelFile(state.getValue(lit) ? litFile : off)
					.rotationY((int) state.getValue(facing).toYRot() + yOffset)
					.build());
		};
	}
}
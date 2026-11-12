package dev.celestiacraft.steam_letters.common.block.steam_pump;

import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;

public class BronzeSteamPumpBlock extends PumpBlock {
	public BronzeSteamPumpBlock(Properties properties) {
		super(properties.sound(SoundType.LANTERN)
				.noOcclusion());
	}

	@Override
	public @NotNull BlockEntityType<? extends PumpBlockEntity> getBlockEntityType() {
		return SlBlockEntities.BRONZE_STEAM_PUMP.get();
	}

	/**
	 * 蒸汽泵不接入动力网络. 覆盖此方法可切断与相邻轴的连接
	 */
	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

	/**
	 * 原版 PumpBlock 默认被当作小齿轮参与啮合传动. 并列放置时会因此进入相邻的齿轮传播链路
	 * 这里明确排除, 使蒸汽泵完全不参与齿轮传力
	 */
	@Override
	public boolean isSmallCog() {
		return false;
	}

	@Override
	public boolean isLargeCog() {
		return super.isLargeCog();
	}

	@Override
	public boolean hideStressImpact() {
		return true;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			ModelFile model = provider.models().getExistingFile(provider.modLoc("block/bronze_pump/block"));

			provider.getVariantBuilder(context.get()).forAllStates((state) -> {
				Direction direction = state.getValue(FACING);
				ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(model);

				if (direction == Direction.DOWN) {
					builder.rotationX(180);
				} else if (direction != Direction.UP) {
					builder.rotationX(90);
					builder.rotationY(((int) direction.toYRot() + 180) % 360);
				}

				return builder.build();
			});
		};
	}
}
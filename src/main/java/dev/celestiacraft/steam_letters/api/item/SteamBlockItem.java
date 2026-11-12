package dev.celestiacraft.steam_letters.api.item;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.logistics.depot.DepotBlock;
import com.simibubi.create.content.logistics.depot.EjectorBlock;
import com.simibubi.create.content.processing.basin.BasinBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 放置逻辑对齐原版 {@code AssemblyOperatorBlockItem}: 点击传送带, 工作盆, 物流底盘或投掷器的顶面时,
 * 落点抬到该方块正上方第二格.
 * <p>
 * 目标格被占用时直接放置失败, 不退回正上方第一格. 退回会让锻锤压住带面, 搅拌机压住盆口,
 * 而这类机器的方块实体都在读取 {@code below(2)} 处的承载物, 贴着一格反而工作不了.
 */
public class SteamBlockItem extends BlockItem {
	public SteamBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public @NotNull InteractionResult place(BlockPlaceContext context) {
		BlockPos placedOnPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
		Level level = context.getLevel();
		BlockState placedOnState = level.getBlockState(placedOnPos);

		if (operatesOn(level, placedOnPos, placedOnState) && context.getClickedFace() == Direction.UP) {
			if (level.getBlockState(placedOnPos.above(2)).canBeReplaced()) {
				context = BlockPlaceContext.at(context, placedOnPos.above(2), Direction.UP);
			} else {
				return InteractionResult.FAIL;
			}
		}

		return super.place(context);
	}

	protected boolean operatesOn(LevelReader level, BlockPos pos, BlockState placedOnState) {
		if (placedOnState.getBlock() instanceof BeltBlock) {
			return placedOnState.getValue(BeltBlock.SLOPE) == BeltSlope.HORIZONTAL;
		}

		return BasinBlock.isBasin(level, pos)
				|| placedOnState.getBlock() instanceof DepotBlock
				|| placedOnState.getBlock() instanceof EjectorBlock;
	}
}
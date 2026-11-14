package dev.celestiacraft.steam_letters.api.item;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.logistics.depot.DepotBlock;
import com.simibubi.create.content.logistics.depot.EjectorBlock;
import com.simibubi.create.content.processing.basin.BasinBlock;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlock;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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
 * <p>
 * 锅炉另外实现多格放置: 贴着已成形的 2x2 及以上锅炉再放一块时, 按结构宽度一次补齐整层, 与流体储罐一致.
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

		InteractionResult result = super.place(context);
		if (result.consumesAction() && getBlock() instanceof SteamBoilerBlock boiler) {
			tryMultiPlace(context, boiler);
		}

		return result;
	}

	/**
	 * 贴着已成形锅炉的顶面或底面放置时, 按该结构的宽度补齐全层, 材料不足或落点被占则维持单块放置.
	 */
	private void tryMultiPlace(BlockPlaceContext context, SteamBoilerBlock boiler) {
		Player player = context.getPlayer();
		if (player == null || player.isShiftKeyDown()) {
			return;
		}

		Direction face = context.getClickedFace();
		if (!face.getAxis().isVertical()) {
			return;
		}

		if (SymmetryWandItem.presentInHotbar(player)) {
			return;
		}

		Level level = context.getLevel();
		BlockPos placedOnPos = context.getClickedPos().relative(face.getOpposite());
		if (!(level.getBlockState(placedOnPos).getBlock() instanceof SteamBoilerBlock)) {
			return;
		}

		SteamBoilerBlockEntity part = ConnectivityHandler.partAt(boiler.getBlockEntityType(), level, placedOnPos);
		SteamBoilerBlockEntity controller = part == null ? null : part.getControllerBoiler();
		if (controller == null) {
			return;
		}

		int width = controller.getWidth();
		if (width <= 1) {
			return;
		}

		BlockPos startPos = face == Direction.DOWN ? controller.getBlockPos().below() : controller.getBlockPos().above(controller.getHeight());
		if (startPos.getY() != context.getClickedPos().getY()) {
			return;
		}

		int toPlace = 0;
		for (int xOffset = 0; xOffset < width; xOffset++) {
			for (int zOffset = 0; zOffset < width; zOffset++) {
				BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
				BlockState offsetState = level.getBlockState(offsetPos);
				if (offsetState.getBlock() instanceof SteamBoilerBlock) {
					continue;
				}
				if (!offsetState.canBeReplaced()) {
					return;
				}
				toPlace++;
			}
		}

		ItemStack stack = context.getItemInHand();
		if (toPlace == 0 || (!player.isCreative() && stack.getCount() < toPlace)) {
			return;
		}

		for (int xOffset = 0; xOffset < width; xOffset++) {
			for (int zOffset = 0; zOffset < width; zOffset++) {
				BlockPos offsetPos = startPos.offset(xOffset, 0, zOffset);
				if (level.getBlockState(offsetPos).getBlock() instanceof SteamBoilerBlock) {
					continue;
				}
				BlockPlaceContext offsetContext = BlockPlaceContext.at(context, offsetPos, face);
				player.getPersistentData().putBoolean("SilenceTankSound", true);
				super.place(offsetContext);
				player.getPersistentData().remove("SilenceTankSound");
			}
		}
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
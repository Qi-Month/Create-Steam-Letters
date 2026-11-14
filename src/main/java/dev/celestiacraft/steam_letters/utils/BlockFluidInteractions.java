package dev.celestiacraft.steam_letters.utils;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;

/**
 * 方块侧流体物品交互的公共入口.
 * <p>
 * 生存模式: 只放行"容器里已经装有流体"的交互 (把水灌进设备), 空容器一律不处理,
 * 因此空桶 / 空罐永远无法从设备里抽取流体 —— 蒸汽只能通过流体管道导出.
 * <p>
 * 创造模式: 不做这项限制, 只要手持的是流体容器就交给流体能力处理, 空容器也允许抽取,
 * 方便直接灌取调试.
 */
public class BlockFluidInteractions {
	public static InteractionResult fillOnly(Level level, BlockPos pos, Player player, InteractionHand hand, Direction side) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.isEmpty()) {
			return InteractionResult.PASS;
		}

		Optional<IFluidHandlerItem> itemHandler = FluidUtil.getFluidHandler(stack);
		Optional<FluidStack> contained = FluidUtil.getFluidContained(stack);
		if (player.isCreative()) {
			// 创造模式只要求手持物品确实是流体容器, 空容器同样放行
			if (itemHandler.isEmpty()) {
				return InteractionResult.PASS;
			}
		} else {
			if (contained.isEmpty() || contained.get().isEmpty()) {
				return InteractionResult.PASS;
			}
		}

		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		IFluidHandler blockHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
		CreateLettersSteam.LOGGER.info("[fillOnly] stack={}, itemHandler={}, contained={}, blockCapability={}", stack, itemHandler.isPresent(), contained, blockHandler);

		boolean handled = FluidUtil.interactWithFluidHandler(player, hand, level, pos, side);
		CreateLettersSteam.LOGGER.info("[fillOnly] handled={}", handled);
		return handled ? InteractionResult.CONSUME : InteractionResult.PASS;
	}
}
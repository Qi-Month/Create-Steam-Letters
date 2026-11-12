package dev.celestiacraft.steam_letters.common.item;

import dev.celestiacraft.steam_letters.common.register.SlDataComponents;
import dev.celestiacraft.steam_letters.common.register.SlItems;
import dev.celestiacraft.steam_letters.tags.SlFluidTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class SteamContainerItem extends Item {
	public static final int CAPACITY = 64000;
	private final boolean emptied;

	public SteamContainerItem(Properties properties, boolean emptied) {
		super(properties.stacksTo(1));
		this.emptied = emptied;
	}

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}

		BlockPos pos = context.getClickedPos();
		Direction side = context.getClickedFace();
		Optional<IFluidHandler> optional = FluidUtil.getFluidHandler(level, pos, side);
		if (optional.isEmpty()) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		IFluidHandler handler = optional.orElse(null);
		if (handler == null) {
			return InteractionResult.PASS;
		}
		InteractionHand hand = context.getHand();
		ItemStack held = context.getItemInHand();

		// 空容器只能抽; 密封容器默认灌出, 潜行时改回抽取.
		boolean extracting = emptied || player.isShiftKeyDown();
		boolean changed = extracting
				? extract(handler, player, held, hand, level, pos)
				: insert(handler, player, held, hand, level, pos);

		return changed ? InteractionResult.SUCCESS : InteractionResult.PASS;
	}

	/**
	 * 从方块里把蒸汽抽进容器, 一次按满容器算.
	 */
	private boolean extract(IFluidHandler handler, Player player, ItemStack held, InteractionHand hand, Level level, BlockPos pos) {
		FluidStack stored = emptied ? FluidStack.EMPTY : readFluid(held);
		int space = CAPACITY - stored.getAmount();
		if (space <= 0) {
			return false;
		}

		FluidStack probe = handler.drain(space, IFluidHandler.FluidAction.SIMULATE);
		if (!isSteamFluid(probe)) {
			return false;
		}
		if (!stored.isEmpty() && !FluidStack.isSameFluidSameComponents(stored, probe)) {
			// 一个容器只能装一种气体.
			return false;
		}

		FluidStack drained = handler.drain(probe.copy(), IFluidHandler.FluidAction.EXECUTE);
		if (drained.isEmpty()) {
			return false;
		}

		FluidStack merged = drained.copy();
		merged.setAmount(Math.min(CAPACITY, stored.getAmount() + drained.getAmount()));

		ItemStack result = emptied ? new ItemStack(SlItems.SEALED_STEAM_CONTAINER.get()) : held.copy();
		writeFluid(result, merged);
		player.setItemInHand(hand, result);
		level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.7F, 1.0F);
		return true;
	}

	/**
	 * 把容器里的蒸汽灌进方块, 一次按空容器算.
	 */
	private boolean insert(IFluidHandler handler, Player player, ItemStack held, InteractionHand hand, Level level, BlockPos pos) {
		if (emptied) {
			return false;
		}
		FluidStack stored = readFluid(held);
		if (stored.isEmpty()) {
			return false;
		}

		int filled = handler.fill(stored.copy(), IFluidHandler.FluidAction.EXECUTE);
		if (filled <= 0) {
			return false;
		}

		int remaining = stored.getAmount() - filled;
		ItemStack result;
		if (remaining <= 0) {
			result = new ItemStack(SlItems.EMPTY_STEAM_CONTAINER.get());
		} else {
			result = held.copy();
			FluidStack left = stored.copy();
			left.setAmount(remaining);
			writeFluid(result, left);
		}
		player.setItemInHand(hand, result);
		level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.7F, 1.0F);
		return true;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return !emptied && !readFluid(stack).isEmpty();
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		FluidStack fluid = readFluid(stack);
		if (fluid.isEmpty()) {
			return 0;
		}
		return Math.min(13, Math.max(1, Math.round(13.0F * fluid.getAmount() / CAPACITY)));
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return 0x6FD9FF;
	}

	public static @NotNull FluidStack readFluid(@NotNull ItemStack stack) {
		CompoundTag tag = stack.get(SlDataComponents.STORED_FLUID.get());
		if (tag == null) {
			return FluidStack.EMPTY;
		}
		return FluidStack.parseOptional(RegistryAccess.EMPTY, tag);
	}

	public static void writeFluid(@NotNull ItemStack stack, @NotNull FluidStack fluid) {
		if (fluid.isEmpty()) {
			stack.remove(SlDataComponents.STORED_FLUID.get());
			return;
		}
		stack.set(SlDataComponents.STORED_FLUID.get(), (CompoundTag) fluid.save(RegistryAccess.EMPTY));
	}

	/**
	 * 只接受 {@code forge:steam} 与 {@code forge:superheated_steam}.
	 */
	public static boolean isSteamFluid(@NotNull FluidStack stack) {
		return !stack.isEmpty()
				&& (stack.getFluid().defaultFluidState().is(SlFluidTags.STEAM)
				|| stack.getFluid().defaultFluidState().is(SlFluidTags.SUPERHEATED_STEAM));
	}
}
package dev.celestiacraft.steam_letters.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SteamBucketItem extends BucketItem {
	public SteamBucketItem(Fluid fluid, Properties properties) {
		super(fluid, properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	@Override
	public boolean emptyContents(@Nullable Player player, @NotNull Level level, @NotNull BlockPos pos, @Nullable BlockHitResult result, @Nullable ItemStack container) {
		return false;
	}
}
package dev.celestiacraft.steam_letters.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SteamMechanismItem extends Item {
	public SteamMechanismItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		ItemStack item = player.getItemInHand(hand);
		player.swing(hand);

		level.playSound(
				null,
				player.getX(),
				player.getY(),
				player.getZ(),
				SoundEvents.FIRE_EXTINGUISH,
				SoundSource.PLAYERS,
				1.0F,
				1.0F
		);

		if (!level.isClientSide()) {
			List<LivingEntity> entities = level.getEntitiesOfClass(
					LivingEntity.class,
					player.getBoundingBox().inflate(2)
			);
			entities.forEach((entity) -> {
				if (entity.distanceToSqr(player) <= 4 && !trigger(entity)) {
					entity.hurt(entity.damageSources().lava(), 8.0F);
				}
			});
		}

		return InteractionResultHolder.success(item);
	}

	private boolean trigger(LivingEntity entity) {
		return entity.hasEffect(MobEffects.FIRE_RESISTANCE);
	}
}
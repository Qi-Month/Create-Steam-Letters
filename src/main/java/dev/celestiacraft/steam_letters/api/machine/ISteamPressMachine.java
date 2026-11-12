package dev.celestiacraft.steam_letters.api.machine;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 除 {@link ISteamStorage} 的状态存取与压床相关的访问器外均有默认实现.
 *
 * <p>
 * {@code MechanicalPressBlockEntity} 已具体实现 {@code canProcessInBulk} / {@code tryProcessOnBelt} /
 * {@code tryProcessInWorld} / {@code tryProcessInBasin} / {@code getKineticSpeed} / {@code getParticleAmount} /
 * {@code onPressingCompleted} / {@code getRecipe} / {@code getPressingBehaviour} / {@code onItemPressed}.
 * 父类具体方法优先于接口 default, 本接口的默认实现全被覆盖, 前三个需由实现类委托给
 * {@code ISteamPressMachine.super} 才能生效, 其余沿用机械动力的行为.
 */
public interface ISteamPressMachine extends PressingBehaviour.PressingBehaviourSpecifics, ISteamStorage {
	Optional<RecipeHolder<PressingRecipe>> getRecipe(ItemStack item);

	PressingBehaviour getPressingBehaviour();

	float getSpeed();

	default Level level() {
		return getPressingBehaviour().getWorld();
	}

	/**
	 * 此方法返回值必须为{@code false}!
	 * <p>
	 * 如果为{@code true}会直接使用机械动力的并行处理, 从而无法正常触发并行.
	 */
	@Override
	default boolean canProcessInBulk() {
		return false;
	}

	@Override
	default boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
		Optional<RecipeHolder<PressingRecipe>> recipe = getRecipe(input.stack);

		if (recipe.isEmpty()) {
			return false;
		}

		int batch = getBatchSize(input.stack.getCount());
		if (batch <= 0) {
			return false;
		}
		if (simulate) {
			return true;
		}

		Level level = level();
		getPressingBehaviour().particleItems.add(input.stack.copyWithCount(batch));

		List<ItemStack> outputs = RecipeApplier.applyRecipeOn(
				level,
				input.stack.copyWithCount(batch),
				recipe.get().value(),
				true
		);

		/*
		 * BeltPressingCallbacks.whenItemHeld 之后只会 shrink(1)
		 * 这里先扣掉 batch - 1 凑成正好 batch.
		 * batch == count 时 stack 正好剩 1 个
		 * 于是它判定 bulk = true 走 convertTo, 整叠被正确移除.
		 */
		input.stack.shrink(batch - 1);

		for (ItemStack created : outputs) {
			if (!created.isEmpty()) {
				onItemPressed(created);
				break;
			}
		}

		outputList.addAll(outputs);
		return true;
	}

	@Override
	default boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
		ItemStack item = itemEntity.getItem();
		Optional<RecipeHolder<PressingRecipe>> recipe = getRecipe(item);
		if (recipe.isEmpty()) {
			return false;
		}

		int batch = getBatchSize(item.getCount());
		if (batch <= 0) {
			return false;
		}
		if (simulate) {
			return true;
		}

		Level level = level();
		getPressingBehaviour().particleItems.add(item.copyWithCount(batch));

		ItemStack itemCreated = ItemStack.EMPTY;
		if (batch == item.getCount()) {
			RecipeApplier.applyRecipeOn(itemEntity, recipe.get().value(), true);
			itemCreated = itemEntity.getItem().copy();
		} else {
			for (ItemStack result : RecipeApplier.applyRecipeOn(
					level,
					item.copyWithCount(batch),
					recipe.get().value(),
					true
			)) {
				if (itemCreated.isEmpty()) {
					itemCreated = result.copy();
				}
				ItemEntity created = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
				created.setDefaultPickUpDelay();
				created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, 0.05f));
				level.addFreshEntity(created);
			}
			item.shrink(batch);
		}

		if (!itemCreated.isEmpty()) {
			onItemPressed(itemCreated);
		}
		return true;
	}

	/**
	 * 机械动力的压床已具体实现该方法, 本默认值不会生效, 仅在链上没有具体实现时兜底.
	 */
	@Override
	default boolean tryProcessInBasin(boolean simulate) {
		return false;
	}

	@Override
	default float getKineticSpeed() {
		return getSpeed();
	}

	@Override
	default int getParticleAmount() {
		return 15;
	}

	@Override
	default void onPressingCompleted() {
	}

	default void onItemPressed(ItemStack result) {
	}
}
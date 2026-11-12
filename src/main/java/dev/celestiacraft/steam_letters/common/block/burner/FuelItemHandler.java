package dev.celestiacraft.steam_letters.common.block.burner;

import lombok.AllArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class FuelItemHandler implements IItemHandler {
	private final SolidBurnerBlockEntity entity;

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return slot == 0 ? entity.getFuel() : ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (slot != 0 || stack.isEmpty() || !entity.isFuel(stack)) {
			return stack;
		}

		ItemStack fuel = entity.getFuel();
		if (!fuel.isEmpty() && !ItemStack.isSameItemSameComponents(fuel, stack)) {
			return stack;
		}

		int limit = Math.min(getSlotLimit(0), stack.getMaxStackSize());
		int space = limit - fuel.getCount();
		if (space <= 0) {
			return stack;
		}

		int moved = Math.min(space, stack.getCount());
		if (!simulate) {
			if (fuel.isEmpty()) {
				entity.setFuel(stack.copyWithCount(moved));
			} else {
				fuel.grow(moved);
			}
			entity.setChanged();
		}

		ItemStack remainder = stack.copy();
		remainder.shrink(moved);
		return remainder;
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot != 0 || amount <= 0) {
			return ItemStack.EMPTY;
		}

		ItemStack fuel = entity.getFuel();
		if (fuel.isEmpty()) {
			return ItemStack.EMPTY;
		}

		int taken = Math.min(amount, fuel.getCount());
		ItemStack result = fuel.copyWithCount(taken);
		if (!simulate) {
			fuel.shrink(taken);
			if (fuel.isEmpty()) {
				entity.setFuel(ItemStack.EMPTY);
			}
			entity.setChanged();
		}
		return result;
	}

	@Override
	public int getSlotLimit(int slot) {
		return SolidBurnerBlockEntity.FUEL_SLOT_LIMIT;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return slot == 0 && entity.isFuel(stack);
	}
}
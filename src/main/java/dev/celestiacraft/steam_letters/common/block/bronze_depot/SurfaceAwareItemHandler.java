package dev.celestiacraft.steam_letters.common.block.bronze_depot;

import lombok.AllArgsConstructor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class SurfaceAwareItemHandler implements IItemHandler {
	private final IItemHandler inventory;
	private final IItemHandler surface;

	@Override
	public int getSlots() {
		return inventory.getSlots() + surface.getSlots();
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return slot < inventory.getSlots()
				? inventory.getStackInSlot(slot)
				: surface.getStackInSlot(slot - inventory.getSlots());
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return slot < inventory.getSlots()
				? inventory.insertItem(slot, stack, simulate)
				: surface.insertItem(slot - inventory.getSlots(), stack, simulate);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		return slot < inventory.getSlots()
				? inventory.extractItem(slot, amount, simulate)
				: surface.extractItem(slot - inventory.getSlots(), amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return slot < inventory.getSlots()
				? inventory.getSlotLimit(slot)
				: surface.getSlotLimit(slot - inventory.getSlots());
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return slot < inventory.getSlots()
				? inventory.isItemValid(slot, stack)
				: surface.isItemValid(slot - inventory.getSlots(), stack);
	}
}
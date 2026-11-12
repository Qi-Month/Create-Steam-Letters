package dev.celestiacraft.steam_letters.common.block.bronze_basin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinInventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BronzeBasinInventory extends BasinInventory {
	public BronzeBasinInventory(int slots, BasinBlockEntity entity) {
		super(slots, entity);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return inv.insertItem(slot, stack, simulate);
	}
}
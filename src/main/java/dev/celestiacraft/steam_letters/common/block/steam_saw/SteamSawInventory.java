package dev.celestiacraft.steam_letters.common.block.steam_saw;

import com.simibubi.create.content.processing.recipe.ProcessingInventory;

public class SteamSawInventory extends ProcessingInventory {
	SteamSawBlockEntity entity;

	public SteamSawInventory(SteamSawBlockEntity entity) {
		super(entity::start);
		this.entity = entity;
	}

	@Override
	public int getSlotLimit(int slot) {
		return slot == 0 ? Math.max(1, entity.getParallelCount()) : super.getSlotLimit(slot);
	}
}
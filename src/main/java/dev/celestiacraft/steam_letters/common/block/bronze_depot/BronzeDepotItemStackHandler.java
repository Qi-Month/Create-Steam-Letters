package dev.celestiacraft.steam_letters.common.block.bronze_depot;

import lombok.Setter;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BronzeDepotItemStackHandler extends ItemStackHandler {
	private final BronzeDepotBlockEntity entity;
	@Setter
	private boolean suppressUpdates;

	public BronzeDepotItemStackHandler(int slots, BronzeDepotBlockEntity entity) {
		super(slots);
		this.entity = entity;
	}

	@Override
	protected void onContentsChanged(int slot) {
		entity.setChanged();

		if (suppressUpdates) {
			return;
		}

		entity.notifyUpdate();
	}
}
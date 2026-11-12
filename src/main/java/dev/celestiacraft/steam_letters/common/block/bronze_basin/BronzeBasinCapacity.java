package dev.celestiacraft.steam_letters.common.block.bronze_basin;

import dev.celestiacraft.steam_letters.config.common.BronzeBasinConfig;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BronzeBasinCapacity {
	public int slotCapacity() {
		return BronzeBasinConfig.SLOT_CAPACITY.get();
	}

	public int totalCapacity() {
		return 4 * slotCapacity();
	}

	public int renderFullCapacity() {
		return 2 * slotCapacity();
	}
}
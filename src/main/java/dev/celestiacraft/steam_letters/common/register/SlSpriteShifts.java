package dev.celestiacraft.steam_letters.common.register;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import dev.celestiacraft.steam_letters.api.SlApis;

public class SlSpriteShifts {
	public static final CTSpriteShiftEntry BRONZE_CASING;

	static {
		BRONZE_CASING = CTSpriteShifter.getCT(
				AllCTTypes.OMNIDIRECTIONAL,
				SlApis.loadResource("block/bronze_casing"),
				SlApis.loadResource("block/bronze_casing_connected")
		);
	}
}
package dev.celestiacraft.steam_letters.client.models;

import net.minecraft.core.Direction;

import java.util.Arrays;

public class SteamBoilerCullData {
	private final boolean[] culled;

	public SteamBoilerCullData() {
		culled = new boolean[4];
		Arrays.fill(culled, false);
	}

	public void setCulled(Direction face, boolean value) {
		if (face.getAxis().isVertical()) {
			return;
		}
		culled[face.get2DDataValue()] = value;
	}

	public boolean isCulled(Direction face) {
		if (face.getAxis().isVertical()) {
			return false;
		}
		return culled[face.get2DDataValue()];
	}
}
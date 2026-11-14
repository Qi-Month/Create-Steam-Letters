package dev.celestiacraft.steam_letters.client.models;

import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.HorizontalCTBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 连接纹理只在同一个多方块结构内部生效, 相邻但没有连成一体的两块锅炉不会互相连纹理, 也不会互相剔面.
 */
public class SteamBoilerCTBehaviour extends HorizontalCTBehaviour {
	public SteamBoilerCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift) {
		super(layerShift, topShift);
	}

	@Override
	public boolean buildContextForOccludedDirections() {
		return true;
	}

	@Override
	public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos, Direction face) {
		return state.getBlock() == other.getBlock()
				&& ConnectivityHandler.isConnected(reader, pos, otherPos);
	}
}
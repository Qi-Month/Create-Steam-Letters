package dev.celestiacraft.steam_letters.common.block.advanced_spout;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AdvancedSpoutSpoutValueBox extends ValueBoxTransform.Sided {
	public AdvancedSpoutSpoutValueBox() {
		fromSide(Direction.NORTH);
	}

	@Override
	protected boolean isSideActive(BlockState state, Direction direction) {
		return direction.getAxis().isHorizontal();
	}

	@Override
	protected Vec3 getSouthLocation() {
		return VecHelper.voxelSpace(8, 8, 16.5);
	}
}
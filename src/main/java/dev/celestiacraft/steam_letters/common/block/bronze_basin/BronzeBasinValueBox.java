package dev.celestiacraft.steam_letters.common.block.bronze_basin;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BronzeBasinValueBox extends ValueBoxTransform.Sided {
	@Override
	protected Vec3 getSouthLocation() {
		return VecHelper.voxelSpace(8, 12, 16.05);
	}

	@Override
	protected boolean isSideActive(BlockState state, Direction direction) {
		return direction.getAxis().isHorizontal();
	}
}
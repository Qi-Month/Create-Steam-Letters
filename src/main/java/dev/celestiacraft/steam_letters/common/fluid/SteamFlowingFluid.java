package dev.celestiacraft.steam_letters.common.fluid;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import org.jetbrains.annotations.NotNull;

public abstract class SteamFlowingFluid extends BaseFlowingFluid {
	protected SteamFlowingFluid(Properties properties) {
		super(properties.slopeFindDistance(3)
				.explosionResistance(100.0F));
	}

	@Override
	public @NotNull BlockState createLegacyBlock(FluidState state) {
		return Blocks.AIR.defaultBlockState();
	}
}
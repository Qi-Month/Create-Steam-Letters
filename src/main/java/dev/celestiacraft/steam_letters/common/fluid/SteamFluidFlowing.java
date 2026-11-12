package dev.celestiacraft.steam_letters.common.fluid;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class SteamFluidFlowing extends SteamFlowingFluid {
	public SteamFluidFlowing(Properties properties) {
		super(properties.slopeFindDistance(3)
				.explosionResistance(100.0F));
		registerDefaultState(getStateDefinition()
				.any()
				.setValue(LEVEL, 7));
	}

	@Override
	protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
		super.createFluidStateDefinition(builder);
		builder.add(LEVEL);
	}

	@Override
	public int getAmount(FluidState state) {
		return state.getValue(LEVEL);
	}

	@Override
	public boolean isSource(@NotNull FluidState state) {
		return false;
	}
}
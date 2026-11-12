package dev.celestiacraft.steam_letters.common.fluid;

import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class SteamFluidSource extends SteamFlowingFluid {
	public SteamFluidSource(Properties properties) {
		super(properties.slopeFindDistance(3)
				.explosionResistance(100.0F));
	}

	@Override
	public int getAmount(@NotNull FluidState state) {
		return 8;
	}

	@Override
	public boolean isSource(@NotNull FluidState state) {
		return true;
	}
}
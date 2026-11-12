package dev.celestiacraft.steam_letters.common.block.engine;

import dev.celestiacraft.steam_letters.common.fluid.BlockEntityFluidTank;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import dev.celestiacraft.steam_letters.config.common.SteamConfig;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class SteamEngineFluidTank extends BlockEntityFluidTank {
	public SteamEngineFluidTank(BlockEntity owner, int capacity) {
		super(owner, capacity, SteamEngineFluidTank::isSteamFluid);
	}

	public static boolean isSteamFluid(FluidStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		Fluid fluid = stack.getFluid();
		if (fluid.isSame(SlFluids.STEAM.get()) || fluid.isSame(SlFluids.SUPERHEATED_STEAM.get())) {
			return true;
		}

		Fluid configured = SteamConfig.getOutputSteamFluid();
		return configured != null && fluid.isSame(configured);
	}
}
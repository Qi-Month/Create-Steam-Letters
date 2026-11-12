package dev.celestiacraft.steam_letters.common.block.burner;

import dev.celestiacraft.steam_letters.common.fluid.BlockEntityFluidTank;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Predicate;

public class FuelFluidTank extends BlockEntityFluidTank {
	public FuelFluidTank(BlockEntity owner, int capacity, Predicate<FluidStack> validator) {
		super(owner, capacity, validator);
	}
}
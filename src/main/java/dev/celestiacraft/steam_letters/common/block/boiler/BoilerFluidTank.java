package dev.celestiacraft.steam_letters.common.block.boiler;

import dev.celestiacraft.steam_letters.common.fluid.BlockEntityFluidTank;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Predicate;

public class BoilerFluidTank extends BlockEntityFluidTank {
	public BoilerFluidTank(BlockEntity entity, int capacity, Predicate<FluidStack> validator) {
		super(entity, capacity, validator);
	}

	public static boolean isWater(FluidStack stack) {
		return !stack.isEmpty() && stack.getFluid().defaultFluidState().is(FluidTags.WATER);
	}
}
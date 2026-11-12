package dev.celestiacraft.steam_letters.common.fluid;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class BlockEntityFluidTank extends FluidTank {
	private final BlockEntity entity;
	private final Predicate<FluidStack> validator;

	public BlockEntityFluidTank(BlockEntity entity, int capacity, Predicate<FluidStack> validator) {
		super(capacity);
		this.entity = entity;
		this.validator = validator;
	}

	@Override
	protected void onContentsChanged() {
		entity.setChanged();
	}

	@Override
	public boolean isFluidValid(FluidStack stack) {
		return validator.test(stack);
	}
}
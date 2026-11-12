package dev.celestiacraft.steam_letters.common.block.boiler;

import lombok.AllArgsConstructor;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class BoilerFluidHandler implements IFluidHandler {
	private final AbstractBoilerBlockEntity boiler;

	@Override
	public int getTanks() {
		return 2;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return tank == AbstractBoilerBlockEntity.TANK_OUTPUT
				? boiler.getOutputTank().getFluid()
				: boiler.getInputTank().getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return tank == AbstractBoilerBlockEntity.TANK_OUTPUT
				? boiler.getOutputTank().getCapacity()
				: boiler.getInputTank().getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return tank == AbstractBoilerBlockEntity.TANK_INPUT && boiler.getInputTank().isFluidValid(stack);
	}

	@Override
	public int fill(FluidStack stack, FluidAction action) {
		return boiler.getInputTank().fill(stack, action);
	}

	@Override
	public @NotNull FluidStack drain(FluidStack stack, FluidAction action) {
		return boiler.getOutputTank().drain(stack, action);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return boiler.getOutputTank().drain(maxDrain, action);
	}
}
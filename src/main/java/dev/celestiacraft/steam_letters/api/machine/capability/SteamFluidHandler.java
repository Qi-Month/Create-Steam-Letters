package dev.celestiacraft.steam_letters.api.machine.capability;

import dev.celestiacraft.steam_letters.api.machine.ISteamStorage;
import lombok.AllArgsConstructor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class SteamFluidHandler implements IFluidHandler {
	private final ISteamStorage entity;

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return entity.getSteamStack();
	}

	@Override
	public int getTankCapacity(int tank) {
		return entity.getSteamCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return entity.isValidSteam(stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return entity.fillSteam(resource, action);
	}

	@Override
	public @NotNull FluidStack drain(FluidStack stack, FluidAction action) {
		FluidStack steam = entity.getSteamStack();
		Fluid fluid = stack.getFluid();

		if (stack.isEmpty() || steam.isEmpty() || !fluid.isSame(steam.getFluid())) {
			return FluidStack.EMPTY;
		}
		return entity.drainSteam(stack.getAmount(), action);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return entity.drainSteam(maxDrain, action);
	}
}
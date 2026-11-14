package dev.celestiacraft.steam_letters.common.block.boiler;

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Consumer;

public class SteamTank extends SmartFluidTank {
	public SteamTank(int capacity, Consumer<FluidStack> callback) {
		super(capacity, callback);
	}

	@Override
	public boolean isFluidValid(FluidStack stack) {
		return SteamBoilerBlockEntity.isSteamStack(stack);
	}
}
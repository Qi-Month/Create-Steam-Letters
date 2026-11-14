package dev.celestiacraft.steam_letters.common.block.boiler;

import lombok.AllArgsConstructor;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 多方块锅炉的双槽处理器: 槽 0 的水只进不出, 槽 1 的蒸汽只出不进.
 * <p>
 * 水的注入与蒸汽的抽取因此都走同一份能力, 外部管道与流体物品无需区分锅炉内部的两个槽.
 */
@AllArgsConstructor
public class SteamBoilerTankHandler implements IFluidHandler {
	private final SteamBoilerBlockEntity boiler;

	@Override
	public int getTanks() {
		return 2;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return tank == SteamBoilerBlockEntity.TANK_STEAM
				? boiler.getSteamTank().getFluid()
				: boiler.getTankInventory().getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return tank == SteamBoilerBlockEntity.TANK_STEAM
				? boiler.getSteamTank().getCapacity()
				: boiler.getTankInventory().getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return tank == SteamBoilerBlockEntity.TANK_WATER && boiler.getTankInventory().isFluidValid(stack);
	}

	@Override
	public int fill(FluidStack stack, FluidAction action) {
		return boiler.getTankInventory().fill(stack, action);
	}

	@Override
	public @NotNull FluidStack drain(FluidStack stack, FluidAction action) {
		return boiler.getSteamTank().drain(stack, action);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return boiler.getSteamTank().drain(maxDrain, action);
	}
}
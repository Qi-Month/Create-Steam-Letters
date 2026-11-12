package dev.celestiacraft.steam_letters.common.block.boiler;

import lombok.Getter;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class SolarBoilerSmoothFluid {
	private static final float SMOOTHING = 0.25F;
	private static final float EPSILON = 1.0E-3F;

	private final FluidTank tank;
	private float level;
	private float previousLevel;
	private boolean initialised;
	@Getter
	private FluidStack rendered = FluidStack.EMPTY;

	public SolarBoilerSmoothFluid(FluidTank tank) {
		this.tank = tank;
	}

	public void tick() {
		FluidStack fluid = tank.getFluid();
		int capacity = tank.getCapacity();
		float target = capacity > 0 ? Mth.clamp(fluid.getAmount() / (float) capacity, 0.0F, 1.0F) : 0.0F;

		if (!fluid.isEmpty() && (rendered.isEmpty() || !rendered.getFluid().isSame(fluid.getFluid()))) {
			rendered = fluid.copy();
		}

		if (!initialised) {
			initialised = true;
			level = target;
			previousLevel = target;
			return;
		}

		previousLevel = level;
		level += (target - level) * SMOOTHING;

		if (Math.abs(target - level) < EPSILON) {
			level = target;
		}
	}

	public float getLevel(float partialTick) {
		return initialised ? Mth.lerp(partialTick, previousLevel, level) : 0.0F;
	}
}
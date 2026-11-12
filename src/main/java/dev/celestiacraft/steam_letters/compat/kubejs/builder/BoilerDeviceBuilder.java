package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.DoubleSupplier;

public abstract class BoilerDeviceBuilder<B extends Block, E extends BlockEntity, SELF extends DeviceBuilder<B, E, SELF>> extends DeviceBuilder<B, E, SELF> {
	protected Double production;

	protected BoilerDeviceBuilder(ResourceLocation id) {
		super(id);
	}

	@Info("Base steam output per tick at 100% efficiency (mB/tick)")
	public SELF production(double production) {
		this.production = production;
		return self();
	}

	protected DoubleSupplier productionOr(DoubleSupplier fallback) {
		return production == null ? fallback : production::doubleValue;
	}
}
package dev.celestiacraft.steam_letters.api.client.lang;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public interface SlLangSugar {
	void add(String key, String value);

	default void addItem(Item item, String name) {
		add(item.getDescriptionId(), name);
	}

	default void addItem(NonNullSupplier<? extends Item> item, String name) {
		addItem(item.get(), name);
	}

	default void addBlock(Block block, String name) {
		add(block.getDescriptionId(), name);
	}

	default void addBlock(NonNullSupplier<? extends Block> block, String name) {
		addBlock(block.get(), name);
	}

	default void addFluid(Fluid fluid, String name) {
		add(fluid.getFluidType().getDescriptionId(), name);
	}

	default void addFluid(NonNullSupplier<? extends Fluid> fluid, String name) {
		addFluid(fluid.get(), name);
	}
}
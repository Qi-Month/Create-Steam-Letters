package dev.celestiacraft.steam_letters.api.client.lang;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluid;

public class EnglishLangProvider extends RegistrateLangProvider {
	public EnglishLangProvider(AbstractRegistrate<?> owner, PackOutput output) {
		super(owner, output);
	}

	public void addFluid(Fluid fluid, String name) {
		add(fluid.getFluidType().getDescriptionId(), name);
	}

	public void addFluid(NonNullSupplier<? extends Fluid> fluid, String name) {
		addFluid(fluid.get(), name);
	}
}
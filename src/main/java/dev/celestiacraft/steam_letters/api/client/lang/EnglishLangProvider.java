package dev.celestiacraft.steam_letters.api.client.lang;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class EnglishLangProvider extends RegistrateLangProvider {
	public EnglishLangProvider(AbstractRegistrate<?> owner, PackOutput output) {
		super(owner, output);
	}

	public void addFluid(Fluid fluid, String name) {
		add(fluid.getFluidType().getDescriptionId(), name);
	}

	public void addFluid(Supplier<Fluid> fluid, String name) {
		addFluid(fluid.get(), name);
	}
}
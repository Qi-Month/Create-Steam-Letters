package dev.celestiacraft.steam_letters.api.client.lang;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ChineseLangProvider extends LanguageProvider implements RegistrateProvider {
	public static final ProviderType<ChineseLangProvider> TYPE;

	private final AbstractRegistrate<?> owner;

	public ChineseLangProvider(AbstractRegistrate<?> owner, PackOutput output) {
		super(output, owner.getModid(), "zh_cn");
		this.owner = owner;
	}

	static {
		TYPE = ProviderType.registerProvider("lang_zh_cn", (context) -> {
			return new ChineseLangProvider(context.parent(), context.output());
		});
	}

	@Override
	public LogicalSide getSide() {
		return LogicalSide.CLIENT;
	}

	@Override
	protected void addTranslations() {
		owner.genData(TYPE, this);
	}

	public void addFluid(Fluid fluid, String name) {
		add(fluid.getFluidType().getDescriptionId(), name);
	}

	public void addFluid(NonNullSupplier<? extends Fluid> fluid, String name) {
		addFluid(fluid.get(), name);
	}
}
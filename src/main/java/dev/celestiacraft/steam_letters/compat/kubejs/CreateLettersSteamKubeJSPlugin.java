package dev.celestiacraft.steam_letters.compat.kubejs;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.common.register.SlRecipeTypes;
import dev.celestiacraft.steam_letters.compat.kubejs.builder.BoilerBuilder;
import dev.celestiacraft.steam_letters.compat.kubejs.builder.FluidBurnerBuilder;
import dev.celestiacraft.steam_letters.compat.kubejs.builder.SolarBoilerBuilder;
import dev.celestiacraft.steam_letters.compat.kubejs.builder.SolidBurnerBuilder;
import dev.celestiacraft.steam_letters.compat.kubejs.builder.SteamEngineDeviceBuilder;
import dev.celestiacraft.steam_letters.compat.kubejs.schema.schema.BurnerSchema;
import dev.celestiacraft.steam_letters.compat.kubejs.schema.component.FuelFluidComponent;
import dev.celestiacraft.steam_letters.compat.kubejs.schema.component.FuelItemComponent;
import dev.celestiacraft.steam_letters.compat.kubejs.schema.component.FuelTimeComponent;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import net.minecraft.core.registries.Registries;

public class CreateLettersSteamKubeJSPlugin implements KubeJSPlugin {
	@Override
	public void registerBindings(BindingRegistry event) {
		event.add("CreateLettersSteam", CreateLettersSteam.class);
		event.add("SlApis", SlApis.class);
	}

	@Override
	public void registerBuilderTypes(BuilderTypeRegistry registry) {
		registry.of(Registries.BLOCK, (types) -> {
			types.add(SlApis.loadResource("boiler"), BoilerBuilder.class, BoilerBuilder::new);
			types.add(SlApis.loadResource("solar"), SolarBoilerBuilder.class, SolarBoilerBuilder::new);
			types.add(SlApis.loadResource("solid_burner"), SolidBurnerBuilder.class, SolidBurnerBuilder::new);
			types.add(SlApis.loadResource("fluid_burner"), FluidBurnerBuilder.class, FluidBurnerBuilder::new);
			// 引擎和飞轮永远成对注册, 只留一个类型, 注册id当前缀,
			// event.create("cmi:cast_iron")自动注册 cmi:cast_iron_steam_engine和cmi:cast_iron_steam_flywheel
			types.add(SlApis.loadResource("engine_and_flywheel"), SteamEngineDeviceBuilder.class, (id) -> {
				return new SteamEngineDeviceBuilder(SteamEngineDeviceBuilder.engineIdOf(id));
			});
		});
	}

	@Override
	public void registerRecipeComponents(RecipeComponentTypeRegistry registry) {
		registry.register(FuelFluidComponent.TYPE);
		registry.register(FuelItemComponent.TYPE);
		registry.register(FuelTimeComponent.TYPE);
	}

	@Override
	public void registerRecipeSchemas(RecipeSchemaRegistry event) {
		event.namespace(CreateLettersSteam.MODID)
				.register(getRecipeType(SlRecipeTypes.SOLID_FUEL), BurnerSchema.SOLID_FUEL)
				.register(getRecipeType(SlRecipeTypes.FLUID_FUEL), BurnerSchema.FLUID_FUEL);
	}

	private String getRecipeType(IRecipeTypeInfo recipe) {
		return recipe.getId().getPath();
	}
}
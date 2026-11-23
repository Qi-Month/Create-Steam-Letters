package dev.celestiacraft.steam_letters.compat.kubejs;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.common.register.SlRecipeTypes;
import dev.celestiacraft.steam_letters.compat.kubejs.builder.*;
import dev.celestiacraft.steam_letters.compat.kubejs.recipe.schema.BurnerSchema;
import dev.celestiacraft.steam_letters.compat.kubejs.recipe.FuelFluid;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class CreateLettersSteamKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void init() {
		addType();
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("CreateLettersSteam", CreateLettersSteam.class);
		event.add("SlApis", SlApis.class);
		event.add("FuelFluid", FuelFluid.class);
	}

	private void addType() {
		RegistryInfo.BLOCK.addType(
				SlApis.loadResource("boiler").toString(),
				BoilerBuilder.class,
				BoilerBuilder::new
		);
		RegistryInfo.BLOCK.addType(
				SlApis.loadResource("solar").toString(),
				SolarBoilerBuilder.class,
				SolarBoilerBuilder::new
		);
		RegistryInfo.BLOCK.addType(
				SlApis.loadResource("solid_burner").toString(),
				SolidBurnerBuilder.class,
				SolidBurnerBuilder::new
		);
		RegistryInfo.BLOCK.addType(
				SlApis.loadResource("fluid_burner").toString(),
				FluidBurnerBuilder.class,
				FluidBurnerBuilder::new
		);
		// 引擎和飞轮永远成对注册, 只留一个类型, 注册id当前缀,
		// event.create("cmi:cast_iron")自动注册 cmi:cast_iron_steam_engine和cmi:cast_iron_steam_flywheel
		RegistryInfo.BLOCK.addType(
				SlApis.loadResource("engine_and_flywheel").toString(),
				SteamEngineDeviceBuilder.class,
				(id) -> {
					return new SteamEngineDeviceBuilder(SteamEngineDeviceBuilder.engineIdOf(id));
				}
		);
	}

	@Override
	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
		event.namespace(CreateLettersSteam.MODID)
				.register(getRecipeType(SlRecipeTypes.SOLID_FUEL), BurnerSchema.SOLID_FUEL)
				.register(getRecipeType(SlRecipeTypes.FLUID_FUEL), BurnerSchema.FLUID_FUEL);
	}

	private String getRecipeType(IRecipeTypeInfo recipe) {
		return recipe.getId().getPath();
	}
}
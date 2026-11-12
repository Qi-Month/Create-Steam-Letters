package dev.celestiacraft.steam_letters.common.register;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.common.recipe.FuelRecipeSerializer;
import dev.celestiacraft.steam_letters.common.recipe.SimpleRecipeTypeInfo;
import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import dev.celestiacraft.steam_letters.common.recipe.type.SolidFuelRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SlRecipeTypes {
	public static final DeferredRegister<RecipeType<?>> TYPES;
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;

	public static final DeferredHolder<RecipeType<?>, RecipeType<SolidFuelRecipe>> SOLID_FUEL;
	public static final DeferredHolder<RecipeSerializer<?>, FuelRecipeSerializer<SolidFuelRecipe>> SOLID_FUEL_SERIALIZER;
	public static final IRecipeTypeInfo SOLID_FUEL_INFO;

	public static final DeferredHolder<RecipeType<?>, RecipeType<FluidFuelRecipe>> FLUID_FUEL;
	public static final DeferredHolder<RecipeSerializer<?>, FuelRecipeSerializer<FluidFuelRecipe>> FLUID_FUEL_SERIALIZER;
	public static final IRecipeTypeInfo FLUID_FUEL_INFO;

	static {
		TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, CreateLettersSteam.MODID);
		SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, CreateLettersSteam.MODID);

		SOLID_FUEL = TYPES.register("solid_fuel", () -> {
			return RecipeType.simple(SlApis.loadResource("solid_fuel"));
		});
		SOLID_FUEL_SERIALIZER = SERIALIZERS.register("solid_fuel", () -> {
			return new FuelRecipeSerializer<>(SolidFuelRecipe::new);
		});
		SOLID_FUEL_INFO = typeInfo(SlApis.loadResource("solid_fuel"), SOLID_FUEL, SOLID_FUEL_SERIALIZER);

		FLUID_FUEL = TYPES.register("fluid_fuel", () -> {
			return RecipeType.simple(SlApis.loadResource("fluid_fuel"));
		});
		FLUID_FUEL_SERIALIZER = SERIALIZERS.register("fluid_fuel", () -> {
			return new FuelRecipeSerializer<>(FluidFuelRecipe::new);
		});
		FLUID_FUEL_INFO = typeInfo(SlApis.loadResource("fluid_fuel"), FLUID_FUEL, FLUID_FUEL_SERIALIZER);
	}

	private static IRecipeTypeInfo typeInfo(ResourceLocation id, Supplier<? extends RecipeType<?>> type, Supplier<? extends RecipeSerializer<?>> serializer) {
		return new SimpleRecipeTypeInfo(id, type, serializer);
	}

	public static void init(IEventBus bus) {
		TYPES.register(bus);
		SERIALIZERS.register(bus);
	}
}

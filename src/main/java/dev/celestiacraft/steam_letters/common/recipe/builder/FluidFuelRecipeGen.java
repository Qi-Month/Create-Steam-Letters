package dev.celestiacraft.steam_letters.common.recipe.builder;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import dev.celestiacraft.steam_letters.common.recipe.type.FuelRecipeParams;
import dev.celestiacraft.steam_letters.common.register.SlRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public abstract class FluidFuelRecipeGen extends ProcessingRecipeGen<FuelRecipeParams, FluidFuelRecipe, FuelRecipeBuilder<FluidFuelRecipe>> {
	protected FluidFuelRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
		super(output, registries, defaultNamespace);
	}

	@Override
	protected @NotNull IRecipeTypeInfo getRecipeType() {
		return SlRecipeTypes.FLUID_FUEL;
	}

	@Override
	protected FuelRecipeBuilder<FluidFuelRecipe> getBuilder(ResourceLocation id) {
		return new FuelRecipeBuilder<>(FluidFuelRecipe::new, id);
	}

	protected GeneratedRecipe recipe(String name, @NotNull UnaryOperator<FluidFuelBuilder> transform) {
		return create(name, (FuelRecipeBuilder<FluidFuelRecipe> builder) -> {
			return transform.apply(new FluidFuelBuilder(builder)).unwrap();
		});
	}

	public static class FluidFuelBuilder {
		private final FuelRecipeBuilder<FluidFuelRecipe> builder;
		private boolean superheated;

		private FluidFuelBuilder(@NotNull FuelRecipeBuilder<FluidFuelRecipe> builder) {
			this.builder = builder;
		}

		public FluidFuelBuilder fluid(Fluid fluid) {
			return ingredient(SizedFluidIngredient.of(fluid, FluidFuelRecipe.FUEL_AMOUNT));
		}

		public FluidFuelBuilder fluid(ResourceLocation fluid) {
			return fluid(BuiltInRegistries.FLUID.get(fluid));
		}

		public FluidFuelBuilder fluid(String fluid) {
			return fluid(BuiltInRegistries.FLUID.get(ResourceLocation.parse(fluid)));
		}

		public FluidFuelBuilder fluidTag(TagKey<Fluid> tag) {
			return ingredient(SizedFluidIngredient.of(tag, FluidFuelRecipe.FUEL_AMOUNT));
		}

		public FluidFuelBuilder fluidTag(ResourceLocation tag) {
			return fluidTag(FluidTags.create(tag));
		}

		public FluidFuelBuilder fluidTag(String tag) {
			return fluidTag(FluidTags.create(ResourceLocation.parse(tag)));
		}

		private FluidFuelBuilder ingredient(@NotNull SizedFluidIngredient ingredient) {
			builder.withFluidIngredients(ingredient);
			return this;
		}

		public FluidFuelBuilder time(int ticks) {
			builder.duration(ticks);
			return this;
		}

		public FluidFuelBuilder timeSeconds(int seconds) {
			time(20 * seconds);
			return this;
		}

		public FluidFuelBuilder superheated(boolean superheated) {
			this.superheated = superheated;
			return this;
		}

		public FluidFuelBuilder superheated() {
			superheated = true;
			return this;
		}

		public FluidFuelBuilder whenModLoaded(@NotNull String modid) {
			builder.whenModLoaded(modid);
			return this;
		}

		public FluidFuelBuilder whenModMissing(@NotNull String modid) {
			builder.whenModMissing(modid);
			return this;
		}

		public FluidFuelBuilder withCondition(@NotNull ICondition condition) {
			builder.withCondition(condition);
			return this;
		}

		private FuelRecipeBuilder<FluidFuelRecipe> unwrap() {
			FluidFuelRecipe.markNextSuperheated(superheated);
			return builder;
		}
	}
}
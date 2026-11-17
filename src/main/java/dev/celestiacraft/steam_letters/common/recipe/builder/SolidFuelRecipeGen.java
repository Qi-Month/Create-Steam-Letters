package dev.celestiacraft.steam_letters.common.recipe.builder;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.celestiacraft.steam_letters.common.recipe.type.FuelRecipeParams;
import dev.celestiacraft.steam_letters.common.recipe.type.SolidFuelRecipe;
import dev.celestiacraft.steam_letters.common.register.SlRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public abstract class SolidFuelRecipeGen extends ProcessingRecipeGen<FuelRecipeParams, SolidFuelRecipe, FuelRecipeBuilder<SolidFuelRecipe>> {
	protected SolidFuelRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
		super(output, registries, defaultNamespace);
	}

	@Override
	protected @NotNull IRecipeTypeInfo getRecipeType() {
		return SlRecipeTypes.SOLID_FUEL;
	}

	@Override
	protected FuelRecipeBuilder<SolidFuelRecipe> getBuilder(ResourceLocation id) {
		return new FuelRecipeBuilder<>(SolidFuelRecipe::new, id);
	}

	protected GeneratedRecipe recipe(String name, @NotNull UnaryOperator<SolidFuelBuilder> transform) {
		return create(name, (FuelRecipeBuilder<SolidFuelRecipe> builder) -> {
			return transform.apply(new SolidFuelBuilder(builder)).unwrap();
		});
	}

	public static class SolidFuelBuilder {
		private final FuelRecipeBuilder<SolidFuelRecipe> builder;
		private boolean superheated;

		private SolidFuelBuilder(@NotNull FuelRecipeBuilder<SolidFuelRecipe> builder) {
			this.builder = builder;
		}

		public SolidFuelBuilder item(ItemLike item) {
			return ingredient(Ingredient.of(item));
		}

		public SolidFuelBuilder item(ResourceLocation item) {
			return item(BuiltInRegistries.ITEM.get(item));
		}

		public SolidFuelBuilder item(String item) {
			return item(BuiltInRegistries.ITEM.get(ResourceLocation.parse(item)));
		}

		public SolidFuelBuilder itemTag(TagKey<Item> tag) {
			return ingredient(Ingredient.of(tag));
		}

		public SolidFuelBuilder itemTag(ResourceLocation tag) {
			return itemTag(ItemTags.create(tag));
		}

		public SolidFuelBuilder itemTag(String tag) {
			return itemTag(ItemTags.create(ResourceLocation.parse(tag)));
		}

		public SolidFuelBuilder ingredient(@NotNull Ingredient ingredient) {
			builder.withItemIngredients(ingredient);
			return this;
		}

		public SolidFuelBuilder time(int ticks) {
			builder.duration(ticks);
			return this;
		}

		public SolidFuelBuilder timeSeconds(int seconds) {
			time(20 * seconds);
			return this;
		}

		public SolidFuelBuilder timeItem(int item) {
			time(200 * item);
			return this;
		}

		public SolidFuelBuilder superheated(boolean superheated) {
			this.superheated = superheated;
			return this;
		}

		public SolidFuelBuilder superheated() {
			superheated = true;
			return this;
		}

		public SolidFuelBuilder whenModLoaded(@NotNull String modid) {
			builder.whenModLoaded(modid);
			return this;
		}

		public SolidFuelBuilder whenModMissing(@NotNull String modid) {
			builder.whenModMissing(modid);
			return this;
		}

		public SolidFuelBuilder withCondition(@NotNull ICondition condition) {
			builder.withCondition(condition);
			return this;
		}

		private FuelRecipeBuilder<SolidFuelRecipe> unwrap() {
			SolidFuelRecipe.markNextSuperheated(superheated);
			return builder;
		}
	}
}
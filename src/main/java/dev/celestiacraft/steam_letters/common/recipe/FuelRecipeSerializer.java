package dev.celestiacraft.steam_letters.common.recipe;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import dev.celestiacraft.steam_letters.common.recipe.type.FuelRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class FuelRecipeSerializer<R extends ProcessingRecipe<RecipeWrapper, FuelRecipeParams>> implements RecipeSerializer<R> {
	private final ProcessingRecipe.Factory<FuelRecipeParams, R> factory;
	private final MapCodec<R> codec;
	private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

	public FuelRecipeSerializer(ProcessingRecipe.Factory<FuelRecipeParams, R> factory) {
		this.factory = factory;
		codec = ProcessingRecipe.codec(factory, FuelRecipeParams.CODEC);
		streamCodec = ProcessingRecipe.streamCodec(factory, FuelRecipeParams.STREAM_CODEC);
	}

	@Override
	public @NotNull MapCodec<R> codec() {
		return codec;
	}

	@Override
	public @NotNull StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
		return streamCodec;
	}

	public ProcessingRecipe.Factory<FuelRecipeParams, R> factory() {
		return factory;
	}
}
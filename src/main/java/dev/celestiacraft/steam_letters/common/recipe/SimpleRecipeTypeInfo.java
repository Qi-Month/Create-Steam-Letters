package dev.celestiacraft.steam_letters.common.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

@AllArgsConstructor
public class SimpleRecipeTypeInfo<R extends Recipe<?>> implements IRecipeTypeInfo {
	@Getter
	private final ResourceLocation id;
	private final DeferredHolder<RecipeType<?>, RecipeType<R>> type;
	private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> serializer;

	public RecipeType<R> get() {
		return type.get();
	}

	@Override
	public <T extends RecipeSerializer<?>> T getSerializer() {
		return (T) serializer.get();
	}

	@Override
	public <I extends RecipeInput, T extends Recipe<I>> RecipeType<T> getType() {
		return (RecipeType<T>) type.get();
	}

	public IRecipeTypeInfo getInfo() {
		return this;
	}
}
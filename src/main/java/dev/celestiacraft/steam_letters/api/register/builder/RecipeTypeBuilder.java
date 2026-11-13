package dev.celestiacraft.steam_letters.api.register.builder;

import dev.celestiacraft.steam_letters.api.register.SlRegistrate;
import dev.celestiacraft.steam_letters.common.recipe.SimpleRecipeTypeInfo;
import lombok.AllArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

@AllArgsConstructor
public class RecipeTypeBuilder<R extends Recipe<?>> {
	private final SlRegistrate owner;
	private final String name;
	private final Supplier<RecipeSerializer<R>> serializer;

	public SimpleRecipeTypeInfo<R> register() {
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(owner.getModid(), name);
		DeferredHolder<RecipeType<?>, RecipeType<R>> type = owner.registerRecipeType(name, () -> {
			return RecipeType.simple(id);
		});
		DeferredHolder<RecipeSerializer<?>, RecipeSerializer<R>> entry = owner.registerRecipeSerializer(name, serializer);
		return new SimpleRecipeTypeInfo<>(id, type, entry);
	}
}
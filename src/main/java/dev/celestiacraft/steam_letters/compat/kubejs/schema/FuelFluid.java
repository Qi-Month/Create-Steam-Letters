package dev.celestiacraft.steam_letters.compat.kubejs.schema;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.codec.CreateCodecs;
import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 流体燃料在脚本里的入参类型.
 * <p>
 * 同时表示"某一种流体"和"某个流体标签", 二选一, 由 {@link #isTag()} 区分. 写进配方 JSON 时总是转成
 * {@link SizedFluidIngredient}, 数量固定为 {@link FluidFuelRecipe#FUEL_AMOUNT}.
 * <p>
 * 脚本里可以直接传字符串 ({@code "minecraft:lava"} / {@code "#minecraft:lava"}), 也可以传
 * {@code FluidStack} / {@code FluidIngredient} / {@link FuelFluid} 本身.
 */
@Getter
public class FuelFluid {
	/**
	 * 与配方 codec 一致的序列化方式, 保证 KubeJS 生成的 JSON 能被 {@link FluidFuelRecipe} 解析.
	 */
	public static final Codec<FuelFluid> CODEC = CreateCodecs.FLAT_SIZED_FLUID_INGREDIENT_WITH_TYPE.xmap(
			(sized) -> {
				return fromIngredient(sized.ingredient());
			}, (fuel) -> {
				return new SizedFluidIngredient(fuel.toIngredient(), FluidFuelRecipe.FUEL_AMOUNT);
			}
	);

	private final ResourceLocation fluid;
	private final ResourceLocation tag;

	private FuelFluid(ResourceLocation fluid, ResourceLocation tag) {
		this.fluid = fluid;
		this.tag = tag;
	}

	@HideFromJS
	public static @NotNull FuelFluid of(@Nullable Object from) {
		if (from instanceof FuelFluid fuel) {
			return fuel;
		}

		if (from instanceof JsonElement element) {
			return of(element.getAsString());
		}

		if (from instanceof SizedFluidIngredient sized) {
			return fromIngredient(sized.ingredient());
		}

		if (from instanceof FluidIngredient ingredient) {
			return fromIngredient(ingredient);
		}

		if (from instanceof FluidStack stack) {
			return ofFluid(BuiltInRegistries.FLUID.getKey(stack.getFluid()));
		}

		if (from instanceof Fluid fluid) {
			return ofFluid(BuiltInRegistries.FLUID.getKey(fluid));
		}

		if (from == null) {
			throw new IllegalArgumentException("fluid fuel can not be null");
		}

		String value = from.toString();
		if (!value.isEmpty() && value.charAt(0) == '#') {
			ResourceLocation tag = ResourceLocation.tryParse(value.substring(1));
			if (tag == null) {
				throw new IllegalArgumentException("invalid fluid tag '" + value + "'");
			}
			return ofTag(tag);
		}

		ResourceLocation fluid = ResourceLocation.tryParse(value);
		if (fluid == null) {
			throw new IllegalArgumentException("invalid fluid '" + value + "'");
		}
		return ofFluid(fluid);
	}

	public static @NotNull FuelFluid ofFluid(@NotNull ResourceLocation fluid) {
		return new FuelFluid(fluid, null);
	}

	public static @NotNull FuelFluid ofTag(@NotNull ResourceLocation tag) {
		return new FuelFluid(null, tag);
	}

	@HideFromJS
	public static @NotNull FuelFluid fromIngredient(@NotNull FluidIngredient ingredient) {
		if (ingredient instanceof TagFluidIngredient tagIngredient) {
			return ofTag(tagIngredient.tag().location());
		}

		FluidStack[] stacks = ingredient.getStacks();
		if (stacks.length != 1) {
			throw new IllegalArgumentException("fluid fuel expects exactly one fluid, got " + stacks.length);
		}
		return ofFluid(BuiltInRegistries.FLUID.getKey(stacks[0].getFluid()));
	}

	public boolean isTag() {
		return tag != null;
	}

	@HideFromJS
	public @NotNull FluidIngredient toIngredient() {
		if (isTag()) {
			return new TagFluidIngredient(TagKey.create(Registries.FLUID, tag));
		}

		return FluidIngredient.single(BuiltInRegistries.FLUID.getHolder(fluid)
				.orElseThrow(() -> {
					return new IllegalArgumentException("unknown fluid '" + fluid + "'");
				}));
	}

	@HideFromJS
	public @NotNull SizedFluidIngredient toSized() {
		return new SizedFluidIngredient(toIngredient(), FluidFuelRecipe.FUEL_AMOUNT);
	}

	public @NotNull JsonElement toJson() {
		if (isTag()) {
			return new JsonPrimitive("#" + tag);
		}
		return new JsonPrimitive(fluid.toString());
	}

	@Override
	public String toString() {
		return isTag() ? "#" + tag : fluid.toString();
	}
}
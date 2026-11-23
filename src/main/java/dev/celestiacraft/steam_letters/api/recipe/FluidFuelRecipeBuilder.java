package dev.celestiacraft.steam_letters.api.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * 流体燃料配方的代码写法, 链式 API 与 datagen 侧 {@code FluidFuelRecipeGen#FluidFuelBuilder} 同名同义.
 * <p>
 * 一条配方吃一种流体, 默认每次燃烧消耗 1000 mB.
 */
public class FluidFuelRecipeBuilder {
	private final ResourceLocation id;
	private final ProcessingRecipeBuilder<FluidFuelRecipe> builder;
	private final List<BooleanSupplier> conditions = new ArrayList<>();
	private boolean superheated;

	public FluidFuelRecipeBuilder(ResourceLocation id) {
		this.id = id;
		builder = new ProcessingRecipeBuilder<>(FluidFuelRecipe::new, id);
	}

	public FluidFuelRecipeBuilder fluid(Fluid fluid) {
		return ingredient(FluidIngredient.fromFluid(fluid, FluidFuelRecipe.FUEL_AMOUNT));
	}

	public FluidFuelRecipeBuilder fluid(ResourceLocation fluid) {
		return fluid(BuiltInRegistries.FLUID.get(fluid));
	}

	public FluidFuelRecipeBuilder fluid(String fluid) {
		return fluid(parse(fluid));
	}

	public FluidFuelRecipeBuilder fluidTag(TagKey<Fluid> tag) {
		return ingredient(FluidIngredient.fromTag(tag, FluidFuelRecipe.FUEL_AMOUNT));
	}

	public FluidFuelRecipeBuilder fluidTag(ResourceLocation tag) {
		return fluidTag(FluidTags.create(tag));
	}

	public FluidFuelRecipeBuilder fluidTag(String tag) {
		return fluidTag(parse(tag));
	}

	public FluidFuelRecipeBuilder ingredient(FluidIngredient ingredient) {
		builder.withFluidIngredients(ingredient);
		return this;
	}

	/**
	 * 燃烧时长, 单位 tick.
	 */
	public FluidFuelRecipeBuilder time(int ticks) {
		builder.duration(ticks);
		return this;
	}

	public FluidFuelRecipeBuilder timeSeconds(int seconds) {
		return time(20 * seconds);
	}

	public FluidFuelRecipeBuilder superheated(boolean superheated) {
		this.superheated = superheated;
		return this;
	}

	public FluidFuelRecipeBuilder superheated() {
		return superheated(true);
	}

	/**
	 * 附加条件, 全部通过才注入. 条件在每次重载时重新求值.
	 */
	private FluidFuelRecipeBuilder when(BooleanSupplier condition) {
		conditions.add(condition);
		return this;
	}

	/**
	 * 用配方条件判断是否注入, 与 datagen 侧 {@code withCondition} 同名同义.
	 */
	public FluidFuelRecipeBuilder whenCondition(ICondition condition) {
		return when(() -> {
			return SlRecipeBuilders.test(condition);
		});
	}

	public FluidFuelRecipeBuilder whenModLoaded(String modid) {
		return when(() -> {
			return ModList.get().isLoaded(modid);
		});
	}

	public FluidFuelRecipeBuilder whenModMissing(String modid) {
		return when(() -> {
			return !ModList.get().isLoaded(modid);
		});
	}

	public FluidFuelRecipe build() {
		FluidFuelRecipe recipe = builder.build();
		recipe.setSuperheated(superheated);

		List<String> errors = new ArrayList<>();
		if (recipe.ingredient() == null) {
			errors.add("no fluid ingredient was given.");
		}
		SlRecipeBuilders.report(id, errors);

		return recipe;
	}

	/**
	 * 提交给 {@link SlRecipeBuilders}. 服务器已在跑时当场生效, 否则等下一次资源重载.
	 */
	public FluidFuelRecipe register() {
		return SlRecipeBuilders.register(build(), conditions);
	}

	private static ResourceLocation parse(String value) {
		ResourceLocation id = ResourceLocation.tryParse(value);
		if (id == null) {
			throw new IllegalArgumentException("invalid resource location: " + value);
		}

		return id;
	}
}
package dev.celestiacraft.steam_letters.api.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import dev.celestiacraft.steam_letters.common.recipe.type.FluidFuelRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * 流体燃料配方的代码写法, 链式 API 与 datagen 侧 {@code FluidFuelRecipeGen#FluidFuelBuilder} 同名同义.
 * <p>
 * 一条配方吃一种流体, 默认每次燃烧消耗 1000 mB.
 * <p>
 * 原料只记下取法, 等配方真正构造时才解析, 因此在模组构造期直接写引用注册表对象的配方也是安全的.
 */
public class FluidFuelRecipeBuilder {
	private final ResourceLocation id;
	private final List<BooleanSupplier> conditions = new ArrayList<>();
	private Supplier<FluidIngredient> ingredient;
	private Integer duration;
	private boolean superheated;

	public FluidFuelRecipeBuilder(ResourceLocation id) {
		this.id = id;
	}

	public FluidFuelRecipeBuilder fluid(Fluid fluid) {
		ingredient = () -> {
			return FluidIngredient.fromFluid(fluid, FluidFuelRecipe.FUEL_AMOUNT);
		};
		return this;
	}

	public FluidFuelRecipeBuilder fluid(Supplier<? extends Fluid> fluid) {
		ingredient = () -> {
			return FluidIngredient.fromFluid(fluid.get(), FluidFuelRecipe.FUEL_AMOUNT);
		};
		return this;
	}

	public FluidFuelRecipeBuilder fluid(ResourceLocation fluid) {
		ingredient = () -> {
			return FluidIngredient.fromFluid(ForgeRegistries.FLUIDS.getValue(fluid), FluidFuelRecipe.FUEL_AMOUNT);
		};
		return this;
	}

	public FluidFuelRecipeBuilder fluid(String fluid) {
		return fluid(parse(fluid));
	}

	public FluidFuelRecipeBuilder fluidTag(TagKey<Fluid> tag) {
		ingredient = () -> {
			return FluidIngredient.fromTag(tag, FluidFuelRecipe.FUEL_AMOUNT);
		};
		return this;
	}

	public FluidFuelRecipeBuilder fluidTag(ResourceLocation tag) {
		return fluidTag(FluidTags.create(tag));
	}

	public FluidFuelRecipeBuilder fluidTag(String tag) {
		return fluidTag(parse(tag));
	}

	/**
	 * 直接给原料. 传进来的 {@link FluidIngredient} 必须已经可用, 需要按 id 或实例延迟解析时改用 fluid / fluidTag.
	 */
	public FluidFuelRecipeBuilder ingredient(FluidIngredient ingredient) {
		this.ingredient = () -> {
			return ingredient;
		};
		return this;
	}

	/**
	 * 燃烧时长, 单位 tick.
	 */
	public FluidFuelRecipeBuilder time(int ticks) {
		duration = ticks;
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

	/**
	 * 立即构造配方. 需要查注册表的原料在这里才解析, 所以调用点必须已经越过注册阶段.
	 */
	public FluidFuelRecipe build() {
		ProcessingRecipeBuilder<FluidFuelRecipe> builder = new ProcessingRecipeBuilder<>(FluidFuelRecipe::new, id);
		if (ingredient != null) {
			builder.withFluidIngredients(ingredient.get());
		}
		if (duration != null) {
			builder.duration(duration);
		}

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
	 * 提交给 {@link SlRecipeBuilders}: 只登记取法, 配方等下一次服务端重载时才构造.
	 */
	public ResourceLocation register() {
		return SlRecipeBuilders.register(id, this::build, conditions);
	}

	private static ResourceLocation parse(String value) {
		ResourceLocation id = ResourceLocation.tryParse(value);
		if (id == null) {
			throw new IllegalArgumentException("invalid resource location: " + value);
		}

		return id;
	}
}
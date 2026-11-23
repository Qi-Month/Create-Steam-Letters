package dev.celestiacraft.steam_letters.api.recipe;

import dev.celestiacraft.steam_letters.common.recipe.builder.FuelRecipeBuilder;
import dev.celestiacraft.steam_letters.common.recipe.type.SolidFuelRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * 固体燃料配方的代码写法, 链式 API 与 datagen 侧 {@code SolidFuelRecipeGen#SolidFuelBuilder} 同名同义.
 * <p>
 * 一条配方只吃一种物品, 时长以 tick 计. 不给时长时按 200 tick (一个物品) 处理, 与 datagen 默认一致.
 */
public class SolidFuelRecipeBuilder {
	private static final int TICKS_PER_ITEM = 200;

	private final ResourceLocation id;
	private final FuelRecipeBuilder<SolidFuelRecipe> builder;
	private final List<BooleanSupplier> conditions = new ArrayList<>();
	private boolean superheated;

	public SolidFuelRecipeBuilder(ResourceLocation id) {
		this.id = id;
		builder = new FuelRecipeBuilder<>(SolidFuelRecipe::new, id);
	}

	public SolidFuelRecipeBuilder item(ItemLike item) {
		return ingredient(Ingredient.of(item));
	}

	public SolidFuelRecipeBuilder item(ResourceLocation item) {
		return item(BuiltInRegistries.ITEM.get(item));
	}

	public SolidFuelRecipeBuilder item(String item) {
		return item(ResourceLocation.parse(item));
	}

	public SolidFuelRecipeBuilder itemTag(TagKey<Item> tag) {
		return ingredient(Ingredient.of(tag));
	}

	public SolidFuelRecipeBuilder itemTag(ResourceLocation tag) {
		return itemTag(ItemTags.create(tag));
	}

	public SolidFuelRecipeBuilder itemTag(String tag) {
		return itemTag(ResourceLocation.parse(tag));
	}

	public SolidFuelRecipeBuilder ingredient(Ingredient ingredient) {
		builder.withItemIngredients(ingredient);
		return this;
	}

	/**
	 * 燃烧时长, 单位 tick.
	 */
	public SolidFuelRecipeBuilder time(int ticks) {
		builder.duration(ticks);
		return this;
	}

	public SolidFuelRecipeBuilder timeSeconds(int seconds) {
		return time(20 * seconds);
	}

	/**
	 * 按物品数计时, 每份 {@value #TICKS_PER_ITEM} tick.
	 */
	public SolidFuelRecipeBuilder timeItem(int items) {
		return time(TICKS_PER_ITEM * items);
	}

	public SolidFuelRecipeBuilder superheated(boolean superheated) {
		this.superheated = superheated;
		return this;
	}

	public SolidFuelRecipeBuilder superheated() {
		return superheated(true);
	}

	/**
	 * 附加条件, 全部通过才注入. 条件在每次重载时重新求值, 因此调用方应传纯函数式判断.
	 */
	private SolidFuelRecipeBuilder when(BooleanSupplier condition) {
		conditions.add(condition);
		return this;
	}

	/**
	 * 用配方条件判断是否注入, 与 datagen 侧 {@code withCondition} 同名同义.
	 */
	public SolidFuelRecipeBuilder whenCondition(ICondition condition) {
		return when(() -> {
			return SlRecipeBuilders.test(condition);
		});
	}

	public SolidFuelRecipeBuilder whenModLoaded(String modid) {
		return when(() -> {
			return ModList.get().isLoaded(modid);
		});
	}

	public SolidFuelRecipeBuilder whenModMissing(String modid) {
		return when(() -> {
			return !ModList.get().isLoaded(modid);
		});
	}

	public RecipeHolder<SolidFuelRecipe> build() {
		SolidFuelRecipe recipe = builder.build();
		recipe.setSuperheated(superheated);

		List<String> errors = new ArrayList<>(recipe.validate());
		if (recipe.ingredient() == null) {
			errors.add("no item ingredient was given.");
		}
		SlRecipeBuilders.report(id, errors);

		return new RecipeHolder<>(id, recipe);
	}

	/**
	 * 提交给 {@link SlRecipeBuilders}. 服务器已在跑时当场生效, 否则等下一次资源重载.
	 */
	public RecipeHolder<SolidFuelRecipe> register() {
		return SlRecipeBuilders.register(build(), conditions);
	}
}
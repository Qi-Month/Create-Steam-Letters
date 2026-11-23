package dev.celestiacraft.steam_letters.api.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import dev.celestiacraft.steam_letters.common.recipe.type.SolidFuelRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * 固体燃料配方的代码写法, 链式 API 与 datagen 侧 {@code SolidFuelRecipeGen#SolidFuelBuilder} 同名同义.
 * <p>
 * 一条配方只吃一种物品, 时长以 tick 计. 不给时长时按 200 tick (一个物品) 处理, 与 datagen 默认一致.
 * <p>
 * 原料只记下取法, 等配方真正构造时才解析, 因此在模组构造期直接写引用注册表对象的配方也是安全的.
 */
public class SolidFuelRecipeBuilder {
	private static final int TICKS_PER_ITEM = 200;

	private final ResourceLocation id;
	private final List<BooleanSupplier> conditions = new ArrayList<>();
	private Supplier<Ingredient> ingredient;
	private Integer duration;
	private boolean superheated;

	public SolidFuelRecipeBuilder(ResourceLocation id) {
		this.id = id;
	}

	public SolidFuelRecipeBuilder item(ItemLike item) {
		ingredient = () -> {
			return Ingredient.of(item);
		};
		return this;
	}

	public SolidFuelRecipeBuilder item(ResourceLocation item) {
		ingredient = () -> {
			return Ingredient.of(ForgeRegistries.ITEMS.getValue(item));
		};
		return this;
	}

	public SolidFuelRecipeBuilder item(String item) {
		return item(parse(item));
	}

	public SolidFuelRecipeBuilder itemTag(TagKey<Item> tag) {
		ingredient = () -> {
			return Ingredient.of(tag);
		};
		return this;
	}

	public SolidFuelRecipeBuilder itemTag(ResourceLocation tag) {
		return itemTag(ItemTags.create(tag));
	}

	public SolidFuelRecipeBuilder itemTag(String tag) {
		return itemTag(parse(tag));
	}

	/**
	 * 直接给原料. 传进来的 {@link Ingredient} 必须已经可用, 需要按 id 或实例延迟解析时改用 item / itemTag.
	 */
	public SolidFuelRecipeBuilder ingredient(Ingredient ingredient) {
		this.ingredient = () -> {
			return ingredient;
		};
		return this;
	}

	/**
	 * 燃烧时长, 单位 tick.
	 */
	public SolidFuelRecipeBuilder time(int ticks) {
		duration = ticks;
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

	/**
	 * 立即构造配方. 需要查注册表的原料在这里才解析, 所以调用点必须已经越过注册阶段.
	 */
	public SolidFuelRecipe build() {
		ProcessingRecipeBuilder<SolidFuelRecipe> builder = new ProcessingRecipeBuilder<>(SolidFuelRecipe::new, id);
		if (ingredient != null) {
			builder.withItemIngredients(ingredient.get());
		}
		if (duration != null) {
			builder.duration(duration);
		}

		SolidFuelRecipe recipe = builder.build();
		recipe.setSuperheated(superheated);

		List<String> errors = new ArrayList<>();
		if (recipe.ingredient() == null) {
			errors.add("no item ingredient was given.");
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
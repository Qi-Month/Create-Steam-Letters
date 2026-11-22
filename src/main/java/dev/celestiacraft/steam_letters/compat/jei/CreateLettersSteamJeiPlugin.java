package dev.celestiacraft.steam_letters.compat.jei;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.common.block.burner.FluidBurnerBlock;
import dev.celestiacraft.steam_letters.common.block.burner.SolidBurnerBlock;
import dev.celestiacraft.steam_letters.common.register.SlRecipeTypes;
import dev.celestiacraft.steam_letters.common.recipe.type.SolidFuelRecipe;
import dev.celestiacraft.steam_letters.common.register.SlBlocks;
import dev.celestiacraft.steam_letters.compat.jei.api.JeiRecipeType;
import dev.celestiacraft.steam_letters.compat.jei.category.FluidFuelBurnerCategory;
import dev.celestiacraft.steam_letters.compat.jei.category.SolidFuelBurnerCategory;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@JeiPlugin
public class CreateLettersSteamJeiPlugin implements IModPlugin {
	private IJeiHelpers helpers;
	private IRecipeCatalystRegistration registration;

	@Override
	public @NotNull ResourceLocation getPluginUid() {
		return SlApis.loadResource("jei_plugin");
	}

	@Override
	public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
		IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();

		registration.addRecipeCategories(
				new SolidFuelBurnerCategory(
						JeiRecipeType.SOLID_FUEL,
						Component.translatable(SlTranKeys.langKey(SlTranKeys.JEI_SOLID_FUEL)),
						helper.createDrawableItemStack(Items.COAL.getDefaultInstance()),
						helper
				),
				new FluidFuelBurnerCategory(
						JeiRecipeType.FLUID_FUEL,
						Component.translatable(SlTranKeys.langKey(SlTranKeys.JEI_FLUID_FUEL)),
						helper.createDrawableItemStack(Items.LAVA_BUCKET.getDefaultInstance()),
						helper
				)
		);
	}

	@Override
	public void registerRecipes(@NotNull IRecipeRegistration registration) {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (connection == null) {
			return;
		}

		// 使用同步到客户端的配方, 包括在专用服务器上
		RecipeManager manager = connection.getRecipeManager();

		List<SolidFuelRecipe> solid = new ArrayList<>(manager.getAllRecipesFor(SlRecipeTypes.SOLID_FUEL.get()));
		solid.addAll(collectFurnaceFuels(solid));
		registration.addRecipes(JeiRecipeType.SOLID_FUEL, solid);

		registration.addRecipes(JeiRecipeType.FLUID_FUEL, manager.getAllRecipesFor(SlRecipeTypes.FLUID_FUEL.get()));
	}

	/**
	 * 固体燃烧室在没有命中 {@code steam_letters:solid_fuel} 配方时会回退到原版熔炉燃料表,
	 * 所以 JEI 里必须把这部分也列出来, 否则玩家只看得到自定义配方.
	 * <p>
	 * 原版燃料按燃烧时间分组: 时长相同的物品合并成一条配方, 槽内轮播.
	 * 逐物品建条目会让木板, 原木这类成组出现的燃料占满整页, 而它们的时间本来就是同一个值.
	 * <p>
	 * 已经被自定义配方覆盖的物品跳过, 避免同一个物品出现两页.
	 * 带流体能力的容器与运行时一致不列出, 只有 solid_fuel 配方能放行.
	 */
	private static List<SolidFuelRecipe> collectFurnaceFuels(List<SolidFuelRecipe> custom) {
		Map<Integer, List<ItemStack>> normal = new TreeMap<>();
		Map<Integer, List<ItemStack>> superheated = new TreeMap<>();

		for (Item item : ForgeRegistries.ITEMS) {
			ItemStack stack = item.getDefaultInstance();
			if (stack.isEmpty() || matchesAny(custom, stack) || isFluidContainer(stack)) {
				continue;
			}

			int time = ForgeHooks.getBurnTime(stack, net.minecraft.world.item.crafting.RecipeType.SMELTING);
			if (time <= 0) {
				continue;
			}

			Map<Integer, List<ItemStack>> target = stack.is(SlItemTags.SUPERHEATED_FUEL) ? superheated : normal;
			target.computeIfAbsent(time, (key) -> {
				return new ArrayList<>();
			}).add(stack);
		}

		List<SolidFuelRecipe> derived = new ArrayList<>();
		addDerivedRecipes(derived, normal, false);
		addDerivedRecipes(derived, superheated, true);
		return derived;
	}

	private static void addDerivedRecipes(List<SolidFuelRecipe> out, Map<Integer, List<ItemStack>> grouped, boolean superheated) {
		String suffix = superheated ? "_superheated" : "";
		grouped.forEach((time, stacks) -> {
			ResourceLocation id = SlApis.loadResource("solid_fuel/derived/" + time + suffix);
			SolidFuelRecipe recipe = new ProcessingRecipeBuilder<>(SolidFuelRecipe::new, id)
					.withItemIngredients(Ingredient.of(stacks.toArray(ItemStack[]::new)))
					.duration(time)
					.build();
			recipe.setSuperheated(superheated);
			out.add(recipe);
		});
	}

	private static boolean matchesAny(List<SolidFuelRecipe> recipes, ItemStack stack) {
		for (SolidFuelRecipe recipe : recipes) {
			if (recipe.matches(stack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 与固体燃烧室运行时保持一致: 带流体能力的容器不按熔炉燃料值列出, 只有 solid_fuel 配方能放行.
	 */
	private static boolean isFluidContainer(ItemStack stack) {
		return FluidUtil.getFluidHandler(stack).isPresent();
	}

	@Override
	public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
		helpers = registration.getJeiHelpers();
		this.registration = registration;

		ItemStack hammer = SlBlocks.STEAM_HAMMER.asStack();
		ItemStack mixer = SlBlocks.STEAM_MIXER.asStack();
		ItemStack spout = SlBlocks.ADVANCED_SPOUT.asStack();
		ItemStack basin = SlBlocks.BRONZE_BASIN.asStack();
		ItemStack saw = SlBlocks.STEAM_SAW.asStack();

		addCatalystsForBlocks(RecipeTypes.FUELING, SolidBurnerBlock.class);

		addCatalystsForBlocks(JeiRecipeType.SOLID_FUEL, SolidBurnerBlock.class);
		addCatalystsForBlocks(JeiRecipeType.FLUID_FUEL, FluidBurnerBlock.class);

		addCatalyst(SlApis.loadCreate("pressing"), hammer);
		addCatalyst(SlApis.loadCreate("packing"), hammer);
		addCatalyst(SlApis.loadCreate("automatic_packing"), hammer);

		addCatalyst(SlApis.loadCreate("mixing"), mixer);
		addCatalyst(SlApis.loadCreate("automatic_shapeless"), mixer);
		addCatalyst(SlApis.loadCreate("automatic_brewing"), mixer);

		addCatalyst(SlApis.loadCreate("spout_filling"), spout);

		addCatalyst(SlApis.loadCreate("sawing"), saw);
		addCatalyst(SlApis.loadCreate("block_cutting"), saw);

		if (ModList.get().isLoaded(CreateDieselGenerators.ID)) {
			addCatalyst(SlApis.loadCDG("compression_molding"), basin);
			addCatalyst(SlApis.loadCDG("compression_molding"), hammer);
			addCatalyst(SlApis.loadCDG("casting"), basin);
			addCatalyst(SlApis.loadCDG("casting"), spout);
			addCatalyst(SlApis.loadCDG("basin_fermenting"), basin);
		}
	}

	private void addCatalystsForBlocks(RecipeType<?> type, Class<? extends Block> blockClass) {
		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			if (blockClass.isInstance(block)) {
				registration.addRecipeCatalyst(block, type);
			}
		}
	}

	private void addCatalystsForItems(RecipeType<?> type, Class<? extends Item> itemClass) {
		for (Item block : ForgeRegistries.ITEMS.getValues()) {
			if (itemClass.isInstance(block)) {
				registration.addRecipeCatalyst(block, type);
			}
		}
	}

	@SuppressWarnings("removal")
	private void addCatalyst(ResourceLocation recipe, ItemStack catalyst) {
		helpers.getRecipeType(recipe)
				.ifPresent((type) -> {
					registration.addRecipeCatalyst(catalyst, type);
				});
	}
}
package dev.celestiacraft.steam_letters.datagen.recipe.type.vanilla;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import dev.celestiacraft.steam_letters.common.register.SlBlocks;
import dev.celestiacraft.steam_letters.common.register.SlItems;
import dev.celestiacraft.steam_letters.common.register.SlMaterials;
import dev.celestiacraft.steam_letters.datagen.recipe.SlRecipeProvider;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class SlCraftingRecipeGen extends SlRecipeProvider {
	public SlCraftingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
		super(output, future);
	}

	public static void addRecipes(RecipeOutput output) {
		addBronzeBasin(output);
		addBurner(output);
		addBoiler(output);
		addMaterial(output);
		addMachine(output);
		addOther(output);
	}

	private static void addMachine(RecipeOutput output) {
		// 空容器
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlItems.EMPTY_STEAM_CONTAINER)
				.pattern(" A ")
				.pattern("BCB")
				.pattern(" A ")
				.define('A', Items.DRIED_KELP)
				.define('B', SlItemTags.TIN_INGOT)
				.define('C', Tags.Items.GLASS_PANES)
				.unlockedBy("dried_kelp", has(Items.DRIED_KELP))
				.save(output, save(SHAPED + "empty_steam_container"));

		// 青铜搅拌器
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlItems.BRONZE_WHISK)
				.pattern(" A ")
				.pattern("CBC")
				.pattern("CCC")
				.define('A', Tags.Items.INGOTS_IRON)
				.define('B', AllItems.WHISK)
				.define('C', SlItemTags.BRONZE_PLATES)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/bronze_whisk"));

		// 高级注液器
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.ADVANCED_SPOUT)
				.pattern("ABA")
				.pattern("DCD")
				.pattern(" A ")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', SlBlocks.BRONZE_CASING)
				.define('C', AllBlocks.SPOUT)
				.define('D', Tags.Items.DUSTS_REDSTONE)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/advanced_spout"));

		// 蒸汽锻锤
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.STEAM_HAMMER)
				.pattern("ABA")
				.pattern("MCM")
				.pattern(" D ")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', SlBlocks.BRONZE_CASING)
				.define('M', SlItems.STEAM_MECHANISM)
				.define('C', AllBlocks.MECHANICAL_PRESS)
				.define('D', SlItemTags.BRONZE_BLOCK)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/steam_hammer"));

		// 蒸汽搅拌器
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.STEAM_MIXER)
				.pattern("ABA")
				.pattern("MCM")
				.pattern(" D ")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', SlBlocks.BRONZE_CASING)
				.define('M', SlItems.STEAM_MECHANISM)
				.define('C', AllBlocks.MECHANICAL_MIXER)
				.define('D', SlItems.BRONZE_WHISK)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/steam_mixer"));

		// 蒸汽锯
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.STEAM_SAW)
				.pattern("MCM")
				.pattern("ABA")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', SlBlocks.BRONZE_CASING)
				.define('M', SlItems.STEAM_MECHANISM)
				.define('C', AllBlocks.MECHANICAL_SAW)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/steam_saw"));

		// 青铜蒸汽泵
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_STEAM_PUMP)
				.pattern("A A")
				.pattern(" B ")
				.pattern("A A")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', AllBlocks.FLUID_PIPE)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/steam_pump_1"));

		// 青铜蒸汽泵
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_STEAM_PUMP)
				.pattern(" A ")
				.pattern("ABA")
				.pattern(" A ")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', AllBlocks.FLUID_PIPE)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/steam_pump2"));

		// 青铜蒸汽引擎
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_STEAM_ENGINE)
				.pattern(" AB")
				.pattern("CD ")
				.pattern(" AB")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', AllBlocks.SHAFT)
				.define('C', SlItemTags.BRONZE_BLOCK)
				.define('D', SlBlocks.BRONZE_CASING)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/engine"));

		// 青铜蒸汽飞轮
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_STEAM_FLYWHEEL)
				.pattern("ABB")
				.pattern("CDB")
				.pattern("ABB")
				.define('A', AllBlocks.SHAFT)
				.define('B', SlItemTags.BRONZE_PLATES)
				.define('C', AllBlocks.FLYWHEEL)
				.define('D', SlBlocks.BRONZE_CASING)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/flywheel"));
	}

	private static void addOther(RecipeOutput output) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AllBlocks.FLYWHEEL)
				.pattern("AAA")
				.pattern("ABA")
				.pattern("AAA")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', AllBlocks.SHAFT)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "flywheel"));
	}

	private static void addMaterial(RecipeOutput output) {
		// Bronze
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SlMaterials.BRONZE_BLOCK)
				.pattern("AAA")
				.pattern("AAA")
				.pattern("AAA")
				.define('A', SlItemTags.BRONZE_INGOT)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_INGOT))
				.save(output, save(SHAPED + "material/bronze_block_ingot"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SlMaterials.BRONZE_INGOT, 9)
				.requires(SlItemTags.BRONZE_BLOCK)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_INGOT))
				.save(output, save(SHAPELESS + "material/bronze_ingot_block"));

		// Tin
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SlMaterials.TIN_BLOCK)
				.pattern("AAA")
				.pattern("AAA")
				.pattern("AAA")
				.define('A', SlItemTags.TIN_INGOT)
				.unlockedBy("tin", has(SlItemTags.TIN_INGOT))
				.save(output, save(SHAPED + "material/tin_block_ingot"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, SlMaterials.TIN_INGOT)
				.pattern("AAA")
				.pattern("AAA")
				.pattern("AAA")
				.define('A', SlItemTags.TIN_NUGGET)
				.unlockedBy("tin", has(SlItemTags.TIN_NUGGET))
				.save(output, save(SHAPED + "material/tin_ingot_nugget"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SlMaterials.TIN_INGOT, 9)
				.requires(SlItemTags.TIN_BLOCK)
				.unlockedBy("tin", has(SlItemTags.TIN_INGOT))
				.save(output, save(SHAPELESS + "material/tin_ingot_block"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SlMaterials.TIN_NUGGET, 9)
				.requires(SlItemTags.TIN_INGOT)
				.unlockedBy("tin", has(SlItemTags.TIN_INGOT))
				.save(output, save(SHAPELESS + "material/tin_nugget_ingot"));
	}

	private static void addBurner(RecipeOutput output) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_SOLID_BURNER)
				.pattern("AAA")
				.pattern("ABA")
				.pattern("CCC")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', Blocks.FURNACE)
				.define('C', Blocks.MUD_BRICKS)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "steam/burner/solid"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_FLUID_BURNER)
				.pattern("AAA")
				.pattern("ABA")
				.pattern("CCC")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', AllBlocks.FLUID_TANK)
				.define('C', Blocks.MUD_BRICKS)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "steam/burner/fluid"));
	}

	private static void addBoiler(RecipeOutput output) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_BOILER)
				.pattern("AAA")
				.pattern("ABA")
				.pattern("AAA")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', AllBlocks.FLUID_TANK)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "steam/boiler"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_SOLAR_BOILER)
				.pattern("ABA")
				.pattern("ACA")
				.pattern("DDD")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', Tags.Items.GLASS_PANES)
				.define('C', AllBlocks.FLUID_TANK)
				.define('D', Blocks.MUD_BRICKS)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "steam/solar"));
	}

	private static void addBronzeBasin(RecipeOutput output) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_BASIN)
				.pattern("A A")
				.pattern("AAA")
				.define('A', SlItemTags.BRONZE_INGOT)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_INGOT))
				.save(output, save(SHAPED + "bronze/basin/ingot"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_BASIN)
				.pattern("A A")
				.pattern("AAA")
				.define('A', SlItemTags.BRONZE_PLATES)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/basin/plate"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_BASIN)
				.pattern("ABA")
				.pattern(" A ")
				.define('A', SlItemTags.BRONZE_INGOT)
				.define('B', AllBlocks.BASIN)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_INGOT))
				.save(output, save(SHAPED + "bronze/basin/ingot_update"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SlBlocks.BRONZE_BASIN)
				.pattern("ABA")
				.pattern(" A ")
				.define('A', SlItemTags.BRONZE_PLATES)
				.define('B', AllBlocks.BASIN)
				.unlockedBy("bronze", has(SlItemTags.BRONZE_PLATES))
				.save(output, save(SHAPED + "bronze/basin/plate_update"));
	}
}
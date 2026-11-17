package dev.celestiacraft.steam_letters.datagen.recipe.type;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.recipe.builder.SolidFuelRecipeGen;
import dev.celestiacraft.steam_letters.common.recipe.conditions.HasItemCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class SlSolidFuelRecipeGen extends SolidFuelRecipeGen {
	public SlSolidFuelRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> future) {
		super(output, future, CreateLettersSteam.MODID);

		// 钻石
		recipe("diamond", (builder) -> {
			return builder.itemTag(Tags.Items.GEMS_DIAMOND)
					.timeItem(30)
					.superheated();
		});

		// 钻石块
		recipe("diamond_block", (builder) -> {
			return builder.itemTag(Tags.Items.STORAGE_BLOCKS_DIAMOND)
					.timeItem(300)
					.superheated();
		});

		// 烈焰棒
		recipe("blaze_rod", (builder) -> {
			return builder.item(Items.BLAZE_ROD)
					.timeItem(12)
					.superheated();
		});

		// 焦煤
		recipe("compat/coal_coke", (builder) -> {
			return builder.itemTag("c:coal_coke")
					.timeItem(16)
					.superheated()
					.withCondition(HasItemCondition.ofTag("c:coal_coke"));
		});

		// 焦煤块
		recipe("compat/coal_coke_block", (builder) -> {
			return builder.itemTag("c:storage_blocks/coal_coke")
					.timeItem(160)
					.superheated()
					.withCondition(HasItemCondition.ofTag("c:storage_blocks/coal_coke"));
		});
	}
}
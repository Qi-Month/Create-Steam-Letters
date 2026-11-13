package dev.celestiacraft.steam_letters.common.register;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.client.models.ItemModelGen;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class SlMaterials {
	public static final ItemEntry<Item>
			BRONZE_INGOT,
			BRONZE_PLATE,
			TIN_INGOT,
			TIN_NUGGET;

	public static final BlockEntry<Block>
			BRONZE_BLOCK,
			TIN_BLOCK;

	static {
		BRONZE_INGOT = SlApis.REGISTRATE.item("bronze_ingot", Item::new)
				.model(ItemModelGen.generated("item/material/bronze/ingot"))
				.tag(Tags.Items.INGOTS)
				.tag(SlItemTags.BRONZE_INGOT)
				.lang("Bronze Ingot")
				.register();

		BRONZE_PLATE = SlApis.REGISTRATE.item("bronze_plate", Item::new)
				.model(ItemModelGen.generated("item/material/bronze/plate"))
				.tag(AllTags.AllItemTags.PLATES.tag)
				.tag(SlItemTags.BRONZE_PLATES)
				.lang("Bronze Plate")
				.register();

		TIN_INGOT = SlApis.REGISTRATE.item("tin_ingot", Item::new)
				.model(ItemModelGen.generated("item/material/tin/ingot"))
				.tag(Tags.Items.INGOTS)
				.tag(SlItemTags.TIN_INGOT)
				.lang("Tin Ingot")
				.register();

		TIN_NUGGET = SlApis.REGISTRATE.item("tin_nugget", Item::new)
				.model(ItemModelGen.generated("item/material/tin/nugget"))
				.tag(Tags.Items.NUGGETS)
				.tag(SlItemTags.TIN_NUGGET)
				.lang("Tin Nugget")
				.register();

		BRONZE_BLOCK = SlApis.REGISTRATE.block("bronze_block", Block::new)
				.item()
				.tag(Tags.Items.STORAGE_BLOCKS)
				.tag(SlItemTags.BRONZE_BLOCK)
				.build()
				.blockstate(BlockStateGen.simpleCubeAll("material/block/bronze"))
				.lang("Block of Bronze")
				.register();

		TIN_BLOCK = SlApis.REGISTRATE.block("tin_block", Block::new)
				.item()
				.tag(Tags.Items.STORAGE_BLOCKS)
				.tag(SlItemTags.TIN_BLOCK)
				.build()
				.blockstate(BlockStateGen.simpleCubeAll("material/block/tin"))
				.lang("Block of Tin")
				.register();
	}

	public static void init() {
	}
}
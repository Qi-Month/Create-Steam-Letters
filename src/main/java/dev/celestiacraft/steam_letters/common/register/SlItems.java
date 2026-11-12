package dev.celestiacraft.steam_letters.common.register;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.client.models.ItemModelGen;
import dev.celestiacraft.steam_letters.common.item.SteamContainerItem;
import dev.celestiacraft.steam_letters.common.item.SteamMechanismItem;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import net.minecraft.world.item.Item;

public class SlItems {
	public static final ItemEntry<SteamMechanismItem> STEAM_MECHANISM;
	public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_STEAM_MECHANISM;
	public static final ItemEntry<SteamContainerItem> EMPTY_STEAM_CONTAINER;
	public static final ItemEntry<SteamContainerItem> SEALED_STEAM_CONTAINER;
	public static final ItemEntry<Item> BRONZE_WHISK;

	static {
		STEAM_MECHANISM = SlApis.REGISTRATE.item("steam_mechanism", SteamMechanismItem::new)
				.model(ItemModelGen.generated("item/steam_mechanism"))
				.lang("Steam Mechanism")
				.register();

		INCOMPLETE_STEAM_MECHANISM = SlApis.REGISTRATE.item("incomplete_steam_mechanism", SequencedAssemblyItem::new)
				.model(ItemModelGen.generated("item/incomplete_steam_mechanism"))
				.lang("Incomplete Steam Mechanism")
				.register();

		EMPTY_STEAM_CONTAINER = SlApis.REGISTRATE.item("empty_steam_container", (properties) -> {
					return new SteamContainerItem(properties, true);
				})
				.tag(SlItemTags.STEAM_CONTAINER)
				.model(ItemModelGen.generated("item/empty_steam_container"))
				.lang("Empty Sealed Steam Container")
				.register();

		SEALED_STEAM_CONTAINER = SlApis.REGISTRATE.item("sealed_steam_container", (properties) -> {
					return new SteamContainerItem(properties, false);
				})
				.tag(SlItemTags.STEAM_CONTAINER)
				.model(ItemModelGen.generated("item/sealed_steam_container"))
				.lang("Sealed Steam Container")
				.register();

		BRONZE_WHISK = SlApis.REGISTRATE.item("bronze_whisk", Item::new)
				.model(ItemModelGen.generated("item/bronze_whisk"))
				.lang("Bronze Whisk")
				.register();
	}

	public static void init() {
	}
}
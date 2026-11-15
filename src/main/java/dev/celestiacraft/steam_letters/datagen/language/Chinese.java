package dev.celestiacraft.steam_letters.datagen.language;

import dev.celestiacraft.steam_letters.api.client.lang.ChineseLangProvider;
import dev.celestiacraft.steam_letters.common.register.SlItems;

public class Chinese {
	public static void addTranslations(ChineseLangProvider provider) {
		addItemLang(provider);
	}

	private static void addItemLang(ChineseLangProvider provider) {
		provider.add(SlItems.BRONZE_WHISK.asItem(), "AAA");
	}
}
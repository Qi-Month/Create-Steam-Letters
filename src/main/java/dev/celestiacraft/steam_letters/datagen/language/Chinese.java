package dev.celestiacraft.steam_letters.datagen.language;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.client.lang.ChineseLangProvider;
import dev.celestiacraft.steam_letters.client.ponder.SlPonderPlugin;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import dev.celestiacraft.steam_letters.common.register.SlItems;
import net.createmod.ponder.api.registration.LangRegistryAccess;
import net.createmod.ponder.foundation.PonderIndex;

public class Chinese {
	public static void addTranslations(ChineseLangProvider provider) {
		addItemLang(provider);

		SlPonderPlugin.register();
		LangRegistryAccess access = PonderIndex.getLangAccess();
		access.provideLang(CreateLettersSteam.MODID, provider::add);
	}

	private static void addItemLang(ChineseLangProvider provider) {
		provider.addItem(SlItems.BRONZE_WHISK, "AAA");
		provider.addFluid(SlFluids.STEAM, "蒸汽");
	}
}
package dev.celestiacraft.steam_letters.api.client.lang;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.client.ponder.SlPonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;

import java.util.LinkedHashMap;
import java.util.Map;

public class SlPonderLang {
	private static final String PREFIX = CreateLettersSteam.MODID + ".ponder.";

	private static Map<String, String> entries;

	public static Map<String, String> entries() {
		if (entries == null) {
			Map<String, String> lang = new LinkedHashMap<>();
			SlPonderPlugin.register();
			PonderIndex.getLangAccess().provideLang(CreateLettersSteam.MODID, lang::put);
			entries = lang;
		}
		return entries;
	}

	public static int count(String scene) {
		String header = PREFIX + scene + ".header";
		String text = PREFIX + scene + ".text_";
		int count = 0;
		for (String key : entries().keySet()) {
			if (header.equals(key) || key.startsWith(text)) {
				count++;
			}
		}
		return count;
	}
}
package dev.celestiacraft.steam_letters.client.ponder;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.common.register.SlBlocks;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SlPonderTags {
	public static final ResourceLocation SL;

	static {
		SL = SlApis.loadResource(CreateLettersSteam.MODID);
	}

	public static void register(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
		helper.registerTag(SL)
				.addToIndex()
				.item(SlBlocks.BRONZE_STEAM_FLYWHEEL, true, false)
				.title("Create: Letter's in Steam")
				.description("Machines")
				.register();
	}
}
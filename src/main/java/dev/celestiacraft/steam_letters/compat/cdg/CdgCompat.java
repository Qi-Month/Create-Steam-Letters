package dev.celestiacraft.steam_letters.compat.cdg;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.content.molds.BasinSpoutCasting;
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.register.SlBlocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = CreateLettersSteam.MODID)
public class CdgCompat {
	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			if (ModList.get().isLoaded(CreateDieselGenerators.ID)) {
				BlockSpoutingBehaviour.BY_BLOCK.register(SlBlocks.BRONZE_BASIN.get(), new BasinSpoutCasting());
			}
		});
	}
}
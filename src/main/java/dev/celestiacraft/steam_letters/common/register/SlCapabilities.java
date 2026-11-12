package dev.celestiacraft.steam_letters.common.register;

import com.tterrag.registrate.util.entry.FluidEntry;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.block.advanced_spout.AdvancedSpoutBlockEntity;
import dev.celestiacraft.steam_letters.common.block.boiler.AbstractBoilerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.bronze_basin.BronzeBasinBlockEntity;
import dev.celestiacraft.steam_letters.common.block.bronze_depot.BronzeDepotBlockEntity;
import dev.celestiacraft.steam_letters.common.block.burner.FluidBurnerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.burner.SolidBurnerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.engine.SteamEngineBlockEntity;
import dev.celestiacraft.steam_letters.common.block.steam_hammer.SteamHammerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.steam_mixer.SteamMixerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.steam_saw.SteamSawBlockEntity;
import dev.celestiacraft.steam_letters.common.fluid.SteamFluidFlowing;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SlCapabilities {
	private static final List<Consumer<RegisterCapabilitiesEvent>> DYNAMIC = new ArrayList<>();

	public static void register(RegisterCapabilitiesEvent event) {
		CreateLettersSteam.LOGGER.info("[SlCapabilities] register invoked");
		AbstractBoilerBlockEntity.registerCapabilities(event);
		AdvancedSpoutBlockEntity.registerCapabilities(event);
		BronzeBasinBlockEntity.registerCapabilities(event);
		BronzeDepotBlockEntity.registerCapabilities(event);
		FluidBurnerBlockEntity.registerCapabilities(event);
		SolidBurnerBlockEntity.registerCapabilities(event);
		SteamEngineBlockEntity.registerCapabilities(event);
		SteamHammerBlockEntity.registerCapabilities(event);
		SteamMixerBlockEntity.registerCapabilities(event);
		SteamSawBlockEntity.registerCapabilities(event);
		registerSteamBuckets(event);
		for (Consumer<RegisterCapabilitiesEvent> registrar : DYNAMIC) {
			registrar.accept(event);
		}
		CreateLettersSteam.LOGGER.info("[SlCapabilities] register finished, dynamic types: {}", DYNAMIC.size());
	}

	public static void addDynamic(Consumer<RegisterCapabilitiesEvent> registrar) {
		DYNAMIC.add(registrar);
	}

	private static void registerSteamBuckets(RegisterCapabilitiesEvent event) {
		registerSteamBucket(event, SlFluids.STEAM);
		registerSteamBucket(event, SlFluids.SUPERHEATED_STEAM);
	}

	private static void registerSteamBucket(RegisterCapabilitiesEvent event, FluidEntry<SteamFluidFlowing> fluid) {
		Optional<Item> bucket = fluid.getBucket();
		if (bucket.isEmpty()) {
			return;
		}

		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> {
			return new FluidBucketWrapper(stack);
		}, bucket.get());
	}
}
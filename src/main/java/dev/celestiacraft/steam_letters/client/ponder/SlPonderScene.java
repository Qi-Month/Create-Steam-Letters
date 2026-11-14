package dev.celestiacraft.steam_letters.client.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import dev.celestiacraft.steam_letters.client.ponder.scene.*;
import dev.celestiacraft.steam_letters.common.block.boiler.SolarBoilerBlock;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlock;
import dev.celestiacraft.steam_letters.common.block.burner.AbstractBurnerBlock;
import dev.celestiacraft.steam_letters.common.block.engine.SteamEngineBlock;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlock;
import dev.celestiacraft.steam_letters.common.register.SlBlocks;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class SlPonderScene {
	public static void register(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		steamHammer(helper);
		steamMixer(helper);
		steamSaw(helper);
		advancedSpout(helper);
		steamEngine(helper);
		solarBoiler(helper);
		bronzeSteamPump(helper);
		bigBoiler(helper);
	}

	private static void steamHammer(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(SlBlocks.STEAM_HAMMER.asItem())
				.addStoryBoard(
						"steam_hammer",
						SteamHammerScene::usage,
						AllCreatePonderTags.KINETIC_APPLIANCES,
						SlPonderTags.SL
				);
	}

	private static void steamMixer(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(SlBlocks.STEAM_MIXER.asItem())
				.addStoryBoard(
						"steam_mixer",
						SteamMixerScene::usage,
						AllCreatePonderTags.KINETIC_APPLIANCES,
						SlPonderTags.SL
				);
	}

	private static void steamSaw(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(SlBlocks.STEAM_SAW.asItem())
				.addStoryBoard(
						"steam_saw",
						SteamSawScene::usage,
						AllCreatePonderTags.KINETIC_APPLIANCES,
						SlPonderTags.SL
				);
	}

	private static void advancedSpout(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(SlBlocks.ADVANCED_SPOUT.asItem())
				.addStoryBoard(
						"advanced_spout",
						AdvancedSpoutScene::usage,
						AllCreatePonderTags.FLUIDS,
						SlPonderTags.SL
				);
	}

	private static void steamEngine(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		SlPonderPlugin.addPonderForBlocks(helper, (block) -> {
			return (block instanceof SteamBoilerBlock)
					|| (block instanceof AbstractBurnerBlock)
					|| (block instanceof SteamFlywheelBlock)
					|| (block instanceof SteamEngineBlock);
		}, "steam_engine", SteamEngineScene::usage, AllCreatePonderTags.FLUIDS, AllCreatePonderTags.KINETIC_SOURCES, SlPonderTags.SL);
	}

	private static void solarBoiler(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		SlPonderPlugin.addPonderForBlocks(helper, (block) -> {
			return (block instanceof SolarBoilerBlock)
					|| (block instanceof SteamFlywheelBlock)
					|| (block instanceof SteamEngineBlock);
		}, "solar_boiler", SolarBoilerScene::usage, AllCreatePonderTags.FLUIDS, SlPonderTags.SL);
	}

	private static void bronzeSteamPump(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		helper.forComponents(SlBlocks.BRONZE_STEAM_PUMP.asItem())
				.addStoryBoard(
						"bronze_steam_pump",
						BronzeSteamPumpScene::usage,
						AllCreatePonderTags.FLUIDS,
						SlPonderTags.SL
				);
	}

	private static void bigBoiler(@NotNull PonderSceneRegistrationHelper<Item> helper) {
		SlPonderPlugin.addPonderForBlocks(helper, (block) -> {
			return (block instanceof SteamBoilerBlock);
		}, "big_boiler", BigBoilerScene::usage, AllCreatePonderTags.FLUIDS, SlPonderTags.SL);
	}
}
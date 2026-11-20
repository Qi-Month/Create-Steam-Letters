package dev.celestiacraft.steam_letters.client;

import com.tterrag.registrate.util.entry.FluidEntry;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.client.block.SlBlockPartialModel;
import dev.celestiacraft.steam_letters.client.particle.SteamParticle;
import dev.celestiacraft.steam_letters.client.ponder.SlPonderPlugin;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerRenderer;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlock;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlockEntity;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelInstance;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelRenderer;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import dev.celestiacraft.steam_letters.common.register.SlParticles;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = CreateLettersSteam.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SlClientSetup {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			SlPonderPlugin.register();
			registerDynamicFlywheelVisuals();
			preloadFlywheelModels();

			applyTranslucentLayer(SlFluids.STEAM);
			applyTranslucentLayer(SlFluids.SUPERHEATED_STEAM);
		});
	}

	@SubscribeEvent
	public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(SlParticles.STEAM.get(), SteamParticle::factory);
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		for (BlockEntityType<SteamFlywheelBlockEntity> type : SteamFlywheelBlockEntity.DYNAMIC_TYPES) {
			event.registerBlockEntityRenderer(type, SteamFlywheelRenderer::new);
		}

		for (BlockEntityType<SteamBoilerBlockEntity> type : SteamBoilerBlockEntity.DYNAMIC_TYPES) {
			event.registerBlockEntityRenderer(type, SteamBoilerRenderer::new);
		}
	}

	private static void preloadFlywheelModels() {
		for (Block block : ForgeRegistries.BLOCKS) {
			if (block instanceof SteamFlywheelBlock flywheel) {
				SlBlockPartialModel.preloadFlywheel(flywheel.getMaterial());
			}
		}
	}

	private static void registerDynamicFlywheelVisuals() {
		for (BlockEntityType<SteamFlywheelBlockEntity> type : SteamFlywheelBlockEntity.DYNAMIC_TYPES) {
			SimpleBlockEntityVisualizer.builder(type)
					.factory(SteamFlywheelInstance::new)
					.neverSkipVanillaRender()
					.apply();
		}
	}


	private static <T extends ForgeFlowingFluid> void applyTranslucentLayer(FluidEntry<T> entry) {
		RenderType translucent = RenderType.translucent();
		Fluid flowing = entry.get();
		Fluid source = entry.getSource();

		ItemBlockRenderTypes.setRenderLayer(flowing, translucent);
		ItemBlockRenderTypes.setRenderLayer(source, translucent);
	}

	@SubscribeEvent
	public static void onPackFinders(AddPackFindersEvent event) {
		final FileSystem[] thermalPackZip = new FileSystem[1];
		String packId = "steam_letters_thermal";
		Path zip = ModList.get().getModFileById(CreateLettersSteam.MODID)
				.getFile()
				.findResource("resourcepacks", "Thermal Hue Pack.zip");

		if (event.getPackType() != PackType.CLIENT_RESOURCES) {
			return;
		}

		event.addRepositorySource((pack) -> {
			PackResources resources;

			try {
				if (thermalPackZip[0] == null || !thermalPackZip[0].isOpen()) {
					thermalPackZip[0] = FileSystems.newFileSystem(zip);
				}

				resources = new PathPackResources(packId, thermalPackZip[0].getRootDirectories().iterator().next(), true);
			} catch (Exception exception) {
				CreateLettersSteam.LOGGER.error("Failed to open thermal resource pack", exception);
				return;
			}

			Pack loaded = Pack.readMetaAndCreate(
					packId,
					Component.translatable(SlTranKeys.THERMAL_PACK_TITLE),
					false,
					(id) -> resources,
					PackType.CLIENT_RESOURCES,
					Pack.Position.TOP,
					PackSource.BUILT_IN
			);

			if (loaded != null) {
				pack.accept(loaded);
			}
		});
	}
}
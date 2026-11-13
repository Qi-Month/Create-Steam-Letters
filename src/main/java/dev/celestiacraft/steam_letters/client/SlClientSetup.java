package dev.celestiacraft.steam_letters.client;

import com.tterrag.registrate.util.entry.FluidEntry;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.client.block.SlBlockPartialModel;
import dev.celestiacraft.steam_letters.client.particle.SteamParticle;
import dev.celestiacraft.steam_letters.client.ponder.SlPonderPlugin;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlock;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelBlockEntity;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelInstance;
import dev.celestiacraft.steam_letters.common.block.flywheel.SteamFlywheelRenderer;
import dev.celestiacraft.steam_letters.common.fluid.SteamFluid;
import dev.celestiacraft.steam_letters.common.fluid.SteamFluidClient;
import dev.celestiacraft.steam_letters.common.fluid.SteamFluidFlowing;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import dev.celestiacraft.steam_letters.common.register.SlParticles;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.core.registries.BuiltInRegistries;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Optional;

@EventBusSubscriber(modid = CreateLettersSteam.MODID, value = Dist.CLIENT)
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
	public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
		registerFluidExtensions(event, SlFluids.STEAM);
		registerFluidExtensions(event, SlFluids.SUPERHEATED_STEAM);
	}

	private static void registerFluidExtensions(RegisterClientExtensionsEvent event, FluidEntry<SteamFluidFlowing> entry) {
		FluidType fluidType = entry.getType();
		if (!(fluidType instanceof SteamFluid steam)) {
			return;
		}

		SteamFluidClient extensions = new SteamFluidClient(
				steam.getStillTexture(),
				steam.getFlowingTexture(),
				steam.getTintColor(),
				steam.getFogColor(),
				steam.getFogDistance()
		);
		event.registerFluidType(extensions, fluidType);
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
	}

	private static void preloadFlywheelModels() {
		for (Block block : BuiltInRegistries.BLOCK) {
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


	private static <T extends BaseFlowingFluid> void applyTranslucentLayer(FluidEntry<T> entry) {
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
			PackLocationInfo location = new PackLocationInfo(
					packId,
					Component.translatable(SlTranKeys.THERMAL_PACK_TITLE),
					PackSource.BUILT_IN,
					Optional.empty()
			);

			Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
				@Override
				public PackResources openPrimary(PackLocationInfo info) {
					return openPack(info);
				}

				@Override
				public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
					return openPack(info);
				}

				private PackResources openPack(PackLocationInfo info) {
					try {
						if (thermalPackZip[0] == null || !thermalPackZip[0].isOpen()) {
							thermalPackZip[0] = FileSystems.newFileSystem(zip);
						}

						return new PathPackResources(info, thermalPackZip[0].getRootDirectories().iterator().next());
					} catch (Exception exception) {
						CreateLettersSteam.LOGGER.error("Failed to open thermal resource pack", exception);
						throw new IllegalStateException(exception);
					}
				}
			};

			Pack loaded = Pack.readMetaAndCreate(
					location,
					supplier,
					PackType.CLIENT_RESOURCES,
					new PackSelectionConfig(false, Pack.Position.TOP, false)
			);

			if (loaded != null) {
				pack.accept(loaded);
			}
		});
	}
}
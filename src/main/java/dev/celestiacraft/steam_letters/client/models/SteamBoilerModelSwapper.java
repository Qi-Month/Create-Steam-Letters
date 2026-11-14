package dev.celestiacraft.steam_letters.client.models;

import com.simibubi.create.foundation.model.ModelSwapper;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 给所有锅炉方块套上连接纹理模型.
 * <p>
 * 在模型烘焙结束时统一替换, 因此 KubeJS 注册的锅炉也能按各自的贴图目录拿到 connected 图集.
 */
@EventBusSubscriber(modid = CreateLettersSteam.MODID, value = Dist.CLIENT)
public class SteamBoilerModelSwapper {
	private static final Set<ResourceLocation> WARNED = new HashSet<>();

	@SubscribeEvent
	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		Map<ModelResourceLocation, BakedModel> models = event.getModels();
		for (Block block : BuiltInRegistries.BLOCK) {
			if (!(block instanceof SteamBoilerBlock boiler)) {
				continue;
			}

			ResourceLocation directory = boiler.getTextureDirectory();
			if (directory == null) {
				continue;
			}

			if (!hasConnectedTextures(directory)) {
				warnMissingTextures(directory);
				continue;
			}

			for (ModelResourceLocation location : ModelSwapper.getAllBlockStateModelLocations(block)) {
				models.computeIfPresent(location, (key, original) -> {
					return SteamBoilerTankModel.create(original, directory);
				});
			}
		}
	}

	/**
	 * 目录下缺少连接纹理时不套连接模型, 否则图集里找不到贴图会让烘焙失败.
	 */
	private static boolean hasConnectedTextures(ResourceLocation directory) {
		ResourceManager resources = Minecraft.getInstance().getResourceManager();
		return resources.getResource(texture(directory, "connected")).isPresent()
				&& resources.getResource(texture(directory, "connected_top")).isPresent();
	}

	private static ResourceLocation texture(ResourceLocation directory, String name) {
		return ResourceLocation.fromNamespaceAndPath(directory.getNamespace(), "textures/" + directory.getPath() + "/" + name + ".png");
	}

	/**
	 * 每个目录只提示一次, 否则每次资源重载都会刷屏.
	 */
	private static void warnMissingTextures(ResourceLocation directory) {
		if (!WARNED.add(directory)) {
			return;
		}

		CreateLettersSteam.LOGGER.warn("Boiler texture directory {} is missing connected.png or connected_top.png, the boiler renders with the flat side texture", directory);
	}
}
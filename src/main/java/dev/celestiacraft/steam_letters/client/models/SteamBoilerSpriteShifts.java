package dev.celestiacraft.steam_letters.client.models;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 锅炉材质的连接纹理图集, 按贴图目录缓存.
 * <p>
 * 每个条目把目录下的普通纹理映射到同目录的 connected 图集, 相邻方块互相连接时由连接行为切换到后者的格子.
 */
public class SteamBoilerSpriteShifts {
	private static final Map<ResourceLocation, CTSpriteShiftEntry> CACHE = new ConcurrentHashMap<>();

	public static CTSpriteShiftEntry side(ResourceLocation directory) {
		return shifted(directory, "side", "connected");
	}

	public static CTSpriteShiftEntry top(ResourceLocation directory) {
		return shifted(directory, "top", "connected_top");
	}

	private static CTSpriteShiftEntry shifted(ResourceLocation directory, String texture, String connected) {
		ResourceLocation original = resource(directory, texture);
		return CACHE.computeIfAbsent(original, (key) -> {
			return CTSpriteShifter.getCT(AllCTTypes.RECTANGLE, key, resource(directory, connected));
		});
	}

	private static ResourceLocation resource(ResourceLocation directory, String name) {
		return ResourceLocation.fromNamespaceAndPath(directory.getNamespace(), directory.getPath() + "/" + name);
	}
}
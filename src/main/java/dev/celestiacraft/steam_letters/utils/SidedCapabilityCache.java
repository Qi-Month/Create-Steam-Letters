package dev.celestiacraft.steam_letters.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import java.util.EnumMap;

/*
 * Portions derived from CreateFluidLogistics, Copyright (c) 2026 yision1.
 * <p>
 * Source: <a href="https://github.com/yision1/CreateFluidLogistics">
 * https://github.com/yision1/CreateFluidLogistics
 * </a>
 * <p>
 * Licensed under the BSD 3-Clause License; see the project LICENSE for the full license text.
 */
public class SidedCapabilityCache<T> {
	private final EnumMap<Direction, BlockCapabilityCache<T, Direction>> caches = new EnumMap<>(Direction.class);
	private final BlockCapability<T, Direction> capability;

	public SidedCapabilityCache(BlockCapability<T, Direction> capability) {
		this.capability = capability;
	}

	public T get(Level level, BlockPos targetPos, Direction direction) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return level.getCapability(capability, targetPos, direction.getOpposite());
		}
		BlockCapabilityCache<T, Direction> cache = caches.get(direction);
		if (cache == null) {
			cache = BlockCapabilityCache.create(capability, serverLevel, targetPos, direction.getOpposite());
			caches.put(direction, cache);
		}
		return cache.getCapability();
	}

	public void clear() {
		caches.clear();
	}
}
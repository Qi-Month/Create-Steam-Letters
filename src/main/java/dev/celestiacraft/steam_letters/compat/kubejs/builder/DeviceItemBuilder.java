package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * 把一个设备 builder 的方块物品挂到 KubeJS 的注册队列上.
 * <p>
 * 物品和方块不在同一张注册表里, 只能等物品的注册事件打开时才能注册.
 */
public class DeviceItemBuilder extends BuilderBase<Item> {
	private final Supplier<Item> factory;

	public DeviceItemBuilder(ResourceLocation id, Supplier<Item> factory) {
		super(id);
		this.factory = factory;
	}

	@Override
	public Item createObject() {
		return factory.get();
	}
}
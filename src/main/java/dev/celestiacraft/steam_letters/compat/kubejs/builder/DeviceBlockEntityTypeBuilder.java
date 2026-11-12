package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * 把一个设备 builder 的方块实体类型挂到 KubeJS 的注册队列上.
 * <p>
 * 方块实体类型不在方块那张注册表里, 只有等它自己的注册事件打开时才能注册,
 * 所以这里只负责在合适的时候调用工厂, 注册时机交给 KubeJS.
 */
public class DeviceBlockEntityTypeBuilder extends BuilderBase<BlockEntityType<?>> {
	private final Supplier<BlockEntityType<?>> factory;

	public DeviceBlockEntityTypeBuilder(ResourceLocation id, Supplier<BlockEntityType<?>> factory) {
		super(id);
		this.factory = factory;
	}

	@Override
	public BlockEntityType<?> createObject() {
		return factory.get();
	}
}
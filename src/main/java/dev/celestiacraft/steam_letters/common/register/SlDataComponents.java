package dev.celestiacraft.steam_letters.common.register;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SlDataComponents {
	public static final DeferredRegister.DataComponents DATA_COMPONENTS;

	/**
	 * 容器物品内储存的流体。
	 * <p>
	 * 数据组件必须实现 {@code equals}/{@code hashCode} 并保持不可变，
	 * 因此这里存序列化后的 NBT，不直接存 {@code FluidStack}。
	 */
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> STORED_FLUID;

	static {
		DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateLettersSteam.MODID);

		STORED_FLUID = DATA_COMPONENTS.registerComponentType("stored_fluid", (builder) -> {
			return builder.persistent(CompoundTag.CODEC)
					.networkSynchronized(ByteBufCodecs.COMPOUND_TAG);
		});
	}

	public static void init(IEventBus bus) {
		DATA_COMPONENTS.register(bus);
	}
}
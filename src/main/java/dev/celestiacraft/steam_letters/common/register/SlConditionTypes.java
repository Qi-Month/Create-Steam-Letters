package dev.celestiacraft.steam_letters.common.register;

import com.mojang.serialization.MapCodec;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.common.recipe.conditions.HasFluidCondition;
import dev.celestiacraft.steam_letters.common.recipe.conditions.HasItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SlConditionTypes {
	public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS;

	public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<HasItemCondition>> HAS_ITEM;
	public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<HasFluidCondition>> HAS_FLUID;

	static {
		CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, CreateLettersSteam.MODID);

		HAS_ITEM = CONDITION_CODECS.register("has_item", () -> {
			return HasItemCondition.CODEC;
		});

		HAS_FLUID = CONDITION_CODECS.register("has_fluid", () -> {
			return HasFluidCondition.CODEC;
		});
	}

	public static void init(IEventBus bus) {
		CONDITION_CODECS.register(bus);
	}
}
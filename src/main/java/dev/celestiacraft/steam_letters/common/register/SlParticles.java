package dev.celestiacraft.steam_letters.common.register;

import dev.celestiacraft.steam_letters.CreateLettersSteam;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SlParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STEAM;

	static {
		PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, CreateLettersSteam.MODID);

		STEAM = PARTICLE_TYPES.register("steam", () -> {
			return new SimpleParticleType(false);
		});
	}

	public static void init(IEventBus bus) {
		PARTICLE_TYPES.register(bus);
	}
}
package dev.celestiacraft.steam_letters.client.particle;

import dev.celestiacraft.steam_letters.api.client.particle.ParticleBase;
import lombok.AllArgsConstructor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;

/*
 * Portions derived from SteamPowered, Copyright (c) 2026 TeamMoegMC.
 * <p>
 * Source: <a href="https://github.com/TeamMoegMC/SteamPowered">
 * https://github.com/TeamMoegMC/SteamPowered
 * </a>
 * <p>
 * Licensed under the GNU GENERAL PUBLIC License; see the project LICENSE for the full license text.
 */
public class SteamParticle extends ParticleBase {
	private static final SecureRandom RANDOM = new SecureRandom();

	public SteamParticle(ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ) {
		super(level, x, y, z, motionX, motionY + 0.4f, motionZ);
		gravity = -0.05F;
		rCol = gCol = bCol = 1.0f;
		originalScale = 0.6F;
		lifetime = (int) (160.0D / (RANDOM.nextDouble() * 0.2D + 0.8D));
	}

	@AllArgsConstructor
	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		@Override
		public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			SteamParticle steamParticle = new SteamParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
			steamParticle.pickSprite(spriteSet);
			return steamParticle;
		}
	}
}
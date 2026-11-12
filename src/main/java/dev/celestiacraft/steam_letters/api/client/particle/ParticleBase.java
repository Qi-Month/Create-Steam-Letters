package dev.celestiacraft.steam_letters.api.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/*
 * Portions derived from SteamPowered, Copyright (c) 2026 TeamMoegMC.
 * <p>
 * Source: <a href="https://github.com/TeamMoegMC/SteamPowered">
 * https://github.com/TeamMoegMC/SteamPowered
 * </a>
 * <p>
 * Licensed under the GNU GENERAL PUBLIC License; see the project LICENSE for the full license text.
 */
public class ParticleBase extends TextureSheetParticle {
	protected float originalScale = 1.3F;

	protected ParticleBase(ClientLevel level, double x, double y, double z) {
		super(level, x, y, z);
	}

	public ParticleBase(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		super(world, x, y, z, motionX, motionY, motionZ);
		xd *= 1.25;
		yd *= 1.25;
		zd *= 1.25;
	}

	@Override
	public @NotNull ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void render(@NotNull VertexConsumer consumer, @NotNull Camera camera, float pt) {
		float age = (this.age + pt) / lifetime * 32.0F;

		age = Mth.clamp(age, 0.0F, 1.0F);
		alpha = Mth.clamp(1 - (this.age + pt) / lifetime, 0.0F, 1.0F);
		quadSize = originalScale * (age + this.age * 0.01875F);
		super.render(consumer, camera, pt);
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		if (age >= lifetime) {
			remove();
		}
		age++;
		yd -= 0.04D * gravity;
		move(xd, yd, zd);

		if (y == yo) {
			xd *= 1.1D;
			zd *= 1.1D;
			age += 7;
		}
		xd *= 0.96D;
		yd *= 0.96D;
		zd *= 0.96D;

		if (onGround) {
			xd *= 0.67D;
			zd *= 0.67D;
		}
	}
}
package dev.celestiacraft.steam_letters.common.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class SteamFluidClient implements IClientFluidTypeExtensions {
	private static final float DEFAULT_FOG_DISTANCE = 1.0f;

	private final ResourceLocation stillTexture;
	private final ResourceLocation flowingTexture;
	private final int tintColor;
	private final Vector3f fogColor;
	private final Supplier<Float> fogDistance;

	public SteamFluidClient(ResourceLocation stillTexture, ResourceLocation flowingTexture, int tintColor, Vector3f fogColor, Supplier<Float> fogDistance) {
		this.stillTexture = stillTexture;
		this.flowingTexture = flowingTexture;
		this.tintColor = tintColor;
		this.fogColor = fogColor;
		this.fogDistance = fogDistance;
	}

	@Override
	public ResourceLocation getStillTexture() {
		return stillTexture;
	}

	@Override
	public ResourceLocation getFlowingTexture() {
		return flowingTexture;
	}

	@Override
	public ResourceLocation getStillTexture(FluidStack stack) {
		return stillTexture;
	}

	@Override
	public ResourceLocation getFlowingTexture(FluidStack stack) {
		return flowingTexture;
	}

	@Override
	public ResourceLocation getStillTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		return stillTexture;
	}

	@Override
	public ResourceLocation getFlowingTexture(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		return flowingTexture;
	}

	@Override
	public int getTintColor(FluidStack stack) {
		return tintColor;
	}

	@Override
	public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		return tintColor;
	}

	@Override
	public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
		return fogColor == null ? fluidFogColor : fogColor;
	}

	@Override
	public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
		float modifier = fogDistance == null ? DEFAULT_FOG_DISTANCE : fogDistance.get();
		if (modifier != 1.0f) {
			RenderSystem.setShaderFogShape(FogShape.CYLINDER);
			RenderSystem.setShaderFogStart(-8);
			RenderSystem.setShaderFogEnd(96.0f * modifier);
		}
	}
}
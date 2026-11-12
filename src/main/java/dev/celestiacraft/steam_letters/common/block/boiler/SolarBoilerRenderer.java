package dev.celestiacraft.steam_letters.common.block.boiler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.celestiacraft.steam_letters.client.render.SlFluidRenderer;
import dev.celestiacraft.steam_letters.config.common.SolarBoilerConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;

public class SolarBoilerRenderer extends SmartBlockEntityRenderer<SolarBoilerBlockEntity> {
	private static final float INNER_MIN = 1 / 16.0F;
	private static final float INNER_MAX = 15 / 16.0F;
	private static final float INNER_BOTTOM = 2 / 16.0F;
	private static final float INNER_TOP = 14 / 16.0F;
	private static final float INNER_HEIGHT = INNER_TOP - INNER_BOTTOM;
	private static final float LEVEL_EPSILON = 1.0E-3F;
	private static final float TOP_CLEARANCE = 0.02F;
	private static final float WATER_ALPHA = 1.0F;
	private static final float STEAM_ALPHA = 0.45F;
	private static final float STEAM_TOP_STEP = 0.01F;
	private static final int FULL_BRIGHT = 0xF000F0;

	public SolarBoilerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(SolarBoilerBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		float waterFill = Mth.clamp(entity.getRenderedWaterLevel(partialTicks), 0.0F, 1.0F);
		float steamFill = Mth.clamp(entity.getRenderedSteamLevel(partialTicks), 0.0F, 1.0F);
		boolean renderFluid = SolarBoilerConfig.RENDER_FLUID.get();

		if (waterFill <= LEVEL_EPSILON && steamFill <= LEVEL_EPSILON) {
			return;
		}

		if (!renderFluid) {
			return;
		}

		stack.pushPose();

		float waterTop;
		FluidStack water = entity.getRenderedWater();
		if (waterFill > LEVEL_EPSILON && !water.isEmpty()) {
			waterTop = Math.min(INNER_BOTTOM + INNER_HEIGHT * waterFill, INNER_TOP - TOP_CLEARANCE);
			renderFluid(water, INNER_BOTTOM, waterTop, buffer, stack, light, WATER_ALPHA);
		}

		FluidStack steam = entity.getRenderedSteam();
		if (steamFill > LEVEL_EPSILON && !steam.isEmpty()) {
			float steamTop = INNER_TOP - TOP_CLEARANCE - STEAM_TOP_STEP;
			float steamBottom = Math.max(INNER_BOTTOM, steamTop - INNER_HEIGHT * steamFill);
			if (steamBottom < steamTop) {
				renderFluid(steam, steamBottom, steamTop, buffer, stack, FULL_BRIGHT, STEAM_ALPHA);
			}
		}

		stack.popPose();
	}

	private static void renderFluid(FluidStack fluid, float yMin, float yMax, MultiBufferSource buffer, PoseStack stack, int light, float alpha) {
		if (yMax - yMin <= LEVEL_EPSILON) {
			return;
		}

		SlFluidRenderer.renderFluidBox(
				fluid,
				INNER_MIN,
				yMin,
				INNER_MIN,
				INNER_MAX,
				yMax,
				INNER_MAX,
				buffer,
				stack,
				light,
				alpha
		);
	}
}
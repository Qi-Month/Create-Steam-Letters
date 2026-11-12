package dev.celestiacraft.steam_letters.common.block.advanced_spout;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.celestiacraft.steam_letters.client.block.SlBlockPartialModel;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;

public class AdvancedSpoutRenderer extends SafeBlockEntityRenderer<AdvancedSpoutBlockEntity> {
	public static final PartialModel[] BITS = {
			SlBlockPartialModel.SPOUT_TOP,
			SlBlockPartialModel.SPOUT_MIDDLE,
			SlBlockPartialModel.SPOUT_BOTTOM
	};

	public AdvancedSpoutRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(AdvancedSpoutBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		SmartFluidTankBehaviour tank = entity.getBehaviour(SmartFluidTankBehaviour.TYPE);
		if (tank == null) {
			return;
		}

		SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
		FluidStack fluidStack = primaryTank.getRenderedFluid();
		float level = primaryTank.getFluidLevel().getValue(partialTicks);

		boolean top = fluidStack.getFluid().getFluidType().isLighterThanAir();

		level = Math.max(level, 0.175f);
		float min = 2.5f / 16.0f;
		float max = min + (11 / 16.0f);
		float yOffset = (11 / 16.0f) * level;

		if (!fluidStack.isEmpty() && level != 0) {
			stack.pushPose();
			if (top) {
				stack.translate(0, max - min, 0);
			} else {
				stack.translate(0, yOffset, 0);
			}

			NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
					fluidStack,
					min,
					min - yOffset,
					min,
					max,
					min,
					max,
					source,
					stack,
					light,
					false,
					true
			);

			stack.popPose();
		}

		int processingTicks = entity.processingTicks;
		float processingPT = processingTicks - partialTicks;
		float cycle = Math.max(1, entity.getFillingTime());
		float pour = entity.getPourTick();
		float span = Mth.clamp(cycle - pour, 1, 10);
		float processingProgress = Mth.clamp(1 - (processingPT - pour) / span, 0, 1);
		float radius = 0;

		if (!fluidStack.isEmpty() && processingTicks != -1) {
			radius = (float) (StrictMath.pow(((2 * processingProgress) - 1), 2) - 1);
			AABB bb = new AABB(0.5, 0.0, 0.5, 0.5, -1.2, 0.5)
					.inflate(radius / 32.0f);
			NeoForgeCatnipServices.FLUID_RENDERER.renderFluidBox(
					fluidStack,
					(float) bb.minX,
					(float) bb.minY,
					(float) bb.minZ,
					(float) bb.maxX,
					(float) bb.maxY,
					(float) bb.maxZ,
					source,
					stack,
					light,
					true,
					true
			);
		}

		float squeeze = radius;
		float midpoint = (cycle + pour) / 2.0f;
		if (processingPT < 0) {
			squeeze = 0;
		} else if (processingPT < 2) {
			squeeze = Mth.lerp(processingPT / 2.0f, 0, -1);
		} else if (processingPT < midpoint) {
			squeeze = -1;
		}

		stack.pushPose();
		for (PartialModel bit : BITS) {
			CachedBuffers.partial(bit, entity.getBlockState())
					.light(light)
					.renderInto(stack, source.getBuffer(RenderType.solid()));
			stack.translate(0, -3 * squeeze / 32.0f, 0);
		}
		stack.popPose();
	}
}
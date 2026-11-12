package dev.celestiacraft.steam_letters.common.block.steam_mixer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.celestiacraft.steam_letters.client.block.SlBlockPartialModel;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SteamMixerRenderer extends KineticBlockEntityRenderer<SteamMixerBlockEntity> {
	public SteamMixerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(@NotNull SteamMixerBlockEntity entity) {
		return true;
	}

	@Override
	protected void renderSafe(SteamMixerBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		if (VisualizationManager.supportsVisualization(entity.getLevel())) {
			return;
		}

		BlockState blockState = entity.getBlockState();
		VertexConsumer solidBuffer = source.getBuffer(RenderType.solid());

		SuperByteBuffer cogwheelRender = CachedBuffers.partial(SlBlockPartialModel.STEAM_MIXER_COGWHEEL, blockState);
		standardKineticRotationTransform(cogwheelRender, entity, light).renderInto(stack, solidBuffer);

		float renderedHeadOffset = entity.getRenderedHeadOffset(partialTicks);
		float speed = entity.getRenderedHeadRotationSpeed(partialTicks);
		float time = AnimationTickHolder.getRenderTime(entity.getLevel());
		float angle = ((time * speed * 6 / 10.0f) % 360) / 180 * (float) Math.PI;

		SuperByteBuffer poleRender = CachedBuffers.partial(SlBlockPartialModel.STEAM_MIXER_POLE, blockState);
		poleRender.translate(0, -renderedHeadOffset, 0)
				.light(light)
				.renderInto(stack, solidBuffer);

		VertexConsumer cutoutBuffer = source.getBuffer(RenderType.cutoutMipped());
		SuperByteBuffer headRender = CachedBuffers.partial(SlBlockPartialModel.STEAM_MIXER_HEAD, blockState);
		headRender.rotateCentered(angle, Direction.UP)
				.translate(0, -renderedHeadOffset, 0)
				.light(light)
				.renderInto(stack, cutoutBuffer);
	}
}
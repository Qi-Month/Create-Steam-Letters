package dev.celestiacraft.steam_letters.common.block.boiler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.celestiacraft.steam_letters.client.block.SlBlockPartialModel;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * 多方块锅炉的仪表盘与过热蒸汽组件渲染.
 * <p>
 * 仪表盘照原版大锅炉的排布画在结构四个侧面的中央, 指针角度按当前等级换算; 组件只装在控制器那一格,
 * 因此整个结构只绘制一次.
 */
public class SteamBoilerRenderer extends SmartBlockEntityRenderer<SteamBoilerBlockEntity> {
	public SteamBoilerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(SteamBoilerBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		if (!entity.isController()) {
			return;
		}

		int brightness = brighten(entity, light);
		VertexConsumer vertex = buffer.getBuffer(RenderType.cutout());
		renderGauges(entity, partialTicks, stack, vertex, brightness);
		if (entity.hasComponent()) {
			CachedBuffers.partial(SlBlockPartialModel.STEAM_BOILER_COMPONENT, entity.getBlockState())
					.light(brightness)
					.renderInto(stack, vertex);
		}
	}

	/**
	 * 控制器在结构底层角块, 常被结构自身与周围方块压暗, 因此与结构中心的光照取较亮的一份.
	 */
	private int brighten(SteamBoilerBlockEntity entity, int light) {
		Level level = entity.getLevel();
		if (level == null) {
			return light;
		}

		int width = entity.getWidth();
		int height = entity.getHeight();
		BlockPos center = entity.getBlockPos().offset(width / 2, (height - 1) / 2, width / 2);
		int blockLight = Math.max(LightTexture.block(light), level.getBrightness(LightLayer.BLOCK, center));
		int skyLight = Math.max(LightTexture.sky(light), level.getBrightness(LightLayer.SKY, center));
		return LightTexture.pack(blockLight, skyLight);
	}

	/**
	 * 结构成形后才画仪表盘, 被方块挡住的方向直接跳过, 指针角度取等级与上限的比值.
	 */
	private void renderGauges(SteamBoilerBlockEntity entity, float partialTicks, PoseStack stack, VertexConsumer vertex, int light) {
		Level level = entity.getLevel();
		int width = entity.getWidth();
		if (level == null || width < SteamBoilerBlockEntity.MIN_WIDTH) {
			return;
		}

		BlockState state = entity.getBlockState();
		float progress = entity.getGaugeValue(partialTicks);
		float dialPivotY = 6.0f / 16;
		float dialPivotZ = 8.0f / 16;

		stack.pushPose();
		stack.translate(width / 2.0f, 0.5F, width / 2.0f);
		for (Direction direction : Iterate.horizontalDirections) {
			if (isOccluded(level, entity, direction)) {
				continue;
			}

			stack.pushPose();
			stack.mulPose(Axis.YP.rotationDegrees(-direction.toYRot() - 90));
			stack.translate(-0.5F, -0.5F, -0.5F);
			stack.translate(width / 2.0f - 6 / 16.00f, 0, 0);
			CachedBuffers.partial(AllPartialModels.BOILER_GAUGE, state)
					.light(light)
					.renderInto(stack, vertex);

			stack.translate(0, dialPivotY, dialPivotZ);
			stack.mulPose(Axis.XP.rotationDegrees(-145 * progress + 90));
			stack.translate(0, -dialPivotY, -dialPivotZ);
			CachedBuffers.partial(AllPartialModels.BOILER_GAUGE_DIAL, state)
					.light(light)
					.renderInto(stack, vertex);
			stack.popPose();
		}
		stack.popPose();
	}

	/**
	 * 与原版大锅炉相同的遮挡判定: 在结构侧面外推一个扁平包围盒, 与方块碰撞即视为被挡住.
	 */
	private boolean isOccluded(Level level, SteamBoilerBlockEntity entity, Direction direction) {
		int width = entity.getWidth();
		AABB box = new AABB(entity.getBlockPos())
				.move(width / 2.0f - 0.5F, 0, width / 2.0f - 0.5F)
				.deflate(5.0f / 8);
		box = box.move(direction.getStepX() * (width / 2.0f + 1 / 4.0f), 0, direction.getStepZ() * (width / 2.0f + 1 / 4.0f));
		box = box.inflate(Math.abs(direction.getStepZ()) / 2.0f, 0.25F, Math.abs(direction.getStepX()) / 2.0f);
		return !level.noCollision(box);
	}

	@Override
	public boolean shouldRenderOffScreen(SteamBoilerBlockEntity entity) {
		return entity.isController();
	}
}
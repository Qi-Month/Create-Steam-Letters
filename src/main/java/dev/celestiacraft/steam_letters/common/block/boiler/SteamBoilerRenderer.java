package dev.celestiacraft.steam_letters.common.block.boiler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.celestiacraft.steam_letters.client.block.SlBlockPartialModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * 过热蒸汽组件的模型渲染.
 * <p>
 * 组件只装在控制器那一格, 因此整个结构只绘制一次; 造型与位置沿用模型 json 自带的坐标.
 */
public class SteamBoilerRenderer extends SmartBlockEntityRenderer<SteamBoilerBlockEntity> {
	public SteamBoilerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(SteamBoilerBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		if (!entity.isController() || !entity.hasComponent()) {
			return;
		}

		BakedModel model = SlBlockPartialModel.STEAM_BOILER_COMPONENT.get();
		Minecraft.getInstance()
				.getBlockRenderer()
				.getModelRenderer()
				.renderModel(
						stack.last(),
						buffer.getBuffer(RenderType.cutout()),
						null,
						model,
						1.0F,
						1.0F,
						1.0F,
						light,
						overlay,
						ModelData.EMPTY,
						RenderType.cutout()
				);
	}
}
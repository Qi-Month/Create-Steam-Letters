package dev.celestiacraft.steam_letters.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 自己实现的流体盒渲染, 代替 catnip 的 FluidRenderHelper.renderFluidBox. 
 * <p>
 * catnip 那套走的是 ponder 专用的 RenderType, 在普通的 BlockEntityRenderer 里
 * 对自定义流体(动画贴图 / 气体)会渲不出来或者采样越界, 所以这里改用原版的
 * {@link RenderType#translucent()} 手写六个面, 每个面正反各画一次, 免得被背面剔除. 
 */
public class SlFluidRenderer {
	public static void renderFluidBox(FluidStack stack, float x1, float y1, float z1, float x2, float y2, float z2, MultiBufferSource buffer, PoseStack ms, int light, float alpha) {
		if (stack.isEmpty()) {
			return;
		}

		IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(stack.getFluid());
		ResourceLocation still = ext.getStillTexture(stack);
		TextureAtlasSprite sprite = Minecraft.getInstance()
				.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
				.apply(still == null ? MissingTextureAtlasSprite.getLocation() : still);

		int color = ext.getTintColor(stack);
		float r = ((color >> 16) & 0xFF) / 255.0F;
		float g = ((color >> 8) & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;
		float a = ((color >> 24) & 0xFF) / 255.0F;
		if (a <= 0.0F) {
			a = 1.0F;
		}
		a *= alpha;

		VertexConsumer vc = buffer.getBuffer(RenderType.translucent());
		Matrix4f pose = ms.last().pose();
		Matrix3f normal = ms.last().normal();

		float u0 = sprite.getU0();
		float u1 = sprite.getU1();
		float v0 = sprite.getV0();
		float v1 = sprite.getV1();

		// 底面 (-Y)
		quad(vc, pose, normal, x1, y1, z1, x1, y1, z2, x2, y1, z2, x2, y1, z1, 0.0F, -1.0F, 0.0F, u0, u1, v0, v1, r, g, b, a, light);
		// 顶面 (+Y)
		quad(vc, pose, normal, x1, y2, z2, x1, y2, z1, x2, y2, z1, x2, y2, z2, 0.0F, 1.0F, 0.0F, u0, u1, v0, v1, r, g, b, a, light);
		// 北面 (-Z)
		quad(vc, pose, normal, x2, y1, z1, x2, y2, z1, x1, y2, z1, x1, y1, z1, 0.0F, 0.0F, -1.0F, u0, u1, v0, v1, r, g, b, a, light);
		// 南面 (+Z)
		quad(vc, pose, normal, x1, y1, z2, x1, y2, z2, x2, y2, z2, x2, y1, z2, 0.0F, 0.0F, 1.0F, u0, u1, v0, v1, r, g, b, a, light);
		// 西面 (-X)
		quad(vc, pose, normal, x1, y1, z1, x1, y2, z1, x1, y2, z2, x1, y1, z2, -1.0F, 0.0F, 0.0F, u0, u1, v0, v1, r, g, b, a, light);
		// 东面 (+X)
		quad(vc, pose, normal, x2, y1, z2, x2, y2, z2, x2, y2, z1, x2, y1, z1, 1.0F, 0.0F, 0.0F, u0, u1, v0, v1, r, g, b, a, light);
	}

	private static void quad(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz, float nx, float ny, float nz, float u0, float u1, float v0, float v1, float r, float g, float b, float a, int light) {
		vertex(consumer, matrix4f, matrix3f, bx, by, bz, u1, v1, nx, ny, nz, r, g, b, a, light);
		vertex(consumer, matrix4f, matrix3f, ax, ay, az, u0, v1, nx, ny, nz, r, g, b, a, light);
		vertex(consumer, matrix4f, matrix3f, dx, dy, dz, u0, v0, nx, ny, nz, r, g, b, a, light);
		vertex(consumer, matrix4f, matrix3f, cx, cy, cz, u1, v0, nx, ny, nz, r, g, b, a, light);

		// 反向再来一遍, 免得开了背面剔除之后从里面看不见
		vertex(consumer, matrix4f, matrix3f, cx, cy, cz, u1, v0, -nx, -ny, -nz, r, g, b, a, light);
		vertex(consumer, matrix4f, matrix3f, dx, dy, dz, u0, v0, -nx, -ny, -nz, r, g, b, a, light);
		vertex(consumer, matrix4f, matrix3f, ax, ay, az, u0, v1, -nx, -ny, -nz, r, g, b, a, light);
		vertex(consumer, matrix4f, matrix3f, bx, by, bz, u1, v1, -nx, -ny, -nz, r, g, b, a, light);
	}

	private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float z, float u, float v, float nx, float ny, float nz, float r, float g, float b, float a, int light) {
		Vector3f normal = matrix3f.transform(new Vector3f(nx, ny, nz));
		consumer.addVertex(matrix4f, x, y, z)
				.setColor(r, g, b, a)
				.setUv(u, v)
				.setLight(light)
				.setNormal(normal.x, normal.y, normal.z);
	}
}
package dev.celestiacraft.steam_letters.common.block.flywheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.client.block.SlBlockPartialModel;
import dev.celestiacraft.steam_letters.compat.shaders.ShaderCompat;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 飞轮的后端渲染: 半轴交给父类, 轮盘与四根传动杆在这里补上.
 * <p>
 * 平时只要 Flywheel 可视化后端可用就提前返回, 由 {@link SteamFlywheelInstance} 负责画这些部件.
 * 唯一例外是光影包生效时: Create 的 rotating 实例类型在光影下可能被整批丢弃, 这时继续用这里的
 * 后端渲染兜底, 两份几何位置与角度完全重合, 不会出现重影.
 */
public class SteamFlywheelRenderer extends KineticBlockEntityRenderer<SteamFlywheelBlockEntity> {
	public SteamFlywheelRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(@NotNull SteamFlywheelBlockEntity entity) {
		return true;
	}

	@Override
	protected void renderSafe(SteamFlywheelBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		super.renderSafe(entity, partialTicks, stack, source, light, overlay);

		if (entity.getLevel() != null && VisualizationManager.supportsVisualization(entity.getLevel())
				&& !ShaderCompat.isShaderPackInUse()) {
			return;
		}

		BlockState state = entity.getBlockState();
		Direction facing = state.getValue(HorizontalKineticBlock.HORIZONTAL_FACING);

		float speed = entity.visualSpeed.get(partialTicks) * 3.0F / 10.0F;
		float angle = entity.angle + speed * partialTicks;

		VertexConsumer buffer = source.getBuffer(RenderType.solid());

		Direction connection = SteamFlywheelBlock.getConnection(state);
		if (connection != null) {
			int connectorLight = LevelRenderer.getLightColor(entity.getLevel(), state, entity.getBlockPos().relative(connection));
			boolean flip = state.getValue(SteamFlywheelBlock.CONNECTION) == SteamFlywheelBlock.ConnectionState.LEFT;
			boolean flipAngle = connection.getAxis() == Direction.Axis.X ^ connection.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
			float rotation = flipAngle ? -angle : angle;

			ResourceLocation material = material(state);

			transformConnector(rotateToFacing(CachedBuffers.partial(SlBlockPartialModel.flywheelConnector(material, SlBlockPartialModel.UPPER_ROTATING_CONNECTOR), state), connection), true, true, rotation, flip).light(connectorLight).renderInto(stack, buffer);
			transformConnector(rotateToFacing(CachedBuffers.partial(SlBlockPartialModel.flywheelConnector(material, SlBlockPartialModel.LOWER_ROTATING_CONNECTOR), state), connection), false, true, rotation, flip).light(connectorLight).renderInto(stack, buffer);
			transformConnector(rotateToFacing(CachedBuffers.partial(SlBlockPartialModel.flywheelConnector(material, SlBlockPartialModel.UPPER_SLIDING_CONNECTOR), state), connection), true, false, rotation, flip).light(connectorLight).renderInto(stack, buffer);
			transformConnector(rotateToFacing(CachedBuffers.partial(SlBlockPartialModel.flywheelConnector(material, SlBlockPartialModel.LOWER_SLIDING_CONNECTOR), state), connection), false, false, rotation, flip).light(connectorLight).renderInto(stack, buffer);
		}

		BlockState referenceState = state.rotate(entity.getLevel(), entity.getBlockPos(), Rotation.CLOCKWISE_90);
		Direction referenceFacing = referenceState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING);

		SuperByteBuffer wheel = CachedBuffers.partialFacing(SlBlockPartialModel.flywheelWheel(material(state)), referenceState, referenceFacing);
		kineticRotationTransform(wheel, entity, facing.getAxis(), AngleHelper.rad(angle), light);
		wheel.renderInto(stack, buffer);
	}

	/**
	 * 轮盘与四根传动杆都跟随方块自己的材质.
	 */
	private ResourceLocation material(BlockState state) {
		return state.getBlock() instanceof SteamFlywheelBlock flywheel ? flywheel.getMaterial() : SlApis.loadResource("bronze");
	}

	@Override
	protected SuperByteBuffer getRotatedModel(SteamFlywheelBlockEntity entity, BlockState state) {
		return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, state.getValue(HorizontalKineticBlock.HORIZONTAL_FACING).getOpposite());
	}

	private SuperByteBuffer transformConnector(SuperByteBuffer buffer, boolean upper, boolean rotating, float angle, boolean flip) {
		float shift = upper ? 0.25F : -0.125F;
		float offset = 0.25F;
		float radians = angle / 180.0F * Mth.PI;
		float shifting = Mth.sin(radians) * shift + offset;
		float maxAngle = upper ? -5.0F : -15.0F;
		float minAngle = upper ? -45.0F : 5.0F;
		float barAngle = rotating ? Mth.lerp((Mth.sin(radians + Mth.HALF_PI) + 1.0F) / 2.0F, minAngle, maxAngle) : 0.0F;

		float pivotX = (upper ? 8.0F : 3.0F) / 16.0F;
		float pivotY = (upper ? 8.0F : 2.0F) / 16.0F;
		float pivotZ = (upper ? 23.0F : 21.5F) / 16.0F;

		buffer.translate(pivotX, pivotY, pivotZ + shifting);
		if (rotating) {
			buffer.rotate(Direction.Axis.X, AngleHelper.rad(barAngle));
		}
		buffer.translate(-pivotX, -pivotY, -pivotZ);

		if (flip && !upper) {
			buffer.translate(0.5625F, 0.0F, 0.0F);
		}

		return buffer;
	}

	private SuperByteBuffer rotateToFacing(SuperByteBuffer buffer, Direction direction) {
		buffer.rotateCentered(AngleHelper.rad(AngleHelper.horizontalAngle(direction)), Direction.Axis.Y);
		return buffer;
	}
}
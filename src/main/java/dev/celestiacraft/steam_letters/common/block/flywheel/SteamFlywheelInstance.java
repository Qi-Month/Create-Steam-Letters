package dev.celestiacraft.steam_letters.common.block.flywheel;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.client.block.SlBlockPartialModel;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SteamFlywheelInstance extends ShaftVisual<SteamFlywheelBlockEntity> implements SimpleDynamicVisual {
	private final Direction facing;
	private final Direction connection;
	private final boolean connectedLeft;

	private final TransformedInstance wheel;
	private final TransformedInstance upperRotating;
	private final TransformedInstance lowerRotating;
	private final TransformedInstance upperSliding;
	private final TransformedInstance lowerSliding;
	private final List<TransformedInstance> connectors = new ArrayList<>(4);

	private final Matrix4f baseTransform = new Matrix4f();
	private float lastAngle = Float.NaN;

	public SteamFlywheelInstance(VisualizationContext context, SteamFlywheelBlockEntity entity, float partialTick) {
		super(context, entity, partialTick);

		facing = blockState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING);
		connection = SteamFlywheelBlock.getConnection(blockState);
		connectedLeft = blockState.getValue(SteamFlywheelBlock.CONNECTION) == SteamFlywheelBlock.ConnectionState.LEFT;

		wheel = instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(SlBlockPartialModel.flywheelWheel(material()), facing.getClockWise()))
				.createInstance();

		if (connection == null) {
			upperRotating = null;
			lowerRotating = null;
			upperSliding = null;
			lowerSliding = null;
		} else {
			upperRotating = createConnector(SlBlockPartialModel.flywheelConnector(material(), SlBlockPartialModel.UPPER_ROTATING_CONNECTOR));
			lowerRotating = createConnector(SlBlockPartialModel.flywheelConnector(material(), SlBlockPartialModel.LOWER_ROTATING_CONNECTOR));
			upperSliding = createConnector(SlBlockPartialModel.flywheelConnector(material(), SlBlockPartialModel.UPPER_SLIDING_CONNECTOR));
			lowerSliding = createConnector(SlBlockPartialModel.flywheelConnector(material(), SlBlockPartialModel.LOWER_SLIDING_CONNECTOR));
		}

		wheel.translate(getVisualPosition());
		baseTransform.set(wheel.pose);

		animate(entity.angle);
		lastAngle = entity.angle;
	}

	/**
	 * 轮盘与四根传动杆都跟随方块自己的材质.
	 */
	private ResourceLocation material() {
		return blockState.getBlock() instanceof SteamFlywheelBlock flywheel
				? flywheel.getMaterial()
				: SlApis.loadResource("bronze");
	}

	private TransformedInstance createConnector(PartialModel model) {
		TransformedInstance connector = instancerProvider()
				.instancer(InstanceTypes.TRANSFORMED, Models.partial(model))
				.createInstance();
		connectors.add(connector);
		return connector;
	}

	@Override
	public void beginFrame(DynamicVisual.Context context) {
		float partialTicks = context.partialTick();

		float speed = blockEntity.visualSpeed.get(partialTicks) * 3.0F / 10.0F;
		float angle = blockEntity.angle + speed * partialTicks;

		if (Math.abs(angle - lastAngle) < 0.001F) {
			return;
		}

		animate(angle);
		lastAngle = angle;
	}

	private void animate(float angle) {
		wheel.setTransform(baseTransform);
		wheel.center();
		wheel.rotate(AngleHelper.rad(angle), rotationAxis());
		wheel.uncenter();
		wheel.setChanged();

		if (connection == null) {
			return;
		}

		boolean flipAngle = connection.getAxis() == Direction.Axis.X ^ connection.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
		float rotation = flipAngle ? -angle : angle;

		animateConnector(upperRotating, true, true, rotation);
		animateConnector(lowerRotating, false, true, rotation);
		animateConnector(upperSliding, true, false, rotation);
		animateConnector(lowerSliding, false, false, rotation);
	}

	private void animateConnector(TransformedInstance connector, boolean upper, boolean rotating, float angle) {
		connector.setTransform(baseTransform);
		connector.rotateCentered(AngleHelper.rad(AngleHelper.horizontalAngle(connection)), Direction.Axis.Y);

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

		connector.translate(pivotX, pivotY, pivotZ + shifting);
		if (rotating) {
			connector.rotate(AngleHelper.rad(barAngle), Direction.Axis.X);
		}
		connector.translate(-pivotX, -pivotY, -pivotZ);

		if (connectedLeft && !upper) {
			connector.translate(0.5625F, 0.0F, 0.0F);
		}

		connector.setChanged();
	}

	@Override
	public void updateLight(float partialTick) {
		super.updateLight(partialTick);
		relight(wheel);

		if (connection != null) {
			relight(getVisualPosition().relative(connection), connectors.toArray(new FlatLit[0]));
		}
	}

	@Override
	protected void _delete() {
		super._delete();
		wheel.delete();
		connectors.forEach(Instance::delete);
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		super.collectCrumblingInstances(consumer);
		consumer.accept(wheel);
		connectors.forEach(consumer);
	}
}
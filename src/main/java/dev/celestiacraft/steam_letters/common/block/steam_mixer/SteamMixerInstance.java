package dev.celestiacraft.steam_letters.common.block.steam_mixer;

import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.celestiacraft.steam_letters.client.block.SlBlockPartialModel;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

public class SteamMixerInstance extends SingleAxisRotatingVisual<SteamMixerBlockEntity> implements SimpleDynamicVisual {
	private final RotatingInstance mixerHead;
	private final OrientedInstance mixerPole;

	public SteamMixerInstance(VisualizationContext context, SteamMixerBlockEntity entity, float partialTick) {
		super(context, entity, partialTick, Models.partial(SlBlockPartialModel.STEAM_MIXER_COGWHEEL));

		mixerHead = instancerProvider().instancer(
				AllInstanceTypes.ROTATING,
				Models.partial(SlBlockPartialModel.STEAM_MIXER_HEAD)
		).createInstance();

		mixerHead.setRotationAxis(Direction.Axis.Y);

		mixerPole = instancerProvider().instancer(
				InstanceTypes.ORIENTED,
				Models.partial(SlBlockPartialModel.STEAM_MIXER_POLE)
		).createInstance();

		animate(partialTick);
	}

	@Override
	public void beginFrame(DynamicVisual.Context context) {
		animate(context.partialTick());
	}

	private void animate(float partialTick) {
		float renderedHeadOffset = blockEntity.getRenderedHeadOffset(partialTick);

		transformPole(renderedHeadOffset);
		transformHead(renderedHeadOffset, partialTick);
	}

	private void transformHead(float renderedHeadOffset, float partialTick) {
		float speed = blockEntity.getRenderedHeadRotationSpeed(partialTick);

		mixerHead.setPosition(getVisualPosition())
				.nudge(0, -renderedHeadOffset, 0)
				.setRotationalSpeed(speed * 2 * RotatingInstance.SPEED_MULTIPLIER)
				.setChanged();
	}

	private void transformPole(float renderedHeadOffset) {
		mixerPole.position(getVisualPosition())
				.translatePosition(0, -renderedHeadOffset, 0)
				.setChanged();
	}

	@Override
	public void updateLight(float partialTick) {
		super.updateLight(partialTick);
		relight(pos.below(), mixerHead);
		relight(mixerPole);
	}

	@Override
	protected void _delete() {
		super._delete();
		mixerHead.delete();
		mixerPole.delete();
	}

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		super.collectCrumblingInstances(consumer);
		consumer.accept(mixerHead);
		consumer.accept(mixerPole);
	}
}
package dev.celestiacraft.steam_letters.common.block.steam_pump;

import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.celestiacraft.steam_letters.tags.SlFluidTags;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BronzeSteamPumpBlockEntity extends PumpBlockEntity {
	private static final float DEFAULT_PUMP_SPEED = 256.0F;
	private static final int IDLE_POLL_INTERVAL = 5;
	private static final int BLOCKED_POLL_INTERVAL = 20;
	private static final int PRESSURE_REFRESH_INTERVAL = 200;
	private static final int MAX_SCAN_NODES = 256;
	private static final int MAX_SCAN_ENDPOINTS = 64;
	private static final int PROBE_AMOUNT = 1000;

	/**
	 * 当前管网是否允许泵送. 为 false 时泵送速度归零, 等同挂起加压
	 */
	private boolean steamAllowed = true;

	/**
	 * 抽取侧上一次观测到的流体. 只有它发生变化才值得重新做整网判断
	 */
	@Nullable
	private Fluid observedFluid;

	/**
	 * 两侧紧邻方块状态的缓存, 仅用于识别连点变化
	 */
	private BlockState lastFrontState;
	private BlockState lastBackState;

	public BronzeSteamPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void initialize() {
		super.initialize();

		if (level != null && !level.isClientSide()) {
			refreshSteamGate();
			updatePressureChange();
		}
	}

	@Override
	public void tick() {
		if (level != null && !level.isClientSide()) {
			pollSourceFluid();
			refreshNeighborLinks();
		}

		super.tick();
	}

	/**
	 * 抽取侧紧邻位置的流体是整张网管的取样点. 这里只做 O(1) 读取, 与上次不同才触发整网判断
	 * 泵送中降频到 5 tick 取样一次, 停泵后放宽到 20 tick 并持续尝试恢复
	 */
	private void pollSourceFluid() {
		Direction front = getFront();
		if (front == null) {
			return;
		}

		int interval = steamAllowed ? IDLE_POLL_INTERVAL : BLOCKED_POLL_INTERVAL;
		if (level.getGameTime() % interval != 0) {
			return;
		}

		Direction side = front.getOpposite();
		Fluid current = peekSourceFluid(side);

		if (steamAllowed && Objects.equals(current, observedFluid)) {
			return;
		}

		observedFluid = current;
		applySteamGate(isSideSteamSafe(side));
	}

	/**
	 * 读取抽取侧紧邻位置的流体, 仅作为"流体更换"的信号源, 不参与最终判定
	 */
	@Nullable
	private Fluid peekSourceFluid(Direction side) {
		BlockPos target = worldPosition.relative(side);

		if (!level.isLoaded(target)) {
			return null;
		}

		FluidTransportBehaviour pipe = FluidPropagator.getPipe(level, target);
		if (pipe != null) {
			FluidStack stack = pipe.getProvidedOutwardFluid(side.getOpposite());
			return stack == null || stack.isEmpty() ? null : stack.getFluid();
		}

		BlockEntity blockEntity = level.getBlockEntity(target);
		if (blockEntity == null) {
			return null;
		}

		IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, target, side.getOpposite());
		if (handler == null) {
			return null;
		}

		FluidStack stack = handler.getFluidInTank(0);
		return stack.isEmpty() ? null : stack.getFluid();
	}

	/**
	 * 管道或容器变动时重建压力. 泵不参与动力网络, 原版靠转速变化触发重建的路径这里不存在
	 */
	private void refreshNeighborLinks() {
		Direction front = getFront();
		if (front == null) {
			return;
		}

		BlockState frontState = level.getBlockState(worldPosition.relative(front));
		BlockState backState = level.getBlockState(worldPosition.relative(front.getOpposite()));

		if (frontState == lastFrontState && backState == lastBackState) {
			if (level.getGameTime() % PRESSURE_REFRESH_INTERVAL != 0) {
				return;
			}
		}

		lastFrontState = frontState;
		lastBackState = backState;
		updatePressureChange();
	}

	/**
	 * 青铜蒸汽泵不吃应力. 恒定以机械动力配置允许的最大转速运转
	 * 配置尚未加载时退回默认值
	 */
	public static float getPumpSpeed() {
		try {
			return AllConfigs.server().kinetics.maxRotationSpeed.get();
		} catch (IllegalStateException | NullPointerException e) {
			return DEFAULT_PUMP_SPEED;
		}
	}

	@Override
	public float getSpeed() {
		return steamAllowed ? getPumpSpeed() : 0.0F;
	}

	@Override
	public float getTheoreticalSpeed() {
		return getSpeed();
	}

	@Override
	public float calculateStressApplied() {
		return 0.0F;
	}

	/**
	 * 判定结果在取样时才刷新, 加压时只读缓存, 不做遍历
	 */
	@Override
	protected void distributePressureTo(Direction direction) {
		if (!steamAllowed) {
			return;
		}

		super.distributePressureTo(direction);
	}

	/**
	 * 重新判定抽取侧, 只在取样信号变化或方位置变动时调用
	 */
	private void refreshSteamGate() {
		Direction front = getFront();
		if (front == null) {
			return;
		}

		applySteamGate(isSideSteamSafe(front.getOpposite()));
	}

	private void applySteamGate(boolean allowed) {
		if (allowed == steamAllowed) {
			return;
		}

		steamAllowed = allowed;
		updatePressureChange();

		if (!steamAllowed) {
			stopFlows();
		}
	}

	/**
	 * 判定失效时立刻切断两侧已建立的输运, 不等管道自行发现压力消失
	 */
	private void stopFlows() {
		Direction front = getFront();
		if (front == null) {
			return;
		}

		FluidPropagator.resetAffectedFluidNetworks(level, worldPosition, front);
		FluidPropagator.resetAffectedFluidNetworks(level, worldPosition, front.getOpposite());
	}

	/**
	 * 抽取侧的整张管网中, 只要有任何一个端点会给出非蒸汽流体, 就判定为不可泵送
	 */
	private boolean isSideSteamSafe(Direction side) {
		Set<BlockPos> visited = new HashSet<>();
		List<ScanNode> frontier = new ArrayList<>();
		int maxDistance = FluidPropagator.getPumpRange();
		int endpointCount = 0;

		frontier.add(new ScanNode(worldPosition.relative(side), side.getOpposite(), 0));

		while (!frontier.isEmpty()) {
			ScanNode node = frontier.remove(0);

			if (visited.size() > MAX_SCAN_NODES) {
				return true;
			}
			if (!level.isLoaded(node.pos)) {
				continue;
			}
			if (!visited.add(node.pos)) {
				continue;
			}

			BlockState state = level.getBlockState(node.pos);
			FluidTransportBehaviour pipe = FluidPropagator.getPipe(level, node.pos);

			if (pipe != null && pipe.canHaveFlowToward(state, node.cameFrom)) {
				if (node.distance >= maxDistance) {
					continue;
				}

				for (Direction direction : Iterate.directions) {
					if (direction == node.cameFrom || !pipe.canHaveFlowToward(state, direction)) {
						continue;
					}

					frontier.add(new ScanNode(node.pos.relative(direction), direction.getOpposite(), node.distance + 1));
				}
				continue;
			}

			if (++endpointCount > MAX_SCAN_ENDPOINTS) {
				return true;
			}

			if (!isEndpointSteamSafe(node.pos, node.cameFrom)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 端点用 drain 模拟真实抽取行为. 抽得出来的不是蒸汽就说明泵会把它抽进管网
	 */
	private boolean isEndpointSteamSafe(BlockPos pos, Direction face) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof PumpBlock) {
			return true;
		}

		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity == null) {
			return true;
		}

		IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, face);
		if (handler == null) {
			return true;
		}

		return isSteamOrEmpty(handler.drain(PROBE_AMOUNT, IFluidHandler.FluidAction.SIMULATE));
	}

	private static boolean isSteamOrEmpty(@Nullable FluidStack stack) {
		return stack == null
				|| stack.isEmpty()
				|| stack.getFluid().defaultFluidState().is(SlFluidTags.STEAM)
				|| stack.getFluid().defaultFluidState().is(SlFluidTags.SUPERHEATED_STEAM);
	}

	private static class ScanNode {
		private final BlockPos pos;
		private final Direction cameFrom;
		private final int distance;

		private ScanNode(BlockPos pos, Direction cameFrom, int distance) {
			this.pos = pos;
			this.cameFrom = cameFrom;
			this.distance = distance;
		}
	}
}
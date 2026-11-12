package dev.celestiacraft.steam_letters.common.block.bronze_basin;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.utils.SidedCapabilityCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BronzeBasinBlockEntity extends BasinBlockEntity {
	public static final int INPUT_SLOT_COUNT = 16;

	public BronzeBasinBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	private FilteringBehaviour filtering;

	private final SidedCapabilityCache<IFluidHandler> outputTargetCache =
			new SidedCapabilityCache<>(Capabilities.FluidHandler.BLOCK);

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		resizeInputInventory(compound.getCompound("InputItems"), registries);
		itemCapability = new CombinedInvWrapper(inputInventory, outputInventory);
	}

	/**
	 * 旧存档的输入槽数可能少于设计值, 反序列化会按存档里的槽数重建容器,
	 * 使 itemCapability 缓存的索引偏移与实际槽数错位, 外部按 getSlots 遍历时越界.
	 * 这里按设计槽数补齐, 原有物品保持在原槽位.
	 */
	private void resizeInputInventory(CompoundTag inputItems, HolderLookup.Provider registries) {
		if (!inputItems.contains("Size", Tag.TAG_INT)) {
			return;
		}

		if (inputItems.getInt("Size") == INPUT_SLOT_COUNT) {
			return;
		}

		CompoundTag resized = inputItems.copy();
		resized.putInt("Size", INPUT_SLOT_COUNT);
		inputInventory.deserializeNBT(registries, resized);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SlBlockEntities.BRONZE_BASIN.get(), (basin, direction) -> {
			return basin.itemCapability;
		});
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, SlBlockEntities.BRONZE_BASIN.get(), (basin, direction) -> {
			return basin.fluidCapability;
		});
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this));

		filtering = new FilteringBehaviour(this, new BronzeBasinValueBox())
				.withCallback((stack) -> {
					notifyChangeOfContents();
				})
				.forRecipes();
		behaviours.add(filtering);

		inputTank = new SmartFluidTankBehaviour(
				SmartFluidTankBehaviour.INPUT,
				this,
				2,
				BronzeBasinCapacity.slotCapacity(),
				true
		).whenFluidUpdates(this::notifyChangeOfContents);

		outputTank = new SmartFluidTankBehaviour(
				SmartFluidTankBehaviour.OUTPUT,
				this,
				2,
				BronzeBasinCapacity.slotCapacity(),
				true
		).whenFluidUpdates(this::notifyChangeOfContents)
				.forbidInsertion();

		behaviours.add(inputTank);
		behaviours.add(outputTank);

		fluidCapability = new CombinedTankWrapper(outputTank.getCapability(), inputTank.getCapability());
	}

	@Override
	public FilteringBehaviour getFilter() {
		return filtering;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		int titleIndex = tooltip.size();
		boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		if (added && tooltip.size() > titleIndex) {
			replaceGoggleTitle(tooltip, titleIndex);
		}
		return added;
	}

	private static void replaceGoggleTitle(List<Component> tooltip, int titleIndex) {
		List<Component> title = new ArrayList<>();
		CreateLang.translate("gui.goggles.basin_contents").forGoggles(title);

		if (!title.isEmpty()) {
			tooltip.set(titleIndex, title.getFirst());
		}
	}

	@Override
	public boolean acceptOutputs(List<ItemStack> outputItems, List<FluidStack> outputFluids, boolean simulate) {
		if (outputFluids.isEmpty()) {
			return super.acceptOutputs(outputItems, outputFluids, simulate);
		}

		outputTank.allowInsertion();
		try {
			IFluidHandler targetTank = outputTank.getCapability();
			if (targetTank == null) {
				return false;
			}

			if (!super.acceptOutputs(outputItems, Collections.emptyList(), true)) {
				return false;
			}
			if (!acceptFluidsIntoOutputTank(outputFluids, true, targetTank)) {
				return false;
			}

			if (simulate) {
				return true;
			}

			if (!super.acceptOutputs(outputItems, Collections.emptyList(), false)) {
				return false;
			}
			return acceptFluidsIntoOutputTank(outputFluids, false, targetTank);
		} finally {
			outputTank.forbidInsertion();
		}
	}

	@Override
	public float getTotalFluidUnits(float partialTicks) {
		if (level != null && level.isClientSide())
			return shouldRenderCopperBasinMixedSurfaceParticles()
					? BronzeBasinCapacity.renderFullCapacity()
					: 0;

		return super.getTotalFluidUnits(partialTicks);
	}

	private boolean shouldRenderCopperBasinMixedSurfaceParticles() {
		List<FluidStack> fullFluids = new ArrayList<>(2);

		if (!collectFullSlotFluids(inputTank, fullFluids)) {
			return false;
		}
		if (!collectFullSlotFluids(outputTank, fullFluids)) {
			return false;
		}

		if (fullFluids.size() != 2) {
			return false;
		}

		return !FluidStack.isSameFluidSameComponents(fullFluids.get(0), fullFluids.get(1));
	}

	private static boolean collectFullSlotFluids(SmartFluidTankBehaviour behaviour, List<FluidStack> fullFluids) {
		if (behaviour == null) {
			return true;
		}

		IFluidHandler handler = behaviour.getCapability();
		if (handler == null) {
			return true;
		}

		for (int tank = 0; tank < handler.getTanks(); tank++) {
			FluidStack fluidStack = handler.getFluidInTank(tank);
			if (fluidStack.isEmpty()) {
				continue;
			}

			if (fluidStack.getAmount() != BronzeBasinCapacity.slotCapacity()) {
				return false;
			}

			fullFluids.add(fluidStack);
			if (fullFluids.size() > 2) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide()) {
			return;
		}
		pushOutputFluidsToSpoutputTarget();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		outputTargetCache.clear();
	}

	private @Nullable IFluidHandler getSpoutputTarget(BlockPos targetPos, Direction direction) {
		if (level == null) {
			return null;
		}
		return outputTargetCache.get(level, targetPos, direction);
	}

	private void pushOutputFluidsToSpoutputTarget() {
		BlockState blockState = getBlockState();
		if (!(blockState.getBlock() instanceof BasinBlock)) {
			return;
		}

		Direction direction = blockState.getValue(BasinBlock.FACING);
		if (direction == Direction.DOWN) {
			return;
		}

		BlockPos targetPos = worldPosition.below().relative(direction);

		IFluidHandler sourceTank = outputTank.getCapability();
		if (sourceTank == null)
			return;

		IFluidHandler targetTank = getSpoutputTarget(targetPos, direction);
		if (targetTank == null) {
			return;
		}

		for (int slot = 0; slot < sourceTank.getTanks(); slot++) {
			FluidStack available = sourceTank.getFluidInTank(slot);
			if (available.isEmpty()) {
				continue;
			}
			if (movePartialFluid(sourceTank, targetTank, available.getAmount()) > 0) {
				notifyChangeOfContents();
				notifyUpdate();
				return;
			}
		}
	}

	private static int movePartialFluid(IFluidHandler sourceTank, IFluidHandler targetTank, int maxAmount) {
		FluidStack available = sourceTank.drain(maxAmount, IFluidHandler.FluidAction.SIMULATE);
		if (available.isEmpty()) {
			return 0;
		}

		int accepted = fillTarget(targetTank, copyWithAmount(available, available.getAmount()), IFluidHandler.FluidAction.SIMULATE);
		if (accepted <= 0) {
			return 0;
		}

		FluidStack toMove = copyWithAmount(available, Math.min(accepted, available.getAmount()));
		int moved = fillTarget(targetTank, toMove, IFluidHandler.FluidAction.EXECUTE);
		if (moved <= 0) {
			return 0;
		}
		sourceTank.drain(copyWithAmount(available, moved), IFluidHandler.FluidAction.EXECUTE);
		return moved;
	}

	private static FluidStack copyWithAmount(FluidStack stack, int amount) {
		if (stack.isEmpty() || amount <= 0) {
			return FluidStack.EMPTY;
		}
		FluidStack copy = stack.copy();
		copy.setAmount(amount);
		return copy;
	}

	private static boolean acceptFluidsIntoOutputTank(List<FluidStack> outputFluids, boolean simulate, IFluidHandler targetTank) {
		for (FluidStack fluidStack : outputFluids) {
			IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
			if (fillTarget(targetTank, fluidStack.copy(), action) != fluidStack.getAmount()) {
				return false;
			}
		}
		return true;
	}

	private static int fillTarget(IFluidHandler targetTank, FluidStack fluidStack, IFluidHandler.FluidAction action) {
		return targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler internalFluidHandler
				? internalFluidHandler.forceFill(fluidStack, action)
				: targetTank.fill(fluidStack, action);
	}
}
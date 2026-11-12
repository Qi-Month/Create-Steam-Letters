package dev.celestiacraft.steam_letters.common.block.steam_saw;

import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import dev.celestiacraft.steam_letters.api.machine.ISteamStorage;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.config.common.SteamSawConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SteamSawBlockEntity extends SawBlockEntity implements ISteamStorage {
	private FluidStack steam = FluidStack.EMPTY;
	private IFluidHandler steamCapability = steamFluidHandler();

	public SteamSawBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new SteamSawInventory(this);
		inventory.remainingTime = -1;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		appendSteamGoggle(tooltip);
		return true;
	}

	@Override
	public FluidStack getSteamStack() {
		return steam;
	}

	@Override
	public void setSteamStack(FluidStack stack) {
		int previous = steam.getAmount();
		steam = stack.isEmpty() ? FluidStack.EMPTY : stack.copy();
		setChanged();
		notifySteamChanged(previous);
	}

	@Override
	public int getSteamCapacity() {
		return SteamSawConfig.STEAM_CAPACITY.get();
	}

	@Override
	public int getSteamConsumption() {
		return SteamSawConfig.STEAM_CONSUMPTION.get();
	}

	@Override
	public int getParallelCount() {
		return SteamSawConfig.STEAM_SAW_PARALLEL.get();
	}

	@Override
	public void start(@NotNull ItemStack stack) {
		if (!hasEnoughSteam()) {
			keepIdle();
			return;
		}

		super.start(stack);

		if (inventory.remainingTime > 0) {
			float multiplier = getProcessingTimeMultiplier();
			inventory.remainingTime = Math.max(1, Math.round(inventory.remainingTime * multiplier));
			inventory.recipeDuration = Math.max(1, Math.round(inventory.recipeDuration * multiplier));
		}
	}

	@Override
	public void tick() {
		if (!level.isClientSide()) {
			keepIdle();
		}

		boolean applied = inventory.appliedRecipe;
		super.tick();

		if (level.isClientSide() || applied || !inventory.appliedRecipe) {
			return;
		}

		if (producedOutput()) {
			consumeSteam();
		}
	}

	private void keepIdle() {
		if (inventory.appliedRecipe) {
			return;
		}

		if (inventory.remainingTime <= 0 || !hasEnoughSteam()) {
			inventory.remainingTime = -1;
		}
	}

	private boolean producedOutput() {
		for (int slot = 1; slot < inventory.getSlots(); slot++) {
			if (!inventory.getStackInSlot(slot).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, SlBlockEntities.STEAM_SAW.get(), (saw, direction) -> {
			return saw.steamCapability;
		});
	}

	@Override
	public void write(@NotNull CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if (!steam.isEmpty()) {
			tag.put("Steam", steam.save(registries));
		}
	}

	@Override
	protected void read(@NotNull CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		steam = FluidStack.parseOptional(registries, tag.getCompound("Steam"));
	}

	/**
	 * 工作时间倍率: {@code processing_time_multiplier} 为 0.25 时, 耗时是原版机械锯的 0.25 倍(快 4 倍).
	 */
	private static float getProcessingTimeMultiplier() {
		return SteamSawConfig.PROCESSING_TIME_MULTIPLIER.get().floatValue();
	}
}
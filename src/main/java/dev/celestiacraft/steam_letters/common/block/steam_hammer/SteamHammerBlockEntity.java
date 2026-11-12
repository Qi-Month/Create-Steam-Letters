package dev.celestiacraft.steam_letters.common.block.steam_hammer;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dev.celestiacraft.steam_letters.api.machine.ISteamBasinMachine;
import dev.celestiacraft.steam_letters.api.machine.ISteamPressMachine;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.config.common.SteamHammerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;

public class SteamHammerBlockEntity extends MechanicalPressBlockEntity implements ISteamPressMachine, ISteamBasinMachine {
	private FluidStack steam = FluidStack.EMPTY;
	private IFluidHandler steamCapability = this.steamFluidHandler();

	public SteamHammerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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
		return SteamHammerConfig.STEAM_CAPACITY.get();
	}

	@Override
	public int getSteamConsumption() {
		return SteamHammerConfig.STEAM_CONSUMPTION.get();
	}

	@Override
	public int getParallelCount() {
		return SteamHammerConfig.STEAM_HAMMER_PARALLEL.get();
	}

	@Override
	protected boolean updateBasin() {
		if (!hasEnoughSteam()) {
			return false;
		}
		return super.updateBasin();
	}

	@Override
	public boolean continueWithPreviousRecipe() {
		if (!hasEnoughSteam()) {
			return false;
		}
		return super.continueWithPreviousRecipe();
	}

	@Override
	public Recipe<?> getBasinRecipe() {
		return currentRecipe;
	}

	@Override
	public Optional<BasinBlockEntity> getBasin() {
		return super.getBasin();
	}

	@Override
	public <I extends RecipeInput> boolean matchBasinRecipe(Recipe<I> recipe) {
		return super.matchBasinRecipe(recipe);
	}

	@Override
	public void onBasinRecipeApplied(int applied) {
		getProcessedRecipeTrigger().ifPresent(this::award);
	}

	@Override
	public void applyBasinRecipe() {
		ISteamBasinMachine.super.applyBasinRecipe();
	}

	@Override
	public boolean canProcessInBulk() {
		return ISteamPressMachine.super.canProcessInBulk();
	}

	@Override
	public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
		return ISteamPressMachine.super.tryProcessOnBelt(input, outputList, simulate);
	}

	@Override
	public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
		return ISteamPressMachine.super.tryProcessInWorld(itemEntity, simulate);
	}

	/**
	 * 工作盆路径的蒸汽由 {@code applyBasinRecipe} 按操作扣减.
	 */
	@Override
	public void onPressingCompleted() {
		super.onPressingCompleted();
		if (!pressingBehaviour.onBasin()) {
			consumeSteam();
		}
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, SlBlockEntities.STEAM_HAMMER.get(), (hammer, direction) -> {
			return hammer.steamCapability;
		});
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if (!steam.isEmpty()) {
			tag.put("Steam", steam.save(registries));
		}
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		steam = FluidStack.parseOptional(registries, tag.getCompound("Steam"));
	}
}
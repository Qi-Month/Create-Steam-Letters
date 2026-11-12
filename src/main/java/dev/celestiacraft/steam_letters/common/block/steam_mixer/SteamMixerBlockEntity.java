package dev.celestiacraft.steam_letters.common.block.steam_mixer;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dev.celestiacraft.steam_letters.api.machine.ISteamBasinMachine;
import dev.celestiacraft.steam_letters.common.register.SlBlockEntities;
import dev.celestiacraft.steam_letters.config.common.SteamMixerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

public class SteamMixerBlockEntity extends MechanicalMixerBlockEntity implements ISteamBasinMachine {
	private FluidStack steam = FluidStack.EMPTY;
	private IFluidHandler steamCapability = this.steamFluidHandler();

	public SteamMixerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
		return SteamMixerConfig.STEAM_CAPACITY.get();
	}

	@Override
	public int getSteamConsumption() {
		return SteamMixerConfig.STEAM_CONSUMPTION.get();
	}

	@Override
	public int getParallelCount() {
		return SteamMixerConfig.STEAM_MIXER_PARALLEL.get();
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

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, SlBlockEntities.STEAM_MIXER.get(), (mixer, direction) -> {
			return mixer.steamCapability;
		});
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
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
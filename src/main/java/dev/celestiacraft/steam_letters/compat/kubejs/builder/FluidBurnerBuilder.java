package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.block.burner.AbstractBurnerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.burner.FluidBurnerBlock;
import dev.celestiacraft.steam_letters.common.block.burner.FluidBurnerBlockEntity;
import dev.celestiacraft.steam_letters.config.common.SteamBurnerConfig;
import dev.celestiacraft.steam_letters.tags.SlBlockTags;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class FluidBurnerBuilder extends BurnerDeviceBuilder<FluidBurnerBlock, FluidBurnerBlockEntity, FluidBurnerBuilder> {
	public FluidBurnerBuilder(ResourceLocation id) {
		super(id);
		itemTag(SlItemTags.STEAM_PRODUCTION.location());
		itemTag(SlItemTags.STEAM_FLUID_BURNER.location());
		blockTag(SlBlockTags.STEAM_PRODUCTION.location());
		blockTag(SlBlockTags.STEAM_FLUID_BURNER.location());
	}

	@Override
	protected FluidBurnerBuilder self() {
		return this;
	}

	@Override
	protected String devicePath() {
		return "block/steam/fluid_burner";
	}

	@Override
	protected FluidBurnerBlock createBlock(BlockBehaviour.Properties properties) {
		SteamEngineSpec spec = new SteamEngineSpec(capacityOr(() -> FluidBurnerBlockEntity.DEFAULT_CAPACITY), () -> 0.0D, maxHeatOr(() -> AbstractBurnerBlockEntity.DEFAULT_MAX_HEAT), temperatureOr(() -> SteamBurnerConfig.MAX_TEMPERATURE.get()));
		return new FluidBurnerBlock(properties, spec, () -> blockEntityType);
	}

	@Override
	protected FluidBurnerBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new FluidBurnerBlockEntity(blockEntityType, pos, state);
	}

	@Override
	protected void registerCapability(RegisterCapabilitiesEvent event, BlockEntityType<FluidBurnerBlockEntity> type) {
		FluidBurnerBlockEntity.registerCapabilities(event, type);
	}
}
package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlock;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlockEntity;
import dev.celestiacraft.steam_letters.tags.SlBlockTags;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class BoilerBuilder extends BoilerDeviceBuilder<SteamBoilerBlock, SteamBoilerBlockEntity, BoilerBuilder> {
	public BoilerBuilder(ResourceLocation id) {
		super(id);
		itemTag(SlItemTags.STEAM_PRODUCTION.location());
		itemTag(SlItemTags.STEAM_BOILER.location());
		blockTag(SlBlockTags.STEAM_PRODUCTION.location());
		blockTag(SlBlockTags.STEAM_BOILER.location());
	}

	@Override
	protected BoilerBuilder self() {
		return this;
	}

	@Override
	protected String devicePath() {
		return "block/steam/boiler";
	}

	@Override
	protected SteamBoilerBlock createBlock(BlockBehaviour.Properties properties) {
		SteamEngineSpec spec = new SteamEngineSpec(capacityOr(() -> SteamBoilerBlockEntity.DEFAULT_CAPACITY), productionOr(() -> SteamBoilerBlockEntity.DEFAULT_PRODUCTION), () -> 0.0D);
		return new SteamBoilerBlock(properties, spec, () -> blockEntityType);
	}

	@Override
	protected SteamBoilerBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SteamBoilerBlockEntity(blockEntityType, pos, state);
	}

	@Override
	protected void registerCapability(RegisterCapabilitiesEvent event, BlockEntityType<SteamBoilerBlockEntity> type) {
		SteamBoilerBlockEntity.registerCapabilities(event, type);
	}
}
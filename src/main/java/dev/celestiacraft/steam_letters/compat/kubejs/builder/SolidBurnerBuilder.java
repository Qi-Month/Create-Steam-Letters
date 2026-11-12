package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.block.burner.AbstractBurnerBlockEntity;
import dev.celestiacraft.steam_letters.common.block.burner.SolidBurnerBlock;
import dev.celestiacraft.steam_letters.common.block.burner.SolidBurnerBlockEntity;
import dev.celestiacraft.steam_letters.config.common.SteamBurnerConfig;
import dev.celestiacraft.steam_letters.tags.SlBlockTags;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class SolidBurnerBuilder extends BurnerDeviceBuilder<SolidBurnerBlock, SolidBurnerBlockEntity, SolidBurnerBuilder> {
	public SolidBurnerBuilder(ResourceLocation id) {
		super(id);
		itemTag(SlItemTags.STEAM_PRODUCTION.location());
		itemTag(SlItemTags.STEAM_SOLID_BURNER.location());
		blockTag(SlBlockTags.STEAM_PRODUCTION.location());
		blockTag(SlBlockTags.STEAM_SOLID_BURNER.location());
	}

	@Override
	protected SolidBurnerBuilder self() {
		return this;
	}

	@Override
	protected String devicePath() {
		return "block/steam/solid_burner";
	}

	@Override
	protected SolidBurnerBlock createBlock(BlockBehaviour.Properties properties) {
		SteamEngineSpec spec = new SteamEngineSpec(() -> 0, () -> 0.0D, maxHeatOr(() -> {
			return AbstractBurnerBlockEntity.DEFAULT_MAX_HEAT;
		}), temperatureOr(() -> {
			return SteamBurnerConfig.MAX_TEMPERATURE.get();
		}));
		return new SolidBurnerBlock(properties, spec, () -> blockEntityType);
	}

	@Override
	protected SolidBurnerBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SolidBurnerBlockEntity(blockEntityType, pos, state);
	}

	@Override
	protected void registerCapability(RegisterCapabilitiesEvent event, BlockEntityType<SolidBurnerBlockEntity> type) {
		SolidBurnerBlockEntity.registerCapabilities(event, type);
	}
}
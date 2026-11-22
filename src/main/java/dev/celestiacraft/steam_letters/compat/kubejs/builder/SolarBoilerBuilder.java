package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.block.solar_boiler.SolarBoilerBlock;
import dev.celestiacraft.steam_letters.common.block.solar_boiler.SolarBoilerBlockEntity;
import dev.celestiacraft.steam_letters.tags.SlBlockTags;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;

public class SolarBoilerBuilder extends BoilerDeviceBuilder<SolarBoilerBlock, SolarBoilerBlockEntity, SolarBoilerBuilder> {
	public SolarBoilerBuilder(ResourceLocation id) {
		super(id);
		itemTag(SlItemTags.STEAM_PRODUCTION.location());
		itemTag(SlItemTags.STEAM_SOLAR_BOILER.location());
		blockTag(SlBlockTags.STEAM_PRODUCTION.location());
		blockTag(SlBlockTags.STEAM_SOLAR_BOILER.location());
	}

	@Override
	protected SolarBoilerBuilder self() {
		return this;
	}

	@Override
	protected String devicePath() {
		return "block/steam/solar_boiler";
	}

	@Override
	protected SolarBoilerBlock createBlock(BlockBehaviour.Properties properties) {
		SteamEngineSpec spec = new SteamEngineSpec(capacityOr(() -> SolarBoilerBlockEntity.DEFAULT_CAPACITY), productionOr(() -> SolarBoilerBlockEntity.DEFAULT_PRODUCTION), () -> 0.0D);
		return new SolarBoilerBlock(properties, spec, () -> blockEntityType);
	}

	@Override
	protected SolarBoilerBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SolarBoilerBlockEntity(blockEntityType, pos, state);
	}

	/**
	 * 太阳能锅炉是 Blockbench 造型(围墙 + 玻璃盖, cutout 渲染), 不能套默认的 cube_bottom_top 方盒,
	 * 所以内联内置 bronze 模型作骨架, 只把贴图换成材质对应的图片, 生成物仍然写进 KubeJS 虚拟资源包.
	 */
	@Override
	protected void generateBlockModels(AssetJsonGenerator generator) {
		if (model != null || usesBuiltinModel()) {
			return;
		}

		generateModel(generator, baseModel(), "block/steam/solar_boiler/bronze", solarBoilerTextures());
	}

	private static Map<String, String> solarBoilerTextures() {
		Map<String, String> textures = new LinkedHashMap<>();
		textures.put("particle", "block/steam/solar_boiler/%s/side");
		textures.put("side", "block/steam/solar_boiler/%s/side");
		textures.put("top", "block/steam/solar_boiler/%s/top");
		textures.put("down", "block/steam/solar_boiler/%s/down");
		return textures;
	}
}
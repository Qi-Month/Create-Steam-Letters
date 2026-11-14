package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.api.machine.SteamEngineSpec;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlock;
import dev.celestiacraft.steam_letters.common.block.boiler.SteamBoilerBlockEntity;
import dev.celestiacraft.steam_letters.tags.SlBlockTags;
import dev.celestiacraft.steam_letters.tags.SlItemTags;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BoilerBuilder extends BoilerDeviceBuilder<SteamBoilerBlock, SteamBoilerBlockEntity, BoilerBuilder> {
	private static final Set<ResourceLocation> ATLAS_TEXTURES = new LinkedHashSet<>(List.of(
			SlApis.loadResource("block/steam/boiler/bronze/connected"),
			SlApis.loadResource("block/steam/boiler/bronze/connected_top")));

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
		return new SteamBoilerBlock(properties, spec, () -> blockEntityType, textureDirectory());
	}

	/**
	 * 连接纹理所在的贴图目录, 自定义模型时返回空, 表示不套连接纹理.
	 */
	private ResourceLocation textureDirectory() {
		if (model != null) {
			return null;
		}

		return ResourceLocation.fromNamespaceAndPath(material.getNamespace(), devicePath() + "/" + material.getPath());
	}

	@Override
	protected SteamBoilerBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SteamBoilerBlockEntity(blockEntityType, pos, state);
	}

	@Override
	protected void registerCapability(RegisterCapabilitiesEvent event, BlockEntityType<SteamBoilerBlockEntity> type) {
		SteamBoilerBlockEntity.registerCapabilities(event, type);
	}

	/**
	 * 连接纹理不被任何模型引用, 必须显式缝进方块图集.
	 */
	@Override
	public void generateAssets(KubeAssetGenerator generator) {
		super.generateAssets(generator);

		ResourceLocation directory = textureDirectory();
		if (directory != null) {
			ATLAS_TEXTURES.add(ResourceLocation.fromNamespaceAndPath(directory.getNamespace(), directory.getPath() + "/connected"));
			ATLAS_TEXTURES.add(ResourceLocation.fromNamespaceAndPath(directory.getNamespace(), directory.getPath() + "/connected_top"));
		}

		writeAtlas(generator);
	}

	/**
	 * 写出的图集文件会覆盖模组自带的同名文件, 因此模组自己的条目也要一并写入.
	 */
	private static void writeAtlas(KubeAssetGenerator generator) {
		JsonArray sources = new JsonArray();

		JsonObject fluid = new JsonObject();
		fluid.addProperty("type", "minecraft:directory");
		fluid.addProperty("prefix", "fluid/");
		fluid.addProperty("source", "fluid");
		sources.add(fluid);

		for (ResourceLocation texture : ATLAS_TEXTURES) {
			JsonObject single = new JsonObject();
			single.addProperty("type", "minecraft:single");
			single.addProperty("resource", texture.toString());
			sources.add(single);
		}

		JsonObject root = new JsonObject();
		root.add("sources", sources);
		generator.json(ResourceLocation.fromNamespaceAndPath("minecraft", "atlases/blocks"), root);
	}

	/**
	 * 锅炉带 top / bottom / shape 三个属性, 每个状态都要指向同一个模型.
	 */
	@Override
	protected void generateBlockState(KubeAssetGenerator generator) {
		JsonObject variant = new JsonObject();
		variant.addProperty("model", modelId(baseModel()));

		JsonObject variants = new JsonObject();
		for (BlockState state : block.getStateDefinition().getPossibleStates()) {
			variants.add(stateKey(state), variant);
		}

		writeBlockState(generator, variants);
	}

	private static String stateKey(BlockState state) {
		StringBuilder key = new StringBuilder();
		for (Property<?> property : state.getProperties()) {
			if (key.length() > 0) {
				key.append(',');
			}

			key.append(property.getName()).append('=').append(valueName(state, property));
		}

		return key.toString();
	}

	private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
		return property.getName(state.getValue(property));
	}
}
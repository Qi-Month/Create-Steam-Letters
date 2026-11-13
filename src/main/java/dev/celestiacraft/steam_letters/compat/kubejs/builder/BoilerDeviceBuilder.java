package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.DoubleSupplier;

public abstract class BoilerDeviceBuilder<B extends Block, E extends BlockEntity, SELF extends DeviceBuilder<B, E, SELF>> extends DeviceBuilder<B, E, SELF> {
	protected Double production;

	protected BoilerDeviceBuilder(ResourceLocation id) {
		super(id);
	}

	@Info("Base steam output per tick at 100% efficiency (mB/tick)")
	public SELF production(double production) {
		this.production = production;
		return self();
	}

	protected DoubleSupplier productionOr(DoubleSupplier fallback) {
		return production == null ? fallback : production::doubleValue;
	}

	/**
	 * 锅炉没有单独的下底面贴图, 底面直接复用顶面.
	 */
	@Override
	protected void generateBlockModels(KubeAssetGenerator generator) {
		String path = baseModel();

		JsonObject textures = new JsonObject();
		textures.addProperty("particle", textureId(path, "side"));
		textures.addProperty("bottom", textureId(path, "top"));
		textures.addProperty("top", textureId(path, "top"));
		textures.addProperty("side", textureId(path, "side"));

		JsonObject root = new JsonObject();
		root.addProperty("parent", "minecraft:block/cube_bottom_top");
		root.add("textures", textures);

		generator.json(modelLocation(path), root);
	}
}
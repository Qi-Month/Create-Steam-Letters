package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import com.google.gson.JsonObject;
import dev.celestiacraft.steam_letters.common.block.burner.AbstractBurnerBlock;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.DoubleSupplier;

public abstract class BurnerDeviceBuilder<B extends Block, E extends BlockEntity, SELF extends DeviceBuilder<B, E, SELF>> extends DeviceBuilder<B, E, SELF> {
	protected Double maxHeat;
	protected Double temperature;

	protected BurnerDeviceBuilder(ResourceLocation id) {
		super(id);
	}

	@Info("Maximum heat")
	public SELF maxHeat(double value) {
		maxHeat = value;
		return self();
	}

	protected DoubleSupplier maxHeatOr(DoubleSupplier fallback) {
		return maxHeat == null ? fallback : maxHeat::doubleValue;
	}

	@Info("Maximum temperature")
	public SELF temperature(double value) {
		temperature = value;
		return self();
	}

	protected DoubleSupplier temperatureOr(DoubleSupplier fallback) {
		return temperature == null ? fallback : temperature::doubleValue;
	}

	@Override
	protected void generateBlockState(KubeAssetGenerator generator) {
		String offModel = modelId(baseModel());
		String litModel = modelId(baseModel() + "_lit");
		String facingKey = AbstractBurnerBlock.FACING.getName();
		String litKey = AbstractBurnerBlock.LIT.getName();

		JsonObject variants = new JsonObject();
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			int y = (int) direction.toYRot();

			for (boolean lit : new boolean[] {
					false,
					true
			}) {
				JsonObject variant = new JsonObject();
				variant.addProperty("model", lit ? litModel : offModel);
				if (y != 0) {
					variant.addProperty("y", y);
				}
				variants.add(facingKey + "=" + direction.getName() + "," + litKey + "=" + lit, variant);
			}
		}

		writeBlockState(generator, variants);
	}

	@Override
	protected void generateBlockModels(KubeAssetGenerator generator) {
		generator.json(modelLocation(baseModel()), createOrientableModel(baseModel(), "off"));
		generator.json(modelLocation(baseModel() + "_lit"), createOrientableModel(baseModel(), "on"));
	}

	protected JsonObject createOrientableModel(String modelPath, String front) {
		JsonObject textures = new JsonObject();
		textures.addProperty("particle", textureId(modelPath, "side"));
		textures.addProperty("bottom", textureId(modelPath, "down"));
		textures.addProperty("top", textureId(modelPath, "top"));
		textures.addProperty("side", textureId(modelPath, "side"));
		textures.addProperty("front", textureId(modelPath, front));

		JsonObject root = new JsonObject();
		root.addProperty("parent", "minecraft:block/orientable_with_bottom");
		root.add("textures", textures);
		return root;
	}
}
package dev.celestiacraft.steam_letters.compat.kubejs.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.api.item.SteamBlockItem;
import dev.celestiacraft.steam_letters.common.register.SlCapabilities;
import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.registry.AdditionalObjectRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforgespi.language.IModFileInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.IntSupplier;

/**
 * 蒸汽方块的注册骨架: 方块 / 方块实体类型 / 方块物品 / 资源与语言.
 * <p>
 * 方块实体类型与方块物品不在方块那张注册表里, 只能等各自的注册事件打开时才能建,
 * 所以这里把两头拆开: 方块本身由 createObject 建, 另外两样挂到 createAdditionalObjects.
 * <p>
 * 第三个类型参数 {@code SELF} 是具体 builder 自己的类型, 由子类以 {@code self()} 返回 {@code this} 固定下来.
 * 所有链式配置方法一律返回 {@code SELF}, 因此在任何一环调用之后, 补全都还收窄在具体 builder 上,
 * 例如 {@code boilerBuilder.material(...).production(...)} 也能正常补全.
 * <p>
 * 注意不要用「子类覆盖 material/model 返回自身类型」的办法做同样的事: 协变返回会在字节码里生成
 * synthetic bridge 方法, Rhino 那一侧就会出现同名同参, 只有返回类型不同的两个方法, 没有必要冒这个险.
 * 子类若要在材质/模型变化时做额外动作, 覆盖 {@link #onAppearanceChanged()} 钩子即可.
 * <p>
 * 标签同理: 想给物品打标签用 {@link #itemTag(ResourceLocation)}, 打方块用 {@link #blockTag(ResourceLocation)},
 * 两边都要用 {@link #tagBoth(ResourceLocation)}. 基类自带的 {@code tag(...)} 只作用于方块.
 */
public abstract class DeviceBuilder<B extends Block, E extends BlockEntity, SELF extends DeviceBuilder<B, E, SELF>> extends BuilderBase<Block> {
	protected static final String MACHINE_BOTTOM = "steam_letters:block/steam/down";

	protected B block;
	protected Item item;
	protected BlockEntityType<E> blockEntityType;

	/**
	 * 方块物品自己的 builder. 物品标签必须记在它身上: KubeJS 的标签收集是按「builder 所属注册表」分流的,
	 * 记在方块 builder 上的标签只会写进 {@code tags/blocks}, 不会自动复制到物品.
	 */
	protected final DeviceItemBuilder itemBuilder;

	protected ResourceLocation material = SlApis.loadResource("bronze");
	protected ResourceLocation model;
	/**
	 * 内部罐容量, 单位 mB. 脚本不写时保持 {@code null}, 由各设备自己决定回退到哪个默认值.
	 */
	protected Integer capacity;
	protected double strength;
	protected double speed;
	protected double consumption;

	protected DeviceBuilder(ResourceLocation id) {
		super(id);

		// 提前建好: 子类可以在构造器里直接 itemTag(...), 不必等 createAdditionalObjects 的注册阶段
		itemBuilder = new DeviceItemBuilder(id, this::buildItem);

		tag(new ResourceLocation[] {
				BlockTags.MINEABLE_WITH_PICKAXE.location(),
				BlockTags.NEEDS_STONE_TOOL.location(),
				AllTags.AllBlockTags.WRENCH_PICKUP.tag.location()
		});
	}

	/**
	 * 返回 {@code this}, 类型固定成具体 builder, 让基类的链式方法把类型收窄回去.
	 */
	protected abstract SELF self();

	@Override
	public Block createObject() {
		block = createBlock(createProperties());
		return block;
	}

	@Override
	public void createAdditionalObjects(AdditionalObjectRegistry registry) {
		registry.add(Registries.BLOCK_ENTITY_TYPE, new DeviceBlockEntityTypeBuilder(id, this::buildBlockEntityType));
		registry.add(Registries.ITEM, itemBuilder);
	}

	/**
	 * 只给方块加标签, 写进 {@code data/<ns>/tags/blocks/}.
	 * <p>
	 * 不直接覆盖基类的 {@link BuilderBase#tag(ResourceLocation[])} 做「方块+物品一起打」的原因见类文档:
	 * 协变覆盖会在 Rhino 上留下同名同参的重载, 所以这里换个名字.
	 */
	@Info("Tags the block only (data/<ns>/tags/blocks)")
	public SELF blockTag(ResourceLocation tag) {
		tag(new ResourceLocation[] {
				tag
		});
		return self();
	}

	/**
	 * 只给方块物品加标签, 写进 {@code data/<ns>/tags/items/}.
	 */
	@Info("Tags the block item only (data/<ns>/tags/items)")
	public SELF itemTag(ResourceLocation tag) {
		itemBuilder.tag(new ResourceLocation[] {
				tag
		});
		return self();
	}

	/**
	 * 同一个标签同时打给方块和方块物品.
	 */
	@Info("Tags both the block and the block item")
	public SELF tagBoth(ResourceLocation tag) {
		tag(new ResourceLocation[] {
				tag
		});
		itemBuilder.tag(new ResourceLocation[] {
				tag
		});
		return self();
	}

	/**
	 * 建方块实体类型, 只由 DeviceBlockEntityTypeBuilder 在方块实体类型注册阶段调用.
	 */
	protected BlockEntityType<E> buildBlockEntityType() {
		BlockEntityType<E> created = BlockEntityType.Builder.of(this::createBlockEntity, block).build(null);
		blockEntityType = created;
		SlCapabilities.addDynamic((event) -> {
			registerCapability(event, created);
		});
		return created;
	}

	/**
	 * 给动态创建的方块实体类型补能力. 默认不注册, 由子类按实体类型实现.
	 * <p>
	 * 能力注册事件只触发一次, 早于这里创建的方块实体类型, 所以在类型建好时登记, 由能力注册末尾统一补.
	 */
	protected void registerCapability(RegisterCapabilitiesEvent event, BlockEntityType<E> type) {
	}

	/**
	 * 建方块物品, 只由 DeviceItemBuilder 在物品注册阶段调用.
	 */
	protected Item buildItem() {
		item = new SteamBlockItem(block, new Item.Properties());
		return item;
	}

	@Info("Material. Determines the default model and texture directory. Defaults to steam_letters:bronze")
	public SELF material(ResourceLocation material) {
		if (!material.equals(this.material)) {
			this.material = material;
			onAppearanceChanged();
		}
		return self();
	}

	@Info("Full model path. When omitted it is derived from the device directory and the material")
	public SELF model(ResourceLocation model) {
		if (!model.equals(this.model)) {
			this.model = model;
			onAppearanceChanged();
		}
		return self();
	}

	/**
	 * 材质或模型变化之后的钩子, 默认无操作.
	 * <p>
	 * 用钩子而不是让子类覆盖 {@link #material(ResourceLocation)} / {@link #model(ResourceLocation)}:
	 * 覆盖会生成 bridge 方法, 在 Rhino 上留下同名同参的重载.
	 */
	protected void onAppearanceChanged() {
	}

	@Info("Internal tank capacity in mB. When omitted the device default is used")
	public SELF capacity(int capacity) {
		this.capacity = capacity;
		return self();
	}

	protected IntSupplier capacityOr(IntSupplier fallback) {
		return capacity == null ? fallback : capacity::intValue;
	}

	@Override
	public void generateAssets(KubeAssetGenerator generator) {
		generateBlockModels(generator);
		generateBlockState(generator);
		generateItemModel(generator);
	}

	@Override
	public void generateLang(LangKubeEvent event) {
		event.add(id.getNamespace(), id.toLanguageKey("block"), displayName(id.getPath()));
	}

	@Override
	public void generateData(KubeDataGenerator generator) {
		generator.json(newID("loot_tables/blocks/", ""), createLootTable());
	}

	private JsonObject createLootTable() {
		JsonObject item = new JsonObject();
		item.addProperty("type", "minecraft:item");
		item.addProperty("name", id.toString());

		JsonArray entries = new JsonArray();
		entries.add(item);

		JsonObject survives = new JsonObject();
		survives.addProperty("condition", "minecraft:survives_explosion");

		JsonArray conditions = new JsonArray();
		conditions.add(survives);

		JsonObject pool = new JsonObject();
		pool.addProperty("rolls", 1.0D);
		pool.addProperty("bonus_rolls", 0.0D);
		pool.add("conditions", conditions);
		pool.add("entries", entries);

		JsonArray pools = new JsonArray();
		pools.add(pool);

		JsonObject table = new JsonObject();
		table.addProperty("type", "minecraft:block");
		table.add("pools", pools);
		table.addProperty("random_sequence", id.getNamespace() + ":blocks/" + id.getPath());
		return table;
	}

	protected abstract String devicePath();

	protected abstract B createBlock(BlockBehaviour.Properties properties);

	protected abstract E createBlockEntity(BlockPos pos, BlockState state);

	/**
	 * 默认方块状态: 单一 variant, 直接指向当前模型. 有朝向或状态的设备覆盖本方法.
	 */
	protected void generateBlockState(KubeAssetGenerator generator) {
		JsonObject variant = new JsonObject();
		variant.addProperty("model", modelId(baseModel()));

		JsonObject variants = new JsonObject();
		variants.add("", variant);

		writeBlockState(generator, variants);
	}

	/**
	 * 默认方块模型: cube_bottom_top 造型, 贴图按 模型目录 + 固定部件后缀 推导.
	 * <p>
	 * 例如 {@code material("steam_letters:steel")} 的锅炉, 侧面贴图是
	 * {@code steam_letters:block/steam/boiler/steel/side}, 顶面是 {@code .../steel/top}.
	 */
	protected void generateBlockModels(KubeAssetGenerator generator) {
		String path = baseModel();

		JsonObject textures = new JsonObject();
		textures.addProperty("particle", textureId(path, "side"));
		textures.addProperty("bottom", MACHINE_BOTTOM);
		textures.addProperty("top", textureId(path, "top"));
		textures.addProperty("side", textureId(path, "side"));

		JsonObject root = new JsonObject();
		root.addProperty("parent", "minecraft:block/cube_bottom_top");
		root.add("textures", textures);

		generator.json(modelLocation(path), root);
	}

	/**
	 * 物品模型 parent 的模型路径. 多数设备直接复用方块模型, 自带独立物品造型的设备覆盖本方法.
	 */
	protected String itemModel() {
		return baseModel();
	}

	protected void generateItemModel(KubeAssetGenerator generator) {
		JsonObject root = new JsonObject();
		root.addProperty("parent", modelId(itemModel()));
		generator.json(itemModelLocation(), root);
	}

	/**
	 * 材质就是模组自带的 bronze 时, 模型直接复用内置资源, 不再重复生成.
	 */
	protected boolean usesBuiltinModel() {
		return CreateLettersSteam.MODID.equals(material.getNamespace()) && "bronze".equals(material.getPath());
	}

	/**
	 * 按材质自动生成方块模型: 直接内联同名 bronze 模型的造型, 只把贴图换成材质对应的图片.
	 * <p>
	 * 生成物和锅炉一类的设备一样, 是完整的自包含模型, 由 KubeJS 写进虚拟资源包,
	 * 不 parent 到模组内置资源, 所以换材质时不再依赖内置的 bronze 模型文件.
	 * <p>
	 * 贴图路径按 材质命名空间 + 固定部件后缀 推导, 所以脚本只需要写材质名:
	 * {@code material("steam_letters:steel")} 的飞轮轮盘是 {@code steam_letters:block/steam/flywheel/steel_wheel},
	 * 外壳是 {@code steam_letters:block/steam/casing/steel_casing}, 引擎气缸是
	 * {@code steam_letters:block/steam/engine/steel_cylinder}.
	 *
	 * @param path            生成的模型路径(不含命名空间与 models/ 前缀)
	 * @param template        作为造型来源的 bronze 模型路径
	 * @param texturePatterns 贴图键到路径模板的映射, 模板里的 %s 会被替换成材质名
	 */
	protected void generateModel(KubeAssetGenerator generator, String path, String template, Map<String, String> texturePatterns) {
		JsonObject root = loadTemplateModel(template);
		JsonObject textures = new JsonObject();
		texturePatterns.forEach((key, pattern) -> textures.addProperty(key, modelNamespace() + ":" + pattern.replace("%s", material.getPath())));
		root.add("textures", textures);

		generator.json(ResourceLocation.fromNamespaceAndPath(modelNamespace(), "models/" + path), root);
	}

	/**
	 * 读模组内置的模型 json 作为造型骨架, 每次返回一份新的对象, 调用方可以随意改.
	 */
	private static JsonObject loadTemplateModel(String template) {
		String path = "/assets/" + CreateLettersSteam.MODID + "/models/" + template + ".json";

		try (InputStream stream = openTemplateModel(path, template)) {
			JsonElement element = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
			JsonObject root = element.getAsJsonObject();
			root.remove("credit");
			return root;
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read model template " + path, e);
		}
	}

	private static InputStream openTemplateModel(String path, String template) throws IOException {
		InputStream stream = DeviceBuilder.class.getResourceAsStream(path);

		if (stream != null) {
			return stream;
		}

		IModFileInfo modFile = ModList.get().getModFileById(CreateLettersSteam.MODID);

		if (modFile != null) {
			Path file = modFile.getFile().findResource("assets", CreateLettersSteam.MODID, "models", template + ".json");

			if (file != null) {
				return Files.newInputStream(file);
			}
		}

		throw new IllegalStateException("Missing model template " + path);
	}

	protected BlockBehaviour.Properties createProperties() {
		return BlockBehaviour.Properties.ofFullCopy(SharedProperties.softMetal())
				.noOcclusion()
				.sound(SoundType.LANTERN);
	}

	protected String modelNamespace() {
		ResourceLocation source = model != null ? model : material;
		return source.getNamespace();
	}

	protected String baseModel() {
		return model != null ? model.getPath() : devicePath() + "/" + material.getPath();
	}

	protected String modelId(String path) {
		return modelNamespace() + ":" + path;
	}

	protected String textureId(String modelPath, String part) {
		return modelNamespace() + ":" + modelPath + "/" + part;
	}

	protected ResourceLocation modelLocation(String modelPath) {
		return ResourceLocation.fromNamespaceAndPath(modelNamespace(), "models/" + modelPath);
	}

	protected void writeBlockState(KubeAssetGenerator generator, JsonObject variants) {
		JsonObject root = new JsonObject();
		root.add("variants", variants);
		generator.json(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "blockstates/" + id.getPath()), root);
	}

	protected ResourceLocation itemModelLocation() {
		return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "models/item/" + id.getPath());
	}

	protected String displayName(String path) {
		StringBuilder builder = new StringBuilder();
		for (String part : path.split("_")) {
			if (part.isEmpty()) {
				continue;
			}
			if (!builder.isEmpty()) {
				builder.append(' ');
			}
			builder.append(Character.toUpperCase(part.charAt(0)))
					.append(part.substring(1));
		}
		return builder.toString();
	}
}
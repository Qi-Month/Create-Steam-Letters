package dev.celestiacraft.steam_letters.client.block;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class SlBlockPartialModel {
	public static final PartialModel
			STEAM_HAMMER;

	public static final PartialModel
			SPOUT_TOP,
			SPOUT_MIDDLE,
			SPOUT_BOTTOM;

	public static final PartialModel
			STEAM_FLYWHEEL_WHEEL,
			STEAM_FLYWHEEL_UPPER_ROTATING,
			STEAM_FLYWHEEL_LOWER_ROTATING,
			STEAM_FLYWHEEL_UPPER_SLIDING,
			STEAM_FLYWHEEL_LOWER_SLIDING;

	public static final PartialModel
			STEAM_MIXER_HEAD,
			STEAM_MIXER_POLE,
			STEAM_MIXER_COGWHEEL;

	public static final String UPPER_ROTATING_CONNECTOR = "upper_rotating_connector";
	public static final String LOWER_ROTATING_CONNECTOR = "lower_rotating_connector";
	public static final String UPPER_SLIDING_CONNECTOR = "upper_sliding_connector";
	public static final String LOWER_SLIDING_CONNECTOR = "lower_sliding_connector";

	public static final List<String> FLYWHEEL_CONNECTOR_PARTS = List.of(
			UPPER_ROTATING_CONNECTOR,
			LOWER_ROTATING_CONNECTOR,
			UPPER_SLIDING_CONNECTOR,
			LOWER_SLIDING_CONNECTOR
	);

	static {
		STEAM_HAMMER = addPartial("steam_hammer/head");

		STEAM_FLYWHEEL_WHEEL = addPartial("steam/flywheel/bronze_wheel");
		STEAM_FLYWHEEL_UPPER_ROTATING = addPartial("steam/flywheel/bronze_upper_rotating_connector");
		STEAM_FLYWHEEL_LOWER_ROTATING = addPartial("steam/flywheel/bronze_lower_rotating_connector");
		STEAM_FLYWHEEL_UPPER_SLIDING = addPartial("steam/flywheel/bronze_upper_sliding_connector");
		STEAM_FLYWHEEL_LOWER_SLIDING = addPartial("steam/flywheel/bronze_lower_sliding_connector");

		STEAM_MIXER_HEAD = addPartial("steam_mixer/head");
		STEAM_MIXER_POLE = addPartial("steam_mixer/pole");
		STEAM_MIXER_COGWHEEL = addPartial("steam_mixer/cogwheel");

		SPOUT_TOP = addPartial("advanced_spout/top");
		SPOUT_MIDDLE = addPartial("advanced_spout/middle");
		SPOUT_BOTTOM = addPartial("advanced_spout/bottom");
	}

	private static final Map<String, PartialModel> FLYWHEEL_PARTS = new HashMap<>();

	/**
	 * 青铜蒸汽管道的接口模型: 玩家自己看的法兰/接头没法用 Create 的 (那是铜色), 所以整套复制了一份.
	 * 键是 {@link FluidTransportBehaviour.AttachmentTypes.ComponentPartials} (CONNECTION / RIM_CONNECTOR / RIM / DRAIN), 内层按朝向索引.
	 */
	public static final Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>> BRONZE_PIPE_ATTACHMENTS =
			buildBronzePipeAttachments();

	/**
	 * 取飞轮的轮盘模型. 内置青铜飞轮直接用模组自带的 {@code bronze_wheel}, 其余材质取
	 * {@code <材质命名空间>:block/steam/flywheel/<材质>_wheel} —— 这些模型由 KubeJS 注册器在注册阶段
	 * 内联青铜造型, 只换贴图生成.
	 */
	public static PartialModel flywheelWheel(ResourceLocation material) {
		if (isBuiltinBronze(material)) {
			return STEAM_FLYWHEEL_WHEEL;
		}

		return flywheelPart(material, "wheel");
	}

	/**
	 * 取飞轮传动杆模型, {@code part} 用本类里 {@code *_CONNECTOR} 常量.
	 */
	public static PartialModel flywheelConnector(ResourceLocation material, String part) {
		if (isBuiltinBronze(material)) {
			switch (part) {
				case UPPER_ROTATING_CONNECTOR -> {
					return STEAM_FLYWHEEL_UPPER_ROTATING;
				}
				case LOWER_ROTATING_CONNECTOR -> {
					return STEAM_FLYWHEEL_LOWER_ROTATING;
				}
				case UPPER_SLIDING_CONNECTOR -> {
					return STEAM_FLYWHEEL_UPPER_SLIDING;
				}
				case LOWER_SLIDING_CONNECTOR -> {
					return STEAM_FLYWHEEL_LOWER_SLIDING;
				}
				default -> {
				}
			}
		}

		return flywheelPart(material, part);
	}

	/**
	 * 在模型烘焙之前把某个材质的轮盘与四根传动杆模型建好.
	 * <p>
	 * Flywheel 的 {@link PartialModel} 只有在 {@code ModelEvent.RegisterAdditional} 时已经被创建,
	 * 才会被加进模型的加载列表; 等到渲染时再创建, 拿到的永远是 missing model, 方块显示成紫黑方块.
	 */
	public static void preloadFlywheel(ResourceLocation material) {
		flywheelWheel(material);

		for (String part : FLYWHEEL_CONNECTOR_PARTS) {
			flywheelConnector(material, part);
		}
	}

	private static boolean isBuiltinBronze(ResourceLocation material) {
		return SlApis.loadResource("bronze").equals(material);
	}

	private static PartialModel flywheelPart(ResourceLocation material, String part) {
		String key = material.getNamespace() + ":" + material.getPath() + "_" + part;

		return FLYWHEEL_PARTS.computeIfAbsent(key, ignored -> PartialModel.of(
				ResourceLocation.fromNamespaceAndPath(material.getNamespace(),
						"block/steam/flywheel/" + material.getPath() + "_" + part)
		));
	}

	private static Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>> buildBronzePipeAttachments() {
		Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>> attachments = new EnumMap<>(FluidTransportBehaviour.AttachmentTypes.ComponentPartials.class);

		for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials part : FluidTransportBehaviour.AttachmentTypes.ComponentPartials.values()) {
			Map<Direction, PartialModel> byFacing = new EnumMap<>(Direction.class);

			for (Direction direction : Direction.values()) {
				String path = "bronze_pipe/" + part.name().toLowerCase(Locale.ROOT)
						+ "/" + direction.getName();

				byFacing.put(direction, addPartial(path));
			}

			attachments.put(part, byFacing);
		}

		return attachments;
	}

	private static PartialModel addPartial(String path) {
		return PartialModel.of(SlApis.loadResource("block/" + path));
	}

	public static void init() {
	}
}

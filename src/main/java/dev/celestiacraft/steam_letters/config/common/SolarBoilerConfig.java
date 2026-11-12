package dev.celestiacraft.steam_letters.config.common;

import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 太阳能锅炉的环境效率配置.
 * <p>
 * 天气系数只乘在已经选中的光路上 (太阳光或人造光), 不与光照基础效率混为一谈.
 * 例: 人造光 50% x 雨天 50% = 25%.
 */
public class SolarBoilerConfig extends ConfigModule {
	public static ModConfigSpec.DoubleValue
			SUNLIGHT_EFFICIENCY,
			ARTIFICIAL_LIGHT_EFFICIENCY,
			RAIN_EFFICIENCY,
			THUNDER_EFFICIENCY;

	public static ModConfigSpec.IntValue ARTIFICIAL_LIGHT_THRESHOLD;

	public static ModConfigSpec.BooleanValue
			REQUIRE_DAYLIGHT,
			RENDER_FLUID;

	public SolarBoilerConfig(ModConfigSpec.Builder builder) {
		super(builder, "solar_boiler");
	}

	@Override
	protected void addConfigs() {
		RENDER_FLUID = builder.translation(key("render_fluid"))
				.comment("Whether or not fluids inside the solar boiler will be rendered.")
				.define("render_fluid", true);

		SUNLIGHT_EFFICIENCY = builder.translation(key("sunlight_efficiency"))
				.comment("Efficiency multiplier when the boiler has direct sky light "
						+ "(and no weather penalty).")
				.defineInRange("sunlight_efficiency", 1.0D, 0.0D, 1.0D);

		ARTIFICIAL_LIGHT_EFFICIENCY = builder.translation(key("artificial_light_efficiency"))
				.comment("Efficiency multiplier when the boiler is lit by block light "
						+ "instead of sky light.")
				.defineInRange("artificial_light_efficiency", 0.5D, 0.0D, 1.0D);

		ARTIFICIAL_LIGHT_THRESHOLD = builder.translation(key("artificial_light_threshold"))
				.comment("Minimum block light level for the artificial light path "
						+ "to be usable.")
				.defineInRange("artificial_light_threshold", 8, 0, 15);

		REQUIRE_DAYLIGHT = builder.translation(key("require_daylight"))
				.comment("Whether the sky light path additionally requires daytime.",
						"Vanilla sky light level stays at 15 at night, so without this "
								+ "the boiler would produce at full speed in complete darkness.")
				.define("require_daylight", true);

		RAIN_EFFICIENCY = builder.translation(key("rain_efficiency"))
				.comment("Weather multiplier while it is raining at the boiler.")
				.defineInRange("rain_efficiency", 0.5D, 0.0D, 1.0D);

		THUNDER_EFFICIENCY = builder.translation(key("thunder_efficiency"))
				.comment("Weather multiplier while a thunder storm is raging at the boiler "
						+ "(includes snow during thunder).")
				.defineInRange("thunder_efficiency", 0.25D, 0.0D, 1.0D);
	}

	/**
	 * 天气系数. 末地等没有天气的维度恒为 1.0.
	 */
	public static double getWeatherMultiplier(Level level, BlockPos pos) {
		if (level.isThundering() && (level.isRainingAt(pos) || level.isRaining())) {
			return THUNDER_EFFICIENCY.get();
		}
		if (level.isRainingAt(pos)) {
			return RAIN_EFFICIENCY.get();
		}
		return 1.0D;
	}

	/**
	 * 天空光路是否可用: (按配置) 处于白天. 遮挡与否由调用方检查方块.
	 * <p>
	 * 这里用世界时间判断昼夜, 而不是 {@code Level#isDay()}: 后者依赖 Level 内部的 skyDarken,
	 * 客户端只在进入世界时计算过一次, 之后每 tick 都不刷新, 于是夜里客户端仍然认为自己在白天,
	 * 护目镜就会显示 100% 而服务端早已停止产汽.
	 */
	public static boolean isSkyLightUsable(Level level) {
		if (!REQUIRE_DAYLIGHT.get()) {
			return true;
		}

		// 固定时间的维度 (下界, 末地) 没有昼夜循环, 昼夜这一关直接放行, 交给遮挡判定.
		if (level.dimensionType().hasFixedTime()) {
			return true;
		}

		// 一天 24000 tick: 0 是日出, 12000 是日落, 取余后落在前半段即为白天.
		// dayTime 由服务端每 20 tick 发包同步, 客户端跟着走, 不会像 skyDarken 那样冻住.
		return level.getDayTime() % 24000L < 12000L;
	}

	public static double getSunlightEfficiency() {
		return SUNLIGHT_EFFICIENCY.get();
	}

	public static double getArtificialLightEfficiency() {
		return ARTIFICIAL_LIGHT_EFFICIENCY.get();
	}

	public static int getArtificialLightThreshold() {
		return ARTIFICIAL_LIGHT_THRESHOLD.get();
	}
}
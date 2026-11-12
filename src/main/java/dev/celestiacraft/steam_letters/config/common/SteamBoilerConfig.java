package dev.celestiacraft.steam_letters.config.common;

import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 锅炉效率阶梯配置.
 * <p>
 * 阈值以「热量占燃烧室最高热量的百分比」表达, 默认 25/50/75/100 即文档中的 25/50/75/100 刻度
 * (默认 maxHeat = 100 时两者等价). 热量低于最低阈值时锅炉不产汽.
 * <p>
 * 过热蒸汽另有一套产率阶梯, 区间是 100 到过热上限 (默认为最高热量的 200%), 默认分 125/150/175 三段阈值.
 */
public class SteamBoilerConfig extends ConfigModule {
	public static ModConfigSpec.IntValue
			EFFICIENCY_THRESHOLD_1,
			EFFICIENCY_THRESHOLD_2,
			EFFICIENCY_THRESHOLD_3,
			EFFICIENCY_THRESHOLD_4;

	public static ModConfigSpec.DoubleValue
			EFFICIENCY_1, EFFICIENCY_2,
			EFFICIENCY_3,
			EFFICIENCY_4;

	public static ModConfigSpec.IntValue
			SUPERHEATED_THRESHOLD_1,
			SUPERHEATED_THRESHOLD_2,
			SUPERHEATED_THRESHOLD_3;

	public static ModConfigSpec.DoubleValue
			SUPERHEATED_PRODUCTION_1,
			SUPERHEATED_PRODUCTION_2,
			SUPERHEATED_PRODUCTION_3,
			SUPERHEATED_PRODUCTION_4;

	public SteamBoilerConfig(ModConfigSpec.Builder builder) {
		super(builder, "steam_boiler");
	}

	@Override
	protected void addConfigs() {
		EFFICIENCY_THRESHOLD_1 = builder.translation(key("efficiency_threshold_1"))
				.comment("Lowest heat percentage at which the boiler starts producing steam.")
				.defineInRange("efficiency_threshold_1", 25, 0, 100);
		EFFICIENCY_THRESHOLD_2 = builder.translation(key("efficiency_threshold_2"))
				.comment("Heat percentage at which the boiler reaches efficiency tier 2.")
				.defineInRange("efficiency_threshold_2", 50, 0, 100);
		EFFICIENCY_THRESHOLD_3 = builder.translation(key("efficiency_threshold_3"))
				.comment("Heat percentage at which the boiler reaches efficiency tier 3.")
				.defineInRange("efficiency_threshold_3", 75, 0, 100);
		EFFICIENCY_THRESHOLD_4 = builder.translation(key("efficiency_threshold_4"))
				.comment("Heat percentage required for the boiler to run at full efficiency.")
				.defineInRange("efficiency_threshold_4", 100, 0, 100);

		EFFICIENCY_1 = builder.translation(key("efficiency_1"))
				.comment("Steam output multiplier of efficiency tier 1.")
				.defineInRange("efficiency_1", 0.25D, 0.0D, 1.0D);
		EFFICIENCY_2 = builder.translation(key("efficiency_2"))
				.comment("Steam output multiplier of efficiency tier 2.")
				.defineInRange("efficiency_2", 0.5D, 0.0D, 1.0D);
		EFFICIENCY_3 = builder.translation(key("efficiency_3"))
				.comment("Steam output multiplier of efficiency tier 3.")
				.defineInRange("efficiency_3", 0.75D, 0.0D, 1.0D);
		EFFICIENCY_4 = builder.translation(key("efficiency_4"))
				.comment("Steam output multiplier of efficiency tier 4, i.e. the maximum boiler output.")
				.defineInRange("efficiency_4", 1.0D, 0.0D, 1.0D);

		SUPERHEATED_THRESHOLD_1 = builder.translation(key("superheated_threshold_1"))
				.comment("Heat percentage at which superheated production reaches tier 2.")
				.defineInRange("superheated_threshold_1", 125, 100, 1000);
		SUPERHEATED_THRESHOLD_2 = builder.translation(key("superheated_threshold_2"))
				.comment("Heat percentage at which superheated production reaches tier 3.")
				.defineInRange("superheated_threshold_2", 150, 100, 1000);
		SUPERHEATED_THRESHOLD_3 = builder.translation(key("superheated_threshold_3"))
				.comment("Heat percentage at which superheated production reaches tier 4, i.e. its full rate.")
				.defineInRange("superheated_threshold_3", 175, 100, 1000);

		SUPERHEATED_PRODUCTION_1 = builder.translation(key("superheated_production_1"))
				.comment("Superheated steam output multiplier while heat stays right above the maximum heat.")
				.defineInRange("superheated_production_1", 0.75D, 0.0D, 4.0D);
		SUPERHEATED_PRODUCTION_2 = builder.translation(key("superheated_production_2"))
				.comment("Superheated steam output multiplier of tier 2.")
				.defineInRange("superheated_production_2", 0.85D, 0.0D, 4.0D);
		SUPERHEATED_PRODUCTION_3 = builder.translation(key("superheated_production_3"))
				.comment("Superheated steam output multiplier of tier 3.")
				.defineInRange("superheated_production_3", 0.95D, 0.0D, 4.0D);
		SUPERHEATED_PRODUCTION_4 = builder.translation(key("superheated_production_4"))
				.comment("Superheated steam output multiplier of tier 4, i.e. the maximum superheated output.")
				.defineInRange("superheated_production_4", 1.0D, 0.0D, 4.0D);
	}

	/**
	 * 按热量百分比取得当前运行效率.
	 *
	 * @param heatPercent 热量占燃烧室最高热量的百分比
	 * @return 0.0 表示不产汽
	 */
	public static double getEfficiency(double heatPercent) {
		double value = Math.min(100.0D, Math.max(0.0D, heatPercent));
		if (value >= EFFICIENCY_THRESHOLD_4.get()) {
			return EFFICIENCY_4.get();
		}
		if (value >= EFFICIENCY_THRESHOLD_3.get()) {
			return EFFICIENCY_3.get();
		}
		if (value >= EFFICIENCY_THRESHOLD_2.get()) {
			return EFFICIENCY_2.get();
		}
		if (value >= EFFICIENCY_THRESHOLD_1.get()) {
			return EFFICIENCY_1.get();
		}
		return 0.0D;
	}

	/**
	 * 按热量百分比取得过热蒸汽的产率倍率.
	 * <p>
	 * 只在热量达到燃烧室最高热量 (即进入过热状态) 之后才有意义. 区间从 100% 到过热上限
	 * (默认为最高热量的 200%) 分四段, 该倍率乘在「产量 x 运行效率」之上.
	 *
	 * @param heatPercent 热量占燃烧室最高热量的百分比, 允许超过 100
	 * @return 过热蒸汽相对普通蒸汽的产率倍率
	 */
	public static double getSuperheatedProductionMultiplier(double heatPercent) {
		double value = Math.max(100.0D, heatPercent);
		if (value >= SUPERHEATED_THRESHOLD_3.get()) {
			return SUPERHEATED_PRODUCTION_4.get();
		}
		if (value >= SUPERHEATED_THRESHOLD_2.get()) {
			return SUPERHEATED_PRODUCTION_3.get();
		}
		if (value >= SUPERHEATED_THRESHOLD_1.get()) {
			return SUPERHEATED_PRODUCTION_2.get();
		}
		return SUPERHEATED_PRODUCTION_1.get();
	}
}
package dev.celestiacraft.steam_letters.datagen.language;

import dev.celestiacraft.steam_letters.api.client.lang.ChineseLangProvider;
import dev.celestiacraft.steam_letters.api.SlTranKeys;
import dev.celestiacraft.steam_letters.client.ponder.SlPonderPlugin;
import dev.celestiacraft.steam_letters.common.block.advanced_spout.AdvancedSpoutScrollValueBehaviour;
import dev.celestiacraft.steam_letters.common.register.SlBlocks;
import dev.celestiacraft.steam_letters.common.register.SlFluids;
import dev.celestiacraft.steam_letters.common.register.SlItems;
import dev.celestiacraft.steam_letters.common.register.SlMaterials;
import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import dev.celestiacraft.steam_letters.CreateLettersSteam;
import lombok.experimental.UtilityClass;
import net.createmod.ponder.api.registration.LangRegistryAccess;
import net.createmod.ponder.foundation.PonderIndex;

import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class Chinese {
	public void addTranslations(ChineseLangProvider provider) {
		addOtherLang(provider);
		addBlockLang(provider);
		addItemLang(provider);
		addFluidLang(provider);
		addCommonLang(provider);
		addSteamHammerLang(provider);
		addSteamMixerLang(provider);
		addSteamSawLang(provider);
		addSteamPumpLang(provider);
		addSteamPipeLang(provider);
		addBronzeBasinLang(provider);
		addAdvancedSpoutLang(provider);
		addAdvancedSpoutTooltipLang(provider);
		addGoggleLang(provider);
		addBronzeBoilerLang(provider);
		addSolarBoilerLang(provider);
		addBurnerLang(provider);
		addSteamEngineLang(provider);
		addSteamEngineTooltipLang(provider);
		addJeiLang(provider);
		addSteamContainerLang(provider);
		addConfigLang(provider);
		addConfigCommentLang(provider);
		addPonderLang(provider);
	}

	private void addOtherLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.THERMAL_PACK_TITLE, "§6热力色调包");
		provider.add(SlTranKeys.THERMAL_PACK_DESCRIPTION, "§6将青铜和设备修改为热力膨胀的青铜配色");
		provider.add(SlTranKeys.BASIC_TAB, "机械动力: 来自蒸汽的信");
	}

	private void addBlockLang(ChineseLangProvider provider) {
		provider.addBlock(SlBlocks.ADVANCED_SPOUT, "高级注液器");
		provider.addBlock(SlBlocks.BRONZE_BASIN, "青铜工作盆");
		provider.addBlock(SlMaterials.BRONZE_BLOCK, "青铜块");
		provider.addBlock(SlBlocks.BRONZE_BOILER, "青铜锅炉");
		provider.addBlock(SlBlocks.BRONZE_CASING, "青铜机壳");
		provider.addBlock(SlBlocks.BRONZE_DEPOT, "青铜置物台");
		provider.addBlock(SlBlocks.BRONZE_FLUID_BURNER, "青铜流体燃烧室");
		provider.addBlock(SlBlocks.BRONZE_SOLAR_BOILER, "青铜太阳能锅炉");
		provider.addBlock(SlBlocks.BRONZE_SOLID_BURNER, "青铜固体燃烧室");
		provider.addBlock(SlBlocks.BRONZE_STEAM_ENGINE, "青铜飞轮引擎");
		provider.addBlock(SlBlocks.BRONZE_STEAM_FLYWHEEL, "青铜蒸汽飞轮");
		provider.addBlock(SlBlocks.BRONZE_STEAM_PUMP, "青铜蒸汽泵");
		provider.addBlock(SlBlocks.STEAM_HAMMER, "蒸汽锻锤");
		provider.addBlock(SlBlocks.STEAM_MIXER, "蒸汽搅拌器");
		provider.addBlock(SlBlocks.STEAM_SAW, "蒸汽锯");
		provider.addBlock(SlMaterials.TIN_BLOCK, "锡块");
	}

	private void addItemLang(ChineseLangProvider provider) {
		provider.addItem(SlMaterials.BRONZE_INGOT, "青铜锭");
		provider.addItem(SlMaterials.BRONZE_PLATE, "青铜板");
		provider.addItem(SlItems.BRONZE_WHISK, "青铜搅拌器");
		provider.addItem(SlItems.EMPTY_STEAM_CONTAINER, "空的密封蒸汽容器");
		provider.addItem(SlItems.INCOMPLETE_STEAM_MECHANISM, "蒸汽构件(半成品)");
		provider.addItem(SlItems.SEALED_STEAM_CONTAINER, "密封蒸汽容器");
		provider.addItem(SlItems.SH_STEAM_COMPONENT, "过热蒸汽组件");
		provider.addItem(SlFluids.STEAM.get().getBucket(), "蒸汽桶");
		provider.addItem(SlItems.STEAM_MECHANISM, "蒸汽构件");
		provider.addItem(SlFluids.SUPERHEATED_STEAM.get().getBucket(), "过热蒸汽桶");
		provider.addItem(SlMaterials.TIN_INGOT, "锡锭");
		provider.addItem(SlMaterials.TIN_NUGGET, "锡粒");
	}

	private void addFluidLang(ChineseLangProvider provider) {
		provider.addFluid(SlFluids.STEAM, "蒸汽");
		provider.addFluid(SlFluids.SUPERHEATED_STEAM, "过热蒸汽");
	}

	private void addCommonLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_CAPACITY), "机器内部能缓存 _%s mB_ 蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_CONDITION), "工作时:");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_CONSUMPTION), "消耗 _%s mB_ 蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_PARALLEL), "每轮最多处理 _%s_ 份");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_PRODUCTION), "满效率产出 _%s mB/t_ 蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_ROTATION), "需要应力");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_STRESS_IMPACT), "应力影响: _%s_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_SUPERHEATED_CONSUMPTION), "使用_过热蒸汽_时, 消耗降至 _%s_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_COMMON_WATER_STEAM_CAPACITY), "内部水与蒸汽各 _%s mB_");
	}

	private void addSteamHammerLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_HAMMER_SUMMARY), "可处理_压片_, _压缩_与_自动打包_配方");
	}

	private void addSteamMixerLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_MIXER_SUMMARY), "处理搅拌配方");
	}

	private void addSteamSawLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_SAW_CONSUMPTION), "每份产出消耗 _%s mB_ 蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_SAW_PROCESSING_TIME), "单份耗时仅为动力锯的 _%s_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_SAW_SUMMARY), "处理切割配方");
	}

	private void addSteamPumpLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PUMP_BEHAVIOUR1), "不需要应力, 也不消耗应力");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PUMP_BEHAVIOUR2), "接上蒸汽管道或蒸汽容器后便会自行开始工作");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PUMP_BEHAVIOUR3), "抽取侧管网混入其他流体时会整体停泵");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PUMP_SUMMARY), "只输送蒸汽, 且只有当管道中的介质是蒸汽时才会工作");
	}

	private void addSteamPipeLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PIPE_BEHAVIOUR), "与青铜蒸汽泵搭配使用");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_PIPE_SUMMARY), "只允许蒸汽通过这条管道");
	}

	private void addBronzeBasinLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_BRONZE_DEPOT_CONTENTS), "青铜置物台内容");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BASIN_BEHAVIOUR1), "可以容纳多种流体和物品, 供搅拌与压块配方使用");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BASIN_BEHAVIOUR2), "一次可以处理更多配方, 不必频繁补充流体");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BASIN_SUMMARY), "每个流体槽可容纳 _%s mB_ 流体, 共 4 槽");
	}

	private void addAdvancedSpoutLang(ChineseLangProvider provider) {
		provider.add(AdvancedSpoutScrollValueBehaviour.LANG_PARALLEL, "并行");
		provider.add(AdvancedSpoutScrollValueBehaviour.LANG_VALUE_PARALLEL, "x%s");
	}

	private void addAdvancedSpoutTooltipLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_ADVANCED_SPOUT_CAPACITY), "机器内部能缓存 _%s mB_ 流体");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_ADVANCED_SPOUT_NO_STEAM), "不需要蒸汽与应力");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_ADVANCED_SPOUT_PARALLEL), "拥有 _%s_ 并行 (可用扳手调整, 1–20)");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_ADVANCED_SPOUT_SUMMARY), "以 _%s_ tick 的速度完成一次注液");
	}

	private void addGoggleLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_BOILER_STEAM_OUTPUT_RATE), "蒸汽产率");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_BOILER_WATER_STORED), "储水");

		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER), "太阳能锅炉");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_IDLE), "无光照, 未产出");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_ARTIFICIAL), "光源: 人造光");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_NONE), "光源: 无");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_RAIN), "光源: 太阳光 (雨天)");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_SUNLIGHT), "光源: 太阳光");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_LIGHT_THUNDER), "光源: 太阳光 (雷雨)");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_PRODUCTION), "蒸汽生产速率");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_RATE), "占满速比例");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_SOLAR_BOILER_WORKING), "正在产出蒸汽");

		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_EFFICIENCY), "运行效率");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_HEAT), "热量");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_LEVEL), "锅炉等级");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_NO_BURNER), "下方没有燃烧室");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_SIZE), "结构尺寸");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_STEAM), "蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED), "过热");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED_NO), "否");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BOILER_SUPERHEATED_YES), "是");

		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BURNER), "蒸汽燃烧室");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BURNER_EFFICIENCY), "生产效率");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BURNER_FUEL_TIME), "剩余燃烧时间");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BURNER_HEAT), "热量");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_BURNER_TEMPERATURE), "温度");

		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_MACHINE_OPERATIONS), "可加工");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_MACHINE_PARALLEL), "并行");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_MACHINE_STEAM), "蒸汽");
	}

	private void addBronzeBoilerLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BOILER_NEEDS_BURNER), "需要正下方的燃烧室");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BOILER_SUMMARY), "把水加热成蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BRONZE_BOILER_SUPERHEATED), "满热量时产出过热蒸汽");
	}

	private void addSolarBoilerLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_SOLAR_BOILER_LIGHT), "日照 _%s_, 人造光 _%s_ (光照 ≥ _%s_)");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_SOLAR_BOILER_SUMMARY), "用光照把水变成蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_SOLAR_BOILER_WEATHER), "雨天 _%s_, 雷雨 _%s_");
	}

	private void addBurnerLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BURNER_CYCLE), "烧满需 _%s_ 秒, 冷却需 _%s_ 秒");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BURNER_MAX_HEAT), "最高热量 _%s_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_BURNER_SUPERHEATED), "过热燃料升温 x_%s_, 上限 _%s_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_FLUID_BURNER_FUEL_CYCLE), "每 _1000 mB_ 燃料燃烧一轮");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_FLUID_BURNER_SUMMARY), "消耗流体燃料, 为上方锅炉提供热量");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_FLUID_BURNER_TANK), "内部燃料罐 _%s mB_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_SOLID_BURNER_SUMMARY), "为上方锅炉提供热量");
	}

	private void addSteamEngineLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE), "飞轮引擎");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_HEATUP), "预热进度");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_STATUS_HEATUP), "预热中");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_STATUS_IDLE), "未运行");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_STATUS_RUNNING), "运行中");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_STEAM), "蒸汽储量");
		provider.add(SlTranKeys.langKey(SlTranKeys.GOGGLE_STEAM_ENGINE_STRESS), "应力输出");
	}

	private void addSteamEngineTooltipLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_CAPACITY), "内部可缓存 _%s mB_ 蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_CONSUMPTION), "满负荷消耗 _%s mB/t_ 蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_DIRECT_DRAW), "从相邻锅炉直抽时效率 _%s_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_HEATUP), "预热 _%s_ 秒后达到满输出");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_OUTPUT), "输出 _%s SU_ / _%s RPM_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_SUMMARY), "把蒸汽转化为应力");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_SUPERHEATED_EFFECTS), "耗汽 x_%s_, 输出 x_%s_");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_ENGINE_SUPERHEATED_HEADER), "过热蒸汽:");
	}

	private void addJeiLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.JEI_FLUID_FUEL), "流体燃料");
		provider.add(SlTranKeys.langKey(SlTranKeys.JEI_HEATED), "加热");
		provider.add(SlTranKeys.langKey(SlTranKeys.JEI_SOLID_FUEL), "固体燃料");
		provider.add(SlTranKeys.langKey(SlTranKeys.JEI_SUPERHEATED), "超级加热");
	}

	private void addSteamContainerLang(ChineseLangProvider provider) {
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_EMPTY), "可容纳 _%s mB_ 蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_EXTRACT), "▸ 右键锅炉, 流体储罐或飞轮引擎以抽取蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_EXTRACT_MORE), "▸ 潜行右键以继续抽取");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_FLUID), "%s: _%s_ / %s mB");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_INSERT), "▸ 右键流体储罐或飞轮引擎以注入蒸汽");
		provider.add(SlTranKeys.langKey(SlTranKeys.TOOLTIP_STEAM_CONTAINER_ONLY_STEAM), "仅能储存蒸汽与过热蒸汽");
	}

	private void addConfigLang(ChineseLangProvider provider) {
		provider.add(ConfigModule.translationKey("advanced_spout"), "高级注液器");
		provider.add(ConfigModule.translationKey("advanced_spout", "capacity"), "高级注液器的流体容量(mB)");
		provider.add(ConfigModule.translationKey("advanced_spout", "parallel"), "高级注液器的默认并行数(可用长按滑条调整)");

		provider.add(ConfigModule.translationKey("boiler_steam"), "大锅炉蒸汽驱动");
		provider.add(ConfigModule.translationKey("boiler_steam", "buffer_multiplier"), "蒸汽缓冲容量倍率(每个锅炉方块 1000 mB)");

		provider.add(ConfigModule.translationKey("bronze_basin"), "青铜工作盆");
		provider.add(ConfigModule.translationKey("bronze_basin", "slot_capacity"), "青铜工作盆单个罐的流体容量(mB)");

		provider.add(ConfigModule.translationKey("general"), "通用");

		provider.add(ConfigModule.translationKey("solar_boiler"), "太阳能锅炉");
		provider.add(ConfigModule.translationKey("solar_boiler", "artificial_light_efficiency"), "人造光下太阳能锅炉的产出效率倍率");
		provider.add(ConfigModule.translationKey("solar_boiler", "artificial_light_threshold"), "判定为人造光所需的最低方块光照等级");
		provider.add(ConfigModule.translationKey("solar_boiler", "rain_efficiency"), "降雨天气的效率倍率");
		provider.add(ConfigModule.translationKey("solar_boiler", "render_fluid"), "是否渲染太阳能锅炉内部的流体");
		provider.add(ConfigModule.translationKey("solar_boiler", "require_daylight"), "太阳光路是否额外要求处于白天(原版天空光等级在夜间仍为满值)");
		provider.add(ConfigModule.translationKey("solar_boiler", "sunlight_efficiency"), "太阳能锅炉无遮挡时的产出效率倍率");
		provider.add(ConfigModule.translationKey("solar_boiler", "thunder_efficiency"), "雷暴天气的效率倍率");

		provider.add(ConfigModule.translationKey("steam"), "蒸汽");
		provider.add(ConfigModule.translationKey("steam", "do_overheated_steam_replace"), "进入过热状态时, 输出罐里残留的普通蒸汽是转换为过热蒸汽还是直接销毁");
		provider.add(ConfigModule.translationKey("steam", "output_steam_fluid"), "锅炉产出的流体 ID. 修改后机器内已有的流体会被原样保留, 不会被转换");
		provider.add(ConfigModule.translationKey("steam", "output_superheated_steam_fluid"), "锅炉在过热状态下产出的流体 ID. 修改后机器内已有的流体不会被转换");
		provider.add(ConfigModule.translationKey("steam", "superheated_steam_consumption_multiplier"), "机器使用过热蒸汽时的蒸汽消耗倍率");

		provider.add(ConfigModule.translationKey("steam_boiler"), "锅炉");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_1"), "锅炉第 1 档运行效率的产出倍率");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_2"), "锅炉第 2 档运行效率的产出倍率");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_3"), "锅炉第 3 档运行效率的产出倍率");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_4"), "锅炉第 4 档运行效率的产出倍率");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_threshold_1"), "锅炉进入第 1 档效率所需的热量百分比");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_threshold_2"), "锅炉进入第 2 档效率所需的热量百分比");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_threshold_3"), "锅炉进入第 3 档效率所需的热量百分比");
		provider.add(ConfigModule.translationKey("steam_boiler", "efficiency_threshold_4"), "锅炉进入第 4 档效率所需的热量百分比");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_production_1"), "过热蒸汽第 1 档产出倍率");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_production_2"), "过热蒸汽第 2 档产出倍率");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_production_3"), "过热蒸汽第 3 档产出倍率");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_production_4"), "过热蒸汽第 4 档产出倍率");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_threshold_1"), "过热蒸汽产出提升到第 2 档所需的热量百分比");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_threshold_2"), "过热蒸汽产出提升到第 3 档所需的热量百分比");
		provider.add(ConfigModule.translationKey("steam_boiler", "superheated_threshold_3"), "过热蒸汽产出提升到第 4 档所需的热量百分比");

		provider.add(ConfigModule.translationKey("steam_burner"), "燃烧室");
		provider.add(ConfigModule.translationKey("steam_burner", "cooldown_seconds"), "热量冷却至 0 所需时间(秒)");
		provider.add(ConfigModule.translationKey("steam_burner", "heatup_seconds"), "烧满热量所需时间(秒)");
		provider.add(ConfigModule.translationKey("steam_burner", "max_temperature"), "最高温度");
		provider.add(ConfigModule.translationKey("steam_burner", "show_temperature"), "护目镜显示温度");
		provider.add(ConfigModule.translationKey("steam_burner", "superheated_heatup_multiplier"), "燃烧过热燃料时的升温速度倍率");

		provider.add(ConfigModule.translationKey("steam_engine"), "飞轮引擎");
		provider.add(ConfigModule.translationKey("steam_engine", "heatup_seconds"), "预热时长(秒)");
		provider.add(ConfigModule.translationKey("steam_engine", "suck_efficiency"), "飞轮引擎从锅炉直抽蒸汽的效率(0.75 即 25% 损耗)");
		provider.add(ConfigModule.translationKey("steam_engine", "superheated_acceleration_multiplier"), "使用过热蒸汽时预热时长的缩短倍率");
		provider.add(ConfigModule.translationKey("steam_engine", "superheated_consumption_multiplier"), "使用过热蒸汽时的蒸汽消耗倍率");
		provider.add(ConfigModule.translationKey("steam_engine", "superheated_output_multiplier"), "使用过热蒸汽时的应力输出倍率");

		provider.add(ConfigModule.translationKey("steam_hammer"), "蒸汽锻锤");
		provider.add(ConfigModule.translationKey("steam_hammer", "steam_capacity"), "蒸汽锻锤的蒸汽容量(mB)");
		provider.add(ConfigModule.translationKey("steam_hammer", "steam_consumption"), "蒸汽锻锤每次工作的蒸汽消耗(mB)");
		provider.add(ConfigModule.translationKey("steam_hammer", "steam_hammer_parallel"), "蒸汽锻锤一次工作的配方并行数");

		provider.add(ConfigModule.translationKey("steam_mixer"), "蒸汽搅拌器");
		provider.add(ConfigModule.translationKey("steam_mixer", "steam_capacity"), "蒸汽搅拌器的蒸汽容量(mB)");
		provider.add(ConfigModule.translationKey("steam_mixer", "steam_consumption"), "蒸汽搅拌器每次工作的蒸汽消耗(mB)");
		provider.add(ConfigModule.translationKey("steam_mixer", "steam_mixer_parallel"), "蒸汽搅拌器一次工作的配方并行数");

		provider.add(ConfigModule.translationKey("steam_saw"), "蒸汽锯");
		provider.add(ConfigModule.translationKey("steam_saw", "processing_time_multiplier"), "蒸汽锯的工作时间倍率");
		provider.add(ConfigModule.translationKey("steam_saw", "steam_capacity"), "蒸汽锯的蒸汽容量(mB)");
		provider.add(ConfigModule.translationKey("steam_saw", "steam_consumption"), "蒸汽锯每次工作的蒸汽消耗(mB)");
		provider.add(ConfigModule.translationKey("steam_saw", "steam_saw_parallel"), "蒸汽锯一次工作的配方并行数");
	}

	private void addConfigCommentLang(ChineseLangProvider provider) {
		comment(provider, ConfigModule.translationKey("advanced_spout", "capacity"), "高级注液器的流体缓存容量(mB).");
		comment(provider, ConfigModule.translationKey("advanced_spout", "parallel"), """
				新放置的高级注液器的默认并行数,
				之后仍可用扳手逐台调整.""");

		comment(provider, ConfigModule.translationKey("boiler_steam", "buffer_multiplier"), """
				缓冲容量 = 锅炉方块数 * 1000 * 该倍率.
				0 表示锅炉不存储蒸汽.""");

		comment(provider, ConfigModule.translationKey("bronze_basin", "slot_capacity"), """
				青铜工作盆每个流体罐的容量(mB).
				工作盆有多个流体罐, 总容量是这个值的数倍.""");

		comment(provider, ConfigModule.translationKey("general"), "下方所有设置都需要重启服务端或客户端后才会生效.");

		comment(provider, ConfigModule.translationKey("solar_boiler", "artificial_light_efficiency"), "锅炉被方块光照亮而非天空光时的效率倍率.");
		comment(provider, ConfigModule.translationKey("solar_boiler", "artificial_light_threshold"), "人造光路可用所需的最低方块光照等级.");
		comment(provider, ConfigModule.translationKey("solar_boiler", "rain_efficiency"), "锅炉所在位置降雨时的天气倍率.");
		comment(provider, ConfigModule.translationKey("solar_boiler", "render_fluid"), """
				是否渲染太阳能锅炉内部的流体.
				关闭后不影响蒸汽产出.""");
		comment(provider, ConfigModule.translationKey("solar_boiler", "require_daylight"), """
				天空光路是否额外要求处于白天.
				原版天空光等级在夜间仍为 15, 不开启该选项时锅炉会在完全黑暗中满速产出.""");
		comment(provider, ConfigModule.translationKey("solar_boiler", "sunlight_efficiency"), "锅炉直接被天空光照射(且无天气惩罚)时的效率倍率.");
		comment(provider, ConfigModule.translationKey("solar_boiler", "thunder_efficiency"), "锅炉所在位置雷暴时的天气倍率(雷暴中的降雪同样计入).");

		comment(provider, ConfigModule.translationKey("steam", "do_overheated_steam_replace"), """
				false 时输出罐中残留的普通蒸汽会被直接销毁; true 时就地转换为等量的过热蒸汽.
				两种方式都不额外消耗水.""");
		comment(provider, ConfigModule.translationKey("steam", "output_steam_fluid"), "蒸汽锅炉与太阳能锅炉产出的流体 ID. 修改后储罐内已有的流体不会被转换.");
		comment(provider, ConfigModule.translationKey("steam", "output_superheated_steam_fluid"), """
				锅炉在过热状态下产出的流体 ID.
				不得与 output_steam_fluid 指向同一个流体, 否则配置加载失败.
				修改后储罐内已有的流体不会被转换.""");
		comment(provider, ConfigModule.translationKey("steam", "superheated_steam_consumption_multiplier"), """
				所有机器使用过热蒸汽时的全局蒸汽消耗倍率.
				0.25 表示只消耗普通蒸汽的四分之一.""");

		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_1"), "第 1 档效率时的蒸汽产出倍率.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_2"), "第 2 档效率时的蒸汽产出倍率.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_3"), "第 3 档效率时的蒸汽产出倍率.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_4"), "第 4 档效率时的蒸汽产出倍率, 即锅炉的最大产出.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_threshold_1"), "锅炉开始产出蒸汽所需的最低热量百分比.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_threshold_2"), "锅炉进入第 2 档效率所需的热量百分比.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_threshold_3"), "锅炉进入第 3 档效率所需的热量百分比.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "efficiency_threshold_4"), "锅炉达到满效率所需的热量百分比.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_production_1"), "热量刚超过最高热量时的过热蒸汽产出倍率.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_production_2"), "过热蒸汽第 2 档产出倍率.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_production_3"), "过热蒸汽第 3 档产出倍率.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_production_4"), "过热蒸汽第 4 档产出倍率, 即过热蒸汽的最大产出.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_threshold_1"), "过热蒸汽产出提升到第 2 档所需的热量百分比.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_threshold_2"), "过热蒸汽产出提升到第 3 档所需的热量百分比.");
		comment(provider, ConfigModule.translationKey("steam_boiler", "superheated_threshold_3"), "过热蒸汽产出达到最大倍率所需的热量百分比.");

		comment(provider, ConfigModule.translationKey("steam_burner", "cooldown_seconds"), "没有燃料燃烧时, 从最高热量降到 0 所需的秒数. 0 表示瞬间冷却.");
		comment(provider, ConfigModule.translationKey("steam_burner", "heatup_seconds"), "燃烧燃料时, 从 0 升到最高热量所需的秒数. 0 表示瞬间烧满.");
		comment(provider, ConfigModule.translationKey("steam_burner", "max_temperature"), "燃烧燃料时的温度上限, 对注册时未指定温度上限的设备生效. 温度只用于显示, 不参与任何计算.");
		comment(provider, ConfigModule.translationKey("steam_burner", "show_temperature"), "是否在护目镜上显示燃烧室的温度数值.");
		comment(provider, ConfigModule.translationKey("steam_burner", "superheated_heatup_multiplier"), """
				燃烧过热燃料且热量未达到最高热量时, 升温时间除以该倍率.
				4.0 表示升温速度快 4 倍.""");

		comment(provider, ConfigModule.translationKey("steam_engine", "heatup_seconds"), """
				飞轮引擎从开始供汽到输出满档应力所需的秒数.
				预热时长四等分, 蒸汽消耗依次为满负荷的 25%, 50%, 75%, 100%.
				0 表示无需预热, 立刻达到满档.""");
		comment(provider, ConfigModule.translationKey("steam_engine", "suck_efficiency"), """
				从相邻锅炉直抽蒸汽的效率(0~1).
				每 tick 从锅炉抽出 4n mB, 实际只有 3n mB 进入引擎,
				因此直连供气的引擎应力输出上限为 75%.""");
		comment(provider, ConfigModule.translationKey("steam_engine", "superheated_acceleration_multiplier"), """
				使用过热蒸汽时, 预热时长按该倍率缩短.
				"2.0 表示预热时间减半.""");
		comment(provider, ConfigModule.translationKey("steam_engine", "superheated_consumption_multiplier"), """
				飞轮引擎使用过热蒸汽时的蒸汽消耗倍率.
				0.5 表示只需普通蒸汽的一半.""");
		comment(provider, ConfigModule.translationKey("steam_engine", "superheated_output_multiplier"), "使用过热蒸汽时的应力输出倍率.");

		comment(provider, ConfigModule.translationKey("steam_hammer", "steam_capacity"), "蒸汽锻锤内部可缓存的蒸汽量(mB).");
		comment(provider, ConfigModule.translationKey("steam_hammer", "steam_consumption"), "蒸汽锻锤每轮工作消耗的蒸汽(mB), 与并行份数无关.");
		comment(provider, ConfigModule.translationKey("steam_hammer", "steam_hammer_parallel"), "蒸汽锻锤同时处理的配方数上限.");

		comment(provider, ConfigModule.translationKey("steam_mixer", "steam_capacity"), "蒸汽搅拌器内部可缓存的蒸汽量(mB).");
		comment(provider, ConfigModule.translationKey("steam_mixer", "steam_consumption"), "蒸汽搅拌器每轮工作消耗的蒸汽(mB), 与并行份数无关.");
		comment(provider, ConfigModule.translationKey("steam_mixer", "steam_mixer_parallel"), "蒸汽搅拌器同时处理的配方数上限.");

		comment(provider, ConfigModule.translationKey("steam_saw", "processing_time_multiplier"), """
				蒸汽锯的工作时间倍率.
				0.25 表示比机械锯快 4 倍.""");
		comment(provider, ConfigModule.translationKey("steam_saw", "steam_capacity"), "蒸汽锯内部可缓存的蒸汽量(mB).");
		comment(provider, ConfigModule.translationKey("steam_saw", "steam_consumption"), "蒸汽锯每完成一次配方消耗的蒸汽(mB).");
		comment(provider, ConfigModule.translationKey("steam_saw", "steam_saw_parallel"), "蒸汽锯同时处理的配方数上限.");
	}

	/**
	 * Ponder 的文本注册.
	 * <p>
	 * Ponder 内部只保存英文原文, provideLang 会把它们按 <模组 id>.ponder.<场景>.<键> 的形式写进语言文件,
	 * 因此这里先交给 Ponder 生成, 再用中文覆盖标签名称.
	 */
	private void addPonderLang(ChineseLangProvider provider) {
		SlPonderPlugin.register();
		LangRegistryAccess access = PonderIndex.getLangAccess();
		Map<String, String> lang = new LinkedHashMap<>();
		access.provideLang(CreateLettersSteam.MODID, lang::put);

		lang.put(SlTranKeys.langKey("ponder.tag.steam_letters"), "机械动力: 来自蒸汽的信");
		lang.put(SlTranKeys.langKey("ponder.tag.steam_letters.description"), "设备");
		lang.forEach(provider::add);
	}

	/**
	 * 配置项注释的中文键.
	 * <p>
	 * Configured 会以 <界面翻译键>.comment 查询注释, 命中即显示译文, 未命中则回退到 toml 里的原文.
	 * 因此这里的内容必须与配置文件中的中文注释逐条对应.
	 */
	private void comment(ChineseLangProvider provider, String translationKey, String comment) {
		provider.add(translationKey + ".comment", comment);
	}
}
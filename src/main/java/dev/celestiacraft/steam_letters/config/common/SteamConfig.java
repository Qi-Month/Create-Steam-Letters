package dev.celestiacraft.steam_letters.config.common;

import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.config.api.ConfigModule;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;

public class SteamConfig extends ConfigModule {
	public static ModConfigSpec.DoubleValue
			SUPERHEATED_STEAM_CONSUMPTION_MULTIPLIER;

	public static ModConfigSpec.ConfigValue<String>
			OUTPUT_STEAM_FLUID,
			OUTPUT_SUPERHEATED_STEAM_FLUID;

	public static ModConfigSpec.BooleanValue
			DO_OVERHEATED_STEAM_REPLACE;

	public SteamConfig(ModConfigSpec.Builder builder) {
		super(builder, "steam");
	}

	@Override
	protected void addConfigs() {
		SUPERHEATED_STEAM_CONSUMPTION_MULTIPLIER = builder.translation(key("superheated_steam_consumption_multiplier"))
				.comment("Global steam consumption multiplier for machines running on superheated steam.",
					"0.25 means such a machine only needs a quarter of the normal steam.")
				.defineInRange("superheated_steam_consumption_multiplier", 0.25, 0.0, 1.0);

		OUTPUT_STEAM_FLUID = builder.translation(key("output_steam_fluid"))
				.comment("Fluid id produced by steam boilers and solar boilers.",
					"Existing tank contents are never converted when this changes.")
				.define("output_steam_fluid", "steam_letters:steam");

		OUTPUT_SUPERHEATED_STEAM_FLUID = builder.translation(key("output_superheated_steam_fluid"))
				.comment("Fluid id produced by steam boilers while a superheated steam component is installed and seething burners sit below.",
					"Must differ from output_steam_fluid, otherwise the config fails to load.",
					"Existing tank contents are never converted when this changes.")
				.define("output_superheated_steam_fluid", "steam_letters:superheated_steam");

		DO_OVERHEATED_STEAM_REPLACE = builder.translation(key("do_overheated_steam_replace"))
				.comment("How a boiler clears the normal steam already sitting in its output tank",
					"before it starts producing superheated steam.",
					"false: destroy that steam. true: convert it in place to the same amount of superheated steam.")
				.define("do_overheated_steam_replace", false);
	}

	/**
	 * 解析配置里声明的输出蒸汽流体.
	 * <p>
	 * 修改该配置不会转换 / 清空 / 销毁任何已在世界里的流体, 只会影响之后新产出的蒸汽.
	 *
	 * @return 配置无效时回退到本模组自带的蒸汽
	 */
	public static Fluid getOutputSteamFluid() {
		return resolveFluid(OUTPUT_STEAM_FLUID, "steam");
	}

	/**
	 * 解析配置里声明的输出过热蒸汽流体.
	 *
	 * @return 配置无效时回退到本模组自带的过热蒸汽
	 */
	public static Fluid getOutputSuperheatedSteamFluid() {
		return resolveFluid(OUTPUT_SUPERHEATED_STEAM_FLUID, "superheated_steam");
	}

	/**
	 * 普通蒸汽与过热蒸汽不允许配置成同一个流体: 输出槽是单流体容器,
	 * 两者相同时锅炉无法区分自己当前产出的是哪一种.
	 * <p>
	 * 校验失败时直接抛出异常, 让这次配置加载失败.
	 */
	public static void validate() {
		Fluid steam = getOutputSteamFluid();
		Fluid superheated = getOutputSuperheatedSteamFluid();
		if (superheated == null || steam != superheated) {
			return;
		}

		ResourceLocation id = BuiltInRegistries.FLUID.getKey(steam);
		throw new IllegalStateException("output_steam_fluid and output_superheated_steam_fluid "
				+ "must not point to the same fluid, but both resolve to "
				+ (id == null ? steam.toString() : id.toString()));
	}

	@Nullable
	private static Fluid resolveFluid(ModConfigSpec.ConfigValue<String> config, String fallbackId) {
		ResourceLocation id = ResourceLocation.tryParse(config.get());
		if (id != null) {
			Fluid fluid = BuiltInRegistries.FLUID.get(id);
			if (fluid != null) {
				return fluid;
			}
		}
		return BuiltInRegistries.FLUID.get(SlApis.loadResource(fallbackId));
	}
}
package dev.celestiacraft.steam_letters;

import com.simibubi.create.api.stress.BlockStressValues;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.api.SlStress;
import dev.celestiacraft.steam_letters.api.recipe.SlRecipeBuilders;
import dev.celestiacraft.steam_letters.client.block.SlBlockPartialModel;
import dev.celestiacraft.steam_letters.common.register.SlRecipeTypes;
import dev.celestiacraft.steam_letters.common.register.*;
import dev.celestiacraft.steam_letters.config.CommonConfigs;
import dev.celestiacraft.steam_letters.config.common.SteamConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CreateLettersSteam.MODID)
public class CreateLettersSteam {
	public static final String MODID = "steam_letters";
	public static final String NAME = "Create: Letter's in Steam";
	public static final String CLASS_NAME = CreateLettersSteam.class.getSimpleName();
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	private static SlStress STRESS_VALUES;
	private static ForgeConfigSpec STRESS_VALUES_SPEC;

	public CreateLettersSteam(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();

		SlApis.REGISTRATE.registerEventListeners(bus);
		SlMaterials.init();
		SlItems.init();
		SlBlocks.init();
		SlBlockEntities.init();
		SlFluids.init();
		SlParticles.init(bus);
		SlRecipeTypes.init();
		SlRecipeBuilders.init();
		SlCreativeTabs.init(bus);
		SlConditionTypes.init();

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
			return () -> {
				SlBlockPartialModel.init();
			};
		});

		registerConfig(context);
	}

	private void registerConfig(FMLJavaModLoadingContext context) {
		context.registerConfig(ModConfig.Type.COMMON, CommonConfigs.SPEC, "nebula/steam_letters/common.toml");

		if (STRESS_VALUES == null) {
			Pair<SlStress, ForgeConfigSpec> stressConfig = SlStress.createConfig();
			STRESS_VALUES = stressConfig.getLeft();
			STRESS_VALUES_SPEC = stressConfig.getRight();

			BlockStressValues.IMPACTS.registerProvider(STRESS_VALUES::getImpact);
			BlockStressValues.CAPACITIES.registerProvider(STRESS_VALUES::getCapacity);
		}
		context.registerConfig(ModConfig.Type.SERVER, STRESS_VALUES_SPEC, "nebula/steam_letters/server.toml");
	}

	@SubscribeEvent
	public void onConfigLoad(ModConfigEvent.Loading event) {
		if (STRESS_VALUES != null && event.getConfig().getSpec().equals(STRESS_VALUES_SPEC)) {
			STRESS_VALUES.onLoad();
		}

		if (event.getConfig().getSpec().equals(CommonConfigs.SPEC)) {
			SteamConfig.validate();
		}
	}

	@SubscribeEvent
	public void onConfigReload(ModConfigEvent.Reloading event) {
		if (STRESS_VALUES != null && event.getConfig().getSpec().equals(STRESS_VALUES_SPEC)) {
			STRESS_VALUES.onReload();
		}

		if (event.getConfig().getSpec().equals(CommonConfigs.SPEC)) {
			SteamConfig.validate();
		}
	}
}
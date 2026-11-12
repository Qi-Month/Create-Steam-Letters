package dev.celestiacraft.steam_letters;

import com.simibubi.create.api.stress.BlockStressValues;
import dev.celestiacraft.steam_letters.api.SlApis;
import dev.celestiacraft.steam_letters.api.SlStress;
import dev.celestiacraft.steam_letters.common.register.*;
import dev.celestiacraft.steam_letters.config.CommonConfigs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
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
	private static ModConfigSpec STRESS_VALUES_SPEC;

	public CreateLettersSteam(IEventBus bus, ModContainer container) {
		SlApis.REGISTRATE.registerEventListeners(bus);
		SlMaterials.init();
		SlItems.init();
		SlBlocks.init();
		SlBlockEntities.init();
		SlFluids.init();
		SlParticles.init(bus);
		SlRecipeTypes.init(bus);
		SlCreativeTabs.init(bus);
		SlDataComponents.init(bus);

		bus.addListener(RegisterCapabilitiesEvent.class, SlCapabilities::register);

		registerConfig(container);
	}

	private void registerConfig(ModContainer container) {
		container.registerConfig(ModConfig.Type.COMMON, CommonConfigs.SPEC, "nebula/steam_letters/common.toml");

		if (STRESS_VALUES == null) {
			Pair<SlStress, ModConfigSpec> stressConfig = SlStress.createConfig();
			STRESS_VALUES = stressConfig.getLeft();
			STRESS_VALUES_SPEC = stressConfig.getRight();

			BlockStressValues.IMPACTS.registerProvider(STRESS_VALUES::getImpact);
			BlockStressValues.CAPACITIES.registerProvider(STRESS_VALUES::getCapacity);
		}
		container.registerConfig(ModConfig.Type.SERVER, STRESS_VALUES_SPEC, "nebula/steam_letters/server.toml");
	}
}
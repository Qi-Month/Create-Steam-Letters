package dev.celestiacraft.steam_letters;

import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CreateLettersSteam.MODID)
public class CreateLettersSteam {
	public static final String MODID = "steam_letters";
	public static final String NAME = "Create: Letter's in Steam";
	public static final String CLASS_NAME = CreateLettersSteam.class.getSimpleName();
	public static final Logger LOGGER = LogManager.getLogger(NAME);
}
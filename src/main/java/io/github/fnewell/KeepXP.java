package io.github.fnewell;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepXP implements ModInitializer {
	public static final String MODID = "keep-xp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		// Check if server has Permission API
		if (FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0")) {
			LOGGER.info("Permissions API found. KeepXP is running based on permissions.");
		} else {
			LOGGER.info("Permissions API not found. KeepXP is turned on for all players.");
		}
	}
}
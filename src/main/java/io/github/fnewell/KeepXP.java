package io.github.fnewell;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KeepXP implements ModInitializer {
	public static final String MODID = "keep-xp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	private static MinecraftServer server;
	public static boolean keepXPoverride = false;
	public static boolean permissionsAPI = false;

	@Override
	public void onInitialize() {
		// Register the event handler
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

		// Register the commands
		CommandUtils.RegisterCommands();
	}

	private void onServerStarted(MinecraftServer server) {
		// Set the server instance
		KeepXP.server = server;

		// Get the server instance and check if KeepXP override is turned on
		DataUtils serverState = DataUtils.getServerState(server);
		keepXPoverride = serverState.keepXPoverride;

		// Check if server has Permission API
		permissionsAPI = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");

		if (keepXPoverride) {
			LOGGER.info("KeepXP is turned on for all players (override).");
		} else {
			if (permissionsAPI) {
				LOGGER.info("Permissions API found. KeepXP is turned on for players based on permissions.");
			} else {
				LOGGER.info("Permissions API not found. KeepXP is turned on for all players.");
			}
		}
	}

	public static MinecraftServer getServer() {
		return server;
	}
}
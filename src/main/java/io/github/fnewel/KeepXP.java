package io.github.fnewel;

import io.github.fnewel.command.KeepXPCommand;
import io.github.fnewel.data.KeepXPSavedData;
import io.github.fnewel.permission.KeepXPPermissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepXP implements ModInitializer {
    public static final String MOD_ID = "keep-xp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> KeepXPCommand.register(dispatcher));

        ServerLifecycleEvents.SERVER_STARTED.register(KeepXP::onServerStarted);
    }

    private static void onServerStarted(MinecraftServer server) {
        // All mods have registered their permission listeners by now
        KeepXPPermissions.detect();

        if (KeepXPPermissions.isActive()) {
            LOGGER.info("KeepXP is controlled by permissions ({}, {}).",
                    KeepXPPermissions.nodeName(KeepXPPermissions.KEEP),
                    KeepXPPermissions.nodeName(KeepXPPermissions.PENALTY, "<0-100>"));
        } else {
            KeepXPSavedData data = KeepXPSavedData.get(server);
            LOGGER.info("No permission mod found. KeepXP is turned {} for all players, penalty {}%.",
                    data.isEnabled() ? "on" : "off", data.getPenalty());
        }
    }
}

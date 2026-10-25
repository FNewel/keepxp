package io.github.fnewel.permission;

import io.github.fnewel.KeepXP;
import io.github.fnewel.data.KeepXPSavedData;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.permission.v1.PermissionContextOwner;
import net.fabricmc.fabric.api.permission.v1.PermissionEvents;
import net.fabricmc.fabric.api.permission.v1.PermissionNode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * Universal permission support.
 *
 * <p>A "permission mod" is any mod that answers permission checks through one of the two
 * standard Fabric permission APIs:
 * <ul>
 *   <li>Fabric Permission API v1 (part of Fabric API since 26.x) - {@link PermissionEvents#ON_REQUEST}</li>
 *   <li>lucko's fabric-permissions-api v0 (older, still widely used) - PermissionCheckEvent</li>
 * </ul>
 * LuckPerms implements both. Detection counts registered listeners on these events, so a mod
 * that only bundles the API library (without providing permissions) is NOT treated as a permission mod.
 *
 * <p>Node names are the same in both APIs and in LuckPerms:
 * "keepxp.keep", "keepxp.penalty.<0-100>", "keepxp.command".
 */
public final class KeepXPPermissions {
    public static final String NAMESPACE = "keepxp";
    public static final String KEEP = "keep";
    public static final String PENALTY = "penalty";
    public static final String COMMAND = "command";

    private static final String LUCKO_V0_MOD_ID = "fabric-permissions-api-v0";

    private static final boolean LUCKO_V0_LOADED = FabricLoader.getInstance().isModLoaded(LUCKO_V0_MOD_ID);
    private static boolean active = false;

    private KeepXPPermissions() {
    }

    /** Full node name as shown to admins, e.g. "keepxp.penalty.40". */
    public static String nodeName(String... parts) {
        return NAMESPACE + "." + String.join(".", parts);
    }

    /** True if a permission mod is installed (detected at server start). */
    public static boolean isActive() {
        return active;
    }

    // ---------------------------------------------------------------- detection

    /** Called on server start, after all mods registered their permission listeners. */
    public static void detect() {
        int v1 = listenerCount(PermissionEvents.ON_REQUEST);
        int v0 = LUCKO_V0_LOADED ? listenerCount(LuckoV0Bridge.event()) : 0;

        if (v1 < 0 || v0 < 0) {
            // Could not inspect Fabric internals - fall back to "API library is installed"
            active = LUCKO_V0_LOADED;
            KeepXP.LOGGER.warn("Could not count permission listeners, assuming permission mod is {}.",
                    active ? "installed" : "not installed");
            return;
        }

        active = v1 > 0 || v0 > 0;
        if (active) {
            KeepXP.LOGGER.info("Permission mod detected (Fabric Permission API listeners: {}, fabric-permissions-api v0 listeners: {}).", v1, v0);
        }
    }

    /** Number of listeners on a Fabric event, or -1 if it can't be determined. */
    private static int listenerCount(Event<?> event) {
        try {
            for (Class<?> c = event.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    Field field = c.getDeclaredField("handlers");
                    field.setAccessible(true);
                    Object handlers = field.get(event);
                    return handlers == null ? 0 : Array.getLength(handlers);
                } catch (NoSuchFieldException ignored) {
                    // try superclass
                }
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            KeepXP.LOGGER.debug("Failed to inspect event {}", event, e);
        }
        return -1;
    }

    // ---------------------------------------------------------------- checks

    /** Player keeps XP only when explicitly granted keepxp.keep. */
    public static boolean canKeepXp(ServerPlayer player) {
        Boolean value = query(player, KEEP);
        return value != null && value;
    }

    /**
     * Penalty from nodes keepxp.penalty.0 ... keepxp.penalty.100.
     * The lowest granted value wins; no node granted means 0 %.
     */
    public static int getPenalty(ServerPlayer player) {
        for (int i = KeepXPSavedData.MIN_PENALTY; i <= KeepXPSavedData.MAX_PENALTY; i++) {
            Boolean value = query(player, PENALTY, Integer.toString(i));
            if (value != null && value) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Admin commands: keepxp.command if a permission mod is installed and has a value for it,
     * otherwise op level 2.
     */
    public static boolean canUseAdminCommands(CommandSourceStack source) {
        if (active) {
            Boolean value = querySource(source, COMMAND);
            if (value != null) {
                return value;
            }
        }
        return Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    }

    // ---------------------------------------------------------------- internals

    /** Fabric v1 first, then lucko v0. Null = no permission mod has a value for this node. */
    private static @Nullable Boolean query(ServerPlayer player, String... parts) {
        Boolean value = player.checkPermission(node(parts));
        if (value == null && LUCKO_V0_LOADED) {
            value = LuckoV0Bridge.check(player, nodeName(parts));
        }
        return value;
    }

    private static @Nullable Boolean querySource(CommandSourceStack source, String... parts) {
        Boolean value = source.checkPermission(node(parts));
        if (value == null && LUCKO_V0_LOADED) {
            value = LuckoV0Bridge.check(source, nodeName(parts));
        }
        return value;
    }

    /** keepxp:penalty/40 -> resolved by LuckPerms as "keepxp.penalty.40". */
    private static PermissionNode<Boolean> node(String... parts) {
        return PermissionNode.of(Identifier.fromNamespaceAndPath(NAMESPACE, String.join("/", parts)));
    }
}

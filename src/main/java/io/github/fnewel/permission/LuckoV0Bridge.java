package io.github.fnewel.permission;

import me.lucko.fabric.api.permissions.v0.PermissionCheckEvent;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

/**
 * The only class that touches lucko's fabric-permissions-api (v0).
 * Loaded only when that API is installed, so the mod works without it.
 */
final class LuckoV0Bridge {
    private LuckoV0Bridge() {
    }

    static Event<?> event() {
        return PermissionCheckEvent.EVENT;
    }

    static @Nullable Boolean check(Entity entity, String node) {
        return toBoolean(Permissions.getPermissionValue(entity, node));
    }

    static @Nullable Boolean check(CommandSourceStack source, String node) {
        return toBoolean(Permissions.getPermissionValue(source, node));
    }

    private static @Nullable Boolean toBoolean(TriState state) {
        return state == TriState.DEFAULT ? null : state.get();
    }
}

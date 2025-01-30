package io.github.fnewell.mixin;

import io.github.fnewell.KeepXP;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Method;


@Mixin(ServerPlayerEntity.class)
public abstract class XpOnRespawn {

    // TODO: remove?
    @Shadow public abstract void onLanding();

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void noXpToDrop(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

        try {
            // Check if KeepXP override is turned on
            if (KeepXP.keepXPoverride) {
                throw new Exception();
            }

            // Try to check if Permissions API is found
            Class<?> permissionsClass = Class.forName("me.lucko.fabric.api.permissions.v0.Permissions");
            Method checkMethod = permissionsClass.getMethod("check", Entity.class, String.class);
            boolean hasPermission = (boolean)checkMethod.invoke(null, oldPlayer, "keep.xp");

            // If Permissions API is found and the player has the permission to keep xp, return the player's xp to its previous state
            if (hasPermission) {
                player.experienceLevel = oldPlayer.experienceLevel;
                player.totalExperience = oldPlayer.totalExperience;
                player.experienceProgress = oldPlayer.experienceProgress;
                player.setScore(oldPlayer.getScore());
            }

        } catch (Exception e) {
            player.experienceLevel = oldPlayer.experienceLevel;
            player.totalExperience = oldPlayer.totalExperience;
            player.experienceProgress = oldPlayer.experienceProgress;
            player.setScore(oldPlayer.getScore());
        }
    }
}
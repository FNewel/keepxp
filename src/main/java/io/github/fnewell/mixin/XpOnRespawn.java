package io.github.fnewell.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;

import me.lucko.fabric.api.permissions.v0.Permissions;

@Mixin(ServerPlayerEntity.class)
public abstract class XpOnRespawn {

    @Shadow public abstract void onLanding();

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void noXpToDrop(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

        // If the player has the permission to keep xp, restore their xp
        if (Permissions.check(oldPlayer, "keep.xp")) {
            player.experienceLevel = oldPlayer.experienceLevel;
            player.totalExperience = oldPlayer.totalExperience;
            player.experienceProgress = oldPlayer.experienceProgress;
            player.setScore(oldPlayer.getScore());
        }
    }
}
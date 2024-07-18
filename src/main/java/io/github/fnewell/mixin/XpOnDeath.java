package io.github.fnewell.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;

import me.lucko.fabric.api.permissions.v0.Permissions;

@Mixin(PlayerEntity.class)
public class XpOnDeath {

    @Inject(method = "getXpToDrop", at = @At("RETURN"), cancellable = true)
    private void noXpToDrop(CallbackInfoReturnable<Integer> info) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        // If the player has the permission to keep xp, set the xp to drop to 0
        if (Permissions.check(player, "keep.xp")) {
            info.setReturnValue(0);
        }
    }
}

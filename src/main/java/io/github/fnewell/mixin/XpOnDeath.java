package io.github.fnewell.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.lang.reflect.Method;


@Mixin(PlayerEntity.class)
public class XpOnDeath {

    //? if >=1.21.4 {
    @Inject(method = "getExperienceToDrop", at = @At("RETURN"), cancellable = true)
    private void noXpToDrop1214(CallbackInfoReturnable<Integer> info) {
        handleXpDrop(info);
    }
    //?} else
    /*@Inject(method = "getXpToDrop", at = @At("RETURN"), cancellable = true)*/
    /*private void noXpToDrop1213(CallbackInfoReturnable<Integer> info) { handleXpDrop(info); };*/


    private void handleXpDrop(CallbackInfoReturnable<Integer> info) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        // Try to check if Permissions API is found
        try {
            Class<?> permissionsClass = Class.forName("me.lucko.fabric.api.permissions.v0.Permissions");
            Method checkMethod = permissionsClass.getMethod("check", Entity.class, String.class);
            boolean hasPermission = (boolean)checkMethod.invoke(null, player, "keep.xp");

            // If Permissions API is found and the player has the permission to keep xp, set the xp to drop to 0
            if (hasPermission) {
                info.setReturnValue(0);
            }

        } catch (Exception e) {
            info.setReturnValue(0);
        }
    }
}

package io.github.fnewell.mixin;

import io.github.fnewell.KeepXP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.lang.reflect.Method;


@Mixin(PlayerEntity.class)
public class XpOnDeath {

    // >=1.21.4
    // int getExperienceToDrop( ServerWorld world, Entity attacker)
    // int getExperienceToDrop( ServerWorld world)

    // <1.21.4
    // int getXpToDrop( ServerWorld world, Entity attacker)
    // int getXpToDrop( ServerWorld world)

    // 1.21-1.21.1
    // int getXpToDrop( ServerWorld world, Entity attacker)
    // int getXpToDrop( )

    // <=1.20.6
    // missing
    // int getXpToDrop( )

    // >=1.16.5 <=1.18.2
    // missing
    // int getXpToDrop( PlayerEntity player)

    //? if >=1.21.4 {
    /*@Inject(method = "getExperienceToDrop", at = @At("RETURN"), cancellable = true)
    private void noXpToDrop1214(CallbackInfoReturnable<Integer> info) {
        handleXpDrop(info);
    }
    *///?} else
    @Inject(method = "getXpToDrop", at = @At("RETURN"), cancellable = true)
    /*private void noXpToDrop1213(CallbackInfoReturnable<Integer> info) { handleXpDrop(info); };*/


    private void handleXpDrop(CallbackInfoReturnable<Integer> info) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        try {
            // Check if KeepXP override is turned on
            if (KeepXP.keepXPoverride) {
                throw new Exception();
            }

            // Try to check if Permissions API is found
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

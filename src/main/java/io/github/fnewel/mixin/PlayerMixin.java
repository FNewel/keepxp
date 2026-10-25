package io.github.fnewel.mixin;

import io.github.fnewel.XpRules;
import io.github.fnewel.access.DeathXpState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    /**
     * XP orbs dropped on death for players who keep XP:
     * penalty 0 -> nothing dropped (everything is kept),
     * penalty above 0 -> only the removed levels are dropped, using the vanilla formula
     * (7 points per removed level, max 100).
     */
    @Inject(method = "getBaseExperienceReward", at = @At("RETURN"), cancellable = true)
    private void keepxp$xpDrop(ServerLevel level, CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof DeathXpState state && state.keepxp$keepsXp()) {
            Player player = (Player) (Object) this;
            cir.setReturnValue(XpRules.penaltyDropPoints(player.experienceLevel, state.keepxp$penalty()));
        }
    }
}

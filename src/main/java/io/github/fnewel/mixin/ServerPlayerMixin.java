package io.github.fnewel.mixin;

import io.github.fnewel.XpRules;
import io.github.fnewel.access.DeathXpState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements DeathXpState {

    @Unique
    private boolean keepxp$keepsXp = false;

    @Unique
    private int keepxp$penalty = 0;

    /** Decide once at death; used by the XP drop (PlayerMixin) and by restoreFrom below. */
    @Inject(method = "die", at = @At("HEAD"))
    private void keepxp$decideOnDeath(DamageSource source, CallbackInfo ci) {
        XpRules.Decision decision = XpRules.decide((ServerPlayer) (Object) this);
        this.keepxp$keepsXp = decision.keep();
        this.keepxp$penalty = decision.penalty();
    }

    /**
     * After respawn copy. Vanilla only copies XP with keepInventory (then we do nothing),
     * so here we copy it ourselves for players who keep XP.
     */
    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void keepxp$restoreXp(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        if (restoreAll) {
            return; // End portal return: vanilla already copied everything
        }
        DeathXpState state = (DeathXpState) oldPlayer;
        if (state.keepxp$keepsXp()) {
            XpRules.restore((ServerPlayer) (Object) this, oldPlayer, state.keepxp$penalty());
        }
    }

    @Override
    public boolean keepxp$keepsXp() {
        return this.keepxp$keepsXp;
    }

    @Override
    public int keepxp$penalty() {
        return this.keepxp$penalty;
    }
}

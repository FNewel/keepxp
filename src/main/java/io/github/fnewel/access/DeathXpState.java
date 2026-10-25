package io.github.fnewel.access;

/**
 * Implemented on ServerPlayer by a mixin. Stores the KeepXP decision made at death,
 * so the XP drop and the respawn use the same result even if permissions change in between.
 */
public interface DeathXpState {
    boolean keepxp$keepsXp();

    int keepxp$penalty();
}

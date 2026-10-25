package io.github.fnewel;

import io.github.fnewel.data.KeepXPSavedData;
import io.github.fnewel.permission.KeepXPPermissions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRules;

/**
 * Decides whether a player keeps XP and with what penalty, and applies it on respawn.
 */
public final class XpRules {
    private XpRules() {
    }

    /** Result of the decision made at the moment of death. */
    public record Decision(boolean keep, int penalty) {
        public static final Decision VANILLA = new Decision(false, 0);
    }

    /**
     * Called at the start of death. Returns VANILLA when the mod should not interfere
     * (keepInventory is on, spectator, or KeepXP disabled / not permitted).
     */
    public static Decision decide(ServerPlayer player) {
        if (player.isSpectator() || player.level().getGameRules().get(GameRules.KEEP_INVENTORY)) {
            return Decision.VANILLA;
        }

        if (KeepXPPermissions.isActive()) {
            if (!KeepXPPermissions.canKeepXp(player)) {
                return Decision.VANILLA;
            }
            return new Decision(true, KeepXPPermissions.getPenalty(player));
        }

        KeepXPSavedData data = KeepXPSavedData.get(player.level().getServer());
        if (!data.isEnabled()) {
            return Decision.VANILLA;
        }
        return new Decision(true, data.getPenalty());
    }

    /**
     * Number of levels left after the penalty. Removed levels are rounded down,
     * e.g. 7 levels, 40 % -> removes floor(2.8) = 2, keeps 5.
     */
    public static int levelsAfterPenalty(int level, int penaltyPercent) {
        if (level <= 0 || penaltyPercent <= 0) {
            return Math.max(level, 0);
        }
        int removed = (int) ((long) level * penaltyPercent / 100);
        return level - removed;
    }

    /**
     * XP points dropped as orbs for the levels removed by the penalty, using the vanilla
     * death formula on those levels: 7 points per level, max 100.
     * E.g. level 20, 30 % -> 6 levels removed -> min(7 * 6, 100) = 42 points.
     */
    public static int penaltyDropPoints(int level, int penaltyPercent) {
        int removed = Math.max(level, 0) - levelsAfterPenalty(level, penaltyPercent);
        return Math.min(removed * 7, 100);
    }

    /**
     * Copies XP from the dead player to the respawned one.
     * Penalty 0 = penalty off: levels, progress bar and points stay untouched.
     * Penalty above 0: levels minus the penalty, progress bar reset to 0.
     */
    public static void restore(ServerPlayer newPlayer, Player oldPlayer, int penaltyPercent) {
        newPlayer.setScore(oldPlayer.getScore());

        if (penaltyPercent <= 0) {
            newPlayer.experienceLevel = oldPlayer.experienceLevel;
            newPlayer.experienceProgress = oldPlayer.experienceProgress;
            newPlayer.totalExperience = oldPlayer.totalExperience;
            return;
        }

        int newLevel = levelsAfterPenalty(oldPlayer.experienceLevel, penaltyPercent);
        newPlayer.experienceLevel = newLevel;
        newPlayer.experienceProgress = 0.0F;
        newPlayer.totalExperience = (int) Math.min(totalXpForLevel(newLevel), Integer.MAX_VALUE);
    }

    /** Total XP points needed to reach the start of the given level (vanilla formula). */
    static long totalXpForLevel(int level) {
        long l = level;
        if (level <= 16) {
            return l * l + 6 * l;
        } else if (level <= 31) {
            return (5 * l * l - 81 * l + 720) / 2;
        } else {
            return (9 * l * l - 325 * l + 4440) / 2;
        }
    }
}

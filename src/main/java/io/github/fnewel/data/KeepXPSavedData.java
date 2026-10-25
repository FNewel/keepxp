package io.github.fnewel.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.fnewel.KeepXP;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * Global KeepXP settings stored in the world save (data/keep-xp/settings.dat).
 * Used only when no permission mod is installed.
 */
public class KeepXPSavedData extends SavedData {
    public static final int MIN_PENALTY = 0;
    public static final int MAX_PENALTY = 100;

    private static final Codec<KeepXPSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(KeepXPSavedData::isEnabled),
            Codec.INT.optionalFieldOf("penalty", 0).forGetter(KeepXPSavedData::getPenalty)
    ).apply(instance, KeepXPSavedData::new));

    public static final SavedDataType<KeepXPSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(KeepXP.MOD_ID, "settings"),
            KeepXPSavedData::new,
            CODEC,
            // Our data has no vanilla fixes, a simple type is required because the field is non-null
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private boolean enabled;
    private int penalty;

    /** Defaults: KeepXP on, no penalty. */
    public KeepXPSavedData() {
        this(true, 0);
    }

    private KeepXPSavedData(boolean enabled, int penalty) {
        this.enabled = enabled;
        this.penalty = Mth.clamp(penalty, MIN_PENALTY, MAX_PENALTY);
    }

    public static KeepXPSavedData get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setDirty();
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = Mth.clamp(penalty, MIN_PENALTY, MAX_PENALTY);
        setDirty();
    }
}

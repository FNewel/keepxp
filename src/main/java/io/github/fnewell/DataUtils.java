package io.github.fnewell;

import net.minecraft.nbt.NbtCompound;
//? if >= 1.20.5
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.Objects;

/*
* Class to store mod data in the world save
* with the PersistentStateManager
* Source: https://wiki.fabricmc.net/tutorial:persistent_states
* */
public class DataUtils extends PersistentState {
    public Boolean keepXPoverride = false;  // Variable to store the KeepXP override state (default: false)

    /*
    * Constructor
    * */
    @Override
    //? if >= 1.20.5 {
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putBoolean("keepXP_override", keepXPoverride);
        return nbt;
    }
    //?} else {
    /*public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putBoolean("keepXP_override", keepXPoverride);
        return nbt;
    }
    *///?}

    /*
    * Create a new instance of the class from NBT data
    * */
    //? if >= 1.20.5 {
    public static DataUtils createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        DataUtils state = new DataUtils();
        state.keepXPoverride = tag.getBoolean("keepXP_override");
        return state;
    }
     //?} else {
    /*public static DataUtils createFromNbt(NbtCompound tag) {
        DataUtils state = new DataUtils();
        state.keepXPoverride = tag.getBoolean("keepXP_override");
        return state;
    }
    *///?}

    /*
    * Get the server state from the PersistentStateManager
    * */
    //? if >= 1.20.2 {
    private static final Type<DataUtils> type = new Type<>(
            DataUtils::new,             // If there's no 'StateSaverAndLoader' yet create one
            DataUtils::createFromNbt,   // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null                        // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );
    //?}

    /*
    * Get the server state from the PersistentStateManager
    *
    * @param server The Minecraft server instance
    * @return The server state
    * */
    public static DataUtils getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        //? if >= 1.20.2 {
        DataUtils state = persistentStateManager.getOrCreate(type, KeepXP.MODID);
        //?} else {
        /*DataUtils state = persistentStateManager.getOrCreate(
                DataUtils::createFromNbt,
                DataUtils::new,
                KeepXP.MODID
        );
        *///?}

        state.markDirty();

        return state;
    }
}

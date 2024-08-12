package com.github.mittenmc.serverutils.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Defines an object which contains a {@link Player} instance.
 * @author GavvyDizzle
 * @version 1.1.2
 * @since 1.1.1
 */
@SuppressWarnings("unused")
public abstract class PlayerContainer {

    @Nullable private Player player;
    @NotNull private final OfflinePlayer offlinePlayer;

    public PlayerContainer(@NotNull Player player) {
        this.player = player;
        this.offlinePlayer = player;
    }

    /**
     * If the offline player is online then this will properly load the player object too.
     * @param offlinePlayer The player
     */
    public PlayerContainer(@NotNull OfflinePlayer offlinePlayer) {
        this.player = offlinePlayer.isOnline() ? offlinePlayer.getPlayer() : null;
        this.offlinePlayer = offlinePlayer;
    }

    /**
     * Loads the player object.
     * This is intended to be called on join.
     */
    public void loadPlayer() {
        player = offlinePlayer.getPlayer();
    }

    /**
     * Unloads the player object.
     * This is intended to be called on quit.
     */
    public void unloadPlayer() {
        player = null;
    }

    /**
     * Obtain the player instance from this object.
     * Implementations may return null when the player is offline.
     * @return The player or null
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    @NotNull
    public UUID getUniqueId() {
        return offlinePlayer.getUniqueId();
    }

}

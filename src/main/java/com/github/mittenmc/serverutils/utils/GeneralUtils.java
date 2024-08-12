package com.github.mittenmc.serverutils.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A collection of generic static methods
 * @author GavvyDizzle
 * @version 1.1.2
 * @since 1.1.2
 */
@SuppressWarnings("unused")
public class GeneralUtils {

    /**
     * Gets the offline player object by the player's name.
     * @param name Their name
     * @return The offline player or null if they have not played before
     * @since 1.1.2
     */
    @Nullable
    public static OfflinePlayer getOfflinePlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) return player;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
            // Since the #getOfflinePlayer() method never returns null, we need to do the above sanity check!
            // If the player is online during their first login, #hasPlayedBefore() will return false
            return null;
        }
        return offlinePlayer;
    }

}

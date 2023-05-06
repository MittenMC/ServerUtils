package com.github.mittenmc.serverutils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * Contains useful methods for getting player names
 * After being retrieved once, player's name is cached by their UUID.
 * @author GavvyDizzle
 * @version 1.0.2
 * @since 1.0.2
 */
public class PlayerNameCache implements Listener {

    private static final HashMap<UUID, String> nameCache;

    static {
        nameCache = new HashMap<>();
    }

    // Defined as protected so other classes cannot create a new instance
    protected PlayerNameCache() {}

    /**
     * @param player The player
     * @return The player's name or "null" if they have never played before
     * @since 1.0.2
     */
    @NotNull
    public static String get(OfflinePlayer player) {
        if (nameCache.containsKey(player.getUniqueId())) {
            return nameCache.get(player.getUniqueId());
        }

        String name = player.getName() != null ? player.getName() : "null";
        nameCache.put(player.getUniqueId(), name);
        return name;
    }

    /**
     * @param uuid The player's uuid
     * @return The player's name or "null" if they have never played before
     * @since 1.0.2
     */
    @NotNull
    public static String get(UUID uuid) {
        if (nameCache.containsKey(uuid)) {
            return nameCache.get(uuid);
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        String name = player.getName() != null ? player.getName() : "null";
        nameCache.put(player.getUniqueId(), name);
        return name;
    }

    // Add players to the cache on join to overwrite their original entry if their name changed
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        nameCache.put(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

}

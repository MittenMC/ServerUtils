package com.github.mittenmc.serverutils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Contains useful methods for getting player names
 * After being retrieved once, player's name is cached by their UUID.
 * @author GavvyDizzle
 * @version 1.1.3
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
     * @return The player's name
     * @since 1.1.3
     */
    @NotNull
    public static String get(Player player) {
        return player.getName();
    }

    /**
     * @param player The player
     * @return The player's name or "null" if they have never played before
     * @since 1.0.2
     */
    @NotNull
    public static String get(OfflinePlayer player) {
        Player p = player.getPlayer();
        if (p != null) return p.getName();

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
        Player p = player.getPlayer();
        if (p != null) return p.getName();

        String name = player.getName() != null ? player.getName() : "null";
        nameCache.put(player.getUniqueId(), name);
        return name;
    }

    /**
     * @param uuids The list of player UUIDs
     * @return A list of player names with the order preserved
     * @since 1.0.3
     */
    @NotNull
    public static Collection<String> get(Collection<UUID> uuids) {
        ArrayList<String> arr = new ArrayList<>(uuids.size());

        for (UUID uuid : uuids) {
            arr.add(get(uuid));
        }
        return arr;
    }

    // Add players to the cache on join to overwrite their original entry if their name changed
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        nameCache.put(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }

}

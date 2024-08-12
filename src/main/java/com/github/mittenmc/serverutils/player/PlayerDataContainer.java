package com.github.mittenmc.serverutils.player;

import com.github.mittenmc.serverutils.player.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages all player data belonging to a plugin.
 * @param <E> The player data type
 * @author GavvyDizzle
 * @version 1.1.5
 * @since 1.1.2
 */
@SuppressWarnings("unused")
public abstract class PlayerDataContainer<E extends PlayerProfile> implements Listener {

    private final JavaPlugin instance;
    private final Map<UUID, E> players;
    private final Map<UUID, E> offlinePlayers;

    public PlayerDataContainer(JavaPlugin instance) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);
        players = new HashMap<>();
        offlinePlayers = new HashMap<>();
    }

    /**
     * A request for a player's data to be loaded who just joined the server.
     * This method should always be called asynchronously!
     * @param player The player who just joined
     * @return The player data or null
     */
    @Nullable
    public abstract E loadPlayerData(Player player);

    /**
     * A request for a player's data to be loaded who is offline.
     * This method should always be called asynchronously!
     * @param offlinePlayer The offline player
     * @return The player data or null
     */
    @Nullable
    public abstract E loadOfflinePlayerData(OfflinePlayer offlinePlayer);

    /**
     * A request for a player's data to be saved.
     * This will be called internally when the player's data is fully released.
     * @param data The player data
     */
    public abstract void savePlayerData(E data);

    /**
     * Saves the player data of all loaded profiles.
     * This will be called internally when the player's data is fully released.
     */
    public abstract void saveAllPlayerData();

    /**
     * Some players may be online when the plugin starts.
     * This will initiate a {@link #loadPlayerData(Player)} for each online player.
     */
    public void initializeOnlinePlayers() {
        for (Player player : instance.getServer().getOnlinePlayers()) {
            loadPlayerData(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        E playerData = offlinePlayers.remove(e.getPlayer().getUniqueId());

        if (playerData != null) {
            playerData.loadPlayer();
            players.put(e.getPlayer().getUniqueId(), playerData);
        } else {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(instance, () -> {
                E newPlayerData = loadPlayerData(e.getPlayer());
                if (newPlayerData == null) {
                    instance.getLogger().log(Level.SEVERE, "Failed to load player data. See issues above (hopefully there are some...)");
                    return;
                }

                // Add to the map synchronously to avoid any potential issues
                Bukkit.getServer().getScheduler().runTask(instance, () -> players.put(e.getPlayer().getUniqueId(), newPlayerData));
            });
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        E playerData = players.remove(e.getPlayer().getUniqueId());
        if (playerData == null) return;

        playerData.unloadPlayer();

        // If the player's data is still in use, save it to the offline data cache
        if (!playerData.getProfileViewers().isEmpty()) {
            offlinePlayers.put(e.getPlayer().getUniqueId(), playerData);
        } else {
            savePlayerData(playerData);
        }
    }

    /**
     * Creates a task that will attempt to unload this profile in 1 tick.
     * The profile will unload and save if nobody is looking at the offline profile
     * @param data The player's data
     */
    public void schedulePlayerUnloadAttempt(E data) {
        if (!offlinePlayers.containsKey(data.getUniqueId())) return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            if (data.getProfileViewers().isEmpty()) {
                savePlayerData(data);
                offlinePlayers.remove(data.getUniqueId());
            }
        }, 1);
    }

    /**
     * Releases any offline player profiles which are no longer in use.
     * Any matches will call {@link #savePlayerData(PlayerProfile)}.
     */
    public void purgeUnusedAccounts() {
        Iterator<Map.Entry<UUID, E>> iterator = offlinePlayers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, E> entry = iterator.next();
            if (entry.getValue().getProfileViewers().isEmpty()) {
                savePlayerData(entry.getValue());
                iterator.remove();
            }
        }
    }

    /**
     * @param uuid The player's UUID
     * @return If the player has loaded data
     */
    public boolean hasLoadedData(UUID uuid) {
        return players.containsKey(uuid) || offlinePlayers.containsKey(uuid);
    }

    /**
     * Retrieve an online player's data
     * @param player The player
     * @return The player's data
     */
    @Nullable
    public E getPlayerData(Player player) {
        return players.get(player.getUniqueId());
    }

    /**
     * Retrieve the loaded data of an online or offline player whose data is loaded
     * @param uuid The player's UUID
     * @return The player's data or null
     */
    @Nullable
    public E getLoadedPlayerData(UUID uuid) {
        return players.getOrDefault(uuid, offlinePlayers.get(uuid));
    }

    /**
     * Retrieve an offline player's data.
     * Since this may need to be loaded from the database, a CompletableFuture is used.
     * @param offlinePlayer The offline player
     * @return The player's data
     */
    public CompletableFuture<E> getPlayerData(OfflinePlayer offlinePlayer) {
        E data = offlinePlayers.get(offlinePlayer.getUniqueId());
        if (data != null) return CompletableFuture.completedFuture(data);

        CompletableFuture<E> future = CompletableFuture.supplyAsync(() -> {
            E newData = loadOfflinePlayerData(offlinePlayer);

            // Add to the map synchronously to avoid any potential issues
            Bukkit.getServer().getScheduler().runTask(instance, () -> offlinePlayers.put(offlinePlayer.getUniqueId(), newData));

            return newData;
        });

        future.exceptionally(error -> {
            instance.getLogger().log(Level.SEVERE, "Failed to load offline player data", error.getStackTrace());
            return null;
        });

        return future;
    }

    /**
     * Gets all loaded data of online and offline players.
     * @return The data of all loaded profiles
     */
    public Collection<E> getAllPlayerData() {
        return Stream.concat(players.values().stream(), offlinePlayers.values().stream())
                .collect(Collectors.toList());
    }
}

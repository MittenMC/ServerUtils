package com.github.mittenmc.serverutils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Contains useful methods for getting player heads.
 * After being retrieved once, a head is cached.
 * @author GavvyDizzle
 * @version 1.0.2
 * @since 1.0
 */
public class PlayerHeads {

    private static final LoadingCache<UUID, ItemStack> skullCache;

    static {
        skullCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(
                        new CacheLoader<UUID, ItemStack>() {
                            @Override
                            public ItemStack load(@Nonnull UUID uuid) {
                                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                                SkullMeta sm = (SkullMeta) skull.getItemMeta();
                                assert sm != null;
                                if (!sm.setOwningPlayer(Bukkit.getOfflinePlayer(uuid))) {
                                    throw new RuntimeException("Failed to set skull owner");
                                }

                                skull.setItemMeta(sm);
                                return skull;
                            }
                        });
    }

    /**
     * Determines if this player's head is currently cached.
     *
     * @param uuid The player's uuid.
     * @return True if the player is cached.
     * @since 1.0
     */
    public static boolean isCached(UUID uuid) {
        return skullCache.asMap().containsKey(uuid);
    }

    /**
     * Gets the cached head of a player.
     * Plugins should verify that {@link PlayerHeads#isCached(UUID)} returns true before calling this method.
     * @deprecated
     * Use {@link #getHead(UUID)}
     *
     * @param uuid The player's uuid.
     * @return The player's skull, AIR if one is not cached.
     * @since 1.0
     */
    @Deprecated
    public static ItemStack getCachedHead(@NotNull UUID uuid) {
        return getHead(uuid);
    }

    /**
     * Gets the cached head of a player.
     * Plugins should verify that {@link PlayerHeads#isCached(UUID)} returns true before calling this method.
     * @deprecated
     * Use {@link #getHead(UUID, String)}
     *
     * @param uuid The player's uuid
     * @param displayName The name of this item
     * @return The player's skull
     * @since 1.0
     */
    @Deprecated
    public static ItemStack getCachedHead(@NotNull UUID uuid, @NotNull String displayName) {
        return getHead(uuid, displayName);
    }

    /**
     * Gets the cached head of a player.
     * Plugins should verify that {@link PlayerHeads#isCached(UUID)} returns true before calling this method.
     * @deprecated
     * Use {@link #getHead(UUID, String, ArrayList)}
     *
     * @param uuid The player's uuid
     * @param displayName The name of this item
     * @param lore The lore to add to this item
     * @return The player's skull
     * @since 1.0
     */
    @Deprecated
    public static ItemStack getCachedHead(@NotNull UUID uuid, @NotNull String displayName, @NotNull ArrayList<String> lore) {
        return getHead(uuid, displayName, lore);
    }

    /**
     * Gets a player skull from UUID.
     * Plugins should verify the cache state through {@link PlayerHeads#isCached(UUID)}.
     * If the UUID is cached, it is safe to call this method synchronously, otherwise it should be called asynchronously.
     *
     * @param uuid The player's uuid
     * @return The default player skull
     * @since 1.0
     */
    public static ItemStack getHead(@NotNull UUID uuid) {
        try {
            return skullCache.get(uuid).clone();
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
    }

    /**
     * Gets a player skull from UUID.
     * Plugins should verify the cache state through {@link PlayerHeads#isCached(UUID)}.
     * If the UUID is cached, it is safe to call this method synchronously, otherwise it should be called asynchronously.
     *
     * @param uuid The player's uuid
     * @param displayName The name of this item
     * @return The default player skull for this user
     * @since 1.0
     */
    public static ItemStack getHead(@NotNull UUID uuid, @NotNull String displayName) {
        ItemStack skull = getHead(uuid);
        if (skull.getType() == Material.AIR) {
            return skull;
        }

        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        assert sm != null;
        sm.setDisplayName(Colors.conv(displayName));
        skull.setItemMeta(sm);

        return skull;
    }

    /**
     * Gets a player skull from UUID.
     * Plugins should verify the cache state through {@link PlayerHeads#isCached(UUID)}.
     * If the UUID is cached, it is safe to call this method synchronously, otherwise it should be called asynchronously.
     *
     * @param uuid The player's uuid
     * @param displayName The name of this item
     * @param lore The lore to add to this item
     * @return The default player skull for this user
     * @since 1.0
     */
    public static ItemStack getHead(@NotNull UUID uuid, @NotNull String displayName, @NotNull ArrayList<String> lore) {
        ItemStack skull = getHead(uuid);
        if (skull.getType() == Material.AIR) {
            return skull;
        }

        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        assert sm != null;
        sm.setDisplayName(Colors.conv(displayName));
        sm.setLore(Colors.conv(lore));
        skull.setItemMeta(sm);

        return skull;
    }

    /**
     * Gets a player skull from the player's UUID.
     * Plugins should verify the cache state through {@link PlayerHeads#isCached(UUID)}.
     * If the UUID is cached, it is safe to call this method synchronously, otherwise it should be called asynchronously.
     *
     * @param player The OfflinePlayer
     * @return The default player skull for this user
     * @since 1.0
     */
    public static ItemStack getHead(@NotNull OfflinePlayer player) {
        return getHead(player.getUniqueId());
    }

    /**
     * Gets a player skull from the player's UUID.
     * Plugins should verify the cache state through {@link PlayerHeads#isCached(UUID)}.
     * If the UUID is cached, it is safe to call this method synchronously, otherwise it should be called asynchronously.
     *
     * @param player The OfflinePlayer
     * @param displayName The name of this item
     * @return The default player skull for this user
     * @since 1.0
     */
    public static ItemStack getHead(@NotNull OfflinePlayer player, @NotNull String displayName) {
        return getHead(player.getUniqueId(), displayName);
    }

    /**
     * Gets a player skull from the player's UUID.
     * Plugins should verify the cache state through {@link PlayerHeads#isCached(UUID)}.
     * If the UUID is cached, it is safe to call this method synchronously, otherwise it should be called asynchronously.
     *
     * @param player The OfflinePlayer
     * @param displayName The name of this item
     * @param lore The lore to add to this item
     * @return The default player skull for this user
     * @since 1.0
     */
    public static ItemStack getHead(@NotNull OfflinePlayer player, @NotNull String displayName, @NotNull ArrayList<String> lore) {
        return getHead(player.getUniqueId(), displayName, lore);
    }

}

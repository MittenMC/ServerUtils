package com.github.mittenmc.serverutils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerSkull {

    private static final HashMap<UUID, ItemStack> skullCache;

    static {
        skullCache = new HashMap<>();
    }

    /**
     * Gets a player skull from UUID.
     * This method should be run inside an asynchronous task.
     * If the uuid is null or if this player has never played before then an AIR ItemStack will be returned!
     *
     * @param uuid The player's uuid
     * @return The default player skull for this user
     */
    public static ItemStack getHead(UUID uuid) {
        if (uuid == null) {
            return new ItemStack(Material.AIR);
        }

        if (skullCache.containsKey(uuid)) {
            return skullCache.get(uuid);
        }

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));

        if (!sm.hasOwner()) {
            return new ItemStack(Material.AIR);
        }

        skull.setItemMeta(sm);
        skullCache.put(uuid, skull);

        return skull;
    }

    /**
     * Gets a player skull from UUID.
     * This method should be run inside an asynchronous task.
     * If the uuid is null or if this player has never played before then an AIR ItemStack will be returned!
     *
     * @param uuid The player's uuid
     * @param displayName The name of this item
     * @return The default player skull for this user
     */
    public static ItemStack getHead(UUID uuid, String displayName) {
        ItemStack skull =  getHead(uuid);

        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setDisplayName(Colors.conv(displayName));
        skull.setItemMeta(sm);

        return skull;
    }

    /**
     * Gets a player skull from UUID.
     * This method should be run inside an asynchronous task.
     * If the uuid is null or if this player has never played before then an AIR ItemStack will be returned!
     *
     * @param uuid The player's uuid
     * @param displayName The name of this item
     * @param lore The lore to add to this item
     * @return The default player skull for this user
     */
    public static ItemStack getHead(UUID uuid, String displayName, ArrayList<String> lore) {
        ItemStack skull = getHead(uuid);

        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setDisplayName(Colors.conv(displayName));
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, Colors.conv(lore.get(i)));
        }
        sm.setLore(lore);
        skull.setItemMeta(sm);

        return skull;
    }

    /**
     * Gets a player skull from UUID.
     * This method should be run inside an asynchronous task.
     * If the player is null then an AIR ItemStack will be returned!
     *
     * @param player The OfflinePlayer
     * @return The default player skull for this user
     */
    public static ItemStack getHead(OfflinePlayer player) {
        if (player == null) {
            return new ItemStack(Material.AIR);
        }

        return getHead(player.getUniqueId());
    }

    /**
     * Gets a player skull from UUID.
     * This method should be run inside an asynchronous task.
     * If the player is null then an AIR ItemStack will be returned!
     *
     * @param player The OfflinePlayer
     * @param displayName The name of this item
     * @return The default player skull for this user
     */
    public static ItemStack getHead(OfflinePlayer player, String displayName) {
        if (player == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack skull = getHead(player.getUniqueId());

        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setDisplayName(Colors.conv(displayName));
        skull.setItemMeta(sm);

        return skull;
    }

    /**
     * Gets a player skull from UUID.
     * This method should be run inside an asynchronous task.
     * If the player is null then an AIR ItemStack will be returned!
     *
     * @param player The OfflinePlayer
     * @param displayName The name of this item
     * @param lore The lore to add to this item
     * @return The default player skull for this user
     */
    public static ItemStack getHead(OfflinePlayer player, String displayName, ArrayList<String> lore) {
        if (player == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack skull = getHead(player.getUniqueId());

        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setDisplayName(Colors.conv(displayName));
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, Colors.conv(lore.get(i)));
        }
        sm.setLore(lore);
        skull.setItemMeta(sm);

        return skull;
    }

}

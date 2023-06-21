package com.github.mittenmc.serverutils;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Contains useful methods for editing an item's ItemMeta
 * @author GavvyDizzle
 * @version 1.0.3
 * @since 1.0.3
 */
public class ItemStackUtils {

    /**
     * Adds all ItemFlags from the ItemStack to hide all item information.
     * This is to be used for inventory items that should not show extra lore text.
     * @param itemStack The ItemStack
     * @since 1.0.3
     */
    public static void addAllItemFlags(@NotNull ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) return;

        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(meta);
    }

    /**
     * Fills placeholders in the item's display name and lore
     * @param itemStack The ItemStack
     * @param map A map of placeholders and their replacement value
     * @since 1.0.3
     */
    public static void replacePlaceholders(@NotNull ItemStack itemStack, @NotNull Map<String, String> map) {
        replaceDisplayNamePlaceholders(itemStack, map);
        replaceLorePlaceholders(itemStack, map);
    }

    /**
     * Fills placeholders in the item's display name
     * @param itemStack The ItemStack
     * @param map A map of placeholders and their replacement value
     * @since 1.0.3
     */
    public static void replaceDisplayNamePlaceholders(@NotNull ItemStack itemStack, @NotNull Map<String, String> map) {
        if (itemStack.getItemMeta() == null || map.isEmpty()) return;

        ItemMeta meta = itemStack.getItemMeta();
        String displayName = meta.getDisplayName();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            displayName = displayName.replace(entry.getKey(), entry.getValue());
        }
        meta.setDisplayName(displayName);
        itemStack.setItemMeta(meta);
    }

    /**
     * Fills placeholders in the item's lore
     * @param itemStack The ItemStack
     * @param map A map of placeholders and their replacement value
     * @since 1.0.3
     */
    public static void replaceLorePlaceholders(@NotNull ItemStack itemStack, @NotNull Map<String, String> map) {
        if (itemStack.getItemMeta() == null || map.isEmpty()) return;

        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore()) return;
        List<String> lore = meta.getLore();
        assert lore != null;
        if (lore.isEmpty()) return;

        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                line = line.replace(entry.getKey(), entry.getValue());
            }
            lore.set(i, line);
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }

}

package com.github.mittenmc.serverutils.gui.pages;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A simple interface which defines how to get an ItemStack.
 * @author GavvyDizzle
 * @version 1.1.10
 * @since 1.0.10
 */
public interface ItemGenerator {

    /**
     * Gets an item representing this object to be shown in the menu.
     * @param player The player
     * @return An ItemStack
     */
    @NotNull
    ItemStack getMenuItem(Player player);

    /**
     * Creates a new ItemStack representing this object to be given to the player.
     * Implementations should always return a fresh instance.
     * @param player The player
     * @return An ItemStack or null
     */
    @Nullable
    ItemStack getPlayerItem(Player player);

}
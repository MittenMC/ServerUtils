package com.github.mittenmc.serverutils.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * A simple inventory interface.
 * @author GavvyDizzle
 * @version 1.0.8
 * @since 1.0.8
 */
public interface ClickableMenu {

    /**
     * Handles when the player opens this inventory
     * @param player The player
     */
    void openInventory(Player player);

    /**
     * Handles when the player closes this inventory
     * @param player The player
     */
    void closeInventory(Player player);

    /**
     * Handles when the player clicks this inventory
     * @param e The original click event
     */
    void handleClick(InventoryClickEvent e);

}

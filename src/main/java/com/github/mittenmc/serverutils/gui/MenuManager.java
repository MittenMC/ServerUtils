package com.github.mittenmc.serverutils.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A generic inventory manager which passes along close and click events to {@link ClickableMenu}.
 * Each plugin should create its own instance of this class.
 * @author GavvyDizzle
 * @version 1.0.10
 * @since 1.0.10
 */
@SuppressWarnings("unused")
public class MenuManager implements Listener {

    private final JavaPlugin instance;
    private final Map<UUID, ClickableMenu> viewers;

    /**
     * Creates a new MenuManager and registers its listeners with the provided plugin.
     * @param plugin The creating plugin
     */
    public MenuManager(JavaPlugin plugin) {
        this.instance = plugin;
        instance.getServer().getPluginManager().registerEvents(this, instance);
        viewers = new HashMap<>();
    }

    /**
     * Saves this menu as opened for the player.
     * @param player The player
     * @param clickableMenu The menu
     */
    public void saveOpenedMenu(Player player, ClickableMenu clickableMenu) {
        viewers.put(player.getUniqueId(), clickableMenu);
    }

    /**
     * Opens the menu for the player.
     * @param player The player
     * @param clickableMenu The menu
     */
    public void openMenu(Player player, ClickableMenu clickableMenu) {
        clickableMenu.openInventory(player);
        viewers.put(player.getUniqueId(), clickableMenu);
    }

    /**
     * Opens the menu for the player.
     * This will perform a 0 tick delay to allow time for the current inventory to close.
     * @param player The player
     * @param clickableMenu The menu
     */
    public void openMenuDelayed(Player player, ClickableMenu clickableMenu) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            clickableMenu.openInventory(player);
            viewers.put(player.getUniqueId(), clickableMenu);
        });
    }

    /**
     * Passes along all {@link InventoryCloseEvent} to the player's open menu.
     * @param e The original event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        ClickableMenu clickableMenu = viewers.remove(e.getPlayer().getUniqueId());
        if (clickableMenu != null) {
            clickableMenu.closeInventory((Player) e.getPlayer());
        }
    }

    /**
     * Passes along all {@link InventoryClickEvent} to the player's open menu.
     * @param e The original event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        ClickableMenu clickableMenu = viewers.get(e.getWhoClicked().getUniqueId());
        if (clickableMenu != null) {
            clickableMenu.handleClick(e);
        }
    }

    /**
     * Calls {@link Player#closeInventory()} for all current viewers.
     */
    public void closeAllMenus() {
        for (UUID uuid : viewers.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.closeInventory();
        }
        viewers.clear();
    }

    /**
     * Creates a new unmodifiable map.
     * @return A map of UUIDs and their open menu
     */
    public Map<UUID, ClickableMenu> getViewers() {
        return Collections.unmodifiableMap(viewers);
    }
}
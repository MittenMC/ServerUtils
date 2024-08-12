package com.github.mittenmc.serverutils.gui.group;

import com.github.mittenmc.serverutils.gui.ClickableMenu;
import com.github.mittenmc.serverutils.player.PlayerDataContainer;
import com.github.mittenmc.serverutils.player.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A generic inventory manager which passes along close and click events to {@link ClickableMenu}.
 * This requires each viewer to pass along a type to further distinguish permissions.
 * Each plugin must create its own instance of this class.
 * @param <V> The menu owner's type
 * @param <E> The group type
 * @author GavvyDizzle
 * @version 1.1.2
 * @since 1.1.1
 */
@SuppressWarnings("unused")
public class GroupedMenuManager<V extends PlayerProfile, E extends Enum<E>> implements Listener {

    private final JavaPlugin instance;
    private final PlayerDataContainer<V> playerDataContainer;
    private final Map<UUID, GroupedMenuViewer<V,E>> viewers;

    public GroupedMenuManager(JavaPlugin instance, PlayerDataContainer<V> playerDataContainer) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);
        this.playerDataContainer = playerDataContainer;
        viewers = new HashMap<>();
    }

    /**
     * Saves this menu as opened for the owner.
     * @param menu The custom menu
     * @param owner The menu owner
     */
    public void saveOpenedMenu(GroupedClickableMenu<V,E> menu, V owner) {
        viewers.put(owner.getUniqueId(), new GroupedMenuViewer<>(menu, owner, null));

        assert owner.getPlayer() != null;
        owner.getProfileViewers().addViewer(owner.getPlayer());
    }

    /**
     * Saves this menu as opened for the viewer.
     * @param menu The custom menu
     * @param owner The menu owner
     * @param viewer The menu viewer
     * @param group The viewer's group
     */
    public void saveOpenedMenu(GroupedClickableMenu<V,E> menu, V owner, Player viewer, @NotNull E group) {
        viewers.put(viewer.getUniqueId(), new GroupedMenuViewer<>(menu, owner, group));
        owner.getProfileViewers().addViewer(viewer);
    }

    /**
     * Opens the menu for the owner.
     * @param menu The custom menu
     * @param owner The menu owner
     */
    public void openMenu(GroupedClickableMenu<V,E> menu, V owner) {
        menu.openInventory(owner);
        saveOpenedMenu(menu, owner);
    }

    /**
     * Opens the menu for the viewer.
     * @param menu The custom menu
     * @param owner The menu owner
     * @param viewer The menu viewer
     * @param group The viewer's group
     */
    public void openMenu(GroupedClickableMenu<V,E> menu, V owner, Player viewer, @NotNull E group) {
        menu.openInventory(owner, viewer, group);
        saveOpenedMenu(menu, owner, viewer, group);
    }

    /**
     * Opens the menu for the owner.
     * This will perform a 0 tick delay to allow time for the current inventory to close.
     * @param menu The custom menu
     * @param owner The menu owner
     */
    public void openMenuDelayed(GroupedClickableMenu<V,E> menu, V owner) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> openMenu(menu, owner));
    }

    /**
     * Opens the menu for the viewer.
     * This will perform a 0 tick delay to allow time for the current inventory to close.
     * @param menu The custom menu
     * @param owner The menu owner
     * @param viewer The menu viewer
     * @param group The viewer's group
     */
    public void openMenuDelayed(GroupedClickableMenu<V,E> menu, V owner, Player viewer, @NotNull E group) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> openMenu(menu, owner, viewer, group));
    }

    /**
     * Passes along all {@link InventoryCloseEvent} to the player's open menu.
     * @param e The original event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        GroupedMenuViewer<V,E> viewer = viewers.remove(e.getPlayer().getUniqueId());
        if (viewer == null) return;

        if (viewer.group() == null) {
            viewer.menu().closeInventory(viewer.owner());
        } else {
            viewer.menu().closeInventory(viewer.owner(), (Player) e.getPlayer(), viewer.group());
        }

        viewer.owner().getProfileViewers().removeViewer((Player) e.getPlayer());
        playerDataContainer.schedulePlayerUnloadAttempt(viewer.owner());
    }

    /**
     * Passes along all {@link InventoryClickEvent} to the player's open menu.
     * @param e The original event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        GroupedMenuViewer<V,E> viewer = viewers.get(e.getWhoClicked().getUniqueId());
        if (viewer == null) return;

        if (viewer.group() == null) {
            viewer.menu().handleClick(e, viewer.owner());
        } else {
            viewer.menu().handleClick(e, viewer.owner(), (Player) e.getWhoClicked(), viewer.group());
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
}

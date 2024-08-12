package com.github.mittenmc.serverutils.gui.instanced;

import com.github.mittenmc.serverutils.gui.ClickableMenu;
import com.github.mittenmc.serverutils.gui.pages.ItemGenerator;
import com.github.mittenmc.serverutils.gui.pages.PagesMenu;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An inventory implementation which displays contents across pages.
 * This class will pass along inventory actions to a {@link PagesMenu}
 * @author GavvyDizzle
 * @version 1.1.5
 * @since 1.1.5
 */
@SuppressWarnings("unused")
public class InstancedPagesMenu<E extends Comparable<? super E> & ItemGenerator> implements ClickableMenu {

    private static class PagesMenuWrapper<E extends Comparable<? super E> & ItemGenerator> {
        private final PagesMenu<E> menu;
        private final UUID owner;
        private int numViewers;

        public PagesMenuWrapper(PagesMenu<E> menu, OfflinePlayer owner) {
            this.menu = menu;
            this.owner = owner.getUniqueId();
        }
    }

    private final Class<? extends PagesMenu<E>> menuClass;
    private final Object[] args;
    private final Map<UUID, PagesMenuWrapper<E>> viewerMap;
    private final Map<UUID, PagesMenuWrapper<E>> ownerMap;

    /**
     * Creates a new instance
     * @param menuClass The menu class to create instances of
     * @param args The arguments to pass to the menu class' constructor
     */
    public InstancedPagesMenu(Class<? extends PagesMenu<E>> menuClass, Object... args) {
        this.menuClass = menuClass;
        this.args = args;
        viewerMap = new HashMap<>();
        ownerMap = new HashMap<>();
    }

    private void addViewer(Player player, PagesMenuWrapper<E> wrapper) {
        viewerMap.put(player.getUniqueId(), wrapper);
        wrapper.numViewers++;
    }

    @Override
    public void openInventory(Player player) {
        throw new UnsupportedOperationException("Unable to open this menu type directly!");
    }

    /**
     * Opens the pages menu for this viewer
     * @param owner The owner
     * @param viewer The viewer
     * @param items The item list to add to the menu if it doesn't exist yet
     */
    public void openMenu(OfflinePlayer owner, Player viewer, Collection<E> items) {
        PagesMenuWrapper<E> wrapper = ownerMap.get(owner.getUniqueId());
        PagesMenu<E> menu;

        if (wrapper == null) {
            menu = PagesMenuFactory.createMenu(menuClass, args);
            if (menu == null) {
                throw new RuntimeException("Failed to create menu");
            }

            menu.setItems(items);

            wrapper = new PagesMenuWrapper<>(menu, owner);
            ownerMap.put(owner.getUniqueId(), wrapper);
        }
        else {
            menu = wrapper.menu;
        }

        addViewer(viewer, wrapper);
        menu.openInventory(viewer);
    }

    @Override
    public void closeInventory(Player player) {
        PagesMenuWrapper<E> wrapper = viewerMap.remove(player.getUniqueId());
        if (wrapper == null) return;

        wrapper.numViewers--;
        if (wrapper.numViewers <= 0) {
            ownerMap.remove(wrapper.owner);
        }

        wrapper.menu.closeInventory(player);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        PagesMenuWrapper<E> wrapper = viewerMap.get(e.getWhoClicked().getUniqueId());
        if (wrapper == null) return;

        wrapper.menu.handleClick(e);
    }

    /**
     * Gets the menu belonging to the owner if one exists.
     * @param uuid The owner's uuid
     * @return The menu or null
     */
    @Nullable
    public PagesMenu<E> getOwnerMenu(UUID uuid) {
        PagesMenuWrapper<E> wrapper = ownerMap.get(uuid);
        if (wrapper == null) return null;

        return wrapper.menu;
    }
}

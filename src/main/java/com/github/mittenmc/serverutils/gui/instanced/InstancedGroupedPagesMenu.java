package com.github.mittenmc.serverutils.gui.instanced;

import com.github.mittenmc.serverutils.gui.group.GroupedClickableMenu;
import com.github.mittenmc.serverutils.gui.group.GroupedPagesMenu;
import com.github.mittenmc.serverutils.gui.pages.ItemGenerator;
import com.github.mittenmc.serverutils.player.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An inventory implementation which displays contents across pages while supporting groups.
 * This class will pass along inventory actions to a {@link GroupedPagesMenu}
 * @author GavvyDizzle
 * @version 1.1.5
 * @since 1.1.5
 */
@SuppressWarnings("unused")
public class InstancedGroupedPagesMenu<V extends PlayerProfile, E extends Enum<E>, I extends Comparable<? super I> & ItemGenerator> implements GroupedClickableMenu<V, E> {

    private static class GroupedPagesMenuWrapper<V extends PlayerProfile, E extends Enum<E>, I extends Comparable<? super I> & ItemGenerator> {
        private final GroupedPagesMenu<V,E,I> menu;
        private final V owner;
        private int numViewers;

        public GroupedPagesMenuWrapper(GroupedPagesMenu<V,E,I> menu, V owner) {
            this.menu = menu;
            this.owner = owner;
        }
    }

    private final Class<? extends GroupedPagesMenu<V,E,I>> menuClass;
    private final Object[] args;
    private final Map<UUID, GroupedPagesMenuWrapper<V,E,I>> viewerMap;
    private final Map<UUID, GroupedPagesMenuWrapper<V,E,I>> ownerMap;

    /**
     * Creates a new instance
     * @param menuClass The menu class to create instances of
     * @param args The arguments to pass to the menu class' constructor
     */
    public InstancedGroupedPagesMenu(Class<? extends GroupedPagesMenu<V,E,I>> menuClass, Object... args) {
        this.menuClass = menuClass;
        this.args = args;
        viewerMap = new HashMap<>();
        ownerMap = new HashMap<>();
    }

    private void addViewer(Player player, GroupedPagesMenuWrapper<V,E,I> wrapper) {
        viewerMap.put(player.getUniqueId(), wrapper);
        wrapper.numViewers++;
    }

    @Override
    public void openInventory(V owner) {
        throw new UnsupportedOperationException("Unable to open this menu type directly!");
    }

    @Override
    public void openInventory(V owner, Player viewer, E group) {
        throw new UnsupportedOperationException("Unable to open this menu type directly!");
    }

    /**
     * Opens the pages menu for this player and sets them as the menu owner
     * @param owner The owner
     * @param items The item list to add to the menu if it doesn't exist yet
     */
    public void openMenu(V owner, Collection<I> items) {
        GroupedPagesMenuWrapper<V,E,I> wrapper = ownerMap.get(owner.getUniqueId());
        GroupedPagesMenu<V,E,I> menu;

        if (wrapper == null) {
            menu = GroupedPagesMenuFactory.createMenu(menuClass, args);
            if (menu == null) {
                throw new RuntimeException("Failed to create menu");
            }

            menu.setItems(items);

            wrapper = new GroupedPagesMenuWrapper<>(menu, owner);
            ownerMap.put(owner.getUniqueId(), wrapper);
        }
        else {
            menu = wrapper.menu;
        }

        assert owner.getPlayer() != null;
        addViewer(owner.getPlayer(), wrapper);
        menu.openInventory(owner);
    }

    /**
     * Opens the pages menu for this viewer
     * @param owner The owner
     * @param viewer The viewer
     * @param group The viewer's group
     * @param items The item list to add to the menu if it doesn't exist yet
     */
    public void openMenu(V owner, Player viewer, E group, Collection<I> items) {
        GroupedPagesMenuWrapper<V,E,I> wrapper = ownerMap.get(owner.getUniqueId());
        GroupedPagesMenu<V,E,I> menu;

        if (wrapper == null) {
            menu = GroupedPagesMenuFactory.createMenu(menuClass, args);
            if (menu == null) {
                throw new RuntimeException("Failed to create menu");
            }

            menu.setItems(items);

            wrapper = new GroupedPagesMenuWrapper<>(menu, owner);
            ownerMap.put(owner.getUniqueId(), wrapper);
        }
        else {
            menu = wrapper.menu;
        }

        addViewer(viewer, wrapper);
        menu.openInventory(owner, viewer, group);
    }

    @Override
    public void closeInventory(V owner) {
        GroupedPagesMenuWrapper<V,E,I> wrapper = viewerMap.remove(owner.getUniqueId());
        if (wrapper == null) return;

        wrapper.numViewers--;
        if (wrapper.numViewers <= 0) {
            ownerMap.remove(wrapper.owner.getUniqueId());
        }

        wrapper.menu.closeInventory(owner);
    }

    @Override
    public void closeInventory(V owner, Player viewer, E group) {
        GroupedPagesMenuWrapper<V,E,I> wrapper = viewerMap.remove(viewer.getUniqueId());
        if (wrapper == null) return;

        wrapper.numViewers--;
        if (wrapper.numViewers <= 0) {
            ownerMap.remove(wrapper.owner.getUniqueId());
        }

        wrapper.menu.closeInventory(owner, viewer, group);
    }

    @Override
    public void handleClick(InventoryClickEvent e, V owner) {
        GroupedPagesMenuWrapper<V,E,I> wrapper = viewerMap.get(e.getWhoClicked().getUniqueId());
        if (wrapper == null) return;

        wrapper.menu.handleClick(e, owner);
    }

    @Override
    public void handleClick(InventoryClickEvent e, V owner, Player viewer, E group) {
        GroupedPagesMenuWrapper<V,E,I> wrapper = viewerMap.get(e.getWhoClicked().getUniqueId());
        if (wrapper == null) return;

        wrapper.menu.handleClick(e, owner, viewer, group);
    }

    /**
     * Gets the menu belonging to the owner if one exists.
     * @param uuid The owner's uuid
     * @return The menu or null
     */
    @Nullable
    public GroupedPagesMenu<V,E,I> getOwnerMenu(UUID uuid) {
        GroupedPagesMenuWrapper<V,E,I> wrapper = ownerMap.get(uuid);
        if (wrapper == null) return null;

        return wrapper.menu;
    }
}

package com.github.mittenmc.serverutils.gui.group;

import com.github.mittenmc.serverutils.gui.pages.ItemGenerator;
import com.github.mittenmc.serverutils.gui.pages.PagesMenu;
import com.github.mittenmc.serverutils.player.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An inventory implementation which displays contents across pages while supporting groups.
 * @author GavvyDizzle
 * @version 1.1.2
 * @since 1.1.1
 */
@SuppressWarnings("unused")
public abstract class GroupedPagesMenu<V extends PlayerProfile, E extends Enum<E>, I extends Comparable<? super I> & ItemGenerator> extends PagesMenu<I> implements GroupedClickableMenu<V, E> {

    // We are required to save another copy here, so we are able to differentiate owner and viewer clicks.
    // The implementation of the PagesMenu does not expose enough information, so this is the only option.
    private final Map<UUID, GroupedMenuViewer<V,E>> viewers;

    public GroupedPagesMenu(PagesMenuBuilder<I> builder) {
        super(builder);
        viewers = new HashMap<>();
    }

    @Override
    public void openInventory(V owner) {
        Player player = owner.getPlayer();
        assert player != null;

        super.openInventory(player);
        viewers.put(player.getUniqueId(), new GroupedMenuViewer<>(null, owner, null));
    }

    @Override
    public void openInventory(V owner, Player viewer, E group) {
        super.openInventory(viewer);
        viewers.put(viewer.getUniqueId(), new GroupedMenuViewer<>(null, owner, group));
    }

    @Override
    public void closeInventory(V owner) {
        Player player = owner.getPlayer();
        assert player != null;

        super.closeInventory(player);
        viewers.remove(player.getUniqueId());
    }

    @Override
    public void closeInventory(V owner, Player viewer, E group) {
        super.closeInventory(viewer);
        viewers.remove(viewer.getUniqueId());
    }

    @Override
    public void handleClick(InventoryClickEvent e, V owner) {
        super.handleClick(e);
    }

    @Override
    public void handleClick(InventoryClickEvent e, V owner, Player viewer, E group) {
        super.handleClick(e);
    }

    @Override
    public void onItemClick(InventoryClickEvent e, Player player, I item) {
        GroupedMenuViewer<V,E> viewer = viewers.get(player.getUniqueId());

        if (viewer.group() == null) {
            onItemClick(e, viewer.owner(), item);
        } else {
            onItemClick(e, viewer.owner(), player, viewer.group(), item);
        }
    }

    public abstract void onItemClick(InventoryClickEvent e, V owner, I item);

    public abstract void onItemClick(InventoryClickEvent e, V owner, Player viewer, E group, I item);
}

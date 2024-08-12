package com.github.mittenmc.serverutils.gui.group;

import com.github.mittenmc.serverutils.player.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * A menu interface which allows different viewer groups to coexist.
 * The self group uses a null reference of {@link E}
 * @param <V> The menu owner's type
 * @param <E> The group type
 * @author GavvyDizzle
 * @version 1.1.2
 * @since 1.1.1
 */
public interface GroupedClickableMenu<V extends PlayerProfile, E extends Enum<E>> {

    /**
     * Handles when the player opens their own custom inventory
     * @param owner The owning player
     */
    void openInventory(V owner);

    /**
     * Handles when another player views a custom inventory
     * @param owner The owning player
     * @param viewer The viewing player
     * @param group The viewer's group
     */
    void openInventory(V owner, Player viewer, E group);

    /**
     * Handles when the player closes their own custom inventory
     * @param owner The owning player
     */
    void closeInventory(V owner);

    /**
     * Handles when another player closes a custom inventory
     * @param owner The owning player
     * @param viewer The viewing player
     * @param group The viewer's group
     */
    void closeInventory(V owner, Player viewer, E group);

    /**
     * Handles when the player clicks their own inventory
     * @param e The original click event
     * @param owner The owning player
     */
    void handleClick(InventoryClickEvent e, V owner);

    /**
     * Handles when the player clicks a custom inventory
     * @param e The original click event
     * @param owner The owning player
     * @param viewer The viewing player
     * @param group The viewer's group
     */
    void handleClick(InventoryClickEvent e, V owner, Player viewer, E group);

}

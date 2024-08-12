package com.github.mittenmc.serverutils.gui.group;

import com.github.mittenmc.serverutils.player.profile.PlayerProfile;

/**
 * Contains information about a menu viewer with a group.
 * @param menu The menu
 * @param owner The menu owner
 * @param group The group the player belongs to
 * @param <V> The menu owner's type
 * @param <E> The group type
 * @author GavvyDizzle
 * @version 1.1.2
 * @since 1.1.1
 */
// Whenever this is referenced, the player object is present. There is no need to save the player object this belongs to.
public record GroupedMenuViewer<V extends PlayerProfile, E extends Enum<E>>
        (GroupedClickableMenu<V, E> menu, V owner, E group) {}

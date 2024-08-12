package com.github.mittenmc.serverutils.player.profile;

import com.github.mittenmc.serverutils.player.PlayerContainer;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a player data object which can have viewers.
 * @see ProfileViewers
 * @author GavvyDizzle
 * @version 1.1.2
 * @since 1.1.2
 */
@SuppressWarnings("unused")
@Getter
public class PlayerProfile extends PlayerContainer {

    private final ProfileViewers<PlayerProfile> profileViewers;

    public PlayerProfile(@NotNull Player player) {
        super(player);
        profileViewers = new ProfileViewers<>(this);
    }

    public PlayerProfile(@NotNull OfflinePlayer offlinePlayer) {
        super(offlinePlayer);
        profileViewers = new ProfileViewers<>(this);
    }
}

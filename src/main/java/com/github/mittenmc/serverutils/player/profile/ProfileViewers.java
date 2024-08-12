package com.github.mittenmc.serverutils.player.profile;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

/**
 * Stores information about the active viewers of {@link E}'s menu
 * Implementations may define logic which locks menu editing to one player at a time.
 * @author GavvyDizzle
 * @version 1.1.2
 * @since 1.1.2
 */
@SuppressWarnings("unused")
public class ProfileViewers<E> {

    @Getter private final E data;
    private int viewerID;
    private final HashMap<UUID, Viewer> viewers;

    public ProfileViewers(E data) {
        this.data = data;
        viewerID = 1;
        viewers = new HashMap<>();
    }

    /**
     * Adds a new viewer to this profile or updates an existing one.
     * @param player The player
     */
    public void addViewer(Player player) {
        Viewer viewer = viewers.get(player.getUniqueId());

        if (viewer != null) {
            viewer.updateAccessTime();
        } else {
            viewers.put(player.getUniqueId(), new Viewer(viewerID++, player.getUniqueId()));
        }
    }

    public void removeViewer(Player player) {
        viewers.remove(player.getUniqueId());
    }

    @Nullable
    private Viewer getViewer(Player player) {
        return viewers.get(player.getUniqueId());
    }

    public boolean isEmpty() {
        return viewers.isEmpty();
    }

    @Getter
    public static class Viewer implements Comparable<Viewer> {

        private final int id;
        private final UUID uuid;
        private long accessMillis;

        public Viewer(int id, UUID uuid) {
            this.id = id;
            this.uuid = uuid;
            accessMillis = System.currentTimeMillis();
        }

        private void updateAccessTime() {
            accessMillis = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "Viewer " + id + " | uuid: " + uuid + " | time: " + accessMillis;
        }

        @Override
        public int compareTo(@NotNull ProfileViewers.Viewer o) {
            return Comparator.comparingLong(Viewer::getAccessMillis)
                    .thenComparingInt(Viewer::getId)
                    .compare(this, o);
        }
    }

}

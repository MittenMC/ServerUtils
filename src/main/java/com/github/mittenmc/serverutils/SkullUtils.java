package com.github.mittenmc.serverutils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.UUID;

/**
 * Contains useful methods for getting textured skulls.
 * After being retrieved once, a skull is cached.
 * <p>
 * Skulls can be retrieved as long as it has the prefix "http://textures.minecraft.net/texture/" or is the value that comes directly after it.
 * <p>
 * This field is located as the last value on <a href="https://minecraft-heads.com">Minecraft Heads</a>
 * @author GavvyDizzle
 * @version 1.0.5
 * @since 1.0.5
 */
public class SkullUtils {

    private static final String URL_PREFIX = "http://textures.minecraft.net/texture/";
    private static final LoadingCache<String, ItemStack> skullCache;

    static {
        skullCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(
                        new CacheLoader<>() {
                            @Override
                            public @NotNull ItemStack load(@Nonnull String url) {
                                ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);

                                if (itemStack.getItemMeta() instanceof SkullMeta skullMeta) {
                                    PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
                                    try {
                                        playerProfile.getTextures().setSkin(URI.create(url).toURL());
                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException("Failed to generate skull from URL");
                                    }
                                    skullMeta.setOwnerProfile(playerProfile);
                                    itemStack.setItemMeta(skullMeta);
                                }

                                return itemStack;
                            }
                        });
    }

    private static String getAsURL(@NotNull String str) {
        if (!str.startsWith("http:")) str = URL_PREFIX + str;
        return str;
    }

    /**
     * Determines if this skull is currently cached.
     *
     * @param url The URL
     * @return True if the skull is cached.
     * @since 1.0.5
     */
    public static boolean isCached(@Nullable String url) {
        if (url == null) return false;

        url = getAsURL(url);
        return skullCache.asMap().containsKey(url);
    }

    /**
     * Gets a player skull from UUID.
     * Plugins should verify the cache state through {@link PlayerHeads#isCached(UUID)}.
     * If the UUID is cached, it is safe to call this method synchronously, otherwise it should be called asynchronously.
     *
     * @param url The URL or suffix of the URL
     * @return The skull for this URL
     * @since 1.0.5
     */
    public static ItemStack getSkull(@Nullable String url) {
        if (url == null) return new ItemStack(Material.AIR);
        url = getAsURL(url);

        try {
            return skullCache.get(url).clone();
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
    }

}

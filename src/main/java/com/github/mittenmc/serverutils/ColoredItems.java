package com.github.mittenmc.serverutils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Contains all glass pane colors for easy use as inventory fillers.
 * @author GavvyDizzle
 * @version 1.0
 * @since 1.0
 */
public enum ColoredItems {

    RED(new ItemStack(Material.RED_STAINED_GLASS_PANE)),
    ORANGE(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE)),
    YELLOW(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE)),
    LIME(new ItemStack(Material.LIME_STAINED_GLASS_PANE)),
    GREEN(new ItemStack(Material.GREEN_STAINED_GLASS_PANE)),
    CYAN(new ItemStack(Material.CYAN_STAINED_GLASS_PANE)),
    LIGHT_BLUE(new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE)),
    BLUE(new ItemStack(Material.BLUE_STAINED_GLASS_PANE)),
    PURPLE(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE)),
    MAGENTA(new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE)),
    PINK(new ItemStack(Material.PINK_STAINED_GLASS_PANE)),
    LIGHT_GRAY(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)),
    GRAY(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)),
    WHITE(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)),
    BLACK(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)),
    BROWN(new ItemStack(Material.BROWN_STAINED_GLASS_PANE));

    private final ItemStack glass;

    ColoredItems(ItemStack glass) {
        ItemMeta meta = glass.getItemMeta();
        assert meta != null;
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        this.glass = glass;
    }

    /**
     * @return A glass pane of this color with no name
     * @since 1.0
     */
    public ItemStack getGlass() {
        return glass.clone();
    }

    /**
     * Gets a glass pane with a custom name
     *
     * @param displayName The name to give the glass
     * @return The glass pane of this color
     * @since 1.0
     */
    public ItemStack getGlass(String displayName) {
        ItemStack glass = this.getGlass();
        ItemMeta meta = glass.getItemMeta();
        assert meta != null;
        meta.setDisplayName(Colors.conv(displayName));
        glass.setItemMeta(meta);

        return glass;
    }

    /**
     * Gets the glass color specified
     *
     * @param color The color glass
     * @return The colored glass if it exists, otherwise AIR
     * @since 1.0
     */
    public static ItemStack getGlassByName(String color) {
        try {
            return ColoredItems.valueOf(color.toUpperCase()).getGlass();
        } catch (IllegalArgumentException e) {
            return new ItemStack(Material.AIR);
        }
    }

}

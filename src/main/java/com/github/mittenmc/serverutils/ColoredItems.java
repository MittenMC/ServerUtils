package com.github.mittenmc.serverutils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum ColoredItems {

    RED(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)),
    ORANGE(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1)),
    YELLOW(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4)),
    LIME(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5)),
    GREEN(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13)),
    CYAN(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9)),
    LIGHT_BLUE(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3)),
    BLUE(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11)),
    PURPLE(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 10)),
    MAGENTA(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2)),
    PINK(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6)),
    LIGHT_GRAY(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8)),
    GRAY(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)),
    WHITE(new ItemStack(Material.STAINED_GLASS_PANE)),
    BLACK(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)),
    BROWN(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 12));

    private final ItemStack glass;

    ColoredItems(ItemStack glass) {
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        this.glass = glass;
    }

    /**
     * @return A glass pane of this color with no name
     */
    public ItemStack getGlass() {
        return glass.clone();
    }

    /**
     * Gets a glass pane with a custom name
     *
     * @param displayName The name to give the glass
     * @return The glass pane of this color
     */
    public ItemStack getGlass(String displayName) {
        ItemStack glass = this.getGlass();
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(Colors.conv(displayName));
        glass.setItemMeta(meta);

        return glass;
    }

    /**
     * Gets the glass color specified
     *
     * @param color The color glass
     * @return The colored glass if it exists, otherwise AIR
     */
    public static ItemStack getGlassByName(String color) {
        try {
            return ColoredItems.valueOf(color.toUpperCase()).getGlass();
        } catch (IllegalArgumentException e) {
            return new ItemStack(Material.AIR);
        }
    }

}

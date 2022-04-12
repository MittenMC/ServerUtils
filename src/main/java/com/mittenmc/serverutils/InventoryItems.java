package com.mittenmc.serverutils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class InventoryItems {

    private static final HashMap<String, ItemStack> fillers;

    static {
        fillers = new HashMap<>(16);
        fillers.put("red", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14));
        fillers.put("orange", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1));
        fillers.put("yellow", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4));
        fillers.put("lime", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
        fillers.put("green", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13));
        fillers.put("cyan", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9));
        fillers.put("light_blue", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3));
        fillers.put("blue", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 11));
        fillers.put("purple", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 10));
        fillers.put("magenta", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2));
        fillers.put("pink", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6));
        fillers.put("light_gray", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8));
        fillers.put("gray", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));
        fillers.put("white", new ItemStack(Material.STAINED_GLASS_PANE));
        fillers.put("black", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
        fillers.put("brown", new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 12));

        for (ItemStack itemStack : fillers.values()) {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(" ");
            itemStack.setItemMeta(meta);
        }
    }

    /**
     * Gets a glass pane with no name when given a color.
     * Returns an empty ItemStack if given an incorrect color.
     *
     * @param color The color of glass
     * @return The glass pane of the requested color, AIR if not found
     */
    public static ItemStack getFiller(String color) {
        if (fillers.containsKey(color.toLowerCase())) {
            return fillers.get(color.toLowerCase());
        }
        else {
            return new ItemStack(Material.AIR);
        }
    }

}

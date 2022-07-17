package com.github.mittenmc.serverutils;

import org.bukkit.Material;

/**
 * Contains useful methods for converting information from .yml files to usable object types.
 * @author GavvyDizzle
 * @version 1.0
 * @since 1.0
 */
public class ConfigUtils {

    /**
     * Gets the material from a string.
     * This method will return DIRT if no material is found.
     *
     * @param material The string to get the material for
     * @return The given material, DIRT otherwise.
     * @since 1.0
     */
    public static Material getMaterial(String material) {
        if (Material.getMaterial(material) == null) {
            return Material.DIRT;
        }
        return Material.getMaterial(material);
    }

    /**
     * Gets the material from a string.
     *
     * @param material The string to get the material for
     * @param defaultMaterial The material to default to if no material is found.
     * @return The given material, the defaultMaterial otherwise.
     * @since 1.0
     */
    public static Material getMaterial(String material, Material defaultMaterial) {
        if (Material.getMaterial(material) == null) {
            return defaultMaterial;
        }
        return Material.getMaterial(material);
    }

}

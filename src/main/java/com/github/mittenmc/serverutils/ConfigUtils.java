package com.github.mittenmc.serverutils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * Contains useful methods for converting information from .yml files to usable object types.
 * @author GavvyDizzle
 * @version 1.0.11
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
    public static Material getMaterial(@Nullable String material) {
        return getMaterial(material, Material.DIRT);
    }

    /**
     * Gets the material from a string.
     * This method will return DIRT if no material is found.
     *
     * @param material The string to get the material for
     * @return The given material, DIRT otherwise.
     * @since 1.0.11
     */
    public static Material getNullableMaterial(@Nullable String material) {
        if (material == null) return null;

        return Material.getMaterial(material.toUpperCase());
    }

    /**
     * Gets the material from a string.
     *
     * @param material The string to get the material for
     * @param defaultMaterial The material to default to if no material is found.
     * @return The given material, the defaultMaterial otherwise.
     * @since 1.0
     */
    public static Material getMaterial(@Nullable String material, @NotNull Material defaultMaterial) {
        if (material == null) return defaultMaterial;

        Material mat = Material.getMaterial(material.toUpperCase());
        return mat != null ? mat : defaultMaterial;
    }

    /**
     * Gets an ItemStack from a ConfigurationSection.
     * If something goes wrong, DIRT will be returned.
     * Use {@link ConfigUtils#getItemStack(ConfigurationSection, String, Logger)} to include logging.
     *
     * @param configurationSection The ConfigurationSection to read from.
     * @return The ItemStack with properties from the ConfigurationSection.
     * @since 1.0.6
     */
    @NotNull
    public static ItemStack getItemStack(@Nullable ConfigurationSection configurationSection) {
        if (configurationSection == null) return new ItemStack(Material.DIRT);

        ItemStack itemStack;
        if (configurationSection.getBoolean("usingSkull")) {
            String skullLink = configurationSection.getString("skullLink");
            if (skullLink == null || skullLink.isBlank()) {
                return new ItemStack(Material.DIRT);
            }
            itemStack = SkullUtils.getSkull(skullLink);
        }
        else {
            itemStack = new ItemStack(getMaterial(configurationSection.getString("material"), Material.DIRT));
        }

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        if (configurationSection.contains("name")) {
            meta.setDisplayName(Colors.conv(configurationSection.getString("name")));
        }

        if (configurationSection.contains("lore")) {
            meta.setLore(Colors.conv(configurationSection.getStringList("lore")));
        }

        if (configurationSection.contains("customModelData")) {
            int customModelData = configurationSection.getInt("customModelData");
            if (customModelData > 0) meta.setCustomModelData(customModelData);
        }

        if (configurationSection.contains("flags")) {
            for (String flag : configurationSection.getStringList("flags")) {
                try {
                    meta.addItemFlags(ItemFlag.valueOf(flag));
                } catch (Exception ignored) {}
            }
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Gets an ItemStack from a ConfigurationSection.
     * If something goes wrong, DIRT will be returned.
     *
     * @param configurationSection The ConfigurationSection to read from.
     * @param fileName The name of the original config file.
     * @param logger The calling plugin's logger.
     * @return The ItemStack with properties from the ConfigurationSection.
     * @since 1.0.6
     */
    @NotNull
    public static ItemStack getItemStack(@Nullable ConfigurationSection configurationSection, String fileName, Logger logger) {
        if (configurationSection == null) return new ItemStack(Material.DIRT);

        ItemStack itemStack;
        if (configurationSection.getBoolean("usingSkull")) {
            String skullLink = configurationSection.getString("skullLink");
            if (skullLink == null || skullLink.isBlank()) {
                logger.warning("No skull link given at " + configurationSection.getCurrentPath() + ".skullLink in " + fileName);
                return new ItemStack(Material.DIRT);
            }
            itemStack = SkullUtils.getSkull(skullLink);
        }
        else {
            itemStack = new ItemStack(getMaterial(configurationSection.getString("material"), Material.DIRT));
        }

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        if (configurationSection.contains("name")) {
            meta.setDisplayName(Colors.conv(configurationSection.getString("name")));
        }

        if (configurationSection.contains("lore")) {
            meta.setLore(Colors.conv(configurationSection.getStringList("lore")));
        }

        if (configurationSection.contains("customModelData")) {
            int customModelData = configurationSection.getInt("customModelData");
            if (customModelData > 0) meta.setCustomModelData(customModelData);
        }

        if (configurationSection.contains("flags")) {
            for (String flag : configurationSection.getStringList("flags")) {
                try {
                    meta.addItemFlags(ItemFlag.valueOf(flag));
                } catch (Exception e) {
                    logger.warning("Invalid ItemFlag '" + flag + "' at " + configurationSection.getCurrentPath() + ".flags in " + fileName);
                }
            }
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Gets an ItemStack from a ConfigurationSection.
     * Use {@link ConfigUtils#getItemStack(ConfigurationSection, String, Logger)} to include logging.
     *
     * @param configurationSection The ConfigurationSection to read from.
     * @return The ItemStack with properties from the ConfigurationSection or null
     * @since 1.0.11
     */
    @Nullable
    public static ItemStack getNullableItemStack(@Nullable ConfigurationSection configurationSection) {
        if (configurationSection == null) return null;

        ItemStack itemStack;
        if (configurationSection.getBoolean("usingSkull")) {
            String skullLink = configurationSection.getString("skullLink");
            if (skullLink == null || skullLink.isBlank()) {
                return null;
            }
            itemStack = SkullUtils.getSkull(skullLink);
        }
        else {
            Material material = getNullableMaterial(configurationSection.getString("material"));
            if (material == null) return null;

            itemStack = new ItemStack(material);
        }

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        if (configurationSection.contains("name")) {
            meta.setDisplayName(Colors.conv(configurationSection.getString("name")));
        }

        if (configurationSection.contains("lore")) {
            meta.setLore(Colors.conv(configurationSection.getStringList("lore")));
        }

        if (configurationSection.contains("customModelData")) {
            int customModelData = configurationSection.getInt("customModelData");
            if (customModelData > 0) meta.setCustomModelData(customModelData);
        }

        if (configurationSection.contains("flags")) {
            for (String flag : configurationSection.getStringList("flags")) {
                try {
                    meta.addItemFlags(ItemFlag.valueOf(flag));
                } catch (Exception ignored) {}
            }
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Gets an ItemStack from a ConfigurationSection.
     *
     * @param configurationSection The ConfigurationSection to read from.
     * @param fileName The name of the original config file.
     * @param logger The calling plugin's logger.
     * @return The ItemStack with properties from the ConfigurationSection or null
     * @since 1.0.11
     */
    @Nullable
    public static ItemStack getNullableItemStack(@Nullable ConfigurationSection configurationSection, String fileName, Logger logger) {
        if (configurationSection == null) return null;

        ItemStack itemStack;
        if (configurationSection.getBoolean("usingSkull")) {
            String skullLink = configurationSection.getString("skullLink");
            if (skullLink == null || skullLink.isBlank()) {
                logger.warning("No skull link given at " + configurationSection.getCurrentPath() + ".skullLink in " + fileName);
                return null;
            }
            itemStack = SkullUtils.getSkull(skullLink);
        }
        else {
            Material material = getNullableMaterial(configurationSection.getString("material"));
            if (material == null) {
                logger.warning("Invalid material defined at " + configurationSection.getCurrentPath() + ".material in " + fileName);
                return null;
            }

            itemStack = new ItemStack(material);
        }

        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        if (configurationSection.contains("name")) {
            meta.setDisplayName(Colors.conv(configurationSection.getString("name")));
        }

        if (configurationSection.contains("lore")) {
            meta.setLore(Colors.conv(configurationSection.getStringList("lore")));
        }

        if (configurationSection.contains("customModelData")) {
            int customModelData = configurationSection.getInt("customModelData");
            if (customModelData > 0) meta.setCustomModelData(customModelData);
        }

        if (configurationSection.contains("flags")) {
            for (String flag : configurationSection.getStringList("flags")) {
                try {
                    meta.addItemFlags(ItemFlag.valueOf(flag));
                } catch (Exception e) {
                    logger.warning("Invalid ItemFlag '" + flag + "' at " + configurationSection.getCurrentPath() + ".flags in " + fileName);
                }
            }
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}

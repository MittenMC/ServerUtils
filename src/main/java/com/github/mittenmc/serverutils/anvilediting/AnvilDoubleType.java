package com.github.mittenmc.serverutils.anvilediting;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Adds support for Java's double primitive type
 * @author GavvyDizzle
 * @version 1.0.7
 * @since 1.0.7
 */
public class AnvilDoubleType extends AnvilInputType {

    public AnvilDoubleType(String inventoryName, ItemStack infoItem, ItemStack resultItem) {
        super(inventoryName, infoItem, resultItem);
    }

    public AnvilDoubleType(String inventoryName, ItemStack backButton, ItemStack infoItem, ItemStack resultItem) {
        super(inventoryName, backButton, infoItem, resultItem);
    }

    @Override
    public Object convert(String value) {
        if (value == null || value.isBlank()) return -1;

        try {
            return Double.parseDouble(value);
        } catch (Exception ignored) {
            return -1;
        }
    }

    @Override
    public String convert(Object value) {
        if (!(value instanceof Double val)) return "";
        return val.toString();
    }

    @Override
    public boolean isInvalidInput(@Nullable String value) {
        if (value == null || value.isBlank()) return true;

        try {
            Double.parseDouble(value);
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }
}
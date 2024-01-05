package com.github.mittenmc.serverutils.anvilediting;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Adds support for Java's int primitive type
 * @author GavvyDizzle
 * @version 1.0.7
 * @since 1.0.7
 */
public class AnvilIntegerType extends AnvilInputType {

    public AnvilIntegerType(String inventoryName, ItemStack infoItem, ItemStack resultItem) {
        super(inventoryName, infoItem, resultItem);
    }

    public AnvilIntegerType(String inventoryName, ItemStack backButton, ItemStack infoItem, ItemStack resultItem) {
        super(inventoryName, backButton, infoItem, resultItem);
    }

    @Override
    public Object convert(String value) {
        if (value == null || value.isBlank()) return -1;

        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return -1;
        }
    }

    @Override
    public String convert(Object value) {
        if (!(value instanceof Integer val)) return "";
        return val.toString();
    }

    @Override
    public boolean isInvalidInput(@Nullable String value) {
        if (value == null || value.isBlank()) return true;

        try {
            Integer.parseInt(value);
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }
}
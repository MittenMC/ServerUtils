package com.github.mittenmc.serverutils.anvilediting;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Adds support for Java's String class
 * @author GavvyDizzle
 * @version 1.0.7
 * @since 1.0.7
 */
public class AnvilStringType extends AnvilInputType {

    public AnvilStringType(String inventoryName, ItemStack infoItem, ItemStack resultItem) {
        super(inventoryName, infoItem, resultItem);
    }

    public AnvilStringType(String inventoryName, ItemStack backButton, ItemStack infoItem, ItemStack resultItem) {
        super(inventoryName, backButton, infoItem, resultItem);
    }

    @Override
    public Object convert(String value) {
        return value;
    }

    @Override
    public String convert(Object value) {
        if (!(value instanceof String val)) return "";
        return val;
    }

    @Override
    public boolean isInvalidInput(@Nullable String value) {
        return value == null || value.isBlank();
    }
}

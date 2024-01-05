package com.github.mittenmc.serverutils.anvilediting;

import com.github.mittenmc.serverutils.ColoredItems;
import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.ItemStackUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

/**
 * Allows for generic types to be passed through an anvil inventory.
 * The placeholder "{value}" is replaced in an item's name and lore fields.
 * @author GavvyDizzle
 * @version 1.0.7
 * @since 1.0.7
 */
public abstract class AnvilInputType {

    private final String inventoryName;
    private final ItemStack backButton, infoItem, resultItem;

    /**
     * Creates a new AnvilInputType with a default back button
     * @param infoItem The info item (slot 1)
     * @param resultItem The result item (slot 2)
     */
    public AnvilInputType(String inventoryName, ItemStack infoItem, ItemStack resultItem) {
        this.inventoryName = inventoryName;
        backButton = ColoredItems.RED.getGlass();
        ItemMeta meta = backButton.getItemMeta();
        meta.setLore(List.of(Colors.conv("&cBack")));
        backButton.setItemMeta(meta);

        this.infoItem = infoItem;
        this.resultItem = resultItem;
    }

    /**
     * Creates a new AnvilInputType with all items provided
     * @param backButton The back button item (slot 0)
     * @param infoItem The info item (slot 1)
     * @param resultItem The result item (slot 2)
     */
    public AnvilInputType(String inventoryName, ItemStack backButton, ItemStack infoItem, ItemStack resultItem) {
        this.inventoryName = inventoryName;
        this.backButton = backButton;
        this.infoItem = infoItem;
        this.resultItem = resultItem;
    }

    /**
     * Converts the String input from the anvil menu into the type this item can recognize.
     * This will impose any constraints necessary on the input.
     * @param value The input from the anvil inventory
     * @return The input converted to child's type
     */
    public abstract Object convert(String value);

    /**
     * Converts this generic type into a String for item placeholders.
     * @param value The generic value
     * @return The generic value converted to a String
     */
    public abstract String convert(Object value);

    /**
     * Determines if the input from the anvil inventory is invalid.
     * @param value The value to check
     * @return True if this input is invalid, false otherwise
     */
    public abstract boolean isInvalidInput(String value);

    /**
     * Gets the back button with this value as its name.
     * The anvil will set the input field to this value.
     * @param value The current value
     * @return A back button with this name
     */
    public ItemStack getBackButton(Object value) {
        ItemStack itemStack = backButton.clone();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(convert(value));
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    /**
     * Gets the info item and replaces placeholders with this value.
     * @param value The current value
     * @return The info item with placeholders filled
     */
    @Nullable
    public ItemStack getInfoItem(Object value) {
        if (infoItem == null) return null;
        else if (!infoItem.hasItemMeta()) return infoItem;

        ItemStack itemStack = infoItem.clone();

        HashMap<String, String> map = new HashMap<>(1);
        map.put("{value}", convert(value));
        ItemStackUtils.replacePlaceholders(itemStack, map);

        return itemStack;
    }

    /**
     * Gets the result item and replaces placeholders with this value
     * @param value The current value
     * @return The result item with placeholders filled
     */
    @Nullable
    public ItemStack getResultItem(@Nullable String value) {
        if (resultItem == null || isInvalidInput(value)) return null;
        else if (!resultItem.hasItemMeta()) return resultItem;

        ItemStack itemStack = resultItem.clone();

        HashMap<String, String> map = new HashMap<>(1);
        map.put("{value}", convert(convert(value)));
        ItemStackUtils.replacePlaceholders(itemStack, map);

        return itemStack;
    }

    public String getInventoryName() {
        return inventoryName;
    }
}

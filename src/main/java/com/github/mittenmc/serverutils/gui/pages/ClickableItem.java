package com.github.mittenmc.serverutils.gui.pages;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines a menu item that is viewable and clickable.
 * @param <E> The element type which is an ItemGenerator
 */
@SuppressWarnings("unused")
public abstract class ClickableItem<E extends ItemGenerator> implements ItemGenerator {

    private final ItemGenerator itemGenerator;

    public ClickableItem(ItemStack itemStack) {
        this.itemGenerator = new ItemGenerator() {
            @Override
            public @NotNull ItemStack getMenuItem(Player player) {
                return itemStack;
            }

            @Override
            public @Nullable ItemStack getPlayerItem(Player player) {
                return null;
            }
        };
    }

    public ClickableItem(E itemGenerator) {
        this.itemGenerator = itemGenerator;
    }

    @Override
    public @NotNull ItemStack getMenuItem(Player player) {
        return itemGenerator.getMenuItem(player);
    }

    @Override
    public @Nullable ItemStack getPlayerItem(Player player) {
        return itemGenerator.getPlayerItem(player);
    }

    /**
     * Handles what happens when this item is clicked.
     * @param e The original click event
     * @param player The player who clicked
     */
    public abstract void onClick(InventoryClickEvent e, Player player);

}
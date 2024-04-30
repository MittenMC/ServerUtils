package com.github.mittenmc.serverutils.gui.pages;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A ClickableItem which defaults to having no click behavior.
 * @param <E> The element type which is an ItemGenerator
 */
@SuppressWarnings("unused")
public class DisplayItem<E extends ItemGenerator> extends ClickableItem<E> {

    public DisplayItem(ItemStack itemStack) {
        super(itemStack);
    }

    public DisplayItem(E itemGenerator) {
        super(itemGenerator);
    }

    @Override
    public void onClick(InventoryClickEvent e, Player player) {

    }
}
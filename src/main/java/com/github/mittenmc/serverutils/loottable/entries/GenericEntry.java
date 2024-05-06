package com.github.mittenmc.serverutils.loottable.entries;

import com.github.mittenmc.serverutils.loottable.LootTableEntry;
import org.bukkit.inventory.ItemStack;

/**
 * A loot table entry that gives itself.
 * @author GavvyDizzle
 * @version 1.0.11
 * @since 1.0.11
 */
@SuppressWarnings("unused")
public class GenericEntry<E> implements LootTableEntry<E> {

    private final E item;
    private final double weight;

    public GenericEntry(E item, double weight) {
        this.item = item;
        this.weight = weight;
    }

    @Override
    public E map() {
        return item;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public E getEntry() {
        return item;
    }

    @Override
    public String toString() {
        return "GenericEntry: (weight=" +  weight + " entry=" + item.toString() + ")";
    }
}

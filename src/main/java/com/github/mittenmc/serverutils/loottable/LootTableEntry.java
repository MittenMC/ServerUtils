package com.github.mittenmc.serverutils.loottable;

/**
 * Defines a loot table entry.
 * @param <E> The resulting type from this item
 * @author GavvyDizzle
 * @version 1.0.11
 * @since 1.0.11
 */
public interface LootTableEntry<E> {

    /**
     * @return The result type from this entry
     */
    E map();

    /**
     * @return The weight of this entry
     */
    double getWeight();

}

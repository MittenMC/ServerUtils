package com.github.mittenmc.serverutils.loottable;

import com.github.mittenmc.serverutils.Numbers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A simple loot table class.
 * @param <K> The {@link LootTableEntry<V>} objects put in the loot table
 * @param <V> The result type to obtain from {@link K}
 * @author GavvyDizzle
 * @version 1.0.11
 * @since 1.0.11
 */
@SuppressWarnings("unused")
public class LootTable<K extends LootTableEntry<V>, V> {

    private final TreeMap<Double, K> table;
    private double totalWeight;

    public LootTable() {
        table = new TreeMap<>();
        totalWeight = 0.0;
    }

    /**
     * Adds an item to this loot table.
     * @param item The item to add
     */
    public void add(@NotNull K item) {
        double weight = item.getWeight();
        if (weight <= 0) return;

        totalWeight += weight;
        table.put(totalWeight, item);
    }

    /**
     * Adds all items to this loot table.
     * @param items The items to add
     */
    public void addAll(Collection<K> items) {
        for (K item : items) {
            add(item);
        }
    }

    /**
     * Maps a drop to its correct type.
     * @param selection The selection from the loot table
     * @return The selection mapped to the output type
     */
    @Nullable
    private V mapDrop(@NotNull K selection) {
        return selection.map();
    }

    @Nullable
    public V getDrop() {
        if (table.isEmpty()) return null;

        double randomWeight = Numbers.randomNumber(0, totalWeight);
        Map.Entry<Double, K> entry = table.ceilingEntry(randomWeight);
        return entry != null ? mapDrop(entry.getValue()) : null;
    }

    /**
     * Get rewards from this loot table.
     * @param n The number of selections to make
     * @param withReplacement Whether to replace selected items
     * @return A list of results
     * @see #getDropsWithReplacement(int)
     * @see #getDropsWithoutReplacement(int)
     */
    public List<V> getDrops(int n, boolean withReplacement) {
        return withReplacement ? getDropsWithReplacement(n) : getDropsWithoutReplacement(n);
    }

    /**
     * Get rewards from this loot table.
     * Results are allowed to be duplicates.
     * @param n The number of selections to make
     * @return A list of results
     */
    public List<V> getDropsWithReplacement(int n) {
        if (table.isEmpty() || n <= 0) return Collections.emptyList();

        List<V> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double randomWeight = Numbers.randomNumber(0, totalWeight);
            Map.Entry<Double, K> entry = table.ceilingEntry(randomWeight);
            if (entry != null) {
                V reward = mapDrop(entry.getValue());
                if (reward != null) result.add(reward);
            }
        }
        return result;
    }

    /**
     * Get rewards from this loot table.
     * Results are removed after being selected.
     * @param n The number of selections to make
     * @return A list of results
     */
    public List<V> getDropsWithoutReplacement(int n) {
        if (table.isEmpty() || n <= 0) return Collections.emptyList();

        TreeMap<Double, K> clone = new TreeMap<>(table);
        double totalWeight = this.totalWeight;
        List<V> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (clone.isEmpty()) break;

            double randomWeight = Numbers.randomNumber(0, totalWeight);
            Map.Entry<Double, K> entry = clone.ceilingEntry(randomWeight);
            if (entry != null) {
                V reward = mapDrop(entry.getValue());
                if (reward != null) result.add(reward);

                // Adjust total weight and remove selected item
                totalWeight -= entry.getKey();
                clone.remove(entry.getKey());
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public int size() {
        return table.size();
    }

    @Override
    public String toString() {
        return "Loot Table (" + table.size() + " entries): " + table;
    }
}

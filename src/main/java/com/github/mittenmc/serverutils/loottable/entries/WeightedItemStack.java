package com.github.mittenmc.serverutils.loottable.entries;

import com.github.mittenmc.serverutils.ConfigUtils;
import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.RandomValuePair;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * A loot table entry that gives an {@link ItemStack}.
 * @author GavvyDizzle
 * @version 1.0.11
 * @since 1.0.11
 */
@SuppressWarnings("unused")
public class WeightedItemStack extends GenericEntry<ItemStack> {

    private final RandomValuePair amountRange;

    public WeightedItemStack(ItemStack itemStack, double weight, int minAmount, int maxAmount) {
        super(itemStack, weight);
        this.amountRange = new RandomValuePair(minAmount, maxAmount);
    }

    @NotNull
    public static List<WeightedItemStack> readList(@Nullable ConfigurationSection section, String fileName, Logger logger) {
        if (section == null) return Collections.emptyList();

        List<WeightedItemStack> list = new ArrayList<>();

        for (String key : section.getKeys(false)) {
            ConfigurationSection entrySection = section.getConfigurationSection(key);
            if (entrySection != null) {
                list.add(read(entrySection, fileName, logger));
            }
        }
        return list;
    }

    @Nullable
    public static WeightedItemStack read(@Nullable ConfigurationSection section, String fileName, Logger logger) {
        if (section == null) return null;

        section.addDefault("weight", 1.0);
        section.addDefault("amount.min", 1);
        section.addDefault("amount.max", 1);
        section.addDefault("item.material", Material.AIR.name());

        int weight = section.getInt("weight");
        if (weight <= 0) {
            logger.warning("Non-positive weight given for WeightedItemStack at " + section.getCurrentPath() + ".weight in " + fileName);
            return null;
        }

        ItemStack item = ConfigUtils.getNullableItemStack(section.getConfigurationSection("item"), fileName, logger);
        if (item == null) return null;

        int min = Numbers.constrain(section.getInt("amount.min"), 1, item.getMaxStackSize());
        int max = Numbers.constrain(section.getInt("amount.max"), 1, item.getMaxStackSize());

        return new WeightedItemStack(item, weight, min, max);
    }

    @Override
    public ItemStack map() {
        ItemStack item = getEntry().clone();
        item.setAmount(amountRange.getRandomInt());
        return item;
    }

    @Override
    public String toString() {
        return "WeightedItemStack: (weight=" + getWeight() + " amounts=[" + amountRange.toString() + "] entry=" + getEntry().toString() + ")";
    }
}

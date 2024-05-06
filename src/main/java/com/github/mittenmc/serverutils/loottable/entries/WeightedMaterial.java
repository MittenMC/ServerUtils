package com.github.mittenmc.serverutils.loottable.entries;

import com.github.mittenmc.serverutils.ConfigUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * A loot table entry that gives a {@link Material}.
 * @author GavvyDizzle
 * @version 1.0.11
 * @since 1.0.11
 */
@SuppressWarnings("unused")
public class WeightedMaterial extends GenericEntry<Material> {

    public WeightedMaterial(Material item, double weight) {
        super(item, weight);
    }

    @NotNull
    public static List<WeightedMaterial> readList(@Nullable ConfigurationSection section, String fileName, Logger logger) {
        if (section == null) return Collections.emptyList();

        List<WeightedMaterial> list = new ArrayList<>();

        for (String key : section.getKeys(false)) {
            ConfigurationSection entrySection = section.getConfigurationSection(key);
            if (entrySection != null) {
                list.add(read(entrySection, fileName, logger));
            }
        }
        return list;
    }

    @Nullable
    public static WeightedMaterial read(@Nullable ConfigurationSection section, String fileName, Logger logger) {
        if (section == null) return null;

        section.addDefault("weight", 1.0);
        section.addDefault("material", Material.AIR.name());

        int weight = section.getInt("weight");
        if (weight <= 0) {
            logger.warning("Non-positive weight given for WeightedItemStack at " + section.getCurrentPath() + ".weight in " + fileName);
            return null;
        }

        Material material = ConfigUtils.getNullableMaterial(section.getString("material"));
        if (material == null) {
            logger.warning("Invalid material '" + section.getString("material") + "' given for WeightedMaterial at " + section.getCurrentPath() + ".material in " + fileName);
        }

        return new WeightedMaterial(material, weight);
    }

    @Override
    public String toString() {
        return "WeightedMaterial: (weight=" + getWeight() + " entry=" + getEntry().name() + ")";
    }
}

package com.github.mittenmc.serverutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A FileConfiguration management interface.
 * This only supports files in a plugin's top-level directory.
 * @author GavvyDizzle
 * @version 1.0.4
 * @since 1.0.4
 */
public class ConfigManager {

    private class FileObject {
        private final File file;
        private FileConfiguration fileConfiguration;

        private FileObject(String fileName) {
            this.file = new File(plugin.getDataFolder(), fileName);
            reload();
        }

        private void reload() {
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.options().copyDefaults(true);
        }

        private void save() {
            try {
                fileConfiguration.save(file);
            }
            catch (IOException e) {
                plugin.getLogger().severe("Failed to save file: " + file.getName());
            }
        }
    }

    private final Plugin plugin;
    private final Map<String, FileObject> fileMap;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        fileMap = new HashMap<>();
    }

    public ConfigManager(Plugin plugin, Set<String> fileNameSet) {
        this.plugin = plugin;
        fileMap = new HashMap<>();
        registerFiles(fileNameSet);
    }

    /**
     * Registers a new yml file.
     * If the file is already registered, this will fail silently.
     * It will not register a file if the requested file is config.yml.
     * @param name The name of the new file
     */
    public void registerFile(String name) {
        if (!name.endsWith(".yml")) name += ".yml";
        if (name.equals("config.yml")) return;

        if (!fileMap.containsKey(name)) {
            fileMap.put(name, new FileObject(name));
        }
    }

    /**
     * Registers multiple new yml files.
     * @param nameSet The names of the files
     */
    public void registerFiles(Set<String> nameSet) {
        for (String str : nameSet) {
            registerFile(str);
        }
    }

    /**
     * @param fileName The name of the config file
     * @return The FileConfiguration for this file name or null if none exists
     */
    @Nullable
    public FileConfiguration get(String fileName) {
        if (!fileName.endsWith(".yml")) fileName += ".yml";

        FileObject fileObject = fileMap.get(fileName);
        if (fileObject == null) return null;

        return fileObject.fileConfiguration;
    }

    /**
     * Saves this config file if it exists
     * @param fileName The name of the config file
     */
    public void save(String fileName) {
        if (!fileName.endsWith(".yml")) fileName += ".yml";

        FileObject fileObject = fileMap.get(fileName);
        if (fileObject != null) {
            fileObject.save();
        }
    }

    /**
     * Saves all config files
     */
    public void saveAll() {
        for (FileObject fileObject : fileMap.values()) {
            fileObject.save();
        }
    }

    /**
     * Reloads this config file if it exists
     * @param fileName The name of the config file
     */
    public void reload(String fileName) {
        if (!fileName.endsWith(".yml")) fileName += ".yml";

        FileObject fileObject = fileMap.get(fileName);
        if (fileObject != null) {
            fileObject.reload();
        }
    }

    /**
     * Reloads all config files
     */
    public void reloadAll() {
        for (FileObject fileObject : fileMap.values()) {
            fileObject.reload();
        }
    }

}

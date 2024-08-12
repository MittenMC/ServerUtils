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
import java.util.logging.Level;

/**
 * A FileConfiguration management interface.
 * @author GavvyDizzle
 * @version 1.0.13
 * @since 1.0.4
 */
@SuppressWarnings("unused")
public class ConfigManager {

    private class FileObject {
        private final File file;
        private FileConfiguration fileConfiguration;

        private FileObject(String path) {
            this.file = new File(plugin.getDataFolder(), path);

            if (path.contains("/")) {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    parentDir.mkdirs();
                }
            }

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
                plugin.getLogger().log(Level.SEVERE, "Failed to save file: " + file.getName(), e);
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

    private String cleanPath(String path) {
        if (path.startsWith("/")) path = path.substring(1);
        if (!path.endsWith(".yml")) path += ".yml";

        return path;
    }

    /**
     * Registers a new yml file.
     * If the file is already registered, this will fail silently.
     * It will not register a file if the requested file is config.yml and in the top level directory
     * @param name The name of the new file
     */
    public void registerFile(String name) {
        name = cleanPath(name);
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
        fileName = cleanPath(fileName);

        FileObject fileObject = fileMap.get(fileName);
        if (fileObject == null) return null;

        return fileObject.fileConfiguration;
    }

    /**
     * Saves this config file if it exists
     * @param fileName The name of the config file
     */
    public void save(String fileName) {
        fileName = cleanPath(fileName);

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
        fileName = cleanPath(fileName);

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

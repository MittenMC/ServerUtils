package com.github.mittenmc.serverutils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Creates a new HikariCP connection pool for a plugin's MySQL database.<p>
 * Attempts to read settings from {@value SETTINGS_FILE_NAME} in the plugin's data folder.<p>
 * Executes any SQL statements in the plugin's resources/{@value SETUP_FILE_NAME} file on initialization.
 * @author GavvyDizzle, Quagmire
 * @version 1.1.3
 * @since 1.1.3
 */
@SuppressWarnings("unused")
public class DatabaseConnectionPool {

    private static final String SETTINGS_FILE_NAME = "database_settings.yml";
    private static final String SETUP_FILE_NAME = "dbsetup.sql";

    protected final JavaPlugin instance;
    private final HikariDataSource dataSource;

    public DatabaseConnectionPool(JavaPlugin instance) {
        this.instance = instance;

        FileConfiguration config = getSettingsFile();

        String host = config.getString("host");
        int port = config.getInt("port");
        String databaseName = config.getString("database");
        String username = config.getString("username");
        String password = config.getString("password");
        int poolSize = config.getInt("pool-size");
        int timeout = config.getInt("timeout", 30000);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, databaseName)); // Only supports MySQL
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setInitializationFailTimeout(0);
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setConnectionTimeout(timeout);

        // Read in all values from the settings section.
        // All keys are assumed to be valid settings.
        ConfigurationSection settings = config.getConfigurationSection("settings");
        if (settings != null) {
            for (String key : settings.getKeys(false)) {
                Object value = settings.get(key);
                hikariConfig.addDataSourceProperty(key, value);
            }
        }

        this.dataSource = new HikariDataSource(hikariConfig);
        createTables();
    }

    /**
     * Gets the settings file while injecting default values and saving them.
     * @return The settings file for reading
     */
    private FileConfiguration getSettingsFile() {
        File file = new File(instance.getDataFolder(), SETTINGS_FILE_NAME);
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);

        fileConfiguration.options().copyDefaults(true);
        addDefaultSettings(fileConfiguration);

        saveSettingsFile(file, fileConfiguration);
        return fileConfiguration;
    }

    /**
     * Saves the settings file to disk.
     * @param file The file
     * @param fileConfiguration The modified FileConfiguration object
     */
    private void saveSettingsFile(File file, FileConfiguration fileConfiguration) {
        try {
            fileConfiguration.save(file);
        } catch (Exception e) {
            instance.getLogger().log(Level.SEVERE, "Failed to save database settings file", e);
        }
    }

    /**
     * Adds default settings to the section
     * @param section The ConfigurationSection to modify
     * @see <a href="https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration">HikariCP MySQL Configuration</a>
     */
    private void addDefaultSettings(ConfigurationSection section) {
        // The version id currently is not in use. Feel free to use it if you need to edit the config settings
        section.addDefault("version", 1);

        section.addDefault("host", "localhost");
        section.addDefault("port", 3306);
        section.addDefault("database", "database");
        section.addDefault("username", "root");
        section.addDefault("password", "password");
        section.addDefault("pool-size", 10);
        section.addDefault("timeout", 10000);

        section.addDefault("settings.cachePrepStmts", true);
        section.addDefault("settings.prepStmtCacheSize", 250);
        section.addDefault("settings.prepStmtCacheSqlLimit", 2048);
        section.addDefault("settings.useServerPrepStmts", true);
        section.addDefault("settings.useLocalSessionState", true);
        section.addDefault("settings.rewriteBatchedStatements", true);
        section.addDefault("settings.cacheResultSetMetadata", true);
        section.addDefault("settings.cacheServerConfiguration", true);
        section.addDefault("settings.elideSetAutoCommits", true);
        section.addDefault("settings.maintainTimeStats", false);
    }

    /**
     * Creates a connection to this database
     * @return A connection
     * @throws SQLException The error(s)
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Attempts to connect to this database.
     * @return If the connection was successful
     */
    public boolean testConnection() {
        try (Connection ignored = getConnection()) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Frees up any remaining database resources
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Executes any SQL statements in the plugin's resources/{@value SETUP_FILE_NAME} file on initialization.
     * This is intended to create or modify tables in the database.
     */
    private void createTables() {
        // Read setup file to create new table
        String setup;
        try (InputStream in = instance.getResource(SETUP_FILE_NAME)) {
            assert in != null;
            setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            instance.getLogger().log(Level.SEVERE, "Failed to read database setup file.", e);
            instance.getServer().getPluginManager().disablePlugin(instance);
            return;
        }

        String[] queries = setup.split(";");
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // execute each query to the database.
            for (String query : queries) {
                if (query.trim().isEmpty()) continue;
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.execute();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            instance.getLogger().log(Level.SEVERE, "Failed to while executing database setup file.", e);
            instance.getServer().getPluginManager().disablePlugin(instance);
        }
    }
}

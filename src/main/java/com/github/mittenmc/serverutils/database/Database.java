package com.github.mittenmc.serverutils.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Links a database to a connection pool.
 * @author GavvyDizzle, Quagmire
 * @version 1.1.3
 * @since 1.1.3
 */
@SuppressWarnings("unused")
public class Database {

    private final JavaPlugin instance;
    protected final DatabaseConnectionPool pool;

    public Database(DatabaseConnectionPool pool) {
        this.instance = pool.instance;
        this.pool = pool;
    }

    /**
     * Pretty logging of a {@link SQLException} with the plugin logger on a {@link Level#SEVERE} level.
     *
     * @param message message to log. What went wrong.
     * @param ex      exception to log
     * @since 1.1.3
     */
    protected void logSQLError(String message, SQLException ex) {
        logSQLError(Level.SEVERE, message, ex);
    }

    /**
     * Pretty logging of a {@link SQLException} with the plugin logger.
     *
     * @param level   logging level of error. A {@link Level} lower than {@link Level#INFO} will be changed to {@link
     *                Level#INFO}
     * @param message message to log. What went wrong.
     * @param ex      exception to log
     * @since 1.1.3
     */
    protected void logSQLError(Level level, String message, SQLException ex) {
        if (level.intValue() < Level.INFO.intValue()) {
            level = Level.INFO;
        }

        instance.getLogger().log(
                level, String.format("%s%nMessage: %s%nCode: %s%nState: %s",
                        message, ex.getMessage(), ex.getErrorCode(), ex.getSQLState()), ex);
    }
}

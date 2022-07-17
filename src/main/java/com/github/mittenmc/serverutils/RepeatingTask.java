package com.github.mittenmc.serverutils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Creates a cancellable Repeating task using Bukkit's #scheduleSyncRepeatingTask().
 * @author Orange-Tabby
 * @version 1.0
 * @since 1.0
 */
public abstract class RepeatingTask implements Runnable {

    private final int taskId;

    /**
     * Creates a new Bukkit repeating task.
     *
     * @param plugin The plugin that created this task
     * @param delay The delay in ticks before starting this task
     * @param period The amount of time in ticks between calls of this task
     */
    public RepeatingTask(JavaPlugin plugin, int delay, int period) {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, delay, period);
    }

    /**
     * Cancels the task.
     */
    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

}
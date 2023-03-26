package com.github.mittenmc.serverutils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Implementation for a SubCommand which represents a command unique in its first argument.
 * @author GavvyDizzle
 * @version 1.0.1
 * @since 1.0
 */
public abstract class SubCommand implements Comparable<SubCommand> {

    /**
     * @return The name of the subcommand
     * @since 1.0
     */
    public abstract String getName();

    /**
     * @return A short explanation of what this command does
     * @since 1.0
     */
    public abstract String getDescription();

    /**
     * @return The command usage
     * @since 1.0
     */
    public abstract String getSyntax();

    /**
     * @return The command usage with colors
     * @since 1.0
     */
    public abstract String getColoredSyntax();

    /**
     * Called when an individual subcommand is run
     *
     * @param sender The sender of the command
     * @param args The arguments of the command
     * @since 1.0
     */
    public abstract void perform(CommandSender sender, String[] args);

    /**
     * Gets a list of suggested responses for this subcommand.
     *
     * @param player The player
     * @param args Whole arguments array
     * @return A String list of suggested responses
     * @since 1.0
     */
    public abstract List<String> getSubcommandArguments(Player player, String[] args);

    /**
     *
     * @param o the object to be compared.
     * @return The value of compareTo() given the command name
     * @since 1.0.1
     */
    @Override
    public int compareTo(SubCommand o) {
        return getName().compareTo(o.getName());
    }
}
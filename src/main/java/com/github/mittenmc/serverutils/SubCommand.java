package com.github.mittenmc.serverutils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Implementation for a SubCommand which represents a command unique in its first argument.
 * @author GavvyDizzle
 * @version 1.0.3
 * @since 1.0
 */
public abstract class SubCommand implements Comparable<SubCommand> {

    private String name, description, syntax, coloredSyntax, permission;

    /**
     * @return The name of the subcommand
     * @since 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Change the name of this command
     * @param name The new name
     * @since 1.0.3
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return A short explanation of what this command does
     * @since 1.0
     */
    public String getDescription() {
        return description;
    }

    /**
     * Change the description of this command
     * @param description The new description
     * @since 1.0.3
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The command usage
     * @since 1.0
     */
    public String getSyntax() {
        return syntax;
    }

    /**
     * Change the syntax of this command
     * @param syntax The new syntax
     * @since 1.0.3
     */
    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    /**
     * @return The command usage with colors
     * @since 1.0
     */
    public String getColoredSyntax() {
        return coloredSyntax;
    }

    /**
     * Change the colored syntax of this command
     * @param coloredSyntax The new colored syntax
     * @since 1.0.3
     */
    public void setColoredSyntax(String coloredSyntax) {
        this.coloredSyntax = coloredSyntax;
    }

    /**
     * Checks this command's permission against the command sender.
     * If the permission is null, this will return true.
     * @param commandSender Who sent the command
     * @return If the sender has permission to use this command
     * @since 1.0.3
     */
    public boolean hasPermission(CommandSender commandSender) {
        if (permission == null) return true;
        return commandSender.hasPermission(permission);
    }

    /**
     * Change the permission of this command
     * @param permission The new permission
     * @since 1.0.3
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

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
     * @deprecated As of release 1.0.2, replaced by {@link #getSubcommandArguments(CommandSender, String[])}.
     * This exists only for backwards compatibility until all plugins have updated to the new version.
     */
    @Deprecated
    public List<String> getSubcommandArguments(Player player, String[] args){
        return null;
    }

    /**
     * Gets a list of suggested responses for this subcommand.
     *
     * @param sender The sender of the command
     * @param args Whole arguments array
     * @return A String list of suggested responses
     * @since 1.0.2
     */
    public abstract List<String> getSubcommandArguments(CommandSender sender, String[] args);

    /**
     * Sorts commands alphabetically by their name
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
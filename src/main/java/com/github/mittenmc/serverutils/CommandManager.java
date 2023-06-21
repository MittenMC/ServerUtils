package com.github.mittenmc.serverutils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A subcommand management interface
 * @author GavvyDizzle
 * @version 1.0.3
 * @since 1.0.3
 */
public class CommandManager implements TabExecutor {

    private final PluginCommand command;
    private final ArrayList<SubCommand> subcommands;
    private final ArrayList<String> subcommandStrings;
    private final String permissionPrefix;
    private String commandDisplayName, noPermissionMessage, invalidSubCommandMessage, noSubCommandProvidedMessage;

    /**
     * Creates a new subcommand wrapper for this command
     * @param command The command
     * @throws RuntimeException If the provided command does not have a permission
     * @since 1.0.3
     */
    public CommandManager(PluginCommand command) {
        this.command = command;
        command.setExecutor(this);

        if (command.getPermission() == null) throw new RuntimeException("This command does not have a permission. All commands must have a permission in the plugin.yml file." +
                "The offending command is '" + command.getName() + "' from the plugin " + command.getPlugin().getName() + ".");
        permissionPrefix = command.getPermission() + ".";

        commandDisplayName = command.getName();
        noPermissionMessage = Colors.conv("&cInsufficient Permission");
        invalidSubCommandMessage = Colors.conv("&cInvalid subcommand provided");
        noSubCommandProvidedMessage = Colors.conv("&cNo subcommand provided. &eUse '/" + commandDisplayName + " help' to see a list of valid commands");

        subcommands = new ArrayList<>();
        subcommandStrings = new ArrayList<>();
    }

    /**
     * Register a new subcommand with this manager
     * @param subCommand The subcommand to register
     * @since 1.0.3
     */
    public void registerCommand(SubCommand subCommand) {
        subcommands.add(subCommand);
        subcommandStrings.add(subCommand.getName());
    }

    /**
     * Sorts all subcommands for use in the help command.
     * This should be called after all subcommands have been registered.
     * @since 1.0.3
     */
    public void sortCommands() {
        Collections.sort(subcommands);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            for (SubCommand subCommand : subcommands) {
                if (args[0].equalsIgnoreCase(subCommand.getName())) {

                    if (!subCommand.hasPermission(sender)) {
                        onNoPermission(sender, args);
                        return true;
                    }

                    subCommand.perform(sender, args);
                    return true;
                }
            }
            onInvalidSubcommand(sender, args);
            return true;
        }
        onNoSubcommand(sender, args);
        return true;
    }

    /**
     * Handles what is done the sender does not have permission to use the command.
     * By default, it will send a message telling them they don't have permission.
     * @param sender The sender of the command
     * @param args The arguments of the command
     */
    public void onNoPermission(CommandSender sender, String[] args) {
        sender.sendMessage(noPermissionMessage);
    }

    /**
     * Handles what is done when no subcommand is provided.
     * By default, it will send a message telling them to use the help command.
     * @param sender The sender of the command
     * @param args The arguments of the command
     */
    public void onNoSubcommand(CommandSender sender, String[] args) {
        sender.sendMessage(noSubCommandProvidedMessage);
    }

    /**
     * Handles what is done when an invalid subcommand is provided.
     * By default, it will send a message telling them it is invalid.
     * @param sender The sender of the command
     * @param args The arguments of the command
     */
    public void onInvalidSubcommand(CommandSender sender, String[] args) {
        sender.sendMessage(invalidSubCommandMessage);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> list = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], subcommandStrings, list);
            return list;
        }
        else if (args.length >= 2) {
            for (SubCommand subcommand : subcommands) {
                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    return subcommand.getSubcommandArguments(sender, args);
                }
            }
        }

        return null;
    }

    /**
     * @return The command
     */
    public PluginCommand getCommand() {
        return command;
    }

    /**
     * @return The display name of this command
     */
    public String getCommandDisplayName() {
        return commandDisplayName;
    }

    /**
     * Update the display name of this command
     * @param commandDisplayName The new display name
     */
    public void setCommandDisplayName(String commandDisplayName) {
        this.commandDisplayName = commandDisplayName;
    }

    /**
     * The permission is what is defined in the plugins.yml file.
     * This value will have a trailing period.
     * @return The permission prefix of this command
     */
    public String getPermissionPrefix() {
        return permissionPrefix;
    }

    /**
     * @return The list of subcommands
     */
    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    /**
     * Update the message sent when the sender does not have permission to use a subcommand
     * @param noPermissionMessage The message to send
     */
    public void setNoPermissionMessage(String noPermissionMessage) {
        this.noPermissionMessage = noPermissionMessage;
    }

    /**
     * Update the message sent when the sender provides a subcommand that does not exist
     * @param invalidSubCommandMessage The message to send
     */
    public void setInvalidSubCommandMessage(String invalidSubCommandMessage) {
        this.invalidSubCommandMessage = invalidSubCommandMessage;
    }

    /**
     * Update the message sent when the sender does not provide a subcommand
     * @param noSubCommandProvidedMessage The message to send
     */
    public void setNoSubCommandProvidedMessage(String noSubCommandProvidedMessage) {
        this.noSubCommandProvidedMessage = noSubCommandProvidedMessage;
    }
}

package com.github.mittenmc.serverutils;

import com.github.mittenmc.serverutils.command.WildcardCommand;
import com.github.mittenmc.serverutils.command.expansion.CommandExpansion;
import com.github.mittenmc.serverutils.command.expansion.ExpansionUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A subcommand management interface
 * @see SubCommand
 * @author GavvyDizzle
 * @version 1.1.7
 * @since 1.0.3
 */
public class CommandManager implements TabExecutor {

    public static final int MAX_ALLOWED_ENUMERATIONS = 100;
    public static final int MAX_ALLOWED_WILDCARD_ENUMERATIONS = 500;

    @Getter private final PluginCommand command;
    @Getter private final Map<String, SubCommand> subCommandMap;
    private final List<String> subcommandStrings;
    @Getter private final String permissionPrefix;
    @Getter @Setter private String commandDisplayName;
    @Setter private String noPermissionMessage, invalidSubCommandMessage, noSubCommandProvidedMessage;

    private static final String tooManyWildcardEnumerationsMessage, noWildcardEnumerationsMessage;

    static {
        tooManyWildcardEnumerationsMessage = Colors.conv("&cToo many attempted enumerations. The maximum amount is " + MAX_ALLOWED_WILDCARD_ENUMERATIONS + "!");
        noWildcardEnumerationsMessage = Colors.conv("&cYour wildcard argument &e'{arg}'&c did not find any matches!");
    }

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

        subCommandMap = new HashMap<>();
        subcommandStrings = new ArrayList<>();
    }

    /**
     * Register a new subcommand with this manager
     * @param subCommand The subcommand to register
     * @since 1.0.3
     */
    public void registerCommand(SubCommand subCommand) {
        subCommandMap.put(subCommand.getName().toLowerCase(), subCommand);
        subcommandStrings.add(subCommand.getName());
        Collections.sort(subcommandStrings);
    }

    /**
     * Sorts all subcommands for use in the help command.
     * This should be called after all subcommands have been registered.
     * @since 1.0.3
     * @deprecated No longer does anything. Please remove!
     */
    @Deprecated
    public void sortCommands() {}

    /**
     * Attempts to find a subcommand matching this value.
     * If one exists in the map, then it will be chosen.
     * If not, all commands will be re-checked for a matching prefix.
     * If only one matches, then use it.
     * @param arg The subcommand name argument passed through the command
     * @return The matching subcommand or null
     */
    @Nullable
    private SubCommand matchCommand(String arg) {
        arg = arg.toLowerCase();

        SubCommand subCommand = subCommandMap.get(arg.toLowerCase());
        if (subCommand != null) return subCommand;

        List<String> arr = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, subcommandStrings, arr);
        if (arr.size() == 1) {
            return subCommandMap.get(arr.get(0).toLowerCase());
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            onNoSubcommand(sender, args);
            return true;
        }

        SubCommand subCommand = matchCommand(args[0]);
        if (subCommand == null) {
            onInvalidSubcommand(sender, args);
            return true;
        }

        if (!subCommand.hasPermission(sender)) {
            onNoPermission(sender, args);
            return true;
        }

        if (subCommand instanceof WildcardCommand) {
            // Wildcard commands support "expanded" logic implicitly, so call them if the wildcard is not present.
            // This is necessary to default WildCard commands to support expansions.
            if (String.join(" ", args).contains("*")) {
                runWildcardCommand(subCommand, sender, args);
            } else {
                runExpandedCommand(subCommand, sender, args);
            }
        }
        else if (subCommand.supportsExpansions()) {
            runExpandedCommand(subCommand, sender, args);
        } else {
            subCommand.perform(sender, args);
        }
        return true;
    }

    /**
     * Attempts to expand the provided command using regex wildcard matching.
     * Since this is passed to a generic regex matcher, it supports further filtering with regex on the wildcard term!
     * @since 1.1.6
     */
    private void runWildcardCommand(SubCommand subCommand, @NotNull CommandSender sender, String[] args) {
        if (!(subCommand instanceof WildcardCommand wildcardCommand)) return;

        // Only expand the first wildcard found. Only supported in args 1 and above
        int index = -1;
        for (int i = 1; i < args.length; i++) {
            if (args[1].contains("*")) {
                index = i;
                break;
            }
        }

        // No * found, run default command
        if (index == -1) {
            subCommand.perform(sender, args);
            return;
        }

        List<String[]> enumerations = new ArrayList<>();
        for (String newArg : WildcardCommand.filter(wildcardCommand.getWildcardValues(index, args), args[index])) {
            String[] newArgs = Arrays.copyOf(args, args.length);
            newArgs[index] = newArg;
            enumerations.add(newArgs);
        }

        if (enumerations.size() > MAX_ALLOWED_WILDCARD_ENUMERATIONS) {
            sender.sendMessage(tooManyWildcardEnumerationsMessage);
            return;
        } else if (enumerations.isEmpty()) {
            sender.sendMessage(noWildcardEnumerationsMessage.replace("{arg}", args[index]));
            return;
        }

        // Call all commands
        for (String[] arr : enumerations) {
            subCommand.perform(sender, arr);
        }
    }

    /**
     * Attempts to expand the provided command according to the following rules:
     * <ul>
     *     <li><code>{a,b,c}</code> will enumerate the comma-separated list.</li>
     *     <li><code>{a..z}</code> will enumerate through a list of characters (letters or integers)</li>
     * </ul>
     * @since 1.1.6
     */
    private void runExpandedCommand(SubCommand subCommand, @NotNull CommandSender sender, String[] args) {
        CommandExpansion expansion = ExpansionUtils.buildExpansion(args);
        if (expansion == null) {
            subCommand.perform(sender, args);
            return;
        }

        if (expansion.validate(sender)) {
            for (String[] expansionArgs : expansion.build()) {
                subCommand.perform(sender, expansionArgs);
            }
        }
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
            SubCommand subCommand = matchCommand(args[0]);
            if (subCommand != null) {
                return subCommand.getSubcommandArguments(sender, args);
            }
        }

        return null;
    }

    /**
     * @return The list of subcommands
     */
    public ArrayList<SubCommand> getSubcommands() {
        return new ArrayList<>(subcommandStrings.stream().map(s -> subCommandMap.get(s.toLowerCase())).toList());
    }

    /**
     * @return The number of registered subcommands
     * @since 1.1.7
     */
    public int size() {
        return subCommandMap.size();
    }
}

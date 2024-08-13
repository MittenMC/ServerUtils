package com.github.mittenmc.serverutils.command;

import com.github.mittenmc.serverutils.command.expansion.CommandExpansion;
import com.github.mittenmc.serverutils.command.expansion.ExpansionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A powerful command which enables pattern matching for *all* commands.
 * This will run a list of commands starting from the first argument after performing any expansions.
 * @see com.github.mittenmc.serverutils.command.expansion.ArgumentExpansion
 * @author GavvyDizzle
 * @version 1.1.7
 * @since 1.1.6
 */
public class ExecCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        label = label.toLowerCase();
        boolean execp = label.equals("execp");

        if (args.length <= 1) {
            if (execp) {
                sender.sendMessage(label + " <player> <cmd> <args>");
            } else {
                sender.sendMessage(label + " <cmd> <args>");
            }
            return true;
        }

        // If the execc alias is used, pass future commands off to the console
        // If execp is used, parse the player from the first argument
        CommandSender expansionSender = label.equals("execc") ? Bukkit.getConsoleSender() : sender;

        if (execp) {
            if (args.length == 2) {
                sender.sendMessage("execp <player> <cmd> <args>");
            }

            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Invalid player: " + args[0]);
                return true;
            }

            expansionSender = player;
        }


        String[] execArgs = execp ? Arrays.copyOfRange(args, 1, args.length) : args;
        if (execArgs[0].startsWith("/")) execArgs[0] = execArgs[0].substring(1);

        CommandExpansion expansion = ExpansionUtils.buildExpansion(execArgs);
        if (expansion == null) {
            Bukkit.dispatchCommand(expansionSender, String.join(" ", execArgs));
        }
        else {
            if (expansion.validate(sender)) {
                for (String[] arr : expansion.build()) {
                    Bukkit.dispatchCommand(expansionSender, String.join(" ", arr));
                }
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return Collections.emptyList();

        String execArgs;

        if (label.equalsIgnoreCase("execp")) {
            if (args.length == 1) return null;

            execArgs = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        } else {
            execArgs = String.join(" ", args);
        }

        if (execArgs.startsWith("/")) execArgs = execArgs.substring(1);

        // Start matching once one character is present
        if (!execArgs.isEmpty()) {
            return Bukkit.getServer().getCommandMap().tabComplete(sender, execArgs);
        }
        return Collections.emptyList();
    }
}

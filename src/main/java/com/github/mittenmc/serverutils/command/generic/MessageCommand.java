package com.github.mittenmc.serverutils.command.generic;

import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.CommandManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageCommand extends SubCommand {

    public MessageCommand(CommandManager commandManager) {
        setName("msg");
        setDescription("Send a message to one or all players. Use \\n for line breaks");
        setSyntax("/" + commandManager.getCommandDisplayName() + " msg <player> <message>");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(commandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        String message = String.join(" ", List.of(args).subList(2, args.length));
        // Color string after line breaks to keep their coloring independent
        List<String> arr = Arrays.stream(message.split("\\\\n")).map(Colors::conv).toList();

        if (args[1].equals("*")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (String s : arr) {
                    player.sendMessage(s);
                }
            }
        } else {
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Invalid player: " + args[1]);
                return;
            }

            for (String s : arr) {
                player.sendMessage(s);
            }
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        if (args.length == 2) return null;
        return Collections.emptyList();
    }
}
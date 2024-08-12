package com.github.mittenmc.serverutils.command.generic;

import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.CommandManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TitleCommand extends SubCommand {

    private static final String splitterRegex = "::|\\n|\\\\n";
    private static final int fadeIn = 10;
    private static final int stay = 70;
    private static final int fadeOut = 20;

    public TitleCommand(CommandManager commandManager) {
        setName("title");
        setDescription("Send a title to one or all players. Split lines with :: or \\n");
        setSyntax("/" + commandManager.getCommandDisplayName() + " title <player> <title> [subtitle]");
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
        String[] arr = message.split(splitterRegex);

        String title = arr.length >= 1 ? Colors.conv(arr[0]) : null;
        String subTitle = arr.length >= 2 ? Colors.conv(arr[1]) : null;

        if (args[1].equals("*")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
            }
        } else {
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Invalid player: " + args[1]);
                return;
            }
            player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        if (args.length == 2) return null;
        return Collections.emptyList();
    }
}
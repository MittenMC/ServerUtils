package com.github.mittenmc.serverutils.command.generic;

import com.github.mittenmc.serverutils.CommandManager;
import com.github.mittenmc.serverutils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SoundCommand extends SubCommand {

    private final List<String> soundArgs = Arrays.stream(Sound.values()).map(Enum::name).toList();

    public SoundCommand(CommandManager commandManager) {
        setName("sound");
        setDescription("Play a sound for one or all players");
        setSyntax("/" + commandManager.getCommandDisplayName() + " sound <player> <sound> [volume] [pitch]");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(commandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(getColoredSyntax());
            return;
        }

        Sound sound;
        try {
            sound = Sound.valueOf(args[2]);
        } catch (Exception ignored) {
            sender.sendMessage(ChatColor.RED + "Invalid sound: " + args[2]);
            return;
        }

        float volume = 1f;
        float pitch = 1f;

        if (args.length >= 4) {
            try {
                volume = Float.parseFloat(args[3]);
            } catch (Exception ignored) {
                sender.sendMessage(ChatColor.RED + "Invalid volume: " + args[3]);
                return;
            }
        }

        if (args.length >= 5) {
            try {
                pitch = Float.parseFloat(args[4]);
            } catch (Exception ignored) {
                sender.sendMessage(ChatColor.RED + "Invalid pitch: " + args[4]);
                return;
            }
        }

        if (args[1].equals("*")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player, sound, volume, pitch);
            }
        } else {
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Invalid player: " + args[1]);
                return;
            }
            player.playSound(player, sound, volume, pitch);
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return null;
        }
        else if (args.length == 3) {
            List<String> arr = new ArrayList<>();
            StringUtil.copyPartialMatches(args[2], soundArgs, arr);
            return arr;
        }
        return Collections.emptyList();
    }
}
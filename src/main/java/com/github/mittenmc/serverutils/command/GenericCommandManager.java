package com.github.mittenmc.serverutils.command;

import com.github.mittenmc.serverutils.CommandManager;
import com.github.mittenmc.serverutils.command.generic.MessageCommand;
import com.github.mittenmc.serverutils.command.generic.SoundCommand;
import com.github.mittenmc.serverutils.command.generic.TitleCommand;
import org.bukkit.command.PluginCommand;

/**
 * Supports various utility commands for this plugin
 * @author GavvyDizzle
 * @version 1.1.7
 * @since 1.1.6
 */
public class GenericCommandManager extends CommandManager {

    public GenericCommandManager(PluginCommand command) {
        super(command);

        registerCommand(new HelpCommand.HelpCommandBuilder(this)
                .commandsPerPage(2)
                .lineSpacerFormatting("&b&m")
                .targetWidth(50)
                .build());
        registerCommand(new MessageCommand(this));
        registerCommand(new SoundCommand(this));
        registerCommand(new TitleCommand(this));
    }
}

package com.github.mittenmc.serverutils.command;

import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.CommandManager;
import com.github.mittenmc.serverutils.Numbers;
import com.github.mittenmc.serverutils.SubCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A generic help command to use with a {@link CommandManager}.
 * The default formatting can be modified when creating a {@link HelpCommandBuilder}.
 * Changing the format of individual command output requires overriding the {@link HelpCommand#getSubcommandFormatting(SubCommand)},
 * method which allows for injection of new components.
 * @see CommandManager
 * @see HelpCommandBuilder
 * @author GavvyDizzle
 * @version 1.1.7
 * @since 1.1.7
 */
@SuppressWarnings("unused")
public class HelpCommand extends SubCommand {

    public static class HelpCommandBuilder {
        //required
        private final CommandManager commandManager;

        //optional
        private String commandTitle;
        private int commandsPerPage = 8;
        private int targetWidth = 40;
        private char lineSpacer = '-';
        private String lineSpacerFormatting = Colors.conv("&e&m");
        private String helpTitle = Colors.conv("&r &6({name} Help)&r ");
        private String pageTitle = Colors.conv("&r &ePage &6{curr} of {max}&r ");
        private String pageDown = Colors.conv("&r &6<<<");
        private String pageUp = Colors.conv("&6>>>&r ");

        public HelpCommandBuilder(CommandManager commandManager) {
            this.commandManager = commandManager;
            commandTitle = commandManager.getCommand().getPlugin().getName();
        }

        /**
         * Update the title of this command/plugin in the command's first line
         * @param title The new title
         * @return The updated Builder for chaining
         */
        public HelpCommandBuilder commandTitle(String title) {
            commandTitle = title;
            return this;
        }

        /**
         * @param size The number of commands to show per page
         * @return The updated Builder for chaining
         */
        public HelpCommandBuilder commandsPerPage(int size) {
            commandsPerPage = size;
            return this;
        }

        /**
         * @param width The header and footer line width
         * @return The updated Builder for chaining
         */
        public HelpCommandBuilder targetWidth(int width) {
            targetWidth = Math.max(10, width);
            return this;
        }

        /**
         * @param spacer The char to use for empty space in the header and footer
         * @return The updated Builder for chaining
         */
        public HelpCommandBuilder lineSpacer(char spacer) {
            lineSpacer = spacer;
            return this;
        }

        /**
         * @param format The new formatting to use on spacers
         * @return The updated Builder for chaining
         */
        public HelpCommandBuilder lineSpacerFormatting(String format) {
            this.lineSpacerFormatting = Colors.conv(format.trim());
            return this;
        }

        /**
         * The title line will parse <code>{name}</code> as the commandTitle.
         * @param text The updated title text
         * @return The updated Builder for chaining
         * @see HelpCommandBuilder#commandTitle(String) 
         */
        public HelpCommandBuilder helpTitle(String text) {
            text = text.strip();
            if (!text.startsWith("&r ")) text = "&r " + text;
            if (!text.endsWith("&r ")) text += "&r ";

            this.helpTitle = Colors.conv(text);
            return this;
        }

        /**
         * The bottom line will parse <code>{curr}</code> and <code>{max}</code> for the current and max page respectively.
         * @param text The updated page info text
         * @return The updated Builder for chaining
         */
        public HelpCommandBuilder pageTitle(String text) {
            text = text.strip();
            if (!text.startsWith("&r ")) text = "&r " + text;
            if (!text.endsWith("&r ")) text += "&r ";

            this.pageTitle = Colors.conv(text);
            return this;
        }

        /**
         * The text that shows when a page down action is available.
         * This text will be injected with hoverable and clickable components.
         * @param text The updated text
         * @return The updated Builder for chaining
         */
        public HelpCommandBuilder pageDown(String text) {
            text = text.strip();
            if (!text.startsWith("&r ")) text = "&r " + text;

            this.pageDown = Colors.conv(text);
            return this;
        }

        /**
         * The text that shows when a page up action is available.
         * This text will be injected with hoverable and clickable components.
         * @param text The updated text
         * @return The updated Builder for chaining
         */
        public HelpCommandBuilder pageUp(String text) {
            text = text.strip();
            if (!text.endsWith("&r ")) text += "&r ";

            this.pageUp = Colors.conv(text);
            return this;
        }

        public HelpCommand build() {
            return new HelpCommand(this);
        }
    }

    private final CommandManager commandManager;
    private final String commandTitle;
    private final int commandsPerPage;
    private final int targetWidth;
    private final char lineSpacer;
    private final String lineSpacerColor;
    private final String helpTitle;
    private final String pageTitle;
    private final String pageDown;
    private final String pageUp;

    private HelpCommand(HelpCommandBuilder builder) {
        this.commandManager = builder.commandManager;
        this.commandTitle = builder.commandTitle;
        this.commandsPerPage = builder.commandsPerPage;
        this.targetWidth = builder.targetWidth;
        this.lineSpacer = builder.lineSpacer;
        this.lineSpacerColor = builder.lineSpacerFormatting;
        this.helpTitle = builder.helpTitle;
        this.pageTitle = builder.pageTitle;
        this.pageDown = builder.pageDown;
        this.pageUp = builder.pageUp;

        setName("help");
        setDescription("Opens this help menu");
        setSyntax("/" + commandManager.getCommandDisplayName() + " help");
        setColoredSyntax(ChatColor.YELLOW + getSyntax());
        setPermission(commandManager.getPermissionPrefix() + getName().toLowerCase());
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Invalid page");
                return;
            }
        }
        page = Numbers.constrain(page, 1, getMaxPage());

        ArrayList<SubCommand> subCommands = commandManager.getSubcommands();

        sender.sendMessage(buildTopLine());
        for (int i = (page - 1) * commandsPerPage; i < Math.min(page * commandsPerPage, subCommands.size()); i++) {
            sender.sendMessage(getSubcommandFormatting(subCommands.get(i)));
        }

        // Send clickable component to player, normal text to console
        if (sender instanceof Player player) {
            player.sendMessage(buildPlayerBottomLine(player, page));
        } else {
            sender.sendMessage(buildConsoleBottomLine(page));
        }
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    /**
     * Defines how a subcommand written to the help command's output in chat.
     * @param subCommand The subcommand to format
     * @return This subcommand's chat output
     */
    public Component getSubcommandFormatting(SubCommand subCommand) {
        return Component.text(Colors.conv("&6" + subCommand.getSyntax() + " - " + "&e" + subCommand.getDescription()));
    }

    private String buildTopLine() {
        String text = helpTitle.replace("{name}", commandTitle);
        String textStripped = Colors.strip(text);

        // Ensure the line length is correct. Add one character to the second filler segment if necessary
        int fillerLength = (targetWidth - textStripped.length()) / 2;
        int totalLength = fillerLength * 2 + textStripped.length();
        String fillerTestStart = new String(new char[fillerLength]).replace("\0", String.valueOf(lineSpacer));
        String fillerTextEnd = totalLength < targetWidth ? fillerTestStart + lineSpacer : fillerTestStart;

        return Colors.conv(lineSpacerColor + fillerTestStart + text + lineSpacerColor + fillerTextEnd);
    }

    private String buildConsoleBottomLine(int page) {
        String text = pageTitle
                .replace("{curr}", String.valueOf(page))
                .replace("{max}", String.valueOf(getMaxPage()));
        String textStripped = Colors.strip(text);

        // Ensure the line length is correct. Add one character to the second filler segment if necessary
        int fillerLength = (targetWidth - textStripped.length()) / 2;
        int totalLength = fillerLength * 2 + textStripped.length();
        String fillerTestStart = new String(new char[fillerLength]).replace("\0", String.valueOf(lineSpacer));
        String fillerTextEnd = totalLength < targetWidth ? fillerTestStart + lineSpacer : fillerTestStart;

        return Colors.conv(lineSpacerColor + fillerTestStart + text + lineSpacerColor + fillerTextEnd);
    }

    private Component buildPlayerBottomLine(Player player, int page) {
        boolean onMinPage = page == 1;
        boolean onMaxPage = page == getMaxPage();

        String text = pageTitle
                .replace("{curr}", String.valueOf(page))
                .replace("{max}", String.valueOf(getMaxPage()));
        String textStripped = Colors.strip(text);

        int directionalLength = 0;
        if (!onMinPage) directionalLength += Colors.strip(pageDown).length();
        if (!onMaxPage) directionalLength += Colors.strip(pageUp).length();

        // Ensure the line length is correct. Add one character to the second filler segment if necessary
        int fillerLength = (targetWidth - textStripped.length() - directionalLength) / 2;
        int totalLength = fillerLength * 2 + textStripped.length();
        String fillerTestStart = new String(new char[fillerLength]).replace("\0", String.valueOf(lineSpacer));
        String fillerTextEnd = totalLength < targetWidth ? fillerTestStart + lineSpacer : fillerTestStart;

        return Component.text(lineSpacerColor + fillerTestStart)
                .append(Component.text(onMinPage ? "" : pageDown)
                .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text("Previous")))
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, buildCommand(player, page-1))))
                .append(Component.text(text))
                .append(Component.text(onMaxPage ? "" : pageUp)
                .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(Component.text("Next")))
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, buildCommand(player, page+1))))
                .append(Component.text(lineSpacerColor + fillerTextEnd));
    }

    private String buildCommand(Player player, int page) {
        return "/execp " + player.getName() + " " + commandManager.getCommand().getName() + " " + getName() + " " + page;
    }

    private int getMaxPage() {
        return Math.max(1, ((commandManager.size()-1) / commandsPerPage) + 1);
    }
}
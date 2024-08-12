package com.github.mittenmc.serverutils.command.expansion;

import com.github.mittenmc.serverutils.Colors;
import com.github.mittenmc.serverutils.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages command expansions for specified subcommands
 * @see CommandManager
 * @see com.github.mittenmc.serverutils.SubCommand
 * @see ArgumentExpansion
 */
public class CommandExpansion {

    // Error messages
    private static final String UNMATCHED_EXPANSION_LENGTHS = Colors.conv("&cPlease ensure all expansions are of equal length ({a} != {b})");
    private static final String TOO_MANY_ENUMERATIONS = Colors.conv("&cToo many attempted enumerations. The maximum amount is " + CommandManager.MAX_ALLOWED_ENUMERATIONS + "!");

    private final List<ArgumentExpansion> expansions;
    private final int size;

    public CommandExpansion(List<ArgumentExpansion> expansions) {
        this.expansions = expansions;

        int temp = 0;
        for (ArgumentExpansion expansion : expansions) {
            temp = Math.max(temp, expansion.size());
        }
        this.size = temp;
    }

    public boolean doesNotContainExpansion() {
        for (ArgumentExpansion expansion : expansions) {
            if (!(expansion instanceof DummyExpansion)) return false;
        }
        return true;
    }

    /**
     * Determines if this entire command expansion is valid.
     * If invalid, an error message will be printed here.
     * @param sender Who to send messages to
     * @return If this is safe to run
     */
    public boolean validate(CommandSender sender) {
        return validateSize(sender) && validateExpansionLengths(sender);
    }

    /**
     * Determines if the number of total enumerations is small enough
     * @param sender Who to send messages to
     * @return If this is safe to run
     */
    private boolean validateSize(CommandSender sender) {
        if (size > CommandManager.MAX_ALLOWED_ENUMERATIONS) {
            sender.sendMessage(TOO_MANY_ENUMERATIONS);
            return false;
        }
        return true;
    }

    /**
     * Determines if the lengths of multiple enumerations are equal.
     * If multiple are present, they must all have the same length.
     * @param sender Who to send messages to
     * @return If this is safe to run
     */
    private boolean validateExpansionLengths(CommandSender sender) {
        for (ArgumentExpansion expansion : expansions) {
            if (expansion.size() == -1) continue;

            if (expansion.size() != size) {
                sender.sendMessage(UNMATCHED_EXPANSION_LENGTHS.replace("{a}", String.valueOf(expansion.size())).replace("{b}", String.valueOf(size)));
                return false;
            }
        }
        return true;
    }

    /**
     * Generates an ordered list of commands to run with all expansions evaluated.
     * @return A list of commands to run
     */
    public List<String[]> build() {
        List<String[]> arr = new ArrayList<>(size);

        // The list of Expansions passed in retain their spaces. Merge the string then split on spaces to make the args array
        for (int i = 0; i < size; i++) {
            StringBuilder builder = new StringBuilder();
            for (ArgumentExpansion expansion : expansions) {
                builder.append(expansion.get(i));
            }
            arr.add(builder.toString().split(" "));
        }
        return arr;
    }
}

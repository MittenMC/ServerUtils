package com.github.mittenmc.serverutils.command.expansion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class which handles logic for command-based pattern matching
 * @see CommandExpansion
 * @version 1.1.6
 * @since 1.1.6
 */
public class ExpansionUtils {

    // This is used to match any of the three types of patterns below
    // If these patterns are altered, then this regex needs to be updated to match it
    private static final Pattern patternPattern = Pattern.compile("\\{([^{}]*|\\{[^{}]*})*}");

    private static final Pattern listPattern = Pattern.compile("\\{([^}]+)}");
    private static final Pattern characterRangePattern = Pattern.compile("\\{([a-z])\\.\\.([a-z])}|\\{([A-Z])\\.\\.([A-Z])}");
    private static final Pattern digitsRangePattern = Pattern.compile("\\{(\\d+)\\.\\.(\\d+)}");

    /**
     * Builds a new CommandExpansion from the command arguments
     * @param args The original command arguments
     * @return The CommandExpansion or null if no patterns were found
     * @since 1.1.6
     */
    @Nullable
    public static CommandExpansion buildExpansion(String[] args) {
        String fullCommand = String.join(" ", args);
        List<ArgumentExpansion> expansions = new ArrayList<>(args.length);
        int lastEnd = 0;

        Matcher matcher = patternPattern.matcher(fullCommand);
        while (matcher.find()) {
            // Add the text before the current match (unmatched portion)
            if (matcher.start() > lastEnd) {
                expansions.add(new DummyExpansion(fullCommand.substring(lastEnd, matcher.start())));
            }

            // Add the matched pattern
            expansions.add(parseExpansion(matcher.group()));

            // Update the last end position
            lastEnd = matcher.end();
        }

        // Add the remaining text after the last match (if any)
        if (lastEnd < fullCommand.length()) {
            expansions.add(new DummyExpansion(fullCommand.substring(lastEnd)));
        }

        CommandExpansion expansion = new CommandExpansion(expansions);
        if (expansion.doesNotContainExpansion()) {
            return null;
        } else {
            return expansion;
        }
    }

    @NotNull
    private static ArgumentExpansion parseExpansion(String arg) {
        // Match {a..z} pattern
        Matcher characterRangeMatcher = characterRangePattern.matcher(arg);
        if (characterRangeMatcher.find()) {
            // Since there are four groups in the pattern, determine which ones to use here
            int firstGroup = characterRangeMatcher.group(1) != null ? 1 : 3;
            return new CharacterExpansion(
                    arg.substring(0, characterRangeMatcher.start()),
                    arg.substring(characterRangeMatcher.end()),
                    characterRangeMatcher.group(firstGroup).charAt(0),
                    characterRangeMatcher.group(firstGroup+1).charAt(0)
            );
        }

        // Match {#..#} (number to number) pattern
        Matcher numberRangeMatcher = digitsRangePattern.matcher(arg);
        if (numberRangeMatcher.find()) {
            return new IntegerExpansion(
                    arg.substring(0, numberRangeMatcher.start()),
                    arg.substring(numberRangeMatcher.end()),
                    Integer.parseInt(numberRangeMatcher.group(1)),
                    Integer.parseInt(numberRangeMatcher.group(2))
            );
        }

        // This MUST follow the {a..z} pattern because it will match that as a single element list!
        // Match {a,b,c} pattern
        Matcher listMatcher = listPattern.matcher(arg);
        if (listMatcher.find()) {
            List<String> matches = Arrays.asList(listMatcher.group(1).split(","));
            if (matches.size() > 1) {
                return new ListExpansion(
                        arg.substring(0, listMatcher.start()),
                        arg.substring(listMatcher.end()),
                        matches
                );
            }
        }

        // Nothing matched, add dummy string
        return new DummyExpansion(arg);
    }
}

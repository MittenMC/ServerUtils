package com.github.mittenmc.serverutils.command;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Enables wildcard support and defines how the wildcard is expanded
 * @version 1.1.6
 * @since 1.1.6
 */
public interface WildcardCommand {

    /**
     *
     * @param index The index from the args array
     * @param args The command's arguments
     * @return A list of all possible values for this
     * @since 1.1.6
     */
    Collection<String> getWildcardValues(int index, String[] args);

    /**
     * Filters a list of strings against regex with a wildcard character.
     * @param originals The list of strings to match against
     * @param wildcardToken A regex token with a *
     * @return A sublist of originals that matches the wildcardToken regex
     * @since 1.1.6
     */
    static Collection<String> filter(Collection<String> originals, String wildcardToken) {
        String regex = wildcardToken.replace("*", ".*");

        return originals.stream()
                .filter(s -> s.matches(regex))
                .collect(Collectors.toList());
    }

}

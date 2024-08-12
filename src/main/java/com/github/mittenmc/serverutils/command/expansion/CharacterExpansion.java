package com.github.mittenmc.serverutils.command.expansion;

import org.jetbrains.annotations.Nullable;

public class CharacterExpansion extends ArgumentExpansion {

    private final char first, second;

    public CharacterExpansion(String argumentPrefix, String argumentSuffix, char first, char second) {
        super(argumentPrefix, argumentSuffix);
        this.first = first;
        this.second = second;
    }

    @Override
    @Nullable String get(int index) {
        int step = (first <= second) ? 1 : -1;
        int nthChar = first + step * index;

        if ((step == 1 && nthChar <= second) || (step == -1 && nthChar >= second)) {
            return getArgumentPrefix() + ((char) (nthChar)) + getArgumentSuffix();
        } else {
            return null;
        }
    }

    @Override
    int size() {
        return Math.abs(first - second) + 1;
    }
}

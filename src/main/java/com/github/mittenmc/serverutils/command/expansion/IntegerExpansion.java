package com.github.mittenmc.serverutils.command.expansion;

import org.jetbrains.annotations.Nullable;

public class IntegerExpansion extends ArgumentExpansion {

    private final int first, second;

    public IntegerExpansion(String argumentPrefix, String argumentSuffix, int first, int second) {
        super(argumentPrefix, argumentSuffix);
        this.first = first;
        this.second = second;
    }

    @Override
    @Nullable String get(int index) {
        int step = (first <= second) ? 1 : -1;
        int nthNumber = first + step * index;

        if ((step == 1 && nthNumber <= second) || (step == -1 && nthNumber >= second)) {
            return getArgumentPrefix() + nthNumber + getArgumentSuffix();
        } else {
            return null;
        }
    }

    @Override
    int size() {
        return Math.abs(first - second) + 1;
    }
}

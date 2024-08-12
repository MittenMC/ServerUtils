package com.github.mittenmc.serverutils.command.expansion;

import org.jetbrains.annotations.Nullable;

public class DummyExpansion extends ArgumentExpansion {

    private final String fullArgument;

    public DummyExpansion(String fullArgument) {
        super("", "");
        this.fullArgument = fullArgument;
    }

    @Override
    @Nullable String get(int index) {
        return fullArgument;
    }

    @Override
    int size() {
        return -1;
    }
}

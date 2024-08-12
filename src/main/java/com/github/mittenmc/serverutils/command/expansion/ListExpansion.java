package com.github.mittenmc.serverutils.command.expansion;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListExpansion extends ArgumentExpansion {

    private final List<String> elements;

    public ListExpansion(String argumentPrefix, String argumentSuffix, List<String> elements) {
        super(argumentPrefix, argumentSuffix);
        this.elements = elements;
    }

    @Override
    @Nullable String get(int index) {
        if (index < 0 || index >= elements.size()) return null;
        return getArgumentPrefix() + elements.get(index) + getArgumentSuffix();
    }

    @Override
    int size() {
        return elements.size();
    }
}

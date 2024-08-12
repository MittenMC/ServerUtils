package com.github.mittenmc.serverutils.command.expansion;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class ArgumentExpansion {

    private final String argumentPrefix, argumentSuffix;

    protected ArgumentExpansion(String argumentPrefix, String argumentSuffix) {
        this.argumentPrefix = argumentPrefix;
        this.argumentSuffix = argumentSuffix;
    }

    /**
     * Gets the value of this Expansion for this iteration
     * @param index The iteration index
     * @return The value or null if out of bounds
     */
    @Nullable
    abstract String get(int index);

    /**
     * Gets the number of enumerations present or -1
     * if it does not support any
     * @return The size of this expansion or -1
     */
    abstract int size();
}

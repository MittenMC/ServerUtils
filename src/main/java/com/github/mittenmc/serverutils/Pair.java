package com.github.mittenmc.serverutils;

import java.util.Objects;

/**
 * A generic Pair class
 *
 * @author GavvyDizzle
 * @version 1.0.4
 * @since 1.0.5
 */
public final class Pair<A, B> {

    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public void setFirst(A a) {
        this.first = a;
    }

    public void setSecond(B b) {
        this.second = b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair[" +
                "first=" + first + ", " +
                "second=" + second + ']';
    }

}

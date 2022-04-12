package com.github.mittenmc.serverutils;

import org.bukkit.Bukkit;

public class RandomValuePair {

    private final int min, max;

    /**
     * Creates a new RandomValuePair
     *
     * @param min The lower value
     * @param max The upper value
     */
    public RandomValuePair(int min, int max) {
        if (min < max) {
            this.min = min;
            this.max = max;
        }
        else {
            this.min = max;
            this.max = min;
        }
    }

    /**
     * @return A random integer, inclusive of both min and max
     */
    public int getRandomInt() {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    /**
     * @return A random double, inclusive of both min and max
     */
    public double getRandomDouble() {
        return min + Math.random() * ((max - min) + 1);
    }

    /**
     * Creates a new RandomValuePair given a string representation.
     *
     * @param value The string representation e.g. "5 10"
     * @return A new RandomValuePair given the value. If an error is encountered, a RandomValuePair of "0 0" will be created.
     */
    public static RandomValuePair getValuePair(String value) {
        try {
            String[] arr = value.split(" ");
            return new RandomValuePair(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        } catch (Exception e) {
            Bukkit.getLogger().warning("The range '" + value + "' is invalid. Setting it to '0 0'!");
            return new RandomValuePair(0, 0);
        }
    }

    /**
     * Creates a new RandomValuePair given a string representation.
     *
     * @param path The config path for troubleshooting
     * @param value The string representation e.g. "5 10"
     * @return A new RandomValuePair given the value. If an error is encountered, a RandomValuePair of "0 0" will be created.
     */
    public static RandomValuePair getValuePair(String path, String value) {
        try {
            String[] arr = value.split(" ");
            return new RandomValuePair(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        } catch (Exception e) {
            Bukkit.getLogger().warning("The range '" + value + "' could not be loaded from " + path + ". Setting it to '0 0'!");
            return new RandomValuePair(0, 0);
        }
    }

    @Override
    public String toString() {
        return "Min(" + min + "), Max(" + max + ")";
    }

}

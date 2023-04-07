package com.github.mittenmc.serverutils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.List;

/**
 * Contains methods to color and un-color text.
 * Uses IridiumColorAPI when converting text to text with color.
 * @see <a href="https://github.com/Iridium-Development/IridiumColorAPI">IridiumColorAPI</a>
 * @author GavvyDizzle
 * @version 1.0.2
 * @since 1.0
 */
public class Colors {

    /**
     * Uses IridiumColorAPI to convert colors
     *
     * @param str The string to convert
     * @return A string with colors added
     * @since 1.0
     */
    public static String conv(String str) {
        return IridiumColorAPI.process(str);
    }

    /**
     * Uses IridiumColorAPI to convert colors
     *
     * @param list The list to convert
     * @return A list with colors added to all lines
     * @since 1.0
     */
    public static List<String> conv(List<String> list) {
        return IridiumColorAPI.process(list);
    }


    /**
     * Removes all color modifications to a string
     *
     * @param msg The message to strip
     * @return The stripped message
     * @since 1.0
     */
    public static String strip(String msg) {
        return ChatColor.stripColor(msg);
    }

    /**
     * Converts a String in hexadecimal form to an RGB color.
     * This adds a leading '#' to the string, so you do not need to provide one.
     * @param hex The hex string
     * @return The Color or null if the string is invalid
     * @since 1.0.2
     */
    public static Color getColor(String hex) {
        if (!hex.startsWith("#")) hex = "#" + hex;

        if (hex.length() != 7) return null;

        try {
            java.awt.Color color = java.awt.Color.decode(hex);
            return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Converts an rbg color to a hexadecimal
     * @param color The color
     * @return The hex code with capital letters
     */
    public static String getHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

}

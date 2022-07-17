package com.github.mittenmc.serverutils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * Contains methods to color and un-color text.
 * Uses IridiumColorAPI when converting text to text with color.
 * See {@link <a href="https://github.com/Iridium-Development/IridiumColorAPI">...</a>}
 * @author GavvyDizzle
 * @version 1.0
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

}

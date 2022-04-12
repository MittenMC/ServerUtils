package com.mittenmc.serverutils;

import org.bukkit.ChatColor;

public class Colors {

    /**
     * Converts all valid color codes using &
     *
     * @param msg The message to convert
     * @return A message with colors added
     */
    public static String conv(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Removes all color modifications to a string
     *
     * @param msg The message to strip
     * @return The stripped message
     */
    public static String strip(String msg) {
        return ChatColor.stripColor(msg);
    }

}

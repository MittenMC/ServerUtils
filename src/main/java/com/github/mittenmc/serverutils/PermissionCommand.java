package com.github.mittenmc.serverutils;

/**
 * Command interface which requires the command to have a permission
 * @author GavvyDizzle
 * @version 1.0.1
 * @since 1.0.1
 */
public interface PermissionCommand {

    abstract String getPermission();
}
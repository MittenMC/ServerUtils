package com.github.mittenmc.serverutils;

/**
 * Command interface which requires the command to have a permission
 * @author GavvyDizzle
 * @version 1.0.1
 * @since 1.0.1
 * @deprecated As of release 1.0.3, permissions were merged into the SubCommand class.
 */
public interface PermissionCommand {

    @Deprecated
    String getPermission();
}

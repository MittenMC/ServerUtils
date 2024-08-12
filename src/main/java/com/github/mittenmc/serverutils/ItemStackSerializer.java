/*
  Copyright (c) 2013 Exo-Network
  This software is provided 'as-is', without any express or implied
  warranty. In no event will the authors be held liable for any damages
  arising from the use of this software.
  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:
     1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
     2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
     3. This notice may not be removed or altered from any source
     distribution.
  manf                   info@manf.tk
 */

package com.github.mittenmc.serverutils;

import lombok.Cleanup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Contains methods for converting ItemStacks to and from a byte[] array for persistent item storage.
 * @author Exo-Network, GavvyDizzle
 * @version 1.0.2
 * @since 1.0
 */
public class ItemStackSerializer {

    /**
     * Converts an ItemStack to a byte array
     * @param item The ItemStack
     * @return The byte array representing this item
     * @throws RuntimeException Thrown when serialization fails
     * @since 1.0
     */
    public static byte[] serializeItemStack(ItemStack item) throws RuntimeException {
        try {
            @Cleanup ByteArrayOutputStream os = new ByteArrayOutputStream();
            @Cleanup BukkitObjectOutputStream bos = new BukkitObjectOutputStream(os);
            bos.writeObject(item);
            return os.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Converts an array of ItemStacks to a byte array
     * @param items The ItemStack list
     * @return The byte array representing the array consisting of the size then the items, or null if the input is null
     * @throws RuntimeException Thrown when serialization fails
     * @since 1.0.2
     */
    public static byte[] serializeItemStackArray(@Nullable ItemStack[] items) throws RuntimeException {
        if (items == null) return null;

        try {
            @Cleanup ByteArrayOutputStream os = new ByteArrayOutputStream();
            @Cleanup BukkitObjectOutputStream bos = new BukkitObjectOutputStream(os);

            // Write the size of the list
            bos.writeInt(items.length);

            // Save every element in the list
            for (ItemStack itemStack : items) {
                bos.writeObject(itemStack);
            }

            return os.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Converts a byte array to an ItemStack.
     * @param b The byte array
     * @return The ItemStack representing this byte array
     * @throws RuntimeException Thrown when serialization fails
     * @since 1.0
     */
    public static ItemStack deserializeItemStack(byte[] b) throws RuntimeException {
        try {
            @Cleanup ByteArrayInputStream bais = new ByteArrayInputStream(b);
            @Cleanup BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
            return (ItemStack) bois.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(new String(b), ex);
        }
    }

    /**
     * Converts a byte array to an array of ItemStacks.
     * @param b The byte array consisting of the size then the items
     * @return The ItemStack array representing this byte array, or null if the input is null
     * @throws RuntimeException Thrown when serialization fails
     * @since 1.0.2
     */
    @Nullable
    public static ItemStack[] deserializeItemStackArray(byte[] b) throws RuntimeException {
        if (b == null) return null;

        try {
            @Cleanup ByteArrayInputStream bais = new ByteArrayInputStream(b);
            @Cleanup BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);

            int size = bois.readInt();
            ItemStack[] items = new ItemStack[size];

            // Read the serialized items
            for (int i = 0; i < size; i++) {
                items[i] = (ItemStack) bois.readObject();
            }

            return items;
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(new String(b), ex);
        }
    }
}
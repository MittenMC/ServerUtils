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
import org.apache.commons.lang.SerializationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ItemStackSerializer {

    public static byte[] serializeItemStack(ItemStack item) throws SerializationException {
        try {
            @Cleanup ByteArrayOutputStream os = new ByteArrayOutputStream();
            @Cleanup BukkitObjectOutputStream bos = new BukkitObjectOutputStream(os);
            bos.writeObject(item);
            return os.toByteArray();
        } catch (IOException ex) {
            throw new SerializationException(ex);
        }
    }

    public static ItemStack deserializeItemStack(byte[] b) throws SerializationException {
        try {
            @Cleanup ByteArrayInputStream bais = new ByteArrayInputStream(b);
            @Cleanup BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
            return (ItemStack) bois.readObject();
        } catch (Exception ex) {
            throw new SerializationException(new String(b), ex);
        }
    }
}
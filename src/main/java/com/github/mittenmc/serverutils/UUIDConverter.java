package com.github.mittenmc.serverutils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDConverter {

    /**
     * Converts a UUID to a byte array.
     *
     * @param uuid The uuid to convert.
     * @return The uuid as a byte[16].
     */
    public static byte[] convert(UUID uuid) {
        return ByteBuffer.wrap(new byte[16])
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits())
                .array();
    }

    /**
     * Converts a byte array (byte[16]) to a UUID.
     *
     * @param bytes The byte array to convert.
     * @return The byte array as a UUID.
     */
    public static UUID convert(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

}

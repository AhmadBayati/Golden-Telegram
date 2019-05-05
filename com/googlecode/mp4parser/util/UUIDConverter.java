package com.googlecode.mp4parser.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class UUIDConverter {
    public static UUID convert(byte[] bArr) {
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        wrap.order(ByteOrder.BIG_ENDIAN);
        return new UUID(wrap.getLong(), wrap.getLong());
    }

    public static byte[] convert(UUID uuid) {
        int i = 8;
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        byte[] bArr = new byte[16];
        for (int i2 = 0; i2 < 8; i2++) {
            bArr[i2] = (byte) ((int) (mostSignificantBits >>> ((7 - i2) * 8)));
        }
        while (i < 16) {
            bArr[i] = (byte) ((int) (leastSignificantBits >>> ((7 - i) * 8)));
            i++;
        }
        return bArr;
    }
}

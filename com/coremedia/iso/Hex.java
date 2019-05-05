package com.coremedia.iso;

import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor;
import java.io.ByteArrayOutputStream;

public class Hex {
    private static final char[] DIGITS;

    static {
        DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }

    public static byte[] decodeHex(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < str.length(); i += 2) {
            byteArrayOutputStream.write(Integer.parseInt(str.substring(i, i + 2), 16));
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String encodeHex(byte[] bArr) {
        return encodeHex(bArr, 0);
    }

    public static String encodeHex(byte[] bArr, int i) {
        int i2 = 0;
        int length = bArr.length;
        char[] cArr = new char[((i > 0 ? length / i : 0) + (length << 1))];
        int i3 = 0;
        while (i3 < length) {
            int i4;
            if (i <= 0 || i3 % i != 0 || i2 <= 0) {
                i4 = i2;
            } else {
                i4 = i2 + 1;
                cArr[i2] = '-';
            }
            int i5 = i4 + 1;
            cArr[i4] = DIGITS[(bArr[i3] & PsExtractor.VIDEO_STREAM_MASK) >>> 4];
            i2 = i5 + 1;
            cArr[i5] = DIGITS[bArr[i3] & 15];
            i3++;
        }
        return new String(cArr);
    }
}

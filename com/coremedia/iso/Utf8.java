package com.coremedia.iso;

import com.hanista.mobogram.messenger.exoplayer.C0700C;
import java.io.UnsupportedEncodingException;

public final class Utf8 {
    public static String convert(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        try {
            return new String(bArr, C0700C.UTF8_NAME);
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    public static byte[] convert(String str) {
        if (str == null) {
            return null;
        }
        try {
            return str.getBytes(C0700C.UTF8_NAME);
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    public static int utf8StringLengthInBytes(String str) {
        if (str == null) {
            return 0;
        }
        try {
            return str.getBytes(C0700C.UTF8_NAME).length;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }
}

package com.coremedia.iso;

import android.support.v4.view.MotionEventCompat;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.ActionBar.Theme;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public final class IsoTypeReader {
    public static int byte2int(byte b) {
        return b < null ? b + TLRPC.USER_FLAG_UNUSED2 : b;
    }

    public static String read4cc(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[4];
        byteBuffer.get(bArr);
        try {
            return new String(bArr, "ISO-8859-1");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static double readFixedPoint0230(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[4];
        byteBuffer.get(bArr);
        return ((double) ((bArr[3] & NalUnitUtil.EXTENDED_SAR) | (((((bArr[0] << 24) & Theme.MSG_TEXT_COLOR) | 0) | ((bArr[1] << 16) & 16711680)) | ((bArr[2] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)))) / 1.073741824E9d;
    }

    public static double readFixedPoint1616(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[4];
        byteBuffer.get(bArr);
        return ((double) ((bArr[3] & NalUnitUtil.EXTENDED_SAR) | (((((bArr[0] << 24) & Theme.MSG_TEXT_COLOR) | 0) | ((bArr[1] << 16) & 16711680)) | ((bArr[2] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)))) / 65536.0d;
    }

    public static float readFixedPoint88(ByteBuffer byteBuffer) {
        byte[] bArr = new byte[2];
        byteBuffer.get(bArr);
        return ((float) ((short) ((bArr[1] & NalUnitUtil.EXTENDED_SAR) | ((short) (((bArr[0] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | 0))))) / 256.0f;
    }

    public static String readIso639(ByteBuffer byteBuffer) {
        int readUInt16 = readUInt16(byteBuffer);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            stringBuilder.append((char) (((readUInt16 >> ((2 - i) * 5)) & 31) + 96));
        }
        return stringBuilder.toString();
    }

    public static String readString(ByteBuffer byteBuffer) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            byte b = byteBuffer.get();
            if (b == null) {
                return Utf8.convert(byteArrayOutputStream.toByteArray());
            }
            byteArrayOutputStream.write(b);
        }
    }

    public static String readString(ByteBuffer byteBuffer, int i) {
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr);
        return Utf8.convert(bArr);
    }

    public static int readUInt16(ByteBuffer byteBuffer) {
        return (0 + (byte2int(byteBuffer.get()) << 8)) + byte2int(byteBuffer.get());
    }

    public static int readUInt16BE(ByteBuffer byteBuffer) {
        return (0 + byte2int(byteBuffer.get())) + (byte2int(byteBuffer.get()) << 8);
    }

    public static int readUInt24(ByteBuffer byteBuffer) {
        return (0 + (readUInt16(byteBuffer) << 8)) + byte2int(byteBuffer.get());
    }

    public static long readUInt32(ByteBuffer byteBuffer) {
        long j = (long) byteBuffer.getInt();
        return j < 0 ? j + 4294967296L : j;
    }

    public static long readUInt32BE(ByteBuffer byteBuffer) {
        return (((long) readUInt8(byteBuffer)) << null) + ((((long) readUInt8(byteBuffer)) << 8) + ((((long) readUInt8(byteBuffer)) << 16) + (((long) readUInt8(byteBuffer)) << 24)));
    }

    public static long readUInt48(ByteBuffer byteBuffer) {
        long readUInt16 = ((long) readUInt16(byteBuffer)) << 32;
        if (readUInt16 >= 0) {
            return readUInt16 + readUInt32(byteBuffer);
        }
        throw new RuntimeException("I don't know how to deal with UInt64! long is not sufficient and I don't want to use BigInt");
    }

    public static long readUInt64(ByteBuffer byteBuffer) {
        long readUInt32 = (readUInt32(byteBuffer) << 32) + 0;
        if (readUInt32 >= 0) {
            return readUInt32 + readUInt32(byteBuffer);
        }
        throw new RuntimeException("I don't know how to deal with UInt64! long is not sufficient and I don't want to use BigInt");
    }

    public static int readUInt8(ByteBuffer byteBuffer) {
        return byte2int(byteBuffer.get());
    }
}

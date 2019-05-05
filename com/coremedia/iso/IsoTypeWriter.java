package com.coremedia.iso;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import java.nio.ByteBuffer;

public final class IsoTypeWriter {
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !IsoTypeWriter.class.desiredAssertionStatus();
    }

    public static void writeFixedPoint0230(ByteBuffer byteBuffer, double d) {
        int i = (int) (1.073741824E9d * d);
        byteBuffer.put((byte) ((Theme.MSG_TEXT_COLOR & i) >> 24));
        byteBuffer.put((byte) ((16711680 & i) >> 16));
        byteBuffer.put((byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & i) >> 8));
        byteBuffer.put((byte) (i & NalUnitUtil.EXTENDED_SAR));
    }

    public static void writeFixedPoint1616(ByteBuffer byteBuffer, double d) {
        int i = (int) (65536.0d * d);
        byteBuffer.put((byte) ((Theme.MSG_TEXT_COLOR & i) >> 24));
        byteBuffer.put((byte) ((16711680 & i) >> 16));
        byteBuffer.put((byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & i) >> 8));
        byteBuffer.put((byte) (i & NalUnitUtil.EXTENDED_SAR));
    }

    public static void writeFixedPoint88(ByteBuffer byteBuffer, double d) {
        short s = (short) ((int) (256.0d * d));
        byteBuffer.put((byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & s) >> 8));
        byteBuffer.put((byte) (s & NalUnitUtil.EXTENDED_SAR));
    }

    public static void writeIso639(ByteBuffer byteBuffer, String str) {
        int i = 0;
        if (str.getBytes().length != 3) {
            throw new IllegalArgumentException("\"" + str + "\" language string isn't exactly 3 characters long!");
        }
        int i2 = 0;
        while (i < 3) {
            i2 += (str.getBytes()[i] - 96) << ((2 - i) * 5);
            i++;
        }
        writeUInt16(byteBuffer, i2);
    }

    public static void writePascalUtfString(ByteBuffer byteBuffer, String str) {
        byte[] convert = Utf8.convert(str);
        if ($assertionsDisabled || convert.length < NalUnitUtil.EXTENDED_SAR) {
            writeUInt8(byteBuffer, convert.length);
            byteBuffer.put(convert);
            return;
        }
        throw new AssertionError();
    }

    public static void writeUInt16(ByteBuffer byteBuffer, int i) {
        int i2 = SupportMenu.USER_MASK & i;
        writeUInt8(byteBuffer, i2 >> 8);
        writeUInt8(byteBuffer, i2 & NalUnitUtil.EXTENDED_SAR);
    }

    public static void writeUInt16BE(ByteBuffer byteBuffer, int i) {
        int i2 = SupportMenu.USER_MASK & i;
        writeUInt8(byteBuffer, i2 & NalUnitUtil.EXTENDED_SAR);
        writeUInt8(byteBuffer, i2 >> 8);
    }

    public static void writeUInt24(ByteBuffer byteBuffer, int i) {
        int i2 = ViewCompat.MEASURED_SIZE_MASK & i;
        writeUInt16(byteBuffer, i2 >> 8);
        writeUInt8(byteBuffer, i2);
    }

    public static void writeUInt32(ByteBuffer byteBuffer, long j) {
        if ($assertionsDisabled || (j >= 0 && j <= 4294967296L)) {
            byteBuffer.putInt((int) j);
            return;
        }
        throw new AssertionError("The given long is not in the range of uint32 (" + j + ")");
    }

    public static void writeUInt32BE(ByteBuffer byteBuffer, long j) {
        if ($assertionsDisabled || (j >= 0 && j <= 4294967296L)) {
            writeUInt16BE(byteBuffer, ((int) j) & SupportMenu.USER_MASK);
            writeUInt16BE(byteBuffer, (int) ((j >> 16) & 65535));
            return;
        }
        throw new AssertionError("The given long is not in the range of uint32 (" + j + ")");
    }

    public static void writeUInt48(ByteBuffer byteBuffer, long j) {
        long j2 = 281474976710655L & j;
        writeUInt16(byteBuffer, (int) (j2 >> 32));
        writeUInt32(byteBuffer, j2 & 4294967295L);
    }

    public static void writeUInt64(ByteBuffer byteBuffer, long j) {
        if ($assertionsDisabled || j >= 0) {
            byteBuffer.putLong(j);
            return;
        }
        throw new AssertionError("The given long is negative");
    }

    public static void writeUInt8(ByteBuffer byteBuffer, int i) {
        byteBuffer.put((byte) (i & NalUnitUtil.EXTENDED_SAR));
    }

    public static void writeUtf8String(ByteBuffer byteBuffer, String str) {
        byteBuffer.put(Utf8.convert(str));
        writeUInt8(byteBuffer, 0);
    }

    public static void writeZeroTermUtf8String(ByteBuffer byteBuffer, String str) {
        byteBuffer.put(Utf8.convert(str));
        writeUInt8(byteBuffer, 0);
    }
}

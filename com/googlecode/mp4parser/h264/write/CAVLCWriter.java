package com.googlecode.mp4parser.h264.write;

import com.googlecode.mp4parser.h264.Debug;
import java.io.OutputStream;

public class CAVLCWriter extends BitstreamWriter {
    public CAVLCWriter(OutputStream outputStream) {
        super(outputStream);
    }

    public void writeBool(boolean z, String str) {
        Debug.print(new StringBuilder(String.valueOf(str)).append("\t").toString());
        write1Bit(z ? 1 : 0);
        Debug.println("\t" + z);
    }

    public void writeNBit(long j, int i, String str) {
        Debug.print(new StringBuilder(String.valueOf(str)).append("\t").toString());
        for (int i2 = 0; i2 < i; i2++) {
            write1Bit(((int) (j >> ((i - i2) - 1))) & 1);
        }
        Debug.println("\t" + j);
    }

    public void writeSE(int i, String str) {
        int i2 = 1;
        Debug.print(new StringBuilder(String.valueOf(str)).append("\t").toString());
        int i3 = (i < 0 ? -1 : 1) * (i << 1);
        if (i <= 0) {
            i2 = 0;
        }
        writeUE(i2 + i3);
        Debug.println("\t" + i);
    }

    public void writeSliceTrailingBits() {
        throw new IllegalStateException("todo");
    }

    public void writeTrailingBits() {
        write1Bit(1);
        writeRemainingZero();
        flush();
    }

    public void writeU(int i, int i2) {
        writeNBit((long) i, i2);
    }

    public void writeU(int i, int i2, String str) {
        Debug.print(new StringBuilder(String.valueOf(str)).append("\t").toString());
        writeNBit((long) i, i2);
        Debug.println("\t" + i);
    }

    public void writeUE(int i) {
        int i2 = 0;
        int i3 = 0;
        while (i2 < 15) {
            if (i < (1 << i2) + i3) {
                break;
            }
            i3 += 1 << i2;
            i2++;
        }
        i2 = 0;
        writeNBit(0, i2);
        write1Bit(1);
        writeNBit((long) (i - i3), i2);
    }

    public void writeUE(int i, String str) {
        Debug.print(new StringBuilder(String.valueOf(str)).append("\t").toString());
        writeUE(i);
        Debug.println("\t" + i);
    }
}

package com.googlecode.mp4parser.h264.read;

import com.googlecode.mp4parser.h264.CharCache;
import java.io.InputStream;

public class BitstreamReader {
    protected static int bitsRead;
    private int curByte;
    protected CharCache debugBits;
    private InputStream is;
    int nBit;
    private int nextByte;

    public BitstreamReader(InputStream inputStream) {
        this.debugBits = new CharCache(50);
        this.is = inputStream;
        this.curByte = inputStream.read();
        this.nextByte = inputStream.read();
    }

    private void advance() {
        this.curByte = this.nextByte;
        this.nextByte = this.is.read();
        this.nBit = 0;
    }

    public void close() {
    }

    public long getBitPosition() {
        return (long) ((bitsRead * 8) + (this.nBit % 8));
    }

    public int getCurBit() {
        return this.nBit;
    }

    public boolean isByteAligned() {
        return this.nBit % 8 == 0;
    }

    public boolean moreRBSPData() {
        if (this.nBit == 8) {
            advance();
        }
        int i = 1 << ((8 - this.nBit) - 1);
        return (this.curByte == -1 || (this.nextByte == -1 && ((((i << 1) + -1) & this.curByte) == i))) ? false : true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int peakNextBits(int r9) {
        /*
        r8 = this;
        r0 = -1;
        r7 = 8;
        r1 = 0;
        if (r9 <= r7) goto L_0x000f;
    L_0x0006:
        r0 = new java.lang.IllegalArgumentException;
        r1 = "N should be less then 8";
        r0.<init>(r1);
        throw r0;
    L_0x000f:
        r2 = r8.nBit;
        if (r2 != r7) goto L_0x001b;
    L_0x0013:
        r8.advance();
        r2 = r8.curByte;
        if (r2 != r0) goto L_0x001b;
    L_0x001a:
        return r0;
    L_0x001b:
        r0 = r8.nBit;
        r0 = 16 - r0;
        r4 = new int[r0];
        r0 = r8.nBit;
        r2 = r0;
        r0 = r1;
    L_0x0025:
        if (r2 < r7) goto L_0x0038;
    L_0x0027:
        r2 = r0;
        r0 = r1;
    L_0x0029:
        if (r0 < r7) goto L_0x0048;
    L_0x002b:
        r0 = r1;
    L_0x002c:
        if (r1 >= r9) goto L_0x001a;
    L_0x002e:
        r0 = r0 << 1;
        r2 = r4[r1];
        r2 = r2 | r0;
        r0 = r1 + 1;
        r1 = r0;
        r0 = r2;
        goto L_0x002c;
    L_0x0038:
        r3 = r0 + 1;
        r5 = r8.curByte;
        r6 = 7 - r2;
        r5 = r5 >> r6;
        r5 = r5 & 1;
        r4[r0] = r5;
        r0 = r2 + 1;
        r2 = r0;
        r0 = r3;
        goto L_0x0025;
    L_0x0048:
        r3 = r2 + 1;
        r5 = r8.nextByte;
        r6 = 7 - r0;
        r5 = r5 >> r6;
        r5 = r5 & 1;
        r4[r2] = r5;
        r0 = r0 + 1;
        r2 = r3;
        goto L_0x0029;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.h264.read.BitstreamReader.peakNextBits(int):int");
    }

    public int read1Bit() {
        if (this.nBit == 8) {
            advance();
            if (this.curByte == -1) {
                return -1;
            }
        }
        int i = (this.curByte >> (7 - this.nBit)) & 1;
        this.nBit++;
        this.debugBits.append(i == 0 ? '0' : '1');
        bitsRead++;
        return i;
    }

    public boolean readBool() {
        return read1Bit() == 1;
    }

    public int readByte() {
        if (this.nBit > 0) {
            advance();
        }
        int i = this.curByte;
        advance();
        return i;
    }

    public long readNBit(int i) {
        if (i > 64) {
            throw new IllegalArgumentException("Can not readByte more then 64 bit");
        }
        long j = 0;
        for (int i2 = 0; i2 < i; i2++) {
            j = (j << 1) | ((long) read1Bit());
        }
        return j;
    }

    public long readRemainingByte() {
        return readNBit(8 - this.nBit);
    }
}

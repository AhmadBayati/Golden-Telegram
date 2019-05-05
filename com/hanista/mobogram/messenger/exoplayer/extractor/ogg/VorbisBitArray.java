package com.hanista.mobogram.messenger.exoplayer.extractor.ogg;

import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;

final class VorbisBitArray {
    private int bitOffset;
    private int byteOffset;
    public final byte[] data;
    private int limit;

    public VorbisBitArray(byte[] bArr) {
        this(bArr, bArr.length);
    }

    public VorbisBitArray(byte[] bArr, int i) {
        this.data = bArr;
        this.limit = i * 8;
    }

    public int bitsLeft() {
        return this.limit - getPosition();
    }

    public int getPosition() {
        return (this.byteOffset * 8) + this.bitOffset;
    }

    public int limit() {
        return this.limit;
    }

    public boolean readBit() {
        return readBits(1) == 1;
    }

    public int readBits(int i) {
        Assertions.checkState(getPosition() + i <= this.limit);
        if (i == 0) {
            return 0;
        }
        long min;
        int i2;
        int i3;
        int i4;
        if (this.bitOffset != 0) {
            min = Math.min(i, 8 - this.bitOffset);
            i2 = (NalUnitUtil.EXTENDED_SAR >>> (8 - min)) & (this.data[this.byteOffset] >>> this.bitOffset);
            this.bitOffset += min;
            if (this.bitOffset == 8) {
                this.byteOffset++;
                this.bitOffset = 0;
            }
        } else {
            min = 0;
            i2 = 0;
        }
        if (i - min > 7) {
            int i5 = (i - min) / 8;
            i3 = i2;
            i2 = 0;
            while (i2 < i5) {
                long j = (long) i3;
                byte[] bArr = this.data;
                int i6 = this.byteOffset;
                this.byteOffset = i6 + 1;
                min += 8;
                i2++;
                i3 = (int) (j | ((((long) bArr[i6]) & 255) << min));
            }
            int i7 = min;
            i4 = i3;
            i3 = i7;
        } else {
            i3 = min;
            i4 = i2;
        }
        if (i > i3) {
            i2 = i - i3;
            i4 |= ((NalUnitUtil.EXTENDED_SAR >>> (8 - i2)) & this.data[this.byteOffset]) << i3;
            this.bitOffset += i2;
        }
        return i4;
    }

    public void reset() {
        this.byteOffset = 0;
        this.bitOffset = 0;
    }

    public void setPosition(int i) {
        boolean z = i < this.limit && i >= 0;
        Assertions.checkArgument(z);
        this.byteOffset = i / 8;
        this.bitOffset = i - (this.byteOffset * 8);
    }

    public void skipBits(int i) {
        Assertions.checkState(getPosition() + i <= this.limit);
        this.byteOffset += i / 8;
        this.bitOffset += i % 8;
        if (this.bitOffset > 7) {
            this.byteOffset++;
            this.bitOffset -= 8;
        }
    }
}

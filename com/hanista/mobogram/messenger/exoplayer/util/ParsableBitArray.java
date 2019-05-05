package com.hanista.mobogram.messenger.exoplayer.util;

public final class ParsableBitArray {
    private int bitOffset;
    private int byteLimit;
    private int byteOffset;
    public byte[] data;

    public ParsableBitArray(byte[] bArr) {
        this(bArr, bArr.length);
    }

    public ParsableBitArray(byte[] bArr, int i) {
        this.data = bArr;
        this.byteLimit = i;
    }

    private void assertValidOffset() {
        boolean z = this.byteOffset >= 0 && this.bitOffset >= 0 && this.bitOffset < 8 && (this.byteOffset < this.byteLimit || (this.byteOffset == this.byteLimit && this.bitOffset == 0));
        Assertions.checkState(z);
    }

    private int readExpGolombCodeNum() {
        int i = 0;
        int i2 = 0;
        while (!readBit()) {
            i2++;
        }
        int i3 = (1 << i2) - 1;
        if (i2 > 0) {
            i = readBits(i2);
        }
        return i3 + i;
    }

    public int bitsLeft() {
        return ((this.byteLimit - this.byteOffset) * 8) - this.bitOffset;
    }

    public boolean canReadExpGolombCodedNum() {
        int i = this.byteOffset;
        int i2 = this.bitOffset;
        int i3 = 0;
        while (this.byteOffset < this.byteLimit && !readBit()) {
            i3++;
        }
        boolean z = this.byteOffset == this.byteLimit;
        this.byteOffset = i;
        this.bitOffset = i2;
        return !z && bitsLeft() >= (i3 * 2) + 1;
    }

    public int getPosition() {
        return (this.byteOffset * 8) + this.bitOffset;
    }

    public boolean readBit() {
        return readBits(1) == 1;
    }

    public int readBits(int i) {
        int i2 = 0;
        if (i != 0) {
            int i3;
            int i4 = i / 8;
            int i5 = 0;
            for (i3 = 0; i3 < i4; i3++) {
                i -= 8;
                i5 |= ((this.bitOffset != 0 ? ((this.data[this.byteOffset] & NalUnitUtil.EXTENDED_SAR) << this.bitOffset) | ((this.data[this.byteOffset + 1] & NalUnitUtil.EXTENDED_SAR) >>> (8 - this.bitOffset)) : this.data[this.byteOffset]) & NalUnitUtil.EXTENDED_SAR) << i;
                this.byteOffset++;
            }
            if (i > 0) {
                i3 = this.bitOffset + i;
                byte b = (byte) (NalUnitUtil.EXTENDED_SAR >> (8 - i));
                if (i3 > 8) {
                    i2 = (b & (((this.data[this.byteOffset] & NalUnitUtil.EXTENDED_SAR) << (i3 - 8)) | ((this.data[this.byteOffset + 1] & NalUnitUtil.EXTENDED_SAR) >> (16 - i3)))) | i5;
                    this.byteOffset++;
                } else {
                    i2 = (b & ((this.data[this.byteOffset] & NalUnitUtil.EXTENDED_SAR) >> (8 - i3))) | i5;
                    if (i3 == 8) {
                        this.byteOffset++;
                    }
                }
                this.bitOffset = i3 % 8;
            } else {
                i2 = i5;
            }
            assertValidOffset();
        }
        return i2;
    }

    public int readSignedExpGolombCodedInt() {
        int readExpGolombCodeNum = readExpGolombCodeNum();
        return (readExpGolombCodeNum % 2 == 0 ? -1 : 1) * ((readExpGolombCodeNum + 1) / 2);
    }

    public int readUnsignedExpGolombCodedInt() {
        return readExpGolombCodeNum();
    }

    public void reset(byte[] bArr) {
        reset(bArr, bArr.length);
    }

    public void reset(byte[] bArr, int i) {
        this.data = bArr;
        this.byteOffset = 0;
        this.bitOffset = 0;
        this.byteLimit = i;
    }

    public void setPosition(int i) {
        this.byteOffset = i / 8;
        this.bitOffset = i - (this.byteOffset * 8);
        assertValidOffset();
    }

    public void skipBits(int i) {
        this.byteOffset += i / 8;
        this.bitOffset += i % 8;
        if (this.bitOffset > 7) {
            this.byteOffset++;
            this.bitOffset -= 8;
        }
        assertValidOffset();
    }
}

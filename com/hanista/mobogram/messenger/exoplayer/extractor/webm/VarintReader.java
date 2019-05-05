package com.hanista.mobogram.messenger.exoplayer.extractor.webm;

import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;

final class VarintReader {
    private static final int STATE_BEGIN_READING = 0;
    private static final int STATE_READ_CONTENTS = 1;
    private static final long[] VARINT_LENGTH_MASKS;
    private int length;
    private final byte[] scratch;
    private int state;

    static {
        VARINT_LENGTH_MASKS = new long[]{128, 64, 32, 16, 8, 4, 2, 1};
    }

    public VarintReader() {
        this.scratch = new byte[8];
    }

    public static long assembleVarint(byte[] bArr, int i, boolean z) {
        long j = ((long) bArr[STATE_BEGIN_READING]) & 255;
        if (z) {
            j &= VARINT_LENGTH_MASKS[i - 1] ^ -1;
        }
        long j2 = j;
        for (int i2 = STATE_READ_CONTENTS; i2 < i; i2 += STATE_READ_CONTENTS) {
            j2 = (j2 << 8) | (((long) bArr[i2]) & 255);
        }
        return j2;
    }

    public static int parseUnsignedVarintLength(int i) {
        for (int i2 = STATE_BEGIN_READING; i2 < VARINT_LENGTH_MASKS.length; i2 += STATE_READ_CONTENTS) {
            if ((VARINT_LENGTH_MASKS[i2] & ((long) i)) != 0) {
                return i2 + STATE_READ_CONTENTS;
            }
        }
        return -1;
    }

    public int getLastLength() {
        return this.length;
    }

    public long readUnsignedVarint(ExtractorInput extractorInput, boolean z, boolean z2, int i) {
        if (this.state == 0) {
            if (!extractorInput.readFully(this.scratch, STATE_BEGIN_READING, STATE_READ_CONTENTS, z)) {
                return -1;
            }
            this.length = parseUnsignedVarintLength(this.scratch[STATE_BEGIN_READING] & NalUnitUtil.EXTENDED_SAR);
            if (this.length == -1) {
                throw new IllegalStateException("No valid varint length mask found");
            }
            this.state = STATE_READ_CONTENTS;
        }
        if (this.length > i) {
            this.state = STATE_BEGIN_READING;
            return -2;
        }
        if (this.length != STATE_READ_CONTENTS) {
            extractorInput.readFully(this.scratch, STATE_READ_CONTENTS, this.length - 1);
        }
        this.state = STATE_BEGIN_READING;
        return assembleVarint(this.scratch, this.length, z2);
    }

    public void reset() {
        this.state = STATE_BEGIN_READING;
        this.length = STATE_BEGIN_READING;
    }
}

package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import java.nio.ByteBuffer;

public class BitReaderBuffer {
    private ByteBuffer buffer;
    int initialPos;
    int position;

    public BitReaderBuffer(ByteBuffer byteBuffer) {
        this.buffer = byteBuffer;
        this.initialPos = byteBuffer.position();
    }

    public int byteSync() {
        int i = 8 - (this.position % 8);
        if (i == 8) {
            i = 0;
        }
        readBits(i);
        return i;
    }

    public int getPosition() {
        return this.position;
    }

    public int readBits(int i) {
        int i2 = this.buffer.get(this.initialPos + (this.position / 8));
        if (i2 < 0) {
            i2 += TLRPC.USER_FLAG_UNUSED2;
        }
        int i3 = 8 - (this.position % 8);
        if (i <= i3) {
            i2 = ((i2 << (this.position % 8)) & NalUnitUtil.EXTENDED_SAR) >> ((i3 - i) + (this.position % 8));
            this.position += i;
        } else {
            i2 = i - i3;
            i2 = readBits(i2) + (readBits(i3) << i2);
        }
        this.buffer.position(this.initialPos + ((int) Math.ceil(((double) this.position) / 8.0d)));
        return i2;
    }

    public boolean readBool() {
        return readBits(1) == 1;
    }

    public int remainingBits() {
        return (this.buffer.limit() * 8) - this.position;
    }
}

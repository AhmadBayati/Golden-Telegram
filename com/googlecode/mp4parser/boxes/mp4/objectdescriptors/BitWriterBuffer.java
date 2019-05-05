package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import android.support.v4.view.InputDeviceCompat;
import com.hanista.mobogram.tgnet.TLRPC;
import java.nio.ByteBuffer;

public class BitWriterBuffer {
    static final /* synthetic */ boolean $assertionsDisabled;
    private ByteBuffer buffer;
    int initialPos;
    int position;

    static {
        $assertionsDisabled = !BitWriterBuffer.class.desiredAssertionStatus();
    }

    public BitWriterBuffer(ByteBuffer byteBuffer) {
        this.position = 0;
        this.buffer = byteBuffer;
        this.initialPos = byteBuffer.position();
    }

    public void writeBits(int i, int i2) {
        if ($assertionsDisabled || i <= (1 << i2) - 1) {
            int i3 = 8 - (this.position % 8);
            int i4;
            if (i2 <= i3) {
                i4 = this.buffer.get(this.initialPos + (this.position / 8));
                if (i4 < 0) {
                    i4 += TLRPC.USER_FLAG_UNUSED2;
                }
                i4 += i << (i3 - i2);
                ByteBuffer byteBuffer = this.buffer;
                int i5 = this.initialPos + (this.position / 8);
                if (i4 > 127) {
                    i4 += InputDeviceCompat.SOURCE_ANY;
                }
                byteBuffer.put(i5, (byte) i4);
                this.position += i2;
            } else {
                i4 = i2 - i3;
                writeBits(i >> i4, i3);
                writeBits(((1 << i4) - 1) & i, i4);
            }
            this.buffer.position((this.position % 8 > 0 ? 1 : 0) + ((this.position / 8) + this.initialPos));
            return;
        }
        throw new AssertionError(String.format("Trying to write a value bigger (%s) than the number bits (%s) allows. Please mask the value before writing it and make your code is really working as intended.", new Object[]{Integer.valueOf(i), Integer.valueOf((1 << i2) - 1)}));
    }
}

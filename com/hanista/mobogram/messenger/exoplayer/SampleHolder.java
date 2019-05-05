package com.hanista.mobogram.messenger.exoplayer;

import java.nio.ByteBuffer;

public final class SampleHolder {
    public static final int BUFFER_REPLACEMENT_MODE_DIRECT = 2;
    public static final int BUFFER_REPLACEMENT_MODE_DISABLED = 0;
    public static final int BUFFER_REPLACEMENT_MODE_NORMAL = 1;
    private final int bufferReplacementMode;
    public final CryptoInfo cryptoInfo;
    public ByteBuffer data;
    public int flags;
    public int size;
    public long timeUs;

    public SampleHolder(int i) {
        this.cryptoInfo = new CryptoInfo();
        this.bufferReplacementMode = i;
    }

    private ByteBuffer createReplacementBuffer(int i) {
        if (this.bufferReplacementMode == BUFFER_REPLACEMENT_MODE_NORMAL) {
            return ByteBuffer.allocate(i);
        }
        if (this.bufferReplacementMode == BUFFER_REPLACEMENT_MODE_DIRECT) {
            return ByteBuffer.allocateDirect(i);
        }
        throw new IllegalStateException("Buffer too small (" + (this.data == null ? BUFFER_REPLACEMENT_MODE_DISABLED : this.data.capacity()) + " < " + i + ")");
    }

    public void clearData() {
        if (this.data != null) {
            this.data.clear();
        }
    }

    public void ensureSpaceForWrite(int i) {
        if (this.data == null) {
            this.data = createReplacementBuffer(i);
            return;
        }
        int capacity = this.data.capacity();
        int position = this.data.position();
        int i2 = position + i;
        if (capacity < i2) {
            ByteBuffer createReplacementBuffer = createReplacementBuffer(i2);
            if (position > 0) {
                this.data.position(BUFFER_REPLACEMENT_MODE_DISABLED);
                this.data.limit(position);
                createReplacementBuffer.put(this.data);
            }
            this.data = createReplacementBuffer;
        }
    }

    public boolean isDecodeOnly() {
        return (this.flags & C0700C.SAMPLE_FLAG_DECODE_ONLY) != 0;
    }

    public boolean isEncrypted() {
        return (this.flags & BUFFER_REPLACEMENT_MODE_DIRECT) != 0;
    }

    public boolean isSyncFrame() {
        return (this.flags & BUFFER_REPLACEMENT_MODE_NORMAL) != 0;
    }
}

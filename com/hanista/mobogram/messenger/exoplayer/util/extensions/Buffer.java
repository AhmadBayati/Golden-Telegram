package com.hanista.mobogram.messenger.exoplayer.util.extensions;

public abstract class Buffer {
    public static final int FLAG_DECODE_ONLY = 2;
    public static final int FLAG_END_OF_STREAM = 1;
    private int flags;

    public final boolean getFlag(int i) {
        return (this.flags & i) == i;
    }

    public void reset() {
        this.flags = 0;
    }

    public final void setFlag(int i) {
        this.flags |= i;
    }
}

package com.hanista.mobogram.messenger.exoplayer.util.extensions;

import com.hanista.mobogram.messenger.exoplayer.SampleHolder;

public class InputBuffer extends Buffer {
    public final SampleHolder sampleHolder;

    public InputBuffer() {
        this.sampleHolder = new SampleHolder(2);
    }

    public void reset() {
        super.reset();
        this.sampleHolder.clearData();
    }
}

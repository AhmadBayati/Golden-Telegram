package com.hanista.mobogram.messenger.exoplayer.extractor.ts;

import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

abstract class ElementaryStreamReader {
    protected final TrackOutput output;

    protected ElementaryStreamReader(TrackOutput trackOutput) {
        this.output = trackOutput;
    }

    public abstract void consume(ParsableByteArray parsableByteArray);

    public abstract void packetFinished();

    public abstract void packetStarted(long j, boolean z);

    public abstract void seek();
}

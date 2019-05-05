package com.hanista.mobogram.messenger.exoplayer.extractor.flv;

import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

abstract class TagPayloadReader {
    private long durationUs;
    protected final TrackOutput output;

    public static final class UnsupportedFormatException extends ParserException {
        public UnsupportedFormatException(String str) {
            super(str);
        }
    }

    protected TagPayloadReader(TrackOutput trackOutput) {
        this.output = trackOutput;
        this.durationUs = -1;
    }

    public final void consume(ParsableByteArray parsableByteArray, long j) {
        if (parseHeader(parsableByteArray)) {
            parsePayload(parsableByteArray, j);
        }
    }

    public final long getDurationUs() {
        return this.durationUs;
    }

    protected abstract boolean parseHeader(ParsableByteArray parsableByteArray);

    protected abstract void parsePayload(ParsableByteArray parsableByteArray, long j);

    public abstract void seek();

    public final void setDurationUs(long j) {
        this.durationUs = j;
    }
}

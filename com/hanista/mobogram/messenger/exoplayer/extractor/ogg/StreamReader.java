package com.hanista.mobogram.messenger.exoplayer.extractor.ogg;

import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.PositionHolder;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

abstract class StreamReader {
    protected ExtractorOutput extractorOutput;
    protected final OggParser oggParser;
    protected final ParsableByteArray scratch;
    protected TrackOutput trackOutput;

    StreamReader() {
        this.scratch = new ParsableByteArray(new byte[65025], 0);
        this.oggParser = new OggParser();
    }

    void init(ExtractorOutput extractorOutput, TrackOutput trackOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = trackOutput;
    }

    abstract int read(ExtractorInput extractorInput, PositionHolder positionHolder);

    void seek() {
        this.oggParser.reset();
        this.scratch.reset();
    }
}

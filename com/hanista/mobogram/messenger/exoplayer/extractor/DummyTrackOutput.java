package com.hanista.mobogram.messenger.exoplayer.extractor;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

public class DummyTrackOutput implements TrackOutput {
    public void format(MediaFormat mediaFormat) {
    }

    public int sampleData(ExtractorInput extractorInput, int i, boolean z) {
        return extractorInput.skip(i);
    }

    public void sampleData(ParsableByteArray parsableByteArray, int i) {
        parsableByteArray.skipBytes(i);
    }

    public void sampleMetadata(long j, int i, int i2, int i3, byte[] bArr) {
    }
}

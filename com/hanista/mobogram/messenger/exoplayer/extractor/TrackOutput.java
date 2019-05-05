package com.hanista.mobogram.messenger.exoplayer.extractor;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

public interface TrackOutput {
    void format(MediaFormat mediaFormat);

    int sampleData(ExtractorInput extractorInput, int i, boolean z);

    void sampleData(ParsableByteArray parsableByteArray, int i);

    void sampleMetadata(long j, int i, int i2, int i3, byte[] bArr);
}

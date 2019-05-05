package com.hanista.mobogram.messenger.exoplayer.chunk;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorOutput;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;
import com.hanista.mobogram.messenger.exoplayer.extractor.TrackOutput;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;

public class ChunkExtractorWrapper implements ExtractorOutput, TrackOutput {
    private final Extractor extractor;
    private boolean extractorInitialized;
    private SingleTrackOutput output;
    private boolean seenTrack;

    public interface SingleTrackOutput extends TrackOutput {
        void drmInitData(DrmInitData drmInitData);

        void seekMap(SeekMap seekMap);
    }

    public ChunkExtractorWrapper(Extractor extractor) {
        this.extractor = extractor;
    }

    public void drmInitData(DrmInitData drmInitData) {
        this.output.drmInitData(drmInitData);
    }

    public void endTracks() {
        Assertions.checkState(this.seenTrack);
    }

    public void format(MediaFormat mediaFormat) {
        this.output.format(mediaFormat);
    }

    public void init(SingleTrackOutput singleTrackOutput) {
        this.output = singleTrackOutput;
        if (this.extractorInitialized) {
            this.extractor.seek();
            return;
        }
        this.extractor.init(this);
        this.extractorInitialized = true;
    }

    public int read(ExtractorInput extractorInput) {
        boolean z = true;
        int read = this.extractor.read(extractorInput, null);
        if (read == 1) {
            z = false;
        }
        Assertions.checkState(z);
        return read;
    }

    public int sampleData(ExtractorInput extractorInput, int i, boolean z) {
        return this.output.sampleData(extractorInput, i, z);
    }

    public void sampleData(ParsableByteArray parsableByteArray, int i) {
        this.output.sampleData(parsableByteArray, i);
    }

    public void sampleMetadata(long j, int i, int i2, int i3, byte[] bArr) {
        this.output.sampleMetadata(j, i, i2, i3, bArr);
    }

    public void seekMap(SeekMap seekMap) {
        this.output.seekMap(seekMap);
    }

    public TrackOutput track(int i) {
        Assertions.checkState(!this.seenTrack);
        this.seenTrack = true;
        return this;
    }
}

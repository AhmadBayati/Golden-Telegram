package com.hanista.mobogram.messenger.exoplayer.chunk;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData;
import com.hanista.mobogram.messenger.exoplayer.extractor.DefaultTrackOutput;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;

public abstract class BaseMediaChunk extends MediaChunk {
    private int firstSampleIndex;
    public final boolean isMediaFormatFinal;
    private DefaultTrackOutput output;

    public BaseMediaChunk(DataSource dataSource, DataSpec dataSpec, int i, Format format, long j, long j2, int i2, boolean z, int i3) {
        super(dataSource, dataSpec, i, format, j, j2, i2, i3);
        this.isMediaFormatFinal = z;
    }

    public abstract DrmInitData getDrmInitData();

    public final int getFirstSampleIndex() {
        return this.firstSampleIndex;
    }

    public abstract MediaFormat getMediaFormat();

    protected final DefaultTrackOutput getOutput() {
        return this.output;
    }

    public void init(DefaultTrackOutput defaultTrackOutput) {
        this.output = defaultTrackOutput;
        this.firstSampleIndex = defaultTrackOutput.getWriteIndex();
    }
}

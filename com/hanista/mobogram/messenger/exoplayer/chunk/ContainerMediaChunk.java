package com.hanista.mobogram.messenger.exoplayer.chunk;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.chunk.ChunkExtractorWrapper.SingleTrackOutput;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData;
import com.hanista.mobogram.messenger.exoplayer.extractor.DefaultExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;

public class ContainerMediaChunk extends BaseMediaChunk implements SingleTrackOutput {
    private final int adaptiveMaxHeight;
    private final int adaptiveMaxWidth;
    private volatile int bytesLoaded;
    private DrmInitData drmInitData;
    private final ChunkExtractorWrapper extractorWrapper;
    private volatile boolean loadCanceled;
    private MediaFormat mediaFormat;
    private final long sampleOffsetUs;

    public ContainerMediaChunk(DataSource dataSource, DataSpec dataSpec, int i, Format format, long j, long j2, int i2, long j3, ChunkExtractorWrapper chunkExtractorWrapper, MediaFormat mediaFormat, int i3, int i4, DrmInitData drmInitData, boolean z, int i5) {
        super(dataSource, dataSpec, i, format, j, j2, i2, z, i5);
        this.extractorWrapper = chunkExtractorWrapper;
        this.sampleOffsetUs = j3;
        this.adaptiveMaxWidth = i3;
        this.adaptiveMaxHeight = i4;
        this.mediaFormat = getAdjustedMediaFormat(mediaFormat, j3, i3, i4);
        this.drmInitData = drmInitData;
    }

    private static MediaFormat getAdjustedMediaFormat(MediaFormat mediaFormat, long j, int i, int i2) {
        if (mediaFormat == null) {
            return null;
        }
        MediaFormat copyWithSubsampleOffsetUs = (j == 0 || mediaFormat.subsampleOffsetUs == PtsTimestampAdjuster.DO_NOT_OFFSET) ? mediaFormat : mediaFormat.copyWithSubsampleOffsetUs(mediaFormat.subsampleOffsetUs + j);
        return (i == -1 && i2 == -1) ? copyWithSubsampleOffsetUs : copyWithSubsampleOffsetUs.copyWithMaxVideoDimensions(i, i2);
    }

    public final long bytesLoaded() {
        return (long) this.bytesLoaded;
    }

    public final void cancelLoad() {
        this.loadCanceled = true;
    }

    public final void drmInitData(DrmInitData drmInitData) {
        this.drmInitData = drmInitData;
    }

    public final void format(MediaFormat mediaFormat) {
        this.mediaFormat = getAdjustedMediaFormat(mediaFormat, this.sampleOffsetUs, this.adaptiveMaxWidth, this.adaptiveMaxHeight);
    }

    public final DrmInitData getDrmInitData() {
        return this.drmInitData;
    }

    public final MediaFormat getMediaFormat() {
        return this.mediaFormat;
    }

    public final boolean isLoadCanceled() {
        return this.loadCanceled;
    }

    public final void load() {
        DataSpec remainderDataSpec = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
        ExtractorInput defaultExtractorInput;
        try {
            defaultExtractorInput = new DefaultExtractorInput(this.dataSource, remainderDataSpec.absoluteStreamPosition, this.dataSource.open(remainderDataSpec));
            if (this.bytesLoaded == 0) {
                this.extractorWrapper.init(this);
            }
            int i = 0;
            while (i == 0) {
                if (!this.loadCanceled) {
                    i = this.extractorWrapper.read(defaultExtractorInput);
                }
            }
            break;
            this.bytesLoaded = (int) (defaultExtractorInput.getPosition() - this.dataSpec.absoluteStreamPosition);
            this.dataSource.close();
        } catch (Throwable th) {
            this.dataSource.close();
        }
    }

    public final int sampleData(ExtractorInput extractorInput, int i, boolean z) {
        return getOutput().sampleData(extractorInput, i, z);
    }

    public final void sampleData(ParsableByteArray parsableByteArray, int i) {
        getOutput().sampleData(parsableByteArray, i);
    }

    public final void sampleMetadata(long j, int i, int i2, int i3, byte[] bArr) {
        getOutput().sampleMetadata(this.sampleOffsetUs + j, i, i2, i3, bArr);
    }

    public final void seekMap(SeekMap seekMap) {
    }
}

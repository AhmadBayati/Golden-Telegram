package com.hanista.mobogram.messenger.exoplayer.chunk;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.chunk.ChunkExtractorWrapper.SingleTrackOutput;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData;
import com.hanista.mobogram.messenger.exoplayer.extractor.DefaultExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.SeekMap;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import com.hanista.mobogram.messenger.exoplayer.util.ParsableByteArray;
import com.hanista.mobogram.messenger.exoplayer.util.Util;

public final class InitializationChunk extends Chunk implements SingleTrackOutput {
    private volatile int bytesLoaded;
    private DrmInitData drmInitData;
    private final ChunkExtractorWrapper extractorWrapper;
    private volatile boolean loadCanceled;
    private MediaFormat mediaFormat;
    private SeekMap seekMap;

    public InitializationChunk(DataSource dataSource, DataSpec dataSpec, int i, Format format, ChunkExtractorWrapper chunkExtractorWrapper) {
        this(dataSource, dataSpec, i, format, chunkExtractorWrapper, -1);
    }

    public InitializationChunk(DataSource dataSource, DataSpec dataSpec, int i, Format format, ChunkExtractorWrapper chunkExtractorWrapper, int i2) {
        super(dataSource, dataSpec, 2, i, format, i2);
        this.extractorWrapper = chunkExtractorWrapper;
    }

    public long bytesLoaded() {
        return (long) this.bytesLoaded;
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }

    public void drmInitData(DrmInitData drmInitData) {
        this.drmInitData = drmInitData;
    }

    public void format(MediaFormat mediaFormat) {
        this.mediaFormat = mediaFormat;
    }

    public DrmInitData getDrmInitData() {
        return this.drmInitData;
    }

    public MediaFormat getFormat() {
        return this.mediaFormat;
    }

    public SeekMap getSeekMap() {
        return this.seekMap;
    }

    public boolean hasDrmInitData() {
        return this.drmInitData != null;
    }

    public boolean hasFormat() {
        return this.mediaFormat != null;
    }

    public boolean hasSeekMap() {
        return this.seekMap != null;
    }

    public boolean isLoadCanceled() {
        return this.loadCanceled;
    }

    public void load() {
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

    public int sampleData(ExtractorInput extractorInput, int i, boolean z) {
        throw new IllegalStateException("Unexpected sample data in initialization chunk");
    }

    public void sampleData(ParsableByteArray parsableByteArray, int i) {
        throw new IllegalStateException("Unexpected sample data in initialization chunk");
    }

    public void sampleMetadata(long j, int i, int i2, int i3, byte[] bArr) {
        throw new IllegalStateException("Unexpected sample data in initialization chunk");
    }

    public void seekMap(SeekMap seekMap) {
        this.seekMap = seekMap;
    }
}

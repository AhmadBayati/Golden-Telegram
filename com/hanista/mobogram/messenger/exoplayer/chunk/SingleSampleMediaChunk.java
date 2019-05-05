package com.hanista.mobogram.messenger.exoplayer.chunk;

import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.tgnet.ConnectionsManager;

public final class SingleSampleMediaChunk extends BaseMediaChunk {
    private volatile int bytesLoaded;
    private volatile boolean loadCanceled;
    private final DrmInitData sampleDrmInitData;
    private final MediaFormat sampleFormat;

    public SingleSampleMediaChunk(DataSource dataSource, DataSpec dataSpec, int i, Format format, long j, long j2, int i2, MediaFormat mediaFormat, DrmInitData drmInitData, int i3) {
        super(dataSource, dataSpec, i, format, j, j2, i2, true, i3);
        this.sampleFormat = mediaFormat;
        this.sampleDrmInitData = drmInitData;
    }

    public long bytesLoaded() {
        return (long) this.bytesLoaded;
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }

    public DrmInitData getDrmInitData() {
        return this.sampleDrmInitData;
    }

    public MediaFormat getMediaFormat() {
        return this.sampleFormat;
    }

    public boolean isLoadCanceled() {
        return this.loadCanceled;
    }

    public void load() {
        int i = 0;
        try {
            this.dataSource.open(Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded));
            while (i != -1) {
                this.bytesLoaded = i + this.bytesLoaded;
                i = getOutput().sampleData(this.dataSource, (int) ConnectionsManager.DEFAULT_DATACENTER_ID, true);
            }
            getOutput().sampleMetadata(this.startTimeUs, 1, this.bytesLoaded, 0, null);
        } finally {
            this.dataSource.close();
        }
    }
}

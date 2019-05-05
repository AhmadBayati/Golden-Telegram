package com.hanista.mobogram.messenger.exoplayer.hls;

import com.hanista.mobogram.messenger.exoplayer.chunk.Format;
import com.hanista.mobogram.messenger.exoplayer.chunk.MediaChunk;
import com.hanista.mobogram.messenger.exoplayer.extractor.DefaultExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorInput;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import com.hanista.mobogram.messenger.exoplayer.util.Util;

public final class TsChunk extends MediaChunk {
    private long adjustedEndTimeUs;
    private int bytesLoaded;
    public final int discontinuitySequenceNumber;
    public final HlsExtractorWrapper extractorWrapper;
    private final boolean isEncrypted;
    private volatile boolean loadCanceled;

    public TsChunk(DataSource dataSource, DataSpec dataSpec, int i, Format format, long j, long j2, int i2, int i3, HlsExtractorWrapper hlsExtractorWrapper, byte[] bArr, byte[] bArr2) {
        super(buildDataSource(dataSource, bArr, bArr2), dataSpec, i, format, j, j2, i2);
        this.discontinuitySequenceNumber = i3;
        this.extractorWrapper = hlsExtractorWrapper;
        this.isEncrypted = this.dataSource instanceof Aes128DataSource;
        this.adjustedEndTimeUs = j;
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] bArr, byte[] bArr2) {
        return (bArr == null || bArr2 == null) ? dataSource : new Aes128DataSource(dataSource, bArr, bArr2);
    }

    public long bytesLoaded() {
        return (long) this.bytesLoaded;
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }

    public long getAdjustedEndTimeUs() {
        return this.adjustedEndTimeUs;
    }

    public boolean isLoadCanceled() {
        return this.loadCanceled;
    }

    public void load() {
        int i;
        DataSpec dataSpec;
        int i2 = 0;
        if (this.isEncrypted) {
            i = this.bytesLoaded != 0 ? 1 : 0;
            dataSpec = this.dataSpec;
        } else {
            i = 0;
            dataSpec = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
        }
        ExtractorInput defaultExtractorInput;
        try {
            defaultExtractorInput = new DefaultExtractorInput(this.dataSource, dataSpec.absoluteStreamPosition, this.dataSource.open(dataSpec));
            if (i != 0) {
                defaultExtractorInput.skipFully(this.bytesLoaded);
            }
            while (i2 == 0) {
                if (!this.loadCanceled) {
                    i2 = this.extractorWrapper.read(defaultExtractorInput);
                }
            }
            break;
            long adjustedEndTimeUs = this.extractorWrapper.getAdjustedEndTimeUs();
            if (adjustedEndTimeUs != Long.MIN_VALUE) {
                this.adjustedEndTimeUs = adjustedEndTimeUs;
            }
            this.bytesLoaded = (int) (defaultExtractorInput.getPosition() - this.dataSpec.absoluteStreamPosition);
            this.dataSource.close();
        } catch (Throwable th) {
            this.dataSource.close();
        }
    }
}

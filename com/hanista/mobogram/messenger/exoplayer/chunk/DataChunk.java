package com.hanista.mobogram.messenger.exoplayer.chunk;

import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import java.util.Arrays;

public abstract class DataChunk extends Chunk {
    private static final int READ_GRANULARITY = 16384;
    private byte[] data;
    private int limit;
    private volatile boolean loadCanceled;

    public DataChunk(DataSource dataSource, DataSpec dataSpec, int i, int i2, Format format, int i3, byte[] bArr) {
        super(dataSource, dataSpec, i, i2, format, i3);
        this.data = bArr;
    }

    private void maybeExpandData() {
        if (this.data == null) {
            this.data = new byte[READ_GRANULARITY];
        } else if (this.data.length < this.limit + READ_GRANULARITY) {
            this.data = Arrays.copyOf(this.data, this.data.length + READ_GRANULARITY);
        }
    }

    public long bytesLoaded() {
        return (long) this.limit;
    }

    public final void cancelLoad() {
        this.loadCanceled = true;
    }

    protected abstract void consume(byte[] bArr, int i);

    public byte[] getDataHolder() {
        return this.data;
    }

    public final boolean isLoadCanceled() {
        return this.loadCanceled;
    }

    public final void load() {
        int i = 0;
        try {
            this.dataSource.open(this.dataSpec);
            this.limit = 0;
            while (i != -1 && !this.loadCanceled) {
                maybeExpandData();
                i = this.dataSource.read(this.data, this.limit, READ_GRANULARITY);
                if (i != -1) {
                    this.limit += i;
                }
            }
            if (!this.loadCanceled) {
                consume(this.data, this.limit);
            }
            this.dataSource.close();
        } catch (Throwable th) {
            this.dataSource.close();
        }
    }
}

package com.hanista.mobogram.messenger.exoplayer.upstream;

import com.hanista.mobogram.messenger.exoplayer.util.Assertions;

public final class PriorityDataSource implements DataSource {
    private final int priority;
    private final DataSource upstream;

    public PriorityDataSource(int i, DataSource dataSource) {
        this.priority = i;
        this.upstream = (DataSource) Assertions.checkNotNull(dataSource);
    }

    public void close() {
        this.upstream.close();
    }

    public long open(DataSpec dataSpec) {
        NetworkLock.instance.proceedOrThrow(this.priority);
        return this.upstream.open(dataSpec);
    }

    public int read(byte[] bArr, int i, int i2) {
        NetworkLock.instance.proceedOrThrow(this.priority);
        return this.upstream.read(bArr, i, i2);
    }
}

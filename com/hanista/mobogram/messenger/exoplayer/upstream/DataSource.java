package com.hanista.mobogram.messenger.exoplayer.upstream;

public interface DataSource {
    void close();

    long open(DataSpec dataSpec);

    int read(byte[] bArr, int i, int i2);
}

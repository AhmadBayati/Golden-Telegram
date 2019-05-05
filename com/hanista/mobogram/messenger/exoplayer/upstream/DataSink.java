package com.hanista.mobogram.messenger.exoplayer.upstream;

public interface DataSink {
    void close();

    DataSink open(DataSpec dataSpec);

    void write(byte[] bArr, int i, int i2);
}

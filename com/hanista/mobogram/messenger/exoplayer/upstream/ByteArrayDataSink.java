package com.hanista.mobogram.messenger.exoplayer.upstream;

import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import java.io.ByteArrayOutputStream;

public final class ByteArrayDataSink implements DataSink {
    private ByteArrayOutputStream stream;

    public void close() {
        this.stream.close();
    }

    public byte[] getData() {
        return this.stream == null ? null : this.stream.toByteArray();
    }

    public DataSink open(DataSpec dataSpec) {
        if (dataSpec.length == -1) {
            this.stream = new ByteArrayOutputStream();
        } else {
            Assertions.checkArgument(dataSpec.length <= 2147483647L);
            this.stream = new ByteArrayOutputStream((int) dataSpec.length);
        }
        return this;
    }

    public void write(byte[] bArr, int i, int i2) {
        this.stream.write(bArr, i, i2);
    }
}

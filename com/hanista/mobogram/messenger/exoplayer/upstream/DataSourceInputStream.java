package com.hanista.mobogram.messenger.exoplayer.upstream;

import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import java.io.InputStream;

public final class DataSourceInputStream extends InputStream {
    private boolean closed;
    private final DataSource dataSource;
    private final DataSpec dataSpec;
    private boolean opened;
    private final byte[] singleByteArray;

    public DataSourceInputStream(DataSource dataSource, DataSpec dataSpec) {
        this.opened = false;
        this.closed = false;
        this.dataSource = dataSource;
        this.dataSpec = dataSpec;
        this.singleByteArray = new byte[1];
    }

    private void checkOpened() {
        if (!this.opened) {
            this.dataSource.open(this.dataSpec);
            this.opened = true;
        }
    }

    public void close() {
        if (!this.closed) {
            this.dataSource.close();
            this.closed = true;
        }
    }

    public void open() {
        checkOpened();
    }

    public int read() {
        return read(this.singleByteArray) == -1 ? -1 : this.singleByteArray[0] & NalUnitUtil.EXTENDED_SAR;
    }

    public int read(byte[] bArr) {
        return read(bArr, 0, bArr.length);
    }

    public int read(byte[] bArr, int i, int i2) {
        Assertions.checkState(!this.closed);
        checkOpened();
        return this.dataSource.read(bArr, i, i2);
    }

    public long skip(long j) {
        Assertions.checkState(!this.closed);
        checkOpened();
        return super.skip(j);
    }
}

package com.hanista.mobogram.messenger.exoplayer.upstream.cache;

import com.hanista.mobogram.messenger.exoplayer.upstream.DataSink;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class CacheDataSink implements DataSink {
    private final Cache cache;
    private DataSpec dataSpec;
    private long dataSpecBytesWritten;
    private File file;
    private final long maxCacheFileSize;
    private FileOutputStream outputStream;
    private long outputStreamBytesWritten;

    public static class CacheDataSinkException extends IOException {
        public CacheDataSinkException(IOException iOException) {
            super(iOException);
        }
    }

    public CacheDataSink(Cache cache, long j) {
        this.cache = (Cache) Assertions.checkNotNull(cache);
        this.maxCacheFileSize = j;
    }

    private void closeCurrentOutputStream() {
        if (this.outputStream != null) {
            try {
                this.outputStream.flush();
                this.outputStream.getFD().sync();
                Util.closeQuietly(this.outputStream);
                this.cache.commitFile(this.file);
                this.outputStream = null;
                this.file = null;
            } catch (Throwable th) {
                Util.closeQuietly(this.outputStream);
                this.file.delete();
                this.outputStream = null;
                this.file = null;
            }
        }
    }

    private void openNextOutputStream() {
        this.file = this.cache.startFile(this.dataSpec.key, this.dataSpec.absoluteStreamPosition + this.dataSpecBytesWritten, Math.min(this.dataSpec.length - this.dataSpecBytesWritten, this.maxCacheFileSize));
        this.outputStream = new FileOutputStream(this.file);
        this.outputStreamBytesWritten = 0;
    }

    public void close() {
        try {
            closeCurrentOutputStream();
        } catch (IOException e) {
            throw new CacheDataSinkException(e);
        }
    }

    public DataSink open(DataSpec dataSpec) {
        Assertions.checkState(dataSpec.length != -1);
        try {
            this.dataSpec = dataSpec;
            this.dataSpecBytesWritten = 0;
            openNextOutputStream();
            return this;
        } catch (IOException e) {
            throw new CacheDataSinkException(e);
        }
    }

    public void write(byte[] bArr, int i, int i2) {
        int i3 = 0;
        while (i3 < i2) {
            try {
                if (this.outputStreamBytesWritten == this.maxCacheFileSize) {
                    closeCurrentOutputStream();
                    openNextOutputStream();
                }
                int min = (int) Math.min((long) (i2 - i3), this.maxCacheFileSize - this.outputStreamBytesWritten);
                this.outputStream.write(bArr, i + i3, min);
                i3 += min;
                this.outputStreamBytesWritten += (long) min;
                this.dataSpecBytesWritten += (long) min;
            } catch (IOException e) {
                throw new CacheDataSinkException(e);
            }
        }
    }
}

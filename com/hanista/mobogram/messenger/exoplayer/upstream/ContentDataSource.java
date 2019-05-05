package com.hanista.mobogram.messenger.exoplayer.upstream;

import android.content.ContentResolver;
import android.content.Context;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ContentDataSource implements UriDataSource {
    private long bytesRemaining;
    private InputStream inputStream;
    private final TransferListener listener;
    private boolean opened;
    private final ContentResolver resolver;
    private String uriString;

    public static class ContentDataSourceException extends IOException {
        public ContentDataSourceException(IOException iOException) {
            super(iOException);
        }
    }

    public ContentDataSource(Context context) {
        this(context, null);
    }

    public ContentDataSource(Context context, TransferListener transferListener) {
        this.resolver = context.getContentResolver();
        this.listener = transferListener;
    }

    public void close() {
        this.uriString = null;
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
                this.inputStream = null;
                if (this.opened) {
                    this.opened = false;
                    if (this.listener != null) {
                        this.listener.onTransferEnd();
                    }
                }
            } catch (IOException e) {
                throw new ContentDataSourceException(e);
            } catch (Throwable th) {
                this.inputStream = null;
                if (this.opened) {
                    this.opened = false;
                    if (this.listener != null) {
                        this.listener.onTransferEnd();
                    }
                }
            }
        }
    }

    public String getUri() {
        return this.uriString;
    }

    public long open(DataSpec dataSpec) {
        try {
            this.uriString = dataSpec.uri.toString();
            this.inputStream = new FileInputStream(this.resolver.openAssetFileDescriptor(dataSpec.uri, "r").getFileDescriptor());
            if (this.inputStream.skip(dataSpec.position) < dataSpec.position) {
                throw new EOFException();
            }
            if (dataSpec.length != -1) {
                this.bytesRemaining = dataSpec.length;
            } else {
                this.bytesRemaining = (long) this.inputStream.available();
                if (this.bytesRemaining == 0) {
                    this.bytesRemaining = -1;
                }
            }
            this.opened = true;
            if (this.listener != null) {
                this.listener.onTransferStart();
            }
            return this.bytesRemaining;
        } catch (IOException e) {
            throw new ContentDataSourceException(e);
        }
    }

    public int read(byte[] bArr, int i, int i2) {
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            if (this.bytesRemaining != -1) {
                i2 = (int) Math.min(this.bytesRemaining, (long) i2);
            }
            int read = this.inputStream.read(bArr, i, i2);
            if (read <= 0) {
                return read;
            }
            if (this.bytesRemaining != -1) {
                this.bytesRemaining -= (long) read;
            }
            if (this.listener == null) {
                return read;
            }
            this.listener.onBytesTransferred(read);
            return read;
        } catch (IOException e) {
            throw new ContentDataSourceException(e);
        }
    }
}

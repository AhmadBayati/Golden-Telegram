package com.hanista.mobogram.messenger.exoplayer;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import com.hanista.mobogram.messenger.exoplayer.SampleSource.SampleSourceReader;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Callback;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Loadable;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import java.io.IOException;
import java.util.Arrays;

public final class SingleSampleSource implements SampleSource, SampleSourceReader, Callback, Loadable {
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private static final int INITIAL_SAMPLE_SIZE = 1;
    private static final int STATE_END_OF_STREAM = 2;
    private static final int STATE_SEND_FORMAT = 0;
    private static final int STATE_SEND_SAMPLE = 1;
    private IOException currentLoadableException;
    private int currentLoadableExceptionCount;
    private long currentLoadableExceptionTimestamp;
    private final DataSource dataSource;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final int eventSourceId;
    private final MediaFormat format;
    private Loader loader;
    private boolean loadingFinished;
    private final int minLoadableRetryCount;
    private long pendingDiscontinuityPositionUs;
    private byte[] sampleData;
    private int sampleSize;
    private int state;
    private final Uri uri;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.SingleSampleSource.1 */
    class C07151 implements Runnable {
        final /* synthetic */ IOException val$e;

        C07151(IOException iOException) {
            this.val$e = iOException;
        }

        public void run() {
            SingleSampleSource.this.eventListener.onLoadError(SingleSampleSource.this.eventSourceId, this.val$e);
        }
    }

    public interface EventListener {
        void onLoadError(int i, IOException iOException);
    }

    public SingleSampleSource(Uri uri, DataSource dataSource, MediaFormat mediaFormat) {
        this(uri, dataSource, mediaFormat, DEFAULT_MIN_LOADABLE_RETRY_COUNT);
    }

    public SingleSampleSource(Uri uri, DataSource dataSource, MediaFormat mediaFormat, int i) {
        this(uri, dataSource, mediaFormat, i, null, null, STATE_SEND_FORMAT);
    }

    public SingleSampleSource(Uri uri, DataSource dataSource, MediaFormat mediaFormat, int i, Handler handler, EventListener eventListener, int i2) {
        this.uri = uri;
        this.dataSource = dataSource;
        this.format = mediaFormat;
        this.minLoadableRetryCount = i;
        this.eventHandler = handler;
        this.eventListener = eventListener;
        this.eventSourceId = i2;
        this.sampleData = new byte[STATE_SEND_SAMPLE];
    }

    private void clearCurrentLoadableException() {
        this.currentLoadableException = null;
        this.currentLoadableExceptionCount = STATE_SEND_FORMAT;
    }

    private long getRetryDelayMillis(long j) {
        return Math.min((j - 1) * 1000, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
    }

    private void maybeStartLoading() {
        if (!this.loadingFinished && this.state != STATE_END_OF_STREAM && !this.loader.isLoading()) {
            if (this.currentLoadableException != null) {
                if (SystemClock.elapsedRealtime() - this.currentLoadableExceptionTimestamp >= getRetryDelayMillis((long) this.currentLoadableExceptionCount)) {
                    this.currentLoadableException = null;
                } else {
                    return;
                }
            }
            this.loader.startLoading(this, this);
        }
    }

    private void notifyLoadError(IOException iOException) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07151(iOException));
        }
    }

    public void cancelLoad() {
    }

    public boolean continueBuffering(int i, long j) {
        maybeStartLoading();
        return this.loadingFinished;
    }

    public void disable(int i) {
        this.state = STATE_END_OF_STREAM;
    }

    public void enable(int i, long j) {
        this.state = STATE_SEND_FORMAT;
        this.pendingDiscontinuityPositionUs = Long.MIN_VALUE;
        clearCurrentLoadableException();
        maybeStartLoading();
    }

    public long getBufferedPositionUs() {
        return this.loadingFinished ? -3 : 0;
    }

    public MediaFormat getFormat(int i) {
        return this.format;
    }

    public int getTrackCount() {
        return STATE_SEND_SAMPLE;
    }

    public boolean isLoadCanceled() {
        return false;
    }

    public void load() {
        int i = STATE_SEND_FORMAT;
        this.sampleSize = STATE_SEND_FORMAT;
        try {
            this.dataSource.open(new DataSpec(this.uri));
            while (i != -1) {
                this.sampleSize = i + this.sampleSize;
                if (this.sampleSize == this.sampleData.length) {
                    this.sampleData = Arrays.copyOf(this.sampleData, this.sampleData.length * STATE_END_OF_STREAM);
                }
                i = this.dataSource.read(this.sampleData, this.sampleSize, this.sampleData.length - this.sampleSize);
            }
        } finally {
            this.dataSource.close();
        }
    }

    public void maybeThrowError() {
        if (this.currentLoadableException != null && this.currentLoadableExceptionCount > this.minLoadableRetryCount) {
            throw this.currentLoadableException;
        }
    }

    public void onLoadCanceled(Loadable loadable) {
    }

    public void onLoadCompleted(Loadable loadable) {
        this.loadingFinished = true;
        clearCurrentLoadableException();
    }

    public void onLoadError(Loadable loadable, IOException iOException) {
        this.currentLoadableException = iOException;
        this.currentLoadableExceptionCount += STATE_SEND_SAMPLE;
        this.currentLoadableExceptionTimestamp = SystemClock.elapsedRealtime();
        notifyLoadError(iOException);
        maybeStartLoading();
    }

    public boolean prepare(long j) {
        if (this.loader == null) {
            this.loader = new Loader("Loader:" + this.format.mimeType);
        }
        return true;
    }

    public int readData(int i, long j, MediaFormatHolder mediaFormatHolder, SampleHolder sampleHolder) {
        if (this.state == STATE_END_OF_STREAM) {
            return -1;
        }
        if (this.state == 0) {
            mediaFormatHolder.format = this.format;
            this.state = STATE_SEND_SAMPLE;
            return -4;
        }
        Assertions.checkState(this.state == STATE_SEND_SAMPLE);
        if (!this.loadingFinished) {
            return -2;
        }
        sampleHolder.timeUs = 0;
        sampleHolder.size = this.sampleSize;
        sampleHolder.flags = STATE_SEND_SAMPLE;
        sampleHolder.ensureSpaceForWrite(sampleHolder.size);
        sampleHolder.data.put(this.sampleData, STATE_SEND_FORMAT, this.sampleSize);
        this.state = STATE_END_OF_STREAM;
        return -3;
    }

    public long readDiscontinuity(int i) {
        long j = this.pendingDiscontinuityPositionUs;
        this.pendingDiscontinuityPositionUs = Long.MIN_VALUE;
        return j;
    }

    public SampleSourceReader register() {
        return this;
    }

    public void release() {
        if (this.loader != null) {
            this.loader.release();
            this.loader = null;
        }
    }

    public void seekToUs(long j) {
        if (this.state == STATE_END_OF_STREAM) {
            this.pendingDiscontinuityPositionUs = j;
            this.state = STATE_SEND_SAMPLE;
        }
    }
}

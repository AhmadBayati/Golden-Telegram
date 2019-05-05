package com.hanista.mobogram.messenger.exoplayer.util;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Callback;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Loadable;
import com.hanista.mobogram.messenger.exoplayer.upstream.UriDataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.UriLoadable;
import com.hanista.mobogram.messenger.exoplayer.upstream.UriLoadable.Parser;
import java.io.IOException;
import java.util.concurrent.CancellationException;

public class ManifestFetcher<T> implements Callback {
    private long currentLoadStartTimestamp;
    private UriLoadable<T> currentLoadable;
    private int enabledCount;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private ManifestIOException loadException;
    private int loadExceptionCount;
    private long loadExceptionTimestamp;
    private Loader loader;
    private volatile T manifest;
    private volatile long manifestLoadCompleteTimestamp;
    private volatile long manifestLoadStartTimestamp;
    volatile String manifestUri;
    private final Parser<T> parser;
    private final UriDataSource uriDataSource;

    public interface RedirectingManifest {
        String getNextManifestUri();
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.util.ManifestFetcher.1 */
    class C07531 implements Runnable {
        C07531() {
        }

        public void run() {
            ManifestFetcher.this.eventListener.onManifestRefreshStarted();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.util.ManifestFetcher.2 */
    class C07542 implements Runnable {
        C07542() {
        }

        public void run() {
            ManifestFetcher.this.eventListener.onManifestRefreshed();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.util.ManifestFetcher.3 */
    class C07553 implements Runnable {
        final /* synthetic */ IOException val$e;

        C07553(IOException iOException) {
            this.val$e = iOException;
        }

        public void run() {
            ManifestFetcher.this.eventListener.onManifestError(this.val$e);
        }
    }

    public interface EventListener {
        void onManifestError(IOException iOException);

        void onManifestRefreshStarted();

        void onManifestRefreshed();
    }

    public interface ManifestCallback<T> {
        void onSingleManifest(T t);

        void onSingleManifestError(IOException iOException);
    }

    public static final class ManifestIOException extends IOException {
        public ManifestIOException(Throwable th) {
            super(th);
        }
    }

    private class SingleFetchHelper implements Callback {
        private final Looper callbackLooper;
        private long loadStartTimestamp;
        private final UriLoadable<T> singleUseLoadable;
        private final Loader singleUseLoader;
        private final ManifestCallback<T> wrappedCallback;

        public SingleFetchHelper(UriLoadable<T> uriLoadable, Looper looper, ManifestCallback<T> manifestCallback) {
            this.singleUseLoadable = uriLoadable;
            this.callbackLooper = looper;
            this.wrappedCallback = manifestCallback;
            this.singleUseLoader = new Loader("manifestLoader:single");
        }

        private void releaseLoader() {
            this.singleUseLoader.release();
        }

        public void onLoadCanceled(Loadable loadable) {
            try {
                this.wrappedCallback.onSingleManifestError(new ManifestIOException(new CancellationException()));
            } finally {
                releaseLoader();
            }
        }

        public void onLoadCompleted(Loadable loadable) {
            try {
                Object result = this.singleUseLoadable.getResult();
                ManifestFetcher.this.onSingleFetchCompleted(result, this.loadStartTimestamp);
                this.wrappedCallback.onSingleManifest(result);
            } finally {
                releaseLoader();
            }
        }

        public void onLoadError(Loadable loadable, IOException iOException) {
            try {
                this.wrappedCallback.onSingleManifestError(iOException);
            } finally {
                releaseLoader();
            }
        }

        public void startLoading() {
            this.loadStartTimestamp = SystemClock.elapsedRealtime();
            this.singleUseLoader.startLoading(this.callbackLooper, this.singleUseLoadable, this);
        }
    }

    public ManifestFetcher(String str, UriDataSource uriDataSource, Parser<T> parser) {
        this(str, uriDataSource, parser, null, null);
    }

    public ManifestFetcher(String str, UriDataSource uriDataSource, Parser<T> parser, Handler handler, EventListener eventListener) {
        this.parser = parser;
        this.manifestUri = str;
        this.uriDataSource = uriDataSource;
        this.eventHandler = handler;
        this.eventListener = eventListener;
    }

    private long getRetryDelayMillis(long j) {
        return Math.min((j - 1) * 1000, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
    }

    private void notifyManifestError(IOException iOException) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07553(iOException));
        }
    }

    private void notifyManifestRefreshStarted() {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07531());
        }
    }

    private void notifyManifestRefreshed() {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07542());
        }
    }

    public void disable() {
        int i = this.enabledCount - 1;
        this.enabledCount = i;
        if (i == 0 && this.loader != null) {
            this.loader.release();
            this.loader = null;
        }
    }

    public void enable() {
        int i = this.enabledCount;
        this.enabledCount = i + 1;
        if (i == 0) {
            this.loadExceptionCount = 0;
            this.loadException = null;
        }
    }

    public T getManifest() {
        return this.manifest;
    }

    public long getManifestLoadCompleteTimestamp() {
        return this.manifestLoadCompleteTimestamp;
    }

    public long getManifestLoadStartTimestamp() {
        return this.manifestLoadStartTimestamp;
    }

    public void maybeThrowError() {
        if (this.loadException != null && this.loadExceptionCount > 1) {
            throw this.loadException;
        }
    }

    public void onLoadCanceled(Loadable loadable) {
    }

    public void onLoadCompleted(Loadable loadable) {
        if (this.currentLoadable == loadable) {
            this.manifest = this.currentLoadable.getResult();
            this.manifestLoadStartTimestamp = this.currentLoadStartTimestamp;
            this.manifestLoadCompleteTimestamp = SystemClock.elapsedRealtime();
            this.loadExceptionCount = 0;
            this.loadException = null;
            if (this.manifest instanceof RedirectingManifest) {
                Object nextManifestUri = ((RedirectingManifest) this.manifest).getNextManifestUri();
                if (!TextUtils.isEmpty(nextManifestUri)) {
                    this.manifestUri = nextManifestUri;
                }
            }
            notifyManifestRefreshed();
        }
    }

    public void onLoadError(Loadable loadable, IOException iOException) {
        if (this.currentLoadable == loadable) {
            this.loadExceptionCount++;
            this.loadExceptionTimestamp = SystemClock.elapsedRealtime();
            this.loadException = new ManifestIOException(iOException);
            notifyManifestError(this.loadException);
        }
    }

    void onSingleFetchCompleted(T t, long j) {
        this.manifest = t;
        this.manifestLoadStartTimestamp = j;
        this.manifestLoadCompleteTimestamp = SystemClock.elapsedRealtime();
    }

    public void requestRefresh() {
        if (this.loadException == null || SystemClock.elapsedRealtime() >= this.loadExceptionTimestamp + getRetryDelayMillis((long) this.loadExceptionCount)) {
            if (this.loader == null) {
                this.loader = new Loader("manifestLoader");
            }
            if (!this.loader.isLoading()) {
                this.currentLoadable = new UriLoadable(this.manifestUri, this.uriDataSource, this.parser);
                this.currentLoadStartTimestamp = SystemClock.elapsedRealtime();
                this.loader.startLoading(this.currentLoadable, this);
                notifyManifestRefreshStarted();
            }
        }
    }

    public void singleLoad(Looper looper, ManifestCallback<T> manifestCallback) {
        new SingleFetchHelper(new UriLoadable(this.manifestUri, this.uriDataSource, this.parser), looper, manifestCallback).startLoading();
    }

    public void updateManifestUri(String str) {
        this.manifestUri = str;
    }
}

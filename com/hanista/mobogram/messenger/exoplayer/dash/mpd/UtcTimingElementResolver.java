package com.hanista.mobogram.messenger.exoplayer.dash.mpd;

import android.os.SystemClock;
import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Callback;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Loadable;
import com.hanista.mobogram.messenger.exoplayer.upstream.UriDataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.UriLoadable;
import com.hanista.mobogram.messenger.exoplayer.upstream.UriLoadable.Parser;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CancellationException;

public final class UtcTimingElementResolver implements Callback {
    private final UtcTimingCallback callback;
    private UriLoadable<Long> singleUseLoadable;
    private Loader singleUseLoader;
    private final UtcTimingElement timingElement;
    private final long timingElementElapsedRealtime;
    private final UriDataSource uriDataSource;

    private static class Iso8601Parser implements Parser<Long> {
        private Iso8601Parser() {
        }

        public Long parse(String str, InputStream inputStream) {
            String readLine = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                return Long.valueOf(simpleDateFormat.parse(readLine).getTime());
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    public interface UtcTimingCallback {
        void onTimestampError(UtcTimingElement utcTimingElement, IOException iOException);

        void onTimestampResolved(UtcTimingElement utcTimingElement, long j);
    }

    private static class XsDateTimeParser implements Parser<Long> {
        private XsDateTimeParser() {
        }

        public Long parse(String str, InputStream inputStream) {
            try {
                return Long.valueOf(Util.parseXsDateTime(new BufferedReader(new InputStreamReader(inputStream)).readLine()));
            } catch (Throwable e) {
                throw new ParserException(e);
            }
        }
    }

    private UtcTimingElementResolver(UriDataSource uriDataSource, UtcTimingElement utcTimingElement, long j, UtcTimingCallback utcTimingCallback) {
        this.uriDataSource = uriDataSource;
        this.timingElement = (UtcTimingElement) Assertions.checkNotNull(utcTimingElement);
        this.timingElementElapsedRealtime = j;
        this.callback = (UtcTimingCallback) Assertions.checkNotNull(utcTimingCallback);
    }

    private void releaseLoader() {
        this.singleUseLoader.release();
    }

    private void resolve() {
        String str = this.timingElement.schemeIdUri;
        if (Util.areEqual(str, "urn:mpeg:dash:utc:direct:2012")) {
            resolveDirect();
        } else if (Util.areEqual(str, "urn:mpeg:dash:utc:http-iso:2014")) {
            resolveHttp(new Iso8601Parser());
        } else if (Util.areEqual(str, "urn:mpeg:dash:utc:http-xsdate:2012") || Util.areEqual(str, "urn:mpeg:dash:utc:http-xsdate:2014")) {
            resolveHttp(new XsDateTimeParser());
        } else {
            this.callback.onTimestampError(this.timingElement, new IOException("Unsupported utc timing scheme"));
        }
    }

    private void resolveDirect() {
        try {
            this.callback.onTimestampResolved(this.timingElement, Util.parseXsDateTime(this.timingElement.value) - this.timingElementElapsedRealtime);
        } catch (Throwable e) {
            this.callback.onTimestampError(this.timingElement, new ParserException(e));
        }
    }

    private void resolveHttp(Parser<Long> parser) {
        this.singleUseLoader = new Loader("utctiming");
        this.singleUseLoadable = new UriLoadable(this.timingElement.value, this.uriDataSource, parser);
        this.singleUseLoader.startLoading(this.singleUseLoadable, this);
    }

    public static void resolveTimingElement(UriDataSource uriDataSource, UtcTimingElement utcTimingElement, long j, UtcTimingCallback utcTimingCallback) {
        new UtcTimingElementResolver(uriDataSource, utcTimingElement, j, utcTimingCallback).resolve();
    }

    public void onLoadCanceled(Loadable loadable) {
        onLoadError(loadable, new IOException("Load cancelled", new CancellationException()));
    }

    public void onLoadCompleted(Loadable loadable) {
        releaseLoader();
        this.callback.onTimestampResolved(this.timingElement, ((Long) this.singleUseLoadable.getResult()).longValue() - SystemClock.elapsedRealtime());
    }

    public void onLoadError(Loadable loadable, IOException iOException) {
        releaseLoader();
        this.callback.onTimestampError(this.timingElement, iOException);
    }
}

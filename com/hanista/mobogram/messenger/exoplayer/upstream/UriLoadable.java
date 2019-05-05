package com.hanista.mobogram.messenger.exoplayer.upstream;

import android.net.Uri;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Loadable;
import java.io.InputStream;

public final class UriLoadable<T> implements Loadable {
    private final DataSpec dataSpec;
    private volatile boolean isCanceled;
    private final Parser<T> parser;
    private volatile T result;
    private final UriDataSource uriDataSource;

    public interface Parser<T> {
        T parse(String str, InputStream inputStream);
    }

    public UriLoadable(String str, UriDataSource uriDataSource, Parser<T> parser) {
        this.uriDataSource = uriDataSource;
        this.parser = parser;
        this.dataSpec = new DataSpec(Uri.parse(str), 1);
    }

    public final void cancelLoad() {
        this.isCanceled = true;
    }

    public final T getResult() {
        return this.result;
    }

    public final boolean isLoadCanceled() {
        return this.isCanceled;
    }

    public final void load() {
        InputStream dataSourceInputStream = new DataSourceInputStream(this.uriDataSource, this.dataSpec);
        try {
            dataSourceInputStream.open();
            this.result = this.parser.parse(this.uriDataSource.getUri(), dataSourceInputStream);
        } finally {
            dataSourceInputStream.close();
        }
    }
}

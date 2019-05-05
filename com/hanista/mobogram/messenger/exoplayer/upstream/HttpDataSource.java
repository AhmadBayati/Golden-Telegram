package com.hanista.mobogram.messenger.exoplayer.upstream;

import android.text.TextUtils;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.Predicate;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface HttpDataSource extends UriDataSource {
    public static final Predicate<String> REJECT_PAYWALL_TYPES;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.upstream.HttpDataSource.1 */
    static class C07501 implements Predicate<String> {
        C07501() {
        }

        public boolean evaluate(String str) {
            String toLowerInvariant = Util.toLowerInvariant(str);
            return (TextUtils.isEmpty(toLowerInvariant) || ((toLowerInvariant.contains(MimeTypes.BASE_TYPE_TEXT) && !toLowerInvariant.contains(MimeTypes.TEXT_VTT)) || toLowerInvariant.contains("html") || toLowerInvariant.contains("xml"))) ? false : true;
        }
    }

    public static class HttpDataSourceException extends IOException {
        public static final int TYPE_CLOSE = 3;
        public static final int TYPE_OPEN = 1;
        public static final int TYPE_READ = 2;
        public final DataSpec dataSpec;
        public final int type;

        public HttpDataSourceException(DataSpec dataSpec, int i) {
            this.dataSpec = dataSpec;
            this.type = i;
        }

        public HttpDataSourceException(IOException iOException, DataSpec dataSpec, int i) {
            super(iOException);
            this.dataSpec = dataSpec;
            this.type = i;
        }

        public HttpDataSourceException(String str, DataSpec dataSpec, int i) {
            super(str);
            this.dataSpec = dataSpec;
            this.type = i;
        }

        public HttpDataSourceException(String str, IOException iOException, DataSpec dataSpec, int i) {
            super(str, iOException);
            this.dataSpec = dataSpec;
            this.type = i;
        }
    }

    public static final class InvalidContentTypeException extends HttpDataSourceException {
        public final String contentType;

        public InvalidContentTypeException(String str, DataSpec dataSpec) {
            super("Invalid content type: " + str, dataSpec, 1);
            this.contentType = str;
        }
    }

    public static final class InvalidResponseCodeException extends HttpDataSourceException {
        public final Map<String, List<String>> headerFields;
        public final int responseCode;

        public InvalidResponseCodeException(int i, Map<String, List<String>> map, DataSpec dataSpec) {
            super("Response code: " + i, dataSpec, 1);
            this.responseCode = i;
            this.headerFields = map;
        }
    }

    static {
        REJECT_PAYWALL_TYPES = new C07501();
    }

    void clearAllRequestProperties();

    void clearRequestProperty(String str);

    void close();

    Map<String, List<String>> getResponseHeaders();

    long open(DataSpec dataSpec);

    int read(byte[] bArr, int i, int i2);

    void setRequestProperty(String str, String str2);
}

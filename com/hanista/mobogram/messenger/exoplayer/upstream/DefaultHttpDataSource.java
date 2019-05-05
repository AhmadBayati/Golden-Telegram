package com.hanista.mobogram.messenger.exoplayer.upstream;

import android.text.TextUtils;
import android.util.Log;
import com.hanista.mobogram.messenger.exoplayer.upstream.HttpDataSource.HttpDataSourceException;
import com.hanista.mobogram.messenger.exoplayer.upstream.HttpDataSource.InvalidContentTypeException;
import com.hanista.mobogram.messenger.exoplayer.upstream.HttpDataSource.InvalidResponseCodeException;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.Predicate;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultHttpDataSource implements HttpDataSource {
    private static final Pattern CONTENT_RANGE_HEADER;
    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 8000;
    public static final int DEFAULT_READ_TIMEOUT_MILLIS = 8000;
    private static final int MAX_REDIRECTS = 20;
    private static final String TAG = "DefaultHttpDataSource";
    private static final AtomicReference<byte[]> skipBufferReference;
    private final boolean allowCrossProtocolRedirects;
    private long bytesRead;
    private long bytesSkipped;
    private long bytesToRead;
    private long bytesToSkip;
    private final int connectTimeoutMillis;
    private HttpURLConnection connection;
    private final Predicate<String> contentTypePredicate;
    private DataSpec dataSpec;
    private InputStream inputStream;
    private final TransferListener listener;
    private boolean opened;
    private final int readTimeoutMillis;
    private final HashMap<String, String> requestProperties;
    private final String userAgent;

    static {
        CONTENT_RANGE_HEADER = Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+)$");
        skipBufferReference = new AtomicReference();
    }

    public DefaultHttpDataSource(String str, Predicate<String> predicate) {
        this(str, predicate, null);
    }

    public DefaultHttpDataSource(String str, Predicate<String> predicate, TransferListener transferListener) {
        this(str, predicate, transferListener, DEFAULT_READ_TIMEOUT_MILLIS, DEFAULT_READ_TIMEOUT_MILLIS);
    }

    public DefaultHttpDataSource(String str, Predicate<String> predicate, TransferListener transferListener, int i, int i2) {
        this(str, predicate, transferListener, i, i2, false);
    }

    public DefaultHttpDataSource(String str, Predicate<String> predicate, TransferListener transferListener, int i, int i2, boolean z) {
        this.userAgent = Assertions.checkNotEmpty(str);
        this.contentTypePredicate = predicate;
        this.listener = transferListener;
        this.requestProperties = new HashMap();
        this.connectTimeoutMillis = i;
        this.readTimeoutMillis = i2;
        this.allowCrossProtocolRedirects = z;
    }

    private void closeConnectionQuietly() {
        if (this.connection != null) {
            try {
                this.connection.disconnect();
            } catch (Throwable e) {
                Log.e(TAG, "Unexpected error while disconnecting", e);
            }
            this.connection = null;
        }
    }

    private static long getContentLength(HttpURLConnection httpURLConnection) {
        long j = -1;
        String headerField = httpURLConnection.getHeaderField("Content-Length");
        if (!TextUtils.isEmpty(headerField)) {
            try {
                j = Long.parseLong(headerField);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Unexpected Content-Length [" + headerField + "]");
            }
        }
        String headerField2 = httpURLConnection.getHeaderField("Content-Range");
        if (TextUtils.isEmpty(headerField2)) {
            return j;
        }
        Matcher matcher = CONTENT_RANGE_HEADER.matcher(headerField2);
        if (!matcher.find()) {
            return j;
        }
        try {
            long parseLong = (Long.parseLong(matcher.group(2)) - Long.parseLong(matcher.group(1))) + 1;
            if (j < 0) {
                return parseLong;
            }
            if (j == parseLong) {
                return j;
            }
            Log.w(TAG, "Inconsistent headers [" + headerField + "] [" + headerField2 + "]");
            return Math.max(j, parseLong);
        } catch (NumberFormatException e2) {
            Log.e(TAG, "Unexpected Content-Range [" + headerField2 + "]");
            return j;
        }
    }

    private static URL handleRedirect(URL url, String str) {
        if (str == null) {
            throw new ProtocolException("Null location redirect");
        }
        URL url2 = new URL(url, str);
        String protocol = url2.getProtocol();
        if ("https".equals(protocol) || "http".equals(protocol)) {
            return url2;
        }
        throw new ProtocolException("Unsupported protocol redirect: " + protocol);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.net.HttpURLConnection makeConnection(com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec r13) {
        /*
        r12 = this;
        r9 = 1;
        r0 = 0;
        r2 = new java.net.URL;
        r1 = r13.uri;
        r1 = r1.toString();
        r2.<init>(r1);
        r3 = r13.postBody;
        r4 = r13.position;
        r6 = r13.length;
        r1 = r13.flags;
        r1 = r1 & 1;
        if (r1 == 0) goto L_0x0024;
    L_0x0019:
        r8 = r9;
    L_0x001a:
        r1 = r12.allowCrossProtocolRedirects;
        if (r1 != 0) goto L_0x0026;
    L_0x001e:
        r1 = r12;
        r0 = r1.makeConnection(r2, r3, r4, r6, r8, r9);
    L_0x0023:
        return r0;
    L_0x0024:
        r8 = r0;
        goto L_0x001a;
    L_0x0026:
        r1 = r0;
    L_0x0027:
        r10 = r1 + 1;
        r9 = 20;
        if (r1 > r9) goto L_0x0064;
    L_0x002d:
        r1 = r12;
        r9 = r0;
        r1 = r1.makeConnection(r2, r3, r4, r6, r8, r9);
        r9 = r1.getResponseCode();
        r11 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        if (r9 == r11) goto L_0x0051;
    L_0x003b:
        r11 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        if (r9 == r11) goto L_0x0051;
    L_0x003f:
        r11 = 302; // 0x12e float:4.23E-43 double:1.49E-321;
        if (r9 == r11) goto L_0x0051;
    L_0x0043:
        r11 = 303; // 0x12f float:4.25E-43 double:1.497E-321;
        if (r9 == r11) goto L_0x0051;
    L_0x0047:
        if (r3 != 0) goto L_0x0062;
    L_0x0049:
        r3 = 307; // 0x133 float:4.3E-43 double:1.517E-321;
        if (r9 == r3) goto L_0x0051;
    L_0x004d:
        r3 = 308; // 0x134 float:4.32E-43 double:1.52E-321;
        if (r9 != r3) goto L_0x0062;
    L_0x0051:
        r3 = 0;
        r9 = "Location";
        r9 = r1.getHeaderField(r9);
        r1.disconnect();
        r2 = handleRedirect(r2, r9);
        r1 = r10;
        goto L_0x0027;
    L_0x0062:
        r0 = r1;
        goto L_0x0023;
    L_0x0064:
        r0 = new java.net.NoRouteToHostException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Too many redirects: ";
        r1 = r1.append(r2);
        r1 = r1.append(r10);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.upstream.DefaultHttpDataSource.makeConnection(com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec):java.net.HttpURLConnection");
    }

    private HttpURLConnection makeConnection(URL url, byte[] bArr, long j, long j2, boolean z, boolean z2) {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(this.connectTimeoutMillis);
        httpURLConnection.setReadTimeout(this.readTimeoutMillis);
        synchronized (this.requestProperties) {
            for (Entry entry : this.requestProperties.entrySet()) {
                httpURLConnection.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
            }
        }
        if (!(j == 0 && j2 == -1)) {
            String str = "bytes=" + j + "-";
            if (j2 != -1) {
                str = str + ((j + j2) - 1);
            }
            httpURLConnection.setRequestProperty("Range", str);
        }
        httpURLConnection.setRequestProperty("User-Agent", this.userAgent);
        if (!z) {
            httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
        }
        httpURLConnection.setInstanceFollowRedirects(z2);
        httpURLConnection.setDoOutput(bArr != null);
        if (bArr != null) {
            httpURLConnection.setFixedLengthStreamingMode(bArr.length);
            httpURLConnection.connect();
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(bArr);
            outputStream.close();
        } else {
            httpURLConnection.connect();
        }
        return httpURLConnection;
    }

    private int readInternal(byte[] bArr, int i, int i2) {
        if (this.bytesToRead != -1) {
            i2 = (int) Math.min((long) i2, this.bytesToRead - this.bytesRead);
        }
        if (i2 == 0) {
            return -1;
        }
        int read = this.inputStream.read(bArr, i, i2);
        if (read != -1) {
            this.bytesRead += (long) read;
            if (this.listener != null) {
                this.listener.onBytesTransferred(read);
            }
            return read;
        } else if (this.bytesToRead == -1 || this.bytesToRead == this.bytesRead) {
            return -1;
        } else {
            throw new EOFException();
        }
    }

    private void skipInternal() {
        if (this.bytesSkipped != this.bytesToSkip) {
            Object obj = (byte[]) skipBufferReference.getAndSet(null);
            if (obj == null) {
                obj = new byte[ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT];
            }
            while (this.bytesSkipped != this.bytesToSkip) {
                int read = this.inputStream.read(obj, 0, (int) Math.min(this.bytesToSkip - this.bytesSkipped, (long) obj.length));
                if (Thread.interrupted()) {
                    throw new InterruptedIOException();
                } else if (read == -1) {
                    throw new EOFException();
                } else {
                    this.bytesSkipped += (long) read;
                    if (this.listener != null) {
                        this.listener.onBytesTransferred(read);
                    }
                }
            }
            skipBufferReference.set(obj);
        }
    }

    protected final long bytesRead() {
        return this.bytesRead;
    }

    protected final long bytesRemaining() {
        return this.bytesToRead == -1 ? this.bytesToRead : this.bytesToRead - this.bytesRead;
    }

    protected final long bytesSkipped() {
        return this.bytesSkipped;
    }

    public void clearAllRequestProperties() {
        synchronized (this.requestProperties) {
            this.requestProperties.clear();
        }
    }

    public void clearRequestProperty(String str) {
        Assertions.checkNotNull(str);
        synchronized (this.requestProperties) {
            this.requestProperties.remove(str);
        }
    }

    public void close() {
        try {
            if (this.inputStream != null) {
                Util.maybeTerminateInputStream(this.connection, bytesRemaining());
                this.inputStream.close();
            }
            this.inputStream = null;
            closeConnectionQuietly();
            if (this.opened) {
                this.opened = false;
                if (this.listener != null) {
                    this.listener.onTransferEnd();
                }
            }
        } catch (IOException e) {
            throw new HttpDataSourceException(e, this.dataSpec, 3);
        } catch (Throwable th) {
            this.inputStream = null;
            closeConnectionQuietly();
            if (this.opened) {
                this.opened = false;
                if (this.listener != null) {
                    this.listener.onTransferEnd();
                }
            }
        }
    }

    protected final HttpURLConnection getConnection() {
        return this.connection;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.connection == null ? null : this.connection.getHeaderFields();
    }

    public String getUri() {
        return this.connection == null ? null : this.connection.getURL().toString();
    }

    public long open(DataSpec dataSpec) {
        long j = 0;
        this.dataSpec = dataSpec;
        this.bytesRead = 0;
        this.bytesSkipped = 0;
        try {
            this.connection = makeConnection(dataSpec);
            try {
                int responseCode = this.connection.getResponseCode();
                if (responseCode < Callback.DEFAULT_DRAG_ANIMATION_DURATION || responseCode > 299) {
                    Map headerFields = this.connection.getHeaderFields();
                    closeConnectionQuietly();
                    throw new InvalidResponseCodeException(responseCode, headerFields, dataSpec);
                }
                String contentType = this.connection.getContentType();
                if (this.contentTypePredicate == null || this.contentTypePredicate.evaluate(contentType)) {
                    if (responseCode == Callback.DEFAULT_DRAG_ANIMATION_DURATION && dataSpec.position != 0) {
                        j = dataSpec.position;
                    }
                    this.bytesToSkip = j;
                    if ((dataSpec.flags & 1) == 0) {
                        j = getContentLength(this.connection);
                        j = dataSpec.length != -1 ? dataSpec.length : j != -1 ? j - this.bytesToSkip : -1;
                        this.bytesToRead = j;
                    } else {
                        this.bytesToRead = dataSpec.length;
                    }
                    try {
                        this.inputStream = this.connection.getInputStream();
                        this.opened = true;
                        if (this.listener != null) {
                            this.listener.onTransferStart();
                        }
                        return this.bytesToRead;
                    } catch (IOException e) {
                        closeConnectionQuietly();
                        throw new HttpDataSourceException(e, dataSpec, 1);
                    }
                }
                closeConnectionQuietly();
                throw new InvalidContentTypeException(contentType, dataSpec);
            } catch (IOException e2) {
                closeConnectionQuietly();
                throw new HttpDataSourceException("Unable to connect to " + dataSpec.uri.toString(), e2, dataSpec, 1);
            }
        } catch (IOException e22) {
            throw new HttpDataSourceException("Unable to connect to " + dataSpec.uri.toString(), e22, dataSpec, 1);
        }
    }

    public int read(byte[] bArr, int i, int i2) {
        try {
            skipInternal();
            return readInternal(bArr, i, i2);
        } catch (IOException e) {
            throw new HttpDataSourceException(e, this.dataSpec, 2);
        }
    }

    public void setRequestProperty(String str, String str2) {
        Assertions.checkNotNull(str);
        Assertions.checkNotNull(str2);
        synchronized (this.requestProperties) {
            this.requestProperties.put(str, str2);
        }
    }
}

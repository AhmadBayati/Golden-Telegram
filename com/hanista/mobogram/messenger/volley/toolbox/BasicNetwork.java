package com.hanista.mobogram.messenger.volley.toolbox;

import android.os.SystemClock;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.volley.Cache.Entry;
import com.hanista.mobogram.messenger.volley.Network;
import com.hanista.mobogram.messenger.volley.Request;
import com.hanista.mobogram.messenger.volley.RetryPolicy;
import com.hanista.mobogram.messenger.volley.ServerError;
import com.hanista.mobogram.messenger.volley.VolleyError;
import com.hanista.mobogram.messenger.volley.VolleyLog;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.impl.cookie.DateUtils;

public class BasicNetwork implements Network {
    protected static final boolean DEBUG;
    private static int DEFAULT_POOL_SIZE;
    private static int SLOW_REQUEST_THRESHOLD_MS;
    protected final HttpStack mHttpStack;
    protected final ByteArrayPool mPool;

    static {
        DEBUG = VolleyLog.DEBUG;
        SLOW_REQUEST_THRESHOLD_MS = 3000;
        DEFAULT_POOL_SIZE = ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT;
    }

    public BasicNetwork(HttpStack httpStack) {
        this(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    public BasicNetwork(HttpStack httpStack, ByteArrayPool byteArrayPool) {
        this.mHttpStack = httpStack;
        this.mPool = byteArrayPool;
    }

    private void addCacheHeaders(Map<String, String> map, Entry entry) {
        if (entry != null) {
            if (entry.etag != null) {
                map.put("If-None-Match", entry.etag);
            }
            if (entry.lastModified > 0) {
                map.put("If-Modified-Since", DateUtils.formatDate(new Date(entry.lastModified)));
            }
        }
    }

    private static void attemptRetryOnException(String str, Request<?> request, VolleyError volleyError) {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int timeoutMs = request.getTimeoutMs();
        try {
            retryPolicy.retry(volleyError);
            request.addMarker(String.format("%s-retry [timeout=%s]", new Object[]{str, Integer.valueOf(timeoutMs)}));
        } catch (VolleyError e) {
            request.addMarker(String.format("%s-timeout-giveup [timeout=%s]", new Object[]{str, Integer.valueOf(timeoutMs)}));
            throw e;
        }
    }

    protected static Map<String, String> convertHeaders(Header[] headerArr) {
        Map<String, String> treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headerArr.length; i++) {
            treeMap.put(headerArr[i].getName(), headerArr[i].getValue());
        }
        return treeMap;
    }

    private byte[] entityToBytes(HttpEntity httpEntity) {
        PoolingByteArrayOutputStream poolingByteArrayOutputStream = new PoolingByteArrayOutputStream(this.mPool, (int) httpEntity.getContentLength());
        byte[] bArr = null;
        try {
            InputStream content = httpEntity.getContent();
            if (content == null) {
                throw new ServerError();
            }
            bArr = this.mPool.getBuf(TLRPC.MESSAGE_FLAG_HAS_VIEWS);
            while (true) {
                int read = content.read(bArr);
                if (read == -1) {
                    break;
                }
                poolingByteArrayOutputStream.write(bArr, 0, read);
            }
            byte[] toByteArray = poolingByteArrayOutputStream.toByteArray();
            return toByteArray;
        } finally {
            try {
                httpEntity.consumeContent();
            } catch (IOException e) {
                VolleyLog.m202v("Error occured when calling consumingContent", new Object[0]);
            }
            this.mPool.returnBuf(bArr);
            poolingByteArrayOutputStream.close();
        }
    }

    private void logSlowRequests(long j, Request<?> request, byte[] bArr, StatusLine statusLine) {
        if (DEBUG || j > ((long) SLOW_REQUEST_THRESHOLD_MS)) {
            String str = "HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]";
            Object[] objArr = new Object[5];
            objArr[0] = request;
            objArr[1] = Long.valueOf(j);
            objArr[2] = bArr != null ? Integer.valueOf(bArr.length) : "null";
            objArr[3] = Integer.valueOf(statusLine.getStatusCode());
            objArr[4] = Integer.valueOf(request.getRetryPolicy().getCurrentRetryCount());
            VolleyLog.m199d(str, objArr);
        }
    }

    protected void logError(String str, String str2, long j) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        VolleyLog.m202v("HTTP ERROR(%s) %d ms to fetch %s", str, Long.valueOf(elapsedRealtime - j), str2);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.hanista.mobogram.messenger.volley.NetworkResponse performRequest(com.hanista.mobogram.messenger.volley.Request<?> r19) {
        /*
        r18 = this;
        r16 = android.os.SystemClock.elapsedRealtime();
    L_0x0004:
        r3 = 0;
        r14 = 0;
        r6 = java.util.Collections.emptyMap();
        r2 = new java.util.HashMap;	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x00e1 }
        r2.<init>();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x00e1 }
        r4 = r19.getCacheEntry();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x00e1 }
        r0 = r18;
        r0.addCacheHeaders(r2, r4);	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x00e1 }
        r0 = r18;
        r4 = r0.mHttpStack;	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x00e1 }
        r0 = r19;
        r15 = r4.performRequest(r0, r2);	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x00e1 }
        r12 = r15.getStatusLine();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r4 = r12.getStatusCode();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r2 = r15.getAllHeaders();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r6 = convertHeaders(r2);	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r2 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
        if (r4 != r2) goto L_0x0065;
    L_0x0036:
        r2 = r19.getCacheEntry();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        if (r2 != 0) goto L_0x004c;
    L_0x003c:
        r3 = new com.hanista.mobogram.messenger.volley.NetworkResponse;	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r4 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
        r5 = 0;
        r7 = 1;
        r8 = android.os.SystemClock.elapsedRealtime();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r8 = r8 - r16;
        r3.<init>(r4, r5, r6, r7, r8);	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
    L_0x004b:
        return r3;
    L_0x004c:
        r3 = r2.responseHeaders;	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r3.putAll(r6);	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r7 = new com.hanista.mobogram.messenger.volley.NetworkResponse;	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r8 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
        r9 = r2.data;	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r10 = r2.responseHeaders;	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r11 = 1;
        r2 = android.os.SystemClock.elapsedRealtime();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r12 = r2 - r16;
        r7.<init>(r8, r9, r10, r11, r12);	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r3 = r7;
        goto L_0x004b;
    L_0x0065:
        r2 = r15.getEntity();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        if (r2 == 0) goto L_0x00a0;
    L_0x006b:
        r2 = r15.getEntity();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        r0 = r18;
        r11 = r0.entityToBytes(r2);	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
    L_0x0075:
        r2 = android.os.SystemClock.elapsedRealtime();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x0140 }
        r8 = r2 - r16;
        r7 = r18;
        r10 = r19;
        r7.logSlowRequests(r8, r10, r11, r12);	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x0140 }
        r2 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r4 < r2) goto L_0x008a;
    L_0x0086:
        r2 = 299; // 0x12b float:4.19E-43 double:1.477E-321;
        if (r4 <= r2) goto L_0x00a4;
    L_0x008a:
        r2 = new java.io.IOException;	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x0140 }
        r2.<init>();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x0140 }
        throw r2;	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x0140 }
    L_0x0090:
        r2 = move-exception;
        r2 = "socket";
        r3 = new com.hanista.mobogram.messenger.volley.TimeoutError;
        r3.<init>();
        r0 = r19;
        attemptRetryOnException(r2, r0, r3);
        goto L_0x0004;
    L_0x00a0:
        r2 = 0;
        r11 = new byte[r2];	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x013c }
        goto L_0x0075;
    L_0x00a4:
        r3 = new com.hanista.mobogram.messenger.volley.NetworkResponse;	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x0140 }
        r7 = 0;
        r8 = android.os.SystemClock.elapsedRealtime();	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x0140 }
        r8 = r8 - r16;
        r5 = r11;
        r3.<init>(r4, r5, r6, r7, r8);	 Catch:{ SocketTimeoutException -> 0x0090, ConnectTimeoutException -> 0x00b2, MalformedURLException -> 0x00c2, IOException -> 0x0140 }
        goto L_0x004b;
    L_0x00b2:
        r2 = move-exception;
        r2 = "connection";
        r3 = new com.hanista.mobogram.messenger.volley.TimeoutError;
        r3.<init>();
        r0 = r19;
        attemptRetryOnException(r2, r0, r3);
        goto L_0x0004;
    L_0x00c2:
        r2 = move-exception;
        r3 = new java.lang.RuntimeException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Bad URL ";
        r4 = r4.append(r5);
        r5 = r19.getUrl();
        r4 = r4.append(r5);
        r4 = r4.toString();
        r3.<init>(r4, r2);
        throw r3;
    L_0x00e1:
        r2 = move-exception;
        r5 = r14;
    L_0x00e3:
        r7 = 0;
        if (r3 == 0) goto L_0x012a;
    L_0x00e6:
        r2 = r3.getStatusLine();
        r4 = r2.getStatusCode();
        r2 = "Unexpected response code %d for %s";
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r8 = 0;
        r9 = java.lang.Integer.valueOf(r4);
        r3[r8] = r9;
        r8 = 1;
        r9 = r19.getUrl();
        r3[r8] = r9;
        com.hanista.mobogram.messenger.volley.VolleyLog.m200e(r2, r3);
        if (r5 == 0) goto L_0x0136;
    L_0x0107:
        r3 = new com.hanista.mobogram.messenger.volley.NetworkResponse;
        r7 = 0;
        r8 = android.os.SystemClock.elapsedRealtime();
        r8 = r8 - r16;
        r3.<init>(r4, r5, r6, r7, r8);
        r2 = 401; // 0x191 float:5.62E-43 double:1.98E-321;
        if (r4 == r2) goto L_0x011b;
    L_0x0117:
        r2 = 403; // 0x193 float:5.65E-43 double:1.99E-321;
        if (r4 != r2) goto L_0x0130;
    L_0x011b:
        r2 = "auth";
        r4 = new com.hanista.mobogram.messenger.volley.AuthFailureError;
        r4.<init>(r3);
        r0 = r19;
        attemptRetryOnException(r2, r0, r4);
        goto L_0x0004;
    L_0x012a:
        r3 = new com.hanista.mobogram.messenger.volley.NoConnectionError;
        r3.<init>(r2);
        throw r3;
    L_0x0130:
        r2 = new com.hanista.mobogram.messenger.volley.ServerError;
        r2.<init>(r3);
        throw r2;
    L_0x0136:
        r2 = new com.hanista.mobogram.messenger.volley.NetworkError;
        r2.<init>(r7);
        throw r2;
    L_0x013c:
        r2 = move-exception;
        r5 = r14;
        r3 = r15;
        goto L_0x00e3;
    L_0x0140:
        r2 = move-exception;
        r5 = r11;
        r3 = r15;
        goto L_0x00e3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.volley.toolbox.BasicNetwork.performRequest(com.hanista.mobogram.messenger.volley.Request):com.hanista.mobogram.messenger.volley.NetworkResponse");
    }
}

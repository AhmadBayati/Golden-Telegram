package com.hanista.mobogram.mobo.notificationservice;

import android.util.Log;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/* renamed from: com.hanista.mobogram.mobo.notificationservice.b */
public class RestClient {
    public static final Integer f1973a;
    private static final String f1974b;
    private static final Integer f1975c;
    private DefaultHttpClient f1976d;
    private String f1977e;

    /* renamed from: com.hanista.mobogram.mobo.notificationservice.b.a */
    private static class RestClient implements HttpRequestInterceptor {
        private RestClient() {
        }

        public void process(HttpRequest httpRequest, HttpContext httpContext) {
            if (!httpRequest.containsHeader("Accept-Encoding")) {
                httpRequest.addHeader("Accept-Encoding", "gzip");
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.notificationservice.b.b */
    private static class RestClient implements HttpResponseInterceptor {
        private RestClient() {
        }

        private void m1962a(HttpResponse httpResponse, Header header) {
            for (HeaderElement name : header.getElements()) {
                if (name.getName().equalsIgnoreCase("gzip")) {
                    httpResponse.setEntity(new RestClient(httpResponse.getEntity()));
                    return;
                }
            }
        }

        public void process(HttpResponse httpResponse, HttpContext httpContext) {
            Header contentEncoding = httpResponse.getEntity().getContentEncoding();
            if (contentEncoding != null) {
                m1962a(httpResponse, contentEncoding);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.notificationservice.b.c */
    private static class RestClient extends HttpEntityWrapper {
        public RestClient(HttpEntity httpEntity) {
            super(httpEntity);
        }

        public InputStream getContent() {
            return new GZIPInputStream(this.wrappedEntity.getContent());
        }

        public long getContentLength() {
            return -1;
        }
    }

    static {
        f1974b = RestClient.class.getSimpleName();
        f1973a = Integer.valueOf(DefaultLoadControl.DEFAULT_HIGH_WATERMARK_MS);
        f1975c = Integer.valueOf(DefaultLoadControl.DEFAULT_HIGH_WATERMARK_MS);
    }

    public RestClient(String str) {
        this.f1977e = str;
    }

    private String m1963a(InputStream inputStream) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(readLine);
            } catch (Throwable e) {
                Log.e(f1974b, "There was an input stream read error.", e);
            }
        }
        return stringBuilder.toString();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String m1964a(java.lang.String r10, org.apache.http.client.methods.HttpEntityEnclosingRequestBase r11) {
        /*
        r9 = this;
        r4 = 0;
        r1 = 0;
        r8 = 2131167230; // 0x7f0707fe float:1.7948728E38 double:1.052936514E-314;
        r0 = 0;
        r6 = r0;
        r0 = r1;
    L_0x0009:
        r2 = 3;
        if (r6 >= r2) goto L_0x014c;
    L_0x000c:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "execution count : [";
        r2 = r2.append(r3);
        r3 = r6 + 1;
        r2 = r2.append(r3);
        r3 = "]";
        r2 = r2.append(r3);
        r2 = r2.toString();
        r9.m1965a(r2);
        r2 = "Accept";
        r3 = "application/json";
        r11.setHeader(r2, r3);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = "Content-Type";
        r3 = "application/json;charset=UTF-8";
        r11.setHeader(r2, r3);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = new org.apache.http.entity.StringEntity;	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = "UTF-8";
        r2.<init>(r10, r3);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r11.setEntity(r2);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = new java.lang.StringBuilder;	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2.<init>();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = "calling : [";
        r2 = r2.append(r3);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = r11.getURI();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = r2.append(r3);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = "]";
        r2 = r2.append(r3);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = r2.toString();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r9.m1965a(r2);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = new java.lang.StringBuilder;	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2.<init>();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = "using data : ";
        r2 = r2.append(r3);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = r2.append(r10);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = r2.toString();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r9.m1965a(r2);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = r9.m1967a();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = r2.execute(r11);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = new java.lang.StringBuilder;	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3.<init>();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r7 = "Status:[";
        r3 = r3.append(r7);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r7 = r2.getStatusLine();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r7 = r7.toString();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = r3.append(r7);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r7 = "]";
        r3 = r3.append(r7);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = r3.toString();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r9.m1965a(r3);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = r2.getStatusLine();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = r3.getStatusCode();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r7 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r3 == r7) goto L_0x00d8;
    L_0x00bc:
        r0 = new com.hanista.mobogram.mobo.notificationservice.a;	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = 2131167231; // 0x7f0707ff float:1.794873E38 double:1.0529365144E-314;
        r0.<init>(r2);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        throw r0;	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
    L_0x00c5:
        r0 = move-exception;
        r2 = r0.getMessage();
        r3 = "ECONNRESET";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x011d;
    L_0x00d3:
        r2 = r6 + 1;
        r6 = r2;
        goto L_0x0009;
    L_0x00d8:
        r7 = r2.getEntity();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = "Content-Length";
        r2 = r2.getLastHeader(r3);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        if (r2 == 0) goto L_0x0164;
    L_0x00e5:
        r2 = r2.getValue();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2 = java.lang.Long.parseLong(r2);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
    L_0x00ed:
        if (r7 == 0) goto L_0x014c;
    L_0x00ef:
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 <= 0) goto L_0x014c;
    L_0x00f3:
        r2 = r7.getContent();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r0 = r9.m1963a(r2);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = new java.lang.StringBuilder;	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3.<init>();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r7 = "Result of conversation: [";
        r3 = r3.append(r7);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = r3.append(r0);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r7 = "]";
        r3 = r3.append(r7);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r3 = r3.toString();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r9.m1965a(r3);	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
        r2.close();	 Catch:{ SocketException -> 0x00c5, InterruptedIOException -> 0x0161, ClientProtocolException -> 0x012b, IOException -> 0x013d }
    L_0x011c:
        return r0;
    L_0x011d:
        r1 = f1974b;
        r2 = "There was a socket error";
        android.util.Log.e(r1, r2, r0);
        r1 = new com.hanista.mobogram.mobo.notificationservice.a;
        r1.<init>(r8, r0);
        throw r1;
    L_0x012b:
        r0 = move-exception;
        r1 = f1974b;
        r2 = "There was a protocol based error.";
        android.util.Log.e(r1, r2, r0);
        r1 = new com.hanista.mobogram.mobo.notificationservice.a;
        r2 = 2131167228; // 0x7f0707fc float:1.7948724E38 double:1.052936513E-314;
        r1.<init>(r2, r0);
        throw r1;
    L_0x013d:
        r0 = move-exception;
        r1 = f1974b;
        r2 = "There was an IO Stream related error.";
        android.util.Log.e(r1, r2, r0);
        r1 = new com.hanista.mobogram.mobo.notificationservice.a;
        r1.<init>(r8, r0);
        throw r1;
    L_0x014c:
        if (r0 == 0) goto L_0x015f;
    L_0x014e:
        r1 = f1974b;
        r2 = "There was an socket timeout error";
        android.util.Log.e(r1, r2, r0);
        r1 = new com.hanista.mobogram.mobo.notificationservice.a;
        r2 = 2131167361; // 0x7f070881 float:1.7948993E38 double:1.0529365786E-314;
        r1.<init>(r2, r0);
        throw r1;
    L_0x015f:
        r0 = r1;
        goto L_0x011c;
    L_0x0161:
        r0 = move-exception;
        goto L_0x00d3;
    L_0x0164:
        r2 = r4;
        goto L_0x00ed;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.mobo.notificationservice.b.a(java.lang.String, org.apache.http.client.methods.HttpEntityEnclosingRequestBase):java.lang.String");
    }

    private void m1965a(String str) {
    }

    public String m1966a(String str, String str2) {
        return m1964a(str2, new HttpPost(this.f1977e + str));
    }

    public DefaultHttpClient m1967a() {
        if (this.f1976d != null) {
            return this.f1976d;
        }
        HttpParams basicHttpParams = new BasicHttpParams();
        basicHttpParams.setParameter("http.connection.timeout", f1973a);
        basicHttpParams.setParameter("http.socket.timeout", f1975c);
        basicHttpParams.setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        basicHttpParams.setParameter("http.useragent", "Apache-HttpClient/Android");
        basicHttpParams.setParameter("http.connection.stalecheck", Boolean.valueOf(false));
        this.f1976d = new DefaultHttpClient(basicHttpParams);
        this.f1976d.addRequestInterceptor(new RestClient());
        this.f1976d.addResponseInterceptor(new RestClient());
        this.f1976d.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        return this.f1976d;
    }
}

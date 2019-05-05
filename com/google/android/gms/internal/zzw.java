package com.google.android.gms.internal;

import com.hanista.mobogram.messenger.exoplayer.ExoPlayer.Factory;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.messenger.volley.toolbox.HttpClientStack.HttpPatch;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.net.URI;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class zzw implements zzy {
    protected final HttpClient zzcd;

    public static final class zza extends HttpEntityEnclosingRequestBase {
        public zza(String str) {
            setURI(URI.create(str));
        }

        public String getMethod() {
            return HttpPatch.METHOD_NAME;
        }
    }

    public zzw(HttpClient httpClient) {
        this.zzcd = httpClient;
    }

    private static void zza(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, zzk<?> com_google_android_gms_internal_zzk_) {
        byte[] zzp = com_google_android_gms_internal_zzk_.zzp();
        if (zzp != null) {
            httpEntityEnclosingRequestBase.setEntity(new ByteArrayEntity(zzp));
        }
    }

    private static void zza(HttpUriRequest httpUriRequest, Map<String, String> map) {
        for (String str : map.keySet()) {
            httpUriRequest.setHeader(str, (String) map.get(str));
        }
    }

    static HttpUriRequest zzb(zzk<?> com_google_android_gms_internal_zzk_, Map<String, String> map) {
        HttpEntityEnclosingRequestBase httpPost;
        switch (com_google_android_gms_internal_zzk_.getMethod()) {
            case VideoPlayer.TRACK_DISABLED /*-1*/:
                byte[] zzl = com_google_android_gms_internal_zzk_.zzl();
                if (zzl == null) {
                    return new HttpGet(com_google_android_gms_internal_zzk_.getUrl());
                }
                HttpUriRequest httpPost2 = new HttpPost(com_google_android_gms_internal_zzk_.getUrl());
                httpPost2.addHeader("Content-Type", com_google_android_gms_internal_zzk_.zzk());
                httpPost2.setEntity(new ByteArrayEntity(zzl));
                return httpPost2;
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                return new HttpGet(com_google_android_gms_internal_zzk_.getUrl());
            case VideoPlayer.TYPE_AUDIO /*1*/:
                httpPost = new HttpPost(com_google_android_gms_internal_zzk_.getUrl());
                httpPost.addHeader("Content-Type", com_google_android_gms_internal_zzk_.zzo());
                zza(httpPost, (zzk) com_google_android_gms_internal_zzk_);
                return httpPost;
            case VideoPlayer.STATE_PREPARING /*2*/:
                httpPost = new HttpPut(com_google_android_gms_internal_zzk_.getUrl());
                httpPost.addHeader("Content-Type", com_google_android_gms_internal_zzk_.zzo());
                zza(httpPost, (zzk) com_google_android_gms_internal_zzk_);
                return httpPost;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return new HttpDelete(com_google_android_gms_internal_zzk_.getUrl());
            case VideoPlayer.STATE_READY /*4*/:
                return new HttpHead(com_google_android_gms_internal_zzk_.getUrl());
            case VideoPlayer.STATE_ENDED /*5*/:
                return new HttpOptions(com_google_android_gms_internal_zzk_.getUrl());
            case Method.TRACE /*6*/:
                return new HttpTrace(com_google_android_gms_internal_zzk_.getUrl());
            case Method.PATCH /*7*/:
                httpPost = new zza(com_google_android_gms_internal_zzk_.getUrl());
                httpPost.addHeader("Content-Type", com_google_android_gms_internal_zzk_.zzo());
                zza(httpPost, (zzk) com_google_android_gms_internal_zzk_);
                return httpPost;
            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }

    public HttpResponse zza(zzk<?> com_google_android_gms_internal_zzk_, Map<String, String> map) {
        HttpUriRequest zzb = zzb(com_google_android_gms_internal_zzk_, map);
        zza(zzb, (Map) map);
        zza(zzb, com_google_android_gms_internal_zzk_.getHeaders());
        zza(zzb);
        HttpParams params = zzb.getParams();
        int zzs = com_google_android_gms_internal_zzk_.zzs();
        HttpConnectionParams.setConnectionTimeout(params, Factory.DEFAULT_MIN_REBUFFER_MS);
        HttpConnectionParams.setSoTimeout(params, zzs);
        return this.zzcd.execute(zzb);
    }

    protected void zza(HttpUriRequest httpUriRequest) {
    }
}

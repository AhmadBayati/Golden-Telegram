package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzh;

public final class zzrx {
    public static final Api<NoOptions> API;
    public static final zzry Dh;
    public static final zzf<zzsb> fa;
    private static final zza<zzsb, NoOptions> fb;

    /* renamed from: com.google.android.gms.internal.zzrx.1 */
    class C02101 extends zza<zzsb, NoOptions> {
        C02101() {
        }

        public /* synthetic */ zze zza(Context context, Looper looper, zzh com_google_android_gms_common_internal_zzh, Object obj, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return zzf(context, looper, com_google_android_gms_common_internal_zzh, (NoOptions) obj, connectionCallbacks, onConnectionFailedListener);
        }

        public zzsb zzf(Context context, Looper looper, zzh com_google_android_gms_common_internal_zzh, NoOptions noOptions, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return new zzsb(context, looper, com_google_android_gms_common_internal_zzh, connectionCallbacks, onConnectionFailedListener);
        }
    }

    static {
        fa = new zzf();
        fb = new C02101();
        API = new Api("Common.API", fb, fa);
        Dh = new zzrz();
    }
}

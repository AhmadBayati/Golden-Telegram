package com.google.android.gms.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzp;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.internal.zzsi;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class zzc {
    public static final String GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms";
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE;
    private static final zzc uN;

    static {
        GOOGLE_PLAY_SERVICES_VERSION_CODE = zze.GOOGLE_PLAY_SERVICES_VERSION_CODE;
        uN = new zzc();
    }

    zzc() {
    }

    public static zzc zzapd() {
        return uN;
    }

    private String zzt(@Nullable Context context, @Nullable String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("gcore_");
        stringBuilder.append(GOOGLE_PLAY_SERVICES_VERSION_CODE);
        stringBuilder.append("-");
        if (!TextUtils.isEmpty(str)) {
            stringBuilder.append(str);
        }
        stringBuilder.append("-");
        if (context != null) {
            stringBuilder.append(context.getPackageName());
        }
        stringBuilder.append("-");
        if (context != null) {
            try {
                stringBuilder.append(zzsi.zzcr(context).getPackageInfo(context.getPackageName(), 0).versionCode);
            } catch (NameNotFoundException e) {
            }
        }
        return stringBuilder.toString();
    }

    @Nullable
    public PendingIntent getErrorResolutionPendingIntent(Context context, int i, int i2) {
        return zza(context, i, i2, null);
    }

    public String getErrorString(int i) {
        return zze.getErrorString(i);
    }

    @Nullable
    public String getOpenSourceSoftwareLicenseInfo(Context context) {
        return zze.getOpenSourceSoftwareLicenseInfo(context);
    }

    public int isGooglePlayServicesAvailable(Context context) {
        int isGooglePlayServicesAvailable = zze.isGooglePlayServicesAvailable(context);
        return zze.zzd(context, isGooglePlayServicesAvailable) ? 18 : isGooglePlayServicesAvailable;
    }

    public boolean isUserResolvableError(int i) {
        return zze.isUserRecoverableError(i);
    }

    @Nullable
    public PendingIntent zza(Context context, int i, int i2, @Nullable String str) {
        if (zzi.zzcl(context) && i == 2) {
            i = 42;
        }
        Intent zza = zza(context, i, str);
        return zza == null ? null : PendingIntent.getActivity(context, i2, zza, 268435456);
    }

    @Nullable
    public Intent zza(Context context, int i, @Nullable String str) {
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
            case VideoPlayer.STATE_PREPARING /*2*/:
                return zzp.zzad(GOOGLE_PLAY_SERVICES_PACKAGE, zzt(context, str));
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return zzp.zzhw(GOOGLE_PLAY_SERVICES_PACKAGE);
            case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                return zzp.zzaux();
            default:
                return null;
        }
    }

    public int zzbo(Context context) {
        return zze.zzbo(context);
    }

    public void zzbp(Context context) {
        zze.zzbc(context);
    }

    public void zzbq(Context context) {
        zze.zzbq(context);
    }

    public boolean zzd(Context context, int i) {
        return zze.zzd(context, i);
    }

    @Nullable
    @Deprecated
    public Intent zzfl(int i) {
        return zza(null, i, null);
    }

    public boolean zzs(Context context, String str) {
        return zze.zzs(context, str);
    }
}

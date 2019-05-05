package com.google.android.gms.common;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller.SessionInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri.Builder;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import com.google.android.gms.C0083R;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.common.util.zzl;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.common.util.zzy;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class zze {
    @Deprecated
    public static final String GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms";
    @Deprecated
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE;
    public static final String GOOGLE_PLAY_STORE_PACKAGE = "com.android.vending";
    public static boolean uX;
    public static boolean uY;
    static boolean uZ;
    private static String va;
    private static int vb;
    private static boolean vc;
    static final AtomicBoolean vd;
    private static final AtomicBoolean ve;

    static {
        GOOGLE_PLAY_SERVICES_VERSION_CODE = zzapk();
        uX = false;
        uY = false;
        uZ = false;
        va = null;
        vb = GOOGLE_PLAY_SERVICES_VERSION_CODE;
        vc = false;
        vd = new AtomicBoolean();
        ve = new AtomicBoolean();
    }

    zze() {
    }

    @Deprecated
    public static PendingIntent getErrorPendingIntent(int i, Context context, int i2) {
        return zzc.zzapd().getErrorResolutionPendingIntent(context, i, i2);
    }

    @Deprecated
    public static String getErrorString(int i) {
        return ConnectionResult.getStatusString(i);
    }

    @Deprecated
    public static String getOpenSourceSoftwareLicenseInfo(Context context) {
        InputStream openInputStream;
        try {
            openInputStream = context.getContentResolver().openInputStream(new Builder().scheme("android.resource").authority(GOOGLE_PLAY_SERVICES_PACKAGE).appendPath("raw").appendPath("oss_notice").build());
            String next = new Scanner(openInputStream).useDelimiter("\\A").next();
            if (openInputStream == null) {
                return next;
            }
            openInputStream.close();
            return next;
        } catch (NoSuchElementException e) {
            if (openInputStream != null) {
                openInputStream.close();
            }
            return null;
        } catch (Exception e2) {
            return null;
        } catch (Throwable th) {
            if (openInputStream != null) {
                openInputStream.close();
            }
        }
    }

    public static Context getRemoteContext(Context context) {
        try {
            return context.createPackageContext(GOOGLE_PLAY_SERVICES_PACKAGE, 3);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static Resources getRemoteResource(Context context) {
        try {
            return context.getPackageManager().getResourcesForApplication(GOOGLE_PLAY_SERVICES_PACKAGE);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    @Deprecated
    public static int isGooglePlayServicesAvailable(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            context.getResources().getString(C0083R.string.common_google_play_services_unknown_issue);
        } catch (Throwable th) {
            Log.e("GooglePlayServicesUtil", "The Google Play services resources were not found. Check your project configuration to ensure that the resources are included.");
        }
        if (!GOOGLE_PLAY_SERVICES_PACKAGE.equals(context.getPackageName())) {
            zzbt(context);
        }
        int i = !zzi.zzcl(context) ? 1 : GOOGLE_PLAY_SERVICES_VERSION_CODE;
        PackageInfo packageInfo = null;
        if (i != 0) {
            try {
                packageInfo = packageManager.getPackageInfo(GOOGLE_PLAY_STORE_PACKAGE, 8256);
            } catch (NameNotFoundException e) {
                Log.w("GooglePlayServicesUtil", "Google Play Store is missing.");
                return 9;
            }
        }
        try {
            PackageInfo packageInfo2 = packageManager.getPackageInfo(GOOGLE_PLAY_SERVICES_PACKAGE, 64);
            zzf zzbz = zzf.zzbz(context);
            if (i != 0) {
                if (zzbz.zza(packageInfo, zzd.uW) == null) {
                    Log.w("GooglePlayServicesUtil", "Google Play Store signature invalid.");
                    return 9;
                }
                if (zzbz.zza(packageInfo2, zzbz.zza(packageInfo, zzd.uW)) == null) {
                    Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
                    return 9;
                }
            } else if (zzbz.zza(packageInfo2, zzd.uW) == null) {
                Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
                return 9;
            }
            if (zzl.zzhj(packageInfo2.versionCode) < zzl.zzhj(GOOGLE_PLAY_SERVICES_VERSION_CODE)) {
                Log.w("GooglePlayServicesUtil", "Google Play services out of date.  Requires " + GOOGLE_PLAY_SERVICES_VERSION_CODE + " but found " + packageInfo2.versionCode);
                return 2;
            }
            ApplicationInfo applicationInfo = packageInfo2.applicationInfo;
            if (applicationInfo == null) {
                try {
                    applicationInfo = packageManager.getApplicationInfo(GOOGLE_PLAY_SERVICES_PACKAGE, GOOGLE_PLAY_SERVICES_VERSION_CODE);
                } catch (Throwable e2) {
                    Log.wtf("GooglePlayServicesUtil", "Google Play services missing when getting application info.", e2);
                    return 1;
                }
            }
            return !applicationInfo.enabled ? 3 : GOOGLE_PLAY_SERVICES_VERSION_CODE;
        } catch (NameNotFoundException e3) {
            Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
            return 1;
        }
    }

    @Deprecated
    public static boolean isUserRecoverableError(int i) {
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
            case VideoPlayer.STATE_PREPARING /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case C0338R.styleable.PromptView_iconTint /*9*/:
                return true;
            default:
                return false;
        }
    }

    private static int zzapk() {
        return zzf.BA;
    }

    @Deprecated
    public static boolean zzapl() {
        return "user".equals(Build.TYPE);
    }

    @TargetApi(19)
    @Deprecated
    public static boolean zzb(Context context, int i, String str) {
        return zzy.zzb(context, i, str);
    }

    @Deprecated
    public static void zzbc(Context context) {
        int isGooglePlayServicesAvailable = zzc.zzapd().isGooglePlayServicesAvailable(context);
        if (isGooglePlayServicesAvailable != 0) {
            Intent zza = zzc.zzapd().zza(context, isGooglePlayServicesAvailable, "e");
            Log.e("GooglePlayServicesUtil", "GooglePlayServices not available due to error " + isGooglePlayServicesAvailable);
            if (zza == null) {
                throw new GooglePlayServicesNotAvailableException(isGooglePlayServicesAvailable);
            }
            throw new GooglePlayServicesRepairableException(isGooglePlayServicesAvailable, "Google Play Services not available", zza);
        }
    }

    @Deprecated
    public static int zzbo(Context context) {
        int i = GOOGLE_PLAY_SERVICES_VERSION_CODE;
        try {
            return context.getPackageManager().getPackageInfo(GOOGLE_PLAY_SERVICES_PACKAGE, GOOGLE_PLAY_SERVICES_VERSION_CODE).versionCode;
        } catch (NameNotFoundException e) {
            Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
            return i;
        }
    }

    @Deprecated
    public static void zzbq(Context context) {
        if (!vd.getAndSet(true)) {
            try {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                if (notificationManager != null) {
                    notificationManager.cancel(10436);
                }
            } catch (SecurityException e) {
            }
        }
    }

    private static void zzbt(Context context) {
        if (!ve.get()) {
            zzbx(context);
            if (vb == 0) {
                throw new IllegalStateException("A required meta-data tag in your app's AndroidManifest.xml does not exist.  You must have the following declaration within the <application> element:     <meta-data android:name=\"com.google.android.gms.version\" android:value=\"@integer/google_play_services_version\" />");
            } else if (vb != GOOGLE_PLAY_SERVICES_VERSION_CODE) {
                int i = GOOGLE_PLAY_SERVICES_VERSION_CODE;
                int i2 = vb;
                String valueOf = String.valueOf("com.google.android.gms.version");
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 290).append("The meta-data tag in your app's AndroidManifest.xml does not have the right value.  Expected ").append(i).append(" but found ").append(i2).append(".  You must have the following declaration within the <application> element:     <meta-data android:name=\"").append(valueOf).append("\" android:value=\"@integer/google_play_services_version\" />").toString());
            }
        }
    }

    public static boolean zzbu(Context context) {
        zzbx(context);
        return uZ;
    }

    public static boolean zzbv(Context context) {
        return zzbu(context) || !zzapl();
    }

    @TargetApi(18)
    public static boolean zzbw(Context context) {
        if (zzs.zzaxq()) {
            Bundle applicationRestrictions = ((UserManager) context.getSystemService("user")).getApplicationRestrictions(context.getPackageName());
            if (applicationRestrictions != null && "true".equals(applicationRestrictions.getString("restricted_profile"))) {
                return true;
            }
        }
        return false;
    }

    private static void zzbx(Context context) {
        if (!vc) {
            zzby(context);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void zzby(android.content.Context r7) {
        /*
        r6 = 1;
        r0 = r7.getPackageName();	 Catch:{ NameNotFoundException -> 0x003b }
        va = r0;	 Catch:{ NameNotFoundException -> 0x003b }
        r0 = com.google.android.gms.internal.zzsi.zzcr(r7);	 Catch:{ NameNotFoundException -> 0x003b }
        r1 = com.google.android.gms.common.internal.zzaa.zzch(r7);	 Catch:{ NameNotFoundException -> 0x003b }
        vb = r1;	 Catch:{ NameNotFoundException -> 0x003b }
        r1 = "com.google.android.gms";
        r2 = 64;
        r0 = r0.getPackageInfo(r1, r2);	 Catch:{ NameNotFoundException -> 0x003b }
        if (r0 == 0) goto L_0x0037;
    L_0x001c:
        r1 = com.google.android.gms.common.zzf.zzbz(r7);	 Catch:{ NameNotFoundException -> 0x003b }
        r2 = 1;
        r2 = new com.google.android.gms.common.zzd.zza[r2];	 Catch:{ NameNotFoundException -> 0x003b }
        r3 = 0;
        r4 = com.google.android.gms.common.zzd.zzd.uW;	 Catch:{ NameNotFoundException -> 0x003b }
        r5 = 1;
        r4 = r4[r5];	 Catch:{ NameNotFoundException -> 0x003b }
        r2[r3] = r4;	 Catch:{ NameNotFoundException -> 0x003b }
        r0 = r1.zza(r0, r2);	 Catch:{ NameNotFoundException -> 0x003b }
        if (r0 == 0) goto L_0x0037;
    L_0x0031:
        r0 = 1;
        uZ = r0;	 Catch:{ NameNotFoundException -> 0x003b }
    L_0x0034:
        vc = r6;
    L_0x0036:
        return;
    L_0x0037:
        r0 = 0;
        uZ = r0;	 Catch:{ NameNotFoundException -> 0x003b }
        goto L_0x0034;
    L_0x003b:
        r0 = move-exception;
        r1 = "GooglePlayServicesUtil";
        r2 = "Cannot find Google Play services package name.";
        android.util.Log.w(r1, r2, r0);	 Catch:{ all -> 0x0048 }
        vc = r6;
        goto L_0x0036;
    L_0x0048:
        r0 = move-exception;
        vc = r6;
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.zze.zzby(android.content.Context):void");
    }

    @Deprecated
    public static boolean zzd(Context context, int i) {
        return i == 18 ? true : i == 1 ? zzs(context, GOOGLE_PLAY_SERVICES_PACKAGE) : false;
    }

    @Deprecated
    public static boolean zze(Context context, int i) {
        return i == 9 ? zzs(context, GOOGLE_PLAY_STORE_PACKAGE) : false;
    }

    @Deprecated
    public static boolean zzf(Context context, int i) {
        return zzy.zzf(context, i);
    }

    @Deprecated
    public static Intent zzfm(int i) {
        return zzc.zzapd().zza(null, i, null);
    }

    static boolean zzfn(int i) {
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
            case VideoPlayer.STATE_PREPARING /*2*/:
            case VideoPlayer.STATE_BUFFERING /*3*/:
            case C0338R.styleable.PromptView_secondaryText /*18*/:
            case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                return true;
            default:
                return false;
        }
    }

    @TargetApi(21)
    static boolean zzs(Context context, String str) {
        boolean equals = str.equals(GOOGLE_PLAY_SERVICES_PACKAGE);
        if (equals && zzd.zzact()) {
            return false;
        }
        if (zzs.zzaxu()) {
            for (SessionInfo appPackageName : context.getPackageManager().getPackageInstaller().getAllSessions()) {
                if (str.equals(appPackageName.getAppPackageName())) {
                    return true;
                }
            }
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str, MessagesController.UPDATE_MASK_CHANNEL);
            if (equals) {
                return applicationInfo.enabled;
            }
            boolean z = applicationInfo.enabled && !zzbw(context);
            return z;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}

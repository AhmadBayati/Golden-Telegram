package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.C0083R;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.zzsi;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.upstream.NetworkLock;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public final class zzi {
    private static final SimpleArrayMap<String, String> Cc;

    static {
        Cc = new SimpleArrayMap();
    }

    public static String zzce(Context context) {
        String str = context.getApplicationInfo().name;
        if (TextUtils.isEmpty(str)) {
            str = context.getPackageName();
            context.getApplicationContext().getPackageManager();
            try {
                str = zzsi.zzcr(context).zzik(context.getPackageName()).toString();
            } catch (NameNotFoundException e) {
            }
        }
        return str;
    }

    @Nullable
    public static String zzg(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return resources.getString(C0083R.string.common_google_play_services_install_title);
            case VideoPlayer.STATE_PREPARING /*2*/:
            case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                return resources.getString(C0083R.string.common_google_play_services_update_title);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return resources.getString(C0083R.string.common_google_play_services_enable_title);
            case VideoPlayer.STATE_READY /*4*/:
            case Method.TRACE /*6*/:
                return null;
            case VideoPlayer.STATE_ENDED /*5*/:
                Log.e("GoogleApiAvailability", "An invalid account was specified when connecting. Please provide a valid account.");
                return zzu(context, "common_google_play_services_invalid_account_title");
            case Method.PATCH /*7*/:
                Log.e("GoogleApiAvailability", "Network error occurred. Please retry request later.");
                return zzu(context, "common_google_play_services_network_error_title");
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                Log.e("GoogleApiAvailability", "Internal error occurred. Please see logs for detailed information");
                return null;
            case C0338R.styleable.PromptView_iconTint /*9*/:
                Log.e("GoogleApiAvailability", "Google Play services is invalid. Cannot recover.");
                return resources.getString(C0083R.string.common_google_play_services_unsupported_title);
            case NetworkLock.DOWNLOAD_PRIORITY /*10*/:
                Log.e("GoogleApiAvailability", "Developer error occurred. Please see logs for detailed information");
                return null;
            case C0338R.styleable.PromptView_maxTextWidth /*11*/:
                Log.e("GoogleApiAvailability", "The application is not licensed to the user.");
                return null;
            case TLRPC.USER_FLAG_PHONE /*16*/:
                Log.e("GoogleApiAvailability", "One of the API components you attempted to connect to is not available.");
                return null;
            case C0338R.styleable.PromptView_primaryTextTypeface /*17*/:
                Log.e("GoogleApiAvailability", "The specified account could not be signed in.");
                return zzu(context, "common_google_play_services_sign_in_failed_title");
            case C0338R.styleable.PromptView_secondaryText /*18*/:
                return resources.getString(C0083R.string.common_google_play_services_updating_title);
            case C0338R.styleable.PromptView_secondaryTextFontFamily /*20*/:
                Log.e("GoogleApiAvailability", "The current user profile is restricted and could not use authenticated features.");
                return zzu(context, "common_google_play_services_restricted_profile_title");
            default:
                Log.e("GoogleApiAvailability", "Unexpected error code " + i);
                return null;
        }
    }

    private static String zzg(Context context, String str, String str2) {
        Resources resources = context.getResources();
        String zzu = zzu(context, str);
        if (zzu == null) {
            zzu = resources.getString(C0083R.string.common_google_play_services_unknown_issue);
        }
        return String.format(resources.getConfiguration().locale, zzu, new Object[]{str2});
    }

    @NonNull
    public static String zzh(Context context, int i) {
        String zzu = i == 6 ? zzu(context, "common_google_play_services_resolution_required_title") : zzg(context, i);
        return zzu == null ? context.getResources().getString(C0083R.string.common_google_play_services_notification_ticker) : zzu;
    }

    @NonNull
    public static String zzi(Context context, int i) {
        Resources resources = context.getResources();
        String zzce = zzce(context);
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                if (com.google.android.gms.common.util.zzi.zzb(resources)) {
                    return resources.getString(C0083R.string.common_google_play_services_install_text_tablet, new Object[]{zzce});
                }
                return resources.getString(C0083R.string.common_google_play_services_install_text_phone, new Object[]{zzce});
            case VideoPlayer.STATE_PREPARING /*2*/:
                return resources.getString(C0083R.string.common_google_play_services_update_text, new Object[]{zzce});
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return resources.getString(C0083R.string.common_google_play_services_enable_text, new Object[]{zzce});
            case VideoPlayer.STATE_ENDED /*5*/:
                return zzg(context, "common_google_play_services_invalid_account_text", zzce);
            case Method.PATCH /*7*/:
                return zzg(context, "common_google_play_services_network_error_text", zzce);
            case C0338R.styleable.PromptView_iconTint /*9*/:
                return resources.getString(C0083R.string.common_google_play_services_unsupported_text, new Object[]{zzce});
            case TLRPC.USER_FLAG_PHONE /*16*/:
                return zzg(context, "common_google_play_services_api_unavailable_text", zzce);
            case C0338R.styleable.PromptView_primaryTextTypeface /*17*/:
                return zzg(context, "common_google_play_services_sign_in_failed_text", zzce);
            case C0338R.styleable.PromptView_secondaryText /*18*/:
                return resources.getString(C0083R.string.common_google_play_services_updating_text, new Object[]{zzce});
            case C0338R.styleable.PromptView_secondaryTextFontFamily /*20*/:
                return zzg(context, "common_google_play_services_restricted_profile_text", zzce);
            case NalUnitTypes.NAL_TYPE_RSV_NVCL42 /*42*/:
                return resources.getString(C0083R.string.common_google_play_services_wear_update_text);
            default:
                return resources.getString(C0083R.string.common_google_play_services_unknown_issue, new Object[]{zzce});
        }
    }

    @NonNull
    public static String zzj(Context context, int i) {
        return i == 6 ? zzg(context, "common_google_play_services_resolution_required_text", zzce(context)) : zzi(context, i);
    }

    @NonNull
    public static String zzk(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                return resources.getString(C0083R.string.common_google_play_services_install_button);
            case VideoPlayer.STATE_PREPARING /*2*/:
                return resources.getString(C0083R.string.common_google_play_services_update_button);
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return resources.getString(C0083R.string.common_google_play_services_enable_button);
            default:
                return resources.getString(17039370);
        }
    }

    @Nullable
    private static String zzu(Context context, String str) {
        synchronized (Cc) {
            String str2 = (String) Cc.get(str);
            if (str2 != null) {
                return str2;
            }
            Resources remoteResource = GooglePlayServicesUtil.getRemoteResource(context);
            if (remoteResource == null) {
                return null;
            }
            int identifier = remoteResource.getIdentifier(str, "string", GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE);
            if (identifier == 0) {
                String str3 = "GoogleApiAvailability";
                String str4 = "Missing resource: ";
                str2 = String.valueOf(str);
                Log.w(str3, str2.length() != 0 ? str4.concat(str2) : new String(str4));
                return null;
            }
            Object string = remoteResource.getString(identifier);
            if (TextUtils.isEmpty(string)) {
                str3 = "GoogleApiAvailability";
                str4 = "Got empty resource: ";
                str2 = String.valueOf(str);
                Log.w(str3, str2.length() != 0 ? str4.concat(str2) : new String(str4));
                return null;
            }
            Cc.put(str, string);
            return string;
        }
    }
}

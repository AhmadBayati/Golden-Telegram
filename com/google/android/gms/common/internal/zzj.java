package com.google.android.gms.common.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.google.android.gms.internal.zzrb;

public abstract class zzj implements OnClickListener {

    /* renamed from: com.google.android.gms.common.internal.zzj.1 */
    class C00991 extends zzj {
        final /* synthetic */ Activity val$activity;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        C00991(Intent intent, Activity activity, int i) {
            this.val$intent = intent;
            this.val$activity = activity;
            this.val$requestCode = i;
        }

        public void zzauo() {
            if (this.val$intent != null) {
                this.val$activity.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzj.2 */
    class C01002 extends zzj {
        final /* synthetic */ Fragment val$fragment;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        C01002(Intent intent, Fragment fragment, int i) {
            this.val$intent = intent;
            this.val$fragment = fragment;
            this.val$requestCode = i;
        }

        public void zzauo() {
            if (this.val$intent != null) {
                this.val$fragment.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzj.3 */
    class C01013 extends zzj {
        final /* synthetic */ zzrb Cd;
        final /* synthetic */ Intent val$intent;
        final /* synthetic */ int val$requestCode;

        C01013(Intent intent, zzrb com_google_android_gms_internal_zzrb, int i) {
            this.val$intent = intent;
            this.Cd = com_google_android_gms_internal_zzrb;
            this.val$requestCode = i;
        }

        @TargetApi(11)
        public void zzauo() {
            if (this.val$intent != null) {
                this.Cd.startActivityForResult(this.val$intent, this.val$requestCode);
            }
        }
    }

    public static zzj zza(Activity activity, Intent intent, int i) {
        return new C00991(intent, activity, i);
    }

    public static zzj zza(@NonNull Fragment fragment, Intent intent, int i) {
        return new C01002(intent, fragment, i);
    }

    public static zzj zza(@NonNull zzrb com_google_android_gms_internal_zzrb, Intent intent, int i) {
        return new C01013(intent, com_google_android_gms_internal_zzrb, i);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            zzauo();
            dialogInterface.dismiss();
        } catch (Throwable e) {
            Log.e("DialogRedirect", "Can't redirect to app settings for Google Play services", e);
        }
    }

    public abstract void zzauo();
}

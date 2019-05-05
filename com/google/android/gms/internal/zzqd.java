package com.google.android.gms.internal;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;

public abstract class zzqd extends zzra implements OnCancelListener {
    protected boolean mStarted;
    protected final GoogleApiAvailability vP;
    private int wA;
    private final Handler wB;
    protected boolean wy;
    private ConnectionResult wz;

    private class zza implements Runnable {
        final /* synthetic */ zzqd wC;

        /* renamed from: com.google.android.gms.internal.zzqd.zza.1 */
        class C01851 extends com.google.android.gms.internal.zzqv.zza {
            final /* synthetic */ Dialog wD;
            final /* synthetic */ zza wE;

            C01851(zza com_google_android_gms_internal_zzqd_zza, Dialog dialog) {
                this.wE = com_google_android_gms_internal_zzqd_zza;
                this.wD = dialog;
            }

            public void zzaqp() {
                this.wE.wC.zzaqo();
                if (this.wD.isShowing()) {
                    this.wD.dismiss();
                }
            }
        }

        private zza(zzqd com_google_android_gms_internal_zzqd) {
            this.wC = com_google_android_gms_internal_zzqd;
        }

        @MainThread
        public void run() {
            if (!this.wC.mStarted) {
                return;
            }
            if (this.wC.wz.hasResolution()) {
                this.wC.yY.startActivityForResult(GoogleApiActivity.zzb(this.wC.getActivity(), this.wC.wz.getResolution(), this.wC.wA, false), 1);
            } else if (this.wC.vP.isUserResolvableError(this.wC.wz.getErrorCode())) {
                this.wC.vP.zza(this.wC.getActivity(), this.wC.yY, this.wC.wz.getErrorCode(), 2, this.wC);
            } else if (this.wC.wz.getErrorCode() == 18) {
                this.wC.vP.zza(this.wC.getActivity().getApplicationContext(), new C01851(this, this.wC.vP.zza(this.wC.getActivity(), this.wC)));
            } else {
                this.wC.zza(this.wC.wz, this.wC.wA);
            }
        }
    }

    protected zzqd(zzrb com_google_android_gms_internal_zzrb) {
        this(com_google_android_gms_internal_zzrb, GoogleApiAvailability.getInstance());
    }

    zzqd(zzrb com_google_android_gms_internal_zzrb, GoogleApiAvailability googleApiAvailability) {
        super(com_google_android_gms_internal_zzrb);
        this.wA = -1;
        this.wB = new Handler(Looper.getMainLooper());
        this.vP = googleApiAvailability;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int r6, int r7, android.content.Intent r8) {
        /*
        r5 = this;
        r4 = 18;
        r2 = 13;
        r0 = 1;
        r1 = 0;
        switch(r6) {
            case 1: goto L_0x0027;
            case 2: goto L_0x0010;
            default: goto L_0x0009;
        };
    L_0x0009:
        r0 = r1;
    L_0x000a:
        if (r0 == 0) goto L_0x003e;
    L_0x000c:
        r5.zzaqo();
    L_0x000f:
        return;
    L_0x0010:
        r2 = r5.vP;
        r3 = r5.getActivity();
        r2 = r2.isGooglePlayServicesAvailable(r3);
        if (r2 != 0) goto L_0x0048;
    L_0x001c:
        r1 = r5.wz;
        r1 = r1.getErrorCode();
        if (r1 != r4) goto L_0x000a;
    L_0x0024:
        if (r2 != r4) goto L_0x000a;
    L_0x0026:
        goto L_0x000f;
    L_0x0027:
        r3 = -1;
        if (r7 == r3) goto L_0x000a;
    L_0x002a:
        if (r7 != 0) goto L_0x0009;
    L_0x002c:
        if (r8 == 0) goto L_0x0046;
    L_0x002e:
        r0 = "<<ResolutionFailureErrorDetail>>";
        r0 = r8.getIntExtra(r0, r2);
    L_0x0035:
        r2 = new com.google.android.gms.common.ConnectionResult;
        r3 = 0;
        r2.<init>(r0, r3);
        r5.wz = r2;
        goto L_0x0009;
    L_0x003e:
        r0 = r5.wz;
        r1 = r5.wA;
        r5.zza(r0, r1);
        goto L_0x000f;
    L_0x0046:
        r0 = r2;
        goto L_0x0035;
    L_0x0048:
        r0 = r1;
        goto L_0x001c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzqd.onActivityResult(int, int, android.content.Intent):void");
    }

    public void onCancel(DialogInterface dialogInterface) {
        zza(new ConnectionResult(13, null), this.wA);
        zzaqo();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.wy = bundle.getBoolean("resolving_error", false);
            if (this.wy) {
                this.wA = bundle.getInt("failed_client_id", -1);
                this.wz = new ConnectionResult(bundle.getInt("failed_status"), (PendingIntent) bundle.getParcelable("failed_resolution"));
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("resolving_error", this.wy);
        if (this.wy) {
            bundle.putInt("failed_client_id", this.wA);
            bundle.putInt("failed_status", this.wz.getErrorCode());
            bundle.putParcelable("failed_resolution", this.wz.getResolution());
        }
    }

    public void onStart() {
        super.onStart();
        this.mStarted = true;
    }

    public void onStop() {
        super.onStop();
        this.mStarted = false;
    }

    protected abstract void zza(ConnectionResult connectionResult, int i);

    protected abstract void zzaqk();

    protected void zzaqo() {
        this.wA = -1;
        this.wy = false;
        this.wz = null;
        zzaqk();
    }

    public void zzb(ConnectionResult connectionResult, int i) {
        if (!this.wy) {
            this.wy = true;
            this.wA = i;
            this.wz = connectionResult;
            this.wB.post(new zza());
        }
    }
}

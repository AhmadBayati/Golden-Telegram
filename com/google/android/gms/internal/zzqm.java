package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzai;
import com.google.android.gms.internal.zzqc.zza;

public class zzqm implements zzqq {
    private final zzqr xk;
    private boolean xl;

    /* renamed from: com.google.android.gms.internal.zzqm.1 */
    class C01881 extends zza {
        final /* synthetic */ zzqm xm;

        C01881(zzqm com_google_android_gms_internal_zzqm, zzqq com_google_android_gms_internal_zzqq) {
            this.xm = com_google_android_gms_internal_zzqm;
            super(com_google_android_gms_internal_zzqq);
        }

        public void zzari() {
            this.xm.onConnectionSuspended(1);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzqm.2 */
    class C01892 extends zza {
        final /* synthetic */ zzqm xm;

        C01892(zzqm com_google_android_gms_internal_zzqm, zzqq com_google_android_gms_internal_zzqq) {
            this.xm = com_google_android_gms_internal_zzqm;
            super(com_google_android_gms_internal_zzqq);
        }

        public void zzari() {
            this.xm.xk.yp.zzn(null);
        }
    }

    public zzqm(zzqr com_google_android_gms_internal_zzqr) {
        this.xl = false;
        this.xk = com_google_android_gms_internal_zzqr;
    }

    private <A extends zzb> void zzf(zza<? extends Result, A> com_google_android_gms_internal_zzqc_zza__extends_com_google_android_gms_common_api_Result__A) {
        this.xk.wV.yc.zzb((zzqe) com_google_android_gms_internal_zzqc_zza__extends_com_google_android_gms_common_api_Result__A);
        zzb zzb = this.xk.wV.zzb(com_google_android_gms_internal_zzqc_zza__extends_com_google_android_gms_common_api_Result__A.zzapp());
        if (zzb.isConnected() || !this.xk.yl.containsKey(com_google_android_gms_internal_zzqc_zza__extends_com_google_android_gms_common_api_Result__A.zzapp())) {
            if (zzb instanceof zzai) {
                zzb = ((zzai) zzb).zzavk();
            }
            com_google_android_gms_internal_zzqc_zza__extends_com_google_android_gms_common_api_Result__A.zzb(zzb);
            return;
        }
        com_google_android_gms_internal_zzqc_zza__extends_com_google_android_gms_common_api_Result__A.zzz(new Status(17));
    }

    public void begin() {
    }

    public void connect() {
        if (this.xl) {
            this.xl = false;
            this.xk.zza(new C01892(this, this));
        }
    }

    public boolean disconnect() {
        if (this.xl) {
            return false;
        }
        if (this.xk.wV.zzaru()) {
            this.xl = true;
            for (zzrp zzasu : this.xk.wV.yb) {
                zzasu.zzasu();
            }
            return false;
        }
        this.xk.zzi(null);
        return true;
    }

    public void onConnected(Bundle bundle) {
    }

    public void onConnectionSuspended(int i) {
        this.xk.zzi(null);
        this.xk.yp.zzc(i, this.xl);
    }

    public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
    }

    void zzarh() {
        if (this.xl) {
            this.xl = false;
            this.xk.wV.yc.release();
            disconnect();
        }
    }

    public <A extends zzb, R extends Result, T extends zza<R, A>> T zzc(T t) {
        return zzd(t);
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zzd(T t) {
        try {
            zzf(t);
        } catch (DeadObjectException e) {
            this.xk.zza(new C01881(this, this));
        }
        return t;
    }
}

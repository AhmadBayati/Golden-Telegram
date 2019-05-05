package com.google.android.gms.internal;

import android.os.Handler;
import java.util.concurrent.Executor;

public class zze implements zzn {
    private final Executor zzr;

    /* renamed from: com.google.android.gms.internal.zze.1 */
    class C01821 implements Executor {
        final /* synthetic */ Handler zzs;
        final /* synthetic */ zze zzt;

        C01821(zze com_google_android_gms_internal_zze, Handler handler) {
            this.zzt = com_google_android_gms_internal_zze;
            this.zzs = handler;
        }

        public void execute(Runnable runnable) {
            this.zzs.post(runnable);
        }
    }

    private class zza implements Runnable {
        final /* synthetic */ zze zzt;
        private final zzk zzu;
        private final zzm zzv;
        private final Runnable zzw;

        public zza(zze com_google_android_gms_internal_zze, zzk com_google_android_gms_internal_zzk, zzm com_google_android_gms_internal_zzm, Runnable runnable) {
            this.zzt = com_google_android_gms_internal_zze;
            this.zzu = com_google_android_gms_internal_zzk;
            this.zzv = com_google_android_gms_internal_zzm;
            this.zzw = runnable;
        }

        public void run() {
            if (this.zzu.isCanceled()) {
                this.zzu.zzd("canceled-at-delivery");
                return;
            }
            if (this.zzv.isSuccess()) {
                this.zzu.zza(this.zzv.result);
            } else {
                this.zzu.zzc(this.zzv.zzbg);
            }
            if (this.zzv.zzbh) {
                this.zzu.zzc("intermediate-response");
            } else {
                this.zzu.zzd("done");
            }
            if (this.zzw != null) {
                this.zzw.run();
            }
        }
    }

    public zze(Handler handler) {
        this.zzr = new C01821(this, handler);
    }

    public void zza(zzk<?> com_google_android_gms_internal_zzk_, zzm<?> com_google_android_gms_internal_zzm_) {
        zza(com_google_android_gms_internal_zzk_, com_google_android_gms_internal_zzm_, null);
    }

    public void zza(zzk<?> com_google_android_gms_internal_zzk_, zzm<?> com_google_android_gms_internal_zzm_, Runnable runnable) {
        com_google_android_gms_internal_zzk_.zzu();
        com_google_android_gms_internal_zzk_.zzc("post-response");
        this.zzr.execute(new zza(this, com_google_android_gms_internal_zzk_, com_google_android_gms_internal_zzm_, runnable));
    }

    public void zza(zzk<?> com_google_android_gms_internal_zzk_, zzr com_google_android_gms_internal_zzr) {
        com_google_android_gms_internal_zzk_.zzc("post-error");
        this.zzr.execute(new zza(this, com_google_android_gms_internal_zzk_, zzm.zzd(com_google_android_gms_internal_zzr), null));
    }
}

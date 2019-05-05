package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zze<TResult> implements zzf<TResult> {
    private final Executor aBG;
    private OnSuccessListener<? super TResult> aJD;
    private final Object zzakd;

    /* renamed from: com.google.android.gms.tasks.zze.1 */
    class C02541 implements Runnable {
        final /* synthetic */ zze aJE;
        final /* synthetic */ Task aJw;

        C02541(zze com_google_android_gms_tasks_zze, Task task) {
            this.aJE = com_google_android_gms_tasks_zze;
            this.aJw = task;
        }

        public void run() {
            synchronized (this.aJE.zzakd) {
                if (this.aJE.aJD != null) {
                    this.aJE.aJD.onSuccess(this.aJw.getResult());
                }
            }
        }
    }

    public zze(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.zzakd = new Object();
        this.aBG = executor;
        this.aJD = onSuccessListener;
    }

    public void cancel() {
        synchronized (this.zzakd) {
            this.aJD = null;
        }
    }

    public void onComplete(@NonNull Task<TResult> task) {
        if (task.isSuccessful()) {
            synchronized (this.zzakd) {
                if (this.aJD == null) {
                    return;
                }
                this.aBG.execute(new C02541(this, task));
            }
        }
    }
}

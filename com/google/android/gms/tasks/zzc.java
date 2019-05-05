package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzc<TResult> implements zzf<TResult> {
    private final Executor aBG;
    private OnCompleteListener<TResult> aJz;
    private final Object zzakd;

    /* renamed from: com.google.android.gms.tasks.zzc.1 */
    class C02521 implements Runnable {
        final /* synthetic */ zzc aJA;
        final /* synthetic */ Task aJw;

        C02521(zzc com_google_android_gms_tasks_zzc, Task task) {
            this.aJA = com_google_android_gms_tasks_zzc;
            this.aJw = task;
        }

        public void run() {
            synchronized (this.aJA.zzakd) {
                if (this.aJA.aJz != null) {
                    this.aJA.aJz.onComplete(this.aJw);
                }
            }
        }
    }

    public zzc(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.zzakd = new Object();
        this.aBG = executor;
        this.aJz = onCompleteListener;
    }

    public void cancel() {
        synchronized (this.zzakd) {
            this.aJz = null;
        }
    }

    public void onComplete(@NonNull Task<TResult> task) {
        synchronized (this.zzakd) {
            if (this.aJz == null) {
                return;
            }
            this.aBG.execute(new C02521(this, task));
        }
    }
}

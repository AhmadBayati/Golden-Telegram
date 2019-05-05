package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzd<TResult> implements zzf<TResult> {
    private final Executor aBG;
    private OnFailureListener aJB;
    private final Object zzakd;

    /* renamed from: com.google.android.gms.tasks.zzd.1 */
    class C02531 implements Runnable {
        final /* synthetic */ zzd aJC;
        final /* synthetic */ Task aJw;

        C02531(zzd com_google_android_gms_tasks_zzd, Task task) {
            this.aJC = com_google_android_gms_tasks_zzd;
            this.aJw = task;
        }

        public void run() {
            synchronized (this.aJC.zzakd) {
                if (this.aJC.aJB != null) {
                    this.aJC.aJB.onFailure(this.aJw.getException());
                }
            }
        }
    }

    public zzd(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.zzakd = new Object();
        this.aBG = executor;
        this.aJB = onFailureListener;
    }

    public void cancel() {
        synchronized (this.zzakd) {
            this.aJB = null;
        }
    }

    public void onComplete(@NonNull Task<TResult> task) {
        if (!task.isSuccessful()) {
            synchronized (this.zzakd) {
                if (this.aJB == null) {
                    return;
                }
                this.aBG.execute(new C02531(this, task));
            }
        }
    }
}

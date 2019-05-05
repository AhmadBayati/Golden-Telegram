package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zza<TResult, TContinuationResult> implements zzf<TResult> {
    private final Executor aBG;
    private final Continuation<TResult, TContinuationResult> aJu;
    private final zzh<TContinuationResult> aJv;

    /* renamed from: com.google.android.gms.tasks.zza.1 */
    class C02501 implements Runnable {
        final /* synthetic */ Task aJw;
        final /* synthetic */ zza aJx;

        C02501(zza com_google_android_gms_tasks_zza, Task task) {
            this.aJx = com_google_android_gms_tasks_zza;
            this.aJw = task;
        }

        public void run() {
            try {
                this.aJx.aJv.setResult(this.aJx.aJu.then(this.aJw));
            } catch (Exception e) {
                if (e.getCause() instanceof Exception) {
                    this.aJx.aJv.setException((Exception) e.getCause());
                } else {
                    this.aJx.aJv.setException(e);
                }
            } catch (Exception e2) {
                this.aJx.aJv.setException(e2);
            }
        }
    }

    public zza(@NonNull Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation, @NonNull zzh<TContinuationResult> com_google_android_gms_tasks_zzh_TContinuationResult) {
        this.aBG = executor;
        this.aJu = continuation;
        this.aJv = com_google_android_gms_tasks_zzh_TContinuationResult;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void onComplete(@NonNull Task<TResult> task) {
        this.aBG.execute(new C02501(this, task));
    }
}

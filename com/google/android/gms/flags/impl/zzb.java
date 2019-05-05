package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.zzvb;
import java.util.concurrent.Callable;

public class zzb {
    private static SharedPreferences UF;

    /* renamed from: com.google.android.gms.flags.impl.zzb.1 */
    class C01171 implements Callable<SharedPreferences> {
        final /* synthetic */ Context zzamt;

        C01171(Context context) {
            this.zzamt = context;
        }

        public /* synthetic */ Object call() {
            return zzbhq();
        }

        public SharedPreferences zzbhq() {
            return this.zzamt.getSharedPreferences("google_sdk_flags", 1);
        }
    }

    static {
        UF = null;
    }

    public static SharedPreferences zzn(Context context) {
        SharedPreferences sharedPreferences;
        synchronized (SharedPreferences.class) {
            if (UF == null) {
                UF = (SharedPreferences) zzvb.zzb(new C01171(context));
            }
            sharedPreferences = UF;
        }
        return sharedPreferences;
    }
}

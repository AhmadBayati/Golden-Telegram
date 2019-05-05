package com.google.android.gms.internal;

import android.content.Context;

public class zzsi {
    private static zzsi Fj;
    private zzsh Fi;

    static {
        Fj = new zzsi();
    }

    public zzsi() {
        this.Fi = null;
    }

    public static zzsh zzcr(Context context) {
        return Fj.zzcq(context);
    }

    public synchronized zzsh zzcq(Context context) {
        if (this.Fi == null) {
            if (context.getApplicationContext() != null) {
                context = context.getApplicationContext();
            }
            this.Fi = new zzsh(context);
        }
        return this.Fi;
    }
}

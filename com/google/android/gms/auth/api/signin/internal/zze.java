package com.google.android.gms.auth.api.signin.internal;

public class zze {
    static int hy;
    private int hz;

    static {
        hy = 31;
    }

    public zze() {
        this.hz = 1;
    }

    public int zzahv() {
        return this.hz;
    }

    public zze zzbd(boolean z) {
        this.hz = (z ? 1 : 0) + (this.hz * hy);
        return this;
    }

    public zze zzq(Object obj) {
        this.hz = (obj == null ? 0 : obj.hashCode()) + (this.hz * hy);
        return this;
    }
}

package com.google.android.gms.internal;

public final class zzva {
    private static zzva Uw;
    private final zzux Ux;
    private final zzuy Uy;

    static {
        zza(new zzva());
    }

    private zzva() {
        this.Ux = new zzux();
        this.Uy = new zzuy();
    }

    protected static void zza(zzva com_google_android_gms_internal_zzva) {
        synchronized (zzva.class) {
            Uw = com_google_android_gms_internal_zzva;
        }
    }

    private static zzva zzbhl() {
        zzva com_google_android_gms_internal_zzva;
        synchronized (zzva.class) {
            com_google_android_gms_internal_zzva = Uw;
        }
        return com_google_android_gms_internal_zzva;
    }

    public static zzux zzbhm() {
        return zzbhl().Ux;
    }

    public static zzuy zzbhn() {
        return zzbhl().Uy;
    }
}

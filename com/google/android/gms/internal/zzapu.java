package com.google.android.gms.internal;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class zzapu extends zzaot<Time> {
    public static final zzaou bmp;
    private final DateFormat bmP;

    /* renamed from: com.google.android.gms.internal.zzapu.1 */
    static class C01691 implements zzaou {
        C01691() {
        }

        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            return com_google_android_gms_internal_zzapx_T.by() == Time.class ? new zzapu() : null;
        }
    }

    static {
        bmp = new C01691();
    }

    public zzapu() {
        this.bmP = new SimpleDateFormat("hh:mm:ss a");
    }

    public synchronized void zza(zzaqa com_google_android_gms_internal_zzaqa, Time time) {
        com_google_android_gms_internal_zzaqa.zzut(time == null ? null : this.bmP.format(time));
    }

    public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
        return zzn(com_google_android_gms_internal_zzapy);
    }

    public synchronized Time zzn(zzapy com_google_android_gms_internal_zzapy) {
        Time time;
        if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
            com_google_android_gms_internal_zzapy.nextNull();
            time = null;
        } else {
            try {
                time = new Time(this.bmP.parse(com_google_android_gms_internal_zzapy.nextString()).getTime());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
        return time;
    }
}

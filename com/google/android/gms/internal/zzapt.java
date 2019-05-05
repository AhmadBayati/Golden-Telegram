package com.google.android.gms.internal;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class zzapt extends zzaot<Date> {
    public static final zzaou bmp;
    private final DateFormat bmP;

    /* renamed from: com.google.android.gms.internal.zzapt.1 */
    static class C01681 implements zzaou {
        C01681() {
        }

        public <T> zzaot<T> zza(zzaob com_google_android_gms_internal_zzaob, zzapx<T> com_google_android_gms_internal_zzapx_T) {
            return com_google_android_gms_internal_zzapx_T.by() == Date.class ? new zzapt() : null;
        }
    }

    static {
        bmp = new C01681();
    }

    public zzapt() {
        this.bmP = new SimpleDateFormat("MMM d, yyyy");
    }

    public synchronized void zza(zzaqa com_google_android_gms_internal_zzaqa, Date date) {
        com_google_android_gms_internal_zzaqa.zzut(date == null ? null : this.bmP.format(date));
    }

    public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) {
        return zzm(com_google_android_gms_internal_zzapy);
    }

    public synchronized Date zzm(zzapy com_google_android_gms_internal_zzapy) {
        Date date;
        if (com_google_android_gms_internal_zzapy.bn() == zzapz.NULL) {
            com_google_android_gms_internal_zzapy.nextNull();
            date = null;
        } else {
            try {
                date = new Date(this.bmP.parse(com_google_android_gms_internal_zzapy.nextString()).getTime());
            } catch (Throwable e) {
                throw new zzaoq(e);
            }
        }
        return date;
    }
}

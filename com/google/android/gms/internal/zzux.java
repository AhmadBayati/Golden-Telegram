package com.google.android.gms.internal;

import android.content.Context;
import com.google.android.gms.internal.zzuw.zzd;
import java.util.ArrayList;
import java.util.Collection;

public class zzux {
    private final Collection<zzuw> zzbah;
    private final Collection<zzd> zzbai;
    private final Collection<zzd> zzbaj;

    public zzux() {
        this.zzbah = new ArrayList();
        this.zzbai = new ArrayList();
        this.zzbaj = new ArrayList();
    }

    public static void initialize(Context context) {
        zzva.zzbhn().initialize(context);
    }

    public void zza(zzuw com_google_android_gms_internal_zzuw) {
        this.zzbah.add(com_google_android_gms_internal_zzuw);
    }
}

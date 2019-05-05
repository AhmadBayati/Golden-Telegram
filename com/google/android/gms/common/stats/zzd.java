package com.google.android.gms.common.stats;

import android.content.ComponentName;
import com.google.android.gms.common.GooglePlayServicesUtil;

public final class zzd {
    public static final ComponentName En;
    public static int Eo;
    public static int Ep;
    public static int Eq;
    public static int Er;
    public static int Es;
    public static int Et;
    public static int Eu;
    public static int LOG_LEVEL_OFF;

    static {
        En = new ComponentName(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, "com.google.android.gms.common.stats.GmsCoreStatsService");
        LOG_LEVEL_OFF = 0;
        Eo = 1;
        Ep = 2;
        Eq = 4;
        Er = 8;
        Es = 16;
        Et = 32;
        Eu = 1;
    }
}

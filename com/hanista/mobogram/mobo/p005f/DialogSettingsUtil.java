package com.hanista.mobogram.mobo.p005f;

import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.f.c */
public class DialogSettingsUtil {
    public static List<DialogSettings> f913a;

    public static DialogSettings m997a(long j) {
        for (DialogSettings dialogSettings : DialogSettingsUtil.m998a()) {
            if (dialogSettings.m976b().longValue() == j) {
                return dialogSettings;
            }
        }
        return null;
    }

    public static List<DialogSettings> m998a() {
        if (f913a == null) {
            DialogSettingsUtil.m1001b();
        }
        return f913a;
    }

    public static void m999a(DialogSettings dialogSettings) {
        dialogSettings.m974a(Long.valueOf(new DataBaseAccess().m842a(dialogSettings).longValue()));
        DialogSettingsUtil.m1001b();
    }

    public static void m1000a(Long l) {
        if (l != null && l.longValue() != 0) {
            new DataBaseAccess().m903n(l);
            DialogSettingsUtil.m1001b();
        }
    }

    private static void m1001b() {
        f913a = new DataBaseAccess().m914u();
    }
}

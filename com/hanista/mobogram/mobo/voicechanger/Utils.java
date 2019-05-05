package com.hanista.mobogram.mobo.voicechanger;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.e */
public final class Utils {
    private static final Map<String, Long> f2639c;
    private final Context f2640a;
    private final Preferences f2641b;

    static {
        f2639c = new HashMap();
    }

    public Utils(Context context) {
        this.f2640a = context;
        this.f2641b = new Preferences(context);
    }

    public static void m2588a() {
        try {
            System.loadLibrary("MoboVoiceChanger");
        } catch (UnsatisfiedLinkError e) {
            Log.d("MoboVoiceChanger", String.format("Native library %s could not be loaded!", new Object[]{"MoboVoiceChanger"}));
        }
    }

    public void m2589a(String str) {
        if (this.f2641b.m2563m()) {
            Log.d("MoboVoiceChanger", str);
        }
    }

    public void m2590a(String str, Object... objArr) {
        m2589a(String.format(str, objArr));
    }
}

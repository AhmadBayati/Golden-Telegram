package com.hanista.mobogram.mobo.persianmaterialdatetimepicker;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings.System;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.a */
public class HapticFeedbackController {
    private final Context f2122a;
    private final ContentObserver f2123b;
    private Vibrator f2124c;
    private boolean f2125d;
    private long f2126e;

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.a.1 */
    class HapticFeedbackController extends ContentObserver {
        final /* synthetic */ HapticFeedbackController f2117a;

        HapticFeedbackController(HapticFeedbackController hapticFeedbackController, Handler handler) {
            this.f2117a = hapticFeedbackController;
            super(handler);
        }

        public void onChange(boolean z) {
            this.f2117a.f2125d = HapticFeedbackController.m2065b(this.f2117a.f2122a);
        }
    }

    public HapticFeedbackController(Context context) {
        this.f2122a = context;
        this.f2123b = new HapticFeedbackController(this, null);
    }

    private static boolean m2065b(Context context) {
        return System.getInt(context.getContentResolver(), "haptic_feedback_enabled", 0) == 1;
    }

    public void m2066a() {
        this.f2124c = (Vibrator) this.f2122a.getSystemService("vibrator");
        this.f2125d = HapticFeedbackController.m2065b(this.f2122a);
        this.f2122a.getContentResolver().registerContentObserver(System.getUriFor("haptic_feedback_enabled"), false, this.f2123b);
    }

    public void m2067b() {
        this.f2124c = null;
        this.f2122a.getContentResolver().unregisterContentObserver(this.f2123b);
    }

    public void m2068c() {
        if (this.f2124c != null && this.f2125d) {
            long uptimeMillis = SystemClock.uptimeMillis();
            if (uptimeMillis - this.f2126e >= 125) {
                this.f2124c.vibrate(5);
                this.f2126e = uptimeMillis;
            }
        }
    }
}

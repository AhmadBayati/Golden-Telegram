package com.hanista.mobogram.mobo.voicechanger.dsp.processors;

import android.content.Context;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.voicechanger.Preferences;
import com.hanista.mobogram.mobo.voicechanger.Utils;
import com.hanista.mobogram.mobo.voicechanger.dsp.Math;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.processors.b */
public final class DenoiseProcessor {
    private final int f2619a;
    private final boolean f2620b;
    private final boolean f2621c;
    private final float f2622d;
    private final float f2623e;
    private final float f2624f;

    public DenoiseProcessor(int i, Context context) {
        this.f2619a = i;
        Preferences preferences = new Preferences(context);
        this.f2620b = preferences.m2554d();
        this.f2621c = preferences.m2556f();
        this.f2622d = Math.pow(10.0f, (float) (-preferences.m2555e()));
        this.f2623e = (((float) preferences.m2557g()) * 2.0f) / ((float) i);
        this.f2624f = (((float) preferences.m2558h()) * 2.0f) / ((float) i);
        new Utils(context).m2590a("Spectral noise gate coeff is %s.", Float.toString(this.f2622d));
        new Utils(context).m2590a("Bandpass freqs are %s and %s.", Integer.toString(preferences.m2557g()), Integer.toString(preferences.m2558h()));
    }

    private static float m2580a(float f, float f2) {
        return f / (f + f2);
    }

    public void m2581a(float[] fArr) {
        if (this.f2620b || this.f2621c) {
            int length = fArr.length / 2;
            float f = this.f2622d;
            int i = (int) (((float) length) * this.f2623e);
            int i2 = (int) (((float) length) * this.f2624f);
            int i3 = 1;
            Object obj = 1;
            while (i3 < length) {
                float f2 = fArr[i3 * 2];
                float f3 = fArr[(i3 * 2) + 1];
                float abs = Math.abs(f2, f3);
                float f4 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                if (this.f2621c) {
                    obj = (i3 < i || i3 > i2) ? null : 1;
                    if (obj == null) {
                        f4 = 0.0f;
                    }
                }
                if (this.f2620b && r0 != null) {
                    f4 = DenoiseProcessor.m2580a(abs / ((float) length), f);
                }
                fArr[i3 * 2] = f2 * f4;
                fArr[(i3 * 2) + 1] = f4 * f3;
                i3++;
            }
        }
    }
}

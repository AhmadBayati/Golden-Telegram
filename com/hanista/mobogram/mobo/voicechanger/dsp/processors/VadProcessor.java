package com.hanista.mobogram.mobo.voicechanger.dsp.processors;

import android.content.Context;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.voicechanger.Preferences;
import com.hanista.mobogram.mobo.voicechanger.Utils;
import com.hanista.mobogram.mobo.voicechanger.dsp.LuenbergerObserver;
import com.hanista.mobogram.mobo.voicechanger.dsp.Math;
import com.hanista.mobogram.mobo.voicechanger.dsp.SchmittTrigger;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.processors.g */
public final class VadProcessor {
    private final int f2628a;
    private final float f2629b;
    private final int f2630c;
    private final float[] f2631d;
    private final LuenbergerObserver f2632e;
    private final SchmittTrigger f2633f;
    private final float f2634g;
    private float f2635h;
    private final boolean f2636i;
    private Utils f2637j;
    private boolean f2638k;

    public VadProcessor(int i, int i2, int i3, int i4, boolean z) {
        this.f2629b = 0.02f;
        this.f2631d = new float[]{0.3f, 0.02f};
        this.f2637j = null;
        this.f2638k = false;
        this.f2628a = i;
        this.f2630c = Math.m2566a(((float) i) * 0.02f);
        if (this.f2637j != null) {
            this.f2637j.m2590a("VAD desired window size is %s.", Integer.valueOf(this.f2630c));
        }
        float f = ((float) (i2 + i3)) / 2.0f;
        this.f2632e = new LuenbergerObserver(f, 0.0f, this.f2631d);
        this.f2633f = new SchmittTrigger(false, f, (float) i2, (float) i3);
        this.f2634g = (float) i4;
        this.f2635h = 0.0f;
        this.f2636i = z;
    }

    public VadProcessor(int i, Context context) {
        this(i, new Preferences(context).m2560j(), new Preferences(context).m2561k(), new Preferences(context).m2562l(), new Preferences(context).m2559i());
        if (new Preferences(context).m2563m()) {
            this.f2637j = new Utils(context);
        }
    }

    private void m2586a(short[] sArr, int i, int i2, float f) {
        boolean z;
        int i3;
        boolean a = this.f2633f.m2574a(this.f2632e.m2573a(Math.rms2dbfs(Math.rms(sArr, i, i2), 1.0E-10f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
        if (this.f2634g > 0.0f) {
            if (a) {
                this.f2635h = 0.0f;
            } else {
                this.f2635h = Math.min(this.f2634g, this.f2635h + f);
                z = this.f2635h < this.f2634g;
                if (!z) {
                    for (i3 = i; i3 < i + i2; i3++) {
                        sArr[i3] = (short) 0;
                    }
                }
                if (this.f2637j != null && this.f2638k != z) {
                    if (z) {
                        this.f2637j.m2589a("Voice activity detected.");
                    } else {
                        this.f2637j.m2589a("Voice inactivity detected.");
                    }
                    this.f2638k = z;
                    return;
                }
            }
        }
        z = a;
        if (z) {
            for (i3 = i; i3 < i + i2; i3++) {
                sArr[i3] = (short) 0;
            }
        }
        if (this.f2637j != null) {
        }
    }

    public void m2587a(short[] sArr) {
        if (this.f2636i) {
            int length = sArr.length / this.f2630c;
            int ceil = length > 0 ? (int) Math.ceil(((float) sArr.length) / ((float) length)) : sArr.length;
            float f = ((float) ceil) / ((float) this.f2628a);
            for (int i = 0; i < length; i++) {
                m2586a(sArr, i * ceil, ceil, f);
            }
        }
    }
}

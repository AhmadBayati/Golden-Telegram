package com.hanista.mobogram.mobo.voicechanger.dsp.processors;

import android.content.Context;
import com.hanista.mobogram.mobo.voicechanger.Preferences;
import com.hanista.mobogram.mobo.voicechanger.dsp.LuenbergerObserver;
import com.hanista.mobogram.mobo.voicechanger.dsp.Math;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.processors.e */
public final class OffsetProcessor {
    private final boolean f2625a;
    private final float[] f2626b;
    private final LuenbergerObserver f2627c;

    public OffsetProcessor(Context context) {
        this(new Preferences(context).m2553c());
    }

    public OffsetProcessor(boolean z) {
        this.f2626b = new float[]{0.025f, 0.0f};
        this.f2625a = z;
        this.f2627c = new LuenbergerObserver(0.0f, 0.0f, this.f2626b);
    }

    public void m2584a(short[] sArr) {
        int i = 0;
        if (this.f2625a) {
            short a = (short) ((int) this.f2627c.m2573a((float) Math.mean(sArr, 0, sArr.length)));
            while (i < sArr.length) {
                sArr[i] = (short) (sArr[i] - a);
                i++;
            }
        }
    }
}

package com.hanista.mobogram.mobo.voicechanger.dsp.processors;

import com.hanista.mobogram.mobo.voicechanger.dsp.Math;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.processors.d */
public final class HoarsenessProcessor {
    public static void m2583a(float[] fArr) {
        int length = fArr.length / 2;
        for (int i = 1; i < length; i++) {
            float abs = Math.abs(fArr[i * 2], fArr[(i * 2) + 1]);
            float random = Math.random(-3.1415927f, 3.1415927f);
            fArr[i * 2] = Math.real(abs, random);
            fArr[(i * 2) + 1] = Math.imag(abs, random);
        }
    }
}

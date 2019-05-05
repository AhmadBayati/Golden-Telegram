package com.hanista.mobogram.mobo.voicechanger.dsp.processors;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.processors.c */
public final class DetuneProcessor {
    public static void m2582a(float[] fArr) {
        int length = fArr.length / 2;
        for (int i = 1; i < length; i++) {
            float f = -fArr[(i * 2) + 1];
            fArr[i * 2] = fArr[i * 2];
            fArr[(i * 2) + 1] = f;
        }
    }
}

package com.hanista.mobogram.mobo.voicechanger.dsp.processors;

import com.hanista.mobogram.mobo.voicechanger.dsp.Math;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.processors.f */
public final class RobotizeProcessor {
    public static void m2585a(float[] fArr) {
        int length = fArr.length / 2;
        for (int i = 1; i < length; i++) {
            fArr[i * 2] = Math.abs(fArr[i * 2], fArr[(i * 2) + 1]);
            fArr[(i * 2) + 1] = 0.0f;
        }
    }
}

package com.hanista.mobogram.mobo.voicechanger.dsp;

import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.c */
public final class Window {
    private final int f2612a;
    private final int f2613b;
    private final boolean f2614c;
    private final boolean f2615d;

    public Window(int i, int i2, boolean z, boolean z2) {
        this.f2612a = i;
        this.f2613b = i2;
        this.f2614c = z;
        this.f2615d = z2;
    }

    public Window(int i, boolean z) {
        this(i, i, z, false);
    }

    private void m2575a(float[] fArr) {
        int i = 0;
        float f = 0.0f;
        for (int i2 = 0; i2 < this.f2612a; i2++) {
            f += fArr[i2] * fArr[i2];
        }
        float sqrt = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / Math.sqrt(f / ((float) this.f2613b));
        while (i < this.f2612a) {
            fArr[i] = fArr[i] * sqrt;
            i++;
        }
    }

    public float[] m2576a() {
        float[] fArr = new float[this.f2612a];
        int i = this.f2614c ? this.f2612a + 1 : this.f2612a;
        for (int i2 = 0; i2 < this.f2612a; i2++) {
            fArr[i2] = 0.5f * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - Math.cos((6.2831855f * ((float) i2)) / (((float) i) - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
        }
        if (this.f2615d) {
            m2575a(fArr);
        }
        return fArr;
    }
}

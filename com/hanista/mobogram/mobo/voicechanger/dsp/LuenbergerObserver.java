package com.hanista.mobogram.mobo.voicechanger.dsp;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.a */
public final class LuenbergerObserver {
    private float f2605a;
    private float f2606b;
    private final float[] f2607c;

    public LuenbergerObserver(float f, float f2, float[] fArr) {
        this.f2605a = f;
        this.f2606b = f2;
        this.f2607c = fArr;
    }

    private float m2571a() {
        return this.f2605a + this.f2606b;
    }

    private float m2572b(float f) {
        float f2 = f - this.f2605a;
        this.f2605a = m2571a() + (this.f2607c[0] * f2);
        this.f2606b += f2 * this.f2607c[1];
        return this.f2605a;
    }

    public float m2573a(float f) {
        m2572b(f);
        return m2571a();
    }
}

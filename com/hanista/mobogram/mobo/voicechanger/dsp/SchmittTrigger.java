package com.hanista.mobogram.mobo.voicechanger.dsp;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.b */
public final class SchmittTrigger {
    private boolean f2608a;
    private float f2609b;
    private final float f2610c;
    private final float f2611d;

    public SchmittTrigger(boolean z, float f, float f2, float f3) {
        this.f2608a = z;
        this.f2609b = f;
        this.f2610c = f2;
        this.f2611d = f3;
    }

    public boolean m2574a(float f) {
        if (f > this.f2609b && f > this.f2611d) {
            this.f2608a = true;
        } else if (f < this.f2609b && f < this.f2610c) {
            this.f2608a = false;
        }
        this.f2609b = f;
        return this.f2608a;
    }
}

package com.hanista.mobogram.mobo.voicechanger.dsp.processors;

public final class NativeTimescaleProcessor {
    private final long f2617a;

    public NativeTimescaleProcessor(int i, int i2, int i3) {
        this.f2617a = alloc(i, i2, i3);
    }

    private native long alloc(int i, int i2, int i3);

    private native void processFrame(long j, float[] fArr);

    public void m2578a(float[] fArr) {
        processFrame(this.f2617a, fArr);
    }
}

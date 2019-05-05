package com.hanista.mobogram.mobo.voicechanger.dsp.processors;

public class NativeResampleProcessor {
    private final long f2616a;

    public NativeResampleProcessor(int i, int i2) {
        this.f2616a = alloc(i, i2);
    }

    private native long alloc(int i, int i2);

    private native void processFrame(long j, float[] fArr, float[] fArr2);

    public void m2577a(float[] fArr, float[] fArr2) {
        processFrame(this.f2616a, fArr, fArr2);
    }
}

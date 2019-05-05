package com.hanista.mobogram.mobo.voicechanger.dsp;

public final class KissFFT {
    private final long f2582a;

    public KissFFT(int i) {
        this.f2582a = alloc(i);
    }

    private native long alloc(int i);

    private native void fft(long j, float[] fArr);

    private native void ifft(long j, float[] fArr);

    public void m2564a(float[] fArr) {
        fft(this.f2582a, fArr);
    }

    public void m2565b(float[] fArr) {
        ifft(this.f2582a, fArr);
    }
}

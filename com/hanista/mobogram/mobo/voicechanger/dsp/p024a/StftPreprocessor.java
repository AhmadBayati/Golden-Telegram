package com.hanista.mobogram.mobo.voicechanger.dsp.p024a;

import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.voicechanger.DataHelper;
import com.hanista.mobogram.mobo.voicechanger.dsp.KissFFT;
import com.hanista.mobogram.mobo.voicechanger.dsp.Math;
import com.hanista.mobogram.mobo.voicechanger.dsp.Window;
import com.hanista.mobogram.mobo.voicechanger.dsp.processors.AmplifyProcessor;
import com.hanista.mobogram.mobo.voicechanger.dsp.processors.DenoiseProcessor;
import com.hanista.mobogram.mobo.voicechanger.dsp.processors.OffsetProcessor;
import com.hanista.mobogram.mobo.voicechanger.dsp.processors.VadProcessor;
import com.hanista.mobogram.mobo.voicechanger.p022b.AudioDevice;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.a.b */
public final class StftPreprocessor {
    private final AudioDevice f2592a;
    private final int f2593b;
    private final int f2594c;
    private final boolean f2595d;
    private final OffsetProcessor f2596e;
    private final VadProcessor f2597f;
    private final AmplifyProcessor f2598g;
    private final DenoiseProcessor f2599h;
    private KissFFT f2600i;
    private final float[] f2601j;
    private final short[] f2602k;
    private final short[] f2603l;
    private int f2604m;

    public StftPreprocessor(AudioDevice audioDevice, int i, int i2, boolean z) {
        this.f2600i = null;
        this.f2592a = audioDevice;
        this.f2593b = i;
        this.f2594c = i2;
        this.f2595d = z;
        this.f2596e = new OffsetProcessor(audioDevice.m2528a());
        this.f2597f = new VadProcessor(audioDevice.m2530b(), audioDevice.m2528a());
        this.f2598g = new AmplifyProcessor(audioDevice.m2528a());
        this.f2599h = new DenoiseProcessor(audioDevice.m2530b(), audioDevice.m2528a());
        this.f2600i = new KissFFT(i);
        this.f2601j = new Window(i, true).m2576a();
        this.f2602k = new short[i];
        this.f2603l = new short[i];
        this.f2604m = -1;
    }

    private static void m2569a(short[] sArr, int i, float[] fArr, int i2, int i3, float[] fArr2) {
        if (i3 != 0) {
            for (int i4 = 0; i4 < i3; i4++) {
                fArr[i4 + i2] = Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, Math.max(Face.UNCOMPUTED_PROBABILITY, ((float) sArr[i4 + i]) / 32767.0f)) * fArr2[i4 + i2];
            }
        }
    }

    public void m2570a(float[] fArr, DataHelper dataHelper) {
        if (this.f2604m == -1) {
            this.f2604m = this.f2593b;
            dataHelper.m2546a(this.f2603l);
            this.f2596e.m2584a(this.f2603l);
            this.f2597f.m2587a(this.f2603l);
            this.f2598g.m2579a(this.f2603l);
        } else if (this.f2604m >= this.f2593b) {
            this.f2604m -= this.f2593b;
            System.arraycopy(this.f2603l, 0, this.f2602k, 0, this.f2593b);
            dataHelper.m2546a(this.f2603l);
            this.f2596e.m2584a(this.f2603l);
            this.f2597f.m2587a(this.f2603l);
            this.f2598g.m2579a(this.f2603l);
        }
        StftPreprocessor.m2569a(this.f2602k, this.f2604m, fArr, 0, this.f2593b - this.f2604m, this.f2601j);
        StftPreprocessor.m2569a(this.f2603l, 0, fArr, this.f2593b - this.f2604m, this.f2604m, this.f2601j);
        if (this.f2595d) {
            this.f2600i.m2564a(fArr);
            this.f2599h.m2581a(fArr);
        }
        this.f2604m += this.f2594c;
    }
}

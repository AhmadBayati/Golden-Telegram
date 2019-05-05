package com.hanista.mobogram.mobo.voicechanger.dsp.p024a;

import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.voicechanger.dsp.KissFFT;
import com.hanista.mobogram.mobo.voicechanger.dsp.Math;
import com.hanista.mobogram.mobo.voicechanger.dsp.Window;
import com.hanista.mobogram.mobo.voicechanger.p022b.AudioDevice;
import java.util.Arrays;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.dsp.a.a */
public final class StftPostprocessor {
    private final AudioDevice f2583a;
    private final int f2584b;
    private final int f2585c;
    private final boolean f2586d;
    private KissFFT f2587e;
    private final float[] f2588f;
    private final short[] f2589g;
    private final short[] f2590h;
    private int f2591i;

    public StftPostprocessor(AudioDevice audioDevice, int i, int i2, boolean z) {
        this.f2587e = null;
        this.f2583a = audioDevice;
        this.f2584b = i;
        this.f2585c = i2;
        this.f2586d = z;
        this.f2587e = new KissFFT(i);
        this.f2588f = new Window(i, true).m2576a();
        this.f2589g = new short[i];
        this.f2590h = new short[i];
        this.f2591i = 0;
    }

    private static void m2567a(float[] fArr, int i, short[] sArr, int i2, int i3, float[] fArr2) {
        if (i3 != 0) {
            for (int i4 = 0; i4 < i3; i4++) {
                float min = Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, Math.max(Face.UNCOMPUTED_PROBABILITY, fArr[i4 + i] * fArr2[i4 + i]));
                int i5 = i4 + i2;
                sArr[i5] = (short) (((short) Math.m2566a(min * 32767.0f)) + sArr[i5]);
            }
        }
    }

    public short[] m2568a(float[] fArr) {
        if (this.f2586d) {
            this.f2587e.m2565b(fArr);
        }
        StftPostprocessor.m2567a(fArr, 0, this.f2589g, this.f2591i, this.f2584b - this.f2591i, this.f2588f);
        StftPostprocessor.m2567a(fArr, this.f2584b - this.f2591i, this.f2590h, 0, this.f2591i, this.f2588f);
        this.f2591i += this.f2585c;
        Object obj = new short[this.f2589g.length];
        if (this.f2591i < this.f2584b) {
            return null;
        }
        this.f2591i -= this.f2584b;
        System.arraycopy(this.f2589g, 0, obj, 0, this.f2589g.length);
        System.arraycopy(this.f2590h, 0, this.f2589g, 0, this.f2584b);
        Arrays.fill(this.f2590h, (short) 0);
        return obj;
    }
}

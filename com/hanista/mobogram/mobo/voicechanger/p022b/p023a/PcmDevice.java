package com.hanista.mobogram.mobo.voicechanger.p022b.p023a;

import android.content.Context;
import com.hanista.mobogram.mobo.voicechanger.p022b.AudioDevice;

/* renamed from: com.hanista.mobogram.mobo.voicechanger.b.a.a */
public abstract class PcmDevice extends AudioDevice {
    private int f2562b;
    private int f2563c;
    private int f2564d;
    private int f2565e;
    private int f2566f;

    public PcmDevice(Context context) {
        super(context);
    }

    protected void m2532b(int i) {
        this.f2562b = i;
    }

    protected void m2533c(int i) {
        this.f2563c = i;
    }

    public int m2534d() {
        return this.f2563c;
    }

    protected void m2535d(int i) {
        this.f2564d = i;
    }

    public int m2536e() {
        return this.f2564d;
    }

    protected void m2537e(int i) {
        this.f2565e = i;
    }

    public int m2538f() {
        return this.f2565e;
    }

    protected void m2539f(int i) {
        this.f2566f = i;
    }

    public int m2540g() {
        return this.f2566f;
    }
}

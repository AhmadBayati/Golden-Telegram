package com.hanista.mobogram.mobo.markers;

import android.os.SystemClock;

/* renamed from: com.hanista.mobogram.mobo.markers.g */
public class Spot {
    public float f1855a;
    public float f1856b;
    public float f1857c;
    public float f1858d;
    public long f1859e;
    public int f1860f;

    public Spot() {
        this(0.0f, 0.0f, 0.0f, 0.0f, SystemClock.currentThreadTimeMillis(), 1);
    }

    public Spot(float f, float f2, float f3, float f4, long j, int i) {
        m1871a(f, f2, f3, f4, j, i);
    }

    public Spot(Spot spot) {
        this(spot.f1855a, spot.f1856b, spot.f1857c, spot.f1858d, spot.f1859e, spot.f1860f);
    }

    public void m1871a(float f, float f2, float f3, float f4, long j, int i) {
        this.f1855a = f;
        this.f1856b = f2;
        this.f1857c = f3;
        this.f1858d = f4;
        this.f1859e = j;
        this.f1860f = i;
    }
}

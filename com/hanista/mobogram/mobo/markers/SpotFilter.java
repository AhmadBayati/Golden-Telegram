package com.hanista.mobogram.mobo.markers;

import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import java.util.Iterator;
import java.util.LinkedList;

/* renamed from: com.hanista.mobogram.mobo.markers.h */
public class SpotFilter {
    public static boolean f1861a;
    public static boolean f1862b;
    LinkedList<Spot> f1863c;
    int f1864d;
    SpotFilter f1865e;
    Spot f1866f;
    private float f1867g;
    private float f1868h;

    /* renamed from: com.hanista.mobogram.mobo.markers.h.a */
    public interface SpotFilter {
        void m1825a(Spot spot);
    }

    static {
        f1861a = true;
        f1862b = true;
    }

    public SpotFilter(int i, float f, float f2, SpotFilter spotFilter) {
        this.f1866f = new Spot();
        this.f1863c = new LinkedList();
        this.f1864d = i;
        this.f1865e = spotFilter;
        if (f < 0.0f || f > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        this.f1867g = f;
        if (f2 < 0.0f || f2 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        this.f1868h = f2;
    }

    public Spot m1872a(Spot spot) {
        if (spot == null) {
            spot = new Spot();
        }
        Iterator it = this.f1863c.iterator();
        float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        float f2 = 0.0f;
        float f3 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        float f4 = 0.0f;
        float f5 = 0.0f;
        float f6 = 0.0f;
        float f7 = 0.0f;
        long j = 0;
        float f8 = 0.0f;
        while (it.hasNext()) {
            Spot spot2 = (Spot) it.next();
            f5 += spot2.f1855a * f;
            f6 += spot2.f1856b * f;
            j = (long) (((float) j) + (((float) spot2.f1859e) * f));
            f7 += spot2.f1858d * f3;
            f8 += spot2.f1857c * f3;
            f2 += f;
            f *= this.f1867g;
            f4 += f3;
            f3 *= this.f1868h;
            if (f1862b && spot2.f1860f == 2) {
                break;
            }
        }
        spot.f1855a = f5 / f2;
        spot.f1856b = f6 / f2;
        spot.f1858d = f7 / f4;
        spot.f1857c = f8 / f4;
        spot.f1859e = j;
        spot.f1860f = ((Spot) this.f1863c.get(0)).f1860f;
        return spot;
    }

    public void m1873a() {
        while (this.f1863c.size() > 0) {
            this.f1866f = m1872a(this.f1866f);
            this.f1863c.removeLast();
            this.f1865e.m1825a(this.f1866f);
        }
        this.f1863c.clear();
    }

    public void m1874b(Spot spot) {
        m1875c(new Spot(spot));
    }

    protected void m1875c(Spot spot) {
        if (this.f1863c.size() == this.f1864d) {
            this.f1863c.removeLast();
        }
        this.f1863c.add(0, spot);
        this.f1866f = m1872a(this.f1866f);
        this.f1865e.m1825a(this.f1866f);
    }
}

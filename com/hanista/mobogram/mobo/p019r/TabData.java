package com.hanista.mobogram.mobo.p019r;

/* renamed from: com.hanista.mobogram.mobo.r.b */
public class TabData implements Comparable<TabData> {
    private int f2424a;
    private int f2425b;
    private int f2426c;
    private int f2427d;
    private int f2428e;
    private int f2429f;
    private boolean f2430g;
    private String f2431h;
    private int f2432i;
    private int f2433j;
    private int f2434k;

    public TabData(int i, int i2, boolean z, String str, int i3, int i4, int i5, int i6, int i7) {
        this.f2424a = i;
        this.f2425b = i2;
        this.f2430g = z;
        this.f2431h = str;
        this.f2432i = i3;
        this.f2428e = i4;
        this.f2429f = i5;
        this.f2426c = i6;
        this.f2427d = i7;
    }

    public int m2225a() {
        return this.f2424a;
    }

    public int m2226a(TabData tabData) {
        return this.f2432i < tabData.f2432i ? -1 : this.f2432i > tabData.f2432i ? 1 : 0;
    }

    public void m2227a(int i) {
        this.f2432i = i;
    }

    public int m2228b() {
        return this.f2425b;
    }

    public void m2229b(int i) {
        this.f2433j = i;
    }

    public void m2230c(int i) {
        this.f2434k = i;
    }

    public boolean m2231c() {
        return this.f2430g;
    }

    public /* synthetic */ int compareTo(Object obj) {
        return m2226a((TabData) obj);
    }

    public String m2232d() {
        return this.f2431h;
    }

    public void m2233d(int i) {
        this.f2434k += i;
    }

    public int m2234e() {
        return this.f2432i;
    }

    public void m2235e(int i) {
        this.f2433j += i;
    }

    public int m2236f() {
        return this.f2428e;
    }

    public int m2237g() {
        return this.f2429f;
    }

    public int m2238h() {
        return this.f2433j;
    }

    public int m2239i() {
        return this.f2434k;
    }

    public int m2240j() {
        return this.f2426c;
    }

    public int m2241k() {
        return this.f2427d;
    }

    public int m2242l() {
        return this.f2434k + this.f2433j;
    }
}

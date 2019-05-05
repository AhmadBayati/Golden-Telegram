package com.hanista.mobogram.mobo.p005f;

/* renamed from: com.hanista.mobogram.mobo.f.a */
public class DialogSettings {
    private Long f888a;
    private Long f889b;
    private int f890c;
    private int f891d;
    private boolean f892e;
    private boolean f893f;
    private int f894g;
    private Long f895h;

    public DialogSettings(Long l, Long l2, int i, int i2, boolean z, boolean z2, int i3, Long l3) {
        this.f888a = l;
        this.f889b = l2;
        this.f890c = i;
        this.f891d = i2;
        this.f892e = z;
        this.f893f = z2;
        this.f894g = i3;
        this.f895h = l3;
    }

    public Long m972a() {
        return this.f888a;
    }

    public void m973a(int i) {
        this.f890c = i;
    }

    public void m974a(Long l) {
        this.f888a = l;
    }

    public void m975a(boolean z) {
        this.f892e = z;
    }

    public Long m976b() {
        return this.f889b;
    }

    public void m977b(int i) {
        this.f894g = i;
    }

    public void m978b(Long l) {
        this.f889b = l;
    }

    public void m979b(boolean z) {
        this.f893f = z;
    }

    public int m980c() {
        return this.f890c;
    }

    public int m981d() {
        return this.f891d;
    }

    public boolean m982e() {
        return this.f892e;
    }

    public boolean m983f() {
        return this.f893f;
    }

    public int m984g() {
        return this.f894g;
    }

    public Long m985h() {
        return this.f895h;
    }
}

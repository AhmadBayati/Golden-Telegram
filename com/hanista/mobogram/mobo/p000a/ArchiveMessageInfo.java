package com.hanista.mobogram.mobo.p000a;

/* renamed from: com.hanista.mobogram.mobo.a.d */
public class ArchiveMessageInfo {
    private Long f119a;
    private Integer f120b;
    private Integer f121c;
    private Long f122d;
    private Long f123e;

    public ArchiveMessageInfo(Long l, Integer num, Integer num2, Long l2, Long l3) {
        this.f119a = l;
        this.f120b = num;
        this.f121c = num2;
        this.f122d = l2;
        this.f123e = l3;
    }

    public Long m234a() {
        return this.f119a;
    }

    public void m235a(Integer num) {
        this.f120b = num;
    }

    public void m236a(Long l) {
        this.f123e = l;
    }

    public Integer m237b() {
        return this.f120b;
    }

    public Integer m238c() {
        return this.f121c;
    }

    public Long m239d() {
        return this.f122d;
    }

    public Long m240e() {
        return this.f123e;
    }
}

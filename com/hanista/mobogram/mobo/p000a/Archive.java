package com.hanista.mobogram.mobo.p000a;

import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.a.a */
public class Archive {
    private Long f80a;
    private String f81b;
    private Integer f82c;
    private List<ArchiveMessageInfo> f83d;
    private List<Integer> f84e;

    public Archive() {
        this.f83d = new ArrayList();
    }

    public Archive(Long l, String str, Integer num) {
        this.f83d = new ArrayList();
        this.f80a = l;
        this.f81b = str;
        this.f82c = num;
    }

    public Long m204a() {
        return this.f80a;
    }

    public void m205a(Integer num) {
        this.f82c = num;
    }

    public void m206a(Long l) {
        this.f80a = l;
    }

    public void m207a(String str) {
        this.f81b = str;
    }

    public String m208b() {
        return this.f81b;
    }

    public Integer m209c() {
        return this.f82c;
    }

    public List<ArchiveMessageInfo> m210d() {
        return this.f83d;
    }

    public List<Integer> m211e() {
        if (this.f84e == null) {
            this.f84e = new ArrayList();
            for (ArchiveMessageInfo b : this.f83d) {
                this.f84e.add(b.m237b());
            }
        }
        return this.f84e;
    }

    public boolean m212f() {
        return this.f80a.longValue() == 0 || this.f80a.longValue() == -1;
    }
}

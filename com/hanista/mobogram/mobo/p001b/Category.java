package com.hanista.mobogram.mobo.p001b;

import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.b.a */
public class Category {
    private Long f155a;
    private String f156b;
    private Integer f157c;
    private List<Long> f158d;
    private int f159e;
    private int f160f;

    public Category() {
        this.f158d = new ArrayList();
    }

    public Category(Long l, String str, Integer num) {
        this.f158d = new ArrayList();
        this.f155a = l;
        this.f156b = str;
        this.f157c = num;
    }

    public Long m276a() {
        return this.f155a;
    }

    public void m277a(int i) {
        this.f159e = i;
    }

    public void m278a(Integer num) {
        this.f157c = num;
    }

    public void m279a(Long l) {
        this.f155a = l;
    }

    public void m280a(String str) {
        this.f156b = str;
    }

    public String m281b() {
        return this.f156b;
    }

    public void m282b(int i) {
        this.f160f = i;
    }

    public Integer m283c() {
        return this.f157c;
    }

    public List<Long> m284d() {
        return this.f158d;
    }

    public int m285e() {
        return this.f159e;
    }

    public int m286f() {
        return this.f160f;
    }
}

package com.hanista.mobogram.mobo.dialogdm;

import com.hanista.mobogram.tgnet.TLRPC.TL_dialog;

/* renamed from: com.hanista.mobogram.mobo.dialogdm.a */
public class DialogDm {
    private Long f561a;
    private Long f562b;
    private int f563c;
    private int f564d;
    private Integer f565e;
    private Long f566f;
    private TL_dialog f567g;

    public DialogDm(Long l, Long l2, int i, int i2, int i3, Long l3) {
        this.f561a = l;
        this.f562b = l2;
        this.f563c = i;
        this.f564d = i2;
        this.f565e = Integer.valueOf(i3);
        this.f566f = l3;
    }

    public Long m556a() {
        return this.f561a;
    }

    public void m557a(int i) {
        this.f563c = i;
    }

    public void m558a(TL_dialog tL_dialog) {
        this.f567g = tL_dialog;
    }

    public void m559a(Integer num) {
        this.f565e = num;
    }

    public void m560a(Long l) {
        this.f562b = l;
    }

    public long m561b() {
        return this.f562b.longValue();
    }

    public void m562b(int i) {
        this.f564d = i;
    }

    public int m563c() {
        return this.f563c;
    }

    public int m564d() {
        return this.f564d;
    }

    public Integer m565e() {
        return this.f565e;
    }

    public Long m566f() {
        return this.f566f;
    }

    public TL_dialog m567g() {
        return this.f567g;
    }
}

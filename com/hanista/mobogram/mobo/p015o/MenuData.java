package com.hanista.mobogram.mobo.p015o;

import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.o.b */
public class MenuData implements Comparable<MenuData> {
    private int f1988a;
    private int f1989b;
    private boolean f1990c;
    private String f1991d;
    private int f1992e;
    private int f1993f;

    public MenuData(int i, int i2, boolean z, String str, int i3, int i4) {
        this.f1988a = i;
        this.f1989b = i2;
        this.f1990c = z;
        this.f1991d = str;
        this.f1992e = i3;
        this.f1993f = i4;
    }

    public int m1970a(MenuData menuData) {
        return this.f1992e < menuData.f1992e ? -1 : this.f1992e > menuData.f1992e ? 1 : 0;
    }

    public void m1971a(int i) {
        this.f1992e = i;
    }

    public boolean m1972a() {
        return this.f1993f == 3 || this.f1993f == 0;
    }

    public int m1973b() {
        return this.f1988a;
    }

    public int m1974c() {
        return this.f1989b;
    }

    public /* synthetic */ int compareTo(Object obj) {
        return m1970a((MenuData) obj);
    }

    public boolean m1975d() {
        return this.f1990c;
    }

    public String m1976e() {
        return this.f1993f == 2 ? "------------" : this.f1993f == 1 ? LocaleController.getString("EmptySpace", C0338R.string.EmptySpace) : this.f1993f == 0 ? LocaleController.getString("Profile", C0338R.string.Profile) : this.f1991d;
    }

    public int m1977f() {
        return this.f1992e;
    }

    public int m1978g() {
        return this.f1993f;
    }
}

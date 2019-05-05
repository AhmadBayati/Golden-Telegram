package com.hanista.mobogram.mobo.p020s;

import android.content.SharedPreferences;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;

/* renamed from: com.hanista.mobogram.mobo.s.o */
public class ThemeBase implements Theme {
    private Theme f2516a;
    private int f2517b;
    private int f2518c;
    private boolean f2519d;

    public ThemeBase(Theme theme) {
        this.f2516a = theme;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
        this.f2517b = sharedPreferences.getInt("main_theme_color", theme.m2289c());
        this.f2518c = sharedPreferences.getInt("main_theme_color", theme.m2292f());
        this.f2519d = sharedPreferences.getBoolean("theme_back_white", true);
    }

    public int m2469a() {
        return this.f2516a.m2287a();
    }

    public String m2470b() {
        return this.f2516a.m2288b();
    }

    public int m2471c() {
        return this.f2517b;
    }

    public int m2472d() {
        return this.f2516a.m2290d();
    }

    public int m2473e() {
        return this.f2516a.m2291e();
    }

    public int m2474f() {
        return this.f2518c;
    }

    public int m2475g() {
        return this.f2519d ? C0338R.drawable.list_selector : this.f2516a.m2293g();
    }

    public int m2476h() {
        return this.f2516a.m2294h();
    }

    public int m2477i() {
        return this.f2516a.m2295i();
    }

    public int m2478j() {
        return this.f2516a.m2296j();
    }

    public int m2479k() {
        return this.f2516a.m2297k();
    }

    public int m2480l() {
        return this.f2516a.m2298l();
    }

    public int m2481m() {
        return this.f2516a.m2299m();
    }

    public int m2482n() {
        return this.f2516a.m2300n();
    }
}

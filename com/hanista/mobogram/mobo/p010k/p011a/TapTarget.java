package com.hanista.mobogram.mobo.p010k.p011a;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.view.View;

/* renamed from: com.hanista.mobogram.mobo.k.a.b */
public class TapTarget {
    final CharSequence f1207a;
    @Nullable
    final CharSequence f1208b;
    Rect f1209c;
    Drawable f1210d;
    Typeface f1211e;
    @ColorRes
    int f1212f;
    @ColorRes
    int f1213g;
    @ColorRes
    int f1214h;
    @ColorRes
    int f1215i;
    @ColorRes
    int f1216j;
    int f1217k;
    int f1218l;
    int f1219m;
    boolean f1220n;
    boolean f1221o;
    boolean f1222p;
    boolean f1223q;

    protected TapTarget(Rect rect, CharSequence charSequence, @Nullable CharSequence charSequence2) {
        this(charSequence, charSequence2);
        if (rect == null) {
            throw new IllegalArgumentException("Cannot pass null bounds or title");
        }
        this.f1209c = rect;
    }

    protected TapTarget(CharSequence charSequence, @Nullable CharSequence charSequence2) {
        this.f1212f = -1;
        this.f1213g = -1;
        this.f1214h = -1;
        this.f1215i = -1;
        this.f1216j = -1;
        this.f1217k = 20;
        this.f1218l = 18;
        this.f1219m = -1;
        this.f1220n = false;
        this.f1221o = true;
        this.f1222p = true;
        this.f1223q = false;
        if (charSequence == null) {
            throw new IllegalArgumentException("Cannot pass null title");
        }
        this.f1207a = charSequence;
        this.f1208b = charSequence2;
    }

    public static TapTarget m1295a(Rect rect, CharSequence charSequence, @Nullable CharSequence charSequence2) {
        return new TapTarget(rect, charSequence, charSequence2);
    }

    public static ViewTapTarget m1296a(View view, CharSequence charSequence, @Nullable CharSequence charSequence2) {
        return new ViewTapTarget(view, charSequence, charSequence2);
    }

    public Rect m1297a() {
        if (this.f1209c != null) {
            return this.f1209c;
        }
        throw new IllegalStateException("Requesting bounds that are not set! Make sure your target is ready");
    }

    public TapTarget m1298a(@ColorRes int i) {
        this.f1212f = i;
        return this;
    }

    public TapTarget m1299a(Typeface typeface) {
        if (typeface == null) {
            throw new IllegalArgumentException("Cannot use a null typeface");
        }
        this.f1211e = typeface;
        return this;
    }

    public TapTarget m1300a(boolean z) {
        this.f1223q = z;
        return this;
    }

    public void m1301a(Runnable runnable) {
        runnable.run();
    }

    public TapTarget m1302b(@ColorRes int i) {
        this.f1215i = i;
        this.f1216j = i;
        return this;
    }

    public TapTarget m1303b(boolean z) {
        this.f1220n = z;
        return this;
    }

    public TapTarget m1304c(@ColorRes int i) {
        this.f1215i = i;
        return this;
    }

    public TapTarget m1305c(boolean z) {
        this.f1221o = z;
        return this;
    }

    public TapTarget m1306d(@ColorRes int i) {
        this.f1216j = i;
        return this;
    }

    public TapTarget m1307e(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Given negative text size");
        }
        this.f1217k = i;
        return this;
    }

    public TapTarget m1308f(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Given negative text size");
        }
        this.f1218l = i;
        return this;
    }

    public TapTarget m1309g(@ColorRes int i) {
        this.f1214h = i;
        return this;
    }
}

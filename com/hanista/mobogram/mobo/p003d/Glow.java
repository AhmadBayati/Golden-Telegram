package com.hanista.mobogram.mobo.p003d;

import android.annotation.TargetApi;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.widget.NestedScrollView;
import android.widget.AbsListView;
import android.widget.EdgeEffect;
import android.widget.ScrollView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import java.lang.reflect.Field;

@TargetApi(21)
/* renamed from: com.hanista.mobogram.mobo.d.a */
public class Glow {
    private static final Class<ScrollView> f482a;
    private static final Field f483b;
    private static final Field f484c;
    private static final Class<NestedScrollView> f485d;
    private static final Field f486e;
    private static final Field f487f;
    private static final Class<RecyclerView> f488g;
    private static final Field f489h;
    private static final Field f490i;
    private static final Field f491j;
    private static final Field f492k;
    private static final Class<AbsListView> f493l;
    private static final Field f494m;
    private static final Field f495n;
    private static final Field f496o;
    private static final Field f497p;
    private static final Field f498q;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static {
        /*
        r7 = -1;
        r5 = 0;
        r6 = 0;
        r8 = 1;
        r0 = android.widget.ScrollView.class;
        f482a = r0;
        r0 = android.support.v4.widget.NestedScrollView.class;
        f485d = r0;
        r0 = com.hanista.mobogram.messenger.support.widget.RecyclerView.class;
        f488g = r0;
        r0 = android.widget.AbsListView.class;
        f493l = r0;
        r0 = f488g;
        r11 = r0.getDeclaredFields();
        r12 = r11.length;
        r10 = r6;
        r0 = r5;
        r2 = r5;
        r3 = r5;
        r4 = r5;
    L_0x0020:
        if (r10 >= r12) goto L_0x0087;
    L_0x0022:
        r1 = r11[r10];
        r9 = r1.getName();
        r13 = r9.hashCode();
        switch(r13) {
            case -1956325951: goto L_0x0053;
            case 489326556: goto L_0x005e;
            case 1512155989: goto L_0x003d;
            case 2011328165: goto L_0x0048;
            default: goto L_0x002f;
        };
    L_0x002f:
        r9 = r7;
    L_0x0030:
        switch(r9) {
            case 0: goto L_0x0069;
            case 1: goto L_0x0071;
            case 2: goto L_0x0079;
            case 3: goto L_0x007f;
            default: goto L_0x0033;
        };
    L_0x0033:
        r1 = r2;
        r2 = r3;
        r3 = r4;
    L_0x0036:
        r4 = r10 + 1;
        r10 = r4;
        r4 = r3;
        r3 = r2;
        r2 = r1;
        goto L_0x0020;
    L_0x003d:
        r13 = "mTopGlow";
        r9 = r9.equals(r13);
        if (r9 == 0) goto L_0x002f;
    L_0x0046:
        r9 = r6;
        goto L_0x0030;
    L_0x0048:
        r13 = "mBottomGlow";
        r9 = r9.equals(r13);
        if (r9 == 0) goto L_0x002f;
    L_0x0051:
        r9 = r8;
        goto L_0x0030;
    L_0x0053:
        r13 = "mLeftGlow";
        r9 = r9.equals(r13);
        if (r9 == 0) goto L_0x002f;
    L_0x005c:
        r9 = 2;
        goto L_0x0030;
    L_0x005e:
        r13 = "mRightGlow";
        r9 = r9.equals(r13);
        if (r9 == 0) goto L_0x002f;
    L_0x0067:
        r9 = 3;
        goto L_0x0030;
    L_0x0069:
        r1.setAccessible(r8);
        r14 = r2;
        r2 = r3;
        r3 = r1;
        r1 = r14;
        goto L_0x0036;
    L_0x0071:
        r1.setAccessible(r8);
        r3 = r4;
        r14 = r1;
        r1 = r2;
        r2 = r14;
        goto L_0x0036;
    L_0x0079:
        r1.setAccessible(r8);
        r2 = r3;
        r3 = r4;
        goto L_0x0036;
    L_0x007f:
        r1.setAccessible(r8);
        r0 = r1;
        r1 = r2;
        r2 = r3;
        r3 = r4;
        goto L_0x0036;
    L_0x0087:
        f489h = r4;
        f492k = r3;
        f490i = r2;
        f491j = r0;
        r0 = f485d;
        r9 = r0.getDeclaredFields();
        r10 = r9.length;
        r0 = r3;
        r2 = r4;
        r4 = r6;
    L_0x0099:
        if (r4 >= r10) goto L_0x00d2;
    L_0x009b:
        r1 = r9[r4];
        r3 = r1.getName();
        r11 = r3.hashCode();
        switch(r11) {
            case -1964119678: goto L_0x00bd;
            case -489649826: goto L_0x00b2;
            default: goto L_0x00a8;
        };
    L_0x00a8:
        r3 = r7;
    L_0x00a9:
        switch(r3) {
            case 0: goto L_0x00c8;
            case 1: goto L_0x00cc;
            default: goto L_0x00ac;
        };
    L_0x00ac:
        r1 = r2;
    L_0x00ad:
        r2 = r4 + 1;
        r4 = r2;
        r2 = r1;
        goto L_0x0099;
    L_0x00b2:
        r11 = "mEdgeGlowTop";
        r3 = r3.equals(r11);
        if (r3 == 0) goto L_0x00a8;
    L_0x00bb:
        r3 = r6;
        goto L_0x00a9;
    L_0x00bd:
        r11 = "mEdgeGlowBottom";
        r3 = r3.equals(r11);
        if (r3 == 0) goto L_0x00a8;
    L_0x00c6:
        r3 = r8;
        goto L_0x00a9;
    L_0x00c8:
        r1.setAccessible(r8);
        goto L_0x00ad;
    L_0x00cc:
        r1.setAccessible(r8);
        r0 = r1;
        r1 = r2;
        goto L_0x00ad;
    L_0x00d2:
        f486e = r2;
        f487f = r0;
        r1 = f482a;
        r9 = r1.getDeclaredFields();
        r10 = r9.length;
        r4 = r6;
    L_0x00de:
        if (r4 >= r10) goto L_0x0117;
    L_0x00e0:
        r1 = r9[r4];
        r3 = r1.getName();
        r11 = r3.hashCode();
        switch(r11) {
            case -1964119678: goto L_0x0102;
            case -489649826: goto L_0x00f7;
            default: goto L_0x00ed;
        };
    L_0x00ed:
        r3 = r7;
    L_0x00ee:
        switch(r3) {
            case 0: goto L_0x010d;
            case 1: goto L_0x0111;
            default: goto L_0x00f1;
        };
    L_0x00f1:
        r1 = r2;
    L_0x00f2:
        r2 = r4 + 1;
        r4 = r2;
        r2 = r1;
        goto L_0x00de;
    L_0x00f7:
        r11 = "mEdgeGlowTop";
        r3 = r3.equals(r11);
        if (r3 == 0) goto L_0x00ed;
    L_0x0100:
        r3 = r6;
        goto L_0x00ee;
    L_0x0102:
        r11 = "mEdgeGlowBottom";
        r3 = r3.equals(r11);
        if (r3 == 0) goto L_0x00ed;
    L_0x010b:
        r3 = r8;
        goto L_0x00ee;
    L_0x010d:
        r1.setAccessible(r8);
        goto L_0x00f2;
    L_0x0111:
        r1.setAccessible(r8);
        r0 = r1;
        r1 = r2;
        goto L_0x00f2;
    L_0x0117:
        f483b = r2;
        f484c = r0;
        r1 = f493l;
        r9 = r1.getDeclaredFields();
        r10 = r9.length;
        r4 = r6;
    L_0x0123:
        if (r4 >= r10) goto L_0x015c;
    L_0x0125:
        r1 = r9[r4];
        r3 = r1.getName();
        r11 = r3.hashCode();
        switch(r11) {
            case -1964119678: goto L_0x0147;
            case -489649826: goto L_0x013c;
            default: goto L_0x0132;
        };
    L_0x0132:
        r3 = r7;
    L_0x0133:
        switch(r3) {
            case 0: goto L_0x0152;
            case 1: goto L_0x0156;
            default: goto L_0x0136;
        };
    L_0x0136:
        r1 = r2;
    L_0x0137:
        r2 = r4 + 1;
        r4 = r2;
        r2 = r1;
        goto L_0x0123;
    L_0x013c:
        r11 = "mEdgeGlowTop";
        r3 = r3.equals(r11);
        if (r3 == 0) goto L_0x0132;
    L_0x0145:
        r3 = r6;
        goto L_0x0133;
    L_0x0147:
        r11 = "mEdgeGlowBottom";
        r3 = r3.equals(r11);
        if (r3 == 0) goto L_0x0132;
    L_0x0150:
        r3 = r8;
        goto L_0x0133;
    L_0x0152:
        r1.setAccessible(r8);
        goto L_0x0137;
    L_0x0156:
        r1.setAccessible(r8);
        r0 = r1;
        r1 = r2;
        goto L_0x0137;
    L_0x015c:
        f494m = r2;
        f495n = r0;
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 21;
        if (r0 >= r1) goto L_0x01ce;
    L_0x0166:
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 14;
        if (r0 >= r1) goto L_0x0199;
    L_0x016c:
        r0 = "android.widget.EdgeGlow";
        r0 = java.lang.Class.forName(r0);	 Catch:{ ClassNotFoundException -> 0x0196 }
    L_0x0173:
        if (r0 == 0) goto L_0x01bc;
    L_0x0175:
        r9 = r0.getDeclaredFields();
        r10 = r9.length;
        r4 = r6;
        r0 = r5;
        r2 = r5;
    L_0x017d:
        if (r4 >= r10) goto L_0x01be;
    L_0x017f:
        r1 = r9[r4];
        r3 = r1.getName();
        r11 = r3.hashCode();
        switch(r11) {
            case 102818762: goto L_0x019c;
            case 102886298: goto L_0x01a7;
            default: goto L_0x018c;
        };
    L_0x018c:
        r3 = r7;
    L_0x018d:
        switch(r3) {
            case 0: goto L_0x01b2;
            case 1: goto L_0x01b6;
            default: goto L_0x0190;
        };
    L_0x0190:
        r1 = r2;
    L_0x0191:
        r2 = r4 + 1;
        r4 = r2;
        r2 = r1;
        goto L_0x017d;
    L_0x0196:
        r0 = move-exception;
        r0 = r5;
        goto L_0x0173;
    L_0x0199:
        r0 = android.widget.EdgeEffect.class;
        goto L_0x0173;
    L_0x019c:
        r11 = "mEdge";
        r3 = r3.equals(r11);
        if (r3 == 0) goto L_0x018c;
    L_0x01a5:
        r3 = r6;
        goto L_0x018d;
    L_0x01a7:
        r11 = "mGlow";
        r3 = r3.equals(r11);
        if (r3 == 0) goto L_0x018c;
    L_0x01b0:
        r3 = r8;
        goto L_0x018d;
    L_0x01b2:
        r1.setAccessible(r8);
        goto L_0x0191;
    L_0x01b6:
        r1.setAccessible(r8);
        r0 = r1;
        r1 = r2;
        goto L_0x0191;
    L_0x01bc:
        r0 = r5;
        r2 = r5;
    L_0x01be:
        f496o = r2;
        f497p = r0;
    L_0x01c2:
        r0 = android.support.v4.widget.EdgeEffectCompat.class;
        r1 = "mEdgeEffect";
        r5 = r0.getDeclaredField(r1);	 Catch:{ NoSuchFieldException -> 0x01d3 }
    L_0x01cb:
        f498q = r5;
        return;
    L_0x01ce:
        f496o = r5;
        f497p = r5;
        goto L_0x01c2;
    L_0x01d3:
        r0 = move-exception;
        goto L_0x01cb;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.mobo.d.a.<clinit>():void");
    }

    public static void m521a(AbsListView absListView, @ColorInt int i) {
        try {
            Glow.m523a(f494m.get(absListView), i);
            Glow.m523a(f495n.get(absListView), i);
        } catch (Exception e) {
        }
    }

    public static void m522a(RecyclerView recyclerView, int i) {
    }

    @TargetApi(21)
    private static void m523a(Object obj, @ColorInt int i) {
        if (obj instanceof EdgeEffectCompat) {
            try {
                Object obj2 = f498q.get(obj);
            } catch (IllegalAccessException e) {
                return;
            }
        }
        obj2 = obj;
        if (obj2 != null) {
            if (VERSION.SDK_INT < 21) {
                try {
                    Drawable drawable = (Drawable) f496o.get(obj2);
                    Drawable drawable2 = (Drawable) f497p.get(obj2);
                    drawable.setColorFilter(i, Mode.SRC_IN);
                    drawable2.setColorFilter(i, Mode.SRC_IN);
                    drawable.setCallback(null);
                    drawable2.setCallback(null);
                    return;
                } catch (Exception e2) {
                    return;
                }
            }
            ((EdgeEffect) obj2).setColor(i);
        }
    }
}

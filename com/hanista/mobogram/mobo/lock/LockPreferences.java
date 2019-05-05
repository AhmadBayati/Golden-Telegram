package com.hanista.mobogram.mobo.lock;

import android.content.Context;
import com.hanista.mobogram.C0338R;
import java.io.Serializable;

/* renamed from: com.hanista.mobogram.mobo.lock.b */
public class LockPreferences implements Serializable {
    public int f1541a;
    public final String f1542b;
    public final Boolean f1543c;
    public final String f1544d;
    public int f1545e;
    public final String f1546f;
    public final int f1547g;
    public final int f1548h;
    public final int f1549i;
    public final int f1550j;
    public final String f1551k;
    public final boolean f1552l;
    public final String f1553m;
    public boolean f1554n;
    public final boolean f1555o;
    public final int f1556p;
    public boolean f1557q;
    private boolean f1558r;

    public LockPreferences(Context context) {
        this.f1558r = true;
        this.f1557q = true;
        PrefUtils prefUtils = new PrefUtils(context);
        this.f1541a = prefUtils.m1586d();
        this.f1542b = prefUtils.m1580a((int) C0338R.string.pref_key_orientation);
        this.f1543c = Boolean.valueOf(prefUtils.m1587d(C0338R.string.pref_key_vibrate, C0338R.bool.pref_def_vibrate));
        this.f1544d = prefUtils.m1580a((int) C0338R.string.pref_key_lock_message);
        if (this.f1558r) {
            this.f1546f = prefUtils.m1582b(C0338R.string.pref_key_background, C0338R.string.pref_def_background);
            this.f1547g = LockPreferences.m1547a(context, prefUtils.m1582b(C0338R.string.pref_key_anim_show_type, C0338R.string.pref_def_anim_show_type), true);
            this.f1549i = prefUtils.m1584c(C0338R.string.pref_key_anim_show_millis, C0338R.string.pref_def_anim_show_millis).intValue();
            this.f1548h = LockPreferences.m1547a(context, prefUtils.m1582b(C0338R.string.pref_key_anim_hide_type, C0338R.string.pref_def_anim_hide_type), false);
            this.f1550j = prefUtils.m1584c(C0338R.string.pref_key_anim_hide_millis, C0338R.string.pref_def_anim_hide_millis).intValue();
        } else {
            this.f1546f = context.getString(C0338R.string.pref_def_background);
            this.f1547g = LockPreferences.m1547a(context, context.getString(C0338R.string.pref_def_anim_show_type), true);
            this.f1549i = Integer.parseInt(context.getString(C0338R.string.pref_def_anim_show_millis));
            this.f1548h = LockPreferences.m1547a(context, context.getString(C0338R.string.pref_def_anim_hide_type), false);
            this.f1550j = Integer.parseInt(context.getString(C0338R.string.pref_def_anim_hide_millis));
        }
        this.f1551k = prefUtils.m1580a((int) C0338R.string.pref_key_password);
        this.f1552l = prefUtils.m1587d(C0338R.string.pref_key_switch_buttons, C0338R.bool.pref_def_switch_buttons);
        this.f1553m = prefUtils.m1580a((int) C0338R.string.pref_key_pattern);
        this.f1554n = prefUtils.m1587d(C0338R.string.pref_key_pattern_stealth, C0338R.bool.pref_def_pattern_stealth);
        this.f1555o = prefUtils.m1587d(C0338R.string.pref_key_pattern_hide_error, C0338R.bool.pref_def_pattern_error_stealth);
        this.f1545e = prefUtils.m1584c(C0338R.string.pref_key_pattern_size, C0338R.string.pref_def_pattern_size).intValue();
        this.f1556p = LockPreferences.m1548a(context, this.f1558r, prefUtils.m1580a((int) C0338R.string.pref_key_pattern_color));
    }

    private static int m1547a(Context context, String str, boolean z) {
        return 0;
    }

    private static int m1548a(Context context, boolean z, String str) {
        if (str != null && z) {
            if (str.equals(context.getString(C0338R.string.pref_val_pattern_color_blue))) {
                return C0338R.drawable.pattern_circle_blue;
            }
            if (str.equals(context.getString(C0338R.string.pref_val_pattern_color_green))) {
                return C0338R.drawable.pattern_circle_green;
            }
        }
        return C0338R.drawable.pattern_circle_white;
    }
}

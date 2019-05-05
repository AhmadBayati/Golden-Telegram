package com.hanista.mobogram.mobo.lock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.util.Log;
import com.hanista.mobogram.C0338R;

/* renamed from: com.hanista.mobogram.mobo.lock.e */
public class PrefUtils {
    private final Context f1603a;
    private final SharedPreferences f1604b;
    private Editor f1605c;

    public PrefUtils(Context context) {
        this.f1603a = context;
        this.f1604b = this.f1603a.getSharedPreferences("com.hanista.mobogram.locker.prefs.default", 0);
    }

    @SuppressLint({"NewApi"})
    public static void m1575a(Editor editor) {
        if (VERSION.SDK_INT < 9) {
            editor.commit();
        } else {
            editor.apply();
        }
    }

    private Boolean m1576c(int i) {
        String string = this.f1603a.getString(i);
        return this.f1604b.contains(string) ? Boolean.valueOf(this.f1604b.getBoolean(string, false)) : null;
    }

    Editor m1577a() {
        if (this.f1605c == null) {
            this.f1605c = this.f1604b.edit();
        }
        return this.f1605c;
    }

    public Editor m1578a(int i, int i2) {
        Editor a = m1577a();
        a.putString(this.f1603a.getString(i), this.f1603a.getString(i2));
        return a;
    }

    public Editor m1579a(int i, Object obj) {
        String string = this.f1603a.getString(i);
        if (string == null) {
            throw new IllegalArgumentException("No resource matched key resource id");
        }
        Log.d(TtmlNode.ANONYMOUS_REGION_ID, "putting (key=" + string + ",value=" + obj + ")");
        Editor a = m1577a();
        if (obj instanceof String) {
            a.putString(string, (String) obj);
        } else if (obj instanceof Integer) {
            a.putInt(string, ((Integer) obj).intValue());
        } else if (obj instanceof Boolean) {
            a.putBoolean(string, ((Boolean) obj).booleanValue());
        } else if (obj instanceof Float) {
            a.putFloat(string, ((Float) obj).floatValue());
        } else if (obj instanceof Long) {
            a.putLong(string, ((Long) obj).longValue());
        } else {
            throw new IllegalArgumentException("Unknown data type");
        }
        return a;
    }

    public String m1580a(int i) {
        return this.f1604b.getString(this.f1603a.getString(i), null);
    }

    Integer m1581b(int i) {
        try {
            return Integer.valueOf(Integer.parseInt(m1580a(i)));
        } catch (Exception e) {
            return null;
        }
    }

    public String m1582b(int i, int i2) {
        String string = this.f1603a.getString(i);
        return this.f1604b.contains(string) ? this.f1604b.getString(string, null) : this.f1603a.getString(i2);
    }

    public void m1583b() {
        PrefUtils.m1575a(m1577a());
        this.f1605c = null;
    }

    public Integer m1584c(int i, int i2) {
        Integer b = m1581b(i);
        return Integer.valueOf(b != null ? b.intValue() : Integer.parseInt(this.f1603a.getString(i2)));
    }

    String m1585c() {
        return m1582b(C0338R.string.pref_key_lock_type, C0338R.string.pref_def_lock_type);
    }

    public int m1586d() {
        return m1585c().equals(this.f1603a.getString(C0338R.string.pref_val_lock_type_pattern)) ? 2 : 0;
    }

    public boolean m1587d(int i, int i2) {
        Boolean c = m1576c(i);
        return c != null ? c.booleanValue() : this.f1603a.getResources().getBoolean(i2);
    }
}

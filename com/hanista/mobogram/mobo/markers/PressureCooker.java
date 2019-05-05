package com.hanista.mobogram.mobo.markers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.PointerIconCompat;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

/* renamed from: com.hanista.mobogram.mobo.markers.d */
class PressureCooker {
    static final Paint f1790a;
    private float f1791b;
    private float f1792c;
    private float f1793d;
    private int f1794e;
    private int f1795f;
    private float f1796g;
    private float f1797h;
    private Context f1798i;

    static {
        f1790a = new Paint(1);
        f1790a.setColor(SupportMenu.CATEGORY_MASK);
    }

    public PressureCooker(Context context) {
        this.f1792c = 0.0f;
        this.f1793d = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.f1794e = PointerIconCompat.TYPE_DEFAULT;
        this.f1795f = this.f1794e;
        this.f1796g = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.f1797h = 0.0f;
        this.f1798i = context;
        m1820a();
    }

    public float m1819a(float f) {
        this.f1791b = f;
        if (f < this.f1796g) {
            this.f1796g = f;
        }
        if (f > this.f1797h) {
            this.f1797h = f;
        }
        int i = this.f1795f - 1;
        this.f1795f = i;
        if (i == 0) {
            this.f1792c = (this.f1792c * 0.9f) + (this.f1796g * 0.1f);
            this.f1793d = (this.f1793d * 0.9f) + (this.f1797h * 0.1f);
            this.f1796g = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.f1797h = 0.0f;
            if (this.f1794e < PointerIconCompat.TYPE_DEFAULT) {
                this.f1794e = (int) (((float) this.f1794e) * 1.5f);
                if (this.f1794e > PointerIconCompat.TYPE_DEFAULT) {
                    this.f1794e = PointerIconCompat.TYPE_DEFAULT;
                }
            }
            this.f1795f = this.f1794e;
            m1823b();
        }
        return (f - this.f1792c) / (this.f1793d - this.f1792c);
    }

    public void m1820a() {
        SharedPreferences sharedPreferences = this.f1798i.getSharedPreferences("Markers", 0);
        this.f1792c = sharedPreferences.getFloat("pressure_min", DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD);
        this.f1793d = sharedPreferences.getFloat("pressure_max", 0.9f);
        m1822a(sharedPreferences.getBoolean("first_run", true));
    }

    public void m1821a(Canvas canvas) {
        canvas.drawText(String.format("[pressurecooker] pressure: %.2f (range: %.2f-%.2f) (recent: %.2f-%.2f) recal: %d", new Object[]{Float.valueOf(this.f1791b), Float.valueOf(this.f1792c), Float.valueOf(this.f1793d), Float.valueOf(this.f1796g), Float.valueOf(this.f1797h), Integer.valueOf(this.f1795f)}), 96.0f, (float) (canvas.getHeight() - 64), f1790a);
    }

    public void m1822a(boolean z) {
        if (z) {
            this.f1794e = 100;
            this.f1795f = 100;
        }
    }

    public void m1823b() {
        Editor edit = this.f1798i.getSharedPreferences("Markers", 0).edit();
        edit.putBoolean("first_run", false);
        edit.putFloat("pressure_min", this.f1792c);
        edit.putFloat("pressure_max", this.f1793d);
        edit.commit();
    }
}

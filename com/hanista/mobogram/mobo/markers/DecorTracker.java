package com.hanista.mobogram.mobo.markers;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.ArrayList;

/* renamed from: com.hanista.mobogram.mobo.markers.c */
public class DecorTracker {
    static DecorTracker f1786a;
    private Rect f1787b;
    private Rect f1788c;
    private ArrayList<View> f1789d;

    static {
        f1786a = new DecorTracker();
    }

    private DecorTracker() {
        this.f1787b = new Rect();
        this.f1788c = new Rect();
        this.f1789d = new ArrayList();
    }

    public static DecorTracker m1816a() {
        return f1786a;
    }

    public void m1817a(Rect rect) {
        this.f1787b.set(rect);
        int size = this.f1789d.size();
        for (int i = 0; i < size; i++) {
            View view = (View) this.f1789d.get(i);
            if (view.getLayoutParams() instanceof MarginLayoutParams) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
                marginLayoutParams.topMargin += this.f1787b.top - this.f1788c.top;
                marginLayoutParams.leftMargin += this.f1787b.left - this.f1788c.left;
                marginLayoutParams.rightMargin += this.f1787b.right - this.f1788c.right;
                marginLayoutParams.bottomMargin += this.f1787b.bottom - this.f1788c.bottom;
                view.setLayoutParams(marginLayoutParams);
            }
        }
        this.f1788c.set(rect);
    }

    public void m1818a(View view) {
        this.f1789d.add(view);
    }
}

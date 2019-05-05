package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityEvent;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.TypefaceHelper;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.Utils;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.MonthAdapter.MonthAdapter;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.LanguageUtils;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.PersianCalendar;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.e */
public abstract class MonthView extends View {
    protected static int f2196a;
    protected static int f2197b;
    protected static int f2198c;
    protected static int f2199d;
    protected static int f2200e;
    protected static int f2201f;
    protected static int f2202g;
    protected static int f2203h;
    protected static float f2204i;
    protected int f2205A;
    protected int f2206B;
    protected int f2207C;
    protected int f2208D;
    protected final PersianCalendar f2209E;
    protected int f2210F;
    protected MonthView f2211G;
    protected int f2212H;
    protected int f2213I;
    protected int f2214J;
    protected int f2215K;
    protected int f2216L;
    protected int f2217M;
    protected int f2218N;
    private String f2219O;
    private String f2220P;
    private final StringBuilder f2221Q;
    private final PersianCalendar f2222R;
    private final MonthView f2223S;
    private boolean f2224T;
    private int f2225U;
    protected DatePickerController f2226j;
    protected int f2227k;
    protected Paint f2228l;
    protected Paint f2229m;
    protected Paint f2230n;
    protected Paint f2231o;
    protected int f2232p;
    protected int f2233q;
    protected int f2234r;
    protected int f2235s;
    protected int f2236t;
    protected int f2237u;
    protected int f2238v;
    protected boolean f2239w;
    protected int f2240x;
    protected int f2241y;
    protected int f2242z;

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.e.b */
    public interface MonthView {
        void m2125a(MonthView monthView, MonthAdapter monthAdapter);
    }

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.e.a */
    protected class MonthView extends ExploreByTouchHelper {
        final /* synthetic */ MonthView f2193a;
        private final Rect f2194b;
        private final PersianCalendar f2195c;

        public MonthView(MonthView monthView, View view) {
            this.f2193a = monthView;
            super(view);
            this.f2194b = new Rect();
            this.f2195c = new PersianCalendar();
        }

        public void m2132a() {
            int focusedVirtualView = getFocusedVirtualView();
            if (focusedVirtualView != TLRPC.MESSAGE_FLAG_MEGAGROUP) {
                getAccessibilityNodeProvider(this.f2193a).performAction(focusedVirtualView, TLRPC.USER_FLAG_UNUSED, null);
            }
        }

        public void m2133a(int i) {
            getAccessibilityNodeProvider(this.f2193a).performAction(i, 64, null);
        }

        protected void m2134a(int i, Rect rect) {
            int i2 = this.f2193a.f2227k;
            int monthHeaderSize = this.f2193a.getMonthHeaderSize();
            int i3 = this.f2193a.f2238v;
            int i4 = (this.f2193a.f2237u - (this.f2193a.f2227k * 2)) / this.f2193a.f2205A;
            int c = (i - 1) + this.f2193a.m2153c();
            i2 += (c % this.f2193a.f2205A) * i4;
            monthHeaderSize += (c / this.f2193a.f2205A) * i3;
            rect.set(i2, monthHeaderSize, i4 + i2, i3 + monthHeaderSize);
        }

        protected CharSequence m2135b(int i) {
            this.f2195c.m2050a(this.f2193a.f2236t, this.f2193a.f2235s, i);
            CharSequence a = LanguageUtils.m2043a(this.f2195c.m2056g());
            if (i != this.f2193a.f2240x) {
                return a;
            }
            return this.f2193a.getContext().getString(C0338R.string.mdtp_item_is_selected, new Object[]{a});
        }

        protected int getVirtualViewAt(float f, float f2) {
            int a = this.f2193a.m2143a(f, f2);
            return a >= 0 ? a : TLRPC.MESSAGE_FLAG_MEGAGROUP;
        }

        protected void getVisibleVirtualViews(List<Integer> list) {
            for (int i = 1; i <= this.f2193a.f2206B; i++) {
                list.add(Integer.valueOf(i));
            }
        }

        protected boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
            switch (i2) {
                case TLRPC.USER_FLAG_PHONE /*16*/:
                    this.f2193a.m2136a(i);
                    return true;
                default:
                    return false;
            }
        }

        protected void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.setContentDescription(m2135b(i));
        }

        protected void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            m2134a(i, this.f2194b);
            accessibilityNodeInfoCompat.setContentDescription(m2135b(i));
            accessibilityNodeInfoCompat.setBoundsInParent(this.f2194b);
            accessibilityNodeInfoCompat.addAction(16);
            if (i == this.f2193a.f2240x) {
                accessibilityNodeInfoCompat.setSelected(true);
            }
        }
    }

    static {
        f2196a = 32;
        f2197b = 10;
        f2198c = 1;
        f2204i = 0.0f;
    }

    public MonthView(Context context, AttributeSet attributeSet, DatePickerController datePickerController) {
        boolean z = false;
        super(context, attributeSet);
        this.f2227k = 0;
        this.f2232p = -1;
        this.f2233q = -1;
        this.f2234r = -1;
        this.f2238v = f2196a;
        this.f2239w = false;
        this.f2240x = -1;
        this.f2241y = -1;
        this.f2242z = 7;
        this.f2205A = 7;
        this.f2206B = this.f2205A;
        this.f2207C = -1;
        this.f2208D = -1;
        this.f2210F = 6;
        this.f2225U = 0;
        this.f2226j = datePickerController;
        Resources resources = context.getResources();
        this.f2209E = new PersianCalendar();
        this.f2222R = new PersianCalendar();
        this.f2219O = resources.getString(C0338R.string.mdtp_day_of_week_label_typeface);
        this.f2220P = resources.getString(C0338R.string.mdtp_sans_serif);
        if (this.f2226j != null && this.f2226j.m2080b()) {
            z = true;
        }
        if (z) {
            this.f2212H = resources.getColor(C0338R.color.mdtp_date_picker_text_normal_dark_theme);
            this.f2214J = resources.getColor(C0338R.color.mdtp_date_picker_month_day_dark_theme);
            this.f2217M = resources.getColor(C0338R.color.mdtp_date_picker_text_disabled_dark_theme);
            this.f2216L = resources.getColor(C0338R.color.mdtp_date_picker_text_highlighted_dark_theme);
        } else {
            this.f2212H = resources.getColor(C0338R.color.mdtp_date_picker_text_normal);
            this.f2214J = resources.getColor(C0338R.color.mdtp_date_picker_month_day);
            this.f2217M = resources.getColor(C0338R.color.mdtp_date_picker_text_disabled);
            this.f2216L = resources.getColor(C0338R.color.mdtp_date_picker_text_highlighted);
        }
        this.f2213I = resources.getColor(C0338R.color.mdtp_white);
        this.f2215K = resources.getColor(C0338R.color.mdtp_accent_color);
        this.f2218N = resources.getColor(C0338R.color.mdtp_white);
        this.f2221Q = new StringBuilder(50);
        f2199d = resources.getDimensionPixelSize(C0338R.dimen.mdtp_day_number_size);
        f2200e = resources.getDimensionPixelSize(C0338R.dimen.mdtp_month_label_size);
        f2201f = resources.getDimensionPixelSize(C0338R.dimen.mdtp_month_day_label_text_size);
        f2202g = resources.getDimensionPixelOffset(C0338R.dimen.mdtp_month_list_item_header_height);
        f2203h = resources.getDimensionPixelSize(C0338R.dimen.mdtp_day_number_select_circle_radius);
        this.f2238v = (resources.getDimensionPixelOffset(C0338R.dimen.mdtp_date_picker_view_animator_height) - getMonthHeaderSize()) / 6;
        this.f2223S = getMonthViewTouchHelper();
        ViewCompat.setAccessibilityDelegate(this, this.f2223S);
        ViewCompat.setImportantForAccessibility(this, 1);
        this.f2224T = true;
        m2144a();
    }

    private void m2136a(int i) {
        if (!m2147a(this.f2236t, this.f2235s, i)) {
            if (this.f2211G != null) {
                this.f2211G.m2125a(this, new MonthAdapter(this.f2236t, this.f2235s, i));
            }
            this.f2223S.sendEventForVirtualView(i, 1);
        }
    }

    private boolean m2138a(int i, PersianCalendar persianCalendar) {
        return this.f2236t == persianCalendar.m2051b() && this.f2235s == persianCalendar.m2052c() && i == persianCalendar.m2054e();
    }

    private boolean m2139c(int i, int i2, int i3) {
        for (PersianCalendar persianCalendar : this.f2226j.m2082d()) {
            if (i < persianCalendar.m2051b()) {
                return false;
            }
            if (i <= persianCalendar.m2051b()) {
                if (i2 < persianCalendar.m2052c()) {
                    return false;
                }
                if (i2 > persianCalendar.m2052c()) {
                    continue;
                } else if (i3 < persianCalendar.m2054e()) {
                    return false;
                } else {
                    if (i3 <= persianCalendar.m2054e()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean m2140d(int i, int i2, int i3) {
        if (this.f2226j == null) {
            return false;
        }
        PersianCalendar h = this.f2226j.m2086h();
        return h != null ? i < h.m2051b() ? true : i <= h.m2051b() ? i2 < h.m2052c() ? true : i2 <= h.m2052c() && i3 < h.m2054e() : false : false;
    }

    private int m2141e() {
        int c = m2153c();
        return ((c + this.f2206B) % this.f2205A > 0 ? 1 : 0) + ((this.f2206B + c) / this.f2205A);
    }

    private boolean m2142e(int i, int i2, int i3) {
        if (this.f2226j == null) {
            return false;
        }
        PersianCalendar i4 = this.f2226j.m2087i();
        return i4 != null ? i > i4.m2051b() ? true : i >= i4.m2051b() ? i2 > i4.m2052c() ? true : i2 >= i4.m2052c() && i3 > i4.m2052c() : false : false;
    }

    private String getMonthAndYearString() {
        this.f2221Q.setLength(0);
        return LanguageUtils.m2043a(this.f2222R.m2053d() + " " + this.f2222R.m2051b());
    }

    public int m2143a(float f, float f2) {
        int b = m2149b(f, f2);
        return (b < 1 || b > this.f2206B) ? -1 : b;
    }

    protected void m2144a() {
        this.f2229m = new Paint();
        this.f2229m.setFakeBoldText(true);
        this.f2229m.setAntiAlias(true);
        this.f2229m.setTextSize((float) f2200e);
        this.f2229m.setTypeface(Typeface.create(this.f2220P, 1));
        this.f2229m.setColor(this.f2212H);
        this.f2229m.setTextAlign(Align.CENTER);
        this.f2229m.setStyle(Style.FILL);
        this.f2230n = new Paint();
        this.f2230n.setFakeBoldText(true);
        this.f2230n.setAntiAlias(true);
        this.f2230n.setColor(this.f2215K);
        this.f2230n.setTextAlign(Align.CENTER);
        this.f2230n.setStyle(Style.FILL);
        this.f2230n.setAlpha(NalUnitUtil.EXTENDED_SAR);
        this.f2231o = new Paint();
        this.f2231o.setAntiAlias(true);
        this.f2231o.setTextSize((float) f2201f);
        this.f2231o.setColor(this.f2214J);
        this.f2231o.setTypeface(TypefaceHelper.m2069a(getContext(), "Roboto-Medium"));
        this.f2231o.setStyle(Style.FILL);
        this.f2231o.setTextAlign(Align.CENTER);
        this.f2231o.setFakeBoldText(true);
        this.f2228l = new Paint();
        this.f2228l.setAntiAlias(true);
        this.f2228l.setTextSize((float) f2199d);
        this.f2228l.setStyle(Style.FILL);
        this.f2228l.setTextAlign(Align.CENTER);
        this.f2228l.setFakeBoldText(false);
    }

    protected void m2145a(Canvas canvas) {
        canvas.drawText(getMonthAndYearString(), (float) ((this.f2237u + (this.f2227k * 2)) / 2), (float) ((getMonthHeaderSize() - f2201f) / 2), this.f2229m);
    }

    public abstract void m2146a(Canvas canvas, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9);

    protected boolean m2147a(int i, int i2, int i3) {
        return this.f2226j.m2082d() != null ? !m2139c(i, i2, i3) : m2140d(i, i2, i3) || m2142e(i, i2, i3);
    }

    public boolean m2148a(MonthAdapter monthAdapter) {
        if (monthAdapter.f2185a != this.f2236t || monthAdapter.f2186b != this.f2235s || monthAdapter.f2187c > this.f2206B) {
            return false;
        }
        this.f2223S.m2133a(monthAdapter.f2187c);
        return true;
    }

    protected int m2149b(float f, float f2) {
        int i = this.f2227k;
        if (f < ((float) i) || f > ((float) (this.f2237u - this.f2227k))) {
            return -1;
        }
        return ((((int) (((f - ((float) i)) * ((float) this.f2205A)) / ((float) ((this.f2237u - i) - this.f2227k)))) - m2153c()) + 1) + ((((int) (f2 - ((float) getMonthHeaderSize()))) / this.f2238v) * this.f2205A);
    }

    public void m2150b() {
        this.f2210F = 6;
        requestLayout();
    }

    protected void m2151b(Canvas canvas) {
        int monthHeaderSize = getMonthHeaderSize() - (f2201f / 2);
        int i = (this.f2237u - (this.f2227k * 2)) / (this.f2205A * 2);
        for (int i2 = 0; i2 < this.f2205A; i2++) {
            int i3 = (((i2 * 2) + 1) * i) + this.f2227k;
            this.f2209E.set(7, (this.f2242z + i2) % this.f2205A);
            canvas.drawText(this.f2209E.m2055f().substring(0, 1), (float) i3, (float) monthHeaderSize, this.f2231o);
        }
    }

    protected boolean m2152b(int i, int i2, int i3) {
        PersianCalendar[] c = this.f2226j.m2081c();
        if (c == null) {
            return false;
        }
        for (PersianCalendar persianCalendar : c) {
            if (i < persianCalendar.m2051b()) {
                return false;
            }
            if (i <= persianCalendar.m2051b()) {
                if (i2 < persianCalendar.m2052c()) {
                    return false;
                }
                if (i2 > persianCalendar.m2052c()) {
                    continue;
                } else if (i3 < persianCalendar.m2054e()) {
                    return false;
                } else {
                    if (i3 <= persianCalendar.m2054e()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected int m2153c() {
        return (this.f2225U < this.f2242z ? this.f2225U + this.f2205A : this.f2225U) - this.f2242z;
    }

    protected void m2154c(Canvas canvas) {
        int monthHeaderSize = (((this.f2238v + f2199d) / 2) - f2198c) + getMonthHeaderSize();
        float f = ((float) (this.f2237u - (this.f2227k * 2))) / (((float) this.f2205A) * 2.0f);
        int i = 1;
        int c = m2153c();
        while (i <= this.f2206B) {
            int i2 = (int) ((((float) ((c * 2) + 1)) * f) + ((float) this.f2227k));
            int i3 = monthHeaderSize - (((this.f2238v + f2199d) / 2) - f2198c);
            Canvas canvas2 = canvas;
            m2146a(canvas2, this.f2236t, this.f2235s, i, i2, monthHeaderSize, (int) (((float) i2) - f), (int) (((float) i2) + f), i3, i3 + this.f2238v);
            int i4 = c + 1;
            if (i4 == this.f2205A) {
                i4 = 0;
                monthHeaderSize += this.f2238v;
            }
            i++;
            c = i4;
        }
    }

    public void m2155d() {
        this.f2223S.m2132a();
    }

    public boolean dispatchHoverEvent(@NonNull MotionEvent motionEvent) {
        return this.f2223S.dispatchHoverEvent(motionEvent) ? true : super.dispatchHoverEvent(motionEvent);
    }

    public MonthAdapter getAccessibilityFocus() {
        int focusedVirtualView = this.f2223S.getFocusedVirtualView();
        return focusedVirtualView >= 0 ? new MonthAdapter(this.f2236t, this.f2235s, focusedVirtualView) : null;
    }

    public int getMonth() {
        return this.f2235s;
    }

    protected int getMonthHeaderSize() {
        return f2202g;
    }

    protected MonthView getMonthViewTouchHelper() {
        return new MonthView(this, this);
    }

    public int getYear() {
        return this.f2236t;
    }

    protected void onDraw(Canvas canvas) {
        m2145a(canvas);
        m2151b(canvas);
        m2154c(canvas);
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), ((this.f2238v * this.f2210F) + getMonthHeaderSize()) + 5);
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.f2237u = i;
        this.f2223S.invalidateRoot();
    }

    public boolean onTouchEvent(@NonNull MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                int a = m2143a(motionEvent.getX(), motionEvent.getY());
                if (a >= 0) {
                    m2136a(a);
                    break;
                }
                break;
        }
        return true;
    }

    public void setAccessibilityDelegate(AccessibilityDelegate accessibilityDelegate) {
        if (!this.f2224T) {
            super.setAccessibilityDelegate(accessibilityDelegate);
        }
    }

    public void setDatePickerController(DatePickerController datePickerController) {
        this.f2226j = datePickerController;
    }

    public void setMonthParams(HashMap<String, Integer> hashMap) {
        if (hashMap.containsKey("month") || hashMap.containsKey("year")) {
            setTag(hashMap);
            if (hashMap.containsKey("height")) {
                this.f2238v = ((Integer) hashMap.get("height")).intValue();
                if (this.f2238v < f2197b) {
                    this.f2238v = f2197b;
                }
            }
            if (hashMap.containsKey("selected_day")) {
                this.f2240x = ((Integer) hashMap.get("selected_day")).intValue();
            }
            this.f2235s = ((Integer) hashMap.get("month")).intValue();
            this.f2236t = ((Integer) hashMap.get("year")).intValue();
            PersianCalendar persianCalendar = new PersianCalendar();
            this.f2239w = false;
            this.f2241y = -1;
            this.f2222R.m2050a(this.f2236t, this.f2235s, 1);
            this.f2225U = this.f2222R.get(7);
            if (hashMap.containsKey("week_start")) {
                this.f2242z = ((Integer) hashMap.get("week_start")).intValue();
            } else {
                this.f2242z = 7;
            }
            this.f2206B = Utils.m2070a(this.f2235s, this.f2236t);
            for (int i = 0; i < this.f2206B; i++) {
                int i2 = i + 1;
                if (m2138a(i2, persianCalendar)) {
                    this.f2239w = true;
                    this.f2241y = i2;
                }
            }
            this.f2210F = m2141e();
            this.f2223S.invalidateRoot();
            return;
        }
        throw new InvalidParameterException("You must specify month and year for this view");
    }

    public void setOnDayClickListener(MonthView monthView) {
        this.f2211G = monthView;
    }

    public void setSelectedDay(int i) {
        this.f2240x = i;
    }
}

package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.Utils;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.DatePickerDialog.DatePickerDialog;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.MonthAdapter.MonthAdapter;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.LanguageUtils;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.PersianCalendar;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.c */
public abstract class DayPickerView extends ListView implements OnScrollListener, DatePickerDialog {
    public static int f2168a;
    protected int f2169b;
    protected boolean f2170c;
    protected int f2171d;
    protected float f2172e;
    protected Context f2173f;
    protected Handler f2174g;
    protected MonthAdapter f2175h;
    protected MonthAdapter f2176i;
    protected MonthAdapter f2177j;
    protected int f2178k;
    protected long f2179l;
    protected int f2180m;
    protected int f2181n;
    protected DayPickerView f2182o;
    private DatePickerController f2183p;
    private boolean f2184q;

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.c.1 */
    class DayPickerView implements Runnable {
        final /* synthetic */ int f2164a;
        final /* synthetic */ DayPickerView f2165b;

        DayPickerView(DayPickerView dayPickerView, int i) {
            this.f2165b = dayPickerView;
            this.f2164a = i;
        }

        public void run() {
            this.f2165b.setSelection(this.f2164a);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.c.a */
    protected class DayPickerView implements Runnable {
        final /* synthetic */ DayPickerView f2166a;
        private int f2167b;

        protected DayPickerView(DayPickerView dayPickerView) {
            this.f2166a = dayPickerView;
        }

        public void m2111a(AbsListView absListView, int i) {
            this.f2166a.f2174g.removeCallbacks(this);
            this.f2167b = i;
            this.f2166a.f2174g.postDelayed(this, 40);
        }

        public void run() {
            int i = 1;
            this.f2166a.f2181n = this.f2167b;
            if (Log.isLoggable("MonthFragment", 3)) {
                Log.d("MonthFragment", "new scroll state: " + this.f2167b + " old state: " + this.f2166a.f2180m);
            }
            if (this.f2167b != 0 || this.f2166a.f2180m == 0 || this.f2166a.f2180m == 1) {
                this.f2166a.f2180m = this.f2167b;
                return;
            }
            this.f2166a.f2180m = this.f2167b;
            View childAt = this.f2166a.getChildAt(0);
            int i2 = 0;
            while (childAt != null && childAt.getBottom() <= 0) {
                i2++;
                childAt = this.f2166a.getChildAt(i2);
            }
            if (childAt != null) {
                i2 = this.f2166a.getFirstVisiblePosition();
                int lastVisiblePosition = this.f2166a.getLastVisiblePosition();
                if (i2 == 0 || lastVisiblePosition == this.f2166a.getCount() - 1) {
                    i = 0;
                }
                int top = childAt.getTop();
                int bottom = childAt.getBottom();
                i2 = this.f2166a.getHeight() / 2;
                if (i != 0 && top < DayPickerView.f2168a) {
                    if (bottom > i2) {
                        this.f2166a.smoothScrollBy(top, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
                    } else {
                        this.f2166a.smoothScrollBy(bottom, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
                    }
                }
            }
        }
    }

    static {
        f2168a = -1;
    }

    public DayPickerView(Context context, DatePickerController datePickerController) {
        super(context);
        this.f2169b = 6;
        this.f2170c = false;
        this.f2171d = 7;
        this.f2172e = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.f2175h = new MonthAdapter();
        this.f2177j = new MonthAdapter();
        this.f2180m = 0;
        this.f2181n = 0;
        this.f2182o = new DayPickerView(this);
        m2118a(context);
        setController(datePickerController);
    }

    private boolean m2112a(MonthAdapter monthAdapter) {
        if (monthAdapter == null) {
            return false;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if ((childAt instanceof MonthView) && ((MonthView) childAt).m2148a(monthAdapter)) {
                return true;
            }
        }
        return false;
    }

    private static String m2113b(MonthAdapter monthAdapter) {
        PersianCalendar persianCalendar = new PersianCalendar();
        persianCalendar.m2050a(monthAdapter.f2185a, monthAdapter.f2186b, monthAdapter.f2187c);
        return ((TtmlNode.ANONYMOUS_REGION_ID + persianCalendar.m2053d()) + " ") + persianCalendar.m2051b();
    }

    private MonthAdapter m2114d() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof MonthView) {
                MonthAdapter accessibilityFocus = ((MonthView) childAt).getAccessibilityFocus();
                if (accessibilityFocus != null) {
                    if (VERSION.SDK_INT != 17) {
                        return accessibilityFocus;
                    }
                    ((MonthView) childAt).m2155d();
                    return accessibilityFocus;
                }
            }
        }
        return null;
    }

    public abstract MonthAdapter m2115a(Context context, DatePickerController datePickerController);

    public void m2116a() {
        m2119a(this.f2183p.m2076a(), false, true, true);
    }

    public void m2117a(int i) {
        clearFocus();
        post(new DayPickerView(this, i));
        onScrollStateChanged(this, 0);
    }

    public void m2118a(Context context) {
        this.f2174g = new Handler();
        setLayoutParams(new LayoutParams(-1, -1));
        setDrawSelectorOnTop(false);
        this.f2173f = context;
        m2121c();
    }

    public boolean m2119a(MonthAdapter monthAdapter, boolean z, boolean z2, boolean z3) {
        View childAt;
        if (z2) {
            this.f2175h.m2124a(monthAdapter);
        }
        this.f2177j.m2124a(monthAdapter);
        int f = ((monthAdapter.f2185a - this.f2183p.m2084f()) * 12) + monthAdapter.f2186b;
        int i = 0;
        while (true) {
            int i2 = i + 1;
            childAt = getChildAt(i);
            if (childAt != null) {
                int top = childAt.getTop();
                if (Log.isLoggable("MonthFragment", 3)) {
                    Log.d("MonthFragment", "child at " + (i2 - 1) + " has top " + top);
                }
                if (top >= 0) {
                    break;
                }
                i = i2;
            } else {
                break;
            }
        }
        i = childAt != null ? getPositionForView(childAt) : 0;
        if (z2) {
            this.f2176i.m2129a(this.f2175h);
        }
        if (Log.isLoggable("MonthFragment", 3)) {
            Log.d("MonthFragment", "GoTo position " + f);
        }
        if (f != i || z3) {
            setMonthDisplayed(this.f2177j);
            this.f2180m = 2;
            if (z) {
                smoothScrollToPositionFromTop(f, f2168a, Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
                return true;
            }
            m2117a(f);
            return false;
        } else if (!z2) {
            return false;
        } else {
            setMonthDisplayed(this.f2175h);
            return false;
        }
    }

    protected void m2120b() {
        if (this.f2176i == null) {
            this.f2176i = m2115a(getContext(), this.f2183p);
        } else {
            this.f2176i.m2129a(this.f2175h);
        }
        setAdapter(this.f2176i);
    }

    protected void m2121c() {
        setCacheColorHint(0);
        setDivider(null);
        setItemsCanFocus(true);
        setFastScrollEnabled(false);
        setVerticalScrollBarEnabled(false);
        setOnScrollListener(this);
        setFadingEdgeLength(0);
        setFriction(ViewConfiguration.getScrollFriction() * this.f2172e);
    }

    public int getMostVisiblePosition() {
        int firstVisiblePosition = getFirstVisiblePosition();
        int height = getHeight();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i < height) {
            View childAt = getChildAt(i2);
            if (childAt == null) {
                break;
            }
            int bottom = childAt.getBottom();
            i = Math.min(bottom, height) - Math.max(0, childAt.getTop());
            if (i > i4) {
                i3 = i2;
            } else {
                i = i4;
            }
            i2++;
            i4 = i;
            i = bottom;
        }
        return i3 + firstVisiblePosition;
    }

    protected void layoutChildren() {
        MonthAdapter d = m2114d();
        super.layoutChildren();
        if (this.f2184q) {
            this.f2184q = false;
        } else {
            m2112a(d);
        }
    }

    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setItemCount(-1);
    }

    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (VERSION.SDK_INT >= 21) {
            accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_SCROLL_BACKWARD);
            accessibilityNodeInfo.addAction(AccessibilityAction.ACTION_SCROLL_FORWARD);
            return;
        }
        accessibilityNodeInfo.addAction(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
        accessibilityNodeInfo.addAction(MessagesController.UPDATE_MASK_CHANNEL);
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        MonthView monthView = (MonthView) absListView.getChildAt(0);
        if (monthView != null) {
            this.f2179l = (long) ((absListView.getFirstVisiblePosition() * monthView.getHeight()) - monthView.getBottom());
            this.f2180m = this.f2181n;
        }
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
        this.f2182o.m2111a(absListView, i);
    }

    @SuppressLint({"NewApi"})
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (i != ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT && i != MessagesController.UPDATE_MASK_CHANNEL) {
            return super.performAccessibilityAction(i, bundle);
        }
        int firstVisiblePosition = getFirstVisiblePosition();
        MonthAdapter monthAdapter = new MonthAdapter((firstVisiblePosition / 12) + this.f2183p.m2084f(), firstVisiblePosition % 12, 1);
        if (i == ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT) {
            monthAdapter.f2186b++;
            if (monthAdapter.f2186b == 12) {
                monthAdapter.f2186b = 0;
                monthAdapter.f2185a++;
            }
        } else if (i == MessagesController.UPDATE_MASK_CHANNEL) {
            View childAt = getChildAt(0);
            if (childAt != null && childAt.getTop() >= -1) {
                monthAdapter.f2186b--;
                if (monthAdapter.f2186b == -1) {
                    monthAdapter.f2186b = 11;
                    monthAdapter.f2185a--;
                }
            }
        }
        Utils.m2072a((View) this, LanguageUtils.m2043a(DayPickerView.m2113b(monthAdapter)));
        m2119a(monthAdapter, true, false, true);
        this.f2184q = true;
        return true;
    }

    public void setController(DatePickerController datePickerController) {
        this.f2183p = datePickerController;
        this.f2183p.m2079a((DatePickerDialog) this);
        m2120b();
        m2116a();
    }

    protected void setMonthDisplayed(MonthAdapter monthAdapter) {
        this.f2178k = monthAdapter.f2186b;
        invalidateViews();
    }
}

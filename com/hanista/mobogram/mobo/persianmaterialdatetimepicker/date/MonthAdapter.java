package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.MonthView.MonthView;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.PersianCalendar;
import java.util.HashMap;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.d */
public abstract class MonthAdapter extends BaseAdapter implements MonthView {
    protected static int f2189b;
    protected final DatePickerController f2190a;
    private final Context f2191c;
    private MonthAdapter f2192d;

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.d.a */
    public static class MonthAdapter {
        int f2185a;
        int f2186b;
        int f2187c;
        private PersianCalendar f2188d;

        public MonthAdapter() {
            m2122a(System.currentTimeMillis());
        }

        public MonthAdapter(int i, int i2, int i3) {
            m2123a(i, i2, i3);
        }

        public MonthAdapter(long j) {
            m2122a(j);
        }

        public MonthAdapter(PersianCalendar persianCalendar) {
            this.f2185a = persianCalendar.m2051b();
            this.f2186b = persianCalendar.m2052c();
            this.f2187c = persianCalendar.m2054e();
        }

        private void m2122a(long j) {
            if (this.f2188d == null) {
                this.f2188d = new PersianCalendar();
            }
            this.f2188d.setTimeInMillis(j);
            this.f2186b = this.f2188d.m2052c();
            this.f2185a = this.f2188d.m2051b();
            this.f2187c = this.f2188d.m2054e();
        }

        public void m2123a(int i, int i2, int i3) {
            this.f2185a = i;
            this.f2186b = i2;
            this.f2187c = i3;
        }

        public void m2124a(MonthAdapter monthAdapter) {
            this.f2185a = monthAdapter.f2185a;
            this.f2186b = monthAdapter.f2186b;
            this.f2187c = monthAdapter.f2187c;
        }
    }

    static {
        f2189b = 7;
    }

    public MonthAdapter(Context context, DatePickerController datePickerController) {
        this.f2191c = context;
        this.f2190a = datePickerController;
        m2128a();
        m2129a(this.f2190a.m2076a());
    }

    private boolean m2126a(int i, int i2) {
        return this.f2192d.f2185a == i && this.f2192d.f2186b == i2;
    }

    public abstract MonthView m2127a(Context context);

    protected void m2128a() {
        this.f2192d = new MonthAdapter(System.currentTimeMillis());
    }

    public void m2129a(MonthAdapter monthAdapter) {
        this.f2192d = monthAdapter;
        notifyDataSetChanged();
    }

    public void m2130a(MonthView monthView, MonthAdapter monthAdapter) {
        if (monthAdapter != null) {
            m2131b(monthAdapter);
        }
    }

    protected void m2131b(MonthAdapter monthAdapter) {
        this.f2190a.m2088j();
        this.f2190a.m2078a(monthAdapter.f2185a, monthAdapter.f2186b, monthAdapter.f2187c);
        m2129a(monthAdapter);
    }

    public int getCount() {
        return ((this.f2190a.m2085g() - this.f2190a.m2084f()) + 1) * 12;
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    @SuppressLint({"NewApi"})
    public View getView(int i, View view, ViewGroup viewGroup) {
        int i2 = -1;
        HashMap hashMap = null;
        if (view != null) {
            view = (MonthView) view;
            hashMap = (HashMap) view.getTag();
        } else {
            view = m2127a(this.f2191c);
            view.setLayoutParams(new LayoutParams(-1, -1));
            view.setClickable(true);
            view.setOnDayClickListener(this);
        }
        if (hashMap == null) {
            hashMap = new HashMap();
        }
        hashMap.clear();
        int i3 = i % 12;
        int f = (i / 12) + this.f2190a.m2084f();
        if (m2126a(f, i3)) {
            i2 = this.f2192d.f2187c;
        }
        view.m2150b();
        hashMap.put("selected_day", Integer.valueOf(i2));
        hashMap.put("year", Integer.valueOf(f));
        hashMap.put("month", Integer.valueOf(i3));
        hashMap.put("week_start", Integer.valueOf(this.f2190a.m2083e()));
        view.setMonthParams(hashMap);
        view.invalidate();
        return view;
    }

    public boolean hasStableIds() {
        return true;
    }
}

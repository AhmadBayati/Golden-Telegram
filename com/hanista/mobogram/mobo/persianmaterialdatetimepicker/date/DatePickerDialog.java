package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date;

import android.animation.ObjectAnimator;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.HapticFeedbackController;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.TypefaceHelper;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.Utils;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.MonthAdapter.MonthAdapter;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.LanguageUtils;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.PersianCalendar;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.HashSet;
import java.util.Iterator;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.b */
public class DatePickerDialog extends DialogFragment implements OnClickListener, DatePickerController {
    private String f2136A;
    private String f2137B;
    private final PersianCalendar f2138a;
    private DatePickerDialog f2139b;
    private HashSet<DatePickerDialog> f2140c;
    private OnCancelListener f2141d;
    private OnDismissListener f2142e;
    private AccessibleDateAnimator f2143f;
    private TextView f2144g;
    private LinearLayout f2145h;
    private TextView f2146i;
    private TextView f2147j;
    private TextView f2148k;
    private DayPickerView f2149l;
    private YearPickerView f2150m;
    private int f2151n;
    private int f2152o;
    private int f2153p;
    private int f2154q;
    private PersianCalendar f2155r;
    private PersianCalendar f2156s;
    private PersianCalendar[] f2157t;
    private PersianCalendar[] f2158u;
    private boolean f2159v;
    private HapticFeedbackController f2160w;
    private boolean f2161x;
    private String f2162y;
    private String f2163z;

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.b.1 */
    class DatePickerDialog implements OnClickListener {
        final /* synthetic */ DatePickerDialog f2134a;

        DatePickerDialog(DatePickerDialog datePickerDialog) {
            this.f2134a = datePickerDialog;
        }

        public void onClick(View view) {
            this.f2134a.m2110j();
            if (this.f2134a.f2139b != null) {
                this.f2134a.f2139b.onDateSet(this.f2134a, this.f2134a.f2138a.m2051b(), this.f2134a.f2138a.m2052c(), this.f2134a.f2138a.m2054e());
            }
            this.f2134a.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.b.2 */
    class DatePickerDialog implements OnClickListener {
        final /* synthetic */ DatePickerDialog f2135a;

        DatePickerDialog(DatePickerDialog datePickerDialog) {
            this.f2135a = datePickerDialog;
        }

        public void onClick(View view) {
            this.f2135a.m2110j();
            this.f2135a.getDialog().cancel();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.b.a */
    public interface DatePickerDialog {
        void m2089a();
    }

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.b.b */
    public interface DatePickerDialog {
        void onDateSet(DatePickerDialog datePickerDialog, int i, int i2, int i3);
    }

    public DatePickerDialog() {
        this.f2138a = new PersianCalendar();
        this.f2140c = new HashSet();
        this.f2151n = -1;
        this.f2152o = 7;
        this.f2153p = 1350;
        this.f2154q = 1450;
        this.f2161x = true;
    }

    public static DatePickerDialog m2091a(DatePickerDialog datePickerDialog, int i, int i2, int i3) {
        DatePickerDialog datePickerDialog2 = new DatePickerDialog();
        datePickerDialog2.m2101b(datePickerDialog, i, i2, i3);
        return datePickerDialog2;
    }

    private void m2092a(int i, int i2) {
    }

    private void m2093a(boolean z) {
        if (this.f2144g != null) {
            this.f2144g.setText(this.f2138a.m2055f());
        }
        this.f2146i.setText(LanguageUtils.m2043a(this.f2138a.m2053d()));
        this.f2147j.setText(LanguageUtils.m2043a(String.valueOf(this.f2138a.m2054e())));
        this.f2148k.setText(LanguageUtils.m2043a(String.valueOf(this.f2138a.m2051b())));
        this.f2143f.setDateMillis(this.f2138a.getTimeInMillis());
        this.f2145h.setContentDescription(LanguageUtils.m2043a(this.f2138a.m2053d() + " " + this.f2138a.m2054e()));
        if (z) {
            Utils.m2072a(this.f2143f, LanguageUtils.m2043a(this.f2138a.m2056g()));
        }
    }

    private void m2095b(int i) {
        ObjectAnimator a;
        switch (i) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                a = Utils.m2071a(this.f2145h, 0.9f, 1.05f);
                if (this.f2161x) {
                    a.setStartDelay(500);
                    this.f2161x = false;
                }
                this.f2149l.m2116a();
                if (this.f2151n != i) {
                    this.f2145h.setSelected(true);
                    this.f2148k.setSelected(false);
                    this.f2143f.setDisplayedChild(0);
                    this.f2151n = i;
                }
                a.start();
                this.f2143f.setContentDescription(this.f2162y + ": " + LanguageUtils.m2043a(this.f2138a.m2056g()));
                Utils.m2072a(this.f2143f, this.f2163z);
            case VideoPlayer.TYPE_AUDIO /*1*/:
                a = Utils.m2071a(this.f2148k, 0.85f, 1.1f);
                if (this.f2161x) {
                    a.setStartDelay(500);
                    this.f2161x = false;
                }
                this.f2150m.m2164a();
                if (this.f2151n != i) {
                    this.f2145h.setSelected(false);
                    this.f2148k.setSelected(true);
                    this.f2143f.setDisplayedChild(1);
                    this.f2151n = i;
                }
                a.start();
                this.f2143f.setContentDescription(this.f2136A + ": " + LanguageUtils.m2043a(String.valueOf(this.f2138a.m2051b())));
                Utils.m2072a(this.f2143f, this.f2137B);
            default:
        }
    }

    private void m2096k() {
        Iterator it = this.f2140c.iterator();
        while (it.hasNext()) {
            ((DatePickerDialog) it.next()).m2089a();
        }
    }

    public MonthAdapter m2097a() {
        return new MonthAdapter(this.f2138a);
    }

    public void m2098a(int i) {
        m2092a(this.f2138a.m2052c(), i);
        this.f2138a.m2050a(i, this.f2138a.m2052c(), this.f2138a.m2054e());
        m2096k();
        m2095b(0);
        m2093a(true);
    }

    public void m2099a(int i, int i2, int i3) {
        this.f2138a.m2050a(i, i2, i3);
        m2096k();
        m2093a(true);
    }

    public void m2100a(DatePickerDialog datePickerDialog) {
        this.f2140c.add(datePickerDialog);
    }

    public void m2101b(DatePickerDialog datePickerDialog, int i, int i2, int i3) {
        this.f2139b = datePickerDialog;
        this.f2138a.m2050a(i, i2, i3);
        this.f2159v = false;
    }

    public boolean m2102b() {
        return this.f2159v;
    }

    public PersianCalendar[] m2103c() {
        return this.f2157t;
    }

    public PersianCalendar[] m2104d() {
        return this.f2158u;
    }

    public int m2105e() {
        return this.f2152o;
    }

    public int m2106f() {
        return this.f2158u != null ? this.f2158u[0].m2051b() : (this.f2155r == null || this.f2155r.m2051b() <= this.f2153p) ? this.f2153p : this.f2155r.m2051b();
    }

    public int m2107g() {
        return this.f2158u != null ? this.f2158u[this.f2158u.length - 1].m2051b() : (this.f2156s == null || this.f2156s.m2051b() >= this.f2154q) ? this.f2154q : this.f2156s.m2051b();
    }

    public PersianCalendar m2108h() {
        return this.f2155r;
    }

    public PersianCalendar m2109i() {
        return this.f2156s;
    }

    public void m2110j() {
        this.f2160w.m2068c();
    }

    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        if (this.f2141d != null) {
            this.f2141d.onCancel(dialogInterface);
        }
    }

    public void onClick(View view) {
        m2110j();
        if (view.getId() == C0338R.id.date_picker_year) {
            m2095b(1);
        } else if (view.getId() == C0338R.id.date_picker_month_and_day) {
            m2095b(0);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().getWindow().setSoftInputMode(3);
        if (bundle != null) {
            this.f2138a.m2050a(bundle.getInt("year"), bundle.getInt("month"), bundle.getInt("day"));
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        int i;
        int i2;
        int i3;
        Log.d("DatePickerDialog", "onCreateView: ");
        getDialog().getWindow().requestFeature(1);
        View inflate = layoutInflater.inflate(C0338R.layout.mdtp_date_picker_dialog, null);
        this.f2144g = (TextView) inflate.findViewById(C0338R.id.date_picker_header);
        this.f2145h = (LinearLayout) inflate.findViewById(C0338R.id.date_picker_month_and_day);
        this.f2145h.setOnClickListener(this);
        this.f2146i = (TextView) inflate.findViewById(C0338R.id.date_picker_month);
        this.f2147j = (TextView) inflate.findViewById(C0338R.id.date_picker_day);
        this.f2148k = (TextView) inflate.findViewById(C0338R.id.date_picker_year);
        this.f2148k.setOnClickListener(this);
        if (bundle != null) {
            this.f2152o = bundle.getInt("week_start");
            this.f2153p = bundle.getInt("year_start");
            this.f2154q = bundle.getInt("year_end");
            i = bundle.getInt("current_view");
            i2 = bundle.getInt("list_position");
            i3 = bundle.getInt("list_position_offset");
            this.f2155r = (PersianCalendar) bundle.getSerializable("min_date");
            this.f2156s = (PersianCalendar) bundle.getSerializable("max_date");
            this.f2157t = (PersianCalendar[]) bundle.getSerializable("highlighted_days");
            this.f2158u = (PersianCalendar[]) bundle.getSerializable("selectable_days");
            this.f2159v = bundle.getBoolean("theme_dark");
        } else {
            i2 = -1;
            i3 = 0;
            i = 0;
        }
        Context activity = getActivity();
        this.f2149l = new SimpleDayPickerView(activity, this);
        this.f2150m = new YearPickerView(activity, this);
        Resources resources = getResources();
        this.f2162y = resources.getString(C0338R.string.mdtp_day_picker_description);
        this.f2163z = resources.getString(C0338R.string.mdtp_select_day);
        this.f2136A = resources.getString(C0338R.string.mdtp_year_picker_description);
        this.f2137B = resources.getString(C0338R.string.mdtp_select_year);
        inflate.setBackgroundColor(activity.getResources().getColor(this.f2159v ? C0338R.color.mdtp_date_picker_view_animator_dark_theme : C0338R.color.mdtp_date_picker_view_animator));
        this.f2143f = (AccessibleDateAnimator) inflate.findViewById(C0338R.id.animator);
        this.f2143f.addView(this.f2149l);
        this.f2143f.addView(this.f2150m);
        this.f2143f.setDateMillis(this.f2138a.getTimeInMillis());
        Animation alphaAnimation = new AlphaAnimation(0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        alphaAnimation.setDuration(300);
        this.f2143f.setInAnimation(alphaAnimation);
        alphaAnimation = new AlphaAnimation(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f);
        alphaAnimation.setDuration(300);
        this.f2143f.setOutAnimation(alphaAnimation);
        Button button = (Button) inflate.findViewById(C0338R.id.ok);
        button.setOnClickListener(new DatePickerDialog(this));
        button.setTypeface(TypefaceHelper.m2069a(activity, "Roboto-Medium"));
        button = (Button) inflate.findViewById(C0338R.id.cancel);
        button.setOnClickListener(new DatePickerDialog(this));
        button.setTypeface(TypefaceHelper.m2069a(activity, "Roboto-Medium"));
        button.setVisibility(isCancelable() ? 0 : 8);
        m2093a(false);
        m2095b(i);
        if (i2 != -1) {
            if (i == 0) {
                this.f2149l.m2117a(i2);
            } else if (i == 1) {
                this.f2150m.m2166a(i2, i3);
            }
        }
        this.f2160w = new HapticFeedbackController(activity);
        return inflate;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if (this.f2142e != null) {
            this.f2142e.onDismiss(dialogInterface);
        }
    }

    public void onPause() {
        super.onPause();
        this.f2160w.m2067b();
    }

    public void onResume() {
        super.onResume();
        this.f2160w.m2066a();
    }

    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("year", this.f2138a.m2051b());
        bundle.putInt("month", this.f2138a.m2052c());
        bundle.putInt("day", this.f2138a.m2054e());
        bundle.putInt("week_start", this.f2152o);
        bundle.putInt("year_start", this.f2153p);
        bundle.putInt("year_end", this.f2154q);
        bundle.putInt("current_view", this.f2151n);
        int i = -1;
        if (this.f2151n == 0) {
            i = this.f2149l.getMostVisiblePosition();
        } else if (this.f2151n == 1) {
            i = this.f2150m.getFirstVisiblePosition();
            bundle.putInt("list_position_offset", this.f2150m.getFirstPositionOffset());
        }
        bundle.putInt("list_position", i);
        bundle.putSerializable("min_date", this.f2155r);
        bundle.putSerializable("max_date", this.f2156s);
        bundle.putSerializable("highlighted_days", this.f2157t);
        bundle.putSerializable("selectable_days", this.f2158u);
        bundle.putBoolean("theme_dark", this.f2159v);
    }
}

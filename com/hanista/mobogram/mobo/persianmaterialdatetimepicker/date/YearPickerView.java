package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.DatePickerDialog.DatePickerDialog;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.LanguageUtils;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.i */
public class YearPickerView extends ListView implements OnItemClickListener, DatePickerDialog {
    private final DatePickerController f2247a;
    private YearPickerView f2248b;
    private int f2249c;
    private int f2250d;
    private TextViewWithCircularIndicator f2251e;

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.i.1 */
    class YearPickerView implements Runnable {
        final /* synthetic */ int f2243a;
        final /* synthetic */ int f2244b;
        final /* synthetic */ YearPickerView f2245c;

        YearPickerView(YearPickerView yearPickerView, int i, int i2) {
            this.f2245c = yearPickerView;
            this.f2243a = i;
            this.f2244b = i2;
        }

        @TargetApi(21)
        public void run() {
            this.f2245c.setSelectionFromTop(this.f2243a, this.f2244b);
            this.f2245c.requestLayout();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.i.a */
    private class YearPickerView extends ArrayAdapter<String> {
        final /* synthetic */ YearPickerView f2246a;

        public YearPickerView(YearPickerView yearPickerView, Context context, int i, List<String> list) {
            this.f2246a = yearPickerView;
            super(context, i, list);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            TextViewWithCircularIndicator textViewWithCircularIndicator = (TextViewWithCircularIndicator) super.getView(i, view, viewGroup);
            textViewWithCircularIndicator.requestLayout();
            boolean z = this.f2246a.f2247a.m2076a().f2185a == YearPickerView.m2163b(textViewWithCircularIndicator);
            textViewWithCircularIndicator.m2075a(z);
            if (z) {
                this.f2246a.f2251e = textViewWithCircularIndicator;
            }
            return textViewWithCircularIndicator;
        }
    }

    public YearPickerView(Context context, DatePickerController datePickerController) {
        super(context);
        this.f2247a = datePickerController;
        this.f2247a.m2079a((DatePickerDialog) this);
        setLayoutParams(new LayoutParams(-1, -2));
        Resources resources = context.getResources();
        this.f2249c = resources.getDimensionPixelOffset(C0338R.dimen.mdtp_date_picker_view_animator_height);
        this.f2250d = resources.getDimensionPixelOffset(C0338R.dimen.mdtp_year_label_height);
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(this.f2250d / 3);
        m2162a(context);
        setOnItemClickListener(this);
        setSelector(new StateListDrawable());
        setDividerHeight(0);
        m2164a();
    }

    private void m2162a(Context context) {
        ArrayList arrayList = new ArrayList();
        for (int f = this.f2247a.m2084f(); f <= this.f2247a.m2085g(); f++) {
            arrayList.add(String.format("%d", new Object[]{Integer.valueOf(f)}));
        }
        this.f2248b = new YearPickerView(this, context, C0338R.layout.mdtp_year_label_text_view, LanguageUtils.m2044a(arrayList));
        setAdapter(this.f2248b);
    }

    private static int m2163b(TextView textView) {
        return Integer.valueOf(LanguageUtils.m2046b(textView.getText().toString())).intValue();
    }

    public void m2164a() {
        this.f2248b.notifyDataSetChanged();
        m2165a(this.f2247a.m2076a().f2185a - this.f2247a.m2084f());
    }

    public void m2165a(int i) {
        m2166a(i, (this.f2249c / 2) - (this.f2250d / 2));
    }

    public void m2166a(int i, int i2) {
        post(new YearPickerView(this, i, i2));
    }

    public int getFirstPositionOffset() {
        View childAt = getChildAt(0);
        return childAt == null ? 0 : childAt.getTop();
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (accessibilityEvent.getEventType() == ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT) {
            accessibilityEvent.setFromIndex(0);
            accessibilityEvent.setToIndex(0);
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        this.f2247a.m2088j();
        TextViewWithCircularIndicator textViewWithCircularIndicator = (TextViewWithCircularIndicator) view;
        if (textViewWithCircularIndicator != null) {
            if (textViewWithCircularIndicator != this.f2251e) {
                if (this.f2251e != null) {
                    this.f2251e.m2075a(false);
                    this.f2251e.requestLayout();
                }
                textViewWithCircularIndicator.m2075a(true);
                textViewWithCircularIndicator.requestLayout();
                this.f2251e = textViewWithCircularIndicator;
            }
            this.f2247a.m2077a(YearPickerView.m2163b(textViewWithCircularIndicator));
            this.f2248b.notifyDataSetChanged();
        }
    }
}

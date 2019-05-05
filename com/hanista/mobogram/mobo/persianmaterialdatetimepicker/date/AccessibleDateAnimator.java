package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ViewAnimator;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.LanguageUtils;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.PersianCalendar;

public class AccessibleDateAnimator extends ViewAnimator {
    private long f2128a;

    public AccessibleDateAnimator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() != 32) {
            return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        accessibilityEvent.getText().clear();
        PersianCalendar persianCalendar = new PersianCalendar();
        persianCalendar.setTimeInMillis(this.f2128a);
        accessibilityEvent.getText().add(LanguageUtils.m2043a(persianCalendar.m2053d() + " " + persianCalendar.m2051b()));
        return true;
    }

    public void setDateMillis(long j) {
        this.f2128a = j;
    }
}

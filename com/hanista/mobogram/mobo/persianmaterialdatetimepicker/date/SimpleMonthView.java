package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.LanguageUtils;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.h */
public class SimpleMonthView extends MonthView {
    public SimpleMonthView(Context context, AttributeSet attributeSet, DatePickerController datePickerController) {
        super(context, attributeSet, datePickerController);
    }

    public void m2158a(Canvas canvas, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        if (this.x == i3) {
            canvas.drawCircle((float) i4, (float) (i5 - (d / 3)), (float) h, this.n);
        }
        if (m2152b(i, i2, i3)) {
            this.l.setTypeface(Typeface.create(Typeface.DEFAULT, 1));
        } else {
            this.l.setTypeface(Typeface.create(Typeface.DEFAULT, 0));
        }
        if (m2147a(i, i2, i3)) {
            this.l.setColor(this.M);
        } else if (this.x == i3) {
            this.l.setColor(this.I);
        } else if (this.w && this.y == i3) {
            this.l.setColor(this.K);
        } else {
            this.l.setColor(m2152b(i, i2, i3) ? this.L : this.H);
        }
        canvas.drawText(LanguageUtils.m2043a(String.format("%d", new Object[]{Integer.valueOf(i3)})), (float) i4, (float) i5, this.l);
    }
}

package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date;

import android.content.Context;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.g */
public class SimpleMonthAdapter extends MonthAdapter {
    public SimpleMonthAdapter(Context context, DatePickerController datePickerController) {
        super(context, datePickerController);
    }

    public MonthView m2157a(Context context) {
        return new SimpleMonthView(context, null, this.a);
    }
}

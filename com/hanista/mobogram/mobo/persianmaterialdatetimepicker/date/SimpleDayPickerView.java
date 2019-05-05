package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date;

import android.content.Context;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.date.f */
public class SimpleDayPickerView extends DayPickerView {
    public SimpleDayPickerView(Context context, DatePickerController datePickerController) {
        super(context, datePickerController);
    }

    public MonthAdapter m2156a(Context context, DatePickerController datePickerController) {
        return new SimpleMonthAdapter(context, datePickerController);
    }
}

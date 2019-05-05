package com.hanista.mobogram.mobo.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.hanista.mobogram.mobo.p008i.FontUtil;

public class HanistaTextView extends TextView {
    public HanistaTextView(Context context) {
        super(context);
        m435a(context, null);
    }

    public HanistaTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        m435a(context, attributeSet);
    }

    public HanistaTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        m435a(context, attributeSet);
    }

    private void m435a(Context context, AttributeSet attributeSet) {
        setTypeface(FontUtil.m1176a().m1161d());
    }
}

package com.hanista.mobogram.mobo.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.hanista.mobogram.mobo.p008i.FontUtil;

public class HanistaButton extends Button {
    public HanistaButton(Context context) {
        super(context);
        m434a(context, null);
    }

    public HanistaButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        m434a(context, attributeSet);
    }

    public HanistaButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        m434a(context, attributeSet);
    }

    private void m434a(Context context, AttributeSet attributeSet) {
        setTypeface(FontUtil.m1176a().m1161d());
    }
}

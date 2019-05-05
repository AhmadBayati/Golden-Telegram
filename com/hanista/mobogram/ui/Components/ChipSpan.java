package com.hanista.mobogram.ui.Components;

import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import com.hanista.mobogram.messenger.AndroidUtilities;

public class ChipSpan extends ImageSpan {
    public int uid;

    public ChipSpan(Drawable drawable, int i) {
        super(drawable, i);
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
        FontMetricsInt fontMetricsInt2 = fontMetricsInt == null ? new FontMetricsInt() : fontMetricsInt;
        int size = super.getSize(paint, charSequence, i, i2, fontMetricsInt2);
        int dp = AndroidUtilities.dp(6.0f);
        int i3 = (fontMetricsInt2.bottom - fontMetricsInt2.top) / 2;
        fontMetricsInt2.top = (-i3) - dp;
        fontMetricsInt2.bottom = i3 - dp;
        fontMetricsInt2.ascent = (-i3) - dp;
        fontMetricsInt2.leading = 0;
        fontMetricsInt2.descent = i3 - dp;
        return size;
    }
}

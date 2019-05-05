package com.hanista.mobogram.ui.Components;

import android.text.TextPaint;
import com.hanista.mobogram.mobo.p008i.FontUtil;

public class URLSpanNoUnderlineBold extends URLSpanNoUnderline {
    public URLSpanNoUnderlineBold(String str) {
        super(str);
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setTypeface(FontUtil.m1176a().m1160c());
        textPaint.setUnderlineText(false);
    }
}

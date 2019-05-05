package com.hanista.mobogram.ui.Components;

import android.text.TextPaint;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class URLSpanUserMention extends URLSpanNoUnderline {
    public URLSpanUserMention(String str) {
        super(str);
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setColor(Theme.MSG_LINK_TEXT_COLOR);
        textPaint.setUnderlineText(false);
    }
}

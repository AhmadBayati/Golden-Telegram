package com.hanista.mobogram.ui.Components;

import android.text.TextPaint;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class URLSpanBotCommand extends URLSpanNoUnderline {
    public static boolean enabled;

    static {
        enabled = true;
    }

    public URLSpanBotCommand(String str) {
        super(str);
    }

    private void initTheme(TextPaint textPaint) {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.cd;
            boolean z = AdvanceTheme.ce;
            if (enabled && z) {
                textPaint.setColor(i);
            }
            textPaint.setUnderlineText(false);
        }
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setColor(enabled ? Theme.MSG_LINK_TEXT_COLOR : Theme.MSG_TEXT_COLOR);
        textPaint.setUnderlineText(false);
        initTheme(textPaint);
    }
}

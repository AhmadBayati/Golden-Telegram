package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class TextInfoPrivacyCell extends FrameLayout {
    private TextView textView;

    public TextInfoPrivacyCell(Context context) {
        int i = 5;
        super(context);
        this.textView = new TextView(context);
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
        this.textView.setTextColor(-8355712);
        this.textView.setLinkTextColor(Theme.MSG_LINK_TEXT_COLOR);
        this.textView.setTextSize(1, 14.0f);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        View view = this.textView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 48, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            int i;
            int i2 = AdvanceTheme.f2495f;
            int i3 = AdvanceTheme.f2496g;
            if ((getTag() != null ? getTag().toString() : TtmlNode.ANONYMOUS_REGION_ID).contains("Profile")) {
                i = AdvanceTheme.aH;
            } else if (i3 != Theme.ACTION_BAR_MODE_SELECTOR_COLOR) {
                setBackgroundColor(i3);
                i = i2;
            } else {
                setBackgroundResource(C0338R.drawable.greydivider);
                i = i2;
            }
            this.textView.setTextColor(i);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(0, 0));
        initTheme();
    }

    public void setText(CharSequence charSequence) {
        this.textView.setText(charSequence);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }
}

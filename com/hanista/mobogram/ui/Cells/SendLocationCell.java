package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.SimpleTextView;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class SendLocationCell extends FrameLayout {
    private SimpleTextView accurateTextView;
    private SimpleTextView titleTextView;

    public SendLocationCell(Context context) {
        int i = 5;
        super(context);
        View imageView = new ImageView(context);
        imageView.setImageResource(C0338R.drawable.pin);
        addView(imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 13.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
        this.titleTextView = new SimpleTextView(context);
        this.titleTextView.setTextSize(16);
        this.titleTextView.setTextColor(-13141330);
        this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.titleTextView.setTypeface(FontUtil.m1176a().m1160c());
        addView(this.titleTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 16.0f : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
        this.accurateTextView = new SimpleTextView(context);
        this.accurateTextView.setTextSize(14);
        this.accurateTextView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        this.accurateTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.accurateTextView.setTypeface(FontUtil.m1176a().m1161d());
        imageView = this.accurateTextView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(imageView, LayoutHelper.createFrame(-1, 20.0f, i | 48, LocaleController.isRTL ? 16.0f : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : 16.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setText(String str, String str2) {
        this.titleTextView.setText(str);
        this.accurateTextView.setText(str2);
    }
}

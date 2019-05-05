package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class TextDetailCell extends FrameLayout {
    private ImageView imageView;
    private TextView textView;
    private TextView valueTextView;

    public TextDetailCell(Context context) {
        int i = 5;
        super(context);
        this.textView = new TextView(context);
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
        this.textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 16.0f : 71.0f, 10.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.valueTextView.setTextColor(-7697782);
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 16.0f : 71.0f, 35.0f, LocaleController.isRTL ? 71.0f : 16.0f, 0.0f));
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ScaleType.CENTER);
        View view = this.imageView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setTextAndValue(String str, String str2) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.imageView.setVisibility(4);
    }

    public void setTextAndValueAndIcon(String str, String str2, int i) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.imageView.setVisibility(0);
        this.imageView.setImageResource(i);
    }

    public void setTextAndValueAndIcon(String str, String str2, Drawable drawable) {
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        this.imageView.setVisibility(0);
        this.imageView.setImageDrawable(drawable);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setValueColor(int i) {
        this.valueTextView.setTextColor(i);
    }
}

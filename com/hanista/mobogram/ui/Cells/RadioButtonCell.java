package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RadioButton;

public class RadioButtonCell extends FrameLayout {
    private static Paint paint;
    private boolean needDivider;
    private RadioButton radioButton;
    private TextView textView;
    private TextView valueTextView;

    public RadioButtonCell(Context context) {
        int i = 17;
        int i2 = 5;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.radioButton = new RadioButton(context);
        this.radioButton.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(-5000269, -13129232);
        addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 18), 10.0f, (float) (LocaleController.isRTL ? 18 : 0), 0.0f));
        this.textView = new TextView(context);
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
        this.textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 17 : 51), 10.0f, (float) (LocaleController.isRTL ? 51 : 17), 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.valueTextView.setTextColor(-7697782);
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(0);
        this.valueTextView.setMaxLines(0);
        this.valueTextView.setSingleLine(false);
        this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0f));
        View view = this.valueTextView;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        int i3 = i2 | 48;
        float f = (float) (LocaleController.isRTL ? 17 : 51);
        if (LocaleController.isRTL) {
            i = 51;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i3, f, 35.0f, (float) i, 0.0f));
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setChecked(boolean z, boolean z2) {
        this.radioButton.setChecked(z, z2);
    }

    public void setTextAndValue(String str, String str2, boolean z, boolean z2) {
        boolean z3 = false;
        this.textView.setText(str);
        this.valueTextView.setText(str2);
        if (str2 == null) {
            this.valueTextView.setVisibility(8);
        } else {
            this.valueTextView.setVisibility(0);
        }
        this.needDivider = z2;
        this.radioButton.setChecked(z, false);
        if (!z2) {
            z3 = true;
        }
        setWillNotDraw(z3);
    }
}

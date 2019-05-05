package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.CheckBoxSquare;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class CheckBoxCell extends FrameLayout {
    private static Paint paint;
    private CheckBoxSquare checkBox;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public CheckBoxCell(Context context) {
        int i = 17;
        int i2 = 5;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.textView = new TextView(context);
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
        this.textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 17 : 46), 0.0f, (float) (LocaleController.isRTL ? 46 : 17), 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.valueTextView.setTextColor(-13660983);
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, (LocaleController.isRTL ? 3 : 5) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.checkBox = new CheckBoxSquare(context);
        View view = this.checkBox;
        if (!LocaleController.isRTL) {
            i2 = 3;
        }
        i2 |= 48;
        float f = (float) (LocaleController.isRTL ? 0 : 17);
        if (!LocaleController.isRTL) {
            i = 0;
        }
        addView(view, LayoutHelper.createFrame(18, 18.0f, i2, f, 15.0f, (float) i, 0.0f));
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), (this.needDivider ? 1 : 0) + AndroidUtilities.dp(48.0f));
        int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.valueTextView.measure(MeasureSpec.makeMeasureSpec(measuredWidth / 2, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0700C.ENCODING_PCM_32BIT));
        this.textView.measure(MeasureSpec.makeMeasureSpec((measuredWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0700C.ENCODING_PCM_32BIT));
        this.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    public void setText(String str, String str2, boolean z, boolean z2) {
        boolean z3 = false;
        this.textView.setText(str);
        this.checkBox.setChecked(z, false);
        this.valueTextView.setText(str2);
        this.needDivider = z2;
        if (!z2) {
            z3 = true;
        }
        setWillNotDraw(z3);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }
}

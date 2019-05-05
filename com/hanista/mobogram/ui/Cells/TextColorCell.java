package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class TextColorCell extends FrameLayout {
    private static Paint paint;
    private Drawable colorDrawable;
    private int currentColor;
    private boolean needDivider;
    private TextView textView;

    public TextColorCell(Context context) {
        int i = 5;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.colorDrawable = getResources().getDrawable(C0338R.drawable.switch_to_on2);
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, i | 48, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            setBackgroundColor(AdvanceTheme.f2497h);
            int i = AdvanceTheme.f2498i;
            this.textView.setTextColor(AdvanceTheme.f2494e);
            paint.setColor(i);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
        if (this.currentColor != 0 && this.colorDrawable != null) {
            int measuredHeight = (getMeasuredHeight() - this.colorDrawable.getMinimumHeight()) / 2;
            int measuredWidth = !LocaleController.isRTL ? (getMeasuredWidth() - this.colorDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(14.5f) : AndroidUtilities.dp(14.5f);
            this.colorDrawable.setBounds(measuredWidth, measuredHeight, this.colorDrawable.getIntrinsicWidth() + measuredWidth, this.colorDrawable.getIntrinsicHeight() + measuredHeight);
            this.colorDrawable.draw(canvas);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(48.0f), C0700C.ENCODING_PCM_32BIT));
        initTheme();
    }

    public void setTextAndColor(String str, int i, boolean z) {
        this.textView.setText(str);
        this.needDivider = z;
        this.currentColor = i;
        this.colorDrawable.setColorFilter(new PorterDuffColorFilter(i, Mode.MULTIPLY));
        boolean z2 = !this.needDivider && this.currentColor == 0;
        setWillNotDraw(z2);
        invalidate();
    }
}

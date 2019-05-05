package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class TextSettingsCell extends FrameLayout {
    private static Paint paint;
    private boolean needDivider;
    private TextView textView;
    private ImageView valueImageView;
    private TextView valueTextView;

    public TextSettingsCell(Context context) {
        int i = 3;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
        addView(this.textView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextColor(ThemeUtil.m2485a().m2289c());
        if (ThemeUtil.m2490b()) {
            this.valueTextView.setTextColor(AdvanceTheme.f2491b);
        }
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        this.valueTextView.setTypeface(FontUtil.m1176a().m1161d());
        addView(this.valueTextView, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, (LocaleController.isRTL ? 3 : 5) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.valueImageView = new ImageView(context);
        this.valueImageView.setScaleType(ScaleType.CENTER);
        this.valueImageView.setVisibility(4);
        View view = this.valueImageView;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 16, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2497h;
            int i2 = AdvanceTheme.f2498i;
            int i3 = AdvanceTheme.f2494e;
            int i4 = AdvanceTheme.f2499j;
            if ((getTag() != null ? getTag().toString() : TtmlNode.ANONYMOUS_REGION_ID).contains("Profile")) {
                int i5 = AdvanceTheme.aA;
                setBackgroundColor(i5);
                if (i5 != -1) {
                    paint.setColor(i5);
                }
                this.textView.setTextColor(AdvanceTheme.aB);
                if (i5 != -1) {
                    this.valueTextView.setTextColor(0);
                    return;
                }
                return;
            }
            setBackgroundColor(i);
            this.textView.setTextColor(i3);
            paint.setColor(i2);
            this.valueTextView.setTextColor(i4);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), paint);
        }
    }

    protected void onMeasure(int i, int i2) {
        initTheme();
        setMeasuredDimension(MeasureSpec.getSize(i), (this.needDivider ? 1 : 0) + AndroidUtilities.dp(48.0f));
        int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        int i3 = measuredWidth / 2;
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(MeasureSpec.makeMeasureSpec(i3, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0700C.ENCODING_PCM_32BIT));
        }
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.measure(MeasureSpec.makeMeasureSpec(i3, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0700C.ENCODING_PCM_32BIT));
            measuredWidth = (measuredWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f);
        }
        this.textView.measure(MeasureSpec.makeMeasureSpec(measuredWidth, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0700C.ENCODING_PCM_32BIT));
    }

    public void setText(String str, boolean z) {
        this.textView.setText(str);
        this.valueTextView.setVisibility(4);
        this.valueImageView.setVisibility(4);
        this.needDivider = z;
        setWillNotDraw(!z);
    }

    public void setTextAndIcon(String str, int i, boolean z) {
        boolean z2 = false;
        this.textView.setText(str);
        this.valueTextView.setVisibility(4);
        if (i != 0) {
            this.valueImageView.setVisibility(0);
            this.valueImageView.setImageResource(i);
        } else {
            this.valueImageView.setVisibility(4);
        }
        this.needDivider = z;
        if (!z) {
            z2 = true;
        }
        setWillNotDraw(z2);
    }

    public void setTextAndValue(String str, String str2, boolean z) {
        boolean z2 = false;
        this.textView.setText(str);
        this.valueImageView.setVisibility(4);
        if (str2 != null) {
            this.valueTextView.setText(str2);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.needDivider = z;
        if (!z) {
            z2 = true;
        }
        setWillNotDraw(z2);
        requestLayout();
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
        initTheme();
    }

    public void setTextValueColor(int i) {
        this.valueTextView.setTextColor(i);
    }
}

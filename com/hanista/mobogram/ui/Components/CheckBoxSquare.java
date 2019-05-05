package com.hanista.mobogram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class CheckBoxSquare extends View {
    private static Paint backgroundPaint = null;
    private static Paint checkPaint = null;
    private static Paint eraser = null;
    private static final float progressBounceDiff = 0.2f;
    private static RectF rectF;
    private boolean attachedToWindow;
    private ObjectAnimator checkAnimator;
    private int color;
    private Bitmap drawBitmap;
    private Canvas drawCanvas;
    private boolean isChecked;
    private boolean isDisabled;
    private float progress;

    public CheckBoxSquare(Context context) {
        super(context);
        this.color = Theme.ACTION_BAR_COLOR;
        if (checkPaint == null) {
            checkPaint = new Paint(1);
            checkPaint.setColor(-1);
            checkPaint.setStyle(Style.STROKE);
            checkPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            eraser = new Paint(1);
            eraser.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            backgroundPaint = new Paint(1);
            rectF = new RectF();
        }
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), Config.ARGB_4444);
        this.drawCanvas = new Canvas(this.drawBitmap);
    }

    private void animateToCheckedState(boolean z) {
        String str = NotificationCompatApi24.CATEGORY_PROGRESS;
        float[] fArr = new float[1];
        fArr[0] = z ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
        this.checkAnimator = ObjectAnimator.ofFloat(this, str, fArr);
        this.checkAnimator.setDuration(300);
        this.checkAnimator.start();
    }

    private void cancelCheckAnimator() {
        if (this.checkAnimator != null) {
            this.checkAnimator.cancel();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            float f;
            float f2;
            if (this.progress <= 0.5f) {
                f = this.progress / 0.5f;
                backgroundPaint.setColor(Color.rgb(((int) (((float) (Color.red(this.color) - 115)) * f)) + 115, ((int) (((float) (Color.green(this.color) - 115)) * f)) + 115, ((int) (((float) (Color.blue(this.color) - 115)) * f)) + 115));
                f2 = f;
            } else {
                f = 2.0f - (this.progress / 0.5f);
                backgroundPaint.setColor(this.color);
                f2 = f;
                f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            }
            if (this.isDisabled) {
                backgroundPaint.setColor(-5197648);
            }
            float dp = ((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) * f2;
            rectF.set(dp, dp, ((float) AndroidUtilities.dp(18.0f)) - dp, ((float) AndroidUtilities.dp(18.0f)) - dp);
            this.drawBitmap.eraseColor(0);
            this.drawCanvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), backgroundPaint);
            if (f != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                f = Math.min((float) AndroidUtilities.dp(7.0f), (f * ((float) AndroidUtilities.dp(7.0f))) + dp);
                rectF.set(((float) AndroidUtilities.dp(2.0f)) + f, ((float) AndroidUtilities.dp(2.0f)) + f, ((float) AndroidUtilities.dp(16.0f)) - f, ((float) AndroidUtilities.dp(16.0f)) - f);
                this.drawCanvas.drawRect(rectF, eraser);
            }
            if (this.progress > 0.5f) {
                this.drawCanvas.drawLine((float) AndroidUtilities.dp(7.5f), (float) ((int) AndroidUtilities.dpf2(13.5f)), (float) ((int) (((float) AndroidUtilities.dp(7.5f)) - (((float) AndroidUtilities.dp(5.0f)) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f2)))), (float) ((int) (AndroidUtilities.dpf2(13.5f) - (((float) AndroidUtilities.dp(5.0f)) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f2)))), checkPaint);
                this.drawCanvas.drawLine((float) ((int) AndroidUtilities.dpf2(6.5f)), (float) ((int) AndroidUtilities.dpf2(13.5f)), (float) ((int) (AndroidUtilities.dpf2(6.5f) + (((float) AndroidUtilities.dp(9.0f)) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f2)))), (float) ((int) (AndroidUtilities.dpf2(13.5f) - (((float) AndroidUtilities.dp(9.0f)) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f2)))), checkPaint);
            }
            canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, null);
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setChecked(boolean z, boolean z2) {
        if (z != this.isChecked) {
            this.isChecked = z;
            if (this.attachedToWindow && z2) {
                animateToCheckedState(z);
                return;
            }
            cancelCheckAnimator();
            setProgress(z ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f);
        }
    }

    public void setColor(int i) {
        this.color = i;
    }

    public void setDisabled(boolean z) {
        this.isDisabled = z;
        invalidate();
    }

    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 0 && this.drawBitmap != null) {
        }
    }
}

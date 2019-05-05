package com.hanista.mobogram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;

public class CheckBox extends View {
    private static Paint backgroundPaint = null;
    private static Paint checkPaint = null;
    private static Paint eraser = null;
    private static Paint eraser2 = null;
    private static Paint paint = null;
    private static final float progressBounceDiff = 0.2f;
    private boolean attachedToWindow;
    private Canvas bitmapCanvas;
    private ObjectAnimator checkAnimator;
    private Bitmap checkBitmap;
    private Canvas checkCanvas;
    private Drawable checkDrawable;
    private int checkOffset;
    private int color;
    private boolean drawBackground;
    private Bitmap drawBitmap;
    private boolean isCheckAnimation;
    private boolean isChecked;
    private float progress;
    private int size;

    public CheckBox(Context context, int i) {
        super(context);
        this.isCheckAnimation = true;
        this.size = 22;
        this.color = -10567099;
        if (paint == null) {
            paint = new Paint(1);
            eraser = new Paint(1);
            eraser.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            eraser2 = new Paint(1);
            eraser2.setColor(0);
            eraser2.setStyle(Style.STROKE);
            eraser2.setStrokeWidth((float) AndroidUtilities.dp(28.0f));
            eraser2.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            backgroundPaint = new Paint(1);
            backgroundPaint.setColor(-1);
            backgroundPaint.setStyle(Style.STROKE);
            backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
        if (ThemeUtil.m2490b()) {
            this.color = AdvanceTheme.f2491b;
            if (i == C0338R.drawable.checkbig && backgroundPaint != null) {
                backgroundPaint.setColor(AdvanceTheme.bj);
            }
        }
        this.checkDrawable = context.getResources().getDrawable(i);
    }

    private void animateToCheckedState(boolean z) {
        this.isCheckAnimation = z;
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
            if (this.drawBackground || this.progress != 0.0f) {
                eraser2.setStrokeWidth((float) AndroidUtilities.dp((float) (this.size + 6)));
                this.drawBitmap.eraseColor(0);
                float measuredWidth = (float) (getMeasuredWidth() / 2);
                float f = this.progress >= 0.5f ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : this.progress / 0.5f;
                float f2 = this.progress < 0.5f ? 0.0f : (this.progress - 0.5f) / 0.5f;
                float f3 = this.isCheckAnimation ? this.progress : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.progress;
                if (f3 < progressBounceDiff) {
                    measuredWidth -= (f3 * ((float) AndroidUtilities.dp(2.0f))) / progressBounceDiff;
                } else if (f3 < 0.4f) {
                    measuredWidth -= ((float) AndroidUtilities.dp(2.0f)) - (((f3 - progressBounceDiff) * ((float) AndroidUtilities.dp(2.0f))) / progressBounceDiff);
                }
                if (this.drawBackground) {
                    paint.setColor(1140850688);
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), measuredWidth - ((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), paint);
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), measuredWidth - ((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), backgroundPaint);
                }
                paint.setColor(this.color);
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), measuredWidth, paint);
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f) * measuredWidth, eraser);
                canvas.drawBitmap(this.drawBitmap, 0.0f, 0.0f, null);
                this.checkBitmap.eraseColor(0);
                int intrinsicWidth = this.checkDrawable.getIntrinsicWidth();
                int intrinsicHeight = this.checkDrawable.getIntrinsicHeight();
                int measuredWidth2 = (getMeasuredWidth() - intrinsicWidth) / 2;
                int measuredHeight = (getMeasuredHeight() - intrinsicHeight) / 2;
                this.checkDrawable.setBounds(measuredWidth2, this.checkOffset + measuredHeight, intrinsicWidth + measuredWidth2, (intrinsicHeight + measuredHeight) + this.checkOffset);
                this.checkDrawable.draw(this.checkCanvas);
                this.checkCanvas.drawCircle((float) ((getMeasuredWidth() / 2) - AndroidUtilities.dp(2.5f)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(4.0f)), (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f2) * ((float) ((getMeasuredWidth() + AndroidUtilities.dp(6.0f)) / 2)), eraser2);
                canvas.drawBitmap(this.checkBitmap, 0.0f, 0.0f, null);
            }
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setCheckOffset(int i) {
        this.checkOffset = i;
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

    public void setDrawBackground(boolean z) {
        this.drawBackground = z;
    }

    public void setProgress(float f) {
        if (this.progress != f) {
            this.progress = f;
            invalidate();
        }
    }

    public void setSize(int i) {
        this.size = i;
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 0 && this.drawBitmap == null) {
            this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Config.ARGB_4444);
            this.bitmapCanvas = new Canvas(this.drawBitmap);
            this.checkBitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Config.ARGB_4444);
            this.checkCanvas = new Canvas(this.checkBitmap);
        }
    }
}

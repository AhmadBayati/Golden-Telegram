package com.hanista.mobogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

public class CloseProgressDrawable2 extends Drawable {
    private float angle;
    private boolean animating;
    private DecelerateInterpolator interpolator;
    private long lastFrameTime;
    private Paint paint;
    private RectF rect;

    public CloseProgressDrawable2() {
        this.paint = new Paint(1);
        this.interpolator = new DecelerateInterpolator();
        this.rect = new RectF();
        this.paint.setColor(-5395027);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStyle(Style.STROKE);
    }

    public void draw(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float f4;
        long currentTimeMillis = System.currentTimeMillis();
        if (this.lastFrameTime != 0) {
            long j = currentTimeMillis - this.lastFrameTime;
            if (this.animating || this.angle != 0.0f) {
                this.angle = (((float) (j * 360)) / 500.0f) + this.angle;
                if (this.animating || this.angle < 720.0f) {
                    this.angle -= (float) (((int) (this.angle / 720.0f)) * 720);
                } else {
                    this.angle = 0.0f;
                }
                invalidateSelf();
            }
        }
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        canvas.rotate(-45.0f);
        if (this.angle >= 0.0f && this.angle < 90.0f) {
            f = 0.0f;
            f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            f3 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            f4 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (this.angle / 90.0f);
        } else if (this.angle >= 90.0f && this.angle < BitmapDescriptorFactory.HUE_CYAN) {
            f = 0.0f;
            f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            f3 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - ((this.angle - 90.0f) / 90.0f);
            f4 = 0.0f;
        } else if (this.angle >= BitmapDescriptorFactory.HUE_CYAN && this.angle < BitmapDescriptorFactory.HUE_VIOLET) {
            f = 0.0f;
            f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - ((this.angle - BitmapDescriptorFactory.HUE_CYAN) / 90.0f);
            f3 = 0.0f;
            f4 = 0.0f;
        } else if (this.angle >= BitmapDescriptorFactory.HUE_VIOLET && this.angle < 360.0f) {
            f = (this.angle - BitmapDescriptorFactory.HUE_VIOLET) / 90.0f;
            f2 = 0.0f;
            f3 = 0.0f;
            f4 = 0.0f;
        } else if (this.angle >= 360.0f && this.angle < 450.0f) {
            f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - ((this.angle - 360.0f) / 90.0f);
            f2 = 0.0f;
            f3 = 0.0f;
            f4 = 0.0f;
        } else if (this.angle >= 450.0f && this.angle < 540.0f) {
            f = 0.0f;
            f2 = 0.0f;
            f3 = 0.0f;
            f4 = (this.angle - 450.0f) / 90.0f;
        } else if (this.angle >= 540.0f && this.angle < 630.0f) {
            f = 0.0f;
            f2 = 0.0f;
            f3 = (this.angle - 540.0f) / 90.0f;
            f4 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        } else if (this.angle < 630.0f || this.angle >= 720.0f) {
            f = 0.0f;
            f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            f3 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            f4 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        } else {
            f = 0.0f;
            f2 = (this.angle - 630.0f) / 90.0f;
            f3 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            f4 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        if (f4 != 0.0f) {
            canvas.drawLine(0.0f, 0.0f, 0.0f, ((float) AndroidUtilities.dp(8.0f)) * f4, this.paint);
        }
        if (f3 != 0.0f) {
            canvas.drawLine(((float) (-AndroidUtilities.dp(8.0f))) * f3, 0.0f, 0.0f, 0.0f, this.paint);
        }
        if (f2 != 0.0f) {
            canvas.drawLine(0.0f, ((float) (-AndroidUtilities.dp(8.0f))) * f2, 0.0f, 0.0f, this.paint);
        }
        if (f != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            canvas.drawLine(((float) AndroidUtilities.dp(8.0f)) * f, 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        }
        canvas.restore();
        int centerX = getBounds().centerX();
        int centerY = getBounds().centerY();
        this.rect.set((float) (centerX - AndroidUtilities.dp(8.0f)), (float) (centerY - AndroidUtilities.dp(8.0f)), (float) (centerX + AndroidUtilities.dp(8.0f)), (float) (centerY + AndroidUtilities.dp(8.0f)));
        canvas.drawArc(this.rect, (this.angle < 360.0f ? 0.0f : this.angle - 360.0f) - 45.0f, this.angle < 360.0f ? this.angle : 720.0f - this.angle, false, this.paint);
        this.lastFrameTime = currentTimeMillis;
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void startAnimation() {
        this.animating = true;
        this.lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
    }

    public void stopAnimation() {
        this.animating = false;
    }
}

package com.hanista.mobogram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;

public class BackDrawable extends Drawable {
    private boolean alwaysClose;
    private boolean animationInProgress;
    private int currentAnimationTime;
    private float currentRotation;
    private float finalRotation;
    private DecelerateInterpolator interpolator;
    private long lastFrameTime;
    private Paint paint;
    private Paint paintB;
    private boolean reverseAngle;

    public BackDrawable(boolean z) {
        this.paint = new Paint(1);
        this.reverseAngle = false;
        this.interpolator = new DecelerateInterpolator();
        this.paintB = new Paint(1);
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.alwaysClose = z;
    }

    public void draw(Canvas canvas) {
        float f;
        if (this.currentRotation != this.finalRotation) {
            if (this.lastFrameTime != 0) {
                this.currentAnimationTime = (int) ((System.currentTimeMillis() - this.lastFrameTime) + ((long) this.currentAnimationTime));
                if (this.currentAnimationTime >= 300) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) this.currentAnimationTime) / BitmapDescriptorFactory.HUE_MAGENTA) * this.finalRotation;
                } else {
                    this.currentRotation = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.interpolator.getInterpolation(((float) this.currentAnimationTime) / BitmapDescriptorFactory.HUE_MAGENTA);
                }
            }
            this.lastFrameTime = System.currentTimeMillis();
            invalidateSelf();
        }
        int i = (int) (-138.0f * this.currentRotation);
        this.paint.setColor(Color.rgb(i + NalUnitUtil.EXTENDED_SAR, i + NalUnitUtil.EXTENDED_SAR, i + NalUnitUtil.EXTENDED_SAR));
        if (ThemeUtil.m2490b() && this.currentRotation < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.paint.setColor(this.paintB.getColor());
        }
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        float f2 = this.currentRotation;
        if (this.alwaysClose) {
            canvas.rotate((((float) (this.reverseAngle ? -180 : 180)) * this.currentRotation) + 135.0f);
            f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        } else {
            canvas.rotate(((float) (this.reverseAngle ? -225 : 135)) * this.currentRotation);
            f = f2;
        }
        canvas.drawLine(((float) (-AndroidUtilities.dp(7.0f))) - (((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) * f), 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        float f3 = (float) (-AndroidUtilities.dp(0.5f));
        float dp = ((float) AndroidUtilities.dp(7.0f)) + (((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) * f);
        f2 = (((float) AndroidUtilities.dp(7.0f)) * f) + ((float) (-AndroidUtilities.dp(7.0f)));
        float dp2 = ((float) AndroidUtilities.dp(0.5f)) - (((float) AndroidUtilities.dp(0.5f)) * f);
        canvas.drawLine(f2, -f3, dp2, -dp, this.paint);
        canvas.drawLine(f2, f3, dp2, dp, this.paint);
        canvas.restore();
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

    public void setColor(int i) {
        this.paint.setColor(i);
        this.paintB.setColor(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setRotation(float f, boolean z) {
        this.lastFrameTime = 0;
        if (this.currentRotation == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.reverseAngle = true;
        } else if (this.currentRotation == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0;
        if (z) {
            if (this.currentRotation < f) {
                this.currentAnimationTime = (int) (this.currentRotation * BitmapDescriptorFactory.HUE_MAGENTA);
            } else {
                this.currentAnimationTime = (int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.currentRotation) * BitmapDescriptorFactory.HUE_MAGENTA);
            }
            this.lastFrameTime = System.currentTimeMillis();
            this.finalRotation = f;
        } else {
            this.currentRotation = f;
            this.finalRotation = f;
        }
        invalidateSelf();
    }
}

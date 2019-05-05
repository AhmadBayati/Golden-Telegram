package com.hanista.mobogram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;

public class MenuDrawable extends Drawable {
    private boolean animationInProgress;
    private int currentAnimationTime;
    private float currentRotation;
    private float finalRotation;
    private DecelerateInterpolator interpolator;
    private long lastFrameTime;
    private Paint paint;
    private boolean reverseAngle;

    public MenuDrawable() {
        this.paint = new Paint(1);
        this.reverseAngle = false;
        this.interpolator = new DecelerateInterpolator();
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        if (ThemeUtil.m2490b()) {
            this.paint.setColor(AdvanceTheme.f2507r);
        }
    }

    public void draw(Canvas canvas) {
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
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        canvas.rotate(((float) (this.reverseAngle ? -180 : 180)) * this.currentRotation);
        canvas.drawLine((float) (-AndroidUtilities.dp(9.0f)), 0.0f, ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(3.0f)) * this.currentRotation), 0.0f, this.paint);
        float dp = (((float) AndroidUtilities.dp(5.0f)) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - Math.abs(this.currentRotation))) - (((float) AndroidUtilities.dp(0.5f)) * Math.abs(this.currentRotation));
        float dp2 = ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(2.5f)) * Math.abs(this.currentRotation));
        float dp3 = ((float) AndroidUtilities.dp(5.0f)) + (((float) AndroidUtilities.dp(2.0f)) * Math.abs(this.currentRotation));
        float dp4 = (((float) AndroidUtilities.dp(7.5f)) * Math.abs(this.currentRotation)) + ((float) (-AndroidUtilities.dp(9.0f)));
        canvas.drawLine(dp4, -dp3, dp2, -dp, this.paint);
        canvas.drawLine(dp4, dp3, dp2, dp, this.paint);
        canvas.restore();
        if (ThemeUtil.m2490b()) {
            this.paint.setColor(AdvanceTheme.f2507r);
        }
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

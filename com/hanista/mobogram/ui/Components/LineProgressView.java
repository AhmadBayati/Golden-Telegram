package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

public class LineProgressView extends View {
    private static DecelerateInterpolator decelerateInterpolator;
    private static Paint progressPaint;
    private float animatedAlphaValue;
    private float animatedProgressValue;
    private float animationProgressStart;
    private int backColor;
    private float currentProgress;
    private long currentProgressTime;
    private long lastUpdateTime;
    private int progressColor;

    static {
        decelerateInterpolator = null;
        progressPaint = null;
    }

    public LineProgressView(Context context) {
        super(context);
        this.lastUpdateTime = 0;
        this.currentProgress = 0.0f;
        this.animationProgressStart = 0.0f;
        this.currentProgressTime = 0;
        this.animatedProgressValue = 0.0f;
        this.animatedAlphaValue = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.progressColor = -13196562;
        if (decelerateInterpolator == null) {
            decelerateInterpolator = new DecelerateInterpolator();
            progressPaint = new Paint(1);
            progressPaint.setStyle(Style.STROKE);
            progressPaint.setStrokeCap(Cap.ROUND);
            progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    private void updateAnimation() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (!(this.animatedProgressValue == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || this.animatedProgressValue == this.currentProgress)) {
            float f = this.currentProgress - this.animationProgressStart;
            if (f > 0.0f) {
                this.currentProgressTime += j;
                if (this.currentProgressTime >= 300) {
                    this.animatedProgressValue = this.currentProgress;
                    this.animationProgressStart = this.currentProgress;
                    this.currentProgressTime = 0;
                } else {
                    this.animatedProgressValue = (f * decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / BitmapDescriptorFactory.HUE_MAGENTA)) + this.animationProgressStart;
                }
            }
            invalidate();
        }
        if (this.animatedProgressValue >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.animatedProgressValue == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT && this.animatedAlphaValue != 0.0f) {
            this.animatedAlphaValue -= ((float) j) / 200.0f;
            if (this.animatedAlphaValue <= 0.0f) {
                this.animatedAlphaValue = 0.0f;
            }
            invalidate();
        }
    }

    public void onDraw(Canvas canvas) {
        if (!(this.backColor == 0 || this.animatedProgressValue == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) {
            progressPaint.setColor(this.backColor);
            progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            canvas.drawRect((float) ((int) (((float) getWidth()) * this.animatedProgressValue)), 0.0f, (float) getWidth(), (float) getHeight(), progressPaint);
        }
        progressPaint.setColor(this.progressColor);
        progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
        canvas.drawRect(0.0f, 0.0f, ((float) getWidth()) * this.animatedProgressValue, (float) getHeight(), progressPaint);
        updateAnimation();
    }

    public void setBackColor(int i) {
        this.backColor = i;
    }

    public void setProgress(float f, boolean z) {
        if (z) {
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = f;
            this.animationProgressStart = f;
        }
        if (f != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.animatedAlphaValue = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        this.currentProgress = f;
        this.currentProgressTime = 0;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
    }
}

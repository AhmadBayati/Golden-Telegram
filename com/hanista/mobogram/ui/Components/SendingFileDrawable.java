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
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class SendingFileDrawable extends Drawable {
    private static DecelerateInterpolator decelerateInterpolator;
    private float animatedProgressValue;
    private float animationProgressStart;
    private RectF cicleRect;
    private float currentProgress;
    private long currentProgressTime;
    private boolean isChat;
    private long lastUpdateTime;
    private Paint paint;
    private float radOffset;
    private boolean started;

    static {
        decelerateInterpolator = null;
    }

    public SendingFileDrawable() {
        this.radOffset = 0.0f;
        this.currentProgress = 0.0f;
        this.animationProgressStart = 0.0f;
        this.currentProgressTime = 0;
        this.animatedProgressValue = 0.0f;
        this.cicleRect = new RectF();
        this.isChat = false;
        this.paint = new Paint(1);
        this.lastUpdateTime = 0;
        this.started = false;
        this.paint.setColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Cap.ROUND);
        decelerateInterpolator = new DecelerateInterpolator();
        initTheme();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            this.paint.setColor(AdvanceTheme.bU);
        }
    }

    private void update() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (this.animatedProgressValue != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.radOffset += ((float) (360 * j)) / 1000.0f;
            float f = this.currentProgress - this.animationProgressStart;
            if (f > 0.0f) {
                this.currentProgressTime = j + this.currentProgressTime;
                if (this.currentProgressTime >= 300) {
                    this.animatedProgressValue = this.currentProgress;
                    this.animationProgressStart = this.currentProgress;
                    this.currentProgressTime = 0;
                } else {
                    this.animatedProgressValue = (f * decelerateInterpolator.getInterpolation(((float) this.currentProgressTime) / BitmapDescriptorFactory.HUE_MAGENTA)) + this.animationProgressStart;
                }
            }
            invalidateSelf();
        }
    }

    public void draw(Canvas canvas) {
        this.cicleRect.set((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), (float) AndroidUtilities.dp(this.isChat ? 3.0f : 4.0f), (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(this.isChat ? 11.0f : 12.0f));
        canvas.drawArc(this.cicleRect, this.radOffset - 0.049804688f, Math.max(BitmapDescriptorFactory.HUE_YELLOW, 360.0f * this.animatedProgressValue), false, this.paint);
        if (this.started) {
            update();
        }
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(14.0f);
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(14.0f);
    }

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setIsChat(boolean z) {
        this.isChat = z;
    }

    public void setProgress(float f, boolean z) {
        if (z) {
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = f;
            this.animationProgressStart = f;
        }
        this.currentProgress = f;
        this.currentProgressTime = 0;
        invalidateSelf();
    }

    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    public void stop() {
        this.started = false;
    }
}

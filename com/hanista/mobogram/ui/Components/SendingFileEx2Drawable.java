package com.hanista.mobogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class SendingFileEx2Drawable extends Drawable {
    private boolean isChat;
    private long lastUpdateTime;
    private Paint paint;
    private float progress;
    private boolean started;

    public SendingFileEx2Drawable() {
        this.isChat = false;
        this.paint = new Paint(1);
        this.lastUpdateTime = 0;
        this.started = false;
        this.paint.setColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setStrokeCap(Cap.ROUND);
        initTheme();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            this.paint.setColor(AdvanceTheme.bU);
        }
    }

    private void update() {
        long j = 50;
        long currentTimeMillis = System.currentTimeMillis();
        long j2 = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        if (j2 <= 50) {
            j = j2;
        }
        this.progress = (((float) j) / 1000.0f) + this.progress;
        while (this.progress > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.progress -= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        float f = 11.0f;
        float dp = (float) ((int) (this.progress <= 0.5f ? (float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) : (((float) AndroidUtilities.dp(11.0f)) * (this.progress - 0.5f)) * 2.0f));
        float dp2 = (float) AndroidUtilities.dp(this.isChat ? 11.0f : 12.0f);
        float dp3 = (float) ((int) (this.progress >= 0.5f ? (float) AndroidUtilities.dp(11.0f) : (((float) AndroidUtilities.dp(11.0f)) * this.progress) * 2.0f));
        if (!this.isChat) {
            f = 12.0f;
        }
        canvas.drawLine(dp, dp2, dp3, (float) AndroidUtilities.dp(f), this.paint);
        if (this.started) {
            update();
        }
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(14.0f);
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(18.0f);
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

    public void start() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.started = true;
        invalidateSelf();
    }

    public void stop() {
        this.started = false;
    }
}

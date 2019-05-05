package com.hanista.mobogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class RecordStatusDrawable extends Drawable {
    private boolean isChat;
    private long lastUpdateTime;
    private Paint paint;
    private float progress;
    private RectF rect;
    private boolean started;

    public RecordStatusDrawable() {
        this.isChat = false;
        this.paint = new Paint(1);
        this.lastUpdateTime = 0;
        this.started = false;
        this.rect = new RectF();
        this.paint.setColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
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
        this.progress = (((float) j) / BitmapDescriptorFactory.HUE_MAGENTA) + this.progress;
        while (this.progress > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.progress -= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(0.0f, (float) (AndroidUtilities.dp(this.isChat ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 2.0f) + (getIntrinsicHeight() / 2)));
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                this.paint.setAlpha((int) (this.progress * 255.0f));
            } else if (i == 3) {
                this.paint.setAlpha((int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.progress) * 255.0f));
            } else {
                this.paint.setAlpha(NalUnitUtil.EXTENDED_SAR);
            }
            float dp = ((float) (AndroidUtilities.dp(4.0f) * i)) + (((float) AndroidUtilities.dp(4.0f)) * this.progress);
            this.rect.set(-dp, -dp, dp, dp);
            canvas.drawArc(this.rect, -15.0f, BitmapDescriptorFactory.HUE_ORANGE, false, this.paint);
        }
        canvas.restore();
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

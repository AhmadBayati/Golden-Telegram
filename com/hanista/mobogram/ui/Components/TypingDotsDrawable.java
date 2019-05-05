package com.hanista.mobogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class TypingDotsDrawable extends Drawable {
    private DecelerateInterpolator decelerateInterpolator;
    private float[] elapsedTimes;
    private boolean isChat;
    private long lastUpdateTime;
    private Paint paint;
    private float[] scales;
    private float[] startTimes;
    private boolean started;

    /* renamed from: com.hanista.mobogram.ui.Components.TypingDotsDrawable.1 */
    class C14961 implements Runnable {
        C14961() {
        }

        public void run() {
            TypingDotsDrawable.this.checkUpdate();
        }
    }

    public TypingDotsDrawable() {
        this.isChat = false;
        this.paint = new Paint(1);
        this.scales = new float[3];
        this.startTimes = new float[]{0.0f, 150.0f, BitmapDescriptorFactory.HUE_MAGENTA};
        this.elapsedTimes = new float[]{0.0f, 0.0f, 0.0f};
        this.lastUpdateTime = 0;
        this.started = false;
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.paint.setColor(Theme.ACTION_BAR_SUBTITLE_COLOR);
        initTheme();
    }

    private void checkUpdate() {
        if (!this.started) {
            return;
        }
        if (NotificationCenter.getInstance().isAnimationInProgress()) {
            AndroidUtilities.runOnUIThread(new C14961(), 100);
        } else {
            update();
        }
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
        for (int i = 0; i < 3; i++) {
            float[] fArr = this.elapsedTimes;
            fArr[i] = fArr[i] + ((float) j);
            float f = this.elapsedTimes[i] - this.startTimes[i];
            if (f <= 0.0f) {
                this.scales[i] = 1.33f;
            } else if (f <= 320.0f) {
                this.scales[i] = this.decelerateInterpolator.getInterpolation(f / 320.0f) + 1.33f;
            } else if (f <= 640.0f) {
                this.scales[i] = (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.decelerateInterpolator.getInterpolation((f - 320.0f) / 320.0f)) + 1.33f;
            } else if (f >= 800.0f) {
                this.elapsedTimes[i] = 0.0f;
                this.startTimes[i] = 0.0f;
                this.scales[i] = 1.33f;
            } else {
                this.scales[i] = 1.33f;
            }
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        int dp = this.isChat ? AndroidUtilities.dp(8.5f) + getBounds().top : AndroidUtilities.dp(9.3f) + getBounds().top;
        canvas.drawCircle((float) AndroidUtilities.dp(3.0f), (float) dp, this.scales[0] * AndroidUtilities.density, this.paint);
        canvas.drawCircle((float) AndroidUtilities.dp(9.0f), (float) dp, this.scales[1] * AndroidUtilities.density, this.paint);
        canvas.drawCircle((float) AndroidUtilities.dp(15.0f), (float) dp, this.scales[2] * AndroidUtilities.density, this.paint);
        checkUpdate();
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(18.0f);
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
        for (int i = 0; i < 3; i++) {
            this.elapsedTimes[i] = 0.0f;
            this.scales[i] = 1.33f;
        }
        this.startTimes[0] = 0.0f;
        this.startTimes[1] = 150.0f;
        this.startTimes[2] = BitmapDescriptorFactory.HUE_MAGENTA;
        this.started = false;
    }
}

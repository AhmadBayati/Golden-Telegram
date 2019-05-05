package com.hanista.mobogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

public class ProgressView {
    public float currentProgress;
    public int height;
    private Paint innerPaint;
    private Paint outerPaint;
    public float progressHeight;
    public int width;

    public ProgressView() {
        this.currentProgress = 0.0f;
        this.progressHeight = (float) AndroidUtilities.dp(2.0f);
        this.innerPaint = new Paint();
        this.outerPaint = new Paint();
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        canvas2.drawRect(0.0f, ((float) (this.height / 2)) - (this.progressHeight / 2.0f), (float) this.width, (this.progressHeight / 2.0f) + ((float) (this.height / 2)), this.innerPaint);
        canvas2 = canvas;
        canvas2.drawRect(0.0f, ((float) (this.height / 2)) - (this.progressHeight / 2.0f), this.currentProgress * ((float) this.width), (this.progressHeight / 2.0f) + ((float) (this.height / 2)), this.outerPaint);
    }

    public void setProgress(float f) {
        this.currentProgress = f;
        if (this.currentProgress < 0.0f) {
            this.currentProgress = 0.0f;
        } else if (this.currentProgress > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.currentProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
    }

    public void setProgressColors(int i, int i2) {
        this.innerPaint.setColor(i);
        this.outerPaint.setColor(i2);
    }
}

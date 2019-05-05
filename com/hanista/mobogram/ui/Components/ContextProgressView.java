package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;
import com.hanista.mobogram.messenger.AndroidUtilities;

public class ContextProgressView extends View {
    private RectF cicleRect;
    private Paint innerPaint;
    private long lastUpdateTime;
    private Paint outerPaint;
    private int radOffset;

    public ContextProgressView(Context context, int i) {
        super(context);
        this.innerPaint = new Paint(1);
        this.outerPaint = new Paint(1);
        this.cicleRect = new RectF();
        this.radOffset = 0;
        this.innerPaint.setStyle(Style.STROKE);
        this.innerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStyle(Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStrokeCap(Cap.ROUND);
        if (i == 0) {
            this.innerPaint.setColor(-4202506);
            this.outerPaint.setColor(-13920542);
            return;
        }
        this.innerPaint.setColor(-4202506);
        this.outerPaint.setColor(-1);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        if (getVisibility() == 0) {
            long currentTimeMillis = System.currentTimeMillis();
            long j = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            this.radOffset = (int) (((float) this.radOffset) + (((float) (j * 360)) / 1000.0f));
            int measuredWidth = (getMeasuredWidth() / 2) - AndroidUtilities.dp(9.0f);
            int measuredHeight = (getMeasuredHeight() / 2) - AndroidUtilities.dp(9.0f);
            this.cicleRect.set((float) measuredWidth, (float) measuredHeight, (float) (measuredWidth + AndroidUtilities.dp(18.0f)), (float) (measuredHeight + AndroidUtilities.dp(18.0f)));
            canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(9.0f), this.innerPaint);
            canvas.drawArc(this.cicleRect, (float) (this.radOffset - 90), 90.0f, false, this.outerPaint);
            invalidate();
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }
}

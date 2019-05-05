package com.hanista.mobogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class CloseProgressDrawable extends Drawable {
    private int currentAnimationTime;
    private int currentSegment;
    private DecelerateInterpolator interpolator;
    private long lastFrameTime;
    private Paint paint;

    public CloseProgressDrawable() {
        this.paint = new Paint(1);
        this.interpolator = new DecelerateInterpolator();
        this.paint.setColor(Theme.ATTACH_SHEET_TEXT_COLOR);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Cap.ROUND);
    }

    public void draw(Canvas canvas) {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.lastFrameTime != 0) {
            this.currentAnimationTime = (int) ((currentTimeMillis - this.lastFrameTime) + ((long) this.currentAnimationTime));
            if (this.currentAnimationTime > Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                this.currentAnimationTime = 0;
                this.currentSegment++;
                if (this.currentSegment == 4) {
                    this.currentSegment -= 4;
                }
            }
        }
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        canvas.rotate(45.0f);
        this.paint.setAlpha(255 - ((this.currentSegment % 4) * 40));
        canvas.drawLine((float) (-AndroidUtilities.dp(8.0f)), 0.0f, 0.0f, 0.0f, this.paint);
        this.paint.setAlpha(255 - (((this.currentSegment + 1) % 4) * 40));
        canvas.drawLine(0.0f, (float) (-AndroidUtilities.dp(8.0f)), 0.0f, 0.0f, this.paint);
        this.paint.setAlpha(255 - (((this.currentSegment + 2) % 4) * 40));
        canvas.drawLine(0.0f, 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        this.paint.setAlpha(255 - (((this.currentSegment + 3) % 4) * 40));
        canvas.drawLine(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(8.0f), this.paint);
        canvas.restore();
        this.lastFrameTime = currentTimeMillis;
        invalidateSelf();
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
}

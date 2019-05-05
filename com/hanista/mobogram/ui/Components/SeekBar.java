package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

public class SeekBar {
    private static Paint innerPaint;
    private static Paint outerPaint;
    private static int thumbWidth;
    private SeekBarDelegate delegate;
    private int height;
    private int innerColor;
    private int outerColor;
    private boolean pressed;
    private boolean selected;
    private int selectedColor;
    private int thumbDX;
    private int thumbX;
    private int width;

    public interface SeekBarDelegate {
        void onSeekBarDrag(float f);
    }

    public SeekBar(Context context) {
        this.thumbX = 0;
        this.thumbDX = 0;
        this.pressed = false;
        if (innerPaint == null) {
            innerPaint = new Paint(1);
            outerPaint = new Paint(1);
            thumbWidth = AndroidUtilities.dp(24.0f);
        }
    }

    public void draw(Canvas canvas) {
        innerPaint.setColor(this.selected ? this.selectedColor : this.innerColor);
        outerPaint.setColor(this.outerColor);
        canvas.drawRect((float) (thumbWidth / 2), (float) ((this.height / 2) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) (this.width - (thumbWidth / 2)), (float) ((this.height / 2) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), innerPaint);
        canvas.drawRect((float) (thumbWidth / 2), (float) ((this.height / 2) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) ((thumbWidth / 2) + this.thumbX), (float) ((this.height / 2) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), outerPaint);
        canvas.drawCircle((float) (this.thumbX + (thumbWidth / 2)), (float) (this.height / 2), (float) AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f), outerPaint);
    }

    public boolean isDragging() {
        return this.pressed;
    }

    public boolean onTouch(int i, float f, float f2) {
        if (i == 0) {
            int i2 = (this.height - thumbWidth) / 2;
            if (((float) (this.thumbX - i2)) <= f && f <= ((float) (i2 + (this.thumbX + thumbWidth))) && f2 >= 0.0f && f2 <= ((float) this.height)) {
                this.pressed = true;
                this.thumbDX = (int) (f - ((float) this.thumbX));
                return true;
            }
        } else if (i == 1 || i == 3) {
            if (this.pressed) {
                if (i == 1 && this.delegate != null) {
                    this.delegate.onSeekBarDrag(((float) this.thumbX) / ((float) (this.width - thumbWidth)));
                }
                this.pressed = false;
                return true;
            }
        } else if (i == 2 && this.pressed) {
            this.thumbX = (int) (f - ((float) this.thumbDX));
            if (this.thumbX < 0) {
                this.thumbX = 0;
                return true;
            } else if (this.thumbX <= this.width - thumbWidth) {
                return true;
            } else {
                this.thumbX = this.width - thumbWidth;
                return true;
            }
        }
        return false;
    }

    public void setColors(int i, int i2, int i3) {
        this.innerColor = i;
        this.outerColor = i2;
        this.selectedColor = i3;
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public void setProgress(float f) {
        this.thumbX = (int) Math.ceil((double) (((float) (this.width - thumbWidth)) * f));
        if (this.thumbX < 0) {
            this.thumbX = 0;
        } else if (this.thumbX > this.width - thumbWidth) {
            this.thumbX = this.width - thumbWidth;
        }
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public void setSize(int i, int i2) {
        this.width = i;
        this.height = i2;
    }
}

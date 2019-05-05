package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

public class PhotoEditorSeekBar extends View {
    private PhotoEditorSeekBarDelegate delegate;
    private Paint innerPaint;
    private int maxValue;
    private int minValue;
    private Paint outerPaint;
    private boolean pressed;
    private float progress;
    private int thumbDX;
    private int thumbSize;

    public interface PhotoEditorSeekBarDelegate {
        void onProgressChanged();
    }

    public PhotoEditorSeekBar(Context context) {
        super(context);
        this.innerPaint = new Paint();
        this.outerPaint = new Paint(1);
        this.thumbSize = AndroidUtilities.dp(16.0f);
        this.thumbDX = 0;
        this.progress = 0.0f;
        this.pressed = false;
        this.innerPaint.setColor(-1724368840);
        this.outerPaint.setColor(-11292945);
    }

    public int getProgress() {
        return (int) (((float) this.minValue) + (this.progress * ((float) (this.maxValue - this.minValue))));
    }

    protected void onDraw(Canvas canvas) {
        int measuredHeight = (getMeasuredHeight() - this.thumbSize) / 2;
        int measuredWidth = (int) (((float) (getMeasuredWidth() - this.thumbSize)) * this.progress);
        canvas.drawRect((float) (this.thumbSize / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) (getMeasuredWidth() - (this.thumbSize / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), this.innerPaint);
        if (this.minValue == 0) {
            canvas.drawRect((float) (this.thumbSize / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) measuredWidth, (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), this.outerPaint);
        } else if (this.progress > 0.5f) {
            canvas.drawRect((float) ((getMeasuredWidth() / 2) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) ((getMeasuredHeight() - this.thumbSize) / 2), (float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() + this.thumbSize) / 2), this.outerPaint);
            canvas.drawRect((float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) measuredWidth, (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), this.outerPaint);
        } else {
            canvas.drawRect((float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() - this.thumbSize) / 2), (float) ((getMeasuredWidth() / 2) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) ((getMeasuredHeight() + this.thumbSize) / 2), this.outerPaint);
            canvas.drawRect((float) measuredWidth, (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), this.outerPaint);
        }
        canvas.drawCircle((float) ((this.thumbSize / 2) + measuredWidth), (float) ((this.thumbSize / 2) + measuredHeight), (float) (this.thumbSize / 2), this.outerPaint);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float f = 0.0f;
        if (motionEvent == null) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float measuredWidth = (float) ((int) (((float) (getMeasuredWidth() - this.thumbSize)) * this.progress));
        if (motionEvent.getAction() == 0) {
            int measuredHeight = (getMeasuredHeight() - this.thumbSize) / 2;
            if (measuredWidth - ((float) measuredHeight) <= x) {
                if (x <= ((float) measuredHeight) + (((float) this.thumbSize) + measuredWidth) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                    this.pressed = true;
                    this.thumbDX = (int) (x - measuredWidth);
                    getParent().requestDisallowInterceptTouchEvent(true);
                    invalidate();
                    return true;
                }
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.pressed) {
                this.pressed = false;
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 2 && this.pressed) {
            float f2 = (float) ((int) (x - ((float) this.thumbDX)));
            if (f2 >= 0.0f) {
                f = f2 > ((float) (getMeasuredWidth() - this.thumbSize)) ? (float) (getMeasuredWidth() - this.thumbSize) : f2;
            }
            this.progress = f / ((float) (getMeasuredWidth() - this.thumbSize));
            if (this.delegate != null) {
                this.delegate.onProgressChanged();
            }
            invalidate();
            return true;
        }
        return false;
    }

    public void setDelegate(PhotoEditorSeekBarDelegate photoEditorSeekBarDelegate) {
        this.delegate = photoEditorSeekBarDelegate;
    }

    public void setMinMax(int i, int i2) {
        this.minValue = i;
        this.maxValue = i2;
    }

    public void setProgress(int i) {
        setProgress(i, true);
    }

    public void setProgress(int i, boolean z) {
        if (i < this.minValue) {
            i = this.minValue;
        } else if (i > this.maxValue) {
            i = this.maxValue;
        }
        this.progress = ((float) (i - this.minValue)) / ((float) (this.maxValue - this.minValue));
        invalidate();
        if (z && this.delegate != null) {
            this.delegate.onProgressChanged();
        }
    }
}

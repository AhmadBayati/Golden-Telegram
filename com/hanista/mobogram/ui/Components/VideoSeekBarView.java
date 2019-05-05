package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

public class VideoSeekBarView extends View {
    private SeekBarDelegate delegate;
    private Paint paint;
    private Paint paint2;
    private boolean pressed;
    private float progress;
    private int thumbDX;
    private int thumbHeight;
    private int thumbWidth;

    public interface SeekBarDelegate {
        void onSeekBarDrag(float f);
    }

    public VideoSeekBarView(Context context) {
        super(context);
        this.paint = new Paint();
        this.paint2 = new Paint(1);
        this.thumbWidth = AndroidUtilities.dp(12.0f);
        this.thumbHeight = AndroidUtilities.dp(12.0f);
        this.thumbDX = 0;
        this.progress = 0.0f;
        this.pressed = false;
        this.paint.setColor(-10724260);
        this.paint2.setColor(-1);
    }

    public float getProgress() {
        return this.progress;
    }

    protected void onDraw(Canvas canvas) {
        int measuredHeight = (getMeasuredHeight() - this.thumbHeight) / 2;
        int measuredWidth = (int) (((float) (getMeasuredWidth() - this.thumbWidth)) * this.progress);
        canvas.drawRect((float) (this.thumbWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), (float) (getMeasuredWidth() - (this.thumbWidth / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), this.paint);
        canvas.drawCircle((float) ((this.thumbWidth / 2) + measuredWidth), (float) ((this.thumbHeight / 2) + measuredHeight), (float) (this.thumbWidth / 2), this.paint2);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float f = 0.0f;
        if (motionEvent == null) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float measuredWidth = (float) ((int) (((float) (getMeasuredWidth() - this.thumbWidth)) * this.progress));
        if (motionEvent.getAction() == 0) {
            int measuredHeight = (getMeasuredHeight() - this.thumbWidth) / 2;
            if (measuredWidth - ((float) measuredHeight) <= x) {
                if (x <= ((float) measuredHeight) + (((float) this.thumbWidth) + measuredWidth) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                    this.pressed = true;
                    this.thumbDX = (int) (x - measuredWidth);
                    getParent().requestDisallowInterceptTouchEvent(true);
                    invalidate();
                    return true;
                }
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.pressed) {
                if (motionEvent.getAction() == 1 && this.delegate != null) {
                    this.delegate.onSeekBarDrag(measuredWidth / ((float) (getMeasuredWidth() - this.thumbWidth)));
                }
                this.pressed = false;
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 2 && this.pressed) {
            float f2 = (float) ((int) (x - ((float) this.thumbDX)));
            if (f2 >= 0.0f) {
                f = f2 > ((float) (getMeasuredWidth() - this.thumbWidth)) ? (float) (getMeasuredWidth() - this.thumbWidth) : f2;
            }
            this.progress = f / ((float) (getMeasuredWidth() - this.thumbWidth));
            invalidate();
            return true;
        }
        return false;
    }

    public void setDelegate(SeekBarDelegate seekBarDelegate) {
        this.delegate = seekBarDelegate;
    }

    public void setProgress(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        this.progress = f;
        invalidate();
    }
}

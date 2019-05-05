package com.hanista.mobogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

public class ShutterButton extends View {
    private static final int LONG_PRESS_TIME = 220;
    private ShutterButtonDelegate delegate;
    private DecelerateInterpolator interpolator;
    private long lastUpdateTime;
    private Runnable longPressed;
    private boolean pressed;
    private Paint redPaint;
    private float redProgress;
    private Drawable shadowDrawable;
    private State state;
    private long totalTime;
    private Paint whitePaint;

    public interface ShutterButtonDelegate {
        void shutterCancel();

        void shutterLongPressed();

        void shutterReleased();
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ShutterButton.1 */
    class C14681 implements Runnable {
        C14681() {
        }

        public void run() {
            if (ShutterButton.this.delegate != null) {
                ShutterButton.this.delegate.shutterLongPressed();
            }
        }
    }

    public enum State {
        DEFAULT,
        RECORDING
    }

    public ShutterButton(Context context) {
        super(context);
        this.interpolator = new DecelerateInterpolator();
        this.longPressed = new C14681();
        this.shadowDrawable = getResources().getDrawable(C0338R.drawable.camera_btn);
        this.whitePaint = new Paint(1);
        this.whitePaint.setStyle(Style.FILL);
        this.whitePaint.setColor(-1);
        this.redPaint = new Paint(1);
        this.redPaint.setStyle(Style.FILL);
        this.redPaint.setColor(-3324089);
        this.state = State.DEFAULT;
    }

    private void setHighlighted(boolean z) {
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr;
        if (z) {
            animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.06f});
            animatorArr[1] = ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.06f});
            animatorSet.playTogether(animatorArr);
        } else {
            animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "scaleX", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            animatorArr[1] = ObjectAnimator.ofFloat(this, "scaleY", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
            animatorSet.playTogether(animatorArr);
            animatorSet.setStartDelay(40);
        }
        animatorSet.setDuration(120);
        animatorSet.setInterpolator(this.interpolator);
        animatorSet.start();
    }

    public ShutterButtonDelegate getDelegate() {
        return this.delegate;
    }

    public State getState() {
        return this.state;
    }

    protected void onDraw(Canvas canvas) {
        long j = 17;
        int measuredWidth = getMeasuredWidth() / 2;
        int measuredHeight = getMeasuredHeight() / 2;
        this.shadowDrawable.setBounds(measuredWidth - AndroidUtilities.dp(36.0f), measuredHeight - AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f) + measuredWidth, AndroidUtilities.dp(36.0f) + measuredHeight);
        this.shadowDrawable.draw(canvas);
        if (this.pressed || getScaleX() != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            float scaleX = (getScaleX() - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) / 0.06f;
            this.whitePaint.setAlpha((int) (255.0f * scaleX));
            canvas.drawCircle((float) measuredWidth, (float) measuredHeight, (float) AndroidUtilities.dp(26.0f), this.whitePaint);
            if (this.state == State.RECORDING) {
                if (this.redProgress != DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    long abs = Math.abs(System.currentTimeMillis() - this.lastUpdateTime);
                    if (abs <= 17) {
                        j = abs;
                    }
                    this.totalTime = j + this.totalTime;
                    if (this.totalTime > 120) {
                        this.totalTime = 120;
                    }
                    this.redProgress = this.interpolator.getInterpolation(((float) this.totalTime) / BitmapDescriptorFactory.HUE_GREEN);
                    invalidate();
                }
                canvas.drawCircle((float) measuredWidth, (float) measuredHeight, (((float) AndroidUtilities.dp(26.0f)) * scaleX) * this.redProgress, this.redPaint);
            } else if (this.redProgress != 0.0f) {
                canvas.drawCircle((float) measuredWidth, (float) measuredHeight, ((float) AndroidUtilities.dp(26.0f)) * scaleX, this.redPaint);
            }
        } else if (this.redProgress != 0.0f) {
            this.redProgress = 0.0f;
        }
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(AndroidUtilities.dp(84.0f), AndroidUtilities.dp(84.0f));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float x2 = motionEvent.getX();
        switch (motionEvent.getAction()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                AndroidUtilities.runOnUIThread(this.longPressed, 220);
                this.pressed = true;
                setHighlighted(true);
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                setHighlighted(false);
                AndroidUtilities.cancelRunOnUIThread(this.longPressed);
                if (x >= 0.0f && x2 >= 0.0f && x <= ((float) getMeasuredWidth()) && x2 <= ((float) getMeasuredHeight())) {
                    this.delegate.shutterReleased();
                    break;
                }
            case VideoPlayer.STATE_PREPARING /*2*/:
                if (x < 0.0f || x2 < 0.0f || x > ((float) getMeasuredWidth()) || x2 > ((float) getMeasuredHeight())) {
                    AndroidUtilities.cancelRunOnUIThread(this.longPressed);
                    if (this.state == State.RECORDING) {
                        setHighlighted(false);
                        this.delegate.shutterCancel();
                        setState(State.DEFAULT, true);
                        break;
                    }
                }
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                setHighlighted(false);
                this.pressed = false;
                break;
        }
        return true;
    }

    public void setDelegate(ShutterButtonDelegate shutterButtonDelegate) {
        this.delegate = shutterButtonDelegate;
    }

    public void setScaleX(float f) {
        super.setScaleX(f);
        invalidate();
    }

    public void setState(State state, boolean z) {
        if (this.state != state) {
            this.state = state;
            if (z) {
                this.lastUpdateTime = System.currentTimeMillis();
                this.totalTime = 0;
                if (this.state != State.RECORDING) {
                    this.redProgress = 0.0f;
                }
            } else if (this.state == State.RECORDING) {
                this.redProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            } else {
                this.redProgress = 0.0f;
            }
            invalidate();
        }
    }
}

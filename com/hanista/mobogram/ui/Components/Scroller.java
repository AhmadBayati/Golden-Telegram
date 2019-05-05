package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

public class Scroller {
    private static float DECELERATION_RATE = 0.0f;
    private static final int DEFAULT_DURATION = 250;
    private static float END_TENSION = 0.0f;
    private static final int FLING_MODE = 1;
    private static final int NB_SAMPLES = 100;
    private static final int SCROLL_MODE = 0;
    private static final float[] SPLINE;
    private static float START_TENSION;
    private static float sViscousFluidNormalize;
    private static float sViscousFluidScale;
    private int mCurrX;
    private int mCurrY;
    private float mDeceleration;
    private float mDeltaX;
    private float mDeltaY;
    private int mDuration;
    private float mDurationReciprocal;
    private int mFinalX;
    private int mFinalY;
    private boolean mFinished;
    private boolean mFlywheel;
    private Interpolator mInterpolator;
    private int mMaxX;
    private int mMaxY;
    private int mMinX;
    private int mMinY;
    private int mMode;
    private final float mPpi;
    private long mStartTime;
    private int mStartX;
    private int mStartY;
    private float mVelocity;

    static {
        DECELERATION_RATE = (float) (Math.log(0.75d) / Math.log(0.9d));
        START_TENSION = 0.4f;
        END_TENSION = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - START_TENSION;
        SPLINE = new float[101];
        float f = 0.0f;
        int i = 0;
        while (i <= NB_SAMPLES) {
            float f2;
            float f3 = ((float) i) / 100.0f;
            float f4 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            float f5 = f;
            while (true) {
                f = ((f4 - f5) / 2.0f) + f5;
                f2 = (3.0f * f) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f);
                float f6 = ((((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f) * START_TENSION) + (END_TENSION * f)) * f2) + ((f * f) * f);
                if (((double) Math.abs(f6 - f3)) < 1.0E-5d) {
                    break;
                } else if (f6 > f3) {
                    f4 = f;
                } else {
                    f5 = f;
                }
            }
            SPLINE[i] = ((f * f) * f) + f2;
            i += FLING_MODE;
            f = f5;
        }
        SPLINE[NB_SAMPLES] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        sViscousFluidScale = 8.0f;
        sViscousFluidNormalize = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        sViscousFluidNormalize = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / viscousFluid(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public Scroller(Context context) {
        this(context, null);
    }

    public Scroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public Scroller(Context context, Interpolator interpolator, boolean z) {
        this.mFinished = true;
        this.mInterpolator = interpolator;
        this.mPpi = context.getResources().getDisplayMetrics().density * 160.0f;
        this.mDeceleration = computeDeceleration(ViewConfiguration.getScrollFriction());
        this.mFlywheel = z;
    }

    private float computeDeceleration(float f) {
        return (386.0878f * this.mPpi) * f;
    }

    static float viscousFluid(float f) {
        float f2 = sViscousFluidScale * f;
        return (f2 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ? f2 - (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - ((float) Math.exp((double) (-f2)))) : ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - ((float) Math.exp((double) (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f2)))) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - 0.36787945f)) + 0.36787945f) * sViscousFluidNormalize;
    }

    public void abortAnimation() {
        this.mCurrX = this.mFinalX;
        this.mCurrY = this.mFinalY;
        this.mFinished = true;
    }

    public boolean computeScrollOffset() {
        if (this.mFinished) {
            return false;
        }
        int currentAnimationTimeMillis = (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
        if (currentAnimationTimeMillis < this.mDuration) {
            float f;
            switch (this.mMode) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    f = ((float) currentAnimationTimeMillis) * this.mDurationReciprocal;
                    f = this.mInterpolator == null ? viscousFluid(f) : this.mInterpolator.getInterpolation(f);
                    this.mCurrX = this.mStartX + Math.round(this.mDeltaX * f);
                    this.mCurrY = Math.round(f * this.mDeltaY) + this.mStartY;
                    break;
                case FLING_MODE /*1*/:
                    f = ((float) currentAnimationTimeMillis) / ((float) this.mDuration);
                    int i = (int) (100.0f * f);
                    float f2 = ((float) i) / 100.0f;
                    float f3 = ((float) (i + FLING_MODE)) / 100.0f;
                    float f4 = SPLINE[i];
                    f = (((f - f2) / (f3 - f2)) * (SPLINE[i + FLING_MODE] - f4)) + f4;
                    this.mCurrX = this.mStartX + Math.round(((float) (this.mFinalX - this.mStartX)) * f);
                    this.mCurrX = Math.min(this.mCurrX, this.mMaxX);
                    this.mCurrX = Math.max(this.mCurrX, this.mMinX);
                    this.mCurrY = Math.round(f * ((float) (this.mFinalY - this.mStartY))) + this.mStartY;
                    this.mCurrY = Math.min(this.mCurrY, this.mMaxY);
                    this.mCurrY = Math.max(this.mCurrY, this.mMinY);
                    if (this.mCurrX == this.mFinalX && this.mCurrY == this.mFinalY) {
                        this.mFinished = true;
                        break;
                    }
            }
        }
        this.mCurrX = this.mFinalX;
        this.mCurrY = this.mFinalY;
        this.mFinished = true;
        return true;
    }

    public void extendDuration(int i) {
        this.mDuration = timePassed() + i;
        this.mDurationReciprocal = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) this.mDuration);
        this.mFinished = false;
    }

    public void fling(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        float currVelocity;
        float f;
        float f2;
        if (this.mFlywheel && !this.mFinished) {
            currVelocity = getCurrVelocity();
            f = (float) (this.mFinalX - this.mStartX);
            f2 = (float) (this.mFinalY - this.mStartY);
            float sqrt = (float) Math.sqrt((double) ((f * f) + (f2 * f2)));
            f = (f / sqrt) * currVelocity;
            currVelocity *= f2 / sqrt;
            if (Math.signum((float) i3) == Math.signum(f) && Math.signum((float) i4) == Math.signum(currVelocity)) {
                i3 = (int) (f + ((float) i3));
                i4 = (int) (currVelocity + ((float) i4));
            }
        }
        this.mMode = FLING_MODE;
        this.mFinished = false;
        f2 = (float) Math.sqrt((double) ((i3 * i3) + (i4 * i4)));
        this.mVelocity = f2;
        double log = Math.log((double) ((START_TENSION * f2) / 800.0f));
        this.mDuration = (int) (1000.0d * Math.exp(log / (((double) DECELERATION_RATE) - 1.0d)));
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = i;
        this.mStartY = i2;
        f = f2 == 0.0f ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : ((float) i3) / f2;
        currVelocity = f2 == 0.0f ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : ((float) i4) / f2;
        int exp = (int) (((double) 1145569280) * Math.exp(log * (((double) DECELERATION_RATE) / (((double) DECELERATION_RATE) - 1.0d))));
        this.mMinX = i5;
        this.mMaxX = i6;
        this.mMinY = i7;
        this.mMaxY = i8;
        this.mFinalX = Math.round(f * ((float) exp)) + i;
        this.mFinalX = Math.min(this.mFinalX, this.mMaxX);
        this.mFinalX = Math.max(this.mFinalX, this.mMinX);
        this.mFinalY = Math.round(currVelocity * ((float) exp)) + i2;
        this.mFinalY = Math.min(this.mFinalY, this.mMaxY);
        this.mFinalY = Math.max(this.mFinalY, this.mMinY);
    }

    public final void forceFinished(boolean z) {
        this.mFinished = z;
    }

    public float getCurrVelocity() {
        return this.mVelocity - ((this.mDeceleration * ((float) timePassed())) / 2000.0f);
    }

    public final int getCurrX() {
        return this.mCurrX;
    }

    public final int getCurrY() {
        return this.mCurrY;
    }

    public final int getDuration() {
        return this.mDuration;
    }

    public final int getFinalX() {
        return this.mFinalX;
    }

    public final int getFinalY() {
        return this.mFinalY;
    }

    public final int getStartX() {
        return this.mStartX;
    }

    public final int getStartY() {
        return this.mStartY;
    }

    public final boolean isFinished() {
        return this.mFinished;
    }

    public boolean isScrollingInDirection(float f, float f2) {
        return !this.mFinished && Math.signum(f) == Math.signum((float) (this.mFinalX - this.mStartX)) && Math.signum(f2) == Math.signum((float) (this.mFinalY - this.mStartY));
    }

    public void setFinalX(int i) {
        this.mFinalX = i;
        this.mDeltaX = (float) (this.mFinalX - this.mStartX);
        this.mFinished = false;
    }

    public void setFinalY(int i) {
        this.mFinalY = i;
        this.mDeltaY = (float) (this.mFinalY - this.mStartY);
        this.mFinished = false;
    }

    public final void setFriction(float f) {
        this.mDeceleration = computeDeceleration(f);
    }

    public void startScroll(int i, int i2, int i3, int i4) {
        startScroll(i, i2, i3, i4, DEFAULT_DURATION);
    }

    public void startScroll(int i, int i2, int i3, int i4, int i5) {
        this.mMode = 0;
        this.mFinished = false;
        this.mDuration = i5;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = i;
        this.mStartY = i2;
        this.mFinalX = i + i3;
        this.mFinalY = i2 + i4;
        this.mDeltaX = (float) i3;
        this.mDeltaY = (float) i4;
        this.mDurationReciprocal = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) this.mDuration);
    }

    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
    }
}

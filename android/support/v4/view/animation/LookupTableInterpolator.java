package android.support.v4.view.animation;

import android.view.animation.Interpolator;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

abstract class LookupTableInterpolator implements Interpolator {
    private final float mStepSize;
    private final float[] mValues;

    public LookupTableInterpolator(float[] fArr) {
        this.mValues = fArr;
        this.mStepSize = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) (this.mValues.length - 1));
    }

    public float getInterpolation(float f) {
        if (f >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            return DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
        if (f <= 0.0f) {
            return 0.0f;
        }
        int min = Math.min((int) (((float) (this.mValues.length - 1)) * f), this.mValues.length - 2);
        float f2 = (f - (((float) min) * this.mStepSize)) / this.mStepSize;
        return ((this.mValues[min + 1] - this.mValues[min]) * f2) + this.mValues[min];
    }
}

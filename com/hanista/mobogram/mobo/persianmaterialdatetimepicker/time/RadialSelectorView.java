package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time.c */
public class RadialSelectorView extends View {
    private final Paint f2314a;
    private boolean f2315b;
    private boolean f2316c;
    private float f2317d;
    private float f2318e;
    private float f2319f;
    private float f2320g;
    private float f2321h;
    private float f2322i;
    private float f2323j;
    private boolean f2324k;
    private boolean f2325l;
    private int f2326m;
    private int f2327n;
    private int f2328o;
    private int f2329p;
    private float f2330q;
    private float f2331r;
    private int f2332s;
    private int f2333t;
    private RadialSelectorView f2334u;
    private int f2335v;
    private double f2336w;
    private boolean f2337x;

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time.c.a */
    private class RadialSelectorView implements AnimatorUpdateListener {
        final /* synthetic */ RadialSelectorView f2313a;

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            this.f2313a.invalidate();
        }
    }

    public RadialSelectorView(Context context) {
        super(context);
        this.f2314a = new Paint();
        this.f2315b = false;
    }

    public int m2184a(float f, float f2, boolean z, Boolean[] boolArr) {
        boolean z2 = true;
        if (!this.f2316c) {
            return -1;
        }
        double sqrt = Math.sqrt((double) (((f2 - ((float) this.f2328o)) * (f2 - ((float) this.f2328o))) + ((f - ((float) this.f2327n)) * (f - ((float) this.f2327n)))));
        if (this.f2325l) {
            if (z) {
                boolArr[0] = Boolean.valueOf(((int) Math.abs(sqrt - ((double) ((int) (((float) this.f2329p) * this.f2319f))))) <= ((int) Math.abs(sqrt - ((double) ((int) (((float) this.f2329p) * this.f2320g))))));
            } else {
                int i = ((int) (((float) this.f2329p) * this.f2320g)) + this.f2333t;
                int i2 = (int) (((float) this.f2329p) * ((this.f2320g + this.f2319f) / 2.0f));
                if (sqrt >= ((double) (((int) (((float) this.f2329p) * this.f2319f)) - this.f2333t)) && sqrt <= ((double) i2)) {
                    boolArr[0] = Boolean.valueOf(true);
                } else if (sqrt > ((double) i) || sqrt < ((double) i2)) {
                    return -1;
                } else {
                    boolArr[0] = Boolean.valueOf(false);
                }
            }
        } else if (!z && ((int) Math.abs(sqrt - ((double) this.f2332s))) > ((int) (((float) this.f2329p) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.f2321h)))) {
            return -1;
        }
        int asin = (int) ((Math.asin(((double) Math.abs(f2 - ((float) this.f2328o))) / sqrt) * 180.0d) / 3.141592653589793d);
        boolean z3 = f > ((float) this.f2327n);
        if (f2 >= ((float) this.f2328o)) {
            z2 = false;
        }
        return (z3 && z2) ? 90 - asin : (!z3 || z2) ? (z3 || z2) ? (z3 || !z2) ? asin : asin + 270 : 270 - asin : asin + 90;
    }

    public void m2185a(int i, boolean z, boolean z2) {
        this.f2335v = i;
        this.f2336w = (((double) i) * 3.141592653589793d) / 180.0d;
        this.f2337x = z2;
        if (!this.f2325l) {
            return;
        }
        if (z) {
            this.f2321h = this.f2319f;
        } else {
            this.f2321h = this.f2320g;
        }
    }

    public ObjectAnimator getDisappearAnimator() {
        if (this.f2315b && this.f2316c) {
            Keyframe ofFloat = Keyframe.ofFloat(0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            Keyframe ofFloat2 = Keyframe.ofFloat(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD, this.f2330q);
            Keyframe ofFloat3 = Keyframe.ofFloat(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f2331r);
            PropertyValuesHolder ofKeyframe = PropertyValuesHolder.ofKeyframe("animationRadiusMultiplier", new Keyframe[]{ofFloat, ofFloat2, ofFloat3});
            ofFloat = Keyframe.ofFloat(0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            ofFloat3 = Keyframe.ofFloat(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f);
            PropertyValuesHolder ofKeyframe2 = PropertyValuesHolder.ofKeyframe("alpha", new Keyframe[]{ofFloat, ofFloat3});
            ObjectAnimator duration = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{ofKeyframe, ofKeyframe2}).setDuration((long) 500);
            duration.addUpdateListener(this.f2334u);
            return duration;
        }
        Log.e("RadialSelectorView", "RadialSelectorView was not ready for animation.");
        return null;
    }

    public ObjectAnimator getReappearAnimator() {
        if (this.f2315b && this.f2316c) {
            int i = (int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT + 0.25f) * ((float) 500));
            float f = (((float) 500) * 0.25f) / ((float) i);
            float f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f));
            Keyframe ofFloat = Keyframe.ofFloat(0.0f, this.f2331r);
            Keyframe ofFloat2 = Keyframe.ofFloat(f, this.f2331r);
            Keyframe ofFloat3 = Keyframe.ofFloat(f2, this.f2330q);
            Keyframe ofFloat4 = Keyframe.ofFloat(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            PropertyValuesHolder ofKeyframe = PropertyValuesHolder.ofKeyframe("animationRadiusMultiplier", new Keyframe[]{ofFloat, ofFloat2, ofFloat3, ofFloat4});
            ofFloat = Keyframe.ofFloat(0.0f, 0.0f);
            Keyframe ofFloat5 = Keyframe.ofFloat(f, 0.0f);
            ofFloat2 = Keyframe.ofFloat(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            PropertyValuesHolder ofKeyframe2 = PropertyValuesHolder.ofKeyframe("alpha", new Keyframe[]{ofFloat, ofFloat5, ofFloat2});
            ObjectAnimator duration = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{ofKeyframe, ofKeyframe2}).setDuration((long) i);
            duration.addUpdateListener(this.f2334u);
            return duration;
        }
        Log.e("RadialSelectorView", "RadialSelectorView was not ready for animation.");
        return null;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void onDraw(Canvas canvas) {
        int i = 1;
        if (getWidth() != 0 && this.f2315b) {
            int i2;
            if (!this.f2316c) {
                this.f2327n = getWidth() / 2;
                this.f2328o = getHeight() / 2;
                this.f2329p = (int) (((float) Math.min(this.f2327n, this.f2328o)) * this.f2317d);
                if (!this.f2324k) {
                    this.f2328o = (int) (((double) this.f2328o) - (((double) ((int) (((float) this.f2329p) * this.f2318e))) * 0.75d));
                }
                this.f2333t = (int) (((float) this.f2329p) * this.f2322i);
                this.f2316c = true;
            }
            this.f2332s = (int) ((((float) this.f2329p) * this.f2321h) * this.f2323j);
            int sin = ((int) (((double) this.f2332s) * Math.sin(this.f2336w))) + this.f2327n;
            int cos = this.f2328o - ((int) (((double) this.f2332s) * Math.cos(this.f2336w)));
            this.f2314a.setAlpha(this.f2326m);
            canvas.drawCircle((float) sin, (float) cos, (float) this.f2333t, this.f2314a);
            boolean z = this.f2337x;
            if (this.f2335v % 30 == 0) {
                i = 0;
            }
            if ((i | z) != 0) {
                this.f2314a.setAlpha(NalUnitUtil.EXTENDED_SAR);
                canvas.drawCircle((float) sin, (float) cos, (float) ((this.f2333t * 2) / 7), this.f2314a);
                i2 = sin;
            } else {
                cos = this.f2332s - this.f2333t;
                sin = this.f2327n + ((int) (((double) cos) * Math.sin(this.f2336w)));
                cos = this.f2328o - ((int) (((double) cos) * Math.cos(this.f2336w)));
                i2 = sin;
            }
            this.f2314a.setAlpha(NalUnitUtil.EXTENDED_SAR);
            this.f2314a.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            canvas.drawLine((float) this.f2327n, (float) this.f2328o, (float) i2, (float) cos, this.f2314a);
        }
    }

    public void setAnimationRadiusMultiplier(float f) {
        this.f2323j = f;
    }
}

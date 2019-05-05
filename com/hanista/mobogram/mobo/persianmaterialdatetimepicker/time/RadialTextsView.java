package com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.LanguageUtils;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time.d */
public class RadialTextsView extends View {
    private float[] f2339A;
    private float[] f2340B;
    private float[] f2341C;
    private float f2342D;
    private float f2343E;
    private float f2344F;
    private RadialTextsView f2345G;
    ObjectAnimator f2346a;
    ObjectAnimator f2347b;
    private final Paint f2348c;
    private final Paint f2349d;
    private boolean f2350e;
    private boolean f2351f;
    private int f2352g;
    private Typeface f2353h;
    private Typeface f2354i;
    private String[] f2355j;
    private String[] f2356k;
    private boolean f2357l;
    private boolean f2358m;
    private float f2359n;
    private float f2360o;
    private float f2361p;
    private float f2362q;
    private float f2363r;
    private float f2364s;
    private int f2365t;
    private int f2366u;
    private float f2367v;
    private boolean f2368w;
    private float f2369x;
    private float f2370y;
    private float[] f2371z;

    /* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.time.d.a */
    private class RadialTextsView implements AnimatorUpdateListener {
        final /* synthetic */ RadialTextsView f2338a;

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            this.f2338a.invalidate();
        }
    }

    public RadialTextsView(Context context) {
        super(context);
        this.f2348c = new Paint();
        this.f2349d = new Paint();
        this.f2352g = -1;
        this.f2351f = false;
    }

    private void m2186a() {
        Keyframe ofFloat = Keyframe.ofFloat(0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        Keyframe ofFloat2 = Keyframe.ofFloat(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD, this.f2343E);
        Keyframe ofFloat3 = Keyframe.ofFloat(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f2344F);
        PropertyValuesHolder ofKeyframe = PropertyValuesHolder.ofKeyframe("animationRadiusMultiplier", new Keyframe[]{ofFloat, ofFloat2, ofFloat3});
        ofFloat2 = Keyframe.ofFloat(0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        ofFloat3 = Keyframe.ofFloat(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f);
        PropertyValuesHolder ofKeyframe2 = PropertyValuesHolder.ofKeyframe("alpha", new Keyframe[]{ofFloat2, ofFloat3});
        this.f2346a = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{ofKeyframe, ofKeyframe2}).setDuration((long) 500);
        this.f2346a.addUpdateListener(this.f2345G);
        int i = (int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT + 0.25f) * ((float) 500));
        float f = (((float) 500) * 0.25f) / ((float) i);
        float f2 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f));
        ofFloat = Keyframe.ofFloat(0.0f, this.f2344F);
        ofFloat3 = Keyframe.ofFloat(f, this.f2344F);
        Keyframe ofFloat4 = Keyframe.ofFloat(f2, this.f2343E);
        Keyframe ofFloat5 = Keyframe.ofFloat(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        PropertyValuesHolder ofKeyframe3 = PropertyValuesHolder.ofKeyframe("animationRadiusMultiplier", new Keyframe[]{ofFloat, ofFloat3, ofFloat4, ofFloat5});
        ofFloat = Keyframe.ofFloat(0.0f, 0.0f);
        Keyframe ofFloat6 = Keyframe.ofFloat(f, 0.0f);
        ofFloat3 = Keyframe.ofFloat(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        PropertyValuesHolder ofKeyframe4 = PropertyValuesHolder.ofKeyframe("alpha", new Keyframe[]{ofFloat, ofFloat6, ofFloat3});
        this.f2347b = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{ofKeyframe3, ofKeyframe4}).setDuration((long) i);
        this.f2347b.addUpdateListener(this.f2345G);
    }

    private void m2187a(float f, float f2, float f3, float f4, float[] fArr, float[] fArr2) {
        float sqrt = (((float) Math.sqrt(3.0d)) * f) / 2.0f;
        float f5 = f / 2.0f;
        this.f2348c.setTextSize(f4);
        this.f2349d.setTextSize(f4);
        float descent = f3 - ((this.f2348c.descent() + this.f2348c.ascent()) / 2.0f);
        fArr[0] = descent - f;
        fArr2[0] = f2 - f;
        fArr[1] = descent - sqrt;
        fArr2[1] = f2 - sqrt;
        fArr[2] = descent - f5;
        fArr2[2] = f2 - f5;
        fArr[3] = descent;
        fArr2[3] = f2;
        fArr[4] = descent + f5;
        fArr2[4] = f5 + f2;
        fArr[5] = descent + sqrt;
        fArr2[5] = sqrt + f2;
        fArr[6] = descent + f;
        fArr2[6] = f2 + f;
    }

    private void m2188a(Canvas canvas, float f, Typeface typeface, String[] strArr, float[] fArr, float[] fArr2) {
        this.f2348c.setTextSize(f);
        this.f2348c.setTypeface(typeface);
        LanguageUtils.m2045a(strArr);
        canvas.drawText(strArr[0], fArr[3], fArr2[0], Integer.parseInt(strArr[0]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[1], fArr[4], fArr2[1], Integer.parseInt(strArr[1]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[2], fArr[5], fArr2[2], Integer.parseInt(strArr[2]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[3], fArr[6], fArr2[3], Integer.parseInt(strArr[3]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[4], fArr[5], fArr2[4], Integer.parseInt(strArr[4]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[5], fArr[4], fArr2[5], Integer.parseInt(strArr[5]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[6], fArr[3], fArr2[6], Integer.parseInt(strArr[6]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[7], fArr[2], fArr2[5], Integer.parseInt(strArr[7]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[8], fArr[1], fArr2[4], Integer.parseInt(strArr[8]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[9], fArr[0], fArr2[3], Integer.parseInt(strArr[9]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[10], fArr[1], fArr2[2], Integer.parseInt(strArr[10]) == this.f2352g ? this.f2349d : this.f2348c);
        canvas.drawText(strArr[11], fArr[2], fArr2[1], Integer.parseInt(strArr[11]) == this.f2352g ? this.f2349d : this.f2348c);
    }

    public ObjectAnimator getDisappearAnimator() {
        if (this.f2351f && this.f2350e && this.f2346a != null) {
            return this.f2346a;
        }
        Log.e("RadialTextsView", "RadialTextView was not ready for animation.");
        return null;
    }

    public ObjectAnimator getReappearAnimator() {
        if (this.f2351f && this.f2350e && this.f2347b != null) {
            return this.f2347b;
        }
        Log.e("RadialTextsView", "RadialTextView was not ready for animation.");
        return null;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void onDraw(Canvas canvas) {
        if (getWidth() != 0 && this.f2351f) {
            if (!this.f2350e) {
                this.f2365t = getWidth() / 2;
                this.f2366u = getHeight() / 2;
                this.f2367v = ((float) Math.min(this.f2365t, this.f2366u)) * this.f2359n;
                if (!this.f2357l) {
                    this.f2366u = (int) (((double) this.f2366u) - (((double) (this.f2367v * this.f2360o)) * 0.75d));
                }
                this.f2369x = this.f2367v * this.f2363r;
                if (this.f2358m) {
                    this.f2370y = this.f2367v * this.f2364s;
                }
                m2186a();
                this.f2368w = true;
                this.f2350e = true;
            }
            if (this.f2368w) {
                m2187a(this.f2342D * (this.f2367v * this.f2361p), (float) this.f2365t, (float) this.f2366u, this.f2369x, this.f2371z, this.f2339A);
                if (this.f2358m) {
                    m2187a(this.f2342D * (this.f2367v * this.f2362q), (float) this.f2365t, (float) this.f2366u, this.f2370y, this.f2340B, this.f2341C);
                }
                this.f2368w = false;
            }
            m2188a(canvas, this.f2369x, this.f2353h, this.f2355j, this.f2339A, this.f2371z);
            if (this.f2358m) {
                m2188a(canvas, this.f2370y, this.f2354i, this.f2356k, this.f2341C, this.f2340B);
            }
        }
    }

    public void setAnimationRadiusMultiplier(float f) {
        this.f2342D = f;
        this.f2368w = true;
    }

    protected void setSelection(int i) {
        this.f2352g = i;
    }
}

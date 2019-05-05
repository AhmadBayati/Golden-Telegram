package com.hanista.mobogram.mobo.p010k.p011a;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

/* renamed from: com.hanista.mobogram.mobo.k.a.a */
class FloatValueAnimatorBuilder {
    final ValueAnimator f1205a;
    FloatValueAnimatorBuilder f1206b;

    /* renamed from: com.hanista.mobogram.mobo.k.a.a.1 */
    class FloatValueAnimatorBuilder implements AnimatorUpdateListener {
        final /* synthetic */ FloatValueAnimatorBuilder f1202a;
        final /* synthetic */ FloatValueAnimatorBuilder f1203b;

        FloatValueAnimatorBuilder(FloatValueAnimatorBuilder floatValueAnimatorBuilder, FloatValueAnimatorBuilder floatValueAnimatorBuilder2) {
            this.f1203b = floatValueAnimatorBuilder;
            this.f1202a = floatValueAnimatorBuilder2;
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            this.f1202a.m1287a(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.a.2 */
    class FloatValueAnimatorBuilder implements AnimatorListener {
        final /* synthetic */ FloatValueAnimatorBuilder f1204a;

        FloatValueAnimatorBuilder(FloatValueAnimatorBuilder floatValueAnimatorBuilder) {
            this.f1204a = floatValueAnimatorBuilder;
        }

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationEnd(Animator animator) {
            this.f1204a.f1206b.m1286a();
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.a.a */
    interface FloatValueAnimatorBuilder {
        void m1286a();
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.a.b */
    interface FloatValueAnimatorBuilder {
        void m1287a(float f);
    }

    protected FloatValueAnimatorBuilder() {
        this(false);
    }

    protected FloatValueAnimatorBuilder(boolean z) {
        if (z) {
            this.f1205a = ValueAnimator.ofFloat(new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f});
        } else {
            this.f1205a = ValueAnimator.ofFloat(new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
        }
    }

    public ValueAnimator m1288a() {
        if (this.f1206b != null) {
            this.f1205a.addListener(new FloatValueAnimatorBuilder(this));
        }
        return this.f1205a;
    }

    public FloatValueAnimatorBuilder m1289a(int i) {
        this.f1205a.setRepeatCount(i);
        return this;
    }

    public FloatValueAnimatorBuilder m1290a(long j) {
        this.f1205a.setStartDelay(j);
        return this;
    }

    public FloatValueAnimatorBuilder m1291a(TimeInterpolator timeInterpolator) {
        this.f1205a.setInterpolator(timeInterpolator);
        return this;
    }

    public FloatValueAnimatorBuilder m1292a(FloatValueAnimatorBuilder floatValueAnimatorBuilder) {
        this.f1206b = floatValueAnimatorBuilder;
        return this;
    }

    public FloatValueAnimatorBuilder m1293a(FloatValueAnimatorBuilder floatValueAnimatorBuilder) {
        this.f1205a.addUpdateListener(new FloatValueAnimatorBuilder(this, floatValueAnimatorBuilder));
        return this;
    }

    public FloatValueAnimatorBuilder m1294b(long j) {
        this.f1205a.setDuration(j);
        return this;
    }
}

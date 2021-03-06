package android.support.v4.animation;

import android.view.View;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import java.util.ArrayList;
import java.util.List;

class GingerbreadAnimatorCompatProvider implements AnimatorProvider {

    private static class GingerbreadFloatValueAnimator implements ValueAnimatorCompat {
        private long mDuration;
        private boolean mEnded;
        private float mFraction;
        List<AnimatorListenerCompat> mListeners;
        private Runnable mLoopRunnable;
        private long mStartTime;
        private boolean mStarted;
        View mTarget;
        List<AnimatorUpdateListenerCompat> mUpdateListeners;

        /* renamed from: android.support.v4.animation.GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.1 */
        class C00001 implements Runnable {
            C00001() {
            }

            public void run() {
                float access$000 = (((float) (GingerbreadFloatValueAnimator.this.getTime() - GingerbreadFloatValueAnimator.this.mStartTime)) * DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) / ((float) GingerbreadFloatValueAnimator.this.mDuration);
                if (access$000 > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT || GingerbreadFloatValueAnimator.this.mTarget.getParent() == null) {
                    access$000 = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                }
                GingerbreadFloatValueAnimator.this.mFraction = access$000;
                GingerbreadFloatValueAnimator.this.notifyUpdateListeners();
                if (GingerbreadFloatValueAnimator.this.mFraction >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    GingerbreadFloatValueAnimator.this.dispatchEnd();
                } else {
                    GingerbreadFloatValueAnimator.this.mTarget.postDelayed(GingerbreadFloatValueAnimator.this.mLoopRunnable, 16);
                }
            }
        }

        public GingerbreadFloatValueAnimator() {
            this.mListeners = new ArrayList();
            this.mUpdateListeners = new ArrayList();
            this.mDuration = 200;
            this.mFraction = 0.0f;
            this.mStarted = false;
            this.mEnded = false;
            this.mLoopRunnable = new C00001();
        }

        private void dispatchCancel() {
            for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                ((AnimatorListenerCompat) this.mListeners.get(size)).onAnimationCancel(this);
            }
        }

        private void dispatchEnd() {
            for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                ((AnimatorListenerCompat) this.mListeners.get(size)).onAnimationEnd(this);
            }
        }

        private void dispatchStart() {
            for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                ((AnimatorListenerCompat) this.mListeners.get(size)).onAnimationStart(this);
            }
        }

        private long getTime() {
            return this.mTarget.getDrawingTime();
        }

        private void notifyUpdateListeners() {
            for (int size = this.mUpdateListeners.size() - 1; size >= 0; size--) {
                ((AnimatorUpdateListenerCompat) this.mUpdateListeners.get(size)).onAnimationUpdate(this);
            }
        }

        public void addListener(AnimatorListenerCompat animatorListenerCompat) {
            this.mListeners.add(animatorListenerCompat);
        }

        public void addUpdateListener(AnimatorUpdateListenerCompat animatorUpdateListenerCompat) {
            this.mUpdateListeners.add(animatorUpdateListenerCompat);
        }

        public void cancel() {
            if (!this.mEnded) {
                this.mEnded = true;
                if (this.mStarted) {
                    dispatchCancel();
                }
                dispatchEnd();
            }
        }

        public float getAnimatedFraction() {
            return this.mFraction;
        }

        public void setDuration(long j) {
            if (!this.mStarted) {
                this.mDuration = j;
            }
        }

        public void setTarget(View view) {
            this.mTarget = view;
        }

        public void start() {
            if (!this.mStarted) {
                this.mStarted = true;
                dispatchStart();
                this.mFraction = 0.0f;
                this.mStartTime = getTime();
                this.mTarget.postDelayed(this.mLoopRunnable, 16);
            }
        }
    }

    GingerbreadAnimatorCompatProvider() {
    }

    public void clearInterpolator(View view) {
    }

    public ValueAnimatorCompat emptyValueAnimator() {
        return new GingerbreadFloatValueAnimator();
    }
}

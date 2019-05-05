package com.hanista.mobogram.ui.ActionBar;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.lang.reflect.Field;
import java.util.HashMap;

public class ActionBarPopupWindow extends PopupWindow {
    private static final OnScrollChangedListener NOP;
    private static final boolean allowAnimation;
    private static DecelerateInterpolator decelerateInterpolator;
    private static final Field superListenerField;
    private boolean animationEnabled;
    private OnScrollChangedListener mSuperScrollListener;
    private ViewTreeObserver mViewTreeObserver;
    private AnimatorSet windowAnimatorSet;

    public interface OnDispatchKeyEventListener {
        void onDispatchKeyEvent(KeyEvent keyEvent);
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarPopupWindow.1 */
    static class C09701 implements OnScrollChangedListener {
        C09701() {
        }

        public void onScrollChanged() {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarPopupWindow.2 */
    class C09712 implements AnimatorListener {
        C09712() {
        }

        public void onAnimationCancel(Animator animator) {
            onAnimationEnd(animator);
        }

        public void onAnimationEnd(Animator animator) {
            ActionBarPopupWindow.this.windowAnimatorSet = null;
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarPopupWindow.3 */
    class C09723 implements AnimatorListener {
        C09723() {
        }

        public void onAnimationCancel(Animator animator) {
            onAnimationEnd(animator);
        }

        public void onAnimationEnd(Animator animator) {
            ActionBarPopupWindow.this.windowAnimatorSet = null;
            ActionBarPopupWindow.this.setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception e) {
            }
            ActionBarPopupWindow.this.unregisterListener();
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }
    }

    public static class ActionBarPopupWindowLayout extends FrameLayout {
        protected static Drawable backgroundDrawable;
        private boolean animationEnabled;
        private int backAlpha;
        private float backScaleX;
        private float backScaleY;
        private int lastStartedChild;
        private LinearLayout linearLayout;
        private OnDispatchKeyEventListener mOnDispatchKeyEventListener;
        private HashMap<View, Integer> positions;
        private ScrollView scrollView;
        private boolean showedFromBotton;

        public ActionBarPopupWindowLayout(Context context) {
            super(context);
            this.backScaleX = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.backScaleY = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            this.backAlpha = NalUnitUtil.EXTENDED_SAR;
            this.lastStartedChild = 0;
            this.animationEnabled = ActionBarPopupWindow.allowAnimation;
            this.positions = new HashMap();
            if (backgroundDrawable == null) {
                backgroundDrawable = getResources().getDrawable(C0338R.drawable.popup_fixed);
            }
            setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            setWillNotDraw(false);
            try {
                this.scrollView = new ScrollView(context);
                this.scrollView.setVerticalScrollBarEnabled(false);
                addView(this.scrollView, LayoutHelper.createFrame(-2, -2.0f));
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
            }
            this.linearLayout = new LinearLayout(context);
            this.linearLayout.setOrientation(1);
            if (this.scrollView != null) {
                this.scrollView.addView(this.linearLayout, new LayoutParams(-2, -2));
            } else {
                addView(this.linearLayout, LayoutHelper.createFrame(-2, -2.0f));
            }
        }

        private void startChildAnimation(View view) {
            if (this.animationEnabled) {
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(view, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT});
                String str = "translationY";
                float[] fArr = new float[2];
                fArr[0] = (float) AndroidUtilities.dp(this.showedFromBotton ? 6.0f : -6.0f);
                fArr[1] = 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, str, fArr);
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(180);
                animatorSet.setInterpolator(ActionBarPopupWindow.decelerateInterpolator);
                animatorSet.start();
            }
        }

        public void addView(View view) {
            this.linearLayout.addView(view);
        }

        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            if (this.mOnDispatchKeyEventListener != null) {
                this.mOnDispatchKeyEventListener.onDispatchKeyEvent(keyEvent);
            }
            return super.dispatchKeyEvent(keyEvent);
        }

        public int getBackAlpha() {
            return this.backAlpha;
        }

        public float getBackScaleX() {
            return this.backScaleX;
        }

        public float getBackScaleY() {
            return this.backScaleY;
        }

        public View getItemAt(int i) {
            return this.linearLayout.getChildAt(i);
        }

        public int getItemsCount() {
            return this.linearLayout.getChildCount();
        }

        protected void onDraw(Canvas canvas) {
            if (backgroundDrawable != null) {
                backgroundDrawable.setAlpha(this.backAlpha);
                if (this.showedFromBotton) {
                    backgroundDrawable.setBounds(0, (int) (((float) getMeasuredHeight()) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.backScaleY)), (int) (((float) getMeasuredWidth()) * this.backScaleX), getMeasuredHeight());
                } else {
                    backgroundDrawable.setBounds(0, 0, (int) (((float) getMeasuredWidth()) * this.backScaleX), (int) (((float) getMeasuredHeight()) * this.backScaleY));
                }
                backgroundDrawable.draw(canvas);
            }
        }

        public void removeInnerViews() {
            this.linearLayout.removeAllViews();
        }

        public void scrollToTop() {
            if (this.scrollView != null) {
                this.scrollView.scrollTo(0, 0);
            }
        }

        public void setAnimationEnabled(boolean z) {
            this.animationEnabled = z;
        }

        public void setBackAlpha(int i) {
            this.backAlpha = i;
        }

        public void setBackScaleX(float f) {
            this.backScaleX = f;
            invalidate();
        }

        public void setBackScaleY(float f) {
            this.backScaleY = f;
            if (this.animationEnabled) {
                int i;
                int itemsCount = getItemsCount();
                int i2 = 0;
                for (i = 0; i < itemsCount; i++) {
                    i2 += getItemAt(i).getVisibility() == 0 ? 1 : 0;
                }
                i = getMeasuredHeight() - AndroidUtilities.dp(16.0f);
                int i3;
                View itemAt;
                Integer num;
                if (this.showedFromBotton) {
                    for (i3 = this.lastStartedChild; i3 >= 0; i3--) {
                        itemAt = getItemAt(i3);
                        if (itemAt.getVisibility() == 0) {
                            num = (Integer) this.positions.get(itemAt);
                            if (num != null && ((float) (i - ((num.intValue() * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(32.0f)))) > ((float) i) * f) {
                                break;
                            }
                            this.lastStartedChild = i3 - 1;
                            startChildAnimation(itemAt);
                        }
                    }
                } else {
                    for (i3 = this.lastStartedChild; i3 < itemsCount; i3++) {
                        itemAt = getItemAt(i3);
                        if (itemAt.getVisibility() == 0) {
                            num = (Integer) this.positions.get(itemAt);
                            if (num != null && ((float) (((num.intValue() + 1) * AndroidUtilities.dp(48.0f)) - AndroidUtilities.dp(24.0f))) > ((float) i) * f) {
                                break;
                            }
                            this.lastStartedChild = i3 + 1;
                            startChildAnimation(itemAt);
                        }
                    }
                }
            }
            invalidate();
        }

        public void setDispatchKeyEventListener(OnDispatchKeyEventListener onDispatchKeyEventListener) {
            this.mOnDispatchKeyEventListener = onDispatchKeyEventListener;
        }

        public void setShowedFromBotton(boolean z) {
            this.showedFromBotton = z;
        }
    }

    static {
        boolean z = true;
        if (VERSION.SDK_INT < 18) {
            z = false;
        }
        allowAnimation = z;
        decelerateInterpolator = new DecelerateInterpolator();
        Field field = null;
        try {
            field = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        superListenerField = field;
        NOP = new C09701();
    }

    public ActionBarPopupWindow() {
        this.animationEnabled = allowAnimation;
        init();
    }

    public ActionBarPopupWindow(int i, int i2) {
        super(i, i2);
        this.animationEnabled = allowAnimation;
        init();
    }

    public ActionBarPopupWindow(Context context) {
        super(context);
        this.animationEnabled = allowAnimation;
        init();
    }

    public ActionBarPopupWindow(View view) {
        super(view);
        this.animationEnabled = allowAnimation;
        init();
    }

    public ActionBarPopupWindow(View view, int i, int i2) {
        super(view, i, i2);
        this.animationEnabled = allowAnimation;
        init();
    }

    public ActionBarPopupWindow(View view, int i, int i2, boolean z) {
        super(view, i, i2, z);
        this.animationEnabled = allowAnimation;
        init();
    }

    private void init() {
        if (superListenerField != null) {
            try {
                this.mSuperScrollListener = (OnScrollChangedListener) superListenerField.get(this);
                superListenerField.set(this, NOP);
            } catch (Exception e) {
                this.mSuperScrollListener = null;
            }
        }
    }

    private void registerListener(View view) {
        if (this.mSuperScrollListener != null) {
            ViewTreeObserver viewTreeObserver = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
            if (viewTreeObserver != this.mViewTreeObserver) {
                if (this.mViewTreeObserver != null && this.mViewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = viewTreeObserver;
                if (viewTreeObserver != null) {
                    viewTreeObserver.addOnScrollChangedListener(this.mSuperScrollListener);
                }
            }
        }
    }

    private void unregisterListener() {
        if (this.mSuperScrollListener != null && this.mViewTreeObserver != null) {
            if (this.mViewTreeObserver.isAlive()) {
                this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
            }
            this.mViewTreeObserver = null;
        }
    }

    public void dismiss() {
        dismiss(true);
    }

    public void dismiss(boolean z) {
        setFocusable(false);
        if (this.animationEnabled && z) {
            if (this.windowAnimatorSet != null) {
                this.windowAnimatorSet.cancel();
            }
            ActionBarPopupWindowLayout actionBarPopupWindowLayout = (ActionBarPopupWindowLayout) getContentView();
            this.windowAnimatorSet = new AnimatorSet();
            AnimatorSet animatorSet = this.windowAnimatorSet;
            Animator[] animatorArr = new Animator[2];
            String str = "translationY";
            float[] fArr = new float[1];
            fArr[0] = (float) AndroidUtilities.dp(actionBarPopupWindowLayout.showedFromBotton ? 5.0f : -5.0f);
            animatorArr[0] = ObjectAnimator.ofFloat(actionBarPopupWindowLayout, str, fArr);
            animatorArr[1] = ObjectAnimator.ofFloat(actionBarPopupWindowLayout, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.windowAnimatorSet.setDuration(150);
            this.windowAnimatorSet.addListener(new C09723());
            this.windowAnimatorSet.start();
            return;
        }
        try {
            super.dismiss();
        } catch (Exception e) {
        }
        unregisterListener();
    }

    public void setAnimationEnabled(boolean z) {
        this.animationEnabled = z;
    }

    public void showAsDropDown(View view, int i, int i2) {
        try {
            super.showAsDropDown(view, i, i2);
            registerListener(view);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void showAtLocation(View view, int i, int i2, int i3) {
        super.showAtLocation(view, i, i2, i3);
        unregisterListener();
    }

    public void startAnimation() {
        if (this.animationEnabled && this.windowAnimatorSet == null) {
            ActionBarPopupWindowLayout actionBarPopupWindowLayout = (ActionBarPopupWindowLayout) getContentView();
            actionBarPopupWindowLayout.setTranslationY(0.0f);
            actionBarPopupWindowLayout.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            actionBarPopupWindowLayout.setPivotX((float) actionBarPopupWindowLayout.getMeasuredWidth());
            actionBarPopupWindowLayout.setPivotY(0.0f);
            int itemsCount = actionBarPopupWindowLayout.getItemsCount();
            actionBarPopupWindowLayout.positions.clear();
            int i = 0;
            for (int i2 = 0; i2 < itemsCount; i2++) {
                View itemAt = actionBarPopupWindowLayout.getItemAt(i2);
                if (itemAt.getVisibility() == 0) {
                    actionBarPopupWindowLayout.positions.put(itemAt, Integer.valueOf(i));
                    itemAt.setAlpha(0.0f);
                    i++;
                }
            }
            if (actionBarPopupWindowLayout.showedFromBotton) {
                actionBarPopupWindowLayout.lastStartedChild = itemsCount - 1;
            } else {
                actionBarPopupWindowLayout.lastStartedChild = 0;
            }
            this.windowAnimatorSet = new AnimatorSet();
            this.windowAnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(actionBarPopupWindowLayout, "backScaleY", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}), ObjectAnimator.ofInt(actionBarPopupWindowLayout, "backAlpha", new int[]{0, NalUnitUtil.EXTENDED_SAR})});
            this.windowAnimatorSet.setDuration((long) ((i * 16) + 150));
            this.windowAnimatorSet.addListener(new C09712());
            this.windowAnimatorSet.start();
        }
    }

    public void update(View view, int i, int i2) {
        super.update(view, i, i2);
        registerListener(view);
    }

    public void update(View view, int i, int i2, int i3, int i4) {
        super.update(view, i, i2, i3, i4);
        registerListener(view);
    }
}

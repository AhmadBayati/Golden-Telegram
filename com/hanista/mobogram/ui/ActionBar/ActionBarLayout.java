package com.hanista.mobogram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v4.view.PointerIconCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.alarmservice.AlarmUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ActionBarLayout extends FrameLayout {
    private static Drawable headerShadowDrawable;
    private static Drawable layerShadowDrawable;
    private static Paint scrimPaint;
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator;
    protected boolean animationInProgress;
    private float animationProgress;
    private Runnable animationRunnable;
    private View backgroundView;
    private boolean beginTrackingSent;
    private LinearLayoutContainer containerView;
    private LinearLayoutContainer containerViewBack;
    private ActionBar currentActionBar;
    private AnimatorSet currentAnimation;
    private DecelerateInterpolator decelerateInterpolator;
    private Runnable delayedOpenAnimationRunnable;
    private ActionBarLayoutDelegate delegate;
    private DrawerLayoutContainer drawerLayoutContainer;
    public ArrayList<BaseFragment> fragmentsStack;
    private boolean inActionMode;
    public float innerTranslationX;
    private long lastFrameTime;
    private boolean maybeStartTracking;
    private Runnable onCloseAnimationEndRunnable;
    private Runnable onOpenAnimationEndRunnable;
    protected Activity parentActivity;
    private boolean removeActionBarExtraHeight;
    protected boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private String titleOverlayText;
    private boolean transitionAnimationInProgress;
    private long transitionAnimationStartTime;
    private boolean useAlphaAnimations;
    private VelocityTracker velocityTracker;
    private Runnable waitingForKeyboardCloseRunnable;

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarLayout.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ BaseFragment val$currentFragment;

        AnonymousClass12(BaseFragment baseFragment) {
            this.val$currentFragment = baseFragment;
        }

        public void run() {
            ActionBarLayout.this.removeFragmentFromStackInternal(this.val$currentFragment);
            ActionBarLayout.this.setVisibility(8);
            if (ActionBarLayout.this.backgroundView != null) {
                ActionBarLayout.this.backgroundView.setVisibility(8);
            }
            if (ActionBarLayout.this.drawerLayoutContainer != null) {
                ActionBarLayout.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarLayout.1 */
    class C09501 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$backAnimation;

        C09501(boolean z) {
            this.val$backAnimation = z;
        }

        public void onAnimationEnd(Animator animator) {
            ActionBarLayout.this.onSlideAnimationEnd(this.val$backAnimation);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarLayout.2 */
    class C09512 implements Runnable {
        final /* synthetic */ boolean val$first;
        final /* synthetic */ boolean val$open;

        C09512(boolean z, boolean z2) {
            this.val$first = z;
            this.val$open = z2;
        }

        public void run() {
            long j = 18;
            if (ActionBarLayout.this.animationRunnable == this) {
                ActionBarLayout.this.animationRunnable = null;
                if (this.val$first) {
                    ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                }
                long nanoTime = System.nanoTime() / C0700C.MICROS_PER_SECOND;
                long access$700 = nanoTime - ActionBarLayout.this.lastFrameTime;
                if (access$700 <= 18) {
                    j = access$700;
                }
                ActionBarLayout.this.lastFrameTime = nanoTime;
                ActionBarLayout.this.animationProgress = (((float) j) / 150.0f) + ActionBarLayout.this.animationProgress;
                if (ActionBarLayout.this.animationProgress > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    ActionBarLayout.this.animationProgress = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                }
                float interpolation = ActionBarLayout.this.decelerateInterpolator.getInterpolation(ActionBarLayout.this.animationProgress);
                if (this.val$open) {
                    ActionBarLayout.this.containerView.setAlpha(interpolation);
                    ActionBarLayout.this.containerView.setTranslationX((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - interpolation) * ((float) AndroidUtilities.dp(48.0f)));
                } else {
                    ActionBarLayout.this.containerViewBack.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - interpolation);
                    ActionBarLayout.this.containerViewBack.setTranslationX(interpolation * ((float) AndroidUtilities.dp(48.0f)));
                }
                if (ActionBarLayout.this.animationProgress < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    ActionBarLayout.this.startLayoutAnimation(this.val$open, false);
                } else {
                    ActionBarLayout.this.onAnimationEndCheck(false);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarLayout.3 */
    class C09523 implements Runnable {
        final /* synthetic */ BaseFragment val$fragment;

        C09523(BaseFragment baseFragment) {
            this.val$fragment = baseFragment;
        }

        public void run() {
            this.val$fragment.onTransitionAnimationEnd(true, false);
            this.val$fragment.onBecomeFullyVisible();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarLayout.4 */
    class C09534 extends AnimatorListenerAdapterProxy {
        C09534() {
        }

        public void onAnimationEnd(Animator animator) {
            ActionBarLayout.this.onAnimationEndCheck(false);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarLayout.5 */
    class C09545 implements Runnable {
        final /* synthetic */ BaseFragment val$currentFragment;
        final /* synthetic */ BaseFragment val$fragment;
        final /* synthetic */ boolean val$removeLast;

        C09545(boolean z, BaseFragment baseFragment, BaseFragment baseFragment2) {
            this.val$removeLast = z;
            this.val$currentFragment = baseFragment;
            this.val$fragment = baseFragment2;
        }

        public void run() {
            if (VERSION.SDK_INT > 15) {
                ActionBarLayout.this.containerView.setLayerType(0, null);
                ActionBarLayout.this.containerViewBack.setLayerType(0, null);
            }
            ActionBarLayout.this.presentFragmentInternalRemoveOld(this.val$removeLast, this.val$currentFragment);
            this.val$fragment.onTransitionAnimationEnd(true, false);
            this.val$fragment.onBecomeFullyVisible();
            ActionBarLayout.this.containerView.setTranslationX(0.0f);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarLayout.6 */
    class C09556 implements Runnable {
        C09556() {
        }

        public void run() {
            ActionBarLayout.this.onAnimationEndCheck(false);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarLayout.7 */
    class C09567 implements Runnable {
        C09567() {
        }

        public void run() {
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                ActionBarLayout.this.startLayoutAnimation(true, true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarLayout.8 */
    class C09578 implements Runnable {
        C09578() {
        }

        public void run() {
            if (ActionBarLayout.this.delayedOpenAnimationRunnable == this) {
                ActionBarLayout.this.delayedOpenAnimationRunnable = null;
                ActionBarLayout.this.startLayoutAnimation(true, true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.ActionBarLayout.9 */
    class C09589 implements Runnable {
        final /* synthetic */ BaseFragment val$currentFragment;
        final /* synthetic */ BaseFragment val$previousFragmentFinal;

        C09589(BaseFragment baseFragment, BaseFragment baseFragment2) {
            this.val$currentFragment = baseFragment;
            this.val$previousFragmentFinal = baseFragment2;
        }

        public void run() {
            if (VERSION.SDK_INT > 15) {
                ActionBarLayout.this.containerView.setLayerType(0, null);
                ActionBarLayout.this.containerViewBack.setLayerType(0, null);
            }
            ActionBarLayout.this.closeLastFragmentInternalRemoveOld(this.val$currentFragment);
            ActionBarLayout.this.containerViewBack.setTranslationX(0.0f);
            this.val$currentFragment.onTransitionAnimationEnd(false, false);
            this.val$previousFragmentFinal.onTransitionAnimationEnd(true, true);
            this.val$previousFragmentFinal.onBecomeFullyVisible();
        }
    }

    public interface ActionBarLayoutDelegate {
        boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout);

        boolean needCloseLastFragment(ActionBarLayout actionBarLayout);

        boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout);

        boolean onPreIme();

        void onRebuildAllFragments(ActionBarLayout actionBarLayout);
    }

    public class LinearLayoutContainer extends LinearLayout {
        private boolean isKeyboardVisible;
        private Rect rect;

        public LinearLayoutContainer(Context context) {
            super(context);
            this.rect = new Rect();
            setOrientation(1);
        }

        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view instanceof ActionBar) {
                return super.drawChild(canvas, view, j);
            }
            int i;
            boolean drawChild;
            int childCount = getChildCount();
            for (i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt != view && (childAt instanceof ActionBar) && childAt.getVisibility() == 0) {
                    if (((ActionBar) childAt).getCastShadows()) {
                        i = childAt.getMeasuredHeight();
                        drawChild = super.drawChild(canvas, view, j);
                        if (!(i == 0 || ActionBarLayout.headerShadowDrawable == null)) {
                            ActionBarLayout.headerShadowDrawable.setBounds(0, i, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + i);
                            ActionBarLayout.headerShadowDrawable.draw(canvas);
                        }
                        return drawChild;
                    }
                    i = 0;
                    drawChild = super.drawChild(canvas, view, j);
                    ActionBarLayout.headerShadowDrawable.setBounds(0, i, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + i);
                    ActionBarLayout.headerShadowDrawable.draw(canvas);
                    return drawChild;
                }
            }
            i = 0;
            drawChild = super.drawChild(canvas, view, j);
            ActionBarLayout.headerShadowDrawable.setBounds(0, i, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + i);
            ActionBarLayout.headerShadowDrawable.draw(canvas);
            return drawChild;
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            boolean z2 = false;
            super.onLayout(z, i, i2, i3, i4);
            View rootView = getRootView();
            getWindowVisibleDisplayFrame(this.rect);
            if (((rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top) > 0) {
                z2 = true;
            }
            this.isKeyboardVisible = z2;
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable != null && !ActionBarLayout.this.containerView.isKeyboardVisible && !ActionBarLayout.this.containerViewBack.isKeyboardVisible) {
                AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.waitingForKeyboardCloseRunnable);
                ActionBarLayout.this.waitingForKeyboardCloseRunnable.run();
                ActionBarLayout.this.waitingForKeyboardCloseRunnable = null;
            }
        }
    }

    public ActionBarLayout(Context context) {
        super(context);
        this.decelerateInterpolator = new DecelerateInterpolator(1.5f);
        this.accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
        this.animationProgress = 0.0f;
        this.delegate = null;
        this.parentActivity = null;
        this.fragmentsStack = null;
        this.parentActivity = (Activity) context;
        if (layerShadowDrawable == null) {
            layerShadowDrawable = getResources().getDrawable(C0338R.drawable.layer_shadow);
            headerShadowDrawable = getResources().getDrawable(C0338R.drawable.header_shadow);
            scrimPaint = new Paint();
        }
    }

    private void closeLastFragmentInternalRemoveOld(BaseFragment baseFragment) {
        baseFragment.onPause();
        baseFragment.onFragmentDestroy();
        baseFragment.setParentLayout(null);
        this.fragmentsStack.remove(baseFragment);
        this.containerViewBack.setVisibility(8);
        bringChildToFront(this.containerView);
    }

    private void onAnimationEndCheck(boolean z) {
        onCloseAnimationEnd(false);
        onOpenAnimationEnd(false);
        if (this.waitingForKeyboardCloseRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.waitingForKeyboardCloseRunnable);
            this.waitingForKeyboardCloseRunnable = null;
        }
        if (this.currentAnimation != null) {
            if (z) {
                this.currentAnimation.cancel();
            }
            this.currentAnimation = null;
        }
        if (this.animationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
            this.animationRunnable = null;
        }
        setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.containerView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.containerView.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.containerView.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.containerViewBack.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.containerViewBack.setScaleX(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.containerViewBack.setScaleY(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    private void onCloseAnimationEnd(boolean z) {
        if (this.transitionAnimationInProgress && this.onCloseAnimationEndRunnable != null) {
            this.transitionAnimationInProgress = false;
            this.transitionAnimationStartTime = 0;
            if (z) {
                new Handler().post(new Runnable() {
                    public void run() {
                        ActionBarLayout.this.onCloseAnimationEndRunnable.run();
                        ActionBarLayout.this.onCloseAnimationEndRunnable = null;
                    }
                });
                return;
            }
            this.onCloseAnimationEndRunnable.run();
            this.onCloseAnimationEndRunnable = null;
        }
    }

    private void onOpenAnimationEnd(boolean z) {
        if (this.transitionAnimationInProgress && this.onOpenAnimationEndRunnable != null) {
            this.transitionAnimationInProgress = false;
            this.transitionAnimationStartTime = 0;
            if (z) {
                new Handler().post(new Runnable() {
                    public void run() {
                        ActionBarLayout.this.onOpenAnimationEndRunnable.run();
                        ActionBarLayout.this.onOpenAnimationEndRunnable = null;
                    }
                });
                return;
            }
            this.onOpenAnimationEndRunnable.run();
            this.onOpenAnimationEndRunnable = null;
        }
    }

    private void onSlideAnimationEnd(boolean z) {
        BaseFragment baseFragment;
        if (z) {
            ViewGroup viewGroup;
            baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
            baseFragment.onPause();
            if (baseFragment.fragmentView != null) {
                viewGroup = (ViewGroup) baseFragment.fragmentView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(baseFragment.fragmentView);
                }
            }
            if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
                viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(baseFragment.actionBar);
                }
            }
        } else {
            baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            baseFragment.onPause();
            baseFragment.onFragmentDestroy();
            baseFragment.setParentLayout(null);
            this.fragmentsStack.remove(this.fragmentsStack.size() - 1);
            LinearLayoutContainer linearLayoutContainer = this.containerView;
            this.containerView = this.containerViewBack;
            this.containerViewBack = linearLayoutContainer;
            bringChildToFront(this.containerView);
            baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            this.currentActionBar = baseFragment.actionBar;
            baseFragment.onResume();
            baseFragment.onBecomeFullyVisible();
        }
        this.containerViewBack.setVisibility(8);
        this.startedTracking = false;
        this.animationInProgress = false;
        this.containerView.setTranslationX(0.0f);
        this.containerViewBack.setTranslationX(0.0f);
        setInnerTranslationX(0.0f);
    }

    private void prepareForMoving(MotionEvent motionEvent) {
        ViewGroup viewGroup;
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) motionEvent.getX();
        this.containerViewBack.setVisibility(0);
        this.beginTrackingSent = false;
        BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2);
        View view = baseFragment.fragmentView;
        if (view == null) {
            view = baseFragment.createView(this.parentActivity);
        } else {
            viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(view);
            }
        }
        viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(view);
        }
        if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
            viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(baseFragment.actionBar);
            }
            if (this.removeActionBarExtraHeight) {
                baseFragment.actionBar.setOccupyStatusBar(false);
            }
            this.containerViewBack.addView(baseFragment.actionBar);
            baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText);
        }
        this.containerViewBack.addView(view);
        LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        view.setLayoutParams(layoutParams);
        if (!baseFragment.hasOwnBackground && view.getBackground() == null) {
            view.setBackgroundColor(-1);
        }
        baseFragment.onResume();
    }

    private void presentFragmentInternalRemoveOld(boolean z, BaseFragment baseFragment) {
        if (baseFragment != null) {
            baseFragment.onPause();
            if (z) {
                baseFragment.onFragmentDestroy();
                baseFragment.setParentLayout(null);
                this.fragmentsStack.remove(baseFragment);
            } else {
                ViewGroup viewGroup;
                if (baseFragment.fragmentView != null) {
                    viewGroup = (ViewGroup) baseFragment.fragmentView.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment.fragmentView);
                    }
                }
                if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
                    viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment.actionBar);
                    }
                }
            }
            this.containerViewBack.setVisibility(8);
        }
    }

    private void removeFragmentFromStackInternal(BaseFragment baseFragment) {
        baseFragment.onPause();
        baseFragment.onFragmentDestroy();
        baseFragment.setParentLayout(null);
        this.fragmentsStack.remove(baseFragment);
    }

    private void startLayoutAnimation(boolean z, boolean z2) {
        if (z2) {
            this.animationProgress = 0.0f;
            this.lastFrameTime = System.nanoTime() / C0700C.MICROS_PER_SECOND;
            if (VERSION.SDK_INT > 15) {
                this.containerView.setLayerType(2, null);
                this.containerViewBack.setLayerType(2, null);
            }
        }
        Runnable c09512 = new C09512(z2, z);
        this.animationRunnable = c09512;
        AndroidUtilities.runOnUIThread(c09512);
    }

    public boolean addFragmentToStack(BaseFragment baseFragment) {
        return addFragmentToStack(baseFragment, -1);
    }

    public boolean addFragmentToStack(BaseFragment baseFragment, int i) {
        if ((this.delegate != null && !this.delegate.needAddFragmentToStack(baseFragment, this)) || !baseFragment.onFragmentCreate()) {
            return false;
        }
        baseFragment.setParentLayout(this);
        if (i == -1) {
            if (!this.fragmentsStack.isEmpty()) {
                ViewGroup viewGroup;
                BaseFragment baseFragment2 = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
                baseFragment2.onPause();
                if (baseFragment2.actionBar != null && baseFragment2.actionBar.getAddToContainer()) {
                    viewGroup = (ViewGroup) baseFragment2.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment2.actionBar);
                    }
                }
                if (baseFragment2.fragmentView != null) {
                    viewGroup = (ViewGroup) baseFragment2.fragmentView.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment2.fragmentView);
                    }
                }
            }
            this.fragmentsStack.add(baseFragment);
        } else {
            this.fragmentsStack.add(i, baseFragment);
        }
        return true;
    }

    public boolean checkTransitionAnimation() {
        if (this.transitionAnimationInProgress && this.transitionAnimationStartTime < System.currentTimeMillis() - 1500) {
            onAnimationEndCheck(true);
        }
        return this.transitionAnimationInProgress;
    }

    public void closeLastFragment(boolean z) {
        closeLastFragmentAfterComment(z);
    }

    public void closeLastFragmentAfterComment(boolean z) {
        if ((this.delegate == null || this.delegate.needCloseLastFragment(this)) && !checkTransitionAnimation() && !this.fragmentsStack.isEmpty()) {
            if (this.parentActivity.getCurrentFocus() != null) {
                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
            }
            setInnerTranslationX(0.0f);
            boolean z2 = z && this.parentActivity.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true);
            BaseFragment baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            BaseFragment baseFragment2 = this.fragmentsStack.size() > 1 ? (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 2) : null;
            if (baseFragment2 != null) {
                ViewGroup viewGroup;
                LinearLayoutContainer linearLayoutContainer = this.containerView;
                this.containerView = this.containerViewBack;
                this.containerViewBack = linearLayoutContainer;
                this.containerView.setVisibility(0);
                baseFragment2.setParentLayout(this);
                View view = baseFragment2.fragmentView;
                if (view == null) {
                    view = baseFragment2.createView(this.parentActivity);
                } else {
                    viewGroup = (ViewGroup) view.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(view);
                    }
                }
                if (baseFragment2.actionBar != null && baseFragment2.actionBar.getAddToContainer()) {
                    if (this.removeActionBarExtraHeight) {
                        baseFragment2.actionBar.setOccupyStatusBar(false);
                    }
                    viewGroup = (ViewGroup) baseFragment2.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment2.actionBar);
                    }
                    this.containerView.addView(baseFragment2.actionBar);
                    baseFragment2.actionBar.setTitleOverlayText(this.titleOverlayText);
                }
                this.containerView.addView(view);
                LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = -1;
                view.setLayoutParams(layoutParams);
                baseFragment2.onTransitionAnimationStart(true, true);
                baseFragment.onTransitionAnimationStart(false, false);
                baseFragment2.onResume();
                this.currentActionBar = baseFragment2.actionBar;
                if (!baseFragment2.hasOwnBackground && view.getBackground() == null) {
                    view.setBackgroundColor(-1);
                }
                if (!z2) {
                    closeLastFragmentInternalRemoveOld(baseFragment);
                }
                if (z2) {
                    this.transitionAnimationStartTime = System.currentTimeMillis();
                    this.transitionAnimationInProgress = true;
                    this.onCloseAnimationEndRunnable = new C09589(baseFragment, baseFragment2);
                    AnimatorSet onCustomTransitionAnimation = baseFragment.onCustomTransitionAnimation(false, new Runnable() {
                        public void run() {
                            ActionBarLayout.this.onAnimationEndCheck(false);
                        }
                    });
                    if (onCustomTransitionAnimation != null) {
                        if (VERSION.SDK_INT > 15) {
                            this.currentAnimation = onCustomTransitionAnimation;
                        } else {
                            this.currentAnimation = onCustomTransitionAnimation;
                        }
                        return;
                    } else if (this.containerView.isKeyboardVisible || this.containerViewBack.isKeyboardVisible) {
                        this.waitingForKeyboardCloseRunnable = new Runnable() {
                            public void run() {
                                if (ActionBarLayout.this.waitingForKeyboardCloseRunnable == this) {
                                    ActionBarLayout.this.startLayoutAnimation(false, true);
                                }
                            }
                        };
                        AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, 200);
                        return;
                    } else {
                        startLayoutAnimation(false, true);
                        return;
                    }
                }
                baseFragment.onTransitionAnimationEnd(false, false);
                baseFragment2.onTransitionAnimationEnd(true, true);
                baseFragment2.onBecomeFullyVisible();
            } else if (this.useAlphaAnimations) {
                this.transitionAnimationStartTime = System.currentTimeMillis();
                this.transitionAnimationInProgress = true;
                this.onCloseAnimationEndRunnable = new AnonymousClass12(baseFragment);
                Collection arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}));
                if (this.backgroundView != null) {
                    arrayList.add(ObjectAnimator.ofFloat(this.backgroundView, "alpha", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}));
                }
                this.currentAnimation = new AnimatorSet();
                this.currentAnimation.playTogether(arrayList);
                this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
                this.currentAnimation.setDuration(200);
                this.currentAnimation.addListener(new AnimatorListenerAdapterProxy() {
                    public void onAnimationEnd(Animator animator) {
                        ActionBarLayout.this.onAnimationEndCheck(false);
                    }

                    public void onAnimationStart(Animator animator) {
                        ActionBarLayout.this.transitionAnimationStartTime = System.currentTimeMillis();
                    }
                });
                this.currentAnimation.start();
            } else {
                removeFragmentFromStackInternal(baseFragment);
                setVisibility(8);
                if (this.backgroundView != null) {
                    this.backgroundView.setVisibility(8);
                }
            }
        }
    }

    public void dismissDialogs() {
        if (!this.fragmentsStack.isEmpty()) {
            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).dismissCurrentDialig();
        }
    }

    public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
        return (keyEvent != null && keyEvent.getKeyCode() == 4 && keyEvent.getAction() == 1) ? (this.delegate != null && this.delegate.onPreIme()) || super.dispatchKeyEventPreIme(keyEvent) : super.dispatchKeyEventPreIme(keyEvent);
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int paddingRight = getPaddingRight() + ((int) this.innerTranslationX);
        int paddingLeft = getPaddingLeft();
        int paddingLeft2 = getPaddingLeft() + width;
        if (view == this.containerViewBack) {
            paddingLeft2 = paddingRight;
        } else if (view == this.containerView) {
            paddingLeft = paddingRight;
        }
        int save = canvas.save();
        if (!this.transitionAnimationInProgress) {
            canvas.clipRect(paddingLeft, 0, paddingLeft2, getHeight());
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        if (paddingRight != 0) {
            if (view == this.containerView) {
                float max = Math.max(0.0f, Math.min(((float) (width - paddingRight)) / ((float) AndroidUtilities.dp(20.0f)), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                layerShadowDrawable.setBounds(paddingRight - layerShadowDrawable.getIntrinsicWidth(), view.getTop(), paddingRight, view.getBottom());
                layerShadowDrawable.setAlpha((int) (max * 255.0f));
                layerShadowDrawable.draw(canvas);
            } else if (view == this.containerViewBack) {
                float min = Math.min(DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD, ((float) (width - paddingRight)) / ((float) width));
                if (min < 0.0f) {
                    min = 0.0f;
                }
                scrimPaint.setColor(((int) (min * 153.0f)) << 24);
                canvas.drawRect((float) paddingLeft, 0.0f, (float) paddingLeft2, (float) getHeight(), scrimPaint);
            }
        }
        return drawChild;
    }

    public void drawHeaderShadow(Canvas canvas, int i) {
        if (headerShadowDrawable != null) {
            headerShadowDrawable.setBounds(0, i, getMeasuredWidth(), headerShadowDrawable.getIntrinsicHeight() + i);
            headerShadowDrawable.draw(canvas);
        }
    }

    public ActionBar getCurrentActionBar() {
        return this.currentActionBar;
    }

    public DrawerLayoutContainer getDrawerLayoutContainer() {
        return this.drawerLayoutContainer;
    }

    public float getInnerTranslationX() {
        return this.innerTranslationX;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void init(ArrayList<BaseFragment> arrayList) {
        this.fragmentsStack = arrayList;
        this.containerViewBack = new LinearLayoutContainer(this.parentActivity);
        addView(this.containerViewBack);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.containerViewBack.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.containerViewBack.setLayoutParams(layoutParams);
        this.containerView = new LinearLayoutContainer(this.parentActivity);
        addView(this.containerView);
        layoutParams = (FrameLayout.LayoutParams) this.containerView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 51;
        this.containerView.setLayoutParams(layoutParams);
        Iterator it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            ((BaseFragment) it.next()).setParentLayout(this);
        }
    }

    public void onActionModeFinished(Object obj) {
        if (this.currentActionBar != null) {
            this.currentActionBar.setVisibility(0);
        }
        this.inActionMode = false;
    }

    public void onActionModeStarted(Object obj) {
        if (this.currentActionBar != null) {
            this.currentActionBar.setVisibility(8);
        }
        this.inActionMode = true;
    }

    public void onBackPressed() {
        if (!this.startedTracking && !checkTransitionAnimation() && !this.fragmentsStack.isEmpty()) {
            if (this.currentActionBar != null && this.currentActionBar.isSearchFieldVisible) {
                this.currentActionBar.closeSearchField();
            } else if (((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onBackPressed() && !this.fragmentsStack.isEmpty()) {
                closeLastFragment(true);
            }
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (!this.fragmentsStack.isEmpty()) {
            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onConfigurationChanged(configuration);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.animationInProgress || checkTransitionAnimation() || onTouchEvent(motionEvent);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (!(i != 82 || checkTransitionAnimation() || this.startedTracking || this.currentActionBar == null)) {
            this.currentActionBar.onMenuButtonPressed();
        }
        return super.onKeyUp(i, keyEvent);
    }

    public void onLowMemory() {
        Iterator it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            ((BaseFragment) it.next()).onLowMemory();
        }
    }

    public void onPause() {
        if (!this.fragmentsStack.isEmpty()) {
            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onPause();
        }
    }

    public void onResume() {
        if (this.transitionAnimationInProgress) {
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
                this.currentAnimation = null;
            }
            if (this.onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd(false);
            } else if (this.onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd(false);
            }
        }
        if (!this.fragmentsStack.isEmpty()) {
            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onResume();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (checkTransitionAnimation() || this.inActionMode || this.animationInProgress) {
            return false;
        }
        if (this.fragmentsStack.size() > 1) {
            if (motionEvent == null || motionEvent.getAction() != 0 || this.startedTracking || this.maybeStartTracking) {
                if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    int max = Math.max(0, (int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                    int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(motionEvent);
                    if (this.maybeStartTracking && !this.startedTracking && ((float) max) >= AndroidUtilities.getPixelsInCM(0.4f, true) && Math.abs(max) / 3 > abs) {
                        prepareForMoving(motionEvent);
                    } else if (this.startedTracking) {
                        if (!this.beginTrackingSent) {
                            if (this.parentActivity.getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
                            }
                            ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onBeginSlide();
                            this.beginTrackingSent = true;
                        }
                        this.containerView.setTranslationX((float) max);
                        setInnerTranslationX((float) max);
                    }
                } else if (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6)) {
                    float xVelocity;
                    float yVelocity;
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(PointerIconCompat.TYPE_DEFAULT);
                    if (!this.startedTracking && ((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).swipeBackEnabled) {
                        xVelocity = this.velocityTracker.getXVelocity();
                        yVelocity = this.velocityTracker.getYVelocity();
                        if (xVelocity >= 3500.0f && xVelocity > Math.abs(yVelocity)) {
                            prepareForMoving(motionEvent);
                            if (!this.beginTrackingSent) {
                                if (((Activity) getContext()).getCurrentFocus() != null) {
                                    AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                                }
                                this.beginTrackingSent = true;
                            }
                        }
                    }
                    if (this.startedTracking) {
                        xVelocity = this.containerView.getX();
                        AnimatorSet animatorSet = new AnimatorSet();
                        yVelocity = this.velocityTracker.getXVelocity();
                        boolean z = xVelocity < ((float) this.containerView.getMeasuredWidth()) / 3.0f && (yVelocity < 3500.0f || yVelocity < this.velocityTracker.getYVelocity());
                        Animator[] animatorArr;
                        if (z) {
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            xVelocity = ((float) this.containerView.getMeasuredWidth()) - xVelocity;
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, "translationX", new float[]{(float) this.containerView.getMeasuredWidth()});
                            animatorArr[1] = ObjectAnimator.ofFloat(this, "innerTranslationX", new float[]{(float) this.containerView.getMeasuredWidth()});
                            animatorSet.playTogether(animatorArr);
                        }
                        animatorSet.setDuration((long) Math.max((int) (xVelocity * (200.0f / ((float) this.containerView.getMeasuredWidth()))), 50));
                        animatorSet.addListener(new C09501(z));
                        animatorSet.start();
                        this.animationInProgress = true;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                    }
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                } else if (motionEvent == null) {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
            } else if (!((BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1)).swipeBackEnabled) {
                return false;
            } else {
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
                if (this.velocityTracker != null) {
                    this.velocityTracker.clear();
                }
            }
        }
        return this.startedTracking;
    }

    public boolean presentFragment(BaseFragment baseFragment) {
        return presentFragment(baseFragment, false, false, true);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z) {
        return presentFragment(baseFragment, z, false, true);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2, boolean z3) {
        if (checkTransitionAnimation() || ((this.delegate != null && z3 && !this.delegate.needPresentFragment(baseFragment, z, z2, this)) || !baseFragment.onFragmentCreate())) {
            return false;
        }
        ViewGroup viewGroup;
        if (this.parentActivity.getCurrentFocus() != null) {
            AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
        }
        Object obj = (z2 || !this.parentActivity.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) ? null : 1;
        BaseFragment baseFragment2 = !this.fragmentsStack.isEmpty() ? (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1) : null;
        baseFragment.setParentLayout(this);
        View view = baseFragment.fragmentView;
        if (view == null) {
            view = baseFragment.createView(this.parentActivity);
        } else {
            viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(view);
            }
        }
        if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
            if (this.removeActionBarExtraHeight) {
                baseFragment.actionBar.setOccupyStatusBar(false);
            }
            viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(baseFragment.actionBar);
            }
            this.containerViewBack.addView(baseFragment.actionBar);
            baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText);
        }
        this.containerViewBack.addView(view);
        LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        view.setLayoutParams(layoutParams);
        this.fragmentsStack.add(baseFragment);
        baseFragment.onResume();
        this.currentActionBar = baseFragment.actionBar;
        if (!baseFragment.hasOwnBackground && view.getBackground() == null) {
            view.setBackgroundColor(-1);
        }
        LinearLayoutContainer linearLayoutContainer = this.containerView;
        this.containerView = this.containerViewBack;
        this.containerViewBack = linearLayoutContainer;
        this.containerView.setVisibility(0);
        setInnerTranslationX(0.0f);
        bringChildToFront(this.containerView);
        if (obj == null) {
            presentFragmentInternalRemoveOld(z, baseFragment2);
            if (this.backgroundView != null) {
                this.backgroundView.setVisibility(0);
            }
        }
        if (obj == null) {
            if (this.backgroundView != null) {
                this.backgroundView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.backgroundView.setVisibility(0);
            }
            baseFragment.onTransitionAnimationStart(true, false);
            baseFragment.onTransitionAnimationEnd(true, false);
            baseFragment.onBecomeFullyVisible();
        } else if (this.useAlphaAnimations && this.fragmentsStack.size() == 1) {
            presentFragmentInternalRemoveOld(z, baseFragment2);
            this.transitionAnimationStartTime = System.currentTimeMillis();
            this.transitionAnimationInProgress = true;
            this.onOpenAnimationEndRunnable = new C09523(baseFragment);
            Collection arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
            if (this.backgroundView != null) {
                this.backgroundView.setVisibility(0);
                arrayList.add(ObjectAnimator.ofFloat(this.backgroundView, "alpha", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
            }
            baseFragment.onTransitionAnimationStart(true, false);
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.playTogether(arrayList);
            this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
            this.currentAnimation.setDuration(200);
            this.currentAnimation.addListener(new C09534());
            this.currentAnimation.start();
        } else {
            this.transitionAnimationStartTime = System.currentTimeMillis();
            this.transitionAnimationInProgress = true;
            this.onOpenAnimationEndRunnable = new C09545(z, baseFragment2, baseFragment);
            baseFragment.onTransitionAnimationStart(true, false);
            AnimatorSet onCustomTransitionAnimation = baseFragment.onCustomTransitionAnimation(true, new C09556());
            if (onCustomTransitionAnimation == null) {
                this.containerView.setAlpha(0.0f);
                this.containerView.setTranslationX(48.0f);
                if (this.containerView.isKeyboardVisible || this.containerViewBack.isKeyboardVisible) {
                    this.waitingForKeyboardCloseRunnable = new C09567();
                    AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, 200);
                } else if (baseFragment.needDelayOpenAnimation()) {
                    this.delayedOpenAnimationRunnable = new C09578();
                    AndroidUtilities.runOnUIThread(this.delayedOpenAnimationRunnable, 200);
                } else {
                    startLayoutAnimation(true, true);
                }
            } else if (VERSION.SDK_INT > 15) {
                this.containerView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.containerView.setTranslationX(0.0f);
                this.currentAnimation = onCustomTransitionAnimation;
            } else {
                this.containerView.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.containerView.setTranslationX(0.0f);
                this.currentAnimation = onCustomTransitionAnimation;
            }
        }
        if (UserConfig.isClientActivated()) {
            AlarmUtil.m275a(this.parentActivity);
        }
        return true;
    }

    public void rebuildAllFragmentViews(boolean z) {
        int i = 0;
        while (true) {
            if (i >= this.fragmentsStack.size() - (z ? 0 : 1)) {
                break;
            }
            ((BaseFragment) this.fragmentsStack.get(i)).clearViews();
            ((BaseFragment) this.fragmentsStack.get(i)).setParentLayout(this);
            i++;
        }
        if (this.delegate != null) {
            this.delegate.onRebuildAllFragments(this);
        }
    }

    public void removeAllFragments() {
        while (this.fragmentsStack.size() > 0) {
            removeFragmentFromStackInternal((BaseFragment) this.fragmentsStack.get(0));
        }
    }

    public void removeFragmentFromStack(BaseFragment baseFragment) {
        if (this.useAlphaAnimations && this.fragmentsStack.size() == 1 && AndroidUtilities.isTablet()) {
            closeLastFragment(true);
        } else {
            removeFragmentFromStackInternal(baseFragment);
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        onTouchEvent(null);
        super.requestDisallowInterceptTouchEvent(z);
    }

    public void resumeDelayedFragmentAnimation() {
        if (this.delayedOpenAnimationRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.delayedOpenAnimationRunnable);
            this.delayedOpenAnimationRunnable.run();
            this.delayedOpenAnimationRunnable = null;
        }
    }

    public void setBackgroundView(View view) {
        this.backgroundView = view;
    }

    public void setDelegate(ActionBarLayoutDelegate actionBarLayoutDelegate) {
        this.delegate = actionBarLayoutDelegate;
    }

    public void setDrawerLayoutContainer(DrawerLayoutContainer drawerLayoutContainer) {
        this.drawerLayoutContainer = drawerLayoutContainer;
    }

    public void setInnerTranslationX(float f) {
        this.innerTranslationX = f;
        invalidate();
    }

    public void setRemoveActionBarExtraHeight(boolean z) {
        this.removeActionBarExtraHeight = z;
    }

    public void setTitleOverlayText(String str) {
        this.titleOverlayText = str;
        Iterator it = this.fragmentsStack.iterator();
        while (it.hasNext()) {
            BaseFragment baseFragment = (BaseFragment) it.next();
            if (baseFragment.actionBar != null) {
                baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText);
            }
        }
    }

    public void setUseAlphaAnimations(boolean z) {
        this.useAlphaAnimations = z;
    }

    public void showLastFragment() {
        if (!this.fragmentsStack.isEmpty()) {
            BaseFragment baseFragment;
            ViewGroup viewGroup;
            for (int i = 0; i < this.fragmentsStack.size() - 1; i++) {
                baseFragment = (BaseFragment) this.fragmentsStack.get(i);
                if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
                    viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(baseFragment.actionBar);
                    }
                }
                if (baseFragment.fragmentView != null) {
                    viewGroup = (ViewGroup) baseFragment.fragmentView.getParent();
                    if (viewGroup != null) {
                        baseFragment.onPause();
                        viewGroup.removeView(baseFragment.fragmentView);
                    }
                }
            }
            baseFragment = (BaseFragment) this.fragmentsStack.get(this.fragmentsStack.size() - 1);
            baseFragment.setParentLayout(this);
            View view = baseFragment.fragmentView;
            if (view == null) {
                view = baseFragment.createView(this.parentActivity);
            } else {
                viewGroup = (ViewGroup) view.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(view);
                }
            }
            if (baseFragment.actionBar != null && baseFragment.actionBar.getAddToContainer()) {
                if (this.removeActionBarExtraHeight) {
                    baseFragment.actionBar.setOccupyStatusBar(false);
                }
                viewGroup = (ViewGroup) baseFragment.actionBar.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(baseFragment.actionBar);
                }
                this.containerView.addView(baseFragment.actionBar);
                baseFragment.actionBar.setTitleOverlayText(this.titleOverlayText);
            }
            this.containerView.addView(view);
            LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            view.setLayoutParams(layoutParams);
            baseFragment.onResume();
            this.currentActionBar = baseFragment.actionBar;
            if (!baseFragment.hasOwnBackground && view.getBackground() == null) {
                view.setBackgroundColor(-1);
            }
        }
    }

    public void startActivityForResult(Intent intent, int i) {
        if (this.parentActivity != null) {
            if (this.transitionAnimationInProgress) {
                if (this.currentAnimation != null) {
                    this.currentAnimation.cancel();
                    this.currentAnimation = null;
                }
                if (this.onCloseAnimationEndRunnable != null) {
                    onCloseAnimationEnd(false);
                } else if (this.onOpenAnimationEndRunnable != null) {
                    onOpenAnimationEnd(false);
                }
                this.containerView.invalidate();
                if (intent != null) {
                    this.parentActivity.startActivityForResult(intent, i);
                }
            } else if (intent != null) {
                this.parentActivity.startActivityForResult(intent, i);
            }
        }
    }
}

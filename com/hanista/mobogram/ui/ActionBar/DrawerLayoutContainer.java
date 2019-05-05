package com.hanista.mobogram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build.VERSION;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p001b.CategoryUtil;
import com.hanista.mobogram.mobo.p010k.MaterialHelperUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.DialogsActivity;

public class DrawerLayoutContainer extends FrameLayout {
    private static final int MIN_DRAWER_MARGIN = 64;
    private boolean allowDrawContent;
    private boolean allowOpenDrawer;
    private boolean beginTrackingSent;
    private AnimatorSet currentAnimation;
    private ViewGroup drawerLayout;
    private boolean drawerOpened;
    private float drawerPosition;
    private boolean inLayout;
    private Object lastInsets;
    private boolean maybeStartTracking;
    private int minDrawerMargin;
    private ActionBarLayout parentActionBarLayout;
    private float scrimOpacity;
    private Paint scrimPaint;
    private Drawable shadowLeft;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private VelocityTracker velocityTracker;

    /* renamed from: com.hanista.mobogram.ui.ActionBar.DrawerLayoutContainer.1 */
    class C09861 implements OnApplyWindowInsetsListener {
        C09861() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            DrawerLayoutContainer drawerLayoutContainer = (DrawerLayoutContainer) view;
            AndroidUtilities.statusBarHeight = windowInsets.getSystemWindowInsetTop();
            DrawerLayoutContainer.this.lastInsets = windowInsets;
            boolean z = windowInsets.getSystemWindowInsetTop() <= 0 && DrawerLayoutContainer.this.getBackground() == null;
            drawerLayoutContainer.setWillNotDraw(z);
            drawerLayoutContainer.requestLayout();
            return windowInsets.consumeSystemWindowInsets();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.DrawerLayoutContainer.2 */
    class C09872 extends AnimatorListenerAdapterProxy {
        C09872() {
        }

        public void onAnimationEnd(Animator animator) {
            DrawerLayoutContainer.this.onDrawerAnimationEnd(true);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.DrawerLayoutContainer.3 */
    class C09883 extends AnimatorListenerAdapterProxy {
        C09883() {
        }

        public void onAnimationEnd(Animator animator) {
            DrawerLayoutContainer.this.onDrawerAnimationEnd(false);
        }
    }

    public DrawerLayoutContainer(Context context) {
        super(context);
        this.scrimPaint = new Paint();
        this.allowDrawContent = true;
        this.minDrawerMargin = (int) ((64.0f * AndroidUtilities.density) + 0.5f);
        setDescendantFocusability(AccessibilityNodeInfoCompat.ACTION_EXPAND);
        setFocusableInTouchMode(true);
        if (VERSION.SDK_INT >= 21) {
            setFitsSystemWindows(true);
            setOnApplyWindowInsetsListener(new C09861());
            setSystemUiVisibility(1280);
        }
        this.shadowLeft = getResources().getDrawable(C0338R.drawable.menu_shadow);
    }

    @SuppressLint({"NewApi"})
    private void applyMarginInsets(MarginLayoutParams marginLayoutParams, Object obj, int i, boolean z) {
        int i2 = 0;
        WindowInsets windowInsets = (WindowInsets) obj;
        if (i == 3) {
            windowInsets = windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), 0, windowInsets.getSystemWindowInsetBottom());
        } else if (i == 5) {
            windowInsets = windowInsets.replaceSystemWindowInsets(0, windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
        }
        marginLayoutParams.leftMargin = windowInsets.getSystemWindowInsetLeft();
        if (!z) {
            i2 = windowInsets.getSystemWindowInsetTop();
        }
        marginLayoutParams.topMargin = i2;
        marginLayoutParams.rightMargin = windowInsets.getSystemWindowInsetRight();
        marginLayoutParams.bottomMargin = windowInsets.getSystemWindowInsetBottom();
    }

    @SuppressLint({"NewApi"})
    private void dispatchChildInsets(View view, Object obj, int i) {
        WindowInsets windowInsets = (WindowInsets) obj;
        if (i == 3) {
            windowInsets = windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), 0, windowInsets.getSystemWindowInsetBottom());
        } else if (i == 5) {
            windowInsets = windowInsets.replaceSystemWindowInsets(0, windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
        }
        view.dispatchApplyWindowInsets(windowInsets);
    }

    private float getScrimOpacity() {
        return this.scrimOpacity;
    }

    private int getTopInset(Object obj) {
        return (VERSION.SDK_INT < 21 || obj == null) ? 0 : ((WindowInsets) obj).getSystemWindowInsetTop();
    }

    private void onDrawerAnimationEnd(boolean z) {
        this.startedTracking = false;
        this.currentAnimation = null;
        this.drawerOpened = z;
        if (!z && (this.drawerLayout instanceof ListView)) {
            ((ListView) this.drawerLayout).setSelectionFromTop(0, 0);
        }
        if (z && (this.drawerLayout instanceof ListView)) {
            try {
                MaterialHelperUtil.m1366a((Activity) getContext(), (ListView) this.drawerLayout);
            } catch (Exception e) {
            }
        }
    }

    private void prepareForDrawerOpen(MotionEvent motionEvent) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        if (motionEvent != null) {
            this.startedTrackingX = (int) motionEvent.getX();
        }
        this.beginTrackingSent = false;
    }

    private void setScrimOpacity(float f) {
        this.scrimOpacity = f;
        invalidate();
    }

    private void updateListBG() {
        if (ThemeUtil.m2490b() && getDrawerLayout() != null) {
            int i = AdvanceTheme.f2484U;
            int i2 = AdvanceTheme.f2485V;
            if (i2 > 0) {
                Orientation orientation;
                switch (i2) {
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i3 = AdvanceTheme.f2486W;
                getDrawerLayout().setBackgroundDrawable(new GradientDrawable(orientation, new int[]{i, i3}));
                return;
            }
            getDrawerLayout().setBackgroundColor(i);
        }
    }

    public void cancelCurrentAnimation() {
        if (this.currentAnimation != null) {
            this.currentAnimation.cancel();
            this.currentAnimation = null;
        }
    }

    public void closeDrawer(boolean z) {
        cancelCurrentAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{0.0f});
        animatorSet.playTogether(animatorArr);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        if (z) {
            animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.drawerLayout.getMeasuredWidth())) * this.drawerPosition), 50));
        } else {
            animatorSet.setDuration(300);
        }
        animatorSet.addListener(new C09883());
        animatorSet.start();
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (!this.allowDrawContent) {
            return false;
        }
        int height = getHeight();
        Object obj = view != this.drawerLayout ? 1 : null;
        int i = 0;
        int i2 = 0;
        int width = getWidth();
        int save = canvas.save();
        if (obj != null) {
            int childCount = getChildCount();
            int i3 = 0;
            while (i3 < childCount) {
                View childAt = getChildAt(i3);
                int i4 = (childAt.getVisibility() != 0 || childAt == this.drawerLayout) ? i : i3;
                if (childAt != view && childAt.getVisibility() == 0 && childAt == this.drawerLayout && childAt.getHeight() >= height) {
                    i = childAt.getRight();
                    if (i > i2) {
                        i2 = i;
                    }
                }
                i3++;
                i = i4;
            }
            if (i2 != 0) {
                canvas.clipRect(i2, 0, width, getHeight());
            }
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        if (this.scrimOpacity <= 0.0f || obj == null) {
            if (this.shadowLeft != null) {
                float max = Math.max(0.0f, Math.min(this.drawerPosition / ((float) AndroidUtilities.dp(20.0f)), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                if (max != 0.0f) {
                    this.shadowLeft.setBounds((int) this.drawerPosition, view.getTop(), ((int) this.drawerPosition) + this.shadowLeft.getIntrinsicWidth(), view.getBottom());
                    this.shadowLeft.setAlpha((int) (max * 255.0f));
                    this.shadowLeft.draw(canvas);
                }
            }
        } else if (indexOfChild(view) == i) {
            this.scrimPaint.setColor(((int) (153.0f * this.scrimOpacity)) << 24);
            canvas.drawRect((float) i2, 0.0f, (float) width, (float) getHeight(), this.scrimPaint);
        }
        return drawChild;
    }

    public View getDrawerLayout() {
        return this.drawerLayout;
    }

    public float getDrawerPosition() {
        return this.drawerPosition;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean isDrawerOpened() {
        return this.drawerOpened;
    }

    public void moveDrawerByX(float f) {
        setDrawerPosition(this.drawerPosition + f);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.parentActionBarLayout.checkTransitionAnimation() || onTouchEvent(motionEvent);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.inLayout = true;
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                try {
                    if (this.drawerLayout != childAt) {
                        childAt.layout(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.leftMargin + childAt.getMeasuredWidth(), layoutParams.topMargin + childAt.getMeasuredHeight());
                    } else {
                        childAt.layout(-childAt.getMeasuredWidth(), layoutParams.topMargin, 0, layoutParams.topMargin + childAt.getMeasuredHeight());
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
        this.inLayout = false;
    }

    @SuppressLint({"NewApi"})
    protected void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        Object obj = (this.lastInsets == null || VERSION.SDK_INT < 21) ? null : 1;
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (obj != null) {
                    if (childAt.getFitsSystemWindows()) {
                        dispatchChildInsets(childAt, this.lastInsets, layoutParams.gravity);
                    } else if (childAt.getTag() == null) {
                        applyMarginInsets(layoutParams, this.lastInsets, layoutParams.gravity, VERSION.SDK_INT >= 21);
                    }
                }
                if (this.drawerLayout != childAt) {
                    childAt.measure(MeasureSpec.makeMeasureSpec((size - layoutParams.leftMargin) - layoutParams.rightMargin, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec((size2 - layoutParams.topMargin) - layoutParams.bottomMargin, C0700C.ENCODING_PCM_32BIT));
                } else {
                    childAt.setPadding(0, 0, 0, 0);
                    childAt.measure(getChildMeasureSpec(i, (this.minDrawerMargin + layoutParams.leftMargin) + layoutParams.rightMargin, layoutParams.width), getChildMeasureSpec(i2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                }
            }
        }
        updateListBG();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = true;
        if (this.parentActionBarLayout.checkTransitionAnimation()) {
            return false;
        }
        if (this.drawerOpened && motionEvent != null && motionEvent.getX() > this.drawerPosition && !this.startedTracking) {
            if (motionEvent.getAction() == 1) {
                closeDrawer(false);
            }
            return true;
        } else if (MoboConstants.f1335b && motionEvent != null && motionEvent.getX() > 150.0f && this.drawerPosition == 0.0f) {
            return false;
        } else {
            if (this.allowOpenDrawer && (this.parentActionBarLayout.fragmentsStack.size() == 1 || ((this.parentActionBarLayout.fragmentsStack.size() == 2 && (this.parentActionBarLayout.fragmentsStack.get(0) instanceof DialogsActivity) && HiddenConfig.f1402e) || ((this.parentActionBarLayout.fragmentsStack.size() == 3 && CategoryUtil.m352c() && (this.parentActionBarLayout.fragmentsStack.get(0) instanceof DialogsActivity) && HiddenConfig.f1402e) || (this.parentActionBarLayout.fragmentsStack.size() == 2 && CategoryUtil.m352c()))))) {
                if (motionEvent != null && ((motionEvent.getAction() == 0 || motionEvent.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking)) {
                    this.startedTrackingPointerId = motionEvent.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) motionEvent.getX();
                    this.startedTrackingY = (int) motionEvent.getY();
                    cancelCurrentAnimation();
                    if (this.velocityTracker != null) {
                        this.velocityTracker.clear();
                    }
                } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    float x = (float) ((int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                    float abs = (float) Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    this.velocityTracker.addMovement(motionEvent);
                    if (this.maybeStartTracking && !this.startedTracking && ((x > 0.0f && x / 3.0f > Math.abs(abs) && Math.abs(x) >= AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD, true)) || (x < 0.0f && Math.abs(x) >= Math.abs(abs) && Math.abs(x) >= AndroidUtilities.getPixelsInCM(0.4f, true)))) {
                        prepareForDrawerOpen(motionEvent);
                        this.startedTrackingX = (int) motionEvent.getX();
                        requestDisallowInterceptTouchEvent(true);
                    } else if (this.startedTracking) {
                        if (!this.beginTrackingSent) {
                            if (((Activity) getContext()).getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                            }
                            this.beginTrackingSent = true;
                        }
                        moveDrawerByX(x);
                        this.startedTrackingX = (int) motionEvent.getX();
                    }
                } else if (motionEvent == null || (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.computeCurrentVelocity(PointerIconCompat.TYPE_DEFAULT);
                    if (this.startedTracking || !(this.drawerPosition == 0.0f || this.drawerPosition == ((float) this.drawerLayout.getMeasuredWidth()))) {
                        float xVelocity = this.velocityTracker.getXVelocity();
                        boolean z2 = (this.drawerPosition < ((float) this.drawerLayout.getMeasuredWidth()) / 2.0f && (xVelocity < 3500.0f || Math.abs(xVelocity) < Math.abs(this.velocityTracker.getYVelocity()))) || (xVelocity < 0.0f && Math.abs(xVelocity) >= 3500.0f);
                        if (z2) {
                            if (!this.drawerOpened || Math.abs(xVelocity) < 3500.0f) {
                                z = false;
                            }
                            closeDrawer(z);
                        } else {
                            z2 = !this.drawerOpened && Math.abs(xVelocity) >= 3500.0f;
                            openDrawer(z2);
                        }
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        this.startedTracking = false;
                    }
                    if (this.velocityTracker != null) {
                        this.velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
            }
            return this.startedTracking;
        }
    }

    public void openDrawer(boolean z) {
        if (this.allowOpenDrawer) {
            if (!(!AndroidUtilities.isTablet() || this.parentActionBarLayout == null || this.parentActionBarLayout.parentActivity == null)) {
                AndroidUtilities.hideKeyboard(this.parentActionBarLayout.parentActivity.getCurrentFocus());
            }
            cancelCurrentAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{(float) this.drawerLayout.getMeasuredWidth()});
            animatorSet.playTogether(animatorArr);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            if (z) {
                animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.drawerLayout.getMeasuredWidth())) * (((float) this.drawerLayout.getMeasuredWidth()) - this.drawerPosition)), 50));
            } else {
                animatorSet.setDuration(300);
            }
            animatorSet.addListener(new C09872());
            animatorSet.start();
            this.currentAnimation = animatorSet;
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }

    public void setAllowDrawContent(boolean z) {
        if (this.allowDrawContent != z) {
            this.allowDrawContent = z;
            invalidate();
        }
    }

    public void setAllowOpenDrawer(boolean z, boolean z2) {
        this.allowOpenDrawer = z;
        if (!this.allowOpenDrawer && this.drawerPosition != 0.0f) {
            if (z2) {
                closeDrawer(true);
                return;
            }
            setDrawerPosition(0.0f);
            onDrawerAnimationEnd(false);
        }
    }

    public void setDrawerLayout(ViewGroup viewGroup) {
        this.drawerLayout = viewGroup;
        addView(this.drawerLayout);
        if (VERSION.SDK_INT >= 21) {
            this.drawerLayout.setFitsSystemWindows(true);
        }
    }

    public void setDrawerPosition(float f) {
        this.drawerPosition = f;
        if (this.drawerPosition > ((float) this.drawerLayout.getMeasuredWidth())) {
            this.drawerPosition = (float) this.drawerLayout.getMeasuredWidth();
        } else if (this.drawerPosition < 0.0f) {
            this.drawerPosition = 0.0f;
        }
        this.drawerLayout.setTranslationX(this.drawerPosition);
        int i = this.drawerPosition > 0.0f ? 0 : 8;
        if (this.drawerLayout.getVisibility() != i) {
            this.drawerLayout.setVisibility(i);
        }
        setScrimOpacity(this.drawerPosition / ((float) this.drawerLayout.getMeasuredWidth()));
    }

    public void setParentActionBarLayout(ActionBarLayout actionBarLayout) {
        this.parentActionBarLayout = actionBarLayout;
    }
}

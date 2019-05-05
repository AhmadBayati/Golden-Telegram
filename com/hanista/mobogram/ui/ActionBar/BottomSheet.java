package com.hanista.mobogram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.googlecode.mp4parser.authoring.tracks.h265.NalUnitTypes;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;

public class BottomSheet extends Dialog {
    protected static int backgroundPaddingLeft;
    protected static int backgroundPaddingTop;
    private AccelerateInterpolator accelerateInterpolator;
    private boolean allowCustomAnimation;
    private boolean applyBottomPadding;
    private boolean applyTopPadding;
    protected ColorDrawable backDrawable;
    protected Paint ciclePaint;
    protected ContainerView container;
    protected ViewGroup containerView;
    protected AnimatorSet currentSheetAnimation;
    private View customView;
    private DecelerateInterpolator decelerateInterpolator;
    private BottomSheetDelegateInterface delegate;
    private boolean dismissed;
    private boolean focusable;
    protected boolean fullWidth;
    private int[] itemIcons;
    private ArrayList<BottomSheetCell> itemViews;
    private CharSequence[] items;
    private WindowInsets lastInsets;
    private int layoutCount;
    private OnClickListener onClickListener;
    private OnFinishOpenAnimationListener onFinishOpenAnimation;
    private Drawable shadowDrawable;
    private Runnable startAnimationRunnable;
    private int tag;
    private CharSequence title;
    private int touchSlop;
    private boolean useFastDismiss;

    /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.1 */
    class C09741 implements OnApplyWindowInsetsListener {
        C09741() {
        }

        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            BottomSheet.this.lastInsets = windowInsets;
            view.requestLayout();
            return windowInsets.consumeSystemWindowInsets();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.2 */
    class C09752 extends FrameLayout {
        C09752(Context context) {
            super(context);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.3 */
    class C09763 implements OnTouchListener {
        C09763() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.4 */
    class C09774 implements View.OnClickListener {
        C09774() {
        }

        public void onClick(View view) {
            BottomSheet.this.dismissWithButtonClick(((Integer) view.getTag()).intValue());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.5 */
    class C09785 implements Runnable {
        C09785() {
        }

        public void run() {
            if (BottomSheet.this.startAnimationRunnable == this && !BottomSheet.this.dismissed) {
                BottomSheet.this.startAnimationRunnable = null;
                BottomSheet.this.startOpenAnimation();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.6 */
    class C09796 extends AnimatorListenerAdapterProxy {
        C09796() {
        }

        public void onAnimationCancel(Animator animator) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator)) {
                BottomSheet.this.currentSheetAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator)) {
                BottomSheet.this.currentSheetAnimation = null;
                if (BottomSheet.this.delegate != null) {
                    BottomSheet.this.delegate.onOpenAnimationEnd();
                }
                BottomSheet.this.container.setLayerType(0, null);
                if (BottomSheet.this.onFinishOpenAnimation != null) {
                    BottomSheet.this.onFinishOpenAnimation.onFinishOpenAnimation();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.7 */
    class C09817 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ int val$item;

        /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.7.1 */
        class C09801 implements Runnable {
            C09801() {
            }

            public void run() {
                try {
                    super.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        C09817(int i) {
            this.val$item = i;
        }

        public void onAnimationCancel(Animator animator) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator)) {
                BottomSheet.this.currentSheetAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator)) {
                BottomSheet.this.currentSheetAnimation = null;
                if (BottomSheet.this.onClickListener != null) {
                    BottomSheet.this.onClickListener.onClick(BottomSheet.this, this.val$item);
                }
                AndroidUtilities.runOnUIThread(new C09801());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.8 */
    class C09838 extends AnimatorListenerAdapterProxy {

        /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.8.1 */
        class C09821 implements Runnable {
            C09821() {
            }

            public void run() {
                try {
                    BottomSheet.this.dismissInternal();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        C09838() {
        }

        public void onAnimationCancel(Animator animator) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator)) {
                BottomSheet.this.currentSheetAnimation = null;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animator)) {
                BottomSheet.this.currentSheetAnimation = null;
                AndroidUtilities.runOnUIThread(new C09821());
            }
        }
    }

    public static class BottomSheetCell extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public BottomSheetCell(Context context, int i) {
            super(context);
            setBackgroundResource(C0338R.drawable.list_selector);
            setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(24, 24, (LocaleController.isRTL ? 5 : 3) | 16));
            this.textView = new TextView(context);
            this.textView.setTypeface(FontUtil.m1176a().m1161d());
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            if (i == 0) {
                this.textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
                this.textView.setTextSize(1, 16.0f);
                addView(this.textView, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 5 : 3) | 16));
            } else if (i == 1) {
                this.textView.setGravity(17);
                this.textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(FontUtil.m1176a().m1160c());
                addView(this.textView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
            }
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C0700C.ENCODING_PCM_32BIT));
        }

        public void setGravity(int i) {
            this.textView.setGravity(i);
        }

        public void setTextAndIcon(CharSequence charSequence, int i) {
            this.textView.setText(charSequence);
            if (i != 0) {
                this.imageView.setImageResource(i);
                this.imageView.setVisibility(0);
                this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(56.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(56.0f) : 0, 0);
                return;
            }
            this.imageView.setVisibility(4);
            this.textView.setPadding(0, 0, 0, 0);
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
        }
    }

    public interface BottomSheetDelegateInterface {
        void onOpenAnimationEnd();

        void onOpenAnimationStart();
    }

    public static class BottomSheetDelegate implements BottomSheetDelegateInterface {
        public void onOpenAnimationEnd() {
        }

        public void onOpenAnimationStart() {
        }
    }

    public static class Builder {
        private BottomSheet bottomSheet;

        /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.Builder.1 */
        class C09841 extends BottomSheet {
            C09841(Context context, boolean z, boolean z2) {
                super(context, z, z2);
            }

            protected boolean canDismissWithSwipe() {
                return false;
            }
        }

        public Builder(Context context) {
            this.bottomSheet = new BottomSheet(context, false);
        }

        public Builder(Context context, boolean z) {
            this.bottomSheet = new BottomSheet(context, z);
        }

        public Builder(Context context, boolean z, boolean z2) {
            this.bottomSheet = new C09841(context, z, z2);
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public Builder setApplyBottomPadding(boolean z) {
            this.bottomSheet.applyBottomPadding = z;
            return this;
        }

        public Builder setApplyTopPadding(boolean z) {
            this.bottomSheet.applyTopPadding = z;
            return this;
        }

        public Builder setCustomView(View view) {
            this.bottomSheet.customView = view;
            return this;
        }

        public Builder setDelegate(BottomSheetDelegate bottomSheetDelegate) {
            this.bottomSheet.setDelegate(bottomSheetDelegate);
            return this;
        }

        public Builder setItems(CharSequence[] charSequenceArr, OnClickListener onClickListener) {
            this.bottomSheet.items = charSequenceArr;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] charSequenceArr, int[] iArr, OnClickListener onClickListener) {
            this.bottomSheet.items = charSequenceArr;
            this.bottomSheet.itemIcons = iArr;
            this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setTag(int i) {
            this.bottomSheet.tag = i;
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.bottomSheet.title = charSequence;
            return this;
        }

        public BottomSheet setUseFullWidth(boolean z) {
            this.bottomSheet.fullWidth = z;
            return this.bottomSheet;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }
    }

    protected class ContainerView extends FrameLayout implements NestedScrollingParent {
        private AnimatorSet currentAnimation;
        private boolean maybeStartTracking;
        private NestedScrollingParentHelper nestedScrollingParentHelper;
        private boolean startedTracking;
        private int startedTrackingPointerId;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker;

        /* renamed from: com.hanista.mobogram.ui.ActionBar.BottomSheet.ContainerView.1 */
        class C09851 extends AnimatorListenerAdapterProxy {
            C09851() {
            }

            public void onAnimationEnd(Animator animator) {
                if (ContainerView.this.currentAnimation != null && ContainerView.this.currentAnimation.equals(animator)) {
                    ContainerView.this.currentAnimation = null;
                }
            }
        }

        public ContainerView(Context context) {
            super(context);
            this.velocityTracker = null;
            this.startedTrackingPointerId = -1;
            this.maybeStartTracking = false;
            this.startedTracking = false;
            this.currentAnimation = null;
            this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }

        private void cancelCurrentAnimation() {
            if (this.currentAnimation != null) {
                this.currentAnimation.cancel();
                this.currentAnimation = null;
            }
        }

        private void checkDismiss(float f, float f2) {
            float translationY = BottomSheet.this.containerView.getTranslationY();
            boolean z = (translationY < AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD, false) && (f2 < 3500.0f || Math.abs(f2) < Math.abs(f))) || (f2 < 0.0f && Math.abs(f2) >= 3500.0f);
            if (z) {
                this.currentAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.currentAnimation;
                Animator[] animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[]{0.0f});
                animatorSet.playTogether(animatorArr);
                this.currentAnimation.setDuration((long) ((int) (150.0f * (translationY / AndroidUtilities.getPixelsInCM(DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD, false)))));
                this.currentAnimation.setInterpolator(new DecelerateInterpolator());
                this.currentAnimation.addListener(new C09851());
                this.currentAnimation.start();
                return;
            }
            z = BottomSheet.this.allowCustomAnimation;
            BottomSheet.this.allowCustomAnimation = false;
            BottomSheet.this.useFastDismiss = true;
            BottomSheet.this.dismiss();
            BottomSheet.this.allowCustomAnimation = z;
        }

        public int getNestedScrollAxes() {
            return this.nestedScrollingParentHelper.getNestedScrollAxes();
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return BottomSheet.this.canDismissWithSwipe() ? onTouchEvent(motionEvent) : super.onInterceptTouchEvent(motionEvent);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            BottomSheet.this.layoutCount = BottomSheet.this.layoutCount - 1;
            if (BottomSheet.this.containerView != null) {
                int measuredHeight = (i4 - i2) - BottomSheet.this.containerView.getMeasuredHeight();
                if (BottomSheet.this.lastInsets != null && VERSION.SDK_INT >= 21) {
                    i += BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                    i3 += BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                }
                int measuredWidth = ((i3 - i) - BottomSheet.this.containerView.getMeasuredWidth()) / 2;
                BottomSheet.this.containerView.layout(measuredWidth, measuredHeight, BottomSheet.this.containerView.getMeasuredWidth() + measuredWidth, BottomSheet.this.containerView.getMeasuredHeight() + measuredHeight);
            }
            int i5 = i3;
            int i6 = i;
            int childCount = getChildCount();
            for (int i7 = 0; i7 < childCount; i7++) {
                View childAt = getChildAt(i7);
                if (!(childAt.getVisibility() == 8 || childAt == BottomSheet.this.containerView || BottomSheet.this.onCustomLayout(childAt, i6, i2, i5, i4))) {
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    int measuredWidth2 = childAt.getMeasuredWidth();
                    int measuredHeight2 = childAt.getMeasuredHeight();
                    int i8 = layoutParams.gravity;
                    if (i8 == -1) {
                        i8 = 51;
                    }
                    int i9 = i8 & 112;
                    switch ((i8 & 7) & 7) {
                        case VideoPlayer.TYPE_AUDIO /*1*/:
                            i8 = ((((i5 - i6) - measuredWidth2) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                            break;
                        case VideoPlayer.STATE_ENDED /*5*/:
                            i8 = (i5 - measuredWidth2) - layoutParams.rightMargin;
                            break;
                        default:
                            i8 = layoutParams.leftMargin;
                            break;
                    }
                    switch (i9) {
                        case TLRPC.USER_FLAG_PHONE /*16*/:
                            measuredHeight = ((((i4 - i2) - measuredHeight2) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                            break;
                        case NalUnitTypes.NAL_TYPE_UNSPEC48 /*48*/:
                            measuredHeight = layoutParams.topMargin;
                            break;
                        case 80:
                            measuredHeight = ((i4 - i2) - measuredHeight2) - layoutParams.bottomMargin;
                            break;
                        default:
                            measuredHeight = layoutParams.topMargin;
                            break;
                    }
                    childAt.layout(i8, measuredHeight, measuredWidth2 + i8, measuredHeight2 + measuredHeight);
                }
            }
            if (BottomSheet.this.layoutCount == 0 && BottomSheet.this.startAnimationRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(BottomSheet.this.startAnimationRunnable);
                BottomSheet.this.startAnimationRunnable.run();
                BottomSheet.this.startAnimationRunnable = null;
            }
        }

        protected void onMeasure(int i, int i2) {
            int i3;
            int i4;
            int size = MeasureSpec.getSize(i);
            int size2 = MeasureSpec.getSize(i2);
            if (BottomSheet.this.lastInsets == null || VERSION.SDK_INT < 21) {
                i3 = size2;
                i4 = size;
            } else {
                i3 = size2 - BottomSheet.this.lastInsets.getSystemWindowInsetBottom();
                i4 = size - (BottomSheet.this.lastInsets.getSystemWindowInsetRight() + BottomSheet.this.lastInsets.getSystemWindowInsetLeft());
            }
            setMeasuredDimension(i4, i3);
            size2 = i4 < i3 ? 1 : 0;
            if (BottomSheet.this.containerView != null) {
                if (BottomSheet.this.fullWidth) {
                    BottomSheet.this.containerView.measure(MeasureSpec.makeMeasureSpec((BottomSheet.backgroundPaddingLeft * 2) + i4, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(i3, TLRPC.MESSAGE_FLAG_MEGAGROUP));
                } else {
                    if (AndroidUtilities.isTablet()) {
                        size2 = MeasureSpec.makeMeasureSpec(((int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD)) + (BottomSheet.backgroundPaddingLeft * 2), C0700C.ENCODING_PCM_32BIT);
                    } else {
                        size2 = MeasureSpec.makeMeasureSpec(size2 != 0 ? (BottomSheet.backgroundPaddingLeft * 2) + i4 : ((int) Math.max(((float) i4) * DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD, (float) Math.min(AndroidUtilities.dp(480.0f), i4))) + (BottomSheet.backgroundPaddingLeft * 2), C0700C.ENCODING_PCM_32BIT);
                    }
                    BottomSheet.this.containerView.measure(size2, MeasureSpec.makeMeasureSpec(i3, TLRPC.MESSAGE_FLAG_MEGAGROUP));
                }
            }
            int childCount = getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                if (!(childAt.getVisibility() == 8 || childAt == BottomSheet.this.containerView || BottomSheet.this.onCustomMeasure(childAt, i4, i3))) {
                    measureChildWithMargins(childAt, MeasureSpec.makeMeasureSpec(i4, C0700C.ENCODING_PCM_32BIT), 0, MeasureSpec.makeMeasureSpec(i3, C0700C.ENCODING_PCM_32BIT), 0);
                }
            }
        }

        public boolean onNestedFling(View view, float f, float f2, boolean z) {
            return false;
        }

        public boolean onNestedPreFling(View view, float f, float f2) {
            return false;
        }

        public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
            float f = 0.0f;
            if (!BottomSheet.this.dismissed) {
                cancelCurrentAnimation();
                float translationY = BottomSheet.this.containerView.getTranslationY();
                if (translationY > 0.0f && i2 > 0) {
                    translationY -= (float) i2;
                    iArr[1] = i2;
                    if (translationY < 0.0f) {
                        iArr[1] = (int) (((float) iArr[1]) + 0.0f);
                    } else {
                        f = translationY;
                    }
                    BottomSheet.this.containerView.setTranslationY(f);
                }
            }
        }

        public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
            float f = 0.0f;
            if (!BottomSheet.this.dismissed) {
                cancelCurrentAnimation();
                if (i4 != 0) {
                    float translationY = BottomSheet.this.containerView.getTranslationY() - ((float) i4);
                    if (translationY >= 0.0f) {
                        f = translationY;
                    }
                    BottomSheet.this.containerView.setTranslationY(f);
                }
            }
        }

        public void onNestedScrollAccepted(View view, View view2, int i) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
            if (!BottomSheet.this.dismissed) {
                cancelCurrentAnimation();
            }
        }

        public boolean onStartNestedScroll(View view, View view2, int i) {
            return (BottomSheet.this.dismissed || i != 2 || BottomSheet.this.canDismissWithSwipe()) ? false : true;
        }

        public void onStopNestedScroll(View view) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
            if (!BottomSheet.this.dismissed) {
                BottomSheet.this.containerView.getTranslationY();
                checkDismiss(0.0f, 0.0f);
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float f = 0.0f;
            if (BottomSheet.this.dismissed) {
                return false;
            }
            if (BottomSheet.this.onContainerTouchEvent(motionEvent)) {
                return true;
            }
            if (BottomSheet.this.canDismissWithTouchOutside() && motionEvent != null && ((motionEvent.getAction() == 0 || motionEvent.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking)) {
                this.startedTrackingX = (int) motionEvent.getX();
                this.startedTrackingY = (int) motionEvent.getY();
                if (this.startedTrackingY < BottomSheet.this.containerView.getTop() || this.startedTrackingX < BottomSheet.this.containerView.getLeft() || this.startedTrackingX > BottomSheet.this.containerView.getRight()) {
                    BottomSheet.this.dismiss();
                    return true;
                }
                this.startedTrackingPointerId = motionEvent.getPointerId(0);
                this.maybeStartTracking = true;
                cancelCurrentAnimation();
                if (this.velocityTracker != null) {
                    this.velocityTracker.clear();
                }
            } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                r1 = (float) Math.abs((int) (motionEvent.getX() - ((float) this.startedTrackingX)));
                float y = (float) (((int) motionEvent.getY()) - this.startedTrackingY);
                this.velocityTracker.addMovement(motionEvent);
                if (this.maybeStartTracking && !this.startedTracking && y > 0.0f && y / 3.0f > Math.abs(r1) && Math.abs(y) >= ((float) BottomSheet.this.touchSlop)) {
                    this.startedTrackingY = (int) motionEvent.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    r1 = BottomSheet.this.containerView.getTranslationY() + y;
                    if (r1 >= 0.0f) {
                        f = r1;
                    }
                    BottomSheet.this.containerView.setTranslationY(f);
                    this.startedTrackingY = (int) motionEvent.getY();
                }
            } else if (motionEvent == null || (motionEvent != null && motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(PointerIconCompat.TYPE_DEFAULT);
                r1 = BottomSheet.this.containerView.getTranslationY();
                if (this.startedTracking || r1 != 0.0f) {
                    checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                    this.startedTracking = false;
                } else {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                }
                if (this.velocityTracker != null) {
                    this.velocityTracker.recycle();
                    this.velocityTracker = null;
                }
                this.startedTrackingPointerId = -1;
            }
            boolean z = this.startedTracking || !BottomSheet.this.canDismissWithSwipe();
            return z;
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            if (this.maybeStartTracking && !this.startedTracking) {
                onTouchEvent(null);
            }
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    public interface OnFinishOpenAnimationListener {
        void onFinishOpenAnimation();
    }

    public BottomSheet(Context context, boolean z) {
        this(context, z, false);
    }

    public BottomSheet(Context context, boolean z, boolean z2) {
        super(context, z2 ? C0338R.style.TransparentDialogWithActionMode : C0338R.style.TransparentDialog);
        this.backDrawable = new ColorDrawable(Theme.MSG_TEXT_COLOR);
        this.allowCustomAnimation = true;
        this.ciclePaint = new Paint(1);
        this.applyTopPadding = true;
        this.applyBottomPadding = true;
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.accelerateInterpolator = new AccelerateInterpolator();
        this.itemViews = new ArrayList();
        if (VERSION.SDK_INT >= 21) {
            getWindow().addFlags(-2147417856);
        }
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Rect rect = new Rect();
        this.shadowDrawable = context.getResources().getDrawable(C0338R.drawable.sheet_shadow);
        this.shadowDrawable.getPadding(rect);
        backgroundPaddingLeft = rect.left;
        backgroundPaddingTop = rect.top;
        this.container = new ContainerView(getContext());
        this.container.setBackgroundDrawable(this.backDrawable);
        this.focusable = z;
        if (VERSION.SDK_INT >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new C09741());
            this.container.setSystemUiVisibility(1280);
        }
        this.ciclePaint.setColor(-1);
        this.backDrawable.setAlpha(0);
    }

    private void cancelSheetAnimation() {
        if (this.currentSheetAnimation != null) {
            this.currentSheetAnimation.cancel();
            this.currentSheetAnimation = null;
        }
    }

    private void startOpenAnimation() {
        if (!this.dismissed) {
            this.containerView.setVisibility(0);
            if (!onCustomOpenAnimation()) {
                if (VERSION.SDK_INT >= 20) {
                    this.container.setLayerType(2, null);
                }
                this.containerView.setTranslationY((float) this.containerView.getMeasuredHeight());
                AnimatorSet animatorSet = new AnimatorSet();
                r1 = new Animator[2];
                r1[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{0.0f});
                r1[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{51});
                animatorSet.playTogether(r1);
                animatorSet.setDuration(200);
                animatorSet.setStartDelay(20);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new C09796());
                animatorSet.start();
                this.currentSheetAnimation = animatorSet;
            }
        }
    }

    protected boolean canDismissWithSwipe() {
        return true;
    }

    protected boolean canDismissWithTouchOutside() {
        return true;
    }

    public void dismiss() {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            if (!this.allowCustomAnimation || !onCustomCloseAnimation()) {
                AnimatorSet animatorSet = new AnimatorSet();
                r1 = new Animator[2];
                r1[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{(float) (this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
                r1[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{0});
                animatorSet.playTogether(r1);
                if (this.useFastDismiss) {
                    int measuredHeight = this.containerView.getMeasuredHeight();
                    animatorSet.setDuration((long) Math.max(60, (int) ((BitmapDescriptorFactory.HUE_CYAN * (((float) measuredHeight) - this.containerView.getTranslationY())) / ((float) measuredHeight))));
                    this.useFastDismiss = false;
                } else {
                    animatorSet.setDuration(180);
                }
                animatorSet.setInterpolator(new AccelerateInterpolator());
                animatorSet.addListener(new C09838());
                animatorSet.start();
                this.currentSheetAnimation = animatorSet;
            }
        }
    }

    public void dismissInternal() {
        try {
            super.dismiss();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public void dismissWithButtonClick(int i) {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            r1 = new Animator[2];
            r1[0] = ObjectAnimator.ofFloat(this.containerView, "translationY", new float[]{(float) (this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f))});
            r1[1] = ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{0});
            animatorSet.playTogether(r1);
            animatorSet.setDuration(180);
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.addListener(new C09817(i));
            animatorSet.start();
            this.currentSheetAnimation = animatorSet;
        }
    }

    public FrameLayout getContainer() {
        return this.container;
    }

    public ViewGroup getSheetContainer() {
        return this.containerView;
    }

    public int getTag() {
        return this.tag;
    }

    public boolean isDismissed() {
        return this.dismissed;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        window.setWindowAnimations(C0338R.style.DialogNoAnimation);
        setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        if (this.containerView == null) {
            this.containerView = new C09752(getContext());
            this.containerView.setBackgroundDrawable(this.shadowDrawable);
            this.containerView.setPadding(backgroundPaddingLeft, backgroundPaddingTop + (this.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0), backgroundPaddingLeft, this.applyBottomPadding ? AndroidUtilities.dp(8.0f) : 0);
        }
        if (VERSION.SDK_INT >= 21) {
            this.containerView.setFitsSystemWindows(true);
        }
        this.containerView.setVisibility(4);
        this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        if (this.customView != null) {
            if (this.customView.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2, 51));
        } else {
            int i;
            if (this.title != null) {
                View textView = new TextView(getContext());
                textView.setTypeface(FontUtil.m1176a().m1161d());
                textView.setLines(1);
                textView.setSingleLine(true);
                textView.setText(this.title);
                textView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
                textView.setTextSize(1, 16.0f);
                textView.setEllipsize(TruncateAt.MIDDLE);
                textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
                textView.setGravity(16);
                this.containerView.addView(textView, LayoutHelper.createFrame(-1, 48.0f));
                textView.setOnTouchListener(new C09763());
                i = 48;
            } else {
                i = 0;
            }
            if (this.items != null) {
                int i2 = 0;
                int i3 = i;
                while (i2 < this.items.length) {
                    View bottomSheetCell = new BottomSheetCell(getContext(), 0);
                    bottomSheetCell.setTextAndIcon(this.items[i2], this.itemIcons != null ? this.itemIcons[i2] : 0);
                    this.containerView.addView(bottomSheetCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, (float) i3, 0.0f, 0.0f));
                    int i4 = i3 + 48;
                    bottomSheetCell.setTag(Integer.valueOf(i2));
                    bottomSheetCell.setOnClickListener(new C09774());
                    this.itemViews.add(bottomSheetCell);
                    i2++;
                    i3 = i4;
                }
            }
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.gravity = 51;
        attributes.dimAmount = 0.0f;
        attributes.flags &= -3;
        if (!this.focusable) {
            attributes.flags |= AccessibilityNodeInfoCompat.ACTION_SET_SELECTION;
        }
        attributes.height = -1;
        window.setAttributes(attributes);
    }

    protected boolean onCustomCloseAnimation() {
        return false;
    }

    protected boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        return false;
    }

    protected boolean onCustomMeasure(View view, int i, int i2) {
        return false;
    }

    protected boolean onCustomOpenAnimation() {
        return false;
    }

    public void setApplyBottomPadding(boolean z) {
        this.applyBottomPadding = z;
    }

    public void setApplyTopPadding(boolean z) {
        this.applyTopPadding = z;
    }

    public void setCustomView(View view) {
        this.customView = view;
    }

    public void setDelegate(BottomSheetDelegateInterface bottomSheetDelegateInterface) {
        this.delegate = bottomSheetDelegateInterface;
    }

    public void setItemText(int i, CharSequence charSequence) {
        if (i >= 0 && i < this.itemViews.size()) {
            ((BottomSheetCell) this.itemViews.get(i)).textView.setText(charSequence);
        }
    }

    public void setOnFinishOpenAnimation(OnFinishOpenAnimationListener onFinishOpenAnimationListener) {
        this.onFinishOpenAnimation = onFinishOpenAnimationListener;
    }

    public void setTitle(CharSequence charSequence) {
        this.title = charSequence;
    }

    public void show() {
        super.show();
        if (this.focusable) {
            getWindow().setSoftInputMode(16);
        }
        this.dismissed = false;
        cancelSheetAnimation();
        if (this.containerView.getMeasuredHeight() == 0) {
            this.containerView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, TLRPC.MESSAGE_FLAG_MEGAGROUP));
        }
        this.backDrawable.setAlpha(0);
        if (VERSION.SDK_INT >= 18) {
            this.layoutCount = 2;
            Runnable c09785 = new C09785();
            this.startAnimationRunnable = c09785;
            AndroidUtilities.runOnUIThread(c09785, 150);
            return;
        }
        startOpenAnimation();
    }
}

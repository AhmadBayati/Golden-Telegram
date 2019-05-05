package com.hanista.mobogram.messenger.support.widget.helper;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.animation.AnimatorListenerCompat;
import android.support.v4.animation.AnimatorUpdateListenerCompat;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ChildDrawingOrderCallback;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemAnimator;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ItemDecoration;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnChildAttachStateChangeListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnItemTouchListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.State;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.List;

public class ItemTouchHelper extends ItemDecoration implements OnChildAttachStateChangeListener {
    private static final int ACTION_MODE_DRAG_MASK = 16711680;
    private static final int ACTION_MODE_IDLE_MASK = 255;
    private static final int ACTION_MODE_SWIPE_MASK = 65280;
    public static final int ACTION_STATE_DRAG = 2;
    public static final int ACTION_STATE_IDLE = 0;
    public static final int ACTION_STATE_SWIPE = 1;
    private static final int ACTIVE_POINTER_ID_NONE = -1;
    public static final int ANIMATION_TYPE_DRAG = 8;
    public static final int ANIMATION_TYPE_SWIPE_CANCEL = 4;
    public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 2;
    private static final boolean DEBUG = false;
    private static final int DIRECTION_FLAG_COUNT = 8;
    public static final int DOWN = 2;
    public static final int END = 32;
    public static final int LEFT = 4;
    private static final int PIXELS_PER_SECOND = 1000;
    public static final int RIGHT = 8;
    public static final int START = 16;
    private static final String TAG = "ItemTouchHelper";
    public static final int UP = 1;
    int mActionState;
    int mActivePointerId;
    Callback mCallback;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback;
    private List<Integer> mDistances;
    private long mDragScrollStartTimeInMs;
    float mDx;
    float mDy;
    private GestureDetectorCompat mGestureDetector;
    float mInitialTouchX;
    float mInitialTouchY;
    float mMaxSwipeVelocity;
    private final OnItemTouchListener mOnItemTouchListener;
    private View mOverdrawChild;
    private int mOverdrawChildPosition;
    final List<View> mPendingCleanup;
    List<RecoverAnimation> mRecoverAnimations;
    private RecyclerView mRecyclerView;
    private final Runnable mScrollRunnable;
    ViewHolder mSelected;
    int mSelectedFlags;
    float mSelectedStartX;
    float mSelectedStartY;
    private int mSlop;
    private List<ViewHolder> mSwapTargets;
    float mSwipeEscapeVelocity;
    private final float[] mTmpPosition;
    private Rect mTmpRect;
    private VelocityTracker mVelocityTracker;

    public interface ViewDropHandler {
        void prepareForDrop(View view, View view2, int i, int i2);
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.1 */
    class C08741 implements Runnable {
        C08741() {
        }

        public void run() {
            if (ItemTouchHelper.this.mSelected != null && ItemTouchHelper.this.scrollIfNecessary()) {
                if (ItemTouchHelper.this.mSelected != null) {
                    ItemTouchHelper.this.moveIfNecessary(ItemTouchHelper.this.mSelected);
                }
                ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
                ViewCompat.postOnAnimation(ItemTouchHelper.this.mRecyclerView, this);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.2 */
    class C08752 implements OnItemTouchListener {
        C08752() {
        }

        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            ItemTouchHelper.this.mGestureDetector.onTouchEvent(motionEvent);
            int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
            if (actionMasked == 0) {
                ItemTouchHelper.this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, ItemTouchHelper.ACTION_STATE_IDLE);
                ItemTouchHelper.this.mInitialTouchX = motionEvent.getX();
                ItemTouchHelper.this.mInitialTouchY = motionEvent.getY();
                ItemTouchHelper.this.obtainVelocityTracker();
                if (ItemTouchHelper.this.mSelected == null) {
                    RecoverAnimation access$600 = ItemTouchHelper.this.findAnimation(motionEvent);
                    if (access$600 != null) {
                        ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
                        itemTouchHelper.mInitialTouchX -= access$600.mX;
                        itemTouchHelper = ItemTouchHelper.this;
                        itemTouchHelper.mInitialTouchY -= access$600.mY;
                        ItemTouchHelper.this.endRecoverAnimation(access$600.mViewHolder, true);
                        if (ItemTouchHelper.this.mPendingCleanup.remove(access$600.mViewHolder.itemView)) {
                            ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, access$600.mViewHolder);
                        }
                        ItemTouchHelper.this.select(access$600.mViewHolder, access$600.mActionState);
                        ItemTouchHelper.this.updateDxDy(motionEvent, ItemTouchHelper.this.mSelectedFlags, ItemTouchHelper.ACTION_STATE_IDLE);
                    }
                }
            } else if (actionMasked == 3 || actionMasked == ItemTouchHelper.UP) {
                ItemTouchHelper.this.mActivePointerId = ItemTouchHelper.ACTIVE_POINTER_ID_NONE;
                ItemTouchHelper.this.select(null, ItemTouchHelper.ACTION_STATE_IDLE);
            } else if (ItemTouchHelper.this.mActivePointerId != ItemTouchHelper.ACTIVE_POINTER_ID_NONE) {
                int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, ItemTouchHelper.this.mActivePointerId);
                if (findPointerIndex >= 0) {
                    ItemTouchHelper.this.checkSelectForSwipe(actionMasked, motionEvent, findPointerIndex);
                }
            }
            if (ItemTouchHelper.this.mVelocityTracker != null) {
                ItemTouchHelper.this.mVelocityTracker.addMovement(motionEvent);
            }
            return ItemTouchHelper.this.mSelected != null ? true : ItemTouchHelper.DEBUG;
        }

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
            if (z) {
                ItemTouchHelper.this.select(null, ItemTouchHelper.ACTION_STATE_IDLE);
            }
        }

        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            int i = ItemTouchHelper.ACTION_STATE_IDLE;
            ItemTouchHelper.this.mGestureDetector.onTouchEvent(motionEvent);
            if (ItemTouchHelper.this.mVelocityTracker != null) {
                ItemTouchHelper.this.mVelocityTracker.addMovement(motionEvent);
            }
            if (ItemTouchHelper.this.mActivePointerId != ItemTouchHelper.ACTIVE_POINTER_ID_NONE) {
                int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
                int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, ItemTouchHelper.this.mActivePointerId);
                if (findPointerIndex >= 0) {
                    ItemTouchHelper.this.checkSelectForSwipe(actionMasked, motionEvent, findPointerIndex);
                }
                ViewHolder viewHolder = ItemTouchHelper.this.mSelected;
                if (viewHolder != null) {
                    switch (actionMasked) {
                        case ItemTouchHelper.UP /*1*/:
                            break;
                        case ItemTouchHelper.DOWN /*2*/:
                            if (findPointerIndex >= 0) {
                                ItemTouchHelper.this.updateDxDy(motionEvent, ItemTouchHelper.this.mSelectedFlags, findPointerIndex);
                                ItemTouchHelper.this.moveIfNecessary(viewHolder);
                                ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
                                ItemTouchHelper.this.mScrollRunnable.run();
                                ItemTouchHelper.this.mRecyclerView.invalidate();
                                return;
                            }
                            return;
                        case VideoPlayer.STATE_BUFFERING /*3*/:
                            if (ItemTouchHelper.this.mVelocityTracker != null) {
                                ItemTouchHelper.this.mVelocityTracker.clear();
                                break;
                            }
                            break;
                        case Method.TRACE /*6*/:
                            actionMasked = MotionEventCompat.getActionIndex(motionEvent);
                            if (MotionEventCompat.getPointerId(motionEvent, actionMasked) == ItemTouchHelper.this.mActivePointerId) {
                                if (actionMasked == 0) {
                                    i = ItemTouchHelper.UP;
                                }
                                ItemTouchHelper.this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, i);
                                ItemTouchHelper.this.updateDxDy(motionEvent, ItemTouchHelper.this.mSelectedFlags, actionMasked);
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                    ItemTouchHelper.this.select(null, ItemTouchHelper.ACTION_STATE_IDLE);
                    ItemTouchHelper.this.mActivePointerId = ItemTouchHelper.ACTIVE_POINTER_ID_NONE;
                }
            }
        }
    }

    private class RecoverAnimation implements AnimatorListenerCompat {
        final int mActionState;
        private final int mAnimationType;
        private boolean mEnded;
        private float mFraction;
        public boolean mIsPendingCleanup;
        boolean mOverridden;
        final float mStartDx;
        final float mStartDy;
        final float mTargetX;
        final float mTargetY;
        private final ValueAnimatorCompat mValueAnimator;
        final ViewHolder mViewHolder;
        float mX;
        float mY;

        /* renamed from: com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.RecoverAnimation.1 */
        class C08811 implements AnimatorUpdateListenerCompat {
            final /* synthetic */ ItemTouchHelper val$this$0;

            C08811(ItemTouchHelper itemTouchHelper) {
                this.val$this$0 = itemTouchHelper;
            }

            public void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat) {
                RecoverAnimation.this.setFraction(valueAnimatorCompat.getAnimatedFraction());
            }
        }

        public RecoverAnimation(ViewHolder viewHolder, int i, int i2, float f, float f2, float f3, float f4) {
            this.mOverridden = ItemTouchHelper.DEBUG;
            this.mEnded = ItemTouchHelper.DEBUG;
            this.mActionState = i2;
            this.mAnimationType = i;
            this.mViewHolder = viewHolder;
            this.mStartDx = f;
            this.mStartDy = f2;
            this.mTargetX = f3;
            this.mTargetY = f4;
            this.mValueAnimator = AnimatorCompatHelper.emptyValueAnimator();
            this.mValueAnimator.addUpdateListener(new C08811(ItemTouchHelper.this));
            this.mValueAnimator.setTarget(viewHolder.itemView);
            this.mValueAnimator.addListener(this);
            setFraction(0.0f);
        }

        public void cancel() {
            this.mValueAnimator.cancel();
        }

        public void onAnimationCancel(ValueAnimatorCompat valueAnimatorCompat) {
            setFraction(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }

        public void onAnimationEnd(ValueAnimatorCompat valueAnimatorCompat) {
            if (!this.mEnded) {
                this.mViewHolder.setIsRecyclable(true);
            }
            this.mEnded = true;
        }

        public void onAnimationRepeat(ValueAnimatorCompat valueAnimatorCompat) {
        }

        public void onAnimationStart(ValueAnimatorCompat valueAnimatorCompat) {
        }

        public void setDuration(long j) {
            this.mValueAnimator.setDuration(j);
        }

        public void setFraction(float f) {
            this.mFraction = f;
        }

        public void start() {
            this.mViewHolder.setIsRecyclable(ItemTouchHelper.DEBUG);
            this.mValueAnimator.start();
        }

        public void update() {
            if (this.mStartDx == this.mTargetX) {
                this.mX = ViewCompat.getTranslationX(this.mViewHolder.itemView);
            } else {
                this.mX = this.mStartDx + (this.mFraction * (this.mTargetX - this.mStartDx));
            }
            if (this.mStartDy == this.mTargetY) {
                this.mY = ViewCompat.getTranslationY(this.mViewHolder.itemView);
            } else {
                this.mY = this.mStartDy + (this.mFraction * (this.mTargetY - this.mStartDy));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.3 */
    class C08763 extends RecoverAnimation {
        final /* synthetic */ ViewHolder val$prevSelected;
        final /* synthetic */ int val$swipeDir;

        C08763(ViewHolder viewHolder, int i, int i2, float f, float f2, float f3, float f4, int i3, ViewHolder viewHolder2) {
            this.val$swipeDir = i3;
            this.val$prevSelected = viewHolder2;
            super(viewHolder, i, i2, f, f2, f3, f4);
        }

        public void onAnimationEnd(ValueAnimatorCompat valueAnimatorCompat) {
            super.onAnimationEnd(valueAnimatorCompat);
            if (!this.mOverridden) {
                if (this.val$swipeDir <= 0) {
                    ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, this.val$prevSelected);
                } else {
                    ItemTouchHelper.this.mPendingCleanup.add(this.val$prevSelected.itemView);
                    this.mIsPendingCleanup = true;
                    if (this.val$swipeDir > 0) {
                        ItemTouchHelper.this.postDispatchSwipe(this, this.val$swipeDir);
                    }
                }
                if (ItemTouchHelper.this.mOverdrawChild == this.val$prevSelected.itemView) {
                    ItemTouchHelper.this.removeChildDrawingOrderCallbackIfNecessary(this.val$prevSelected.itemView);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.4 */
    class C08774 implements Runnable {
        final /* synthetic */ RecoverAnimation val$anim;
        final /* synthetic */ int val$swipeDir;

        C08774(RecoverAnimation recoverAnimation, int i) {
            this.val$anim = recoverAnimation;
            this.val$swipeDir = i;
        }

        public void run() {
            if (ItemTouchHelper.this.mRecyclerView != null && ItemTouchHelper.this.mRecyclerView.isAttachedToWindow() && !this.val$anim.mOverridden && this.val$anim.mViewHolder.getAdapterPosition() != ItemTouchHelper.ACTIVE_POINTER_ID_NONE) {
                ItemAnimator itemAnimator = ItemTouchHelper.this.mRecyclerView.getItemAnimator();
                if ((itemAnimator == null || !itemAnimator.isRunning(null)) && !ItemTouchHelper.this.hasRunningRecoverAnim()) {
                    ItemTouchHelper.this.mCallback.onSwiped(this.val$anim.mViewHolder, this.val$swipeDir);
                } else {
                    ItemTouchHelper.this.mRecyclerView.post(this);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.5 */
    class C08785 implements ChildDrawingOrderCallback {
        C08785() {
        }

        public int onGetChildDrawingOrder(int i, int i2) {
            if (ItemTouchHelper.this.mOverdrawChild == null) {
                return i2;
            }
            int access$2300 = ItemTouchHelper.this.mOverdrawChildPosition;
            if (access$2300 == ItemTouchHelper.ACTIVE_POINTER_ID_NONE) {
                access$2300 = ItemTouchHelper.this.mRecyclerView.indexOfChild(ItemTouchHelper.this.mOverdrawChild);
                ItemTouchHelper.this.mOverdrawChildPosition = access$2300;
            }
            return i2 == i + ItemTouchHelper.ACTIVE_POINTER_ID_NONE ? access$2300 : i2 >= access$2300 ? i2 + ItemTouchHelper.UP : i2;
        }
    }

    public static abstract class Callback {
        private static final int ABS_HORIZONTAL_DIR_FLAGS = 789516;
        public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;
        public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;
        private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 500;
        static final int RELATIVE_DIR_FLAGS = 3158064;
        private static final Interpolator sDragScrollInterpolator;
        private static final Interpolator sDragViewScrollCapInterpolator;
        private static final ItemTouchUIUtil sUICallback;
        private int mCachedMaxScrollSpeed;

        /* renamed from: com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback.1 */
        static class C08791 implements Interpolator {
            C08791() {
            }

            public float getInterpolation(float f) {
                return (((f * f) * f) * f) * f;
            }
        }

        /* renamed from: com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback.2 */
        static class C08802 implements Interpolator {
            C08802() {
            }

            public float getInterpolation(float f) {
                float f2 = f - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                return (f2 * (((f2 * f2) * f2) * f2)) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            }
        }

        static {
            sDragScrollInterpolator = new C08791();
            sDragViewScrollCapInterpolator = new C08802();
            if (VERSION.SDK_INT >= 21) {
                sUICallback = new Lollipop();
            } else if (VERSION.SDK_INT >= 11) {
                sUICallback = new Honeycomb();
            } else {
                sUICallback = new Gingerbread();
            }
        }

        public Callback() {
            this.mCachedMaxScrollSpeed = ItemTouchHelper.ACTIVE_POINTER_ID_NONE;
        }

        public static int convertToRelativeDirection(int i, int i2) {
            int i3 = i & ABS_HORIZONTAL_DIR_FLAGS;
            if (i3 == 0) {
                return i;
            }
            int i4 = (i3 ^ ItemTouchHelper.ACTIVE_POINTER_ID_NONE) & i;
            return i2 == 0 ? i4 | (i3 << ItemTouchHelper.DOWN) : (i4 | ((i3 << ItemTouchHelper.UP) & -789517)) | (((i3 << ItemTouchHelper.UP) & ABS_HORIZONTAL_DIR_FLAGS) << ItemTouchHelper.DOWN);
        }

        public static ItemTouchUIUtil getDefaultUIUtil() {
            return sUICallback;
        }

        private int getMaxDragScroll(RecyclerView recyclerView) {
            if (this.mCachedMaxScrollSpeed == ItemTouchHelper.ACTIVE_POINTER_ID_NONE) {
                this.mCachedMaxScrollSpeed = AndroidUtilities.dp(20.0f);
            }
            return this.mCachedMaxScrollSpeed;
        }

        private boolean hasDragFlag(RecyclerView recyclerView, ViewHolder viewHolder) {
            return (getAbsoluteMovementFlags(recyclerView, viewHolder) & ItemTouchHelper.ACTION_MODE_DRAG_MASK) != 0 ? true : ItemTouchHelper.DEBUG;
        }

        private boolean hasSwipeFlag(RecyclerView recyclerView, ViewHolder viewHolder) {
            return (getAbsoluteMovementFlags(recyclerView, viewHolder) & ItemTouchHelper.ACTION_MODE_SWIPE_MASK) != 0 ? true : ItemTouchHelper.DEBUG;
        }

        public static int makeFlag(int i, int i2) {
            return i2 << (i * ItemTouchHelper.RIGHT);
        }

        public static int makeMovementFlags(int i, int i2) {
            return (makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, i2 | i) | makeFlag(ItemTouchHelper.UP, i2)) | makeFlag(ItemTouchHelper.DOWN, i);
        }

        private void onDraw(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, List<RecoverAnimation> list, int i, float f, float f2) {
            int i2;
            int size = list.size();
            for (i2 = ItemTouchHelper.ACTION_STATE_IDLE; i2 < size; i2 += ItemTouchHelper.UP) {
                RecoverAnimation recoverAnimation = (RecoverAnimation) list.get(i2);
                recoverAnimation.update();
                int save = canvas.save();
                onChildDraw(canvas, recyclerView, recoverAnimation.mViewHolder, recoverAnimation.mX, recoverAnimation.mY, recoverAnimation.mActionState, ItemTouchHelper.DEBUG);
                canvas.restoreToCount(save);
            }
            if (viewHolder != null) {
                i2 = canvas.save();
                onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, true);
                canvas.restoreToCount(i2);
            }
        }

        private void onDrawOver(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, List<RecoverAnimation> list, int i, float f, float f2) {
            int i2;
            int size = list.size();
            for (i2 = ItemTouchHelper.ACTION_STATE_IDLE; i2 < size; i2 += ItemTouchHelper.UP) {
                RecoverAnimation recoverAnimation = (RecoverAnimation) list.get(i2);
                int save = canvas.save();
                onChildDrawOver(canvas, recyclerView, recoverAnimation.mViewHolder, recoverAnimation.mX, recoverAnimation.mY, recoverAnimation.mActionState, ItemTouchHelper.DEBUG);
                canvas.restoreToCount(save);
            }
            if (viewHolder != null) {
                i2 = canvas.save();
                onChildDrawOver(canvas, recyclerView, viewHolder, f, f2, i, true);
                canvas.restoreToCount(i2);
            }
            Object obj = null;
            int i3 = size + ItemTouchHelper.ACTIVE_POINTER_ID_NONE;
            while (i3 >= 0) {
                Object obj2;
                recoverAnimation = (RecoverAnimation) list.get(i3);
                if (!recoverAnimation.mEnded || recoverAnimation.mIsPendingCleanup) {
                    obj2 = !recoverAnimation.mEnded ? ItemTouchHelper.UP : obj;
                } else {
                    list.remove(i3);
                    obj2 = obj;
                }
                i3 += ItemTouchHelper.ACTIVE_POINTER_ID_NONE;
                obj = obj2;
            }
            if (obj != null) {
                recyclerView.invalidate();
            }
        }

        public boolean canDropOver(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
            return true;
        }

        public ViewHolder chooseDropTarget(ViewHolder viewHolder, List<ViewHolder> list, int i, int i2) {
            int width = i + viewHolder.itemView.getWidth();
            int height = i2 + viewHolder.itemView.getHeight();
            ViewHolder viewHolder2 = null;
            int i3 = ItemTouchHelper.ACTIVE_POINTER_ID_NONE;
            int left = i - viewHolder.itemView.getLeft();
            int top = i2 - viewHolder.itemView.getTop();
            int size = list.size();
            int i4 = ItemTouchHelper.ACTION_STATE_IDLE;
            while (i4 < size) {
                int i5;
                ViewHolder viewHolder3;
                int i6;
                ViewHolder viewHolder4;
                int i7;
                ViewHolder viewHolder5 = (ViewHolder) list.get(i4);
                if (left > 0) {
                    int right = viewHolder5.itemView.getRight() - width;
                    if (right < 0 && viewHolder5.itemView.getRight() > viewHolder.itemView.getRight()) {
                        right = Math.abs(right);
                        if (right > i3) {
                            i5 = right;
                            viewHolder3 = viewHolder5;
                            if (left < 0) {
                                i3 = viewHolder5.itemView.getLeft() - i;
                                if (i3 > 0 && viewHolder5.itemView.getLeft() < viewHolder.itemView.getLeft()) {
                                    i3 = Math.abs(i3);
                                    if (i3 > i5) {
                                        viewHolder3 = viewHolder5;
                                        if (top < 0) {
                                            i5 = viewHolder5.itemView.getTop() - i2;
                                            if (i5 > 0 && viewHolder5.itemView.getTop() < viewHolder.itemView.getTop()) {
                                                i5 = Math.abs(i5);
                                                if (i5 > i3) {
                                                    viewHolder3 = viewHolder5;
                                                    if (top > 0) {
                                                        i3 = viewHolder5.itemView.getBottom() - height;
                                                        if (i3 < 0 && viewHolder5.itemView.getBottom() > viewHolder.itemView.getBottom()) {
                                                            i3 = Math.abs(i3);
                                                            if (i3 > i5) {
                                                                i6 = i3;
                                                                viewHolder4 = viewHolder5;
                                                                i7 = i6;
                                                                i4 += ItemTouchHelper.UP;
                                                                viewHolder2 = viewHolder4;
                                                                i3 = i7;
                                                            }
                                                        }
                                                    }
                                                    i7 = i5;
                                                    viewHolder4 = viewHolder3;
                                                    i4 += ItemTouchHelper.UP;
                                                    viewHolder2 = viewHolder4;
                                                    i3 = i7;
                                                }
                                            }
                                        }
                                        i5 = i3;
                                        if (top > 0) {
                                            i3 = viewHolder5.itemView.getBottom() - height;
                                            i3 = Math.abs(i3);
                                            if (i3 > i5) {
                                                i6 = i3;
                                                viewHolder4 = viewHolder5;
                                                i7 = i6;
                                                i4 += ItemTouchHelper.UP;
                                                viewHolder2 = viewHolder4;
                                                i3 = i7;
                                            }
                                        }
                                        i7 = i5;
                                        viewHolder4 = viewHolder3;
                                        i4 += ItemTouchHelper.UP;
                                        viewHolder2 = viewHolder4;
                                        i3 = i7;
                                    }
                                }
                            }
                            i3 = i5;
                            if (top < 0) {
                                i5 = viewHolder5.itemView.getTop() - i2;
                                i5 = Math.abs(i5);
                                if (i5 > i3) {
                                    viewHolder3 = viewHolder5;
                                    if (top > 0) {
                                        i3 = viewHolder5.itemView.getBottom() - height;
                                        i3 = Math.abs(i3);
                                        if (i3 > i5) {
                                            i6 = i3;
                                            viewHolder4 = viewHolder5;
                                            i7 = i6;
                                            i4 += ItemTouchHelper.UP;
                                            viewHolder2 = viewHolder4;
                                            i3 = i7;
                                        }
                                    }
                                    i7 = i5;
                                    viewHolder4 = viewHolder3;
                                    i4 += ItemTouchHelper.UP;
                                    viewHolder2 = viewHolder4;
                                    i3 = i7;
                                }
                            }
                            i5 = i3;
                            if (top > 0) {
                                i3 = viewHolder5.itemView.getBottom() - height;
                                i3 = Math.abs(i3);
                                if (i3 > i5) {
                                    i6 = i3;
                                    viewHolder4 = viewHolder5;
                                    i7 = i6;
                                    i4 += ItemTouchHelper.UP;
                                    viewHolder2 = viewHolder4;
                                    i3 = i7;
                                }
                            }
                            i7 = i5;
                            viewHolder4 = viewHolder3;
                            i4 += ItemTouchHelper.UP;
                            viewHolder2 = viewHolder4;
                            i3 = i7;
                        }
                    }
                }
                viewHolder3 = viewHolder2;
                i5 = i3;
                if (left < 0) {
                    i3 = viewHolder5.itemView.getLeft() - i;
                    i3 = Math.abs(i3);
                    if (i3 > i5) {
                        viewHolder3 = viewHolder5;
                        if (top < 0) {
                            i5 = viewHolder5.itemView.getTop() - i2;
                            i5 = Math.abs(i5);
                            if (i5 > i3) {
                                viewHolder3 = viewHolder5;
                                if (top > 0) {
                                    i3 = viewHolder5.itemView.getBottom() - height;
                                    i3 = Math.abs(i3);
                                    if (i3 > i5) {
                                        i6 = i3;
                                        viewHolder4 = viewHolder5;
                                        i7 = i6;
                                        i4 += ItemTouchHelper.UP;
                                        viewHolder2 = viewHolder4;
                                        i3 = i7;
                                    }
                                }
                                i7 = i5;
                                viewHolder4 = viewHolder3;
                                i4 += ItemTouchHelper.UP;
                                viewHolder2 = viewHolder4;
                                i3 = i7;
                            }
                        }
                        i5 = i3;
                        if (top > 0) {
                            i3 = viewHolder5.itemView.getBottom() - height;
                            i3 = Math.abs(i3);
                            if (i3 > i5) {
                                i6 = i3;
                                viewHolder4 = viewHolder5;
                                i7 = i6;
                                i4 += ItemTouchHelper.UP;
                                viewHolder2 = viewHolder4;
                                i3 = i7;
                            }
                        }
                        i7 = i5;
                        viewHolder4 = viewHolder3;
                        i4 += ItemTouchHelper.UP;
                        viewHolder2 = viewHolder4;
                        i3 = i7;
                    }
                }
                i3 = i5;
                if (top < 0) {
                    i5 = viewHolder5.itemView.getTop() - i2;
                    i5 = Math.abs(i5);
                    if (i5 > i3) {
                        viewHolder3 = viewHolder5;
                        if (top > 0) {
                            i3 = viewHolder5.itemView.getBottom() - height;
                            i3 = Math.abs(i3);
                            if (i3 > i5) {
                                i6 = i3;
                                viewHolder4 = viewHolder5;
                                i7 = i6;
                                i4 += ItemTouchHelper.UP;
                                viewHolder2 = viewHolder4;
                                i3 = i7;
                            }
                        }
                        i7 = i5;
                        viewHolder4 = viewHolder3;
                        i4 += ItemTouchHelper.UP;
                        viewHolder2 = viewHolder4;
                        i3 = i7;
                    }
                }
                i5 = i3;
                if (top > 0) {
                    i3 = viewHolder5.itemView.getBottom() - height;
                    i3 = Math.abs(i3);
                    if (i3 > i5) {
                        i6 = i3;
                        viewHolder4 = viewHolder5;
                        i7 = i6;
                        i4 += ItemTouchHelper.UP;
                        viewHolder2 = viewHolder4;
                        i3 = i7;
                    }
                }
                i7 = i5;
                viewHolder4 = viewHolder3;
                i4 += ItemTouchHelper.UP;
                viewHolder2 = viewHolder4;
                i3 = i7;
            }
            return viewHolder2;
        }

        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            sUICallback.clearView(viewHolder.itemView);
        }

        public int convertToAbsoluteDirection(int i, int i2) {
            int i3 = i & RELATIVE_DIR_FLAGS;
            if (i3 == 0) {
                return i;
            }
            int i4 = (i3 ^ ItemTouchHelper.ACTIVE_POINTER_ID_NONE) & i;
            return i2 == 0 ? i4 | (i3 >> ItemTouchHelper.DOWN) : (i4 | ((i3 >> ItemTouchHelper.UP) & -3158065)) | (((i3 >> ItemTouchHelper.UP) & RELATIVE_DIR_FLAGS) >> ItemTouchHelper.DOWN);
        }

        final int getAbsoluteMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return convertToAbsoluteDirection(getMovementFlags(recyclerView, viewHolder), ViewCompat.getLayoutDirection(recyclerView));
        }

        public long getAnimationDuration(RecyclerView recyclerView, int i, float f, float f2) {
            ItemAnimator itemAnimator = recyclerView.getItemAnimator();
            return itemAnimator == null ? i == ItemTouchHelper.RIGHT ? 200 : 250 : i == ItemTouchHelper.RIGHT ? itemAnimator.getMoveDuration() : itemAnimator.getRemoveDuration();
        }

        public int getBoundingBoxMargin() {
            return ItemTouchHelper.ACTION_STATE_IDLE;
        }

        public float getMoveThreshold(ViewHolder viewHolder) {
            return 0.5f;
        }

        public abstract int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder);

        public float getSwipeEscapeVelocity(float f) {
            return f;
        }

        public float getSwipeThreshold(ViewHolder viewHolder) {
            return 0.5f;
        }

        public float getSwipeVelocityThreshold(float f) {
            return f;
        }

        public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int i, int i2, int i3, long j) {
            float f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            int maxDragScroll = (int) (((float) (getMaxDragScroll(recyclerView) * ((int) Math.signum((float) i2)))) * sDragViewScrollCapInterpolator.getInterpolation(Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, (((float) Math.abs(i2)) * DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) / ((float) i))));
            if (j <= DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
                f = ((float) j) / 500.0f;
            }
            int interpolation = (int) (sDragScrollInterpolator.getInterpolation(f) * ((float) maxDragScroll));
            return interpolation == 0 ? i2 > 0 ? ItemTouchHelper.UP : ItemTouchHelper.ACTIVE_POINTER_ID_NONE : interpolation;
        }

        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            sUICallback.onDraw(canvas, recyclerView, viewHolder.itemView, f, f2, i, z);
        }

        public void onChildDrawOver(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            sUICallback.onDrawOver(canvas, recyclerView, viewHolder.itemView, f, f2, i, z);
        }

        public abstract boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2);

        public void onMoved(RecyclerView recyclerView, ViewHolder viewHolder, int i, ViewHolder viewHolder2, int i2, int i3, int i4) {
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof ViewDropHandler) {
                ((ViewDropHandler) layoutManager).prepareForDrop(viewHolder.itemView, viewHolder2.itemView, i3, i4);
                return;
            }
            if (layoutManager.canScrollHorizontally()) {
                if (layoutManager.getDecoratedLeft(viewHolder2.itemView) <= recyclerView.getPaddingLeft()) {
                    recyclerView.scrollToPosition(i2);
                }
                if (layoutManager.getDecoratedRight(viewHolder2.itemView) >= recyclerView.getWidth() - recyclerView.getPaddingRight()) {
                    recyclerView.scrollToPosition(i2);
                }
            }
            if (layoutManager.canScrollVertically()) {
                if (layoutManager.getDecoratedTop(viewHolder2.itemView) <= recyclerView.getPaddingTop()) {
                    recyclerView.scrollToPosition(i2);
                }
                if (layoutManager.getDecoratedBottom(viewHolder2.itemView) >= recyclerView.getHeight() - recyclerView.getPaddingBottom()) {
                    recyclerView.scrollToPosition(i2);
                }
            }
        }

        public void onSelectedChanged(ViewHolder viewHolder, int i) {
            if (viewHolder != null) {
                sUICallback.onSelected(viewHolder.itemView);
            }
        }

        public abstract void onSwiped(ViewHolder viewHolder, int i);
    }

    private class ItemTouchHelperGestureListener extends SimpleOnGestureListener {
        private ItemTouchHelperGestureListener() {
        }

        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        public void onLongPress(MotionEvent motionEvent) {
            View access$2400 = ItemTouchHelper.this.findChildView(motionEvent);
            if (access$2400 != null) {
                ViewHolder childViewHolder = ItemTouchHelper.this.mRecyclerView.getChildViewHolder(access$2400);
                if (childViewHolder != null && ItemTouchHelper.this.mCallback.hasDragFlag(ItemTouchHelper.this.mRecyclerView, childViewHolder) && MotionEventCompat.getPointerId(motionEvent, ItemTouchHelper.ACTION_STATE_IDLE) == ItemTouchHelper.this.mActivePointerId) {
                    int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, ItemTouchHelper.this.mActivePointerId);
                    float x = MotionEventCompat.getX(motionEvent, findPointerIndex);
                    float y = MotionEventCompat.getY(motionEvent, findPointerIndex);
                    ItemTouchHelper.this.mInitialTouchX = x;
                    ItemTouchHelper.this.mInitialTouchY = y;
                    ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
                    ItemTouchHelper.this.mDy = 0.0f;
                    itemTouchHelper.mDx = 0.0f;
                    if (ItemTouchHelper.this.mCallback.isLongPressDragEnabled()) {
                        ItemTouchHelper.this.select(childViewHolder, ItemTouchHelper.DOWN);
                    }
                }
            }
        }
    }

    public static abstract class SimpleCallback extends Callback {
        private int mDefaultDragDirs;
        private int mDefaultSwipeDirs;

        public SimpleCallback(int i, int i2) {
            this.mDefaultSwipeDirs = i2;
            this.mDefaultDragDirs = i;
        }

        public int getDragDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
            return this.mDefaultDragDirs;
        }

        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            return Callback.makeMovementFlags(getDragDirs(recyclerView, viewHolder), getSwipeDirs(recyclerView, viewHolder));
        }

        public int getSwipeDirs(RecyclerView recyclerView, ViewHolder viewHolder) {
            return this.mDefaultSwipeDirs;
        }

        public void setDefaultDragDirs(int i) {
            this.mDefaultDragDirs = i;
        }

        public void setDefaultSwipeDirs(int i) {
            this.mDefaultSwipeDirs = i;
        }
    }

    public ItemTouchHelper(Callback callback) {
        this.mPendingCleanup = new ArrayList();
        this.mTmpPosition = new float[DOWN];
        this.mSelected = null;
        this.mActivePointerId = ACTIVE_POINTER_ID_NONE;
        this.mActionState = ACTION_STATE_IDLE;
        this.mRecoverAnimations = new ArrayList();
        this.mScrollRunnable = new C08741();
        this.mChildDrawingOrderCallback = null;
        this.mOverdrawChild = null;
        this.mOverdrawChildPosition = ACTIVE_POINTER_ID_NONE;
        this.mOnItemTouchListener = new C08752();
        this.mCallback = callback;
    }

    private void addChildDrawingOrderCallback() {
        if (VERSION.SDK_INT < 21) {
            if (this.mChildDrawingOrderCallback == null) {
                this.mChildDrawingOrderCallback = new C08785();
            }
            this.mRecyclerView.setChildDrawingOrderCallback(this.mChildDrawingOrderCallback);
        }
    }

    private int checkHorizontalSwipe(ViewHolder viewHolder, int i) {
        int i2 = RIGHT;
        if ((i & 12) != 0) {
            int i3 = this.mDx > 0.0f ? RIGHT : LEFT;
            if (this.mVelocityTracker != null && this.mActivePointerId > ACTIVE_POINTER_ID_NONE) {
                this.mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
                float xVelocity = VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId);
                float yVelocity = VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId);
                if (xVelocity <= 0.0f) {
                    i2 = LEFT;
                }
                float abs = Math.abs(xVelocity);
                if ((i2 & i) != 0 && i3 == i2 && abs >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && abs > Math.abs(yVelocity)) {
                    return i2;
                }
            }
            float width = ((float) this.mRecyclerView.getWidth()) * this.mCallback.getSwipeThreshold(viewHolder);
            if ((i & i3) != 0 && Math.abs(this.mDx) > width) {
                return i3;
            }
        }
        return ACTION_STATE_IDLE;
    }

    private boolean checkSelectForSwipe(int i, MotionEvent motionEvent, int i2) {
        if (this.mSelected != null || i != DOWN || this.mActionState == DOWN || !this.mCallback.isItemViewSwipeEnabled() || this.mRecyclerView.getScrollState() == UP) {
            return DEBUG;
        }
        ViewHolder findSwipedView = findSwipedView(motionEvent);
        if (findSwipedView == null) {
            return DEBUG;
        }
        int absoluteMovementFlags = (this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, findSwipedView) & ACTION_MODE_SWIPE_MASK) >> RIGHT;
        if (absoluteMovementFlags == 0) {
            return DEBUG;
        }
        float x = MotionEventCompat.getX(motionEvent, i2);
        x -= this.mInitialTouchX;
        float y = MotionEventCompat.getY(motionEvent, i2) - this.mInitialTouchY;
        float abs = Math.abs(x);
        float abs2 = Math.abs(y);
        if (abs < ((float) this.mSlop) && abs2 < ((float) this.mSlop)) {
            return DEBUG;
        }
        if (abs > abs2) {
            if (x < 0.0f && (absoluteMovementFlags & LEFT) == 0) {
                return DEBUG;
            }
            if (x > 0.0f && (absoluteMovementFlags & RIGHT) == 0) {
                return DEBUG;
            }
        } else if (y < 0.0f && (absoluteMovementFlags & UP) == 0) {
            return DEBUG;
        } else {
            if (y > 0.0f && (absoluteMovementFlags & DOWN) == 0) {
                return DEBUG;
            }
        }
        this.mDy = 0.0f;
        this.mDx = 0.0f;
        this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, ACTION_STATE_IDLE);
        select(findSwipedView, UP);
        return true;
    }

    private int checkVerticalSwipe(ViewHolder viewHolder, int i) {
        int i2 = DOWN;
        if ((i & 3) != 0) {
            int i3 = this.mDy > 0.0f ? DOWN : UP;
            if (this.mVelocityTracker != null && this.mActivePointerId > ACTIVE_POINTER_ID_NONE) {
                this.mVelocityTracker.computeCurrentVelocity(PIXELS_PER_SECOND, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
                float xVelocity = VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mActivePointerId);
                float yVelocity = VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId);
                if (yVelocity <= 0.0f) {
                    i2 = UP;
                }
                float abs = Math.abs(yVelocity);
                if ((i2 & i) != 0 && i2 == i3 && abs >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && abs > Math.abs(xVelocity)) {
                    return i2;
                }
            }
            float height = ((float) this.mRecyclerView.getHeight()) * this.mCallback.getSwipeThreshold(viewHolder);
            if ((i & i3) != 0 && Math.abs(this.mDy) > height) {
                return i3;
            }
        }
        return ACTION_STATE_IDLE;
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeItemDecoration(this);
        this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
        for (int size = this.mRecoverAnimations.size() + ACTIVE_POINTER_ID_NONE; size >= 0; size += ACTIVE_POINTER_ID_NONE) {
            this.mCallback.clearView(this.mRecyclerView, ((RecoverAnimation) this.mRecoverAnimations.get(ACTION_STATE_IDLE)).mViewHolder);
        }
        this.mRecoverAnimations.clear();
        this.mOverdrawChild = null;
        this.mOverdrawChildPosition = ACTIVE_POINTER_ID_NONE;
        releaseVelocityTracker();
    }

    private int endRecoverAnimation(ViewHolder viewHolder, boolean z) {
        for (int size = this.mRecoverAnimations.size() + ACTIVE_POINTER_ID_NONE; size >= 0; size += ACTIVE_POINTER_ID_NONE) {
            RecoverAnimation recoverAnimation = (RecoverAnimation) this.mRecoverAnimations.get(size);
            if (recoverAnimation.mViewHolder == viewHolder) {
                recoverAnimation.mOverridden |= z;
                if (!recoverAnimation.mEnded) {
                    recoverAnimation.cancel();
                }
                this.mRecoverAnimations.remove(size);
                return recoverAnimation.mAnimationType;
            }
        }
        return ACTION_STATE_IDLE;
    }

    private RecoverAnimation findAnimation(MotionEvent motionEvent) {
        if (this.mRecoverAnimations.isEmpty()) {
            return null;
        }
        View findChildView = findChildView(motionEvent);
        for (int size = this.mRecoverAnimations.size() + ACTIVE_POINTER_ID_NONE; size >= 0; size += ACTIVE_POINTER_ID_NONE) {
            RecoverAnimation recoverAnimation = (RecoverAnimation) this.mRecoverAnimations.get(size);
            if (recoverAnimation.mViewHolder.itemView == findChildView) {
                return recoverAnimation;
            }
        }
        return null;
    }

    private View findChildView(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (this.mSelected != null) {
            View view = this.mSelected.itemView;
            if (hitTest(view, x, y, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy)) {
                return view;
            }
        }
        for (int size = this.mRecoverAnimations.size() + ACTIVE_POINTER_ID_NONE; size >= 0; size += ACTIVE_POINTER_ID_NONE) {
            RecoverAnimation recoverAnimation = (RecoverAnimation) this.mRecoverAnimations.get(size);
            View view2 = recoverAnimation.mViewHolder.itemView;
            if (hitTest(view2, x, y, recoverAnimation.mX, recoverAnimation.mY)) {
                return view2;
            }
        }
        return this.mRecyclerView.findChildViewUnder(x, y);
    }

    private List<ViewHolder> findSwapTargets(ViewHolder viewHolder) {
        if (this.mSwapTargets == null) {
            this.mSwapTargets = new ArrayList();
            this.mDistances = new ArrayList();
        } else {
            this.mSwapTargets.clear();
            this.mDistances.clear();
        }
        int boundingBoxMargin = this.mCallback.getBoundingBoxMargin();
        int round = Math.round(this.mSelectedStartX + this.mDx) - boundingBoxMargin;
        int round2 = Math.round(this.mSelectedStartY + this.mDy) - boundingBoxMargin;
        int width = (viewHolder.itemView.getWidth() + round) + (boundingBoxMargin * DOWN);
        int height = (viewHolder.itemView.getHeight() + round2) + (boundingBoxMargin * DOWN);
        int i = (round + width) / DOWN;
        int i2 = (round2 + height) / DOWN;
        LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        int childCount = layoutManager.getChildCount();
        for (int i3 = ACTION_STATE_IDLE; i3 < childCount; i3 += UP) {
            View childAt = layoutManager.getChildAt(i3);
            if (childAt != viewHolder.itemView && childAt.getBottom() >= round2 && childAt.getTop() <= height && childAt.getRight() >= round && childAt.getLeft() <= width) {
                ViewHolder childViewHolder = this.mRecyclerView.getChildViewHolder(childAt);
                if (this.mCallback.canDropOver(this.mRecyclerView, this.mSelected, childViewHolder)) {
                    int abs = Math.abs(i - ((childAt.getLeft() + childAt.getRight()) / DOWN));
                    boundingBoxMargin = Math.abs(i2 - ((childAt.getBottom() + childAt.getTop()) / DOWN));
                    int i4 = (abs * abs) + (boundingBoxMargin * boundingBoxMargin);
                    int size = this.mSwapTargets.size();
                    int i5 = ACTION_STATE_IDLE;
                    abs = ACTION_STATE_IDLE;
                    while (abs < size && i4 > ((Integer) this.mDistances.get(abs)).intValue()) {
                        i5 += UP;
                        abs += UP;
                    }
                    this.mSwapTargets.add(i5, childViewHolder);
                    this.mDistances.add(i5, Integer.valueOf(i4));
                }
            }
        }
        return this.mSwapTargets;
    }

    private ViewHolder findSwipedView(MotionEvent motionEvent) {
        LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        if (this.mActivePointerId == ACTIVE_POINTER_ID_NONE) {
            return null;
        }
        int findPointerIndex = MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId);
        float x = MotionEventCompat.getX(motionEvent, findPointerIndex) - this.mInitialTouchX;
        float y = MotionEventCompat.getY(motionEvent, findPointerIndex) - this.mInitialTouchY;
        x = Math.abs(x);
        y = Math.abs(y);
        if (x < ((float) this.mSlop) && y < ((float) this.mSlop)) {
            return null;
        }
        if (x > y && layoutManager.canScrollHorizontally()) {
            return null;
        }
        if (y > x && layoutManager.canScrollVertically()) {
            return null;
        }
        View findChildView = findChildView(motionEvent);
        return findChildView != null ? this.mRecyclerView.getChildViewHolder(findChildView) : null;
    }

    private void getSelectedDxDy(float[] fArr) {
        if ((this.mSelectedFlags & 12) != 0) {
            fArr[ACTION_STATE_IDLE] = (this.mSelectedStartX + this.mDx) - ((float) this.mSelected.itemView.getLeft());
        } else {
            fArr[ACTION_STATE_IDLE] = ViewCompat.getTranslationX(this.mSelected.itemView);
        }
        if ((this.mSelectedFlags & 3) != 0) {
            fArr[UP] = (this.mSelectedStartY + this.mDy) - ((float) this.mSelected.itemView.getTop());
        } else {
            fArr[UP] = ViewCompat.getTranslationY(this.mSelected.itemView);
        }
    }

    private boolean hasRunningRecoverAnim() {
        int size = this.mRecoverAnimations.size();
        for (int i = ACTION_STATE_IDLE; i < size; i += UP) {
            if (!((RecoverAnimation) this.mRecoverAnimations.get(i)).mEnded) {
                return true;
            }
        }
        return DEBUG;
    }

    private static boolean hitTest(View view, float f, float f2, float f3, float f4) {
        return (f < f3 || f > ((float) view.getWidth()) + f3 || f2 < f4 || f2 > ((float) view.getHeight()) + f4) ? DEBUG : true;
    }

    private void initGestureDetector() {
        if (this.mGestureDetector == null) {
            this.mGestureDetector = new GestureDetectorCompat(this.mRecyclerView.getContext(), new ItemTouchHelperGestureListener());
        }
    }

    private void moveIfNecessary(ViewHolder viewHolder) {
        if (!this.mRecyclerView.isLayoutRequested() && this.mActionState == DOWN) {
            float moveThreshold = this.mCallback.getMoveThreshold(viewHolder);
            int i = (int) (this.mSelectedStartX + this.mDx);
            int i2 = (int) (this.mSelectedStartY + this.mDy);
            if (((float) Math.abs(i2 - viewHolder.itemView.getTop())) >= ((float) viewHolder.itemView.getHeight()) * moveThreshold || ((float) Math.abs(i - viewHolder.itemView.getLeft())) >= moveThreshold * ((float) viewHolder.itemView.getWidth())) {
                List findSwapTargets = findSwapTargets(viewHolder);
                if (findSwapTargets.size() != 0) {
                    ViewHolder chooseDropTarget = this.mCallback.chooseDropTarget(viewHolder, findSwapTargets, i, i2);
                    if (chooseDropTarget == null) {
                        this.mSwapTargets.clear();
                        this.mDistances.clear();
                        return;
                    }
                    int adapterPosition = chooseDropTarget.getAdapterPosition();
                    int adapterPosition2 = viewHolder.getAdapterPosition();
                    if (this.mCallback.onMove(this.mRecyclerView, viewHolder, chooseDropTarget)) {
                        this.mCallback.onMoved(this.mRecyclerView, viewHolder, adapterPosition2, chooseDropTarget, adapterPosition, i, i2);
                    }
                }
            }
        }
    }

    private void obtainVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
        }
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    private void postDispatchSwipe(RecoverAnimation recoverAnimation, int i) {
        this.mRecyclerView.post(new C08774(recoverAnimation, i));
    }

    private void releaseVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void removeChildDrawingOrderCallbackIfNecessary(View view) {
        if (view == this.mOverdrawChild) {
            this.mOverdrawChild = null;
            if (this.mChildDrawingOrderCallback != null) {
                this.mRecyclerView.setChildDrawingOrderCallback(null);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean scrollIfNecessary() {
        /*
        r14 = this;
        r12 = -9223372036854775808;
        r0 = 0;
        r5 = 0;
        r1 = r14.mSelected;
        if (r1 != 0) goto L_0x000b;
    L_0x0008:
        r14.mDragScrollStartTimeInMs = r12;
    L_0x000a:
        return r0;
    L_0x000b:
        r10 = java.lang.System.currentTimeMillis();
        r2 = r14.mDragScrollStartTimeInMs;
        r1 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
        if (r1 != 0) goto L_0x00bb;
    L_0x0015:
        r6 = 0;
    L_0x0017:
        r1 = r14.mRecyclerView;
        r1 = r1.getLayoutManager();
        r2 = r14.mTmpRect;
        if (r2 != 0) goto L_0x0028;
    L_0x0021:
        r2 = new android.graphics.Rect;
        r2.<init>();
        r14.mTmpRect = r2;
    L_0x0028:
        r2 = r14.mSelected;
        r2 = r2.itemView;
        r3 = r14.mTmpRect;
        r1.calculateItemDecorationsForChild(r2, r3);
        r2 = r1.canScrollHorizontally();
        if (r2 == 0) goto L_0x00e6;
    L_0x0037:
        r2 = r14.mSelectedStartX;
        r3 = r14.mDx;
        r2 = r2 + r3;
        r2 = (int) r2;
        r3 = r14.mTmpRect;
        r3 = r3.left;
        r3 = r2 - r3;
        r4 = r14.mRecyclerView;
        r4 = r4.getPaddingLeft();
        r4 = r3 - r4;
        r3 = r14.mDx;
        r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r3 >= 0) goto L_0x00c1;
    L_0x0051:
        if (r4 >= 0) goto L_0x00c1;
    L_0x0053:
        r1 = r1.canScrollVertically();
        if (r1 == 0) goto L_0x010e;
    L_0x0059:
        r1 = r14.mSelectedStartY;
        r2 = r14.mDy;
        r1 = r1 + r2;
        r1 = (int) r1;
        r2 = r14.mTmpRect;
        r2 = r2.top;
        r2 = r1 - r2;
        r3 = r14.mRecyclerView;
        r3 = r3.getPaddingTop();
        r8 = r2 - r3;
        r2 = r14.mDy;
        r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1));
        if (r2 >= 0) goto L_0x00e9;
    L_0x0073:
        if (r8 >= 0) goto L_0x00e9;
    L_0x0075:
        if (r4 == 0) goto L_0x0117;
    L_0x0077:
        r1 = r14.mCallback;
        r2 = r14.mRecyclerView;
        r3 = r14.mSelected;
        r3 = r3.itemView;
        r3 = r3.getWidth();
        r5 = r14.mRecyclerView;
        r5 = r5.getWidth();
        r4 = r1.interpolateOutOfBoundsScroll(r2, r3, r4, r5, r6);
        r9 = r4;
    L_0x008e:
        if (r8 == 0) goto L_0x0115;
    L_0x0090:
        r1 = r14.mCallback;
        r2 = r14.mRecyclerView;
        r3 = r14.mSelected;
        r3 = r3.itemView;
        r3 = r3.getHeight();
        r4 = r14.mRecyclerView;
        r5 = r4.getHeight();
        r4 = r8;
        r1 = r1.interpolateOutOfBoundsScroll(r2, r3, r4, r5, r6);
    L_0x00a7:
        if (r9 != 0) goto L_0x00ab;
    L_0x00a9:
        if (r1 == 0) goto L_0x0111;
    L_0x00ab:
        r2 = r14.mDragScrollStartTimeInMs;
        r0 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
        if (r0 != 0) goto L_0x00b3;
    L_0x00b1:
        r14.mDragScrollStartTimeInMs = r10;
    L_0x00b3:
        r0 = r14.mRecyclerView;
        r0.scrollBy(r9, r1);
        r0 = 1;
        goto L_0x000a;
    L_0x00bb:
        r2 = r14.mDragScrollStartTimeInMs;
        r6 = r10 - r2;
        goto L_0x0017;
    L_0x00c1:
        r3 = r14.mDx;
        r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r3 <= 0) goto L_0x00e6;
    L_0x00c7:
        r3 = r14.mSelected;
        r3 = r3.itemView;
        r3 = r3.getWidth();
        r2 = r2 + r3;
        r3 = r14.mTmpRect;
        r3 = r3.right;
        r2 = r2 + r3;
        r3 = r14.mRecyclerView;
        r3 = r3.getWidth();
        r4 = r14.mRecyclerView;
        r4 = r4.getPaddingRight();
        r3 = r3 - r4;
        r4 = r2 - r3;
        if (r4 > 0) goto L_0x0053;
    L_0x00e6:
        r4 = r0;
        goto L_0x0053;
    L_0x00e9:
        r2 = r14.mDy;
        r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1));
        if (r2 <= 0) goto L_0x010e;
    L_0x00ef:
        r2 = r14.mSelected;
        r2 = r2.itemView;
        r2 = r2.getHeight();
        r1 = r1 + r2;
        r2 = r14.mTmpRect;
        r2 = r2.bottom;
        r1 = r1 + r2;
        r2 = r14.mRecyclerView;
        r2 = r2.getHeight();
        r3 = r14.mRecyclerView;
        r3 = r3.getPaddingBottom();
        r2 = r2 - r3;
        r8 = r1 - r2;
        if (r8 > 0) goto L_0x0075;
    L_0x010e:
        r8 = r0;
        goto L_0x0075;
    L_0x0111:
        r14.mDragScrollStartTimeInMs = r12;
        goto L_0x000a;
    L_0x0115:
        r1 = r8;
        goto L_0x00a7;
    L_0x0117:
        r9 = r4;
        goto L_0x008e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.scrollIfNecessary():boolean");
    }

    private void select(ViewHolder viewHolder, int i) {
        if (viewHolder != this.mSelected || i != this.mActionState) {
            this.mDragScrollStartTimeInMs = Long.MIN_VALUE;
            int i2 = this.mActionState;
            endRecoverAnimation(viewHolder, true);
            this.mActionState = i;
            if (i == DOWN) {
                this.mOverdrawChild = viewHolder.itemView;
                addChildDrawingOrderCallback();
            }
            int i3 = (UP << ((i * RIGHT) + RIGHT)) + ACTIVE_POINTER_ID_NONE;
            Object obj = null;
            if (this.mSelected != null) {
                ViewHolder viewHolder2 = this.mSelected;
                if (viewHolder2.itemView.getParent() != null) {
                    float f;
                    float signum;
                    int swipeIfNecessary = i2 == DOWN ? ACTION_STATE_IDLE : swipeIfNecessary(viewHolder2);
                    releaseVelocityTracker();
                    switch (swipeIfNecessary) {
                        case UP /*1*/:
                        case DOWN /*2*/:
                            f = 0.0f;
                            signum = Math.signum(this.mDy) * ((float) this.mRecyclerView.getHeight());
                            break;
                        case LEFT /*4*/:
                        case RIGHT /*8*/:
                        case START /*16*/:
                        case END /*32*/:
                            signum = 0.0f;
                            f = Math.signum(this.mDx) * ((float) this.mRecyclerView.getWidth());
                            break;
                        default:
                            f = 0.0f;
                            signum = 0.0f;
                            break;
                    }
                    int i4 = i2 == DOWN ? RIGHT : swipeIfNecessary > 0 ? DOWN : LEFT;
                    getSelectedDxDy(this.mTmpPosition);
                    float f2 = this.mTmpPosition[ACTION_STATE_IDLE];
                    float f3 = this.mTmpPosition[UP];
                    RecoverAnimation c08763 = new C08763(viewHolder2, i4, i2, f2, f3, f, signum, swipeIfNecessary, viewHolder2);
                    c08763.setDuration(this.mCallback.getAnimationDuration(this.mRecyclerView, i4, f - f2, signum - f3));
                    this.mRecoverAnimations.add(c08763);
                    c08763.start();
                    obj = UP;
                } else {
                    removeChildDrawingOrderCallbackIfNecessary(viewHolder2.itemView);
                    this.mCallback.clearView(this.mRecyclerView, viewHolder2);
                }
                this.mSelected = null;
            }
            Object obj2 = obj;
            if (viewHolder != null) {
                this.mSelectedFlags = (this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, viewHolder) & i3) >> (this.mActionState * RIGHT);
                this.mSelectedStartX = (float) viewHolder.itemView.getLeft();
                this.mSelectedStartY = (float) viewHolder.itemView.getTop();
                this.mSelected = viewHolder;
                if (i == DOWN) {
                    this.mSelected.itemView.performHapticFeedback(ACTION_STATE_IDLE);
                }
            }
            ViewParent parent = this.mRecyclerView.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(this.mSelected != null ? true : DEBUG);
            }
            if (obj2 == null) {
                this.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout();
            }
            this.mCallback.onSelectedChanged(this.mSelected, this.mActionState);
            this.mRecyclerView.invalidate();
        }
    }

    private void setupCallbacks() {
        this.mSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
        this.mRecyclerView.addItemDecoration(this);
        this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
        this.mRecyclerView.addOnChildAttachStateChangeListener(this);
        initGestureDetector();
    }

    private int swipeIfNecessary(ViewHolder viewHolder) {
        if (this.mActionState == DOWN) {
            return ACTION_STATE_IDLE;
        }
        int movementFlags = this.mCallback.getMovementFlags(this.mRecyclerView, viewHolder);
        int convertToAbsoluteDirection = (this.mCallback.convertToAbsoluteDirection(movementFlags, ViewCompat.getLayoutDirection(this.mRecyclerView)) & ACTION_MODE_SWIPE_MASK) >> RIGHT;
        if (convertToAbsoluteDirection == 0) {
            return ACTION_STATE_IDLE;
        }
        int i = (movementFlags & ACTION_MODE_SWIPE_MASK) >> RIGHT;
        if (Math.abs(this.mDx) > Math.abs(this.mDy)) {
            movementFlags = checkHorizontalSwipe(viewHolder, convertToAbsoluteDirection);
            if (movementFlags > 0) {
                return (i & movementFlags) == 0 ? Callback.convertToRelativeDirection(movementFlags, ViewCompat.getLayoutDirection(this.mRecyclerView)) : movementFlags;
            } else {
                movementFlags = checkVerticalSwipe(viewHolder, convertToAbsoluteDirection);
                return movementFlags > 0 ? movementFlags : ACTION_STATE_IDLE;
            }
        } else {
            movementFlags = checkVerticalSwipe(viewHolder, convertToAbsoluteDirection);
            if (movementFlags > 0) {
                return movementFlags;
            }
            movementFlags = checkHorizontalSwipe(viewHolder, convertToAbsoluteDirection);
            return movementFlags > 0 ? (i & movementFlags) == 0 ? Callback.convertToRelativeDirection(movementFlags, ViewCompat.getLayoutDirection(this.mRecyclerView)) : movementFlags : ACTION_STATE_IDLE;
        }
    }

    private void updateDxDy(MotionEvent motionEvent, int i, int i2) {
        float x = MotionEventCompat.getX(motionEvent, i2);
        float y = MotionEventCompat.getY(motionEvent, i2);
        this.mDx = x - this.mInitialTouchX;
        this.mDy = y - this.mInitialTouchY;
        if ((i & LEFT) == 0) {
            this.mDx = Math.max(0.0f, this.mDx);
        }
        if ((i & RIGHT) == 0) {
            this.mDx = Math.min(0.0f, this.mDx);
        }
        if ((i & UP) == 0) {
            this.mDy = Math.max(0.0f, this.mDy);
        }
        if ((i & DOWN) == 0) {
            this.mDy = Math.min(0.0f, this.mDy);
        }
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        if (this.mRecyclerView != recyclerView) {
            if (this.mRecyclerView != null) {
                destroyCallbacks();
            }
            this.mRecyclerView = recyclerView;
            if (this.mRecyclerView != null) {
                recyclerView.getResources();
                this.mSwipeEscapeVelocity = (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN);
                this.mMaxSwipeVelocity = (float) AndroidUtilities.dp(800.0f);
                setupCallbacks();
            }
        }
    }

    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
        rect.setEmpty();
    }

    public void onChildViewAttachedToWindow(View view) {
    }

    public void onChildViewDetachedFromWindow(View view) {
        removeChildDrawingOrderCallbackIfNecessary(view);
        ViewHolder childViewHolder = this.mRecyclerView.getChildViewHolder(view);
        if (childViewHolder != null) {
            if (this.mSelected == null || childViewHolder != this.mSelected) {
                endRecoverAnimation(childViewHolder, DEBUG);
                if (this.mPendingCleanup.remove(childViewHolder.itemView)) {
                    this.mCallback.clearView(this.mRecyclerView, childViewHolder);
                    return;
                }
                return;
            }
            select(null, ACTION_STATE_IDLE);
        }
    }

    public void onDraw(Canvas canvas, RecyclerView recyclerView, State state) {
        float f;
        float f2 = 0.0f;
        this.mOverdrawChildPosition = ACTIVE_POINTER_ID_NONE;
        if (this.mSelected != null) {
            getSelectedDxDy(this.mTmpPosition);
            f = this.mTmpPosition[ACTION_STATE_IDLE];
            f2 = this.mTmpPosition[UP];
        } else {
            f = 0.0f;
        }
        this.mCallback.onDraw(canvas, recyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f, f2);
    }

    public void onDrawOver(Canvas canvas, RecyclerView recyclerView, State state) {
        float f;
        float f2 = 0.0f;
        if (this.mSelected != null) {
            getSelectedDxDy(this.mTmpPosition);
            f = this.mTmpPosition[ACTION_STATE_IDLE];
            f2 = this.mTmpPosition[UP];
        } else {
            f = 0.0f;
        }
        this.mCallback.onDrawOver(canvas, recyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f, f2);
    }

    public void startDrag(ViewHolder viewHolder) {
        if (!this.mCallback.hasDragFlag(this.mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start drag has been called but swiping is not enabled");
        } else if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e(TAG, "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this ItemTouchHelper.");
        } else {
            obtainVelocityTracker();
            this.mDy = 0.0f;
            this.mDx = 0.0f;
            select(viewHolder, DOWN);
        }
    }

    public void startSwipe(ViewHolder viewHolder) {
        if (!this.mCallback.hasSwipeFlag(this.mRecyclerView, viewHolder)) {
            Log.e(TAG, "Start swipe has been called but dragging is not enabled");
        } else if (viewHolder.itemView.getParent() != this.mRecyclerView) {
            Log.e(TAG, "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this ItemTouchHelper.");
        } else {
            obtainVelocityTracker();
            this.mDy = 0.0f;
            this.mDx = 0.0f;
            select(viewHolder, UP);
        }
    }
}

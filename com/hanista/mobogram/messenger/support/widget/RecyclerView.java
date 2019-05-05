package com.hanista.mobogram.messenger.support.widget;

import android.content.Context;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.os.TraceCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.text.Cue;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerView extends ViewGroup implements NestedScrollingChild, ScrollingView {
    static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC;
    private static final boolean DEBUG = false;
    static final boolean DISPATCH_TEMP_DETACH = false;
    private static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
    public static final int HORIZONTAL = 0;
    private static final int INVALID_POINTER = -1;
    public static final int INVALID_TYPE = -1;
    private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
    private static final int MAX_SCROLL_DURATION = 2000;
    public static final long NO_ID = -1;
    public static final int NO_POSITION = -1;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final String TAG = "RecyclerView";
    public static final int TOUCH_SLOP_DEFAULT = 0;
    public static final int TOUCH_SLOP_PAGING = 1;
    private static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
    private static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
    private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
    private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
    private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
    private static final String TRACE_SCROLL_TAG = "RV Scroll";
    public static final int VERTICAL = 1;
    private static final Interpolator sQuinticInterpolator;
    private int glowColor;
    private RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
    private final AccessibilityManager mAccessibilityManager;
    private OnItemTouchListener mActiveOnItemTouchListener;
    private Adapter mAdapter;
    AdapterHelper mAdapterHelper;
    private boolean mAdapterUpdateDuringMeasure;
    private EdgeEffectCompat mBottomGlow;
    private ChildDrawingOrderCallback mChildDrawingOrderCallback;
    ChildHelper mChildHelper;
    private boolean mClipToPadding;
    private boolean mDataSetHasChangedAfterLayout;
    private int mEatRequestLayout;
    private int mEatenAccessibilityChangeFlags;
    private boolean mFirstLayoutComplete;
    private boolean mHasFixedSize;
    private boolean mIgnoreMotionEventTillDown;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private boolean mIsAttached;
    ItemAnimator mItemAnimator;
    private ItemAnimatorListener mItemAnimatorListener;
    private Runnable mItemAnimatorRunner;
    private final ArrayList<ItemDecoration> mItemDecorations;
    boolean mItemsAddedOrRemoved;
    boolean mItemsChanged;
    private int mLastTouchX;
    private int mLastTouchY;
    @VisibleForTesting
    LayoutManager mLayout;
    private boolean mLayoutFrozen;
    private int mLayoutOrScrollCounter;
    private boolean mLayoutRequestEaten;
    private EdgeEffectCompat mLeftGlow;
    private final int mMaxFlingVelocity;
    private final int mMinFlingVelocity;
    private final int[] mMinMaxLayoutPositions;
    private final int[] mNestedOffsets;
    private final RecyclerViewDataObserver mObserver;
    private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
    private final ArrayList<OnItemTouchListener> mOnItemTouchListeners;
    private SavedState mPendingSavedState;
    private final boolean mPostUpdatesOnAnimation;
    private boolean mPostedAnimatorRunner;
    final Recycler mRecycler;
    private RecyclerListener mRecyclerListener;
    private EdgeEffectCompat mRightGlow;
    private final int[] mScrollConsumed;
    private float mScrollFactor;
    private OnScrollListener mScrollListener;
    private List<OnScrollListener> mScrollListeners;
    private final int[] mScrollOffset;
    private int mScrollPointerId;
    private int mScrollState;
    private NestedScrollingChildHelper mScrollingChildHelper;
    final State mState;
    private final Rect mTempRect;
    private EdgeEffectCompat mTopGlow;
    private int mTouchSlop;
    private final Runnable mUpdateChildViewsRunnable;
    private VelocityTracker mVelocityTracker;
    private final ViewFlinger mViewFlinger;
    private final ProcessCallback mViewInfoProcessCallback;
    final ViewInfoStore mViewInfoStore;
    private int topGlowOffset;

    public static abstract class ItemAnimator {
        public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        public static final int FLAG_CHANGED = 2;
        public static final int FLAG_INVALIDATED = 4;
        public static final int FLAG_MOVED = 2048;
        public static final int FLAG_REMOVED = 8;
        private long mAddDuration;
        private long mChangeDuration;
        private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners;
        private ItemAnimatorListener mListener;
        private long mMoveDuration;
        private long mRemoveDuration;

        @Retention(RetentionPolicy.SOURCE)
        public @interface AdapterChanges {
        }

        public interface ItemAnimatorFinishedListener {
            void onAnimationsFinished();
        }

        interface ItemAnimatorListener {
            void onAnimationFinished(ViewHolder viewHolder);
        }

        public static class ItemHolderInfo {
            public int bottom;
            public int changeFlags;
            public int left;
            public int right;
            public int top;

            public ItemHolderInfo setFrom(ViewHolder viewHolder) {
                return setFrom(viewHolder, RecyclerView.TOUCH_SLOP_DEFAULT);
            }

            public ItemHolderInfo setFrom(ViewHolder viewHolder, int i) {
                View view = viewHolder.itemView;
                this.left = view.getLeft();
                this.top = view.getTop();
                this.right = view.getRight();
                this.bottom = view.getBottom();
                return this;
            }
        }

        public ItemAnimator() {
            this.mListener = null;
            this.mFinishedListeners = new ArrayList();
            this.mAddDuration = 120;
            this.mRemoveDuration = 120;
            this.mMoveDuration = 250;
            this.mChangeDuration = 250;
        }

        static int buildAdapterChangeFlagsForAnimations(ViewHolder viewHolder) {
            int access$6500 = viewHolder.mFlags & 14;
            if (viewHolder.isInvalid()) {
                return FLAG_INVALIDATED;
            }
            if ((access$6500 & FLAG_INVALIDATED) != 0) {
                return access$6500;
            }
            int oldPosition = viewHolder.getOldPosition();
            int adapterPosition = viewHolder.getAdapterPosition();
            return (oldPosition == RecyclerView.NO_POSITION || adapterPosition == RecyclerView.NO_POSITION || oldPosition == adapterPosition) ? access$6500 : access$6500 | FLAG_MOVED;
        }

        public abstract boolean animateAppearance(@NonNull ViewHolder viewHolder, @Nullable ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2);

        public abstract boolean animateChange(@NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder2, @NonNull ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2);

        public abstract boolean animateDisappearance(@NonNull ViewHolder viewHolder, @NonNull ItemHolderInfo itemHolderInfo, @Nullable ItemHolderInfo itemHolderInfo2);

        public abstract boolean animatePersistence(@NonNull ViewHolder viewHolder, @NonNull ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2);

        public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder viewHolder) {
            return true;
        }

        public boolean canReuseUpdatedViewHolder(@NonNull ViewHolder viewHolder, @NonNull List<Object> list) {
            return canReuseUpdatedViewHolder(viewHolder);
        }

        public final void dispatchAnimationFinished(ViewHolder viewHolder) {
            onAnimationFinished(viewHolder);
            if (this.mListener != null) {
                this.mListener.onAnimationFinished(viewHolder);
            }
        }

        public final void dispatchAnimationStarted(ViewHolder viewHolder) {
            onAnimationStarted(viewHolder);
        }

        public final void dispatchAnimationsFinished() {
            int size = this.mFinishedListeners.size();
            for (int i = RecyclerView.TOUCH_SLOP_DEFAULT; i < size; i += RecyclerView.VERTICAL) {
                ((ItemAnimatorFinishedListener) this.mFinishedListeners.get(i)).onAnimationsFinished();
            }
            this.mFinishedListeners.clear();
        }

        public abstract void endAnimation(ViewHolder viewHolder);

        public abstract void endAnimations();

        public long getAddDuration() {
            return this.mAddDuration;
        }

        public long getChangeDuration() {
            return this.mChangeDuration;
        }

        public long getMoveDuration() {
            return this.mMoveDuration;
        }

        public long getRemoveDuration() {
            return this.mRemoveDuration;
        }

        public abstract boolean isRunning();

        public final boolean isRunning(ItemAnimatorFinishedListener itemAnimatorFinishedListener) {
            boolean isRunning = isRunning();
            if (itemAnimatorFinishedListener != null) {
                if (isRunning) {
                    this.mFinishedListeners.add(itemAnimatorFinishedListener);
                } else {
                    itemAnimatorFinishedListener.onAnimationsFinished();
                }
            }
            return isRunning;
        }

        public ItemHolderInfo obtainHolderInfo() {
            return new ItemHolderInfo();
        }

        public void onAnimationFinished(ViewHolder viewHolder) {
        }

        public void onAnimationStarted(ViewHolder viewHolder) {
        }

        @NonNull
        public ItemHolderInfo recordPostLayoutInformation(@NonNull State state, @NonNull ViewHolder viewHolder) {
            return obtainHolderInfo().setFrom(viewHolder);
        }

        @NonNull
        public ItemHolderInfo recordPreLayoutInformation(@NonNull State state, @NonNull ViewHolder viewHolder, int i, @NonNull List<Object> list) {
            return obtainHolderInfo().setFrom(viewHolder);
        }

        public abstract void runPendingAnimations();

        public void setAddDuration(long j) {
            this.mAddDuration = j;
        }

        public void setChangeDuration(long j) {
            this.mChangeDuration = j;
        }

        void setListener(ItemAnimatorListener itemAnimatorListener) {
            this.mListener = itemAnimatorListener;
        }

        public void setMoveDuration(long j) {
            this.mMoveDuration = j;
        }

        public void setRemoveDuration(long j) {
            this.mRemoveDuration = j;
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        final Rect mDecorInsets;
        boolean mInsetsDirty;
        boolean mPendingInvalidate;
        ViewHolder mViewHolder;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            this.mDecorInsets = new Rect();
            this.mInsetsDirty = true;
            this.mPendingInvalidate = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public int getViewAdapterPosition() {
            return this.mViewHolder.getAdapterPosition();
        }

        public int getViewLayoutPosition() {
            return this.mViewHolder.getLayoutPosition();
        }

        public int getViewPosition() {
            return this.mViewHolder.getPosition();
        }

        public boolean isItemChanged() {
            return this.mViewHolder.isUpdated();
        }

        public boolean isItemRemoved() {
            return this.mViewHolder.isRemoved();
        }

        public boolean isViewInvalid() {
            return this.mViewHolder.isInvalid();
        }

        public boolean viewNeedsUpdate() {
            return this.mViewHolder.needsUpdate();
        }
    }

    public static abstract class LayoutManager {
        private boolean mAutoMeasure;
        ChildHelper mChildHelper;
        private int mHeight;
        private int mHeightMode;
        boolean mIsAttachedToWindow;
        private boolean mMeasurementCacheEnabled;
        RecyclerView mRecyclerView;
        private boolean mRequestedSimpleAnimations;
        @Nullable
        SmoothScroller mSmoothScroller;
        private int mWidth;
        private int mWidthMode;

        public static class Properties {
            public int orientation;
            public boolean reverseLayout;
            public int spanCount;
            public boolean stackFromEnd;
        }

        public LayoutManager() {
            this.mRequestedSimpleAnimations = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mIsAttachedToWindow = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mAutoMeasure = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mMeasurementCacheEnabled = true;
        }

        private void addViewInt(View view, int i, boolean z) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (z || childViewHolderInt.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(childViewHolderInt);
            } else {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(childViewHolderInt);
            }
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (childViewHolderInt.wasReturnedFromScrap() || childViewHolderInt.isScrap()) {
                if (childViewHolderInt.isScrap()) {
                    childViewHolderInt.unScrap();
                } else {
                    childViewHolderInt.clearReturnedFromScrapFlag();
                }
                this.mChildHelper.attachViewToParent(view, i, view.getLayoutParams(), RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
            } else if (view.getParent() == this.mRecyclerView) {
                int indexOfChild = this.mChildHelper.indexOfChild(view);
                if (i == RecyclerView.NO_POSITION) {
                    i = this.mChildHelper.getChildCount();
                }
                if (indexOfChild == RecyclerView.NO_POSITION) {
                    throw new IllegalStateException("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:" + this.mRecyclerView.indexOfChild(view));
                } else if (indexOfChild != i) {
                    this.mRecyclerView.mLayout.moveView(indexOfChild, i);
                }
            } else {
                this.mChildHelper.addView(view, i, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
                layoutParams.mInsetsDirty = true;
                if (this.mSmoothScroller != null && this.mSmoothScroller.isRunning()) {
                    this.mSmoothScroller.onChildAttachedToWindow(view);
                }
            }
            if (layoutParams.mPendingInvalidate) {
                childViewHolderInt.itemView.invalidate();
                layoutParams.mPendingInvalidate = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            }
        }

        public static int chooseSize(int i, int i2, int i3) {
            int mode = MeasureSpec.getMode(i);
            int size = MeasureSpec.getSize(i);
            switch (mode) {
                case TLRPC.MESSAGE_FLAG_MEGAGROUP /*-2147483648*/:
                    return Math.min(size, Math.max(i2, i3));
                case C0700C.ENCODING_PCM_32BIT /*1073741824*/:
                    return size;
                default:
                    return Math.max(i2, i3);
            }
        }

        private void detachViewInternal(int i, View view) {
            this.mChildHelper.detachViewFromParent(i);
        }

        public static int getChildMeasureSpec(int i, int i2, int i3, int i4, boolean z) {
            int i5 = RecyclerView.TOUCH_SLOP_DEFAULT;
            int max = Math.max(RecyclerView.TOUCH_SLOP_DEFAULT, i - i3);
            if (z) {
                if (i4 >= 0) {
                    i5 = C0700C.ENCODING_PCM_32BIT;
                    max = i4;
                } else if (i4 == RecyclerView.NO_POSITION) {
                    switch (i2) {
                        case TLRPC.MESSAGE_FLAG_MEGAGROUP /*-2147483648*/:
                        case C0700C.ENCODING_PCM_32BIT /*1073741824*/:
                            i5 = max;
                            break;
                        case RecyclerView.TOUCH_SLOP_DEFAULT /*0*/:
                            i2 = RecyclerView.TOUCH_SLOP_DEFAULT;
                            break;
                        default:
                            i2 = RecyclerView.TOUCH_SLOP_DEFAULT;
                            break;
                    }
                    max = i5;
                    i5 = i2;
                } else {
                    if (i4 == -2) {
                        max = RecyclerView.TOUCH_SLOP_DEFAULT;
                    }
                    max = RecyclerView.TOUCH_SLOP_DEFAULT;
                }
            } else if (i4 >= 0) {
                i5 = C0700C.ENCODING_PCM_32BIT;
                max = i4;
            } else if (i4 == RecyclerView.NO_POSITION) {
                i5 = i2;
            } else {
                if (i4 == -2) {
                    if (i2 == TLRPC.MESSAGE_FLAG_MEGAGROUP || i2 == C0700C.ENCODING_PCM_32BIT) {
                        i5 = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                    }
                }
                max = RecyclerView.TOUCH_SLOP_DEFAULT;
            }
            return MeasureSpec.makeMeasureSpec(max, i5);
        }

        @Deprecated
        public static int getChildMeasureSpec(int i, int i2, int i3, boolean z) {
            int i4 = C0700C.ENCODING_PCM_32BIT;
            int max = Math.max(RecyclerView.TOUCH_SLOP_DEFAULT, i - i2);
            if (z) {
                if (i3 < 0) {
                    i4 = RecyclerView.TOUCH_SLOP_DEFAULT;
                    i3 = RecyclerView.TOUCH_SLOP_DEFAULT;
                }
            } else if (i3 < 0) {
                if (i3 == RecyclerView.NO_POSITION) {
                    i3 = max;
                } else if (i3 == -2) {
                    i4 = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                    i3 = max;
                } else {
                    i4 = RecyclerView.TOUCH_SLOP_DEFAULT;
                    i3 = RecyclerView.TOUCH_SLOP_DEFAULT;
                }
            }
            return MeasureSpec.makeMeasureSpec(i3, i4);
        }

        public static Properties getProperties(Context context, AttributeSet attributeSet, int i, int i2) {
            Properties properties = new Properties();
            properties.orientation = RecyclerView.VERTICAL;
            properties.spanCount = RecyclerView.VERTICAL;
            properties.reverseLayout = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            properties.stackFromEnd = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            return properties;
        }

        private static boolean isMeasurementUpToDate(int i, int i2, int i3) {
            int mode = MeasureSpec.getMode(i2);
            int size = MeasureSpec.getSize(i2);
            if (i3 > 0 && i != i3) {
                return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            }
            switch (mode) {
                case TLRPC.MESSAGE_FLAG_MEGAGROUP /*-2147483648*/:
                    return size < i ? RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : true;
                case RecyclerView.TOUCH_SLOP_DEFAULT /*0*/:
                    return true;
                case C0700C.ENCODING_PCM_32BIT /*1073741824*/:
                    return size != i ? RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : true;
                default:
                    return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            }
        }

        private void onSmoothScrollerStopped(SmoothScroller smoothScroller) {
            if (this.mSmoothScroller == smoothScroller) {
                this.mSmoothScroller = null;
            }
        }

        private void scrapOrRecycleView(Recycler recycler, int i, View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (!childViewHolderInt.shouldIgnore()) {
                if (!childViewHolderInt.isInvalid() || childViewHolderInt.isRemoved() || this.mRecyclerView.mAdapter.hasStableIds()) {
                    detachViewAt(i);
                    recycler.scrapView(view);
                    this.mRecyclerView.mViewInfoStore.onViewDetached(childViewHolderInt);
                    return;
                }
                removeViewAt(i);
                recycler.recycleViewHolderInternal(childViewHolderInt);
            }
        }

        public void addDisappearingView(View view) {
            addDisappearingView(view, RecyclerView.NO_POSITION);
        }

        public void addDisappearingView(View view, int i) {
            addViewInt(view, i, true);
        }

        public void addView(View view) {
            addView(view, RecyclerView.NO_POSITION);
        }

        public void addView(View view, int i) {
            addViewInt(view, i, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
        }

        public void assertInLayoutOrScroll(String str) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.assertInLayoutOrScroll(str);
            }
        }

        public void assertNotInLayoutOrScroll(String str) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.assertNotInLayoutOrScroll(str);
            }
        }

        public void attachView(View view) {
            attachView(view, RecyclerView.NO_POSITION);
        }

        public void attachView(View view, int i) {
            attachView(view, i, (LayoutParams) view.getLayoutParams());
        }

        public void attachView(View view, int i, LayoutParams layoutParams) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.isRemoved()) {
                this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(childViewHolderInt);
            } else {
                this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(childViewHolderInt);
            }
            this.mChildHelper.attachViewToParent(view, i, layoutParams, childViewHolderInt.isRemoved());
        }

        public void calculateItemDecorationsForChild(View view, Rect rect) {
            if (this.mRecyclerView == null) {
                rect.set(RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.TOUCH_SLOP_DEFAULT);
            } else {
                rect.set(this.mRecyclerView.getItemDecorInsetsForChild(view));
            }
        }

        public boolean canScrollHorizontally() {
            return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public boolean canScrollVertically() {
            return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public boolean checkLayoutParams(LayoutParams layoutParams) {
            return layoutParams != null ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public int computeHorizontalScrollExtent(State state) {
            return RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int computeHorizontalScrollOffset(State state) {
            return RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int computeHorizontalScrollRange(State state) {
            return RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int computeVerticalScrollExtent(State state) {
            return RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int computeVerticalScrollOffset(State state) {
            return RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int computeVerticalScrollRange(State state) {
            return RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public void detachAndScrapAttachedViews(Recycler recycler) {
            for (int childCount = getChildCount() + RecyclerView.NO_POSITION; childCount >= 0; childCount += RecyclerView.NO_POSITION) {
                scrapOrRecycleView(recycler, childCount, getChildAt(childCount));
            }
        }

        public void detachAndScrapView(View view, Recycler recycler) {
            scrapOrRecycleView(recycler, this.mChildHelper.indexOfChild(view), view);
        }

        public void detachAndScrapViewAt(int i, Recycler recycler) {
            scrapOrRecycleView(recycler, i, getChildAt(i));
        }

        public void detachView(View view) {
            int indexOfChild = this.mChildHelper.indexOfChild(view);
            if (indexOfChild >= 0) {
                detachViewInternal(indexOfChild, view);
            }
        }

        public void detachViewAt(int i) {
            detachViewInternal(i, getChildAt(i));
        }

        void dispatchAttachedToWindow(RecyclerView recyclerView) {
            this.mIsAttachedToWindow = true;
            onAttachedToWindow(recyclerView);
        }

        void dispatchDetachedFromWindow(RecyclerView recyclerView, Recycler recycler) {
            this.mIsAttachedToWindow = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            onDetachedFromWindow(recyclerView, recycler);
        }

        public void endAnimation(View view) {
            if (this.mRecyclerView.mItemAnimator != null) {
                this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(view));
            }
        }

        @Nullable
        public View findContainingItemView(View view) {
            if (this.mRecyclerView == null) {
                return null;
            }
            View findContainingItemView = this.mRecyclerView.findContainingItemView(view);
            return (findContainingItemView == null || this.mChildHelper.isHidden(findContainingItemView)) ? null : findContainingItemView;
        }

        public View findViewByPosition(int i) {
            int childCount = getChildCount();
            for (int i2 = RecyclerView.TOUCH_SLOP_DEFAULT; i2 < childCount; i2 += RecyclerView.VERTICAL) {
                View childAt = getChildAt(i2);
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(childAt);
                if (childViewHolderInt != null && childViewHolderInt.getLayoutPosition() == i && !childViewHolderInt.shouldIgnore() && (this.mRecyclerView.mState.isPreLayout() || !childViewHolderInt.isRemoved())) {
                    return childAt;
                }
            }
            return null;
        }

        public abstract LayoutParams generateDefaultLayoutParams();

        public LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
            return new LayoutParams(context, attributeSet);
        }

        public LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            return layoutParams instanceof LayoutParams ? new LayoutParams((LayoutParams) layoutParams) : layoutParams instanceof MarginLayoutParams ? new LayoutParams((MarginLayoutParams) layoutParams) : new LayoutParams(layoutParams);
        }

        public int getBaseline() {
            return RecyclerView.NO_POSITION;
        }

        public int getBottomDecorationHeight(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.bottom;
        }

        public View getChildAt(int i) {
            return this.mChildHelper != null ? this.mChildHelper.getChildAt(i) : null;
        }

        public int getChildCount() {
            return this.mChildHelper != null ? this.mChildHelper.getChildCount() : RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public boolean getClipToPadding() {
            return (this.mRecyclerView == null || !this.mRecyclerView.mClipToPadding) ? RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : true;
        }

        public int getColumnCountForAccessibility(Recycler recycler, State state) {
            return (this.mRecyclerView == null || this.mRecyclerView.mAdapter == null || !canScrollHorizontally()) ? RecyclerView.VERTICAL : this.mRecyclerView.mAdapter.getItemCount();
        }

        public int getDecoratedBottom(View view) {
            return view.getBottom() + getBottomDecorationHeight(view);
        }

        public int getDecoratedLeft(View view) {
            return view.getLeft() - getLeftDecorationWidth(view);
        }

        public int getDecoratedMeasuredHeight(View view) {
            Rect rect = ((LayoutParams) view.getLayoutParams()).mDecorInsets;
            return rect.bottom + (view.getMeasuredHeight() + rect.top);
        }

        public int getDecoratedMeasuredWidth(View view) {
            Rect rect = ((LayoutParams) view.getLayoutParams()).mDecorInsets;
            return rect.right + (view.getMeasuredWidth() + rect.left);
        }

        public int getDecoratedRight(View view) {
            return view.getRight() + getRightDecorationWidth(view);
        }

        public int getDecoratedTop(View view) {
            return view.getTop() - getTopDecorationHeight(view);
        }

        public View getFocusedChild() {
            if (this.mRecyclerView == null) {
                return null;
            }
            View focusedChild = this.mRecyclerView.getFocusedChild();
            return (focusedChild == null || this.mChildHelper.isHidden(focusedChild)) ? null : focusedChild;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public int getHeightMode() {
            return this.mHeightMode;
        }

        public int getItemCount() {
            Adapter adapter = this.mRecyclerView != null ? this.mRecyclerView.getAdapter() : null;
            return adapter != null ? adapter.getItemCount() : RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int getItemViewType(View view) {
            return RecyclerView.getChildViewHolderInt(view).getItemViewType();
        }

        public int getLayoutDirection() {
            return ViewCompat.getLayoutDirection(this.mRecyclerView);
        }

        public int getLeftDecorationWidth(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.left;
        }

        public int getMinimumHeight() {
            return ViewCompat.getMinimumHeight(this.mRecyclerView);
        }

        public int getMinimumWidth() {
            return ViewCompat.getMinimumWidth(this.mRecyclerView);
        }

        public int getPaddingBottom() {
            return this.mRecyclerView != null ? this.mRecyclerView.getPaddingBottom() : RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int getPaddingEnd() {
            return this.mRecyclerView != null ? ViewCompat.getPaddingEnd(this.mRecyclerView) : RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int getPaddingLeft() {
            return this.mRecyclerView != null ? this.mRecyclerView.getPaddingLeft() : RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int getPaddingRight() {
            return this.mRecyclerView != null ? this.mRecyclerView.getPaddingRight() : RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int getPaddingStart() {
            return this.mRecyclerView != null ? ViewCompat.getPaddingStart(this.mRecyclerView) : RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int getPaddingTop() {
            return this.mRecyclerView != null ? this.mRecyclerView.getPaddingTop() : RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int getPosition(View view) {
            return ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        }

        public int getRightDecorationWidth(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.right;
        }

        public int getRowCountForAccessibility(Recycler recycler, State state) {
            return (this.mRecyclerView == null || this.mRecyclerView.mAdapter == null || !canScrollVertically()) ? RecyclerView.VERTICAL : this.mRecyclerView.mAdapter.getItemCount();
        }

        public int getSelectionModeForAccessibility(Recycler recycler, State state) {
            return RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public int getTopDecorationHeight(View view) {
            return ((LayoutParams) view.getLayoutParams()).mDecorInsets.top;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getWidthMode() {
            return this.mWidthMode;
        }

        boolean hasFlexibleChildInBothOrientations() {
            int childCount = getChildCount();
            for (int i = RecyclerView.TOUCH_SLOP_DEFAULT; i < childCount; i += RecyclerView.VERTICAL) {
                android.view.ViewGroup.LayoutParams layoutParams = getChildAt(i).getLayoutParams();
                if (layoutParams.width < 0 && layoutParams.height < 0) {
                    return true;
                }
            }
            return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public boolean hasFocus() {
            return (this.mRecyclerView == null || !this.mRecyclerView.hasFocus()) ? RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : true;
        }

        public void ignoreView(View view) {
            if (view.getParent() != this.mRecyclerView || this.mRecyclerView.indexOfChild(view) == RecyclerView.NO_POSITION) {
                throw new IllegalArgumentException("View should be fully attached to be ignored");
            }
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            childViewHolderInt.addFlags(TLRPC.USER_FLAG_UNUSED);
            this.mRecyclerView.mViewInfoStore.removeViewHolder(childViewHolderInt);
        }

        public boolean isAttachedToWindow() {
            return this.mIsAttachedToWindow;
        }

        public boolean isAutoMeasureEnabled() {
            return this.mAutoMeasure;
        }

        public boolean isFocused() {
            return (this.mRecyclerView == null || !this.mRecyclerView.isFocused()) ? RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : true;
        }

        public boolean isLayoutHierarchical(Recycler recycler, State state) {
            return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public boolean isMeasurementCacheEnabled() {
            return this.mMeasurementCacheEnabled;
        }

        public boolean isSmoothScrolling() {
            return (this.mSmoothScroller == null || !this.mSmoothScroller.isRunning()) ? RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : true;
        }

        public void layoutDecorated(View view, int i, int i2, int i3, int i4) {
            Rect rect = ((LayoutParams) view.getLayoutParams()).mDecorInsets;
            view.layout(rect.left + i, rect.top + i2, i3 - rect.right, i4 - rect.bottom);
        }

        public void measureChild(View view, int i, int i2) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            Rect itemDecorInsetsForChild = this.mRecyclerView.getItemDecorInsetsForChild(view);
            int i3 = (itemDecorInsetsForChild.left + itemDecorInsetsForChild.right) + i;
            int i4 = (itemDecorInsetsForChild.bottom + itemDecorInsetsForChild.top) + i2;
            i3 = getChildMeasureSpec(getWidth(), getWidthMode(), i3 + (getPaddingLeft() + getPaddingRight()), layoutParams.width, canScrollHorizontally());
            i4 = getChildMeasureSpec(getHeight(), getHeightMode(), i4 + (getPaddingTop() + getPaddingBottom()), layoutParams.height, canScrollVertically());
            if (shouldMeasureChild(view, i3, i4, layoutParams)) {
                view.measure(i3, i4);
            }
        }

        public void measureChildWithMargins(View view, int i, int i2) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            Rect itemDecorInsetsForChild = this.mRecyclerView.getItemDecorInsetsForChild(view);
            int i3 = (itemDecorInsetsForChild.left + itemDecorInsetsForChild.right) + i;
            int i4 = (itemDecorInsetsForChild.bottom + itemDecorInsetsForChild.top) + i2;
            i3 = getChildMeasureSpec(getWidth(), getWidthMode(), i3 + (((getPaddingLeft() + getPaddingRight()) + layoutParams.leftMargin) + layoutParams.rightMargin), layoutParams.width, canScrollHorizontally());
            i4 = getChildMeasureSpec(getHeight(), getHeightMode(), i4 + (((getPaddingTop() + getPaddingBottom()) + layoutParams.topMargin) + layoutParams.bottomMargin), layoutParams.height, canScrollVertically());
            if (shouldMeasureChild(view, i3, i4, layoutParams)) {
                view.measure(i3, i4);
            }
        }

        public void moveView(int i, int i2) {
            View childAt = getChildAt(i);
            if (childAt == null) {
                throw new IllegalArgumentException("Cannot move a child from non-existing index:" + i);
            }
            detachViewAt(i);
            attachView(childAt, i2);
        }

        public void offsetChildrenHorizontal(int i) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.offsetChildrenHorizontal(i);
            }
        }

        public void offsetChildrenVertical(int i) {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.offsetChildrenVertical(i);
            }
        }

        public void onAdapterChanged(Adapter adapter, Adapter adapter2) {
        }

        public boolean onAddFocusables(RecyclerView recyclerView, ArrayList<View> arrayList, int i, int i2) {
            return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        @CallSuper
        public void onAttachedToWindow(RecyclerView recyclerView) {
        }

        @Deprecated
        public void onDetachedFromWindow(RecyclerView recyclerView) {
        }

        @CallSuper
        public void onDetachedFromWindow(RecyclerView recyclerView, Recycler recycler) {
            onDetachedFromWindow(recyclerView);
        }

        @Nullable
        public View onFocusSearchFailed(View view, int i, Recycler recycler, State state) {
            return null;
        }

        public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, accessibilityEvent);
        }

        public void onInitializeAccessibilityEvent(Recycler recycler, State state, AccessibilityEvent accessibilityEvent) {
            boolean z = true;
            AccessibilityRecordCompat asRecord = AccessibilityEventCompat.asRecord(accessibilityEvent);
            if (this.mRecyclerView != null && asRecord != null) {
                if (!(ViewCompat.canScrollVertically(this.mRecyclerView, RecyclerView.VERTICAL) || ViewCompat.canScrollVertically(this.mRecyclerView, RecyclerView.NO_POSITION) || ViewCompat.canScrollHorizontally(this.mRecyclerView, RecyclerView.NO_POSITION) || ViewCompat.canScrollHorizontally(this.mRecyclerView, RecyclerView.VERTICAL))) {
                    z = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                }
                asRecord.setScrollable(z);
                if (this.mRecyclerView.mAdapter != null) {
                    asRecord.setItemCount(this.mRecyclerView.mAdapter.getItemCount());
                }
            }
        }

        void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, accessibilityNodeInfoCompat);
        }

        public void onInitializeAccessibilityNodeInfo(Recycler recycler, State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            if (ViewCompat.canScrollVertically(this.mRecyclerView, RecyclerView.NO_POSITION) || ViewCompat.canScrollHorizontally(this.mRecyclerView, RecyclerView.NO_POSITION)) {
                accessibilityNodeInfoCompat.addAction((int) MessagesController.UPDATE_MASK_CHANNEL);
                accessibilityNodeInfoCompat.setScrollable(true);
            }
            if (ViewCompat.canScrollVertically(this.mRecyclerView, RecyclerView.VERTICAL) || ViewCompat.canScrollHorizontally(this.mRecyclerView, RecyclerView.VERTICAL)) {
                accessibilityNodeInfoCompat.addAction((int) ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
                accessibilityNodeInfoCompat.setScrollable(true);
            }
            accessibilityNodeInfoCompat.setCollectionInfo(CollectionInfoCompat.obtain(getRowCountForAccessibility(recycler, state), getColumnCountForAccessibility(recycler, state), isLayoutHierarchical(recycler, state), getSelectionModeForAccessibility(recycler, state)));
        }

        void onInitializeAccessibilityNodeInfoForItem(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt != null && !childViewHolderInt.isRemoved() && !this.mChildHelper.isHidden(childViewHolderInt.itemView)) {
                onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, view, accessibilityNodeInfoCompat);
            }
        }

        public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(canScrollVertically() ? getPosition(view) : RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.VERTICAL, canScrollHorizontally() ? getPosition(view) : RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.VERTICAL, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST));
        }

        public View onInterceptFocusSearch(View view, int i) {
            return null;
        }

        public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        }

        public void onItemsChanged(RecyclerView recyclerView) {
        }

        public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        }

        public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        }

        public void onItemsUpdated(RecyclerView recyclerView, int i, int i2) {
        }

        public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
            onItemsUpdated(recyclerView, i, i2);
        }

        public void onLayoutChildren(Recycler recycler, State state) {
            Log.e(RecyclerView.TAG, "You must override onLayoutChildren(Recycler recycler, State state) ");
        }

        public void onMeasure(Recycler recycler, State state, int i, int i2) {
            this.mRecyclerView.defaultOnMeasure(i, i2);
        }

        @Deprecated
        public boolean onRequestChildFocus(RecyclerView recyclerView, View view, View view2) {
            return (isSmoothScrolling() || recyclerView.isComputingLayout()) ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public boolean onRequestChildFocus(RecyclerView recyclerView, State state, View view, View view2) {
            return onRequestChildFocus(recyclerView, view, view2);
        }

        public void onRestoreInstanceState(Parcelable parcelable) {
        }

        public Parcelable onSaveInstanceState() {
            return null;
        }

        public void onScrollStateChanged(int i) {
        }

        boolean performAccessibilityAction(int i, Bundle bundle) {
            return performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, i, bundle);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean performAccessibilityAction(com.hanista.mobogram.messenger.support.widget.RecyclerView.Recycler r7, com.hanista.mobogram.messenger.support.widget.RecyclerView.State r8, int r9, android.os.Bundle r10) {
            /*
            r6 = this;
            r4 = -1;
            r2 = 1;
            r1 = 0;
            r0 = r6.mRecyclerView;
            if (r0 != 0) goto L_0x0008;
        L_0x0007:
            return r1;
        L_0x0008:
            switch(r9) {
                case 4096: goto L_0x004a;
                case 8192: goto L_0x0018;
                default: goto L_0x000b;
            };
        L_0x000b:
            r0 = r1;
            r3 = r1;
        L_0x000d:
            if (r3 != 0) goto L_0x0011;
        L_0x000f:
            if (r0 == 0) goto L_0x0007;
        L_0x0011:
            r1 = r6.mRecyclerView;
            r1.scrollBy(r0, r3);
            r1 = r2;
            goto L_0x0007;
        L_0x0018:
            r0 = r6.mRecyclerView;
            r0 = android.support.v4.view.ViewCompat.canScrollVertically(r0, r4);
            if (r0 == 0) goto L_0x007f;
        L_0x0020:
            r0 = r6.getHeight();
            r3 = r6.getPaddingTop();
            r0 = r0 - r3;
            r3 = r6.getPaddingBottom();
            r0 = r0 - r3;
            r0 = -r0;
        L_0x002f:
            r3 = r6.mRecyclerView;
            r3 = android.support.v4.view.ViewCompat.canScrollHorizontally(r3, r4);
            if (r3 == 0) goto L_0x007a;
        L_0x0037:
            r3 = r6.getWidth();
            r4 = r6.getPaddingLeft();
            r3 = r3 - r4;
            r4 = r6.getPaddingRight();
            r3 = r3 - r4;
            r3 = -r3;
            r5 = r3;
            r3 = r0;
            r0 = r5;
            goto L_0x000d;
        L_0x004a:
            r0 = r6.mRecyclerView;
            r0 = android.support.v4.view.ViewCompat.canScrollVertically(r0, r2);
            if (r0 == 0) goto L_0x007d;
        L_0x0052:
            r0 = r6.getHeight();
            r3 = r6.getPaddingTop();
            r0 = r0 - r3;
            r3 = r6.getPaddingBottom();
            r0 = r0 - r3;
        L_0x0060:
            r3 = r6.mRecyclerView;
            r3 = android.support.v4.view.ViewCompat.canScrollHorizontally(r3, r2);
            if (r3 == 0) goto L_0x007a;
        L_0x0068:
            r3 = r6.getWidth();
            r4 = r6.getPaddingLeft();
            r3 = r3 - r4;
            r4 = r6.getPaddingRight();
            r3 = r3 - r4;
            r5 = r3;
            r3 = r0;
            r0 = r5;
            goto L_0x000d;
        L_0x007a:
            r3 = r0;
            r0 = r1;
            goto L_0x000d;
        L_0x007d:
            r0 = r1;
            goto L_0x0060;
        L_0x007f:
            r0 = r1;
            goto L_0x002f;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager.performAccessibilityAction(com.hanista.mobogram.messenger.support.widget.RecyclerView$Recycler, com.hanista.mobogram.messenger.support.widget.RecyclerView$State, int, android.os.Bundle):boolean");
        }

        boolean performAccessibilityActionForItem(View view, int i, Bundle bundle) {
            return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, view, i, bundle);
        }

        public boolean performAccessibilityActionForItem(Recycler recycler, State state, View view, int i, Bundle bundle) {
            return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public void postOnAnimation(Runnable runnable) {
            if (this.mRecyclerView != null) {
                ViewCompat.postOnAnimation(this.mRecyclerView, runnable);
            }
        }

        public void removeAllViews() {
            for (int childCount = getChildCount() + RecyclerView.NO_POSITION; childCount >= 0; childCount += RecyclerView.NO_POSITION) {
                this.mChildHelper.removeViewAt(childCount);
            }
        }

        public void removeAndRecycleAllViews(Recycler recycler) {
            for (int childCount = getChildCount() + RecyclerView.NO_POSITION; childCount >= 0; childCount += RecyclerView.NO_POSITION) {
                if (!RecyclerView.getChildViewHolderInt(getChildAt(childCount)).shouldIgnore()) {
                    removeAndRecycleViewAt(childCount, recycler);
                }
            }
        }

        void removeAndRecycleScrapInt(Recycler recycler) {
            int scrapCount = recycler.getScrapCount();
            for (int i = scrapCount + RecyclerView.NO_POSITION; i >= 0; i += RecyclerView.NO_POSITION) {
                View scrapViewAt = recycler.getScrapViewAt(i);
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(scrapViewAt);
                if (!childViewHolderInt.shouldIgnore()) {
                    childViewHolderInt.setIsRecyclable(RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
                    if (childViewHolderInt.isTmpDetached()) {
                        this.mRecyclerView.removeDetachedView(scrapViewAt, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
                    }
                    if (this.mRecyclerView.mItemAnimator != null) {
                        this.mRecyclerView.mItemAnimator.endAnimation(childViewHolderInt);
                    }
                    childViewHolderInt.setIsRecyclable(true);
                    recycler.quickRecycleScrapView(scrapViewAt);
                }
            }
            recycler.clearScrap();
            if (scrapCount > 0) {
                this.mRecyclerView.invalidate();
            }
        }

        public void removeAndRecycleView(View view, Recycler recycler) {
            removeView(view);
            recycler.recycleView(view);
        }

        public void removeAndRecycleViewAt(int i, Recycler recycler) {
            View childAt = getChildAt(i);
            removeViewAt(i);
            recycler.recycleView(childAt);
        }

        public boolean removeCallbacks(Runnable runnable) {
            return this.mRecyclerView != null ? this.mRecyclerView.removeCallbacks(runnable) : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public void removeDetachedView(View view) {
            this.mRecyclerView.removeDetachedView(view, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
        }

        public void removeView(View view) {
            this.mChildHelper.removeView(view);
        }

        public void removeViewAt(int i) {
            if (getChildAt(i) != null) {
                this.mChildHelper.removeViewAt(i);
            }
        }

        public boolean requestChildRectangleOnScreen(RecyclerView recyclerView, View view, Rect rect, boolean z) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int width = getWidth() - getPaddingRight();
            int height = getHeight() - getPaddingBottom();
            int left = (view.getLeft() + rect.left) - view.getScrollX();
            int top = (view.getTop() + rect.top) - view.getScrollY();
            int width2 = left + rect.width();
            int height2 = top + rect.height();
            int min = Math.min(RecyclerView.TOUCH_SLOP_DEFAULT, left - paddingLeft);
            int min2 = Math.min(RecyclerView.TOUCH_SLOP_DEFAULT, top - paddingTop);
            int max = Math.max(RecyclerView.TOUCH_SLOP_DEFAULT, width2 - width);
            height = Math.max(RecyclerView.TOUCH_SLOP_DEFAULT, height2 - height);
            if (getLayoutDirection() == RecyclerView.VERTICAL) {
                if (max == 0) {
                    max = Math.max(min, width2 - width);
                }
                min = max;
            } else {
                min = min != 0 ? min : Math.min(left - paddingLeft, max);
            }
            max = min2 != 0 ? min2 : Math.min(top - paddingTop, height);
            if (min == 0 && max == 0) {
                return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            }
            if (z) {
                recyclerView.scrollBy(min, max);
            } else {
                recyclerView.smoothScrollBy(min, max);
            }
            return true;
        }

        public void requestLayout() {
            if (this.mRecyclerView != null) {
                this.mRecyclerView.requestLayout();
            }
        }

        public void requestSimpleAnimationsInNextLayout() {
            this.mRequestedSimpleAnimations = true;
        }

        public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
            return RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public void scrollToPosition(int i) {
        }

        public int scrollVerticallyBy(int i, Recycler recycler, State state) {
            return RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public void setAutoMeasureEnabled(boolean z) {
            this.mAutoMeasure = z;
        }

        void setExactMeasureSpecsFrom(RecyclerView recyclerView) {
            setMeasureSpecs(MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(recyclerView.getHeight(), C0700C.ENCODING_PCM_32BIT));
        }

        void setMeasureSpecs(int i, int i2) {
            this.mWidth = MeasureSpec.getSize(i);
            this.mWidthMode = MeasureSpec.getMode(i);
            if (this.mWidthMode == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC) {
                this.mWidth = RecyclerView.TOUCH_SLOP_DEFAULT;
            }
            this.mHeight = MeasureSpec.getSize(i2);
            this.mHeightMode = MeasureSpec.getMode(i2);
            if (this.mHeightMode == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC) {
                this.mHeight = RecyclerView.TOUCH_SLOP_DEFAULT;
            }
        }

        public void setMeasuredDimension(int i, int i2) {
            this.mRecyclerView.setMeasuredDimension(i, i2);
        }

        public void setMeasuredDimension(Rect rect, int i, int i2) {
            setMeasuredDimension(chooseSize(i, (rect.width() + getPaddingLeft()) + getPaddingRight(), getMinimumWidth()), chooseSize(i2, (rect.height() + getPaddingTop()) + getPaddingBottom(), getMinimumHeight()));
        }

        void setMeasuredDimensionFromChildren(int i, int i2) {
            int i3 = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int i4 = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            int childCount = getChildCount();
            if (childCount == 0) {
                this.mRecyclerView.defaultOnMeasure(i, i2);
                return;
            }
            int i5 = RecyclerView.TOUCH_SLOP_DEFAULT;
            int i6 = TLRPC.MESSAGE_FLAG_MEGAGROUP;
            int i7 = ConnectionsManager.DEFAULT_DATACENTER_ID;
            while (i5 < childCount) {
                View childAt = getChildAt(i5);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int decoratedLeft = getDecoratedLeft(childAt) - layoutParams.leftMargin;
                int decoratedRight = layoutParams.rightMargin + getDecoratedRight(childAt);
                int decoratedTop = getDecoratedTop(childAt) - layoutParams.topMargin;
                int decoratedBottom = layoutParams.bottomMargin + getDecoratedBottom(childAt);
                if (decoratedLeft >= i7) {
                    decoratedLeft = i7;
                }
                if (decoratedRight <= i6) {
                    decoratedRight = i6;
                }
                if (decoratedTop >= i3) {
                    decoratedTop = i3;
                }
                if (decoratedBottom <= i4) {
                    decoratedBottom = i4;
                }
                i5 += RecyclerView.VERTICAL;
                i6 = decoratedRight;
                i3 = decoratedTop;
                i7 = decoratedLeft;
                i4 = decoratedBottom;
            }
            this.mRecyclerView.mTempRect.set(i7, i3, i6, i4);
            setMeasuredDimension(this.mRecyclerView.mTempRect, i, i2);
        }

        public void setMeasurementCacheEnabled(boolean z) {
            this.mMeasurementCacheEnabled = z;
        }

        void setRecyclerView(RecyclerView recyclerView) {
            if (recyclerView == null) {
                this.mRecyclerView = null;
                this.mChildHelper = null;
                this.mWidth = RecyclerView.TOUCH_SLOP_DEFAULT;
                this.mHeight = RecyclerView.TOUCH_SLOP_DEFAULT;
            } else {
                this.mRecyclerView = recyclerView;
                this.mChildHelper = recyclerView.mChildHelper;
                this.mWidth = recyclerView.getWidth();
                this.mHeight = recyclerView.getHeight();
            }
            this.mWidthMode = C0700C.ENCODING_PCM_32BIT;
            this.mHeightMode = C0700C.ENCODING_PCM_32BIT;
        }

        boolean shouldMeasureChild(View view, int i, int i2, LayoutParams layoutParams) {
            return (!view.isLayoutRequested() && this.mMeasurementCacheEnabled && isMeasurementUpToDate(view.getWidth(), i, layoutParams.width) && isMeasurementUpToDate(view.getHeight(), i2, layoutParams.height)) ? RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : true;
        }

        boolean shouldMeasureTwice() {
            return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        boolean shouldReMeasureChild(View view, int i, int i2, LayoutParams layoutParams) {
            return (this.mMeasurementCacheEnabled && isMeasurementUpToDate(view.getMeasuredWidth(), i, layoutParams.width) && isMeasurementUpToDate(view.getMeasuredHeight(), i2, layoutParams.height)) ? RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : true;
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
            Log.e(RecyclerView.TAG, "You must override smoothScrollToPosition to support smooth scrolling");
        }

        public void startSmoothScroll(SmoothScroller smoothScroller) {
            if (!(this.mSmoothScroller == null || smoothScroller == this.mSmoothScroller || !this.mSmoothScroller.isRunning())) {
                this.mSmoothScroller.stop();
            }
            this.mSmoothScroller = smoothScroller;
            this.mSmoothScroller.start(this.mRecyclerView, this);
        }

        public void stopIgnoringView(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            childViewHolderInt.stopIgnoring();
            childViewHolderInt.resetInternal();
            childViewHolderInt.addFlags(4);
        }

        void stopSmoothScroller() {
            if (this.mSmoothScroller != null) {
                this.mSmoothScroller.stop();
            }
        }

        public boolean supportsPredictiveItemAnimations() {
            return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }
    }

    public static abstract class SmoothScroller {
        private LayoutManager mLayoutManager;
        private boolean mPendingInitialRun;
        private RecyclerView mRecyclerView;
        private final Action mRecyclingAction;
        private boolean mRunning;
        private int mTargetPosition;
        private View mTargetView;

        public static class Action {
            public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
            private boolean changed;
            private int consecutiveUpdates;
            private int mDuration;
            private int mDx;
            private int mDy;
            private Interpolator mInterpolator;
            private int mJumpToPosition;

            public Action(int i, int i2) {
                this(i, i2, UNDEFINED_DURATION, null);
            }

            public Action(int i, int i2, int i3) {
                this(i, i2, i3, null);
            }

            public Action(int i, int i2, int i3, Interpolator interpolator) {
                this.mJumpToPosition = RecyclerView.NO_POSITION;
                this.changed = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                this.consecutiveUpdates = RecyclerView.TOUCH_SLOP_DEFAULT;
                this.mDx = i;
                this.mDy = i2;
                this.mDuration = i3;
                this.mInterpolator = interpolator;
            }

            private void runIfNecessary(RecyclerView recyclerView) {
                if (this.mJumpToPosition >= 0) {
                    int i = this.mJumpToPosition;
                    this.mJumpToPosition = RecyclerView.NO_POSITION;
                    recyclerView.jumpToPositionForSmoothScroller(i);
                    this.changed = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                } else if (this.changed) {
                    validate();
                    if (this.mInterpolator != null) {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
                    } else if (this.mDuration == UNDEFINED_DURATION) {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
                    } else {
                        recyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
                    }
                    this.consecutiveUpdates += RecyclerView.VERTICAL;
                    if (this.consecutiveUpdates > 10) {
                        Log.e(RecyclerView.TAG, "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
                    }
                    this.changed = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                } else {
                    this.consecutiveUpdates = RecyclerView.TOUCH_SLOP_DEFAULT;
                }
            }

            private void validate() {
                if (this.mInterpolator != null && this.mDuration < RecyclerView.VERTICAL) {
                    throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
                } else if (this.mDuration < RecyclerView.VERTICAL) {
                    throw new IllegalStateException("Scroll duration must be a positive number");
                }
            }

            public int getDuration() {
                return this.mDuration;
            }

            public int getDx() {
                return this.mDx;
            }

            public int getDy() {
                return this.mDy;
            }

            public Interpolator getInterpolator() {
                return this.mInterpolator;
            }

            boolean hasJumpTarget() {
                return this.mJumpToPosition >= 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            }

            public void jumpTo(int i) {
                this.mJumpToPosition = i;
            }

            public void setDuration(int i) {
                this.changed = true;
                this.mDuration = i;
            }

            public void setDx(int i) {
                this.changed = true;
                this.mDx = i;
            }

            public void setDy(int i) {
                this.changed = true;
                this.mDy = i;
            }

            public void setInterpolator(Interpolator interpolator) {
                this.changed = true;
                this.mInterpolator = interpolator;
            }

            public void update(int i, int i2, int i3, Interpolator interpolator) {
                this.mDx = i;
                this.mDy = i2;
                this.mDuration = i3;
                this.mInterpolator = interpolator;
                this.changed = true;
            }
        }

        public SmoothScroller() {
            this.mTargetPosition = RecyclerView.NO_POSITION;
            this.mRecyclingAction = new Action(RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.TOUCH_SLOP_DEFAULT);
        }

        private void onAnimation(int i, int i2) {
            RecyclerView recyclerView = this.mRecyclerView;
            if (!this.mRunning || this.mTargetPosition == RecyclerView.NO_POSITION || recyclerView == null) {
                stop();
            }
            this.mPendingInitialRun = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            if (this.mTargetView != null) {
                if (getChildPosition(this.mTargetView) == this.mTargetPosition) {
                    onTargetFound(this.mTargetView, recyclerView.mState, this.mRecyclingAction);
                    this.mRecyclingAction.runIfNecessary(recyclerView);
                    stop();
                } else {
                    Log.e(RecyclerView.TAG, "Passed over target position while smooth scrolling.");
                    this.mTargetView = null;
                }
            }
            if (this.mRunning) {
                onSeekTargetStep(i, i2, recyclerView.mState, this.mRecyclingAction);
                boolean hasJumpTarget = this.mRecyclingAction.hasJumpTarget();
                this.mRecyclingAction.runIfNecessary(recyclerView);
                if (!hasJumpTarget) {
                    return;
                }
                if (this.mRunning) {
                    this.mPendingInitialRun = true;
                    recyclerView.mViewFlinger.postOnAnimation();
                    return;
                }
                stop();
            }
        }

        public View findViewByPosition(int i) {
            return this.mRecyclerView.mLayout.findViewByPosition(i);
        }

        public int getChildCount() {
            return this.mRecyclerView.mLayout.getChildCount();
        }

        public int getChildPosition(View view) {
            return this.mRecyclerView.getChildLayoutPosition(view);
        }

        @Nullable
        public LayoutManager getLayoutManager() {
            return this.mLayoutManager;
        }

        public int getTargetPosition() {
            return this.mTargetPosition;
        }

        @Deprecated
        public void instantScrollToPosition(int i) {
            this.mRecyclerView.scrollToPosition(i);
        }

        public boolean isPendingInitialRun() {
            return this.mPendingInitialRun;
        }

        public boolean isRunning() {
            return this.mRunning;
        }

        protected void normalize(PointF pointF) {
            double sqrt = Math.sqrt((double) ((pointF.x * pointF.x) + (pointF.y * pointF.y)));
            pointF.x = (float) (((double) pointF.x) / sqrt);
            pointF.y = (float) (((double) pointF.y) / sqrt);
        }

        protected void onChildAttachedToWindow(View view) {
            if (getChildPosition(view) == getTargetPosition()) {
                this.mTargetView = view;
            }
        }

        protected abstract void onSeekTargetStep(int i, int i2, State state, Action action);

        protected abstract void onStart();

        protected abstract void onStop();

        protected abstract void onTargetFound(View view, State state, Action action);

        public void setTargetPosition(int i) {
            this.mTargetPosition = i;
        }

        void start(RecyclerView recyclerView, LayoutManager layoutManager) {
            this.mRecyclerView = recyclerView;
            this.mLayoutManager = layoutManager;
            if (this.mTargetPosition == RecyclerView.NO_POSITION) {
                throw new IllegalArgumentException("Invalid target position");
            }
            this.mRecyclerView.mState.mTargetPosition = this.mTargetPosition;
            this.mRunning = true;
            this.mPendingInitialRun = true;
            this.mTargetView = findViewByPosition(getTargetPosition());
            onStart();
            this.mRecyclerView.mViewFlinger.postOnAnimation();
        }

        protected final void stop() {
            if (this.mRunning) {
                onStop();
                this.mRecyclerView.mState.mTargetPosition = RecyclerView.NO_POSITION;
                this.mTargetView = null;
                this.mTargetPosition = RecyclerView.NO_POSITION;
                this.mPendingInitialRun = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                this.mRunning = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                this.mLayoutManager.onSmoothScrollerStopped(this);
                this.mLayoutManager = null;
                this.mRecyclerView = null;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.RecyclerView.1 */
    class C08621 implements Runnable {
        C08621() {
        }

        public void run() {
            if (RecyclerView.this.mFirstLayoutComplete && !RecyclerView.this.isLayoutRequested()) {
                if (RecyclerView.this.mLayoutFrozen) {
                    RecyclerView.this.mLayoutRequestEaten = true;
                } else {
                    RecyclerView.this.consumePendingUpdateOperations();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.RecyclerView.2 */
    class C08632 implements Runnable {
        C08632() {
        }

        public void run() {
            if (RecyclerView.this.mItemAnimator != null) {
                RecyclerView.this.mItemAnimator.runPendingAnimations();
            }
            RecyclerView.this.mPostedAnimatorRunner = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.RecyclerView.3 */
    static class C08643 implements Interpolator {
        C08643() {
        }

        public float getInterpolation(float f) {
            float f2 = f - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            return (f2 * (((f2 * f2) * f2) * f2)) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.RecyclerView.4 */
    class C08654 implements ProcessCallback {
        C08654() {
        }

        public void processAppeared(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo, ItemHolderInfo itemHolderInfo2) {
            RecyclerView.this.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2);
        }

        public void processDisappeared(ViewHolder viewHolder, @NonNull ItemHolderInfo itemHolderInfo, @Nullable ItemHolderInfo itemHolderInfo2) {
            RecyclerView.this.mRecycler.unscrapView(viewHolder);
            RecyclerView.this.animateDisappearance(viewHolder, itemHolderInfo, itemHolderInfo2);
        }

        public void processPersistent(ViewHolder viewHolder, @NonNull ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2) {
            viewHolder.setIsRecyclable(RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
            if (RecyclerView.this.mDataSetHasChangedAfterLayout) {
                if (RecyclerView.this.mItemAnimator.animateChange(viewHolder, viewHolder, itemHolderInfo, itemHolderInfo2)) {
                    RecyclerView.this.postAnimationRunner();
                }
            } else if (RecyclerView.this.mItemAnimator.animatePersistence(viewHolder, itemHolderInfo, itemHolderInfo2)) {
                RecyclerView.this.postAnimationRunner();
            }
        }

        public void unused(ViewHolder viewHolder) {
            RecyclerView.this.mLayout.removeAndRecycleView(viewHolder.itemView, RecyclerView.this.mRecycler);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.RecyclerView.5 */
    class C08665 implements Callback {
        C08665() {
        }

        public void addView(View view, int i) {
            RecyclerView.this.addView(view, i);
            RecyclerView.this.dispatchChildAttached(view);
        }

        public void attachViewToParent(View view, int i, android.view.ViewGroup.LayoutParams layoutParams) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt != null) {
                if (childViewHolderInt.isTmpDetached() || childViewHolderInt.shouldIgnore()) {
                    childViewHolderInt.clearTmpDetachFlag();
                } else {
                    throw new IllegalArgumentException("Called attach on a child which is not detached: " + childViewHolderInt);
                }
            }
            RecyclerView.this.attachViewToParent(view, i, layoutParams);
        }

        public void detachViewFromParent(int i) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(childAt);
                if (childViewHolderInt != null) {
                    if (!childViewHolderInt.isTmpDetached() || childViewHolderInt.shouldIgnore()) {
                        childViewHolderInt.addFlags(TLRPC.USER_FLAG_UNUSED2);
                    } else {
                        throw new IllegalArgumentException("called detach on an already detached child " + childViewHolderInt);
                    }
                }
            }
            RecyclerView.this.detachViewFromParent(i);
        }

        public View getChildAt(int i) {
            return RecyclerView.this.getChildAt(i);
        }

        public int getChildCount() {
            return RecyclerView.this.getChildCount();
        }

        public ViewHolder getChildViewHolder(View view) {
            return RecyclerView.getChildViewHolderInt(view);
        }

        public int indexOfChild(View view) {
            return RecyclerView.this.indexOfChild(view);
        }

        public void onEnteredHiddenState(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt != null) {
                childViewHolderInt.onEnteredHiddenState();
            }
        }

        public void onLeftHiddenState(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt != null) {
                childViewHolderInt.onLeftHiddenState();
            }
        }

        public void removeAllViews() {
            int childCount = getChildCount();
            for (int i = RecyclerView.TOUCH_SLOP_DEFAULT; i < childCount; i += RecyclerView.VERTICAL) {
                RecyclerView.this.dispatchChildDetached(getChildAt(i));
            }
            RecyclerView.this.removeAllViews();
        }

        public void removeViewAt(int i) {
            View childAt = RecyclerView.this.getChildAt(i);
            if (childAt != null) {
                RecyclerView.this.dispatchChildDetached(childAt);
            }
            RecyclerView.this.removeViewAt(i);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.support.widget.RecyclerView.6 */
    class C08676 implements Callback {
        C08676() {
        }

        void dispatchUpdate(UpdateOp updateOp) {
            switch (updateOp.cmd) {
                case RecyclerView.VERTICAL /*1*/:
                    RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, updateOp.positionStart, updateOp.itemCount);
                case RecyclerView.SCROLL_STATE_SETTLING /*2*/:
                    RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, updateOp.positionStart, updateOp.itemCount);
                case VideoPlayer.STATE_READY /*4*/:
                    RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, updateOp.positionStart, updateOp.itemCount, updateOp.payload);
                case TLRPC.USER_FLAG_USERNAME /*8*/:
                    RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, updateOp.positionStart, updateOp.itemCount, RecyclerView.VERTICAL);
                default:
            }
        }

        public ViewHolder findViewHolder(int i) {
            ViewHolder findViewHolderForPosition = RecyclerView.this.findViewHolderForPosition(i, true);
            return (findViewHolderForPosition == null || RecyclerView.this.mChildHelper.isHidden(findViewHolderForPosition.itemView)) ? null : findViewHolderForPosition;
        }

        public void markViewHoldersUpdated(int i, int i2, Object obj) {
            RecyclerView.this.viewRangeUpdate(i, i2, obj);
            RecyclerView.this.mItemsChanged = true;
        }

        public void offsetPositionsForAdd(int i, int i2) {
            RecyclerView.this.offsetPositionRecordsForInsert(i, i2);
            RecyclerView.this.mItemsAddedOrRemoved = true;
        }

        public void offsetPositionsForMove(int i, int i2) {
            RecyclerView.this.offsetPositionRecordsForMove(i, i2);
            RecyclerView.this.mItemsAddedOrRemoved = true;
        }

        public void offsetPositionsForRemovingInvisible(int i, int i2) {
            RecyclerView.this.offsetPositionRecordsForRemove(i, i2, true);
            RecyclerView.this.mItemsAddedOrRemoved = true;
            State state = RecyclerView.this.mState;
            state.mDeletedInvisibleItemCountSincePreviousLayout = state.mDeletedInvisibleItemCountSincePreviousLayout + i2;
        }

        public void offsetPositionsForRemovingLaidOutOrNewView(int i, int i2) {
            RecyclerView.this.offsetPositionRecordsForRemove(i, i2, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
            RecyclerView.this.mItemsAddedOrRemoved = true;
        }

        public void onDispatchFirstPass(UpdateOp updateOp) {
            dispatchUpdate(updateOp);
        }

        public void onDispatchSecondPass(UpdateOp updateOp) {
            dispatchUpdate(updateOp);
        }
    }

    public static abstract class Adapter<VH extends ViewHolder> {
        private boolean mHasStableIds;
        private final AdapterDataObservable mObservable;

        public Adapter() {
            this.mObservable = new AdapterDataObservable();
            this.mHasStableIds = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public final void bindViewHolder(VH vh, int i) {
            vh.mPosition = i;
            if (hasStableIds()) {
                vh.mItemId = getItemId(i);
            }
            vh.setFlags(RecyclerView.VERTICAL, 519);
            TraceCompat.beginSection(RecyclerView.TRACE_BIND_VIEW_TAG);
            onBindViewHolder(vh, i, vh.getUnmodifiedPayloads());
            vh.clearPayload();
            TraceCompat.endSection();
        }

        public final VH createViewHolder(ViewGroup viewGroup, int i) {
            TraceCompat.beginSection(RecyclerView.TRACE_CREATE_VIEW_TAG);
            VH onCreateViewHolder = onCreateViewHolder(viewGroup, i);
            onCreateViewHolder.mItemViewType = i;
            TraceCompat.endSection();
            return onCreateViewHolder;
        }

        public abstract int getItemCount();

        public long getItemId(int i) {
            return RecyclerView.NO_ID;
        }

        public int getItemViewType(int i) {
            return RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        public final boolean hasObservers() {
            return this.mObservable.hasObservers();
        }

        public final boolean hasStableIds() {
            return this.mHasStableIds;
        }

        public void notifyDataSetChanged() {
            this.mObservable.notifyChanged();
        }

        public void notifyItemChanged(int i) {
            this.mObservable.notifyItemRangeChanged(i, RecyclerView.VERTICAL);
        }

        public void notifyItemChanged(int i, Object obj) {
            this.mObservable.notifyItemRangeChanged(i, RecyclerView.VERTICAL, obj);
        }

        public void notifyItemInserted(int i) {
            this.mObservable.notifyItemRangeInserted(i, RecyclerView.VERTICAL);
        }

        public void notifyItemMoved(int i, int i2) {
            this.mObservable.notifyItemMoved(i, i2);
        }

        public void notifyItemRangeChanged(int i, int i2) {
            this.mObservable.notifyItemRangeChanged(i, i2);
        }

        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            this.mObservable.notifyItemRangeChanged(i, i2, obj);
        }

        public void notifyItemRangeInserted(int i, int i2) {
            this.mObservable.notifyItemRangeInserted(i, i2);
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            this.mObservable.notifyItemRangeRemoved(i, i2);
        }

        public void notifyItemRemoved(int i) {
            this.mObservable.notifyItemRangeRemoved(i, RecyclerView.VERTICAL);
        }

        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        }

        public abstract void onBindViewHolder(VH vh, int i);

        public void onBindViewHolder(VH vh, int i, List<Object> list) {
            onBindViewHolder(vh, i);
        }

        public abstract VH onCreateViewHolder(ViewGroup viewGroup, int i);

        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        }

        public boolean onFailedToRecycleView(VH vh) {
            return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public void onViewAttachedToWindow(VH vh) {
        }

        public void onViewDetachedFromWindow(VH vh) {
        }

        public void onViewRecycled(VH vh) {
        }

        public void registerAdapterDataObserver(AdapterDataObserver adapterDataObserver) {
            this.mObservable.registerObserver(adapterDataObserver);
        }

        public void setHasStableIds(boolean z) {
            if (hasObservers()) {
                throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
            }
            this.mHasStableIds = z;
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver adapterDataObserver) {
            this.mObservable.unregisterObserver(adapterDataObserver);
        }
    }

    static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        AdapterDataObservable() {
        }

        public boolean hasObservers() {
            return !this.mObservers.isEmpty() ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public void notifyChanged() {
            for (int size = this.mObservers.size() + RecyclerView.NO_POSITION; size >= 0; size += RecyclerView.NO_POSITION) {
                ((AdapterDataObserver) this.mObservers.get(size)).onChanged();
            }
        }

        public void notifyItemMoved(int i, int i2) {
            for (int size = this.mObservers.size() + RecyclerView.NO_POSITION; size >= 0; size += RecyclerView.NO_POSITION) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeMoved(i, i2, RecyclerView.VERTICAL);
            }
        }

        public void notifyItemRangeChanged(int i, int i2) {
            notifyItemRangeChanged(i, i2, null);
        }

        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            for (int size = this.mObservers.size() + RecyclerView.NO_POSITION; size >= 0; size += RecyclerView.NO_POSITION) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeChanged(i, i2, obj);
            }
        }

        public void notifyItemRangeInserted(int i, int i2) {
            for (int size = this.mObservers.size() + RecyclerView.NO_POSITION; size >= 0; size += RecyclerView.NO_POSITION) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeInserted(i, i2);
            }
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            for (int size = this.mObservers.size() + RecyclerView.NO_POSITION; size >= 0; size += RecyclerView.NO_POSITION) {
                ((AdapterDataObserver) this.mObservers.get(size)).onItemRangeRemoved(i, i2);
            }
        }
    }

    public static abstract class AdapterDataObserver {
        public void onChanged() {
        }

        public void onItemRangeChanged(int i, int i2) {
        }

        public void onItemRangeChanged(int i, int i2, Object obj) {
            onItemRangeChanged(i, i2);
        }

        public void onItemRangeInserted(int i, int i2) {
        }

        public void onItemRangeMoved(int i, int i2, int i3) {
        }

        public void onItemRangeRemoved(int i, int i2) {
        }
    }

    public interface ChildDrawingOrderCallback {
        int onGetChildDrawingOrder(int i, int i2);
    }

    private class ItemAnimatorRestoreListener implements ItemAnimatorListener {
        private ItemAnimatorRestoreListener() {
        }

        public void onAnimationFinished(ViewHolder viewHolder) {
            viewHolder.setIsRecyclable(true);
            if (viewHolder.mShadowedHolder != null && viewHolder.mShadowingHolder == null) {
                viewHolder.mShadowedHolder = null;
            }
            viewHolder.mShadowingHolder = null;
            if (!viewHolder.shouldBeKeptAsChild() && !RecyclerView.this.removeAnimatingView(viewHolder.itemView) && viewHolder.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(viewHolder.itemView, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
            }
        }
    }

    public static abstract class ItemDecoration {
        @Deprecated
        public void getItemOffsets(Rect rect, int i, RecyclerView recyclerView) {
            rect.set(RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.TOUCH_SLOP_DEFAULT);
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
            getItemOffsets(rect, ((LayoutParams) view.getLayoutParams()).getViewLayoutPosition(), recyclerView);
        }

        @Deprecated
        public void onDraw(Canvas canvas, RecyclerView recyclerView) {
        }

        public void onDraw(Canvas canvas, RecyclerView recyclerView, State state) {
            onDraw(canvas, recyclerView);
        }

        @Deprecated
        public void onDrawOver(Canvas canvas, RecyclerView recyclerView) {
        }

        public void onDrawOver(Canvas canvas, RecyclerView recyclerView, State state) {
            onDrawOver(canvas, recyclerView);
        }
    }

    public interface OnChildAttachStateChangeListener {
        void onChildViewAttachedToWindow(View view);

        void onChildViewDetachedFromWindow(View view);
    }

    public interface OnItemTouchListener {
        boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent);

        void onRequestDisallowInterceptTouchEvent(boolean z);

        void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent);
    }

    public static abstract class OnScrollListener {
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
        }
    }

    public static class RecycledViewPool {
        private static final int DEFAULT_MAX_SCRAP = 5;
        private int mAttachCount;
        private SparseIntArray mMaxScrap;
        private SparseArray<ArrayList<ViewHolder>> mScrap;

        public RecycledViewPool() {
            this.mScrap = new SparseArray();
            this.mMaxScrap = new SparseIntArray();
            this.mAttachCount = RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        private ArrayList<ViewHolder> getScrapHeapForType(int i) {
            ArrayList<ViewHolder> arrayList = (ArrayList) this.mScrap.get(i);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.mScrap.put(i, arrayList);
                if (this.mMaxScrap.indexOfKey(i) < 0) {
                    this.mMaxScrap.put(i, DEFAULT_MAX_SCRAP);
                }
            }
            return arrayList;
        }

        void attach(Adapter adapter) {
            this.mAttachCount += RecyclerView.VERTICAL;
        }

        public void clear() {
            this.mScrap.clear();
        }

        void detach() {
            this.mAttachCount += RecyclerView.NO_POSITION;
        }

        public ViewHolder getRecycledView(int i) {
            ArrayList arrayList = (ArrayList) this.mScrap.get(i);
            if (arrayList == null || arrayList.isEmpty()) {
                return null;
            }
            int size = arrayList.size() + RecyclerView.NO_POSITION;
            ViewHolder viewHolder = (ViewHolder) arrayList.get(size);
            arrayList.remove(size);
            return viewHolder;
        }

        void onAdapterChanged(Adapter adapter, Adapter adapter2, boolean z) {
            if (adapter != null) {
                detach();
            }
            if (!z && this.mAttachCount == 0) {
                clear();
            }
            if (adapter2 != null) {
                attach(adapter2);
            }
        }

        public void putRecycledView(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            ArrayList scrapHeapForType = getScrapHeapForType(itemViewType);
            if (this.mMaxScrap.get(itemViewType) > scrapHeapForType.size()) {
                viewHolder.resetInternal();
                scrapHeapForType.add(viewHolder);
            }
        }

        public void setMaxRecycledViews(int i, int i2) {
            this.mMaxScrap.put(i, i2);
            ArrayList arrayList = (ArrayList) this.mScrap.get(i);
            if (arrayList != null) {
                while (arrayList.size() > i2) {
                    arrayList.remove(arrayList.size() + RecyclerView.NO_POSITION);
                }
            }
        }

        int size() {
            int i = RecyclerView.TOUCH_SLOP_DEFAULT;
            for (int i2 = RecyclerView.TOUCH_SLOP_DEFAULT; i2 < this.mScrap.size(); i2 += RecyclerView.VERTICAL) {
                ArrayList arrayList = (ArrayList) this.mScrap.valueAt(i2);
                if (arrayList != null) {
                    i += arrayList.size();
                }
            }
            return i;
        }
    }

    public final class Recycler {
        private static final int DEFAULT_CACHE_SIZE = 2;
        final ArrayList<ViewHolder> mAttachedScrap;
        final ArrayList<ViewHolder> mCachedViews;
        private ArrayList<ViewHolder> mChangedScrap;
        private RecycledViewPool mRecyclerPool;
        private final List<ViewHolder> mUnmodifiableAttachedScrap;
        private ViewCacheExtension mViewCacheExtension;
        private int mViewCacheMax;

        public Recycler() {
            this.mAttachedScrap = new ArrayList();
            this.mChangedScrap = null;
            this.mCachedViews = new ArrayList();
            this.mUnmodifiableAttachedScrap = Collections.unmodifiableList(this.mAttachedScrap);
            this.mViewCacheMax = DEFAULT_CACHE_SIZE;
        }

        private void attachAccessibilityDelegate(View view) {
            if (RecyclerView.this.isAccessibilityEnabled()) {
                if (ViewCompat.getImportantForAccessibility(view) == 0) {
                    ViewCompat.setImportantForAccessibility(view, RecyclerView.VERTICAL);
                }
                if (!ViewCompat.hasAccessibilityDelegate(view)) {
                    ViewCompat.setAccessibilityDelegate(view, RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
                }
            }
        }

        private void invalidateDisplayListInt(ViewGroup viewGroup, boolean z) {
            for (int childCount = viewGroup.getChildCount() + RecyclerView.NO_POSITION; childCount >= 0; childCount += RecyclerView.NO_POSITION) {
                View childAt = viewGroup.getChildAt(childCount);
                if (childAt instanceof ViewGroup) {
                    invalidateDisplayListInt((ViewGroup) childAt, true);
                }
            }
            if (!z) {
                return;
            }
            if (viewGroup.getVisibility() == 4) {
                viewGroup.setVisibility(RecyclerView.TOUCH_SLOP_DEFAULT);
                viewGroup.setVisibility(4);
                return;
            }
            int visibility = viewGroup.getVisibility();
            viewGroup.setVisibility(4);
            viewGroup.setVisibility(visibility);
        }

        private void invalidateDisplayListInt(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ViewGroup) {
                invalidateDisplayListInt((ViewGroup) viewHolder.itemView, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
            }
        }

        void addViewHolderToRecycledViewPool(ViewHolder viewHolder) {
            ViewCompat.setAccessibilityDelegate(viewHolder.itemView, null);
            dispatchViewRecycled(viewHolder);
            viewHolder.mOwnerRecyclerView = null;
            getRecycledViewPool().putRecycledView(viewHolder);
        }

        public void bindViewToPosition(View view, int i) {
            boolean z = true;
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt == null) {
                throw new IllegalArgumentException("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter");
            }
            int findPositionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
            if (findPositionOffset < 0 || findPositionOffset >= RecyclerView.this.mAdapter.getItemCount()) {
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + i + "(offset:" + findPositionOffset + ").state:" + RecyclerView.this.mState.getItemCount());
            }
            LayoutParams layoutParams;
            childViewHolderInt.mOwnerRecyclerView = RecyclerView.this;
            RecyclerView.this.mAdapter.bindViewHolder(childViewHolderInt, findPositionOffset);
            attachAccessibilityDelegate(view);
            if (RecyclerView.this.mState.isPreLayout()) {
                childViewHolderInt.mPreLayoutPosition = i;
            }
            android.view.ViewGroup.LayoutParams layoutParams2 = childViewHolderInt.itemView.getLayoutParams();
            if (layoutParams2 == null) {
                layoutParams = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                childViewHolderInt.itemView.setLayoutParams(layoutParams);
            } else if (RecyclerView.this.checkLayoutParams(layoutParams2)) {
                layoutParams = (LayoutParams) layoutParams2;
            } else {
                layoutParams = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams2);
                childViewHolderInt.itemView.setLayoutParams(layoutParams);
            }
            layoutParams.mInsetsDirty = true;
            layoutParams.mViewHolder = childViewHolderInt;
            if (childViewHolderInt.itemView.getParent() != null) {
                z = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            }
            layoutParams.mPendingInvalidate = z;
        }

        public void clear() {
            this.mAttachedScrap.clear();
            recycleAndClearCachedViews();
        }

        void clearOldPositions() {
            int i;
            int i2 = RecyclerView.TOUCH_SLOP_DEFAULT;
            int size = this.mCachedViews.size();
            for (i = RecyclerView.TOUCH_SLOP_DEFAULT; i < size; i += RecyclerView.VERTICAL) {
                ((ViewHolder) this.mCachedViews.get(i)).clearOldPosition();
            }
            size = this.mAttachedScrap.size();
            for (i = RecyclerView.TOUCH_SLOP_DEFAULT; i < size; i += RecyclerView.VERTICAL) {
                ((ViewHolder) this.mAttachedScrap.get(i)).clearOldPosition();
            }
            if (this.mChangedScrap != null) {
                i = this.mChangedScrap.size();
                while (i2 < i) {
                    ((ViewHolder) this.mChangedScrap.get(i2)).clearOldPosition();
                    i2 += RecyclerView.VERTICAL;
                }
            }
        }

        void clearScrap() {
            this.mAttachedScrap.clear();
            if (this.mChangedScrap != null) {
                this.mChangedScrap.clear();
            }
        }

        public int convertPreLayoutPositionToPostLayout(int i) {
            if (i >= 0 && i < RecyclerView.this.mState.getItemCount()) {
                return !RecyclerView.this.mState.isPreLayout() ? i : RecyclerView.this.mAdapterHelper.findPositionOffset(i);
            } else {
                throw new IndexOutOfBoundsException("invalid position " + i + ". State item count is " + RecyclerView.this.mState.getItemCount());
            }
        }

        void dispatchViewRecycled(ViewHolder viewHolder) {
            if (RecyclerView.this.mRecyclerListener != null) {
                RecyclerView.this.mRecyclerListener.onViewRecycled(viewHolder);
            }
            if (RecyclerView.this.mAdapter != null) {
                RecyclerView.this.mAdapter.onViewRecycled(viewHolder);
            }
            if (RecyclerView.this.mState != null) {
                RecyclerView.this.mViewInfoStore.removeViewHolder(viewHolder);
            }
        }

        ViewHolder getChangedScrapViewForPosition(int i) {
            int i2 = RecyclerView.TOUCH_SLOP_DEFAULT;
            if (this.mChangedScrap != null) {
                int size = this.mChangedScrap.size();
                if (size != 0) {
                    ViewHolder viewHolder;
                    int i3 = RecyclerView.TOUCH_SLOP_DEFAULT;
                    while (i3 < size) {
                        viewHolder = (ViewHolder) this.mChangedScrap.get(i3);
                        if (viewHolder.wasReturnedFromScrap() || viewHolder.getLayoutPosition() != i) {
                            i3 += RecyclerView.VERTICAL;
                        } else {
                            viewHolder.addFlags(32);
                            return viewHolder;
                        }
                    }
                    if (RecyclerView.this.mAdapter.hasStableIds()) {
                        int findPositionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
                        if (findPositionOffset > 0 && findPositionOffset < RecyclerView.this.mAdapter.getItemCount()) {
                            long itemId = RecyclerView.this.mAdapter.getItemId(findPositionOffset);
                            while (i2 < size) {
                                viewHolder = (ViewHolder) this.mChangedScrap.get(i2);
                                if (viewHolder.wasReturnedFromScrap() || viewHolder.getItemId() != itemId) {
                                    i2 += RecyclerView.VERTICAL;
                                } else {
                                    viewHolder.addFlags(32);
                                    return viewHolder;
                                }
                            }
                        }
                    }
                    return null;
                }
            }
            return null;
        }

        RecycledViewPool getRecycledViewPool() {
            if (this.mRecyclerPool == null) {
                this.mRecyclerPool = new RecycledViewPool();
            }
            return this.mRecyclerPool;
        }

        int getScrapCount() {
            return this.mAttachedScrap.size();
        }

        public List<ViewHolder> getScrapList() {
            return this.mUnmodifiableAttachedScrap;
        }

        View getScrapViewAt(int i) {
            return ((ViewHolder) this.mAttachedScrap.get(i)).itemView;
        }

        ViewHolder getScrapViewForId(long j, int i, boolean z) {
            int size;
            for (size = this.mAttachedScrap.size() + RecyclerView.NO_POSITION; size >= 0; size += RecyclerView.NO_POSITION) {
                ViewHolder viewHolder = (ViewHolder) this.mAttachedScrap.get(size);
                if (viewHolder.getItemId() == j && !viewHolder.wasReturnedFromScrap()) {
                    if (i == viewHolder.getItemViewType()) {
                        viewHolder.addFlags(32);
                        if (!viewHolder.isRemoved() || RecyclerView.this.mState.isPreLayout()) {
                            return viewHolder;
                        }
                        viewHolder.setFlags(DEFAULT_CACHE_SIZE, 14);
                        return viewHolder;
                    } else if (!z) {
                        this.mAttachedScrap.remove(size);
                        RecyclerView.this.removeDetachedView(viewHolder.itemView, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
                        quickRecycleScrapView(viewHolder.itemView);
                    }
                }
            }
            for (size = this.mCachedViews.size() + RecyclerView.NO_POSITION; size >= 0; size += RecyclerView.NO_POSITION) {
                viewHolder = (ViewHolder) this.mCachedViews.get(size);
                if (viewHolder.getItemId() == j) {
                    if (i == viewHolder.getItemViewType()) {
                        if (z) {
                            return viewHolder;
                        }
                        this.mCachedViews.remove(size);
                        return viewHolder;
                    } else if (!z) {
                        recycleCachedViewAt(size);
                    }
                }
            }
            return null;
        }

        ViewHolder getScrapViewForPosition(int i, int i2, boolean z) {
            int i3 = RecyclerView.TOUCH_SLOP_DEFAULT;
            int size = this.mAttachedScrap.size();
            int i4 = RecyclerView.TOUCH_SLOP_DEFAULT;
            while (i4 < size) {
                View findHiddenNonRemovedView;
                ViewHolder viewHolder = (ViewHolder) this.mAttachedScrap.get(i4);
                if (viewHolder.wasReturnedFromScrap() || viewHolder.getLayoutPosition() != i || viewHolder.isInvalid() || (!RecyclerView.this.mState.mInPreLayout && viewHolder.isRemoved())) {
                    i4 += RecyclerView.VERTICAL;
                } else if (i2 == RecyclerView.NO_POSITION || viewHolder.getItemViewType() == i2) {
                    viewHolder.addFlags(32);
                    return viewHolder;
                } else {
                    Log.e(RecyclerView.TAG, "Scrap view for position " + i + " isn't dirty but has wrong view type! (found " + viewHolder.getItemViewType() + " but expected " + i2 + ")");
                    if (!z) {
                        findHiddenNonRemovedView = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(i, i2);
                        if (findHiddenNonRemovedView != null) {
                            viewHolder = RecyclerView.getChildViewHolderInt(findHiddenNonRemovedView);
                            RecyclerView.this.mChildHelper.unhide(findHiddenNonRemovedView);
                            i3 = RecyclerView.this.mChildHelper.indexOfChild(findHiddenNonRemovedView);
                            if (i3 != RecyclerView.NO_POSITION) {
                                throw new IllegalStateException("layout index should not be -1 after unhiding a view:" + viewHolder);
                            }
                            RecyclerView.this.mChildHelper.detachViewFromParent(i3);
                            scrapView(findHiddenNonRemovedView);
                            viewHolder.addFlags(8224);
                            return viewHolder;
                        }
                    }
                    i4 = this.mCachedViews.size();
                    while (i3 < i4) {
                        viewHolder = (ViewHolder) this.mCachedViews.get(i3);
                        if (viewHolder.isInvalid() || viewHolder.getLayoutPosition() != i) {
                            i3 += RecyclerView.VERTICAL;
                        } else if (z) {
                            return viewHolder;
                        } else {
                            this.mCachedViews.remove(i3);
                            return viewHolder;
                        }
                    }
                    return null;
                }
            }
            if (z) {
                findHiddenNonRemovedView = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(i, i2);
                if (findHiddenNonRemovedView != null) {
                    viewHolder = RecyclerView.getChildViewHolderInt(findHiddenNonRemovedView);
                    RecyclerView.this.mChildHelper.unhide(findHiddenNonRemovedView);
                    i3 = RecyclerView.this.mChildHelper.indexOfChild(findHiddenNonRemovedView);
                    if (i3 != RecyclerView.NO_POSITION) {
                        RecyclerView.this.mChildHelper.detachViewFromParent(i3);
                        scrapView(findHiddenNonRemovedView);
                        viewHolder.addFlags(8224);
                        return viewHolder;
                    }
                    throw new IllegalStateException("layout index should not be -1 after unhiding a view:" + viewHolder);
                }
            }
            i4 = this.mCachedViews.size();
            while (i3 < i4) {
                viewHolder = (ViewHolder) this.mCachedViews.get(i3);
                if (viewHolder.isInvalid()) {
                }
                i3 += RecyclerView.VERTICAL;
            }
            return null;
        }

        public View getViewForPosition(int i) {
            return getViewForPosition(i, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
        }

        View getViewForPosition(int i, boolean z) {
            boolean z2 = true;
            if (i < 0 || i >= RecyclerView.this.mState.getItemCount()) {
                throw new IndexOutOfBoundsException("Invalid item position " + i + "(" + i + "). Item count:" + RecyclerView.this.mState.getItemCount());
            }
            ViewHolder changedScrapViewForPosition;
            boolean z3;
            ViewHolder viewHolder;
            boolean z4;
            int findPositionOffset;
            boolean z5;
            int findPositionOffset2;
            android.view.ViewGroup.LayoutParams layoutParams;
            LayoutParams layoutParams2;
            if (RecyclerView.this.mState.isPreLayout()) {
                changedScrapViewForPosition = getChangedScrapViewForPosition(i);
                ViewHolder viewHolder2 = changedScrapViewForPosition;
                z3 = changedScrapViewForPosition != null ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                viewHolder = viewHolder2;
            } else {
                viewHolder = null;
                z3 = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            }
            if (viewHolder == null) {
                viewHolder = getScrapViewForPosition(i, RecyclerView.NO_POSITION, z);
                if (viewHolder != null) {
                    if (validateViewHolderForOffsetPosition(viewHolder)) {
                        z4 = true;
                    } else {
                        if (!z) {
                            viewHolder.addFlags(4);
                            if (viewHolder.isScrap()) {
                                RecyclerView.this.removeDetachedView(viewHolder.itemView, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
                                viewHolder.unScrap();
                            } else if (viewHolder.wasReturnedFromScrap()) {
                                viewHolder.clearReturnedFromScrapFlag();
                            }
                            recycleViewHolderInternal(viewHolder);
                        }
                        viewHolder = null;
                        z4 = z3;
                    }
                    if (viewHolder == null) {
                        findPositionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
                        if (findPositionOffset >= 0 || findPositionOffset >= RecyclerView.this.mAdapter.getItemCount()) {
                            throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + i + "(offset:" + findPositionOffset + ").state:" + RecyclerView.this.mState.getItemCount() + " tag " + RecyclerView.this.getTag() + " adapter " + RecyclerView.this.getAdapter());
                        }
                        int itemViewType = RecyclerView.this.mAdapter.getItemViewType(findPositionOffset);
                        if (RecyclerView.this.mAdapter.hasStableIds()) {
                            viewHolder = getScrapViewForId(RecyclerView.this.mAdapter.getItemId(findPositionOffset), itemViewType, z);
                            if (viewHolder != null) {
                                viewHolder.mPosition = findPositionOffset;
                                z4 = true;
                            }
                        }
                        if (viewHolder == null && this.mViewCacheExtension != null) {
                            View viewForPositionAndType = this.mViewCacheExtension.getViewForPositionAndType(this, i, itemViewType);
                            if (viewForPositionAndType != null) {
                                viewHolder = RecyclerView.this.getChildViewHolder(viewForPositionAndType);
                                if (viewHolder == null) {
                                    throw new IllegalArgumentException("getViewForPositionAndType returned a view which does not have a ViewHolder");
                                } else if (viewHolder.shouldIgnore()) {
                                    throw new IllegalArgumentException("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view.");
                                }
                            }
                        }
                        if (viewHolder == null) {
                            viewHolder = getRecycledViewPool().getRecycledView(itemViewType);
                            if (viewHolder != null) {
                                viewHolder.resetInternal();
                                if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST) {
                                    invalidateDisplayListInt(viewHolder);
                                }
                            }
                        }
                        if (viewHolder == null) {
                            changedScrapViewForPosition = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, itemViewType);
                            z5 = z4;
                            if (z5 && !RecyclerView.this.mState.isPreLayout() && changedScrapViewForPosition.hasAnyOfTheFlags(MessagesController.UPDATE_MASK_CHANNEL)) {
                                changedScrapViewForPosition.setFlags(RecyclerView.TOUCH_SLOP_DEFAULT, MessagesController.UPDATE_MASK_CHANNEL);
                                if (RecyclerView.this.mState.mRunSimpleAnimations) {
                                    RecyclerView.this.recordAnimationInfoIfBouncedHiddenView(changedScrapViewForPosition, RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, changedScrapViewForPosition, ItemAnimator.buildAdapterChangeFlagsForAnimations(changedScrapViewForPosition) | ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, changedScrapViewForPosition.getUnmodifiedPayloads()));
                                }
                            }
                            if (!RecyclerView.this.mState.isPreLayout() && changedScrapViewForPosition.isBound()) {
                                changedScrapViewForPosition.mPreLayoutPosition = i;
                                z4 = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                            } else if (changedScrapViewForPosition.isBound() || changedScrapViewForPosition.needsUpdate() || changedScrapViewForPosition.isInvalid()) {
                                findPositionOffset2 = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
                                changedScrapViewForPosition.mOwnerRecyclerView = RecyclerView.this;
                                RecyclerView.this.mAdapter.bindViewHolder(changedScrapViewForPosition, findPositionOffset2);
                                attachAccessibilityDelegate(changedScrapViewForPosition.itemView);
                                if (RecyclerView.this.mState.isPreLayout()) {
                                    changedScrapViewForPosition.mPreLayoutPosition = i;
                                }
                                z4 = true;
                            } else {
                                z4 = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                            }
                            layoutParams = changedScrapViewForPosition.itemView.getLayoutParams();
                            if (layoutParams != null) {
                                layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                                changedScrapViewForPosition.itemView.setLayoutParams(layoutParams2);
                            } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                                layoutParams2 = (LayoutParams) layoutParams;
                            } else {
                                layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                                changedScrapViewForPosition.itemView.setLayoutParams(layoutParams2);
                            }
                            layoutParams2.mViewHolder = changedScrapViewForPosition;
                            if (!(z5 && r3)) {
                                z2 = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                            }
                            layoutParams2.mPendingInvalidate = z2;
                            return changedScrapViewForPosition.itemView;
                        }
                    }
                    changedScrapViewForPosition = viewHolder;
                    z5 = z4;
                    changedScrapViewForPosition.setFlags(RecyclerView.TOUCH_SLOP_DEFAULT, MessagesController.UPDATE_MASK_CHANNEL);
                    if (RecyclerView.this.mState.mRunSimpleAnimations) {
                        RecyclerView.this.recordAnimationInfoIfBouncedHiddenView(changedScrapViewForPosition, RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, changedScrapViewForPosition, ItemAnimator.buildAdapterChangeFlagsForAnimations(changedScrapViewForPosition) | ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, changedScrapViewForPosition.getUnmodifiedPayloads()));
                    }
                    if (!RecyclerView.this.mState.isPreLayout()) {
                    }
                    if (changedScrapViewForPosition.isBound()) {
                    }
                    findPositionOffset2 = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
                    changedScrapViewForPosition.mOwnerRecyclerView = RecyclerView.this;
                    RecyclerView.this.mAdapter.bindViewHolder(changedScrapViewForPosition, findPositionOffset2);
                    attachAccessibilityDelegate(changedScrapViewForPosition.itemView);
                    if (RecyclerView.this.mState.isPreLayout()) {
                        changedScrapViewForPosition.mPreLayoutPosition = i;
                    }
                    z4 = true;
                    layoutParams = changedScrapViewForPosition.itemView.getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                        changedScrapViewForPosition.itemView.setLayoutParams(layoutParams2);
                    } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                        layoutParams2 = (LayoutParams) layoutParams;
                    } else {
                        layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                        changedScrapViewForPosition.itemView.setLayoutParams(layoutParams2);
                    }
                    layoutParams2.mViewHolder = changedScrapViewForPosition;
                    z2 = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                    layoutParams2.mPendingInvalidate = z2;
                    return changedScrapViewForPosition.itemView;
                }
            }
            z4 = z3;
            if (viewHolder == null) {
                findPositionOffset = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
                if (findPositionOffset >= 0) {
                }
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + i + "(offset:" + findPositionOffset + ").state:" + RecyclerView.this.mState.getItemCount() + " tag " + RecyclerView.this.getTag() + " adapter " + RecyclerView.this.getAdapter());
            }
            changedScrapViewForPosition = viewHolder;
            z5 = z4;
            changedScrapViewForPosition.setFlags(RecyclerView.TOUCH_SLOP_DEFAULT, MessagesController.UPDATE_MASK_CHANNEL);
            if (RecyclerView.this.mState.mRunSimpleAnimations) {
                RecyclerView.this.recordAnimationInfoIfBouncedHiddenView(changedScrapViewForPosition, RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, changedScrapViewForPosition, ItemAnimator.buildAdapterChangeFlagsForAnimations(changedScrapViewForPosition) | ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, changedScrapViewForPosition.getUnmodifiedPayloads()));
            }
            if (!RecyclerView.this.mState.isPreLayout()) {
            }
            if (changedScrapViewForPosition.isBound()) {
            }
            findPositionOffset2 = RecyclerView.this.mAdapterHelper.findPositionOffset(i);
            changedScrapViewForPosition.mOwnerRecyclerView = RecyclerView.this;
            RecyclerView.this.mAdapter.bindViewHolder(changedScrapViewForPosition, findPositionOffset2);
            attachAccessibilityDelegate(changedScrapViewForPosition.itemView);
            if (RecyclerView.this.mState.isPreLayout()) {
                changedScrapViewForPosition.mPreLayoutPosition = i;
            }
            z4 = true;
            layoutParams = changedScrapViewForPosition.itemView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams2 = (LayoutParams) RecyclerView.this.generateDefaultLayoutParams();
                changedScrapViewForPosition.itemView.setLayoutParams(layoutParams2);
            } else if (RecyclerView.this.checkLayoutParams(layoutParams)) {
                layoutParams2 = (LayoutParams) RecyclerView.this.generateLayoutParams(layoutParams);
                changedScrapViewForPosition.itemView.setLayoutParams(layoutParams2);
            } else {
                layoutParams2 = (LayoutParams) layoutParams;
            }
            layoutParams2.mViewHolder = changedScrapViewForPosition;
            z2 = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            layoutParams2.mPendingInvalidate = z2;
            return changedScrapViewForPosition.itemView;
        }

        void markItemDecorInsetsDirty() {
            int size = this.mCachedViews.size();
            for (int i = RecyclerView.TOUCH_SLOP_DEFAULT; i < size; i += RecyclerView.VERTICAL) {
                LayoutParams layoutParams = (LayoutParams) ((ViewHolder) this.mCachedViews.get(i)).itemView.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.mInsetsDirty = true;
                }
            }
        }

        void markKnownViewsInvalid() {
            if (RecyclerView.this.mAdapter == null || !RecyclerView.this.mAdapter.hasStableIds()) {
                recycleAndClearCachedViews();
                return;
            }
            int size = this.mCachedViews.size();
            for (int i = RecyclerView.TOUCH_SLOP_DEFAULT; i < size; i += RecyclerView.VERTICAL) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(i);
                if (viewHolder != null) {
                    viewHolder.addFlags(6);
                    viewHolder.addChangePayload(null);
                }
            }
        }

        void offsetPositionRecordsForInsert(int i, int i2) {
            int size = this.mCachedViews.size();
            for (int i3 = RecyclerView.TOUCH_SLOP_DEFAULT; i3 < size; i3 += RecyclerView.VERTICAL) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(i3);
                if (viewHolder != null && viewHolder.mPosition >= i) {
                    viewHolder.offsetPosition(i2, true);
                }
            }
        }

        void offsetPositionRecordsForMove(int i, int i2) {
            int i3;
            int i4;
            int i5;
            if (i < i2) {
                i3 = RecyclerView.NO_POSITION;
                i4 = i2;
                i5 = i;
            } else {
                i3 = RecyclerView.VERTICAL;
                i4 = i;
                i5 = i2;
            }
            int size = this.mCachedViews.size();
            for (int i6 = RecyclerView.TOUCH_SLOP_DEFAULT; i6 < size; i6 += RecyclerView.VERTICAL) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(i6);
                if (viewHolder != null && viewHolder.mPosition >= r3 && viewHolder.mPosition <= r2) {
                    if (viewHolder.mPosition == i) {
                        viewHolder.offsetPosition(i2 - i, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
                    } else {
                        viewHolder.offsetPosition(i3, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
                    }
                }
            }
        }

        void offsetPositionRecordsForRemove(int i, int i2, boolean z) {
            int i3 = i + i2;
            for (int size = this.mCachedViews.size() + RecyclerView.NO_POSITION; size >= 0; size += RecyclerView.NO_POSITION) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(size);
                if (viewHolder != null) {
                    if (viewHolder.mPosition >= i3) {
                        viewHolder.offsetPosition(-i2, z);
                    } else if (viewHolder.mPosition >= i) {
                        viewHolder.addFlags(8);
                        recycleCachedViewAt(size);
                    }
                }
            }
        }

        void onAdapterChanged(Adapter adapter, Adapter adapter2, boolean z) {
            clear();
            getRecycledViewPool().onAdapterChanged(adapter, adapter2, z);
        }

        void quickRecycleScrapView(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            childViewHolderInt.mScrapContainer = null;
            childViewHolderInt.mInChangeScrap = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            childViewHolderInt.clearReturnedFromScrapFlag();
            recycleViewHolderInternal(childViewHolderInt);
        }

        void recycleAndClearCachedViews() {
            for (int size = this.mCachedViews.size() + RecyclerView.NO_POSITION; size >= 0; size += RecyclerView.NO_POSITION) {
                recycleCachedViewAt(size);
            }
            this.mCachedViews.clear();
        }

        void recycleCachedViewAt(int i) {
            addViewHolderToRecycledViewPool((ViewHolder) this.mCachedViews.get(i));
            this.mCachedViews.remove(i);
        }

        public void recycleView(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (childViewHolderInt.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(view, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
            }
            if (childViewHolderInt.isScrap()) {
                childViewHolderInt.unScrap();
            } else if (childViewHolderInt.wasReturnedFromScrap()) {
                childViewHolderInt.clearReturnedFromScrapFlag();
            }
            recycleViewHolderInternal(childViewHolderInt);
        }

        void recycleViewHolderInternal(ViewHolder viewHolder) {
            boolean z = true;
            int i = RecyclerView.TOUCH_SLOP_DEFAULT;
            if (viewHolder.isScrap() || viewHolder.itemView.getParent() != null) {
                StringBuilder append = new StringBuilder().append("Scrapped or attached views may not be recycled. isScrap:").append(viewHolder.isScrap()).append(" isAttached:");
                if (viewHolder.itemView.getParent() == null) {
                    z = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
                }
                throw new IllegalArgumentException(append.append(z).toString());
            } else if (viewHolder.isTmpDetached()) {
                throw new IllegalArgumentException("Tmp detached view should be removed from RecyclerView before it can be recycled: " + viewHolder);
            } else if (viewHolder.shouldIgnore()) {
                throw new IllegalArgumentException("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle.");
            } else {
                int i2;
                boolean access$4900 = viewHolder.doesTransientStatePreventRecycling();
                boolean z2 = (RecyclerView.this.mAdapter != null && access$4900 && RecyclerView.this.mAdapter.onFailedToRecycleView(viewHolder)) ? true : RecyclerView.TOUCH_SLOP_DEFAULT;
                if (z2 || viewHolder.isRecyclable()) {
                    if (!viewHolder.hasAnyOfTheFlags(14)) {
                        int size = this.mCachedViews.size();
                        if (size == this.mViewCacheMax && size > 0) {
                            recycleCachedViewAt(RecyclerView.TOUCH_SLOP_DEFAULT);
                        }
                        if (size < this.mViewCacheMax) {
                            this.mCachedViews.add(viewHolder);
                            z2 = true;
                            if (z2) {
                                addViewHolderToRecycledViewPool(viewHolder);
                                i = RecyclerView.VERTICAL;
                                i2 = z2;
                            } else {
                                z = z2;
                            }
                        }
                    }
                    z2 = RecyclerView.TOUCH_SLOP_DEFAULT;
                    if (z2) {
                        z = z2;
                    } else {
                        addViewHolderToRecycledViewPool(viewHolder);
                        i = RecyclerView.VERTICAL;
                        i2 = z2;
                    }
                } else {
                    i2 = RecyclerView.TOUCH_SLOP_DEFAULT;
                }
                RecyclerView.this.mViewInfoStore.removeViewHolder(viewHolder);
                if (i2 == 0 && r1 == 0 && access$4900) {
                    viewHolder.mOwnerRecyclerView = null;
                }
            }
        }

        void recycleViewInternal(View view) {
            recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(view));
        }

        void scrapView(View view) {
            ViewHolder childViewHolderInt = RecyclerView.getChildViewHolderInt(view);
            if (!childViewHolderInt.hasAnyOfTheFlags(12) && childViewHolderInt.isUpdated() && !RecyclerView.this.canReuseUpdatedViewHolder(childViewHolderInt)) {
                if (this.mChangedScrap == null) {
                    this.mChangedScrap = new ArrayList();
                }
                childViewHolderInt.setScrapContainer(this, true);
                this.mChangedScrap.add(childViewHolderInt);
            } else if (!childViewHolderInt.isInvalid() || childViewHolderInt.isRemoved() || RecyclerView.this.mAdapter.hasStableIds()) {
                childViewHolderInt.setScrapContainer(this, RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
                this.mAttachedScrap.add(childViewHolderInt);
            } else {
                throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool.");
            }
        }

        void setAdapterPositionsAsUnknown() {
            int size = this.mCachedViews.size();
            for (int i = RecyclerView.TOUCH_SLOP_DEFAULT; i < size; i += RecyclerView.VERTICAL) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(i);
                if (viewHolder != null) {
                    viewHolder.addFlags(TLRPC.USER_FLAG_UNUSED3);
                }
            }
        }

        void setRecycledViewPool(RecycledViewPool recycledViewPool) {
            if (this.mRecyclerPool != null) {
                this.mRecyclerPool.detach();
            }
            this.mRecyclerPool = recycledViewPool;
            if (recycledViewPool != null) {
                this.mRecyclerPool.attach(RecyclerView.this.getAdapter());
            }
        }

        void setViewCacheExtension(ViewCacheExtension viewCacheExtension) {
            this.mViewCacheExtension = viewCacheExtension;
        }

        public void setViewCacheSize(int i) {
            this.mViewCacheMax = i;
            for (int size = this.mCachedViews.size() + RecyclerView.NO_POSITION; size >= 0 && this.mCachedViews.size() > i; size += RecyclerView.NO_POSITION) {
                recycleCachedViewAt(size);
            }
        }

        void unscrapView(ViewHolder viewHolder) {
            if (viewHolder.mInChangeScrap) {
                this.mChangedScrap.remove(viewHolder);
            } else {
                this.mAttachedScrap.remove(viewHolder);
            }
            viewHolder.mScrapContainer = null;
            viewHolder.mInChangeScrap = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            viewHolder.clearReturnedFromScrapFlag();
        }

        boolean validateViewHolderForOffsetPosition(ViewHolder viewHolder) {
            if (viewHolder.isRemoved()) {
                return RecyclerView.this.mState.isPreLayout();
            }
            if (viewHolder.mPosition >= 0 && viewHolder.mPosition < RecyclerView.this.mAdapter.getItemCount()) {
                return (RecyclerView.this.mState.isPreLayout() || RecyclerView.this.mAdapter.getItemViewType(viewHolder.mPosition) == viewHolder.getItemViewType()) ? (!RecyclerView.this.mAdapter.hasStableIds() || viewHolder.getItemId() == RecyclerView.this.mAdapter.getItemId(viewHolder.mPosition)) ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            } else {
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + viewHolder);
            }
        }

        void viewRangeUpdate(int i, int i2) {
            int i3 = i + i2;
            for (int size = this.mCachedViews.size() + RecyclerView.NO_POSITION; size >= 0; size += RecyclerView.NO_POSITION) {
                ViewHolder viewHolder = (ViewHolder) this.mCachedViews.get(size);
                if (viewHolder != null) {
                    int layoutPosition = viewHolder.getLayoutPosition();
                    if (layoutPosition >= i && layoutPosition < i3) {
                        viewHolder.addFlags(DEFAULT_CACHE_SIZE);
                        recycleCachedViewAt(size);
                    }
                }
            }
        }
    }

    public interface RecyclerListener {
        void onViewRecycled(ViewHolder viewHolder);
    }

    private class RecyclerViewDataObserver extends AdapterDataObserver {
        private RecyclerViewDataObserver() {
        }

        public void onChanged() {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapter.hasStableIds()) {
                RecyclerView.this.mState.mStructureChanged = true;
                RecyclerView.this.setDataSetChangedAfterLayout();
            } else {
                RecyclerView.this.mState.mStructureChanged = true;
                RecyclerView.this.setDataSetChangedAfterLayout();
            }
            if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
                RecyclerView.this.requestLayout();
            }
        }

        public void onItemRangeChanged(int i, int i2, Object obj) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(i, i2, obj)) {
                triggerUpdateProcessor();
            }
        }

        public void onItemRangeInserted(int i, int i2) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(i, i2)) {
                triggerUpdateProcessor();
            }
        }

        public void onItemRangeMoved(int i, int i2, int i3) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(i, i2, i3)) {
                triggerUpdateProcessor();
            }
        }

        public void onItemRangeRemoved(int i, int i2) {
            RecyclerView.this.assertNotInLayoutOrScroll(null);
            if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(i, i2)) {
                triggerUpdateProcessor();
            }
        }

        void triggerUpdateProcessor() {
            if (RecyclerView.this.mPostUpdatesOnAnimation && RecyclerView.this.mHasFixedSize && RecyclerView.this.mIsAttached) {
                ViewCompat.postOnAnimation(RecyclerView.this, RecyclerView.this.mUpdateChildViewsRunnable);
                return;
            }
            RecyclerView.this.mAdapterUpdateDuringMeasure = true;
            RecyclerView.this.requestLayout();
        }
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        Parcelable mLayoutState;

        /* renamed from: com.hanista.mobogram.messenger.support.widget.RecyclerView.SavedState.1 */
        static class C08681 implements Creator<SavedState> {
            C08681() {
            }

            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        }

        static {
            CREATOR = new C08681();
        }

        SavedState(Parcel parcel) {
            super(parcel);
            this.mLayoutState = parcel.readParcelable(LayoutManager.class.getClassLoader());
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private void copyFrom(SavedState savedState) {
            this.mLayoutState = savedState.mLayoutState;
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeParcelable(this.mLayoutState, RecyclerView.TOUCH_SLOP_DEFAULT);
        }
    }

    public static class SimpleOnItemTouchListener implements OnItemTouchListener {
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            return RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
        }

        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        }
    }

    public static class State {
        static final int STEP_ANIMATIONS = 4;
        static final int STEP_LAYOUT = 2;
        static final int STEP_START = 1;
        private SparseArray<Object> mData;
        private int mDeletedInvisibleItemCountSincePreviousLayout;
        private boolean mInPreLayout;
        private boolean mIsMeasuring;
        int mItemCount;
        private int mLayoutStep;
        private int mPreviousLayoutItemCount;
        private boolean mRunPredictiveAnimations;
        private boolean mRunSimpleAnimations;
        private boolean mStructureChanged;
        private int mTargetPosition;
        private boolean mTrackOldChangeHolders;

        @Retention(RetentionPolicy.SOURCE)
        @interface LayoutState {
        }

        public State() {
            this.mTargetPosition = RecyclerView.NO_POSITION;
            this.mLayoutStep = STEP_START;
            this.mItemCount = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mPreviousLayoutItemCount = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mDeletedInvisibleItemCountSincePreviousLayout = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mStructureChanged = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mInPreLayout = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mRunSimpleAnimations = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mRunPredictiveAnimations = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mTrackOldChangeHolders = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mIsMeasuring = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        void assertLayoutStep(int i) {
            if ((this.mLayoutStep & i) == 0) {
                throw new IllegalStateException("Layout state should be one of " + Integer.toBinaryString(i) + " but it is " + Integer.toBinaryString(this.mLayoutStep));
            }
        }

        public boolean didStructureChange() {
            return this.mStructureChanged;
        }

        public <T> T get(int i) {
            return this.mData == null ? null : this.mData.get(i);
        }

        public int getItemCount() {
            return this.mInPreLayout ? this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout : this.mItemCount;
        }

        public int getTargetScrollPosition() {
            return this.mTargetPosition;
        }

        public boolean hasTargetScrollPosition() {
            return this.mTargetPosition != RecyclerView.NO_POSITION ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public boolean isMeasuring() {
            return this.mIsMeasuring;
        }

        public boolean isPreLayout() {
            return this.mInPreLayout;
        }

        public void put(int i, Object obj) {
            if (this.mData == null) {
                this.mData = new SparseArray();
            }
            this.mData.put(i, obj);
        }

        public void remove(int i) {
            if (this.mData != null) {
                this.mData.remove(i);
            }
        }

        State reset() {
            this.mTargetPosition = RecyclerView.NO_POSITION;
            if (this.mData != null) {
                this.mData.clear();
            }
            this.mItemCount = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mStructureChanged = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mIsMeasuring = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            return this;
        }

        public String toString() {
            return "State{mTargetPosition=" + this.mTargetPosition + ", mData=" + this.mData + ", mItemCount=" + this.mItemCount + ", mPreviousLayoutItemCount=" + this.mPreviousLayoutItemCount + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.mDeletedInvisibleItemCountSincePreviousLayout + ", mStructureChanged=" + this.mStructureChanged + ", mInPreLayout=" + this.mInPreLayout + ", mRunSimpleAnimations=" + this.mRunSimpleAnimations + ", mRunPredictiveAnimations=" + this.mRunPredictiveAnimations + '}';
        }

        public boolean willRunPredictiveAnimations() {
            return this.mRunPredictiveAnimations;
        }

        public boolean willRunSimpleAnimations() {
            return this.mRunSimpleAnimations;
        }
    }

    public static abstract class ViewCacheExtension {
        public abstract View getViewForPositionAndType(Recycler recycler, int i, int i2);
    }

    private class ViewFlinger implements Runnable {
        private boolean mEatRunOnAnimationRequest;
        private Interpolator mInterpolator;
        private int mLastFlingX;
        private int mLastFlingY;
        private boolean mReSchedulePostAnimationCallback;
        private ScrollerCompat mScroller;

        public ViewFlinger() {
            this.mInterpolator = RecyclerView.sQuinticInterpolator;
            this.mEatRunOnAnimationRequest = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mReSchedulePostAnimationCallback = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mScroller = ScrollerCompat.create(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
        }

        private int computeScrollDuration(int i, int i2, int i3, int i4) {
            int round;
            int abs = Math.abs(i);
            int abs2 = Math.abs(i2);
            Object obj = abs > abs2 ? RecyclerView.VERTICAL : null;
            int sqrt = (int) Math.sqrt((double) ((i3 * i3) + (i4 * i4)));
            int sqrt2 = (int) Math.sqrt((double) ((i * i) + (i2 * i2)));
            int width = obj != null ? RecyclerView.this.getWidth() : RecyclerView.this.getHeight();
            int i5 = width / RecyclerView.SCROLL_STATE_SETTLING;
            float distanceInfluenceForSnapDuration = (distanceInfluenceForSnapDuration(Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, (((float) sqrt2) * DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) / ((float) width))) * ((float) i5)) + ((float) i5);
            if (sqrt > 0) {
                round = Math.round(1000.0f * Math.abs(distanceInfluenceForSnapDuration / ((float) sqrt))) * 4;
            } else {
                round = (int) (((((float) (obj != null ? abs : abs2)) / ((float) width)) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) * BitmapDescriptorFactory.HUE_MAGENTA);
            }
            return Math.min(round, RecyclerView.MAX_SCROLL_DURATION);
        }

        private void disableRunOnAnimationRequests() {
            this.mReSchedulePostAnimationCallback = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mEatRunOnAnimationRequest = true;
        }

        private float distanceInfluenceForSnapDuration(float f) {
            return (float) Math.sin((double) ((float) (((double) (f - 0.5f)) * 0.4712389167638204d)));
        }

        private void enableRunOnAnimationRequests() {
            this.mEatRunOnAnimationRequest = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            if (this.mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        public void fling(int i, int i2) {
            RecyclerView.this.setScrollState(RecyclerView.SCROLL_STATE_SETTLING);
            this.mLastFlingY = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mLastFlingX = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mScroller.fling(RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.TOUCH_SLOP_DEFAULT, i, i2, TLRPC.MESSAGE_FLAG_MEGAGROUP, ConnectionsManager.DEFAULT_DATACENTER_ID, TLRPC.MESSAGE_FLAG_MEGAGROUP, ConnectionsManager.DEFAULT_DATACENTER_ID);
            postOnAnimation();
        }

        void postOnAnimation() {
            if (this.mEatRunOnAnimationRequest) {
                this.mReSchedulePostAnimationCallback = true;
                return;
            }
            RecyclerView.this.removeCallbacks(this);
            ViewCompat.postOnAnimation(RecyclerView.this, this);
        }

        public void run() {
            if (RecyclerView.this.mLayout == null) {
                stop();
                return;
            }
            disableRunOnAnimationRequests();
            RecyclerView.this.consumePendingUpdateOperations();
            ScrollerCompat scrollerCompat = this.mScroller;
            SmoothScroller smoothScroller = RecyclerView.this.mLayout.mSmoothScroller;
            if (scrollerCompat.computeScrollOffset()) {
                int itemCount;
                int i;
                int currVelocity;
                int i2;
                Object obj;
                Object obj2;
                int currX = scrollerCompat.getCurrX();
                int currY = scrollerCompat.getCurrY();
                int i3 = currX - this.mLastFlingX;
                int i4 = currY - this.mLastFlingY;
                int i5 = RecyclerView.TOUCH_SLOP_DEFAULT;
                int i6 = RecyclerView.TOUCH_SLOP_DEFAULT;
                this.mLastFlingX = currX;
                this.mLastFlingY = currY;
                int i7 = RecyclerView.TOUCH_SLOP_DEFAULT;
                int i8 = RecyclerView.TOUCH_SLOP_DEFAULT;
                if (RecyclerView.this.mAdapter != null) {
                    RecyclerView.this.eatRequestLayout();
                    RecyclerView.this.onEnterLayoutOrScroll();
                    TraceCompat.beginSection(RecyclerView.TRACE_SCROLL_TAG);
                    if (i3 != 0) {
                        i5 = RecyclerView.this.mLayout.scrollHorizontallyBy(i3, RecyclerView.this.mRecycler, RecyclerView.this.mState);
                        i7 = i3 - i5;
                    }
                    if (i4 != 0) {
                        i6 = RecyclerView.this.mLayout.scrollVerticallyBy(i4, RecyclerView.this.mRecycler, RecyclerView.this.mState);
                        i8 = i4 - i6;
                    }
                    TraceCompat.endSection();
                    RecyclerView.this.repositionShadowingViews();
                    RecyclerView.this.onExitLayoutOrScroll();
                    RecyclerView.this.resumeRequestLayout(RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST);
                    if (!(smoothScroller == null || smoothScroller.isPendingInitialRun() || !smoothScroller.isRunning())) {
                        itemCount = RecyclerView.this.mState.getItemCount();
                        if (itemCount == 0) {
                            smoothScroller.stop();
                            i = i7;
                            i7 = i6;
                            i6 = i;
                        } else if (smoothScroller.getTargetPosition() >= itemCount) {
                            smoothScroller.setTargetPosition(itemCount + RecyclerView.NO_POSITION);
                            smoothScroller.onAnimation(i3 - i7, i4 - i8);
                            i = i7;
                            i7 = i6;
                            i6 = i;
                        } else {
                            smoothScroller.onAnimation(i3 - i7, i4 - i8);
                        }
                        if (!RecyclerView.this.mItemDecorations.isEmpty()) {
                            RecyclerView.this.invalidate();
                        }
                        if (ViewCompat.getOverScrollMode(RecyclerView.this) != RecyclerView.SCROLL_STATE_SETTLING) {
                            RecyclerView.this.considerReleasingGlowsOnScroll(i3, i4);
                        }
                        if (!(i6 == 0 && i8 == 0)) {
                            currVelocity = (int) scrollerCompat.getCurrVelocity();
                            if (i6 == currX) {
                                itemCount = i6 >= 0 ? -currVelocity : i6 <= 0 ? currVelocity : RecyclerView.TOUCH_SLOP_DEFAULT;
                                i2 = itemCount;
                            } else {
                                i2 = RecyclerView.TOUCH_SLOP_DEFAULT;
                            }
                            if (i8 != currY) {
                                currVelocity = RecyclerView.TOUCH_SLOP_DEFAULT;
                            } else if (i8 < 0) {
                                currVelocity = -currVelocity;
                            } else if (i8 <= 0) {
                                currVelocity = RecyclerView.TOUCH_SLOP_DEFAULT;
                            }
                            if (ViewCompat.getOverScrollMode(RecyclerView.this) != RecyclerView.SCROLL_STATE_SETTLING) {
                                RecyclerView.this.absorbGlows(i2, currVelocity);
                            }
                            if ((i2 != 0 || i6 == currX || scrollerCompat.getFinalX() == 0) && (currVelocity != 0 || i8 == currY || scrollerCompat.getFinalY() == 0)) {
                                scrollerCompat.abortAnimation();
                            }
                        }
                        if (!(i5 == 0 && i7 == 0)) {
                            RecyclerView.this.dispatchOnScrolled(i5, i7);
                        }
                        if (!RecyclerView.this.awakenScrollBars()) {
                            RecyclerView.this.invalidate();
                        }
                        obj = (i4 == 0 && RecyclerView.this.mLayout.canScrollVertically() && i7 == i4) ? RecyclerView.VERTICAL : RecyclerView.TOUCH_SLOP_DEFAULT;
                        obj2 = (i3 == 0 && RecyclerView.this.mLayout.canScrollHorizontally() && i5 == i3) ? RecyclerView.VERTICAL : null;
                        obj2 = ((i3 == 0 || i4 != 0) && obj2 == null && obj == null) ? null : RecyclerView.VERTICAL;
                        if (!scrollerCompat.isFinished() || obj2 == null) {
                            RecyclerView.this.setScrollState(RecyclerView.TOUCH_SLOP_DEFAULT);
                        } else {
                            postOnAnimation();
                        }
                    }
                }
                i = i7;
                i7 = i6;
                i6 = i;
                if (RecyclerView.this.mItemDecorations.isEmpty()) {
                    RecyclerView.this.invalidate();
                }
                if (ViewCompat.getOverScrollMode(RecyclerView.this) != RecyclerView.SCROLL_STATE_SETTLING) {
                    RecyclerView.this.considerReleasingGlowsOnScroll(i3, i4);
                }
                currVelocity = (int) scrollerCompat.getCurrVelocity();
                if (i6 == currX) {
                    i2 = RecyclerView.TOUCH_SLOP_DEFAULT;
                } else {
                    if (i6 >= 0) {
                        if (i6 <= 0) {
                        }
                    }
                    i2 = itemCount;
                }
                if (i8 != currY) {
                    currVelocity = RecyclerView.TOUCH_SLOP_DEFAULT;
                } else if (i8 < 0) {
                    currVelocity = -currVelocity;
                } else if (i8 <= 0) {
                    currVelocity = RecyclerView.TOUCH_SLOP_DEFAULT;
                }
                if (ViewCompat.getOverScrollMode(RecyclerView.this) != RecyclerView.SCROLL_STATE_SETTLING) {
                    RecyclerView.this.absorbGlows(i2, currVelocity);
                }
                scrollerCompat.abortAnimation();
                RecyclerView.this.dispatchOnScrolled(i5, i7);
                if (RecyclerView.this.awakenScrollBars()) {
                    RecyclerView.this.invalidate();
                }
                if (i4 == 0) {
                }
                if (i3 == 0) {
                }
                if (i3 == 0) {
                }
                if (scrollerCompat.isFinished()) {
                }
                RecyclerView.this.setScrollState(RecyclerView.TOUCH_SLOP_DEFAULT);
            }
            if (smoothScroller != null) {
                if (smoothScroller.isPendingInitialRun()) {
                    smoothScroller.onAnimation(RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.TOUCH_SLOP_DEFAULT);
                }
                if (!this.mReSchedulePostAnimationCallback) {
                    smoothScroller.stop();
                }
            }
            enableRunOnAnimationRequests();
        }

        public void smoothScrollBy(int i, int i2) {
            smoothScrollBy(i, i2, (int) RecyclerView.TOUCH_SLOP_DEFAULT, (int) RecyclerView.TOUCH_SLOP_DEFAULT);
        }

        public void smoothScrollBy(int i, int i2, int i3) {
            smoothScrollBy(i, i2, i3, RecyclerView.sQuinticInterpolator);
        }

        public void smoothScrollBy(int i, int i2, int i3, int i4) {
            smoothScrollBy(i, i2, computeScrollDuration(i, i2, i3, i4));
        }

        public void smoothScrollBy(int i, int i2, int i3, Interpolator interpolator) {
            if (this.mInterpolator != interpolator) {
                this.mInterpolator = interpolator;
                this.mScroller = ScrollerCompat.create(RecyclerView.this.getContext(), interpolator);
            }
            RecyclerView.this.setScrollState(RecyclerView.SCROLL_STATE_SETTLING);
            this.mLastFlingY = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mLastFlingX = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mScroller.startScroll(RecyclerView.TOUCH_SLOP_DEFAULT, RecyclerView.TOUCH_SLOP_DEFAULT, i, i2, i3);
            postOnAnimation();
        }

        public void stop() {
            RecyclerView.this.removeCallbacks(this);
            this.mScroller.abortAnimation();
        }
    }

    public static abstract class ViewHolder {
        static final int FLAG_ADAPTER_FULLUPDATE = 1024;
        static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
        static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
        static final int FLAG_BOUND = 1;
        static final int FLAG_IGNORE = 128;
        static final int FLAG_INVALID = 4;
        static final int FLAG_MOVED = 2048;
        static final int FLAG_NOT_RECYCLABLE = 16;
        static final int FLAG_REMOVED = 8;
        static final int FLAG_RETURNED_FROM_SCRAP = 32;
        static final int FLAG_TMP_DETACHED = 256;
        static final int FLAG_UPDATE = 2;
        private static final List<Object> FULLUPDATE_PAYLOADS;
        public final View itemView;
        private int mFlags;
        private boolean mInChangeScrap;
        private int mIsRecyclableCount;
        long mItemId;
        int mItemViewType;
        int mOldPosition;
        RecyclerView mOwnerRecyclerView;
        List<Object> mPayloads;
        int mPosition;
        int mPreLayoutPosition;
        private Recycler mScrapContainer;
        ViewHolder mShadowedHolder;
        ViewHolder mShadowingHolder;
        List<Object> mUnmodifiedPayloads;
        private int mWasImportantForAccessibilityBeforeHidden;

        static {
            FULLUPDATE_PAYLOADS = Collections.EMPTY_LIST;
        }

        public ViewHolder(View view) {
            this.mPosition = RecyclerView.NO_POSITION;
            this.mOldPosition = RecyclerView.NO_POSITION;
            this.mItemId = RecyclerView.NO_ID;
            this.mItemViewType = RecyclerView.NO_POSITION;
            this.mPreLayoutPosition = RecyclerView.NO_POSITION;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            this.mPayloads = null;
            this.mUnmodifiedPayloads = null;
            this.mIsRecyclableCount = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mScrapContainer = null;
            this.mInChangeScrap = RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
            this.mWasImportantForAccessibilityBeforeHidden = RecyclerView.TOUCH_SLOP_DEFAULT;
            if (view == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = view;
        }

        private void createPayloadsIfNeeded() {
            if (this.mPayloads == null) {
                this.mPayloads = new ArrayList();
                this.mUnmodifiedPayloads = Collections.unmodifiableList(this.mPayloads);
            }
        }

        private boolean doesTransientStatePreventRecycling() {
            return ((this.mFlags & FLAG_NOT_RECYCLABLE) == 0 && ViewCompat.hasTransientState(this.itemView)) ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        private void onEnteredHiddenState() {
            this.mWasImportantForAccessibilityBeforeHidden = ViewCompat.getImportantForAccessibility(this.itemView);
            ViewCompat.setImportantForAccessibility(this.itemView, FLAG_INVALID);
        }

        private void onLeftHiddenState() {
            ViewCompat.setImportantForAccessibility(this.itemView, this.mWasImportantForAccessibilityBeforeHidden);
            this.mWasImportantForAccessibilityBeforeHidden = RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        private boolean shouldBeKeptAsChild() {
            return (this.mFlags & FLAG_NOT_RECYCLABLE) != 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        void addChangePayload(Object obj) {
            if (obj == null) {
                addFlags(FLAG_ADAPTER_FULLUPDATE);
            } else if ((this.mFlags & FLAG_ADAPTER_FULLUPDATE) == 0) {
                createPayloadsIfNeeded();
                this.mPayloads.add(obj);
            }
        }

        void addFlags(int i) {
            this.mFlags |= i;
        }

        void clearOldPosition() {
            this.mOldPosition = RecyclerView.NO_POSITION;
            this.mPreLayoutPosition = RecyclerView.NO_POSITION;
        }

        void clearPayload() {
            if (this.mPayloads != null) {
                this.mPayloads.clear();
            }
            this.mFlags &= -1025;
        }

        void clearReturnedFromScrapFlag() {
            this.mFlags &= -33;
        }

        void clearTmpDetachFlag() {
            this.mFlags &= -257;
        }

        void flagRemovedAndOffsetPosition(int i, int i2, boolean z) {
            addFlags(FLAG_REMOVED);
            offsetPosition(i2, z);
            this.mPosition = i;
        }

        public final int getAdapterPosition() {
            return this.mOwnerRecyclerView == null ? RecyclerView.NO_POSITION : this.mOwnerRecyclerView.getAdapterPositionFor(this);
        }

        public final long getItemId() {
            return this.mItemId;
        }

        public final int getItemViewType() {
            return this.mItemViewType;
        }

        public final int getLayoutPosition() {
            return this.mPreLayoutPosition == RecyclerView.NO_POSITION ? this.mPosition : this.mPreLayoutPosition;
        }

        public final int getOldPosition() {
            return this.mOldPosition;
        }

        @Deprecated
        public final int getPosition() {
            return this.mPreLayoutPosition == RecyclerView.NO_POSITION ? this.mPosition : this.mPreLayoutPosition;
        }

        List<Object> getUnmodifiedPayloads() {
            return (this.mFlags & FLAG_ADAPTER_FULLUPDATE) == 0 ? (this.mPayloads == null || this.mPayloads.size() == 0) ? FULLUPDATE_PAYLOADS : this.mUnmodifiedPayloads : FULLUPDATE_PAYLOADS;
        }

        boolean hasAnyOfTheFlags(int i) {
            return (this.mFlags & i) != 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        boolean isAdapterPositionUnknown() {
            return ((this.mFlags & FLAG_ADAPTER_POSITION_UNKNOWN) != 0 || isInvalid()) ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        boolean isBound() {
            return (this.mFlags & FLAG_BOUND) != 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        boolean isInvalid() {
            return (this.mFlags & FLAG_INVALID) != 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        public final boolean isRecyclable() {
            return ((this.mFlags & FLAG_NOT_RECYCLABLE) != 0 || ViewCompat.hasTransientState(this.itemView)) ? RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : true;
        }

        boolean isRemoved() {
            return (this.mFlags & FLAG_REMOVED) != 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        boolean isScrap() {
            return this.mScrapContainer != null ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        boolean isTmpDetached() {
            return (this.mFlags & FLAG_TMP_DETACHED) != 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        boolean isUpdated() {
            return (this.mFlags & FLAG_UPDATE) != 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        boolean needsUpdate() {
            return (this.mFlags & FLAG_UPDATE) != 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        void offsetPosition(int i, boolean z) {
            if (this.mOldPosition == RecyclerView.NO_POSITION) {
                this.mOldPosition = this.mPosition;
            }
            if (this.mPreLayoutPosition == RecyclerView.NO_POSITION) {
                this.mPreLayoutPosition = this.mPosition;
            }
            if (z) {
                this.mPreLayoutPosition += i;
            }
            this.mPosition += i;
            if (this.itemView.getLayoutParams() != null) {
                ((LayoutParams) this.itemView.getLayoutParams()).mInsetsDirty = true;
            }
        }

        void resetInternal() {
            this.mFlags = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mPosition = RecyclerView.NO_POSITION;
            this.mOldPosition = RecyclerView.NO_POSITION;
            this.mItemId = RecyclerView.NO_ID;
            this.mPreLayoutPosition = RecyclerView.NO_POSITION;
            this.mIsRecyclableCount = RecyclerView.TOUCH_SLOP_DEFAULT;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            clearPayload();
            this.mWasImportantForAccessibilityBeforeHidden = RecyclerView.TOUCH_SLOP_DEFAULT;
        }

        void saveOldPosition() {
            if (this.mOldPosition == RecyclerView.NO_POSITION) {
                this.mOldPosition = this.mPosition;
            }
        }

        void setFlags(int i, int i2) {
            this.mFlags = (this.mFlags & (i2 ^ RecyclerView.NO_POSITION)) | (i & i2);
        }

        public final void setIsRecyclable(boolean z) {
            this.mIsRecyclableCount = z ? this.mIsRecyclableCount + RecyclerView.NO_POSITION : this.mIsRecyclableCount + FLAG_BOUND;
            if (this.mIsRecyclableCount < 0) {
                this.mIsRecyclableCount = RecyclerView.TOUCH_SLOP_DEFAULT;
                Log.e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
            } else if (!z && this.mIsRecyclableCount == FLAG_BOUND) {
                this.mFlags |= FLAG_NOT_RECYCLABLE;
            } else if (z && this.mIsRecyclableCount == 0) {
                this.mFlags &= -17;
            }
        }

        void setScrapContainer(Recycler recycler, boolean z) {
            this.mScrapContainer = recycler;
            this.mInChangeScrap = z;
        }

        boolean shouldIgnore() {
            return (this.mFlags & FLAG_IGNORE) != 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }

        void stopIgnoring() {
            this.mFlags &= -129;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder("ViewHolder{" + Integer.toHexString(hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
            if (isScrap()) {
                stringBuilder.append(" scrap ").append(this.mInChangeScrap ? "[changeScrap]" : "[attachedScrap]");
            }
            if (isInvalid()) {
                stringBuilder.append(" invalid");
            }
            if (!isBound()) {
                stringBuilder.append(" unbound");
            }
            if (needsUpdate()) {
                stringBuilder.append(" update");
            }
            if (isRemoved()) {
                stringBuilder.append(" removed");
            }
            if (shouldIgnore()) {
                stringBuilder.append(" ignored");
            }
            if (isTmpDetached()) {
                stringBuilder.append(" tmpDetached");
            }
            if (!isRecyclable()) {
                stringBuilder.append(" not recyclable(" + this.mIsRecyclableCount + ")");
            }
            if (isAdapterPositionUnknown()) {
                stringBuilder.append(" undefined adapter position");
            }
            if (this.itemView.getParent() == null) {
                stringBuilder.append(" no parent");
            }
            stringBuilder.append("}");
            return stringBuilder.toString();
        }

        void unScrap() {
            this.mScrapContainer.unscrapView(this);
        }

        boolean wasReturnedFromScrap() {
            return (this.mFlags & FLAG_RETURNED_FROM_SCRAP) != 0 ? true : RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST;
        }
    }

    static {
        boolean z = (VERSION.SDK_INT == 18 || VERSION.SDK_INT == 19 || VERSION.SDK_INT == 20) ? true : FORCE_INVALIDATE_DISPLAY_LIST;
        FORCE_INVALIDATE_DISPLAY_LIST = z;
        ALLOW_SIZE_IN_UNSPECIFIED_SPEC = VERSION.SDK_INT >= 23 ? true : FORCE_INVALIDATE_DISPLAY_LIST;
        LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[]{Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE};
        sQuinticInterpolator = new C08643();
    }

    public RecyclerView(Context context) {
        this(context, null);
    }

    public RecyclerView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, TOUCH_SLOP_DEFAULT);
    }

    public RecyclerView(Context context, @Nullable AttributeSet attributeSet, int i) {
        boolean z = FORCE_INVALIDATE_DISPLAY_LIST;
        super(context, attributeSet, i);
        this.mObserver = new RecyclerViewDataObserver();
        this.mRecycler = new Recycler();
        this.mViewInfoStore = new ViewInfoStore();
        this.mUpdateChildViewsRunnable = new C08621();
        this.mTempRect = new Rect();
        this.mItemDecorations = new ArrayList();
        this.mOnItemTouchListeners = new ArrayList();
        this.mEatRequestLayout = TOUCH_SLOP_DEFAULT;
        this.mDataSetHasChangedAfterLayout = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mLayoutOrScrollCounter = TOUCH_SLOP_DEFAULT;
        this.topGlowOffset = TOUCH_SLOP_DEFAULT;
        this.glowColor = TOUCH_SLOP_DEFAULT;
        this.mItemAnimator = new DefaultItemAnimator();
        this.mScrollState = TOUCH_SLOP_DEFAULT;
        this.mScrollPointerId = NO_POSITION;
        this.mScrollFactor = Cue.DIMEN_UNSET;
        this.mViewFlinger = new ViewFlinger();
        this.mState = new State();
        this.mItemsAddedOrRemoved = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mItemsChanged = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mItemAnimatorListener = new ItemAnimatorRestoreListener();
        this.mPostedAnimatorRunner = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mMinMaxLayoutPositions = new int[SCROLL_STATE_SETTLING];
        this.mScrollOffset = new int[SCROLL_STATE_SETTLING];
        this.mScrollConsumed = new int[SCROLL_STATE_SETTLING];
        this.mNestedOffsets = new int[SCROLL_STATE_SETTLING];
        this.mItemAnimatorRunner = new C08632();
        this.mViewInfoProcessCallback = new C08654();
        setScrollContainer(true);
        setFocusableInTouchMode(true);
        this.mPostUpdatesOnAnimation = VERSION.SDK_INT >= 16 ? true : FORCE_INVALIDATE_DISPLAY_LIST;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        if (ViewCompat.getOverScrollMode(this) == SCROLL_STATE_SETTLING) {
            z = true;
        }
        setWillNotDraw(z);
        this.mItemAnimator.setListener(this.mItemAnimatorListener);
        initAdapterManager();
        initChildrenHelper();
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, VERTICAL);
        }
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
        setNestedScrollingEnabled(true);
    }

    private void addAnimatingView(ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        boolean z = view.getParent() == this ? true : FORCE_INVALIDATE_DISPLAY_LIST;
        this.mRecycler.unscrapView(getChildViewHolder(view));
        if (viewHolder.isTmpDetached()) {
            this.mChildHelper.attachViewToParent(view, NO_POSITION, view.getLayoutParams(), true);
        } else if (z) {
            this.mChildHelper.hide(view);
        } else {
            this.mChildHelper.addView(view, true);
        }
    }

    private void animateAppearance(@NonNull ViewHolder viewHolder, @Nullable ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2) {
        viewHolder.setIsRecyclable(FORCE_INVALIDATE_DISPLAY_LIST);
        if (this.mItemAnimator.animateAppearance(viewHolder, itemHolderInfo, itemHolderInfo2)) {
            postAnimationRunner();
        }
    }

    private void animateChange(@NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder2, @NonNull ItemHolderInfo itemHolderInfo, @NonNull ItemHolderInfo itemHolderInfo2, boolean z, boolean z2) {
        viewHolder.setIsRecyclable(FORCE_INVALIDATE_DISPLAY_LIST);
        if (z) {
            addAnimatingView(viewHolder);
        }
        if (viewHolder != viewHolder2) {
            if (z2) {
                addAnimatingView(viewHolder2);
            }
            viewHolder.mShadowedHolder = viewHolder2;
            addAnimatingView(viewHolder);
            this.mRecycler.unscrapView(viewHolder);
            viewHolder2.setIsRecyclable(FORCE_INVALIDATE_DISPLAY_LIST);
            viewHolder2.mShadowingHolder = viewHolder;
        }
        if (this.mItemAnimator.animateChange(viewHolder, viewHolder2, itemHolderInfo, itemHolderInfo2)) {
            postAnimationRunner();
        }
    }

    private void animateDisappearance(@NonNull ViewHolder viewHolder, @NonNull ItemHolderInfo itemHolderInfo, @Nullable ItemHolderInfo itemHolderInfo2) {
        addAnimatingView(viewHolder);
        viewHolder.setIsRecyclable(FORCE_INVALIDATE_DISPLAY_LIST);
        if (this.mItemAnimator.animateDisappearance(viewHolder, itemHolderInfo, itemHolderInfo2)) {
            postAnimationRunner();
        }
    }

    private boolean canReuseUpdatedViewHolder(ViewHolder viewHolder) {
        return (this.mItemAnimator == null || this.mItemAnimator.canReuseUpdatedViewHolder(viewHolder, viewHolder.getUnmodifiedPayloads())) ? true : FORCE_INVALIDATE_DISPLAY_LIST;
    }

    private void cancelTouch() {
        resetTouch();
        setScrollState(TOUCH_SLOP_DEFAULT);
    }

    private void considerReleasingGlowsOnScroll(int i, int i2) {
        int i3 = TOUCH_SLOP_DEFAULT;
        if (!(this.mLeftGlow == null || this.mLeftGlow.isFinished() || i <= 0)) {
            i3 = this.mLeftGlow.onRelease();
        }
        if (!(this.mRightGlow == null || this.mRightGlow.isFinished() || i >= 0)) {
            i3 |= this.mRightGlow.onRelease();
        }
        if (!(this.mTopGlow == null || this.mTopGlow.isFinished() || i2 <= 0)) {
            i3 |= this.mTopGlow.onRelease();
        }
        if (!(this.mBottomGlow == null || this.mBottomGlow.isFinished() || i2 >= 0)) {
            i3 |= this.mBottomGlow.onRelease();
        }
        if (i3 != 0) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void consumePendingUpdateOperations() {
        if (!this.mFirstLayoutComplete) {
            return;
        }
        if (this.mDataSetHasChangedAfterLayout) {
            TraceCompat.beginSection(TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG);
            dispatchLayout();
            TraceCompat.endSection();
        } else if (!this.mAdapterHelper.hasPendingUpdates()) {
        } else {
            if (this.mAdapterHelper.hasAnyUpdateTypes(4) && !this.mAdapterHelper.hasAnyUpdateTypes(11)) {
                TraceCompat.beginSection(TRACE_HANDLE_ADAPTER_UPDATES_TAG);
                eatRequestLayout();
                this.mAdapterHelper.preProcess();
                if (!this.mLayoutRequestEaten) {
                    if (hasUpdatedView()) {
                        dispatchLayout();
                    } else {
                        this.mAdapterHelper.consumePostponedUpdates();
                    }
                }
                resumeRequestLayout(true);
                TraceCompat.endSection();
            } else if (this.mAdapterHelper.hasPendingUpdates()) {
                TraceCompat.beginSection(TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG);
                dispatchLayout();
                TraceCompat.endSection();
            }
        }
    }

    private void createLayoutManager(Context context, String str, AttributeSet attributeSet, int i, int i2) {
        if (str != null) {
            String trim = str.trim();
            if (trim.length() != 0) {
                String fullClassName = getFullClassName(context, trim);
                try {
                    Object[] objArr;
                    Constructor constructor;
                    Class asSubclass = (isInEditMode() ? getClass().getClassLoader() : context.getClassLoader()).loadClass(fullClassName).asSubclass(LayoutManager.class);
                    try {
                        objArr = new Object[]{context, attributeSet, Integer.valueOf(i), Integer.valueOf(i2)};
                        constructor = asSubclass.getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
                    } catch (Throwable e) {
                        constructor = asSubclass.getConstructor(new Class[TOUCH_SLOP_DEFAULT]);
                        objArr = null;
                    }
                    constructor.setAccessible(true);
                    setLayoutManager((LayoutManager) constructor.newInstance(objArr));
                } catch (Throwable e2) {
                    e2.initCause(e);
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Error creating LayoutManager " + fullClassName, e2);
                } catch (Throwable e3) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Unable to find LayoutManager " + fullClassName, e3);
                } catch (Throwable e32) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + fullClassName, e32);
                } catch (Throwable e322) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + fullClassName, e322);
                } catch (Throwable e3222) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Cannot access non-public constructor " + fullClassName, e3222);
                } catch (Throwable e32222) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Class is not a LayoutManager " + fullClassName, e32222);
                }
            }
        }
    }

    private boolean didChildRangeChange(int i, int i2) {
        if (this.mChildHelper.getChildCount() == 0) {
            return (i == 0 && i2 == 0) ? FORCE_INVALIDATE_DISPLAY_LIST : true;
        } else {
            findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
            return (this.mMinMaxLayoutPositions[TOUCH_SLOP_DEFAULT] == i && this.mMinMaxLayoutPositions[VERTICAL] == i2) ? FORCE_INVALIDATE_DISPLAY_LIST : true;
        }
    }

    private void dispatchChildAttached(View view) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        onChildAttachedToWindow(view);
        if (!(this.mAdapter == null || childViewHolderInt == null)) {
            this.mAdapter.onViewAttachedToWindow(childViewHolderInt);
        }
        if (this.mOnChildAttachStateListeners != null) {
            for (int size = this.mOnChildAttachStateListeners.size() + NO_POSITION; size >= 0; size += NO_POSITION) {
                ((OnChildAttachStateChangeListener) this.mOnChildAttachStateListeners.get(size)).onChildViewAttachedToWindow(view);
            }
        }
    }

    private void dispatchChildDetached(View view) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        onChildDetachedFromWindow(view);
        if (!(this.mAdapter == null || childViewHolderInt == null)) {
            this.mAdapter.onViewDetachedFromWindow(childViewHolderInt);
        }
        if (this.mOnChildAttachStateListeners != null) {
            for (int size = this.mOnChildAttachStateListeners.size() + NO_POSITION; size >= 0; size += NO_POSITION) {
                ((OnChildAttachStateChangeListener) this.mOnChildAttachStateListeners.get(size)).onChildViewDetachedFromWindow(view);
            }
        }
    }

    private void dispatchContentChangedIfNecessary() {
        int i = this.mEatenAccessibilityChangeFlags;
        this.mEatenAccessibilityChangeFlags = TOUCH_SLOP_DEFAULT;
        if (i != 0 && isAccessibilityEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(TLRPC.MESSAGE_FLAG_HAS_BOT_ID);
            AccessibilityEventCompat.setContentChangeTypes(obtain, i);
            sendAccessibilityEventUnchecked(obtain);
        }
    }

    private void dispatchLayoutStep1() {
        int childCount;
        int i;
        ViewHolder childViewHolderInt;
        boolean z = true;
        this.mState.assertLayoutStep(VERTICAL);
        this.mState.mIsMeasuring = FORCE_INVALIDATE_DISPLAY_LIST;
        eatRequestLayout();
        this.mViewInfoStore.clear();
        onEnterLayoutOrScroll();
        processAdapterUpdatesAndSetAnimationFlags();
        State state = this.mState;
        if (!(this.mState.mRunSimpleAnimations && this.mItemsChanged)) {
            z = FORCE_INVALIDATE_DISPLAY_LIST;
        }
        state.mTrackOldChangeHolders = z;
        this.mItemsChanged = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mItemsAddedOrRemoved = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mState.mInPreLayout = this.mState.mRunPredictiveAnimations;
        this.mState.mItemCount = this.mAdapter.getItemCount();
        findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
        if (this.mState.mRunSimpleAnimations) {
            childCount = this.mChildHelper.getChildCount();
            for (i = TOUCH_SLOP_DEFAULT; i < childCount; i += VERTICAL) {
                childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!childViewHolderInt.shouldIgnore() && (!childViewHolderInt.isInvalid() || this.mAdapter.hasStableIds())) {
                    this.mViewInfoStore.addToPreLayout(childViewHolderInt, this.mItemAnimator.recordPreLayoutInformation(this.mState, childViewHolderInt, ItemAnimator.buildAdapterChangeFlagsForAnimations(childViewHolderInt), childViewHolderInt.getUnmodifiedPayloads()));
                    if (!(!this.mState.mTrackOldChangeHolders || !childViewHolderInt.isUpdated() || childViewHolderInt.isRemoved() || childViewHolderInt.shouldIgnore() || childViewHolderInt.isInvalid())) {
                        this.mViewInfoStore.addToOldChangeHolders(getChangedHolderKey(childViewHolderInt), childViewHolderInt);
                    }
                }
            }
        }
        if (this.mState.mRunPredictiveAnimations) {
            saveOldPositions();
            z = this.mState.mStructureChanged;
            this.mState.mStructureChanged = FORCE_INVALIDATE_DISPLAY_LIST;
            this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
            this.mState.mStructureChanged = z;
            for (i = TOUCH_SLOP_DEFAULT; i < this.mChildHelper.getChildCount(); i += VERTICAL) {
                childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
                if (!(childViewHolderInt.shouldIgnore() || this.mViewInfoStore.isInPreLayout(childViewHolderInt))) {
                    childCount = ItemAnimator.buildAdapterChangeFlagsForAnimations(childViewHolderInt);
                    boolean hasAnyOfTheFlags = childViewHolderInt.hasAnyOfTheFlags(MessagesController.UPDATE_MASK_CHANNEL);
                    if (!hasAnyOfTheFlags) {
                        childCount |= ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT;
                    }
                    ItemHolderInfo recordPreLayoutInformation = this.mItemAnimator.recordPreLayoutInformation(this.mState, childViewHolderInt, childCount, childViewHolderInt.getUnmodifiedPayloads());
                    if (hasAnyOfTheFlags) {
                        recordAnimationInfoIfBouncedHiddenView(childViewHolderInt, recordPreLayoutInformation);
                    } else {
                        this.mViewInfoStore.addToAppearedInPreLayoutHolders(childViewHolderInt, recordPreLayoutInformation);
                    }
                }
            }
            clearOldPositions();
        } else {
            clearOldPositions();
        }
        onExitLayoutOrScroll();
        resumeRequestLayout(FORCE_INVALIDATE_DISPLAY_LIST);
        this.mState.mLayoutStep = SCROLL_STATE_SETTLING;
    }

    private void dispatchLayoutStep2() {
        eatRequestLayout();
        onEnterLayoutOrScroll();
        this.mState.assertLayoutStep(6);
        this.mAdapterHelper.consumeUpdatesInOnePass();
        this.mState.mItemCount = this.mAdapter.getItemCount();
        this.mState.mDeletedInvisibleItemCountSincePreviousLayout = TOUCH_SLOP_DEFAULT;
        this.mState.mInPreLayout = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
        this.mState.mStructureChanged = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mPendingSavedState = null;
        State state = this.mState;
        boolean z = (!this.mState.mRunSimpleAnimations || this.mItemAnimator == null) ? FORCE_INVALIDATE_DISPLAY_LIST : true;
        state.mRunSimpleAnimations = z;
        this.mState.mLayoutStep = 4;
        onExitLayoutOrScroll();
        resumeRequestLayout(FORCE_INVALIDATE_DISPLAY_LIST);
    }

    private void dispatchLayoutStep3() {
        this.mState.assertLayoutStep(4);
        eatRequestLayout();
        onEnterLayoutOrScroll();
        this.mState.mLayoutStep = VERTICAL;
        if (this.mState.mRunSimpleAnimations) {
            for (int childCount = this.mChildHelper.getChildCount() + NO_POSITION; childCount >= 0; childCount += NO_POSITION) {
                ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(childCount));
                if (!childViewHolderInt.shouldIgnore()) {
                    long changedHolderKey = getChangedHolderKey(childViewHolderInt);
                    ItemHolderInfo recordPostLayoutInformation = this.mItemAnimator.recordPostLayoutInformation(this.mState, childViewHolderInt);
                    ViewHolder fromOldChangeHolders = this.mViewInfoStore.getFromOldChangeHolders(changedHolderKey);
                    if (fromOldChangeHolders == null || fromOldChangeHolders.shouldIgnore()) {
                        this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                    } else {
                        boolean isDisappearing = this.mViewInfoStore.isDisappearing(fromOldChangeHolders);
                        boolean isDisappearing2 = this.mViewInfoStore.isDisappearing(childViewHolderInt);
                        if (isDisappearing && fromOldChangeHolders == childViewHolderInt) {
                            this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                        } else {
                            ItemHolderInfo popFromPreLayout = this.mViewInfoStore.popFromPreLayout(fromOldChangeHolders);
                            this.mViewInfoStore.addToPostLayout(childViewHolderInt, recordPostLayoutInformation);
                            ItemHolderInfo popFromPostLayout = this.mViewInfoStore.popFromPostLayout(childViewHolderInt);
                            if (popFromPreLayout == null) {
                                handleMissingPreInfoForChangeError(changedHolderKey, childViewHolderInt, fromOldChangeHolders);
                            } else {
                                animateChange(fromOldChangeHolders, childViewHolderInt, popFromPreLayout, popFromPostLayout, isDisappearing, isDisappearing2);
                            }
                        }
                    }
                }
            }
            this.mViewInfoStore.process(this.mViewInfoProcessCallback);
        }
        this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
        this.mState.mPreviousLayoutItemCount = this.mState.mItemCount;
        this.mDataSetHasChangedAfterLayout = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mState.mRunSimpleAnimations = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mState.mRunPredictiveAnimations = FORCE_INVALIDATE_DISPLAY_LIST;
        this.mLayout.mRequestedSimpleAnimations = FORCE_INVALIDATE_DISPLAY_LIST;
        if (this.mRecycler.mChangedScrap != null) {
            this.mRecycler.mChangedScrap.clear();
        }
        onExitLayoutOrScroll();
        resumeRequestLayout(FORCE_INVALIDATE_DISPLAY_LIST);
        this.mViewInfoStore.clear();
        if (didChildRangeChange(this.mMinMaxLayoutPositions[TOUCH_SLOP_DEFAULT], this.mMinMaxLayoutPositions[VERTICAL])) {
            dispatchOnScrolled(TOUCH_SLOP_DEFAULT, TOUCH_SLOP_DEFAULT);
        }
    }

    private boolean dispatchOnItemTouch(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (this.mActiveOnItemTouchListener != null) {
            if (action == 0) {
                this.mActiveOnItemTouchListener = null;
            } else {
                this.mActiveOnItemTouchListener.onTouchEvent(this, motionEvent);
                if (action == 3 || action == VERTICAL) {
                    this.mActiveOnItemTouchListener = null;
                }
                return true;
            }
        }
        if (action != 0) {
            int size = this.mOnItemTouchListeners.size();
            for (int i = TOUCH_SLOP_DEFAULT; i < size; i += VERTICAL) {
                OnItemTouchListener onItemTouchListener = (OnItemTouchListener) this.mOnItemTouchListeners.get(i);
                if (onItemTouchListener.onInterceptTouchEvent(this, motionEvent)) {
                    this.mActiveOnItemTouchListener = onItemTouchListener;
                    return true;
                }
            }
        }
        return FORCE_INVALIDATE_DISPLAY_LIST;
    }

    private boolean dispatchOnItemTouchIntercept(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 3 || action == 0) {
            this.mActiveOnItemTouchListener = null;
        }
        int size = this.mOnItemTouchListeners.size();
        int i = TOUCH_SLOP_DEFAULT;
        while (i < size) {
            OnItemTouchListener onItemTouchListener = (OnItemTouchListener) this.mOnItemTouchListeners.get(i);
            if (!onItemTouchListener.onInterceptTouchEvent(this, motionEvent) || action == 3) {
                i += VERTICAL;
            } else {
                this.mActiveOnItemTouchListener = onItemTouchListener;
                return true;
            }
        }
        return FORCE_INVALIDATE_DISPLAY_LIST;
    }

    private void findMinMaxChildLayoutPositions(int[] iArr) {
        int childCount = this.mChildHelper.getChildCount();
        if (childCount == 0) {
            iArr[TOUCH_SLOP_DEFAULT] = TOUCH_SLOP_DEFAULT;
            iArr[VERTICAL] = TOUCH_SLOP_DEFAULT;
            return;
        }
        int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
        int i2 = TLRPC.MESSAGE_FLAG_MEGAGROUP;
        int i3 = TOUCH_SLOP_DEFAULT;
        while (i3 < childCount) {
            int i4;
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i3));
            if (childViewHolderInt.shouldIgnore()) {
                i4 = i;
            } else {
                i4 = childViewHolderInt.getLayoutPosition();
                if (i4 < i) {
                    i = i4;
                }
                if (i4 > i2) {
                    i2 = i4;
                    i4 = i;
                } else {
                    i4 = i;
                }
            }
            i3 += VERTICAL;
            i = i4;
        }
        iArr[TOUCH_SLOP_DEFAULT] = i;
        iArr[VERTICAL] = i2;
    }

    private int getAdapterPositionFor(ViewHolder viewHolder) {
        return (viewHolder.hasAnyOfTheFlags(524) || !viewHolder.isBound()) ? NO_POSITION : this.mAdapterHelper.applyPendingUpdatesToPosition(viewHolder.mPosition);
    }

    static ViewHolder getChildViewHolderInt(View view) {
        return view == null ? null : ((LayoutParams) view.getLayoutParams()).mViewHolder;
    }

    private String getFullClassName(Context context, String str) {
        return str.charAt(TOUCH_SLOP_DEFAULT) == '.' ? context.getPackageName() + str : str.indexOf(46) == NO_POSITION ? RecyclerView.class.getPackage().getName() + '.' + str : str;
    }

    private float getScrollFactor() {
        if (this.mScrollFactor == Cue.DIMEN_UNSET) {
            TypedValue typedValue = new TypedValue();
            if (!getContext().getTheme().resolveAttribute(16842829, typedValue, true)) {
                return 0.0f;
            }
            this.mScrollFactor = typedValue.getDimension(getContext().getResources().getDisplayMetrics());
        }
        return this.mScrollFactor;
    }

    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (this.mScrollingChildHelper == null) {
            this.mScrollingChildHelper = new NestedScrollingChildHelper(this);
        }
        return this.mScrollingChildHelper;
    }

    private void handleMissingPreInfoForChangeError(long j, ViewHolder viewHolder, ViewHolder viewHolder2) {
        int childCount = this.mChildHelper.getChildCount();
        int i = TOUCH_SLOP_DEFAULT;
        while (i < childCount) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (childViewHolderInt == viewHolder || getChangedHolderKey(childViewHolderInt) != j) {
                i += VERTICAL;
            } else if (this.mAdapter == null || !this.mAdapter.hasStableIds()) {
                throw new IllegalStateException("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:" + childViewHolderInt + " \n View Holder 2:" + viewHolder);
            } else {
                throw new IllegalStateException("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:" + childViewHolderInt + " \n View Holder 2:" + viewHolder);
            }
        }
        Log.e(TAG, "Problem while matching changed view holders with the newones. The pre-layout information for the change holder " + viewHolder2 + " cannot be found but it is necessary for " + viewHolder);
    }

    private boolean hasUpdatedView() {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = TOUCH_SLOP_DEFAULT; i < childCount; i += VERTICAL) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore() && childViewHolderInt.isUpdated()) {
                return true;
            }
        }
        return FORCE_INVALIDATE_DISPLAY_LIST;
    }

    private void initChildrenHelper() {
        this.mChildHelper = new ChildHelper(new C08665());
    }

    private void jumpToPositionForSmoothScroller(int i) {
        if (this.mLayout != null) {
            this.mLayout.scrollToPosition(i);
            awakenScrollBars();
        }
    }

    private void onEnterLayoutOrScroll() {
        this.mLayoutOrScrollCounter += VERTICAL;
    }

    private void onExitLayoutOrScroll() {
        this.mLayoutOrScrollCounter += NO_POSITION;
        if (this.mLayoutOrScrollCounter < VERTICAL) {
            this.mLayoutOrScrollCounter = TOUCH_SLOP_DEFAULT;
            dispatchContentChangedIfNecessary();
        }
    }

    private void onPointerUp(MotionEvent motionEvent) {
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (MotionEventCompat.getPointerId(motionEvent, actionIndex) == this.mScrollPointerId) {
            actionIndex = actionIndex == 0 ? VERTICAL : TOUCH_SLOP_DEFAULT;
            this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
            int x = (int) (MotionEventCompat.getX(motionEvent, actionIndex) + 0.5f);
            this.mLastTouchX = x;
            this.mInitialTouchX = x;
            actionIndex = (int) (MotionEventCompat.getY(motionEvent, actionIndex) + 0.5f);
            this.mLastTouchY = actionIndex;
            this.mInitialTouchY = actionIndex;
        }
    }

    private void postAnimationRunner() {
        if (!this.mPostedAnimatorRunner && this.mIsAttached) {
            ViewCompat.postOnAnimation(this, this.mItemAnimatorRunner);
            this.mPostedAnimatorRunner = true;
        }
    }

    private boolean predictiveItemAnimationsEnabled() {
        return (this.mItemAnimator == null || !this.mLayout.supportsPredictiveItemAnimations()) ? FORCE_INVALIDATE_DISPLAY_LIST : true;
    }

    private void processAdapterUpdatesAndSetAnimationFlags() {
        boolean z = true;
        if (this.mDataSetHasChangedAfterLayout) {
            this.mAdapterHelper.reset();
            markKnownViewsInvalid();
            this.mLayout.onItemsChanged(this);
        }
        if (predictiveItemAnimationsEnabled()) {
            this.mAdapterHelper.preProcess();
        } else {
            this.mAdapterHelper.consumeUpdatesInOnePass();
        }
        boolean z2 = (this.mItemsAddedOrRemoved || this.mItemsChanged) ? true : TOUCH_SLOP_DEFAULT;
        State state = this.mState;
        boolean z3 = (!this.mFirstLayoutComplete || this.mItemAnimator == null || (!(this.mDataSetHasChangedAfterLayout || z2 || this.mLayout.mRequestedSimpleAnimations) || (this.mDataSetHasChangedAfterLayout && !this.mAdapter.hasStableIds()))) ? FORCE_INVALIDATE_DISPLAY_LIST : true;
        state.mRunSimpleAnimations = z3;
        State state2 = this.mState;
        if (!(this.mState.mRunSimpleAnimations && z2 && !this.mDataSetHasChangedAfterLayout && predictiveItemAnimationsEnabled())) {
            z = FORCE_INVALIDATE_DISPLAY_LIST;
        }
        state2.mRunPredictiveAnimations = z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void pullGlows(float r8, float r9, float r10, float r11) {
        /*
        r7 = this;
        r6 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = 1;
        r5 = 0;
        r1 = 0;
        r2 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1));
        if (r2 >= 0) goto L_0x0050;
    L_0x0009:
        r7.ensureLeftGlow();
        r2 = r7.mLeftGlow;
        r3 = -r9;
        r4 = r7.getWidth();
        r4 = (float) r4;
        r3 = r3 / r4;
        r4 = r7.getHeight();
        r4 = (float) r4;
        r4 = r10 / r4;
        r4 = r6 - r4;
        r2 = r2.onPull(r3, r4);
        if (r2 == 0) goto L_0x0025;
    L_0x0024:
        r1 = r0;
    L_0x0025:
        r2 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1));
        if (r2 >= 0) goto L_0x006f;
    L_0x0029:
        r7.ensureTopGlow();
        r2 = r7.mTopGlow;
        r3 = -r11;
        r4 = r7.getHeight();
        r4 = (float) r4;
        r3 = r3 / r4;
        r4 = r7.getWidth();
        r4 = (float) r4;
        r4 = r8 / r4;
        r2 = r2.onPull(r3, r4);
        if (r2 == 0) goto L_0x008e;
    L_0x0042:
        if (r0 != 0) goto L_0x004c;
    L_0x0044:
        r0 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1));
        if (r0 != 0) goto L_0x004c;
    L_0x0048:
        r0 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1));
        if (r0 == 0) goto L_0x004f;
    L_0x004c:
        android.support.v4.view.ViewCompat.postInvalidateOnAnimation(r7);
    L_0x004f:
        return;
    L_0x0050:
        r2 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1));
        if (r2 <= 0) goto L_0x0025;
    L_0x0054:
        r7.ensureRightGlow();
        r2 = r7.mRightGlow;
        r3 = r7.getWidth();
        r3 = (float) r3;
        r3 = r9 / r3;
        r4 = r7.getHeight();
        r4 = (float) r4;
        r4 = r10 / r4;
        r2 = r2.onPull(r3, r4);
        if (r2 == 0) goto L_0x0025;
    L_0x006d:
        r1 = r0;
        goto L_0x0025;
    L_0x006f:
        r2 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1));
        if (r2 <= 0) goto L_0x008e;
    L_0x0073:
        r7.ensureBottomGlow();
        r2 = r7.mBottomGlow;
        r3 = r7.getHeight();
        r3 = (float) r3;
        r3 = r11 / r3;
        r4 = r7.getWidth();
        r4 = (float) r4;
        r4 = r8 / r4;
        r4 = r6 - r4;
        r2 = r2.onPull(r3, r4);
        if (r2 != 0) goto L_0x0042;
    L_0x008e:
        r0 = r1;
        goto L_0x0042;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.support.widget.RecyclerView.pullGlows(float, float, float, float):void");
    }

    private void recordAnimationInfoIfBouncedHiddenView(ViewHolder viewHolder, ItemHolderInfo itemHolderInfo) {
        viewHolder.setFlags(TOUCH_SLOP_DEFAULT, MessagesController.UPDATE_MASK_CHANNEL);
        if (this.mState.mTrackOldChangeHolders && viewHolder.isUpdated() && !viewHolder.isRemoved() && !viewHolder.shouldIgnore()) {
            this.mViewInfoStore.addToOldChangeHolders(getChangedHolderKey(viewHolder), viewHolder);
        }
        this.mViewInfoStore.addToPreLayout(viewHolder, itemHolderInfo);
    }

    private void releaseGlows() {
        int i = TOUCH_SLOP_DEFAULT;
        if (this.mLeftGlow != null) {
            i = this.mLeftGlow.onRelease();
        }
        if (this.mTopGlow != null) {
            i |= this.mTopGlow.onRelease();
        }
        if (this.mRightGlow != null) {
            i |= this.mRightGlow.onRelease();
        }
        if (this.mBottomGlow != null) {
            i |= this.mBottomGlow.onRelease();
        }
        if (i != 0) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean removeAnimatingView(View view) {
        eatRequestLayout();
        boolean removeViewIfHidden = this.mChildHelper.removeViewIfHidden(view);
        if (removeViewIfHidden) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(view);
            this.mRecycler.unscrapView(childViewHolderInt);
            this.mRecycler.recycleViewHolderInternal(childViewHolderInt);
        }
        resumeRequestLayout(!removeViewIfHidden ? true : FORCE_INVALIDATE_DISPLAY_LIST);
        return removeViewIfHidden;
    }

    private void repositionShadowingViews() {
        int childCount = this.mChildHelper.getChildCount();
        for (int i = TOUCH_SLOP_DEFAULT; i < childCount; i += VERTICAL) {
            View childAt = this.mChildHelper.getChildAt(i);
            ViewHolder childViewHolder = getChildViewHolder(childAt);
            if (!(childViewHolder == null || childViewHolder.mShadowingHolder == null)) {
                View view = childViewHolder.mShadowingHolder.itemView;
                int left = childAt.getLeft();
                int top = childAt.getTop();
                if (left != view.getLeft() || top != view.getTop()) {
                    view.layout(left, top, view.getWidth() + left, view.getHeight() + top);
                }
            }
        }
    }

    private void resetTouch() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.clear();
        }
        stopNestedScroll();
        releaseGlows();
    }

    private void setAdapterInternal(Adapter adapter, boolean z, boolean z2) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterAdapterDataObserver(this.mObserver);
            this.mAdapter.onDetachedFromRecyclerView(this);
        }
        if (!z || z2) {
            if (this.mItemAnimator != null) {
                this.mItemAnimator.endAnimations();
            }
            if (this.mLayout != null) {
                this.mLayout.removeAndRecycleAllViews(this.mRecycler);
                this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
            }
            this.mRecycler.clear();
        }
        this.mAdapterHelper.reset();
        Adapter adapter2 = this.mAdapter;
        this.mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mObserver);
            adapter.onAttachedToRecyclerView(this);
        }
        if (this.mLayout != null) {
            this.mLayout.onAdapterChanged(adapter2, this.mAdapter);
        }
        this.mRecycler.onAdapterChanged(adapter2, this.mAdapter, z);
        this.mState.mStructureChanged = true;
        markKnownViewsInvalid();
    }

    private void setDataSetChangedAfterLayout() {
        if (!this.mDataSetHasChangedAfterLayout) {
            this.mDataSetHasChangedAfterLayout = true;
            int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
            for (int i = TOUCH_SLOP_DEFAULT; i < unfilteredChildCount; i += VERTICAL) {
                ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
                if (!(childViewHolderInt == null || childViewHolderInt.shouldIgnore())) {
                    childViewHolderInt.addFlags(TLRPC.USER_FLAG_UNUSED3);
                }
            }
            this.mRecycler.setAdapterPositionsAsUnknown();
        }
    }

    private void setScrollState(int i) {
        if (i != this.mScrollState) {
            this.mScrollState = i;
            if (i != SCROLL_STATE_SETTLING) {
                stopScrollersInternal();
            }
            dispatchOnScrollStateChanged(i);
        }
    }

    private void stopScrollersInternal() {
        this.mViewFlinger.stop();
        if (this.mLayout != null) {
            this.mLayout.stopSmoothScroller();
        }
    }

    void absorbGlows(int i, int i2) {
        if (i < 0) {
            ensureLeftGlow();
            this.mLeftGlow.onAbsorb(-i);
        } else if (i > 0) {
            ensureRightGlow();
            this.mRightGlow.onAbsorb(i);
        }
        if (i2 < 0) {
            ensureTopGlow();
            this.mTopGlow.onAbsorb(-i2);
        } else if (i2 > 0) {
            ensureBottomGlow();
            this.mBottomGlow.onAbsorb(i2);
        }
        if (i != 0 || i2 != 0) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        if (this.mLayout == null || !this.mLayout.onAddFocusables(this, arrayList, i, i2)) {
            super.addFocusables(arrayList, i, i2);
        }
    }

    public void addItemDecoration(ItemDecoration itemDecoration) {
        addItemDecoration(itemDecoration, NO_POSITION);
    }

    public void addItemDecoration(ItemDecoration itemDecoration, int i) {
        if (this.mLayout != null) {
            this.mLayout.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
        }
        if (this.mItemDecorations.isEmpty()) {
            setWillNotDraw(FORCE_INVALIDATE_DISPLAY_LIST);
        }
        if (i < 0) {
            this.mItemDecorations.add(itemDecoration);
        } else {
            this.mItemDecorations.add(i, itemDecoration);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public void addOnChildAttachStateChangeListener(OnChildAttachStateChangeListener onChildAttachStateChangeListener) {
        if (this.mOnChildAttachStateListeners == null) {
            this.mOnChildAttachStateListeners = new ArrayList();
        }
        this.mOnChildAttachStateListeners.add(onChildAttachStateChangeListener);
    }

    public void addOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.mOnItemTouchListeners.add(onItemTouchListener);
    }

    public void addOnScrollListener(OnScrollListener onScrollListener) {
        if (this.mScrollListeners == null) {
            this.mScrollListeners = new ArrayList();
        }
        this.mScrollListeners.add(onScrollListener);
    }

    void applyEdgeEffectColor(EdgeEffectCompat edgeEffectCompat) {
        if (VERSION.SDK_INT >= 21 && this.glowColor != 0) {
            try {
                Field declaredField = EdgeEffectCompat.class.getDeclaredField("mEdgeEffect");
                declaredField.setAccessible(true);
                EdgeEffect edgeEffect = (EdgeEffect) declaredField.get(edgeEffectCompat);
                if (edgeEffect != null) {
                    edgeEffect.setColor(this.glowColor);
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    void assertInLayoutOrScroll(String str) {
        if (!isComputingLayout()) {
            if (str == null) {
                throw new IllegalStateException("Cannot call this method unless RecyclerView is computing a layout or scrolling");
            }
            throw new IllegalStateException(str);
        }
    }

    void assertNotInLayoutOrScroll(String str) {
        if (!isComputingLayout()) {
            return;
        }
        if (str == null) {
            throw new IllegalStateException("Cannot call this method while RecyclerView is computing a layout or scrolling");
        }
        throw new IllegalStateException(str);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return ((layoutParams instanceof LayoutParams) && this.mLayout.checkLayoutParams((LayoutParams) layoutParams)) ? true : FORCE_INVALIDATE_DISPLAY_LIST;
    }

    void clearOldPositions() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = TOUCH_SLOP_DEFAULT; i < unfilteredChildCount; i += VERTICAL) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.clearOldPosition();
            }
        }
        this.mRecycler.clearOldPositions();
    }

    public void clearOnChildAttachStateChangeListeners() {
        if (this.mOnChildAttachStateListeners != null) {
            this.mOnChildAttachStateListeners.clear();
        }
    }

    public void clearOnScrollListeners() {
        if (this.mScrollListeners != null) {
            this.mScrollListeners.clear();
        }
    }

    public int computeHorizontalScrollExtent() {
        return (this.mLayout != null && this.mLayout.canScrollHorizontally()) ? this.mLayout.computeHorizontalScrollExtent(this.mState) : TOUCH_SLOP_DEFAULT;
    }

    public int computeHorizontalScrollOffset() {
        return (this.mLayout != null && this.mLayout.canScrollHorizontally()) ? this.mLayout.computeHorizontalScrollOffset(this.mState) : TOUCH_SLOP_DEFAULT;
    }

    public int computeHorizontalScrollRange() {
        return (this.mLayout != null && this.mLayout.canScrollHorizontally()) ? this.mLayout.computeHorizontalScrollRange(this.mState) : TOUCH_SLOP_DEFAULT;
    }

    public int computeVerticalScrollExtent() {
        return (this.mLayout != null && this.mLayout.canScrollVertically()) ? this.mLayout.computeVerticalScrollExtent(this.mState) : TOUCH_SLOP_DEFAULT;
    }

    public int computeVerticalScrollOffset() {
        return (this.mLayout != null && this.mLayout.canScrollVertically()) ? this.mLayout.computeVerticalScrollOffset(this.mState) : TOUCH_SLOP_DEFAULT;
    }

    public int computeVerticalScrollRange() {
        return (this.mLayout != null && this.mLayout.canScrollVertically()) ? this.mLayout.computeVerticalScrollRange(this.mState) : TOUCH_SLOP_DEFAULT;
    }

    void defaultOnMeasure(int i, int i2) {
        setMeasuredDimension(LayoutManager.chooseSize(i, getPaddingLeft() + getPaddingRight(), ViewCompat.getMinimumWidth(this)), LayoutManager.chooseSize(i2, getPaddingTop() + getPaddingBottom(), ViewCompat.getMinimumHeight(this)));
    }

    void dispatchLayout() {
        if (this.mAdapter == null) {
            Log.e(TAG, "No adapter attached; skipping layout");
        } else if (this.mLayout == null) {
            Log.e(TAG, "No layout manager attached; skipping layout");
        } else {
            this.mState.mIsMeasuring = FORCE_INVALIDATE_DISPLAY_LIST;
            if (this.mState.mLayoutStep == VERTICAL) {
                dispatchLayoutStep1();
                this.mLayout.setExactMeasureSpecsFrom(this);
                dispatchLayoutStep2();
            } else if (!this.mAdapterHelper.hasUpdates() && this.mLayout.getWidth() == getWidth() && this.mLayout.getHeight() == getHeight()) {
                this.mLayout.setExactMeasureSpecsFrom(this);
            } else {
                this.mLayout.setExactMeasureSpecsFrom(this);
                dispatchLayoutStep2();
            }
            dispatchLayoutStep3();
        }
    }

    public boolean dispatchNestedFling(float f, float f2, boolean z) {
        return getScrollingChildHelper().dispatchNestedFling(f, f2, z);
    }

    public boolean dispatchNestedPreFling(float f, float f2) {
        return getScrollingChildHelper().dispatchNestedPreFling(f, f2);
    }

    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2) {
        return getScrollingChildHelper().dispatchNestedPreScroll(i, i2, iArr, iArr2);
    }

    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr) {
        return getScrollingChildHelper().dispatchNestedScroll(i, i2, i3, i4, iArr);
    }

    void dispatchOnScrollStateChanged(int i) {
        if (this.mLayout != null) {
            this.mLayout.onScrollStateChanged(i);
        }
        onScrollStateChanged(i);
        if (this.mScrollListener != null) {
            this.mScrollListener.onScrollStateChanged(this, i);
        }
        if (this.mScrollListeners != null) {
            for (int size = this.mScrollListeners.size() + NO_POSITION; size >= 0; size += NO_POSITION) {
                ((OnScrollListener) this.mScrollListeners.get(size)).onScrollStateChanged(this, i);
            }
        }
    }

    void dispatchOnScrolled(int i, int i2) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        onScrollChanged(scrollX, scrollY, scrollX, scrollY);
        onScrolled(i, i2);
        if (this.mScrollListener != null) {
            this.mScrollListener.onScrolled(this, i, i2);
        }
        if (this.mScrollListeners != null) {
            for (scrollY = this.mScrollListeners.size() + NO_POSITION; scrollY >= 0; scrollY += NO_POSITION) {
                ((OnScrollListener) this.mScrollListeners.get(scrollY)).onScrolled(this, i, i2);
            }
        }
    }

    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchThawSelfOnly(sparseArray);
    }

    protected void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchFreezeSelfOnly(sparseArray);
    }

    public void draw(Canvas canvas) {
        int i;
        int i2;
        int i3 = VERTICAL;
        int i4 = TOUCH_SLOP_DEFAULT;
        super.draw(canvas);
        int size = this.mItemDecorations.size();
        for (i = TOUCH_SLOP_DEFAULT; i < size; i += VERTICAL) {
            ((ItemDecoration) this.mItemDecorations.get(i)).onDrawOver(canvas, this, this.mState);
        }
        if (this.mLeftGlow == null || this.mLeftGlow.isFinished()) {
            i2 = TOUCH_SLOP_DEFAULT;
        } else {
            i = canvas.save();
            i2 = this.mClipToPadding ? getPaddingBottom() : TOUCH_SLOP_DEFAULT;
            canvas.rotate(BitmapDescriptorFactory.HUE_VIOLET);
            canvas.translate((float) (i2 + (-getHeight())), 0.0f);
            i2 = (this.mLeftGlow == null || !this.mLeftGlow.draw(canvas)) ? TOUCH_SLOP_DEFAULT : VERTICAL;
            canvas.restoreToCount(i);
        }
        if (!(this.mTopGlow == null || this.mTopGlow.isFinished())) {
            size = canvas.save();
            if (this.mClipToPadding) {
                canvas.translate((float) getPaddingLeft(), (float) getPaddingTop());
            }
            canvas.translate(0.0f, (float) this.topGlowOffset);
            i = (this.mTopGlow == null || !this.mTopGlow.draw(canvas)) ? TOUCH_SLOP_DEFAULT : VERTICAL;
            i2 |= i;
            canvas.restoreToCount(size);
        }
        if (!(this.mRightGlow == null || this.mRightGlow.isFinished())) {
            size = canvas.save();
            int width = getWidth();
            i = this.mClipToPadding ? getPaddingTop() : TOUCH_SLOP_DEFAULT;
            canvas.rotate(90.0f);
            canvas.translate((float) (-i), (float) (-width));
            i = (this.mRightGlow == null || !this.mRightGlow.draw(canvas)) ? TOUCH_SLOP_DEFAULT : VERTICAL;
            i2 |= i;
            canvas.restoreToCount(size);
        }
        if (!(this.mBottomGlow == null || this.mBottomGlow.isFinished())) {
            i = canvas.save();
            canvas.rotate(BitmapDescriptorFactory.HUE_CYAN);
            if (this.mClipToPadding) {
                canvas.translate((float) ((-getWidth()) + getPaddingRight()), (float) ((-getHeight()) + getPaddingBottom()));
            } else {
                canvas.translate((float) (-getWidth()), (float) (-getHeight()));
            }
            if (this.mBottomGlow != null && this.mBottomGlow.draw(canvas)) {
                i4 = VERTICAL;
            }
            i2 |= i4;
            canvas.restoreToCount(i);
        }
        if (i2 != 0 || this.mItemAnimator == null || this.mItemDecorations.size() <= 0 || !this.mItemAnimator.isRunning()) {
            i3 = i2;
        }
        if (i3 != 0) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean drawChild(Canvas canvas, View view, long j) {
        return super.drawChild(canvas, view, j);
    }

    void eatRequestLayout() {
        this.mEatRequestLayout += VERTICAL;
        if (this.mEatRequestLayout == VERTICAL && !this.mLayoutFrozen) {
            this.mLayoutRequestEaten = FORCE_INVALIDATE_DISPLAY_LIST;
        }
    }

    void ensureBottomGlow() {
        if (this.mBottomGlow == null) {
            this.mBottomGlow = new EdgeEffectCompat(getContext());
            if (this.mClipToPadding) {
                this.mBottomGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
            applyEdgeEffectColor(this.mBottomGlow);
        }
    }

    void ensureLeftGlow() {
        if (this.mLeftGlow == null) {
            this.mLeftGlow = new EdgeEffectCompat(getContext());
            if (this.mClipToPadding) {
                this.mLeftGlow.setSize((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
            } else {
                this.mLeftGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
            }
            applyEdgeEffectColor(this.mLeftGlow);
        }
    }

    void ensureRightGlow() {
        if (this.mRightGlow == null) {
            this.mRightGlow = new EdgeEffectCompat(getContext());
            if (this.mClipToPadding) {
                this.mRightGlow.setSize((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight());
            } else {
                this.mRightGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
            }
            applyEdgeEffectColor(this.mRightGlow);
        }
    }

    void ensureTopGlow() {
        if (this.mTopGlow == null) {
            this.mTopGlow = new EdgeEffectCompat(getContext());
            if (this.mClipToPadding) {
                this.mTopGlow.setSize((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom());
            } else {
                this.mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
            }
            applyEdgeEffectColor(this.mTopGlow);
        }
    }

    public View findChildViewUnder(float f, float f2) {
        for (int childCount = this.mChildHelper.getChildCount() + NO_POSITION; childCount >= 0; childCount += NO_POSITION) {
            View childAt = this.mChildHelper.getChildAt(childCount);
            float translationX = ViewCompat.getTranslationX(childAt);
            float translationY = ViewCompat.getTranslationY(childAt);
            if (f >= ((float) childAt.getLeft()) + translationX && f <= translationX + ((float) childAt.getRight()) && f2 >= ((float) childAt.getTop()) + translationY && f2 <= ((float) childAt.getBottom()) + translationY) {
                return childAt;
            }
        }
        return null;
    }

    @Nullable
    public View findContainingItemView(View view) {
        RecyclerView parent = view.getParent();
        View view2 = view;
        while (parent != null && parent != this && (parent instanceof View)) {
            View view3 = parent;
            view2 = view3;
            ViewParent parent2 = view3.getParent();
        }
        return parent == this ? view2 : null;
    }

    @Nullable
    public ViewHolder findContainingViewHolder(View view) {
        View findContainingItemView = findContainingItemView(view);
        return findContainingItemView == null ? null : getChildViewHolder(findContainingItemView);
    }

    public ViewHolder findViewHolderForAdapterPosition(int i) {
        if (this.mDataSetHasChangedAfterLayout) {
            return null;
        }
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i2 = TOUCH_SLOP_DEFAULT; i2 < unfilteredChildCount; i2 += VERTICAL) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i2));
            if (childViewHolderInt != null && !childViewHolderInt.isRemoved() && getAdapterPositionFor(childViewHolderInt) == i) {
                return childViewHolderInt;
            }
        }
        return null;
    }

    public ViewHolder findViewHolderForItemId(long j) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = TOUCH_SLOP_DEFAULT; i < unfilteredChildCount; i += VERTICAL) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (childViewHolderInt != null && childViewHolderInt.getItemId() == j) {
                return childViewHolderInt;
            }
        }
        return null;
    }

    public ViewHolder findViewHolderForLayoutPosition(int i) {
        return findViewHolderForPosition(i, FORCE_INVALIDATE_DISPLAY_LIST);
    }

    @Deprecated
    public ViewHolder findViewHolderForPosition(int i) {
        return findViewHolderForPosition(i, FORCE_INVALIDATE_DISPLAY_LIST);
    }

    ViewHolder findViewHolderForPosition(int i, boolean z) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i2 = TOUCH_SLOP_DEFAULT; i2 < unfilteredChildCount; i2 += VERTICAL) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i2));
            if (!(childViewHolderInt == null || childViewHolderInt.isRemoved())) {
                if (z) {
                    if (childViewHolderInt.mPosition == i) {
                        return childViewHolderInt;
                    }
                } else if (childViewHolderInt.getLayoutPosition() == i) {
                    return childViewHolderInt;
                }
            }
        }
        return null;
    }

    public boolean fling(int i, int i2) {
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return FORCE_INVALIDATE_DISPLAY_LIST;
        } else if (this.mLayoutFrozen) {
            return FORCE_INVALIDATE_DISPLAY_LIST;
        } else {
            boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
            boolean canScrollVertically = this.mLayout.canScrollVertically();
            if (!canScrollHorizontally || Math.abs(i) < this.mMinFlingVelocity) {
                i = TOUCH_SLOP_DEFAULT;
            }
            if (!canScrollVertically || Math.abs(i2) < this.mMinFlingVelocity) {
                i2 = TOUCH_SLOP_DEFAULT;
            }
            if ((i == 0 && i2 == 0) || dispatchNestedPreFling((float) i, (float) i2)) {
                return FORCE_INVALIDATE_DISPLAY_LIST;
            }
            canScrollHorizontally = (canScrollHorizontally || canScrollVertically) ? true : FORCE_INVALIDATE_DISPLAY_LIST;
            dispatchNestedFling((float) i, (float) i2, canScrollHorizontally);
            if (!canScrollHorizontally) {
                return FORCE_INVALIDATE_DISPLAY_LIST;
            }
            this.mViewFlinger.fling(Math.max(-this.mMaxFlingVelocity, Math.min(i, this.mMaxFlingVelocity)), Math.max(-this.mMaxFlingVelocity, Math.min(i2, this.mMaxFlingVelocity)));
            return true;
        }
    }

    public View focusSearch(View view, int i) {
        View onInterceptFocusSearch = this.mLayout.onInterceptFocusSearch(view, i);
        if (onInterceptFocusSearch != null) {
            return onInterceptFocusSearch;
        }
        onInterceptFocusSearch = FocusFinder.getInstance().findNextFocus(this, view, i);
        if (!(onInterceptFocusSearch != null || this.mAdapter == null || this.mLayout == null || isComputingLayout() || this.mLayoutFrozen)) {
            eatRequestLayout();
            onInterceptFocusSearch = this.mLayout.onFocusSearchFailed(view, i, this.mRecycler, this.mState);
            resumeRequestLayout(FORCE_INVALIDATE_DISPLAY_LIST);
        }
        return onInterceptFocusSearch == null ? super.focusSearch(view, i) : onInterceptFocusSearch;
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        if (this.mLayout != null) {
            return this.mLayout.generateDefaultLayoutParams();
        }
        throw new IllegalStateException("RecyclerView has no LayoutManager");
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        if (this.mLayout != null) {
            return this.mLayout.generateLayoutParams(getContext(), attributeSet);
        }
        throw new IllegalStateException("RecyclerView has no LayoutManager");
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (this.mLayout != null) {
            return this.mLayout.generateLayoutParams(layoutParams);
        }
        throw new IllegalStateException("RecyclerView has no LayoutManager");
    }

    public Adapter getAdapter() {
        return this.mAdapter;
    }

    public int getBaseline() {
        return this.mLayout != null ? this.mLayout.getBaseline() : super.getBaseline();
    }

    long getChangedHolderKey(ViewHolder viewHolder) {
        return this.mAdapter.hasStableIds() ? viewHolder.getItemId() : (long) viewHolder.mPosition;
    }

    public int getChildAdapterPosition(View view) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        return childViewHolderInt != null ? childViewHolderInt.getAdapterPosition() : NO_POSITION;
    }

    protected int getChildDrawingOrder(int i, int i2) {
        return this.mChildDrawingOrderCallback == null ? super.getChildDrawingOrder(i, i2) : this.mChildDrawingOrderCallback.onGetChildDrawingOrder(i, i2);
    }

    public long getChildItemId(View view) {
        if (this.mAdapter == null || !this.mAdapter.hasStableIds()) {
            return NO_ID;
        }
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        return childViewHolderInt != null ? childViewHolderInt.getItemId() : NO_ID;
    }

    public int getChildLayoutPosition(View view) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        return childViewHolderInt != null ? childViewHolderInt.getLayoutPosition() : NO_POSITION;
    }

    @Deprecated
    public int getChildPosition(View view) {
        return getChildAdapterPosition(view);
    }

    public ViewHolder getChildViewHolder(View view) {
        Object parent = view.getParent();
        if (parent == null || parent == this) {
            return getChildViewHolderInt(view);
        }
        throw new IllegalArgumentException("View " + view + " is not a direct child of " + this);
    }

    public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public ItemAnimator getItemAnimator() {
        return this.mItemAnimator;
    }

    Rect getItemDecorInsetsForChild(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (!layoutParams.mInsetsDirty) {
            return layoutParams.mDecorInsets;
        }
        Rect rect = layoutParams.mDecorInsets;
        rect.set(TOUCH_SLOP_DEFAULT, TOUCH_SLOP_DEFAULT, TOUCH_SLOP_DEFAULT, TOUCH_SLOP_DEFAULT);
        int size = this.mItemDecorations.size();
        for (int i = TOUCH_SLOP_DEFAULT; i < size; i += VERTICAL) {
            this.mTempRect.set(TOUCH_SLOP_DEFAULT, TOUCH_SLOP_DEFAULT, TOUCH_SLOP_DEFAULT, TOUCH_SLOP_DEFAULT);
            ((ItemDecoration) this.mItemDecorations.get(i)).getItemOffsets(this.mTempRect, view, this, this.mState);
            rect.left += this.mTempRect.left;
            rect.top += this.mTempRect.top;
            rect.right += this.mTempRect.right;
            rect.bottom += this.mTempRect.bottom;
        }
        layoutParams.mInsetsDirty = FORCE_INVALIDATE_DISPLAY_LIST;
        return rect;
    }

    public LayoutManager getLayoutManager() {
        return this.mLayout;
    }

    public int getMaxFlingVelocity() {
        return this.mMaxFlingVelocity;
    }

    public int getMinFlingVelocity() {
        return this.mMinFlingVelocity;
    }

    public RecycledViewPool getRecycledViewPool() {
        return this.mRecycler.getRecycledViewPool();
    }

    public int getScrollState() {
        return this.mScrollState;
    }

    public boolean hasFixedSize() {
        return this.mHasFixedSize;
    }

    public boolean hasNestedScrollingParent() {
        return getScrollingChildHelper().hasNestedScrollingParent();
    }

    public boolean hasPendingAdapterUpdates() {
        return (!this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout || this.mAdapterHelper.hasPendingUpdates()) ? true : FORCE_INVALIDATE_DISPLAY_LIST;
    }

    void initAdapterManager() {
        this.mAdapterHelper = new AdapterHelper(new C08676());
    }

    void invalidateGlows() {
        this.mBottomGlow = null;
        this.mTopGlow = null;
        this.mRightGlow = null;
        this.mLeftGlow = null;
    }

    public void invalidateItemDecorations() {
        if (this.mItemDecorations.size() != 0) {
            if (this.mLayout != null) {
                this.mLayout.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
            }
            markItemDecorInsetsDirty();
            requestLayout();
        }
    }

    boolean isAccessibilityEnabled() {
        return (this.mAccessibilityManager == null || !this.mAccessibilityManager.isEnabled()) ? FORCE_INVALIDATE_DISPLAY_LIST : true;
    }

    public boolean isAnimating() {
        return (this.mItemAnimator == null || !this.mItemAnimator.isRunning()) ? FORCE_INVALIDATE_DISPLAY_LIST : true;
    }

    public boolean isAttachedToWindow() {
        return this.mIsAttached;
    }

    public boolean isComputingLayout() {
        return this.mLayoutOrScrollCounter > 0 ? true : FORCE_INVALIDATE_DISPLAY_LIST;
    }

    public boolean isLayoutFrozen() {
        return this.mLayoutFrozen;
    }

    public boolean isNestedScrollingEnabled() {
        return getScrollingChildHelper().isNestedScrollingEnabled();
    }

    void markItemDecorInsetsDirty() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = TOUCH_SLOP_DEFAULT; i < unfilteredChildCount; i += VERTICAL) {
            ((LayoutParams) this.mChildHelper.getUnfilteredChildAt(i).getLayoutParams()).mInsetsDirty = true;
        }
        this.mRecycler.markItemDecorInsetsDirty();
    }

    void markKnownViewsInvalid() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = TOUCH_SLOP_DEFAULT; i < unfilteredChildCount; i += VERTICAL) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!(childViewHolderInt == null || childViewHolderInt.shouldIgnore())) {
                childViewHolderInt.addFlags(6);
            }
        }
        markItemDecorInsetsDirty();
        this.mRecycler.markKnownViewsInvalid();
    }

    public void offsetChildrenHorizontal(int i) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i2 = TOUCH_SLOP_DEFAULT; i2 < childCount; i2 += VERTICAL) {
            this.mChildHelper.getChildAt(i2).offsetLeftAndRight(i);
        }
    }

    public void offsetChildrenVertical(int i) {
        int childCount = this.mChildHelper.getChildCount();
        for (int i2 = TOUCH_SLOP_DEFAULT; i2 < childCount; i2 += VERTICAL) {
            this.mChildHelper.getChildAt(i2).offsetTopAndBottom(i);
        }
    }

    void offsetPositionRecordsForInsert(int i, int i2) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i3 = TOUCH_SLOP_DEFAULT; i3 < unfilteredChildCount; i3 += VERTICAL) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i3));
            if (!(childViewHolderInt == null || childViewHolderInt.shouldIgnore() || childViewHolderInt.mPosition < i)) {
                childViewHolderInt.offsetPosition(i2, FORCE_INVALIDATE_DISPLAY_LIST);
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForInsert(i, i2);
        requestLayout();
    }

    void offsetPositionRecordsForMove(int i, int i2) {
        int i3;
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        int i4;
        int i5;
        if (i < i2) {
            i3 = NO_POSITION;
            i4 = i2;
            i5 = i;
        } else {
            i3 = VERTICAL;
            i4 = i;
            i5 = i2;
        }
        for (int i6 = TOUCH_SLOP_DEFAULT; i6 < unfilteredChildCount; i6 += VERTICAL) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i6));
            if (childViewHolderInt != null && childViewHolderInt.mPosition >= r3 && childViewHolderInt.mPosition <= r2) {
                if (childViewHolderInt.mPosition == i) {
                    childViewHolderInt.offsetPosition(i2 - i, FORCE_INVALIDATE_DISPLAY_LIST);
                } else {
                    childViewHolderInt.offsetPosition(i3, FORCE_INVALIDATE_DISPLAY_LIST);
                }
                this.mState.mStructureChanged = true;
            }
        }
        this.mRecycler.offsetPositionRecordsForMove(i, i2);
        requestLayout();
    }

    void offsetPositionRecordsForRemove(int i, int i2, boolean z) {
        int i3 = i + i2;
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i4 = TOUCH_SLOP_DEFAULT; i4 < unfilteredChildCount; i4 += VERTICAL) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i4));
            if (!(childViewHolderInt == null || childViewHolderInt.shouldIgnore())) {
                if (childViewHolderInt.mPosition >= i3) {
                    childViewHolderInt.offsetPosition(-i2, z);
                    this.mState.mStructureChanged = true;
                } else if (childViewHolderInt.mPosition >= i) {
                    childViewHolderInt.flagRemovedAndOffsetPosition(i + NO_POSITION, -i2, z);
                    this.mState.mStructureChanged = true;
                }
            }
        }
        this.mRecycler.offsetPositionRecordsForRemove(i, i2, z);
        requestLayout();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLayoutOrScrollCounter = TOUCH_SLOP_DEFAULT;
        this.mIsAttached = true;
        this.mFirstLayoutComplete = FORCE_INVALIDATE_DISPLAY_LIST;
        if (this.mLayout != null) {
            this.mLayout.dispatchAttachedToWindow(this);
        }
        this.mPostedAnimatorRunner = FORCE_INVALIDATE_DISPLAY_LIST;
    }

    public void onChildAttachedToWindow(View view) {
    }

    public void onChildDetachedFromWindow(View view) {
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mItemAnimator != null) {
            this.mItemAnimator.endAnimations();
        }
        this.mFirstLayoutComplete = FORCE_INVALIDATE_DISPLAY_LIST;
        stopScroll();
        this.mIsAttached = FORCE_INVALIDATE_DISPLAY_LIST;
        if (this.mLayout != null) {
            this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
        }
        removeCallbacks(this.mItemAnimatorRunner);
        this.mViewInfoStore.onDetach();
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = this.mItemDecorations.size();
        for (int i = TOUCH_SLOP_DEFAULT; i < size; i += VERTICAL) {
            ((ItemDecoration) this.mItemDecorations.get(i)).onDraw(canvas, this, this.mState);
        }
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        if (!(this.mLayout == null || this.mLayoutFrozen || (MotionEventCompat.getSource(motionEvent) & SCROLL_STATE_SETTLING) == 0 || motionEvent.getAction() != 8)) {
            float f = this.mLayout.canScrollVertically() ? -MotionEventCompat.getAxisValue(motionEvent, 9) : 0.0f;
            float axisValue = this.mLayout.canScrollHorizontally() ? MotionEventCompat.getAxisValue(motionEvent, 10) : 0.0f;
            if (!(f == 0.0f && axisValue == 0.0f)) {
                float scrollFactor = getScrollFactor();
                scrollByInternal((int) (axisValue * scrollFactor), (int) (f * scrollFactor), motionEvent);
            }
        }
        return FORCE_INVALIDATE_DISPLAY_LIST;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int i = NO_POSITION;
        boolean z = true;
        if (this.mLayoutFrozen) {
            return FORCE_INVALIDATE_DISPLAY_LIST;
        }
        if (dispatchOnItemTouchIntercept(motionEvent)) {
            cancelTouch();
            return true;
        } else if (this.mLayout == null) {
            return FORCE_INVALIDATE_DISPLAY_LIST;
        } else {
            boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
            boolean canScrollVertically = this.mLayout.canScrollVertically();
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
            int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
            int i2;
            switch (actionMasked) {
                case TOUCH_SLOP_DEFAULT /*0*/:
                    if (this.mIgnoreMotionEventTillDown) {
                        this.mIgnoreMotionEventTillDown = FORCE_INVALIDATE_DISPLAY_LIST;
                    }
                    this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, TOUCH_SLOP_DEFAULT);
                    i = (int) (motionEvent.getX() + 0.5f);
                    this.mLastTouchX = i;
                    this.mInitialTouchX = i;
                    i = (int) (motionEvent.getY() + 0.5f);
                    this.mLastTouchY = i;
                    this.mInitialTouchY = i;
                    if (this.mScrollState == SCROLL_STATE_SETTLING) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        setScrollState(VERTICAL);
                    }
                    int[] iArr = this.mNestedOffsets;
                    this.mNestedOffsets[VERTICAL] = TOUCH_SLOP_DEFAULT;
                    iArr[TOUCH_SLOP_DEFAULT] = TOUCH_SLOP_DEFAULT;
                    i2 = canScrollHorizontally ? VERTICAL : TOUCH_SLOP_DEFAULT;
                    if (canScrollVertically) {
                        i2 |= SCROLL_STATE_SETTLING;
                    }
                    startNestedScroll(i2);
                    break;
                case VERTICAL /*1*/:
                    this.mVelocityTracker.clear();
                    stopNestedScroll();
                    break;
                case SCROLL_STATE_SETTLING /*2*/:
                    actionMasked = MotionEventCompat.findPointerIndex(motionEvent, this.mScrollPointerId);
                    if (actionMasked >= 0) {
                        actionIndex = (int) (MotionEventCompat.getX(motionEvent, actionMasked) + 0.5f);
                        actionMasked = (int) (MotionEventCompat.getY(motionEvent, actionMasked) + 0.5f);
                        if (this.mScrollState != VERTICAL) {
                            actionIndex -= this.mInitialTouchX;
                            actionMasked -= this.mInitialTouchY;
                            if (!canScrollHorizontally || Math.abs(actionIndex) <= this.mTouchSlop) {
                                canScrollHorizontally = FORCE_INVALIDATE_DISPLAY_LIST;
                            } else {
                                this.mLastTouchX = ((actionIndex < 0 ? NO_POSITION : VERTICAL) * this.mTouchSlop) + this.mInitialTouchX;
                                canScrollHorizontally = true;
                            }
                            if (canScrollVertically && Math.abs(actionMasked) > this.mTouchSlop) {
                                i2 = this.mInitialTouchY;
                                int i3 = this.mTouchSlop;
                                if (actionMasked >= 0) {
                                    i = VERTICAL;
                                }
                                this.mLastTouchY = i2 + (i * i3);
                                canScrollHorizontally = true;
                            }
                            if (canScrollHorizontally) {
                                setScrollState(VERTICAL);
                                break;
                            }
                        }
                    }
                    Log.e(TAG, "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return FORCE_INVALIDATE_DISPLAY_LIST;
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    cancelTouch();
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                    i2 = (int) (MotionEventCompat.getX(motionEvent, actionIndex) + 0.5f);
                    this.mLastTouchX = i2;
                    this.mInitialTouchX = i2;
                    i2 = (int) (MotionEventCompat.getY(motionEvent, actionIndex) + 0.5f);
                    this.mLastTouchY = i2;
                    this.mInitialTouchY = i2;
                    break;
                case Method.TRACE /*6*/:
                    onPointerUp(motionEvent);
                    break;
            }
            if (this.mScrollState != VERTICAL) {
                z = FORCE_INVALIDATE_DISPLAY_LIST;
            }
            return z;
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        TraceCompat.beginSection(TRACE_ON_LAYOUT_TAG);
        dispatchLayout();
        TraceCompat.endSection();
        this.mFirstLayoutComplete = true;
    }

    protected void onMeasure(int i, int i2) {
        boolean z = FORCE_INVALIDATE_DISPLAY_LIST;
        if (this.mLayout == null) {
            defaultOnMeasure(i, i2);
        } else if (this.mLayout.mAutoMeasure) {
            int mode = MeasureSpec.getMode(i);
            int mode2 = MeasureSpec.getMode(i2);
            if (mode == C0700C.ENCODING_PCM_32BIT && mode2 == C0700C.ENCODING_PCM_32BIT) {
                z = true;
            }
            this.mLayout.onMeasure(this.mRecycler, this.mState, i, i2);
            if (!z && this.mAdapter != null) {
                if (this.mState.mLayoutStep == VERTICAL) {
                    dispatchLayoutStep1();
                }
                this.mLayout.setMeasureSpecs(i, i2);
                this.mState.mIsMeasuring = true;
                dispatchLayoutStep2();
                this.mLayout.setMeasuredDimensionFromChildren(i, i2);
                if (this.mLayout.shouldMeasureTwice()) {
                    this.mLayout.setMeasureSpecs(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0700C.ENCODING_PCM_32BIT));
                    this.mState.mIsMeasuring = true;
                    dispatchLayoutStep2();
                    this.mLayout.setMeasuredDimensionFromChildren(i, i2);
                }
            }
        } else if (this.mHasFixedSize) {
            this.mLayout.onMeasure(this.mRecycler, this.mState, i, i2);
        } else {
            if (this.mAdapterUpdateDuringMeasure) {
                eatRequestLayout();
                processAdapterUpdatesAndSetAnimationFlags();
                if (this.mState.mRunPredictiveAnimations) {
                    this.mState.mInPreLayout = true;
                } else {
                    this.mAdapterHelper.consumeUpdatesInOnePass();
                    this.mState.mInPreLayout = FORCE_INVALIDATE_DISPLAY_LIST;
                }
                this.mAdapterUpdateDuringMeasure = FORCE_INVALIDATE_DISPLAY_LIST;
                resumeRequestLayout(FORCE_INVALIDATE_DISPLAY_LIST);
            }
            if (this.mAdapter != null) {
                this.mState.mItemCount = this.mAdapter.getItemCount();
            } else {
                this.mState.mItemCount = TOUCH_SLOP_DEFAULT;
            }
            eatRequestLayout();
            this.mLayout.onMeasure(this.mRecycler, this.mState, i, i2);
            resumeRequestLayout(FORCE_INVALIDATE_DISPLAY_LIST);
            this.mState.mInPreLayout = FORCE_INVALIDATE_DISPLAY_LIST;
        }
    }

    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            this.mPendingSavedState = (SavedState) parcelable;
            super.onRestoreInstanceState(this.mPendingSavedState.getSuperState());
            if (this.mLayout != null && this.mPendingSavedState.mLayoutState != null) {
                this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState);
                return;
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (this.mPendingSavedState != null) {
            savedState.copyFrom(this.mPendingSavedState);
        } else if (this.mLayout != null) {
            savedState.mLayoutState = this.mLayout.onSaveInstanceState();
        } else {
            savedState.mLayoutState = null;
        }
        return savedState;
    }

    public void onScrollStateChanged(int i) {
    }

    public void onScrolled(int i, int i2) {
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 || i2 != i4) {
            invalidateGlows();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = FORCE_INVALIDATE_DISPLAY_LIST;
        if (this.mLayoutFrozen || this.mIgnoreMotionEventTillDown) {
            return FORCE_INVALIDATE_DISPLAY_LIST;
        }
        if (dispatchOnItemTouch(motionEvent)) {
            cancelTouch();
            return true;
        } else if (this.mLayout == null) {
            return FORCE_INVALIDATE_DISPLAY_LIST;
        } else {
            boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
            boolean canScrollVertically = this.mLayout.canScrollVertically();
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            MotionEvent obtain = MotionEvent.obtain(motionEvent);
            int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
            int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
            if (actionMasked == 0) {
                int[] iArr = this.mNestedOffsets;
                this.mNestedOffsets[VERTICAL] = TOUCH_SLOP_DEFAULT;
                iArr[TOUCH_SLOP_DEFAULT] = TOUCH_SLOP_DEFAULT;
            }
            obtain.offsetLocation((float) this.mNestedOffsets[TOUCH_SLOP_DEFAULT], (float) this.mNestedOffsets[VERTICAL]);
            switch (actionMasked) {
                case TOUCH_SLOP_DEFAULT /*0*/:
                    this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, TOUCH_SLOP_DEFAULT);
                    actionMasked = (int) (motionEvent.getX() + 0.5f);
                    this.mLastTouchX = actionMasked;
                    this.mInitialTouchX = actionMasked;
                    actionMasked = (int) (motionEvent.getY() + 0.5f);
                    this.mLastTouchY = actionMasked;
                    this.mInitialTouchY = actionMasked;
                    actionMasked = canScrollHorizontally ? VERTICAL : TOUCH_SLOP_DEFAULT;
                    if (canScrollVertically) {
                        actionMasked |= SCROLL_STATE_SETTLING;
                    }
                    startNestedScroll(actionMasked);
                    break;
                case VERTICAL /*1*/:
                    this.mVelocityTracker.addMovement(obtain);
                    this.mVelocityTracker.computeCurrentVelocity(PointerIconCompat.TYPE_DEFAULT, (float) this.mMaxFlingVelocity);
                    float f = canScrollHorizontally ? -VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mScrollPointerId) : 0.0f;
                    float f2 = canScrollVertically ? -VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mScrollPointerId) : 0.0f;
                    if ((f == 0.0f && f2 == 0.0f) || !fling((int) f, (int) f2)) {
                        setScrollState(TOUCH_SLOP_DEFAULT);
                    }
                    resetTouch();
                    z = true;
                    break;
                case SCROLL_STATE_SETTLING /*2*/:
                    actionMasked = MotionEventCompat.findPointerIndex(motionEvent, this.mScrollPointerId);
                    if (actionMasked >= 0) {
                        int x = (int) (MotionEventCompat.getX(motionEvent, actionMasked) + 0.5f);
                        int y = (int) (MotionEventCompat.getY(motionEvent, actionMasked) + 0.5f);
                        int i = this.mLastTouchX - x;
                        actionMasked = this.mLastTouchY - y;
                        if (dispatchNestedPreScroll(i, actionMasked, this.mScrollConsumed, this.mScrollOffset)) {
                            i -= this.mScrollConsumed[TOUCH_SLOP_DEFAULT];
                            actionMasked -= this.mScrollConsumed[VERTICAL];
                            obtain.offsetLocation((float) this.mScrollOffset[TOUCH_SLOP_DEFAULT], (float) this.mScrollOffset[VERTICAL]);
                            int[] iArr2 = this.mNestedOffsets;
                            iArr2[TOUCH_SLOP_DEFAULT] = iArr2[TOUCH_SLOP_DEFAULT] + this.mScrollOffset[TOUCH_SLOP_DEFAULT];
                            iArr2 = this.mNestedOffsets;
                            iArr2[VERTICAL] = iArr2[VERTICAL] + this.mScrollOffset[VERTICAL];
                        }
                        if (this.mScrollState != VERTICAL) {
                            boolean z2;
                            if (!canScrollHorizontally || Math.abs(i) <= this.mTouchSlop) {
                                z2 = FORCE_INVALIDATE_DISPLAY_LIST;
                            } else {
                                i = i > 0 ? i - this.mTouchSlop : i + this.mTouchSlop;
                                z2 = true;
                            }
                            if (canScrollVertically && Math.abs(actionMasked) > this.mTouchSlop) {
                                actionMasked = actionMasked > 0 ? actionMasked - this.mTouchSlop : actionMasked + this.mTouchSlop;
                                z2 = true;
                            }
                            if (z2) {
                                setScrollState(VERTICAL);
                            }
                        }
                        if (this.mScrollState == VERTICAL) {
                            this.mLastTouchX = x - this.mScrollOffset[TOUCH_SLOP_DEFAULT];
                            this.mLastTouchY = y - this.mScrollOffset[VERTICAL];
                            if (!canScrollHorizontally) {
                                i = TOUCH_SLOP_DEFAULT;
                            }
                            if (!canScrollVertically) {
                                actionMasked = TOUCH_SLOP_DEFAULT;
                            }
                            if (scrollByInternal(i, actionMasked, obtain)) {
                                getParent().requestDisallowInterceptTouchEvent(true);
                                break;
                            }
                        }
                    }
                    Log.e(TAG, "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return FORCE_INVALIDATE_DISPLAY_LIST;
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    cancelTouch();
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    this.mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                    actionMasked = (int) (MotionEventCompat.getX(motionEvent, actionIndex) + 0.5f);
                    this.mLastTouchX = actionMasked;
                    this.mInitialTouchX = actionMasked;
                    actionMasked = (int) (MotionEventCompat.getY(motionEvent, actionIndex) + 0.5f);
                    this.mLastTouchY = actionMasked;
                    this.mInitialTouchY = actionMasked;
                    break;
                case Method.TRACE /*6*/:
                    onPointerUp(motionEvent);
                    break;
            }
            if (!z) {
                this.mVelocityTracker.addMovement(obtain);
            }
            obtain.recycle();
            return true;
        }
    }

    protected void removeDetachedView(View view, boolean z) {
        ViewHolder childViewHolderInt = getChildViewHolderInt(view);
        if (childViewHolderInt != null) {
            if (childViewHolderInt.isTmpDetached()) {
                childViewHolderInt.clearTmpDetachFlag();
            } else if (!childViewHolderInt.shouldIgnore()) {
                throw new IllegalArgumentException("Called removeDetachedView with a view which is not flagged as tmp detached." + childViewHolderInt);
            }
        }
        dispatchChildDetached(view);
        super.removeDetachedView(view, z);
    }

    public void removeItemDecoration(ItemDecoration itemDecoration) {
        if (this.mLayout != null) {
            this.mLayout.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
        }
        this.mItemDecorations.remove(itemDecoration);
        if (this.mItemDecorations.isEmpty()) {
            setWillNotDraw(ViewCompat.getOverScrollMode(this) == SCROLL_STATE_SETTLING ? true : FORCE_INVALIDATE_DISPLAY_LIST);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    public void removeOnChildAttachStateChangeListener(OnChildAttachStateChangeListener onChildAttachStateChangeListener) {
        if (this.mOnChildAttachStateListeners != null) {
            this.mOnChildAttachStateListeners.remove(onChildAttachStateChangeListener);
        }
    }

    public void removeOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.mOnItemTouchListeners.remove(onItemTouchListener);
        if (this.mActiveOnItemTouchListener == onItemTouchListener) {
            this.mActiveOnItemTouchListener = null;
        }
    }

    public void removeOnScrollListener(OnScrollListener onScrollListener) {
        if (this.mScrollListeners != null) {
            this.mScrollListeners.remove(onScrollListener);
        }
    }

    public void requestChildFocus(View view, View view2) {
        if (!(this.mLayout.onRequestChildFocus(this, this.mState, view, view2) || view2 == null)) {
            this.mTempRect.set(TOUCH_SLOP_DEFAULT, TOUCH_SLOP_DEFAULT, view2.getWidth(), view2.getHeight());
            android.view.ViewGroup.LayoutParams layoutParams = view2.getLayoutParams();
            if (layoutParams instanceof LayoutParams) {
                LayoutParams layoutParams2 = (LayoutParams) layoutParams;
                if (!layoutParams2.mInsetsDirty) {
                    Rect rect = layoutParams2.mDecorInsets;
                    Rect rect2 = this.mTempRect;
                    rect2.left -= rect.left;
                    rect2 = this.mTempRect;
                    rect2.right += rect.right;
                    rect2 = this.mTempRect;
                    rect2.top -= rect.top;
                    rect2 = this.mTempRect;
                    rect2.bottom = rect.bottom + rect2.bottom;
                }
            }
            offsetDescendantRectToMyCoords(view2, this.mTempRect);
            offsetRectIntoDescendantCoords(view, this.mTempRect);
            requestChildRectangleOnScreen(view, this.mTempRect, !this.mFirstLayoutComplete ? true : FORCE_INVALIDATE_DISPLAY_LIST);
        }
        super.requestChildFocus(view, view2);
    }

    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        return this.mLayout.requestChildRectangleOnScreen(this, view, rect, z);
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        int size = this.mOnItemTouchListeners.size();
        for (int i = TOUCH_SLOP_DEFAULT; i < size; i += VERTICAL) {
            ((OnItemTouchListener) this.mOnItemTouchListeners.get(i)).onRequestDisallowInterceptTouchEvent(z);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public void requestLayout() {
        if (this.mEatRequestLayout != 0 || this.mLayoutFrozen) {
            this.mLayoutRequestEaten = true;
        } else {
            super.requestLayout();
        }
    }

    void resumeRequestLayout(boolean z) {
        if (this.mEatRequestLayout < VERTICAL) {
            this.mEatRequestLayout = VERTICAL;
        }
        if (!z) {
            this.mLayoutRequestEaten = FORCE_INVALIDATE_DISPLAY_LIST;
        }
        if (this.mEatRequestLayout == VERTICAL) {
            if (!(!z || !this.mLayoutRequestEaten || this.mLayoutFrozen || this.mLayout == null || this.mAdapter == null)) {
                dispatchLayout();
            }
            if (!this.mLayoutFrozen) {
                this.mLayoutRequestEaten = FORCE_INVALIDATE_DISPLAY_LIST;
            }
        }
        this.mEatRequestLayout += NO_POSITION;
    }

    void saveOldPositions() {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        for (int i = TOUCH_SLOP_DEFAULT; i < unfilteredChildCount; i += VERTICAL) {
            ViewHolder childViewHolderInt = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
            if (!childViewHolderInt.shouldIgnore()) {
                childViewHolderInt.saveOldPosition();
            }
        }
    }

    public void scrollBy(int i, int i2) {
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else if (!this.mLayoutFrozen) {
            boolean canScrollHorizontally = this.mLayout.canScrollHorizontally();
            boolean canScrollVertically = this.mLayout.canScrollVertically();
            if (canScrollHorizontally || canScrollVertically) {
                if (!canScrollHorizontally) {
                    i = TOUCH_SLOP_DEFAULT;
                }
                if (!canScrollVertically) {
                    i2 = TOUCH_SLOP_DEFAULT;
                }
                scrollByInternal(i, i2, null);
            }
        }
    }

    boolean scrollByInternal(int i, int i2, MotionEvent motionEvent) {
        int scrollHorizontallyBy;
        int i3;
        int i4;
        int i5;
        consumePendingUpdateOperations();
        if (this.mAdapter != null) {
            int scrollVerticallyBy;
            eatRequestLayout();
            onEnterLayoutOrScroll();
            TraceCompat.beginSection(TRACE_SCROLL_TAG);
            if (i != 0) {
                scrollHorizontallyBy = this.mLayout.scrollHorizontallyBy(i, this.mRecycler, this.mState);
                i3 = i - scrollHorizontallyBy;
            } else {
                scrollHorizontallyBy = TOUCH_SLOP_DEFAULT;
                i3 = TOUCH_SLOP_DEFAULT;
            }
            if (i2 != 0) {
                scrollVerticallyBy = this.mLayout.scrollVerticallyBy(i2, this.mRecycler, this.mState);
                i4 = i2 - scrollVerticallyBy;
            } else {
                scrollVerticallyBy = TOUCH_SLOP_DEFAULT;
                i4 = TOUCH_SLOP_DEFAULT;
            }
            TraceCompat.endSection();
            repositionShadowingViews();
            onExitLayoutOrScroll();
            resumeRequestLayout(FORCE_INVALIDATE_DISPLAY_LIST);
            i5 = i4;
            i4 = scrollHorizontallyBy;
            scrollHorizontallyBy = scrollVerticallyBy;
        } else {
            scrollHorizontallyBy = TOUCH_SLOP_DEFAULT;
            i4 = TOUCH_SLOP_DEFAULT;
            i5 = TOUCH_SLOP_DEFAULT;
            i3 = TOUCH_SLOP_DEFAULT;
        }
        if (!this.mItemDecorations.isEmpty()) {
            invalidate();
        }
        if (dispatchNestedScroll(i4, scrollHorizontallyBy, i3, i5, this.mScrollOffset)) {
            this.mLastTouchX -= this.mScrollOffset[TOUCH_SLOP_DEFAULT];
            this.mLastTouchY -= this.mScrollOffset[VERTICAL];
            if (motionEvent != null) {
                motionEvent.offsetLocation((float) this.mScrollOffset[TOUCH_SLOP_DEFAULT], (float) this.mScrollOffset[VERTICAL]);
            }
            int[] iArr = this.mNestedOffsets;
            iArr[TOUCH_SLOP_DEFAULT] = iArr[TOUCH_SLOP_DEFAULT] + this.mScrollOffset[TOUCH_SLOP_DEFAULT];
            iArr = this.mNestedOffsets;
            iArr[VERTICAL] = iArr[VERTICAL] + this.mScrollOffset[VERTICAL];
        } else if (ViewCompat.getOverScrollMode(this) != SCROLL_STATE_SETTLING) {
            if (motionEvent != null) {
                pullGlows(motionEvent.getX(), (float) i3, motionEvent.getY(), (float) i5);
            }
            considerReleasingGlowsOnScroll(i, i2);
        }
        if (!(i4 == 0 && scrollHorizontallyBy == 0)) {
            dispatchOnScrolled(i4, scrollHorizontallyBy);
        }
        if (!awakenScrollBars()) {
            invalidate();
        }
        return (i4 == 0 && scrollHorizontallyBy == 0) ? FORCE_INVALIDATE_DISPLAY_LIST : true;
    }

    public void scrollTo(int i, int i2) {
        Log.w(TAG, "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
    }

    public void scrollToPosition(int i) {
        if (!this.mLayoutFrozen) {
            stopScroll();
            if (this.mLayout == null) {
                Log.e(TAG, "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
                return;
            }
            this.mLayout.scrollToPosition(i);
            awakenScrollBars();
        }
    }

    public void sendAccessibilityEventUnchecked(AccessibilityEvent accessibilityEvent) {
        if (!shouldDeferAccessibilityEvent(accessibilityEvent)) {
            super.sendAccessibilityEventUnchecked(accessibilityEvent);
        }
    }

    public void setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate recyclerViewAccessibilityDelegate) {
        this.mAccessibilityDelegate = recyclerViewAccessibilityDelegate;
        ViewCompat.setAccessibilityDelegate(this, this.mAccessibilityDelegate);
    }

    public void setAdapter(Adapter adapter) {
        stopScroll();
        setLayoutFrozen(FORCE_INVALIDATE_DISPLAY_LIST);
        setAdapterInternal(adapter, FORCE_INVALIDATE_DISPLAY_LIST, true);
        requestLayout();
    }

    public void setChildDrawingOrderCallback(ChildDrawingOrderCallback childDrawingOrderCallback) {
        if (childDrawingOrderCallback != this.mChildDrawingOrderCallback) {
            this.mChildDrawingOrderCallback = childDrawingOrderCallback;
            setChildrenDrawingOrderEnabled(this.mChildDrawingOrderCallback != null ? true : FORCE_INVALIDATE_DISPLAY_LIST);
        }
    }

    public void setClipToPadding(boolean z) {
        if (z != this.mClipToPadding) {
            invalidateGlows();
        }
        this.mClipToPadding = z;
        super.setClipToPadding(z);
        if (this.mFirstLayoutComplete) {
            requestLayout();
        }
    }

    public void setGlowColor(int i) {
        this.glowColor = i;
    }

    public void setHasFixedSize(boolean z) {
        this.mHasFixedSize = z;
    }

    public void setItemAnimator(ItemAnimator itemAnimator) {
        if (this.mItemAnimator != null) {
            this.mItemAnimator.endAnimations();
            this.mItemAnimator.setListener(null);
        }
        this.mItemAnimator = itemAnimator;
        if (this.mItemAnimator != null) {
            this.mItemAnimator.setListener(this.mItemAnimatorListener);
        }
    }

    public void setItemViewCacheSize(int i) {
        this.mRecycler.setViewCacheSize(i);
    }

    public void setLayoutFrozen(boolean z) {
        if (z != this.mLayoutFrozen) {
            assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
            if (z) {
                long uptimeMillis = SystemClock.uptimeMillis();
                onTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, TOUCH_SLOP_DEFAULT));
                this.mLayoutFrozen = true;
                this.mIgnoreMotionEventTillDown = true;
                stopScroll();
                return;
            }
            this.mLayoutFrozen = FORCE_INVALIDATE_DISPLAY_LIST;
            if (!(!this.mLayoutRequestEaten || this.mLayout == null || this.mAdapter == null)) {
                requestLayout();
            }
            this.mLayoutRequestEaten = FORCE_INVALIDATE_DISPLAY_LIST;
        }
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        if (layoutManager != this.mLayout) {
            stopScroll();
            if (this.mLayout != null) {
                if (this.mIsAttached) {
                    this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
                }
                this.mLayout.setRecyclerView(null);
            }
            this.mRecycler.clear();
            this.mChildHelper.removeAllViewsUnfiltered();
            this.mLayout = layoutManager;
            if (layoutManager != null) {
                if (layoutManager.mRecyclerView != null) {
                    throw new IllegalArgumentException("LayoutManager " + layoutManager + " is already attached to a RecyclerView: " + layoutManager.mRecyclerView);
                }
                this.mLayout.setRecyclerView(this);
                if (this.mIsAttached) {
                    this.mLayout.dispatchAttachedToWindow(this);
                }
            }
            requestLayout();
        }
    }

    public void setNestedScrollingEnabled(boolean z) {
        getScrollingChildHelper().setNestedScrollingEnabled(z);
    }

    @Deprecated
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mScrollListener = onScrollListener;
    }

    public void setRecycledViewPool(RecycledViewPool recycledViewPool) {
        this.mRecycler.setRecycledViewPool(recycledViewPool);
    }

    public void setRecyclerListener(RecyclerListener recyclerListener) {
        this.mRecyclerListener = recyclerListener;
    }

    public void setScrollingTouchSlop(int i) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        switch (i) {
            case TOUCH_SLOP_DEFAULT /*0*/:
                break;
            case VERTICAL /*1*/:
                this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(viewConfiguration);
                return;
            default:
                Log.w(TAG, "setScrollingTouchSlop(): bad argument constant " + i + "; using default value");
                break;
        }
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    public void setTopGlowOffset(int i) {
        this.topGlowOffset = i;
    }

    public void setViewCacheExtension(ViewCacheExtension viewCacheExtension) {
        this.mRecycler.setViewCacheExtension(viewCacheExtension);
    }

    boolean shouldDeferAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int i = TOUCH_SLOP_DEFAULT;
        if (!isComputingLayout()) {
            return FORCE_INVALIDATE_DISPLAY_LIST;
        }
        int contentChangeTypes = accessibilityEvent != null ? AccessibilityEventCompat.getContentChangeTypes(accessibilityEvent) : TOUCH_SLOP_DEFAULT;
        if (contentChangeTypes != 0) {
            i = contentChangeTypes;
        }
        this.mEatenAccessibilityChangeFlags = i | this.mEatenAccessibilityChangeFlags;
        return true;
    }

    public void smoothScrollBy(int i, int i2) {
        int i3 = TOUCH_SLOP_DEFAULT;
        if (this.mLayout == null) {
            Log.e(TAG, "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else if (!this.mLayoutFrozen) {
            if (!this.mLayout.canScrollHorizontally()) {
                i = TOUCH_SLOP_DEFAULT;
            }
            if (this.mLayout.canScrollVertically()) {
                i3 = i2;
            }
            if (i != 0 || i3 != 0) {
                this.mViewFlinger.smoothScrollBy(i, i3);
            }
        }
    }

    public void smoothScrollToPosition(int i) {
        if (!this.mLayoutFrozen) {
            if (this.mLayout == null) {
                Log.e(TAG, "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            } else {
                this.mLayout.smoothScrollToPosition(this, this.mState, i);
            }
        }
    }

    public boolean startNestedScroll(int i) {
        return getScrollingChildHelper().startNestedScroll(i);
    }

    public void stopNestedScroll() {
        getScrollingChildHelper().stopNestedScroll();
    }

    public void stopScroll() {
        setScrollState(TOUCH_SLOP_DEFAULT);
        stopScrollersInternal();
    }

    public void swapAdapter(Adapter adapter, boolean z) {
        setLayoutFrozen(FORCE_INVALIDATE_DISPLAY_LIST);
        setAdapterInternal(adapter, true, z);
        setDataSetChangedAfterLayout();
        requestLayout();
    }

    void viewRangeUpdate(int i, int i2, Object obj) {
        int unfilteredChildCount = this.mChildHelper.getUnfilteredChildCount();
        int i3 = i + i2;
        for (int i4 = TOUCH_SLOP_DEFAULT; i4 < unfilteredChildCount; i4 += VERTICAL) {
            View unfilteredChildAt = this.mChildHelper.getUnfilteredChildAt(i4);
            ViewHolder childViewHolderInt = getChildViewHolderInt(unfilteredChildAt);
            if (childViewHolderInt != null && !childViewHolderInt.shouldIgnore() && childViewHolderInt.mPosition >= i && childViewHolderInt.mPosition < i3) {
                childViewHolderInt.addFlags(SCROLL_STATE_SETTLING);
                childViewHolderInt.addChangePayload(obj);
                ((LayoutParams) unfilteredChildAt.getLayoutParams()).mInsetsDirty = true;
            }
        }
        this.mRecycler.viewRangeUpdate(i, i2);
    }
}

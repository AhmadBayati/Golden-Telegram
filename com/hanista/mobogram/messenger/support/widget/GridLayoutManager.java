package com.hanista.mobogram.messenger.support.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Recycler;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.State;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.Arrays;

public class GridLayoutManager extends LinearLayoutManager {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_SPAN_COUNT = -1;
    private static final String TAG = "GridLayoutManager";
    int[] mCachedBorders;
    final Rect mDecorInsets;
    boolean mPendingSpanCountChange;
    final SparseIntArray mPreLayoutSpanIndexCache;
    final SparseIntArray mPreLayoutSpanSizeCache;
    View[] mSet;
    int mSpanCount;
    SpanSizeLookup mSpanSizeLookup;

    public static abstract class SpanSizeLookup {
        private boolean mCacheSpanIndices;
        final SparseIntArray mSpanIndexCache;

        public SpanSizeLookup() {
            this.mSpanIndexCache = new SparseIntArray();
            this.mCacheSpanIndices = GridLayoutManager.DEBUG;
        }

        int findReferenceIndexFromCache(int i) {
            int i2 = 0;
            int size = this.mSpanIndexCache.size() + GridLayoutManager.DEFAULT_SPAN_COUNT;
            while (i2 <= size) {
                int i3 = (i2 + size) >>> 1;
                if (this.mSpanIndexCache.keyAt(i3) < i) {
                    i2 = i3 + 1;
                } else {
                    size = i3 + GridLayoutManager.DEFAULT_SPAN_COUNT;
                }
            }
            size = i2 + GridLayoutManager.DEFAULT_SPAN_COUNT;
            return (size < 0 || size >= this.mSpanIndexCache.size()) ? GridLayoutManager.DEFAULT_SPAN_COUNT : this.mSpanIndexCache.keyAt(size);
        }

        int getCachedSpanIndex(int i, int i2) {
            if (!this.mCacheSpanIndices) {
                return getSpanIndex(i, i2);
            }
            int i3 = this.mSpanIndexCache.get(i, GridLayoutManager.DEFAULT_SPAN_COUNT);
            if (i3 != GridLayoutManager.DEFAULT_SPAN_COUNT) {
                return i3;
            }
            i3 = getSpanIndex(i, i2);
            this.mSpanIndexCache.put(i, i3);
            return i3;
        }

        public int getSpanGroupIndex(int i, int i2) {
            int spanSize = getSpanSize(i);
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            while (i3 < i) {
                int spanSize2 = getSpanSize(i3);
                i5 += spanSize2;
                if (i5 == i2) {
                    i4++;
                    spanSize2 = 0;
                } else if (i5 > i2) {
                    i4++;
                } else {
                    spanSize2 = i5;
                }
                i3++;
                i5 = spanSize2;
            }
            return i5 + spanSize > i2 ? i4 + 1 : i4;
        }

        public int getSpanIndex(int i, int i2) {
            int spanSize = getSpanSize(i);
            if (spanSize == i2) {
                return 0;
            }
            int findReferenceIndexFromCache;
            int spanSize2;
            int i3;
            if (this.mCacheSpanIndices && this.mSpanIndexCache.size() > 0) {
                findReferenceIndexFromCache = findReferenceIndexFromCache(i);
                if (findReferenceIndexFromCache >= 0) {
                    spanSize2 = this.mSpanIndexCache.get(findReferenceIndexFromCache) + getSpanSize(findReferenceIndexFromCache);
                    findReferenceIndexFromCache++;
                    i3 = findReferenceIndexFromCache;
                    while (i3 < i) {
                        findReferenceIndexFromCache = getSpanSize(i3);
                        spanSize2 += findReferenceIndexFromCache;
                        if (spanSize2 == i2) {
                            findReferenceIndexFromCache = 0;
                        } else if (spanSize2 <= i2) {
                            findReferenceIndexFromCache = spanSize2;
                        }
                        i3++;
                        spanSize2 = findReferenceIndexFromCache;
                    }
                    return spanSize2 + spanSize > i2 ? spanSize2 : 0;
                }
            }
            findReferenceIndexFromCache = 0;
            spanSize2 = 0;
            i3 = findReferenceIndexFromCache;
            while (i3 < i) {
                findReferenceIndexFromCache = getSpanSize(i3);
                spanSize2 += findReferenceIndexFromCache;
                if (spanSize2 == i2) {
                    findReferenceIndexFromCache = 0;
                } else if (spanSize2 <= i2) {
                    findReferenceIndexFromCache = spanSize2;
                }
                i3++;
                spanSize2 = findReferenceIndexFromCache;
            }
            if (spanSize2 + spanSize > i2) {
            }
        }

        public abstract int getSpanSize(int i);

        public void invalidateSpanIndexCache() {
            this.mSpanIndexCache.clear();
        }

        public boolean isSpanIndexCacheEnabled() {
            return this.mCacheSpanIndices;
        }

        public void setSpanIndexCacheEnabled(boolean z) {
            this.mCacheSpanIndices = z;
        }
    }

    public static final class DefaultSpanSizeLookup extends SpanSizeLookup {
        public int getSpanIndex(int i, int i2) {
            return i % i2;
        }

        public int getSpanSize(int i) {
            return 1;
        }
    }

    public static class LayoutParams extends com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams {
        public static final int INVALID_SPAN_ID = -1;
        private int mSpanIndex;
        private int mSpanSize;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.mSpanIndex = INVALID_SPAN_ID;
            this.mSpanSize = 0;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mSpanIndex = INVALID_SPAN_ID;
            this.mSpanSize = 0;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mSpanIndex = INVALID_SPAN_ID;
            this.mSpanSize = 0;
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.mSpanIndex = INVALID_SPAN_ID;
            this.mSpanSize = 0;
        }

        public LayoutParams(com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
            this.mSpanIndex = INVALID_SPAN_ID;
            this.mSpanSize = 0;
        }

        public int getSpanIndex() {
            return this.mSpanIndex;
        }

        public int getSpanSize() {
            return this.mSpanSize;
        }
    }

    public GridLayoutManager(Context context, int i) {
        super(context);
        this.mPendingSpanCountChange = DEBUG;
        this.mSpanCount = DEFAULT_SPAN_COUNT;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        setSpanCount(i);
    }

    public GridLayoutManager(Context context, int i, int i2, boolean z) {
        super(context, i2, z);
        this.mPendingSpanCountChange = DEBUG;
        this.mSpanCount = DEFAULT_SPAN_COUNT;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        setSpanCount(i);
    }

    public GridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mPendingSpanCountChange = DEBUG;
        this.mSpanCount = DEFAULT_SPAN_COUNT;
        this.mPreLayoutSpanSizeCache = new SparseIntArray();
        this.mPreLayoutSpanIndexCache = new SparseIntArray();
        this.mSpanSizeLookup = new DefaultSpanSizeLookup();
        this.mDecorInsets = new Rect();
        setSpanCount(LayoutManager.getProperties(context, attributeSet, i, i2).spanCount);
    }

    private void assignSpans(Recycler recycler, State state, int i, int i2, boolean z) {
        int i3;
        int i4;
        int i5;
        int i6;
        if (z) {
            i3 = 1;
            i4 = 0;
        } else {
            i4 = i + DEFAULT_SPAN_COUNT;
            i3 = DEFAULT_SPAN_COUNT;
            i = DEFAULT_SPAN_COUNT;
        }
        if (this.mOrientation == 1 && isLayoutRTL()) {
            i5 = this.mSpanCount + DEFAULT_SPAN_COUNT;
            i6 = DEFAULT_SPAN_COUNT;
        } else {
            i5 = 0;
            i6 = 1;
        }
        int i7 = i5;
        for (i5 = i4; i5 != i; i5 += i3) {
            View view = this.mSet[i5];
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.mSpanSize = getSpanSize(recycler, state, getPosition(view));
            if (i6 != DEFAULT_SPAN_COUNT || layoutParams.mSpanSize <= 1) {
                layoutParams.mSpanIndex = i7;
            } else {
                layoutParams.mSpanIndex = i7 - (layoutParams.mSpanSize + DEFAULT_SPAN_COUNT);
            }
            i7 += layoutParams.mSpanSize * i6;
        }
    }

    private void cachePreLayoutSpanMapping() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LayoutParams layoutParams = (LayoutParams) getChildAt(i).getLayoutParams();
            int viewLayoutPosition = layoutParams.getViewLayoutPosition();
            this.mPreLayoutSpanSizeCache.put(viewLayoutPosition, layoutParams.getSpanSize());
            this.mPreLayoutSpanIndexCache.put(viewLayoutPosition, layoutParams.getSpanIndex());
        }
    }

    private void calculateItemBorders(int i) {
        this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, i);
    }

    static int[] calculateItemBorders(int[] iArr, int i, int i2) {
        int i3 = 0;
        if (!(iArr != null && iArr.length == i + 1 && iArr[iArr.length + DEFAULT_SPAN_COUNT] == i2)) {
            iArr = new int[(i + 1)];
        }
        iArr[0] = 0;
        int i4 = i2 / i;
        int i5 = i2 % i;
        int i6 = 0;
        for (int i7 = 1; i7 <= i; i7++) {
            int i8;
            i3 += i5;
            if (i3 <= 0 || i - i3 >= i5) {
                i8 = i4;
            } else {
                i8 = i4 + 1;
                i3 -= i;
            }
            i6 += i8;
            iArr[i7] = i6;
        }
        return iArr;
    }

    private void clearPreLayoutSpanMappingCache() {
        this.mPreLayoutSpanSizeCache.clear();
        this.mPreLayoutSpanIndexCache.clear();
    }

    private void ensureAnchorIsInCorrectSpan(Recycler recycler, State state, AnchorInfo anchorInfo, int i) {
        Object obj = 1;
        if (i != 1) {
            obj = null;
        }
        int spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
        if (obj != null) {
            while (spanIndex > 0 && anchorInfo.mPosition > 0) {
                anchorInfo.mPosition += DEFAULT_SPAN_COUNT;
                spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
            }
            return;
        }
        int itemCount = state.getItemCount() + DEFAULT_SPAN_COUNT;
        int i2 = anchorInfo.mPosition;
        int i3 = spanIndex;
        while (i2 < itemCount) {
            spanIndex = getSpanIndex(recycler, state, i2 + 1);
            if (spanIndex <= i3) {
                break;
            }
            i2++;
            i3 = spanIndex;
        }
        anchorInfo.mPosition = i2;
    }

    private void ensureViewSet() {
        if (this.mSet == null || this.mSet.length != this.mSpanCount) {
            this.mSet = new View[this.mSpanCount];
        }
    }

    private int getSpanGroupIndex(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanGroupIndex(i, this.mSpanCount);
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != DEFAULT_SPAN_COUNT) {
            return this.mSpanSizeLookup.getSpanGroupIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        Log.w(TAG, "Cannot find span size for pre layout position. " + i);
        return 0;
    }

    private int getSpanIndex(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
        }
        int i2 = this.mPreLayoutSpanIndexCache.get(i, DEFAULT_SPAN_COUNT);
        if (i2 != DEFAULT_SPAN_COUNT) {
            return i2;
        }
        i2 = recycler.convertPreLayoutPositionToPostLayout(i);
        if (i2 != DEFAULT_SPAN_COUNT) {
            return this.mSpanSizeLookup.getCachedSpanIndex(i2, this.mSpanCount);
        }
        Log.w(TAG, "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + i);
        return 0;
    }

    private int getSpanSize(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanSize(i);
        }
        int i2 = this.mPreLayoutSpanSizeCache.get(i, DEFAULT_SPAN_COUNT);
        if (i2 != DEFAULT_SPAN_COUNT) {
            return i2;
        }
        i2 = recycler.convertPreLayoutPositionToPostLayout(i);
        if (i2 != DEFAULT_SPAN_COUNT) {
            return this.mSpanSizeLookup.getSpanSize(i2);
        }
        Log.w(TAG, "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + i);
        return 1;
    }

    private void guessMeasurement(float f, int i) {
        calculateItemBorders(Math.max(Math.round(((float) this.mSpanCount) * f), i));
    }

    private void measureChildWithDecorationsAndMargin(View view, int i, int i2, boolean z, boolean z2) {
        calculateItemDecorationsForChild(view, this.mDecorInsets);
        com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams layoutParams = (com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams) view.getLayoutParams();
        if (z || this.mOrientation == 1) {
            i = updateSpecWithExtra(i, layoutParams.leftMargin + this.mDecorInsets.left, layoutParams.rightMargin + this.mDecorInsets.right);
        }
        if (z || this.mOrientation == 0) {
            i2 = updateSpecWithExtra(i2, layoutParams.topMargin + this.mDecorInsets.top, layoutParams.bottomMargin + this.mDecorInsets.bottom);
        }
        if (z2 ? shouldReMeasureChild(view, i, i2, layoutParams) : shouldMeasureChild(view, i, i2, layoutParams)) {
            view.measure(i, i2);
        }
    }

    private void updateMeasurements() {
        calculateItemBorders(getOrientation() == 1 ? (getWidth() - getPaddingRight()) - getPaddingLeft() : (getHeight() - getPaddingBottom()) - getPaddingTop());
    }

    private int updateSpecWithExtra(int i, int i2, int i3) {
        if (i2 == 0 && i3 == 0) {
            return i;
        }
        int mode = MeasureSpec.getMode(i);
        return (mode == TLRPC.MESSAGE_FLAG_MEGAGROUP || mode == C0700C.ENCODING_PCM_32BIT) ? MeasureSpec.makeMeasureSpec(Math.max(0, (MeasureSpec.getSize(i) - i2) - i3), mode) : i;
    }

    public boolean checkLayoutParams(com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    View findReferenceChild(Recycler recycler, State state, int i, int i2, int i3) {
        View view = null;
        ensureLayoutState();
        int startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
        int endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
        int i4 = i2 > i ? 1 : DEFAULT_SPAN_COUNT;
        View view2 = null;
        while (i != i2) {
            View view3;
            View childAt = getChildAt(i);
            int position = getPosition(childAt);
            if (position >= 0 && position < i3) {
                if (getSpanIndex(recycler, state, position) != 0) {
                    view3 = view;
                    childAt = view2;
                } else if (((com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams) childAt.getLayoutParams()).isItemRemoved()) {
                    if (view2 == null) {
                        view3 = view;
                    }
                } else if (this.mOrientationHelper.getDecoratedStart(childAt) < endAfterPadding && this.mOrientationHelper.getDecoratedEnd(childAt) >= startAfterPadding) {
                    return childAt;
                } else {
                    if (view == null) {
                        view3 = childAt;
                        childAt = view2;
                    }
                }
                i += i4;
                view = view3;
                view2 = childAt;
            }
            view3 = view;
            childAt = view2;
            i += i4;
            view = view3;
            view2 = childAt;
        }
        if (view == null) {
            view = view2;
        }
        return view;
    }

    public com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return this.mOrientation == 0 ? new LayoutParams(-2, (int) DEFAULT_SPAN_COUNT) : new LayoutParams((int) DEFAULT_SPAN_COUNT, -2);
    }

    public com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
        return new LayoutParams(context, attributeSet);
    }

    public com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof MarginLayoutParams ? new LayoutParams((MarginLayoutParams) layoutParams) : new LayoutParams(layoutParams);
    }

    public int getColumnCountForAccessibility(Recycler recycler, State state) {
        return this.mOrientation == 1 ? this.mSpanCount : state.getItemCount() < 1 ? 0 : getSpanGroupIndex(recycler, state, state.getItemCount() + DEFAULT_SPAN_COUNT) + 1;
    }

    public int getRowCountForAccessibility(Recycler recycler, State state) {
        return this.mOrientation == 0 ? this.mSpanCount : state.getItemCount() < 1 ? 0 : getSpanGroupIndex(recycler, state, state.getItemCount() + DEFAULT_SPAN_COUNT) + 1;
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public SpanSizeLookup getSpanSizeLookup() {
        return this.mSpanSizeLookup;
    }

    void layoutChunk(Recycler recycler, State state, LayoutState layoutState, LayoutChunkResult layoutChunkResult) {
        int modeInOther = this.mOrientationHelper.getModeInOther();
        Object obj = modeInOther != 1073741824 ? 1 : null;
        int i = getChildCount() > 0 ? this.mCachedBorders[this.mSpanCount] : 0;
        if (obj != null) {
            updateMeasurements();
        }
        boolean z = layoutState.mItemDirection == 1 ? true : DEBUG;
        int i2 = 0;
        int i3 = 0;
        int i4 = this.mSpanCount;
        if (!z) {
            i4 = getSpanIndex(recycler, state, layoutState.mCurrentPosition) + getSpanSize(recycler, state, layoutState.mCurrentPosition);
        }
        while (i2 < this.mSpanCount && layoutState.hasMore(state) && i4 > 0) {
            int i5 = layoutState.mCurrentPosition;
            int spanSize = getSpanSize(recycler, state, i5);
            if (spanSize <= this.mSpanCount) {
                i4 -= spanSize;
                if (i4 >= 0) {
                    View next = layoutState.next(recycler);
                    if (next == null) {
                        break;
                    }
                    i3 += spanSize;
                    this.mSet[i2] = next;
                    i2++;
                } else {
                    break;
                }
            }
            throw new IllegalArgumentException("Item at position " + i5 + " requires " + spanSize + " spans but GridLayoutManager has only " + this.mSpanCount + " spans.");
        }
        if (i2 == 0) {
            layoutChunkResult.mFinished = true;
            return;
        }
        View view;
        int childMeasureSpec;
        int childMeasureSpec2;
        assignSpans(recycler, state, i2, i3, z);
        int i6 = 0;
        float f = 0.0f;
        spanSize = 0;
        while (i6 < i2) {
            View view2 = this.mSet[i6];
            if (layoutState.mScrapList == null) {
                if (z) {
                    addView(view2);
                } else {
                    addView(view2, 0);
                }
            } else if (z) {
                addDisappearingView(view2);
            } else {
                addDisappearingView(view2, 0);
            }
            LayoutParams layoutParams = (LayoutParams) view2.getLayoutParams();
            int childMeasureSpec3 = LayoutManager.getChildMeasureSpec(this.mCachedBorders[layoutParams.mSpanIndex + layoutParams.mSpanSize] - this.mCachedBorders[layoutParams.mSpanIndex], modeInOther, 0, this.mOrientation == 0 ? layoutParams.height : layoutParams.width, DEBUG);
            int childMeasureSpec4 = LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), this.mOrientationHelper.getMode(), 0, this.mOrientation == 1 ? layoutParams.height : layoutParams.width, true);
            if (this.mOrientation == 1) {
                measureChildWithDecorationsAndMargin(view2, childMeasureSpec3, childMeasureSpec4, layoutParams.height == DEFAULT_SPAN_COUNT ? true : DEBUG, DEBUG);
            } else {
                measureChildWithDecorationsAndMargin(view2, childMeasureSpec4, childMeasureSpec3, layoutParams.width == DEFAULT_SPAN_COUNT ? true : DEBUG, DEBUG);
            }
            i5 = this.mOrientationHelper.getDecoratedMeasurement(view2);
            if (i5 <= spanSize) {
                i5 = spanSize;
            }
            float decoratedMeasurementInOther = (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT * ((float) this.mOrientationHelper.getDecoratedMeasurementInOther(view2))) / ((float) layoutParams.mSpanSize);
            if (decoratedMeasurementInOther <= f) {
                decoratedMeasurementInOther = f;
            }
            i6++;
            f = decoratedMeasurementInOther;
            spanSize = i5;
        }
        if (obj != null) {
            guessMeasurement(f, i);
            spanSize = 0;
            int i7 = 0;
            while (i7 < i2) {
                view = this.mSet[i7];
                layoutParams = (LayoutParams) view.getLayoutParams();
                childMeasureSpec = LayoutManager.getChildMeasureSpec(this.mCachedBorders[layoutParams.mSpanIndex + layoutParams.mSpanSize] - this.mCachedBorders[layoutParams.mSpanIndex], C0700C.ENCODING_PCM_32BIT, 0, this.mOrientation == 0 ? layoutParams.height : layoutParams.width, DEBUG);
                childMeasureSpec2 = LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), this.mOrientationHelper.getMode(), 0, this.mOrientation == 1 ? layoutParams.height : layoutParams.width, true);
                if (this.mOrientation == 1) {
                    measureChildWithDecorationsAndMargin(view, childMeasureSpec, childMeasureSpec2, DEBUG, true);
                } else {
                    measureChildWithDecorationsAndMargin(view, childMeasureSpec2, childMeasureSpec, DEBUG, true);
                }
                i4 = this.mOrientationHelper.getDecoratedMeasurement(view);
                if (i4 <= spanSize) {
                    i4 = spanSize;
                }
                i7++;
                spanSize = i4;
            }
        }
        childMeasureSpec2 = MeasureSpec.makeMeasureSpec(spanSize, C0700C.ENCODING_PCM_32BIT);
        for (i5 = 0; i5 < i2; i5++) {
            view = this.mSet[i5];
            if (this.mOrientationHelper.getDecoratedMeasurement(view) != spanSize) {
                layoutParams = (LayoutParams) view.getLayoutParams();
                childMeasureSpec = LayoutManager.getChildMeasureSpec(this.mCachedBorders[layoutParams.mSpanIndex + layoutParams.mSpanSize] - this.mCachedBorders[layoutParams.mSpanIndex], C0700C.ENCODING_PCM_32BIT, 0, this.mOrientation == 0 ? layoutParams.height : layoutParams.width, DEBUG);
                if (this.mOrientation == 1) {
                    measureChildWithDecorationsAndMargin(view, childMeasureSpec, childMeasureSpec2, true, true);
                } else {
                    measureChildWithDecorationsAndMargin(view, childMeasureSpec2, childMeasureSpec, true, true);
                }
            }
        }
        layoutChunkResult.mConsumed = spanSize;
        i5 = 0;
        i4 = 0;
        if (this.mOrientation == 1) {
            if (layoutState.mLayoutDirection == DEFAULT_SPAN_COUNT) {
                i4 = layoutState.mOffset;
                i5 = i4 - spanSize;
                spanSize = 0;
                i3 = 0;
            } else {
                i5 = layoutState.mOffset;
                i4 = i5 + spanSize;
                spanSize = 0;
                i3 = 0;
            }
        } else if (layoutState.mLayoutDirection == DEFAULT_SPAN_COUNT) {
            i3 = layoutState.mOffset;
            int i8 = i3;
            i3 -= spanSize;
            spanSize = i8;
        } else {
            i3 = layoutState.mOffset;
            spanSize += i3;
        }
        int i9 = 0;
        childMeasureSpec = i3;
        i3 = spanSize;
        spanSize = i5;
        i5 = i4;
        while (i9 < i2) {
            int i10;
            int i11;
            view = this.mSet[i9];
            layoutParams = (LayoutParams) view.getLayoutParams();
            if (this.mOrientation != 1) {
                spanSize = this.mCachedBorders[layoutParams.mSpanIndex] + getPaddingTop();
                i5 = this.mOrientationHelper.getDecoratedMeasurementInOther(view) + spanSize;
                i10 = i3;
                i11 = childMeasureSpec;
            } else if (isLayoutRTL()) {
                i3 = getPaddingLeft() + this.mCachedBorders[layoutParams.mSpanIndex + layoutParams.mSpanSize];
                i10 = i3;
                i11 = i3 - this.mOrientationHelper.getDecoratedMeasurementInOther(view);
            } else {
                childMeasureSpec = this.mCachedBorders[layoutParams.mSpanIndex] + getPaddingLeft();
                i10 = this.mOrientationHelper.getDecoratedMeasurementInOther(view) + childMeasureSpec;
                i11 = childMeasureSpec;
            }
            layoutDecorated(view, i11 + layoutParams.leftMargin, spanSize + layoutParams.topMargin, i10 - layoutParams.rightMargin, i5 - layoutParams.bottomMargin);
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                layoutChunkResult.mIgnoreConsumed = true;
            }
            layoutChunkResult.mFocusable |= view.isFocusable();
            i9++;
            i3 = i10;
            childMeasureSpec = i11;
        }
        Arrays.fill(this.mSet, null);
    }

    void onAnchorReady(Recycler recycler, State state, AnchorInfo anchorInfo, int i) {
        super.onAnchorReady(recycler, state, anchorInfo, i);
        updateMeasurements();
        if (state.getItemCount() > 0 && !state.isPreLayout()) {
            ensureAnchorIsInCorrectSpan(recycler, state, anchorInfo, i);
        }
        ensureViewSet();
    }

    public View onFocusSearchFailed(View view, int i, Recycler recycler, State state) {
        View findContainingItemView = findContainingItemView(view);
        if (findContainingItemView == null) {
            return null;
        }
        LayoutParams layoutParams = (LayoutParams) findContainingItemView.getLayoutParams();
        int access$000 = layoutParams.mSpanIndex;
        int access$0002 = layoutParams.mSpanIndex + layoutParams.mSpanSize;
        if (super.onFocusSearchFailed(view, i, recycler, state) == null) {
            return null;
        }
        int childCount;
        int i2;
        int i3;
        if (((convertFocusDirectionToLayoutDirection(i) == 1 ? true : DEBUG) != this.mShouldReverseLayout ? 1 : null) != null) {
            childCount = getChildCount() + DEFAULT_SPAN_COUNT;
            i2 = DEFAULT_SPAN_COUNT;
            i3 = DEFAULT_SPAN_COUNT;
        } else {
            childCount = 0;
            i2 = 1;
            i3 = getChildCount();
        }
        Object obj = (this.mOrientation == 1 && isLayoutRTL()) ? 1 : null;
        View view2 = null;
        int i4 = DEFAULT_SPAN_COUNT;
        int i5 = 0;
        int i6 = childCount;
        while (i6 != i3) {
            View childAt = getChildAt(i6);
            if (childAt == findContainingItemView) {
                break;
            }
            View view3;
            if (childAt.isFocusable()) {
                layoutParams = (LayoutParams) childAt.getLayoutParams();
                int access$0003 = layoutParams.mSpanIndex;
                int access$0004 = layoutParams.mSpanIndex + layoutParams.mSpanSize;
                if (access$0003 == access$000 && access$0004 == access$0002) {
                    return childAt;
                }
                Object obj2 = null;
                if (view2 == null) {
                    obj2 = 1;
                } else {
                    int min = Math.min(access$0004, access$0002) - Math.max(access$0003, access$000);
                    if (min > i5) {
                        obj2 = 1;
                    } else if (min == i5) {
                        if (obj == (access$0003 > i4 ? 1 : null)) {
                            obj2 = 1;
                        }
                    }
                }
                if (obj2 != null) {
                    i5 = layoutParams.mSpanIndex;
                    childCount = Math.min(access$0004, access$0002) - Math.max(access$0003, access$000);
                    view3 = childAt;
                } else {
                    childCount = i5;
                    i5 = i4;
                    view3 = view2;
                }
            } else {
                childCount = i5;
                i5 = i4;
                view3 = view2;
            }
            i6 += i2;
            view2 = view3;
            i4 = i5;
            i5 = childCount;
        }
        return view2;
    }

    public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        android.view.ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof LayoutParams) {
            LayoutParams layoutParams2 = (LayoutParams) layoutParams;
            int spanGroupIndex = getSpanGroupIndex(recycler, state, layoutParams2.getViewLayoutPosition());
            if (this.mOrientation == 0) {
                int spanIndex = layoutParams2.getSpanIndex();
                int spanSize = layoutParams2.getSpanSize();
                boolean z = (this.mSpanCount <= 1 || layoutParams2.getSpanSize() != this.mSpanCount) ? DEBUG : true;
                accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(spanIndex, spanSize, spanGroupIndex, 1, z, DEBUG));
                return;
            }
            int spanIndex2 = layoutParams2.getSpanIndex();
            int spanSize2 = layoutParams2.getSpanSize();
            boolean z2 = (this.mSpanCount <= 1 || layoutParams2.getSpanSize() != this.mSpanCount) ? DEBUG : true;
            accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(spanGroupIndex, 1, spanIndex2, spanSize2, z2, DEBUG));
            return;
        }
        super.onInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
    }

    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        if (state.isPreLayout()) {
            cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);
        clearPreLayoutSpanMappingCache();
        if (!state.isPreLayout()) {
            this.mPendingSpanCountChange = DEBUG;
        }
    }

    public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollHorizontallyBy(i, recycler, state);
    }

    public int scrollVerticallyBy(int i, Recycler recycler, State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollVerticallyBy(i, recycler, state);
    }

    public void setMeasuredDimension(Rect rect, int i, int i2) {
        if (this.mCachedBorders == null) {
            super.setMeasuredDimension(rect, i, i2);
        }
        int paddingRight = getPaddingRight() + getPaddingLeft();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        if (this.mOrientation == 1) {
            paddingTop = LayoutManager.chooseSize(i2, paddingTop + rect.height(), getMinimumHeight());
            paddingRight = LayoutManager.chooseSize(i, paddingRight + this.mCachedBorders[this.mCachedBorders.length + DEFAULT_SPAN_COUNT], getMinimumWidth());
        } else {
            paddingRight = LayoutManager.chooseSize(i, paddingRight + rect.width(), getMinimumWidth());
            paddingTop = LayoutManager.chooseSize(i2, paddingTop + this.mCachedBorders[this.mCachedBorders.length + DEFAULT_SPAN_COUNT], getMinimumHeight());
        }
        setMeasuredDimension(paddingRight, paddingTop);
    }

    public void setSpanCount(int i) {
        if (i != this.mSpanCount) {
            this.mPendingSpanCountChange = true;
            if (i < 1) {
                throw new IllegalArgumentException("Span count should be at least 1. Provided " + i);
            }
            this.mSpanCount = i;
            this.mSpanSizeLookup.invalidateSpanIndexCache();
        }
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    public void setStackFromEnd(boolean z) {
        if (z) {
            throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
        }
        super.setStackFromEnd(DEBUG);
    }

    public boolean supportsPredictiveItemAnimations() {
        return (this.mPendingSavedState != null || this.mPendingSpanCountChange) ? DEBUG : true;
    }
}

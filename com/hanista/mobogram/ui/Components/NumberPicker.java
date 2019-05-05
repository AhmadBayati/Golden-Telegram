package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.ActionBar.Theme;
import java.util.Locale;

public class NumberPicker extends LinearLayout {
    private static final int DEFAULT_LAYOUT_RESOURCE_ID = 0;
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = 1;
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 3;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
    private Scroller mAdjustScroller;
    private int mBottomSelectionDividerBottom;
    private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
    private boolean mComputeMaxWidth;
    private int mCurrentScrollOffset;
    private boolean mDecrementVirtualButtonPressed;
    private String[] mDisplayedValues;
    private Scroller mFlingScroller;
    private Formatter mFormatter;
    private boolean mIncrementVirtualButtonPressed;
    private boolean mIngonreMoveEvents;
    private int mInitialScrollOffset;
    private TextView mInputText;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private int mLastHandledDownDpadKeyCode;
    private int mLastHoveredChildVirtualViewId;
    private long mLongPressUpdateInterval;
    private int mMaxHeight;
    private int mMaxValue;
    private int mMaxWidth;
    private int mMaximumFlingVelocity;
    private int mMinHeight;
    private int mMinValue;
    private int mMinWidth;
    private int mMinimumFlingVelocity;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private PressedStateHelper mPressedStateHelper;
    private int mPreviousScrollerY;
    private int mScrollState;
    private Drawable mSelectionDivider;
    private int mSelectionDividerHeight;
    private int mSelectionDividersDistance;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache;
    private final int[] mSelectorIndices;
    private int mSelectorTextGapHeight;
    private Paint mSelectorWheelPaint;
    private int mSolidColor;
    private int mTextSize;
    private int mTopSelectionDividerTop;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private Drawable mVirtualButtonPressedDrawable;
    private boolean mWrapSelectorWheel;

    public interface Formatter {
        String format(int i);
    }

    class ChangeCurrentByOneFromLongPressCommand implements Runnable {
        private boolean mIncrement;

        ChangeCurrentByOneFromLongPressCommand() {
        }

        private void setStep(boolean z) {
            this.mIncrement = z;
        }

        public void run() {
            NumberPicker.this.changeValueByOne(this.mIncrement);
            NumberPicker.this.postDelayed(this, NumberPicker.this.mLongPressUpdateInterval);
        }
    }

    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        void onScrollStateChange(NumberPicker numberPicker, int i);
    }

    public interface OnValueChangeListener {
        void onValueChange(NumberPicker numberPicker, int i, int i2);
    }

    class PressedStateHelper implements Runnable {
        public static final int BUTTON_DECREMENT = 2;
        public static final int BUTTON_INCREMENT = 1;
        private final int MODE_PRESS;
        private final int MODE_TAPPED;
        private int mManagedButton;
        private int mMode;

        PressedStateHelper() {
            this.MODE_PRESS = BUTTON_INCREMENT;
            this.MODE_TAPPED = BUTTON_DECREMENT;
        }

        public void buttonPressDelayed(int i) {
            cancel();
            this.mMode = BUTTON_INCREMENT;
            this.mManagedButton = i;
            NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getTapTimeout());
        }

        public void buttonTapped(int i) {
            cancel();
            this.mMode = BUTTON_DECREMENT;
            this.mManagedButton = i;
            NumberPicker.this.post(this);
        }

        public void cancel() {
            this.mMode = NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID;
            this.mManagedButton = NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID;
            NumberPicker.this.removeCallbacks(this);
            if (NumberPicker.this.mIncrementVirtualButtonPressed) {
                NumberPicker.this.mIncrementVirtualButtonPressed = false;
                NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
            }
            NumberPicker.this.mDecrementVirtualButtonPressed = false;
            if (NumberPicker.this.mDecrementVirtualButtonPressed) {
                NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
            }
        }

        public void run() {
            switch (this.mMode) {
                case BUTTON_INCREMENT /*1*/:
                    switch (this.mManagedButton) {
                        case BUTTON_INCREMENT /*1*/:
                            NumberPicker.this.mIncrementVirtualButtonPressed = true;
                            NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                        case BUTTON_DECREMENT /*2*/:
                            NumberPicker.this.mDecrementVirtualButtonPressed = true;
                            NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                        default:
                    }
                case BUTTON_DECREMENT /*2*/:
                    switch (this.mManagedButton) {
                        case BUTTON_INCREMENT /*1*/:
                            if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getPressedStateDuration());
                            }
                            NumberPicker.this.mIncrementVirtualButtonPressed = NumberPicker.this.mIncrementVirtualButtonPressed ^ BUTTON_INCREMENT;
                            NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.getRight(), NumberPicker.this.getBottom());
                        case BUTTON_DECREMENT /*2*/:
                            if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, (long) ViewConfiguration.getPressedStateDuration());
                            }
                            NumberPicker.this.mDecrementVirtualButtonPressed = NumberPicker.this.mDecrementVirtualButtonPressed ^ BUTTON_INCREMENT;
                            NumberPicker.this.invalidate(NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.DEFAULT_LAYOUT_RESOURCE_ID, NumberPicker.this.getRight(), NumberPicker.this.mTopSelectionDividerTop);
                        default:
                    }
                default:
            }
        }
    }

    public NumberPicker(Context context) {
        super(context);
        this.mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
        this.mSelectorIndexToStringCache = new SparseArray();
        this.mSelectorIndices = new int[SELECTOR_WHEEL_ITEM_COUNT];
        this.mInitialScrollOffset = TLRPC.MESSAGE_FLAG_MEGAGROUP;
        this.mScrollState = DEFAULT_LAYOUT_RESOURCE_ID;
        this.mLastHandledDownDpadKeyCode = SIZE_UNSPECIFIED;
        init();
    }

    public NumberPicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
        this.mSelectorIndexToStringCache = new SparseArray();
        this.mSelectorIndices = new int[SELECTOR_WHEEL_ITEM_COUNT];
        this.mInitialScrollOffset = TLRPC.MESSAGE_FLAG_MEGAGROUP;
        this.mScrollState = DEFAULT_LAYOUT_RESOURCE_ID;
        this.mLastHandledDownDpadKeyCode = SIZE_UNSPECIFIED;
        init();
    }

    public NumberPicker(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
        this.mSelectorIndexToStringCache = new SparseArray();
        this.mSelectorIndices = new int[SELECTOR_WHEEL_ITEM_COUNT];
        this.mInitialScrollOffset = TLRPC.MESSAGE_FLAG_MEGAGROUP;
        this.mScrollState = DEFAULT_LAYOUT_RESOURCE_ID;
        this.mLastHandledDownDpadKeyCode = SIZE_UNSPECIFIED;
        init();
    }

    private void changeValueByOne(boolean z) {
        this.mInputText.setVisibility(4);
        if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
            moveToFinalScrollerPosition(this.mAdjustScroller);
        }
        this.mPreviousScrollerY = DEFAULT_LAYOUT_RESOURCE_ID;
        if (z) {
            this.mFlingScroller.startScroll(DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, -this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        } else {
            this.mFlingScroller.startScroll(DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        }
        invalidate();
    }

    private void decrementSelectorIndices(int[] iArr) {
        System.arraycopy(iArr, DEFAULT_LAYOUT_RESOURCE_ID, iArr, SELECTOR_MIDDLE_ITEM_INDEX, iArr.length + SIZE_UNSPECIFIED);
        int i = iArr[SELECTOR_MIDDLE_ITEM_INDEX] + SIZE_UNSPECIFIED;
        if (this.mWrapSelectorWheel && i < this.mMinValue) {
            i = this.mMaxValue;
        }
        iArr[DEFAULT_LAYOUT_RESOURCE_ID] = i;
        ensureCachedScrollSelectorValue(i);
    }

    private void ensureCachedScrollSelectorValue(int i) {
        SparseArray sparseArray = this.mSelectorIndexToStringCache;
        if (((String) sparseArray.get(i)) == null) {
            Object obj;
            if (i < this.mMinValue || i > this.mMaxValue) {
                obj = TtmlNode.ANONYMOUS_REGION_ID;
            } else if (this.mDisplayedValues != null) {
                obj = this.mDisplayedValues[i - this.mMinValue];
            } else {
                obj = formatNumber(i);
            }
            sparseArray.put(i, obj);
        }
    }

    private boolean ensureScrollWheelAdjusted() {
        int i = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (i == 0) {
            return false;
        }
        this.mPreviousScrollerY = DEFAULT_LAYOUT_RESOURCE_ID;
        if (Math.abs(i) > this.mSelectorElementHeight / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT) {
            i += i > 0 ? -this.mSelectorElementHeight : this.mSelectorElementHeight;
        }
        this.mAdjustScroller.startScroll(DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, i, SELECTOR_ADJUSTMENT_DURATION_MILLIS);
        invalidate();
        return true;
    }

    private void fling(int i) {
        this.mPreviousScrollerY = DEFAULT_LAYOUT_RESOURCE_ID;
        if (i > 0) {
            this.mFlingScroller.fling(DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, i, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, ConnectionsManager.DEFAULT_DATACENTER_ID);
        } else {
            this.mFlingScroller.fling(DEFAULT_LAYOUT_RESOURCE_ID, ConnectionsManager.DEFAULT_DATACENTER_ID, DEFAULT_LAYOUT_RESOURCE_ID, i, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, ConnectionsManager.DEFAULT_DATACENTER_ID);
        }
        invalidate();
    }

    private String formatNumber(int i) {
        return this.mFormatter != null ? this.mFormatter.format(i) : formatNumberWithLocale(i);
    }

    private static String formatNumberWithLocale(int i) {
        Object[] objArr = new Object[SELECTOR_MIDDLE_ITEM_INDEX];
        objArr[DEFAULT_LAYOUT_RESOURCE_ID] = Integer.valueOf(i);
        return String.format(Locale.getDefault(), "%d", objArr);
    }

    private int getSelectedPos(String str) {
        if (this.mDisplayedValues == null) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException e) {
                return this.mMinValue;
            }
        }
        for (int i = DEFAULT_LAYOUT_RESOURCE_ID; i < this.mDisplayedValues.length; i += SELECTOR_MIDDLE_ITEM_INDEX) {
            str = str.toLowerCase();
            if (this.mDisplayedValues[i].toLowerCase().startsWith(str)) {
                return i + this.mMinValue;
            }
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e2) {
            return this.mMinValue;
        }
    }

    private int getWrappedSelectorIndex(int i) {
        return i > this.mMaxValue ? (this.mMinValue + ((i - this.mMaxValue) % (this.mMaxValue - this.mMinValue))) + SIZE_UNSPECIFIED : i < this.mMinValue ? (this.mMaxValue - ((this.mMinValue - i) % (this.mMaxValue - this.mMinValue))) + SELECTOR_MIDDLE_ITEM_INDEX : i;
    }

    private void incrementSelectorIndices(int[] iArr) {
        System.arraycopy(iArr, SELECTOR_MIDDLE_ITEM_INDEX, iArr, DEFAULT_LAYOUT_RESOURCE_ID, iArr.length + SIZE_UNSPECIFIED);
        int i = iArr[iArr.length - 2] + SELECTOR_MIDDLE_ITEM_INDEX;
        if (this.mWrapSelectorWheel && i > this.mMaxValue) {
            i = this.mMinValue;
        }
        iArr[iArr.length + SIZE_UNSPECIFIED] = i;
        ensureCachedScrollSelectorValue(i);
    }

    private void init() {
        this.mSolidColor = DEFAULT_LAYOUT_RESOURCE_ID;
        this.mSelectionDivider = getResources().getDrawable(C0338R.drawable.numberpicker_selection_divider);
        if (ThemeUtil.m2490b()) {
            this.mSelectionDivider.setColorFilter(AdvanceTheme.f2491b, Mode.MULTIPLY);
        }
        this.mSelectionDividerHeight = (int) TypedValue.applyDimension(SELECTOR_MIDDLE_ITEM_INDEX, 2.0f, getResources().getDisplayMetrics());
        this.mSelectionDividersDistance = (int) TypedValue.applyDimension(SELECTOR_MIDDLE_ITEM_INDEX, 48.0f, getResources().getDisplayMetrics());
        this.mMinHeight = SIZE_UNSPECIFIED;
        this.mMaxHeight = (int) TypedValue.applyDimension(SELECTOR_MIDDLE_ITEM_INDEX, BitmapDescriptorFactory.HUE_CYAN, getResources().getDisplayMetrics());
        if (this.mMinHeight == SIZE_UNSPECIFIED || this.mMaxHeight == SIZE_UNSPECIFIED || this.mMinHeight <= this.mMaxHeight) {
            this.mMinWidth = (int) TypedValue.applyDimension(SELECTOR_MIDDLE_ITEM_INDEX, 64.0f, getResources().getDisplayMetrics());
            this.mMaxWidth = SIZE_UNSPECIFIED;
            if (this.mMinWidth == SIZE_UNSPECIFIED || this.mMaxWidth == SIZE_UNSPECIFIED || this.mMinWidth <= this.mMaxWidth) {
                this.mComputeMaxWidth = this.mMaxWidth == SIZE_UNSPECIFIED;
                this.mVirtualButtonPressedDrawable = getResources().getDrawable(C0338R.drawable.item_background_holo_light);
                this.mPressedStateHelper = new PressedStateHelper();
                setWillNotDraw(false);
                this.mInputText = new TextView(getContext());
                addView(this.mInputText);
                this.mInputText.setLayoutParams(new LayoutParams(SIZE_UNSPECIFIED, -2));
                this.mInputText.setGravity(17);
                this.mInputText.setSingleLine(true);
                this.mInputText.setBackgroundResource(DEFAULT_LAYOUT_RESOURCE_ID);
                this.mInputText.setTextSize(UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT, 18.0f);
                ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
                this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
                this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
                this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity() / SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT;
                this.mTextSize = (int) this.mInputText.getTextSize();
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setTextAlign(Align.CENTER);
                paint.setTextSize((float) this.mTextSize);
                paint.setTypeface(this.mInputText.getTypeface());
                paint.setColor(this.mInputText.getTextColors().getColorForState(ENABLED_STATE_SET, SIZE_UNSPECIFIED));
                this.mSelectorWheelPaint = paint;
                this.mFlingScroller = new Scroller(getContext(), null, true);
                this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
                updateInputTextView();
                return;
            }
            throw new IllegalArgumentException("minWidth > maxWidth");
        }
        throw new IllegalArgumentException("minHeight > maxHeight");
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((getBottom() - getTop()) - this.mTextSize) / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] iArr = this.mSelectorIndices;
        this.mSelectorTextGapHeight = (int) ((((float) ((getBottom() - getTop()) - (iArr.length * this.mTextSize))) / ((float) iArr.length)) + 0.5f);
        this.mSelectorElementHeight = this.mTextSize + this.mSelectorTextGapHeight;
        this.mInitialScrollOffset = (this.mInputText.getBaseline() + this.mInputText.getTop()) - (this.mSelectorElementHeight * SELECTOR_MIDDLE_ITEM_INDEX);
        this.mCurrentScrollOffset = this.mInitialScrollOffset;
        updateInputTextView();
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] iArr = this.mSelectorIndices;
        int value = getValue();
        for (int i = DEFAULT_LAYOUT_RESOURCE_ID; i < this.mSelectorIndices.length; i += SELECTOR_MIDDLE_ITEM_INDEX) {
            int i2 = (i + SIZE_UNSPECIFIED) + value;
            if (this.mWrapSelectorWheel) {
                i2 = getWrappedSelectorIndex(i2);
            }
            iArr[i] = i2;
            ensureCachedScrollSelectorValue(iArr[i]);
        }
    }

    private int makeMeasureSpec(int i, int i2) {
        if (i2 == SIZE_UNSPECIFIED) {
            return i;
        }
        int size = MeasureSpec.getSize(i);
        int mode = MeasureSpec.getMode(i);
        switch (mode) {
            case TLRPC.MESSAGE_FLAG_MEGAGROUP /*-2147483648*/:
                return MeasureSpec.makeMeasureSpec(Math.min(size, i2), C0700C.ENCODING_PCM_32BIT);
            case DEFAULT_LAYOUT_RESOURCE_ID /*0*/:
                return MeasureSpec.makeMeasureSpec(i2, C0700C.ENCODING_PCM_32BIT);
            case C0700C.ENCODING_PCM_32BIT /*1073741824*/:
                return i;
            default:
                throw new IllegalArgumentException("Unknown measure mode: " + mode);
        }
    }

    private boolean moveToFinalScrollerPosition(Scroller scroller) {
        scroller.forceFinished(true);
        int finalY = scroller.getFinalY() - scroller.getCurrY();
        int i = this.mInitialScrollOffset - ((this.mCurrentScrollOffset + finalY) % this.mSelectorElementHeight);
        if (i == 0) {
            return false;
        }
        if (Math.abs(i) > this.mSelectorElementHeight / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT) {
            i = i > 0 ? i - this.mSelectorElementHeight : i + this.mSelectorElementHeight;
        }
        scrollBy(DEFAULT_LAYOUT_RESOURCE_ID, i + finalY);
        return true;
    }

    private void notifyChange(int i, int i2) {
        if (this.mOnValueChangeListener != null) {
            this.mOnValueChangeListener.onValueChange(this, i, this.mValue);
        }
    }

    private void onScrollStateChange(int i) {
        if (this.mScrollState != i) {
            this.mScrollState = i;
            if (this.mOnScrollListener != null) {
                this.mOnScrollListener.onScrollStateChange(this, i);
            }
        }
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            if (!ensureScrollWheelAdjusted()) {
                updateInputTextView();
            }
            onScrollStateChange(DEFAULT_LAYOUT_RESOURCE_ID);
        } else if (this.mScrollState != SELECTOR_MIDDLE_ITEM_INDEX) {
            updateInputTextView();
        }
    }

    private void postChangeCurrentByOneFromLongPress(boolean z, long j) {
        if (this.mChangeCurrentByOneFromLongPressCommand == null) {
            this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
        } else {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
        this.mChangeCurrentByOneFromLongPressCommand.setStep(z);
        postDelayed(this.mChangeCurrentByOneFromLongPressCommand, j);
    }

    private void removeAllCallbacks() {
        if (this.mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
        this.mPressedStateHelper.cancel();
    }

    private void removeChangeCurrentByOneFromLongPress() {
        if (this.mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
    }

    public static int resolveSizeAndState(int i, int i2, int i3) {
        int mode = MeasureSpec.getMode(i2);
        int size = MeasureSpec.getSize(i2);
        switch (mode) {
            case TLRPC.MESSAGE_FLAG_MEGAGROUP /*-2147483648*/:
                if (size < i) {
                    i = size | AccessibilityEventCompat.TYPE_ASSIST_READING_CONTEXT;
                    break;
                }
                break;
            case C0700C.ENCODING_PCM_32BIT /*1073741824*/:
                i = size;
                break;
        }
        return (Theme.MSG_TEXT_COLOR & i3) | i;
    }

    private int resolveSizeAndStateRespectingMinSize(int i, int i2, int i3) {
        return i != SIZE_UNSPECIFIED ? resolveSizeAndState(Math.max(i, i2), i3, DEFAULT_LAYOUT_RESOURCE_ID) : i2;
    }

    private void setValueInternal(int i, boolean z) {
        if (this.mValue != i) {
            int wrappedSelectorIndex = this.mWrapSelectorWheel ? getWrappedSelectorIndex(i) : Math.min(Math.max(i, this.mMinValue), this.mMaxValue);
            int i2 = this.mValue;
            this.mValue = wrappedSelectorIndex;
            updateInputTextView();
            if (z) {
                notifyChange(i2, wrappedSelectorIndex);
            }
            initializeSelectorWheelIndices();
            invalidate();
        }
    }

    private void tryComputeMaxWidth() {
        int i = DEFAULT_LAYOUT_RESOURCE_ID;
        if (this.mComputeMaxWidth) {
            int i2;
            if (this.mDisplayedValues == null) {
                float f = 0.0f;
                int i3 = DEFAULT_LAYOUT_RESOURCE_ID;
                while (i3 <= 9) {
                    float measureText = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i3));
                    if (measureText <= f) {
                        measureText = f;
                    }
                    i3 += SELECTOR_MIDDLE_ITEM_INDEX;
                    f = measureText;
                }
                for (i2 = this.mMaxValue; i2 > 0; i2 /= 10) {
                    i += SELECTOR_MIDDLE_ITEM_INDEX;
                }
                i2 = (int) (((float) i) * f);
            } else {
                String[] strArr = this.mDisplayedValues;
                int length = strArr.length;
                i2 = DEFAULT_LAYOUT_RESOURCE_ID;
                for (int i4 = DEFAULT_LAYOUT_RESOURCE_ID; i4 < length; i4 += SELECTOR_MIDDLE_ITEM_INDEX) {
                    float measureText2 = this.mSelectorWheelPaint.measureText(strArr[i4]);
                    if (measureText2 > ((float) i2)) {
                        i2 = (int) measureText2;
                    }
                }
            }
            i2 += this.mInputText.getPaddingLeft() + this.mInputText.getPaddingRight();
            if (this.mMaxWidth != i2) {
                if (i2 > this.mMinWidth) {
                    this.mMaxWidth = i2;
                } else {
                    this.mMaxWidth = this.mMinWidth;
                }
                invalidate();
            }
        }
    }

    private boolean updateInputTextView() {
        CharSequence formatNumber = this.mDisplayedValues == null ? formatNumber(this.mValue) : this.mDisplayedValues[this.mValue - this.mMinValue];
        if (TextUtils.isEmpty(formatNumber) || formatNumber.equals(this.mInputText.getText().toString())) {
            return false;
        }
        this.mInputText.setText(formatNumber);
        return true;
    }

    public void computeScroll() {
        Scroller scroller = this.mFlingScroller;
        if (scroller.isFinished()) {
            scroller = this.mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currY = scroller.getCurrY();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(DEFAULT_LAYOUT_RESOURCE_ID, currY - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    protected int computeVerticalScrollOffset() {
        return this.mCurrentScrollOffset;
    }

    protected int computeVerticalScrollRange() {
        return ((this.mMaxValue - this.mMinValue) + SELECTOR_MIDDLE_ITEM_INDEX) * this.mSelectorElementHeight;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchKeyEvent(android.view.KeyEvent r6) {
        /*
        r5 = this;
        r4 = 20;
        r1 = 1;
        r0 = r6.getKeyCode();
        switch(r0) {
            case 19: goto L_0x0013;
            case 20: goto L_0x0013;
            case 23: goto L_0x000f;
            case 66: goto L_0x000f;
            default: goto L_0x000a;
        };
    L_0x000a:
        r1 = super.dispatchKeyEvent(r6);
    L_0x000e:
        return r1;
    L_0x000f:
        r5.removeAllCallbacks();
        goto L_0x000a;
    L_0x0013:
        r2 = r6.getAction();
        switch(r2) {
            case 0: goto L_0x001b;
            case 1: goto L_0x004f;
            default: goto L_0x001a;
        };
    L_0x001a:
        goto L_0x000a;
    L_0x001b:
        r2 = r5.mWrapSelectorWheel;
        if (r2 != 0) goto L_0x0021;
    L_0x001f:
        if (r0 != r4) goto L_0x0042;
    L_0x0021:
        r2 = r5.getValue();
        r3 = r5.getMaxValue();
        if (r2 >= r3) goto L_0x000a;
    L_0x002b:
        r5.requestFocus();
        r5.mLastHandledDownDpadKeyCode = r0;
        r5.removeAllCallbacks();
        r2 = r5.mFlingScroller;
        r2 = r2.isFinished();
        if (r2 == 0) goto L_0x000e;
    L_0x003b:
        if (r0 != r4) goto L_0x004d;
    L_0x003d:
        r0 = r1;
    L_0x003e:
        r5.changeValueByOne(r0);
        goto L_0x000e;
    L_0x0042:
        r2 = r5.getValue();
        r3 = r5.getMinValue();
        if (r2 <= r3) goto L_0x000a;
    L_0x004c:
        goto L_0x002b;
    L_0x004d:
        r0 = 0;
        goto L_0x003e;
    L_0x004f:
        r2 = r5.mLastHandledDownDpadKeyCode;
        if (r2 != r0) goto L_0x000a;
    L_0x0053:
        r0 = -1;
        r5.mLastHandledDownDpadKeyCode = r0;
        goto L_0x000e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.Components.NumberPicker.dispatchKeyEvent(android.view.KeyEvent):boolean");
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case SELECTOR_MIDDLE_ITEM_INDEX /*1*/:
            case SELECTOR_WHEEL_ITEM_COUNT /*3*/:
                removeAllCallbacks();
                break;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case SELECTOR_MIDDLE_ITEM_INDEX /*1*/:
            case SELECTOR_WHEEL_ITEM_COUNT /*3*/:
                removeAllCallbacks();
                break;
        }
        return super.dispatchTrackballEvent(motionEvent);
    }

    protected float getBottomFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public int getSolidColor() {
        return this.mSolidColor;
    }

    protected float getTopFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    public int getValue() {
        return this.mValue;
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllCallbacks();
    }

    protected void onDraw(Canvas canvas) {
        float right = (float) ((getRight() - getLeft()) / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
        float f = (float) this.mCurrentScrollOffset;
        if (this.mVirtualButtonPressedDrawable != null && this.mScrollState == 0) {
            if (this.mDecrementVirtualButtonPressed) {
                this.mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
                this.mVirtualButtonPressedDrawable.setBounds(DEFAULT_LAYOUT_RESOURCE_ID, DEFAULT_LAYOUT_RESOURCE_ID, getRight(), this.mTopSelectionDividerTop);
                this.mVirtualButtonPressedDrawable.draw(canvas);
            }
            if (this.mIncrementVirtualButtonPressed) {
                this.mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
                this.mVirtualButtonPressedDrawable.setBounds(DEFAULT_LAYOUT_RESOURCE_ID, this.mBottomSelectionDividerBottom, getRight(), getBottom());
                this.mVirtualButtonPressedDrawable.draw(canvas);
            }
        }
        int[] iArr = this.mSelectorIndices;
        float f2 = f;
        for (int i = DEFAULT_LAYOUT_RESOURCE_ID; i < iArr.length; i += SELECTOR_MIDDLE_ITEM_INDEX) {
            String str = (String) this.mSelectorIndexToStringCache.get(iArr[i]);
            if (i != SELECTOR_MIDDLE_ITEM_INDEX || this.mInputText.getVisibility() != 0) {
                canvas.drawText(str, right, f2, this.mSelectorWheelPaint);
            }
            f2 += (float) this.mSelectorElementHeight;
        }
        if (this.mSelectionDivider != null) {
            int i2 = this.mTopSelectionDividerTop;
            this.mSelectionDivider.setBounds(DEFAULT_LAYOUT_RESOURCE_ID, i2, getRight(), this.mSelectionDividerHeight + i2);
            this.mSelectionDivider.draw(canvas);
            i2 = this.mBottomSelectionDividerBottom;
            this.mSelectionDivider.setBounds(DEFAULT_LAYOUT_RESOURCE_ID, i2 - this.mSelectionDividerHeight, getRight(), i2);
            this.mSelectionDivider.draw(canvas);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        switch (motionEvent.getActionMasked()) {
            case DEFAULT_LAYOUT_RESOURCE_ID /*0*/:
                removeAllCallbacks();
                this.mInputText.setVisibility(4);
                float y = motionEvent.getY();
                this.mLastDownEventY = y;
                this.mLastDownOrMoveEventY = y;
                this.mLastDownEventTime = motionEvent.getEventTime();
                this.mIngonreMoveEvents = false;
                if (this.mLastDownEventY < ((float) this.mTopSelectionDividerTop)) {
                    if (this.mScrollState == 0) {
                        this.mPressedStateHelper.buttonPressDelayed(UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
                    }
                } else if (this.mLastDownEventY > ((float) this.mBottomSelectionDividerBottom) && this.mScrollState == 0) {
                    this.mPressedStateHelper.buttonPressDelayed(SELECTOR_MIDDLE_ITEM_INDEX);
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                if (!this.mFlingScroller.isFinished()) {
                    this.mFlingScroller.forceFinished(true);
                    this.mAdjustScroller.forceFinished(true);
                    onScrollStateChange(DEFAULT_LAYOUT_RESOURCE_ID);
                } else if (!this.mAdjustScroller.isFinished()) {
                    this.mFlingScroller.forceFinished(true);
                    this.mAdjustScroller.forceFinished(true);
                } else if (this.mLastDownEventY < ((float) this.mTopSelectionDividerTop)) {
                    postChangeCurrentByOneFromLongPress(false, (long) ViewConfiguration.getLongPressTimeout());
                } else if (this.mLastDownEventY > ((float) this.mBottomSelectionDividerBottom)) {
                    postChangeCurrentByOneFromLongPress(true, (long) ViewConfiguration.getLongPressTimeout());
                }
                return true;
            default:
                return false;
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int measuredWidth2 = this.mInputText.getMeasuredWidth();
        int measuredHeight2 = this.mInputText.getMeasuredHeight();
        measuredWidth = (measuredWidth - measuredWidth2) / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT;
        measuredHeight = (measuredHeight - measuredHeight2) / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT;
        this.mInputText.layout(measuredWidth, measuredHeight, measuredWidth2 + measuredWidth, measuredHeight2 + measuredHeight);
        if (z) {
            initializeSelectorWheel();
            initializeFadingEdges();
            this.mTopSelectionDividerTop = ((getHeight() - this.mSelectionDividersDistance) / UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT) - this.mSelectionDividerHeight;
            this.mBottomSelectionDividerBottom = (this.mTopSelectionDividerTop + (this.mSelectionDividerHeight * UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT)) + this.mSelectionDividersDistance;
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(makeMeasureSpec(i, this.mMaxWidth), makeMeasureSpec(i2, this.mMaxHeight));
        setMeasuredDimension(resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), i), resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), i2));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        switch (motionEvent.getActionMasked()) {
            case SELECTOR_MIDDLE_ITEM_INDEX /*1*/:
                removeChangeCurrentByOneFromLongPress();
                this.mPressedStateHelper.cancel();
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(PointerIconCompat.TYPE_DEFAULT, (float) this.mMaximumFlingVelocity);
                int yVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(yVelocity) > this.mMinimumFlingVelocity) {
                    fling(yVelocity);
                    onScrollStateChange(UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
                } else {
                    yVelocity = (int) motionEvent.getY();
                    long eventTime = motionEvent.getEventTime() - this.mLastDownEventTime;
                    if (((int) Math.abs(((float) yVelocity) - this.mLastDownEventY)) > this.mTouchSlop || eventTime >= ((long) ViewConfiguration.getTapTimeout())) {
                        ensureScrollWheelAdjusted();
                    } else {
                        yVelocity = (yVelocity / this.mSelectorElementHeight) + SIZE_UNSPECIFIED;
                        if (yVelocity > 0) {
                            changeValueByOne(true);
                            this.mPressedStateHelper.buttonTapped(SELECTOR_MIDDLE_ITEM_INDEX);
                        } else if (yVelocity < 0) {
                            changeValueByOne(false);
                            this.mPressedStateHelper.buttonTapped(UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT);
                        }
                    }
                    onScrollStateChange(DEFAULT_LAYOUT_RESOURCE_ID);
                }
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
                break;
            case UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT /*2*/:
                if (!this.mIngonreMoveEvents) {
                    float y = motionEvent.getY();
                    if (this.mScrollState == SELECTOR_MIDDLE_ITEM_INDEX) {
                        scrollBy(DEFAULT_LAYOUT_RESOURCE_ID, (int) (y - this.mLastDownOrMoveEventY));
                        invalidate();
                    } else if (((int) Math.abs(y - this.mLastDownEventY)) > this.mTouchSlop) {
                        removeAllCallbacks();
                        onScrollStateChange(SELECTOR_MIDDLE_ITEM_INDEX);
                    }
                    this.mLastDownOrMoveEventY = y;
                    break;
                }
                break;
        }
        return true;
    }

    public void scrollBy(int i, int i2) {
        int[] iArr = this.mSelectorIndices;
        if (!this.mWrapSelectorWheel && i2 > 0 && iArr[SELECTOR_MIDDLE_ITEM_INDEX] <= this.mMinValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        } else if (this.mWrapSelectorWheel || i2 >= 0 || iArr[SELECTOR_MIDDLE_ITEM_INDEX] < this.mMaxValue) {
            this.mCurrentScrollOffset += i2;
            while (this.mCurrentScrollOffset - this.mInitialScrollOffset > this.mSelectorTextGapHeight) {
                this.mCurrentScrollOffset -= this.mSelectorElementHeight;
                decrementSelectorIndices(iArr);
                setValueInternal(iArr[SELECTOR_MIDDLE_ITEM_INDEX], true);
                if (!this.mWrapSelectorWheel && iArr[SELECTOR_MIDDLE_ITEM_INDEX] <= this.mMinValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
            while (this.mCurrentScrollOffset - this.mInitialScrollOffset < (-this.mSelectorTextGapHeight)) {
                this.mCurrentScrollOffset += this.mSelectorElementHeight;
                incrementSelectorIndices(iArr);
                setValueInternal(iArr[SELECTOR_MIDDLE_ITEM_INDEX], true);
                if (!this.mWrapSelectorWheel && iArr[SELECTOR_MIDDLE_ITEM_INDEX] >= this.mMaxValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
        } else {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        }
    }

    public void setDisplayedValues(String[] strArr) {
        if (this.mDisplayedValues != strArr) {
            this.mDisplayedValues = strArr;
            updateInputTextView();
            initializeSelectorWheelIndices();
            tryComputeMaxWidth();
        }
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mInputText.setEnabled(z);
    }

    public void setFormatter(Formatter formatter) {
        if (formatter != this.mFormatter) {
            this.mFormatter = formatter;
            initializeSelectorWheelIndices();
            updateInputTextView();
        }
    }

    public void setMaxValue(int i) {
        if (this.mMaxValue != i) {
            if (i < 0) {
                throw new IllegalArgumentException("maxValue must be >= 0");
            }
            this.mMaxValue = i;
            if (this.mMaxValue < this.mValue) {
                this.mValue = this.mMaxValue;
            }
            setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length);
            initializeSelectorWheelIndices();
            updateInputTextView();
            tryComputeMaxWidth();
            invalidate();
        }
    }

    public void setMinValue(int i) {
        if (this.mMinValue != i) {
            this.mMinValue = i;
            if (this.mMinValue > this.mValue) {
                this.mValue = this.mMinValue;
            }
            setWrapSelectorWheel(this.mMaxValue - this.mMinValue > this.mSelectorIndices.length);
            initializeSelectorWheelIndices();
            updateInputTextView();
            tryComputeMaxWidth();
            invalidate();
        }
    }

    public void setOnLongPressUpdateInterval(long j) {
        this.mLongPressUpdateInterval = j;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangeListener) {
        this.mOnValueChangeListener = onValueChangeListener;
    }

    public void setValue(int i) {
        setValueInternal(i, false);
    }

    public void setWrapSelectorWheel(boolean z) {
        Object obj = this.mMaxValue - this.mMinValue >= this.mSelectorIndices.length ? SELECTOR_MIDDLE_ITEM_INDEX : null;
        if ((!z || obj != null) && z != this.mWrapSelectorWheel) {
            this.mWrapSelectorWheel = z;
        }
    }
}

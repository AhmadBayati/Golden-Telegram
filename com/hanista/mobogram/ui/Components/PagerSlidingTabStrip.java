package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;

public class PagerSlidingTabStrip extends HorizontalScrollView {
    private boolean addCounterToTabs;
    private int currentPosition;
    private float currentPositionOffset;
    private LayoutParams defaultTabLayoutParams;
    public OnPageChangeListener delegatePageListener;
    private int dividerPadding;
    private int indicatorColor;
    private int indicatorHeight;
    private boolean isMainScreen;
    private int lastScrollX;
    private OnLongClickOnTabListener onLongClickOnTabListener;
    private final PageListener pageListener;
    private ViewPager pager;
    private Paint rectPaint;
    private int scrollOffset;
    private boolean shouldExpand;
    private boolean showIndicatorAtTop;
    private int tabCount;
    private int tabPadding;
    private LinearLayout tabsContainer;
    private int underlineColor;
    private int underlineHeight;

    public interface OnLongClickOnTabListener {
        void onLongClick(int i);
    }

    public interface IconTabProvider {
        void customOnDraw(Canvas canvas, int i);

        int getPageIconResId(int i);

        int getPageIconSelectedResId(int i);

        int getPageIconUnSelectedResId(int i);
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.1 */
    class C13561 implements OnGlobalLayoutListener {
        C13561() {
        }

        public void onGlobalLayout() {
            if (VERSION.SDK_INT < 16) {
                PagerSlidingTabStrip.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                PagerSlidingTabStrip.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            PagerSlidingTabStrip.this.currentPosition = PagerSlidingTabStrip.this.pager.getCurrentItem();
            PagerSlidingTabStrip.this.scrollToChild(PagerSlidingTabStrip.this.currentPosition, 0);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.2 */
    class C13572 extends ImageView {
        final /* synthetic */ int val$position;

        C13572(Context context, int i) {
            this.val$position = i;
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (PagerSlidingTabStrip.this.pager.getAdapter() instanceof IconTabProvider) {
                ((IconTabProvider) PagerSlidingTabStrip.this.pager.getAdapter()).customOnDraw(canvas, this.val$position);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.3 */
    class C13583 implements OnClickListener {
        final /* synthetic */ int val$position;

        C13583(int i) {
            this.val$position = i;
        }

        public void onClick(View view) {
            PagerSlidingTabStrip.this.pager.setCurrentItem(this.val$position);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.4 */
    class C13594 implements OnLongClickListener {
        final /* synthetic */ int val$position;

        C13594(int i) {
            this.val$position = i;
        }

        public boolean onLongClick(View view) {
            if (PagerSlidingTabStrip.this.onLongClickOnTabListener == null) {
                return false;
            }
            PagerSlidingTabStrip.this.onLongClickOnTabListener.onLongClick(this.val$position);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.5 */
    class C13605 implements Runnable {
        C13605() {
        }

        public void run() {
            PagerSlidingTabStrip.this.notifyDataSetChanged();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.6 */
    class C13616 implements OnClickListener {
        final /* synthetic */ int val$position;

        C13616(int i) {
            this.val$position = i;
        }

        public void onClick(View view) {
            PagerSlidingTabStrip.this.pager.setCurrentItem(this.val$position);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.7 */
    class C13627 implements OnLongClickListener {
        final /* synthetic */ int val$position;

        C13627(int i) {
            this.val$position = i;
        }

        public boolean onLongClick(View view) {
            if (PagerSlidingTabStrip.this.onLongClickOnTabListener == null) {
                return false;
            }
            PagerSlidingTabStrip.this.onLongClickOnTabListener.onLongClick(this.val$position);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.PagerSlidingTabStrip.8 */
    class C13638 implements OnLongClickListener {
        final /* synthetic */ int val$position;

        C13638(int i) {
            this.val$position = i;
        }

        public boolean onLongClick(View view) {
            if (PagerSlidingTabStrip.this.onLongClickOnTabListener == null) {
                return false;
            }
            PagerSlidingTabStrip.this.onLongClickOnTabListener.onLongClick(this.val$position);
            return true;
        }
    }

    public class FilterableStateListDrawable extends StateListDrawable {
        private int childrenCount;
        private int currIdx;
        private SparseArray<ColorFilter> filterMap;

        public FilterableStateListDrawable() {
            this.currIdx = -1;
            this.childrenCount = 0;
            this.filterMap = new SparseArray();
        }

        private ColorFilter getColorFilterForIdx(int i) {
            return this.filterMap != null ? (ColorFilter) this.filterMap.get(i) : null;
        }

        public void addState(int[] iArr, Drawable drawable) {
            super.addState(iArr, drawable);
            this.childrenCount++;
        }

        public void addState(int[] iArr, Drawable drawable, ColorFilter colorFilter) {
            int i = this.childrenCount;
            addState(iArr, drawable);
            this.filterMap.put(i, colorFilter);
        }

        public boolean selectDrawable(int i) {
            if (this.currIdx != i) {
                setColorFilter(getColorFilterForIdx(i));
            }
            boolean selectDrawable = super.selectDrawable(i);
            if (getCurrent() != null) {
                if (!selectDrawable) {
                    i = this.currIdx;
                }
                this.currIdx = i;
                if (!selectDrawable) {
                    setColorFilter(getColorFilterForIdx(this.currIdx));
                }
            } else if (getCurrent() == null) {
                this.currIdx = -1;
                setColorFilter(null);
            }
            return selectDrawable;
        }
    }

    private class PageListener implements OnPageChangeListener {
        private PageListener() {
        }

        public void onPageScrollStateChanged(int i) {
            if (i == 0) {
                PagerSlidingTabStrip.this.scrollToChild(PagerSlidingTabStrip.this.pager.getCurrentItem(), 0);
            }
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageScrollStateChanged(i);
            }
        }

        public void onPageScrolled(int i, float f, int i2) {
            PagerSlidingTabStrip.this.currentPosition = i;
            PagerSlidingTabStrip.this.currentPositionOffset = f;
            PagerSlidingTabStrip.this.scrollToChild(i, (int) (((float) PagerSlidingTabStrip.this.tabsContainer.getChildAt(i).getWidth()) * f));
            PagerSlidingTabStrip.this.invalidate();
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageScrolled(i, f, i2);
            }
        }

        public void onPageSelected(int i) {
            if (PagerSlidingTabStrip.this.delegatePageListener != null) {
                PagerSlidingTabStrip.this.delegatePageListener.onPageSelected(i);
            }
            int i2 = 0;
            while (i2 < PagerSlidingTabStrip.this.tabsContainer.getChildCount()) {
                PagerSlidingTabStrip.this.tabsContainer.getChildAt(i2).setSelected(i2 == i);
                if (PagerSlidingTabStrip.this.tabsContainer.getChildAt(i2) instanceof FrameLayout) {
                    FrameLayout frameLayout = (FrameLayout) PagerSlidingTabStrip.this.tabsContainer.getChildAt(i2);
                    for (int i3 = 0; i3 < frameLayout.getChildCount(); i3++) {
                        frameLayout.getChildAt(i3).setSelected(i2 == i);
                    }
                }
                i2++;
            }
        }
    }

    public PagerSlidingTabStrip(Context context) {
        super(context);
        this.pageListener = new PageListener();
        this.currentPosition = 0;
        this.currentPositionOffset = 0.0f;
        this.indicatorColor = -10066330;
        this.underlineColor = 436207616;
        this.shouldExpand = false;
        this.scrollOffset = AndroidUtilities.dp(52.0f);
        this.indicatorHeight = AndroidUtilities.dp(8.0f);
        this.underlineHeight = AndroidUtilities.dp(2.0f);
        this.dividerPadding = AndroidUtilities.dp(12.0f);
        this.tabPadding = AndroidUtilities.dp(24.0f);
        this.lastScrollX = 0;
        setFillViewport(true);
        setWillNotDraw(false);
        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);
        this.defaultTabLayoutParams = new LayoutParams(-2, -1);
    }

    private void addIconTab(int i, int i2, int i3, int i4) {
        boolean z = true;
        View c13572 = new C13572(getContext(), i);
        c13572.setFocusable(true);
        c13572.setImageResource(i2);
        if (ThemeUtil.m2490b()) {
            c13572.setImageDrawable(setImageButtonState(i3, i4));
        }
        c13572.setScaleType(ScaleType.CENTER);
        c13572.setOnClickListener(new C13583(i));
        this.tabsContainer.addView(c13572);
        if (i != this.currentPosition) {
            z = false;
        }
        c13572.setSelected(z);
        c13572.setOnLongClickListener(new C13594(i));
    }

    private void addIconTabWithCounter(int i, int i2, int i3, int i4) {
        View frameLayout = new FrameLayout(getContext());
        OnClickListener c13616 = new C13616(i);
        View imageView = new ImageView(getContext());
        imageView.setFocusable(true);
        imageView.setImageResource(i2);
        if (ThemeUtil.m2490b()) {
            imageView.setImageDrawable(setImageButtonState(i3, i4));
        }
        imageView.setScaleType(ScaleType.CENTER);
        frameLayout.setOnClickListener(c13616);
        imageView.setOnClickListener(c13616);
        imageView.setSelected(i == this.currentPosition);
        imageView.setOnLongClickListener(new C13627(i));
        frameLayout.addView(imageView, LayoutHelper.createFrame(-1, -1, 17));
        imageView = new TextView(getContext());
        imageView.setTag(Integer.valueOf(i2));
        imageView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        imageView.setTextSize(1, 11.0f);
        imageView.setIncludeFontPadding(false);
        imageView.setOnClickListener(c13616);
        imageView.setGravity(17);
        imageView.setOnLongClickListener(new C13638(i));
        frameLayout.addView(imageView, LayoutHelper.createFrame(-2, 18, 85));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.bottomMargin = AndroidUtilities.dp(4.0f);
        imageView.setLayoutParams(layoutParams);
        this.tabsContainer.addView(frameLayout);
    }

    private void scrollToChild(int i, int i2) {
        if (this.tabCount != 0) {
            int left = this.tabsContainer.getChildAt(i).getLeft() + i2;
            if (i > 0 || i2 > 0) {
                left -= this.scrollOffset;
            }
            if (left != this.lastScrollX) {
                this.lastScrollX = left;
                scrollTo(left, 0);
            }
        }
    }

    private StateListDrawable setImageButtonState(int i, int i2) {
        Drawable drawable = getResources().getDrawable(i2);
        Drawable newDrawable = drawable.getConstantState().newDrawable();
        Drawable drawable2 = getResources().getDrawable(i);
        Drawable newDrawable2 = drawable2.getConstantState().newDrawable();
        int i3 = AdvanceTheme.ci;
        int i4 = AdvanceTheme.cj;
        if (this.isMainScreen) {
            i3 = AdvanceTheme.f2511v;
            i4 = AdvanceTheme.f2512w;
        }
        StateListDrawable filterableStateListDrawable = new FilterableStateListDrawable();
        filterableStateListDrawable.addState(new int[]{-16842908, -16842913, -16842919}, newDrawable, new PorterDuffColorFilter(i4, Mode.SRC_IN));
        filterableStateListDrawable.addState(new int[]{-16842908, -16842913, -16842919}, drawable);
        filterableStateListDrawable.addState(new int[]{-16842908, 16842913, -16842919}, newDrawable2, new PorterDuffColorFilter(i3, Mode.SRC_IN));
        filterableStateListDrawable.addState(new int[]{-16842908, 16842913, -16842919}, drawable2);
        filterableStateListDrawable.addState(new int[]{16842919}, newDrawable2, new PorterDuffColorFilter(i3, Mode.SRC_IN));
        filterableStateListDrawable.addState(new int[]{16842919}, drawable2);
        filterableStateListDrawable.addState(new int[]{16842908, -16842913, -16842919}, newDrawable, new PorterDuffColorFilter(i4, Mode.SRC_IN));
        filterableStateListDrawable.addState(new int[]{16842908, -16842913, -16842919}, drawable);
        filterableStateListDrawable.addState(new int[]{16842908, 16842913, -16842919}, newDrawable, new PorterDuffColorFilter(i4, Mode.SRC_IN));
        filterableStateListDrawable.addState(new int[]{16842908, 16842913, -16842919}, drawable);
        return filterableStateListDrawable;
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View childAt = this.tabsContainer.getChildAt(i);
            childAt.setLayoutParams(this.defaultTabLayoutParams);
            if (this.shouldExpand) {
                childAt.setPadding(0, 0, 0, 0);
                childAt.setLayoutParams(new LayoutParams(-1, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            } else {
                childAt.setPadding(this.tabPadding, 0, this.tabPadding, 0);
            }
        }
    }

    public int getDividerPadding() {
        return this.dividerPadding;
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public int getIndicatorHeight() {
        return this.indicatorHeight;
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    public boolean getShouldExpand() {
        return this.shouldExpand;
    }

    public int getTabPaddingLeftRight() {
        return this.tabPadding;
    }

    public LinearLayout getTabsContainer() {
        return this.tabsContainer;
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public int getUnderlineHeight() {
        return this.underlineHeight;
    }

    public void notifyDataSetChanged() {
        this.tabsContainer.removeAllViews();
        this.tabCount = this.pager.getAdapter().getCount();
        for (int i = 0; i < this.tabCount; i++) {
            if (this.pager.getAdapter() instanceof IconTabProvider) {
                IconTabProvider iconTabProvider = (IconTabProvider) this.pager.getAdapter();
                if (this.addCounterToTabs) {
                    addIconTabWithCounter(i, iconTabProvider.getPageIconResId(i), iconTabProvider.getPageIconSelectedResId(i), iconTabProvider.getPageIconUnSelectedResId(i));
                } else {
                    addIconTab(i, iconTabProvider.getPageIconResId(i), iconTabProvider.getPageIconSelectedResId(i), iconTabProvider.getPageIconUnSelectedResId(i));
                }
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new C13561());
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            int height = getHeight();
            this.rectPaint.setColor(this.underlineColor);
            canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            View childAt = this.tabsContainer.getChildAt(this.currentPosition);
            float left = (float) childAt.getLeft();
            float right = (float) childAt.getRight();
            if (this.currentPositionOffset > 0.0f && this.currentPosition < this.tabCount - 1) {
                childAt = this.tabsContainer.getChildAt(this.currentPosition + 1);
                float left2 = (float) childAt.getLeft();
                left = (left * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.currentPositionOffset)) + (left2 * this.currentPositionOffset);
                right = (((float) childAt.getRight()) * this.currentPositionOffset) + ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.currentPositionOffset) * right);
            }
            this.rectPaint.setColor(this.indicatorColor);
            if (this.showIndicatorAtTop) {
                canvas.drawRect(left, 0.0f, right, (float) this.indicatorHeight, this.rectPaint);
                return;
            }
            canvas.drawRect(left, (float) (height - this.indicatorHeight), right, (float) height, this.rectPaint);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.shouldExpand && MeasureSpec.getMode(i) != 0) {
            this.tabsContainer.measure(getMeasuredWidth() | C0700C.ENCODING_PCM_32BIT, i2);
        }
    }

    public void onSizeChanged(int i, int i2, int i3, int i4) {
        if (!this.shouldExpand) {
            post(new C13605());
        }
    }

    public void setAddCounterToTabs(boolean z) {
        this.addCounterToTabs = z;
    }

    public void setDividerPadding(int i) {
        this.dividerPadding = i;
        invalidate();
    }

    public void setIndicatorColor(int i) {
        this.indicatorColor = i;
        invalidate();
    }

    public void setIndicatorColorResource(int i) {
        this.indicatorColor = getResources().getColor(i);
        invalidate();
    }

    public void setIndicatorHeight(int i) {
        this.indicatorHeight = i;
        invalidate();
    }

    public void setIsMainScreen(boolean z) {
        this.isMainScreen = z;
    }

    public void setOnLongClickOnTabListener(OnLongClickOnTabListener onLongClickOnTabListener) {
        this.onLongClickOnTabListener = onLongClickOnTabListener;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.delegatePageListener = onPageChangeListener;
    }

    public void setScrollOffset(int i) {
        this.scrollOffset = i;
        invalidate();
    }

    public void setShouldExpand(boolean z) {
        this.shouldExpand = z;
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        updateTabStyles();
        requestLayout();
    }

    public void setShowIndicatorAtTop(boolean z) {
        this.showIndicatorAtTop = z;
    }

    public void setTabPaddingLeftRight(int i) {
        this.tabPadding = i;
        updateTabStyles();
    }

    public void setUnderlineColor(int i) {
        this.underlineColor = i;
        invalidate();
    }

    public void setUnderlineColorResource(int i) {
        this.underlineColor = getResources().getColor(i);
        invalidate();
    }

    public void setUnderlineHeight(int i) {
        this.underlineHeight = i;
        invalidate();
    }

    public void setViewPager(ViewPager viewPager) {
        this.pager = viewPager;
        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        viewPager.setOnPageChangeListener(this.pageListener);
        notifyDataSetChanged();
    }
}

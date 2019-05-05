package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.Document;

public class ScrollSlidingTabStrip extends HorizontalScrollView {
    private int currentPosition;
    private LayoutParams defaultTabLayoutParams;
    private ScrollSlidingTabStripDelegate delegate;
    private int dividerPadding;
    private int indicatorColor;
    private int indicatorHeight;
    private int lastScrollX;
    private Paint rectPaint;
    private int scrollOffset;
    private int tabCount;
    private int tabPadding;
    private LinearLayout tabsContainer;
    private int underlineColor;
    private int underlineHeight;

    public interface ScrollSlidingTabStripDelegate {
        void onPageSelected(int i);
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ScrollSlidingTabStrip.1 */
    class C14501 implements OnClickListener {
        final /* synthetic */ int val$position;

        C14501(int i) {
            this.val$position = i;
        }

        public void onClick(View view) {
            ScrollSlidingTabStrip.this.delegate.onPageSelected(this.val$position);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ScrollSlidingTabStrip.2 */
    class C14512 implements OnClickListener {
        final /* synthetic */ int val$position;

        C14512(int i) {
            this.val$position = i;
        }

        public void onClick(View view) {
            ScrollSlidingTabStrip.this.delegate.onPageSelected(this.val$position);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ScrollSlidingTabStrip.3 */
    class C14523 implements OnClickListener {
        final /* synthetic */ int val$position;

        C14523(int i) {
            this.val$position = i;
        }

        public void onClick(View view) {
            ScrollSlidingTabStrip.this.delegate.onPageSelected(this.val$position);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.ScrollSlidingTabStrip.4 */
    class C14534 implements OnClickListener {
        final /* synthetic */ int val$position;

        C14534(int i) {
            this.val$position = i;
        }

        public void onClick(View view) {
            ScrollSlidingTabStrip.this.delegate.onPageSelected(this.val$position);
        }
    }

    public ScrollSlidingTabStrip(Context context) {
        super(context);
        this.indicatorColor = -10066330;
        this.underlineColor = 436207616;
        this.scrollOffset = AndroidUtilities.dp(52.0f);
        this.underlineHeight = AndroidUtilities.dp(2.0f);
        this.dividerPadding = AndroidUtilities.dp(12.0f);
        this.tabPadding = AndroidUtilities.dp(24.0f);
        this.lastScrollX = 0;
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);
        this.defaultTabLayoutParams = new LayoutParams(AndroidUtilities.dp(52.0f), -1);
    }

    private void paintTabIcons(int i) {
        if (ThemeUtil.m2490b()) {
            getResources().getDrawable(i).setColorFilter(AdvanceTheme.cj, Mode.SRC_IN);
        }
    }

    private void scrollToChild(int i) {
        if (this.tabCount != 0 && this.tabsContainer.getChildAt(i) != null) {
            int left = this.tabsContainer.getChildAt(i).getLeft();
            if (i > 0) {
                left -= this.scrollOffset;
            }
            int scrollX = getScrollX();
            if (left == this.lastScrollX) {
                return;
            }
            if (left < scrollX) {
                this.lastScrollX = left;
                smoothScrollTo(this.lastScrollX, 0);
            } else if (this.scrollOffset + left > (scrollX + getWidth()) - (this.scrollOffset * 2)) {
                this.lastScrollX = (left - getWidth()) + (this.scrollOffset * 3);
                smoothScrollTo(this.lastScrollX, 0);
            }
        }
    }

    public void addIconTab(int i) {
        boolean z = true;
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        View imageView = new ImageView(getContext());
        imageView.setFocusable(true);
        paintTabIcons(i);
        imageView.setImageResource(i);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setOnClickListener(new C14512(i2));
        this.tabsContainer.addView(imageView);
        if (i2 != this.currentPosition) {
            z = false;
        }
        imageView.setSelected(z);
    }

    public void addIconTab(int i, OnLongClickListener onLongClickListener) {
        boolean z = true;
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        View imageView = new ImageView(getContext());
        imageView.setFocusable(true);
        paintTabIcons(i);
        imageView.setImageResource(i);
        imageView.setScaleType(ScaleType.CENTER);
        imageView.setOnClickListener(new C14534(i2));
        imageView.setOnLongClickListener(onLongClickListener);
        this.tabsContainer.addView(imageView);
        if (i2 != this.currentPosition) {
            z = false;
        }
        imageView.setSelected(z);
    }

    public TextView addIconTabWithCounter(int i) {
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        View frameLayout = new FrameLayout(getContext());
        frameLayout.setFocusable(true);
        this.tabsContainer.addView(frameLayout);
        View imageView = new ImageView(getContext());
        imageView.setImageResource(i);
        imageView.setScaleType(ScaleType.CENTER);
        frameLayout.setOnClickListener(new C14501(i2));
        frameLayout.addView(imageView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        frameLayout.setSelected(i2 == this.currentPosition);
        View textView = new TextView(getContext());
        textView.setTypeface(FontUtil.m1176a().m1160c());
        textView.setTextSize(1, 12.0f);
        textView.setTextColor(-1);
        textView.setGravity(17);
        textView.setBackgroundResource(C0338R.drawable.sticker_badge);
        textView.setMinWidth(AndroidUtilities.dp(18.0f));
        textView.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, 18.0f, 51, 26.0f, 6.0f, 0.0f, 0.0f));
        return textView;
    }

    public void addStickerTab(Document document, Object obj, OnLongClickListener onLongClickListener) {
        int i = this.tabCount;
        this.tabCount = i + 1;
        View frameLayout = new FrameLayout(getContext());
        frameLayout.setFocusable(true);
        frameLayout.setOnClickListener(new C14523(i));
        this.tabsContainer.addView(frameLayout);
        frameLayout.setSelected(i == this.currentPosition);
        frameLayout.setTag(obj);
        frameLayout.setOnLongClickListener(onLongClickListener);
        View backupImageView = new BackupImageView(getContext());
        if (!(document == null || document.thumb == null)) {
            backupImageView.setImage(document.thumb.location, null, "webp", null);
        }
        backupImageView.setAspectFit(true);
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            float left;
            float right;
            int height = getHeight();
            this.rectPaint.setColor(this.underlineColor);
            canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            View childAt = this.tabsContainer.getChildAt(this.currentPosition);
            if (childAt != null) {
                left = (float) childAt.getLeft();
                right = (float) childAt.getRight();
            } else {
                right = 0.0f;
                left = 0.0f;
            }
            this.rectPaint.setColor(this.indicatorColor);
            if (this.indicatorHeight == 0) {
                canvas.drawRect(left, 0.0f, right, (float) height, this.rectPaint);
                return;
            }
            canvas.drawRect(left, (float) (height - this.indicatorHeight), right, (float) height, this.rectPaint);
        }
    }

    public void onPageScrolled(int i, int i2) {
        if (this.currentPosition != i) {
            this.currentPosition = i;
            if (i < this.tabsContainer.getChildCount()) {
                int i3 = 0;
                while (i3 < this.tabsContainer.getChildCount()) {
                    this.tabsContainer.getChildAt(i3).setSelected(i3 == i);
                    i3++;
                }
                if (i2 != i || i <= 1) {
                    scrollToChild(i);
                } else {
                    scrollToChild(i - 1);
                }
                invalidate();
            }
        }
    }

    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
    }

    public void removeTabs() {
        this.tabsContainer.removeAllViews();
        this.tabCount = 0;
        this.currentPosition = 0;
    }

    public void selectTab(int i) {
        if (i >= 0 && i < this.tabCount) {
            View childAt = this.tabsContainer.getChildAt(i);
            if (VERSION.SDK_INT >= 15) {
                childAt.callOnClick();
            } else {
                childAt.performClick();
            }
        }
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public void setIndicatorColor(int i) {
        this.indicatorColor = i;
        invalidate();
    }

    public void setIndicatorHeight(int i) {
        this.indicatorHeight = i;
        invalidate();
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

    public void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            this.tabsContainer.getChildAt(i).setLayoutParams(this.defaultTabLayoutParams);
        }
    }
}

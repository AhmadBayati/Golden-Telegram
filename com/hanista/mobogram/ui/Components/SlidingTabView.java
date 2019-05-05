package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class SlidingTabView extends LinearLayout {
    private float animateTabXTo;
    private SlidingTabViewDelegate delegate;
    private DecelerateInterpolator interpolator;
    private Paint paint;
    private int selectedTab;
    private long startAnimationTime;
    private float startAnimationX;
    private int tabCount;
    private float tabWidth;
    private float tabX;
    private long totalAnimationDiff;

    /* renamed from: com.hanista.mobogram.ui.Components.SlidingTabView.1 */
    class C14711 implements OnClickListener {
        final /* synthetic */ int val$position;

        C14711(int i) {
            this.val$position = i;
        }

        public void onClick(View view) {
            SlidingTabView.this.didSelectTab(this.val$position);
        }
    }

    public interface SlidingTabViewDelegate {
        void didSelectTab(int i);
    }

    public SlidingTabView(Context context) {
        super(context);
        this.selectedTab = 0;
        this.tabCount = 0;
        this.tabWidth = 0.0f;
        this.tabX = 0.0f;
        this.animateTabXTo = 0.0f;
        this.paint = new Paint();
        this.startAnimationTime = 0;
        this.totalAnimationDiff = 0;
        this.startAnimationX = 0.0f;
        setOrientation(0);
        setWeightSum(100.0f);
        this.paint.setColor(-1);
        setWillNotDraw(false);
        this.interpolator = new DecelerateInterpolator();
    }

    private void animateToTab(int i) {
        this.animateTabXTo = ((float) i) * this.tabWidth;
        this.startAnimationX = this.tabX;
        this.totalAnimationDiff = 0;
        this.startAnimationTime = System.currentTimeMillis();
        invalidate();
    }

    private void didSelectTab(int i) {
        if (this.selectedTab != i) {
            this.selectedTab = i;
            animateToTab(i);
            if (this.delegate != null) {
                this.delegate.didSelectTab(i);
            }
        }
    }

    public void addTextTab(int i, String str) {
        View textView = new TextView(getContext());
        textView.setText(str);
        textView.setFocusable(true);
        textView.setGravity(17);
        textView.setSingleLine();
        textView.setTextColor(-1);
        textView.setTextSize(1, 14.0f);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false));
        textView.setOnClickListener(new C14711(i));
        addView(textView);
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = 0;
        layoutParams.weight = 50.0f;
        textView.setLayoutParams(layoutParams);
        this.tabCount++;
    }

    public int getSeletedTab() {
        return this.selectedTab;
    }

    protected void onDraw(Canvas canvas) {
        if (this.tabX != this.animateTabXTo) {
            long currentTimeMillis = System.currentTimeMillis() - this.startAnimationTime;
            this.startAnimationTime = System.currentTimeMillis();
            this.totalAnimationDiff = currentTimeMillis + this.totalAnimationDiff;
            if (this.totalAnimationDiff > 200) {
                this.totalAnimationDiff = 200;
                this.tabX = this.animateTabXTo;
            } else {
                this.tabX = this.startAnimationX + (this.interpolator.getInterpolation(((float) this.totalAnimationDiff) / 200.0f) * (this.animateTabXTo - this.startAnimationX));
                invalidate();
            }
        }
        Canvas canvas2 = canvas;
        canvas2.drawRect(this.tabX, (float) (getHeight() - AndroidUtilities.dp(2.0f)), this.tabWidth + this.tabX, (float) getHeight(), this.paint);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.tabWidth = ((float) (i3 - i)) / ((float) this.tabCount);
        float f = this.tabWidth * ((float) this.selectedTab);
        this.tabX = f;
        this.animateTabXTo = f;
    }

    public void setDelegate(SlidingTabViewDelegate slidingTabViewDelegate) {
        this.delegate = slidingTabViewDelegate;
    }
}

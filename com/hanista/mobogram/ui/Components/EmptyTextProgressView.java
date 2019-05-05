package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.mobo.p008i.FontUtil;

public class EmptyTextProgressView extends FrameLayout {
    private boolean inLayout;
    private ProgressBar progressBar;
    private boolean showAtCenter;
    private TextView textView;

    /* renamed from: com.hanista.mobogram.ui.Components.EmptyTextProgressView.1 */
    class C13501 implements OnTouchListener {
        C13501() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    public EmptyTextProgressView(Context context) {
        super(context);
        this.progressBar = new ProgressBar(context);
        this.progressBar.setVisibility(4);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0f));
        this.textView = new TextView(context);
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
        this.textView.setTextSize(1, 20.0f);
        this.textView.setTextColor(-8355712);
        this.textView.setGravity(17);
        this.textView.setVisibility(4);
        this.textView.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.textView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f));
        setOnTouchListener(new C13501());
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.inLayout = true;
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int childCount = getChildCount();
        for (int i7 = 0; i7 < childCount; i7++) {
            View childAt = getChildAt(i7);
            if (childAt.getVisibility() != 8) {
                int measuredWidth = (i5 - childAt.getMeasuredWidth()) / 2;
                int measuredHeight = this.showAtCenter ? ((i6 / 2) - childAt.getMeasuredHeight()) / 2 : (i6 - childAt.getMeasuredHeight()) / 2;
                childAt.layout(measuredWidth, measuredHeight, childAt.getMeasuredWidth() + measuredWidth, childAt.getMeasuredHeight() + measuredHeight);
            }
        }
        this.inLayout = false;
    }

    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }

    public void setShowAtCenter(boolean z) {
        this.showAtCenter = z;
    }

    public void setText(String str) {
        this.textView.setText(str);
    }

    public void setTextSize(int i) {
        this.textView.setTextSize(1, (float) i);
    }

    public void showProgress() {
        this.textView.setVisibility(4);
        this.progressBar.setVisibility(0);
    }

    public void showTextView() {
        this.textView.setVisibility(0);
        this.progressBar.setVisibility(4);
    }
}

package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class TextInfoCell extends FrameLayout {
    private TextView textView;

    public TextInfoCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(-6052957);
        this.textView.setTextSize(1, 13.0f);
        this.textView.setGravity(17);
        this.textView.setPadding(0, AndroidUtilities.dp(19.0f), 0, AndroidUtilities.dp(19.0f));
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
        addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 17, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setText(String str) {
        this.textView.setText(str);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }
}

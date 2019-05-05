package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class LocationLoadingCell extends FrameLayout {
    private ProgressBar progressBar;
    private TextView textView;

    public LocationLoadingCell(Context context) {
        super(context);
        this.progressBar = new ProgressBar(context);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        this.textView = new TextView(context);
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
        this.textView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        this.textView.setTextSize(1, 16.0f);
        this.textView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
        addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec((int) (((float) AndroidUtilities.dp(56.0f)) * 2.5f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setLoading(boolean z) {
        int i = 4;
        this.progressBar.setVisibility(z ? 0 : 4);
        TextView textView = this.textView;
        if (!z) {
            i = 0;
        }
        textView.setVisibility(i);
    }
}

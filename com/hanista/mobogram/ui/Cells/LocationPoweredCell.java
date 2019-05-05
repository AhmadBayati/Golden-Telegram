package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class LocationPoweredCell extends FrameLayout {
    public LocationPoweredCell(Context context) {
        super(context);
        View linearLayout = new LinearLayout(context);
        addView(linearLayout, LayoutHelper.createFrame(-2, -2, 17));
        View textView = new TextView(context);
        textView.setTextSize(1, 16.0f);
        textView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        textView.setText("Powered by");
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
        textView = new ImageView(context);
        textView.setImageResource(C0338R.drawable.foursquare);
        textView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        linearLayout.addView(textView, LayoutHelper.createLinear(35, -2));
        textView = new TextView(context);
        textView.setTextSize(1, 16.0f);
        textView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        textView.setText("Foursquare");
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), C0700C.ENCODING_PCM_32BIT));
    }
}

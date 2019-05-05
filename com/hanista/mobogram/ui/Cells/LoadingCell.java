package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class LoadingCell extends FrameLayout {
    public LoadingCell(Context context) {
        super(context);
        addView(new ProgressBar(context), LayoutHelper.createFrame(-2, -2, 17));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), C0700C.ENCODING_PCM_32BIT));
    }
}

package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;

public class ShadowBottomSectionCell extends View {
    public ShadowBottomSectionCell(Context context) {
        super(context);
        setBackgroundResource(C0338R.drawable.greydivider_bottom);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(6.0f), C0700C.ENCODING_PCM_32BIT));
    }
}

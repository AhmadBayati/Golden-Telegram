package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.hanista.mobogram.messenger.exoplayer.C0700C;

public class EmptyCell extends FrameLayout {
    int cellHeight;

    public EmptyCell(Context context) {
        this(context, 8);
    }

    public EmptyCell(Context context, int i) {
        super(context);
        this.cellHeight = i;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(this.cellHeight, C0700C.ENCODING_PCM_32BIT));
    }

    public void setHeight(int i) {
        this.cellHeight = i;
        requestLayout();
    }
}

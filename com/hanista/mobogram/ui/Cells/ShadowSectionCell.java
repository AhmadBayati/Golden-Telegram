package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class ShadowSectionCell extends View {
    boolean bTheme;
    private int size;

    public ShadowSectionCell(Context context) {
        super(context);
        this.size = 12;
        setBackgroundResource(C0338R.drawable.greydivider);
        this.bTheme = true;
    }

    public ShadowSectionCell(Context context, boolean z) {
        super(context);
        this.size = 12;
        setBackgroundResource(C0338R.drawable.greydivider);
        this.bTheme = z;
    }

    private void setTheme() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2496g;
            if (i == Theme.ACTION_BAR_MODE_SELECTOR_COLOR) {
                setBackgroundResource(C0338R.drawable.greydivider);
            } else {
                setBackgroundColor(i);
            }
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.size), C0700C.ENCODING_PCM_32BIT));
        if (this.bTheme) {
            setTheme();
        }
    }

    public void setSize(int i) {
        this.size = i;
    }
}

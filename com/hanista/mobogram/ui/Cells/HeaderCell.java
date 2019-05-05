package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class HeaderCell extends FrameLayout {
    private TextView textView;

    public HeaderCell(Context context) {
        int i = 5;
        super(context);
        this.textView = new TextView(getContext());
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(FontUtil.m1176a().m1160c());
        this.textView.setTextColor(ThemeUtil.m2485a().m2289c());
        if (ThemeUtil.m2490b()) {
            this.textView.setTextColor(AdvanceTheme.f2491b);
        }
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, i | 48, 17.0f, 15.0f, 17.0f, 0.0f));
    }

    private void setTheme() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2497h;
            int i2 = AdvanceTheme.f2499j;
            setBackgroundColor(i);
            this.textView.setTextColor(i2);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(38.0f), C0700C.ENCODING_PCM_32BIT));
        setTheme();
    }

    public void setText(String str) {
        this.textView.setText(str);
    }
}

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
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class GreySectionCell extends FrameLayout {
    private TextView textView;

    public GreySectionCell(Context context) {
        int i = 5;
        super(context);
        setBackgroundColor(-855310);
        this.textView = new TextView(getContext());
        this.textView.setTextSize(1, 13.0f);
        this.textView.setTypeface(FontUtil.m1176a().m1160c());
        this.textView.setTextColor(-7697782);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setTypeface(FontUtil.m1176a().m1161d());
        View view = this.textView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, i | 48, 16.0f, 0.0f, 16.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setText(String str) {
        this.textView.setText(str);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }
}

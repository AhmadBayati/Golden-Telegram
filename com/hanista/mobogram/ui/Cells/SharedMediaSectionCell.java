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
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class SharedMediaSectionCell extends FrameLayout {
    private TextView textView;

    public SharedMediaSectionCell(Context context) {
        int i = 5;
        super(context);
        this.textView = new TextView(getContext());
        this.textView.setTextSize(1, 14.0f);
        this.textView.setTypeface(FontUtil.m1176a().m1160c());
        this.textView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        View view = this.textView;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, i | 48, 13.0f, 0.0f, 13.0f, 0.0f));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setText(String str) {
        this.textView.setText(str);
    }
}

package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class LetterSectionCell extends FrameLayout {
    private TextView textView;

    public LetterSectionCell(Context context) {
        super(context);
        setLayoutParams(new LayoutParams(AndroidUtilities.dp(54.0f), AndroidUtilities.dp(64.0f)));
        this.textView = new TextView(getContext());
        this.textView.setTextSize(1, 22.0f);
        this.textView.setTypeface(FontUtil.m1176a().m1160c());
        this.textView.setTextColor(-8355712);
        this.textView.setGravity(17);
        addView(this.textView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
    }

    public void setCellHeight(int i) {
        setLayoutParams(new LayoutParams(AndroidUtilities.dp(54.0f), i));
    }

    public void setLetter(String str) {
        this.textView.setText(str.toUpperCase());
    }

    public void setLetterColor(int i) {
        this.textView.setTextColor(i);
    }
}

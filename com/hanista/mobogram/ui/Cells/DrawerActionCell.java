package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class DrawerActionCell extends FrameLayout {
    public TextView textView;

    public DrawerActionCell(Context context) {
        super(context);
        this.textView = new TextView(context);
        this.textView.setTextColor(ThemeUtil.m2485a().m2289c());
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(FontUtil.m1176a().m1160c());
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(19);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(34.0f));
        addView(this.textView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 51, 14.0f, 0.0f, 16.0f, 0.0f));
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            this.textView.setTextColor(AdvanceTheme.f2487X);
            this.textView.setTextSize(1, (float) AdvanceTheme.f2488Y);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C0700C.ENCODING_PCM_32BIT));
        initTheme();
    }

    public void setTextAndIcon(String str, int i) {
        try {
            this.textView.setText(str);
            this.textView.setCompoundDrawablesWithIntrinsicBounds(i, 0, 0, 0);
            if (ThemeUtil.m2490b()) {
                int i2 = AdvanceTheme.f2483T;
                Drawable drawable = getResources().getDrawable(i);
                drawable.setColorFilter(i2, Mode.SRC_IN);
                this.textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
        }
    }

    public void setTextAndIcon(String str, Drawable drawable) {
        try {
            this.textView.setText(str);
            this.textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
        }
    }
}

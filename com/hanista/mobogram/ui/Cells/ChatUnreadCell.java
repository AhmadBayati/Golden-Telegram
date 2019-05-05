package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class ChatUnreadCell extends FrameLayout {
    private TextView textView;

    public ChatUnreadCell(Context context) {
        super(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundResource(C0338R.drawable.newmsg_divider);
        initTheme(frameLayout);
        addView(frameLayout, LayoutHelper.createFrame(-1, 27.0f, 51, 0.0f, 7.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(C0338R.drawable.ic_ab_new);
        initTheme(imageView);
        imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 10.0f, 0.0f));
        this.textView = new TextView(context);
        this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setTextColor(Theme.CHAT_UNREAD_TEXT_COLOR);
        this.textView.setTypeface(FontUtil.m1176a().m1160c());
        initTheme(this.textView);
        addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
    }

    private void initTheme(FrameLayout frameLayout) {
        if (ThemeUtil.m2490b()) {
            int c = AdvanceTheme.m2286c(AdvanceTheme.bn, -855638017);
            if (c != -855638017) {
                frameLayout.setBackgroundColor(c);
            }
        }
    }

    private void initTheme(ImageView imageView) {
        if (ThemeUtil.m2490b()) {
            int c = AdvanceTheme.m2286c(AdvanceTheme.bV, -6113849);
            Drawable drawable = getResources().getDrawable(C0338R.drawable.ic_ab_new);
            drawable.setColorFilter(c, Mode.SRC_IN);
            imageView.setImageDrawable(drawable);
        }
    }

    private void initTheme(TextView textView) {
        if (ThemeUtil.m2490b()) {
            textView.setTextColor(AdvanceTheme.m2286c(AdvanceTheme.bV, -11898217));
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setText(String str) {
        this.textView.setText(str);
    }
}

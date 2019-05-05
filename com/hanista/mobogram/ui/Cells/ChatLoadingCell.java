package com.hanista.mobogram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.google.android.gms.common.ConnectionResult;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class ChatLoadingCell extends FrameLayout {
    private FrameLayout frameLayout;

    public ChatLoadingCell(Context context) {
        super(context);
        this.frameLayout = new FrameLayout(context);
        this.frameLayout.setBackgroundResource(C0338R.drawable.system_loader);
        this.frameLayout.getBackground().setColorFilter(Theme.colorFilter);
        addView(this.frameLayout, LayoutHelper.createFrame(36, 36, 17));
        View progressBar = new ProgressBar(context);
        try {
            progressBar.setIndeterminateDrawable(getResources().getDrawable(C0338R.drawable.loading_animation));
        } catch (Exception e) {
        }
        progressBar.setIndeterminate(true);
        AndroidUtilities.setProgressBarAnimationDuration(progressBar, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED);
        this.frameLayout.addView(progressBar, LayoutHelper.createFrame(32, 32, 17));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), C0700C.ENCODING_PCM_32BIT));
    }

    public void setProgressVisible(boolean z) {
        this.frameLayout.setVisibility(z ? 0 : 4);
    }
}

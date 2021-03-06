package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class PickerBottomLayoutViewer extends FrameLayout {
    public TextView cancelButton;
    public TextView doneButton;
    public TextView doneButtonBadgeTextView;
    private boolean isDarkTheme;

    public PickerBottomLayoutViewer(Context context) {
        this(context, true);
    }

    public PickerBottomLayoutViewer(Context context, boolean z) {
        super(context);
        this.isDarkTheme = z;
        setBackgroundColor(this.isDarkTheme ? -15066598 : -1);
        this.cancelButton = new TextView(context);
        this.cancelButton.setTextSize(1, 14.0f);
        this.cancelButton.setTextColor(this.isDarkTheme ? -1 : -15095832);
        this.cancelButton.setGravity(17);
        this.cancelButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR : Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR, false));
        this.cancelButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.cancelButton.setText(LocaleController.getString("Cancel", C0338R.string.Cancel).toUpperCase());
        this.cancelButton.setTypeface(FontUtil.m1176a().m1160c());
        addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        this.doneButton = new TextView(context);
        this.doneButton.setTextSize(1, 14.0f);
        this.doneButton.setTextColor(this.isDarkTheme ? -1 : -15095832);
        this.doneButton.setGravity(17);
        this.doneButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR : Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR, false));
        this.doneButton.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.doneButton.setText(LocaleController.getString("Send", C0338R.string.Send).toUpperCase());
        this.doneButton.setTypeface(FontUtil.m1176a().m1160c());
        addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        this.doneButtonBadgeTextView = new TextView(context);
        this.doneButtonBadgeTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(-1);
        this.doneButtonBadgeTextView.setGravity(17);
        this.doneButtonBadgeTextView.setBackgroundResource(this.isDarkTheme ? C0338R.drawable.photobadge : C0338R.drawable.bluecounter);
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addView(this.doneButtonBadgeTextView, LayoutHelper.createFrame(-2, 23.0f, 53, 0.0f, 0.0f, 7.0f, 0.0f));
    }

    public void updateSelectedCount(int i, boolean z) {
        int i2 = -1;
        if (i == 0) {
            this.doneButtonBadgeTextView.setVisibility(8);
            if (z) {
                this.doneButton.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
                this.doneButton.setEnabled(false);
                return;
            }
            TextView textView = this.doneButton;
            if (!this.isDarkTheme) {
                i2 = -15095832;
            }
            textView.setTextColor(i2);
            return;
        }
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(i)}));
        textView = this.doneButton;
        if (!this.isDarkTheme) {
            i2 = -15095832;
        }
        textView.setTextColor(i2);
        if (z) {
            this.doneButton.setEnabled(true);
        }
    }
}

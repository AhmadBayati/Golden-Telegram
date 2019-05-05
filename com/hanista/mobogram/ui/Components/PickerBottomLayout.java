package com.hanista.mobogram.ui.Components;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class PickerBottomLayout extends FrameLayout {
    public TextView cancelButton;
    public LinearLayout doneButton;
    public TextView doneButtonBadgeTextView;
    public TextView doneButtonTextView;
    private boolean isDarkTheme;

    public PickerBottomLayout(Context context) {
        this(context, true);
    }

    public PickerBottomLayout(Context context, boolean z) {
        int i = -1;
        super(context);
        this.isDarkTheme = z;
        setBackgroundColor(this.isDarkTheme ? -15066598 : -1);
        this.cancelButton = new TextView(context);
        this.cancelButton.setTextSize(1, 14.0f);
        this.cancelButton.setTextColor(this.isDarkTheme ? -1 : -15095832);
        this.cancelButton.setGravity(17);
        this.cancelButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR : Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR, false));
        this.cancelButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.cancelButton.setText(LocaleController.getString("Cancel", C0338R.string.Cancel).toUpperCase());
        this.cancelButton.setTypeface(FontUtil.m1176a().m1160c());
        addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        this.doneButton = new LinearLayout(context);
        this.doneButton.setOrientation(0);
        this.doneButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.isDarkTheme ? Theme.ACTION_BAR_PICKER_SELECTOR_COLOR : Theme.ACTION_BAR_CHANNEL_INTRO_SELECTOR_COLOR, false));
        this.doneButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
        this.doneButtonBadgeTextView = new TextView(context);
        this.doneButtonBadgeTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.doneButtonBadgeTextView.setTextSize(1, 13.0f);
        this.doneButtonBadgeTextView.setTextColor(-1);
        this.doneButtonBadgeTextView.setGravity(17);
        this.doneButtonBadgeTextView.setBackgroundResource(this.isDarkTheme ? C0338R.drawable.photobadge : C0338R.drawable.bluecounter);
        this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0f));
        this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
        this.doneButtonTextView = new TextView(context);
        this.doneButtonTextView.setTextSize(1, 14.0f);
        TextView textView = this.doneButtonTextView;
        if (!this.isDarkTheme) {
            i = -15095832;
        }
        textView.setTextColor(i);
        this.doneButtonTextView.setGravity(17);
        this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.doneButtonTextView.setText(LocaleController.getString("Send", C0338R.string.Send).toUpperCase());
        this.doneButtonTextView.setTypeface(FontUtil.m1176a().m1160c());
        this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    }

    public void updateSelectedCount(int i, boolean z) {
        int i2 = -1;
        if (i == 0) {
            this.doneButtonBadgeTextView.setVisibility(8);
            if (z) {
                this.doneButtonTextView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
                this.doneButton.setEnabled(false);
                return;
            }
            TextView textView = this.doneButtonTextView;
            if (!this.isDarkTheme) {
                i2 = -15095832;
            }
            textView.setTextColor(i2);
            return;
        }
        this.doneButtonBadgeTextView.setVisibility(0);
        this.doneButtonBadgeTextView.setText(String.format("%d", new Object[]{Integer.valueOf(i)}));
        textView = this.doneButtonTextView;
        if (!this.isDarkTheme) {
            i2 = -15095832;
        }
        textView.setTextColor(i2);
        if (z) {
            this.doneButton.setEnabled(true);
        }
    }
}

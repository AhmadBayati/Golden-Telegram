package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class ChangePhoneHelpActivity extends BaseFragment {

    /* renamed from: com.hanista.mobogram.ui.ChangePhoneHelpActivity.1 */
    class C11561 extends ActionBarMenuOnItemClick {
        C11561() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChangePhoneHelpActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangePhoneHelpActivity.2 */
    class C11572 implements OnTouchListener {
        C11572() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangePhoneHelpActivity.3 */
    class C11593 implements OnClickListener {

        /* renamed from: com.hanista.mobogram.ui.ChangePhoneHelpActivity.3.1 */
        class C11581 implements DialogInterface.OnClickListener {
            C11581() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ChangePhoneHelpActivity.this.presentFragment(new ChangePhoneActivity(), true);
            }
        }

        C11593() {
        }

        public void onClick(View view) {
            if (ChangePhoneHelpActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(ChangePhoneHelpActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setMessage(LocaleController.getString("PhoneNumberAlert", C0338R.string.PhoneNumberAlert));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C11581());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                ChangePhoneHelpActivity.this.showDialog(builder.create());
            }
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        User currentUser = UserConfig.getCurrentUser();
        CharSequence string = (currentUser == null || currentUser.phone == null || currentUser.phone.length() == 0) ? LocaleController.getString("NumberUnknown", C0338R.string.NumberUnknown) : PhoneFormat.getInstance().format("+" + currentUser.phone);
        this.actionBar.setTitle(string);
        this.actionBar.setActionBarMenuOnItemClick(new C11561());
        this.fragmentView = new RelativeLayout(context);
        this.fragmentView.setOnTouchListener(new C11572());
        RelativeLayout relativeLayout = (RelativeLayout) this.fragmentView;
        View scrollView = new ScrollView(context);
        relativeLayout.addView(scrollView);
        LayoutParams layoutParams = (LayoutParams) scrollView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        layoutParams.addRule(15, -1);
        scrollView.setLayoutParams(layoutParams);
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f));
        scrollView.addView(linearLayout);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -2;
        linearLayout.setLayoutParams(layoutParams2);
        scrollView = new ImageView(context);
        scrollView.setImageResource(C0338R.drawable.phone_change);
        linearLayout.addView(scrollView);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.gravity = 1;
        scrollView.setLayoutParams(layoutParams3);
        scrollView = new TextView(context);
        scrollView.setTextSize(1, 16.0f);
        scrollView.setGravity(1);
        scrollView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        scrollView.setTypeface(FontUtil.m1176a().m1161d());
        try {
            scrollView.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", C0338R.string.PhoneNumberHelp)));
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            scrollView.setText(LocaleController.getString("PhoneNumberHelp", C0338R.string.PhoneNumberHelp));
        }
        linearLayout.addView(scrollView);
        layoutParams3 = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.gravity = 1;
        layoutParams3.leftMargin = AndroidUtilities.dp(20.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(20.0f);
        layoutParams3.topMargin = AndroidUtilities.dp(56.0f);
        scrollView.setLayoutParams(layoutParams3);
        scrollView = new TextView(context);
        scrollView.setTextSize(1, 18.0f);
        scrollView.setGravity(1);
        scrollView.setTextColor(Theme.DIALOGS_PRINTING_TEXT_COLOR);
        if (ThemeUtil.m2490b()) {
            scrollView.setTextColor(AdvanceTheme.f2491b);
        }
        scrollView.setText(LocaleController.getString("PhoneNumberChange", C0338R.string.PhoneNumberChange));
        scrollView.setTypeface(FontUtil.m1176a().m1160c());
        scrollView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f));
        linearLayout.addView(scrollView);
        layoutParams3 = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.gravity = 1;
        layoutParams3.leftMargin = AndroidUtilities.dp(20.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(20.0f);
        layoutParams3.topMargin = AndroidUtilities.dp(46.0f);
        scrollView.setLayoutParams(layoutParams3);
        scrollView.setOnClickListener(new C11593());
        return this.fragmentView;
    }
}

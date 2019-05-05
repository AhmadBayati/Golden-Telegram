package com.hanista.mobogram.mobo;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

/* renamed from: com.hanista.mobogram.mobo.o */
public class ReportHelpActivity extends BaseFragment {

    /* renamed from: com.hanista.mobogram.mobo.o.1 */
    class ReportHelpActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ReportHelpActivity f1978a;

        ReportHelpActivity(ReportHelpActivity reportHelpActivity) {
            this.f1978a = reportHelpActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f1978a.finishFragment();
            } else if (i == 1) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                MoboUtils.m1700a(this.f1978a.getParentActivity(), "spam@telegram.org", "SPAM", LocaleController.formatString("ReportSpamEmailContent", C0338R.string.ReportSpamEmailContent, "+" + user.phone, "@" + user.username), null);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.o.2 */
    class ReportHelpActivity implements OnTouchListener {
        final /* synthetic */ ReportHelpActivity f1979a;

        ReportHelpActivity(ReportHelpActivity reportHelpActivity) {
            this.f1979a = reportHelpActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ReportProblemHelp", C0338R.string.ReportProblemHelp));
        this.actionBar.setActionBarMenuOnItemClick(new ReportHelpActivity(this));
        this.actionBar.createMenu().addItem(1, (int) C0338R.drawable.ic_send_mail);
        this.fragmentView = new LinearLayout(context);
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener(new ReportHelpActivity(this));
        View scrollView = new ScrollView(context);
        ((LinearLayout) this.fragmentView).addView(scrollView, LayoutHelper.createLinear(-1, -1));
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        View textView = new TextView(context);
        textView.setTextSize(1, 18.0f);
        textView.setTextColor(Theme.MSG_TEXT_COLOR);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail1", C0338R.string.ReportProblemHelpDetail1)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 17.0f);
        textView.setTextColor(-7697782);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail2", C0338R.string.ReportProblemHelpDetail2)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 18.0f);
        textView.setTextColor(Theme.MSG_TEXT_COLOR);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail3", C0338R.string.ReportProblemHelpDetail3)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 17.0f);
        textView.setTextColor(-7697782);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail4", C0338R.string.ReportProblemHelpDetail4)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 18.0f);
        textView.setTextColor(Theme.MSG_TEXT_COLOR);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail5", C0338R.string.ReportProblemHelpDetail5)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 17.0f);
        textView.setTextColor(-7697782);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail6", C0338R.string.ReportProblemHelpDetail6)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 18.0f);
        textView.setTextColor(Theme.MSG_TEXT_COLOR);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail7", C0338R.string.ReportProblemHelpDetail7)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 17.0f);
        textView.setTextColor(-7697782);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail8", C0338R.string.ReportProblemHelpDetail8)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 18.0f);
        textView.setTextColor(Theme.MSG_TEXT_COLOR);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail9", C0338R.string.ReportProblemHelpDetail9)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 17.0f);
        textView.setTextColor(-7697782);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail10", C0338R.string.ReportProblemHelpDetail10)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 18.0f);
        textView.setTextColor(Theme.MSG_TEXT_COLOR);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail11", C0338R.string.ReportProblemHelpDetail11)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        textView = new TextView(context);
        textView.setTextSize(1, 17.0f);
        textView.setTextColor(-7697782);
        textView.setGravity(5);
        textView.setTypeface(FontUtil.m1176a().m1161d());
        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("ReportProblemHelpDetail12", C0338R.string.ReportProblemHelpDetail12)));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, LocaleController.isRTL ? 5 : 3, 15, 10, 15, 0));
        scrollView.addView(linearLayout, LayoutHelper.createLinear(-1, -1));
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}

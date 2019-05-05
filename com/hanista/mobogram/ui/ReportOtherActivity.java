package com.hanista.mobogram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_reportPeer;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputReportReasonOther;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class ReportOtherActivity extends BaseFragment {
    private static final int done_button = 1;
    private long dialog_id;
    private View doneButton;
    private EditText firstNameField;
    private View headerLabelView;

    /* renamed from: com.hanista.mobogram.ui.ReportOtherActivity.1 */
    class C18601 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.ReportOtherActivity.1.1 */
        class C18591 implements RequestDelegate {
            C18591() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
            }
        }

        C18601() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ReportOtherActivity.this.finishFragment();
            } else if (i == ReportOtherActivity.done_button && ReportOtherActivity.this.firstNameField.getText().length() != 0) {
                TLObject tL_account_reportPeer = new TL_account_reportPeer();
                tL_account_reportPeer.peer = MessagesController.getInputPeer((int) ReportOtherActivity.this.dialog_id);
                tL_account_reportPeer.reason = new TL_inputReportReasonOther();
                tL_account_reportPeer.reason.text = ReportOtherActivity.this.firstNameField.getText().toString();
                ConnectionsManager.getInstance().sendRequest(tL_account_reportPeer, new C18591());
                ReportOtherActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ReportOtherActivity.2 */
    class C18612 implements OnTouchListener {
        C18612() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ReportOtherActivity.3 */
    class C18623 implements OnEditorActionListener {
        C18623() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ReportOtherActivity.this.doneButton == null) {
                return false;
            }
            ReportOtherActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ReportOtherActivity.4 */
    class C18634 implements Runnable {
        C18634() {
        }

        public void run() {
            if (ReportOtherActivity.this.firstNameField != null) {
                ReportOtherActivity.this.firstNameField.requestFocus();
                AndroidUtilities.showKeyboard(ReportOtherActivity.this.firstNameField);
            }
        }
    }

    public ReportOtherActivity(Bundle bundle) {
        super(bundle);
        this.dialog_id = getArguments().getLong("dialog_id", 0);
    }

    public View createView(Context context) {
        int i = 3;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ReportChat", C0338R.string.ReportChat));
        this.actionBar.setActionBarMenuOnItemClick(new C18601());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        View linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(done_button);
        this.fragmentView.setOnTouchListener(new C18612());
        this.firstNameField = new EditText(context);
        this.firstNameField.setTextSize(done_button, 18.0f);
        this.firstNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.firstNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.firstNameField.setMaxLines(3);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        EditText editText = this.firstNameField;
        if (LocaleController.isRTL) {
            i = 5;
        }
        editText.setGravity(i);
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        this.firstNameField.setOnEditorActionListener(new C18623());
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setHint(LocaleController.getString("ReportChatDescription", C0338R.string.ReportChatDescription));
        this.firstNameField.setSelection(this.firstNameField.length());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new C18634(), 100);
        }
    }
}

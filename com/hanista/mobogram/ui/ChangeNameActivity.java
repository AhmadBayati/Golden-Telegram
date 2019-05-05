package com.hanista.mobogram.ui;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
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
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_updateProfile;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class ChangeNameActivity extends BaseFragment {
    private static final int done_button = 1;
    private View doneButton;
    private EditText firstNameField;
    private View headerLabelView;
    private EditText lastNameField;

    /* renamed from: com.hanista.mobogram.ui.ChangeNameActivity.1 */
    class C11231 extends ActionBarMenuOnItemClick {
        C11231() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChangeNameActivity.this.finishFragment();
            } else if (i == ChangeNameActivity.done_button && ChangeNameActivity.this.firstNameField.getText().length() != 0) {
                ChangeNameActivity.this.saveName();
                ChangeNameActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeNameActivity.2 */
    class C11242 implements OnTouchListener {
        C11242() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeNameActivity.3 */
    class C11253 implements OnEditorActionListener {
        C11253() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            ChangeNameActivity.this.lastNameField.requestFocus();
            ChangeNameActivity.this.lastNameField.setSelection(ChangeNameActivity.this.lastNameField.length());
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeNameActivity.4 */
    class C11264 implements OnEditorActionListener {
        C11264() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6) {
                return false;
            }
            ChangeNameActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeNameActivity.5 */
    class C11275 implements RequestDelegate {
        C11275() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeNameActivity.6 */
    class C11286 implements Runnable {
        C11286() {
        }

        public void run() {
            if (ChangeNameActivity.this.firstNameField != null) {
                ChangeNameActivity.this.firstNameField.requestFocus();
                AndroidUtilities.showKeyboard(ChangeNameActivity.this.firstNameField);
            }
        }
    }

    private void saveName() {
        User currentUser = UserConfig.getCurrentUser();
        if (currentUser != null && this.lastNameField.getText() != null && this.firstNameField.getText() != null) {
            String obj = this.firstNameField.getText().toString();
            String obj2 = this.lastNameField.getText().toString();
            if (currentUser.first_name == null || !currentUser.first_name.equals(obj) || currentUser.last_name == null || !currentUser.last_name.equals(obj2)) {
                TLObject tL_account_updateProfile = new TL_account_updateProfile();
                tL_account_updateProfile.flags = 3;
                tL_account_updateProfile.first_name = obj;
                currentUser.first_name = obj;
                tL_account_updateProfile.last_name = obj2;
                currentUser.last_name = obj2;
                currentUser = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                if (currentUser != null) {
                    currentUser.first_name = tL_account_updateProfile.first_name;
                    currentUser.last_name = tL_account_updateProfile.last_name;
                }
                UserConfig.saveConfig(true);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter instance = NotificationCenter.getInstance();
                int i = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[done_button];
                objArr[0] = Integer.valueOf(done_button);
                instance.postNotificationName(i, objArr);
                ConnectionsManager.getInstance().sendRequest(tL_account_updateProfile, new C11275());
            }
        }
    }

    public View createView(Context context) {
        int i = 5;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EditName", C0338R.string.EditName));
        this.actionBar.setActionBarMenuOnItemClick(new C11231());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.SRC_IN);
            this.doneButton = createMenu.addItemWithWidth((int) done_button, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.doneButton = createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        User currentUser = user == null ? UserConfig.getCurrentUser() : user;
        View linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(done_button);
        this.fragmentView.setOnTouchListener(new C11242());
        this.firstNameField = new EditText(context);
        this.firstNameField.setTextSize(done_button, 18.0f);
        this.firstNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.firstNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        if (ThemeUtil.m2490b()) {
            this.firstNameField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
        }
        this.firstNameField.setMaxLines(done_button);
        this.firstNameField.setLines(done_button);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", C0338R.string.FirstName));
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new C11253());
        this.lastNameField = new EditText(context);
        this.lastNameField.setTextSize(done_button, 18.0f);
        this.lastNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.lastNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        if (ThemeUtil.m2490b()) {
            this.lastNameField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
        }
        this.lastNameField.setMaxLines(done_button);
        this.lastNameField.setLines(done_button);
        this.lastNameField.setSingleLine(true);
        EditText editText = this.lastNameField;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        editText.setGravity(i);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(6);
        this.lastNameField.setHint(LocaleController.getString("LastName", C0338R.string.LastName));
        AndroidUtilities.clearCursorDrawable(this.lastNameField);
        linearLayout.addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 16.0f, 24.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new C11264());
        if (currentUser != null) {
            this.firstNameField.setText(currentUser.first_name);
            this.firstNameField.setSelection(this.firstNameField.length());
            this.lastNameField.setText(currentUser.last_name);
        }
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
        initThemeActionBar();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new C11286(), 100);
        }
    }
}

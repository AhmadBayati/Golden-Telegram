package com.hanista.mobogram.ui;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
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
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class ChangeChatNameActivity extends BaseFragment {
    private static final int done_button = 1;
    private int chat_id;
    private View doneButton;
    private EditText firstNameField;
    private View headerLabelView;

    /* renamed from: com.hanista.mobogram.ui.ChangeChatNameActivity.1 */
    class C11191 extends ActionBarMenuOnItemClick {
        C11191() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChangeChatNameActivity.this.finishFragment();
            } else if (i == ChangeChatNameActivity.done_button && ChangeChatNameActivity.this.firstNameField.getText().length() != 0) {
                ChangeChatNameActivity.this.saveName();
                ChangeChatNameActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeChatNameActivity.2 */
    class C11202 implements OnTouchListener {
        C11202() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeChatNameActivity.3 */
    class C11213 implements OnEditorActionListener {
        C11213() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChangeChatNameActivity.this.doneButton == null) {
                return false;
            }
            ChangeChatNameActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChangeChatNameActivity.4 */
    class C11224 implements Runnable {
        C11224() {
        }

        public void run() {
            if (ChangeChatNameActivity.this.firstNameField != null) {
                ChangeChatNameActivity.this.firstNameField.requestFocus();
                AndroidUtilities.showKeyboard(ChangeChatNameActivity.this.firstNameField);
            }
        }
    }

    public ChangeChatNameActivity(Bundle bundle) {
        super(bundle);
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            initThemeActionBar();
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done).setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
        }
    }

    private void saveName() {
        MessagesController.getInstance().changeChatTitle(this.chat_id, this.firstNameField.getText().toString());
    }

    public View createView(Context context) {
        int i = 3;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EditName", C0338R.string.EditName));
        this.actionBar.setActionBarMenuOnItemClick(new C11191());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            this.doneButton = createMenu.addItemWithWidth((int) done_button, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.doneButton = createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
        View linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(done_button);
        this.fragmentView.setOnTouchListener(new C11202());
        this.firstNameField = new EditText(context);
        this.firstNameField.setText(chat.title);
        this.firstNameField.setTextSize(done_button, 18.0f);
        this.firstNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.firstNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        if (ThemeUtil.m2490b()) {
            this.firstNameField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
        }
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
        this.firstNameField.setOnEditorActionListener(new C11213());
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        if (this.chat_id > 0) {
            this.firstNameField.setHint(LocaleController.getString("GroupName", C0338R.string.GroupName));
        } else {
            this.firstNameField.setHint(LocaleController.getString("EnterListName", C0338R.string.EnterListName));
        }
        this.firstNameField.setSelection(this.firstNameField.length());
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.chat_id = getArguments().getInt("chat_id", 0);
        return true;
    }

    public void onResume() {
        super.onResume();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
        initTheme();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new C11224(), 100);
        }
    }
}

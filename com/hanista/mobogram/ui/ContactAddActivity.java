package com.hanista.mobogram.ui;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;

public class ContactAddActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private boolean addContact;
    private BackupImageView avatarImage;
    private View doneButton;
    private EditText firstNameField;
    private EditText lastNameField;
    private TextView nameTextView;
    private TextView onlineTextView;
    private String phone;
    private int user_id;

    /* renamed from: com.hanista.mobogram.ui.ContactAddActivity.1 */
    class C15061 extends ActionBarMenuOnItemClick {
        C15061() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ContactAddActivity.this.finishFragment();
            } else if (i == ContactAddActivity.done_button && ContactAddActivity.this.firstNameField.getText().length() != 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(ContactAddActivity.this.user_id));
                user.first_name = ContactAddActivity.this.firstNameField.getText().toString();
                user.last_name = ContactAddActivity.this.lastNameField.getText().toString();
                ContactsController.getInstance().addContact(user);
                ContactAddActivity.this.finishFragment();
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("spam3_" + ContactAddActivity.this.user_id, ContactAddActivity.done_button).commit();
                NotificationCenter instance = NotificationCenter.getInstance();
                int i2 = NotificationCenter.updateInterfaces;
                Object[] objArr = new Object[ContactAddActivity.done_button];
                objArr[0] = Integer.valueOf(ContactAddActivity.done_button);
                instance.postNotificationName(i2, objArr);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactAddActivity.2 */
    class C15072 implements OnTouchListener {
        C15072() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactAddActivity.3 */
    class C15083 implements OnEditorActionListener {
        C15083() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            ContactAddActivity.this.lastNameField.requestFocus();
            ContactAddActivity.this.lastNameField.setSelection(ContactAddActivity.this.lastNameField.length());
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ContactAddActivity.4 */
    class C15094 implements OnEditorActionListener {
        C15094() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6) {
                return false;
            }
            ContactAddActivity.this.doneButton.performClick();
            return true;
        }
    }

    public ContactAddActivity(Bundle bundle) {
        super(bundle);
        this.phone = null;
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            if (this.fragmentView != null) {
                this.fragmentView.setBackgroundColor(AdvanceTheme.f2497h);
            }
            if (this.avatarImage != null) {
                this.avatarImage.setRoundRadius(AndroidUtilities.dp((float) AdvanceTheme.f2502m));
            }
            if (this.nameTextView != null) {
                if (this.nameTextView.getBackground() != null) {
                    this.nameTextView.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.nameTextView.setTextColor(AdvanceTheme.m2286c(AdvanceTheme.ax, Theme.STICKERS_SHEET_TITLE_TEXT_COLOR));
            }
            if (this.onlineTextView != null) {
                if (this.onlineTextView.getBackground() != null) {
                    this.onlineTextView.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.onlineTextView.setTextColor(AdvanceTheme.aJ);
            }
            if (this.firstNameField != null) {
                if (this.firstNameField.getBackground() != null) {
                    this.firstNameField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.firstNameField.setHintTextColor(AdvanceTheme.f2495f);
                this.firstNameField.setTextColor(AdvanceTheme.f2494e);
            }
            if (this.lastNameField != null) {
                if (this.lastNameField.getBackground() != null) {
                    this.lastNameField.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.lastNameField.setHintTextColor(AdvanceTheme.f2495f);
                this.lastNameField.setTextColor(AdvanceTheme.f2494e);
            }
        }
    }

    private void updateAvatarLayout() {
        if (this.nameTextView != null) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            if (user != null) {
                this.nameTextView.setText(PhoneFormat.getInstance().format("+" + user.phone));
                this.onlineTextView.setText(LocaleController.formatUserStatus(user));
                TLObject tLObject = null;
                if (user.photo != null) {
                    tLObject = user.photo.photo_small;
                }
                this.avatarImage.setImage(tLObject, "50_50", new AvatarDrawable(user));
            }
        }
    }

    public View createView(Context context) {
        int i = 5;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.addContact) {
            this.actionBar.setTitle(LocaleController.getString("AddContactTitle", C0338R.string.AddContactTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("EditName", C0338R.string.EditName));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C15061());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context);
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(done_button);
        ((ScrollView) this.fragmentView).addView(linearLayout);
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnTouchListener(new C15072());
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams2.topMargin = AndroidUtilities.dp(24.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(24.0f);
        layoutParams2.rightMargin = AndroidUtilities.dp(24.0f);
        layoutParams2.width = -1;
        layoutParams2.height = -2;
        frameLayout.setLayoutParams(layoutParams2);
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
        frameLayout.addView(this.avatarImage);
        layoutParams = (LayoutParams) this.avatarImage.getLayoutParams();
        layoutParams.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        layoutParams.width = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        layoutParams.height = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW);
        this.avatarImage.setLayoutParams(layoutParams);
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.nameTextView.setTextSize(done_button, 20.0f);
        this.nameTextView.setLines(done_button);
        this.nameTextView.setMaxLines(done_button);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.nameTextView.setTypeface(FontUtil.m1176a().m1160c());
        frameLayout.addView(this.nameTextView);
        layoutParams = (LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 80.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 80.0f : 0.0f);
        layoutParams.topMargin = AndroidUtilities.dp(3.0f);
        layoutParams.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        this.nameTextView.setLayoutParams(layoutParams);
        this.onlineTextView = new TextView(context);
        this.onlineTextView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
        this.onlineTextView.setTextSize(done_button, 14.0f);
        this.onlineTextView.setLines(done_button);
        this.onlineTextView.setMaxLines(done_button);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        frameLayout.addView(this.onlineTextView);
        layoutParams = (LayoutParams) this.onlineTextView.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 80.0f);
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? 80.0f : 0.0f);
        layoutParams.topMargin = AndroidUtilities.dp(32.0f);
        layoutParams.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        this.onlineTextView.setLayoutParams(layoutParams);
        this.firstNameField = new EditText(context);
        this.firstNameField.setTextSize(done_button, 18.0f);
        this.firstNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.firstNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.firstNameField.setMaxLines(done_button);
        this.firstNameField.setLines(done_button);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", C0338R.string.FirstName));
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        linearLayout.addView(this.firstNameField);
        layoutParams2 = (LinearLayout.LayoutParams) this.firstNameField.getLayoutParams();
        layoutParams2.topMargin = AndroidUtilities.dp(24.0f);
        layoutParams2.height = AndroidUtilities.dp(36.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(24.0f);
        layoutParams2.rightMargin = AndroidUtilities.dp(24.0f);
        layoutParams2.width = -1;
        this.firstNameField.setLayoutParams(layoutParams2);
        this.firstNameField.setOnEditorActionListener(new C15083());
        this.lastNameField = new EditText(context);
        this.lastNameField.setTextSize(done_button, 18.0f);
        this.lastNameField.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.lastNameField.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
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
        linearLayout.addView(this.lastNameField);
        layoutParams2 = (LinearLayout.LayoutParams) this.lastNameField.getLayoutParams();
        layoutParams2.topMargin = AndroidUtilities.dp(16.0f);
        layoutParams2.height = AndroidUtilities.dp(36.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(24.0f);
        layoutParams2.rightMargin = AndroidUtilities.dp(24.0f);
        layoutParams2.width = -1;
        this.lastNameField.setLayoutParams(layoutParams2);
        this.lastNameField.setOnEditorActionListener(new C15094());
        User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
        if (user != null) {
            if (user.phone == null && this.phone != null) {
                user.phone = PhoneFormat.stripExceptNumbers(this.phone);
            }
            this.firstNameField.setText(user.first_name);
            this.firstNameField.setSelection(this.firstNameField.length());
            this.lastNameField.setText(user.last_name);
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if ((intValue & 2) != 0 || (intValue & 4) != 0) {
                updateAvatarLayout();
            }
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        this.user_id = getArguments().getInt("user_id", 0);
        this.phone = getArguments().getString("phone");
        this.addContact = getArguments().getBoolean("addContact", false);
        return MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)) != null && super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onResume() {
        super.onResume();
        updateAvatarLayout();
        if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
        initThemeActionBar();
        initTheme();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }
}

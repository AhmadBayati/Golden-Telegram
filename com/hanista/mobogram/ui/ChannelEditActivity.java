package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatPhoto;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.AvatarUpdater;
import com.hanista.mobogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.concurrent.Semaphore;

public class ChannelEditActivity extends BaseFragment implements NotificationCenterDelegate, AvatarUpdaterDelegate {
    private static final int done_button = 1;
    private TextSettingsCell adminCell;
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int chatId;
    private boolean createAfterUpload;
    private Chat currentChat;
    private EditText descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private ChatFull info;
    private EditText nameTextView;
    private ProgressDialog progressDialog;
    private boolean signMessages;
    private TextSettingsCell typeCell;
    private InputFile uploadedAvatar;

    /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ InputFile val$file;
        final /* synthetic */ PhotoSize val$small;

        AnonymousClass10(InputFile inputFile, PhotoSize photoSize) {
            this.val$file = inputFile;
            this.val$small = photoSize;
        }

        public void run() {
            ChannelEditActivity.this.uploadedAvatar = this.val$file;
            ChannelEditActivity.this.avatar = this.val$small.location;
            ChannelEditActivity.this.avatarImage.setImage(ChannelEditActivity.this.avatar, "50_50", ChannelEditActivity.this.avatarDrawable);
            if (ChannelEditActivity.this.createAfterUpload) {
                try {
                    if (ChannelEditActivity.this.progressDialog != null && ChannelEditActivity.this.progressDialog.isShowing()) {
                        ChannelEditActivity.this.progressDialog.dismiss();
                        ChannelEditActivity.this.progressDialog = null;
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                ChannelEditActivity.this.doneButton.performClick();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.1 */
    class C11921 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;

        C11921(Semaphore semaphore) {
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChannelEditActivity.this.currentChat = MessagesStorage.getInstance().getChat(ChannelEditActivity.this.chatId);
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.2 */
    class C11942 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.2.1 */
        class C11931 implements OnClickListener {
            C11931() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ChannelEditActivity.this.createAfterUpload = false;
                ChannelEditActivity.this.progressDialog = null;
                ChannelEditActivity.this.donePressed = false;
                try {
                    dialogInterface.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        C11942() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChannelEditActivity.this.finishFragment();
            } else if (i == ChannelEditActivity.done_button && !ChannelEditActivity.this.donePressed) {
                if (ChannelEditActivity.this.nameTextView.length() == 0) {
                    Vibrator vibrator = (Vibrator) ChannelEditActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                    AndroidUtilities.shakeView(ChannelEditActivity.this.nameTextView, 2.0f, 0);
                    return;
                }
                ChannelEditActivity.this.donePressed = true;
                if (ChannelEditActivity.this.avatarUpdater.uploadingAvatar != null) {
                    ChannelEditActivity.this.createAfterUpload = true;
                    ChannelEditActivity.this.progressDialog = new ProgressDialog(ChannelEditActivity.this.getParentActivity());
                    ChannelEditActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
                    ChannelEditActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                    ChannelEditActivity.this.progressDialog.setCancelable(false);
                    ChannelEditActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", C0338R.string.Cancel), new C11931());
                    ChannelEditActivity.this.progressDialog.show();
                    return;
                }
                if (!ChannelEditActivity.this.currentChat.title.equals(ChannelEditActivity.this.nameTextView.getText().toString())) {
                    MessagesController.getInstance().changeChatTitle(ChannelEditActivity.this.chatId, ChannelEditActivity.this.nameTextView.getText().toString());
                }
                if (!(ChannelEditActivity.this.info == null || ChannelEditActivity.this.info.about.equals(ChannelEditActivity.this.descriptionTextView.getText().toString()))) {
                    MessagesController.getInstance().updateChannelAbout(ChannelEditActivity.this.chatId, ChannelEditActivity.this.descriptionTextView.getText().toString(), ChannelEditActivity.this.info);
                }
                if (ChannelEditActivity.this.signMessages != ChannelEditActivity.this.currentChat.signatures) {
                    ChannelEditActivity.this.currentChat.signatures = true;
                    MessagesController.getInstance().toogleChannelSignatures(ChannelEditActivity.this.chatId, ChannelEditActivity.this.signMessages);
                }
                if (ChannelEditActivity.this.uploadedAvatar != null) {
                    MessagesController.getInstance().changeChatAvatar(ChannelEditActivity.this.chatId, ChannelEditActivity.this.uploadedAvatar);
                } else if (ChannelEditActivity.this.avatar == null && (ChannelEditActivity.this.currentChat.photo instanceof TL_chatPhoto)) {
                    MessagesController.getInstance().changeChatAvatar(ChannelEditActivity.this.chatId, null);
                }
                ChannelEditActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.3 */
    class C11963 implements View.OnClickListener {

        /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.3.1 */
        class C11951 implements OnClickListener {
            C11951() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ChannelEditActivity.this.avatarUpdater.openCamera();
                } else if (i == ChannelEditActivity.done_button) {
                    ChannelEditActivity.this.avatarUpdater.openGallery();
                } else if (i == 2) {
                    ChannelEditActivity.this.avatar = null;
                    ChannelEditActivity.this.uploadedAvatar = null;
                    ChannelEditActivity.this.avatarImage.setImage(ChannelEditActivity.this.avatar, "50_50", ChannelEditActivity.this.avatarDrawable);
                }
            }
        }

        C11963() {
        }

        public void onClick(View view) {
            if (ChannelEditActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(ChannelEditActivity.this.getParentActivity());
                builder.setItems(ChannelEditActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", C0338R.string.FromCamera), LocaleController.getString("FromGalley", C0338R.string.FromGalley), LocaleController.getString("DeletePhoto", C0338R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", C0338R.string.FromCamera), LocaleController.getString("FromGalley", C0338R.string.FromGalley)}, new C11951());
                ChannelEditActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.4 */
    class C11974 implements TextWatcher {
        C11974() {
        }

        public void afterTextChanged(Editable editable) {
            ChannelEditActivity.this.avatarDrawable.setInfo(5, ChannelEditActivity.this.nameTextView.length() > 0 ? ChannelEditActivity.this.nameTextView.getText().toString() : null, null, false);
            ChannelEditActivity.this.avatarImage.invalidate();
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.5 */
    class C11985 implements OnEditorActionListener {
        C11985() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChannelEditActivity.this.doneButton == null) {
                return false;
            }
            ChannelEditActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.6 */
    class C11996 implements TextWatcher {
        C11996() {
        }

        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.7 */
    class C12007 implements View.OnClickListener {
        C12007() {
        }

        public void onClick(View view) {
            ChannelEditActivity.this.signMessages = !ChannelEditActivity.this.signMessages;
            ((TextCheckCell) view).setChecked(ChannelEditActivity.this.signMessages);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.8 */
    class C12018 implements View.OnClickListener {
        C12018() {
        }

        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", ChannelEditActivity.this.chatId);
            bundle.putInt("type", ChannelEditActivity.done_button);
            ChannelEditActivity.this.presentFragment(new ChannelUsersActivity(bundle));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.9 */
    class C12039 implements View.OnClickListener {

        /* renamed from: com.hanista.mobogram.ui.ChannelEditActivity.9.1 */
        class C12021 implements OnClickListener {
            C12021() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
                if (AndroidUtilities.isTablet()) {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i2 = NotificationCenter.closeChats;
                    Object[] objArr = new Object[ChannelEditActivity.done_button];
                    objArr[0] = Long.valueOf(-((long) ChannelEditActivity.this.chatId));
                    instance.postNotificationName(i2, objArr);
                } else {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                MessagesController.getInstance().deleteUserFromChat(ChannelEditActivity.this.chatId, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), ChannelEditActivity.this.info);
                ChannelEditActivity.this.finishFragment();
            }
        }

        C12039() {
        }

        public void onClick(View view) {
            Builder builder = new Builder(ChannelEditActivity.this.getParentActivity());
            if (ChannelEditActivity.this.currentChat.megagroup) {
                builder.setMessage(LocaleController.getString("MegaDeleteAlert", C0338R.string.MegaDeleteAlert));
            } else {
                builder.setMessage(LocaleController.getString("ChannelDeleteAlert", C0338R.string.ChannelDeleteAlert));
            }
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C12021());
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            ChannelEditActivity.this.showDialog(builder.create());
        }
    }

    public ChannelEditActivity(Bundle bundle) {
        super(bundle);
        this.avatarDrawable = new AvatarDrawable();
        this.avatarUpdater = new AvatarUpdater();
        this.chatId = bundle.getInt("chat_id", 0);
    }

    private void updateAdminCell() {
        if (this.adminCell != null) {
            if (this.info != null) {
                TextSettingsCell textSettingsCell = this.adminCell;
                String string = LocaleController.getString("ChannelAdministrators", C0338R.string.ChannelAdministrators);
                Object[] objArr = new Object[done_button];
                objArr[0] = Integer.valueOf(this.info.admins_count);
                textSettingsCell.setTextAndValue(string, String.format("%d", objArr), false);
                return;
            }
            this.adminCell.setText(LocaleController.getString("ChannelAdministrators", C0338R.string.ChannelAdministrators), false);
        }
    }

    private void updateTypeCell() {
        String string = (this.currentChat.username == null || this.currentChat.username.length() == 0) ? LocaleController.getString("ChannelTypePrivate", C0338R.string.ChannelTypePrivate) : LocaleController.getString("ChannelTypePublic", C0338R.string.ChannelTypePublic);
        if (this.currentChat.megagroup) {
            this.typeCell.setTextAndValue(LocaleController.getString("GroupType", C0338R.string.GroupType), string, false);
        } else {
            this.typeCell.setTextAndValue(LocaleController.getString("ChannelType", C0338R.string.ChannelType), string, false);
        }
        if (this.currentChat.creator && (this.info == null || this.info.can_set_username)) {
            this.typeCell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", ChannelEditActivity.this.chatId);
                    BaseFragment channelEditTypeActivity = new ChannelEditTypeActivity(bundle);
                    channelEditTypeActivity.setInfo(ChannelEditActivity.this.info);
                    ChannelEditActivity.this.presentFragment(channelEditTypeActivity);
                }
            });
            this.typeCell.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            this.typeCell.setTextValueColor(-13660983);
            return;
        }
        this.typeCell.setOnClickListener(null);
        this.typeCell.setTextColor(-5723992);
        this.typeCell.setTextValueColor(-5723992);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C11942());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
            this.doneButton = createMenu.addItemWithWidth((int) done_button, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.doneButton = createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        int i = AdvanceTheme.aA;
        int i2 = AdvanceTheme.aB;
        this.fragmentView = new ScrollView(context);
        if (ThemeUtil.m2490b()) {
            this.fragmentView.setBackgroundColor(i != -1 ? i : Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        } else {
            this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        }
        ScrollView scrollView = (ScrollView) this.fragmentView;
        scrollView.setFillViewport(true);
        View linearLayout = new LinearLayout(context);
        scrollView.addView(linearLayout, new LayoutParams(-1, -2));
        linearLayout.setOrientation(done_button);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", C0338R.string.ChannelEdit));
        View linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(done_button);
        if (ThemeUtil.m2490b()) {
            linearLayout2.setBackgroundColor(i);
        } else {
            linearLayout2.setBackgroundColor(-1);
        }
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        View frameLayout = new FrameLayout(context);
        linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, false);
        this.avatarDrawable.setDrawPhoto(true);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
        this.avatarImage.setOnClickListener(new C11963());
        this.nameTextView = new EditText(context);
        if (this.currentChat.megagroup) {
            this.nameTextView.setHint(LocaleController.getString("GroupName", C0338R.string.GroupName));
        } else {
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", C0338R.string.EnterChannelName));
        }
        this.nameTextView.setMaxLines(4);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.nameTextView.setTextSize(done_button, 16.0f);
        this.nameTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.nameTextView.setImeOptions(268435456);
        this.nameTextView.setInputType(16385);
        this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        InputFilter[] inputFilterArr = new InputFilter[done_button];
        inputFilterArr[0] = new LengthFilter(100);
        this.nameTextView.setFilters(inputFilterArr);
        AndroidUtilities.clearCursorDrawable(this.nameTextView);
        if (ThemeUtil.m2490b()) {
            this.nameTextView.setTextColor(i2);
        } else {
            this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        }
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 16.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 16.0f, 0.0f));
        this.nameTextView.addTextChangedListener(new C11974());
        linearLayout2 = new View(context);
        linearLayout2.setBackgroundColor(-3158065);
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(-1, done_button));
        View linearLayout3 = new LinearLayout(context);
        linearLayout3.setOrientation(done_button);
        if (ThemeUtil.m2490b()) {
            linearLayout3.setBackgroundColor(i);
        } else {
            linearLayout3.setBackgroundColor(-1);
        }
        linearLayout.addView(linearLayout3, LayoutHelper.createLinear(-1, -2));
        this.descriptionTextView = new EditText(context);
        this.descriptionTextView.setTextSize(done_button, 16.0f);
        this.descriptionTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        if (ThemeUtil.m2490b()) {
            this.descriptionTextView.setTextColor(i != -1 ? i2 : Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        } else {
            this.descriptionTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        }
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.descriptionTextView.setBackgroundDrawable(null);
        this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.descriptionTextView.setInputType(180225);
        this.descriptionTextView.setImeOptions(6);
        inputFilterArr = new InputFilter[done_button];
        inputFilterArr[0] = new LengthFilter(NalUnitUtil.EXTENDED_SAR);
        this.descriptionTextView.setFilters(inputFilterArr);
        this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", C0338R.string.DescriptionOptionalPlaceholder));
        AndroidUtilities.clearCursorDrawable(this.descriptionTextView);
        linearLayout3.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 17.0f, 12.0f, 17.0f, 6.0f));
        this.descriptionTextView.setOnEditorActionListener(new C11985());
        this.descriptionTextView.addTextChangedListener(new C11996());
        linearLayout2 = new ShadowSectionCell(context);
        linearLayout2.setSize(20);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.megagroup || !this.currentChat.megagroup) {
            linearLayout2 = new FrameLayout(context);
            linearLayout2.setBackgroundColor(-1);
            linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
            this.typeCell = new TextSettingsCell(context);
            updateTypeCell();
            this.typeCell.setBackgroundResource(C0338R.drawable.list_selector);
            linearLayout2.addView(this.typeCell, LayoutHelper.createFrame(-1, -2.0f));
            linearLayout2 = new View(context);
            linearLayout2.setBackgroundColor(-3158065);
            linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(-1, done_button));
            linearLayout2 = new FrameLayout(context);
            linearLayout2.setBackgroundColor(-1);
            linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
            if (this.currentChat.megagroup) {
                this.adminCell = new TextSettingsCell(context);
                updateAdminCell();
                this.adminCell.setBackgroundResource(C0338R.drawable.list_selector);
                linearLayout2.addView(this.adminCell, LayoutHelper.createFrame(-1, -2.0f));
                this.adminCell.setOnClickListener(new C12018());
                linearLayout2 = new ShadowSectionCell(context);
                linearLayout2.setSize(20);
                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
                if (!this.currentChat.creator) {
                    linearLayout2.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                }
            } else {
                View textCheckCell = new TextCheckCell(context);
                textCheckCell.setBackgroundResource(C0338R.drawable.list_selector);
                textCheckCell.setTextAndCheck(LocaleController.getString("ChannelSignMessages", C0338R.string.ChannelSignMessages), this.signMessages, false);
                linearLayout2.addView(textCheckCell, LayoutHelper.createFrame(-1, -2.0f));
                textCheckCell.setOnClickListener(new C12007());
                linearLayout2 = new TextInfoPrivacyCell(context);
                linearLayout2.setBackgroundResource(C0338R.drawable.greydivider);
                linearLayout2.setText(LocaleController.getString("ChannelSignMessagesInfo", C0338R.string.ChannelSignMessagesInfo));
                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
            }
        }
        if (this.currentChat.creator) {
            linearLayout2 = new FrameLayout(context);
            linearLayout2.setBackgroundColor(-1);
            linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
            textCheckCell = new TextSettingsCell(context);
            textCheckCell.setTextColor(-1229511);
            textCheckCell.setBackgroundResource(C0338R.drawable.list_selector);
            if (this.currentChat.megagroup) {
                textCheckCell.setText(LocaleController.getString("DeleteMega", C0338R.string.DeleteMega), false);
            } else {
                textCheckCell.setText(LocaleController.getString("ChannelDelete", C0338R.string.ChannelDelete), false);
            }
            linearLayout2.addView(textCheckCell, LayoutHelper.createFrame(-1, -2.0f));
            textCheckCell.setOnClickListener(new C12039());
            linearLayout2 = new TextInfoPrivacyCell(context);
            linearLayout2.setBackgroundResource(C0338R.drawable.greydivider_bottom);
            if (this.currentChat.megagroup) {
                linearLayout2.setText(LocaleController.getString("MegaDeleteInfo", C0338R.string.MegaDeleteInfo));
            } else {
                linearLayout2.setText(LocaleController.getString("ChannelDeleteInfo", C0338R.string.ChannelDeleteInfo));
            }
            linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        }
        this.nameTextView.setText(this.currentChat.title);
        this.nameTextView.setSelection(this.nameTextView.length());
        if (this.info != null) {
            this.descriptionTextView.setText(this.info.about);
        }
        if (this.currentChat.photo != null) {
            this.avatar = this.currentChat.photo.photo_small;
            this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable);
        } else {
            this.avatarImage.setImageDrawable(this.avatarDrawable);
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null) {
                    this.descriptionTextView.setText(chatFull.about);
                }
                this.info = chatFull;
                updateAdminCell();
                updateTypeCell();
            }
        } else if (i == NotificationCenter.updateInterfaces && (((Integer) objArr[0]).intValue() & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
            updateTypeCell();
        }
    }

    public void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new AnonymousClass10(inputFile, photoSize));
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.avatarUpdater.onActivityResult(i, i2, intent);
    }

    public boolean onFragmentCreate() {
        this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
        if (this.currentChat == null) {
            Semaphore semaphore = new Semaphore(0);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C11921(semaphore));
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (this.currentChat == null) {
                return false;
            }
            MessagesController.getInstance().putChat(this.currentChat, true);
            if (this.info == null) {
                MessagesStorage.getInstance().loadChatInfo(this.chatId, semaphore, false, false);
                try {
                    semaphore.acquire();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
                if (this.info == null) {
                    return false;
                }
            }
        }
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = this;
        this.signMessages = this.currentChat.signatures;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.avatarUpdater != null) {
            this.avatarUpdater.clear();
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        initThemeActionBar();
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = bundle.getString("path");
        }
    }

    public void saveSelfArgs(Bundle bundle) {
        if (!(this.avatarUpdater == null || this.avatarUpdater.currentPicturePath == null)) {
            bundle.putString("path", this.avatarUpdater.currentPicturePath);
        }
        if (this.nameTextView != null) {
            String obj = this.nameTextView.getText().toString();
            if (obj != null && obj.length() != 0) {
                bundle.putString("nameTextView", obj);
            }
        }
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
    }
}

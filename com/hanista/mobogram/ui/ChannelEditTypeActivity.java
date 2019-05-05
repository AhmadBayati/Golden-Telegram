package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ExportedChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_boolTrue;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_checkUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_exportInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_updateUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatInviteExported;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputChannelEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_chats;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.AdminedChannelCell;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.LoadingCell;
import com.hanista.mobogram.ui.Cells.RadioButtonCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextBlockCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ChannelEditTypeActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells;
    private TextInfoPrivacyCell adminedInfoCell;
    private boolean canCreatePublic;
    private int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private Chat currentChat;
    private boolean donePressed;
    private HeaderCell headerCell;
    private ExportedChatInvite invite;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private EditText nameTextView;
    private TextBlockCell privateContainer;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private ShadowSectionCell sectionCell;
    private TextInfoPrivacyCell typeInfoCell;

    /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.1 */
    class C12051 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;

        C12051(Semaphore semaphore) {
            this.val$semaphore = semaphore;
        }

        public void run() {
            ChannelEditTypeActivity.this.currentChat = MessagesStorage.getInstance().getChat(ChannelEditTypeActivity.this.chatId);
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.2 */
    class C12072 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.2.1 */
        class C12061 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C12061(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                ChannelEditTypeActivity channelEditTypeActivity = ChannelEditTypeActivity.this;
                boolean z = this.val$error == null || !this.val$error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
                channelEditTypeActivity.canCreatePublic = z;
                if (!ChannelEditTypeActivity.this.canCreatePublic) {
                    ChannelEditTypeActivity.this.loadAdminedChannels();
                }
            }
        }

        C12072() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C12061(tL_error));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.3 */
    class C12083 extends ActionBarMenuOnItemClick {
        C12083() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChannelEditTypeActivity.this.finishFragment();
            } else if (i == ChannelEditTypeActivity.done_button && !ChannelEditTypeActivity.this.donePressed) {
                if (ChannelEditTypeActivity.this.isPrivate || (((ChannelEditTypeActivity.this.currentChat.username != null || ChannelEditTypeActivity.this.nameTextView.length() == 0) && (ChannelEditTypeActivity.this.currentChat.username == null || ChannelEditTypeActivity.this.currentChat.username.equalsIgnoreCase(ChannelEditTypeActivity.this.nameTextView.getText().toString()))) || ChannelEditTypeActivity.this.nameTextView.length() == 0 || ChannelEditTypeActivity.this.lastNameAvailable)) {
                    ChannelEditTypeActivity.this.donePressed = true;
                    String str = ChannelEditTypeActivity.this.currentChat.username != null ? ChannelEditTypeActivity.this.currentChat.username : TtmlNode.ANONYMOUS_REGION_ID;
                    String obj = ChannelEditTypeActivity.this.isPrivate ? TtmlNode.ANONYMOUS_REGION_ID : ChannelEditTypeActivity.this.nameTextView.getText().toString();
                    if (!str.equals(obj)) {
                        MessagesController.getInstance().updateChannelUserName(ChannelEditTypeActivity.this.chatId, obj);
                    }
                    ChannelEditTypeActivity.this.finishFragment();
                    return;
                }
                Vibrator vibrator = (Vibrator) ChannelEditTypeActivity.this.getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                AndroidUtilities.shakeView(ChannelEditTypeActivity.this.checkTextView, 2.0f, 0);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.4 */
    class C12094 implements OnClickListener {
        C12094() {
        }

        public void onClick(View view) {
            if (ChannelEditTypeActivity.this.isPrivate) {
                ChannelEditTypeActivity.this.isPrivate = false;
                ChannelEditTypeActivity.this.updatePrivatePublic();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.5 */
    class C12105 implements OnClickListener {
        C12105() {
        }

        public void onClick(View view) {
            if (!ChannelEditTypeActivity.this.isPrivate) {
                ChannelEditTypeActivity.this.isPrivate = true;
                ChannelEditTypeActivity.this.updatePrivatePublic();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.6 */
    class C12116 implements TextWatcher {
        C12116() {
        }

        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            ChannelEditTypeActivity.this.checkUserName(ChannelEditTypeActivity.this.nameTextView.getText().toString());
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.7 */
    class C12127 implements OnClickListener {
        C12127() {
        }

        public void onClick(View view) {
            if (ChannelEditTypeActivity.this.invite != null) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelEditTypeActivity.this.invite.link));
                    Toast.makeText(ChannelEditTypeActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", C0338R.string.LinkCopied), 0).show();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.8 */
    class C12188 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.8.1 */
        class C12171 implements Runnable {
            final /* synthetic */ TLObject val$response;

            /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.8.1.1 */
            class C12161 implements OnClickListener {

                /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.8.1.1.1 */
                class C12151 implements DialogInterface.OnClickListener {
                    final /* synthetic */ Chat val$channel;

                    /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.8.1.1.1.1 */
                    class C12141 implements RequestDelegate {

                        /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.8.1.1.1.1.1 */
                        class C12131 implements Runnable {
                            C12131() {
                            }

                            public void run() {
                                ChannelEditTypeActivity.this.canCreatePublic = true;
                                if (ChannelEditTypeActivity.this.nameTextView.length() > 0) {
                                    ChannelEditTypeActivity.this.checkUserName(ChannelEditTypeActivity.this.nameTextView.getText().toString());
                                }
                                ChannelEditTypeActivity.this.updatePrivatePublic();
                            }
                        }

                        C12141() {
                        }

                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tLObject instanceof TL_boolTrue) {
                                AndroidUtilities.runOnUIThread(new C12131());
                            }
                        }
                    }

                    C12151(Chat chat) {
                        this.val$channel = chat;
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        TLObject tL_channels_updateUsername = new TL_channels_updateUsername();
                        tL_channels_updateUsername.channel = MessagesController.getInputChannel(this.val$channel);
                        tL_channels_updateUsername.username = TtmlNode.ANONYMOUS_REGION_ID;
                        ConnectionsManager.getInstance().sendRequest(tL_channels_updateUsername, new C12141(), 64);
                    }
                }

                C12161() {
                }

                public void onClick(View view) {
                    Chat currentChannel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
                    Builder builder = new Builder(ChannelEditTypeActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    if (currentChannel.megagroup) {
                        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", C0338R.string.RevokeLinkAlert, "telegram.me/" + currentChannel.username, currentChannel.title)));
                    } else {
                        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", C0338R.string.RevokeLinkAlertChannel, "telegram.me/" + currentChannel.username, currentChannel.title)));
                    }
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    builder.setPositiveButton(LocaleController.getString("RevokeButton", C0338R.string.RevokeButton), new C12151(currentChannel));
                    ChannelEditTypeActivity.this.showDialog(builder.create());
                }
            }

            C12171(TLObject tLObject) {
                this.val$response = tLObject;
            }

            public void run() {
                ChannelEditTypeActivity.this.loadingAdminedChannels = false;
                if (this.val$response != null && ChannelEditTypeActivity.this.getParentActivity() != null) {
                    for (int i = 0; i < ChannelEditTypeActivity.this.adminedChannelCells.size(); i += ChannelEditTypeActivity.done_button) {
                        ChannelEditTypeActivity.this.linearLayout.removeView((View) ChannelEditTypeActivity.this.adminedChannelCells.get(i));
                    }
                    ChannelEditTypeActivity.this.adminedChannelCells.clear();
                    TL_messages_chats tL_messages_chats = (TL_messages_chats) this.val$response;
                    int i2 = 0;
                    while (i2 < tL_messages_chats.chats.size()) {
                        View adminedChannelCell = new AdminedChannelCell(ChannelEditTypeActivity.this.getParentActivity(), new C12161());
                        adminedChannelCell.setChannel((Chat) tL_messages_chats.chats.get(i2), i2 == tL_messages_chats.chats.size() + -1);
                        ChannelEditTypeActivity.this.adminedChannelCells.add(adminedChannelCell);
                        ChannelEditTypeActivity.this.linearLayout.addView(adminedChannelCell, ChannelEditTypeActivity.this.linearLayout.getChildCount() - 1, LayoutHelper.createLinear(-1, 72));
                        i2 += ChannelEditTypeActivity.done_button;
                    }
                    ChannelEditTypeActivity.this.updatePrivatePublic();
                }
            }
        }

        C12188() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C12171(tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.9 */
    class C12219 implements Runnable {
        final /* synthetic */ String val$name;

        /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.9.1 */
        class C12201 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.9.1.1 */
            class C12191 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C12191(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    ChannelEditTypeActivity.this.checkReqId = 0;
                    if (ChannelEditTypeActivity.this.lastCheckName != null && ChannelEditTypeActivity.this.lastCheckName.equals(C12219.this.val$name)) {
                        if (this.val$error == null && (this.val$response instanceof TL_boolTrue)) {
                            TextView access$800 = ChannelEditTypeActivity.this.checkTextView;
                            Object[] objArr = new Object[ChannelEditTypeActivity.done_button];
                            objArr[0] = C12219.this.val$name;
                            access$800.setText(LocaleController.formatString("LinkAvailable", C0338R.string.LinkAvailable, objArr));
                            ChannelEditTypeActivity.this.checkTextView.setTextColor(-14248148);
                            ChannelEditTypeActivity.this.lastNameAvailable = true;
                            return;
                        }
                        if (this.val$error == null || !this.val$error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                            ChannelEditTypeActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", C0338R.string.LinkInUse));
                        } else {
                            ChannelEditTypeActivity.this.canCreatePublic = false;
                            ChannelEditTypeActivity.this.loadAdminedChannels();
                        }
                        ChannelEditTypeActivity.this.checkTextView.setTextColor(-3198928);
                        ChannelEditTypeActivity.this.lastNameAvailable = false;
                    }
                }
            }

            C12201() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C12191(tL_error, tLObject));
            }
        }

        C12219(String str) {
            this.val$name = str;
        }

        public void run() {
            TLObject tL_channels_checkUsername = new TL_channels_checkUsername();
            tL_channels_checkUsername.username = this.val$name;
            tL_channels_checkUsername.channel = MessagesController.getInputChannel(ChannelEditTypeActivity.this.chatId);
            ChannelEditTypeActivity.this.checkReqId = ConnectionsManager.getInstance().sendRequest(tL_channels_checkUsername, new C12201(), 2);
        }
    }

    public ChannelEditTypeActivity(Bundle bundle) {
        super(bundle);
        this.canCreatePublic = true;
        this.adminedChannelCells = new ArrayList();
        this.chatId = bundle.getInt("chat_id", 0);
    }

    private boolean checkUserName(String str) {
        if (str == null || str.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (this.checkRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (str != null) {
            if (str.startsWith("_") || str.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", C0338R.string.LinkInvalid));
                this.checkTextView.setTextColor(-3198928);
                return false;
            }
            int i = 0;
            while (i < str.length()) {
                char charAt = str.charAt(i);
                if (i != 0 || charAt < '0' || charAt > '9') {
                    if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                        this.checkTextView.setText(LocaleController.getString("LinkInvalid", C0338R.string.LinkInvalid));
                        this.checkTextView.setTextColor(-3198928);
                        return false;
                    }
                    i += done_button;
                } else if (this.currentChat.megagroup) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumberMega", C0338R.string.LinkInvalidStartNumberMega));
                    this.checkTextView.setTextColor(-3198928);
                    return false;
                } else {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", C0338R.string.LinkInvalidStartNumber));
                    this.checkTextView.setTextColor(-3198928);
                    return false;
                }
            }
        }
        if (str == null || str.length() < 5) {
            if (this.currentChat.megagroup) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalidShortMega", C0338R.string.LinkInvalidShortMega));
                this.checkTextView.setTextColor(-3198928);
                return false;
            }
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", C0338R.string.LinkInvalidShort));
            this.checkTextView.setTextColor(-3198928);
            return false;
        } else if (str.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", C0338R.string.LinkInvalidLong));
            this.checkTextView.setTextColor(-3198928);
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", C0338R.string.LinkChecking));
            this.checkTextView.setTextColor(-9605774);
            this.lastCheckName = str;
            this.checkRunnable = new C12219(str);
            AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            return true;
        }
    }

    private void generateLink() {
        if (!this.loadingInvite && this.invite == null) {
            this.loadingInvite = true;
            TLObject tL_channels_exportInvite = new TL_channels_exportInvite();
            tL_channels_exportInvite.channel = MessagesController.getInputChannel(this.chatId);
            ConnectionsManager.getInstance().sendRequest(tL_channels_exportInvite, new RequestDelegate() {

                /* renamed from: com.hanista.mobogram.ui.ChannelEditTypeActivity.10.1 */
                class C12041 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    C12041(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        if (this.val$error == null) {
                            ChannelEditTypeActivity.this.invite = (ExportedChatInvite) this.val$response;
                        }
                        ChannelEditTypeActivity.this.loadingInvite = false;
                        ChannelEditTypeActivity.this.privateContainer.setText(ChannelEditTypeActivity.this.invite != null ? ChannelEditTypeActivity.this.invite.link : LocaleController.getString("Loading", C0338R.string.Loading), false);
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C12041(tL_error, tLObject));
                }
            });
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            ConnectionsManager.getInstance().sendRequest(new TL_channels_getAdminedPublicChannels(), new C12188());
        }
    }

    private void updatePrivatePublic() {
        int i = 8;
        boolean z = false;
        if (this.sectionCell != null) {
            int i2;
            if (this.isPrivate || this.canCreatePublic) {
                this.typeInfoCell.setTextColor(-8355712);
                this.sectionCell.setVisibility(0);
                this.adminedInfoCell.setVisibility(8);
                this.typeInfoCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                for (i2 = 0; i2 < this.adminedChannelCells.size(); i2 += done_button) {
                    ((AdminedChannelCell) this.adminedChannelCells.get(i2)).setVisibility(8);
                }
                this.linkContainer.setVisibility(0);
                this.loadingAdminedCell.setVisibility(8);
                if (this.currentChat.megagroup) {
                    this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("MegaPrivateLinkHelp", C0338R.string.MegaPrivateLinkHelp) : LocaleController.getString("MegaUsernameHelp", C0338R.string.MegaUsernameHelp));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", C0338R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", C0338R.string.ChannelLinkTitle));
                } else {
                    this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("ChannelPrivateLinkHelp", C0338R.string.ChannelPrivateLinkHelp) : LocaleController.getString("ChannelUsernameHelp", C0338R.string.ChannelUsernameHelp));
                    this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", C0338R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", C0338R.string.ChannelLinkTitle));
                }
                this.publicContainer.setVisibility(this.isPrivate ? 8 : 0);
                this.privateContainer.setVisibility(this.isPrivate ? 0 : 8);
                this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
                this.privateContainer.setText(this.invite != null ? this.invite.link : LocaleController.getString("Loading", C0338R.string.Loading), false);
                TextView textView = this.checkTextView;
                if (!(this.isPrivate || this.checkTextView.length() == 0)) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else {
                this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", C0338R.string.ChangePublicLimitReached));
                this.typeInfoCell.setTextColor(-3198928);
                this.linkContainer.setVisibility(8);
                this.sectionCell.setVisibility(8);
                if (this.loadingAdminedChannels) {
                    this.loadingAdminedCell.setVisibility(0);
                    for (i2 = 0; i2 < this.adminedChannelCells.size(); i2 += done_button) {
                        ((AdminedChannelCell) this.adminedChannelCells.get(i2)).setVisibility(8);
                    }
                    this.typeInfoCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    this.adminedInfoCell.setVisibility(8);
                } else {
                    this.typeInfoCell.setBackgroundResource(C0338R.drawable.greydivider);
                    this.loadingAdminedCell.setVisibility(8);
                    for (i2 = 0; i2 < this.adminedChannelCells.size(); i2 += done_button) {
                        ((AdminedChannelCell) this.adminedChannelCells.get(i2)).setVisibility(0);
                    }
                    this.adminedInfoCell.setVisibility(0);
                }
            }
            RadioButtonCell radioButtonCell = this.radioButtonCell1;
            if (!this.isPrivate) {
                z = true;
            }
            radioButtonCell.setChecked(z, true);
            this.radioButtonCell2.setChecked(this.isPrivate, true);
            this.nameTextView.clearFocus();
            AndroidUtilities.hideKeyboard(this.nameTextView);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C12083());
        this.actionBar.createMenu().addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new ScrollView(context);
        this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        ScrollView scrollView = (ScrollView) this.fragmentView;
        scrollView.setFillViewport(true);
        this.linearLayout = new LinearLayout(context);
        scrollView.addView(this.linearLayout, new LayoutParams(-1, -2));
        this.linearLayout.setOrientation(done_button);
        if (this.currentChat.megagroup) {
            this.actionBar.setTitle(LocaleController.getString("GroupType", C0338R.string.GroupType));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ChannelType", C0338R.string.ChannelType));
        }
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(done_button);
        linearLayout.setBackgroundColor(-1);
        this.linearLayout.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1 = new RadioButtonCell(context);
        this.radioButtonCell1.setBackgroundResource(C0338R.drawable.list_selector);
        if (this.currentChat.megagroup) {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("MegaPublic", C0338R.string.MegaPublic), LocaleController.getString("MegaPublicInfo", C0338R.string.MegaPublicInfo), !this.isPrivate, false);
        } else {
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", C0338R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", C0338R.string.ChannelPublicInfo), !this.isPrivate, false);
        }
        linearLayout.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1.setOnClickListener(new C12094());
        this.radioButtonCell2 = new RadioButtonCell(context);
        this.radioButtonCell2.setBackgroundResource(C0338R.drawable.list_selector);
        if (this.currentChat.megagroup) {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("MegaPrivate", C0338R.string.MegaPrivate), LocaleController.getString("MegaPrivateInfo", C0338R.string.MegaPrivateInfo), this.isPrivate, false);
        } else {
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", C0338R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", C0338R.string.ChannelPrivateInfo), this.isPrivate, false);
        }
        linearLayout.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell2.setOnClickListener(new C12105());
        this.sectionCell = new ShadowSectionCell(context);
        this.linearLayout.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        this.linkContainer = new LinearLayout(context);
        this.linkContainer.setOrientation(done_button);
        this.linkContainer.setBackgroundColor(-1);
        this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(context);
        this.linkContainer.addView(this.headerCell);
        this.publicContainer = new LinearLayout(context);
        this.publicContainer.setOrientation(0);
        this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0f, 7.0f, 17.0f, 0.0f));
        View editText = new EditText(context);
        editText.setText("telegram.me/");
        editText.setTextSize(done_button, 18.0f);
        editText.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        editText.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        editText.setMaxLines(done_button);
        editText.setLines(done_button);
        editText.setEnabled(false);
        editText.setBackgroundDrawable(null);
        editText.setPadding(0, 0, 0, 0);
        editText.setSingleLine(true);
        editText.setInputType(163840);
        editText.setImeOptions(6);
        this.publicContainer.addView(editText, LayoutHelper.createLinear(-2, 36));
        this.nameTextView = new EditText(context);
        this.nameTextView.setTextSize(done_button, 18.0f);
        if (!this.isPrivate) {
            this.nameTextView.setText(this.currentChat.username);
        }
        this.nameTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.nameTextView.setMaxLines(done_button);
        this.nameTextView.setLines(done_button);
        this.nameTextView.setBackgroundDrawable(null);
        this.nameTextView.setPadding(0, 0, 0, 0);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setInputType(163872);
        this.nameTextView.setImeOptions(6);
        this.nameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", C0338R.string.ChannelUsernamePlaceholder));
        AndroidUtilities.clearCursorDrawable(this.nameTextView);
        this.publicContainer.addView(this.nameTextView, LayoutHelper.createLinear(-1, 36));
        this.nameTextView.addTextChangedListener(new C12116());
        this.privateContainer = new TextBlockCell(context);
        this.privateContainer.setBackgroundResource(C0338R.drawable.list_selector);
        this.linkContainer.addView(this.privateContainer);
        this.privateContainer.setOnClickListener(new C12127());
        this.checkTextView = new TextView(context);
        this.checkTextView.setTextSize(done_button, 15.0f);
        this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.checkTextView.setVisibility(8);
        this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
        this.typeInfoCell = new TextInfoPrivacyCell(context);
        this.typeInfoCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
        this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
        this.loadingAdminedCell = new LoadingCell(context);
        this.linearLayout.addView(this.loadingAdminedCell, LayoutHelper.createLinear(-1, -2));
        this.adminedInfoCell = new TextInfoPrivacyCell(context);
        this.adminedInfoCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
        this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
        updatePrivatePublic();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr[0];
            if (chatFull.id == this.chatId) {
                this.invite = chatFull.exported_invite;
                updatePrivatePublic();
            }
        }
    }

    public boolean onFragmentCreate() {
        boolean z = false;
        this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
        if (this.currentChat == null) {
            Semaphore semaphore = new Semaphore(0);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C12051(semaphore));
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (this.currentChat == null) {
                return false;
            }
            MessagesController.getInstance().putChat(this.currentChat, true);
        }
        if (this.currentChat.username == null || this.currentChat.username.length() == 0) {
            z = true;
        }
        this.isPrivate = z;
        if (this.isPrivate) {
            TLObject tL_channels_checkUsername = new TL_channels_checkUsername();
            tL_channels_checkUsername.username = "1";
            tL_channels_checkUsername.channel = new TL_inputChannelEmpty();
            ConnectionsManager.getInstance().sendRequest(tL_channels_checkUsername, new C12072());
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void setInfo(ChatFull chatFull) {
        if (chatFull == null) {
            return;
        }
        if (chatFull.exported_invite instanceof TL_chatInviteExported) {
            this.invite = chatFull.exported_invite;
        } else {
            generateLink();
        }
    }
}

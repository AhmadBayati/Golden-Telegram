package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ExportedChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.InputUser;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_boolTrue;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_checkUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_exportInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_updateUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputChannelEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_chats;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.ContactsAdapter;
import com.hanista.mobogram.ui.Adapters.SearchAdapter;
import com.hanista.mobogram.ui.Cells.AdminedChannelCell;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.LoadingCell;
import com.hanista.mobogram.ui.Cells.RadioButtonCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextBlockCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.AvatarUpdater;
import com.hanista.mobogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.ChipSpan;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.LetterSectionsListView;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ChannelCreateActivity extends BaseFragment implements NotificationCenterDelegate, AvatarUpdaterDelegate {
    private static final int done_button = 1;
    private static final int select_all_button = 2;
    private ArrayList<AdminedChannelCell> adminedChannelCells;
    private TextInfoPrivacyCell adminedInfoCell;
    private ArrayList<ChipSpan> allSpans;
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int beforeChangeIndex;
    private boolean canCreatePublic;
    private CharSequence changeString;
    private int chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private boolean createAfterUpload;
    private int currentStep;
    private EditText descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private TextView emptyTextView;
    private HeaderCell headerCell;
    private boolean ignoreChange;
    private ExportedChatInvite invite;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linkContainer;
    private LetterSectionsListView listView;
    private ContactsAdapter listViewAdapter;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private EditText nameTextView;
    private String nameToSet;
    private TextBlockCell privateContainer;
    private ProgressDialog progressDialog;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private ShadowSectionCell sectionCell;
    private HashMap<Integer, ChipSpan> selectedContacts;
    private TextInfoPrivacyCell typeInfoCell;
    private InputFile uploadedAvatar;

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.17 */
    class AnonymousClass17 implements Runnable {
        final /* synthetic */ InputFile val$file;
        final /* synthetic */ PhotoSize val$small;

        AnonymousClass17(InputFile inputFile, PhotoSize photoSize) {
            this.val$file = inputFile;
            this.val$small = photoSize;
        }

        public void run() {
            ChannelCreateActivity.this.uploadedAvatar = this.val$file;
            ChannelCreateActivity.this.avatar = this.val$small.location;
            ChannelCreateActivity.this.avatarImage.setImage(ChannelCreateActivity.this.avatar, "50_50", ChannelCreateActivity.this.avatarDrawable);
            if (ChannelCreateActivity.this.createAfterUpload) {
                try {
                    if (ChannelCreateActivity.this.progressDialog != null && ChannelCreateActivity.this.progressDialog.isShowing()) {
                        ChannelCreateActivity.this.progressDialog.dismiss();
                        ChannelCreateActivity.this.progressDialog = null;
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                ChannelCreateActivity.this.doneButton.performClick();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.19 */
    class AnonymousClass19 implements Runnable {
        final /* synthetic */ String val$name;

        /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.19.1 */
        class C11791 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.19.1.1 */
            class C11781 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C11781(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    ChannelCreateActivity.this.checkReqId = 0;
                    if (ChannelCreateActivity.this.lastCheckName != null && ChannelCreateActivity.this.lastCheckName.equals(AnonymousClass19.this.val$name)) {
                        if (this.val$error == null && (this.val$response instanceof TL_boolTrue)) {
                            TextView access$1000 = ChannelCreateActivity.this.checkTextView;
                            Object[] objArr = new Object[ChannelCreateActivity.done_button];
                            objArr[0] = AnonymousClass19.this.val$name;
                            access$1000.setText(LocaleController.formatString("LinkAvailable", C0338R.string.LinkAvailable, objArr));
                            ChannelCreateActivity.this.checkTextView.setTextColor(-14248148);
                            ChannelCreateActivity.this.lastNameAvailable = true;
                            return;
                        }
                        if (this.val$error == null || !this.val$error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                            ChannelCreateActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", C0338R.string.LinkInUse));
                        } else {
                            ChannelCreateActivity.this.canCreatePublic = false;
                            ChannelCreateActivity.this.loadAdminedChannels();
                        }
                        ChannelCreateActivity.this.checkTextView.setTextColor(-3198928);
                        ChannelCreateActivity.this.lastNameAvailable = false;
                    }
                }
            }

            C11791() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C11781(tL_error, tLObject));
            }
        }

        AnonymousClass19(String str) {
            this.val$name = str;
        }

        public void run() {
            TLObject tL_channels_checkUsername = new TL_channels_checkUsername();
            tL_channels_checkUsername.username = this.val$name;
            tL_channels_checkUsername.channel = MessagesController.getInputChannel(ChannelCreateActivity.this.chatId);
            ChannelCreateActivity.this.checkReqId = ConnectionsManager.getInstance().sendRequest(tL_channels_checkUsername, new C11791(), ChannelCreateActivity.select_all_button);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.1 */
    class C11801 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.1.1 */
        class C11711 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C11711(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                boolean z = this.val$error == null || !this.val$error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
                channelCreateActivity.canCreatePublic = z;
            }
        }

        C11801() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C11711(tL_error));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.2 */
    class C11832 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.2.1 */
        class C11811 implements OnClickListener {
            C11811() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ChannelCreateActivity.this.createAfterUpload = false;
                ChannelCreateActivity.this.progressDialog = null;
                ChannelCreateActivity.this.donePressed = false;
                try {
                    dialogInterface.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.2.2 */
        class C11822 implements OnClickListener {
            final /* synthetic */ int val$reqId;

            C11822(int i) {
                this.val$reqId = i;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
                ChannelCreateActivity.this.donePressed = false;
                try {
                    dialogInterface.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        C11832() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChannelCreateActivity.this.finishFragment();
            } else if (i == ChannelCreateActivity.done_button) {
                Vibrator vibrator;
                if (ChannelCreateActivity.this.currentStep == 0) {
                    if (!ChannelCreateActivity.this.donePressed) {
                        if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                            vibrator = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                            if (vibrator != null) {
                                vibrator.vibrate(200);
                            }
                            AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView, 2.0f, 0);
                            return;
                        }
                        ChannelCreateActivity.this.donePressed = true;
                        if (ChannelCreateActivity.this.avatarUpdater.uploadingAvatar != null) {
                            ChannelCreateActivity.this.createAfterUpload = true;
                            ChannelCreateActivity.this.progressDialog = new ProgressDialog(ChannelCreateActivity.this.getParentActivity());
                            ChannelCreateActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
                            ChannelCreateActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                            ChannelCreateActivity.this.progressDialog.setCancelable(false);
                            ChannelCreateActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", C0338R.string.Cancel), new C11811());
                            ChannelCreateActivity.this.progressDialog.show();
                            return;
                        }
                        int createChat = MessagesController.getInstance().createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), ChannelCreateActivity.select_all_button, ChannelCreateActivity.this);
                        ChannelCreateActivity.this.progressDialog = new ProgressDialog(ChannelCreateActivity.this.getParentActivity());
                        ChannelCreateActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
                        ChannelCreateActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                        ChannelCreateActivity.this.progressDialog.setCancelable(false);
                        ChannelCreateActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", C0338R.string.Cancel), new C11822(createChat));
                        ChannelCreateActivity.this.progressDialog.show();
                    }
                } else if (ChannelCreateActivity.this.currentStep == ChannelCreateActivity.done_button) {
                    if (!ChannelCreateActivity.this.isPrivate) {
                        if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                            Builder builder = new Builder(ChannelCreateActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                            builder.setMessage(LocaleController.getString("ChannelPublicEmptyUsername", C0338R.string.ChannelPublicEmptyUsername));
                            builder.setPositiveButton(LocaleController.getString("Close", C0338R.string.Close), null);
                            ChannelCreateActivity.this.showDialog(builder.create());
                            return;
                        } else if (ChannelCreateActivity.this.lastNameAvailable) {
                            MessagesController.getInstance().updateChannelUserName(ChannelCreateActivity.this.chatId, ChannelCreateActivity.this.lastCheckName);
                        } else {
                            vibrator = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                            if (vibrator != null) {
                                vibrator.vibrate(200);
                            }
                            AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView, 2.0f, 0);
                            return;
                        }
                    }
                    r0 = new Bundle();
                    r0.putInt("step", ChannelCreateActivity.select_all_button);
                    r0.putInt("chat_id", ChannelCreateActivity.this.chatId);
                    ChannelCreateActivity.this.presentFragment(new ChannelCreateActivity(r0), true);
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (Integer user : ChannelCreateActivity.this.selectedContacts.keySet()) {
                        InputUser inputUser = MessagesController.getInputUser(MessagesController.getInstance().getUser(user));
                        if (inputUser != null) {
                            arrayList.add(inputUser);
                        }
                    }
                    MessagesController.getInstance().addUsersToChannel(ChannelCreateActivity.this.chatId, arrayList, null);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    r0 = new Bundle();
                    r0.putInt("chat_id", ChannelCreateActivity.this.chatId);
                    ChannelCreateActivity.this.presentFragment(new ChatActivity(r0), true);
                }
            } else if (i == ChannelCreateActivity.select_all_button) {
                ChannelCreateActivity.this.selectAll();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.3 */
    class C11843 implements OnTouchListener {
        C11843() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.4 */
    class C11864 implements View.OnClickListener {

        /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.4.1 */
        class C11851 implements OnClickListener {
            C11851() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ChannelCreateActivity.this.avatarUpdater.openCamera();
                } else if (i == ChannelCreateActivity.done_button) {
                    ChannelCreateActivity.this.avatarUpdater.openGallery();
                } else if (i == ChannelCreateActivity.select_all_button) {
                    ChannelCreateActivity.this.avatar = null;
                    ChannelCreateActivity.this.uploadedAvatar = null;
                    ChannelCreateActivity.this.avatarImage.setImage(ChannelCreateActivity.this.avatar, "50_50", ChannelCreateActivity.this.avatarDrawable);
                }
            }
        }

        C11864() {
        }

        public void onClick(View view) {
            if (ChannelCreateActivity.this.getParentActivity() != null) {
                CharSequence[] charSequenceArr;
                Builder builder = new Builder(ChannelCreateActivity.this.getParentActivity());
                if (ChannelCreateActivity.this.avatar != null) {
                    charSequenceArr = new CharSequence[]{LocaleController.getString("FromCamera", C0338R.string.FromCamera), LocaleController.getString("FromGalley", C0338R.string.FromGalley), LocaleController.getString("DeletePhoto", C0338R.string.DeletePhoto)};
                } else {
                    charSequenceArr = new CharSequence[ChannelCreateActivity.select_all_button];
                    charSequenceArr[0] = LocaleController.getString("FromCamera", C0338R.string.FromCamera);
                    charSequenceArr[ChannelCreateActivity.done_button] = LocaleController.getString("FromGalley", C0338R.string.FromGalley);
                }
                builder.setItems(charSequenceArr, new C11851());
                ChannelCreateActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.5 */
    class C11875 implements TextWatcher {
        C11875() {
        }

        public void afterTextChanged(Editable editable) {
            ChannelCreateActivity.this.avatarDrawable.setInfo(5, ChannelCreateActivity.this.nameTextView.length() > 0 ? ChannelCreateActivity.this.nameTextView.getText().toString() : null, null, false);
            ChannelCreateActivity.this.avatarImage.invalidate();
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.6 */
    class C11886 implements OnEditorActionListener {
        C11886() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChannelCreateActivity.this.doneButton == null) {
                return false;
            }
            ChannelCreateActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.7 */
    class C11897 implements TextWatcher {
        C11897() {
        }

        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.8 */
    class C11908 implements View.OnClickListener {
        C11908() {
        }

        public void onClick(View view) {
            if (ChannelCreateActivity.this.isPrivate) {
                ChannelCreateActivity.this.isPrivate = false;
                ChannelCreateActivity.this.updatePrivatePublic();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.9 */
    class C11919 implements View.OnClickListener {
        C11919() {
        }

        public void onClick(View view) {
            if (!ChannelCreateActivity.this.isPrivate) {
                ChannelCreateActivity.this.isPrivate = true;
                ChannelCreateActivity.this.updatePrivatePublic();
            }
        }
    }

    public ChannelCreateActivity(Bundle bundle) {
        boolean z = true;
        super(bundle);
        this.adminedChannelCells = new ArrayList();
        this.selectedContacts = new HashMap();
        this.allSpans = new ArrayList();
        this.canCreatePublic = true;
        this.currentStep = bundle.getInt("step", 0);
        if (this.currentStep == 0) {
            this.avatarDrawable = new AvatarDrawable();
            this.avatarUpdater = new AvatarUpdater();
            TLObject tL_channels_checkUsername = new TL_channels_checkUsername();
            tL_channels_checkUsername.username = "1";
            tL_channels_checkUsername.channel = new TL_inputChannelEmpty();
            ConnectionsManager.getInstance().sendRequest(tL_channels_checkUsername, new C11801());
            return;
        }
        if (this.currentStep == done_button) {
            this.canCreatePublic = bundle.getBoolean("canCreatePublic", true);
            if (this.canCreatePublic) {
                z = false;
            }
            this.isPrivate = z;
            if (!this.canCreatePublic) {
                loadAdminedChannels();
            }
        }
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
                if (i == 0 && charAt >= '0' && charAt <= '9') {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", C0338R.string.LinkInvalidStartNumber));
                    this.checkTextView.setTextColor(-3198928);
                    return false;
                } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalid", C0338R.string.LinkInvalid));
                    this.checkTextView.setTextColor(-3198928);
                    return false;
                } else {
                    i += done_button;
                }
            }
        }
        if (str == null || str.length() < 5) {
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
            this.checkRunnable = new AnonymousClass19(str);
            AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            return true;
        }
    }

    private ChipSpan createAndPutChipForUser(User user) {
        try {
            View inflate = ((LayoutInflater) ApplicationLoader.applicationContext.getSystemService("layout_inflater")).inflate(C0338R.layout.group_create_bubble, null);
            TextView textView = (TextView) inflate.findViewById(C0338R.id.bubble_text_view);
            String userName = UserObject.getUserName(user);
            if (!(userName.length() != 0 || user.phone == null || user.phone.length() == 0)) {
                userName = PhoneFormat.getInstance().format("+" + user.phone);
            }
            textView.setText(userName + ", ");
            int makeMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
            inflate.measure(makeMeasureSpec, makeMeasureSpec);
            inflate.layout(0, 0, inflate.getMeasuredWidth(), inflate.getMeasuredHeight());
            Bitmap createBitmap = Bitmap.createBitmap(inflate.getWidth(), inflate.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.translate((float) (-inflate.getScrollX()), (float) (-inflate.getScrollY()));
            inflate.draw(canvas);
            inflate.setDrawingCacheEnabled(true);
            inflate.getDrawingCache().copy(Config.ARGB_8888, true);
            inflate.destroyDrawingCache();
            Drawable bitmapDrawable = new BitmapDrawable(createBitmap);
            bitmapDrawable.setBounds(0, 0, createBitmap.getWidth(), createBitmap.getHeight());
            Object spannableStringBuilder = new SpannableStringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
            ChipSpan chipSpan = new ChipSpan(bitmapDrawable, done_button);
            this.allSpans.add(chipSpan);
            this.selectedContacts.put(Integer.valueOf(user.id), chipSpan);
            Iterator it = this.allSpans.iterator();
            while (it.hasNext()) {
                ImageSpan imageSpan = (ImageSpan) it.next();
                spannableStringBuilder.append("<<");
                spannableStringBuilder.setSpan(imageSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length(), 33);
            }
            this.nameTextView.setText(spannableStringBuilder);
            this.nameTextView.setSelection(spannableStringBuilder.length());
            return chipSpan;
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return null;
        }
    }

    private void generateLink() {
        if (!this.loadingInvite && this.invite == null) {
            this.loadingInvite = true;
            TLObject tL_channels_exportInvite = new TL_channels_exportInvite();
            tL_channels_exportInvite.channel = MessagesController.getInputChannel(this.chatId);
            ConnectionsManager.getInstance().sendRequest(tL_channels_exportInvite, new RequestDelegate() {

                /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.16.1 */
                class C11721 implements Runnable {
                    final /* synthetic */ TL_error val$error;
                    final /* synthetic */ TLObject val$response;

                    C11721(TL_error tL_error, TLObject tLObject) {
                        this.val$error = tL_error;
                        this.val$response = tLObject;
                    }

                    public void run() {
                        if (this.val$error == null) {
                            ChannelCreateActivity.this.invite = (ExportedChatInvite) this.val$response;
                        }
                        ChannelCreateActivity.this.loadingInvite = false;
                        ChannelCreateActivity.this.privateContainer.setText(ChannelCreateActivity.this.invite != null ? ChannelCreateActivity.this.invite.link : LocaleController.getString("Loading", C0338R.string.Loading), false);
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C11721(tL_error, tLObject));
                }
            });
        }
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            if (this.fragmentView != null) {
                this.fragmentView.setBackgroundColor(AdvanceTheme.f2497h);
            }
            if (this.avatarDrawable != null) {
                this.avatarDrawable.setColor(AdvanceTheme.f2503n);
                int dp = AndroidUtilities.dp((float) AdvanceTheme.f2502m);
                this.avatarDrawable.setRadius(dp);
                this.avatarImage.setRoundRadius(dp);
            }
            if (this.nameTextView != null) {
                if (this.nameTextView.getBackground() != null) {
                    this.nameTextView.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.nameTextView.setHintTextColor(AdvanceTheme.f2495f);
                this.nameTextView.setTextColor(AdvanceTheme.f2494e);
            }
            if (this.emptyTextView != null) {
                this.emptyTextView.setTextColor(AdvanceTheme.f2495f);
            }
            if (this.descriptionTextView != null) {
                if (this.descriptionTextView.getBackground() != null) {
                    this.descriptionTextView.getBackground().setColorFilter(AdvanceTheme.f2491b, Mode.SRC_IN);
                }
                this.descriptionTextView.setHintTextColor(AdvanceTheme.f2495f);
                this.descriptionTextView.setTextColor(AdvanceTheme.f2494e);
            }
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_select_all).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
        }
    }

    private void loadAdminedChannels() {
        if (!this.loadingAdminedChannels) {
            this.loadingAdminedChannels = true;
            updatePrivatePublic();
            ConnectionsManager.getInstance().sendRequest(new TL_channels_getAdminedPublicChannels(), new RequestDelegate() {

                /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.18.1 */
                class C11771 implements Runnable {
                    final /* synthetic */ TLObject val$response;

                    /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.18.1.1 */
                    class C11761 implements View.OnClickListener {

                        /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.18.1.1.1 */
                        class C11751 implements OnClickListener {
                            final /* synthetic */ Chat val$channel;

                            /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.18.1.1.1.1 */
                            class C11741 implements RequestDelegate {

                                /* renamed from: com.hanista.mobogram.ui.ChannelCreateActivity.18.1.1.1.1.1 */
                                class C11731 implements Runnable {
                                    C11731() {
                                    }

                                    public void run() {
                                        ChannelCreateActivity.this.canCreatePublic = true;
                                        if (ChannelCreateActivity.this.nameTextView.length() > 0) {
                                            ChannelCreateActivity.this.checkUserName(ChannelCreateActivity.this.nameTextView.getText().toString());
                                        }
                                        ChannelCreateActivity.this.updatePrivatePublic();
                                    }
                                }

                                C11741() {
                                }

                                public void run(TLObject tLObject, TL_error tL_error) {
                                    if (tLObject instanceof TL_boolTrue) {
                                        AndroidUtilities.runOnUIThread(new C11731());
                                    }
                                }
                            }

                            C11751(Chat chat) {
                                this.val$channel = chat;
                            }

                            public void onClick(DialogInterface dialogInterface, int i) {
                                TLObject tL_channels_updateUsername = new TL_channels_updateUsername();
                                tL_channels_updateUsername.channel = MessagesController.getInputChannel(this.val$channel);
                                tL_channels_updateUsername.username = TtmlNode.ANONYMOUS_REGION_ID;
                                ConnectionsManager.getInstance().sendRequest(tL_channels_updateUsername, new C11741(), 64);
                            }
                        }

                        C11761() {
                        }

                        public void onClick(View view) {
                            Chat currentChannel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
                            Builder builder = new Builder(ChannelCreateActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                            Object[] objArr;
                            if (currentChannel.megagroup) {
                                objArr = new Object[ChannelCreateActivity.select_all_button];
                                objArr[0] = "telegram.me/" + currentChannel.username;
                                objArr[ChannelCreateActivity.done_button] = currentChannel.title;
                                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", C0338R.string.RevokeLinkAlert, objArr)));
                            } else {
                                objArr = new Object[ChannelCreateActivity.select_all_button];
                                objArr[0] = "telegram.me/" + currentChannel.username;
                                objArr[ChannelCreateActivity.done_button] = currentChannel.title;
                                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", C0338R.string.RevokeLinkAlertChannel, objArr)));
                            }
                            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                            builder.setPositiveButton(LocaleController.getString("RevokeButton", C0338R.string.RevokeButton), new C11751(currentChannel));
                            ChannelCreateActivity.this.showDialog(builder.create());
                        }
                    }

                    C11771(TLObject tLObject) {
                        this.val$response = tLObject;
                    }

                    public void run() {
                        ChannelCreateActivity.this.loadingAdminedChannels = false;
                        if (this.val$response != null && ChannelCreateActivity.this.getParentActivity() != null) {
                            for (int i = 0; i < ChannelCreateActivity.this.adminedChannelCells.size(); i += ChannelCreateActivity.done_button) {
                                ChannelCreateActivity.this.linearLayout.removeView((View) ChannelCreateActivity.this.adminedChannelCells.get(i));
                            }
                            ChannelCreateActivity.this.adminedChannelCells.clear();
                            TL_messages_chats tL_messages_chats = (TL_messages_chats) this.val$response;
                            int i2 = 0;
                            while (i2 < tL_messages_chats.chats.size()) {
                                View adminedChannelCell = new AdminedChannelCell(ChannelCreateActivity.this.getParentActivity(), new C11761());
                                adminedChannelCell.setChannel((Chat) tL_messages_chats.chats.get(i2), i2 == tL_messages_chats.chats.size() + -1);
                                ChannelCreateActivity.this.adminedChannelCells.add(adminedChannelCell);
                                ChannelCreateActivity.this.linearLayout.addView(adminedChannelCell, ChannelCreateActivity.this.linearLayout.getChildCount() - 1, LayoutHelper.createLinear(-1, 72));
                                i2 += ChannelCreateActivity.done_button;
                            }
                            ChannelCreateActivity.this.updatePrivatePublic();
                        }
                    }
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new C11771(tLObject));
                }
            });
        }
    }

    private void selectAll() {
        this.progressDialog = new ProgressDialog(getParentActivity());
        this.progressDialog.setMessage(LocaleController.getString("PleaseWait", C0338R.string.PleaseWait));
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ChannelCreateActivity.this.selectedContacts.clear();
                ChannelCreateActivity.this.allSpans.clear();
                ChannelCreateActivity.this.ignoreChange = true;
                int i;
                User user;
                if (ChannelCreateActivity.this.searching && ChannelCreateActivity.this.searchWas) {
                    for (i = 0; i < ChannelCreateActivity.this.searchListViewAdapter.getCount(); i += ChannelCreateActivity.done_button) {
                        user = (User) ChannelCreateActivity.this.searchListViewAdapter.getItem(i);
                        if (user != null) {
                            ChannelCreateActivity.this.createAndPutChipForUser(user).uid = user.id;
                        }
                    }
                } else {
                    for (i = 0; i < ChannelCreateActivity.this.listViewAdapter.getSectionCount(); i += ChannelCreateActivity.done_button) {
                        for (int i2 = 0; i2 < ChannelCreateActivity.this.listViewAdapter.getCountForSection(i); i2 += ChannelCreateActivity.done_button) {
                            user = (User) ChannelCreateActivity.this.listViewAdapter.getItem(i, i2);
                            if (user != null) {
                                ChannelCreateActivity.this.createAndPutChipForUser(user).uid = user.id;
                            }
                        }
                    }
                }
                ChannelCreateActivity.this.ignoreChange = false;
                ChannelCreateActivity.this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", ChannelCreateActivity.this.selectedContacts.size()));
                if (ChannelCreateActivity.this.searching || ChannelCreateActivity.this.searchWas) {
                    ChannelCreateActivity.this.ignoreChange = true;
                    Object spannableStringBuilder = new SpannableStringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                    Iterator it = ChannelCreateActivity.this.allSpans.iterator();
                    while (it.hasNext()) {
                        ImageSpan imageSpan = (ImageSpan) it.next();
                        spannableStringBuilder.append("<<");
                        spannableStringBuilder.setSpan(imageSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length(), 33);
                    }
                    ChannelCreateActivity.this.nameTextView.setText(spannableStringBuilder);
                    ChannelCreateActivity.this.nameTextView.setSelection(spannableStringBuilder.length());
                    ChannelCreateActivity.this.ignoreChange = false;
                    ChannelCreateActivity.this.searchListViewAdapter.searchDialogs(null);
                    ChannelCreateActivity.this.searching = false;
                    ChannelCreateActivity.this.searchWas = false;
                    ChannelCreateActivity.this.listView.setAdapter(ChannelCreateActivity.this.listViewAdapter);
                    ChannelCreateActivity.this.listViewAdapter.notifyDataSetChanged();
                    if (VERSION.SDK_INT >= 11) {
                        ChannelCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
                    }
                    ChannelCreateActivity.this.listView.setFastScrollEnabled(true);
                    ChannelCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
                    ChannelCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
                }
                ChannelCreateActivity.this.progressDialog.dismiss();
            }
        }, 500);
    }

    private void showErrorAlert(String str) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            Object obj = -1;
            switch (str.hashCode()) {
                case -141887186:
                    if (str.equals("USERNAMES_UNAVAILABLE")) {
                        obj = select_all_button;
                        break;
                    }
                    break;
                case 288843630:
                    if (str.equals("USERNAME_INVALID")) {
                        obj = null;
                        break;
                    }
                    break;
                case 533175271:
                    if (str.equals("USERNAME_OCCUPIED")) {
                        obj = done_button;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    builder.setMessage(LocaleController.getString("LinkInvalid", C0338R.string.LinkInvalid));
                    break;
                case done_button /*1*/:
                    builder.setMessage(LocaleController.getString("LinkInUse", C0338R.string.LinkInUse));
                    break;
                case select_all_button /*2*/:
                    builder.setMessage(LocaleController.getString("FeatureUnavailable", C0338R.string.FeatureUnavailable));
                    break;
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred));
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            showDialog(builder.create());
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
                this.typeInfoCell.setText(this.isPrivate ? LocaleController.getString("ChannelPrivateLinkHelp", C0338R.string.ChannelPrivateLinkHelp) : LocaleController.getString("ChannelUsernameHelp", C0338R.string.ChannelUsernameHelp));
                this.headerCell.setText(this.isPrivate ? LocaleController.getString("ChannelInviteLinkTitle", C0338R.string.ChannelInviteLinkTitle) : LocaleController.getString("ChannelLinkTitle", C0338R.string.ChannelLinkTitle));
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

    private void updateVisibleRows(int i) {
        if (this.listView != null) {
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2 += done_button) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                }
            }
        }
    }

    public View createView(Context context) {
        int i = 3;
        int i2 = done_button;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C11832());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
            drawable.setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            this.doneButton = createMenu.addItemWithWidth((int) done_button, drawable, AndroidUtilities.dp(56.0f));
        } else {
            this.doneButton = createMenu.addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        }
        if (this.currentStep != select_all_button) {
            this.fragmentView = new ScrollView(context);
            ScrollView scrollView = (ScrollView) this.fragmentView;
            scrollView.setFillViewport(true);
            this.linearLayout = new LinearLayout(context);
            scrollView.addView(this.linearLayout, new LayoutParams(-1, -2));
        } else {
            this.fragmentView = new LinearLayout(context);
            this.fragmentView.setOnTouchListener(new C11843());
            this.linearLayout = (LinearLayout) this.fragmentView;
        }
        this.linearLayout.setOrientation(done_button);
        View frameLayout;
        if (this.currentStep == 0) {
            this.actionBar.setTitle(LocaleController.getString("NewChannel", C0338R.string.NewChannel));
            this.fragmentView.setBackgroundColor(-1);
            frameLayout = new FrameLayout(context);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            this.avatarImage = new BackupImageView(context);
            this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.avatarDrawable.setInfo(5, null, null, false);
            this.avatarDrawable.setDrawPhoto(true);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarImage.setOnClickListener(new C11864());
            this.nameTextView = new EditText(context);
            this.nameTextView.setHint(LocaleController.getString("EnterChannelName", C0338R.string.EnterChannelName));
            if (this.nameToSet != null) {
                this.nameTextView.setText(this.nameToSet);
                this.nameToSet = null;
            }
            this.nameTextView.setMaxLines(4);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.nameTextView.setTextSize(done_button, 16.0f);
            this.nameTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.nameTextView.setImeOptions(268435456);
            this.nameTextView.setInputType(16385);
            InputFilter[] inputFilterArr = new InputFilter[done_button];
            inputFilterArr[0] = new LengthFilter(100);
            this.nameTextView.setFilters(inputFilterArr);
            this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
            AndroidUtilities.clearCursorDrawable(this.nameTextView);
            this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 16.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 16.0f, 0.0f));
            this.nameTextView.addTextChangedListener(new C11875());
            this.descriptionTextView = new EditText(context);
            this.descriptionTextView.setTextSize(done_button, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.descriptionTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.descriptionTextView.setInputType(180225);
            this.descriptionTextView.setImeOptions(6);
            inputFilterArr = new InputFilter[done_button];
            inputFilterArr[0] = new LengthFilter(120);
            this.descriptionTextView.setFilters(inputFilterArr);
            this.descriptionTextView.setHint(LocaleController.getString("DescriptionPlaceholder", C0338R.string.DescriptionPlaceholder));
            AndroidUtilities.clearCursorDrawable(this.descriptionTextView);
            this.linearLayout.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            this.descriptionTextView.setOnEditorActionListener(new C11886());
            this.descriptionTextView.addTextChangedListener(new C11897());
            View textView = new TextView(context);
            textView.setTypeface(FontUtil.m1176a().m1161d());
            textView.setTextSize(done_button, 15.0f);
            textView.setTextColor(-9605774);
            if (ThemeUtil.m2490b()) {
                textView.setTextColor(AdvanceTheme.f2495f);
            }
            textView.setGravity(LocaleController.isRTL ? 5 : 3);
            textView.setText(LocaleController.getString("DescriptionInfo", C0338R.string.DescriptionInfo));
            this.linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 20));
        } else if (this.currentStep == done_button) {
            this.actionBar.setTitle(LocaleController.getString("ChannelSettings", C0338R.string.ChannelSettings));
            this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
            View linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(done_button);
            linearLayout.setBackgroundColor(-1);
            this.linearLayout.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
            if (ThemeUtil.m2490b()) {
                this.fragmentView.setBackgroundColor(AdvanceTheme.f2496g);
                linearLayout.setBackgroundColor(AdvanceTheme.f2497h);
            }
            this.radioButtonCell1 = new RadioButtonCell(context);
            this.radioButtonCell1.setBackgroundResource(C0338R.drawable.list_selector);
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", C0338R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", C0338R.string.ChannelPublicInfo), !this.isPrivate, false);
            linearLayout.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1.setOnClickListener(new C11908());
            this.radioButtonCell2 = new RadioButtonCell(context);
            this.radioButtonCell2.setBackgroundResource(C0338R.drawable.list_selector);
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", C0338R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", C0338R.string.ChannelPrivateInfo), this.isPrivate, false);
            linearLayout.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell2.setOnClickListener(new C11919());
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
            frameLayout = new EditText(context);
            frameLayout.setText("telegram.me/");
            frameLayout.setTextSize(done_button, 18.0f);
            frameLayout.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            frameLayout.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            frameLayout.setMaxLines(done_button);
            frameLayout.setLines(done_button);
            frameLayout.setEnabled(false);
            frameLayout.setBackgroundDrawable(null);
            frameLayout.setPadding(0, 0, 0, 0);
            frameLayout.setSingleLine(true);
            frameLayout.setInputType(163840);
            frameLayout.setImeOptions(6);
            this.publicContainer.addView(frameLayout, LayoutHelper.createLinear(-2, 36));
            this.nameTextView = new EditText(context);
            this.nameTextView.setTextSize(done_button, 18.0f);
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
            this.nameTextView.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    ChannelCreateActivity.this.checkUserName(ChannelCreateActivity.this.nameTextView.getText().toString());
                }
            });
            this.privateContainer = new TextBlockCell(context);
            this.privateContainer.setBackgroundResource(C0338R.drawable.list_selector);
            this.linkContainer.addView(this.privateContainer);
            this.privateContainer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (ChannelCreateActivity.this.invite != null) {
                        try {
                            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelCreateActivity.this.invite.link));
                            Toast.makeText(ChannelCreateActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", C0338R.string.LinkCopied), 0).show();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                }
            });
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
            if (ThemeUtil.m2490b()) {
                this.linkContainer.setBackgroundColor(AdvanceTheme.f2497h);
                frameLayout.setHintTextColor(AdvanceTheme.f2495f);
                frameLayout.setTextColor(AdvanceTheme.f2494e);
                this.nameTextView.setHintTextColor(AdvanceTheme.f2495f);
                this.nameTextView.setTextColor(AdvanceTheme.f2494e);
            }
        } else if (this.currentStep == select_all_button) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", C0338R.string.ChannelAddMembers));
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
            createMenu.addItemWithWidth((int) select_all_button, (int) C0338R.drawable.ic_select_all, AndroidUtilities.dp(56.0f));
            this.searchListViewAdapter = new SearchAdapter(context, null, false, false, false, false);
            this.searchListViewAdapter.setCheckedMap(this.selectedContacts);
            this.searchListViewAdapter.setUseUserCell(true);
            this.listViewAdapter = new ContactsAdapter(context, done_button, false, null, false);
            this.listViewAdapter.setCheckedMap(this.selectedContacts);
            frameLayout = new FrameLayout(context);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            this.nameTextView = new EditText(context);
            this.nameTextView.setTextSize(done_button, 16.0f);
            this.nameTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
            this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
            this.nameTextView.setInputType(655536);
            this.nameTextView.setMinimumHeight(AndroidUtilities.dp(54.0f));
            this.nameTextView.setSingleLine(false);
            this.nameTextView.setLines(select_all_button);
            this.nameTextView.setMaxLines(select_all_button);
            this.nameTextView.setVerticalScrollBarEnabled(true);
            this.nameTextView.setHorizontalScrollBarEnabled(false);
            this.nameTextView.setPadding(0, 0, 0, 0);
            this.nameTextView.setHint(LocaleController.getString("AddMutual", C0338R.string.AddMutual));
            this.nameTextView.setTextIsSelectable(false);
            this.nameTextView.setImeOptions(268435462);
            EditText editText = this.nameTextView;
            if (LocaleController.isRTL) {
                i = 5;
            }
            editText.setGravity(i | 16);
            AndroidUtilities.clearCursorDrawable(this.nameTextView);
            frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
            this.nameTextView.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                    if (!ChannelCreateActivity.this.ignoreChange) {
                        String str;
                        boolean z;
                        int selectionEnd = ChannelCreateActivity.this.nameTextView.getSelectionEnd();
                        if (editable.toString().length() < ChannelCreateActivity.this.changeString.toString().length()) {
                            str = TtmlNode.ANONYMOUS_REGION_ID;
                            try {
                                str = ChannelCreateActivity.this.changeString.toString().substring(selectionEnd, ChannelCreateActivity.this.beforeChangeIndex);
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                            }
                            if (str.length() > 0) {
                                z = ChannelCreateActivity.this.searching && ChannelCreateActivity.this.searchWas;
                                Spannable text = ChannelCreateActivity.this.nameTextView.getText();
                                for (int i = 0; i < ChannelCreateActivity.this.allSpans.size(); i += ChannelCreateActivity.done_button) {
                                    ChipSpan chipSpan = (ChipSpan) ChannelCreateActivity.this.allSpans.get(i);
                                    if (text.getSpanStart(chipSpan) == -1) {
                                        ChannelCreateActivity.this.allSpans.remove(chipSpan);
                                        ChannelCreateActivity.this.selectedContacts.remove(Integer.valueOf(chipSpan.uid));
                                    }
                                }
                                ChannelCreateActivity.this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", ChannelCreateActivity.this.selectedContacts.size()));
                                ChannelCreateActivity.this.listView.invalidateViews();
                            } else {
                                z = true;
                            }
                        } else {
                            z = true;
                        }
                        if (z) {
                            str = ChannelCreateActivity.this.nameTextView.getText().toString().replace("<", TtmlNode.ANONYMOUS_REGION_ID);
                            if (str.length() != 0) {
                                ChannelCreateActivity.this.searching = true;
                                ChannelCreateActivity.this.searchWas = true;
                                if (ChannelCreateActivity.this.listView != null) {
                                    ChannelCreateActivity.this.listView.setAdapter(ChannelCreateActivity.this.searchListViewAdapter);
                                    ChannelCreateActivity.this.searchListViewAdapter.notifyDataSetChanged();
                                    ChannelCreateActivity.this.listView.setFastScrollAlwaysVisible(false);
                                    ChannelCreateActivity.this.listView.setFastScrollEnabled(false);
                                    ChannelCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                                }
                                if (ChannelCreateActivity.this.emptyTextView != null) {
                                    ChannelCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoResult", C0338R.string.NoResult));
                                }
                                ChannelCreateActivity.this.searchListViewAdapter.searchDialogs(str);
                                return;
                            }
                            ChannelCreateActivity.this.searchListViewAdapter.searchDialogs(null);
                            ChannelCreateActivity.this.searching = false;
                            ChannelCreateActivity.this.searchWas = false;
                            ChannelCreateActivity.this.listView.setAdapter(ChannelCreateActivity.this.listViewAdapter);
                            ChannelCreateActivity.this.listViewAdapter.notifyDataSetChanged();
                            ChannelCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
                            ChannelCreateActivity.this.listView.setFastScrollEnabled(true);
                            ChannelCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
                            ChannelCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
                        }
                    }
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (!ChannelCreateActivity.this.ignoreChange) {
                        ChannelCreateActivity.this.beforeChangeIndex = ChannelCreateActivity.this.nameTextView.getSelectionStart();
                        ChannelCreateActivity.this.changeString = new SpannableString(charSequence);
                    }
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }
            });
            View linearLayout2 = new LinearLayout(context);
            linearLayout2.setVisibility(4);
            linearLayout2.setOrientation(done_button);
            this.linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -1));
            linearLayout2.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
            this.emptyTextView = new TextView(context);
            this.emptyTextView.setTextColor(-8355712);
            this.emptyTextView.setTextSize(20.0f);
            this.emptyTextView.setGravity(17);
            this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
            linearLayout2.addView(this.emptyTextView, LayoutHelper.createLinear(-1, -1, 0.5f));
            linearLayout2.addView(new FrameLayout(context), LayoutHelper.createLinear(-1, -1, 0.5f));
            this.listView = new LetterSectionsListView(context);
            this.listView.setEmptyView(linearLayout2);
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setDivider(null);
            this.listView.setDividerHeight(0);
            this.listView.setFastScrollEnabled(true);
            this.listView.setScrollBarStyle(33554432);
            this.listView.setAdapter(this.listViewAdapter);
            this.listView.setFastScrollAlwaysVisible(true);
            LetterSectionsListView letterSectionsListView = this.listView;
            if (!LocaleController.isRTL) {
                i2 = select_all_button;
            }
            letterSectionsListView.setVerticalScrollbarPosition(i2);
            this.linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    User user;
                    if (ChannelCreateActivity.this.searching && ChannelCreateActivity.this.searchWas) {
                        user = (User) ChannelCreateActivity.this.searchListViewAdapter.getItem(i);
                    } else {
                        int sectionForPosition = ChannelCreateActivity.this.listViewAdapter.getSectionForPosition(i);
                        int positionInSectionForPosition = ChannelCreateActivity.this.listViewAdapter.getPositionInSectionForPosition(i);
                        if (positionInSectionForPosition >= 0 && sectionForPosition >= 0) {
                            user = (User) ChannelCreateActivity.this.listViewAdapter.getItem(sectionForPosition, positionInSectionForPosition);
                        } else {
                            return;
                        }
                    }
                    if (user != null) {
                        Object spannableStringBuilder;
                        boolean z;
                        ChipSpan chipSpan;
                        if (ChannelCreateActivity.this.selectedContacts.containsKey(Integer.valueOf(user.id))) {
                            try {
                                chipSpan = (ChipSpan) ChannelCreateActivity.this.selectedContacts.get(Integer.valueOf(user.id));
                                ChannelCreateActivity.this.selectedContacts.remove(Integer.valueOf(user.id));
                                spannableStringBuilder = new SpannableStringBuilder(ChannelCreateActivity.this.nameTextView.getText());
                                spannableStringBuilder.delete(spannableStringBuilder.getSpanStart(chipSpan), spannableStringBuilder.getSpanEnd(chipSpan));
                                ChannelCreateActivity.this.allSpans.remove(chipSpan);
                                ChannelCreateActivity.this.ignoreChange = true;
                                ChannelCreateActivity.this.nameTextView.setText(spannableStringBuilder);
                                ChannelCreateActivity.this.nameTextView.setSelection(spannableStringBuilder.length());
                                ChannelCreateActivity.this.ignoreChange = false;
                                z = false;
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                                z = false;
                            }
                        } else {
                            ChannelCreateActivity.this.ignoreChange = true;
                            chipSpan = ChannelCreateActivity.this.createAndPutChipForUser(user);
                            if (chipSpan != null) {
                                chipSpan.uid = user.id;
                            }
                            ChannelCreateActivity.this.ignoreChange = false;
                            if (chipSpan != null) {
                                z = true;
                            } else {
                                return;
                            }
                        }
                        ChannelCreateActivity.this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", ChannelCreateActivity.this.selectedContacts.size()));
                        if (ChannelCreateActivity.this.searching || ChannelCreateActivity.this.searchWas) {
                            ChannelCreateActivity.this.ignoreChange = true;
                            spannableStringBuilder = new SpannableStringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                            Iterator it = ChannelCreateActivity.this.allSpans.iterator();
                            while (it.hasNext()) {
                                ImageSpan imageSpan = (ImageSpan) it.next();
                                spannableStringBuilder.append("<<");
                                spannableStringBuilder.setSpan(imageSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length(), 33);
                            }
                            ChannelCreateActivity.this.nameTextView.setText(spannableStringBuilder);
                            ChannelCreateActivity.this.nameTextView.setSelection(spannableStringBuilder.length());
                            ChannelCreateActivity.this.ignoreChange = false;
                            ChannelCreateActivity.this.searchListViewAdapter.searchDialogs(null);
                            ChannelCreateActivity.this.searching = false;
                            ChannelCreateActivity.this.searchWas = false;
                            ChannelCreateActivity.this.listView.setAdapter(ChannelCreateActivity.this.listViewAdapter);
                            ChannelCreateActivity.this.listViewAdapter.notifyDataSetChanged();
                            ChannelCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
                            ChannelCreateActivity.this.listView.setFastScrollEnabled(true);
                            ChannelCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
                            ChannelCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
                        } else if (view instanceof UserCell) {
                            ((UserCell) view).setChecked(z, true);
                        }
                    }
                }
            });
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                    if (absListView.isFastScrollEnabled()) {
                        AndroidUtilities.clearDrawableAnimation(absListView);
                    }
                }

                public void onScrollStateChanged(AbsListView absListView, int i) {
                    boolean z = true;
                    if (i == ChannelCreateActivity.done_button) {
                        AndroidUtilities.hideKeyboard(ChannelCreateActivity.this.nameTextView);
                    }
                    if (ChannelCreateActivity.this.listViewAdapter != null) {
                        ContactsAdapter access$3300 = ChannelCreateActivity.this.listViewAdapter;
                        if (i == 0) {
                            z = false;
                        }
                        access$3300.setIsScrolling(z);
                    }
                }
            });
        }
        initTheme();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        int intValue;
        if (i == NotificationCenter.updateInterfaces) {
            intValue = ((Integer) objArr[0]).intValue();
            if ((intValue & select_all_button) != 0 || (intValue & done_button) != 0 || (intValue & 4) != 0) {
                updateVisibleRows(intValue);
            }
        } else if (i == NotificationCenter.chatDidFailCreate) {
            if (this.progressDialog != null) {
                try {
                    this.progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
            this.donePressed = false;
        } else if (i == NotificationCenter.chatDidCreated) {
            if (this.progressDialog != null) {
                try {
                    this.progressDialog.dismiss();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
            intValue = ((Integer) objArr[0]).intValue();
            Bundle bundle = new Bundle();
            bundle.putInt("step", done_button);
            bundle.putInt("chat_id", intValue);
            bundle.putBoolean("canCreatePublic", this.canCreatePublic);
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance().changeChatAvatar(intValue, this.uploadedAvatar);
            }
            presentFragment(new ChannelCreateActivity(bundle), true);
        } else if (i == NotificationCenter.contactsDidLoaded && this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new AnonymousClass17(inputFile, photoSize));
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.avatarUpdater.onActivityResult(i, i2, intent);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.currentStep == select_all_button) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
        } else if (this.currentStep == done_button) {
            generateLink();
        }
        if (this.avatarUpdater != null) {
            this.avatarUpdater.parentFragment = this;
            this.avatarUpdater.delegate = this;
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.currentStep == select_all_button) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
        }
        if (this.avatarUpdater != null) {
            this.avatarUpdater.clear();
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        initThemeActionBar();
        initTheme();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.currentStep != done_button) {
            this.nameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.nameTextView);
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.currentStep == 0) {
            if (this.avatarUpdater != null) {
                this.avatarUpdater.currentPicturePath = bundle.getString("path");
            }
            Object string = bundle.getString("nameTextView");
            if (string == null) {
                return;
            }
            if (this.nameTextView != null) {
                this.nameTextView.setText(string);
            } else {
                this.nameToSet = string;
            }
        }
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.currentStep == 0) {
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
    }
}

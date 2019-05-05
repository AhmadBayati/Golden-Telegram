package com.hanista.mobogram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.SendMessagesHelper;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.browser.Browser;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.query.DraftQuery;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.AboutActivity;
import com.hanista.mobogram.mobo.EmojiSettingsActivity;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.MoboSettingsActivity;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.ReportHelpActivity;
import com.hanista.mobogram.mobo.ThemeActivity;
import com.hanista.mobogram.mobo.UsernameFinderActivity;
import com.hanista.mobogram.mobo.dialogdm.DialogDmActivity;
import com.hanista.mobogram.mobo.download.DownloadManagerActivity;
import com.hanista.mobogram.mobo.p000a.ArchiveActivity;
import com.hanista.mobogram.mobo.p001b.CategoryActivity;
import com.hanista.mobogram.mobo.p001b.CategorySettingsActivity;
import com.hanista.mobogram.mobo.p001b.CategoryUtil;
import com.hanista.mobogram.mobo.p003d.Glow;
import com.hanista.mobogram.mobo.p013m.AllMediaActivity;
import com.hanista.mobogram.mobo.p015o.MenuData;
import com.hanista.mobogram.mobo.p016p.SpecificContactActivity;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.telegraph.ui.UpdateActivity;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.InputStickerSet;
import com.hanista.mobogram.tgnet.TLRPC.MessageMedia;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_resolveUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputGameShortName;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputMediaGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputStickerSetShortName;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_checkChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_importChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_userContact_old2;
import com.hanista.mobogram.tgnet.TLRPC.Updates;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBarLayout;
import com.hanista.mobogram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.DrawerLayoutContainer;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.DrawerLayoutAdapter;
import com.hanista.mobogram.ui.Components.JoinGroupAlert;
import com.hanista.mobogram.ui.Components.PasscodeView;
import com.hanista.mobogram.ui.Components.PasscodeView.PasscodeViewDelegate;
import com.hanista.mobogram.ui.Components.StickersAlert;
import com.hanista.mobogram.ui.DialogsActivity.DialogsActivityDelegate;
import com.hanista.mobogram.ui.LocationActivity.LocationActivityDelegate;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class LaunchActivity extends Activity implements NotificationCenterDelegate, ActionBarLayoutDelegate, DialogsActivityDelegate {
    private static ArrayList<BaseFragment> layerFragmentsStack;
    private static ArrayList<BaseFragment> mainFragmentsStack;
    private static ArrayList<BaseFragment> rightFragmentsStack;
    private ActionBarLayout actionBarLayout;
    private View backgroundTablet;
    private ArrayList<User> contactsToSend;
    private int currentConnectionState;
    private String documentsMimeType;
    private ArrayList<String> documentsOriginalPathsArray;
    private ArrayList<String> documentsPathsArray;
    private ArrayList<Uri> documentsUrisArray;
    private DrawerLayoutAdapter drawerLayoutAdapter;
    protected DrawerLayoutContainer drawerLayoutContainer;
    private boolean finished;
    private ActionBarLayout layersActionBarLayout;
    public ListView listView;
    private Runnable lockRunnable;
    private OnGlobalLayoutListener onGlobalLayoutListener;
    private Intent passcodeSaveIntent;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private PasscodeView passcodeView;
    private ArrayList<Uri> photoPathsArray;
    private ActionBarLayout rightActionBarLayout;
    private String sendingText;
    private FrameLayout shadowTablet;
    private FrameLayout shadowTabletSide;
    private boolean tabletFullSize;
    private String videoPath;
    private AlertDialog visibleDialog;

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.10 */
    class AnonymousClass10 implements RequestDelegate {
        final /* synthetic */ String val$botChat;
        final /* synthetic */ String val$botUser;
        final /* synthetic */ String val$game;
        final /* synthetic */ String val$group;
        final /* synthetic */ boolean val$hasUrl;
        final /* synthetic */ boolean val$isExternal;
        final /* synthetic */ String val$message;
        final /* synthetic */ Integer val$messageId;
        final /* synthetic */ ProgressDialog val$progressDialog;
        final /* synthetic */ String val$sticker;
        final /* synthetic */ String val$username;

        /* renamed from: com.hanista.mobogram.ui.LaunchActivity.10.1 */
        class C16001 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: com.hanista.mobogram.ui.LaunchActivity.10.1.1 */
            class C15991 implements OnClickListener {
                C15991() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    LaunchActivity.this.runLinkRequest(AnonymousClass10.this.val$username, AnonymousClass10.this.val$group, AnonymousClass10.this.val$sticker, AnonymousClass10.this.val$botUser, AnonymousClass10.this.val$botChat, AnonymousClass10.this.val$message, AnonymousClass10.this.val$hasUrl, AnonymousClass10.this.val$messageId, AnonymousClass10.this.val$game, 1);
                }
            }

            C16001(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (!LaunchActivity.this.isFinishing()) {
                    try {
                        AnonymousClass10.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                    if (this.val$error != null || LaunchActivity.this.actionBarLayout == null) {
                        Builder builder = new Builder(LaunchActivity.this);
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            builder.setMessage(LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else {
                            builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", C0338R.string.JoinToGroupErrorNotExist));
                        }
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                        LaunchActivity.this.showAlertDialog(builder);
                        return;
                    }
                    ChatInvite chatInvite = (ChatInvite) this.val$response;
                    if (chatInvite.chat != null && !ChatObject.isLeftFromChat(chatInvite.chat)) {
                        MessagesController.getInstance().putChat(chatInvite.chat, false);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(chatInvite.chat);
                        MessagesStorage.getInstance().putUsersAndChats(null, arrayList, false, true);
                        Bundle bundle = new Bundle();
                        bundle.putInt("chat_id", chatInvite.chat.id);
                        if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.checkCanOpenChat(bundle, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                            BaseFragment chatActivity = new ChatActivity(bundle);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            LaunchActivity.this.actionBarLayout.presentFragment(chatActivity, false, true, true);
                        }
                    } else if (((chatInvite.chat != null || (chatInvite.channel && !chatInvite.megagroup)) && (chatInvite.chat == null || (ChatObject.isChannel(chatInvite.chat) && !chatInvite.chat.megagroup))) || LaunchActivity.mainFragmentsStack.isEmpty()) {
                        CharSequence formatString;
                        Builder builder2 = new Builder(LaunchActivity.this);
                        builder2.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        String str;
                        Object[] objArr;
                        if ((chatInvite.megagroup || !chatInvite.channel) && (!ChatObject.isChannel(chatInvite.chat) || chatInvite.chat.megagroup)) {
                            str = "JoinToGroup";
                            objArr = new Object[1];
                            objArr[0] = chatInvite.chat != null ? chatInvite.chat.title : chatInvite.title;
                            formatString = LocaleController.formatString(str, C0338R.string.JoinToGroup, objArr);
                        } else {
                            str = "ChannelJoinTo";
                            objArr = new Object[1];
                            objArr[0] = chatInvite.chat != null ? chatInvite.chat.title : chatInvite.title;
                            formatString = LocaleController.formatString(str, C0338R.string.ChannelJoinTo, objArr);
                        }
                        if (AnonymousClass10.this.val$isExternal) {
                            if (MoboUtils.m1694a(LaunchActivity.this) != null) {
                                builder2.setMessage(formatString + "\n\n" + LocaleController.formatString("AdIntentsAppAlert", C0338R.string.AdIntentsAppAlert, MoboUtils.m1694a(LaunchActivity.this).m355a()));
                            } else {
                                builder2.setMessage(formatString + "\n\n" + LocaleController.getString("AdIntentsAlert", C0338R.string.AdIntentsAlert));
                            }
                        } else {
                            builder2.setMessage(formatString);
                        }
                        builder2.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C15991());
                        builder2.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        LaunchActivity.this.showAlertDialog(builder2);
                    } else {
                        BaseFragment baseFragment = (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1);
                        baseFragment.showDialog(new JoinGroupAlert(LaunchActivity.this, chatInvite, AnonymousClass10.this.val$group, baseFragment));
                    }
                }
            }
        }

        AnonymousClass10(ProgressDialog progressDialog, String str, boolean z, String str2, String str3, String str4, String str5, String str6, boolean z2, Integer num, String str7) {
            this.val$progressDialog = progressDialog;
            this.val$group = str;
            this.val$isExternal = z;
            this.val$username = str2;
            this.val$sticker = str3;
            this.val$botUser = str4;
            this.val$botChat = str5;
            this.val$message = str6;
            this.val$hasUrl = z2;
            this.val$messageId = num;
            this.val$game = str7;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C16001(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.11 */
    class AnonymousClass11 implements RequestDelegate {
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: com.hanista.mobogram.ui.LaunchActivity.11.1 */
        class C16011 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C16011(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (!LaunchActivity.this.isFinishing()) {
                    try {
                        AnonymousClass11.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                    if (this.val$error != null) {
                        Builder builder = new Builder(LaunchActivity.this);
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            builder.setMessage(LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else if (this.val$error.text.equals("USERS_TOO_MUCH")) {
                            builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", C0338R.string.JoinToGroupErrorFull));
                        } else {
                            builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", C0338R.string.JoinToGroupErrorNotExist));
                        }
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                        LaunchActivity.this.showAlertDialog(builder);
                    } else if (LaunchActivity.this.actionBarLayout != null) {
                        Updates updates = (Updates) this.val$response;
                        if (!updates.chats.isEmpty()) {
                            Chat chat = (Chat) updates.chats.get(0);
                            chat.left = false;
                            chat.kicked = false;
                            MessagesController.getInstance().putUsers(updates.users, false);
                            MessagesController.getInstance().putChats(updates.chats, false);
                            Bundle bundle = new Bundle();
                            bundle.putInt("chat_id", chat.id);
                            if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.checkCanOpenChat(bundle, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                                BaseFragment chatActivity = new ChatActivity(bundle);
                                if (!MoboConstants.f1355v) {
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                }
                                LaunchActivity.this.actionBarLayout.presentFragment(chatActivity, false, true, true);
                            }
                        }
                    }
                }
            }
        }

        AnonymousClass11(ProgressDialog progressDialog) {
            this.val$progressDialog = progressDialog;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                MessagesController.getInstance().processUpdates((Updates) tLObject, false);
            }
            AndroidUtilities.runOnUIThread(new C16011(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.12 */
    class AnonymousClass12 implements DialogsActivityDelegate {
        final /* synthetic */ boolean val$hasUrl;
        final /* synthetic */ String val$message;

        AnonymousClass12(boolean z, String str) {
            this.val$hasUrl = z;
            this.val$message = str;
        }

        public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            bundle.putBoolean("hasUrl", this.val$hasUrl);
            int i = (int) j;
            int i2 = (int) (j >> 32);
            if (i == 0) {
                bundle.putInt("enc_id", i2);
            } else if (i2 == 1) {
                bundle.putInt("chat_id", i);
            } else if (i > 0) {
                bundle.putInt("user_id", i);
            } else if (i < 0) {
                bundle.putInt("chat_id", -i);
            }
            if (MessagesController.checkCanOpenChat(bundle, dialogsActivity)) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                DraftQuery.saveDraft(j, this.val$message, null, null, true);
                LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.13 */
    class AnonymousClass13 implements OnClickListener {
        final /* synthetic */ int val$reqId;

        AnonymousClass13(int i) {
            this.val$reqId = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
            try {
                dialogInterface.dismiss();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.17 */
    class AnonymousClass17 implements OnClickListener {
        final /* synthetic */ HashMap val$waitingForLocation;

        /* renamed from: com.hanista.mobogram.ui.LaunchActivity.17.1 */
        class C16021 implements LocationActivityDelegate {
            C16021() {
            }

            public void didSelectLocation(MessageMedia messageMedia) {
                for (Entry value : AnonymousClass17.this.val$waitingForLocation.entrySet()) {
                    MessageObject messageObject = (MessageObject) value.getValue();
                    SendMessagesHelper.getInstance().sendMessage(messageMedia, messageObject.getDialogId(), messageObject, null, null);
                }
            }
        }

        AnonymousClass17(HashMap hashMap) {
            this.val$waitingForLocation = hashMap;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (!LaunchActivity.mainFragmentsStack.isEmpty() && AndroidUtilities.isGoogleMapsInstalled((BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                BaseFragment locationActivity = new LocationActivity();
                locationActivity.setDelegate(new C16021());
                LaunchActivity.this.presentFragment(locationActivity);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.1 */
    class C16031 extends RelativeLayout {
        private boolean inLayout;

        C16031(Context context) {
            super(context);
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int i6 = i3 - i;
            int i7 = i4 - i2;
            if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
            } else {
                i5 = (i6 / 100) * 35;
                if (i5 < AndroidUtilities.dp(320.0f)) {
                    i5 = AndroidUtilities.dp(320.0f);
                }
                LaunchActivity.this.shadowTabletSide.layout(i5, 0, LaunchActivity.this.shadowTabletSide.getMeasuredWidth() + i5, LaunchActivity.this.shadowTabletSide.getMeasuredHeight());
                LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
                LaunchActivity.this.rightActionBarLayout.layout(i5, 0, LaunchActivity.this.rightActionBarLayout.getMeasuredWidth() + i5, LaunchActivity.this.rightActionBarLayout.getMeasuredHeight());
            }
            i6 = (i6 - LaunchActivity.this.layersActionBarLayout.getMeasuredWidth()) / 2;
            i5 = (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ((i7 - LaunchActivity.this.layersActionBarLayout.getMeasuredHeight()) / 2);
            LaunchActivity.this.layersActionBarLayout.layout(i6, i5, LaunchActivity.this.layersActionBarLayout.getMeasuredWidth() + i6, LaunchActivity.this.layersActionBarLayout.getMeasuredHeight() + i5);
            LaunchActivity.this.backgroundTablet.layout(0, 0, LaunchActivity.this.backgroundTablet.getMeasuredWidth(), LaunchActivity.this.backgroundTablet.getMeasuredHeight());
            LaunchActivity.this.shadowTablet.layout(0, 0, LaunchActivity.this.shadowTablet.getMeasuredWidth(), LaunchActivity.this.shadowTablet.getMeasuredHeight());
        }

        protected void onMeasure(int i, int i2) {
            this.inLayout = true;
            int size = MeasureSpec.getSize(i);
            int size2 = MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
                LaunchActivity.this.tabletFullSize = true;
                LaunchActivity.this.actionBarLayout.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, C0700C.ENCODING_PCM_32BIT));
            } else {
                LaunchActivity.this.tabletFullSize = false;
                int i3 = (size / 100) * 35;
                if (i3 < AndroidUtilities.dp(320.0f)) {
                    i3 = AndroidUtilities.dp(320.0f);
                }
                LaunchActivity.this.actionBarLayout.measure(MeasureSpec.makeMeasureSpec(i3, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, C0700C.ENCODING_PCM_32BIT));
                LaunchActivity.this.shadowTabletSide.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, C0700C.ENCODING_PCM_32BIT));
                LaunchActivity.this.rightActionBarLayout.measure(MeasureSpec.makeMeasureSpec(size - i3, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, C0700C.ENCODING_PCM_32BIT));
            }
            LaunchActivity.this.backgroundTablet.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, C0700C.ENCODING_PCM_32BIT));
            LaunchActivity.this.shadowTablet.measure(MeasureSpec.makeMeasureSpec(size, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(size2, C0700C.ENCODING_PCM_32BIT));
            LaunchActivity.this.layersActionBarLayout.measure(MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0f), size), C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(528.0f) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0), size2), C0700C.ENCODING_PCM_32BIT));
            this.inLayout = false;
        }

        public void requestLayout() {
            if (!this.inLayout) {
                super.requestLayout();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.22 */
    class AnonymousClass22 implements OnClickListener {
        final /* synthetic */ List val$otherInstalledPackageNames;

        AnonymousClass22(List list) {
            this.val$otherInstalledPackageNames = list;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent = new Intent();
            intent.setClassName((String) this.val$otherInstalledPackageNames.get(i), "com.hanista.mobogram.ui.LaunchActivity");
            LaunchActivity.this.startActivity(intent);
            LaunchActivity.this.finish();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.2 */
    class C16042 implements OnTouchListener {
        C16042() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty() || motionEvent.getAction() != 1) {
                return false;
            }
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int[] iArr = new int[2];
            LaunchActivity.this.layersActionBarLayout.getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            if (LaunchActivity.this.layersActionBarLayout.checkTransitionAnimation() || (x > ((float) i) && x < ((float) (i + LaunchActivity.this.layersActionBarLayout.getWidth())) && y > ((float) i2) && y < ((float) (LaunchActivity.this.layersActionBarLayout.getHeight() + i2)))) {
                return false;
            }
            if (!LaunchActivity.this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                while (LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                    LaunchActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(0));
                }
                LaunchActivity.this.layersActionBarLayout.closeLastFragment(true);
            }
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.3 */
    class C16053 implements View.OnClickListener {
        C16053() {
        }

        public void onClick(View view) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.4 */
    class C16064 extends ListView {
        C16064(Context context) {
            super(context);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.5 */
    class C16075 implements OnItemClickListener {
        C16075() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            MenuData menuData = (MenuData) LaunchActivity.this.drawerLayoutAdapter.getItem(i);
            if (menuData.m1973b() == 2) {
                if (MoboUtils.m1726i(LaunchActivity.this) && !MoboUtils.m1724h(LaunchActivity.this)) {
                    LaunchActivity.this.showDownloadMobo2Dialog();
                } else if (MoboUtils.m1724h(LaunchActivity.this)) {
                    LaunchActivity.this.startOtherMobos();
                }
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 4) {
                LaunchActivity.this.showCreateChatDialog();
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 0 && !UserConfig.isRobot) {
                r0 = new Bundle();
                r0.putInt("user_id", UserConfig.getClientUserId());
                LaunchActivity.this.presentFragment(new ChatActivity(r0));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 6) {
                LaunchActivity.this.presentFragment(new DownloadManagerActivity(new Bundle()));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 7) {
                LaunchActivity.this.presentFragment(new AllMediaActivity(new Bundle()));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 8) {
                r0 = new Bundle();
                r0.putBoolean("fromMenu", true);
                LaunchActivity.this.presentFragment(new CategoryActivity(r0));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 9) {
                LaunchActivity.this.presentFragment(new SpecificContactActivity(new Bundle()));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 10) {
                r0 = new Bundle();
                r0.putBoolean("onlyOnlines", true);
                LaunchActivity.this.presentFragment(new ContactsActivity(r0));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 11) {
                LaunchActivity.this.presentFragment(new UpdateActivity(null));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 12) {
                LaunchActivity.this.presentFragment(new MoboSettingsActivity());
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 14) {
                LaunchActivity.this.presentFragment(new UsernameFinderActivity());
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 15) {
                LaunchActivity.this.presentFragment(new ContactsActivity(null));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 16) {
                LaunchActivity.this.presentFragment(new ThemeActivity(new Bundle()));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 17) {
                try {
                    r0 = new Intent("android.intent.action.SEND");
                    r0.setType("text/plain");
                    r0.setClass(LaunchActivity.this, LaunchActivity.class);
                    r0.putExtra("android.intent.extra.TEXT", LocaleController.getString("InviteText", C0338R.string.InviteText));
                    LaunchActivity.this.startActivity(r0);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 18) {
                LaunchActivity.this.presentFragment(new SettingsActivity());
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 19) {
                Browser.openUrl(LaunchActivity.this, LocaleController.getString("TelegramFaqUrl", C0338R.string.TelegramFaqUrl));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 20) {
                r0 = new Intent("android.intent.action.VIEW");
                r0.setData(Uri.parse("bazaar://collection?slug=by_author&aid=6149"));
                LaunchActivity.this.startActivity(r0);
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 21) {
                LaunchActivity.this.presentFragment(new AboutActivity());
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 22) {
                LaunchActivity.this.presentFragment(new DialogDmActivity(new Bundle()));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 23) {
                try {
                    String str = "com.hanista.moboplus";
                    r0 = LaunchActivity.this.getPackageManager().getLaunchIntentForPackage(str);
                    if (r0 == null) {
                        r0 = new Intent("android.intent.action.VIEW", Uri.parse("bazaar://details?id=" + str));
                    }
                    LaunchActivity.this.startActivityForResult(r0, 503);
                } catch (Throwable e2) {
                    LaunchActivity.this.startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("https://cafebazaar.ir/app/com.hanista.moboplus")), 503);
                    FileLog.m18e("tmessages", e2);
                }
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 24) {
                if (MoboConstants.aI) {
                    LaunchActivity.this.presentFragment(new ArchiveActivity());
                } else {
                    r0 = new Bundle();
                    r0.putInt("user_id", UserConfig.getClientUserId());
                    LaunchActivity.this.presentFragment(new ChatActivity(r0));
                }
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            } else if (menuData.m1973b() == 26) {
                LaunchActivity.this.turnOff();
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.6 */
    class C16086 implements OnGlobalLayoutListener {
        final /* synthetic */ View val$view;

        C16086(View view) {
            this.val$view = view;
        }

        public void onGlobalLayout() {
            int measuredHeight = this.val$view.getMeasuredHeight();
            if (VERSION.SDK_INT >= 21) {
                measuredHeight -= AndroidUtilities.statusBarHeight;
            }
            if (measuredHeight > AndroidUtilities.dp(100.0f) && measuredHeight < AndroidUtilities.displaySize.y && AndroidUtilities.dp(100.0f) + measuredHeight > AndroidUtilities.displaySize.y) {
                AndroidUtilities.displaySize.y = measuredHeight;
                FileLog.m16e("tmessages", "fix display size y to " + AndroidUtilities.displaySize.y);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.7 */
    class C16097 implements PasscodeViewDelegate {
        C16097() {
        }

        public void didAcceptedPassword() {
            UserConfig.isWaitingForPasscodeEnter = false;
            if (LaunchActivity.this.passcodeSaveIntent != null) {
                LaunchActivity.this.handleIntent(LaunchActivity.this.passcodeSaveIntent, LaunchActivity.this.passcodeSaveIntentIsNew, LaunchActivity.this.passcodeSaveIntentIsRestore, true);
                LaunchActivity.this.passcodeSaveIntent = null;
            }
            LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            LaunchActivity.this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                LaunchActivity.this.layersActionBarLayout.showLastFragment();
                LaunchActivity.this.rightActionBarLayout.showLastFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.8 */
    class C16108 implements Runnable {
        final /* synthetic */ Bundle val$args;

        C16108(Bundle bundle) {
            this.val$args = bundle;
        }

        public void run() {
            LaunchActivity.this.presentFragment(new CancelAccountDeletionActivity(this.val$args));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.LaunchActivity.9 */
    class C16149 implements RequestDelegate {
        final /* synthetic */ String val$botChat;
        final /* synthetic */ String val$botUser;
        final /* synthetic */ String val$game;
        final /* synthetic */ Integer val$messageId;
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: com.hanista.mobogram.ui.LaunchActivity.9.1 */
        class C16131 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: com.hanista.mobogram.ui.LaunchActivity.9.1.1 */
            class C16111 implements DialogsActivityDelegate {
                final /* synthetic */ TL_contacts_resolvedPeer val$res;

                C16111(TL_contacts_resolvedPeer tL_contacts_resolvedPeer) {
                    this.val$res = tL_contacts_resolvedPeer;
                }

                public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
                    TL_inputMediaGame tL_inputMediaGame = new TL_inputMediaGame();
                    tL_inputMediaGame.id = new TL_inputGameShortName();
                    tL_inputMediaGame.id.short_name = C16149.this.val$game;
                    tL_inputMediaGame.id.bot_id = MessagesController.getInputUser((User) this.val$res.users.get(0));
                    SendMessagesHelper.getInstance().sendGame(MessagesController.getInputPeer((int) j), tL_inputMediaGame, 0, 0);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", true);
                    int i = (int) j;
                    int i2 = (int) (j >> 32);
                    if (i == 0) {
                        bundle.putInt("enc_id", i2);
                    } else if (i2 == 1) {
                        bundle.putInt("chat_id", i);
                    } else if (i > 0) {
                        bundle.putInt("user_id", i);
                    } else if (i < 0) {
                        bundle.putInt("chat_id", -i);
                    }
                    if (MessagesController.checkCanOpenChat(bundle, dialogsActivity)) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true);
                    }
                }
            }

            /* renamed from: com.hanista.mobogram.ui.LaunchActivity.9.1.2 */
            class C16122 implements DialogsActivityDelegate {
                final /* synthetic */ User val$user;

                C16122(User user) {
                    this.val$user = user;
                }

                public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", true);
                    bundle.putInt("chat_id", -((int) j));
                    if (LaunchActivity.mainFragmentsStack.isEmpty() || MessagesController.checkCanOpenChat(bundle, (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        MessagesController.getInstance().addUserToChat(-((int) j), this.val$user, null, 0, C16149.this.val$botChat, null);
                        LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(bundle), true, false, true);
                    }
                }
            }

            C16131(TLObject tLObject, TL_error tL_error) {
                this.val$response = tLObject;
                this.val$error = tL_error;
            }

            public void run() {
                if (!LaunchActivity.this.isFinishing()) {
                    try {
                        C16149.this.val$progressDialog.dismiss();
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                    TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) this.val$response;
                    if (this.val$error != null || LaunchActivity.this.actionBarLayout == null || (C16149.this.val$game != null && (C16149.this.val$game == null || tL_contacts_resolvedPeer.users.isEmpty()))) {
                        try {
                            Toast.makeText(LaunchActivity.this, LocaleController.getString("NoUsernameFound", C0338R.string.NoUsernameFound), 0).show();
                            return;
                        } catch (Throwable e2) {
                            FileLog.m18e("tmessages", e2);
                            return;
                        }
                    }
                    MessagesController.getInstance().putUsers(tL_contacts_resolvedPeer.users, false);
                    MessagesController.getInstance().putChats(tL_contacts_resolvedPeer.chats, false);
                    MessagesStorage.getInstance().putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, false, true);
                    Bundle bundle;
                    if (C16149.this.val$game != null) {
                        bundle = new Bundle();
                        bundle.putBoolean("onlySelect", true);
                        bundle.putBoolean("cantSendToChannels", true);
                        bundle.putInt("dialogsType", 1);
                        bundle.putString("selectAlertString", LocaleController.getString("SendGameTo", C0338R.string.SendGameTo));
                        bundle.putString("selectAlertStringGroup", LocaleController.getString("SendGameToGroup", C0338R.string.SendGameToGroup));
                        BaseFragment dialogsActivity = new DialogsActivity(bundle);
                        dialogsActivity.setDelegate(new C16111(tL_contacts_resolvedPeer));
                        boolean z = AndroidUtilities.isTablet() ? LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() > 0 && (LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity) : LaunchActivity.this.actionBarLayout.fragmentsStack.size() > 1 && (LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
                        LaunchActivity.this.actionBarLayout.presentFragment(dialogsActivity, z, true, true);
                        if (PhotoViewer.getInstance().isVisible()) {
                            PhotoViewer.getInstance().closePhoto(false, true);
                        }
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        if (AndroidUtilities.isTablet()) {
                            LaunchActivity.this.actionBarLayout.showLastFragment();
                            LaunchActivity.this.rightActionBarLayout.showLastFragment();
                            return;
                        }
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    } else if (C16149.this.val$botChat != null) {
                        User user = !tL_contacts_resolvedPeer.users.isEmpty() ? (User) tL_contacts_resolvedPeer.users.get(0) : null;
                        if (user == null || (user.bot && user.bot_nochats)) {
                            try {
                                Toast.makeText(LaunchActivity.this, LocaleController.getString("BotCantJoinGroups", C0338R.string.BotCantJoinGroups), 0).show();
                                return;
                            } catch (Throwable e22) {
                                FileLog.m18e("tmessages", e22);
                                return;
                            }
                        }
                        bundle = new Bundle();
                        bundle.putBoolean("onlySelect", true);
                        bundle.putInt("dialogsType", 2);
                        bundle.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", C0338R.string.AddToTheGroupTitle, UserObject.getUserName(user), "%1$s"));
                        BaseFragment dialogsActivity2 = new DialogsActivity(bundle);
                        dialogsActivity2.setDelegate(new C16122(user));
                        LaunchActivity.this.presentFragment(dialogsActivity2);
                    } else {
                        boolean z2;
                        Bundle bundle2 = new Bundle();
                        long j;
                        if (tL_contacts_resolvedPeer.chats.isEmpty()) {
                            bundle2.putInt("user_id", ((User) tL_contacts_resolvedPeer.users.get(0)).id);
                            j = (long) ((User) tL_contacts_resolvedPeer.users.get(0)).id;
                        } else {
                            bundle2.putInt("chat_id", ((Chat) tL_contacts_resolvedPeer.chats.get(0)).id);
                            j = (long) (-((Chat) tL_contacts_resolvedPeer.chats.get(0)).id);
                        }
                        if (C16149.this.val$botUser == null || tL_contacts_resolvedPeer.users.size() <= 0 || !((User) tL_contacts_resolvedPeer.users.get(0)).bot) {
                            z2 = false;
                        } else {
                            bundle2.putString("botUser", C16149.this.val$botUser);
                            z2 = true;
                        }
                        if (C16149.this.val$messageId != null) {
                            bundle2.putInt("message_id", C16149.this.val$messageId.intValue());
                        }
                        BaseFragment baseFragment = !LaunchActivity.mainFragmentsStack.isEmpty() ? (BaseFragment) LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1) : null;
                        if (baseFragment != null && !MessagesController.checkCanOpenChat(bundle2, baseFragment)) {
                            return;
                        }
                        if (z2 && baseFragment != null && (baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getDialogId() == r4) {
                            ((ChatActivity) baseFragment).setBotUser(C16149.this.val$botUser);
                            return;
                        }
                        BaseFragment chatActivity = new ChatActivity(bundle2);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        LaunchActivity.this.actionBarLayout.presentFragment(chatActivity, false, true, true);
                    }
                }
            }
        }

        C16149(ProgressDialog progressDialog, String str, String str2, String str3, Integer num) {
            this.val$progressDialog = progressDialog;
            this.val$game = str;
            this.val$botChat = str2;
            this.val$botUser = str3;
            this.val$messageId = num;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C16131(tLObject, tL_error));
        }
    }

    private class VcardData {
        String name;
        ArrayList<String> phones;

        private VcardData() {
            this.phones = new ArrayList();
        }
    }

    static {
        mainFragmentsStack = new ArrayList();
        layerFragmentsStack = new ArrayList();
        rightFragmentsStack = new ArrayList();
    }

    private void applyAdvanceTheme(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            boolean b;
            File file = new File(data.getPath());
            int i = (file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("png")) ? 1 : 0;
            boolean endsWith = file.getName().toLowerCase().endsWith("ttf");
            if (i != 0) {
                try {
                    b = ThemeUtil.m2491b(file.getAbsolutePath());
                } catch (Throwable th) {
                    file.delete();
                    new File(data.getPath().substring(0, data.getPath().lastIndexOf(".")) + "_wallpaper.jpg").delete();
                }
            } else {
                b = endsWith ? ThemeUtil.m2493c(file.getAbsolutePath()) : ThemeUtil.m2488a(file);
            }
            file.delete();
            new File(data.getPath().substring(0, data.getPath().lastIndexOf(".")) + "_wallpaper.jpg").delete();
            Builder builder = new Builder(this);
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            if (b) {
                CharSequence string = i != 0 ? LocaleController.getString("WallpaperApplied", C0338R.string.WallpaperApplied) : endsWith ? LocaleController.getString("FontApplied", C0338R.string.FontApplied) : LocaleController.getString("AdvanceThemeApplied", C0338R.string.AdvanceThemeApplied);
                builder.setMessage(string);
            } else {
                builder.setMessage(LocaleController.getString("ErrorOccurred", C0338R.string.ErrorOccurred));
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    LaunchActivity.this.finishApp();
                }
            });
            builder.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialogInterface) {
                    LaunchActivity.this.finishApp();
                }
            });
            builder.create().show();
        }
    }

    private void checkLayout() {
        int i = 8;
        int i2 = 0;
        if (!AndroidUtilities.isTablet()) {
            return;
        }
        if (AndroidUtilities.isInMultiwindow || (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2)) {
            this.tabletFullSize = true;
            if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                while (this.rightActionBarLayout.fragmentsStack.size() > 0) {
                    BaseFragment baseFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(0);
                    baseFragment.onPause();
                    this.rightActionBarLayout.fragmentsStack.remove(0);
                    this.actionBarLayout.fragmentsStack.add(baseFragment);
                }
                if (this.passcodeView.getVisibility() != 0) {
                    this.actionBarLayout.showLastFragment();
                }
            }
            this.shadowTabletSide.setVisibility(8);
            this.rightActionBarLayout.setVisibility(8);
            View view = this.backgroundTablet;
            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                i = 0;
            }
            view.setVisibility(i);
            return;
        }
        this.tabletFullSize = false;
        if (this.actionBarLayout.fragmentsStack.size() >= 2) {
            while (1 < this.actionBarLayout.fragmentsStack.size()) {
                baseFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(1);
                baseFragment.onPause();
                this.actionBarLayout.fragmentsStack.remove(1);
                this.rightActionBarLayout.fragmentsStack.add(baseFragment);
            }
            if (this.passcodeView.getVisibility() != 0) {
                this.actionBarLayout.showLastFragment();
                this.rightActionBarLayout.showLastFragment();
            }
        }
        this.rightActionBarLayout.setVisibility(this.rightActionBarLayout.fragmentsStack.isEmpty() ? 8 : 0);
        this.backgroundTablet.setVisibility(this.rightActionBarLayout.fragmentsStack.isEmpty() ? 0 : 8);
        FrameLayout frameLayout = this.shadowTabletSide;
        if (this.actionBarLayout.fragmentsStack.isEmpty()) {
            i2 = 8;
        }
        frameLayout.setVisibility(i2);
    }

    private void finishApp() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 1234567, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    private boolean handleIntent(Intent intent, boolean z, boolean z2, boolean z3) {
        Throwable e;
        Integer num;
        boolean z4;
        Bundle bundle;
        BaseFragment baseFragment;
        BaseFragment dialogsActivity;
        int flags = intent.getFlags();
        if (!(intent == null || intent.getAction() == null || !intent.getAction().equals("android.intent.action.EDIT"))) {
            if (intent.getStringExtra("settingPath") != null) {
                if (intent.getStringExtra("settingPath").equals("emoji")) {
                    presentFragment(new EmojiSettingsActivity());
                }
                return false;
            }
        }
        if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.EDIT") && intent.getData() != null) {
            applyAdvanceTheme(intent);
            return false;
        } else if (z3 || !(AndroidUtilities.needShowPasscode(true) || UserConfig.isWaitingForPasscodeEnter)) {
            Object obj;
            Integer num2;
            int i;
            boolean z5;
            Bundle bundle2;
            boolean z6 = false;
            Integer valueOf = Integer.valueOf(0);
            Integer valueOf2 = Integer.valueOf(0);
            Integer valueOf3 = Integer.valueOf(0);
            Integer valueOf4 = Integer.valueOf(0);
            long j = (intent == null || intent.getExtras() == null) ? 0 : intent.getExtras().getLong("dialogId", 0);
            Object obj2 = null;
            this.photoPathsArray = null;
            this.videoPath = null;
            this.sendingText = null;
            this.documentsPathsArray = null;
            this.documentsOriginalPathsArray = null;
            this.documentsMimeType = null;
            this.documentsUrisArray = null;
            this.contactsToSend = null;
            if (!(!UserConfig.isClientActivated() || (AccessibilityNodeInfoCompat.ACTION_DISMISS & flags) != 0 || intent == null || intent.getAction() == null || z2)) {
                String type;
                String stringExtra;
                String stringExtra2;
                Parcelable parcelableExtra;
                Uri uri;
                int i2;
                String queryParameter;
                if ("android.intent.action.SEND".equals(intent.getAction())) {
                    Object obj3 = null;
                    type = intent.getType();
                    if (type == null || !type.equals("text/x-vcard")) {
                        stringExtra = intent.getStringExtra("android.intent.extra.TEXT");
                        if (stringExtra == null) {
                            CharSequence charSequenceExtra = intent.getCharSequenceExtra("android.intent.extra.TEXT");
                            if (charSequenceExtra != null) {
                                stringExtra = charSequenceExtra.toString();
                            }
                        }
                        stringExtra2 = intent.getStringExtra("android.intent.extra.SUBJECT");
                        if (stringExtra != null && stringExtra.length() != 0) {
                            if (!((!stringExtra.startsWith("http://") && !stringExtra.startsWith("https://")) || stringExtra2 == null || stringExtra2.length() == 0)) {
                                stringExtra = stringExtra2 + "\n" + stringExtra;
                            }
                            this.sendingText = stringExtra;
                        } else if (stringExtra2 != null && stringExtra2.length() > 0) {
                            this.sendingText = stringExtra2;
                        }
                        parcelableExtra = intent.getParcelableExtra("android.intent.extra.STREAM");
                        if (parcelableExtra != null) {
                            if (!(parcelableExtra instanceof Uri)) {
                                parcelableExtra = Uri.parse(parcelableExtra.toString());
                            }
                            uri = (Uri) parcelableExtra;
                            if (uri != null && AndroidUtilities.isInternalUri(uri)) {
                                obj3 = 1;
                            }
                            if (obj3 == null) {
                                if (uri == null || ((type == null || !type.startsWith("image/")) && !uri.toString().toLowerCase().endsWith(".jpg"))) {
                                    stringExtra2 = AndroidUtilities.getPath(uri);
                                    if (stringExtra2 != null) {
                                        if (stringExtra2.startsWith("file:")) {
                                            stringExtra2 = stringExtra2.replace("file://", TtmlNode.ANONYMOUS_REGION_ID);
                                        }
                                        if (type == null || !type.startsWith("video/")) {
                                            if (this.documentsPathsArray == null) {
                                                this.documentsPathsArray = new ArrayList();
                                                this.documentsOriginalPathsArray = new ArrayList();
                                            }
                                            this.documentsPathsArray.add(stringExtra2);
                                            this.documentsOriginalPathsArray.add(uri.toString());
                                        } else {
                                            this.videoPath = stringExtra2;
                                        }
                                    } else {
                                        if (this.documentsUrisArray == null) {
                                            this.documentsUrisArray = new ArrayList();
                                        }
                                        this.documentsUrisArray.add(uri);
                                        this.documentsMimeType = type;
                                    }
                                } else {
                                    if (this.photoPathsArray == null) {
                                        this.photoPathsArray = new ArrayList();
                                    }
                                    this.photoPathsArray.add(uri);
                                }
                            }
                            obj = obj3;
                        } else {
                            obj = this.sendingText == null ? 1 : null;
                        }
                    } else {
                        uri = (Uri) intent.getExtras().get("android.intent.extra.STREAM");
                        if (uri != null) {
                            InputStream openInputStream = getContentResolver().openInputStream(uri);
                            ArrayList arrayList = new ArrayList();
                            VcardData vcardData = null;
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openInputStream, C0700C.UTF8_NAME));
                            while (true) {
                                stringExtra2 = bufferedReader.readLine();
                                if (stringExtra2 != null) {
                                    FileLog.m16e("tmessages", stringExtra2);
                                    String[] split = stringExtra2.split(":");
                                    if (split.length == 2) {
                                        VcardData vcardData2;
                                        if (split[0].equals("BEGIN") && split[1].equals("VCARD")) {
                                            LaunchActivity launchActivity = this;
                                            vcardData = new VcardData();
                                            arrayList.add(vcardData);
                                            vcardData2 = vcardData;
                                        } else {
                                            vcardData2 = (split[0].equals("END") && split[1].equals("VCARD")) ? null : vcardData;
                                        }
                                        if (vcardData2 == null) {
                                            vcardData = vcardData2;
                                        } else {
                                            if (split[0].startsWith("FN") || (split[0].startsWith("ORG") && TextUtils.isEmpty(vcardData2.name))) {
                                                stringExtra = null;
                                                stringExtra2 = null;
                                                for (String queryParameter2 : split[0].split(";")) {
                                                    String[] split2 = queryParameter2.split("=");
                                                    int length = split2.length;
                                                    if (r0 == 2) {
                                                        if (split2[0].equals("CHARSET")) {
                                                            stringExtra = split2[1];
                                                        } else {
                                                            if (split2[0].equals("ENCODING")) {
                                                                stringExtra2 = split2[1];
                                                            }
                                                        }
                                                    }
                                                }
                                                vcardData2.name = split[1];
                                                if (stringExtra2 != null && stringExtra2.equalsIgnoreCase("QUOTED-PRINTABLE")) {
                                                    while (vcardData2.name.endsWith("=") && stringExtra2 != null) {
                                                        vcardData2.name = vcardData2.name.substring(0, vcardData2.name.length() - 1);
                                                        type = bufferedReader.readLine();
                                                        if (type == null) {
                                                            break;
                                                        }
                                                        vcardData2.name += type;
                                                    }
                                                    byte[] decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(vcardData2.name.getBytes());
                                                    if (!(decodeQuotedPrintable == null || decodeQuotedPrintable.length == 0)) {
                                                        type = new String(decodeQuotedPrintable, stringExtra);
                                                        if (type != null) {
                                                            vcardData2.name = type;
                                                        }
                                                    }
                                                }
                                            } else {
                                                try {
                                                    if (split[0].startsWith("TEL")) {
                                                        stringExtra = PhoneFormat.stripExceptNumbers(split[1], true);
                                                        if (stringExtra.length() > 0) {
                                                            vcardData2.phones.add(stringExtra);
                                                        }
                                                    }
                                                } catch (Throwable e2) {
                                                    FileLog.m18e("tmessages", e2);
                                                    obj = 1;
                                                }
                                            }
                                            vcardData = vcardData2;
                                        }
                                    }
                                } else {
                                    try {
                                        break;
                                    } catch (Throwable e22) {
                                        FileLog.m18e("tmessages", e22);
                                    }
                                }
                            }
                            bufferedReader.close();
                            openInputStream.close();
                            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                                vcardData = (VcardData) arrayList.get(i3);
                                if (!(vcardData.name == null || vcardData.phones.isEmpty())) {
                                    if (this.contactsToSend == null) {
                                        this.contactsToSend = new ArrayList();
                                    }
                                    for (i2 = 0; i2 < vcardData.phones.size(); i2++) {
                                        stringExtra2 = (String) vcardData.phones.get(i2);
                                        User tL_userContact_old2 = new TL_userContact_old2();
                                        tL_userContact_old2.phone = stringExtra2;
                                        tL_userContact_old2.first_name = vcardData.name;
                                        tL_userContact_old2.last_name = TtmlNode.ANONYMOUS_REGION_ID;
                                        tL_userContact_old2.id = 0;
                                        this.contactsToSend.add(tL_userContact_old2);
                                    }
                                }
                            }
                            obj = null;
                        } else {
                            obj = 1;
                        }
                    }
                    if (obj != null) {
                        Toast.makeText(this, "Unsupported content", 0).show();
                    }
                    obj = null;
                    num2 = valueOf4;
                } else if (intent.getAction().equals("android.intent.action.SEND_MULTIPLE")) {
                    try {
                        ArrayList arrayList2;
                        Object parse;
                        Parcelable parcelable;
                        ArrayList parcelableArrayListExtra = intent.getParcelableArrayListExtra("android.intent.extra.STREAM");
                        r7 = intent.getType();
                        if (parcelableArrayListExtra != null) {
                            i = 0;
                            while (i < parcelableArrayListExtra.size()) {
                                parcelableExtra = (Parcelable) parcelableArrayListExtra.get(i);
                                if (!(parcelableExtra instanceof Uri)) {
                                    parcelableExtra = Uri.parse(parcelableExtra.toString());
                                }
                                uri = (Uri) parcelableExtra;
                                if (uri == null || !AndroidUtilities.isInternalUri(uri)) {
                                    r2 = i;
                                } else {
                                    parcelableArrayListExtra.remove(i);
                                    r2 = i - 1;
                                }
                                i = r2 + 1;
                            }
                            if (parcelableArrayListExtra.isEmpty()) {
                                arrayList2 = null;
                                if (arrayList2 != null) {
                                    obj = 1;
                                } else if (r7 == null && r7.startsWith("image/")) {
                                    for (i = 0; i < arrayList2.size(); i++) {
                                        parcelableExtra = (Parcelable) arrayList2.get(i);
                                        if (!(parcelableExtra instanceof Uri)) {
                                            parcelableExtra = Uri.parse(parcelableExtra.toString());
                                        }
                                        uri = (Uri) parcelableExtra;
                                        if (this.photoPathsArray == null) {
                                            this.photoPathsArray = new ArrayList();
                                        }
                                        this.photoPathsArray.add(uri);
                                    }
                                    obj = null;
                                } else {
                                    for (i2 = 0; i2 < arrayList2.size(); i2++) {
                                        parcelableExtra = (Parcelable) arrayList2.get(i2);
                                        if (parcelableExtra instanceof Uri) {
                                            parse = Uri.parse(parcelableExtra.toString());
                                        } else {
                                            parcelable = parcelableExtra;
                                        }
                                        obj = AndroidUtilities.getPath((Uri) parse);
                                        parse = parse.toString();
                                        if (parse == null) {
                                            parse = obj;
                                        }
                                        if (obj == null) {
                                            if (obj.startsWith("file:")) {
                                                obj = obj.replace("file://", TtmlNode.ANONYMOUS_REGION_ID);
                                            }
                                            if (this.documentsPathsArray == null) {
                                                this.documentsPathsArray = new ArrayList();
                                                this.documentsOriginalPathsArray = new ArrayList();
                                            }
                                            this.documentsPathsArray.add(obj);
                                            this.documentsOriginalPathsArray.add(parse);
                                        }
                                    }
                                    obj = null;
                                }
                                if (obj != null) {
                                    Toast.makeText(this, "Unsupported content", 0).show();
                                }
                                obj = null;
                                num2 = valueOf4;
                            }
                        }
                        arrayList2 = parcelableArrayListExtra;
                        if (arrayList2 != null) {
                            obj = 1;
                        } else {
                            if (r7 == null) {
                            }
                            for (i2 = 0; i2 < arrayList2.size(); i2++) {
                                parcelableExtra = (Parcelable) arrayList2.get(i2);
                                if (parcelableExtra instanceof Uri) {
                                    parcelable = parcelableExtra;
                                } else {
                                    parse = Uri.parse(parcelableExtra.toString());
                                }
                                obj = AndroidUtilities.getPath((Uri) parse);
                                parse = parse.toString();
                                if (parse == null) {
                                    parse = obj;
                                }
                                if (obj == null) {
                                    if (obj.startsWith("file:")) {
                                        obj = obj.replace("file://", TtmlNode.ANONYMOUS_REGION_ID);
                                    }
                                    if (this.documentsPathsArray == null) {
                                        this.documentsPathsArray = new ArrayList();
                                        this.documentsOriginalPathsArray = new ArrayList();
                                    }
                                    this.documentsPathsArray.add(obj);
                                    this.documentsOriginalPathsArray.add(parse);
                                }
                            }
                            obj = null;
                        }
                    } catch (Throwable e222) {
                        FileLog.m18e("tmessages", e222);
                        obj = 1;
                    }
                    if (obj != null) {
                        Toast.makeText(this, "Unsupported content", 0).show();
                    }
                    obj = null;
                    num2 = valueOf4;
                } else if ("android.intent.action.VIEW".equals(intent.getAction())) {
                    Uri data = intent.getData();
                    if (data != null) {
                        String str;
                        String replace;
                        String str2;
                        String str3;
                        String str4;
                        boolean z7;
                        Integer num3;
                        Integer num4 = null;
                        boolean z8 = false;
                        stringExtra = data.getScheme();
                        if (stringExtra != null) {
                            String str5;
                            if (stringExtra.equals("http") || stringExtra.equals("https")) {
                                String str6;
                                boolean z9;
                                String str7;
                                String str8;
                                stringExtra = data.getHost().toLowerCase();
                                if (stringExtra.equals("telegram.me") || stringExtra.equals("telegram.dog")) {
                                    stringExtra = data.getPath();
                                    if (stringExtra != null && stringExtra.length() > 1) {
                                        stringExtra = stringExtra.substring(1);
                                        if (stringExtra.startsWith("joinchat/")) {
                                            stringExtra2 = null;
                                            str = null;
                                            replace = stringExtra.replace("joinchat/", TtmlNode.ANONYMOUS_REGION_ID);
                                            stringExtra = null;
                                            r7 = null;
                                            str6 = null;
                                            str2 = null;
                                            str3 = null;
                                            str5 = null;
                                        } else if (stringExtra.startsWith("addstickers/")) {
                                            stringExtra2 = null;
                                            str = null;
                                            replace = null;
                                            str6 = stringExtra.replace("addstickers/", TtmlNode.ANONYMOUS_REGION_ID);
                                            stringExtra = null;
                                            r7 = null;
                                            str2 = null;
                                            str3 = null;
                                            str5 = null;
                                        } else if (stringExtra.startsWith("msg/") || stringExtra.startsWith("share/")) {
                                            stringExtra = data.getQueryParameter("url");
                                            if (stringExtra == null) {
                                                stringExtra = TtmlNode.ANONYMOUS_REGION_ID;
                                            }
                                            if (data.getQueryParameter(MimeTypes.BASE_TYPE_TEXT) != null) {
                                                if (stringExtra.length() > 0) {
                                                    stringExtra2 = stringExtra + "\n";
                                                    z5 = true;
                                                } else {
                                                    stringExtra2 = stringExtra;
                                                    z5 = false;
                                                }
                                                stringExtra2 = stringExtra2 + data.getQueryParameter(MimeTypes.BASE_TYPE_TEXT);
                                            } else {
                                                stringExtra2 = stringExtra;
                                                z5 = false;
                                            }
                                            if (stringExtra2.length() > MessagesController.UPDATE_MASK_CHAT_ADMINS) {
                                                stringExtra2 = stringExtra2.substring(0, MessagesController.UPDATE_MASK_CHAT_ADMINS);
                                            }
                                            while (stringExtra2.endsWith("\n")) {
                                                stringExtra2 = stringExtra2.substring(0, stringExtra2.length() - 1);
                                            }
                                            z8 = z5;
                                            str6 = null;
                                            stringExtra = null;
                                            r7 = stringExtra2;
                                            stringExtra2 = null;
                                            str = null;
                                            replace = null;
                                            str5 = null;
                                            str2 = null;
                                            str3 = null;
                                        } else if (stringExtra.startsWith("confirmphone")) {
                                            stringExtra2 = data.getQueryParameter("phone");
                                            stringExtra = data.getQueryParameter("hash");
                                            r7 = null;
                                            str5 = null;
                                            str6 = null;
                                            str2 = null;
                                            str3 = stringExtra2;
                                            stringExtra2 = null;
                                            str = null;
                                            replace = null;
                                        } else if (stringExtra.length() >= 1) {
                                            List pathSegments = data.getPathSegments();
                                            if (pathSegments.size() > 0) {
                                                stringExtra = (String) pathSegments.get(0);
                                                if (pathSegments.size() > 1) {
                                                    num2 = Utilities.parseInt((String) pathSegments.get(1));
                                                    if (num2.intValue() == 0) {
                                                        stringExtra2 = stringExtra;
                                                        num = null;
                                                    } else {
                                                        Integer num5 = num2;
                                                        stringExtra2 = stringExtra;
                                                        num = num5;
                                                    }
                                                } else {
                                                    stringExtra2 = stringExtra;
                                                    num = null;
                                                }
                                            } else {
                                                num = null;
                                                stringExtra2 = null;
                                            }
                                            str = data.getQueryParameter(TtmlNode.START);
                                            str3 = data.getQueryParameter("startgroup");
                                            replace = null;
                                            str2 = stringExtra2;
                                            stringExtra2 = data.getQueryParameter("game");
                                            num4 = num;
                                            stringExtra = null;
                                            r7 = null;
                                            str6 = null;
                                            String str9 = str;
                                            str = str3;
                                            str3 = null;
                                            str5 = str9;
                                        }
                                        z9 = z8;
                                        str4 = replace;
                                        replace = stringExtra2;
                                        stringExtra2 = str2;
                                        str2 = str3;
                                        str3 = str5;
                                        z7 = z9;
                                        str7 = r7;
                                        r7 = str;
                                        str = str7;
                                        str8 = str6;
                                        num3 = num4;
                                        type = str8;
                                        if (str2 == null || stringExtra != null) {
                                            bundle2 = new Bundle();
                                            bundle2.putString("phone", str2);
                                            bundle2.putString("hash", stringExtra);
                                            AndroidUtilities.runOnUIThread(new C16108(bundle2));
                                        } else if (stringExtra2 == null && str4 == null && type == null && str == null && replace == null) {
                                            try {
                                                Cursor query = getContentResolver().query(intent.getData(), null, null, null, null);
                                                if (query != null) {
                                                    if (query.moveToFirst()) {
                                                        r2 = query.getInt(query.getColumnIndex("DATA4"));
                                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                        num = Integer.valueOf(r2);
                                                    } else {
                                                        num = valueOf;
                                                    }
                                                    try {
                                                        query.close();
                                                    } catch (Throwable e3) {
                                                        valueOf = num;
                                                        e222 = e3;
                                                        FileLog.m18e("tmessages", e222);
                                                        obj = null;
                                                        num2 = valueOf4;
                                                        if (valueOf.intValue() == 0) {
                                                            bundle2 = new Bundle();
                                                            bundle2.putInt("user_id", valueOf.intValue());
                                                            if (this.actionBarLayout.presentFragment(new ChatActivity(bundle2), false, true, true)) {
                                                                z6 = true;
                                                            }
                                                            z4 = z6;
                                                        } else if (valueOf2.intValue() == 0) {
                                                            bundle2 = new Bundle();
                                                            bundle2.putInt("chat_id", valueOf2.intValue());
                                                            if (this.actionBarLayout.presentFragment(new ChatActivity(bundle2), false, true, true)) {
                                                                z6 = true;
                                                            }
                                                            z4 = z6;
                                                        } else if (valueOf3.intValue() == 0) {
                                                            bundle = new Bundle();
                                                            bundle.putInt("enc_id", valueOf3.intValue());
                                                            if (this.actionBarLayout.presentFragment(new ChatActivity(bundle), false, true, true)) {
                                                                z6 = true;
                                                            }
                                                            z4 = z6;
                                                        } else if (obj2 == null) {
                                                            if (AndroidUtilities.isTablet()) {
                                                                this.actionBarLayout.removeAllFragments();
                                                            } else if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                                while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                                                                    this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                                                                }
                                                                this.layersActionBarLayout.closeLastFragment(false);
                                                            }
                                                            z = false;
                                                            z4 = false;
                                                        } else if (obj == null) {
                                                            if (AndroidUtilities.isTablet()) {
                                                                while (i < this.actionBarLayout.fragmentsStack.size()) {
                                                                    baseFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(i);
                                                                    if (baseFragment instanceof AudioPlayerActivity) {
                                                                    } else {
                                                                        this.actionBarLayout.removeFragmentFromStack(baseFragment);
                                                                        break;
                                                                        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                    }
                                                                }
                                                                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                            } else {
                                                                while (i < this.layersActionBarLayout.fragmentsStack.size()) {
                                                                    baseFragment = (BaseFragment) this.layersActionBarLayout.fragmentsStack.get(i);
                                                                    if (baseFragment instanceof AudioPlayerActivity) {
                                                                    } else {
                                                                        this.layersActionBarLayout.removeFragmentFromStack(baseFragment);
                                                                        break;
                                                                        this.actionBarLayout.showLastFragment();
                                                                        this.rightActionBarLayout.showLastFragment();
                                                                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                    }
                                                                }
                                                                this.actionBarLayout.showLastFragment();
                                                                this.rightActionBarLayout.showLastFragment();
                                                                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                            }
                                                            this.actionBarLayout.presentFragment(new AudioPlayerActivity(), false, true, true);
                                                            z4 = true;
                                                        } else {
                                                            if (this.videoPath == null) {
                                                            }
                                                            if (!AndroidUtilities.isTablet()) {
                                                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                            }
                                                            if (j != 0) {
                                                                bundle = new Bundle();
                                                                bundle.putBoolean("onlySelect", true);
                                                                if (this.contactsToSend == null) {
                                                                    bundle.putString("selectAlertString", LocaleController.getString("SendContactTo", C0338R.string.SendMessagesTo));
                                                                    bundle.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", C0338R.string.SendContactToGroup));
                                                                } else {
                                                                    bundle.putString("selectAlertString", LocaleController.getString("SendMessagesTo", C0338R.string.SendMessagesTo));
                                                                    bundle.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", C0338R.string.SendMessagesToGroup));
                                                                }
                                                                dialogsActivity = new DialogsActivity(bundle);
                                                                dialogsActivity.setDelegate(this);
                                                                if (AndroidUtilities.isTablet()) {
                                                                    if (this.actionBarLayout.fragmentsStack.size() <= 1) {
                                                                    }
                                                                }
                                                                this.actionBarLayout.presentFragment(dialogsActivity, z5, true, true);
                                                                if (PhotoViewer.getInstance().isVisible()) {
                                                                    PhotoViewer.getInstance().closePhoto(false, true);
                                                                }
                                                                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                if (AndroidUtilities.isTablet()) {
                                                                    this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                } else {
                                                                    this.actionBarLayout.showLastFragment();
                                                                    this.rightActionBarLayout.showLastFragment();
                                                                }
                                                                z4 = true;
                                                            } else {
                                                                didSelectDialog(null, j, false);
                                                                z4 = z6;
                                                            }
                                                        }
                                                        if (AndroidUtilities.isTablet()) {
                                                            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                                                                if (UserConfig.isClientActivated()) {
                                                                    this.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                                    this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                                } else {
                                                                    this.actionBarLayout.addFragmentToStack(CategoryUtil.m352c() ? new CategoryActivity() : new DialogsActivity(null));
                                                                    this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                                }
                                                            }
                                                        } else if (UserConfig.isClientActivated()) {
                                                            if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                                this.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                            }
                                                        } else if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                                                            this.actionBarLayout.addFragmentToStack(CategoryUtil.m352c() ? new CategoryActivity() : new DialogsActivity(null));
                                                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                        }
                                                        this.actionBarLayout.showLastFragment();
                                                        if (AndroidUtilities.isTablet()) {
                                                            this.layersActionBarLayout.showLastFragment();
                                                            this.rightActionBarLayout.showLastFragment();
                                                        }
                                                        intent.setAction(null);
                                                        return z4;
                                                    }
                                                }
                                                num = valueOf;
                                                valueOf = num;
                                            } catch (Exception e4) {
                                                e222 = e4;
                                                FileLog.m18e("tmessages", e222);
                                                obj = null;
                                                num2 = valueOf4;
                                                if (valueOf.intValue() == 0) {
                                                    bundle2 = new Bundle();
                                                    bundle2.putInt("user_id", valueOf.intValue());
                                                    if (this.actionBarLayout.presentFragment(new ChatActivity(bundle2), false, true, true)) {
                                                        z6 = true;
                                                    }
                                                    z4 = z6;
                                                } else if (valueOf2.intValue() == 0) {
                                                    bundle2 = new Bundle();
                                                    bundle2.putInt("chat_id", valueOf2.intValue());
                                                    if (this.actionBarLayout.presentFragment(new ChatActivity(bundle2), false, true, true)) {
                                                        z6 = true;
                                                    }
                                                    z4 = z6;
                                                } else if (valueOf3.intValue() == 0) {
                                                    bundle = new Bundle();
                                                    bundle.putInt("enc_id", valueOf3.intValue());
                                                    if (this.actionBarLayout.presentFragment(new ChatActivity(bundle), false, true, true)) {
                                                        z6 = true;
                                                    }
                                                    z4 = z6;
                                                } else if (obj2 == null) {
                                                    if (AndroidUtilities.isTablet()) {
                                                        this.actionBarLayout.removeAllFragments();
                                                    } else if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                                                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                                                        }
                                                        this.layersActionBarLayout.closeLastFragment(false);
                                                    }
                                                    z = false;
                                                    z4 = false;
                                                } else if (obj == null) {
                                                    if (this.videoPath == null) {
                                                    }
                                                    if (AndroidUtilities.isTablet()) {
                                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                    }
                                                    if (j != 0) {
                                                        didSelectDialog(null, j, false);
                                                        z4 = z6;
                                                    } else {
                                                        bundle = new Bundle();
                                                        bundle.putBoolean("onlySelect", true);
                                                        if (this.contactsToSend == null) {
                                                            bundle.putString("selectAlertString", LocaleController.getString("SendMessagesTo", C0338R.string.SendMessagesTo));
                                                            bundle.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", C0338R.string.SendMessagesToGroup));
                                                        } else {
                                                            bundle.putString("selectAlertString", LocaleController.getString("SendContactTo", C0338R.string.SendMessagesTo));
                                                            bundle.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", C0338R.string.SendContactToGroup));
                                                        }
                                                        dialogsActivity = new DialogsActivity(bundle);
                                                        dialogsActivity.setDelegate(this);
                                                        if (AndroidUtilities.isTablet()) {
                                                            if (this.actionBarLayout.fragmentsStack.size() <= 1) {
                                                            }
                                                        }
                                                        this.actionBarLayout.presentFragment(dialogsActivity, z5, true, true);
                                                        if (PhotoViewer.getInstance().isVisible()) {
                                                            PhotoViewer.getInstance().closePhoto(false, true);
                                                        }
                                                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                        if (AndroidUtilities.isTablet()) {
                                                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                        } else {
                                                            this.actionBarLayout.showLastFragment();
                                                            this.rightActionBarLayout.showLastFragment();
                                                        }
                                                        z4 = true;
                                                    }
                                                } else {
                                                    if (AndroidUtilities.isTablet()) {
                                                        while (i < this.actionBarLayout.fragmentsStack.size()) {
                                                            baseFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(i);
                                                            if (baseFragment instanceof AudioPlayerActivity) {
                                                            } else {
                                                                this.actionBarLayout.removeFragmentFromStack(baseFragment);
                                                                break;
                                                                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                            }
                                                        }
                                                        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    } else {
                                                        while (i < this.layersActionBarLayout.fragmentsStack.size()) {
                                                            baseFragment = (BaseFragment) this.layersActionBarLayout.fragmentsStack.get(i);
                                                            if (baseFragment instanceof AudioPlayerActivity) {
                                                            } else {
                                                                this.layersActionBarLayout.removeFragmentFromStack(baseFragment);
                                                                break;
                                                                this.actionBarLayout.showLastFragment();
                                                                this.rightActionBarLayout.showLastFragment();
                                                                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                            }
                                                        }
                                                        this.actionBarLayout.showLastFragment();
                                                        this.rightActionBarLayout.showLastFragment();
                                                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                    }
                                                    this.actionBarLayout.presentFragment(new AudioPlayerActivity(), false, true, true);
                                                    z4 = true;
                                                }
                                                if (AndroidUtilities.isTablet()) {
                                                    if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                                                        if (UserConfig.isClientActivated()) {
                                                            if (CategoryUtil.m352c()) {
                                                            }
                                                            this.actionBarLayout.addFragmentToStack(CategoryUtil.m352c() ? new CategoryActivity() : new DialogsActivity(null));
                                                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                        } else {
                                                            this.actionBarLayout.addFragmentToStack(new LoginActivity());
                                                            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                        }
                                                    }
                                                } else if (UserConfig.isClientActivated()) {
                                                    if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                                                        if (CategoryUtil.m352c()) {
                                                        }
                                                        this.actionBarLayout.addFragmentToStack(CategoryUtil.m352c() ? new CategoryActivity() : new DialogsActivity(null));
                                                        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                                                    }
                                                } else if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                                    this.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                                    this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                                                }
                                                this.actionBarLayout.showLastFragment();
                                                if (AndroidUtilities.isTablet()) {
                                                    this.layersActionBarLayout.showLastFragment();
                                                    this.rightActionBarLayout.showLastFragment();
                                                }
                                                intent.setAction(null);
                                                return z4;
                                            }
                                        } else {
                                            boolean z10;
                                            if (intent != null) {
                                                if (intent.getStringExtra("com.android.browser.application_id") != null) {
                                                    if (intent.getStringExtra("com.android.browser.application_id").equalsIgnoreCase(getPackageName())) {
                                                        z10 = false;
                                                        runLinkRequest(stringExtra2, str4, type, str3, r7, str, z7, num3, replace, 0, z10);
                                                    }
                                                }
                                            }
                                            z10 = true;
                                            runLinkRequest(stringExtra2, str4, type, str3, r7, str, z7, num3, replace, 0, z10);
                                        }
                                    }
                                }
                                stringExtra = null;
                                stringExtra2 = null;
                                r7 = null;
                                str = null;
                                str6 = null;
                                replace = null;
                                str5 = null;
                                str2 = null;
                                str3 = null;
                                z9 = z8;
                                str4 = replace;
                                replace = stringExtra2;
                                stringExtra2 = str2;
                                str2 = str3;
                                str3 = str5;
                                z7 = z9;
                                str7 = r7;
                                r7 = str;
                                str = str7;
                                str8 = str6;
                                num3 = num4;
                                type = str8;
                                if (str2 == null) {
                                }
                                bundle2 = new Bundle();
                                bundle2.putString("phone", str2);
                                bundle2.putString("hash", stringExtra);
                                AndroidUtilities.runOnUIThread(new C16108(bundle2));
                            } else if (stringExtra.equals("tg")) {
                                stringExtra = data.toString();
                                if (stringExtra.startsWith("tg:resolve") || stringExtra.startsWith("tg://resolve")) {
                                    uri = Uri.parse(stringExtra.replace("tg:resolve", "tg://telegram.org").replace("tg://resolve", "tg://telegram.org"));
                                    str3 = uri.getQueryParameter("domain");
                                    str2 = uri.getQueryParameter(TtmlNode.START);
                                    replace = uri.getQueryParameter("startgroup");
                                    str = uri.getQueryParameter("game");
                                    num4 = Utilities.parseInt(uri.getQueryParameter("post"));
                                    if (num4.intValue() == 0) {
                                        stringExtra = null;
                                        stringExtra2 = str3;
                                        r7 = replace;
                                        str3 = str2;
                                        str2 = null;
                                        replace = str;
                                        z7 = false;
                                        str = null;
                                        str4 = null;
                                        num3 = null;
                                        type = null;
                                    } else {
                                        stringExtra = null;
                                        stringExtra2 = str3;
                                        r7 = replace;
                                        str3 = str2;
                                        str2 = null;
                                        replace = str;
                                        z7 = false;
                                        str = null;
                                        str4 = null;
                                        num3 = num4;
                                        type = null;
                                    }
                                    if (str2 == null) {
                                    }
                                    bundle2 = new Bundle();
                                    bundle2.putString("phone", str2);
                                    bundle2.putString("hash", stringExtra);
                                    AndroidUtilities.runOnUIThread(new C16108(bundle2));
                                } else if (stringExtra.startsWith("tg:join") || stringExtra.startsWith("tg://join")) {
                                    String queryParameter3 = Uri.parse(stringExtra.replace("tg:join", "tg://telegram.org").replace("tg://join", "tg://telegram.org")).getQueryParameter("invite");
                                    stringExtra = null;
                                    stringExtra2 = null;
                                    r7 = null;
                                    str3 = null;
                                    str2 = null;
                                    replace = null;
                                    z7 = false;
                                    str = null;
                                    str4 = queryParameter3;
                                    num3 = null;
                                    type = null;
                                    if (str2 == null) {
                                    }
                                    bundle2 = new Bundle();
                                    bundle2.putString("phone", str2);
                                    bundle2.putString("hash", stringExtra);
                                    AndroidUtilities.runOnUIThread(new C16108(bundle2));
                                } else if (stringExtra.startsWith("tg:addstickers") || stringExtra.startsWith("tg://addstickers")) {
                                    queryParameter2 = Uri.parse(stringExtra.replace("tg:addstickers", "tg://telegram.org").replace("tg://addstickers", "tg://telegram.org")).getQueryParameter("set");
                                    stringExtra = null;
                                    stringExtra2 = null;
                                    r7 = null;
                                    str3 = null;
                                    str2 = null;
                                    replace = null;
                                    z7 = false;
                                    str = null;
                                    str4 = null;
                                    num3 = null;
                                    type = queryParameter2;
                                    if (str2 == null) {
                                    }
                                    bundle2 = new Bundle();
                                    bundle2.putString("phone", str2);
                                    bundle2.putString("hash", stringExtra);
                                    AndroidUtilities.runOnUIThread(new C16108(bundle2));
                                } else if (stringExtra.startsWith("tg:msg") || stringExtra.startsWith("tg://msg") || stringExtra.startsWith("tg://share") || stringExtra.startsWith("tg:share")) {
                                    Uri parse2 = Uri.parse(stringExtra.replace("tg:msg", "tg://telegram.org").replace("tg://msg", "tg://telegram.org").replace("tg://share", "tg://telegram.org").replace("tg:share", "tg://telegram.org"));
                                    stringExtra = parse2.getQueryParameter("url");
                                    if (stringExtra == null) {
                                        stringExtra = TtmlNode.ANONYMOUS_REGION_ID;
                                    }
                                    if (parse2.getQueryParameter(MimeTypes.BASE_TYPE_TEXT) != null) {
                                        if (stringExtra.length() > 0) {
                                            z8 = true;
                                            stringExtra = stringExtra + "\n";
                                        }
                                        stringExtra = stringExtra + parse2.getQueryParameter(MimeTypes.BASE_TYPE_TEXT);
                                    }
                                    if (stringExtra.length() > MessagesController.UPDATE_MASK_CHAT_ADMINS) {
                                        stringExtra = stringExtra.substring(0, MessagesController.UPDATE_MASK_CHAT_ADMINS);
                                    }
                                    while (stringExtra.endsWith("\n")) {
                                        stringExtra = stringExtra.substring(0, stringExtra.length() - 1);
                                    }
                                    num3 = null;
                                    stringExtra2 = null;
                                    str3 = null;
                                    type = null;
                                    str2 = null;
                                    z7 = z8;
                                    str4 = null;
                                    r7 = null;
                                    replace = null;
                                    str = stringExtra;
                                    stringExtra = null;
                                    if (str2 == null) {
                                    }
                                    bundle2 = new Bundle();
                                    bundle2.putString("phone", str2);
                                    bundle2.putString("hash", stringExtra);
                                    AndroidUtilities.runOnUIThread(new C16108(bundle2));
                                } else if (stringExtra.startsWith("tg:confirmphone") || stringExtra.startsWith("tg://confirmphone")) {
                                    str5 = data.getQueryParameter("phone");
                                    stringExtra = data.getQueryParameter("hash");
                                    stringExtra2 = null;
                                    r7 = null;
                                    str3 = null;
                                    str2 = str5;
                                    replace = null;
                                    z7 = false;
                                    str = null;
                                    str4 = null;
                                    num3 = null;
                                    type = null;
                                    if (str2 == null) {
                                    }
                                    bundle2 = new Bundle();
                                    bundle2.putString("phone", str2);
                                    bundle2.putString("hash", stringExtra);
                                    AndroidUtilities.runOnUIThread(new C16108(bundle2));
                                }
                            }
                        }
                        stringExtra = null;
                        stringExtra2 = null;
                        r7 = null;
                        str3 = null;
                        str2 = null;
                        replace = null;
                        z7 = false;
                        str = null;
                        str4 = null;
                        num3 = null;
                        type = null;
                        if (str2 == null) {
                        }
                        bundle2 = new Bundle();
                        bundle2.putString("phone", str2);
                        bundle2.putString("hash", stringExtra);
                        AndroidUtilities.runOnUIThread(new C16108(bundle2));
                    }
                    obj = null;
                    num2 = valueOf4;
                } else if (intent.getAction().equals("com.hanista.mobogram.OPEN_ACCOUNT")) {
                    num2 = Integer.valueOf(1);
                    obj = null;
                } else if (intent.getAction().startsWith("com.tmessages.openchat")) {
                    Integer valueOf5;
                    r2 = intent.getIntExtra("chatId", 0);
                    i = intent.getIntExtra("userId", 0);
                    flags = intent.getIntExtra("encId", 0);
                    if (r2 != 0) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        num2 = valueOf3;
                        valueOf5 = Integer.valueOf(r2);
                        obj = null;
                    } else if (i != 0) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        valueOf = Integer.valueOf(i);
                        obj = null;
                        num2 = valueOf3;
                        valueOf5 = valueOf2;
                    } else if (flags != 0) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        num2 = Integer.valueOf(flags);
                        valueOf5 = valueOf2;
                        obj = null;
                    } else {
                        obj = 1;
                        num2 = valueOf3;
                        valueOf5 = valueOf2;
                    }
                    obj2 = obj;
                    valueOf3 = num2;
                    valueOf2 = valueOf5;
                    obj = null;
                    num2 = valueOf4;
                } else if (intent.getAction().equals("com.tmessages.openplayer")) {
                    obj = 1;
                    num2 = valueOf4;
                } else if (intent.getAction().startsWith("com.tmessages.opendownloadmanager")) {
                    num2 = valueOf4;
                    z6 = this.actionBarLayout.presentFragment(new DownloadManagerActivity(null), false, true, true);
                    obj = null;
                } else if (intent.getAction().startsWith("com.tmessages.opendialogdm")) {
                    if (this.actionBarLayout.presentFragment(new DialogDmActivity(null), false, true, true)) {
                        z6 = true;
                        obj = null;
                        num2 = valueOf4;
                    }
                }
                if (valueOf.intValue() == 0) {
                    bundle2 = new Bundle();
                    bundle2.putInt("user_id", valueOf.intValue());
                    if (mainFragmentsStack.isEmpty() || MessagesController.checkCanOpenChat(bundle2, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
                        if (this.actionBarLayout.presentFragment(new ChatActivity(bundle2), false, true, true)) {
                            z6 = true;
                        }
                    }
                    z4 = z6;
                } else if (valueOf2.intValue() == 0) {
                    bundle2 = new Bundle();
                    bundle2.putInt("chat_id", valueOf2.intValue());
                    if (mainFragmentsStack.isEmpty() || MessagesController.checkCanOpenChat(bundle2, (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
                        if (this.actionBarLayout.presentFragment(new ChatActivity(bundle2), false, true, true)) {
                            z6 = true;
                        }
                    }
                    z4 = z6;
                } else if (valueOf3.intValue() == 0) {
                    bundle = new Bundle();
                    bundle.putInt("enc_id", valueOf3.intValue());
                    if (this.actionBarLayout.presentFragment(new ChatActivity(bundle), false, true, true)) {
                        z6 = true;
                    }
                    z4 = z6;
                } else if (obj2 == null) {
                    if (AndroidUtilities.isTablet()) {
                        this.actionBarLayout.removeAllFragments();
                    } else if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(false);
                    }
                    z = false;
                    z4 = false;
                } else if (obj == null) {
                    if (AndroidUtilities.isTablet()) {
                        for (i = 0; i < this.layersActionBarLayout.fragmentsStack.size(); i++) {
                            baseFragment = (BaseFragment) this.layersActionBarLayout.fragmentsStack.get(i);
                            if (baseFragment instanceof AudioPlayerActivity) {
                                this.layersActionBarLayout.removeFragmentFromStack(baseFragment);
                                break;
                            }
                        }
                        this.actionBarLayout.showLastFragment();
                        this.rightActionBarLayout.showLastFragment();
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    } else {
                        for (i = 0; i < this.actionBarLayout.fragmentsStack.size(); i++) {
                            baseFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(i);
                            if (baseFragment instanceof AudioPlayerActivity) {
                                this.actionBarLayout.removeFragmentFromStack(baseFragment);
                                break;
                            }
                        }
                        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                    this.actionBarLayout.presentFragment(new AudioPlayerActivity(), false, true, true);
                    z4 = true;
                } else if (this.videoPath == null || this.photoPathsArray != null || this.sendingText != null || this.documentsPathsArray != null || this.contactsToSend != null || this.documentsUrisArray != null) {
                    if (AndroidUtilities.isTablet()) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    }
                    if (j != 0) {
                        bundle = new Bundle();
                        bundle.putBoolean("onlySelect", true);
                        if (this.contactsToSend == null) {
                            bundle.putString("selectAlertString", LocaleController.getString("SendContactTo", C0338R.string.SendMessagesTo));
                            bundle.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", C0338R.string.SendContactToGroup));
                        } else {
                            bundle.putString("selectAlertString", LocaleController.getString("SendMessagesTo", C0338R.string.SendMessagesTo));
                            bundle.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", C0338R.string.SendMessagesToGroup));
                        }
                        dialogsActivity = new DialogsActivity(bundle);
                        dialogsActivity.setDelegate(this);
                        z5 = AndroidUtilities.isTablet() ? this.layersActionBarLayout.fragmentsStack.size() <= 0 && (this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity) : this.actionBarLayout.fragmentsStack.size() <= 1 && (this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity);
                        this.actionBarLayout.presentFragment(dialogsActivity, z5, true, true);
                        if (PhotoViewer.getInstance().isVisible()) {
                            PhotoViewer.getInstance().closePhoto(false, true);
                        }
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        if (AndroidUtilities.isTablet()) {
                            this.actionBarLayout.showLastFragment();
                            this.rightActionBarLayout.showLastFragment();
                        } else {
                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        }
                        z4 = true;
                    } else {
                        didSelectDialog(null, j, false);
                        z4 = z6;
                    }
                } else if (num2.intValue() != 0) {
                    this.actionBarLayout.presentFragment(new SettingsActivity(), false, true, true);
                    if (AndroidUtilities.isTablet()) {
                        this.actionBarLayout.showLastFragment();
                        this.rightActionBarLayout.showLastFragment();
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    } else {
                        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                    z4 = true;
                } else {
                    z4 = z6;
                }
                if (!(z4 || r31)) {
                    if (AndroidUtilities.isTablet()) {
                        if (UserConfig.isClientActivated()) {
                            if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                                this.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                            }
                        } else if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                            if (CategoryUtil.m352c()) {
                            }
                            this.actionBarLayout.addFragmentToStack(CategoryUtil.m352c() ? new CategoryActivity() : new DialogsActivity(null));
                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        }
                    } else if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                        if (UserConfig.isClientActivated()) {
                            this.actionBarLayout.addFragmentToStack(new LoginActivity());
                            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        } else {
                            if (CategoryUtil.m352c()) {
                            }
                            this.actionBarLayout.addFragmentToStack(CategoryUtil.m352c() ? new CategoryActivity() : new DialogsActivity(null));
                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        }
                    }
                    this.actionBarLayout.showLastFragment();
                    if (AndroidUtilities.isTablet()) {
                        this.layersActionBarLayout.showLastFragment();
                        this.rightActionBarLayout.showLastFragment();
                    }
                }
                intent.setAction(null);
                return z4;
            }
            obj = null;
            num2 = valueOf4;
            if (valueOf.intValue() == 0) {
                bundle2 = new Bundle();
                bundle2.putInt("user_id", valueOf.intValue());
                if (this.actionBarLayout.presentFragment(new ChatActivity(bundle2), false, true, true)) {
                    z6 = true;
                }
                z4 = z6;
            } else if (valueOf2.intValue() == 0) {
                bundle2 = new Bundle();
                bundle2.putInt("chat_id", valueOf2.intValue());
                if (this.actionBarLayout.presentFragment(new ChatActivity(bundle2), false, true, true)) {
                    z6 = true;
                }
                z4 = z6;
            } else if (valueOf3.intValue() == 0) {
                bundle = new Bundle();
                bundle.putInt("enc_id", valueOf3.intValue());
                if (this.actionBarLayout.presentFragment(new ChatActivity(bundle), false, true, true)) {
                    z6 = true;
                }
                z4 = z6;
            } else if (obj2 == null) {
                if (AndroidUtilities.isTablet()) {
                    this.actionBarLayout.removeAllFragments();
                } else if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(false);
                }
                z = false;
                z4 = false;
            } else if (obj == null) {
                if (this.videoPath == null) {
                }
                if (AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                }
                if (j != 0) {
                    didSelectDialog(null, j, false);
                    z4 = z6;
                } else {
                    bundle = new Bundle();
                    bundle.putBoolean("onlySelect", true);
                    if (this.contactsToSend == null) {
                        bundle.putString("selectAlertString", LocaleController.getString("SendMessagesTo", C0338R.string.SendMessagesTo));
                        bundle.putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", C0338R.string.SendMessagesToGroup));
                    } else {
                        bundle.putString("selectAlertString", LocaleController.getString("SendContactTo", C0338R.string.SendMessagesTo));
                        bundle.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", C0338R.string.SendContactToGroup));
                    }
                    dialogsActivity = new DialogsActivity(bundle);
                    dialogsActivity.setDelegate(this);
                    if (AndroidUtilities.isTablet()) {
                        if (this.actionBarLayout.fragmentsStack.size() <= 1) {
                        }
                    }
                    this.actionBarLayout.presentFragment(dialogsActivity, z5, true, true);
                    if (PhotoViewer.getInstance().isVisible()) {
                        PhotoViewer.getInstance().closePhoto(false, true);
                    }
                    this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    if (AndroidUtilities.isTablet()) {
                        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    } else {
                        this.actionBarLayout.showLastFragment();
                        this.rightActionBarLayout.showLastFragment();
                    }
                    z4 = true;
                }
            } else {
                if (AndroidUtilities.isTablet()) {
                    while (i < this.actionBarLayout.fragmentsStack.size()) {
                        baseFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(i);
                        if (baseFragment instanceof AudioPlayerActivity) {
                        } else {
                            this.actionBarLayout.removeFragmentFromStack(baseFragment);
                            break;
                            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                        }
                    }
                    this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                } else {
                    while (i < this.layersActionBarLayout.fragmentsStack.size()) {
                        baseFragment = (BaseFragment) this.layersActionBarLayout.fragmentsStack.get(i);
                        if (baseFragment instanceof AudioPlayerActivity) {
                        } else {
                            this.layersActionBarLayout.removeFragmentFromStack(baseFragment);
                            break;
                            this.actionBarLayout.showLastFragment();
                            this.rightActionBarLayout.showLastFragment();
                            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        }
                    }
                    this.actionBarLayout.showLastFragment();
                    this.rightActionBarLayout.showLastFragment();
                    this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                }
                this.actionBarLayout.presentFragment(new AudioPlayerActivity(), false, true, true);
                z4 = true;
            }
            if (AndroidUtilities.isTablet()) {
                if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                    if (UserConfig.isClientActivated()) {
                        if (CategoryUtil.m352c()) {
                        }
                        this.actionBarLayout.addFragmentToStack(CategoryUtil.m352c() ? new CategoryActivity() : new DialogsActivity(null));
                        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    } else {
                        this.actionBarLayout.addFragmentToStack(new LoginActivity());
                        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                    }
                }
            } else if (UserConfig.isClientActivated()) {
                if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                    if (CategoryUtil.m352c()) {
                    }
                    this.actionBarLayout.addFragmentToStack(CategoryUtil.m352c() ? new CategoryActivity() : new DialogsActivity(null));
                    this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                }
            } else if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                this.layersActionBarLayout.addFragmentToStack(new LoginActivity());
                this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            }
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.showLastFragment();
                this.rightActionBarLayout.showLastFragment();
            }
            intent.setAction(null);
            return z4;
        } else {
            showPasscodeActivity();
            this.passcodeSaveIntent = intent;
            this.passcodeSaveIntentIsNew = z;
            this.passcodeSaveIntentIsRestore = z2;
            UserConfig.saveConfig(false);
            return false;
        }
    }

    private void initGhostMode() {
        if (this.actionBarLayout.getCurrentActionBar() != null) {
            this.actionBarLayout.getCurrentActionBar().changeGhostModeVisibility();
        }
    }

    private void initThemeListView() {
        if (ThemeUtil.m2490b()) {
            Glow.m521a(this.listView, AdvanceTheme.bc);
        }
    }

    private boolean isMainMoboAvailable() {
        if (MoboUtils.m1726i(this)) {
            return true;
        }
        Builder builder = new Builder(this);
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        String str;
        Object[] objArr;
        if (!MoboUtils.m1731n(this)) {
            str = "MainMoboRequiredMessage";
            objArr = new Object[1];
            objArr[0] = MoboUtils.m1727j(this) ? LocaleController.getString("Second", C0338R.string.Second) : LocaleController.getString("Third", C0338R.string.Third);
            builder.setMessage(LocaleController.formatString(str, C0338R.string.MainMoboRequiredMessage, objArr));
            builder.setPositiveButton(LocaleController.getString("DownloadFromBazzar", C0338R.string.DownloadFromBazzar), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("https://cafebazaar.ir/app/com.hanista.mobogram/?l=fa"));
                    LaunchActivity.this.startActivity(intent);
                    LaunchActivity.this.finish();
                }
            });
            builder.create().show();
        } else if (!MoboUtils.m1732o(this)) {
            str = "MainMoboInvalidMessage";
            objArr = new Object[1];
            objArr[0] = MoboUtils.m1727j(this) ? LocaleController.getString("Second", C0338R.string.Second) : LocaleController.getString("Third", C0338R.string.Third);
            builder.setMessage(LocaleController.formatString(str, C0338R.string.MainMoboInvalidMessage, objArr));
            builder.setPositiveButton(LocaleController.getString("DownloadFromBazzar", C0338R.string.DownloadFromBazzar), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("https://cafebazaar.ir/app/com.hanista.mobogram/?l=fa"));
                    LaunchActivity.this.startActivity(intent);
                    LaunchActivity.this.finish();
                }
            });
            builder.create().show();
        } else if (MoboUtils.m1733p(this)) {
            return true;
        } else {
            builder.setMessage(LocaleController.getString("MainMoboVersionInvalidMessage", C0338R.string.MainMoboVersionInvalidMessage));
            builder.setPositiveButton(LocaleController.getString("DownloadFromBazzar", C0338R.string.DownloadFromBazzar), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse("https://cafebazaar.ir/app/com.hanista.mobogram/?l=fa"));
                    LaunchActivity.this.startActivity(intent);
                    LaunchActivity.this.finish();
                }
            });
            builder.create().show();
        }
        return false;
    }

    private void onFinish() {
        if (!this.finished) {
            this.finished = true;
            if (this.lockRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
                this.lockRunnable = null;
            }
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mainUserInfoChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.menuSettingsChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdatedConnectionState);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needShowAlert);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        }
    }

    private void onPasscodePause() {
        if (this.lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (UserConfig.passcodeHash.length() != 0) {
            UserConfig.lastPauseTime = ConnectionsManager.getInstance().getCurrentTime();
            this.lockRunnable = new Runnable() {
                public void run() {
                    if (LaunchActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            FileLog.m16e("tmessages", "lock app");
                            LaunchActivity.this.showPasscodeActivity();
                        } else {
                            FileLog.m16e("tmessages", "didn't pass lock check");
                        }
                        LaunchActivity.this.lockRunnable = null;
                    }
                }
            };
            if (UserConfig.appLocked) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, 1000);
            } else if (UserConfig.autoLockIn != 0) {
                AndroidUtilities.runOnUIThread(this.lockRunnable, (((long) UserConfig.autoLockIn) * 1000) + 1000);
            }
        } else {
            UserConfig.lastPauseTime = 0;
        }
        UserConfig.saveConfig(false);
    }

    private void onPasscodeResume() {
        if (this.lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (AndroidUtilities.needShowPasscode(true)) {
            showPasscodeActivity();
        }
        if (UserConfig.lastPauseTime != 0) {
            UserConfig.lastPauseTime = 0;
            UserConfig.saveConfig(false);
        }
    }

    private void showCreateChatDialog() {
        Builder builder = new Builder(this);
        List arrayList = new ArrayList();
        arrayList.add(LocaleController.getString("NewGroup", C0338R.string.NewGroup));
        arrayList.add(LocaleController.getString("NewSecretChat", C0338R.string.NewSecretChat));
        arrayList.add(LocaleController.getString("NewChannel", C0338R.string.NewChannel));
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (MessagesController.isFeatureEnabled("chat_create", (BaseFragment) LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1))) {
                        LaunchActivity.this.presentFragment(new GroupCreateActivity());
                    }
                } else if (i == 1) {
                    r0 = new Bundle();
                    r0.putBoolean("onlyUsers", true);
                    r0.putBoolean("destroyAfterSelect", true);
                    r0.putBoolean("createSecretChat", true);
                    r0.putBoolean("allowBots", false);
                    LaunchActivity.this.presentFragment(new ContactsActivity(r0));
                } else if (MessagesController.isFeatureEnabled("broadcast_create", (BaseFragment) LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1))) {
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (sharedPreferences.getBoolean("channel_intro", false)) {
                        r0 = new Bundle();
                        r0.putInt("step", 0);
                        LaunchActivity.this.presentFragment(new ChannelCreateActivity(r0));
                        return;
                    }
                    LaunchActivity.this.presentFragment(new ChannelIntroActivity());
                    sharedPreferences.edit().putBoolean("channel_intro", true).commit();
                }
            }
        });
        builder.create().show();
    }

    private void showDownloadMobo2Dialog() {
        Builder builder = new Builder(this);
        builder.setTitle(LocaleController.getString("AddUser", C0338R.string.AddUser)).setMessage(LocaleController.getString("DownloadMobo2Help", C0338R.string.DownloadMobo2Help));
        builder.setPositiveButton(LocaleController.getString("DownloadMobo2", C0338R.string.DownloadMobo2), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("https://cafebazaar.ir/app/com.hanista.mobogram.two/?l=fa"));
                LaunchActivity.this.startActivity(intent);
            }
        });
        builder.setNegativeButton(LocaleController.getString("DownloadMobo3", C0338R.string.DownloadMobo3), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("https://cafebazaar.ir/app/com.hanista.mobogram.three/?l=fa"));
                LaunchActivity.this.startActivity(intent);
            }
        });
        builder.create().show();
    }

    private void showPasscodeActivity() {
        if (this.passcodeView != null) {
            UserConfig.appLocked = true;
            if (PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().closePhoto(false, true);
            }
            this.passcodeView.onShow();
            UserConfig.isWaitingForPasscodeEnter = true;
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            this.passcodeView.setDelegate(new C16097());
        }
    }

    private void startOtherMobos() {
        List<String> m = MoboUtils.m1730m(this);
        if (m.size() != 0) {
            if (m.size() == 1) {
                Intent intent = new Intent();
                intent.setClassName((String) m.get(0), "com.hanista.mobogram.ui.LaunchActivity");
                startActivity(intent);
                finish();
                return;
            }
            Builder builder = new Builder(this);
            List arrayList = new ArrayList();
            for (String d : m) {
                arrayList.add(MoboUtils.m1714d(d));
            }
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new AnonymousClass22(m));
            builder.create().show();
        }
    }

    private void turnOff() {
        if (MoboConstants.aO) {
            MoboUtils.m1723h();
            return;
        }
        Builder builder = new Builder(this);
        builder.setTitle(LocaleController.getString("TurnOff", C0338R.string.TurnOff));
        builder.setMessage(LocaleController.getString("TurnOffAlert", C0338R.string.TurnOffAlert));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MoboUtils.m1723h();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showAlertDialog(builder);
    }

    private void updateCurrentConnectionState() {
        String str = null;
        if (this.currentConnectionState == 2) {
            str = MoboConstants.aO ? LocaleController.getString("TurnOff", C0338R.string.TurnOff) : LocaleController.getString("WaitingForNetwork", C0338R.string.WaitingForNetwork);
        } else if (this.currentConnectionState == 1) {
            str = LocaleController.getString("Connecting", C0338R.string.Connecting);
        } else if (this.currentConnectionState == 4) {
            str = LocaleController.getString("Updating", C0338R.string.Updating);
        }
        this.actionBarLayout.setTitleOverlayText(str);
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.appDidLogout) {
            if (this.drawerLayoutAdapter != null) {
                this.drawerLayoutAdapter.notifyDataSetChanged();
            }
            Iterator it = this.actionBarLayout.fragmentsStack.iterator();
            while (it.hasNext()) {
                ((BaseFragment) it.next()).onFragmentDestroy();
            }
            this.actionBarLayout.fragmentsStack.clear();
            if (AndroidUtilities.isTablet()) {
                it = this.layersActionBarLayout.fragmentsStack.iterator();
                while (it.hasNext()) {
                    ((BaseFragment) it.next()).onFragmentDestroy();
                }
                this.layersActionBarLayout.fragmentsStack.clear();
                it = this.rightActionBarLayout.fragmentsStack.iterator();
                while (it.hasNext()) {
                    ((BaseFragment) it.next()).onFragmentDestroy();
                }
                this.rightActionBarLayout.fragmentsStack.clear();
            }
            startActivity(new Intent(this, IntroActivity.class));
            onFinish();
            finish();
        } else if (i == NotificationCenter.closeOtherAppActivities) {
            if (objArr[0] != this) {
                onFinish();
                finish();
            }
        } else if (i == NotificationCenter.didUpdatedConnectionState) {
            int connectionState = ConnectionsManager.getInstance().getConnectionState();
            if (this.currentConnectionState != connectionState) {
                FileLog.m15d("tmessages", "switch to state " + connectionState);
                this.currentConnectionState = connectionState;
                updateCurrentConnectionState();
            }
        } else if (i == NotificationCenter.mainUserInfoChanged) {
            this.drawerLayoutAdapter.notifyDataSetChanged();
        } else if (i == NotificationCenter.menuSettingsChanged) {
            this.drawerLayoutAdapter.reloadMenuDatas();
            this.drawerLayoutAdapter.notifyDataSetChanged();
        } else if (i == NotificationCenter.needShowAlert) {
            Integer num = (Integer) objArr[0];
            r1 = new Builder(this);
            r1.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            r1.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            if (num.intValue() != 2) {
                r1.setNegativeButton(LocaleController.getString("MoreInfo", C0338R.string.MoreInfo), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            LaunchActivity.this.presentFragment(new ReportHelpActivity());
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                });
            }
            if (num.intValue() == 0) {
                r1.setMessage(LocaleController.getString("NobodyLikesSpam1", C0338R.string.NobodyLikesSpam1));
            } else if (num.intValue() == 1) {
                r1.setMessage(LocaleController.getString("NobodyLikesSpam2", C0338R.string.NobodyLikesSpam2));
            } else if (num.intValue() == 2) {
                r1.setMessage((String) objArr[1]);
            }
            if (!mainFragmentsStack.isEmpty()) {
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(r1.create());
            }
        } else if (i == NotificationCenter.wasUnableToFindCurrentLocation) {
            HashMap hashMap = (HashMap) objArr[0];
            r1 = new Builder(this);
            r1.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
            r1.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
            r1.setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", C0338R.string.ShareYouLocationUnableManually), new AnonymousClass17(hashMap));
            r1.setMessage(LocaleController.getString("ShareYouLocationUnable", C0338R.string.ShareYouLocationUnable));
            if (!mainFragmentsStack.isEmpty()) {
                ((BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(r1.create());
            }
        } else if (i == NotificationCenter.didSetNewWallpapper && this.listView != null) {
            View childAt = this.listView.getChildAt(0);
            if (childAt != null) {
                childAt.invalidate();
            }
        }
    }

    public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
        if (j != 0) {
            int i = (int) j;
            int i2 = (int) (j >> 32);
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (!AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            }
            if (i == 0) {
                bundle.putInt("enc_id", i2);
            } else if (i2 == 1) {
                bundle.putInt("chat_id", i);
            } else if (i > 0) {
                bundle.putInt("user_id", i);
            } else if (i < 0) {
                bundle.putInt("chat_id", -i);
            }
            if (MessagesController.checkCanOpenChat(bundle, dialogsActivity)) {
                BaseFragment chatActivity = new ChatActivity(bundle);
                if (this.videoPath == null) {
                    this.actionBarLayout.presentFragment(chatActivity, dialogsActivity != null, dialogsActivity == null, true);
                    if (this.photoPathsArray != null) {
                        ArrayList arrayList = null;
                        if (this.sendingText != null && this.sendingText.length() <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && this.photoPathsArray.size() == 1) {
                            arrayList = new ArrayList();
                            arrayList.add(this.sendingText);
                            this.sendingText = null;
                        }
                        SendMessagesHelper.prepareSendingPhotos(null, this.photoPathsArray, j, null, arrayList, null);
                    }
                    if (this.sendingText != null) {
                        SendMessagesHelper.prepareSendingText(this.sendingText, j);
                    }
                    if (!(this.documentsPathsArray == null && this.documentsUrisArray == null)) {
                        SendMessagesHelper.prepareSendingDocuments(this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, this.documentsMimeType, j, null);
                    }
                    if (!(this.contactsToSend == null || this.contactsToSend.isEmpty())) {
                        Iterator it = this.contactsToSend.iterator();
                        while (it.hasNext()) {
                            SendMessagesHelper.getInstance().sendMessage((User) it.next(), j, null, null, null);
                        }
                    }
                } else if (VERSION.SDK_INT >= 16) {
                    if (AndroidUtilities.isTablet()) {
                        this.actionBarLayout.presentFragment(chatActivity, false, true, true);
                    } else {
                        this.actionBarLayout.addFragmentToStack(chatActivity, this.actionBarLayout.fragmentsStack.size() - 1);
                    }
                    if (!(chatActivity.openVideoEditor(this.videoPath, dialogsActivity != null, false) || dialogsActivity == null || AndroidUtilities.isTablet())) {
                        dialogsActivity.finishFragment(true);
                    }
                } else {
                    this.actionBarLayout.presentFragment(chatActivity, dialogsActivity != null, dialogsActivity == null, true);
                    SendMessagesHelper.prepareSendingVideo(this.videoPath, 0, 0, 0, 0, null, j, null, null);
                }
                this.photoPathsArray = null;
                this.videoPath = null;
                this.sendingText = null;
                this.documentsPathsArray = null;
                this.documentsOriginalPathsArray = null;
                this.contactsToSend = null;
            }
        }
    }

    public ActionBarLayout getActionBarLayout() {
        return this.actionBarLayout;
    }

    public boolean needAddFragmentToStack(BaseFragment baseFragment, ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z = ((baseFragment instanceof LoginActivity) || (baseFragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z, true);
            if (baseFragment instanceof DialogsActivity) {
                if (((DialogsActivity) baseFragment).isMainDialogList() && actionBarLayout != this.actionBarLayout) {
                    this.actionBarLayout.removeAllFragments();
                    this.actionBarLayout.addFragmentToStack(baseFragment);
                    this.layersActionBarLayout.removeAllFragments();
                    this.layersActionBarLayout.setVisibility(8);
                    this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    if (this.tabletFullSize) {
                        return false;
                    }
                    this.shadowTabletSide.setVisibility(0);
                    if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    this.backgroundTablet.setVisibility(0);
                    return false;
                }
            } else if ((baseFragment instanceof ChatActivity) || (((baseFragment instanceof DialogsActivity) && this.layersActionBarLayout.fragmentsStack.isEmpty()) || (baseFragment instanceof CategoryActivity) || (baseFragment instanceof CategorySettingsActivity) || (baseFragment instanceof DownloadManagerActivity) || (baseFragment instanceof AllMediaActivity))) {
                if (!this.tabletFullSize && actionBarLayout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.addFragmentToStack(baseFragment);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                    return false;
                } else if (this.tabletFullSize && actionBarLayout != this.actionBarLayout) {
                    this.actionBarLayout.addFragmentToStack(baseFragment);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                    }
                    this.layersActionBarLayout.closeLastFragment(true);
                    return false;
                }
            } else if (actionBarLayout != this.layersActionBarLayout) {
                this.layersActionBarLayout.setVisibility(0);
                this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (baseFragment instanceof LoginActivity) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                }
                this.layersActionBarLayout.addFragmentToStack(baseFragment);
                return false;
            }
            return true;
        }
        drawerLayoutContainer = this.drawerLayoutContainer;
        z = ((baseFragment instanceof LoginActivity) || (baseFragment instanceof CountrySelectActivity)) ? false : true;
        drawerLayoutContainer.setAllowOpenDrawer(z, false);
        return true;
    }

    public boolean needCloseLastFragment(ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet()) {
            if (actionBarLayout == this.actionBarLayout && actionBarLayout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (actionBarLayout == this.rightActionBarLayout) {
                if (!this.tabletFullSize) {
                    this.backgroundTablet.setVisibility(0);
                }
            } else if (actionBarLayout == this.layersActionBarLayout && this.actionBarLayout.fragmentsStack.isEmpty() && this.layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (actionBarLayout.fragmentsStack.size() <= 1) {
            onFinish();
            finish();
            return false;
        }
        return true;
    }

    public boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, ActionBarLayout actionBarLayout) {
        boolean z3 = true;
        if (AndroidUtilities.isTablet()) {
            DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
            boolean z4 = ((baseFragment instanceof LoginActivity) || (baseFragment instanceof CountrySelectActivity) || this.layersActionBarLayout.getVisibility() == 0) ? false : true;
            drawerLayoutContainer.setAllowOpenDrawer(z4, true);
            if ((baseFragment instanceof DialogsActivity) && ((DialogsActivity) baseFragment).isMainDialogList() && actionBarLayout != this.actionBarLayout) {
                this.actionBarLayout.removeAllFragments();
                this.actionBarLayout.presentFragment(baseFragment, z, z2, false);
                this.layersActionBarLayout.removeAllFragments();
                this.layersActionBarLayout.setVisibility(8);
                this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                if (this.tabletFullSize) {
                    return false;
                }
                this.shadowTabletSide.setVisibility(0);
                if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    return false;
                }
                this.backgroundTablet.setVisibility(0);
                return false;
            } else if ((baseFragment instanceof ChatActivity) || (((baseFragment instanceof DialogsActivity) && this.layersActionBarLayout.fragmentsStack.isEmpty()) || (baseFragment instanceof CategoryActivity) || (baseFragment instanceof CategorySettingsActivity) || (baseFragment instanceof DownloadManagerActivity) || (baseFragment instanceof AllMediaActivity))) {
                ActionBarLayout actionBarLayout2;
                if ((!this.tabletFullSize && actionBarLayout == this.rightActionBarLayout) || (this.tabletFullSize && actionBarLayout == this.actionBarLayout)) {
                    boolean z5 = (this.tabletFullSize && actionBarLayout == this.actionBarLayout && this.actionBarLayout.fragmentsStack.size() == 1) ? false : true;
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        actionBarLayout2 = this.layersActionBarLayout;
                        if (z2) {
                            z3 = false;
                        }
                        actionBarLayout2.closeLastFragment(z3);
                    }
                    if (!z5) {
                        this.actionBarLayout.presentFragment(baseFragment, false, z2, false);
                    }
                    return z5;
                } else if (!this.tabletFullSize && actionBarLayout != this.rightActionBarLayout) {
                    this.rightActionBarLayout.setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.presentFragment(baseFragment, z, true, false);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                    }
                    actionBarLayout2 = this.layersActionBarLayout;
                    if (z2) {
                        z3 = false;
                    }
                    actionBarLayout2.closeLastFragment(z3);
                    return false;
                } else if (!this.tabletFullSize || actionBarLayout == this.actionBarLayout) {
                    if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                        }
                        this.layersActionBarLayout.closeLastFragment(!z2);
                    }
                    actionBarLayout2 = this.actionBarLayout;
                    if (this.actionBarLayout.fragmentsStack.size() <= 1) {
                        z3 = false;
                    }
                    actionBarLayout2.presentFragment(baseFragment, z3, z2, false);
                    return false;
                } else {
                    this.actionBarLayout.presentFragment(baseFragment, this.actionBarLayout.fragmentsStack.size() > 1, z2, false);
                    if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                        return false;
                    }
                    while (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0) {
                        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(0));
                    }
                    actionBarLayout2 = this.layersActionBarLayout;
                    if (z2) {
                        z3 = false;
                    }
                    actionBarLayout2.closeLastFragment(z3);
                    return false;
                }
            } else if (actionBarLayout == this.layersActionBarLayout) {
                return true;
            } else {
                this.layersActionBarLayout.setVisibility(0);
                this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
                if (baseFragment instanceof LoginActivity) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
                }
                this.layersActionBarLayout.presentFragment(baseFragment, z, z2, false);
                return false;
            }
        }
        drawerLayoutContainer = this.drawerLayoutContainer;
        z4 = ((baseFragment instanceof LoginActivity) || (baseFragment instanceof CountrySelectActivity)) ? false : true;
        drawerLayoutContainer.setAllowOpenDrawer(z4, false);
        return true;
    }

    public void onActionModeFinished(ActionMode actionMode) {
        super.onActionModeFinished(actionMode);
        if (VERSION.SDK_INT < 23 || actionMode.getType() != 1) {
            this.actionBarLayout.onActionModeFinished(actionMode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeFinished(actionMode);
                this.layersActionBarLayout.onActionModeFinished(actionMode);
            }
        }
    }

    public void onActionModeStarted(ActionMode actionMode) {
        super.onActionModeStarted(actionMode);
        if (VERSION.SDK_INT < 23 || actionMode.getType() != 1) {
            this.actionBarLayout.onActionModeStarted(actionMode);
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onActionModeStarted(actionMode);
                this.layersActionBarLayout.onActionModeStarted(actionMode);
            }
        }
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        if (!(UserConfig.passcodeHash.length() == 0 || UserConfig.lastPauseTime == 0)) {
            UserConfig.lastPauseTime = 0;
            UserConfig.saveConfig(false);
        }
        super.onActivityResult(i, i2, intent);
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(i, i2, intent);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(i, i2, intent);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(i, i2, intent);
            }
        }
    }

    public void onBackPressed() {
        boolean z = false;
        if (this.passcodeView == null) {
            finish();
        } else if (this.passcodeView.getVisibility() == 0) {
            finish();
        } else if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else if (this.drawerLayoutContainer.isDrawerOpened()) {
            this.drawerLayoutContainer.closeDrawer(false);
        } else if (!AndroidUtilities.isTablet()) {
            this.actionBarLayout.onBackPressed();
        } else if (this.layersActionBarLayout.getVisibility() == 0) {
            this.layersActionBarLayout.onBackPressed();
        } else {
            if (this.rightActionBarLayout.getVisibility() == 0 && !this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                z = !((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() + -1)).onBackPressed();
            }
            if (!z) {
                this.actionBarLayout.onBackPressed();
            }
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        AndroidUtilities.checkDisplaySize(this, configuration);
        super.onConfigurationChanged(configuration);
        checkLayout();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onCreate(android.os.Bundle r10) {
        /*
        r9 = this;
        r1 = 8;
        r8 = 1134559232; // 0x43a00000 float:320.0 double:5.605467397E-315;
        r4 = -1;
        r3 = 1;
        r2 = 0;
        com.hanista.mobogram.messenger.ApplicationLoader.postInitApplication();
        com.hanista.mobogram.messenger.NativeCrashManager.handleDumpFiles(r9);
        r0 = r9.getResources();
        r0 = r0.getConfiguration();
        com.hanista.mobogram.messenger.AndroidUtilities.checkDisplaySize(r9, r0);
        r0 = r9.isMainMoboAvailable();
        if (r0 != 0) goto L_0x0022;
    L_0x001e:
        super.onCreate(r10);
    L_0x0021:
        return;
    L_0x0022:
        r0 = com.hanista.mobogram.messenger.UserConfig.isClientActivated();
        if (r0 != 0) goto L_0x0084;
    L_0x0028:
        r0 = r9.getIntent();
        if (r0 == 0) goto L_0x0055;
    L_0x002e:
        r5 = r0.getAction();
        if (r5 == 0) goto L_0x0055;
    L_0x0034:
        r5 = "android.intent.action.SEND";
        r6 = r0.getAction();
        r5 = r5.equals(r6);
        if (r5 != 0) goto L_0x004e;
    L_0x0041:
        r5 = r0.getAction();
        r6 = "android.intent.action.SEND_MULTIPLE";
        r5 = r5.equals(r6);
        if (r5 == 0) goto L_0x0055;
    L_0x004e:
        super.onCreate(r10);
        r9.finish();
        goto L_0x0021;
    L_0x0055:
        if (r0 == 0) goto L_0x0084;
    L_0x0057:
        r5 = "fromIntro";
        r0 = r0.getBooleanExtra(r5, r2);
        if (r0 != 0) goto L_0x0084;
    L_0x0060:
        r0 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;
        r5 = "logininfo2";
        r0 = r0.getSharedPreferences(r5, r2);
        r0 = r0.getAll();
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x0084;
    L_0x0073:
        r0 = new android.content.Intent;
        r1 = com.hanista.mobogram.ui.IntroActivity.class;
        r0.<init>(r9, r1);
        r9.startActivity(r0);
        super.onCreate(r10);
        r9.finish();
        goto L_0x0021;
    L_0x0084:
        r9.requestWindowFeature(r3);
        r0 = 2131361795; // 0x7f0a0003 float:1.8343352E38 double:1.053032642E-314;
        r9.setTheme(r0);
        r0 = r9.getWindow();
        r5 = 2130838451; // 0x7f0203b3 float:1.7281885E38 double:1.0527740755E-314;
        r0.setBackgroundDrawableResource(r5);
        super.onCreate(r10);
        r0 = android.os.Build.VERSION.SDK_INT;
        r5 = 24;
        if (r0 < r5) goto L_0x00a6;
    L_0x00a0:
        r0 = r9.isInMultiWindowMode();
        com.hanista.mobogram.messenger.AndroidUtilities.isInMultiwindow = r0;
    L_0x00a6:
        com.hanista.mobogram.ui.ActionBar.Theme.loadRecources(r9);
        r0 = com.hanista.mobogram.messenger.UserConfig.passcodeHash;
        r0 = r0.length();
        if (r0 == 0) goto L_0x00bf;
    L_0x00b1:
        r0 = com.hanista.mobogram.messenger.UserConfig.appLocked;
        if (r0 == 0) goto L_0x00bf;
    L_0x00b5:
        r0 = com.hanista.mobogram.tgnet.ConnectionsManager.getInstance();
        r0 = r0.getCurrentTime();
        com.hanista.mobogram.messenger.UserConfig.lastPauseTime = r0;
    L_0x00bf:
        r0 = r9.getResources();
        r5 = "status_bar_height";
        r6 = "dimen";
        r7 = "android";
        r0 = r0.getIdentifier(r5, r6, r7);
        if (r0 <= 0) goto L_0x00dc;
    L_0x00d2:
        r5 = r9.getResources();
        r0 = r5.getDimensionPixelSize(r0);
        com.hanista.mobogram.messenger.AndroidUtilities.statusBarHeight = r0;
    L_0x00dc:
        r0 = new com.hanista.mobogram.ui.ActionBar.ActionBarLayout;
        r0.<init>(r9);
        r9.actionBarLayout = r0;
        r0 = new com.hanista.mobogram.ui.ActionBar.DrawerLayoutContainer;
        r0.<init>(r9);
        r9.drawerLayoutContainer = r0;
        r0 = r9.drawerLayoutContainer;
        r5 = new android.view.ViewGroup$LayoutParams;
        r5.<init>(r4, r4);
        r9.setContentView(r0, r5);
        r0 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x038b;
    L_0x00fa:
        r0 = r9.getWindow();
        r5 = 16;
        r0.setSoftInputMode(r5);
        r5 = new com.hanista.mobogram.ui.LaunchActivity$1;
        r5.<init>(r9);
        r0 = r9.drawerLayoutContainer;
        r6 = -1082130432; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r6 = com.hanista.mobogram.ui.Components.LayoutHelper.createFrame(r4, r6);
        r0.addView(r5, r6);
        r0 = new android.view.View;
        r0.<init>(r9);
        r9.backgroundTablet = r0;
        r0 = r9.getResources();
        r6 = 2130837620; // 0x7f020074 float:1.72802E38 double:1.052773665E-314;
        r0 = r0.getDrawable(r6);
        r0 = (android.graphics.drawable.BitmapDrawable) r0;
        r6 = android.graphics.Shader.TileMode.REPEAT;
        r7 = android.graphics.Shader.TileMode.REPEAT;
        r0.setTileModeXY(r6, r7);
        r6 = r9.backgroundTablet;
        r6.setBackgroundDrawable(r0);
        r0 = r9.backgroundTablet;
        r6 = com.hanista.mobogram.ui.Components.LayoutHelper.createRelative(r4, r4);
        r5.addView(r0, r6);
        r0 = r9.actionBarLayout;
        r5.addView(r0);
        r0 = new com.hanista.mobogram.ui.ActionBar.ActionBarLayout;
        r0.<init>(r9);
        r9.rightActionBarLayout = r0;
        r0 = r9.rightActionBarLayout;
        r6 = rightFragmentsStack;
        r0.init(r6);
        r0 = r9.rightActionBarLayout;
        r0.setDelegate(r9);
        r0 = r9.rightActionBarLayout;
        r5.addView(r0);
        r0 = new android.widget.FrameLayout;
        r0.<init>(r9);
        r9.shadowTabletSide = r0;
        r0 = r9.shadowTabletSide;
        r6 = 1076449908; // 0x40295274 float:2.6456575 double:5.31836919E-315;
        r0.setBackgroundColor(r6);
        r0 = r9.shadowTabletSide;
        r5.addView(r0);
        r0 = new android.widget.FrameLayout;
        r0.<init>(r9);
        r9.shadowTablet = r0;
        r6 = r9.shadowTablet;
        r0 = layerFragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x0385;
    L_0x017e:
        r0 = r1;
    L_0x017f:
        r6.setVisibility(r0);
        r0 = r9.shadowTablet;
        r6 = 2130706432; // 0x7f000000 float:1.7014118E38 double:1.0527088494E-314;
        r0.setBackgroundColor(r6);
        r0 = r9.shadowTablet;
        r5.addView(r0);
        r0 = r9.shadowTablet;
        r6 = new com.hanista.mobogram.ui.LaunchActivity$2;
        r6.<init>();
        r0.setOnTouchListener(r6);
        r0 = r9.shadowTablet;
        r6 = new com.hanista.mobogram.ui.LaunchActivity$3;
        r6.<init>();
        r0.setOnClickListener(r6);
        r0 = new com.hanista.mobogram.ui.ActionBar.ActionBarLayout;
        r0.<init>(r9);
        r9.layersActionBarLayout = r0;
        r0 = r9.layersActionBarLayout;
        r0.setRemoveActionBarExtraHeight(r3);
        r0 = r9.layersActionBarLayout;
        r6 = r9.shadowTablet;
        r0.setBackgroundView(r6);
        r0 = r9.layersActionBarLayout;
        r0.setUseAlphaAnimations(r3);
        r0 = r9.layersActionBarLayout;
        r6 = 2130837607; // 0x7f020067 float:1.7280173E38 double:1.0527736585E-314;
        r0.setBackgroundResource(r6);
        r0 = r9.layersActionBarLayout;
        r6 = layerFragmentsStack;
        r0.init(r6);
        r0 = r9.layersActionBarLayout;
        r0.setDelegate(r9);
        r0 = r9.layersActionBarLayout;
        r6 = r9.drawerLayoutContainer;
        r0.setDrawerLayoutContainer(r6);
        r0 = r9.layersActionBarLayout;
        r6 = layerFragmentsStack;
        r6 = r6.isEmpty();
        if (r6 == 0) goto L_0x0388;
    L_0x01df:
        r0.setVisibility(r1);
        r0 = r9.layersActionBarLayout;
        r5.addView(r0);
    L_0x01e7:
        r0 = new com.hanista.mobogram.ui.LaunchActivity$4;
        r0.<init>(r9);
        r9.listView = r0;
        r9.initThemeListView();
        r0 = r9.listView;
        r0.setBackgroundColor(r4);
        r0 = r9.listView;
        r1 = new com.hanista.mobogram.ui.Adapters.DrawerLayoutAdapter;
        r1.<init>(r9);
        r9.drawerLayoutAdapter = r1;
        r0.setAdapter(r1);
        r0 = r9.listView;
        r0.setChoiceMode(r3);
        r0 = r9.listView;
        r1 = 0;
        r0.setDivider(r1);
        r0 = r9.listView;
        r0.setDividerHeight(r2);
        r0 = r9.listView;
        r0.setVerticalScrollBarEnabled(r2);
        r0 = r9.drawerLayoutContainer;
        r1 = r9.listView;
        r0.setDrawerLayout(r1);
        r0 = r9.listView;
        r0 = r0.getLayoutParams();
        r0 = (android.widget.FrameLayout.LayoutParams) r0;
        r1 = com.hanista.mobogram.messenger.AndroidUtilities.getRealScreenSize();
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r5 == 0) goto L_0x0399;
    L_0x0230:
        r1 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
    L_0x0234:
        r0.width = r1;
        r0.height = r4;
        r1 = r9.listView;
        r1.setLayoutParams(r0);
        r0 = r9.listView;
        r1 = new com.hanista.mobogram.ui.LaunchActivity$5;
        r1.<init>();
        r0.setOnItemClickListener(r1);
        r0 = r9.drawerLayoutContainer;
        r1 = r9.actionBarLayout;
        r0.setParentActionBarLayout(r1);
        r0 = r9.actionBarLayout;
        r1 = r9.drawerLayoutContainer;
        r0.setDrawerLayoutContainer(r1);
        r0 = r9.actionBarLayout;
        r1 = mainFragmentsStack;
        r0.init(r1);
        r0 = r9.actionBarLayout;
        r0.setDelegate(r9);
        com.hanista.mobogram.messenger.ApplicationLoader.loadWallpaper();
        r0 = new com.hanista.mobogram.ui.Components.PasscodeView;
        r0.<init>(r9);
        r9.passcodeView = r0;
        r0 = r9.drawerLayoutContainer;
        r1 = r9.passcodeView;
        r0.addView(r1);
        r0 = r9.passcodeView;
        r0 = r0.getLayoutParams();
        r0 = (android.widget.FrameLayout.LayoutParams) r0;
        r0.width = r4;
        r0.height = r4;
        r1 = r9.passcodeView;
        r1.setLayoutParams(r0);
        r0 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r1 = com.hanista.mobogram.messenger.NotificationCenter.closeOtherAppActivities;
        r5 = new java.lang.Object[r3];
        r5[r2] = r9;
        r0.postNotificationName(r1, r5);
        r0 = com.hanista.mobogram.tgnet.ConnectionsManager.getInstance();
        r0 = r0.getConnectionState();
        r9.currentConnectionState = r0;
        r0 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r1 = com.hanista.mobogram.messenger.NotificationCenter.appDidLogout;
        r0.addObserver(r9, r1);
        r0 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r1 = com.hanista.mobogram.messenger.NotificationCenter.mainUserInfoChanged;
        r0.addObserver(r9, r1);
        r0 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r1 = com.hanista.mobogram.messenger.NotificationCenter.closeOtherAppActivities;
        r0.addObserver(r9, r1);
        r0 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r1 = com.hanista.mobogram.messenger.NotificationCenter.didUpdatedConnectionState;
        r0.addObserver(r9, r1);
        r0 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r1 = com.hanista.mobogram.messenger.NotificationCenter.needShowAlert;
        r0.addObserver(r9, r1);
        r0 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r1 = com.hanista.mobogram.messenger.NotificationCenter.wasUnableToFindCurrentLocation;
        r0.addObserver(r9, r1);
        r0 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r1 = com.hanista.mobogram.messenger.NotificationCenter.didSetNewWallpapper;
        r0.addObserver(r9, r1);
        r0 = com.hanista.mobogram.messenger.NotificationCenter.getInstance();
        r1 = com.hanista.mobogram.messenger.NotificationCenter.menuSettingsChanged;
        r0.addObserver(r9, r1);
        r0 = r9.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x04ca;
    L_0x02ec:
        r0 = com.hanista.mobogram.messenger.UserConfig.isClientActivated();
        if (r0 != 0) goto L_0x03b2;
    L_0x02f2:
        r0 = r9.actionBarLayout;
        r1 = new com.hanista.mobogram.ui.LoginActivity;
        r1.<init>();
        r0.addFragmentToStack(r1);
        r0 = r9.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r2, r2);
    L_0x0301:
        if (r10 == 0) goto L_0x031e;
    L_0x0303:
        r0 = "fragment";
        r0 = r10.getString(r0);	 Catch:{ Exception -> 0x0444 }
        if (r0 == 0) goto L_0x031e;
    L_0x030c:
        r1 = "args";
        r1 = r10.getBundle(r1);	 Catch:{ Exception -> 0x0444 }
        r5 = r0.hashCode();	 Catch:{ Exception -> 0x0444 }
        switch(r5) {
            case -1529105743: goto L_0x0424;
            case -1349522494: goto L_0x0418;
            case 3052376: goto L_0x03d0;
            case 3108362: goto L_0x040c;
            case 98629247: goto L_0x03f4;
            case 706535667: goto L_0x03e8;
            case 738950403: goto L_0x0400;
            case 1434631203: goto L_0x03dc;
            default: goto L_0x031a;
        };
    L_0x031a:
        r0 = r4;
    L_0x031b:
        switch(r0) {
            case 0: goto L_0x0430;
            case 1: goto L_0x044d;
            case 2: goto L_0x045c;
            case 3: goto L_0x046b;
            case 4: goto L_0x047f;
            case 5: goto L_0x0493;
            case 6: goto L_0x04a7;
            case 7: goto L_0x04bb;
            default: goto L_0x031e;
        };
    L_0x031e:
        r9.checkLayout();
        r0 = r9.getIntent();
        if (r10 == 0) goto L_0x051c;
    L_0x0327:
        r9.handleIntent(r0, r2, r3, r2);
        r0 = android.os.Build.DISPLAY;	 Catch:{ Exception -> 0x052a }
        r2 = android.os.Build.USER;	 Catch:{ Exception -> 0x052a }
        if (r0 == 0) goto L_0x051f;
    L_0x0330:
        r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x052a }
        r1 = r0;
    L_0x0335:
        if (r2 == 0) goto L_0x0525;
    L_0x0337:
        r0 = r1.toLowerCase();	 Catch:{ Exception -> 0x052a }
    L_0x033b:
        r2 = "flyme";
        r1 = r1.contains(r2);	 Catch:{ Exception -> 0x052a }
        if (r1 != 0) goto L_0x034d;
    L_0x0344:
        r1 = "flyme";
        r0 = r0.contains(r1);	 Catch:{ Exception -> 0x052a }
        if (r0 == 0) goto L_0x036a;
    L_0x034d:
        r0 = 1;
        com.hanista.mobogram.messenger.AndroidUtilities.incorrectDisplaySizeFix = r0;	 Catch:{ Exception -> 0x052a }
        r0 = r9.getWindow();	 Catch:{ Exception -> 0x052a }
        r0 = r0.getDecorView();	 Catch:{ Exception -> 0x052a }
        r0 = r0.getRootView();	 Catch:{ Exception -> 0x052a }
        r1 = r0.getViewTreeObserver();	 Catch:{ Exception -> 0x052a }
        r2 = new com.hanista.mobogram.ui.LaunchActivity$6;	 Catch:{ Exception -> 0x052a }
        r2.<init>(r0);	 Catch:{ Exception -> 0x052a }
        r9.onGlobalLayoutListener = r2;	 Catch:{ Exception -> 0x052a }
        r1.addOnGlobalLayoutListener(r2);	 Catch:{ Exception -> 0x052a }
    L_0x036a:
        r0 = com.hanista.mobogram.ui.PhotoViewer.getInstance();
        r0.setParentActivity(r9);
        r0 = new com.hanista.mobogram.mobo.notificationservice.NotificationAlarmBroadcastReceiver;
        r0.<init>();
        r0.m1950a(r9);
        r0 = new android.content.Intent;
        r1 = com.hanista.mobogram.mobo.alarmservice.AlarmService.class;
        r0.<init>(r9, r1);
        r9.startService(r0);
        goto L_0x0021;
    L_0x0385:
        r0 = r2;
        goto L_0x017f;
    L_0x0388:
        r1 = r2;
        goto L_0x01df;
    L_0x038b:
        r0 = r9.drawerLayoutContainer;
        r1 = r9.actionBarLayout;
        r5 = new android.view.ViewGroup$LayoutParams;
        r5.<init>(r4, r4);
        r0.addView(r1, r5);
        goto L_0x01e7;
    L_0x0399:
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r8);
        r6 = r1.x;
        r1 = r1.y;
        r1 = java.lang.Math.min(r6, r1);
        r6 = 1113587712; // 0x42600000 float:56.0 double:5.50185432E-315;
        r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r6);
        r1 = r1 - r6;
        r1 = java.lang.Math.min(r5, r1);
        goto L_0x0234;
    L_0x03b2:
        r1 = r9.actionBarLayout;
        r0 = com.hanista.mobogram.mobo.p001b.CategoryUtil.m352c();
        if (r0 == 0) goto L_0x03c9;
    L_0x03ba:
        r0 = new com.hanista.mobogram.mobo.b.b;
        r0.<init>();
    L_0x03bf:
        r1.addFragmentToStack(r0);
        r0 = r9.drawerLayoutContainer;
        r0.setAllowOpenDrawer(r3, r2);
        goto L_0x0301;
    L_0x03c9:
        r0 = new com.hanista.mobogram.ui.DialogsActivity;
        r5 = 0;
        r0.<init>(r5);
        goto L_0x03bf;
    L_0x03d0:
        r5 = "chat";
        r0 = r0.equals(r5);	 Catch:{ Exception -> 0x0444 }
        if (r0 == 0) goto L_0x031a;
    L_0x03d9:
        r0 = r2;
        goto L_0x031b;
    L_0x03dc:
        r5 = "settings";
        r0 = r0.equals(r5);	 Catch:{ Exception -> 0x0444 }
        if (r0 == 0) goto L_0x031a;
    L_0x03e5:
        r0 = r3;
        goto L_0x031b;
    L_0x03e8:
        r5 = "mobo_settings";
        r0 = r0.equals(r5);	 Catch:{ Exception -> 0x0444 }
        if (r0 == 0) goto L_0x031a;
    L_0x03f1:
        r0 = 2;
        goto L_0x031b;
    L_0x03f4:
        r5 = "group";
        r0 = r0.equals(r5);	 Catch:{ Exception -> 0x0444 }
        if (r0 == 0) goto L_0x031a;
    L_0x03fd:
        r0 = 3;
        goto L_0x031b;
    L_0x0400:
        r5 = "channel";
        r0 = r0.equals(r5);	 Catch:{ Exception -> 0x0444 }
        if (r0 == 0) goto L_0x031a;
    L_0x0409:
        r0 = 4;
        goto L_0x031b;
    L_0x040c:
        r5 = "edit";
        r0 = r0.equals(r5);	 Catch:{ Exception -> 0x0444 }
        if (r0 == 0) goto L_0x031a;
    L_0x0415:
        r0 = 5;
        goto L_0x031b;
    L_0x0418:
        r5 = "chat_profile";
        r0 = r0.equals(r5);	 Catch:{ Exception -> 0x0444 }
        if (r0 == 0) goto L_0x031a;
    L_0x0421:
        r0 = 6;
        goto L_0x031b;
    L_0x0424:
        r5 = "wallpapers";
        r0 = r0.equals(r5);	 Catch:{ Exception -> 0x0444 }
        if (r0 == 0) goto L_0x031a;
    L_0x042d:
        r0 = 7;
        goto L_0x031b;
    L_0x0430:
        if (r1 == 0) goto L_0x031e;
    L_0x0432:
        r0 = new com.hanista.mobogram.ui.ChatActivity;	 Catch:{ Exception -> 0x0444 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0444 }
        r1 = r9.actionBarLayout;	 Catch:{ Exception -> 0x0444 }
        r1 = r1.addFragmentToStack(r0);	 Catch:{ Exception -> 0x0444 }
        if (r1 == 0) goto L_0x031e;
    L_0x043f:
        r0.restoreSelfArgs(r10);	 Catch:{ Exception -> 0x0444 }
        goto L_0x031e;
    L_0x0444:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x031e;
    L_0x044d:
        r0 = new com.hanista.mobogram.ui.SettingsActivity;	 Catch:{ Exception -> 0x0444 }
        r0.<init>();	 Catch:{ Exception -> 0x0444 }
        r1 = r9.actionBarLayout;	 Catch:{ Exception -> 0x0444 }
        r1.addFragmentToStack(r0);	 Catch:{ Exception -> 0x0444 }
        r0.restoreSelfArgs(r10);	 Catch:{ Exception -> 0x0444 }
        goto L_0x031e;
    L_0x045c:
        r0 = new com.hanista.mobogram.mobo.l;	 Catch:{ Exception -> 0x0444 }
        r0.<init>();	 Catch:{ Exception -> 0x0444 }
        r1 = r9.actionBarLayout;	 Catch:{ Exception -> 0x0444 }
        r1.addFragmentToStack(r0);	 Catch:{ Exception -> 0x0444 }
        r0.restoreSelfArgs(r10);	 Catch:{ Exception -> 0x0444 }
        goto L_0x031e;
    L_0x046b:
        if (r1 == 0) goto L_0x031e;
    L_0x046d:
        r0 = new com.hanista.mobogram.ui.GroupCreateFinalActivity;	 Catch:{ Exception -> 0x0444 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0444 }
        r1 = r9.actionBarLayout;	 Catch:{ Exception -> 0x0444 }
        r1 = r1.addFragmentToStack(r0);	 Catch:{ Exception -> 0x0444 }
        if (r1 == 0) goto L_0x031e;
    L_0x047a:
        r0.restoreSelfArgs(r10);	 Catch:{ Exception -> 0x0444 }
        goto L_0x031e;
    L_0x047f:
        if (r1 == 0) goto L_0x031e;
    L_0x0481:
        r0 = new com.hanista.mobogram.ui.ChannelCreateActivity;	 Catch:{ Exception -> 0x0444 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0444 }
        r1 = r9.actionBarLayout;	 Catch:{ Exception -> 0x0444 }
        r1 = r1.addFragmentToStack(r0);	 Catch:{ Exception -> 0x0444 }
        if (r1 == 0) goto L_0x031e;
    L_0x048e:
        r0.restoreSelfArgs(r10);	 Catch:{ Exception -> 0x0444 }
        goto L_0x031e;
    L_0x0493:
        if (r1 == 0) goto L_0x031e;
    L_0x0495:
        r0 = new com.hanista.mobogram.ui.ChannelEditActivity;	 Catch:{ Exception -> 0x0444 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0444 }
        r1 = r9.actionBarLayout;	 Catch:{ Exception -> 0x0444 }
        r1 = r1.addFragmentToStack(r0);	 Catch:{ Exception -> 0x0444 }
        if (r1 == 0) goto L_0x031e;
    L_0x04a2:
        r0.restoreSelfArgs(r10);	 Catch:{ Exception -> 0x0444 }
        goto L_0x031e;
    L_0x04a7:
        if (r1 == 0) goto L_0x031e;
    L_0x04a9:
        r0 = new com.hanista.mobogram.ui.ProfileActivity;	 Catch:{ Exception -> 0x0444 }
        r0.<init>(r1);	 Catch:{ Exception -> 0x0444 }
        r1 = r9.actionBarLayout;	 Catch:{ Exception -> 0x0444 }
        r1 = r1.addFragmentToStack(r0);	 Catch:{ Exception -> 0x0444 }
        if (r1 == 0) goto L_0x031e;
    L_0x04b6:
        r0.restoreSelfArgs(r10);	 Catch:{ Exception -> 0x0444 }
        goto L_0x031e;
    L_0x04bb:
        r0 = new com.hanista.mobogram.ui.WallpapersActivity;	 Catch:{ Exception -> 0x0444 }
        r0.<init>();	 Catch:{ Exception -> 0x0444 }
        r1 = r9.actionBarLayout;	 Catch:{ Exception -> 0x0444 }
        r1.addFragmentToStack(r0);	 Catch:{ Exception -> 0x0444 }
        r0.restoreSelfArgs(r10);	 Catch:{ Exception -> 0x0444 }
        goto L_0x031e;
    L_0x04ca:
        r0 = com.hanista.mobogram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x0533;
    L_0x04d0:
        r0 = r9.actionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.size();
        if (r0 > r3) goto L_0x051a;
    L_0x04da:
        r0 = r9.layersActionBarLayout;
        r0 = r0.fragmentsStack;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x051a;
    L_0x04e4:
        r0 = r3;
    L_0x04e5:
        r1 = r9.layersActionBarLayout;
        r1 = r1.fragmentsStack;
        r1 = r1.size();
        if (r1 != r3) goto L_0x04fc;
    L_0x04ef:
        r1 = r9.layersActionBarLayout;
        r1 = r1.fragmentsStack;
        r1 = r1.get(r2);
        r1 = r1 instanceof com.hanista.mobogram.ui.LoginActivity;
        if (r1 == 0) goto L_0x04fc;
    L_0x04fb:
        r0 = r2;
    L_0x04fc:
        r1 = r9.actionBarLayout;
        r1 = r1.fragmentsStack;
        r1 = r1.size();
        if (r1 != r3) goto L_0x0513;
    L_0x0506:
        r1 = r9.actionBarLayout;
        r1 = r1.fragmentsStack;
        r1 = r1.get(r2);
        r1 = r1 instanceof com.hanista.mobogram.ui.LoginActivity;
        if (r1 == 0) goto L_0x0513;
    L_0x0512:
        r0 = r2;
    L_0x0513:
        r1 = r9.drawerLayoutContainer;
        r1.setAllowOpenDrawer(r0, r2);
        goto L_0x031e;
    L_0x051a:
        r0 = r2;
        goto L_0x04e5;
    L_0x051c:
        r3 = r2;
        goto L_0x0327;
    L_0x051f:
        r0 = "";
        r1 = r0;
        goto L_0x0335;
    L_0x0525:
        r0 = "";
        goto L_0x033b;
    L_0x052a:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x036a;
    L_0x0533:
        r0 = r3;
        goto L_0x04fc;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.LaunchActivity.onCreate(android.os.Bundle):void");
    }

    protected void onDestroy() {
        PhotoViewer.getInstance().destroyPhotoViewer();
        SecretPhotoViewer.getInstance().destroyPhotoViewer();
        StickerPreviewViewer.getInstance().destroy();
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        try {
            if (this.onGlobalLayoutListener != null) {
                View rootView = getWindow().getDecorView().getRootView();
                if (VERSION.SDK_INT < 16) {
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this.onGlobalLayoutListener);
                } else {
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
                }
            }
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
        super.onDestroy();
        onFinish();
    }

    public boolean onKeyUp(int i, @NonNull KeyEvent keyEvent) {
        if (i == 82 && !UserConfig.isWaitingForPasscodeEnter) {
            if (PhotoViewer.getInstance().isVisible()) {
                return super.onKeyUp(i, keyEvent);
            }
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getVisibility() == 0 && !this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    this.layersActionBarLayout.onKeyUp(i, keyEvent);
                } else if (this.rightActionBarLayout.getVisibility() != 0 || this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                    this.actionBarLayout.onKeyUp(i, keyEvent);
                } else {
                    this.rightActionBarLayout.onKeyUp(i, keyEvent);
                }
            } else if (this.actionBarLayout.fragmentsStack.size() != 1) {
                this.actionBarLayout.onKeyUp(i, keyEvent);
            } else if (this.drawerLayoutContainer.isDrawerOpened()) {
                this.drawerLayoutContainer.closeDrawer(false);
            } else {
                if (getCurrentFocus() != null) {
                    AndroidUtilities.hideKeyboard(getCurrentFocus());
                }
                this.drawerLayoutContainer.openDrawer(false);
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.actionBarLayout.onLowMemory();
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onLowMemory();
            this.layersActionBarLayout.onLowMemory();
        }
    }

    public void onMultiWindowModeChanged(boolean z) {
        AndroidUtilities.isInMultiwindow = z;
        checkLayout();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, true, false, false);
    }

    protected void onPause() {
        super.onPause();
        if (this.actionBarLayout != null) {
            ApplicationLoader.mainInterfacePaused = true;
            onPasscodePause();
            this.actionBarLayout.onPause();
            if (AndroidUtilities.isTablet()) {
                this.rightActionBarLayout.onPause();
                this.layersActionBarLayout.onPause();
            }
            if (this.passcodeView != null) {
                this.passcodeView.onPause();
            }
            ConnectionsManager.getInstance().setAppPaused(true, false);
            AndroidUtilities.unregisterUpdates();
            if (PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().onPause();
            }
        }
    }

    public boolean onPreIme() {
        if (!PhotoViewer.getInstance().isVisible()) {
            return false;
        }
        PhotoViewer.getInstance().closePhoto(true, false);
        return true;
    }

    public void onRebuildAllFragments(ActionBarLayout actionBarLayout) {
        if (AndroidUtilities.isTablet() && actionBarLayout == this.layersActionBarLayout) {
            this.rightActionBarLayout.rebuildAllFragmentViews(true);
            this.rightActionBarLayout.showLastFragment();
            this.actionBarLayout.rebuildAllFragmentViews(true);
            this.actionBarLayout.showLastFragment();
        }
        this.drawerLayoutAdapter.notifyDataSetChanged();
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 3 || i == 4 || i == 5 || i == 19 || i == 20) {
            int i2 = 1;
            if (iArr.length > 0 && iArr[0] == 0) {
                if (i == 4) {
                    ImageLoader.getInstance().checkMediaPaths();
                    return;
                } else if (i == 5) {
                    ContactsController.getInstance().readContacts();
                    return;
                } else if (i == 3) {
                    return;
                } else {
                    if (i == 19 || i == 20) {
                        i2 = 0;
                    }
                }
            }
            if (i2 != 0) {
                Builder builder = new Builder(this);
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                if (i == 3) {
                    builder.setMessage(LocaleController.getString("PermissionNoAudio", C0338R.string.PermissionNoAudio));
                } else if (i == 4) {
                    builder.setMessage(LocaleController.getString("PermissionStorage", C0338R.string.PermissionStorage));
                } else if (i == 5) {
                    builder.setMessage(LocaleController.getString("PermissionContacts", C0338R.string.PermissionContacts));
                } else if (i == 19 || i == 20) {
                    builder.setMessage(LocaleController.getString("PermissionNoCamera", C0338R.string.PermissionNoCamera));
                }
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", C0338R.string.PermissionOpenSettings), new OnClickListener() {
                    @TargetApi(9)
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                            LaunchActivity.this.startActivity(intent);
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                });
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                builder.show();
                return;
            }
        } else if (i == 2 && iArr.length > 0 && iArr[0] == 0) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (AndroidUtilities.isTablet()) {
            if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
            }
            if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
                ((BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(i, strArr, iArr);
            }
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.passcodeView != null) {
            ApplicationLoader.mainInterfacePaused = false;
            onPasscodeResume();
            if (this.passcodeView.getVisibility() != 0) {
                this.actionBarLayout.onResume();
                if (AndroidUtilities.isTablet()) {
                    this.rightActionBarLayout.onResume();
                    this.layersActionBarLayout.onResume();
                }
            } else {
                this.actionBarLayout.dismissDialogs();
                if (AndroidUtilities.isTablet()) {
                    this.rightActionBarLayout.dismissDialogs();
                    this.layersActionBarLayout.dismissDialogs();
                }
                this.passcodeView.onResume();
            }
            AndroidUtilities.checkForCrashes(this);
            AndroidUtilities.checkForUpdates(this);
            ConnectionsManager.getInstance().setAppPaused(false, false);
            updateCurrentConnectionState();
            if (PhotoViewer.getInstance().isVisible()) {
                PhotoViewer.getInstance().onResume();
            }
            initGhostMode();
        }
    }

    protected void onSaveInstanceState(Bundle bundle) {
        try {
            BaseFragment baseFragment;
            super.onSaveInstanceState(bundle);
            if (!AndroidUtilities.isTablet()) {
                if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    baseFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                }
                baseFragment = null;
            } else if (!this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                baseFragment = (BaseFragment) this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
            } else if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
                if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                    baseFragment = (BaseFragment) this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
                }
                baseFragment = null;
            } else {
                baseFragment = (BaseFragment) this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
            }
            if (baseFragment != null) {
                Bundle arguments = baseFragment.getArguments();
                if ((baseFragment instanceof ChatActivity) && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "chat");
                } else if (baseFragment instanceof SettingsActivity) {
                    bundle.putString("fragment", "settings");
                } else if ((baseFragment instanceof GroupCreateFinalActivity) && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "group");
                } else if (baseFragment instanceof WallpapersActivity) {
                    bundle.putString("fragment", "wallpapers");
                } else {
                    if (baseFragment instanceof ProfileActivity) {
                        if (((ProfileActivity) baseFragment).isChat() && arguments != null) {
                            bundle.putBundle("args", arguments);
                            bundle.putString("fragment", "chat_profile");
                        }
                    }
                    if ((baseFragment instanceof ChannelCreateActivity) && arguments != null && arguments.getInt("step") == 0) {
                        bundle.putBundle("args", arguments);
                        bundle.putString("fragment", "channel");
                    } else if ((baseFragment instanceof ChannelEditActivity) && arguments != null) {
                        bundle.putBundle("args", arguments);
                        bundle.putString("fragment", "edit");
                    } else if (baseFragment instanceof MoboSettingsActivity) {
                        bundle.putString("fragment", "mobo_settings");
                    }
                }
                baseFragment.saveSelfArgs(bundle);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    protected void onStart() {
        super.onStart();
        Browser.bindCustomTabsService(this);
    }

    protected void onStop() {
        super.onStop();
        Browser.unbindCustomTabsService(this);
    }

    public void presentFragment(BaseFragment baseFragment) {
        this.actionBarLayout.presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        return this.actionBarLayout.presentFragment(baseFragment, z, z2, true);
    }

    public void runLinkRequest(String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, String str7, int i) {
        runLinkRequest(str, str2, str3, str4, str5, str6, z, num, str7, i, false);
    }

    public void runLinkRequest(String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, String str7, int i, boolean z2) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        int i2 = 0;
        if (str != null) {
            TLObject tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
            tL_contacts_resolveUsername.username = str;
            i2 = ConnectionsManager.getInstance().sendRequest(tL_contacts_resolveUsername, new C16149(progressDialog, str7, str5, str4, num));
        } else if (str2 != null) {
            if (i == 0) {
                TLObject tL_messages_checkChatInvite = new TL_messages_checkChatInvite();
                tL_messages_checkChatInvite.hash = str2;
                ConnectionsManager instance = ConnectionsManager.getInstance();
                i2 = r17.sendRequest(tL_messages_checkChatInvite, new AnonymousClass10(progressDialog, str2, z2, str, str3, str4, str5, str6, z, num, str7), 2);
            } else if (i == 1) {
                TLObject tL_messages_importChatInvite = new TL_messages_importChatInvite();
                tL_messages_importChatInvite.hash = str2;
                ConnectionsManager.getInstance().sendRequest(tL_messages_importChatInvite, new AnonymousClass11(progressDialog), 2);
            }
        } else if (str3 != null) {
            if (!mainFragmentsStack.isEmpty()) {
                InputStickerSet tL_inputStickerSetShortName = new TL_inputStickerSetShortName();
                tL_inputStickerSetShortName.short_name = str3;
                BaseFragment baseFragment = (BaseFragment) mainFragmentsStack.get(mainFragmentsStack.size() - 1);
                baseFragment.showDialog(new StickersAlert(this, baseFragment, tL_inputStickerSetShortName, null, null));
                return;
            }
            return;
        } else if (str6 != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            BaseFragment dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(new AnonymousClass12(z, str6));
            presentFragment(dialogsActivity, false, true);
        }
        if (i2 != 0) {
            progressDialog.setButton(-2, LocaleController.getString("Cancel", C0338R.string.Cancel), new AnonymousClass13(i2));
            progressDialog.show();
        }
    }

    public AlertDialog showAlertDialog(Builder builder) {
        AlertDialog alertDialog = null;
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        try {
            this.visibleDialog = builder.show();
            this.visibleDialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    LaunchActivity.this.visibleDialog = null;
                }
            });
            return this.visibleDialog;
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
            return alertDialog;
        }
    }
}

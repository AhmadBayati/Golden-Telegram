package com.hanista.mobogram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.PointerIconCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.AnimatorListenerAdapterProxy;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageReceiver;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.SecretChatHelper;
import com.hanista.mobogram.messenger.SendMessagesHelper;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.AdaptiveEvaluator;
import com.hanista.mobogram.messenger.query.BotQuery;
import com.hanista.mobogram.messenger.query.SharedMediaQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p010k.MaterialHelperUtil;
import com.hanista.mobogram.mobo.p014n.ChatMembersActivity;
import com.hanista.mobogram.mobo.p016p.SpecificContactActivity;
import com.hanista.mobogram.mobo.p016p.SpecificContactBiz;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.BotInfo;
import com.hanista.mobogram.tgnet.TLRPC.ChannelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantCreator;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantEditor;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantsRecent;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelRoleEditor;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_channelParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_editAdmin;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatChannelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantCreator;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatPhotoEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import com.hanista.mobogram.tgnet.TLRPC.TL_encryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageEncryptedAction;
import com.hanista.mobogram.tgnet.TLRPC.TL_userEmpty;
import com.hanista.mobogram.tgnet.TLRPC.Updates;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BackDrawable;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.SimpleTextView;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.AboutLinkCell;
import com.hanista.mobogram.ui.Cells.AboutLinkCell.AboutLinkCellDelegate;
import com.hanista.mobogram.ui.Cells.DividerCell;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.LoadingCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCell;
import com.hanista.mobogram.ui.Cells.TextDetailCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.AlertsCreator;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.AvatarUpdater;
import com.hanista.mobogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.ContactsActivity.ContactsActivityMultiSelectDelegate;
import com.hanista.mobogram.ui.DialogsActivity.DialogsActivityDelegate;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ProfileActivity extends BaseFragment implements NotificationCenterDelegate, DialogsActivityDelegate, PhotoViewerProvider {
    private static final int add_contact = 1;
    private static final int add_shortcut = 14;
    private static final int add_to_channel = 162;
    private static final int add_to_specific_contacts = 163;
    private static final int block_contact = 2;
    private static final int convert_to_supergroup = 13;
    private static final int delete_contact = 5;
    private static final int edit_channel = 12;
    private static final int edit_contact = 4;
    private static final int edit_name = 8;
    private static final int invite_to_group = 9;
    private static final int leave_group = 7;
    private static final int leave_without_delete = 161;
    private static final int search_members = 164;
    private static final int set_admins = 11;
    private static final int share = 10;
    private static final int share_contact = 3;
    private int addMemberRow;
    private boolean allowProfileAnimation;
    private ActionBarMenuItem animatingItem;
    private float animationProgress;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int blockedUsersRow;
    private BotInfo botInfo;
    private int channelInfoRow;
    private int channelNameRow;
    private int chat_id;
    private int convertHelpRow;
    private int convertRow;
    private boolean creatingChat;
    private int creatorID;
    private SimpleTextView creatorTextView;
    private Chat currentChat;
    private EncryptedChat currentEncryptedChat;
    private long dialog_id;
    private int emptyRow;
    private int emptyRowChat;
    private int emptyRowChat2;
    private int extraHeight;
    private int groupManagerId;
    private boolean hasSearchMemnbersItem;
    private ChatFull info;
    private int initialAnimationExtraHeight;
    private LinearLayoutManager layoutManager;
    private int leaveChannelRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int loadMoreMembersRow;
    private boolean loadingUsers;
    private int managementRow;
    private int membersEndRow;
    private int membersRow;
    private int membersSectionRow;
    private long mergeDialogId;
    private SimpleTextView[] nameTextView;
    private int onlineCount;
    private SimpleTextView[] onlineTextView;
    private boolean openAnimationInProgress;
    private HashMap<Integer, ChatParticipant> participantsMap;
    private int phoneRow;
    private boolean playProfileAnimation;
    private int rowCount;
    private int sectionRow;
    private int selectedUser;
    private int settingsKeyRow;
    private int settingsNotificationsRow;
    private int settingsTimerRow;
    private int sharedMediaRow;
    private ArrayList<Integer> sortedUsers;
    private int startSecretChatRow;
    private TopView topView;
    private int topViewColor;
    private int totalMediaCount;
    private int totalMediaCountMerge;
    private boolean userBlocked;
    private int userInfoRow;
    private int userSectionRow;
    private int user_id;
    private int usernameRow;
    private boolean usersEndReached;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.15 */
    class AnonymousClass15 implements OnClickListener {
        final /* synthetic */ User val$user;

        AnonymousClass15(User user) {
            this.val$user = user;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 0) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + this.val$user.username));
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.16 */
    class AnonymousClass16 implements OnClickListener {
        final /* synthetic */ User val$user;

        AnonymousClass16(User user) {
            this.val$user = user;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 0) {
                try {
                    Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + this.val$user.phone));
                    intent.addFlags(268435456);
                    ProfileActivity.this.getParentActivity().startActivityForResult(intent, 500);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            } else if (i == ProfileActivity.add_contact) {
                try {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + this.val$user.phone));
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.17 */
    class AnonymousClass17 implements OnClickListener {
        final /* synthetic */ int val$position;

        AnonymousClass17(int i) {
            this.val$position = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            try {
                CharSequence charSequence;
                if (this.val$position == ProfileActivity.this.channelInfoRow) {
                    charSequence = ProfileActivity.this.info.about;
                } else {
                    Object userAbout = MessagesController.getInstance().getUserAbout(ProfileActivity.this.botInfo.user_id);
                }
                if (charSequence != null) {
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", charSequence));
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.19 */
    class AnonymousClass19 implements RequestDelegate {
        final /* synthetic */ int val$delay;
        final /* synthetic */ TL_channels_getParticipants val$req;

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.19.1 */
        class C18251 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C18251(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$error == null) {
                    TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) this.val$response;
                    MessagesController.getInstance().putUsers(tL_channels_channelParticipants.users, false);
                    if (tL_channels_channelParticipants.users.size() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                        ProfileActivity.this.usersEndReached = true;
                    }
                    if (AnonymousClass19.this.val$req.offset == 0) {
                        ProfileActivity.this.participantsMap.clear();
                        ProfileActivity.this.info.participants = new TL_chatParticipants();
                        MessagesStorage.getInstance().putUsersAndChats(tL_channels_channelParticipants.users, null, true, true);
                        MessagesStorage.getInstance().updateChannelUsers(ProfileActivity.this.chat_id, tL_channels_channelParticipants.participants);
                    }
                    for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i += ProfileActivity.add_contact) {
                        TL_chatChannelParticipant tL_chatChannelParticipant = new TL_chatChannelParticipant();
                        tL_chatChannelParticipant.channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i);
                        tL_chatChannelParticipant.inviter_id = tL_chatChannelParticipant.channelParticipant.inviter_id;
                        tL_chatChannelParticipant.user_id = tL_chatChannelParticipant.channelParticipant.user_id;
                        tL_chatChannelParticipant.date = tL_chatChannelParticipant.channelParticipant.date;
                        if (!ProfileActivity.this.participantsMap.containsKey(Integer.valueOf(tL_chatChannelParticipant.user_id))) {
                            ProfileActivity.this.info.participants.participants.add(tL_chatChannelParticipant);
                            ProfileActivity.this.participantsMap.put(Integer.valueOf(tL_chatChannelParticipant.user_id), tL_chatChannelParticipant);
                        }
                    }
                }
                ProfileActivity.this.updateOnlineCount();
                ProfileActivity.this.loadingUsers = false;
                ProfileActivity.this.updateRowsIds();
                if (ProfileActivity.this.listAdapter != null) {
                    ProfileActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        }

        AnonymousClass19(TL_channels_getParticipants tL_channels_getParticipants, int i) {
            this.val$req = tL_channels_getParticipants;
            this.val$delay = i;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C18251(tL_error, tLObject), (long) this.val$delay);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.1 */
    class C18261 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;

        C18261(Semaphore semaphore) {
            this.val$semaphore = semaphore;
        }

        public void run() {
            ProfileActivity.this.currentChat = MessagesStorage.getInstance().getChat(ProfileActivity.this.chat_id);
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.23 */
    class AnonymousClass23 implements Runnable {
        final /* synthetic */ Object[] val$args;

        AnonymousClass23(Object[] objArr) {
            this.val$args = objArr;
        }

        public void run() {
            NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            EncryptedChat encryptedChat = (EncryptedChat) this.val$args[0];
            Bundle bundle = new Bundle();
            bundle.putInt("enc_id", encryptedChat.id);
            ProfileActivity.this.presentFragment(new ChatActivity(bundle), true);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.24 */
    class AnonymousClass24 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ Runnable val$callback;

        AnonymousClass24(Runnable runnable) {
            this.val$callback = runnable;
        }

        public void onAnimationEnd(Animator animator) {
            if (VERSION.SDK_INT > 15) {
                ProfileActivity.this.listView.setLayerType(0, null);
            }
            if (ProfileActivity.this.animatingItem != null) {
                ProfileActivity.this.actionBar.createMenu().clearItems();
                ProfileActivity.this.animatingItem = null;
            }
            this.val$callback.run();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.25 */
    class AnonymousClass25 implements Runnable {
        final /* synthetic */ AnimatorSet val$animatorSet;

        AnonymousClass25(AnimatorSet animatorSet) {
            this.val$animatorSet = animatorSet;
        }

        public void run() {
            this.val$animatorSet.start();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.29 */
    class AnonymousClass29 implements OnClickListener {
        final /* synthetic */ ArrayList val$options;

        AnonymousClass29(ArrayList arrayList) {
            this.val$options = arrayList;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i >= 0 && i < this.val$options.size() && ((Integer) this.val$options.get(i)).intValue() == 0 && ProfileActivity.this.currentChat != null) {
                ProfileActivity.this.showUserMessages(ProfileActivity.this.selectedUser);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.2 */
    class C18272 implements AvatarUpdaterDelegate {
        C18272() {
        }

        public void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
            if (ProfileActivity.this.chat_id != 0) {
                MessagesController.getInstance().changeChatAvatar(ProfileActivity.this.chat_id, inputFile);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.3 */
    class C18283 extends ActionBar {
        C18283(Context context) {
            super(context);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return super.onTouchEvent(motionEvent);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.4 */
    class C18324 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.4.1 */
        class C18291 implements OnClickListener {
            C18291() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (ProfileActivity.this.userBlocked) {
                    MessagesController.getInstance().unblockUser(ProfileActivity.this.user_id);
                } else {
                    MessagesController.getInstance().blockUser(ProfileActivity.this.user_id);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.4.2 */
        class C18302 implements OnClickListener {
            final /* synthetic */ User val$user;

            C18302(User user) {
                this.val$user = user;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(this.val$user);
                ContactsController.getInstance().deleteContact(arrayList);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.4.3 */
        class C18313 implements DialogsActivityDelegate {
            final /* synthetic */ User val$user;

            C18313(User user) {
                this.val$user = user;
            }

            public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                bundle.putInt("chat_id", -((int) j));
                if (MessagesController.checkCanOpenChat(bundle, dialogsActivity)) {
                    NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    MessagesController.getInstance().addUserToChat(-((int) j), this.val$user, null, 0, null, ProfileActivity.this);
                    ProfileActivity.this.presentFragment(new ChatActivity(bundle), true);
                    ProfileActivity.this.removeSelfFromStack();
                }
            }
        }

        C18324() {
        }

        public void onItemClick(int i) {
            if (ProfileActivity.this.getParentActivity() != null) {
                if (i == -1) {
                    ProfileActivity.this.finishFragment();
                } else if (i == ProfileActivity.block_contact) {
                    r0 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (r0 == null) {
                        return;
                    }
                    if (!r0.bot) {
                        Builder builder = new Builder(ProfileActivity.this.getParentActivity());
                        if (ProfileActivity.this.userBlocked) {
                            builder.setMessage(LocaleController.getString("AreYouSureUnblockContact", C0338R.string.AreYouSureUnblockContact));
                        } else {
                            builder.setMessage(LocaleController.getString("AreYouSureBlockContact", C0338R.string.AreYouSureBlockContact));
                        }
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C18291());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        ProfileActivity.this.showDialog(builder.create());
                    } else if (ProfileActivity.this.userBlocked) {
                        MessagesController.getInstance().unblockUser(ProfileActivity.this.user_id);
                        SendMessagesHelper.getInstance().sendMessage("/start", (long) ProfileActivity.this.user_id, null, null, false, null, null, null);
                        ProfileActivity.this.finishFragment();
                    } else {
                        MessagesController.getInstance().blockUser(ProfileActivity.this.user_id);
                    }
                } else if (i == ProfileActivity.add_contact) {
                    r0 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    r1 = new Bundle();
                    r1.putInt("user_id", r0.id);
                    r1.putBoolean("addContact", true);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(r1));
                } else if (i == ProfileActivity.share_contact) {
                    r0 = new Bundle();
                    r0.putBoolean("onlySelect", true);
                    r0.putInt("dialogsType", ProfileActivity.add_contact);
                    r0.putString("selectAlertString", LocaleController.getString("SendContactTo", C0338R.string.SendContactTo));
                    r0.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", C0338R.string.SendContactToGroup));
                    r1 = new DialogsActivity(r0);
                    r1.setDelegate(ProfileActivity.this);
                    ProfileActivity.this.presentFragment(r1);
                } else if (i == ProfileActivity.edit_contact) {
                    r0 = new Bundle();
                    r0.putInt("user_id", ProfileActivity.this.user_id);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(r0));
                } else if (i == ProfileActivity.delete_contact) {
                    r0 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (r0 != null && ProfileActivity.this.getParentActivity() != null) {
                        Builder builder2 = new Builder(ProfileActivity.this.getParentActivity());
                        builder2.setMessage(LocaleController.getString("AreYouSureDeleteContact", C0338R.string.AreYouSureDeleteContact));
                        builder2.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        builder2.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C18302(r0));
                        builder2.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                        ProfileActivity.this.showDialog(builder2.create());
                    }
                } else if (i == ProfileActivity.leave_group) {
                    ProfileActivity.this.leaveChatPressed();
                } else if (i == ProfileActivity.leave_without_delete) {
                    ProfileActivity.this.leaveWitoutDeletePressed();
                } else if (i == ProfileActivity.search_members) {
                    r0 = new Bundle();
                    r0.putInt("chat_id", ProfileActivity.this.chat_id);
                    r1 = new ChatMembersActivity(r0);
                    r1.m1943a(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(r1);
                } else if (i == ProfileActivity.edit_name) {
                    r0 = new Bundle();
                    r0.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ChangeChatNameActivity(r0));
                } else if (i == ProfileActivity.edit_channel) {
                    r0 = new Bundle();
                    r0.putInt("chat_id", ProfileActivity.this.chat_id);
                    r1 = new ChannelEditActivity(r0);
                    r1.setInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(r1);
                } else if (i == ProfileActivity.invite_to_group) {
                    r0 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (r0 != null) {
                        r1 = new Bundle();
                        r1.putBoolean("onlySelect", true);
                        r1.putInt("dialogsType", ProfileActivity.block_contact);
                        r5 = new Object[ProfileActivity.block_contact];
                        r5[0] = UserObject.getUserName(r0);
                        r5[ProfileActivity.add_contact] = "%1$s";
                        r1.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", C0338R.string.AddToTheGroupTitle, r5));
                        BaseFragment dialogsActivity = new DialogsActivity(r1);
                        dialogsActivity.setDelegate(new C18313(r0));
                        ProfileActivity.this.presentFragment(dialogsActivity);
                    }
                } else if (i == ProfileActivity.share) {
                    try {
                        r0 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (r0 != null) {
                            r1 = new Intent("android.intent.action.SEND");
                            r1.setType("text/plain");
                            String userAbout = MessagesController.getInstance().getUserAbout(ProfileActivity.this.botInfo.user_id);
                            if (ProfileActivity.this.botInfo == null || userAbout == null) {
                                Object[] objArr = new Object[ProfileActivity.add_contact];
                                objArr[0] = r0.username;
                                r1.putExtra("android.intent.extra.TEXT", String.format("https://telegram.me/%s", objArr));
                            } else {
                                r5 = new Object[ProfileActivity.block_contact];
                                r5[0] = userAbout;
                                r5[ProfileActivity.add_contact] = r0.username;
                                r1.putExtra("android.intent.extra.TEXT", String.format("%s https://telegram.me/%s", r5));
                            }
                            ProfileActivity.this.startActivityForResult(Intent.createChooser(r1, LocaleController.getString("BotShare", C0338R.string.BotShare)), 500);
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                } else if (i == ProfileActivity.set_admins) {
                    r0 = new Bundle();
                    r0.putInt("chat_id", ProfileActivity.this.chat_id);
                    r1 = new SetAdminsActivity(r0);
                    r1.setChatInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(r1);
                } else if (i == ProfileActivity.convert_to_supergroup) {
                    r0 = new Bundle();
                    r0.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ConvertGroupActivity(r0));
                } else if (i == ProfileActivity.add_shortcut) {
                    try {
                        long j;
                        if (ProfileActivity.this.currentEncryptedChat != null) {
                            j = ((long) ProfileActivity.this.currentEncryptedChat.id) << 32;
                        } else if (ProfileActivity.this.user_id != 0) {
                            j = (long) ProfileActivity.this.user_id;
                        } else if (ProfileActivity.this.chat_id != 0) {
                            j = (long) (-ProfileActivity.this.chat_id);
                        } else {
                            return;
                        }
                        AndroidUtilities.installShortcut(j);
                    } catch (Throwable e2) {
                        FileLog.m18e("tmessages", e2);
                    }
                } else if (i == ProfileActivity.add_to_channel) {
                    r0 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    r1 = new Intent(ProfileActivity.this.getParentActivity(), LaunchActivity.class);
                    r1.setAction("android.intent.action.VIEW");
                    r1.setData(Uri.parse("https://telegram.me/" + r0.username + "?startgroup=new"));
                    ProfileActivity.this.getParentActivity().startActivity(r1);
                } else if (i == ProfileActivity.add_to_specific_contacts) {
                    r0 = new Bundle();
                    r0.putInt("user_id", ProfileActivity.this.user_id);
                    ProfileActivity.this.presentFragment(new SpecificContactActivity(r0));
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.5 */
    class C18335 extends FrameLayout {
        C18335(Context context) {
            super(context);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ProfileActivity.this.checkListViewScroll();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.6 */
    class C18346 extends RecyclerListView {
        C18346(Context context) {
            super(context);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.7 */
    class C18357 extends LinearLayoutManager {
        C18357(Context context) {
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.8 */
    class C18388 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.8.1 */
        class C18361 implements OnClickListener {
            C18361() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.creatingChat = true;
                SecretChatHelper.getInstance().startSecretChat(ProfileActivity.this.getParentActivity(), MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id)));
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.8.2 */
        class C18372 implements OnClickListener {
            C18372() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance().convertToMegaGroup(ProfileActivity.this.getParentActivity(), ProfileActivity.this.chat_id);
            }
        }

        C18388() {
        }

        public void onItemClick(View view, int i) {
            if (ProfileActivity.this.getParentActivity() != null) {
                Bundle bundle;
                if (i == ProfileActivity.this.sharedMediaRow) {
                    bundle = new Bundle();
                    if (ProfileActivity.this.user_id != 0) {
                        bundle.putLong("dialog_id", ProfileActivity.this.dialog_id != 0 ? ProfileActivity.this.dialog_id : (long) ProfileActivity.this.user_id);
                    } else {
                        bundle.putLong("dialog_id", (long) (-ProfileActivity.this.chat_id));
                    }
                    BaseFragment mediaActivity = new MediaActivity(bundle);
                    mediaActivity.setChatInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(mediaActivity);
                } else if (i == ProfileActivity.this.settingsKeyRow) {
                    r0 = new Bundle();
                    r0.putInt("chat_id", (int) (ProfileActivity.this.dialog_id >> 32));
                    ProfileActivity.this.presentFragment(new IdenticonActivity(r0));
                } else if (i == ProfileActivity.this.settingsTimerRow) {
                    ProfileActivity.this.showDialog(AndroidUtilities.buildTTLAlert(ProfileActivity.this.getParentActivity(), ProfileActivity.this.currentEncryptedChat).create());
                } else if (i == ProfileActivity.this.settingsNotificationsRow) {
                    bundle = new Bundle();
                    if (ProfileActivity.this.user_id != 0) {
                        bundle.putLong("dialog_id", ProfileActivity.this.dialog_id == 0 ? (long) ProfileActivity.this.user_id : ProfileActivity.this.dialog_id);
                    } else if (ProfileActivity.this.chat_id != 0) {
                        bundle.putLong("dialog_id", (long) (-ProfileActivity.this.chat_id));
                    }
                    ProfileActivity.this.presentFragment(new ProfileNotificationsActivity(bundle));
                } else if (i == ProfileActivity.this.startSecretChatRow) {
                    r0 = new Builder(ProfileActivity.this.getParentActivity());
                    r0.setMessage(LocaleController.getString("AreYouSureSecretChat", C0338R.string.AreYouSureSecretChat));
                    r0.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    r0.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C18361());
                    r0.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    ProfileActivity.this.showDialog(r0.create());
                } else if (i > ProfileActivity.this.emptyRowChat2 && i < ProfileActivity.this.membersEndRow) {
                    int i2 = !ProfileActivity.this.sortedUsers.isEmpty() ? ((ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((i - ProfileActivity.this.emptyRowChat2) - 1)).intValue())).user_id : ((ChatParticipant) ProfileActivity.this.info.participants.participants.get((i - ProfileActivity.this.emptyRowChat2) - 1)).user_id;
                    if (i2 != UserConfig.getClientUserId()) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("user_id", i2);
                        ProfileActivity.this.presentFragment(new ProfileActivity(bundle2));
                    }
                } else if (i == ProfileActivity.this.addMemberRow) {
                    ProfileActivity.this.openAddMember();
                } else if (i == ProfileActivity.this.channelNameRow) {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        if (ProfileActivity.this.info.about == null || ProfileActivity.this.info.about.length() <= 0) {
                            intent.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\nhttps://telegram.me/" + ProfileActivity.this.currentChat.username);
                        } else {
                            intent.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\n" + ProfileActivity.this.info.about + "\nhttps://telegram.me/" + ProfileActivity.this.currentChat.username);
                        }
                        ProfileActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", C0338R.string.BotShare)), 500);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                } else if (i == ProfileActivity.this.leaveChannelRow) {
                    ProfileActivity.this.leaveChatPressed();
                } else if (i == ProfileActivity.this.membersRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.managementRow) {
                    r0 = new Bundle();
                    r0.putInt("chat_id", ProfileActivity.this.chat_id);
                    if (i == ProfileActivity.this.blockedUsersRow) {
                        r0.putInt("type", 0);
                    } else if (i == ProfileActivity.this.managementRow) {
                        r0.putInt("type", ProfileActivity.add_contact);
                    } else if (i == ProfileActivity.this.membersRow) {
                        r0.putInt("type", ProfileActivity.block_contact);
                    }
                    ProfileActivity.this.presentFragment(new ChannelUsersActivity(r0));
                } else if (i == ProfileActivity.this.convertRow) {
                    r0 = new Builder(ProfileActivity.this.getParentActivity());
                    r0.setMessage(LocaleController.getString("ConvertGroupAlert", C0338R.string.ConvertGroupAlert));
                    r0.setTitle(LocaleController.getString("ConvertGroupAlertWarning", C0338R.string.ConvertGroupAlertWarning));
                    r0.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C18372());
                    r0.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    ProfileActivity.this.showDialog(r0.create());
                } else {
                    ProfileActivity.this.processOnClickOrPress(i);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ProfileActivity.9 */
    class C18449 implements OnItemLongClickListener {

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.9.1 */
        class C18421 implements OnClickListener {
            final /* synthetic */ ChatParticipant val$user;

            /* renamed from: com.hanista.mobogram.ui.ProfileActivity.9.1.1 */
            class C18411 implements RequestDelegate {

                /* renamed from: com.hanista.mobogram.ui.ProfileActivity.9.1.1.1 */
                class C18391 implements Runnable {
                    C18391() {
                    }

                    public void run() {
                        MessagesController.getInstance().loadFullChat(ProfileActivity.this.chat_id, 0, true);
                    }
                }

                /* renamed from: com.hanista.mobogram.ui.ProfileActivity.9.1.1.2 */
                class C18402 implements Runnable {
                    final /* synthetic */ TL_error val$error;

                    C18402(TL_error tL_error) {
                        this.val$error = tL_error;
                    }

                    public void run() {
                        AlertsCreator.showAddUserAlert(this.val$error.text, ProfileActivity.this, false);
                    }
                }

                C18411() {
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        MessagesController.getInstance().processUpdates((Updates) tLObject, false);
                        AndroidUtilities.runOnUIThread(new C18391(), 1000);
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new C18402(tL_error));
                }
            }

            C18421(ChatParticipant chatParticipant) {
                this.val$user = chatParticipant;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    TL_chatChannelParticipant tL_chatChannelParticipant = (TL_chatChannelParticipant) this.val$user;
                    tL_chatChannelParticipant.channelParticipant = new TL_channelParticipantEditor();
                    tL_chatChannelParticipant.channelParticipant.inviter_id = UserConfig.getClientUserId();
                    tL_chatChannelParticipant.channelParticipant.user_id = this.val$user.user_id;
                    tL_chatChannelParticipant.channelParticipant.date = this.val$user.date;
                    TLObject tL_channels_editAdmin = new TL_channels_editAdmin();
                    tL_channels_editAdmin.channel = MessagesController.getInputChannel(ProfileActivity.this.chat_id);
                    tL_channels_editAdmin.user_id = MessagesController.getInputUser(ProfileActivity.this.selectedUser);
                    tL_channels_editAdmin.role = new TL_channelRoleEditor();
                    ConnectionsManager.getInstance().sendRequest(tL_channels_editAdmin, new C18411());
                } else if (i == ProfileActivity.add_contact) {
                    ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                } else if (i == ProfileActivity.block_contact) {
                    ProfileActivity.this.showUserMessages(ProfileActivity.this.selectedUser);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.9.2 */
        class C18432 implements OnClickListener {
            C18432() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                } else if (i == ProfileActivity.add_contact) {
                    ProfileActivity.this.showUserMessages(ProfileActivity.this.selectedUser);
                }
            }
        }

        C18449() {
        }

        public boolean onItemClick(View view, int i) {
            if (i <= ProfileActivity.this.emptyRowChat2 || i >= ProfileActivity.this.membersEndRow) {
                return ProfileActivity.this.processOnClickOrPress(i);
            }
            if (ProfileActivity.this.getParentActivity() == null) {
                return false;
            }
            boolean z;
            ChatParticipant chatParticipant = !ProfileActivity.this.sortedUsers.isEmpty() ? (ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((i - ProfileActivity.this.emptyRowChat2) - 1)).intValue()) : (ChatParticipant) ProfileActivity.this.info.participants.participants.get((i - ProfileActivity.this.emptyRowChat2) - 1);
            ProfileActivity.this.selectedUser = chatParticipant.user_id;
            boolean z2;
            if (ChatObject.isChannel(ProfileActivity.this.currentChat)) {
                ChannelParticipant channelParticipant = ((TL_chatChannelParticipant) chatParticipant).channelParticipant;
                if (chatParticipant.user_id != UserConfig.getClientUserId()) {
                    if (ProfileActivity.this.currentChat.creator) {
                        z = true;
                    } else if ((channelParticipant instanceof TL_channelParticipant) && (ProfileActivity.this.currentChat.editor || channelParticipant.inviter_id == UserConfig.getClientUserId())) {
                        z = true;
                    }
                    z2 = (channelParticipant instanceof TL_channelParticipant) && !MessagesController.getInstance().getUser(Integer.valueOf(chatParticipant.user_id)).bot;
                }
                z = false;
                if (!(channelParticipant instanceof TL_channelParticipant)) {
                }
            } else {
                if (chatParticipant.user_id != UserConfig.getClientUserId()) {
                    if (ProfileActivity.this.currentChat.creator) {
                        z2 = false;
                        z = true;
                    } else if ((chatParticipant instanceof TL_chatParticipant) && ((ProfileActivity.this.currentChat.admin && ProfileActivity.this.currentChat.admins_enabled) || chatParticipant.inviter_id == UserConfig.getClientUserId())) {
                        z2 = false;
                        z = true;
                    }
                }
                z2 = false;
                z = false;
            }
            if (z) {
                Builder builder = new Builder(ProfileActivity.this.getParentActivity());
                if (ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.currentChat.creator && r4) {
                    CharSequence[] charSequenceArr = new CharSequence[ProfileActivity.share_contact];
                    charSequenceArr[0] = LocaleController.getString("SetAsAdmin", C0338R.string.SetAsAdmin);
                    charSequenceArr[ProfileActivity.add_contact] = LocaleController.getString("KickFromGroup", C0338R.string.KickFromGroup);
                    charSequenceArr[ProfileActivity.block_contact] = LocaleController.getString("ShowThisUserMessages", C0338R.string.ShowThisUserMessages);
                    builder.setItems(charSequenceArr, new C18421(chatParticipant));
                } else {
                    CharSequence[] charSequenceArr2 = new CharSequence[ProfileActivity.block_contact];
                    charSequenceArr2[0] = ProfileActivity.this.chat_id > 0 ? LocaleController.getString("KickFromGroup", C0338R.string.KickFromGroup) : LocaleController.getString("KickFromBroadcast", C0338R.string.KickFromBroadcast);
                    charSequenceArr2[ProfileActivity.add_contact] = LocaleController.getString("ShowThisUserMessages", C0338R.string.ShowThisUserMessages);
                    builder.setItems(charSequenceArr2, new C18432());
                }
                ProfileActivity.this.showDialog(builder.create());
                return true;
            }
            ProfileActivity.this.createUserAvatarMenu();
            return true;
        }
    }

    private class ListAdapter extends Adapter {
        private Context mContext;

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.ListAdapter.1 */
        class C18451 extends TextDetailCell {
            C18451(Context context) {
                super(context);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == ProfileActivity.block_contact)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.ListAdapter.2 */
        class C18462 extends TextCell {
            C18462(Context context) {
                super(context);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == ProfileActivity.block_contact)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.ListAdapter.3 */
        class C18473 extends UserCell {
            C18473(Context context, int i, int i2, boolean z) {
                super(context, i, i2, z);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == ProfileActivity.block_contact)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ProfileActivity.ListAdapter.4 */
        class C18484 implements AboutLinkCellDelegate {
            C18484() {
            }

            public void didPressUrl(String str) {
                if (str.startsWith("@")) {
                    MessagesController.openByUserName(str.substring(ProfileActivity.add_contact), ProfileActivity.this, 0);
                } else if (str.startsWith("#")) {
                    r0 = new DialogsActivity(null);
                    r0.setSearchString(str);
                    ProfileActivity.this.presentFragment(r0);
                } else if (str.startsWith("/") && ProfileActivity.this.parentLayout.fragmentsStack.size() > ProfileActivity.add_contact) {
                    r0 = (BaseFragment) ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                    if (r0 instanceof ChatActivity) {
                        ProfileActivity.this.finishFragment();
                        ((ChatActivity) r0).chatActivityEnterView.setCommand(null, str, false, false);
                    }
                }
            }
        }

        private class Holder extends ViewHolder {
            public Holder(View view) {
                super(view);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            return (i == ProfileActivity.this.emptyRow || i == ProfileActivity.this.emptyRowChat || i == ProfileActivity.this.emptyRowChat2) ? 0 : (i == ProfileActivity.this.sectionRow || i == ProfileActivity.this.userSectionRow) ? ProfileActivity.add_contact : (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.channelNameRow) ? ProfileActivity.block_contact : (i == ProfileActivity.this.leaveChannelRow || i == ProfileActivity.this.sharedMediaRow || i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsNotificationsRow || i == ProfileActivity.this.startSecretChatRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.membersRow || i == ProfileActivity.this.managementRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.convertRow || i == ProfileActivity.this.addMemberRow) ? ProfileActivity.share_contact : (i <= ProfileActivity.this.emptyRowChat2 || i >= ProfileActivity.this.membersEndRow) ? i == ProfileActivity.this.membersSectionRow ? ProfileActivity.delete_contact : i == ProfileActivity.this.convertHelpRow ? 6 : i == ProfileActivity.this.loadMoreMembersRow ? ProfileActivity.leave_group : (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow) ? ProfileActivity.edit_name : 0 : ProfileActivity.edit_contact;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
            r10 = this;
            r9 = -1;
            r7 = -13129447; // 0xffffffffff37a919 float:-2.4412673E38 double:NaN;
            r8 = 0;
            r4 = 1;
            r3 = 0;
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.f2491b;
            r2 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aB;
            r5 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aF;
            r0 = r11.getItemViewType();
            switch(r0) {
                case 0: goto L_0x0080;
                case 1: goto L_0x0014;
                case 2: goto L_0x00b0;
                case 3: goto L_0x0205;
                case 4: goto L_0x04e5;
                case 5: goto L_0x0014;
                case 6: goto L_0x0014;
                case 7: goto L_0x0014;
                case 8: goto L_0x05cf;
                default: goto L_0x0014;
            };
        L_0x0014:
            r0 = r3;
        L_0x0015:
            if (r0 == 0) goto L_0x007f;
        L_0x0017:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.user_id;
            if (r0 == 0) goto L_0x0636;
        L_0x001f:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.phoneRow;
            if (r12 == r0) goto L_0x005f;
        L_0x0027:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.settingsTimerRow;
            if (r12 == r0) goto L_0x005f;
        L_0x002f:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.settingsKeyRow;
            if (r12 == r0) goto L_0x005f;
        L_0x0037:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.settingsNotificationsRow;
            if (r12 == r0) goto L_0x005f;
        L_0x003f:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.sharedMediaRow;
            if (r12 == r0) goto L_0x005f;
        L_0x0047:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.startSecretChatRow;
            if (r12 == r0) goto L_0x005f;
        L_0x004f:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.usernameRow;
            if (r12 == r0) goto L_0x005f;
        L_0x0057:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.userInfoRow;
            if (r12 != r0) goto L_0x0060;
        L_0x005f:
            r3 = r4;
        L_0x0060:
            if (r3 == 0) goto L_0x06a1;
        L_0x0062:
            r0 = r11.itemView;
            r0 = r0.getBackground();
            if (r0 != 0) goto L_0x007f;
        L_0x006a:
            r0 = r11.itemView;
            r1 = 2130838069; // 0x7f020235 float:1.728111E38 double:1.0527738867E-314;
            r0.setBackgroundResource(r1);
            r0 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r0 == 0) goto L_0x007f;
        L_0x0078:
            r0 = r11.itemView;
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aA;
            r0.setBackgroundColor(r1);
        L_0x007f:
            return;
        L_0x0080:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.emptyRowChat;
            if (r12 == r0) goto L_0x0090;
        L_0x0088:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.emptyRowChat2;
            if (r12 != r0) goto L_0x00a0;
        L_0x0090:
            r0 = r11.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.EmptyCell) r0;
            r1 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
            r1 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r1);
            r0.setHeight(r1);
            r0 = r4;
            goto L_0x0015;
        L_0x00a0:
            r0 = r11.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.EmptyCell) r0;
            r1 = 1108344832; // 0x42100000 float:36.0 double:5.47595105E-315;
            r1 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r1);
            r0.setHeight(r1);
            r0 = r4;
            goto L_0x0015;
        L_0x00b0:
            r0 = r11.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.TextDetailCell) r0;
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x00c2;
        L_0x00ba:
            r0.setTextColor(r2);
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aH;
            r0.setValueColor(r1);
        L_0x00c2:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.phoneRow;
            if (r12 != r1) goto L_0x0149;
        L_0x00ca:
            r1 = com.hanista.mobogram.messenger.MessagesController.getInstance();
            r2 = com.hanista.mobogram.ui.ProfileActivity.this;
            r2 = r2.user_id;
            r2 = java.lang.Integer.valueOf(r2);
            r1 = r1.getUser(r2);
            r2 = r1.phone;
            if (r2 == 0) goto L_0x013e;
        L_0x00e0:
            r2 = r1.phone;
            r2 = r2.length();
            if (r2 == 0) goto L_0x013e;
        L_0x00e8:
            r2 = com.hanista.mobogram.PhoneFormat.PhoneFormat.getInstance();
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r7 = "+";
            r6 = r6.append(r7);
            r1 = r1.phone;
            r1 = r6.append(r1);
            r1 = r1.toString();
            r1 = r2.format(r1);
        L_0x0106:
            r2 = "PhoneMobile";
            r6 = 2131166108; // 0x7f07039c float:1.7946452E38 double:1.0529359595E-314;
            r2 = com.hanista.mobogram.messenger.LocaleController.getString(r2, r6);
            r6 = 2130838301; // 0x7f02031d float:1.728158E38 double:1.0527740014E-314;
            r0.setTextAndValueAndIcon(r1, r2, r6);
            r2 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r2 == 0) goto L_0x013b;
        L_0x011c:
            r2 = r10.mContext;
            r2 = r2.getResources();
            r6 = 2130838301; // 0x7f02031d float:1.728158E38 double:1.0527740014E-314;
            r2 = r2.getDrawable(r6);
            r6 = android.graphics.PorterDuff.Mode.SRC_IN;
            r2.setColorFilter(r5, r6);
            r5 = "PhoneMobile";
            r6 = 2131166108; // 0x7f07039c float:1.7946452E38 double:1.0529359595E-314;
            r5 = com.hanista.mobogram.messenger.LocaleController.getString(r5, r6);
            r0.setTextAndValueAndIcon(r1, r5, r2);
        L_0x013b:
            r0 = r4;
            goto L_0x0015;
        L_0x013e:
            r1 = "NumberUnknown";
            r2 = 2131166045; // 0x7f07035d float:1.7946324E38 double:1.0529359284E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
            goto L_0x0106;
        L_0x0149:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.usernameRow;
            if (r12 != r1) goto L_0x019b;
        L_0x0151:
            r1 = com.hanista.mobogram.messenger.MessagesController.getInstance();
            r2 = com.hanista.mobogram.ui.ProfileActivity.this;
            r2 = r2.user_id;
            r2 = java.lang.Integer.valueOf(r2);
            r1 = r1.getUser(r2);
            if (r1 == 0) goto L_0x0197;
        L_0x0165:
            r2 = r1.username;
            if (r2 == 0) goto L_0x0197;
        L_0x0169:
            r2 = r1.username;
            r2 = r2.length();
            if (r2 == 0) goto L_0x0197;
        L_0x0171:
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r5 = "@";
            r2 = r2.append(r5);
            r1 = r1.username;
            r1 = r2.append(r1);
            r1 = r1.toString();
        L_0x0187:
            r2 = "Username";
            r5 = 2131166367; // 0x7f07049f float:1.7946977E38 double:1.0529360875E-314;
            r2 = com.hanista.mobogram.messenger.LocaleController.getString(r2, r5);
            r0.setTextAndValue(r1, r2);
            r0 = r4;
            goto L_0x0015;
        L_0x0197:
            r1 = "-";
            goto L_0x0187;
        L_0x019b:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.channelNameRow;
            if (r12 != r1) goto L_0x06b0;
        L_0x01a3:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.currentChat;
            if (r1 == 0) goto L_0x0201;
        L_0x01ab:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.currentChat;
            r1 = r1.username;
            if (r1 == 0) goto L_0x0201;
        L_0x01b5:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.currentChat;
            r1 = r1.username;
            r1 = r1.length();
            if (r1 == 0) goto L_0x0201;
        L_0x01c3:
            r1 = new java.lang.StringBuilder;
            r1.<init>();
            r2 = "@";
            r1 = r1.append(r2);
            r2 = com.hanista.mobogram.ui.ProfileActivity.this;
            r2 = r2.currentChat;
            r2 = r2.username;
            r1 = r1.append(r2);
            r1 = r1.toString();
        L_0x01df:
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r5 = "telegram.me/";
            r2 = r2.append(r5);
            r5 = com.hanista.mobogram.ui.ProfileActivity.this;
            r5 = r5.currentChat;
            r5 = r5.username;
            r2 = r2.append(r5);
            r2 = r2.toString();
            r0.setTextAndValue(r1, r2);
            r0 = r4;
            goto L_0x0015;
        L_0x0201:
            r1 = "-";
            goto L_0x01df;
        L_0x0205:
            r0 = r11.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.TextCell) r0;
            r6 = -14606047; // 0xffffffffff212121 float:-2.1417772E38 double:NaN;
            r0.setTextColor(r6);
            r6 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r6 == 0) goto L_0x0218;
        L_0x0215:
            r0.setTextColor(r2);
        L_0x0218:
            r2 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aC;
            r2 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.m2286c(r2, r1);
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.sharedMediaRow;
            if (r12 != r1) goto L_0x029d;
        L_0x0226:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.totalMediaCount;
            if (r1 != r9) goto L_0x0268;
        L_0x022e:
            r1 = "Loading";
            r5 = 2131165836; // 0x7f07028c float:1.79459E38 double:1.052935825E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r5);
        L_0x0238:
            r5 = com.hanista.mobogram.ui.ProfileActivity.this;
            r5 = r5.user_id;
            if (r5 == 0) goto L_0x028f;
        L_0x0240:
            r5 = com.hanista.mobogram.messenger.UserConfig.getClientUserId();
            r6 = com.hanista.mobogram.ui.ProfileActivity.this;
            r6 = r6.user_id;
            if (r5 != r6) goto L_0x028f;
        L_0x024c:
            r5 = "SharedMedia";
            r6 = 2131166287; // 0x7f07044f float:1.7946815E38 double:1.052936048E-314;
            r5 = com.hanista.mobogram.messenger.LocaleController.getString(r5, r6);
            r6 = 2130838371; // 0x7f020363 float:1.7281722E38 double:1.052774036E-314;
            r0.setTextAndValueAndIcon(r5, r1, r6);
        L_0x025c:
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x0265;
        L_0x0262:
            r0.setValueColor(r2);
        L_0x0265:
            r0 = r4;
            goto L_0x0015;
        L_0x0268:
            r5 = "%d";
            r6 = new java.lang.Object[r4];
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r7 = r1.totalMediaCount;
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.totalMediaCountMerge;
            if (r1 == r9) goto L_0x028d;
        L_0x027b:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.totalMediaCountMerge;
        L_0x0281:
            r1 = r1 + r7;
            r1 = java.lang.Integer.valueOf(r1);
            r6[r3] = r1;
            r1 = java.lang.String.format(r5, r6);
            goto L_0x0238;
        L_0x028d:
            r1 = r3;
            goto L_0x0281;
        L_0x028f:
            r5 = "SharedMedia";
            r6 = 2131166287; // 0x7f07044f float:1.7946815E38 double:1.052936048E-314;
            r5 = com.hanista.mobogram.messenger.LocaleController.getString(r5, r6);
            r0.setTextAndValue(r5, r1);
            goto L_0x025c;
        L_0x029d:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.settingsTimerRow;
            if (r12 != r1) goto L_0x02e9;
        L_0x02a5:
            r1 = com.hanista.mobogram.messenger.MessagesController.getInstance();
            r5 = com.hanista.mobogram.ui.ProfileActivity.this;
            r6 = r5.dialog_id;
            r5 = 32;
            r6 = r6 >> r5;
            r5 = (int) r6;
            r5 = java.lang.Integer.valueOf(r5);
            r1 = r1.getEncryptedChat(r5);
            r5 = r1.ttl;
            if (r5 != 0) goto L_0x02e2;
        L_0x02bf:
            r1 = "ShortMessageLifetimeForever";
            r5 = 2131166291; // 0x7f070453 float:1.7946823E38 double:1.05293605E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r5);
        L_0x02c9:
            r5 = "MessageLifetime";
            r6 = 2131165874; // 0x7f0702b2 float:1.7945977E38 double:1.052935844E-314;
            r5 = com.hanista.mobogram.messenger.LocaleController.getString(r5, r6);
            r0.setTextAndValue(r5, r1);
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x02df;
        L_0x02dc:
            r0.setValueColor(r2);
        L_0x02df:
            r0 = r4;
            goto L_0x0015;
        L_0x02e2:
            r1 = r1.ttl;
            r1 = com.hanista.mobogram.messenger.AndroidUtilities.formatTTLString(r1);
            goto L_0x02c9;
        L_0x02e9:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.settingsNotificationsRow;
            if (r12 != r1) goto L_0x030d;
        L_0x02f1:
            r1 = "NotificationsAndSounds";
            r2 = 2131166033; // 0x7f070351 float:1.79463E38 double:1.0529359225E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
            r2 = 2130838371; // 0x7f020363 float:1.7281722E38 double:1.052774036E-314;
            r0.setTextAndIcon(r1, r2);
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x06b0;
        L_0x0307:
            r0.setValueColor(r5);
            r0 = r4;
            goto L_0x0015;
        L_0x030d:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.startSecretChatRow;
            if (r12 != r1) goto L_0x033f;
        L_0x0315:
            r1 = "StartEncryptedChat";
            r2 = 2131166309; // 0x7f070465 float:1.794686E38 double:1.052936059E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
            r0.setText(r1);
            r0.setTextColor(r7);
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x06b0;
        L_0x032b:
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aC;
            r2 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.f2491b;
            r5 = 21;
            r2 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.m2276a(r2, r5);
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.m2286c(r1, r2);
            r0.setTextColor(r1);
            r0 = r4;
            goto L_0x0015;
        L_0x033f:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.settingsKeyRow;
            if (r12 != r1) goto L_0x0375;
        L_0x0347:
            r1 = new com.hanista.mobogram.ui.Components.IdenticonDrawable;
            r1.<init>();
            r2 = com.hanista.mobogram.messenger.MessagesController.getInstance();
            r5 = com.hanista.mobogram.ui.ProfileActivity.this;
            r6 = r5.dialog_id;
            r5 = 32;
            r6 = r6 >> r5;
            r5 = (int) r6;
            r5 = java.lang.Integer.valueOf(r5);
            r2 = r2.getEncryptedChat(r5);
            r1.setEncryptedChat(r2);
            r2 = "EncryptionKey";
            r5 = 2131165615; // 0x7f0701af float:1.7945452E38 double:1.052935716E-314;
            r2 = com.hanista.mobogram.messenger.LocaleController.getString(r2, r5);
            r0.setTextAndValueDrawable(r2, r1);
            r0 = r4;
            goto L_0x0015;
        L_0x0375:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.leaveChannelRow;
            if (r12 != r1) goto L_0x03aa;
        L_0x037d:
            r1 = -1229511; // 0xffffffffffed3d39 float:NaN double:NaN;
            r0.setTextColor(r1);
            r1 = "LeaveChannel";
            r2 = 2131165817; // 0x7f070279 float:1.7945862E38 double:1.052935816E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
            r0.setText(r1);
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x06b0;
        L_0x0396:
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aC;
            r2 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.f2491b;
            r5 = 21;
            r2 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.m2276a(r2, r5);
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.m2286c(r1, r2);
            r0.setTextColor(r1);
            r0 = r4;
            goto L_0x0015;
        L_0x03aa:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.convertRow;
            if (r12 != r1) goto L_0x03c5;
        L_0x03b2:
            r1 = "UpgradeGroup";
            r2 = 2131166363; // 0x7f07049b float:1.794697E38 double:1.0529360855E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
            r0.setText(r1);
            r0.setTextColor(r7);
            r0 = r4;
            goto L_0x0015;
        L_0x03c5:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.membersRow;
            if (r12 != r1) goto L_0x0415;
        L_0x03cd:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.info;
            if (r1 == 0) goto L_0x0405;
        L_0x03d5:
            r1 = "ChannelMembers";
            r5 = 2131165435; // 0x7f0700fb float:1.7945087E38 double:1.052935627E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r5);
            r5 = "%d";
            r6 = new java.lang.Object[r4];
            r7 = com.hanista.mobogram.ui.ProfileActivity.this;
            r7 = r7.info;
            r7 = r7.participants_count;
            r7 = java.lang.Integer.valueOf(r7);
            r6[r3] = r7;
            r5 = java.lang.String.format(r5, r6);
            r0.setTextAndValue(r1, r5);
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x06b0;
        L_0x03ff:
            r0.setValueColor(r2);
            r0 = r4;
            goto L_0x0015;
        L_0x0405:
            r1 = "ChannelMembers";
            r2 = 2131165435; // 0x7f0700fb float:1.7945087E38 double:1.052935627E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
            r0.setText(r1);
            r0 = r4;
            goto L_0x0015;
        L_0x0415:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.managementRow;
            if (r12 != r1) goto L_0x0465;
        L_0x041d:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.info;
            if (r1 == 0) goto L_0x0455;
        L_0x0425:
            r1 = "ChannelAdministrators";
            r5 = 2131165408; // 0x7f0700e0 float:1.7945032E38 double:1.0529356137E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r5);
            r5 = "%d";
            r6 = new java.lang.Object[r4];
            r7 = com.hanista.mobogram.ui.ProfileActivity.this;
            r7 = r7.info;
            r7 = r7.admins_count;
            r7 = java.lang.Integer.valueOf(r7);
            r6[r3] = r7;
            r5 = java.lang.String.format(r5, r6);
            r0.setTextAndValue(r1, r5);
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x06b0;
        L_0x044f:
            r0.setValueColor(r2);
            r0 = r4;
            goto L_0x0015;
        L_0x0455:
            r1 = "ChannelAdministrators";
            r2 = 2131165408; // 0x7f0700e0 float:1.7945032E38 double:1.0529356137E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
            r0.setText(r1);
            r0 = r4;
            goto L_0x0015;
        L_0x0465:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.blockedUsersRow;
            if (r12 != r1) goto L_0x04b5;
        L_0x046d:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.info;
            if (r1 == 0) goto L_0x04a5;
        L_0x0475:
            r1 = "ChannelBlockedUsers";
            r5 = 2131165413; // 0x7f0700e5 float:1.7945042E38 double:1.052935616E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r5);
            r5 = "%d";
            r6 = new java.lang.Object[r4];
            r7 = com.hanista.mobogram.ui.ProfileActivity.this;
            r7 = r7.info;
            r7 = r7.kicked_count;
            r7 = java.lang.Integer.valueOf(r7);
            r6[r3] = r7;
            r5 = java.lang.String.format(r5, r6);
            r0.setTextAndValue(r1, r5);
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x06b0;
        L_0x049f:
            r0.setValueColor(r2);
            r0 = r4;
            goto L_0x0015;
        L_0x04a5:
            r1 = "ChannelBlockedUsers";
            r2 = 2131165413; // 0x7f0700e5 float:1.7945042E38 double:1.052935616E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
            r0.setText(r1);
            r0 = r4;
            goto L_0x0015;
        L_0x04b5:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.addMemberRow;
            if (r12 != r1) goto L_0x06b0;
        L_0x04bd:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.chat_id;
            if (r1 <= 0) goto L_0x04d5;
        L_0x04c5:
            r1 = "AddMember";
            r2 = 2131165262; // 0x7f07004e float:1.7944736E38 double:1.0529355416E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
            r0.setText(r1);
            r0 = r4;
            goto L_0x0015;
        L_0x04d5:
            r1 = "AddRecipient";
            r2 = 2131165264; // 0x7f070050 float:1.794474E38 double:1.0529355426E-314;
            r1 = com.hanista.mobogram.messenger.LocaleController.getString(r1, r2);
            r0.setText(r1);
            r0 = r4;
            goto L_0x0015;
        L_0x04e5:
            r0 = r11.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.UserCell) r0;
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.sortedUsers;
            r1 = r1.isEmpty();
            if (r1 != 0) goto L_0x054b;
        L_0x04f5:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.info;
            r1 = r1.participants;
            r2 = r1.participants;
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.sortedUsers;
            r5 = com.hanista.mobogram.ui.ProfileActivity.this;
            r5 = r5.emptyRowChat2;
            r5 = r12 - r5;
            r5 = r5 + -1;
            r1 = r1.get(r5);
            r1 = (java.lang.Integer) r1;
            r1 = r1.intValue();
            r1 = r2.get(r1);
            r1 = (com.hanista.mobogram.tgnet.TLRPC.ChatParticipant) r1;
            r2 = r1;
        L_0x0520:
            if (r2 == 0) goto L_0x06b0;
        L_0x0522:
            r1 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_chatChannelParticipant;
            if (r1 == 0) goto L_0x0588;
        L_0x0526:
            r1 = r2;
            r1 = (com.hanista.mobogram.tgnet.TLRPC.TL_chatChannelParticipant) r1;
            r1 = r1.channelParticipant;
            r5 = com.hanista.mobogram.messenger.MessagesController.getInstance();
            r2 = r2.user_id;
            r2 = java.lang.Integer.valueOf(r2);
            r2 = r5.getUser(r2);
            r5 = r1 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantCreator;
            if (r5 == 0) goto L_0x0567;
        L_0x053d:
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2485a();
            r1 = r1.m2300n();
        L_0x0545:
            r0.setData(r2, r8, r8, r1);
            r0 = r4;
            goto L_0x0015;
        L_0x054b:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.info;
            r1 = r1.participants;
            r1 = r1.participants;
            r2 = com.hanista.mobogram.ui.ProfileActivity.this;
            r2 = r2.emptyRowChat2;
            r2 = r12 - r2;
            r2 = r2 + -1;
            r1 = r1.get(r2);
            r1 = (com.hanista.mobogram.tgnet.TLRPC.ChatParticipant) r1;
            r2 = r1;
            goto L_0x0520;
        L_0x0567:
            r5 = r1 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantEditor;
            if (r5 != 0) goto L_0x056f;
        L_0x056b:
            r1 = r1 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantModerator;
            if (r1 == 0) goto L_0x0578;
        L_0x056f:
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2485a();
            r1 = r1.m2296j();
            goto L_0x0545;
        L_0x0578:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.emptyRowChat2;
            r1 = r1 + 1;
            if (r12 != r1) goto L_0x0586;
        L_0x0582:
            r1 = 2130838118; // 0x7f020266 float:1.728121E38 double:1.052773911E-314;
            goto L_0x0545;
        L_0x0586:
            r1 = r3;
            goto L_0x0545;
        L_0x0588:
            r1 = com.hanista.mobogram.messenger.MessagesController.getInstance();
            r5 = r2.user_id;
            r5 = java.lang.Integer.valueOf(r5);
            r5 = r1.getUser(r5);
            r1 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantCreator;
            if (r1 != 0) goto L_0x05a4;
        L_0x059a:
            r1 = r2.user_id;
            r6 = com.hanista.mobogram.ui.ProfileActivity.this;
            r6 = r6.groupManagerId;
            if (r1 != r6) goto L_0x05b2;
        L_0x05a4:
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2485a();
            r1 = r1.m2300n();
        L_0x05ac:
            r0.setData(r5, r8, r8, r1);
            r0 = r4;
            goto L_0x0015;
        L_0x05b2:
            r1 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantAdmin;
            if (r1 == 0) goto L_0x05bf;
        L_0x05b6:
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2485a();
            r1 = r1.m2296j();
            goto L_0x05ac;
        L_0x05bf:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.emptyRowChat2;
            r1 = r1 + 1;
            if (r12 != r1) goto L_0x05cd;
        L_0x05c9:
            r1 = 2130838118; // 0x7f020266 float:1.728121E38 double:1.052773911E-314;
            goto L_0x05ac;
        L_0x05cd:
            r1 = r3;
            goto L_0x05ac;
        L_0x05cf:
            r0 = r11.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.AboutLinkCell) r0;
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x05e1;
        L_0x05d9:
            r0.setTextColor(r2);
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aH;
            r0.setLinkColor(r1);
        L_0x05e1:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.userInfoRow;
            if (r12 != r1) goto L_0x0600;
        L_0x05e9:
            r1 = com.hanista.mobogram.messenger.MessagesController.getInstance();
            r2 = com.hanista.mobogram.ui.ProfileActivity.this;
            r2 = r2.user_id;
            r1 = r1.getUserAbout(r2);
            r2 = 2130837595; // 0x7f02005b float:1.7280149E38 double:1.0527736526E-314;
            r0.setTextAndIcon(r1, r2);
            r0 = r4;
            goto L_0x0015;
        L_0x0600:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.channelInfoRow;
            if (r12 != r1) goto L_0x06b0;
        L_0x0608:
            r1 = com.hanista.mobogram.ui.ProfileActivity.this;
            r1 = r1.info;
            r1 = r1.about;
        L_0x0610:
            r2 = "\n\n\n";
            r2 = r1.contains(r2);
            if (r2 == 0) goto L_0x0624;
        L_0x0619:
            r2 = "\n\n\n";
            r6 = "\n\n";
            r1 = r1.replace(r2, r6);
            goto L_0x0610;
        L_0x0624:
            r2 = 2130837595; // 0x7f02005b float:1.7280149E38 double:1.0527736526E-314;
            r0.setTextAndIcon(r1, r2);
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r1 == 0) goto L_0x0633;
        L_0x0630:
            r0.setIconColor(r5);
        L_0x0633:
            r0 = r4;
            goto L_0x0015;
        L_0x0636:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.chat_id;
            if (r0 == 0) goto L_0x0060;
        L_0x063e:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.convertRow;
            if (r12 == r0) goto L_0x069e;
        L_0x0646:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.settingsNotificationsRow;
            if (r12 == r0) goto L_0x069e;
        L_0x064e:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.sharedMediaRow;
            if (r12 == r0) goto L_0x069e;
        L_0x0656:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.emptyRowChat2;
            if (r12 <= r0) goto L_0x0666;
        L_0x065e:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.membersEndRow;
            if (r12 < r0) goto L_0x069e;
        L_0x0666:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.addMemberRow;
            if (r12 == r0) goto L_0x069e;
        L_0x066e:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.channelNameRow;
            if (r12 == r0) goto L_0x069e;
        L_0x0676:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.leaveChannelRow;
            if (r12 == r0) goto L_0x069e;
        L_0x067e:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.membersRow;
            if (r12 == r0) goto L_0x069e;
        L_0x0686:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.managementRow;
            if (r12 == r0) goto L_0x069e;
        L_0x068e:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.blockedUsersRow;
            if (r12 == r0) goto L_0x069e;
        L_0x0696:
            r0 = com.hanista.mobogram.ui.ProfileActivity.this;
            r0 = r0.channelInfoRow;
            if (r12 != r0) goto L_0x0060;
        L_0x069e:
            r3 = r4;
            goto L_0x0060;
        L_0x06a1:
            r0 = r11.itemView;
            r0 = r0.getBackground();
            if (r0 == 0) goto L_0x007f;
        L_0x06a9:
            r0 = r11.itemView;
            r0.setBackgroundDrawable(r8);
            goto L_0x007f;
        L_0x06b0:
            r0 = r4;
            goto L_0x0015;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.ui.ProfileActivity.ListAdapter.onBindViewHolder(com.hanista.mobogram.messenger.support.widget.RecyclerView$ViewHolder, int):void");
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            int i2 = AdvanceTheme.aA;
            int i3 = AdvanceTheme.aK;
            int i4 = AdvanceTheme.aB;
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new EmptyCell(this.mContext);
                    break;
                case ProfileActivity.add_contact /*1*/:
                    view = new DividerCell(this.mContext);
                    view.setPadding(AndroidUtilities.dp(72.0f), 0, 0, 0);
                    if (ThemeUtil.m2490b()) {
                        view.setTag("profile_row_color");
                        view.setBackgroundColor(0);
                        if (i3 > 0) {
                            view.setTag("Profile00");
                            break;
                        }
                    }
                    break;
                case ProfileActivity.block_contact /*2*/:
                    view = new C18451(this.mContext);
                    if (ThemeUtil.m2490b()) {
                        ((TextDetailCell) view).setTextColor(i4);
                        ((TextDetailCell) view).setValueColor(AdvanceTheme.aH);
                        view.setBackgroundColor(0);
                        break;
                    }
                    break;
                case ProfileActivity.share_contact /*3*/:
                    view = new C18462(this.mContext);
                    if (ThemeUtil.m2490b()) {
                        view.setBackgroundColor(0);
                        ((TextCell) view).setTextColor(i4);
                        break;
                    }
                    break;
                case ProfileActivity.edit_contact /*4*/:
                    View c18473 = new C18473(this.mContext, 61, 0, true);
                    if (!ThemeUtil.m2490b()) {
                        view = c18473;
                        break;
                    }
                    c18473.setBackgroundColor(0);
                    c18473.setTag("Profile");
                    view = c18473;
                    break;
                case ProfileActivity.delete_contact /*5*/:
                    view = new ShadowSectionCell(this.mContext);
                    if (ThemeUtil.m2490b()) {
                        view = new ShadowSectionCell(this.mContext, false);
                        if (i2 != -1 || i3 > 0) {
                            view.setBackgroundColor(0);
                            break;
                        }
                    }
                    break;
                case Method.TRACE /*6*/:
                    view = new TextInfoPrivacyCell(this.mContext);
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) view;
                    textInfoPrivacyCell.setBackgroundResource(C0338R.drawable.greydivider);
                    Object[] objArr = new Object[ProfileActivity.add_contact];
                    objArr[0] = LocaleController.formatPluralString("Members", MessagesController.getInstance().maxMegagroupCount);
                    textInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ConvertGroupInfo", C0338R.string.ConvertGroupInfo, objArr)));
                    if (ThemeUtil.m2490b() && (i2 != -1 || i3 > 0)) {
                        view.setBackgroundColor(0);
                        break;
                    }
                case ProfileActivity.leave_group /*7*/:
                    view = new LoadingCell(this.mContext);
                    break;
                case ProfileActivity.edit_name /*8*/:
                    view = new AboutLinkCell(this.mContext);
                    if (ThemeUtil.m2490b() && (i2 != -1 || i3 > 0)) {
                        view.setBackgroundColor(0);
                    }
                    ((AboutLinkCell) view).setDelegate(new C18484());
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }
    }

    private class TopView extends View {
        private int currentColor;
        private Paint paint;

        public TopView(Context context) {
            super(context);
            this.paint = new Paint();
        }

        protected void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(91.0f);
            if (ThemeUtil.m2490b()) {
                this.paint.setColor(ProfileActivity.this.topViewColor);
            }
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (ProfileActivity.this.extraHeight + measuredHeight), this.paint);
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, ProfileActivity.this.extraHeight + measuredHeight);
            }
        }

        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), ((ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + AndroidUtilities.dp(91.0f));
        }

        public void setBackgroundColor(int i) {
            if (i != this.currentColor) {
                this.paint.setColor(i);
                invalidate();
            }
        }
    }

    public ProfileActivity(Bundle bundle) {
        super(bundle);
        this.nameTextView = new SimpleTextView[block_contact];
        this.onlineTextView = new SimpleTextView[block_contact];
        this.participantsMap = new HashMap();
        this.allowProfileAnimation = true;
        this.onlineCount = -1;
        this.totalMediaCount = -1;
        this.totalMediaCountMerge = -1;
        this.rowCount = 0;
    }

    private void checkListViewScroll() {
        boolean z = false;
        if (this.listView.getChildCount() > 0 && !this.openAnimationInProgress) {
            View childAt = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
            int top = childAt.getTop();
            int i = (top < 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
            if (this.extraHeight != i) {
                this.extraHeight = i;
                this.topView.invalidate();
                if (this.playProfileAnimation) {
                    if (this.extraHeight != 0) {
                        z = true;
                    }
                    this.allowProfileAnimation = z;
                }
                needLayout();
            }
        }
    }

    private void createActionBarMenu() {
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.clearItems();
        this.animatingItem = null;
        Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_other);
        if (ThemeUtil.m2490b()) {
            drawable.setColorFilter(AdvanceTheme.aD, Mode.MULTIPLY);
        }
        ActionBarMenuItem actionBarMenuItem = null;
        if (this.user_id != 0) {
            if (UserConfig.getClientUserId() == this.user_id) {
                actionBarMenuItem = createMenu.addItem((int) share, (int) C0338R.drawable.ic_ab_other);
                actionBarMenuItem.addSubItem(share_contact, LocaleController.getString("ShareContact", C0338R.string.ShareContact), 0);
            } else if (ContactsController.getInstance().contactsDict.get(this.user_id) == null) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
                if (user != null) {
                    actionBarMenuItem = ThemeUtil.m2490b() ? createMenu.addItem((int) share, drawable) : createMenu.addItem((int) share, (int) C0338R.drawable.ic_ab_other);
                    if (user.bot) {
                        if (!user.bot_nochats) {
                            actionBarMenuItem.addSubItem(invite_to_group, LocaleController.getString("BotInvite", C0338R.string.BotInvite), 0);
                        }
                        actionBarMenuItem.addSubItem(share, LocaleController.getString("BotShare", C0338R.string.BotShare), 0);
                    }
                    if (user.phone != null && user.phone.length() != 0) {
                        actionBarMenuItem.addSubItem(add_contact, LocaleController.getString("AddContact", C0338R.string.AddContact), 0);
                        actionBarMenuItem.addSubItem(share_contact, LocaleController.getString("ShareContact", C0338R.string.ShareContact), 0);
                        actionBarMenuItem.addSubItem(block_contact, !this.userBlocked ? LocaleController.getString("BlockContact", C0338R.string.BlockContact) : LocaleController.getString("Unblock", C0338R.string.Unblock), 0);
                    } else if (user.bot) {
                        actionBarMenuItem.addSubItem(block_contact, !this.userBlocked ? LocaleController.getString("BotStop", C0338R.string.BotStop) : LocaleController.getString("BotRestart", C0338R.string.BotRestart), 0);
                    } else {
                        actionBarMenuItem.addSubItem(block_contact, !this.userBlocked ? LocaleController.getString("BlockContact", C0338R.string.BlockContact) : LocaleController.getString("Unblock", C0338R.string.Unblock), 0);
                    }
                    actionBarMenuItem.addSubItem(invite_to_group, LocaleController.getString("AddToGroupOrChannel", C0338R.string.AddToGroupOrChannel), 0);
                    if (!SpecificContactBiz.f2058a.containsKey(Integer.valueOf(this.user_id))) {
                        actionBarMenuItem.addSubItem(add_to_specific_contacts, LocaleController.getString("AddToSpecificContacts", C0338R.string.AddToSpecificContacts), 0);
                    }
                } else {
                    return;
                }
            } else {
                actionBarMenuItem = ThemeUtil.m2490b() ? createMenu.addItem((int) share, drawable) : createMenu.addItem((int) share, (int) C0338R.drawable.ic_ab_other);
                actionBarMenuItem.addSubItem(share_contact, LocaleController.getString("ShareContact", C0338R.string.ShareContact), 0);
                actionBarMenuItem.addSubItem(block_contact, !this.userBlocked ? LocaleController.getString("BlockContact", C0338R.string.BlockContact) : LocaleController.getString("Unblock", C0338R.string.Unblock), 0);
                actionBarMenuItem.addSubItem(edit_contact, LocaleController.getString("EditContact", C0338R.string.EditContact), 0);
                actionBarMenuItem.addSubItem(delete_contact, LocaleController.getString("DeleteContact", C0338R.string.DeleteContact), 0);
                actionBarMenuItem.addSubItem(invite_to_group, LocaleController.getString("AddToGroupOrChannel", C0338R.string.AddToGroupOrChannel), 0);
                if (!SpecificContactBiz.f2058a.containsKey(Integer.valueOf(this.user_id))) {
                    actionBarMenuItem.addSubItem(add_to_specific_contacts, LocaleController.getString("AddToSpecificContacts", C0338R.string.AddToSpecificContacts), 0);
                }
            }
        } else if (this.chat_id != 0) {
            if (this.chat_id > 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                if (this.writeButton != null) {
                    boolean isChannel = ChatObject.isChannel(this.currentChat);
                    if ((!isChannel || this.currentChat.creator || (this.currentChat.megagroup && this.currentChat.editor)) && (isChannel || this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                        this.writeButton.setImageResource(C0338R.drawable.floating_camera);
                        this.writeButton.setPadding(0, 0, 0, 0);
                    } else {
                        this.writeButton.setImageResource(C0338R.drawable.floating_message);
                        this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                    }
                    if (ThemeUtil.m2490b()) {
                        this.writeButton.setColorFilter(AdvanceTheme.aF, Mode.SRC_IN);
                    }
                }
                if (ChatObject.isChannel(chat)) {
                    if (chat.creator || (chat.megagroup && chat.editor)) {
                        actionBarMenuItem = ThemeUtil.m2490b() ? createMenu.addItem((int) share, drawable) : createMenu.addItem((int) share, (int) C0338R.drawable.ic_ab_other);
                        actionBarMenuItem.addSubItem(edit_channel, LocaleController.getString("ChannelEdit", C0338R.string.ChannelEdit), 0);
                    }
                    if (!(chat.creator || chat.left || chat.kicked || !chat.megagroup)) {
                        if (actionBarMenuItem == null) {
                            actionBarMenuItem = ThemeUtil.m2490b() ? createMenu.addItem((int) share, drawable) : createMenu.addItem((int) share, (int) C0338R.drawable.ic_ab_other);
                        }
                        actionBarMenuItem.addSubItem(leave_group, LocaleController.getString("LeaveMegaMenu", C0338R.string.LeaveMegaMenu), 0);
                    }
                    if (!(chat.left || chat.kicked || !chat.megagroup)) {
                        actionBarMenuItem.addSubItem(search_members, LocaleController.getString("SearchMembers", C0338R.string.SearchMembers), 0);
                        this.hasSearchMemnbersItem = true;
                    }
                } else {
                    actionBarMenuItem = ThemeUtil.m2490b() ? createMenu.addItem((int) share, drawable) : createMenu.addItem((int) share, (int) C0338R.drawable.ic_ab_other);
                    if (chat.creator && this.chat_id > 0) {
                        actionBarMenuItem.addSubItem(set_admins, LocaleController.getString("SetAdmins", C0338R.string.SetAdmins), 0);
                    }
                    if (!chat.admins_enabled || chat.creator || chat.admin) {
                        actionBarMenuItem.addSubItem(edit_name, LocaleController.getString("EditName", C0338R.string.EditName), 0);
                    }
                    if (chat.creator && (this.info == null || this.info.participants.participants.size() > add_contact)) {
                        actionBarMenuItem.addSubItem(convert_to_supergroup, LocaleController.getString("ConvertGroupMenu", C0338R.string.ConvertGroupMenu), 0);
                    }
                    actionBarMenuItem.addSubItem(leave_group, LocaleController.getString("DeleteAndExit", C0338R.string.DeleteAndExit), 0);
                    if (!(ChatObject.isChannel(this.chat_id) || chat.megagroup)) {
                        actionBarMenuItem.addSubItem(leave_without_delete, LocaleController.getString("LeaveWithoutDelete", C0338R.string.LeaveWithoutDelete), 0);
                    }
                    actionBarMenuItem.addSubItem(search_members, LocaleController.getString("SearchMembers", C0338R.string.SearchMembers), 0);
                    this.hasSearchMemnbersItem = true;
                }
            } else {
                actionBarMenuItem = ThemeUtil.m2490b() ? createMenu.addItem((int) share, drawable) : createMenu.addItem((int) share, (int) C0338R.drawable.ic_ab_other);
                actionBarMenuItem.addSubItem(edit_name, LocaleController.getString("EditName", C0338R.string.EditName), 0);
            }
        }
        if (actionBarMenuItem == null) {
            actionBarMenuItem = ThemeUtil.m2490b() ? createMenu.addItem((int) share, drawable) : createMenu.addItem((int) share, (int) C0338R.drawable.ic_ab_other);
        }
        actionBarMenuItem.addSubItem(add_shortcut, LocaleController.getString("AddShortcut", C0338R.string.AddShortcut), 0);
    }

    private void createUserAvatarMenu() {
        Builder builder = new Builder(getParentActivity());
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(LocaleController.getString("ShowThisUserMessages", C0338R.string.ShowThisUserMessages));
        arrayList2.add(Integer.valueOf(0));
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new AnonymousClass29(arrayList2));
        builder.setTitle(LocaleController.getString("User", C0338R.string.User));
        showDialog(builder.create());
    }

    private void fetchUsersFromChannelInfo() {
        if ((this.info instanceof TL_channelFull) && this.info.participants != null) {
            for (int i = 0; i < this.info.participants.participants.size(); i += add_contact) {
                ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(i);
                this.participantsMap.put(Integer.valueOf(chatParticipant.user_id), chatParticipant);
                if (((TL_chatChannelParticipant) chatParticipant).channelParticipant instanceof TL_channelParticipantCreator) {
                    this.creatorID = chatParticipant.user_id;
                }
            }
        }
    }

    private void findGroupManager() {
        if (this.info != null && this.info.participants != null) {
            Iterator it = this.info.participants.participants.iterator();
            int i = Integer.MAX_VALUE;
            while (it.hasNext()) {
                ChatParticipant chatParticipant = (ChatParticipant) it.next();
                if (chatParticipant instanceof TL_chatParticipantCreator) {
                    this.groupManagerId = chatParticipant.user_id;
                    return;
                }
                int i2;
                if (chatParticipant.inviter_id != chatParticipant.user_id || chatParticipant.date >= i) {
                    i2 = i;
                } else {
                    this.groupManagerId = chatParticipant.user_id;
                    i2 = chatParticipant.date;
                }
                i = i2;
            }
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ProfileActivity.this.fragmentView != null) {
                        ProfileActivity.this.checkListViewScroll();
                        ProfileActivity.this.needLayout();
                        ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private void getChannelParticipants(boolean z) {
        int i = 0;
        if (!this.loadingUsers && this.participantsMap != null && this.info != null) {
            this.loadingUsers = true;
            int i2 = (this.participantsMap.isEmpty() || !z) ? 0 : 300;
            TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
            tL_channels_getParticipants.channel = MessagesController.getInputChannel(this.chat_id);
            tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
            if (!z) {
                i = this.participantsMap.size();
            }
            tL_channels_getParticipants.offset = i;
            tL_channels_getParticipants.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_channels_getParticipants, new AnonymousClass19(tL_channels_getParticipants, i2)), this.classGuid);
        }
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            this.avatarImage.setRoundRadius(AndroidUtilities.dp((float) AdvanceTheme.aE));
            int i = AdvanceTheme.aJ;
            int i2 = AdvanceTheme.az;
            int i3 = 0;
            while (i3 < block_contact) {
                if (this.playProfileAnimation || i3 != 0) {
                    this.nameTextView[i3].setTextColor(AdvanceTheme.m2286c(AdvanceTheme.ax, -1));
                    this.nameTextView[i3].setTextSize(AdvanceTheme.ay);
                    this.onlineTextView[i3].setTextColor(i);
                    this.onlineTextView[i3].setTextSize(i2);
                }
                i3 += add_contact;
            }
            this.creatorTextView.setTextColor(i);
            SimpleTextView simpleTextView = this.creatorTextView;
            if (i2 >= add_shortcut) {
                i2 = add_shortcut;
            }
            simpleTextView.setTextSize(i2);
            if (this.user_id != 0 || (this.chat_id >= 0 && !ChatObject.isLeftFromChat(this.currentChat))) {
                try {
                    this.writeButton.getBackground().setColorFilter(AdvanceTheme.aA, Mode.SRC_IN);
                    this.writeButton.setColorFilter(AdvanceTheme.aF, Mode.SRC_IN);
                } catch (Throwable th) {
                    FileLog.m18e("tmessages", th);
                }
            }
        }
    }

    private void initThemeBackButton(ActionBar actionBar) {
        if (ThemeUtil.m2490b()) {
            Drawable backDrawable = new BackDrawable(false);
            ((BackDrawable) backDrawable).setColor(AdvanceTheme.aD);
            actionBar.setBackButtonDrawable(backDrawable);
        }
    }

    private void kickUser(int i) {
        int i2 = 0;
        int i3;
        if (i != 0) {
            MessagesController.getInstance().deleteUserFromChat(this.chat_id, MessagesController.getInstance().getUser(Integer.valueOf(i)), this.info);
            if (this.currentChat.megagroup && this.info != null && this.info.participants != null) {
                for (i3 = 0; i3 < this.info.participants.participants.size(); i3 += add_contact) {
                    if (((TL_chatChannelParticipant) this.info.participants.participants.get(i3)).channelParticipant.user_id == i) {
                        if (this.info != null) {
                            ChatFull chatFull = this.info;
                            chatFull.participants_count--;
                        }
                        this.info.participants.participants.remove(i3);
                        i3 = add_contact;
                        if (!(this.info == null || this.info.participants == null)) {
                            while (i2 < this.info.participants.participants.size()) {
                                if (((ChatParticipant) this.info.participants.participants.get(i2)).user_id != i) {
                                    this.info.participants.participants.remove(i2);
                                    i3 = add_contact;
                                    break;
                                }
                                i2 += add_contact;
                            }
                        }
                        if (i3 == 0) {
                            updateOnlineCount();
                            updateRowsIds();
                            this.listAdapter.notifyDataSetChanged();
                            return;
                        }
                        return;
                    }
                }
                i3 = 0;
                while (i2 < this.info.participants.participants.size()) {
                    if (((ChatParticipant) this.info.participants.participants.get(i2)).user_id != i) {
                        i2 += add_contact;
                    } else {
                        this.info.participants.participants.remove(i2);
                        i3 = add_contact;
                        break;
                        if (i3 == 0) {
                            updateOnlineCount();
                            updateRowsIds();
                            this.listAdapter.notifyDataSetChanged();
                            return;
                        }
                        return;
                    }
                }
                if (i3 == 0) {
                    updateOnlineCount();
                    updateRowsIds();
                    this.listAdapter.notifyDataSetChanged();
                    return;
                }
                return;
            }
            return;
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter instance = NotificationCenter.getInstance();
            i3 = NotificationCenter.closeChats;
            Object[] objArr = new Object[add_contact];
            objArr[0] = Long.valueOf(-((long) this.chat_id));
            instance.postNotificationName(i3, objArr);
        } else {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance().deleteUserFromChat(this.chat_id, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), this.info);
        this.playProfileAnimation = false;
        finishFragment();
    }

    private void leaveChatPressed() {
        Builder builder = new Builder(getParentActivity());
        if (!ChatObject.isChannel(this.chat_id) || this.currentChat.megagroup) {
            builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0338R.string.AreYouSureDeleteAndExit));
        } else {
            builder.setMessage(ChatObject.isChannel(this.chat_id) ? LocaleController.getString("ChannelLeaveAlert", C0338R.string.ChannelLeaveAlert) : LocaleController.getString("AreYouSureDeleteAndExit", C0338R.string.AreYouSureDeleteAndExit));
        }
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.kickUser(0);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void leaveWitoutDeletePressed() {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        builder.setMessage(LocaleController.getString("AreYouSureLeaveWithoutDelete", C0338R.string.AreYouSureLeaveWithoutDelete));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance().deleteUserFromChat(ProfileActivity.this.chat_id, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), ProfileActivity.this.info, false);
                ProfileActivity.this.playProfileAnimation = false;
                if (ProfileActivity.this.parentLayout != null && ProfileActivity.this.parentLayout.fragmentsStack.size() > ProfileActivity.add_contact) {
                    try {
                        ProfileActivity.this.parentLayout.fragmentsStack.remove(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                    } catch (Exception e) {
                    }
                }
                ProfileActivity.this.finishFragment();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void needLayout() {
        FrameLayout.LayoutParams layoutParams;
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        if (!(this.listView == null || this.openAnimationInProgress)) {
            layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarImage != null) {
            float dp = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.listView.setTopGlowOffset(this.extraHeight);
            if (this.writeButton != null) {
                this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    int i = dp > DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD ? add_contact : 0;
                    if (i != (this.writeButton.getTag() == null ? add_contact : 0)) {
                        if (i != 0) {
                            this.writeButton.setTag(null);
                        } else {
                            this.writeButton.setTag(Integer.valueOf(0));
                        }
                        if (this.writeButtonAnimation != null) {
                            AnimatorSet animatorSet = this.writeButtonAnimation;
                            this.writeButtonAnimation = null;
                            animatorSet.cancel();
                        }
                        this.writeButtonAnimation = new AnimatorSet();
                        AnimatorSet animatorSet2;
                        Animator[] animatorArr;
                        float[] fArr;
                        float[] fArr2;
                        if (i != 0) {
                            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                            animatorSet2 = this.writeButtonAnimation;
                            animatorArr = new Animator[share_contact];
                            fArr = new float[add_contact];
                            fArr[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                            animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr);
                            fArr = new float[add_contact];
                            fArr[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                            animatorArr[add_contact] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr);
                            fArr2 = new float[add_contact];
                            fArr2[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                            animatorArr[block_contact] = ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr2);
                            animatorSet2.playTogether(animatorArr);
                        } else {
                            this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                            animatorSet2 = this.writeButtonAnimation;
                            animatorArr = new Animator[share_contact];
                            fArr = new float[add_contact];
                            fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                            animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr);
                            fArr = new float[add_contact];
                            fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                            animatorArr[add_contact] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr);
                            fArr2 = new float[add_contact];
                            fArr2[0] = 0.0f;
                            animatorArr[block_contact] = ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr2);
                            animatorSet2.playTogether(animatorArr);
                        }
                        this.writeButtonAnimation.setDuration(150);
                        this.writeButtonAnimation.addListener(new AnimatorListenerAdapterProxy() {
                            public void onAnimationEnd(Animator animator) {
                                if (ProfileActivity.this.writeButtonAnimation != null && ProfileActivity.this.writeButtonAnimation.equals(animator)) {
                                    ProfileActivity.this.writeButtonAnimation = null;
                                }
                            }
                        });
                        this.writeButtonAnimation.start();
                    }
                }
            }
            float currentActionBarHeight2 = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT + dp))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * dp);
            this.avatarImage.setScaleX(((18.0f * dp) + 42.0f) / 42.0f);
            this.avatarImage.setScaleY(((18.0f * dp) + 42.0f) / 42.0f);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * dp);
            this.avatarImage.setTranslationY((float) Math.ceil((double) currentActionBarHeight2));
            for (currentActionBarHeight = 0; currentActionBarHeight < block_contact; currentActionBarHeight += add_contact) {
                if (this.nameTextView[currentActionBarHeight] != null) {
                    this.nameTextView[currentActionBarHeight].setTranslationX((AndroidUtilities.density * -21.0f) * dp);
                    this.nameTextView[currentActionBarHeight].setTranslationY((((float) Math.floor((double) currentActionBarHeight2)) + ((float) AndroidUtilities.dp(1.3f))) + (((float) AndroidUtilities.dp(7.0f)) * dp));
                    this.onlineTextView[currentActionBarHeight].setTranslationX((AndroidUtilities.density * -21.0f) * dp);
                    this.onlineTextView[currentActionBarHeight].setTranslationY((((float) Math.floor((double) currentActionBarHeight2)) + ((float) AndroidUtilities.dp(24.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * dp));
                    this.creatorTextView.setTranslationX((AndroidUtilities.density * -21.0f) * dp);
                    if (ThemeUtil.m2490b()) {
                        this.creatorTextView.setTranslationY((((float) AndroidUtilities.dp((float) (((AdvanceTheme.ay - 18) + (AdvanceTheme.az - 14)) + 32))) + ((float) Math.floor((double) currentActionBarHeight2))) + (((float) Math.floor((double) (22.0f * AndroidUtilities.density))) * dp));
                    } else {
                        this.creatorTextView.setTranslationY((((float) Math.floor((double) currentActionBarHeight2)) + ((float) AndroidUtilities.dp(32.0f))) + (((float) Math.floor((double) (22.0f * AndroidUtilities.density))) * dp));
                    }
                    this.nameTextView[currentActionBarHeight].setScaleX((0.12f * dp) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    this.nameTextView[currentActionBarHeight].setScaleY((0.12f * dp) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    if (currentActionBarHeight == add_contact && !this.openAnimationInProgress) {
                        int dp2 = (int) (((float) ((AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x) - AndroidUtilities.dp(126.0f + (40.0f * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - dp))))) - this.nameTextView[currentActionBarHeight].getTranslationX());
                        layoutParams = (FrameLayout.LayoutParams) this.nameTextView[currentActionBarHeight].getLayoutParams();
                        if (((float) dp2) < ((float) this.nameTextView[currentActionBarHeight].getSideDrawablesSize()) + (this.nameTextView[currentActionBarHeight].getPaint().measureText(this.nameTextView[currentActionBarHeight].getText().toString()) * this.nameTextView[currentActionBarHeight].getScaleX())) {
                            layoutParams.width = (int) Math.ceil((double) (((float) dp2) / this.nameTextView[currentActionBarHeight].getScaleX()));
                        } else {
                            layoutParams.width = -2;
                        }
                        this.nameTextView[currentActionBarHeight].setLayoutParams(layoutParams);
                        layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[currentActionBarHeight].getLayoutParams();
                        layoutParams.rightMargin = (int) Math.ceil((double) ((this.onlineTextView[currentActionBarHeight].getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - dp))));
                        this.onlineTextView[currentActionBarHeight].setLayoutParams(layoutParams);
                        layoutParams = (FrameLayout.LayoutParams) this.creatorTextView.getLayoutParams();
                        layoutParams.rightMargin = (int) Math.ceil((double) ((this.creatorTextView.getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - dp))));
                        this.creatorTextView.setLayoutParams(layoutParams);
                    }
                }
            }
            if (((double) dp) > 0.85d) {
                this.creatorTextView.setVisibility(0);
            } else {
                this.creatorTextView.setVisibility(edit_name);
            }
        }
        updateActionBarBG();
    }

    private void openAddMember() {
        int i = 0;
        Bundle bundle = new Bundle();
        bundle.putBoolean("onlyUsers", true);
        bundle.putBoolean("destroyAfterSelect", true);
        bundle.putBoolean("returnAsResult", true);
        bundle.putBoolean("needForwardCount", !ChatObject.isChannel(this.currentChat));
        if (this.chat_id > 0) {
            if (this.currentChat.creator) {
                bundle.putInt("chat_id", this.currentChat.id);
            }
            bundle.putString("selectAlertString", LocaleController.getString("AddToTheGroup", C0338R.string.AddToTheGroup));
        }
        BaseFragment contactsActivity = new ContactsActivity(bundle);
        bundle.putBoolean("multiSelectMode", true);
        contactsActivity.setMultiSelectDelegate(new ContactsActivityMultiSelectDelegate() {
            public void didSelectContacts(List<Integer> list, String str) {
                for (Integer user : list) {
                    MessagesController.getInstance().addUserToChat(ProfileActivity.this.chat_id, MessagesController.getInstance().getUser(user), ProfileActivity.this.info, str != null ? Utilities.parseInt(str).intValue() : 0, null, ProfileActivity.this);
                }
            }
        });
        if (!(this.info == null || this.info.participants == null)) {
            HashMap hashMap = new HashMap();
            while (i < this.info.participants.participants.size()) {
                hashMap.put(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(i)).user_id), null);
                i += add_contact;
            }
            contactsActivity.setIgnoreUsers(hashMap);
        }
        presentFragment(contactsActivity);
    }

    private boolean processOnClickOrPress(int i) {
        User user;
        Builder builder;
        CharSequence[] charSequenceArr;
        if (i == this.usernameRow) {
            user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            if (user == null || user.username == null) {
                return false;
            }
            builder = new Builder(getParentActivity());
            charSequenceArr = new CharSequence[add_contact];
            charSequenceArr[0] = LocaleController.getString("Copy", C0338R.string.Copy);
            builder.setItems(charSequenceArr, new AnonymousClass15(user));
            showDialog(builder.create());
            return true;
        } else if (i == this.phoneRow) {
            user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            if (user == null || user.phone == null || user.phone.length() == 0 || getParentActivity() == null) {
                return false;
            }
            builder = new Builder(getParentActivity());
            charSequenceArr = new CharSequence[block_contact];
            charSequenceArr[0] = LocaleController.getString("Call", C0338R.string.Call);
            charSequenceArr[add_contact] = LocaleController.getString("Copy", C0338R.string.Copy);
            builder.setItems(charSequenceArr, new AnonymousClass16(user));
            showDialog(builder.create());
            return true;
        } else if (i != this.channelInfoRow && i != this.userInfoRow) {
            return false;
        } else {
            Builder builder2 = new Builder(getParentActivity());
            CharSequence[] charSequenceArr2 = new CharSequence[add_contact];
            charSequenceArr2[0] = LocaleController.getString("Copy", C0338R.string.Copy);
            builder2.setItems(charSequenceArr2, new AnonymousClass17(i));
            showDialog(builder2.create());
            return true;
        }
    }

    private void showMaterialHelp() {
        try {
            MaterialHelperUtil.m1360a(getParentActivity(), this.hasSearchMemnbersItem ? this.actionBar.createMenu().getItem(share) : null);
        } catch (Exception e) {
        }
    }

    private void showMegaGroupManagerFinderHelp() {
        if (this.currentChat != null && ChatObject.isChannel(this.chat_id) && this.currentChat.megagroup) {
            SettingManager settingManager = new SettingManager();
            if (!settingManager.m944b("megaManagerFinderHelpDisplayed")) {
                settingManager.m943a("megaManagerFinderHelpDisplayed", true);
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("MegaGroupManagerFinderHelp", C0338R.string.MegaGroupManagerFinderHelp));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        }
    }

    private void showUserMessages(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chat_id);
        bundle.putInt("just_from_id", i);
        presentFragment(new ChatActivity(bundle));
    }

    private void updateActionBarBG() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.aM;
            this.actionBar.setBackgroundColor(i);
            this.listView.setGlowColor(i);
            this.topViewColor = i;
            int i2 = AdvanceTheme.aN;
            if (i2 > 0) {
                Orientation orientation;
                this.topViewColor = 0;
                int i3 = AdvanceTheme.aO;
                switch (i2) {
                    case block_contact /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case share_contact /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case edit_contact /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        this.topViewColor = i3;
                        break;
                }
                int[] iArr = new int[block_contact];
                iArr[0] = i;
                iArr[add_contact] = i3;
                this.actionBar.setBackgroundDrawable(new GradientDrawable(orientation, iArr));
            }
            this.topView.setBackgroundColor(this.topViewColor);
        }
    }

    private void updateListBG() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.aA;
            int i2 = AdvanceTheme.aK;
            if (i2 > 0) {
                Orientation orientation;
                switch (i2) {
                    case block_contact /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case share_contact /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case edit_contact /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i3 = AdvanceTheme.aL;
                int[] iArr = new int[block_contact];
                iArr[0] = i;
                iArr[add_contact] = i3;
                this.listView.setBackgroundDrawable(new GradientDrawable(orientation, iArr));
                return;
            }
            this.listView.setBackgroundColor(i);
        }
    }

    private void updateOnlineCount() {
        this.onlineCount = 0;
        int currentTime = ConnectionsManager.getInstance().getCurrentTime();
        this.sortedUsers.clear();
        if ((this.info instanceof TL_chatFull) || ((this.info instanceof TL_channelFull) && this.info.participants_count <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && this.info.participants != null)) {
            for (int i = 0; i < this.info.participants.participants.size(); i += add_contact) {
                ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(i);
                User user = MessagesController.getInstance().getUser(Integer.valueOf(chatParticipant.user_id));
                if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getClientUserId()) || user.status.expires <= AdaptiveEvaluator.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS))) {
                    this.onlineCount += add_contact;
                }
                this.sortedUsers.add(Integer.valueOf(i));
                if (chatParticipant instanceof TL_chatParticipantCreator) {
                    this.creatorID = chatParticipant.user_id;
                }
            }
            try {
                Collections.sort(this.sortedUsers, new Comparator<Integer>() {
                    public int compare(Integer num, Integer num2) {
                        int i;
                        int i2;
                        User user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) ProfileActivity.this.info.participants.participants.get(num2.intValue())).user_id));
                        User user2 = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) ProfileActivity.this.info.participants.participants.get(num.intValue())).user_id));
                        if (user == null || user.status == null) {
                            i = 0;
                        } else {
                            i = user.id == ProfileActivity.this.creatorID ? (ConnectionsManager.getInstance().getCurrentTime() + 50000) - 100 : user.id == UserConfig.getClientUserId() ? ConnectionsManager.getInstance().getCurrentTime() + 50000 : user.status.expires;
                        }
                        if (user2 == null || user2.status == null) {
                            i2 = 0;
                        } else {
                            i2 = user2.id == UserConfig.getClientUserId() ? ConnectionsManager.getInstance().getCurrentTime() + 50000 : user2.status.expires;
                            if (user2.id == ProfileActivity.this.creatorID) {
                                i2 = (ConnectionsManager.getInstance().getCurrentTime() + 50000) - 100;
                            }
                        }
                        return (i <= 0 || i2 <= 0) ? (i >= 0 || i2 >= 0) ? ((i >= 0 || i2 <= 0) && (i != 0 || i2 == 0)) ? ((i2 >= 0 || i <= 0) && (i2 != 0 || i == 0)) ? 0 : ProfileActivity.add_contact : -1 : i > i2 ? ProfileActivity.add_contact : i < i2 ? -1 : 0 : i > i2 ? ProfileActivity.add_contact : i < i2 ? -1 : 0;
                    }
                });
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (this.listAdapter != null) {
                this.listAdapter.notifyItemRangeChanged(this.emptyRowChat2 + add_contact, this.sortedUsers.size());
            }
        }
    }

    private void updateProfileData() {
        FileLocation fileLocation = null;
        boolean z = true;
        if (this.avatarImage != null && this.nameTextView != null) {
            CharSequence format;
            int i;
            int i2;
            if (this.user_id != 0) {
                TLObject tLObject;
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
                if (user.photo != null) {
                    tLObject = user.photo.photo_small;
                    fileLocation = user.photo.photo_big;
                } else {
                    tLObject = null;
                }
                this.avatarDrawable.setInfo(user);
                this.avatarImage.setImage(tLObject, "50_50", this.avatarDrawable);
                CharSequence userName = UserObject.getUserName(user);
                CharSequence string = (user.id == 333000 || user.id == 777000) ? LocaleController.getString("ServiceNotifications", C0338R.string.ServiceNotifications) : user.bot ? LocaleController.getString("Bot", C0338R.string.Bot) : LocaleController.formatUserStatus(user);
                for (int i3 = 0; i3 < block_contact; i3 += add_contact) {
                    if (this.nameTextView[i3] != null) {
                        if (i3 == 0 && user.id != UserConfig.getClientUserId() && user.id / PointerIconCompat.TYPE_DEFAULT != 777 && user.id / PointerIconCompat.TYPE_DEFAULT != 333 && user.phone != null && user.phone.length() != 0 && ContactsController.getInstance().contactsDict.get(user.id) == null && (ContactsController.getInstance().contactsDict.size() != 0 || !ContactsController.getInstance().isLoadingContacts())) {
                            format = PhoneFormat.getInstance().format("+" + user.phone);
                            if (!this.nameTextView[i3].getText().equals(format)) {
                                this.nameTextView[i3].setText(format);
                            }
                        } else if (!this.nameTextView[i3].getText().equals(userName)) {
                            this.nameTextView[i3].setText(userName);
                        }
                        if (!this.onlineTextView[i3].getText().equals(string)) {
                            this.onlineTextView[i3].setText(string);
                        }
                        i = this.currentEncryptedChat != null ? C0338R.drawable.ic_lock_header : 0;
                        if (i3 == 0) {
                            i2 = MessagesController.getInstance().isDialogMuted((this.dialog_id > 0 ? 1 : (this.dialog_id == 0 ? 0 : -1)) != 0 ? this.dialog_id : (long) this.user_id) ? C0338R.drawable.mute_fixed : 0;
                        } else {
                            i2 = user.verified ? C0338R.drawable.check_profile_fixed : 0;
                        }
                        this.nameTextView[i3].setLeftDrawable(i);
                        this.nameTextView[i3].setRightDrawable(i2);
                    }
                }
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.getInstance().isShowingImage(fileLocation), false);
            } else if (this.chat_id != 0) {
                TLObject tLObject2;
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                if (chat != null) {
                    this.currentChat = chat;
                } else {
                    chat = this.currentChat;
                }
                if (!ChatObject.isChannel(chat)) {
                    i2 = chat.participants_count;
                    if (this.info != null) {
                        i2 = this.info.participants.participants.size();
                    }
                    if (i2 == 0 || this.onlineCount <= add_contact) {
                        format = LocaleController.formatPluralString("Members", i2);
                    } else {
                        Object[] objArr = new Object[block_contact];
                        objArr[0] = LocaleController.formatPluralString("Members", i2);
                        objArr[add_contact] = LocaleController.formatPluralString("Online", this.onlineCount);
                        format = String.format("%s, %s", objArr);
                    }
                } else if (this.info == null || (!this.currentChat.megagroup && (this.info.participants_count == 0 || this.currentChat.admin || this.info.can_view_participants))) {
                    format = this.currentChat.megagroup ? LocaleController.getString("Loading", C0338R.string.Loading).toLowerCase() : (chat.flags & 64) != 0 ? LocaleController.getString("ChannelPublic", C0338R.string.ChannelPublic).toLowerCase() : LocaleController.getString("ChannelPrivate", C0338R.string.ChannelPrivate).toLowerCase();
                } else if (!this.currentChat.megagroup || this.info.participants_count > Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                    int[] iArr = new int[add_contact];
                    CharSequence formatShortNumber = LocaleController.formatShortNumber(this.info.participants_count, iArr);
                    String formatPluralString = LocaleController.formatPluralString("Members", iArr[0]);
                    Object[] objArr2 = new Object[add_contact];
                    objArr2[0] = Integer.valueOf(iArr[0]);
                    format = formatPluralString.replace(String.format("%d", objArr2), formatShortNumber);
                } else if (this.onlineCount <= add_contact || this.info.participants_count == 0) {
                    format = LocaleController.formatPluralString("Members", this.info.participants_count);
                } else {
                    Object[] objArr3 = new Object[block_contact];
                    objArr3[0] = LocaleController.formatPluralString("Members", this.info.participants_count);
                    objArr3[add_contact] = LocaleController.formatPluralString("Online", this.onlineCount);
                    format = String.format("%s, %s", objArr3);
                }
                if (this.creatorID != 0) {
                    this.creatorTextView.setText(LocaleController.getString("ChannelCreator", C0338R.string.ChannelCreator) + ": " + UserObject.getUserName(MessagesController.getInstance().getUser(Integer.valueOf(this.creatorID))));
                }
                i = 0;
                while (i < block_contact) {
                    if (this.nameTextView[i] != null) {
                        if (!(chat.title == null || this.nameTextView[i].getText().equals(chat.title))) {
                            this.nameTextView[i].setText(chat.title);
                        }
                        this.nameTextView[i].setLeftDrawable(null);
                        if (i == 0) {
                            this.nameTextView[i].setRightDrawable(MessagesController.getInstance().isDialogMuted((long) (-this.chat_id)) ? C0338R.drawable.mute_fixed : 0);
                        } else if (chat.verified || chat.id == 1009604744) {
                            this.nameTextView[i].setRightDrawable((int) C0338R.drawable.check_profile_fixed);
                        } else {
                            this.nameTextView[i].setRightDrawable(null);
                        }
                        if (!this.currentChat.megagroup || this.info == null || this.info.participants_count > Callback.DEFAULT_DRAG_ANIMATION_DURATION || this.onlineCount <= 0) {
                            if (i == 0 && ChatObject.isChannel(this.currentChat) && this.info != null && this.info.participants_count != 0 && (this.currentChat.megagroup || this.currentChat.broadcast)) {
                                int[] iArr2 = new int[add_contact];
                                CharSequence formatShortNumber2 = LocaleController.formatShortNumber(this.info.participants_count, iArr2);
                                SimpleTextView simpleTextView = this.onlineTextView[i];
                                String formatPluralString2 = LocaleController.formatPluralString("Members", iArr2[0]);
                                Object[] objArr4 = new Object[add_contact];
                                objArr4[0] = Integer.valueOf(iArr2[0]);
                                simpleTextView.setText(formatPluralString2.replace(String.format("%d", objArr4), formatShortNumber2));
                            } else if (!this.onlineTextView[i].getText().equals(format)) {
                                this.onlineTextView[i].setText(format);
                            }
                        } else if (!this.onlineTextView[i].getText().equals(format)) {
                            this.onlineTextView[i].setText(format);
                        }
                    }
                    i += add_contact;
                }
                if (chat.photo != null) {
                    tLObject2 = chat.photo.photo_small;
                    fileLocation = chat.photo.photo_big;
                } else {
                    tLObject2 = null;
                }
                if (ThemeUtil.m2490b()) {
                    int dp = AndroidUtilities.dp((float) AdvanceTheme.aE);
                    this.avatarImage.getImageReceiver().setRoundRadius(dp);
                    this.avatarDrawable.setRadius(dp);
                }
                this.avatarDrawable.setInfo(chat);
                this.avatarImage.setImage(tLObject2, "50_50", this.avatarDrawable);
                ImageReceiver imageReceiver = this.avatarImage.getImageReceiver();
                if (PhotoViewer.getInstance().isShowingImage(fileLocation)) {
                    z = false;
                }
                imageReceiver.setVisible(z, false);
            }
        }
    }

    private void updateRowsIds() {
        this.emptyRow = -1;
        this.phoneRow = -1;
        this.userInfoRow = -1;
        this.userSectionRow = -1;
        this.sectionRow = -1;
        this.sharedMediaRow = -1;
        this.settingsNotificationsRow = -1;
        this.usernameRow = -1;
        this.settingsTimerRow = -1;
        this.settingsKeyRow = -1;
        this.startSecretChatRow = -1;
        this.membersEndRow = -1;
        this.emptyRowChat2 = -1;
        this.addMemberRow = -1;
        this.channelInfoRow = -1;
        this.channelNameRow = -1;
        this.convertRow = -1;
        this.convertHelpRow = -1;
        this.emptyRowChat = -1;
        this.membersSectionRow = -1;
        this.membersRow = -1;
        this.managementRow = -1;
        this.leaveChannelRow = -1;
        this.loadMoreMembersRow = -1;
        this.blockedUsersRow = -1;
        this.rowCount = 0;
        int i;
        if (this.user_id != 0) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            int i2 = this.rowCount;
            this.rowCount = i2 + add_contact;
            this.emptyRow = i2;
            if (user == null || !user.bot) {
                i2 = this.rowCount;
                this.rowCount = i2 + add_contact;
                this.phoneRow = i2;
            }
            if (!(user == null || user.username == null || user.username.length() <= 0)) {
                i2 = this.rowCount;
                this.rowCount = i2 + add_contact;
                this.usernameRow = i2;
            }
            if (MessagesController.getInstance().getUserAbout(user.id) != null) {
                i2 = this.rowCount;
                this.rowCount = i2 + add_contact;
                this.userSectionRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + add_contact;
                this.userInfoRow = i2;
            } else {
                this.userSectionRow = -1;
                this.userInfoRow = -1;
            }
            i2 = this.rowCount;
            this.rowCount = i2 + add_contact;
            this.sectionRow = i2;
            if (this.user_id != UserConfig.getClientUserId()) {
                i2 = this.rowCount;
                this.rowCount = i2 + add_contact;
                this.settingsNotificationsRow = i2;
            }
            i2 = this.rowCount;
            this.rowCount = i2 + add_contact;
            this.sharedMediaRow = i2;
            if (this.currentEncryptedChat instanceof TL_encryptedChat) {
                i2 = this.rowCount;
                this.rowCount = i2 + add_contact;
                this.settingsTimerRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + add_contact;
                this.settingsKeyRow = i2;
            }
            if (user != null && !user.bot && this.currentEncryptedChat == null && user.id != UserConfig.getClientUserId() && !UserConfig.isRobot) {
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.startSecretChatRow = i;
            }
        } else if (this.chat_id == 0) {
        } else {
            if (this.chat_id > 0) {
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.emptyRow = i;
                if (ChatObject.isChannel(this.currentChat) && (!(this.info == null || this.info.about == null || this.info.about.length() <= 0) || (this.currentChat.username != null && this.currentChat.username.length() > 0))) {
                    if (!(this.info == null || this.info.about == null || this.info.about.length() <= 0)) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.channelInfoRow = i;
                    }
                    if (this.currentChat.username != null && this.currentChat.username.length() > 0) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.channelNameRow = i;
                    }
                    i = this.rowCount;
                    this.rowCount = i + add_contact;
                    this.sectionRow = i;
                }
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.settingsNotificationsRow = i;
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.sharedMediaRow = i;
                if (ChatObject.isChannel(this.currentChat)) {
                    if (!(this.currentChat.megagroup || this.info == null || (!this.currentChat.creator && !this.info.can_view_participants))) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.membersRow = i;
                    }
                    if (!(ChatObject.isNotInChat(this.currentChat) || this.currentChat.megagroup || (!this.currentChat.creator && !this.currentChat.editor && !this.currentChat.moderator))) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.managementRow = i;
                    }
                    if (!ChatObject.isNotInChat(this.currentChat) && this.currentChat.megagroup && (this.currentChat.editor || this.currentChat.creator)) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.blockedUsersRow = i;
                    }
                    if (!(this.currentChat.creator || this.currentChat.left || this.currentChat.kicked || this.currentChat.megagroup)) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.leaveChannelRow = i;
                    }
                    if (this.currentChat.megagroup && ((this.currentChat.editor || this.currentChat.creator || this.currentChat.democracy) && (this.info == null || this.info.participants_count < MessagesController.getInstance().maxMegagroupCount))) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.addMemberRow = i;
                    }
                    if (this.info != null && this.info.participants != null && !this.info.participants.participants.isEmpty()) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.emptyRowChat = i;
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.membersSectionRow = i;
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.emptyRowChat2 = i;
                        this.rowCount += this.info.participants.participants.size();
                        this.membersEndRow = this.rowCount;
                        if (!this.usersEndReached) {
                            i = this.rowCount;
                            this.rowCount = i + add_contact;
                            this.loadMoreMembersRow = i;
                            return;
                        }
                        return;
                    }
                    return;
                }
                if (this.info != null) {
                    if (!(this.info.participants instanceof TL_chatParticipantsForbidden) && this.info.participants.participants.size() < MessagesController.getInstance().maxGroupCount && (this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.addMemberRow = i;
                    }
                    if (this.currentChat.creator && this.info.participants.participants.size() >= MessagesController.getInstance().minGroupConvertSize) {
                        i = this.rowCount;
                        this.rowCount = i + add_contact;
                        this.convertRow = i;
                    }
                }
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.emptyRowChat = i;
                if (this.convertRow != -1) {
                    i = this.rowCount;
                    this.rowCount = i + add_contact;
                    this.convertHelpRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + add_contact;
                    this.membersSectionRow = i;
                }
                if (this.info != null && !(this.info.participants instanceof TL_chatParticipantsForbidden)) {
                    i = this.rowCount;
                    this.rowCount = i + add_contact;
                    this.emptyRowChat2 = i;
                    this.rowCount += this.info.participants.participants.size();
                    this.membersEndRow = this.rowCount;
                }
            } else if (!ChatObject.isChannel(this.currentChat) && this.info != null && !(this.info.participants instanceof TL_chatParticipantsForbidden)) {
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.addMemberRow = i;
                i = this.rowCount;
                this.rowCount = i + add_contact;
                this.emptyRowChat2 = i;
                this.rowCount += this.info.participants.participants.size();
                this.membersEndRow = this.rowCount;
            }
        }
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    protected ActionBar createActionBar(Context context) {
        ActionBar c18283 = new C18283(context);
        int i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
        c18283.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(i));
        c18283.setBackButtonDrawable(new BackDrawable(false));
        initThemeBackButton(c18283);
        c18283.setCastShadows(false);
        c18283.setAddToContainer(false);
        boolean z = VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet();
        c18283.setOccupyStatusBar(z);
        return c18283;
    }

    public View createView(Context context) {
        SimpleTextView simpleTextView;
        this.hasOwnBackground = true;
        this.extraHeight = AndroidUtilities.dp(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new C18324());
        createActionBarMenu();
        this.listAdapter = new ListAdapter(context);
        this.avatarDrawable = new AvatarDrawable();
        this.avatarDrawable.setProfile(true);
        this.fragmentView = new C18335(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new C18346(context);
        this.listView.setTag(Integer.valueOf(6));
        this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        this.listView.setBackgroundColor(-1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setClipToPadding(false);
        this.layoutManager = new C18357(context);
        this.layoutManager.setOrientation(add_contact);
        this.listView.setLayoutManager(this.layoutManager);
        RecyclerListView recyclerListView = this.listView;
        int i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
        recyclerListView.setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C18388());
        this.listView.setOnItemLongClickListener(new C18449());
        this.topView = new TopView(context);
        TopView topView = this.topView;
        i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
        topView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i));
        frameLayout.addView(this.topView);
        frameLayout.addView(this.actionBar);
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ProfileActivity.this.user_id != 0) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user.photo != null && user.photo.photo_big != null) {
                        PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
                        PhotoViewer.getInstance().openPhoto(user.photo.photo_big, ProfileActivity.this);
                    }
                } else if (ProfileActivity.this.chat_id != 0) {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                    if (chat.photo != null && chat.photo.photo_big != null) {
                        PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
                        PhotoViewer.getInstance().openPhoto(chat.photo.photo_big, ProfileActivity.this);
                    }
                }
            }
        });
        int i2 = 0;
        while (i2 < block_contact) {
            if (this.playProfileAnimation || i2 != 0) {
                this.nameTextView[i2] = new SimpleTextView(context);
                this.nameTextView[i2].setTextColor(-1);
                this.nameTextView[i2].setTextSize(18);
                this.nameTextView[i2].setGravity(share_contact);
                this.nameTextView[i2].setTypeface(FontUtil.m1176a().m1160c());
                this.nameTextView[i2].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[i2].setRightDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[i2].setPivotX(0.0f);
                this.nameTextView[i2].setPivotY(0.0f);
                frameLayout.addView(this.nameTextView[i2], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i2 == 0 ? 48.0f : 0.0f, 0.0f));
                this.onlineTextView[i2] = new SimpleTextView(context);
                simpleTextView = this.onlineTextView[i2];
                i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
                simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i));
                this.onlineTextView[i2].setTextSize(add_shortcut);
                this.onlineTextView[i2].setGravity(share_contact);
                this.onlineTextView[i2].setTypeface(FontUtil.m1176a().m1161d());
                frameLayout.addView(this.onlineTextView[i2], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i2 == 0 ? 48.0f : 8.0f, 0.0f));
            }
            i2 += add_contact;
        }
        this.creatorTextView = new SimpleTextView(context);
        simpleTextView = this.creatorTextView;
        i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
        simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i));
        this.creatorTextView.setTextSize(add_shortcut);
        this.creatorTextView.setText(" ");
        this.creatorTextView.setGravity(share_contact);
        this.creatorTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.creatorTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ProfileActivity.this.creatorID > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", ProfileActivity.this.creatorID);
                    ProfileActivity.this.presentFragment(new ProfileActivity(bundle));
                }
            }
        });
        frameLayout.addView(this.creatorTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        if (this.user_id != 0 || (this.chat_id >= 0 && !ChatObject.isLeftFromChat(this.currentChat))) {
            this.writeButton = new ImageView(context);
            try {
                this.writeButton.setBackgroundResource(C0338R.drawable.floating_user_states);
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
            }
            this.writeButton.setScaleType(ScaleType.CENTER);
            if (this.user_id != 0) {
                this.writeButton.setImageResource(C0338R.drawable.floating_message);
                this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
            } else if (this.chat_id != 0) {
                boolean isChannel = ChatObject.isChannel(this.currentChat);
                if ((!isChannel || this.currentChat.creator || (this.currentChat.megagroup && this.currentChat.editor)) && (isChannel || this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                    this.writeButton.setImageResource(C0338R.drawable.floating_camera);
                } else {
                    this.writeButton.setImageResource(C0338R.drawable.floating_message);
                    this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                }
            }
            frameLayout.addView(this.writeButton, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator stateListAnimator = new StateListAnimator();
                int[] iArr = new int[add_contact];
                iArr[0] = 16842919;
                float[] fArr = new float[block_contact];
                fArr[0] = (float) AndroidUtilities.dp(2.0f);
                fArr[add_contact] = (float) AndroidUtilities.dp(4.0f);
                stateListAnimator.addState(iArr, ObjectAnimator.ofFloat(this.writeButton, "translationZ", fArr).setDuration(200));
                iArr = new int[0];
                fArr = new float[block_contact];
                fArr[0] = (float) AndroidUtilities.dp(4.0f);
                fArr[add_contact] = (float) AndroidUtilities.dp(2.0f);
                stateListAnimator.addState(iArr, ObjectAnimator.ofFloat(this.writeButton, "translationZ", fArr).setDuration(200));
                this.writeButton.setStateListAnimator(stateListAnimator);
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.writeButton.setOnClickListener(new View.OnClickListener() {

                /* renamed from: com.hanista.mobogram.ui.ProfileActivity.13.1 */
                class C18241 implements OnClickListener {
                    C18241() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            ProfileActivity.this.avatarUpdater.openCamera();
                        } else if (i == ProfileActivity.add_contact) {
                            ProfileActivity.this.avatarUpdater.openGallery();
                        } else if (i == ProfileActivity.block_contact) {
                            MessagesController.getInstance().changeChatAvatar(ProfileActivity.this.chat_id, null);
                        }
                    }
                }

                public void onClick(View view) {
                    if (ProfileActivity.this.getParentActivity() != null) {
                        Bundle bundle;
                        if (ProfileActivity.this.user_id != 0) {
                            if (ProfileActivity.this.playProfileAnimation && (ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)) {
                                ProfileActivity.this.finishFragment();
                                return;
                            }
                            User user = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                            if (user != null && !(user instanceof TL_userEmpty)) {
                                bundle = new Bundle();
                                bundle.putInt("user_id", ProfileActivity.this.user_id);
                                if (MessagesController.checkCanOpenChat(bundle, ProfileActivity.this)) {
                                    NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    ProfileActivity.this.presentFragment(new ChatActivity(bundle), true);
                                }
                            }
                        } else if (ProfileActivity.this.chat_id != 0) {
                            boolean isChannel = ChatObject.isChannel(ProfileActivity.this.currentChat);
                            if ((!isChannel || ProfileActivity.this.currentChat.creator || (ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.currentChat.editor)) && (isChannel || ProfileActivity.this.currentChat.admin || ProfileActivity.this.currentChat.creator || !ProfileActivity.this.currentChat.admins_enabled)) {
                                CharSequence[] charSequenceArr;
                                Builder builder = new Builder(ProfileActivity.this.getParentActivity());
                                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                                if (chat.photo == null || chat.photo.photo_big == null || (chat.photo instanceof TL_chatPhotoEmpty)) {
                                    charSequenceArr = new CharSequence[ProfileActivity.block_contact];
                                    charSequenceArr[0] = LocaleController.getString("FromCamera", C0338R.string.FromCamera);
                                    charSequenceArr[ProfileActivity.add_contact] = LocaleController.getString("FromGalley", C0338R.string.FromGalley);
                                } else {
                                    charSequenceArr = new CharSequence[ProfileActivity.share_contact];
                                    charSequenceArr[0] = LocaleController.getString("FromCamera", C0338R.string.FromCamera);
                                    charSequenceArr[ProfileActivity.add_contact] = LocaleController.getString("FromGalley", C0338R.string.FromGalley);
                                    charSequenceArr[ProfileActivity.block_contact] = LocaleController.getString("DeletePhoto", C0338R.string.DeletePhoto);
                                }
                                builder.setItems(charSequenceArr, new C18241());
                                ProfileActivity.this.showDialog(builder.create());
                            } else if (ProfileActivity.this.playProfileAnimation && (ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)) {
                                ProfileActivity.this.finishFragment();
                            } else {
                                bundle = new Bundle();
                                bundle.putInt("chat_id", ProfileActivity.this.currentChat.id);
                                if (MessagesController.checkCanOpenChat(bundle, ProfileActivity.this)) {
                                    NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    ProfileActivity.this.presentFragment(new ChatActivity(bundle), true);
                                }
                            }
                        }
                    }
                }
            });
        }
        initTheme();
        needLayout();
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ProfileActivity.this.checkListViewScroll();
                if (ProfileActivity.this.participantsMap != null && ProfileActivity.this.loadMoreMembersRow != -1 && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.loadMoreMembersRow - 8) {
                    ProfileActivity.this.getChannelParticipants(false);
                }
            }
        });
        updateListBG();
        showMaterialHelp();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        int i2 = 0;
        int intValue;
        Holder holder;
        Chat chat;
        if (i == NotificationCenter.updateInterfaces) {
            intValue = ((Integer) objArr[0]).intValue();
            if (this.user_id != 0) {
                if (!((intValue & block_contact) == 0 && (intValue & add_contact) == 0 && (intValue & edit_contact) == 0)) {
                    updateProfileData();
                }
                if ((intValue & TLRPC.MESSAGE_FLAG_HAS_VIEWS) != 0 && this.listView != null) {
                    holder = (Holder) this.listView.findViewHolderForPosition(this.phoneRow);
                    if (holder != null) {
                        this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                    }
                }
            } else if (this.chat_id != 0) {
                if ((intValue & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                    if (chat != null) {
                        this.currentChat = chat;
                        createActionBarMenu();
                        updateRowsIds();
                        if (this.listAdapter != null) {
                            this.listAdapter.notifyDataSetChanged();
                        }
                    }
                }
                if (!((intValue & MessagesController.UPDATE_MASK_CHANNEL) == 0 && (intValue & edit_name) == 0 && (intValue & 16) == 0 && (intValue & 32) == 0 && (intValue & edit_contact) == 0)) {
                    updateOnlineCount();
                    updateProfileData();
                }
                if ((intValue & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                    updateRowsIds();
                    if (this.listAdapter != null) {
                        this.listAdapter.notifyDataSetChanged();
                    }
                }
                if (((intValue & block_contact) != 0 || (intValue & add_contact) != 0 || (intValue & edit_contact) != 0) && this.listView != null) {
                    int childCount = this.listView.getChildCount();
                    while (i2 < childCount) {
                        View childAt = this.listView.getChildAt(i2);
                        if (childAt instanceof UserCell) {
                            ((UserCell) childAt).update(intValue);
                        }
                        i2 += add_contact;
                    }
                }
            }
        } else if (i == NotificationCenter.contactsDidLoaded) {
            createActionBarMenu();
        } else if (i == NotificationCenter.mediaCountDidLoaded) {
            long longValue = ((Long) objArr[0]).longValue();
            long j = this.dialog_id;
            if (j == 0) {
                if (this.user_id != 0) {
                    j = (long) this.user_id;
                } else if (this.chat_id != 0) {
                    j = (long) (-this.chat_id);
                }
            }
            if (longValue == j || longValue == this.mergeDialogId) {
                if (longValue == j) {
                    this.totalMediaCount = ((Integer) objArr[add_contact]).intValue();
                } else {
                    this.totalMediaCountMerge = ((Integer) objArr[add_contact]).intValue();
                }
                if (this.listView != null) {
                    intValue = this.listView.getChildCount();
                    while (i2 < intValue) {
                        holder = (Holder) this.listView.getChildViewHolder(this.listView.getChildAt(i2));
                        if (holder.getAdapterPosition() == this.sharedMediaRow) {
                            this.listAdapter.onBindViewHolder(holder, this.sharedMediaRow);
                            return;
                        }
                        i2 += add_contact;
                    }
                }
            }
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                AndroidUtilities.runOnUIThread(new AnonymousClass23(objArr));
            }
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            EncryptedChat encryptedChat = (EncryptedChat) objArr[0];
            if (this.currentEncryptedChat != null && encryptedChat.id == this.currentEncryptedChat.id) {
                this.currentEncryptedChat = encryptedChat;
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (i == NotificationCenter.blockedUsersDidLoaded) {
            boolean z = this.userBlocked;
            this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.user_id));
            if (z != this.userBlocked) {
                createActionBarMenu();
            }
        } else if (i == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr[0];
            if (chatFull.id == this.chat_id) {
                boolean booleanValue = ((Boolean) objArr[block_contact]).booleanValue();
                if ((this.info instanceof TL_channelFull) && chatFull.participants == null && this.info != null) {
                    chatFull.participants = this.info.participants;
                }
                boolean z2 = this.info == null && (chatFull instanceof TL_channelFull);
                this.info = chatFull;
                if (this.mergeDialogId == 0 && this.info.migrated_from_chat_id != 0) {
                    this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
                    SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                }
                fetchUsersFromChannelInfo();
                updateOnlineCount();
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
                chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                if (chat != null) {
                    this.currentChat = chat;
                    createActionBarMenu();
                }
                if (!this.currentChat.megagroup) {
                    return;
                }
                if (z2 || !booleanValue) {
                    getChannelParticipants(true);
                }
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.botInfoDidLoaded) {
            BotInfo botInfo = (BotInfo) objArr[0];
            if (botInfo.user_id == this.user_id) {
                this.botInfo = botInfo;
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (i == NotificationCenter.userInfoDidLoaded) {
            if (((Integer) objArr[0]).intValue() == this.user_id) {
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (i == NotificationCenter.didReceivedNewMessages && ((Long) objArr[0]).longValue() == this.dialog_id) {
            ArrayList arrayList = (ArrayList) objArr[add_contact];
            while (i2 < arrayList.size()) {
                MessageObject messageObject = (MessageObject) arrayList.get(i2);
                if (this.currentEncryptedChat != null && messageObject.messageOwner.action != null && (messageObject.messageOwner.action instanceof TL_messageEncryptedAction) && (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) {
                    TL_decryptedMessageActionSetMessageTTL tL_decryptedMessageActionSetMessageTTL = (TL_decryptedMessageActionSetMessageTTL) messageObject.messageOwner.action.encryptedAction;
                    if (this.listAdapter != null) {
                        this.listAdapter.notifyDataSetChanged();
                    }
                }
                i2 += add_contact;
            }
        }
    }

    public void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z) {
        if (j != 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            int i = (int) j;
            if (i == 0) {
                bundle.putInt("enc_id", (int) (j >> 32));
            } else if (i > 0) {
                bundle.putInt("user_id", i);
            } else if (i < 0) {
                bundle.putInt("chat_id", -i);
            }
            if (MessagesController.checkCanOpenChat(bundle, dialogsActivity)) {
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(bundle), true);
                removeSelfFromStack();
                SendMessagesHelper.getInstance().sendMessage(MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)), j, null, null, null);
            }
        }
    }

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        PlaceProviderObject placeProviderObject = null;
        if (fileLocation != null) {
            FileLocation fileLocation2;
            if (this.user_id != 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
                if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                    fileLocation2 = user.photo.photo_big;
                }
                fileLocation2 = null;
            } else {
                if (this.chat_id != 0) {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
                    if (!(chat == null || chat.photo == null || chat.photo.photo_big == null)) {
                        fileLocation2 = chat.photo.photo_big;
                    }
                }
                fileLocation2 = null;
            }
            if (fileLocation2 != null && fileLocation2.local_id == fileLocation.local_id && fileLocation2.volume_id == fileLocation.volume_id && fileLocation2.dc_id == fileLocation.dc_id) {
                int[] iArr = new int[block_contact];
                this.avatarImage.getLocationInWindow(iArr);
                placeProviderObject = new PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[add_contact] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                placeProviderObject.parentView = this.avatarImage;
                placeProviderObject.imageReceiver = this.avatarImage.getImageReceiver();
                if (this.user_id != 0) {
                    placeProviderObject.dialogId = this.user_id;
                } else if (this.chat_id != 0) {
                    placeProviderObject.dialogId = -this.chat_id;
                }
                placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
                placeProviderObject.size = -1;
                placeProviderObject.radius = this.avatarImage.getImageReceiver().getRoundRadius();
                placeProviderObject.scale = this.avatarImage.getScaleX();
            }
        }
        return placeProviderObject;
    }

    public int getSelectedCount() {
        return 0;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public boolean isChat() {
        return this.chat_id != 0;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (this.chat_id != 0) {
            this.avatarUpdater.onActivityResult(i, i2, intent);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    protected AnimatorSet onCustomTransitionAnimation(boolean z, Runnable runnable) {
        if (!this.playProfileAnimation || !this.allowProfileAnimation) {
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        if (VERSION.SDK_INT > 15) {
            this.listView.setLayerType(block_contact, null);
        }
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (createMenu.getItem(share) == null && this.animatingItem == null) {
            this.animatingItem = createMenu.addItem((int) share, (int) C0338R.drawable.ic_ab_other);
        }
        int ceil;
        Collection arrayList;
        float[] fArr;
        Object obj;
        String str;
        float[] fArr2;
        float[] fArr3;
        if (z) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[add_contact].getLayoutParams();
            layoutParams.rightMargin = (int) ((-21.0f * AndroidUtilities.density) + ((float) AndroidUtilities.dp(8.0f)));
            this.onlineTextView[add_contact].setLayoutParams(layoutParams);
            ceil = (int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f))) + (21.0f * AndroidUtilities.density)));
            layoutParams = (FrameLayout.LayoutParams) this.nameTextView[add_contact].getLayoutParams();
            if (((float) ceil) < ((float) this.nameTextView[add_contact].getSideDrawablesSize()) + (this.nameTextView[add_contact].getPaint().measureText(this.nameTextView[add_contact].getText().toString()) * 1.12f)) {
                layoutParams.width = (int) Math.ceil((double) (((float) ceil) / 1.12f));
            } else {
                layoutParams.width = -2;
            }
            this.nameTextView[add_contact].setLayoutParams(layoutParams);
            this.initialAnimationExtraHeight = AndroidUtilities.dp(88.0f);
            this.fragmentView.setBackgroundColor(0);
            setAnimationProgress(0.0f);
            arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
            if (this.writeButton != null) {
                this.writeButton.setScaleX(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD);
                this.writeButton.setScaleY(DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD);
                this.writeButton.setAlpha(0.0f);
                fArr = new float[add_contact];
                fArr[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr));
                fArr = new float[add_contact];
                fArr[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr));
                fArr = new float[add_contact];
                fArr[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr));
            }
            ceil = 0;
            while (ceil < block_contact) {
                this.onlineTextView[ceil].setAlpha(ceil == 0 ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f);
                this.nameTextView[ceil].setAlpha(ceil == 0 ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f);
                obj = this.onlineTextView[ceil];
                str = "alpha";
                fArr2 = new float[add_contact];
                fArr2[0] = ceil == 0 ? 0.0f : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                arrayList.add(ObjectAnimator.ofFloat(obj, str, fArr2));
                obj = this.nameTextView[ceil];
                str = "alpha";
                fArr2 = new float[add_contact];
                fArr2[0] = ceil == 0 ? 0.0f : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                arrayList.add(ObjectAnimator.ofFloat(obj, str, fArr2));
                ceil += add_contact;
            }
            if (this.animatingItem != null) {
                this.animatingItem.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                fArr3 = new float[add_contact];
                fArr3[0] = 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", fArr3));
            }
            animatorSet.playTogether(arrayList);
        } else {
            this.initialAnimationExtraHeight = this.extraHeight;
            arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f}));
            if (this.writeButton != null) {
                fArr = new float[add_contact];
                fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr));
                fArr = new float[add_contact];
                fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr));
                fArr = new float[add_contact];
                fArr[0] = 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr));
            }
            ceil = 0;
            while (ceil < block_contact) {
                obj = this.onlineTextView[ceil];
                str = "alpha";
                fArr2 = new float[add_contact];
                fArr2[0] = ceil == 0 ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(obj, str, fArr2));
                obj = this.nameTextView[ceil];
                str = "alpha";
                fArr2 = new float[add_contact];
                fArr2[0] = ceil == 0 ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(obj, str, fArr2));
                ceil += add_contact;
            }
            if (this.animatingItem != null) {
                this.animatingItem.setAlpha(0.0f);
                fArr3 = new float[add_contact];
                fArr3[0] = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                arrayList.add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", fArr3));
            }
            animatorSet.playTogether(arrayList);
        }
        animatorSet.addListener(new AnonymousClass24(runnable));
        animatorSet.setInterpolator(new DecelerateInterpolator());
        AndroidUtilities.runOnUIThread(new AnonymousClass25(animatorSet), 50);
        return animatorSet;
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
    }

    public boolean onFragmentCreate() {
        this.user_id = this.arguments.getInt("user_id", 0);
        this.chat_id = getArguments().getInt("chat_id", 0);
        if (this.user_id != 0) {
            this.dialog_id = this.arguments.getLong("dialog_id", 0);
            if (this.dialog_id != 0) {
                this.currentEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            }
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
            if (user == null) {
                return false;
            }
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.botInfoDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.userInfoDidLoaded);
            if (this.currentEncryptedChat != null) {
                NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
            }
            this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.user_id));
            if (user.bot) {
                BotQuery.loadBotInfo(user.id, true, this.classGuid);
            }
            MessagesController.getInstance().loadFullUser(MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
            this.participantsMap = null;
        } else if (this.chat_id == 0) {
            return false;
        } else {
            this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
            if (this.currentChat == null) {
                Semaphore semaphore = new Semaphore(0);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C18261(semaphore));
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
            if (this.currentChat.megagroup) {
                getChannelParticipants(true);
            } else {
                this.participantsMap = null;
            }
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
            this.sortedUsers = new ArrayList();
            updateOnlineCount();
            this.avatarUpdater = new AvatarUpdater();
            this.avatarUpdater.delegate = new C18272();
            this.avatarUpdater.parentFragment = this;
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance().loadFullChat(this.chat_id, this.classGuid, true);
            }
        }
        if (this.dialog_id != 0) {
            SharedMediaQuery.getMediaCount(this.dialog_id, 0, this.classGuid, true);
        } else if (this.user_id != 0) {
            SharedMediaQuery.getMediaCount((long) this.user_id, 0, this.classGuid, true);
        } else if (this.chat_id > 0) {
            SharedMediaQuery.getMediaCount((long) (-this.chat_id), 0, this.classGuid, true);
            if (this.mergeDialogId != 0) {
                SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
            }
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaCountDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        updateRowsIds();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaCountDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        if (this.user_id != 0) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.botInfoDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.userInfoDidLoaded);
            MessagesController.getInstance().cancelLoadFullUser(this.user_id);
            if (this.currentEncryptedChat != null) {
                NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
            }
        } else if (this.chat_id != 0) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
            this.avatarUpdater.clear();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        updateProfileData();
        fixLayout();
    }

    protected void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (!z2 && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = false;
        }
        NotificationCenter.getInstance().setAnimationInProgress(false);
    }

    protected void onTransitionAnimationStart(boolean z, boolean z2) {
        if (!z2 && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = true;
        }
        NotificationCenter instance = NotificationCenter.getInstance();
        int[] iArr = new int[share_contact];
        iArr[0] = NotificationCenter.dialogsNeedReload;
        iArr[add_contact] = NotificationCenter.closeChats;
        iArr[block_contact] = NotificationCenter.mediaCountDidLoaded;
        instance.setAllowedNotificationsDutingAnimation(iArr);
        NotificationCenter.getInstance().setAnimationInProgress(true);
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.chat_id != 0) {
            MessagesController.getInstance().loadChatInfo(this.chat_id, null, false);
            if (this.avatarUpdater != null) {
                this.avatarUpdater.currentPicturePath = bundle.getString("path");
            }
        }
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.chat_id != 0 && this.avatarUpdater != null && this.avatarUpdater.currentPicturePath != null) {
            bundle.putString("path", this.avatarUpdater.currentPicturePath);
        }
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
    }

    public void setAnimationProgress(float f) {
        int i = delete_contact;
        this.animationProgress = f;
        this.listView.setAlpha(f);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * f));
        int i2 = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id) && !this.currentChat.megagroup)) ? delete_contact : this.chat_id;
        i2 = AvatarDrawable.getProfileBackColorForId(i2);
        int red = Color.red(Theme.ACTION_BAR_COLOR);
        int green = Color.green(Theme.ACTION_BAR_COLOR);
        int blue = Color.blue(Theme.ACTION_BAR_COLOR);
        this.topView.setBackgroundColor(Color.rgb(red + ((int) (((float) (Color.red(i2) - red)) * f)), green + ((int) (((float) (Color.green(i2) - green)) * f)), ((int) (((float) (Color.blue(i2) - blue)) * f)) + blue));
        if (this.user_id == 0 && (!ChatObject.isChannel(this.chat_id) || this.currentChat.megagroup)) {
            i = this.chat_id;
        }
        i2 = AvatarDrawable.getProfileTextColorForId(i);
        i = Color.red(Theme.ACTION_BAR_SUBTITLE_COLOR);
        red = Color.green(Theme.ACTION_BAR_SUBTITLE_COLOR);
        green = Color.blue(Theme.ACTION_BAR_SUBTITLE_COLOR);
        blue = (int) (((float) (Color.red(i2) - i)) * f);
        int green2 = (int) (((float) (Color.green(i2) - red)) * f);
        int blue2 = (int) (((float) (Color.blue(i2) - green)) * f);
        for (i2 = 0; i2 < block_contact; i2 += add_contact) {
            if (!(this.onlineTextView[i2] == null || ThemeUtil.m2490b())) {
                this.onlineTextView[i2].setTextColor(Color.rgb(i + blue, red + green2, green + blue2));
            }
        }
        this.extraHeight = (int) (((float) this.initialAnimationExtraHeight) * f);
        i = AvatarDrawable.getProfileColorForId(this.user_id != 0 ? this.user_id : this.chat_id);
        i2 = AvatarDrawable.getColorForId(this.user_id != 0 ? this.user_id : this.chat_id);
        if (i != i2) {
            this.avatarDrawable.setColor(Color.rgb(((int) (((float) (Color.red(i) - Color.red(i2))) * f)) + Color.red(i2), ((int) (((float) (Color.green(i) - Color.green(i2))) * f)) + Color.green(i2), Color.blue(i2) + ((int) (((float) (Color.blue(i) - Color.blue(i2))) * f))));
            this.avatarImage.invalidate();
        }
        needLayout();
        updateActionBarBG();
    }

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        if (!(this.info == null || this.info.migrated_from_chat_id == 0)) {
            this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
        }
        fetchUsersFromChannelInfo();
    }

    public void setPhotoChecked(int i) {
    }

    public void setPlayProfileAnimation(boolean z) {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (!AndroidUtilities.isTablet() && sharedPreferences.getBoolean("view_animations", true)) {
            this.playProfileAnimation = z;
        }
    }

    public void updatePhotoAtIndex(int i) {
    }

    public void willHidePhotoViewer() {
        this.avatarImage.getImageReceiver().setVisible(true, true);
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}

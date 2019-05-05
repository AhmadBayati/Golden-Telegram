package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.ChannelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.ChannelParticipantRole;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.InputUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantCreator;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantEditor;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantModerator;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantSelf;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantsKicked;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantsRecent;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelRoleEditor;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelRoleEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_channelParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_editAdmin;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_kickFromChannel;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.Updates;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.RadioCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.AlertsCreator;
import com.hanista.mobogram.ui.Components.EmptyTextProgressView;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.ContactsActivity.ContactsActivityDelegate;
import com.hanista.mobogram.ui.ContactsActivity.ContactsActivityMultiSelectDelegate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChannelUsersActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chatId;
    private EmptyTextProgressView emptyView;
    private boolean firstLoaded;
    private boolean isAdmin;
    private boolean isMegagroup;
    private boolean isPublic;
    private ListAdapter listViewAdapter;
    private boolean loadingUsers;
    private ArrayList<ChannelParticipant> participants;
    private int participantsStartRow;
    private int type;

    /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.1 */
    class C12261 extends ActionBarMenuOnItemClick {
        C12261() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChannelUsersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.2 */
    class C12292 implements OnItemClickListener {
        final /* synthetic */ ListView val$listView;

        /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.2.1 */
        class C12271 implements ContactsActivityMultiSelectDelegate {
            C12271() {
            }

            public void didSelectContacts(List<Integer> list, String str) {
                ChannelUsersActivity.this.addMultiContacts(list);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.2.2 */
        class C12282 implements ContactsActivityDelegate {
            C12282() {
            }

            public void didSelectContact(User user, String str) {
                ChannelUsersActivity.this.setUserChannelRole(user, new TL_channelRoleEditor());
            }
        }

        C12292(ListView listView) {
            this.val$listView = listView;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            Bundle bundle;
            BaseFragment contactsActivity;
            if (ChannelUsersActivity.this.type == 2) {
                if (ChannelUsersActivity.this.isAdmin) {
                    if (i == 0) {
                        bundle = new Bundle();
                        bundle.putBoolean("onlyUsers", true);
                        bundle.putBoolean("destroyAfterSelect", true);
                        bundle.putBoolean("multiSelectMode", true);
                        bundle.putBoolean("returnAsResult", true);
                        bundle.putBoolean("needForwardCount", false);
                        bundle.putBoolean("allowUsernameSearch", false);
                        contactsActivity = new ContactsActivity(bundle);
                        contactsActivity.setMultiSelectDelegate(new C12271());
                        ChannelUsersActivity.this.presentFragment(contactsActivity);
                    } else if (!ChannelUsersActivity.this.isPublic && i == 1) {
                        ChannelUsersActivity.this.presentFragment(new GroupInviteActivity(ChannelUsersActivity.this.chatId));
                    }
                }
            } else if (ChannelUsersActivity.this.type == 1 && ChannelUsersActivity.this.isAdmin) {
                if (ChannelUsersActivity.this.isMegagroup && (i == 1 || i == 2)) {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
                    if (chat != null) {
                        boolean z;
                        if (i == 1 && !chat.democracy) {
                            chat.democracy = true;
                            z = true;
                        } else if (i == 2 && chat.democracy) {
                            chat.democracy = false;
                            z = true;
                        } else {
                            z = false;
                        }
                        if (z) {
                            MessagesController.getInstance().toogleChannelInvites(ChannelUsersActivity.this.chatId, chat.democracy);
                            int childCount = this.val$listView.getChildCount();
                            for (int i2 = 0; i2 < childCount; i2++) {
                                View childAt = this.val$listView.getChildAt(i2);
                                if (childAt instanceof RadioCell) {
                                    int intValue = ((Integer) childAt.getTag()).intValue();
                                    RadioCell radioCell = (RadioCell) childAt;
                                    boolean z2 = (intValue == 0 && chat.democracy) || (intValue == 1 && !chat.democracy);
                                    radioCell.setChecked(z2, true);
                                }
                            }
                            return;
                        }
                        return;
                    }
                    return;
                } else if (i == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
                    bundle = new Bundle();
                    bundle.putBoolean("onlyUsers", true);
                    bundle.putBoolean("destroyAfterSelect", true);
                    bundle.putBoolean("returnAsResult", true);
                    bundle.putBoolean("needForwardCount", false);
                    bundle.putBoolean("allowUsernameSearch", true);
                    bundle.putString("selectAlertString", LocaleController.getString("ChannelAddUserAdminAlert", C0338R.string.ChannelAddUserAdminAlert));
                    contactsActivity = new ContactsActivity(bundle);
                    contactsActivity.setDelegate(new C12282());
                    ChannelUsersActivity.this.presentFragment(contactsActivity);
                    return;
                }
            }
            ChannelParticipant channelParticipant = null;
            if (i >= ChannelUsersActivity.this.participantsStartRow && i < ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) {
                channelParticipant = (ChannelParticipant) ChannelUsersActivity.this.participants.get(i - ChannelUsersActivity.this.participantsStartRow);
            }
            if (channelParticipant != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putInt("user_id", channelParticipant.user_id);
                ChannelUsersActivity.this.presentFragment(new ProfileActivity(bundle2));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.3 */
    class C12333 implements OnItemLongClickListener {

        /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.3.1 */
        class C12321 implements OnClickListener {
            final /* synthetic */ ChannelParticipant val$finalParticipant;

            /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.3.1.1 */
            class C12311 implements RequestDelegate {

                /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.3.1.1.1 */
                class C12301 implements Runnable {
                    final /* synthetic */ Updates val$updates;

                    C12301(Updates updates) {
                        this.val$updates = updates;
                    }

                    public void run() {
                        MessagesController.getInstance().loadFullChat(((Chat) this.val$updates.chats.get(0)).id, 0, true);
                    }
                }

                C12311() {
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tLObject != null) {
                        Updates updates = (Updates) tLObject;
                        MessagesController.getInstance().processUpdates(updates, false);
                        if (!updates.chats.isEmpty()) {
                            AndroidUtilities.runOnUIThread(new C12301(updates), 1000);
                        }
                    }
                }
            }

            C12321(ChannelParticipant channelParticipant) {
                this.val$finalParticipant = channelParticipant;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (ChannelUsersActivity.this.type == 0) {
                        ChannelUsersActivity.this.participants.remove(this.val$finalParticipant);
                        ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                        TLObject tL_channels_kickFromChannel = new TL_channels_kickFromChannel();
                        tL_channels_kickFromChannel.kicked = false;
                        tL_channels_kickFromChannel.user_id = MessagesController.getInputUser(this.val$finalParticipant.user_id);
                        tL_channels_kickFromChannel.channel = MessagesController.getInputChannel(ChannelUsersActivity.this.chatId);
                        ConnectionsManager.getInstance().sendRequest(tL_channels_kickFromChannel, new C12311());
                    } else if (ChannelUsersActivity.this.type == 1) {
                        ChannelUsersActivity.this.setUserChannelRole(MessagesController.getInstance().getUser(Integer.valueOf(this.val$finalParticipant.user_id)), new TL_channelRoleEmpty());
                    } else if (ChannelUsersActivity.this.type == 2) {
                        MessagesController.getInstance().deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance().getUser(Integer.valueOf(this.val$finalParticipant.user_id)), null);
                    }
                } else if (i == 1 && ChannelUsersActivity.this.type == 0) {
                    ChannelUsersActivity.this.showUserMessages(this.val$finalParticipant.user_id);
                }
            }
        }

        C12333() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (ChannelUsersActivity.this.getParentActivity() == null) {
                return false;
            }
            ChannelParticipant channelParticipant = (i < ChannelUsersActivity.this.participantsStartRow || i >= ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) ? null : (ChannelParticipant) ChannelUsersActivity.this.participants.get(i - ChannelUsersActivity.this.participantsStartRow);
            if (channelParticipant == null) {
                return false;
            }
            Builder builder = new Builder(ChannelUsersActivity.this.getParentActivity());
            CharSequence[] charSequenceArr = ChannelUsersActivity.this.type == 0 ? new CharSequence[]{LocaleController.getString("Unblock", C0338R.string.Unblock), LocaleController.getString("ShowThisUserMessages", C0338R.string.ShowThisUserMessages)} : ChannelUsersActivity.this.type == 1 ? new CharSequence[]{LocaleController.getString("ChannelRemoveUserAdmin", C0338R.string.ChannelRemoveUserAdmin)} : ChannelUsersActivity.this.type == 2 ? new CharSequence[]{LocaleController.getString("ChannelRemoveUser", C0338R.string.ChannelRemoveUser)} : null;
            builder.setItems(charSequenceArr, new C12321(channelParticipant));
            ChannelUsersActivity.this.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.4 */
    class C12364 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.4.1 */
        class C12341 implements Runnable {
            C12341() {
            }

            public void run() {
                MessagesController.getInstance().loadFullChat(ChannelUsersActivity.this.chatId, 0, true);
            }
        }

        /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.4.2 */
        class C12352 implements Runnable {
            final /* synthetic */ TL_error val$error;

            C12352(TL_error tL_error) {
                this.val$error = tL_error;
            }

            public void run() {
                AlertsCreator.showAddUserAlert(this.val$error.text, ChannelUsersActivity.this, !ChannelUsersActivity.this.isMegagroup);
            }
        }

        C12364() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                MessagesController.getInstance().processUpdates((Updates) tLObject, false);
                AndroidUtilities.runOnUIThread(new C12341(), 1000);
                return;
            }
            AndroidUtilities.runOnUIThread(new C12352(tL_error));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.5 */
    class C12375 implements Runnable {
        C12375() {
        }

        public void run() {
            ChannelUsersActivity.this.getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.6 */
    class C12416 implements RequestDelegate {

        /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.6.1 */
        class C12401 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.6.1.1 */
            class C12381 implements Comparator<ChannelParticipant> {
                C12381() {
                }

                public int compare(ChannelParticipant channelParticipant, ChannelParticipant channelParticipant2) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(channelParticipant2.user_id));
                    User user2 = MessagesController.getInstance().getUser(Integer.valueOf(channelParticipant.user_id));
                    int currentTime = (user == null || user.status == null) ? 0 : user.id == UserConfig.getClientUserId() ? ConnectionsManager.getInstance().getCurrentTime() + 50000 : user.status.expires;
                    int currentTime2 = (user2 == null || user2.status == null) ? 0 : user2.id == UserConfig.getClientUserId() ? ConnectionsManager.getInstance().getCurrentTime() + 50000 : user2.status.expires;
                    return (currentTime <= 0 || currentTime2 <= 0) ? (currentTime >= 0 || currentTime2 >= 0) ? ((currentTime >= 0 || currentTime2 <= 0) && (currentTime != 0 || currentTime2 == 0)) ? (currentTime2 >= 0 || currentTime <= 0) ? (currentTime2 != 0 || currentTime == 0) ? 0 : 1 : 1 : -1 : currentTime <= currentTime2 ? currentTime < currentTime2 ? -1 : 0 : 1 : currentTime > currentTime2 ? 1 : currentTime < currentTime2 ? -1 : 0;
                }
            }

            /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.6.1.2 */
            class C12392 implements Comparator<ChannelParticipant> {
                C12392() {
                }

                public int compare(ChannelParticipant channelParticipant, ChannelParticipant channelParticipant2) {
                    int access$1100 = ChannelUsersActivity.this.getChannelAdminParticipantType(channelParticipant);
                    int access$11002 = ChannelUsersActivity.this.getChannelAdminParticipantType(channelParticipant2);
                    return access$1100 > access$11002 ? 1 : access$1100 < access$11002 ? -1 : 0;
                }
            }

            C12401(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$error == null) {
                    TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) this.val$response;
                    MessagesController.getInstance().putUsers(tL_channels_channelParticipants.users, false);
                    ChannelUsersActivity.this.participants = tL_channels_channelParticipants.participants;
                    try {
                        if (ChannelUsersActivity.this.type == 0 || ChannelUsersActivity.this.type == 2) {
                            Collections.sort(ChannelUsersActivity.this.participants, new C12381());
                        } else if (ChannelUsersActivity.this.type == 1) {
                            Collections.sort(tL_channels_channelParticipants.participants, new C12392());
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
                ChannelUsersActivity.this.loadingUsers = false;
                ChannelUsersActivity.this.firstLoaded = true;
                if (ChannelUsersActivity.this.emptyView != null) {
                    ChannelUsersActivity.this.emptyView.showTextView();
                }
                if (ChannelUsersActivity.this.listViewAdapter != null) {
                    ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                }
            }
        }

        C12416() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C12401(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ChannelUsersActivity.7 */
    class C12427 implements OnClickListener {
        final /* synthetic */ List val$userIds;

        C12427(List list) {
            this.val$userIds = list;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < this.val$userIds.size() - 1; i2++) {
                InputUser inputUser = MessagesController.getInputUser(MessagesController.getInstance().getUser((Integer) this.val$userIds.get(i2)));
                if (inputUser != null) {
                    arrayList.add(inputUser);
                }
            }
            MessagesController.getInstance().addUsersToChannel(ChannelUsersActivity.this.chatId, arrayList, null);
            if (this.val$userIds.size() > 0) {
                MessagesController.getInstance().addUserToChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance().getUser((Integer) this.val$userIds.get(this.val$userIds.size() - 1)), null, 0, null, ChannelUsersActivity.this);
            }
            ChannelUsersActivity.this.getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            int i = 1;
            if (ChannelUsersActivity.this.participants.isEmpty() && ChannelUsersActivity.this.type == 0) {
                return 0;
            }
            if (ChannelUsersActivity.this.loadingUsers && !ChannelUsersActivity.this.firstLoaded) {
                return 0;
            }
            if (ChannelUsersActivity.this.type != 1) {
                return (ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) + 1;
            }
            int size = ChannelUsersActivity.this.participants.size();
            if (ChannelUsersActivity.this.isAdmin) {
                i = 2;
            }
            size += i;
            i = (ChannelUsersActivity.this.isAdmin && ChannelUsersActivity.this.isMegagroup) ? 4 : 0;
            return size + i;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            if (ChannelUsersActivity.this.type == 1) {
                if (ChannelUsersActivity.this.isAdmin) {
                    if (ChannelUsersActivity.this.isMegagroup) {
                        if (i == 0) {
                            return 5;
                        }
                        if (i == 1 || i == 2) {
                            return 6;
                        }
                        if (i == 3) {
                            return 3;
                        }
                    }
                    if (i == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
                        return 4;
                    }
                    if (i == (ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) + 1) {
                        return 1;
                    }
                }
            } else if (ChannelUsersActivity.this.type == 2 && ChannelUsersActivity.this.isAdmin) {
                if (ChannelUsersActivity.this.isPublic) {
                    if (i == 0) {
                        return 2;
                    }
                    if (i == 1) {
                        return 1;
                    }
                } else if (i == 0 || i == 1) {
                    return 2;
                } else {
                    if (i == 2) {
                        return 1;
                    }
                }
            }
            return i == ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow ? 1 : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
            int i2 = AdvanceTheme.aA;
            int i3 = AdvanceTheme.aB;
            int i4 = AdvanceTheme.aH;
            int itemViewType = getItemViewType(i);
            View userCell;
            if (itemViewType == 0) {
                if (view == null) {
                    userCell = new UserCell(this.mContext, 1, 0, false);
                    userCell.setBackgroundColor(-1);
                    if (ThemeUtil.m2490b()) {
                        userCell.setBackgroundColor(i2);
                        userCell.setTag("Profile");
                    }
                } else {
                    userCell = view;
                }
                UserCell userCell2 = (UserCell) userCell;
                if (ThemeUtil.m2490b()) {
                    userCell2.setNameColor(i3);
                    userCell2.setStatusColor(i4);
                }
                ChannelParticipant channelParticipant = (ChannelParticipant) ChannelUsersActivity.this.participants.get(i - ChannelUsersActivity.this.participantsStartRow);
                TLObject user = MessagesController.getInstance().getUser(Integer.valueOf(channelParticipant.user_id));
                if (user == null) {
                    return userCell;
                }
                CharSequence string;
                if (ChannelUsersActivity.this.type == 0) {
                    string = (user.phone == null || user.phone.length() == 0) ? LocaleController.getString("NumberUnknown", C0338R.string.NumberUnknown) : PhoneFormat.getInstance().format("+" + user.phone);
                    userCell2.setData(user, null, string, 0);
                    return userCell;
                } else if (ChannelUsersActivity.this.type == 1) {
                    string = ((channelParticipant instanceof TL_channelParticipantCreator) || (channelParticipant instanceof TL_channelParticipantSelf)) ? LocaleController.getString("ChannelCreator", C0338R.string.ChannelCreator) : channelParticipant instanceof TL_channelParticipantModerator ? LocaleController.getString("ChannelModerator", C0338R.string.ChannelModerator) : channelParticipant instanceof TL_channelParticipantEditor ? LocaleController.getString("ChannelEditor", C0338R.string.ChannelEditor) : null;
                    userCell2.setData(user, null, string, 0);
                    return userCell;
                } else if (ChannelUsersActivity.this.type != 2) {
                    return userCell;
                } else {
                    userCell2.setData(user, null, null, 0);
                    return userCell;
                }
            } else if (itemViewType == 1) {
                userCell = view == null ? new TextInfoPrivacyCell(this.mContext) : view;
                if (ChannelUsersActivity.this.type == 0) {
                    ((TextInfoPrivacyCell) userCell).setText(String.format("%1$s\n\n%2$s", new Object[]{LocaleController.getString("NoBlockedGroup", C0338R.string.NoBlockedGroup), LocaleController.getString("UnblockText", C0338R.string.UnblockText)}));
                    userCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                } else if (ChannelUsersActivity.this.type == 1) {
                    if (!ChannelUsersActivity.this.isAdmin) {
                        ((TextInfoPrivacyCell) userCell).setText(TtmlNode.ANONYMOUS_REGION_ID);
                        userCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    } else if (ChannelUsersActivity.this.isMegagroup) {
                        ((TextInfoPrivacyCell) userCell).setText(LocaleController.getString("MegaAdminsInfo", C0338R.string.MegaAdminsInfo));
                        userCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    } else {
                        ((TextInfoPrivacyCell) userCell).setText(LocaleController.getString("ChannelAdminsInfo", C0338R.string.ChannelAdminsInfo));
                        userCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    }
                } else if (ChannelUsersActivity.this.type == 2) {
                    if (((ChannelUsersActivity.this.isPublic || i != 2) && i != 1) || !ChannelUsersActivity.this.isAdmin) {
                        ((TextInfoPrivacyCell) userCell).setText(TtmlNode.ANONYMOUS_REGION_ID);
                        userCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    } else {
                        if (ChannelUsersActivity.this.isMegagroup) {
                            ((TextInfoPrivacyCell) userCell).setText(TtmlNode.ANONYMOUS_REGION_ID);
                        } else {
                            ((TextInfoPrivacyCell) userCell).setText(LocaleController.getString("ChannelMembersInfo", C0338R.string.ChannelMembersInfo));
                        }
                        userCell.setBackgroundResource(C0338R.drawable.greydivider);
                    }
                }
                if (!ThemeUtil.m2490b()) {
                    return userCell;
                }
                ((TextInfoPrivacyCell) userCell).setTextColor(i4);
                if (i2 != -1) {
                    userCell.setBackgroundColor(i2);
                }
                userCell.setTag("Profile");
                return userCell;
            } else if (itemViewType == 2) {
                if (view == null) {
                    userCell = new TextSettingsCell(this.mContext);
                    userCell.setBackgroundColor(-1);
                    if (ThemeUtil.m2490b()) {
                        userCell.setBackgroundColor(i2);
                        userCell.setTag("Profile");
                    }
                } else {
                    userCell = view;
                }
                TextSettingsCell textSettingsCell = (TextSettingsCell) userCell;
                if (ThemeUtil.m2490b()) {
                    textSettingsCell.setTextColor(i3);
                }
                if (ChannelUsersActivity.this.type == 2) {
                    if (i == 0) {
                        textSettingsCell.setText(LocaleController.getString("AddMember", C0338R.string.AddMember), true);
                        return userCell;
                    } else if (i != 1) {
                        return userCell;
                    } else {
                        textSettingsCell.setText(LocaleController.getString("ChannelInviteViaLink", C0338R.string.ChannelInviteViaLink), false);
                        return userCell;
                    }
                } else if (ChannelUsersActivity.this.type != 1) {
                    return userCell;
                } else {
                    textSettingsCell.setTextAndIcon(LocaleController.getString("ChannelAddAdmin", C0338R.string.ChannelAddAdmin), C0338R.drawable.managers, false);
                    return userCell;
                }
            } else {
                if (itemViewType == 3) {
                    if (view == null) {
                        userCell = new ShadowSectionCell(this.mContext);
                        if (!ThemeUtil.m2490b()) {
                            return userCell;
                        }
                        userCell = new ShadowSectionCell(this.mContext, false);
                        userCell.setBackgroundColor(i2);
                        return userCell;
                    }
                } else if (itemViewType == 4) {
                    if (view == null) {
                        userCell = new TextCell(this.mContext);
                        userCell.setBackgroundColor(-1);
                        if (ThemeUtil.m2490b() && i2 != -1) {
                            userCell.setBackgroundColor(i2);
                        }
                    } else {
                        userCell = view;
                    }
                    ((TextCell) userCell).setTextAndIcon(LocaleController.getString("ChannelAddAdmin", C0338R.string.ChannelAddAdmin), C0338R.drawable.managers);
                    if (!ThemeUtil.m2490b()) {
                        return userCell;
                    }
                    ((TextCell) userCell).setTextColor(i3);
                    return userCell;
                } else if (itemViewType == 5) {
                    if (view == null) {
                        userCell = new HeaderCell(this.mContext);
                        userCell.setBackgroundColor(-1);
                        if (ThemeUtil.m2490b() && i2 != -1) {
                            userCell.setBackgroundColor(i2);
                        }
                    } else {
                        userCell = view;
                    }
                    ((HeaderCell) userCell).setText(LocaleController.getString("WhoCanAddMembers", C0338R.string.WhoCanAddMembers));
                    return userCell;
                } else if (itemViewType == 6) {
                    if (view == null) {
                        userCell = new RadioCell(this.mContext);
                        userCell.setBackgroundColor(-1);
                        if (ThemeUtil.m2490b() && i2 != -1) {
                            userCell.setBackgroundColor(i2);
                        }
                    } else {
                        userCell = view;
                    }
                    RadioCell radioCell = (RadioCell) userCell;
                    if (ThemeUtil.m2490b()) {
                        radioCell.setTextColor(i3);
                    }
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
                    String string2;
                    if (i == 1) {
                        radioCell.setTag(Integer.valueOf(0));
                        string2 = LocaleController.getString("WhoCanAddMembersAllMembers", C0338R.string.WhoCanAddMembersAllMembers);
                        boolean z2 = chat != null && chat.democracy;
                        radioCell.setText(string2, z2, true);
                        return userCell;
                    } else if (i != 2) {
                        return userCell;
                    } else {
                        radioCell.setTag(Integer.valueOf(1));
                        string2 = LocaleController.getString("WhoCanAddMembersAdmins", C0338R.string.WhoCanAddMembersAdmins);
                        if (chat == null || chat.democracy) {
                            z = false;
                        }
                        radioCell.setText(string2, z, false);
                        return userCell;
                    }
                }
                return view;
            }
        }

        public int getViewTypeCount() {
            return 7;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return getCount() == 0 || (ChannelUsersActivity.this.participants.isEmpty() && ChannelUsersActivity.this.loadingUsers);
        }

        public boolean isEnabled(int i) {
            boolean z = true;
            if (ChannelUsersActivity.this.type == 2) {
                if (ChannelUsersActivity.this.isAdmin) {
                    if (ChannelUsersActivity.this.isPublic) {
                        if (i == 0) {
                            return true;
                        }
                        if (i == 1) {
                            return false;
                        }
                    } else if (i == 0 || i == 1) {
                        return true;
                    } else {
                        if (i == 2) {
                            return false;
                        }
                    }
                }
            } else if (ChannelUsersActivity.this.type == 1) {
                if (i == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
                    return ChannelUsersActivity.this.isAdmin;
                }
                if (i == (ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) + 1) {
                    return false;
                }
                if (ChannelUsersActivity.this.isMegagroup && ChannelUsersActivity.this.isAdmin && i < 4) {
                    boolean z2 = i == 1 || i == 2;
                    return z2;
                }
            }
            if (i == ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow || ((ChannelParticipant) ChannelUsersActivity.this.participants.get(i - ChannelUsersActivity.this.participantsStartRow)).user_id == UserConfig.getClientUserId()) {
                z = false;
            }
            return z;
        }
    }

    public ChannelUsersActivity(Bundle bundle) {
        int i = 0;
        super(bundle);
        this.participants = new ArrayList();
        this.chatId = this.arguments.getInt("chat_id");
        this.type = this.arguments.getInt("type");
        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
        if (chat != null) {
            if (chat.creator) {
                this.isAdmin = true;
                this.isPublic = (chat.flags & 64) != 0;
            }
            this.isMegagroup = chat.megagroup;
        }
        if (this.type == 0) {
            this.participantsStartRow = 0;
        } else if (this.type == 1) {
            if (this.isAdmin && this.isMegagroup) {
                i = 4;
            }
            this.participantsStartRow = i;
        } else if (this.type == 2) {
            if (this.isAdmin) {
                i = this.isPublic ? 2 : 3;
            }
            this.participantsStartRow = i;
        }
    }

    private void addMultiContacts(List<Integer> list) {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        builder.setMessage(LocaleController.getString("ChannelAddMultiTo", C0338R.string.ChannelAddMultiTo));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C12427(list));
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    private int getChannelAdminParticipantType(ChannelParticipant channelParticipant) {
        return ((channelParticipant instanceof TL_channelParticipantCreator) || (channelParticipant instanceof TL_channelParticipantSelf)) ? 0 : channelParticipant instanceof TL_channelParticipantEditor ? 1 : 2;
    }

    private void getChannelParticipants(int i, int i2) {
        if (!this.loadingUsers) {
            this.loadingUsers = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
            TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
            tL_channels_getParticipants.channel = MessagesController.getInputChannel(this.chatId);
            if (this.type == 0) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsKicked();
            } else if (this.type == 1) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsAdmins();
            } else if (this.type == 2) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
            }
            tL_channels_getParticipants.offset = i;
            tL_channels_getParticipants.limit = i2;
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_channels_getParticipants, new C12416()), this.classGuid);
        }
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            this.fragmentView.setBackgroundColor(AdvanceTheme.aA);
        }
    }

    private void showUserMessages(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.chatId);
        bundle.putInt("just_from_id", i);
        presentFragment(new ChatActivity(bundle));
    }

    public View createView(Context context) {
        int i = 1;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.type == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlockedUsers", C0338R.string.ChannelBlockedUsers));
        } else if (this.type == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", C0338R.string.ChannelAdministrators));
        } else if (this.type == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelMembers", C0338R.string.ChannelMembers));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C12261());
        this.actionBar.createMenu();
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        initTheme();
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        if (this.type == 0) {
            if (this.isMegagroup) {
                this.emptyView.setText(LocaleController.getString("NoBlockedGroup", C0338R.string.NoBlockedGroup));
            } else {
                this.emptyView.setText(LocaleController.getString("NoBlocked", C0338R.string.NoBlocked));
            }
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        View listView = new ListView(context);
        listView.setEmptyView(this.emptyView);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setDrawSelectorOnTop(true);
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        if (!LocaleController.isRTL) {
            i = 2;
        }
        listView.setVerticalScrollbarPosition(i);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        listView.setOnItemClickListener(new C12292(listView));
        if (this.isAdmin || (this.isMegagroup && this.type == 0)) {
            listView.setOnItemLongClickListener(new C12333());
        }
        if (this.loadingUsers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoaded && ((ChatFull) objArr[0]).id == this.chatId) {
            AndroidUtilities.runOnUIThread(new C12375());
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
        initThemeActionBar();
    }

    public void setUserChannelRole(User user, ChannelParticipantRole channelParticipantRole) {
        if (user != null && channelParticipantRole != null) {
            TLObject tL_channels_editAdmin = new TL_channels_editAdmin();
            tL_channels_editAdmin.channel = MessagesController.getInputChannel(this.chatId);
            tL_channels_editAdmin.user_id = MessagesController.getInputUser(user);
            tL_channels_editAdmin.role = channelParticipantRole;
            ConnectionsManager.getInstance().sendRequest(tL_channels_editAdmin, new C12364());
        }
    }
}

package com.hanista.mobogram.mobo.p014n;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.query.SharedMediaQuery;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.OnScrollListener;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.messenger.volley.Request.Method;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.ChannelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantEditor;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantsRecent;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelRoleEditor;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_channelParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_editAdmin;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_getParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatChannelParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipants;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.Updates;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Cells.DividerCell;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.LoadingCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCell;
import com.hanista.mobogram.ui.Cells.TextDetailCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.AlertsCreator;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.RecyclerListView;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemClickListener;
import com.hanista.mobogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.DialogsActivity;
import com.hanista.mobogram.ui.DialogsActivity.DialogsActivityDelegate;
import com.hanista.mobogram.ui.ProfileActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

/* renamed from: com.hanista.mobogram.mobo.n.a */
public class ChatMembersActivity extends BaseFragment implements NotificationCenterDelegate, DialogsActivityDelegate {
    private RecyclerListView f1936a;
    private LinearLayoutManager f1937b;
    private ChatMembersActivity f1938c;
    private int f1939d;
    private long f1940e;
    private boolean f1941f;
    private HashMap<Integer, ChatParticipant> f1942g;
    private boolean f1943h;
    private ChatFull f1944i;
    private int f1945j;
    private ArrayList<Integer> f1946k;
    private Chat f1947l;
    private int f1948m;
    private int f1949n;
    private int f1950o;
    private int f1951p;
    private int f1952q;
    private ChatParticipants f1953r;
    private String f1954s;

    /* renamed from: com.hanista.mobogram.mobo.n.a.1 */
    class ChatMembersActivity implements Runnable {
        final /* synthetic */ Semaphore f1907a;
        final /* synthetic */ ChatMembersActivity f1908b;

        ChatMembersActivity(ChatMembersActivity chatMembersActivity, Semaphore semaphore) {
            this.f1908b = chatMembersActivity;
            this.f1907a = semaphore;
        }

        public void run() {
            this.f1908b.f1947l = MessagesStorage.getInstance().getChat(this.f1908b.f1939d);
            this.f1907a.release();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.n.a.2 */
    class ChatMembersActivity implements OnClickListener {
        final /* synthetic */ ArrayList f1909a;
        final /* synthetic */ ChatMembersActivity f1910b;

        ChatMembersActivity(ChatMembersActivity chatMembersActivity, ArrayList arrayList) {
            this.f1910b = chatMembersActivity;
            this.f1909a = arrayList;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i >= 0 && i < this.f1909a.size() && ((Integer) this.f1909a.get(i)).intValue() == 0 && this.f1910b.f1947l != null) {
                this.f1910b.m1918b(this.f1910b.f1945j);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.n.a.3 */
    class ChatMembersActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ChatMembersActivity f1911a;

        ChatMembersActivity(ChatMembersActivity chatMembersActivity) {
            this.f1911a = chatMembersActivity;
        }

        public void onItemClick(int i) {
            if (this.f1911a.getParentActivity() != null && i == -1) {
                this.f1911a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.n.a.4 */
    class ChatMembersActivity extends RecyclerListView {
        final /* synthetic */ ChatMembersActivity f1912a;

        ChatMembersActivity(ChatMembersActivity chatMembersActivity, Context context) {
            this.f1912a = chatMembersActivity;
            super(context);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.n.a.5 */
    class ChatMembersActivity extends LinearLayoutManager {
        final /* synthetic */ ChatMembersActivity f1913a;

        ChatMembersActivity(ChatMembersActivity chatMembersActivity, Context context) {
            this.f1913a = chatMembersActivity;
            super(context);
        }

        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.n.a.6 */
    class ChatMembersActivity implements OnItemClickListener {
        final /* synthetic */ ChatMembersActivity f1914a;

        ChatMembersActivity(ChatMembersActivity chatMembersActivity) {
            this.f1914a = chatMembersActivity;
        }

        public void onItemClick(View view, int i) {
            if (this.f1914a.getParentActivity() != null && i > this.f1914a.f1948m && i < this.f1914a.f1950o) {
                int i2 = !this.f1914a.f1946k.isEmpty() ? ((ChatParticipant) this.f1914a.f1944i.participants.participants.get(((Integer) this.f1914a.f1946k.get((i - this.f1914a.f1948m) - 1)).intValue())).user_id : ((ChatParticipant) this.f1914a.f1944i.participants.participants.get((i - this.f1914a.f1948m) - 1)).user_id;
                if (i2 != UserConfig.getClientUserId()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", i2);
                    this.f1914a.presentFragment(new ProfileActivity(bundle));
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.n.a.7 */
    class ChatMembersActivity implements OnItemLongClickListener {
        final /* synthetic */ ChatMembersActivity f1922a;

        /* renamed from: com.hanista.mobogram.mobo.n.a.7.1 */
        class ChatMembersActivity implements OnClickListener {
            final /* synthetic */ ChatParticipant f1919a;
            final /* synthetic */ ChatMembersActivity f1920b;

            /* renamed from: com.hanista.mobogram.mobo.n.a.7.1.1 */
            class ChatMembersActivity implements RequestDelegate {
                final /* synthetic */ ChatMembersActivity f1918a;

                /* renamed from: com.hanista.mobogram.mobo.n.a.7.1.1.1 */
                class ChatMembersActivity implements Runnable {
                    final /* synthetic */ ChatMembersActivity f1915a;

                    ChatMembersActivity(ChatMembersActivity chatMembersActivity) {
                        this.f1915a = chatMembersActivity;
                    }

                    public void run() {
                        MessagesController.getInstance().loadFullChat(this.f1915a.f1918a.f1920b.f1922a.f1939d, 0, true);
                    }
                }

                /* renamed from: com.hanista.mobogram.mobo.n.a.7.1.1.2 */
                class ChatMembersActivity implements Runnable {
                    final /* synthetic */ TL_error f1916a;
                    final /* synthetic */ ChatMembersActivity f1917b;

                    ChatMembersActivity(ChatMembersActivity chatMembersActivity, TL_error tL_error) {
                        this.f1917b = chatMembersActivity;
                        this.f1916a = tL_error;
                    }

                    public void run() {
                        AlertsCreator.showAddUserAlert(this.f1916a.text, this.f1917b.f1918a.f1920b.f1922a, false);
                    }
                }

                ChatMembersActivity(ChatMembersActivity chatMembersActivity) {
                    this.f1918a = chatMembersActivity;
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        MessagesController.getInstance().processUpdates((Updates) tLObject, false);
                        AndroidUtilities.runOnUIThread(new ChatMembersActivity(this), 1000);
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new ChatMembersActivity(this, tL_error));
                }
            }

            ChatMembersActivity(ChatMembersActivity chatMembersActivity, ChatParticipant chatParticipant) {
                this.f1920b = chatMembersActivity;
                this.f1919a = chatParticipant;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    TL_chatChannelParticipant tL_chatChannelParticipant = (TL_chatChannelParticipant) this.f1919a;
                    tL_chatChannelParticipant.channelParticipant = new TL_channelParticipantEditor();
                    tL_chatChannelParticipant.channelParticipant.inviter_id = UserConfig.getClientUserId();
                    tL_chatChannelParticipant.channelParticipant.user_id = this.f1919a.user_id;
                    tL_chatChannelParticipant.channelParticipant.date = this.f1919a.date;
                    TLObject tL_channels_editAdmin = new TL_channels_editAdmin();
                    tL_channels_editAdmin.channel = MessagesController.getInputChannel(this.f1920b.f1922a.f1939d);
                    tL_channels_editAdmin.user_id = MessagesController.getInputUser(this.f1920b.f1922a.f1945j);
                    tL_channels_editAdmin.role = new TL_channelRoleEditor();
                    ConnectionsManager.getInstance().sendRequest(tL_channels_editAdmin, new ChatMembersActivity(this));
                } else if (i == 1) {
                    this.f1920b.f1922a.m1913a(this.f1920b.f1922a.f1945j);
                } else if (i == 2) {
                    this.f1920b.f1922a.m1918b(this.f1920b.f1922a.f1945j);
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.n.a.7.2 */
        class ChatMembersActivity implements OnClickListener {
            final /* synthetic */ ChatMembersActivity f1921a;

            ChatMembersActivity(ChatMembersActivity chatMembersActivity) {
                this.f1921a = chatMembersActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    this.f1921a.f1922a.m1913a(this.f1921a.f1922a.f1945j);
                } else if (i == 1) {
                    this.f1921a.f1922a.m1918b(this.f1921a.f1922a.f1945j);
                }
            }
        }

        ChatMembersActivity(ChatMembersActivity chatMembersActivity) {
            this.f1922a = chatMembersActivity;
        }

        public boolean onItemClick(View view, int i) {
            if (i <= this.f1922a.f1948m || i >= this.f1922a.f1950o || this.f1922a.getParentActivity() == null) {
                return false;
            }
            int i2;
            ChatParticipant chatParticipant = !this.f1922a.f1946k.isEmpty() ? (ChatParticipant) this.f1922a.f1944i.participants.participants.get(((Integer) this.f1922a.f1946k.get((i - this.f1922a.f1948m) - 1)).intValue()) : (ChatParticipant) this.f1922a.f1944i.participants.participants.get((i - this.f1922a.f1948m) - 1);
            this.f1922a.f1945j = chatParticipant.user_id;
            boolean z;
            boolean z2;
            if (ChatObject.isChannel(this.f1922a.f1947l)) {
                User user;
                ChannelParticipant channelParticipant = ((TL_chatChannelParticipant) chatParticipant).channelParticipant;
                if (chatParticipant.user_id != UserConfig.getClientUserId()) {
                    if (this.f1922a.f1947l.creator) {
                        i2 = 1;
                    } else if ((channelParticipant instanceof TL_channelParticipant) && (this.f1922a.f1947l.editor || channelParticipant.inviter_id == UserConfig.getClientUserId())) {
                        i2 = 1;
                    }
                    user = MessagesController.getInstance().getUser(Integer.valueOf(chatParticipant.user_id));
                    if ((channelParticipant instanceof TL_channelParticipant) || user.bot) {
                        z = false;
                    } else {
                        int i3 = 1;
                    }
                }
                z2 = false;
                user = MessagesController.getInstance().getUser(Integer.valueOf(chatParticipant.user_id));
                if (channelParticipant instanceof TL_channelParticipant) {
                }
                z = false;
            } else {
                if (chatParticipant.user_id != UserConfig.getClientUserId()) {
                    if (this.f1922a.f1947l.creator) {
                        z = false;
                        i2 = 1;
                    } else if ((chatParticipant instanceof TL_chatParticipant) && ((this.f1922a.f1947l.admin && this.f1922a.f1947l.admins_enabled) || chatParticipant.inviter_id == UserConfig.getClientUserId())) {
                        z = false;
                        i2 = 1;
                    }
                }
                z = false;
                z2 = false;
            }
            if (i2 == 0) {
                this.f1922a.m1926d();
                return true;
            }
            Builder builder = new Builder(this.f1922a.getParentActivity());
            if (this.f1922a.f1947l.megagroup && this.f1922a.f1947l.creator && r4 != 0) {
                builder.setItems(new CharSequence[]{LocaleController.getString("SetAsAdmin", C0338R.string.SetAsAdmin), LocaleController.getString("KickFromGroup", C0338R.string.KickFromGroup), LocaleController.getString("ShowThisUserMessages", C0338R.string.ShowThisUserMessages)}, new ChatMembersActivity(this, chatParticipant));
            } else {
                CharSequence[] charSequenceArr = new CharSequence[2];
                charSequenceArr[0] = this.f1922a.f1939d > 0 ? LocaleController.getString("KickFromGroup", C0338R.string.KickFromGroup) : LocaleController.getString("KickFromBroadcast", C0338R.string.KickFromBroadcast);
                charSequenceArr[1] = LocaleController.getString("ShowThisUserMessages", C0338R.string.ShowThisUserMessages);
                builder.setItems(charSequenceArr, new ChatMembersActivity(this));
            }
            this.f1922a.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.n.a.8 */
    class ChatMembersActivity extends OnScrollListener {
        final /* synthetic */ ChatMembersActivity f1923a;

        ChatMembersActivity(ChatMembersActivity chatMembersActivity) {
            this.f1923a = chatMembersActivity;
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (this.f1923a.f1942g != null && this.f1923a.f1951p != -1 && this.f1923a.f1937b.findLastVisibleItemPosition() > this.f1923a.f1951p - 8) {
                this.f1923a.m1915a(false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.n.a.9 */
    class ChatMembersActivity implements RequestDelegate {
        final /* synthetic */ TL_channels_getParticipants f1927a;
        final /* synthetic */ int f1928b;
        final /* synthetic */ ChatMembersActivity f1929c;

        /* renamed from: com.hanista.mobogram.mobo.n.a.9.1 */
        class ChatMembersActivity implements Runnable {
            final /* synthetic */ TL_error f1924a;
            final /* synthetic */ TLObject f1925b;
            final /* synthetic */ ChatMembersActivity f1926c;

            ChatMembersActivity(ChatMembersActivity chatMembersActivity, TL_error tL_error, TLObject tLObject) {
                this.f1926c = chatMembersActivity;
                this.f1924a = tL_error;
                this.f1925b = tLObject;
            }

            public void run() {
                if (this.f1924a == null) {
                    TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) this.f1925b;
                    MessagesController.getInstance().putUsers(tL_channels_channelParticipants.users, false);
                    if (tL_channels_channelParticipants.users.size() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                        this.f1926c.f1929c.f1943h = true;
                    }
                    if (this.f1926c.f1927a.offset == 0) {
                        this.f1926c.f1929c.f1942g.clear();
                        this.f1926c.f1929c.f1944i.participants = new TL_chatParticipants();
                        MessagesStorage.getInstance().putUsersAndChats(tL_channels_channelParticipants.users, null, true, true);
                    }
                    for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
                        TL_chatChannelParticipant tL_chatChannelParticipant = new TL_chatChannelParticipant();
                        tL_chatChannelParticipant.channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i);
                        tL_chatChannelParticipant.inviter_id = tL_chatChannelParticipant.channelParticipant.inviter_id;
                        tL_chatChannelParticipant.user_id = tL_chatChannelParticipant.channelParticipant.user_id;
                        tL_chatChannelParticipant.date = tL_chatChannelParticipant.channelParticipant.date;
                        if (!this.f1926c.f1929c.f1942g.containsKey(Integer.valueOf(tL_chatChannelParticipant.user_id))) {
                            if (this.f1926c.f1929c.f1954s != null) {
                                User user = MessagesController.getInstance().getUser(Integer.valueOf(tL_chatChannelParticipant.user_id));
                                if ((user.first_name != null && user.first_name.toLowerCase().contains(this.f1926c.f1929c.f1954s)) || ((user.last_name != null && user.last_name.toLowerCase().contains(this.f1926c.f1929c.f1954s)) || (user.username != null && user.username.toLowerCase().contains(this.f1926c.f1929c.f1954s)))) {
                                    this.f1926c.f1929c.f1944i.participants.participants.add(tL_chatChannelParticipant);
                                    this.f1926c.f1929c.f1942g.put(Integer.valueOf(tL_chatChannelParticipant.user_id), tL_chatChannelParticipant);
                                }
                            } else {
                                this.f1926c.f1929c.f1944i.participants.participants.add(tL_chatChannelParticipant);
                                this.f1926c.f1929c.f1942g.put(Integer.valueOf(tL_chatChannelParticipant.user_id), tL_chatChannelParticipant);
                            }
                        }
                    }
                }
                this.f1926c.f1929c.f1941f = false;
                this.f1926c.f1929c.m1917b();
                if (this.f1926c.f1929c.f1938c != null) {
                    this.f1926c.f1929c.f1938c.notifyDataSetChanged();
                }
            }
        }

        ChatMembersActivity(ChatMembersActivity chatMembersActivity, TL_channels_getParticipants tL_channels_getParticipants, int i) {
            this.f1929c = chatMembersActivity;
            this.f1927a = tL_channels_getParticipants;
            this.f1928b = i;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new ChatMembersActivity(this, tL_error, tLObject), (long) this.f1928b);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.n.a.a */
    private class ChatMembersActivity extends Adapter {
        final /* synthetic */ ChatMembersActivity f1934a;
        private Context f1935b;

        /* renamed from: com.hanista.mobogram.mobo.n.a.a.1 */
        class ChatMembersActivity extends TextDetailCell {
            final /* synthetic */ ChatMembersActivity f1930a;

            ChatMembersActivity(ChatMembersActivity chatMembersActivity, Context context) {
                this.f1930a = chatMembersActivity;
                super(context);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.n.a.a.2 */
        class ChatMembersActivity extends TextCell {
            final /* synthetic */ ChatMembersActivity f1931a;

            ChatMembersActivity(ChatMembersActivity chatMembersActivity, Context context) {
                this.f1931a = chatMembersActivity;
                super(context);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.n.a.a.3 */
        class ChatMembersActivity extends UserCell {
            final /* synthetic */ ChatMembersActivity f1932a;

            ChatMembersActivity(ChatMembersActivity chatMembersActivity, Context context, int i, int i2, boolean z) {
                this.f1932a = chatMembersActivity;
                super(context, i, i2, z);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (VERSION.SDK_INT >= 21 && getBackground() != null && (motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
                    getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.n.a.a.a */
        private class ChatMembersActivity extends ViewHolder {
            final /* synthetic */ ChatMembersActivity f1933a;

            public ChatMembersActivity(ChatMembersActivity chatMembersActivity, View view) {
                this.f1933a = chatMembersActivity;
                super(view);
            }
        }

        public ChatMembersActivity(ChatMembersActivity chatMembersActivity, Context context) {
            this.f1934a = chatMembersActivity;
            this.f1935b = context;
        }

        public int getItemCount() {
            return this.f1934a.f1952q;
        }

        public int getItemViewType(int i) {
            return i == this.f1934a.f1948m ? 0 : (i <= this.f1934a.f1948m || i >= this.f1934a.f1950o) ? i == this.f1934a.f1949n ? 5 : i == this.f1934a.f1951p ? 7 : 0 : 4;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder r9, int r10) {
            /*
            r8 = this;
            r3 = 2130838118; // 0x7f020266 float:1.728121E38 double:1.052773911E-314;
            r5 = 1;
            r7 = 0;
            r4 = 0;
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aB;
            r0 = r9.getItemViewType();
            switch(r0) {
                case 0: goto L_0x004b;
                case 1: goto L_0x000f;
                case 2: goto L_0x0071;
                case 3: goto L_0x0085;
                case 4: goto L_0x009b;
                case 5: goto L_0x000f;
                case 6: goto L_0x000f;
                case 7: goto L_0x000f;
                case 8: goto L_0x0175;
                default: goto L_0x000f;
            };
        L_0x000f:
            r0 = r4;
        L_0x0010:
            if (r0 == 0) goto L_0x004a;
        L_0x0012:
            r0 = r8.f1934a;
            r0 = r0.f1939d;
            if (r0 == 0) goto L_0x002b;
        L_0x001a:
            r0 = r8.f1934a;
            r0 = r0.f1948m;
            if (r10 <= r0) goto L_0x002b;
        L_0x0022:
            r0 = r8.f1934a;
            r0 = r0.f1950o;
            if (r10 >= r0) goto L_0x002b;
        L_0x002a:
            r4 = r5;
        L_0x002b:
            if (r4 == 0) goto L_0x018a;
        L_0x002d:
            r0 = r9.itemView;
            r0 = r0.getBackground();
            if (r0 != 0) goto L_0x004a;
        L_0x0035:
            r0 = r9.itemView;
            r1 = 2130838069; // 0x7f020235 float:1.728111E38 double:1.0527738867E-314;
            r0.setBackgroundResource(r1);
            r0 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r0 == 0) goto L_0x004a;
        L_0x0043:
            r0 = r9.itemView;
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aA;
            r0.setBackgroundColor(r1);
        L_0x004a:
            return;
        L_0x004b:
            r0 = r8.f1934a;
            r0 = r0.f1948m;
            if (r10 != r0) goto L_0x0062;
        L_0x0053:
            r0 = r9.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.EmptyCell) r0;
            r1 = 1090519040; // 0x41000000 float:8.0 double:5.38787994E-315;
            r1 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r1);
            r0.setHeight(r1);
            r0 = r5;
            goto L_0x0010;
        L_0x0062:
            r0 = r9.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.EmptyCell) r0;
            r1 = 1108344832; // 0x42100000 float:36.0 double:5.47595105E-315;
            r1 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r1);
            r0.setHeight(r1);
            r0 = r5;
            goto L_0x0010;
        L_0x0071:
            r0 = r9.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.TextDetailCell) r0;
            r2 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r2 == 0) goto L_0x0199;
        L_0x007b:
            r0.setTextColor(r1);
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aH;
            r0.setValueColor(r1);
            r0 = r5;
            goto L_0x0010;
        L_0x0085:
            r0 = r9.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.TextCell) r0;
            r2 = -14606047; // 0xffffffffff212121 float:-2.1417772E38 double:NaN;
            r0.setTextColor(r2);
            r2 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r2 == 0) goto L_0x0199;
        L_0x0095:
            r0.setTextColor(r1);
            r0 = r5;
            goto L_0x0010;
        L_0x009b:
            r0 = r9.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.UserCell) r0;
            r1 = r8.f1934a;
            r1 = r1.f1946k;
            r1 = r1.isEmpty();
            if (r1 != 0) goto L_0x0101;
        L_0x00ab:
            r1 = r8.f1934a;
            r1 = r1.f1944i;
            r1 = r1.participants;
            r2 = r1.participants;
            r1 = r8.f1934a;
            r1 = r1.f1946k;
            r6 = r8.f1934a;
            r6 = r6.f1948m;
            r6 = r10 - r6;
            r6 = r6 + -1;
            r1 = r1.get(r6);
            r1 = (java.lang.Integer) r1;
            r1 = r1.intValue();
            r1 = r2.get(r1);
            r1 = (com.hanista.mobogram.tgnet.TLRPC.ChatParticipant) r1;
            r2 = r1;
        L_0x00d6:
            if (r2 == 0) goto L_0x0199;
        L_0x00d8:
            r1 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_chatChannelParticipant;
            if (r1 == 0) goto L_0x013c;
        L_0x00dc:
            r1 = r2;
            r1 = (com.hanista.mobogram.tgnet.TLRPC.TL_chatChannelParticipant) r1;
            r1 = r1.channelParticipant;
            r6 = com.hanista.mobogram.messenger.MessagesController.getInstance();
            r2 = r2.user_id;
            r2 = java.lang.Integer.valueOf(r2);
            r2 = r6.getUser(r2);
            r6 = r1 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantCreator;
            if (r6 == 0) goto L_0x011d;
        L_0x00f3:
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2485a();
            r1 = r1.m2300n();
        L_0x00fb:
            r0.setData(r2, r7, r7, r1);
            r0 = r5;
            goto L_0x0010;
        L_0x0101:
            r1 = r8.f1934a;
            r1 = r1.f1944i;
            r1 = r1.participants;
            r1 = r1.participants;
            r2 = r8.f1934a;
            r2 = r2.f1948m;
            r2 = r10 - r2;
            r2 = r2 + -1;
            r1 = r1.get(r2);
            r1 = (com.hanista.mobogram.tgnet.TLRPC.ChatParticipant) r1;
            r2 = r1;
            goto L_0x00d6;
        L_0x011d:
            r6 = r1 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantEditor;
            if (r6 != 0) goto L_0x0125;
        L_0x0121:
            r1 = r1 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_channelParticipantModerator;
            if (r1 == 0) goto L_0x012e;
        L_0x0125:
            r1 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2485a();
            r1 = r1.m2296j();
            goto L_0x00fb;
        L_0x012e:
            r1 = r8.f1934a;
            r1 = r1.f1948m;
            r1 = r1 + 1;
            if (r10 != r1) goto L_0x013a;
        L_0x0138:
            r1 = r3;
            goto L_0x00fb;
        L_0x013a:
            r1 = r4;
            goto L_0x00fb;
        L_0x013c:
            r1 = com.hanista.mobogram.messenger.MessagesController.getInstance();
            r6 = r2.user_id;
            r6 = java.lang.Integer.valueOf(r6);
            r1 = r1.getUser(r6);
            r6 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantCreator;
            if (r6 == 0) goto L_0x015c;
        L_0x014e:
            r2 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2485a();
            r3 = r2.m2300n();
        L_0x0156:
            r0.setData(r1, r7, r7, r3);
            r0 = r5;
            goto L_0x0010;
        L_0x015c:
            r2 = r2 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_chatParticipantAdmin;
            if (r2 == 0) goto L_0x0169;
        L_0x0160:
            r2 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2485a();
            r3 = r2.m2296j();
            goto L_0x0156;
        L_0x0169:
            r2 = r8.f1934a;
            r2 = r2.f1948m;
            r2 = r2 + 1;
            if (r10 == r2) goto L_0x0156;
        L_0x0173:
            r3 = r4;
            goto L_0x0156;
        L_0x0175:
            r0 = r9.itemView;
            r0 = (com.hanista.mobogram.ui.Cells.AboutLinkCell) r0;
            r2 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();
            if (r2 == 0) goto L_0x0199;
        L_0x017f:
            r0.setTextColor(r1);
            r1 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.aH;
            r0.setLinkColor(r1);
            r0 = r5;
            goto L_0x0010;
        L_0x018a:
            r0 = r9.itemView;
            r0 = r0.getBackground();
            if (r0 == 0) goto L_0x004a;
        L_0x0192:
            r0 = r9.itemView;
            r0.setBackgroundDrawable(r7);
            goto L_0x004a;
        L_0x0199:
            r0 = r5;
            goto L_0x0010;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.mobo.n.a.a.onBindViewHolder(com.hanista.mobogram.messenger.support.widget.RecyclerView$ViewHolder, int):void");
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            int i2 = AdvanceTheme.aA;
            int i3 = AdvanceTheme.aK;
            int i4 = AdvanceTheme.aB;
            View view = null;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    view = new EmptyCell(this.f1935b);
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    view = new DividerCell(this.f1935b);
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
                case VideoPlayer.STATE_PREPARING /*2*/:
                    view = new ChatMembersActivity(this, this.f1935b);
                    if (ThemeUtil.m2490b()) {
                        ((TextDetailCell) view).setTextColor(i4);
                        ((TextDetailCell) view).setValueColor(AdvanceTheme.aH);
                        view.setBackgroundColor(0);
                        break;
                    }
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    view = new ChatMembersActivity(this, this.f1935b);
                    if (ThemeUtil.m2490b()) {
                        view.setBackgroundColor(0);
                        ((TextCell) view).setTextColor(i4);
                        break;
                    }
                    break;
                case VideoPlayer.STATE_READY /*4*/:
                    View chatMembersActivity = new ChatMembersActivity(this, this.f1935b, 61, 0, true);
                    if (!ThemeUtil.m2490b()) {
                        view = chatMembersActivity;
                        break;
                    }
                    chatMembersActivity.setBackgroundColor(0);
                    chatMembersActivity.setTag("Profile");
                    view = chatMembersActivity;
                    break;
                case VideoPlayer.STATE_ENDED /*5*/:
                    view = new ShadowSectionCell(this.f1935b);
                    if (ThemeUtil.m2490b()) {
                        view = new ShadowSectionCell(this.f1935b, false);
                        if (i2 != -1 || i3 > 0) {
                            view.setBackgroundColor(0);
                            break;
                        }
                    }
                    break;
                case Method.TRACE /*6*/:
                    view = new TextInfoPrivacyCell(this.f1935b);
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) view;
                    textInfoPrivacyCell.setBackgroundResource(C0338R.drawable.greydivider);
                    textInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ConvertGroupInfo", C0338R.string.ConvertGroupInfo, LocaleController.formatPluralString("Members", MessagesController.getInstance().maxMegagroupCount))));
                    if (ThemeUtil.m2490b() && (i2 != -1 || i3 > 0)) {
                        view.setBackgroundColor(0);
                        break;
                    }
                case Method.PATCH /*7*/:
                    view = new LoadingCell(this.f1935b);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new ChatMembersActivity(this, view);
        }
    }

    public ChatMembersActivity(Bundle bundle) {
        super(bundle);
        this.f1942g = new HashMap();
        this.f1952q = 0;
    }

    private void m1912a() {
        if ((this.f1944i instanceof TL_channelFull) && this.f1944i.participants != null) {
            for (int i = 0; i < this.f1944i.participants.participants.size(); i++) {
                ChatParticipant chatParticipant = (ChatParticipant) this.f1944i.participants.participants.get(i);
                this.f1942g.put(Integer.valueOf(chatParticipant.user_id), chatParticipant);
            }
        }
    }

    private void m1913a(int i) {
        int i2 = 0;
        if (i != 0) {
            MessagesController.getInstance().deleteUserFromChat(this.f1939d, MessagesController.getInstance().getUser(Integer.valueOf(i)), this.f1944i);
            if (this.f1947l.megagroup && this.f1944i != null && this.f1944i.participants != null) {
                int i3;
                for (i3 = 0; i3 < this.f1944i.participants.participants.size(); i3++) {
                    if (((TL_chatChannelParticipant) this.f1944i.participants.participants.get(i3)).channelParticipant.user_id == i) {
                        if (this.f1944i != null) {
                            ChatFull chatFull = this.f1944i;
                            chatFull.participants_count--;
                        }
                        this.f1944i.participants.participants.remove(i3);
                        i3 = 1;
                        if (!(this.f1944i == null || this.f1944i.participants == null)) {
                            while (i2 < this.f1944i.participants.participants.size()) {
                                if (((ChatParticipant) this.f1944i.participants.participants.get(i2)).user_id != i) {
                                    this.f1944i.participants.participants.remove(i2);
                                    i3 = 1;
                                    break;
                                }
                                i2++;
                            }
                        }
                        if (i3 == 0) {
                            m1917b();
                            this.f1938c.notifyDataSetChanged();
                            return;
                        }
                        return;
                    }
                }
                i3 = 0;
                while (i2 < this.f1944i.participants.participants.size()) {
                    if (((ChatParticipant) this.f1944i.participants.participants.get(i2)).user_id != i) {
                        i2++;
                    } else {
                        this.f1944i.participants.participants.remove(i2);
                        i3 = 1;
                        break;
                        if (i3 == 0) {
                            m1917b();
                            this.f1938c.notifyDataSetChanged();
                            return;
                        }
                        return;
                    }
                }
                if (i3 == 0) {
                    m1917b();
                    this.f1938c.notifyDataSetChanged();
                    return;
                }
                return;
            }
            return;
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.f1939d)));
        } else {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance().deleteUserFromChat(this.f1939d, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), this.f1944i);
        finishFragment();
    }

    private void m1915a(boolean z) {
        int i = 0;
        if (!this.f1941f && this.f1942g != null && this.f1944i != null) {
            this.f1941f = true;
            int i2 = (this.f1942g.isEmpty() || !z) ? 0 : 300;
            TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
            tL_channels_getParticipants.channel = MessagesController.getInputChannel(this.f1939d);
            tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
            if (!z) {
                i = this.f1942g.size();
            }
            tL_channels_getParticipants.offset = i;
            tL_channels_getParticipants.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_channels_getParticipants, new ChatMembersActivity(this, tL_channels_getParticipants, i2)), this.classGuid);
        }
    }

    private void m1917b() {
        this.f1950o = -1;
        this.f1948m = -1;
        this.f1949n = -1;
        this.f1951p = -1;
        this.f1952q = 0;
        if (this.f1939d == 0) {
            return;
        }
        int i;
        if (this.f1939d > 0) {
            if (!ChatObject.isChannel(this.f1947l)) {
                i = this.f1952q;
                this.f1952q = i + 1;
                this.f1949n = i;
                if (this.f1944i != null && !(this.f1944i.participants instanceof TL_chatParticipantsForbidden)) {
                    i = this.f1952q;
                    this.f1952q = i + 1;
                    this.f1948m = i;
                    this.f1952q += this.f1944i.participants.participants.size();
                    this.f1950o = this.f1952q;
                }
            } else if (this.f1944i != null && this.f1944i.participants != null && !this.f1944i.participants.participants.isEmpty()) {
                i = this.f1952q;
                this.f1952q = i + 1;
                this.f1949n = i;
                i = this.f1952q;
                this.f1952q = i + 1;
                this.f1948m = i;
                this.f1952q += this.f1944i.participants.participants.size();
                this.f1950o = this.f1952q;
                if (!this.f1943h) {
                    i = this.f1952q;
                    this.f1952q = i + 1;
                    this.f1951p = i;
                }
            }
        } else if (!ChatObject.isChannel(this.f1947l) && this.f1944i != null && !(this.f1944i.participants instanceof TL_chatParticipantsForbidden)) {
            i = this.f1952q;
            this.f1952q = i + 1;
            this.f1948m = i;
            this.f1952q += this.f1944i.participants.participants.size();
            this.f1950o = this.f1952q;
        }
    }

    private void m1918b(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("chat_id", this.f1939d);
        bundle.putInt("just_from_id", i);
        presentFragment(new ChatActivity(bundle));
    }

    private void m1922c() {
        this.actionBar.createMenu().addItem(0, (int) C0338R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            final /* synthetic */ com.hanista.mobogram.mobo.p014n.ChatMembersActivity f1906a;

            {
                this.f1906a = r1;
            }

            public void onSearchCollapse() {
                this.f1906a.f1954s = null;
                this.f1906a.f1936a.setVerticalScrollBarEnabled(false);
                if (this.f1906a.f1947l.megagroup) {
                    this.f1906a.m1915a(true);
                } else if (this.f1906a.f1944i != null && this.f1906a.f1953r != null) {
                    this.f1906a.f1944i.participants = this.f1906a.f1953r;
                    this.f1906a.m1917b();
                    if (this.f1906a.f1938c != null) {
                        this.f1906a.f1938c.notifyDataSetChanged();
                    }
                }
            }

            public void onSearchExpand() {
            }

            public void onTextChanged(EditText editText) {
                this.f1906a.f1954s = editText.getText().toString().toLowerCase();
                if (this.f1906a.f1947l.megagroup) {
                    this.f1906a.m1915a(true);
                } else if (this.f1906a.f1944i != null && this.f1906a.f1953r != null) {
                    HashMap hashMap = new HashMap();
                    this.f1906a.f1944i.participants = new TL_chatParticipants();
                    for (int i = 0; i < this.f1906a.f1953r.participants.size(); i++) {
                        ChatParticipant chatParticipant = (ChatParticipant) this.f1906a.f1953r.participants.get(i);
                        if (!hashMap.containsKey(Integer.valueOf(chatParticipant.user_id))) {
                            if (this.f1906a.f1954s != null) {
                                User user = MessagesController.getInstance().getUser(Integer.valueOf(chatParticipant.user_id));
                                if ((user.first_name != null && user.first_name.toLowerCase().contains(this.f1906a.f1954s)) || ((user.last_name != null && user.last_name.toLowerCase().contains(this.f1906a.f1954s)) || (user.username != null && user.username.toLowerCase().contains(this.f1906a.f1954s)))) {
                                    this.f1906a.f1944i.participants.participants.add(chatParticipant);
                                    hashMap.put(Integer.valueOf(chatParticipant.user_id), chatParticipant);
                                }
                            } else {
                                this.f1906a.f1944i.participants.participants.add(chatParticipant);
                                hashMap.put(Integer.valueOf(chatParticipant.user_id), chatParticipant);
                            }
                        }
                    }
                    this.f1906a.m1917b();
                    if (this.f1906a.f1938c != null) {
                        this.f1906a.f1938c.notifyDataSetChanged();
                    }
                }
            }
        }).getSearchField().setHint(LocaleController.getString("NameOrUsername", C0338R.string.NameOrUsername));
    }

    private void m1926d() {
        Builder builder = new Builder(getParentActivity());
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(LocaleController.getString("ShowThisUserMessages", C0338R.string.ShowThisUserMessages));
        arrayList2.add(Integer.valueOf(0));
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new ChatMembersActivity(this, arrayList2));
        builder.setTitle(LocaleController.getString("User", C0338R.string.User));
        showDialog(builder.create());
    }

    private void m1928e() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.aA;
            int i2 = AdvanceTheme.aK;
            if (i2 > 0) {
                Orientation orientation;
                switch (i2) {
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i3 = AdvanceTheme.aL;
                this.f1936a.setBackgroundDrawable(new GradientDrawable(orientation, new int[]{i, i3}));
                return;
            }
            this.f1936a.setBackgroundColor(i);
        }
    }

    private void m1930f() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.aM;
            this.actionBar.setBackgroundColor(i);
            this.f1936a.setGlowColor(i);
            int i2 = AdvanceTheme.aN;
            if (i2 > 0) {
                Orientation orientation;
                int i3 = AdvanceTheme.aO;
                switch (i2) {
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                this.actionBar.setBackgroundDrawable(new GradientDrawable(orientation, new int[]{i, i3}));
            }
        }
    }

    public void m1943a(ChatFull chatFull) {
        this.f1944i = chatFull;
        if (!(this.f1944i == null || this.f1944i.migrated_from_chat_id == 0)) {
            this.f1940e = (long) (-this.f1944i.migrated_from_chat_id);
        }
        if (this.f1944i != null) {
            this.f1953r = this.f1944i.participants;
        }
        m1912a();
    }

    public View createView(Context context) {
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_back);
            drawable.setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            this.actionBar.setBackButtonDrawable(drawable);
        } else {
            this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SearchMembers", C0338R.string.SearchMembers));
        this.actionBar.setActionBarMenuOnItemClick(new ChatMembersActivity(this));
        m1922c();
        this.f1938c = new ChatMembersActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f1936a = new ChatMembersActivity(this, context);
        this.f1936a.setTag(Integer.valueOf(6));
        this.f1936a.setBackgroundColor(-1);
        initThemeBackground(this.f1936a);
        this.f1936a.setVerticalScrollBarEnabled(false);
        this.f1936a.setItemAnimator(null);
        this.f1936a.setLayoutAnimation(null);
        this.f1936a.setClipToPadding(false);
        this.f1937b = new ChatMembersActivity(this, context);
        this.f1937b.setOrientation(1);
        this.f1936a.setLayoutManager(this.f1937b);
        RecyclerListView recyclerListView = this.f1936a;
        int i = (!ChatObject.isChannel(this.f1939d) || this.f1947l.megagroup) ? this.f1939d : 5;
        recyclerListView.setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
        frameLayout.addView(this.f1936a, LayoutHelper.createFrame(-1, -1, 51));
        this.f1936a.setAdapter(this.f1938c);
        this.f1936a.setOnItemClickListener(new ChatMembersActivity(this));
        this.f1936a.setOnItemLongClickListener(new ChatMembersActivity(this));
        this.f1936a.setOnScrollListener(new ChatMembersActivity(this));
        m1928e();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        int i2 = 0;
        Chat chat;
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if (this.f1939d != 0) {
                if ((intValue & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(this.f1939d));
                    if (chat != null) {
                        this.f1947l = chat;
                        m1917b();
                        if (this.f1938c != null) {
                            this.f1938c.notifyDataSetChanged();
                        }
                    }
                }
                if ((intValue & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                    m1917b();
                    if (this.f1938c != null) {
                        this.f1938c.notifyDataSetChanged();
                    }
                }
                if (((intValue & 2) != 0 || (intValue & 1) != 0 || (intValue & 4) != 0) && this.f1936a != null) {
                    int childCount = this.f1936a.getChildCount();
                    while (i2 < childCount) {
                        View childAt = this.f1936a.getChildAt(i2);
                        if (childAt instanceof UserCell) {
                            ((UserCell) childAt).update(intValue);
                        }
                        i2++;
                    }
                }
            }
        } else if (i == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr[0];
            if (chatFull.id == this.f1939d) {
                boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
                if ((this.f1944i instanceof TL_channelFull) && chatFull.participants == null && this.f1944i != null) {
                    chatFull.participants = this.f1944i.participants;
                }
                boolean z = this.f1944i == null && (chatFull instanceof TL_channelFull);
                this.f1944i = chatFull;
                if (this.f1940e == 0 && this.f1944i.migrated_from_chat_id != 0) {
                    this.f1940e = (long) (-this.f1944i.migrated_from_chat_id);
                    SharedMediaQuery.getMediaCount(this.f1940e, 0, this.classGuid, true);
                }
                m1912a();
                m1917b();
                if (this.f1938c != null) {
                    this.f1938c.notifyDataSetChanged();
                }
                chat = MessagesController.getInstance().getChat(Integer.valueOf(this.f1939d));
                if (chat != null) {
                    this.f1947l = chat;
                }
                if (!this.f1947l.megagroup) {
                    return;
                }
                if (z || !booleanValue) {
                    m1915a(true);
                }
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
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
            }
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (this.f1936a != null) {
            this.f1936a.invalidateViews();
        }
    }

    public boolean onFragmentCreate() {
        this.f1939d = getArguments().getInt("chat_id", 0);
        if (this.f1939d == 0) {
            return false;
        }
        this.f1947l = MessagesController.getInstance().getChat(Integer.valueOf(this.f1939d));
        if (this.f1947l == null) {
            Semaphore semaphore = new Semaphore(0);
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new ChatMembersActivity(this, semaphore));
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (this.f1947l == null) {
                return false;
            }
            MessagesController.getInstance().putChat(this.f1947l, true);
        }
        if (this.f1947l.megagroup) {
            m1915a(true);
        } else {
            this.f1942g = null;
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        this.f1946k = new ArrayList();
        if (ChatObject.isChannel(this.f1947l)) {
            MessagesController.getInstance().loadFullChat(this.f1939d, this.classGuid, true);
        }
        if (this.f1939d > 0) {
            SharedMediaQuery.getMediaCount((long) (-this.f1939d), 0, this.classGuid, true);
            if (this.f1940e != 0) {
                SharedMediaQuery.getMediaCount(this.f1940e, 0, this.classGuid, true);
            }
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        m1917b();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        if (this.f1939d != 0) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.f1938c != null) {
            this.f1938c.notifyDataSetChanged();
        }
        m1930f();
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.f1939d != 0) {
            MessagesController.getInstance().loadChatInfo(this.f1939d, null, false);
        }
    }
}

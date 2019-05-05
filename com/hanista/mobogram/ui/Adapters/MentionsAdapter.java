package com.hanista.mobogram.ui.Adapters;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.SendMessagesHelper.LocationProvider;
import com.hanista.mobogram.messenger.SendMessagesHelper.LocationProvider.LocationProviderDelegate;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.query.SearchQuery;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.BotInfo;
import com.hanista.mobogram.tgnet.TLRPC.BotInlineResult;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ChatParticipant;
import com.hanista.mobogram.tgnet.TLRPC.TL_botCommand;
import com.hanista.mobogram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import com.hanista.mobogram.tgnet.TLRPC.TL_channelFull;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_resolveUsername;
import com.hanista.mobogram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import com.hanista.mobogram.tgnet.TLRPC.TL_document;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inlineBotSwitchPM;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputGeoPoint;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_botResults;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_getInlineBotResults;
import com.hanista.mobogram.tgnet.TLRPC.TL_photo;
import com.hanista.mobogram.tgnet.TLRPC.TL_topPeer;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Cells.BotSwitchCell;
import com.hanista.mobogram.ui.Cells.ContextLinkCell;
import com.hanista.mobogram.ui.Cells.ContextLinkCell.ContextLinkCellDelegate;
import com.hanista.mobogram.ui.Cells.MentionCell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

public class MentionsAdapter extends BaseSearchAdapterRecycler {
    private boolean allowNewMentions;
    private HashMap<Integer, BotInfo> botInfo;
    private int botsCount;
    private boolean contextMedia;
    private int contextQueryReqid;
    private Runnable contextQueryRunnable;
    private int contextUsernameReqid;
    private MentionsAdapterDelegate delegate;
    private long dialog_id;
    private User foundContextBot;
    private ChatFull info;
    private boolean isDarkTheme;
    private Location lastKnownLocation;
    private int lastPosition;
    private String lastText;
    private LocationProvider locationProvider;
    private Context mContext;
    private ArrayList<MessageObject> messages;
    private boolean needBotContext;
    private boolean needUsernames;
    private String nextQueryOffset;
    private boolean noUserName;
    private BaseFragment parentFragment;
    private int resultLength;
    private int resultStartPosition;
    private ArrayList<BotInlineResult> searchResultBotContext;
    private HashMap<String, BotInlineResult> searchResultBotContextById;
    private TL_inlineBotSwitchPM searchResultBotContextSwitch;
    private ArrayList<String> searchResultCommands;
    private ArrayList<String> searchResultCommandsHelp;
    private ArrayList<User> searchResultCommandsUsers;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<User> searchResultUsernames;
    private String searchingContextQuery;
    private String searchingContextUsername;

    /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.1 */
    class C10271 implements LocationProviderDelegate {
        C10271() {
        }

        public void onLocationAcquired(Location location) {
            if (MentionsAdapter.this.foundContextBot != null && MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                MentionsAdapter.this.lastKnownLocation = location;
                MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, MentionsAdapter.this.searchingContextQuery, TtmlNode.ANONYMOUS_REGION_ID);
            }
        }

        public void onUnableLocationAcquire() {
            MentionsAdapter.this.onLocationUnavailable();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.2 */
    class C10282 extends LocationProvider {
        C10282(LocationProviderDelegate locationProviderDelegate) {
            super(locationProviderDelegate);
        }

        public void stop() {
            super.stop();
            MentionsAdapter.this.lastKnownLocation = null;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.3 */
    class C10333 implements Runnable {
        final /* synthetic */ String val$query;
        final /* synthetic */ String val$username;

        /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.3.1 */
        class C10321 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.3.1.1 */
            class C10311 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.3.1.1.1 */
                class C10291 implements OnClickListener {
                    final /* synthetic */ User val$foundContextBotFinal;

                    C10291(User user) {
                        this.val$foundContextBotFinal = user;
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (this.val$foundContextBotFinal != null) {
                            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("inlinegeo_" + this.val$foundContextBotFinal.id, true).commit();
                            MentionsAdapter.this.checkLocationPermissionsOrStart();
                        }
                    }
                }

                /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.3.1.1.2 */
                class C10302 implements OnClickListener {
                    C10302() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        MentionsAdapter.this.onLocationUnavailable();
                    }
                }

                C10311(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    if (MentionsAdapter.this.searchingContextUsername != null && MentionsAdapter.this.searchingContextUsername.equals(C10333.this.val$username)) {
                        MentionsAdapter.this.contextUsernameReqid = 0;
                        MentionsAdapter.this.foundContextBot = null;
                        MentionsAdapter.this.locationProvider.stop();
                        if (this.val$error == null) {
                            TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) this.val$response;
                            if (!tL_contacts_resolvedPeer.users.isEmpty()) {
                                User user = (User) tL_contacts_resolvedPeer.users.get(0);
                                if (user.bot && user.bot_inline_placeholder != null) {
                                    MessagesController.getInstance().putUser(user, false);
                                    MessagesStorage.getInstance().putUsersAndChats(tL_contacts_resolvedPeer.users, null, true, true);
                                    MentionsAdapter.this.foundContextBot = user;
                                    if (MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                                        if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("inlinegeo_" + MentionsAdapter.this.foundContextBot.id, false) || MentionsAdapter.this.parentFragment == null || MentionsAdapter.this.parentFragment.getParentActivity() == null) {
                                            MentionsAdapter.this.checkLocationPermissionsOrStart();
                                        } else {
                                            User access$000 = MentionsAdapter.this.foundContextBot;
                                            Builder builder = new Builder(MentionsAdapter.this.parentFragment.getParentActivity());
                                            builder.setTitle(LocaleController.getString("ShareYouLocationTitle", C0338R.string.ShareYouLocationTitle));
                                            builder.setMessage(LocaleController.getString("ShareYouLocationInline", C0338R.string.ShareYouLocationInline));
                                            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C10291(access$000));
                                            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), new C10302());
                                            MentionsAdapter.this.parentFragment.showDialog(builder.create());
                                        }
                                    }
                                }
                            }
                        }
                        if (MentionsAdapter.this.foundContextBot == null) {
                            MentionsAdapter.this.noUserName = true;
                            return;
                        }
                        if (MentionsAdapter.this.delegate != null) {
                            MentionsAdapter.this.delegate.onContextSearch(true);
                        }
                        MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, MentionsAdapter.this.searchingContextQuery, TtmlNode.ANONYMOUS_REGION_ID);
                    }
                }
            }

            C10321() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new C10311(tL_error, tLObject));
            }
        }

        C10333(String str, String str2) {
            this.val$query = str;
            this.val$username = str2;
        }

        public void run() {
            if (MentionsAdapter.this.contextQueryRunnable == this) {
                MentionsAdapter.this.contextQueryRunnable = null;
                if (MentionsAdapter.this.foundContextBot == null && !MentionsAdapter.this.noUserName) {
                    TLObject tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
                    tL_contacts_resolveUsername.username = MentionsAdapter.this.searchingContextUsername = this.val$username;
                    MentionsAdapter.this.contextUsernameReqid = ConnectionsManager.getInstance().sendRequest(tL_contacts_resolveUsername, new C10321());
                } else if (!MentionsAdapter.this.noUserName) {
                    MentionsAdapter.this.searchForContextBotResults(MentionsAdapter.this.foundContextBot, this.val$query, TtmlNode.ANONYMOUS_REGION_ID);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.4 */
    class C10354 implements RequestDelegate {
        final /* synthetic */ String val$offset;
        final /* synthetic */ String val$query;

        /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.4.1 */
        class C10341 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C10341(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                boolean z = false;
                if (MentionsAdapter.this.searchingContextQuery != null && C10354.this.val$query.equals(MentionsAdapter.this.searchingContextQuery)) {
                    if (MentionsAdapter.this.delegate != null) {
                        MentionsAdapter.this.delegate.onContextSearch(false);
                    }
                    MentionsAdapter.this.contextQueryReqid = 0;
                    if (this.val$error == null) {
                        boolean z2;
                        TL_messages_botResults tL_messages_botResults = (TL_messages_botResults) this.val$response;
                        MentionsAdapter.this.nextQueryOffset = tL_messages_botResults.next_offset;
                        if (MentionsAdapter.this.searchResultBotContextById == null) {
                            MentionsAdapter.this.searchResultBotContextById = new HashMap();
                            MentionsAdapter.this.searchResultBotContextSwitch = tL_messages_botResults.switch_pm;
                        }
                        int i = 0;
                        while (i < tL_messages_botResults.results.size()) {
                            BotInlineResult botInlineResult = (BotInlineResult) tL_messages_botResults.results.get(i);
                            if (MentionsAdapter.this.searchResultBotContextById.containsKey(botInlineResult.id) || (!(botInlineResult.document instanceof TL_document) && !(botInlineResult.photo instanceof TL_photo) && botInlineResult.content_url == null && (botInlineResult.send_message instanceof TL_botInlineMessageMediaAuto))) {
                                tL_messages_botResults.results.remove(i);
                                i--;
                            }
                            botInlineResult.query_id = tL_messages_botResults.query_id;
                            MentionsAdapter.this.searchResultBotContextById.put(botInlineResult.id, botInlineResult);
                            i++;
                        }
                        if (MentionsAdapter.this.searchResultBotContext == null || C10354.this.val$offset.length() == 0) {
                            MentionsAdapter.this.searchResultBotContext = tL_messages_botResults.results;
                            MentionsAdapter.this.contextMedia = tL_messages_botResults.gallery;
                            z2 = false;
                        } else {
                            MentionsAdapter.this.searchResultBotContext.addAll(tL_messages_botResults.results);
                            if (tL_messages_botResults.results.isEmpty()) {
                                MentionsAdapter.this.nextQueryOffset = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            z2 = true;
                        }
                        MentionsAdapter.this.searchResultHashtags = null;
                        MentionsAdapter.this.searchResultUsernames = null;
                        MentionsAdapter.this.searchResultCommands = null;
                        MentionsAdapter.this.searchResultCommandsHelp = null;
                        MentionsAdapter.this.searchResultCommandsUsers = null;
                        if (z2) {
                            z2 = MentionsAdapter.this.searchResultBotContextSwitch != null;
                            MentionsAdapter.this.notifyItemChanged(((z2 ? 1 : 0) + (MentionsAdapter.this.searchResultBotContext.size() - tL_messages_botResults.results.size())) - 1);
                            MentionsAdapter.this.notifyItemRangeInserted((z2 ? 1 : 0) + (MentionsAdapter.this.searchResultBotContext.size() - tL_messages_botResults.results.size()), tL_messages_botResults.results.size());
                        } else {
                            MentionsAdapter.this.notifyDataSetChanged();
                        }
                        MentionsAdapterDelegate access$1200 = MentionsAdapter.this.delegate;
                        if (!(MentionsAdapter.this.searchResultBotContext.isEmpty() && MentionsAdapter.this.searchResultBotContextSwitch == null)) {
                            z = true;
                        }
                        access$1200.needChangePanelVisibility(z);
                    }
                }
            }
        }

        C10354(String str, String str2) {
            this.val$query = str;
            this.val$offset = str2;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C10341(tL_error, tLObject));
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.5 */
    class C10365 implements Comparator<User> {
        final /* synthetic */ HashMap val$newResultsHashMap;
        final /* synthetic */ ArrayList val$users;

        C10365(HashMap hashMap, ArrayList arrayList) {
            this.val$newResultsHashMap = hashMap;
            this.val$users = arrayList;
        }

        public int compare(User user, User user2) {
            if (this.val$newResultsHashMap.containsKey(Integer.valueOf(user.id)) && this.val$newResultsHashMap.containsKey(Integer.valueOf(user2.id))) {
                return 0;
            }
            if (this.val$newResultsHashMap.containsKey(Integer.valueOf(user.id))) {
                return -1;
            }
            if (this.val$newResultsHashMap.containsKey(Integer.valueOf(user2.id))) {
                return 1;
            }
            int indexOf = this.val$users.indexOf(Integer.valueOf(user.id));
            int indexOf2 = this.val$users.indexOf(Integer.valueOf(user2.id));
            return (indexOf == -1 || indexOf2 == -1) ? (indexOf == -1 || indexOf2 != -1) ? (indexOf != -1 || indexOf2 == -1) ? 0 : 1 : -1 : indexOf >= indexOf2 ? indexOf == indexOf2 ? 0 : 1 : -1;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Adapters.MentionsAdapter.6 */
    class C10376 implements ContextLinkCellDelegate {
        C10376() {
        }

        public void didPressedImage(ContextLinkCell contextLinkCell) {
            MentionsAdapter.this.delegate.onContextClick(contextLinkCell.getResult());
        }
    }

    public class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(BotInlineResult botInlineResult);

        void onContextSearch(boolean z);
    }

    public MentionsAdapter(Context context, boolean z, long j, MentionsAdapterDelegate mentionsAdapterDelegate) {
        this.allowNewMentions = true;
        this.needUsernames = true;
        this.needBotContext = true;
        this.locationProvider = new C10282(new C10271());
        this.mContext = context;
        this.delegate = mentionsAdapterDelegate;
        this.isDarkTheme = z;
        this.dialog_id = j;
    }

    private void checkLocationPermissionsOrStart() {
        if (this.parentFragment != null && this.parentFragment.getParentActivity() != null) {
            if (VERSION.SDK_INT >= 23 && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            } else if (this.foundContextBot != null && this.foundContextBot.bot_inline_geo) {
                this.locationProvider.start();
            }
        }
    }

    private void onLocationUnavailable() {
        if (this.foundContextBot != null && this.foundContextBot.bot_inline_geo) {
            this.lastKnownLocation = new Location("network");
            this.lastKnownLocation.setLatitude(-1000.0d);
            this.lastKnownLocation.setLongitude(-1000.0d);
            searchForContextBotResults(this.foundContextBot, this.searchingContextQuery, TtmlNode.ANONYMOUS_REGION_ID);
        }
    }

    private void searchForContextBot(String str, String str2) {
        if (this.foundContextBot == null || this.foundContextBot.username == null || !this.foundContextBot.username.equals(str) || this.searchingContextQuery == null || !this.searchingContextQuery.equals(str2)) {
            this.searchResultBotContext = null;
            this.searchResultBotContextById = null;
            this.searchResultBotContextSwitch = null;
            notifyDataSetChanged();
            if (this.foundContextBot != null) {
                this.delegate.needChangePanelVisibility(false);
            }
            if (this.contextQueryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
                this.contextQueryRunnable = null;
            }
            if (TextUtils.isEmpty(str) || !(this.searchingContextUsername == null || this.searchingContextUsername.equals(str))) {
                if (this.contextUsernameReqid != 0) {
                    ConnectionsManager.getInstance().cancelRequest(this.contextUsernameReqid, true);
                    this.contextUsernameReqid = 0;
                }
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.foundContextBot = null;
                this.searchingContextUsername = null;
                this.searchingContextQuery = null;
                this.locationProvider.stop();
                this.noUserName = false;
                if (this.delegate != null) {
                    this.delegate.onContextSearch(false);
                }
                if (str == null || str.length() == 0) {
                    return;
                }
            }
            if (str2 == null) {
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.searchingContextQuery = null;
                if (this.delegate != null) {
                    this.delegate.onContextSearch(false);
                    return;
                }
                return;
            }
            if (this.delegate != null) {
                if (this.foundContextBot != null) {
                    this.delegate.onContextSearch(true);
                } else if (str.equals("gif")) {
                    this.searchingContextUsername = "gif";
                    this.delegate.onContextSearch(false);
                }
            }
            this.searchingContextQuery = str2;
            this.contextQueryRunnable = new C10333(str2, str);
            AndroidUtilities.runOnUIThread(this.contextQueryRunnable, 400);
        }
    }

    private void searchForContextBotResults(User user, String str, String str2) {
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        if (str == null || user == null) {
            this.searchingContextQuery = null;
        } else if (!user.bot_inline_geo || this.lastKnownLocation != null) {
            TLObject tL_messages_getInlineBotResults = new TL_messages_getInlineBotResults();
            tL_messages_getInlineBotResults.bot = MessagesController.getInputUser(user);
            tL_messages_getInlineBotResults.query = str;
            tL_messages_getInlineBotResults.offset = str2;
            if (!(!user.bot_inline_geo || this.lastKnownLocation == null || this.lastKnownLocation.getLatitude() == -1000.0d)) {
                tL_messages_getInlineBotResults.flags |= 1;
                tL_messages_getInlineBotResults.geo_point = new TL_inputGeoPoint();
                tL_messages_getInlineBotResults.geo_point.lat = this.lastKnownLocation.getLatitude();
                tL_messages_getInlineBotResults.geo_point._long = this.lastKnownLocation.getLongitude();
            }
            int i = (int) this.dialog_id;
            int i2 = (int) (this.dialog_id >> 32);
            if (i != 0) {
                tL_messages_getInlineBotResults.peer = MessagesController.getInputPeer(i);
            } else {
                tL_messages_getInlineBotResults.peer = new TL_inputPeerEmpty();
            }
            this.contextQueryReqid = ConnectionsManager.getInstance().sendRequest(tL_messages_getInlineBotResults, new C10354(str, str2), 2);
        }
    }

    public void clearRecentHashtags() {
        super.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
        if (this.delegate != null) {
            this.delegate.needChangePanelVisibility(false);
        }
    }

    public String getBotCaption() {
        return this.foundContextBot != null ? this.foundContextBot.bot_inline_placeholder : (this.searchingContextUsername == null || !this.searchingContextUsername.equals("gif")) ? null : "Search GIFs";
    }

    public TL_inlineBotSwitchPM getBotContextSwitch() {
        return this.searchResultBotContextSwitch;
    }

    public int getContextBotId() {
        return this.foundContextBot != null ? this.foundContextBot.id : 0;
    }

    public String getContextBotName() {
        return this.foundContextBot != null ? this.foundContextBot.username : TtmlNode.ANONYMOUS_REGION_ID;
    }

    public User getContextBotUser() {
        return this.foundContextBot != null ? this.foundContextBot : null;
    }

    public Object getItem(int i) {
        if (this.searchResultBotContext != null) {
            if (this.searchResultBotContextSwitch != null) {
                if (i == 0) {
                    return this.searchResultBotContextSwitch;
                }
                i--;
            }
            return (i < 0 || i >= this.searchResultBotContext.size()) ? null : this.searchResultBotContext.get(i);
        } else if (this.searchResultUsernames != null) {
            return (i < 0 || i >= this.searchResultUsernames.size()) ? null : this.searchResultUsernames.get(i);
        } else {
            if (this.searchResultHashtags != null) {
                return (i < 0 || i >= this.searchResultHashtags.size()) ? null : this.searchResultHashtags.get(i);
            } else {
                if (this.searchResultCommands == null || i < 0 || i >= this.searchResultCommands.size()) {
                    return null;
                }
                if (this.searchResultCommandsUsers == null || (this.botsCount == 1 && !(this.info instanceof TL_channelFull))) {
                    return this.searchResultCommands.get(i);
                }
                if (this.searchResultCommandsUsers.get(i) != null) {
                    String str = "%s@%s";
                    Object[] objArr = new Object[2];
                    objArr[0] = this.searchResultCommands.get(i);
                    objArr[1] = this.searchResultCommandsUsers.get(i) != null ? ((User) this.searchResultCommandsUsers.get(i)).username : TtmlNode.ANONYMOUS_REGION_ID;
                    return String.format(str, objArr);
                }
                return String.format("%s", new Object[]{this.searchResultCommands.get(i)});
            }
        }
    }

    public int getItemCount() {
        int i = 0;
        if (this.searchResultBotContext == null) {
            return this.searchResultUsernames != null ? this.searchResultUsernames.size() : this.searchResultHashtags != null ? this.searchResultHashtags.size() : this.searchResultCommands != null ? this.searchResultCommands.size() : 0;
        } else {
            int size = this.searchResultBotContext.size();
            if (this.searchResultBotContextSwitch != null) {
                i = 1;
            }
            return i + size;
        }
    }

    public int getItemPosition(int i) {
        return (this.searchResultBotContext == null || this.searchResultBotContextSwitch == null) ? i : i - 1;
    }

    public int getItemViewType(int i) {
        return this.searchResultBotContext != null ? (i != 0 || this.searchResultBotContextSwitch == null) ? 1 : 2 : 0;
    }

    public int getResultLength() {
        return this.resultLength;
    }

    public int getResultStartPosition() {
        return this.resultStartPosition;
    }

    public ArrayList<BotInlineResult> getSearchResultBotContext() {
        return this.searchResultBotContext;
    }

    public boolean isBotCommands() {
        return this.searchResultCommands != null;
    }

    public boolean isBotContext() {
        return this.searchResultBotContext != null;
    }

    public boolean isLongClickEnabled() {
        return (this.searchResultHashtags == null && this.searchResultCommands == null) ? false : true;
    }

    public boolean isMediaLayout() {
        return this.contextMedia;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        boolean z = true;
        if (this.searchResultBotContext != null) {
            boolean z2 = this.searchResultBotContextSwitch != null;
            if (viewHolder.getItemViewType() != 2) {
                if (z2) {
                    i--;
                }
                ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
                BotInlineResult botInlineResult = (BotInlineResult) this.searchResultBotContext.get(i);
                boolean z3 = this.contextMedia;
                boolean z4 = i != this.searchResultBotContext.size() + -1;
                if (!(z2 && i == 0)) {
                    z = false;
                }
                contextLinkCell.setLink(botInlineResult, z3, z4, z);
            } else if (z2) {
                ((BotSwitchCell) viewHolder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else if (this.searchResultUsernames != null) {
            ((MentionCell) viewHolder.itemView).setUser((User) this.searchResultUsernames.get(i));
        } else if (this.searchResultHashtags != null) {
            ((MentionCell) viewHolder.itemView).setText((String) this.searchResultHashtags.get(i));
        } else if (this.searchResultCommands != null) {
            ((MentionCell) viewHolder.itemView).setBotCommand((String) this.searchResultCommands.get(i), (String) this.searchResultCommandsHelp.get(i), this.searchResultCommandsUsers != null ? (User) this.searchResultCommandsUsers.get(i) : null);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View contextLinkCell;
        if (i == 1) {
            contextLinkCell = new ContextLinkCell(this.mContext);
            ((ContextLinkCell) contextLinkCell).setDelegate(new C10376());
        } else if (i == 2) {
            contextLinkCell = new BotSwitchCell(this.mContext);
        } else {
            contextLinkCell = new MentionCell(this.mContext);
            ((MentionCell) contextLinkCell).setIsDarkTheme(this.isDarkTheme);
        }
        return new Holder(contextLinkCell);
    }

    public void onDestroy() {
        if (this.locationProvider != null) {
            this.locationProvider.stop();
        }
        if (this.contextQueryRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
            this.contextQueryRunnable = null;
        }
        if (this.contextUsernameReqid != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.contextUsernameReqid, true);
            this.contextUsernameReqid = 0;
        }
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        this.foundContextBot = null;
        this.searchingContextUsername = null;
        this.searchingContextQuery = null;
        this.noUserName = false;
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i != 2 || this.foundContextBot == null || !this.foundContextBot.bot_inline_geo) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            onLocationUnavailable();
        } else {
            this.locationProvider.start();
        }
    }

    public void searchForContextBotForNextOffset() {
        if (this.contextQueryReqid == 0 && this.nextQueryOffset != null && this.nextQueryOffset.length() != 0 && this.foundContextBot != null && this.searchingContextQuery != null) {
            searchForContextBotResults(this.foundContextBot, this.searchingContextQuery, this.nextQueryOffset);
        }
    }

    public void searchUsernameOrHashtag(String str, int i, ArrayList<MessageObject> arrayList) {
        if (str == null || str.length() == 0) {
            searchForContextBot(null, null);
            this.delegate.needChangePanelVisibility(false);
            this.lastText = null;
            return;
        }
        int i2;
        int i3 = str.length() > 0 ? i - 1 : i;
        this.lastText = null;
        StringBuilder stringBuilder = new StringBuilder();
        if (this.needBotContext && str.charAt(0) == '@') {
            String substring;
            String str2;
            int indexOf = str.indexOf(32);
            int length = str.length();
            String str3 = null;
            if (indexOf > 0) {
                str3 = str.substring(1, indexOf);
                substring = str.substring(indexOf + 1);
            } else if (str.charAt(length - 1) == 't' && str.charAt(length - 2) == 'o' && str.charAt(length - 3) == 'b') {
                str3 = str.substring(1);
                substring = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                searchForContextBot(null, null);
                substring = null;
            }
            if (str3 == null || str3.length() < 1) {
                str2 = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                for (i2 = 1; i2 < str3.length(); i2++) {
                    char charAt = str3.charAt(i2);
                    if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                        str2 = TtmlNode.ANONYMOUS_REGION_ID;
                        break;
                    }
                }
                str2 = str3;
            }
            searchForContextBot(str2, substring);
        } else {
            searchForContextBot(null, null);
        }
        if (this.foundContextBot == null) {
            int i4 = -1;
            i2 = i3;
            Object obj = null;
            while (i2 >= 0) {
                if (i2 < str.length()) {
                    char charAt2 = str.charAt(i2);
                    if (i2 == 0 || str.charAt(i2 - 1) == ' ' || str.charAt(i2 - 1) == '\n') {
                        if (charAt2 != '@') {
                            if (charAt2 != '#') {
                                if (i2 == 0 && this.botInfo != null && charAt2 == '/') {
                                    i3 = 2;
                                    this.resultStartPosition = i2;
                                    this.resultLength = stringBuilder.length() + 1;
                                    break;
                                }
                            } else if (this.hashtagsLoadedFromDb) {
                                i3 = 1;
                                this.resultStartPosition = i2;
                                this.resultLength = stringBuilder.length() + 1;
                                stringBuilder.insert(0, charAt2);
                            } else {
                                loadRecentHashtags();
                                this.lastText = str;
                                this.lastPosition = i;
                                this.messages = arrayList;
                                this.delegate.needChangePanelVisibility(false);
                                return;
                            }
                        } else if (this.needUsernames || (this.needBotContext && i2 == 0)) {
                            if (obj != null) {
                                this.delegate.needChangePanelVisibility(false);
                                return;
                            } else if (this.info != null || i2 == 0) {
                                i3 = 0;
                                this.resultStartPosition = i2;
                                this.resultLength = stringBuilder.length() + 1;
                                i4 = i2;
                            } else {
                                this.lastText = str;
                                this.lastPosition = i;
                                this.messages = arrayList;
                                this.delegate.needChangePanelVisibility(false);
                                return;
                            }
                        }
                    }
                    if ((charAt2 < '0' || charAt2 > '9') && ((charAt2 < 'a' || charAt2 > 'z') && ((charAt2 < 'A' || charAt2 > 'Z') && charAt2 != '_'))) {
                        obj = 1;
                    }
                    stringBuilder.insert(0, charAt2);
                }
                i2--;
            }
            i3 = -1;
            if (i3 == -1) {
                this.delegate.needChangePanelVisibility(false);
            } else if (i3 == 0) {
                User user;
                r3 = new ArrayList();
                for (i2 = 0; i2 < Math.min(100, arrayList.size()); i2++) {
                    i3 = ((MessageObject) arrayList.get(i2)).messageOwner.from_id;
                    if (!r3.contains(Integer.valueOf(i3))) {
                        r3.add(Integer.valueOf(i3));
                    }
                }
                String toLowerCase = stringBuilder.toString().toLowerCase();
                r5 = new ArrayList();
                HashMap hashMap = new HashMap();
                if (this.needBotContext && r2 == 0 && !SearchQuery.inlineBots.isEmpty()) {
                    i4 = 0;
                    for (i2 = 0; i2 < SearchQuery.inlineBots.size(); i2++) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_topPeer) SearchQuery.inlineBots.get(i2)).peer.user_id));
                        if (user != null) {
                            if (user.username != null && user.username.length() > 0 && ((toLowerCase.length() > 0 && user.username.toLowerCase().startsWith(toLowerCase)) || toLowerCase.length() == 0)) {
                                r5.add(user);
                                hashMap.put(Integer.valueOf(user.id), user);
                                i4++;
                            }
                            if (i4 == 5) {
                                break;
                            }
                        }
                    }
                }
                if (!(this.info == null || this.info.participants == null)) {
                    for (i2 = 0; i2 < this.info.participants.participants.size(); i2++) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(i2)).user_id));
                        if (!(user == null || UserObject.isUserSelf(user) || hashMap.containsKey(Integer.valueOf(user.id)))) {
                            if (toLowerCase.length() == 0) {
                                if (!user.deleted && (this.allowNewMentions || !(this.allowNewMentions || user.username == null || user.username.length() == 0))) {
                                    r5.add(user);
                                }
                            } else if (user.username != null && user.username.length() > 0 && user.username.toLowerCase().startsWith(toLowerCase)) {
                                r5.add(user);
                            } else if (this.allowNewMentions || !(user.username == null || user.username.length() == 0)) {
                                if (user.first_name != null && user.first_name.length() > 0 && user.first_name.toLowerCase().startsWith(toLowerCase)) {
                                    r5.add(user);
                                } else if (user.last_name != null && user.last_name.length() > 0 && user.last_name.toLowerCase().startsWith(toLowerCase)) {
                                    r5.add(user);
                                }
                            }
                        }
                    }
                }
                this.searchResultHashtags = null;
                this.searchResultCommands = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                this.searchResultUsernames = r5;
                Collections.sort(this.searchResultUsernames, new C10365(hashMap, r3));
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(!r5.isEmpty());
            } else if (i3 == 1) {
                ArrayList arrayList2 = new ArrayList();
                String toLowerCase2 = stringBuilder.toString().toLowerCase();
                for (i2 = 0; i2 < this.hashtags.size(); i2++) {
                    HashtagObject hashtagObject = (HashtagObject) this.hashtags.get(i2);
                    if (!(hashtagObject == null || hashtagObject.hashtag == null || !hashtagObject.hashtag.startsWith(toLowerCase2))) {
                        arrayList2.add(hashtagObject.hashtag);
                    }
                }
                this.searchResultHashtags = arrayList2;
                this.searchResultUsernames = null;
                this.searchResultCommands = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(!arrayList2.isEmpty());
            } else if (i3 == 2) {
                r3 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                r5 = new ArrayList();
                String toLowerCase3 = stringBuilder.toString().toLowerCase();
                for (Entry value : this.botInfo.entrySet()) {
                    BotInfo botInfo = (BotInfo) value.getValue();
                    for (i4 = 0; i4 < botInfo.commands.size(); i4++) {
                        TL_botCommand tL_botCommand = (TL_botCommand) botInfo.commands.get(i4);
                        if (!(tL_botCommand == null || tL_botCommand.command == null || !tL_botCommand.command.startsWith(toLowerCase3))) {
                            r3.add("/" + tL_botCommand.command);
                            arrayList3.add(tL_botCommand.description);
                            r5.add(MessagesController.getInstance().getUser(Integer.valueOf(botInfo.user_id)));
                        }
                    }
                }
                this.searchResultHashtags = null;
                this.searchResultUsernames = null;
                this.searchResultCommands = r3;
                this.searchResultCommandsHelp = arrayList3;
                this.searchResultCommandsUsers = r5;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(!r3.isEmpty());
            }
        }
    }

    public void setAllowNewMentions(boolean z) {
        this.allowNewMentions = z;
    }

    public void setBotInfo(HashMap<Integer, BotInfo> hashMap) {
        this.botInfo = hashMap;
    }

    public void setBotsCount(int i) {
        this.botsCount = i;
    }

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        if (this.lastText != null) {
            searchUsernameOrHashtag(this.lastText, this.lastPosition, this.messages);
        }
    }

    protected void setHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        super.setHashtags(arrayList, hashMap);
        if (this.lastText != null) {
            searchUsernameOrHashtag(this.lastText, this.lastPosition, this.messages);
        }
    }

    public void setNeedBotContext(boolean z) {
        this.needBotContext = z;
    }

    public void setNeedUsernames(boolean z) {
        this.needUsernames = z;
    }

    public void setParentFragment(BaseFragment baseFragment) {
        this.parentFragment = baseFragment;
    }
}

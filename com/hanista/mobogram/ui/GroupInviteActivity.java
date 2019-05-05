package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatFull;
import com.hanista.mobogram.tgnet.TLRPC.ExportedChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_exportInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_chatInviteExported;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_exportChatInvite;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.TextBlockCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class GroupInviteActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chat_id;
    private int copyLinkRow;
    private ExportedChatInvite invite;
    private int linkInfoRow;
    private int linkRow;
    private ListAdapter listAdapter;
    private boolean loading;
    private int revokeLinkRow;
    private int rowCount;
    private int shadowRow;
    private int shareLinkRow;

    /* renamed from: com.hanista.mobogram.ui.GroupInviteActivity.1 */
    class C15761 extends ActionBarMenuOnItemClick {
        C15761() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                GroupInviteActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupInviteActivity.2 */
    class C15782 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.GroupInviteActivity.2.1 */
        class C15771 implements OnClickListener {
            C15771() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                GroupInviteActivity.this.generateLink(true);
            }
        }

        C15782() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (GroupInviteActivity.this.getParentActivity() != null) {
                if (i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.linkRow) {
                    if (GroupInviteActivity.this.invite != null) {
                        try {
                            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", GroupInviteActivity.this.invite.link));
                            Toast.makeText(GroupInviteActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", C0338R.string.LinkCopied), 0).show();
                        } catch (Throwable e) {
                            FileLog.m18e("tmessages", e);
                        }
                    }
                } else if (i == GroupInviteActivity.this.shareLinkRow) {
                    if (GroupInviteActivity.this.invite != null) {
                        try {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            intent.putExtra("android.intent.extra.TEXT", GroupInviteActivity.this.invite.link);
                            GroupInviteActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", C0338R.string.InviteToGroupByLink)), 500);
                        } catch (Throwable e2) {
                            FileLog.m18e("tmessages", e2);
                        }
                    }
                } else if (i == GroupInviteActivity.this.revokeLinkRow) {
                    Builder builder = new Builder(GroupInviteActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("RevokeAlert", C0338R.string.RevokeAlert));
                    builder.setTitle(LocaleController.getString("RevokeLink", C0338R.string.RevokeLink));
                    builder.setPositiveButton(LocaleController.getString("RevokeButton", C0338R.string.RevokeButton), new C15771());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    GroupInviteActivity.this.showDialog(builder.create());
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupInviteActivity.3 */
    class C15803 implements RequestDelegate {
        final /* synthetic */ boolean val$newRequest;

        /* renamed from: com.hanista.mobogram.ui.GroupInviteActivity.3.1 */
        class C15791 implements Runnable {
            final /* synthetic */ TL_error val$error;
            final /* synthetic */ TLObject val$response;

            C15791(TL_error tL_error, TLObject tLObject) {
                this.val$error = tL_error;
                this.val$response = tLObject;
            }

            public void run() {
                if (this.val$error == null) {
                    GroupInviteActivity.this.invite = (ExportedChatInvite) this.val$response;
                    if (C15803.this.val$newRequest) {
                        if (GroupInviteActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(GroupInviteActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("RevokeAlertNewLink", C0338R.string.RevokeAlertNewLink));
                            builder.setTitle(LocaleController.getString("RevokeLink", C0338R.string.RevokeLink));
                            builder.setNegativeButton(LocaleController.getString("OK", C0338R.string.OK), null);
                            GroupInviteActivity.this.showDialog(builder.create());
                        } else {
                            return;
                        }
                    }
                }
                GroupInviteActivity.this.loading = false;
                GroupInviteActivity.this.listAdapter.notifyDataSetChanged();
            }
        }

        C15803(boolean z) {
            this.val$newRequest = z;
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new C15791(tL_error, tLObject));
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
            return GroupInviteActivity.this.loading ? 0 : GroupInviteActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.shareLinkRow || i == GroupInviteActivity.this.revokeLinkRow) ? 0 : (i == GroupInviteActivity.this.shadowRow || i == GroupInviteActivity.this.linkInfoRow) ? 1 : i == GroupInviteActivity.this.linkRow ? 2 : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            View textSettingsCell;
            if (itemViewType == 0) {
                if (view == null) {
                    textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                } else {
                    textSettingsCell = view;
                }
                TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                if (i == GroupInviteActivity.this.copyLinkRow) {
                    textSettingsCell2.setText(LocaleController.getString("CopyLink", C0338R.string.CopyLink), true);
                    return textSettingsCell;
                } else if (i == GroupInviteActivity.this.shareLinkRow) {
                    textSettingsCell2.setText(LocaleController.getString("ShareLink", C0338R.string.ShareLink), false);
                    return textSettingsCell;
                } else if (i != GroupInviteActivity.this.revokeLinkRow) {
                    return textSettingsCell;
                } else {
                    textSettingsCell2.setText(LocaleController.getString("RevokeLink", C0338R.string.RevokeLink), true);
                    return textSettingsCell;
                }
            } else if (itemViewType == 1) {
                textSettingsCell = view == null ? new TextInfoPrivacyCell(this.mContext) : view;
                if (i == GroupInviteActivity.this.shadowRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(TtmlNode.ANONYMOUS_REGION_ID);
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textSettingsCell;
                } else if (i != GroupInviteActivity.this.linkInfoRow) {
                    return textSettingsCell;
                } else {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(GroupInviteActivity.this.chat_id));
                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                        ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("LinkInfo", C0338R.string.LinkInfo));
                    } else {
                        ((TextInfoPrivacyCell) textSettingsCell).setText(LocaleController.getString("ChannelLinkInfo", C0338R.string.ChannelLinkInfo));
                    }
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider);
                    return textSettingsCell;
                }
            } else if (itemViewType != 2) {
                return view;
            } else {
                itemViewType = AdvanceTheme.f2497h;
                int i2 = AdvanceTheme.f2494e;
                if (view == null) {
                    textSettingsCell = new TextBlockCell(this.mContext);
                    textSettingsCell.setBackgroundColor(-1);
                    if (ThemeUtil.m2490b()) {
                        textSettingsCell.setBackgroundColor(itemViewType);
                    }
                } else {
                    textSettingsCell = view;
                }
                ((TextBlockCell) textSettingsCell).setText(GroupInviteActivity.this.invite != null ? GroupInviteActivity.this.invite.link : "error", false);
                if (!ThemeUtil.m2490b()) {
                    return textSettingsCell;
                }
                ((TextBlockCell) textSettingsCell).setTextColor(i2);
                return textSettingsCell;
            }
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return GroupInviteActivity.this.loading;
        }

        public boolean isEnabled(int i) {
            return i == GroupInviteActivity.this.revokeLinkRow || i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.shareLinkRow || i == GroupInviteActivity.this.linkRow;
        }
    }

    public GroupInviteActivity(int i) {
        this.chat_id = i;
    }

    private void generateLink(boolean z) {
        TLObject tL_channels_exportInvite;
        this.loading = true;
        if (ChatObject.isChannel(this.chat_id)) {
            tL_channels_exportInvite = new TL_channels_exportInvite();
            tL_channels_exportInvite.channel = MessagesController.getInputChannel(this.chat_id);
        } else {
            tL_channels_exportInvite = new TL_messages_exportChatInvite();
            tL_channels_exportInvite.chat_id = this.chat_id;
        }
        ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(tL_channels_exportInvite, new C15803(z)), this.classGuid);
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLink", C0338R.string.InviteLink));
        this.actionBar.setActionBarMenuOnItemClick(new C15761());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        if (ThemeUtil.m2490b()) {
            frameLayout.setBackgroundColor(AdvanceTheme.f2497h);
        }
        View frameLayout2 = new FrameLayout(context);
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        frameLayout2.addView(new ProgressBar(context), LayoutHelper.createFrame(-2, -2, 17));
        View listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setEmptyView(frameLayout2);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, -1, 51));
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new C15782());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr[0];
            int intValue = ((Integer) objArr[1]).intValue();
            if (chatFull.id == this.chat_id && intValue == this.classGuid) {
                this.invite = MessagesController.getInstance().getExportedInvite(this.chat_id);
                if (this.invite instanceof TL_chatInviteExported) {
                    this.loading = false;
                    if (this.listAdapter != null) {
                        this.listAdapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                generateLink(false);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        MessagesController.getInstance().loadFullChat(this.chat_id, this.classGuid, true);
        this.loading = true;
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.linkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.linkInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.copyLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.revokeLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shadowRow = i;
        return true;
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}

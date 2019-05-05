package com.hanista.mobogram.ui.Components;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.support.widget.LinearLayoutManager;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.Adapter;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.LayoutParams;
import com.hanista.mobogram.messenger.support.widget.RecyclerView.ViewHolder;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.ChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_importChatInvite;
import com.hanista.mobogram.tgnet.TLRPC.Updates;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.JoinSheetUserCell;
import com.hanista.mobogram.ui.ChatActivity;

public class JoinGroupAlert extends BottomSheet {
    private ChatInvite chatInvite;
    private BaseFragment fragment;
    private String hash;

    /* renamed from: com.hanista.mobogram.ui.Components.JoinGroupAlert.1 */
    class C13511 implements OnClickListener {
        C13511() {
        }

        public void onClick(View view) {
            JoinGroupAlert.this.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.Components.JoinGroupAlert.2 */
    class C13542 implements OnClickListener {

        /* renamed from: com.hanista.mobogram.ui.Components.JoinGroupAlert.2.1 */
        class C13531 implements RequestDelegate {

            /* renamed from: com.hanista.mobogram.ui.Components.JoinGroupAlert.2.1.1 */
            class C13521 implements Runnable {
                final /* synthetic */ TL_error val$error;
                final /* synthetic */ TLObject val$response;

                C13521(TL_error tL_error, TLObject tLObject) {
                    this.val$error = tL_error;
                    this.val$response = tLObject;
                }

                public void run() {
                    if (JoinGroupAlert.this.fragment != null && JoinGroupAlert.this.fragment.getParentActivity() != null) {
                        if (this.val$error == null) {
                            Updates updates = (Updates) this.val$response;
                            if (!updates.chats.isEmpty()) {
                                Chat chat = (Chat) updates.chats.get(0);
                                chat.left = false;
                                chat.kicked = false;
                                MessagesController.getInstance().putUsers(updates.users, false);
                                MessagesController.getInstance().putChats(updates.chats, false);
                                Bundle bundle = new Bundle();
                                bundle.putInt("chat_id", chat.id);
                                if (MessagesController.checkCanOpenChat(bundle, JoinGroupAlert.this.fragment)) {
                                    JoinGroupAlert.this.fragment.presentFragment(new ChatActivity(bundle), JoinGroupAlert.this.fragment instanceof ChatActivity);
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        Builder builder = new Builder(JoinGroupAlert.this.fragment.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                        if (this.val$error.text.startsWith("FLOOD_WAIT")) {
                            builder.setMessage(LocaleController.getString("FloodWait", C0338R.string.FloodWait));
                        } else if (this.val$error.text.equals("USERS_TOO_MUCH")) {
                            builder.setMessage(LocaleController.getString("JoinToGroupErrorFull", C0338R.string.JoinToGroupErrorFull));
                        } else {
                            builder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", C0338R.string.JoinToGroupErrorNotExist));
                        }
                        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                        JoinGroupAlert.this.fragment.showDialog(builder.create());
                    }
                }
            }

            C13531() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    MessagesController.getInstance().processUpdates((Updates) tLObject, false);
                }
                AndroidUtilities.runOnUIThread(new C13521(tL_error, tLObject));
            }
        }

        C13542() {
        }

        public void onClick(View view) {
            JoinGroupAlert.this.dismiss();
            TLObject tL_messages_importChatInvite = new TL_messages_importChatInvite();
            tL_messages_importChatInvite.hash = JoinGroupAlert.this.hash;
            ConnectionsManager.getInstance().sendRequest(tL_messages_importChatInvite, new C13531(), 2);
        }
    }

    private class Holder extends ViewHolder {
        public Holder(View view) {
            super(view);
        }
    }

    private class UsersAdapter extends Adapter {
        private Context context;

        public UsersAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            int size = JoinGroupAlert.this.chatInvite.participants.size();
            return size != (JoinGroupAlert.this.chatInvite.chat != null ? JoinGroupAlert.this.chatInvite.chat.participants_count : JoinGroupAlert.this.chatInvite.participants_count) ? size + 1 : size;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            JoinSheetUserCell joinSheetUserCell = (JoinSheetUserCell) viewHolder.itemView;
            if (i < JoinGroupAlert.this.chatInvite.participants.size()) {
                joinSheetUserCell.setUser((User) JoinGroupAlert.this.chatInvite.participants.get(i));
            } else {
                joinSheetUserCell.setCount((JoinGroupAlert.this.chatInvite.chat != null ? JoinGroupAlert.this.chatInvite.chat.participants_count : JoinGroupAlert.this.chatInvite.participants_count) - JoinGroupAlert.this.chatInvite.participants.size());
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View joinSheetUserCell = new JoinSheetUserCell(this.context);
            joinSheetUserCell.setLayoutParams(new LayoutParams(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(90.0f)));
            return new Holder(joinSheetUserCell);
        }
    }

    public JoinGroupAlert(Context context, ChatInvite chatInvite, String str, BaseFragment baseFragment) {
        int i;
        CharSequence charSequence;
        Drawable drawable;
        super(context, false);
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        this.fragment = baseFragment;
        this.chatInvite = chatInvite;
        this.hash = str;
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setClickable(true);
        setCustomView(linearLayout);
        TLObject tLObject = null;
        Drawable avatarDrawable;
        String str2;
        if (chatInvite.chat != null) {
            avatarDrawable = new AvatarDrawable(chatInvite.chat);
            if (this.chatInvite.chat.photo != null) {
                tLObject = this.chatInvite.chat.photo.photo_small;
            }
            str2 = chatInvite.chat.title;
            i = chatInvite.chat.participants_count;
            charSequence = str2;
            drawable = avatarDrawable;
        } else {
            avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(0, chatInvite.title, null, false);
            if (this.chatInvite.photo != null) {
                tLObject = this.chatInvite.photo.photo_small;
            }
            str2 = chatInvite.title;
            i = chatInvite.participants_count;
            Object obj = str2;
            drawable = avatarDrawable;
        }
        View backupImageView = new BackupImageView(context);
        backupImageView.setRoundRadius(AndroidUtilities.dp(35.0f));
        backupImageView.setImage(tLObject, "50_50", drawable);
        linearLayout.addView(backupImageView, LayoutHelper.createLinear(70, 70, 49, 0, 12, 0, 0));
        backupImageView = new TextView(context);
        backupImageView.setTypeface(FontUtil.m1176a().m1160c());
        backupImageView.setTextSize(1, 17.0f);
        backupImageView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        backupImageView.setText(charSequence);
        backupImageView.setSingleLine(true);
        backupImageView.setEllipsize(TruncateAt.END);
        linearLayout.addView(backupImageView, LayoutHelper.createLinear(-2, -2, 49, 10, 10, 10, i > 0 ? 0 : 10));
        if (i > 0) {
            View textView = new TextView(context);
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(Theme.PINNED_PANEL_MESSAGE_TEXT_COLOR);
            textView.setSingleLine(true);
            textView.setEllipsize(TruncateAt.END);
            textView.setText(LocaleController.formatPluralString("Members", i));
            linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 49, 10, 4, 10, 10));
        }
        if (!chatInvite.participants.isEmpty()) {
            View recyclerListView = new RecyclerListView(context);
            recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
            recyclerListView.setNestedScrollingEnabled(false);
            recyclerListView.setClipToPadding(false);
            recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
            recyclerListView.setHorizontalScrollBarEnabled(false);
            recyclerListView.setVerticalScrollBarEnabled(false);
            recyclerListView.setAdapter(new UsersAdapter(context));
            recyclerListView.setGlowColor(33554431);
            linearLayout.addView(recyclerListView, LayoutHelper.createLinear(-2, 90, 49, 0, 0, 0, 0));
        }
        View view = new View(context);
        view.setBackgroundResource(C0338R.drawable.header_shadow_reverse);
        linearLayout.addView(view, LayoutHelper.createLinear(-1, 3));
        view = new PickerBottomLayout(context, false);
        linearLayout.addView(view, LayoutHelper.createFrame(-1, 48, 83));
        view.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        view.cancelButton.setTextColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
        view.cancelButton.setText(LocaleController.getString("Cancel", C0338R.string.Cancel).toUpperCase());
        view.cancelButton.setOnClickListener(new C13511());
        view.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        view.doneButton.setVisibility(0);
        view.doneButtonBadgeTextView.setVisibility(8);
        view.doneButtonTextView.setTextColor(Theme.STICKERS_SHEET_SEND_TEXT_COLOR);
        view.doneButtonTextView.setText(LocaleController.getString("JoinGroup", C0338R.string.JoinGroup));
        view.doneButton.setOnClickListener(new C13542());
    }
}

package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.InputFile;
import com.hanista.mobogram.tgnet.TLRPC.PhotoSize;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.GreySectionCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.AvatarUpdater;
import com.hanista.mobogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import com.hanista.mobogram.ui.Components.BackupImageView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

public class GroupCreateFinalActivity extends BaseFragment implements NotificationCenterDelegate, AvatarUpdaterDelegate {
    private static final int done_button = 1;
    private FileLocation avatar;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int chatType;
    private boolean createAfterUpload;
    private boolean donePressed;
    private ListAdapter listAdapter;
    private ListView listView;
    private EditText nameTextView;
    private String nameToSet;
    private ProgressDialog progressDialog;
    private ArrayList<Integer> selectedContacts;
    private InputFile uploadedAvatar;

    /* renamed from: com.hanista.mobogram.ui.GroupCreateFinalActivity.1 */
    class C15691 implements Runnable {
        final /* synthetic */ Semaphore val$semaphore;
        final /* synthetic */ ArrayList val$users;
        final /* synthetic */ ArrayList val$usersToLoad;

        C15691(ArrayList arrayList, ArrayList arrayList2, Semaphore semaphore) {
            this.val$users = arrayList;
            this.val$usersToLoad = arrayList2;
            this.val$semaphore = semaphore;
        }

        public void run() {
            this.val$users.addAll(MessagesStorage.getInstance().getUsers(this.val$usersToLoad));
            this.val$semaphore.release();
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupCreateFinalActivity.2 */
    class C15712 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.GroupCreateFinalActivity.2.1 */
        class C15701 implements OnClickListener {
            final /* synthetic */ int val$reqId;

            C15701(int i) {
                this.val$reqId = i;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
                GroupCreateFinalActivity.this.donePressed = false;
                try {
                    dialogInterface.dismiss();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        C15712() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                GroupCreateFinalActivity.this.finishFragment();
            } else if (i == GroupCreateFinalActivity.done_button && !GroupCreateFinalActivity.this.donePressed && GroupCreateFinalActivity.this.nameTextView.getText().length() != 0) {
                GroupCreateFinalActivity.this.donePressed = true;
                if (GroupCreateFinalActivity.this.chatType == GroupCreateFinalActivity.done_button) {
                    MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.nameTextView.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
                } else if (GroupCreateFinalActivity.this.avatarUpdater.uploadingAvatar != null) {
                    GroupCreateFinalActivity.this.createAfterUpload = true;
                } else {
                    GroupCreateFinalActivity.this.progressDialog = new ProgressDialog(GroupCreateFinalActivity.this.getParentActivity());
                    GroupCreateFinalActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", C0338R.string.Loading));
                    GroupCreateFinalActivity.this.progressDialog.setCanceledOnTouchOutside(false);
                    GroupCreateFinalActivity.this.progressDialog.setCancelable(false);
                    GroupCreateFinalActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", C0338R.string.Cancel), new C15701(MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.nameTextView.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this)));
                    GroupCreateFinalActivity.this.progressDialog.show();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupCreateFinalActivity.3 */
    class C15733 implements View.OnClickListener {

        /* renamed from: com.hanista.mobogram.ui.GroupCreateFinalActivity.3.1 */
        class C15721 implements OnClickListener {
            C15721() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    GroupCreateFinalActivity.this.avatarUpdater.openCamera();
                } else if (i == GroupCreateFinalActivity.done_button) {
                    GroupCreateFinalActivity.this.avatarUpdater.openGallery();
                } else if (i == 2) {
                    GroupCreateFinalActivity.this.avatar = null;
                    GroupCreateFinalActivity.this.uploadedAvatar = null;
                    GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
                }
            }
        }

        C15733() {
        }

        public void onClick(View view) {
            if (GroupCreateFinalActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(GroupCreateFinalActivity.this.getParentActivity());
                builder.setItems(GroupCreateFinalActivity.this.avatar != null ? new CharSequence[]{LocaleController.getString("FromCamera", C0338R.string.FromCamera), LocaleController.getString("FromGalley", C0338R.string.FromGalley), LocaleController.getString("DeletePhoto", C0338R.string.DeletePhoto)} : new CharSequence[]{LocaleController.getString("FromCamera", C0338R.string.FromCamera), LocaleController.getString("FromGalley", C0338R.string.FromGalley)}, new C15721());
                GroupCreateFinalActivity.this.showDialog(builder.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupCreateFinalActivity.4 */
    class C15744 implements TextWatcher {
        C15744() {
        }

        public void afterTextChanged(Editable editable) {
            GroupCreateFinalActivity.this.avatarDrawable.setInfo(5, GroupCreateFinalActivity.this.nameTextView.length() > 0 ? GroupCreateFinalActivity.this.nameTextView.getText().toString() : null, null, false);
            GroupCreateFinalActivity.this.avatarImage.invalidate();
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.ui.GroupCreateFinalActivity.5 */
    class C15755 implements Runnable {
        final /* synthetic */ InputFile val$file;
        final /* synthetic */ PhotoSize val$small;

        C15755(InputFile inputFile, PhotoSize photoSize) {
            this.val$file = inputFile;
            this.val$small = photoSize;
        }

        public void run() {
            GroupCreateFinalActivity.this.uploadedAvatar = this.val$file;
            GroupCreateFinalActivity.this.avatar = this.val$small.location;
            GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
            if (GroupCreateFinalActivity.this.createAfterUpload) {
                FileLog.m16e("tmessages", "avatar did uploaded");
                MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.nameTextView.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
            }
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
            return GroupCreateFinalActivity.this.selectedContacts.size();
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View userCell = view == null ? new UserCell(this.mContext, GroupCreateFinalActivity.done_button, 0, false) : view;
            ((UserCell) userCell).setData(MessagesController.getInstance().getUser((Integer) GroupCreateFinalActivity.this.selectedContacts.get(i)), null, null, 0);
            return userCell;
        }

        public int getViewTypeCount() {
            return GroupCreateFinalActivity.done_button;
        }

        public boolean isEnabled(int i) {
            return false;
        }
    }

    public GroupCreateFinalActivity(Bundle bundle) {
        super(bundle);
        this.avatarUpdater = new AvatarUpdater();
        this.progressDialog = null;
        this.nameToSet = null;
        this.chatType = 0;
        this.chatType = bundle.getInt("chatType", 0);
        this.avatarDrawable = new AvatarDrawable();
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
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.chatType == done_button) {
            this.actionBar.setTitle(LocaleController.getString("NewBroadcastList", C0338R.string.NewBroadcastList));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewGroup", C0338R.string.NewGroup));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C15712());
        this.actionBar.createMenu().addItemWithWidth((int) done_button, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(done_button);
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        LayoutParams layoutParams = (LayoutParams) frameLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -2;
        layoutParams.gravity = 51;
        frameLayout.setLayoutParams(layoutParams);
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0f));
        this.avatarDrawable.setInfo(5, null, null, this.chatType == done_button);
        this.avatarImage.setImageDrawable(this.avatarDrawable);
        frameLayout.addView(this.avatarImage);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.avatarImage.getLayoutParams();
        layoutParams2.width = AndroidUtilities.dp(64.0f);
        layoutParams2.height = AndroidUtilities.dp(64.0f);
        layoutParams2.topMargin = AndroidUtilities.dp(12.0f);
        layoutParams2.bottomMargin = AndroidUtilities.dp(12.0f);
        layoutParams2.leftMargin = LocaleController.isRTL ? 0 : AndroidUtilities.dp(16.0f);
        layoutParams2.rightMargin = LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0;
        layoutParams2.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        this.avatarImage.setLayoutParams(layoutParams2);
        if (this.chatType != done_button) {
            this.avatarDrawable.setDrawPhoto(true);
            this.avatarImage.setOnClickListener(new C15733());
        }
        this.nameTextView = new EditText(context);
        this.nameTextView.setHint(this.chatType == 0 ? LocaleController.getString("EnterGroupNamePlaceholder", C0338R.string.EnterGroupNamePlaceholder) : LocaleController.getString("EnterListName", C0338R.string.EnterListName));
        if (this.nameToSet != null) {
            this.nameTextView.setText(this.nameToSet);
            this.nameToSet = null;
        }
        this.nameTextView.setMaxLines(4);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.nameTextView.setTextSize(done_button, 16.0f);
        this.nameTextView.setHintTextColor(Theme.SHARE_SHEET_EDIT_PLACEHOLDER_TEXT_COLOR);
        this.nameTextView.setImeOptions(268435456);
        this.nameTextView.setInputType(MessagesController.UPDATE_MASK_CHAT_ADMINS);
        this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
        InputFilter[] inputFilterArr = new InputFilter[done_button];
        inputFilterArr[0] = new LengthFilter(100);
        this.nameTextView.setFilters(inputFilterArr);
        AndroidUtilities.clearCursorDrawable(this.nameTextView);
        this.nameTextView.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        frameLayout.addView(this.nameTextView);
        layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -2;
        layoutParams2.leftMargin = LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(96.0f);
        layoutParams2.rightMargin = LocaleController.isRTL ? AndroidUtilities.dp(96.0f) : AndroidUtilities.dp(16.0f);
        layoutParams2.gravity = 16;
        this.nameTextView.setLayoutParams(layoutParams2);
        if (this.chatType != done_button) {
            this.nameTextView.addTextChangedListener(new C15744());
        }
        View greySectionCell = new GreySectionCell(context);
        greySectionCell.setText(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
        linearLayout.addView(greySectionCell);
        this.listView = new ListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        ListView listView = this.listView;
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        linearLayout.addView(this.listView);
        LayoutParams layoutParams3 = (LayoutParams) this.listView.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = -1;
        this.listView.setLayoutParams(layoutParams3);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        int intValue;
        if (i == NotificationCenter.updateInterfaces) {
            intValue = ((Integer) objArr[0]).intValue();
            if ((intValue & 2) != 0 || (intValue & done_button) != 0 || (intValue & 4) != 0) {
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
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", intValue);
            presentFragment(new ChatActivity(bundle), true);
            if (this.uploadedAvatar != null) {
                MessagesController.getInstance().changeChatAvatar(intValue, this.uploadedAvatar);
            }
        }
    }

    public void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new C15755(inputFile, photoSize));
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.avatarUpdater.onActivityResult(i, i2, intent);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidFailCreate);
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = this;
        this.selectedContacts = getArguments().getIntegerArrayList("result");
        ArrayList arrayList = new ArrayList();
        Iterator it = this.selectedContacts.iterator();
        while (it.hasNext()) {
            Integer num = (Integer) it.next();
            if (MessagesController.getInstance().getUser(num) == null) {
                arrayList.add(num);
            }
        }
        if (!arrayList.isEmpty()) {
            Semaphore semaphore = new Semaphore(0);
            ArrayList arrayList2 = new ArrayList();
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C15691(arrayList2, arrayList, semaphore));
            try {
                semaphore.acquire();
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            if (arrayList.size() != arrayList2.size()) {
                return false;
            }
            if (arrayList2.isEmpty()) {
                return false;
            }
            Iterator it2 = arrayList2.iterator();
            while (it2.hasNext()) {
                MessagesController.getInstance().putUser((User) it2.next(), true);
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidFailCreate);
        this.avatarUpdater.clear();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.nameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.nameTextView);
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
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
}

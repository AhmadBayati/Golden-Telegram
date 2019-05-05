package com.hanista.mobogram.telegraph.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.telegraph.model.UpdateModel;
import com.hanista.mobogram.tgnet.TLRPC.FileLocation;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.BackupImageView;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.PhotoViewer;
import com.hanista.mobogram.ui.PhotoViewer.PhotoViewerProvider;
import com.hanista.mobogram.ui.PhotoViewer.PlaceProviderObject;
import java.util.ArrayList;

public class UpdateActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private static final int delete = 2;
    private static final int filter = 3;
    private int currentFilterType;
    private UpdateCursorAdapter cursorAdapter;
    private DataBaseAccess dataBaseAccess;
    private TextView emptyTextView;
    private ActionBarMenuItem filterItem;
    private boolean justChangedPhoto;
    private int justForUserId;
    private ListView listView;
    private boolean paused;
    private UpdateModel selectedUpdate;
    private User selectedUser;
    protected BackupImageView selectedUserAvatar;

    /* renamed from: com.hanista.mobogram.telegraph.ui.UpdateActivity.1 */
    class C09261 extends ActionBarMenuOnItemClick {
        C09261() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                UpdateActivity.this.finishFragment();
            } else if (i == UpdateActivity.delete) {
                UpdateActivity.this.showDeleteHistoryConfirmation();
            } else if (i == UpdateActivity.filter) {
                UpdateActivity.this.showFilterDialog();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.telegraph.ui.UpdateActivity.2 */
    class C09272 implements OnTouchListener {
        C09272() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.telegraph.ui.UpdateActivity.3 */
    class C09283 implements OnItemClickListener {
        C09283() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            UpdateActivity.this.selectedUpdate = UpdateActivity.this.dataBaseAccess.m836a((Cursor) UpdateActivity.this.cursorAdapter.getItem(i));
            if (UpdateActivity.this.selectedUpdate != null) {
                UpdateActivity.this.selectedUser = MessagesController.getInstance().getUser(Integer.valueOf(UpdateActivity.this.selectedUpdate.getUserId()));
                if (UpdateActivity.this.selectedUser != null) {
                    UpdateActivity.this.selectedUserAvatar = ((UpdateCell) view).getAvatarImageView();
                    UpdateActivity.this.showUserActionsDialog();
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.telegraph.ui.UpdateActivity.4 */
    class C09294 implements OnClickListener {
        final /* synthetic */ ArrayList val$options;

        C09294(ArrayList arrayList) {
            this.val$options = arrayList;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            int intValue = ((Integer) this.val$options.get(i)).intValue();
            if (intValue == 1) {
                UpdateActivity.this.openChatActivity();
            } else if (intValue == UpdateActivity.delete) {
                PhotoViewer.getInstance().setParentActivity(UpdateActivity.this.getParentActivity());
                UpdateActivity.this.justChangedPhoto = false;
                PhotoViewer.getInstance().openPhoto(UpdateActivity.this.selectedUser.photo.photo_big, UpdateActivity.this);
            } else if (intValue == UpdateActivity.filter) {
                PhotoViewer.getInstance().setParentActivity(UpdateActivity.this.getParentActivity());
                if (UpdateActivity.this.selectedUpdate.getPhotoBig() != null) {
                    UpdateActivity.this.justChangedPhoto = true;
                    PhotoViewer.getInstance().openPhoto(UpdateActivity.this.selectedUpdate.getPhotoBig(), UpdateActivity.this);
                }
            } else if (intValue == 4) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", UpdateActivity.this.selectedUpdate.getUserId());
                UpdateActivity.this.presentFragment(new UpdateActivity(bundle));
            } else if (intValue == 5) {
                UpdateActivity.this.dataBaseAccess.m853a(UpdateActivity.this.selectedUpdate.getId());
                UpdateActivity.this.forceReload();
            }
            dialogInterface.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.telegraph.ui.UpdateActivity.5 */
    class C09305 implements OnClickListener {
        C09305() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (UpdateActivity.this.justForUserId == 0) {
                UpdateActivity.this.dataBaseAccess.m859b();
            } else {
                UpdateActivity.this.dataBaseAccess.m852a(UpdateActivity.this.justForUserId);
            }
            UpdateActivity.this.forceReload();
        }
    }

    /* renamed from: com.hanista.mobogram.telegraph.ui.UpdateActivity.6 */
    class C09316 implements OnClickListener {
        C09316() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 0) {
                UpdateActivity.this.filterItem.setIcon((int) C0338R.drawable.ic_ab_filter);
            } else {
                UpdateActivity.this.filterItem.setIcon((int) C0338R.drawable.ic_ab_filter_selected);
            }
            if (i == 0) {
                UpdateActivity.this.currentFilterType = 0;
            } else if (i == 1) {
                UpdateActivity.this.currentFilterType = UpdateActivity.delete;
            } else if (i == UpdateActivity.delete) {
                UpdateActivity.this.currentFilterType = UpdateActivity.filter;
            } else if (i == UpdateActivity.filter) {
                UpdateActivity.this.currentFilterType = 4;
            } else if (i == 4) {
                UpdateActivity.this.currentFilterType = 5;
            }
            UpdateActivity.this.forceReload();
            dialogInterface.dismiss();
        }
    }

    private class UpdateCursorAdapter extends CursorAdapter {
        private DataBaseAccess dataBaseAccess;

        /* renamed from: com.hanista.mobogram.telegraph.ui.UpdateActivity.UpdateCursorAdapter.1 */
        class C09321 implements View.OnClickListener {
            C09321() {
            }

            public void onClick(View view) {
                UpdateCell updateCell = (UpdateCell) view.getParent();
                UpdateActivity.this.selectedUpdate = updateCell.getUpdateModel();
                if (UpdateActivity.this.selectedUpdate != null) {
                    UpdateActivity.this.selectedUser = MessagesController.getInstance().getUser(Integer.valueOf(UpdateActivity.this.selectedUpdate.getUserId()));
                    if (UpdateActivity.this.selectedUser != null) {
                        UpdateActivity.this.selectedUserAvatar = updateCell.getAvatarImageView();
                        UpdateActivity.this.showUserActionsDialog();
                    }
                }
            }
        }

        public UpdateCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
            this.dataBaseAccess = new DataBaseAccess();
        }

        public void bindView(View view, Context context, Cursor cursor) {
            ((UpdateCell) view).setData(this.dataBaseAccess.m836a(cursor));
            ((UpdateCell) view).setOnOptionsClick(new C09321());
        }

        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return new UpdateCell(this.mContext, 10);
        }
    }

    public UpdateActivity(Bundle bundle) {
        super(bundle);
        this.currentFilterType = 0;
    }

    private void forceReload() {
        this.cursorAdapter.changeCursor(new DataBaseAccess().m832a(this.currentFilterType, 500, this.justForUserId));
        this.cursorAdapter.notifyDataSetChanged();
    }

    private void initTheme() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.bc;
            this.actionBar.setBackgroundColor(i);
            int i2 = AdvanceTheme.bd;
            if (i2 > 0) {
                Orientation orientation;
                switch (i2) {
                    case delete /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case filter /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i3 = AdvanceTheme.be;
                int[] iArr = new int[delete];
                iArr[0] = i;
                iArr[1] = i3;
                this.actionBar.setBackgroundDrawable(new GradientDrawable(orientation, iArr));
            }
            this.actionBar.setTitleColor(AdvanceTheme.bb);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_delete).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_filter).setColorFilter(AdvanceTheme.ba, Mode.SRC_IN);
        }
    }

    private void openChatActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", this.selectedUser.id);
        presentFragment(new ChatActivity(bundle), false);
    }

    private void showDeleteHistoryConfirmation() {
        Builder builder = new Builder(getParentActivity());
        builder.setMessage(this.justForUserId == 0 ? LocaleController.getString("AreYouSureDeleteChanges", C0338R.string.AreYouSureDeleteChanges) : LocaleController.getString("AreYouSureDeleteContactChanges", C0338R.string.AreYouSureDeleteContactChanges));
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C09305());
        builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
        showDialog(builder.create());
    }

    public boolean allowCaption() {
        return true;
    }

    public boolean cancelButtonPressed() {
        return false;
    }

    public View createView(Context context) {
        this.fragmentView = new FrameLayout(context);
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_back);
            drawable.setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            this.actionBar.setBackButtonDrawable(drawable);
        } else {
            this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ContactsChanges", C0338R.string.ContactsChanges));
        if (this.justForUserId != 0) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.justForUserId));
            if (user != null) {
                this.actionBar.setTitle(ContactsController.formatName(user.first_name, user.last_name));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new C09261());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addItem((int) delete, (int) C0338R.drawable.ic_delete);
        this.filterItem = createMenu.addItem((int) filter, (int) C0338R.drawable.ic_ab_filter);
        this.dataBaseAccess = new DataBaseAccess();
        View linearLayout = new LinearLayout(context);
        if (ThemeUtil.m2490b()) {
            linearLayout.setBackgroundColor(AdvanceTheme.aU);
        }
        linearLayout.setVisibility(4);
        linearLayout.setOrientation(1);
        ((FrameLayout) this.fragmentView).addView(linearLayout);
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnTouchListener(new C09272());
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTextColor(-8355712);
        if (ThemeUtil.m2490b()) {
            this.emptyTextView.setTextColor(AdvanceTheme.aR);
        }
        this.emptyTextView.setTextSize(1, 20.0f);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.emptyTextView.setText(LocaleController.getString("NoContactChanges", C0338R.string.NoContactChanges));
        linearLayout.addView(this.emptyTextView);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.emptyTextView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        this.emptyTextView.setLayoutParams(layoutParams2);
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams2);
        this.cursorAdapter = new UpdateCursorAdapter(context, new DataBaseAccess().m832a(this.currentFilterType, 500, this.justForUserId));
        this.listView = new ListView(context);
        initThemeBackground(this.listView);
        this.listView.setEmptyView(linearLayout);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setCacheColorHint(0);
        this.listView.setScrollingCacheEnabled(false);
        this.listView.setAdapter(this.cursorAdapter);
        AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
        ((FrameLayout) this.fragmentView).addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setOnItemClickListener(new C09283());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (!this.paused) {
            UpdateNotificationUtil.dismissNotification();
            this.dataBaseAccess.m851a();
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        }
        forceReload();
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        PlaceProviderObject placeProviderObject = null;
        if (fileLocation != null) {
            FileLocation photoBig;
            int[] iArr;
            if (!(this.selectedUser == null || this.selectedUser.id == 0)) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.selectedUser.id));
                if (this.selectedUpdate != null && this.selectedUpdate.getPhotoBig() != null && this.justChangedPhoto) {
                    photoBig = this.selectedUpdate.getPhotoBig();
                    iArr = new int[delete];
                    this.selectedUserAvatar.getLocationInWindow(iArr);
                    placeProviderObject = new PlaceProviderObject();
                    placeProviderObject.viewX = iArr[0];
                    placeProviderObject.viewY = iArr[1] - AndroidUtilities.statusBarHeight;
                    placeProviderObject.parentView = this.selectedUserAvatar;
                    placeProviderObject.imageReceiver = this.selectedUserAvatar.getImageReceiver();
                    placeProviderObject.dialogId = this.selectedUser.id;
                    if (this.justChangedPhoto) {
                        placeProviderObject.dialogId = 0;
                    }
                    placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
                    placeProviderObject.size = -1;
                    placeProviderObject.radius = this.selectedUserAvatar.getImageReceiver().getRoundRadius();
                } else if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                    photoBig = user.photo.photo_big;
                    if (photoBig != null && photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
                        iArr = new int[delete];
                        this.selectedUserAvatar.getLocationInWindow(iArr);
                        placeProviderObject = new PlaceProviderObject();
                        placeProviderObject.viewX = iArr[0];
                        placeProviderObject.viewY = iArr[1] - AndroidUtilities.statusBarHeight;
                        placeProviderObject.parentView = this.selectedUserAvatar;
                        placeProviderObject.imageReceiver = this.selectedUserAvatar.getImageReceiver();
                        placeProviderObject.dialogId = this.selectedUser.id;
                        if (this.justChangedPhoto) {
                            placeProviderObject.dialogId = 0;
                        }
                        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
                        placeProviderObject.size = -1;
                        placeProviderObject.radius = this.selectedUserAvatar.getImageReceiver().getRoundRadius();
                    }
                }
            }
            photoBig = null;
            iArr = new int[delete];
            this.selectedUserAvatar.getLocationInWindow(iArr);
            placeProviderObject = new PlaceProviderObject();
            placeProviderObject.viewX = iArr[0];
            placeProviderObject.viewY = iArr[1] - AndroidUtilities.statusBarHeight;
            placeProviderObject.parentView = this.selectedUserAvatar;
            placeProviderObject.imageReceiver = this.selectedUserAvatar.getImageReceiver();
            placeProviderObject.dialogId = this.selectedUser.id;
            if (this.justChangedPhoto) {
                placeProviderObject.dialogId = 0;
            }
            placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
            placeProviderObject.size = -1;
            placeProviderObject.radius = this.selectedUserAvatar.getImageReceiver().getRoundRadius();
        }
        return placeProviderObject;
    }

    public int getSelectedCount() {
        return 0;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (getArguments() != null) {
            this.justForUserId = this.arguments.getInt("user_id", 0);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    public void onResume() {
        super.onResume();
        this.paused = false;
        UpdateNotificationUtil.dismissNotification();
        this.dataBaseAccess.m851a();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        forceReload();
        initTheme();
    }

    public boolean scaleToFill() {
        return false;
    }

    public void sendButtonPressed(int i) {
    }

    public void setPhotoChecked(int i) {
    }

    protected void showFilterDialog() {
        int i = 0;
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(C0338R.string.filter_title);
        CharSequence[] charSequenceArr = new CharSequence[]{r1.getString(C0338R.string.AllChanges), r1.getString(C0338R.string.change_name), r1.getString(C0338R.string.change_photo), r1.getString(C0338R.string.change_phone), r1.getString(C0338R.string.change_mutual)};
        if (this.currentFilterType != 0) {
            i = this.currentFilterType - 1;
        }
        builder.setSingleChoiceItems(charSequenceArr, i, new C09316());
        showDialog(builder.create());
    }

    protected void showUserActionsDialog() {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(ContactsController.formatName(this.selectedUser.first_name, this.selectedUser.last_name));
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(getParentActivity().getString(C0338R.string.send_message_in_telegram));
        arrayList2.add(Integer.valueOf(1));
        if (!(this.selectedUser.photo == null || this.selectedUser.photo.photo_big == null)) {
            arrayList.add(getParentActivity().getString(C0338R.string.show_user_photos));
            arrayList2.add(Integer.valueOf(delete));
        }
        if (this.selectedUpdate.getPhotoBig() != null) {
            arrayList.add(getParentActivity().getString(C0338R.string.ShowChangedPhoto));
            arrayList2.add(Integer.valueOf(filter));
        }
        if (this.justForUserId == 0) {
            arrayList.add(getParentActivity().getString(C0338R.string.ShowHistory));
            arrayList2.add(Integer.valueOf(4));
        }
        arrayList.add(getParentActivity().getString(C0338R.string.Delete));
        arrayList2.add(Integer.valueOf(5));
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new C09294(arrayList2));
        showDialog(builder.create());
    }

    public void updatePhotoAtIndex(int i) {
    }

    public void willHidePhotoViewer() {
        if (this.selectedUserAvatar != null) {
            this.selectedUserAvatar.getImageReceiver().setVisible(true, true);
        }
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}

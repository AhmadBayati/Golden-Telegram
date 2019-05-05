package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.TextInfoCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.ContactsActivity.ContactsActivityDelegate;

public class BlockedUsersActivity extends BaseFragment implements NotificationCenterDelegate, ContactsActivityDelegate {
    private static final int block_user = 1;
    private TextView emptyTextView;
    private ListView listView;
    private ListAdapter listViewAdapter;
    private FrameLayout progressView;
    private int selectedUserId;

    /* renamed from: com.hanista.mobogram.ui.BlockedUsersActivity.1 */
    class C10651 extends ActionBarMenuOnItemClick {
        C10651() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                BlockedUsersActivity.this.finishFragment();
            } else if (i == BlockedUsersActivity.block_user) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("returnAsResult", true);
                BaseFragment contactsActivity = new ContactsActivity(bundle);
                contactsActivity.setDelegate(BlockedUsersActivity.this);
                BlockedUsersActivity.this.presentFragment(contactsActivity);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.BlockedUsersActivity.2 */
    class C10662 implements OnTouchListener {
        C10662() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.BlockedUsersActivity.3 */
    class C10673 implements OnItemClickListener {
        C10673() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i < MessagesController.getInstance().blockedUsers.size()) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", ((Integer) MessagesController.getInstance().blockedUsers.get(i)).intValue());
                BlockedUsersActivity.this.presentFragment(new ProfileActivity(bundle));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.BlockedUsersActivity.4 */
    class C10694 implements OnItemLongClickListener {

        /* renamed from: com.hanista.mobogram.ui.BlockedUsersActivity.4.1 */
        class C10681 implements OnClickListener {
            C10681() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    MessagesController.getInstance().unblockUser(BlockedUsersActivity.this.selectedUserId);
                }
            }
        }

        C10694() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i >= 0 && i < MessagesController.getInstance().blockedUsers.size() && BlockedUsersActivity.this.getParentActivity() != null) {
                BlockedUsersActivity.this.selectedUserId = ((Integer) MessagesController.getInstance().blockedUsers.get(i)).intValue();
                Builder builder = new Builder(BlockedUsersActivity.this.getParentActivity());
                CharSequence[] charSequenceArr = new CharSequence[BlockedUsersActivity.block_user];
                charSequenceArr[0] = LocaleController.getString("Unblock", C0338R.string.Unblock);
                builder.setItems(charSequenceArr, new C10681());
                BlockedUsersActivity.this.showDialog(builder.create());
            }
            return true;
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
            return MessagesController.getInstance().blockedUsers.isEmpty() ? 0 : MessagesController.getInstance().blockedUsers.size() + BlockedUsersActivity.block_user;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == MessagesController.getInstance().blockedUsers.size() ? BlockedUsersActivity.block_user : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            View userCell;
            if (itemViewType == 0) {
                if (view == null) {
                    userCell = new UserCell(this.mContext, BlockedUsersActivity.block_user, 0, false);
                    userCell.setTag("Pref");
                } else {
                    userCell = view;
                }
                if (ThemeUtil.m2490b()) {
                    ((UserCell) userCell).setNameColor(AdvanceTheme.f2494e);
                    ((UserCell) userCell).setStatusColor(AdvanceTheme.f2495f);
                }
                TLObject user = MessagesController.getInstance().getUser((Integer) MessagesController.getInstance().blockedUsers.get(i));
                if (user == null) {
                    return userCell;
                }
                CharSequence charSequence;
                if (user.bot) {
                    charSequence = LocaleController.getString("Bot", C0338R.string.Bot).substring(0, BlockedUsersActivity.block_user).toUpperCase() + LocaleController.getString("Bot", C0338R.string.Bot).substring(BlockedUsersActivity.block_user);
                } else {
                    Object string = (user.phone == null || user.phone.length() == 0) ? LocaleController.getString("NumberUnknown", C0338R.string.NumberUnknown) : PhoneFormat.getInstance().format("+" + user.phone);
                }
                ((UserCell) userCell).setData(user, null, charSequence, 0);
                return userCell;
            } else if (itemViewType != BlockedUsersActivity.block_user || view != null) {
                return view;
            } else {
                userCell = new TextInfoCell(this.mContext);
                ((TextInfoCell) userCell).setText(LocaleController.getString("UnblockText", C0338R.string.UnblockText));
                if (!ThemeUtil.m2490b()) {
                    return userCell;
                }
                ((TextInfoCell) userCell).setTextColor(AdvanceTheme.f2495f);
                return userCell;
            }
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return MessagesController.getInstance().blockedUsers.isEmpty();
        }

        public boolean isEnabled(int i) {
            return i != MessagesController.getInstance().blockedUsers.size();
        }
    }

    private void updateVisibleRows(int i) {
        if (this.listView != null) {
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2 += block_user) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                }
            }
        }
    }

    public View createView(Context context) {
        int i = block_user;
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("BlockedUsers", C0338R.string.BlockedUsers));
        this.actionBar.setActionBarMenuOnItemClick(new C10651());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.plus);
            drawable.setColorFilter(AdvanceTheme.f2492c, Mode.SRC_IN);
            createMenu.addItem((int) block_user, drawable);
        } else {
            createMenu.addItem((int) block_user, (int) C0338R.drawable.plus);
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyTextView = new TextView(context);
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(20.0f);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setVisibility(4);
        this.emptyTextView.setTypeface(FontUtil.m1176a().m1161d());
        this.emptyTextView.setText(LocaleController.getString("NoBlocked", C0338R.string.NoBlocked));
        frameLayout.addView(this.emptyTextView, LayoutHelper.createFrame(-1, -1, 51));
        this.emptyTextView.setOnTouchListener(new C10662());
        this.progressView = new FrameLayout(context);
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.progressView.addView(new ProgressBar(context), LayoutHelper.createFrame(-2, -2, 17));
        this.listView = new ListView(context);
        initThemeBackground(this.listView);
        this.listView.setEmptyView(this.emptyTextView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        ListView listView = this.listView;
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        listView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        listView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        this.listView.setOnItemClickListener(new C10673());
        this.listView.setOnItemLongClickListener(new C10694());
        if (MessagesController.getInstance().loadingBlockedUsers) {
            this.progressView.setVisibility(0);
            this.emptyTextView.setVisibility(8);
            this.listView.setEmptyView(null);
        } else {
            this.progressView.setVisibility(8);
            this.listView.setEmptyView(this.emptyTextView);
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if ((intValue & 2) != 0 || (intValue & block_user) != 0) {
                updateVisibleRows(intValue);
            }
        } else if (i == NotificationCenter.blockedUsersDidLoaded) {
            if (this.progressView != null) {
                this.progressView.setVisibility(8);
            }
            if (this.listView != null && this.listView.getEmptyView() == null) {
                this.listView.setEmptyView(this.emptyTextView);
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void didSelectContact(User user, String str) {
        if (user != null) {
            MessagesController.getInstance().blockUser(user.id);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
        MessagesController.getInstance().getBlockedUsers(false);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
        initThemeActionBar();
    }
}

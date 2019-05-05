package com.hanista.mobogram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.TextInfoCell;
import com.hanista.mobogram.ui.Cells.UserCell;
import com.hanista.mobogram.ui.GroupCreateActivity.GroupCreateActivityDelegate;
import java.util.ArrayList;
import java.util.Iterator;

public class PrivacyUsersActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int block_user = 1;
    private PrivacyActivityDelegate delegate;
    private boolean isAlwaysShare;
    private boolean isGroup;
    private ListView listView;
    private ListAdapter listViewAdapter;
    private int selectedUserId;
    private ArrayList<Integer> uidArray;

    public interface PrivacyActivityDelegate {
        void didUpdatedUserList(ArrayList<Integer> arrayList, boolean z);
    }

    /* renamed from: com.hanista.mobogram.ui.PrivacyUsersActivity.1 */
    class C18191 extends ActionBarMenuOnItemClick {

        /* renamed from: com.hanista.mobogram.ui.PrivacyUsersActivity.1.1 */
        class C18181 implements GroupCreateActivityDelegate {
            C18181() {
            }

            public void didSelectUsers(ArrayList<Integer> arrayList) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    Integer num = (Integer) it.next();
                    if (!PrivacyUsersActivity.this.uidArray.contains(num)) {
                        PrivacyUsersActivity.this.uidArray.add(num);
                    }
                }
                PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                if (PrivacyUsersActivity.this.delegate != null) {
                    PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, true);
                }
            }
        }

        C18191() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PrivacyUsersActivity.this.finishFragment();
            } else if (i == PrivacyUsersActivity.block_user) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(PrivacyUsersActivity.this.isAlwaysShare ? "isAlwaysShare" : "isNeverShare", true);
                bundle.putBoolean("isGroup", PrivacyUsersActivity.this.isGroup);
                BaseFragment groupCreateActivity = new GroupCreateActivity(bundle);
                groupCreateActivity.setDelegate(new C18181());
                PrivacyUsersActivity.this.presentFragment(groupCreateActivity);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PrivacyUsersActivity.2 */
    class C18202 implements OnTouchListener {
        C18202() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PrivacyUsersActivity.3 */
    class C18213 implements OnItemClickListener {
        C18213() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i < PrivacyUsersActivity.this.uidArray.size()) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", ((Integer) PrivacyUsersActivity.this.uidArray.get(i)).intValue());
                PrivacyUsersActivity.this.presentFragment(new ProfileActivity(bundle));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.PrivacyUsersActivity.4 */
    class C18234 implements OnItemLongClickListener {

        /* renamed from: com.hanista.mobogram.ui.PrivacyUsersActivity.4.1 */
        class C18221 implements OnClickListener {
            C18221() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    PrivacyUsersActivity.this.uidArray.remove(Integer.valueOf(PrivacyUsersActivity.this.selectedUserId));
                    PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    if (PrivacyUsersActivity.this.delegate != null) {
                        PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, false);
                    }
                }
            }
        }

        C18234() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i >= 0 && i < PrivacyUsersActivity.this.uidArray.size() && PrivacyUsersActivity.this.getParentActivity() != null) {
                PrivacyUsersActivity.this.selectedUserId = ((Integer) PrivacyUsersActivity.this.uidArray.get(i)).intValue();
                Builder builder = new Builder(PrivacyUsersActivity.this.getParentActivity());
                CharSequence[] charSequenceArr = new CharSequence[PrivacyUsersActivity.block_user];
                charSequenceArr[0] = LocaleController.getString("Delete", C0338R.string.Delete);
                builder.setItems(charSequenceArr, new C18221());
                PrivacyUsersActivity.this.showDialog(builder.create());
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
            return PrivacyUsersActivity.this.uidArray.isEmpty() ? 0 : PrivacyUsersActivity.this.uidArray.size() + PrivacyUsersActivity.block_user;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == PrivacyUsersActivity.this.uidArray.size() ? PrivacyUsersActivity.block_user : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            View userCell;
            if (itemViewType == 0) {
                userCell = view == null ? new UserCell(this.mContext, PrivacyUsersActivity.block_user, 0, false) : view;
                userCell.setTag("Contacts");
                TLObject user = MessagesController.getInstance().getUser((Integer) PrivacyUsersActivity.this.uidArray.get(i));
                UserCell userCell2 = (UserCell) userCell;
                CharSequence string = (user.phone == null || user.phone.length() == 0) ? LocaleController.getString("NumberUnknown", C0338R.string.NumberUnknown) : PhoneFormat.getInstance().format("+" + user.phone);
                userCell2.setData(user, null, string, 0);
                return userCell;
            } else if (itemViewType != PrivacyUsersActivity.block_user || view != null) {
                return view;
            } else {
                userCell = new TextInfoCell(this.mContext);
                ((TextInfoCell) userCell).setText(LocaleController.getString("RemoveFromListText", C0338R.string.RemoveFromListText));
                if (!ThemeUtil.m2490b()) {
                    return userCell;
                }
                userCell.setBackgroundColor(AdvanceTheme.f2497h);
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
            return PrivacyUsersActivity.this.uidArray.isEmpty();
        }

        public boolean isEnabled(int i) {
            return i != PrivacyUsersActivity.this.uidArray.size();
        }
    }

    public PrivacyUsersActivity(ArrayList<Integer> arrayList, boolean z, boolean z2) {
        this.uidArray = arrayList;
        this.isAlwaysShare = z2;
        this.isGroup = z;
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
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.isGroup) {
            if (this.isAlwaysShare) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", C0338R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", C0338R.string.NeverAllow));
            }
        } else if (this.isAlwaysShare) {
            this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", C0338R.string.AlwaysShareWithTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", C0338R.string.NeverShareWithTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C18191());
        this.actionBar.createMenu().addItem((int) block_user, (int) C0338R.drawable.plus);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        View textView = new TextView(context);
        textView.setTextColor(-8355712);
        textView.setTextSize(20.0f);
        textView.setGravity(17);
        textView.setVisibility(4);
        textView.setText(LocaleController.getString("NoContacts", C0338R.string.NoContacts));
        frameLayout.addView(textView);
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        textView.setLayoutParams(layoutParams);
        textView.setOnTouchListener(new C18202());
        this.listView = new ListView(context);
        initThemeBackground(this.listView);
        this.listView.setEmptyView(textView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        ListView listView = this.listView;
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? block_user : 2);
        frameLayout.addView(this.listView);
        LayoutParams layoutParams2 = (LayoutParams) this.listView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        this.listView.setLayoutParams(layoutParams2);
        this.listView.setOnItemClickListener(new C18213());
        this.listView.setOnItemLongClickListener(new C18234());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if ((intValue & 2) != 0 || (intValue & block_user) != 0) {
                updateVisibleRows(intValue);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
        initThemeActionBar();
    }

    public void setDelegate(PrivacyActivityDelegate privacyActivityDelegate) {
        this.delegate = privacyActivityDelegate;
    }
}

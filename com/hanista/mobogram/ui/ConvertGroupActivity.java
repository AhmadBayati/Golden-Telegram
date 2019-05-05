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
import android.widget.FrameLayout;
import android.widget.ListView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;

public class ConvertGroupActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chat_id;
    private int convertDetailRow;
    private int convertInfoRow;
    private int convertRow;
    private ListAdapter listAdapter;
    private int rowCount;

    /* renamed from: com.hanista.mobogram.ui.ConvertGroupActivity.1 */
    class C15241 extends ActionBarMenuOnItemClick {
        C15241() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ConvertGroupActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.ui.ConvertGroupActivity.2 */
    class C15262 implements OnItemClickListener {

        /* renamed from: com.hanista.mobogram.ui.ConvertGroupActivity.2.1 */
        class C15251 implements OnClickListener {
            C15251() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance().convertToMegaGroup(ConvertGroupActivity.this.getParentActivity(), ConvertGroupActivity.this.chat_id);
            }
        }

        C15262() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i == ConvertGroupActivity.this.convertRow) {
                Builder builder = new Builder(ConvertGroupActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("ConvertGroupAlert", C0338R.string.ConvertGroupAlert));
                builder.setTitle(LocaleController.getString("ConvertGroupAlertWarning", C0338R.string.ConvertGroupAlertWarning));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new C15251());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                ConvertGroupActivity.this.showDialog(builder.create());
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
            return ConvertGroupActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == ConvertGroupActivity.this.convertRow ? 0 : (i == ConvertGroupActivity.this.convertInfoRow || i == ConvertGroupActivity.this.convertDetailRow) ? 1 : 0;
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
                if (i != ConvertGroupActivity.this.convertRow) {
                    return textSettingsCell;
                }
                textSettingsCell2.setText(LocaleController.getString("ConvertGroup", C0338R.string.ConvertGroup), false);
                return textSettingsCell;
            } else if (itemViewType != 1) {
                return view;
            } else {
                textSettingsCell = view == null ? new TextInfoPrivacyCell(this.mContext) : view;
                if (i == ConvertGroupActivity.this.convertInfoRow) {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(AndroidUtilities.replaceTags(LocaleController.getString("ConvertGroupInfo2", C0338R.string.ConvertGroupInfo2)));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider);
                    return textSettingsCell;
                } else if (i != ConvertGroupActivity.this.convertDetailRow) {
                    return textSettingsCell;
                } else {
                    ((TextInfoPrivacyCell) textSettingsCell).setText(AndroidUtilities.replaceTags(LocaleController.getString("ConvertGroupInfo3", C0338R.string.ConvertGroupInfo3)));
                    textSettingsCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textSettingsCell;
                }
            }
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == ConvertGroupActivity.this.convertRow;
        }
    }

    public ConvertGroupActivity(Bundle bundle) {
        super(bundle);
        this.chat_id = bundle.getInt("chat_id");
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ConvertGroup", C0338R.string.ConvertGroup));
        this.actionBar.setActionBarMenuOnItemClick(new C15241());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        View listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY));
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new C15262());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.convertInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.convertRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.convertDetailRow = i;
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}

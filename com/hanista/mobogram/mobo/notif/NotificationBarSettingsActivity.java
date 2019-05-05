package com.hanista.mobogram.mobo.notif;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Cells.TextInfoCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.LayoutHelper;

/* renamed from: com.hanista.mobogram.mobo.notif.a */
public class NotificationBarSettingsActivity extends BaseFragment {
    private ListView f1966a;
    private NotificationBarSettingsActivity f1967b;
    private int f1968c;
    private int f1969d;
    private int f1970e;
    private int f1971f;

    /* renamed from: com.hanista.mobogram.mobo.notif.a.1 */
    class NotificationBarSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ NotificationBarSettingsActivity f1962a;

        NotificationBarSettingsActivity(NotificationBarSettingsActivity notificationBarSettingsActivity) {
            this.f1962a = notificationBarSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f1962a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.notif.a.2 */
    class NotificationBarSettingsActivity implements OnItemClickListener {
        final /* synthetic */ NotificationBarSettingsActivity f1963a;

        NotificationBarSettingsActivity(NotificationBarSettingsActivity notificationBarSettingsActivity) {
            this.f1963a = notificationBarSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            boolean z2;
            TextDetailCheckCell textDetailCheckCell;
            if (i == this.f1963a.f1968c) {
                z2 = MoboConstants.aL;
                edit.putBoolean("notif_show_reply_btn", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f1963a.f1969d) {
                z2 = MoboConstants.aM;
                edit.putBoolean("notif_show_mark_as_read_btn", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f1963a.f1970e) {
                z2 = MoboConstants.aN;
                edit.putBoolean("notif_mark_as_read_in_popup", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
                MoboConstants.m1379a();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.notif.a.a */
    private class NotificationBarSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ NotificationBarSettingsActivity f1964a;
        private Context f1965b;

        public NotificationBarSettingsActivity(NotificationBarSettingsActivity notificationBarSettingsActivity, Context context) {
            this.f1964a = notificationBarSettingsActivity;
            this.f1965b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f1964a.f1971f;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == this.f1964a.f1969d || i == this.f1964a.f1968c || i == this.f1964a.f1970e) ? 8 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f1965b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f1965b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f1965b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f1965b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f1965b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        if (i == this.f1964a.f1968c) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowReplyButton", C0338R.string.ShowReplyButton), LocaleController.getString("ShowReplyButtonDetail", C0338R.string.ShowReplyButtonDetail), MoboConstants.aL, true);
                        } else if (i == this.f1964a.f1969d) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowMarkAsReadButton", C0338R.string.ShowMarkAsReadButton), LocaleController.getString("ShowMarkAsReadButtonDetail", C0338R.string.ShowMarkAsReadButtonDetail), MoboConstants.aM, true);
                        } else if (i == this.f1964a.f1970e) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("MarkAsReadInPopup", C0338R.string.MarkAsReadInPopup), LocaleController.getString("MarkAsReadInPopupDetail", C0338R.string.MarkAsReadInPopupDetail), MoboConstants.aN, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f1965b) : view;
                    } else {
                        if (itemViewType == 5) {
                            return view == null ? new TextInfoCell(this.f1965b) : view;
                        } else {
                            if (itemViewType != 6) {
                                return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f1965b) : view;
                            } else {
                                textSettingsCell = view == null ? new TextDetailSettingsCell(this.f1965b) : view;
                                TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                                return textSettingsCell;
                            }
                        }
                    }
                }
            }
        }

        public int getViewTypeCount() {
            return 9;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == this.f1964a.f1969d || i == this.f1964a.f1970e || i == this.f1964a.f1968c;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationBarSettings", C0338R.string.NotificationBarSettings));
        this.actionBar.setActionBarMenuOnItemClick(new NotificationBarSettingsActivity(this));
        this.f1967b = new NotificationBarSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f1966a = new ListView(context);
        initThemeBackground(this.f1966a);
        this.f1966a.setDivider(null);
        this.f1966a.setDividerHeight(0);
        this.f1966a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f1966a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f1966a, LayoutHelper.createFrame(-1, -1, 51));
        this.f1966a.setAdapter(this.f1967b);
        this.f1966a.setOnItemClickListener(new NotificationBarSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.f1971f = 0;
        int i = this.f1971f;
        this.f1971f = i + 1;
        this.f1968c = i;
        i = this.f1971f;
        this.f1971f = i + 1;
        this.f1969d = i;
        i = this.f1971f;
        this.f1971f = i + 1;
        this.f1970e = i;
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}

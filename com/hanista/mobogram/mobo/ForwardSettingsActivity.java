package com.hanista.mobogram.mobo;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
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
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.p019r.TabData;
import com.hanista.mobogram.mobo.p019r.TabsUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.EmptyCell;
import com.hanista.mobogram.ui.Cells.HeaderCell;
import com.hanista.mobogram.ui.Cells.ShadowSectionCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.AvatarDrawable;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.h */
public class ForwardSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListView f1103a;
    private ForwardSettingsActivity f1104b;
    private int f1105c;
    private int f1106d;
    private int f1107e;
    private int f1108f;
    private int f1109g;
    private int f1110h;
    private int f1111i;
    private int f1112j;
    private int f1113k;
    private int f1114l;
    private int f1115m;

    /* renamed from: com.hanista.mobogram.mobo.h.1 */
    class ForwardSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ForwardSettingsActivity f1094a;

        ForwardSettingsActivity(ForwardSettingsActivity forwardSettingsActivity) {
            this.f1094a = forwardSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f1094a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.h.2 */
    class ForwardSettingsActivity implements OnItemClickListener {
        final /* synthetic */ ForwardSettingsActivity f1097a;

        /* renamed from: com.hanista.mobogram.mobo.h.2.1 */
        class ForwardSettingsActivity implements OnClickListener {
            final /* synthetic */ List f1095a;
            final /* synthetic */ ForwardSettingsActivity f1096b;

            ForwardSettingsActivity(ForwardSettingsActivity forwardSettingsActivity, List list) {
                this.f1096b = forwardSettingsActivity;
                this.f1095a = list;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putBoolean("multi_forward_last_selected_tab", false);
                if (i == this.f1095a.size()) {
                    edit.putBoolean("multi_forward_last_selected_tab", true);
                } else {
                    edit.putInt("multi_forward_default_tab", ((TabData) this.f1095a.get(i)).m2225a());
                }
                edit.commit();
                if (this.f1096b.f1097a.f1103a != null) {
                    this.f1096b.f1097a.f1103a.invalidateViews();
                }
                MoboConstants.m1379a();
                this.f1096b.f1097a.parentLayout.rebuildAllFragmentViews(false);
            }
        }

        ForwardSettingsActivity(ForwardSettingsActivity forwardSettingsActivity) {
            this.f1097a = forwardSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
            Editor edit = sharedPreferences.edit();
            boolean z2;
            TextDetailCheckCell textDetailCheckCell;
            if (i == this.f1097a.f1107e) {
                z2 = sharedPreferences.getBoolean("multi_forward", false);
                edit.putBoolean("multi_forward", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
            } else if (i == this.f1097a.f1108f) {
                z2 = sharedPreferences.getBoolean("forward_no_name_without_caption", false);
                edit.putBoolean("forward_no_name_without_caption", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
            } else if (i == this.f1097a.f1111i) {
                z2 = MoboConstants.ah;
                edit.putBoolean("multi_forward_show_tabs", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
            } else if (i == this.f1097a.f1112j) {
                Builder builder = new Builder(this.f1097a.getParentActivity());
                builder.setTitle(LocaleController.getString("DefaultTab", C0338R.string.DefaultTab));
                List<TabData> a = TabsUtil.m2260a(true, true);
                List arrayList = new ArrayList();
                for (TabData d : a) {
                    arrayList.add(d.m2232d());
                }
                arrayList.add(LocaleController.getString("LastSelectedTab", C0338R.string.LastSelectedTab));
                builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new ForwardSettingsActivity(this, a));
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                this.f1097a.showDialog(builder.create());
            } else if (i == this.f1097a.f1113k) {
                z2 = MoboConstants.aj;
                edit.putBoolean("multi_forward_show_as_list", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
            } else if (i == this.f1097a.f1114l) {
                z2 = MoboConstants.ai;
                edit.putBoolean("multi_forward_show_phone_contact_tab", !z2);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    textDetailCheckCell = (TextDetailCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textDetailCheckCell.setChecked(z);
                }
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.h.a */
    private class ForwardSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ ForwardSettingsActivity f1098a;
        private Context f1099b;

        public ForwardSettingsActivity(ForwardSettingsActivity forwardSettingsActivity, Context context) {
            this.f1098a = forwardSettingsActivity;
            this.f1099b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f1098a.f1115m;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == this.f1098a.f1107e || i == this.f1098a.f1108f || i == this.f1098a.f1111i || i == this.f1098a.f1113k || i == this.f1098a.f1114l) ? 8 : i == this.f1098a.f1112j ? 6 : (i == this.f1098a.f1106d || i == this.f1098a.f1110h) ? 4 : (i == this.f1098a.f1105c || i == this.f1098a.f1109g) ? 7 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                if (view == null) {
                    return new EmptyCell(this.f1099b);
                }
            } else if (itemViewType == 1) {
                if (view == null) {
                    return new TextInfoPrivacyCell(this.f1099b);
                }
            } else if (itemViewType == 2) {
                r1 = view == null ? new TextSettingsCell(this.f1099b) : view;
                TextSettingsCell textSettingsCell = (TextSettingsCell) r1;
                return r1;
            } else if (itemViewType == 3) {
                r1 = view == null ? new TextCheckCell(this.f1099b) : view;
                TextCheckCell textCheckCell = (TextCheckCell) r1;
                return r1;
            } else if (itemViewType == 8) {
                r1 = view == null ? new TextDetailCheckCell(this.f1099b) : view;
                TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) r1;
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                if (i == this.f1098a.f1107e) {
                    textDetailCheckCell.setTextAndCheck(LocaleController.getString("MultiForward", C0338R.string.MultiForward), LocaleController.getString("MultiForwardDetail", C0338R.string.MultiForwardDetail), sharedPreferences.getBoolean("multi_forward", false), true);
                    return r1;
                } else if (i == this.f1098a.f1108f) {
                    textDetailCheckCell.setTextAndCheck(LocaleController.getString("ForwardNoNameWithoutCaption", C0338R.string.ForwardNoNameWithoutCaption), LocaleController.getString("ForwardNoNameWithoutCaptionDetail", C0338R.string.ForwardNoNameWithoutCaptionDetail), sharedPreferences.getBoolean("forward_no_name_without_caption", false), true);
                    return r1;
                } else if (i == this.f1098a.f1111i) {
                    textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowTabs", C0338R.string.ShowTabs), LocaleController.getString("ShowTabsDetail", C0338R.string.ShowTabsDetail), MoboConstants.ah, true);
                    return r1;
                } else if (i == this.f1098a.f1113k) {
                    textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowAsList", C0338R.string.ShowAsList), LocaleController.getString("ShowAsListDetail", C0338R.string.ShowAsListDetail), MoboConstants.aj, true);
                    return r1;
                } else if (i != this.f1098a.f1114l) {
                    return r1;
                } else {
                    textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowPhoneContactsTab", C0338R.string.ShowPhoneContactsTab), LocaleController.getString("ShowPhoneContactsTabDetail", C0338R.string.ShowPhoneContactsTabDetail), MoboConstants.ai, true);
                    return r1;
                }
            } else if (itemViewType == 4) {
                r1 = view == null ? new HeaderCell(this.f1099b) : view;
                if (i == this.f1098a.f1106d) {
                    ((HeaderCell) r1).setText(LocaleController.getString("Forward", C0338R.string.Forward));
                    return r1;
                } else if (i != this.f1098a.f1110h) {
                    return r1;
                } else {
                    ((HeaderCell) r1).setText(LocaleController.getString("MultiForwardPanel", C0338R.string.MultiForwardPanel));
                    return r1;
                }
            } else if (itemViewType == 6) {
                r1 = view == null ? new TextDetailSettingsCell(this.f1099b) : view;
                TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) r1;
                if (i != this.f1098a.f1112j) {
                    return r1;
                }
                textDetailSettingsCell.setMultilineDetail(false);
                if (MoboConstants.al) {
                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("DefaultTab", C0338R.string.DefaultTab), LocaleController.getString("LastSelectedTab", C0338R.string.LastSelectedTab), true);
                    return r1;
                }
                List<TabData> a = TabsUtil.m2260a(false, true);
                textDetailSettingsCell.setTextAndValue(LocaleController.getString("DefaultTab", C0338R.string.DefaultTab), LocaleController.getString("DefaultTab", C0338R.string.DefaultTab), true);
                for (TabData tabData : a) {
                    if (tabData.m2225a() == MoboConstants.ak) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("DefaultTab", C0338R.string.DefaultTab), tabData.m2232d(), true);
                        return r1;
                    }
                }
                return r1;
            } else if (itemViewType == 7 && view == null) {
                return new ShadowSectionCell(this.f1099b);
            }
            return view;
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
            return i == this.f1098a.f1107e || i == this.f1098a.f1113k || i == this.f1098a.f1114l || i == this.f1098a.f1108f || i == this.f1098a.f1111i || i == this.f1098a.f1112j;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ForwardSettings", C0338R.string.ForwardSettings));
        this.actionBar.setActionBarMenuOnItemClick(new ForwardSettingsActivity(this));
        this.f1104b = new ForwardSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f1103a = new ListView(context);
        initThemeBackground(this.f1103a);
        this.f1103a.setDivider(null);
        this.f1103a.setDividerHeight(0);
        this.f1103a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f1103a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f1103a, LayoutHelper.createFrame(-1, -1, 51));
        this.f1103a.setAdapter(this.f1104b);
        this.f1103a.setOnItemClickListener(new ForwardSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.f1103a.invalidateViews();
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        this.f1115m = 0;
        int i = this.f1115m;
        this.f1115m = i + 1;
        this.f1105c = i;
        i = this.f1115m;
        this.f1115m = i + 1;
        this.f1106d = i;
        i = this.f1115m;
        this.f1115m = i + 1;
        this.f1107e = i;
        i = this.f1115m;
        this.f1115m = i + 1;
        this.f1108f = i;
        i = this.f1115m;
        this.f1115m = i + 1;
        this.f1109g = i;
        i = this.f1115m;
        this.f1115m = i + 1;
        this.f1110h = i;
        i = this.f1115m;
        this.f1115m = i + 1;
        this.f1111i = i;
        i = this.f1115m;
        this.f1115m = i + 1;
        this.f1112j = i;
        i = this.f1115m;
        this.f1115m = i + 1;
        this.f1113k = i;
        if (UserConfig.isRobot) {
            this.f1114l = -1;
        } else {
            i = this.f1115m;
            this.f1115m = i + 1;
            this.f1114l = i;
        }
        return true;
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}

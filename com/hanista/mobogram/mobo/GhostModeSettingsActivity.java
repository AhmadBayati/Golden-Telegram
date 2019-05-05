package com.hanista.mobogram.mobo;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
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
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.mobo.p004e.SettingManager;
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
import com.hanista.mobogram.ui.PhotoViewer;

/* renamed from: com.hanista.mobogram.mobo.i */
public class GhostModeSettingsActivity extends BaseFragment {
    private ListView f1138a;
    private GhostModeSettingsActivity f1139b;
    private int f1140c;
    private int f1141d;
    private int f1142e;
    private int f1143f;
    private int f1144g;

    /* renamed from: com.hanista.mobogram.mobo.i.1 */
    class GhostModeSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ GhostModeSettingsActivity f1116a;

        GhostModeSettingsActivity(GhostModeSettingsActivity ghostModeSettingsActivity) {
            this.f1116a = ghostModeSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f1116a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.i.2 */
    class GhostModeSettingsActivity implements OnItemClickListener {
        final /* synthetic */ GhostModeSettingsActivity f1117a;

        GhostModeSettingsActivity(GhostModeSettingsActivity ghostModeSettingsActivity) {
            this.f1117a = ghostModeSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            boolean z2 = false;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
            Editor edit = sharedPreferences.edit();
            boolean z3;
            TextCheckCell textCheckCell;
            if (i == this.f1117a.f1140c) {
                z3 = MoboConstants.ag;
                edit.putBoolean("hide_phone", !z3);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    textCheckCell = (TextCheckCell) view;
                    if (z3) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            } else if (i == this.f1117a.f1141d) {
                z3 = sharedPreferences.getBoolean("ghost_mode", false);
                edit.putBoolean("ghost_mode", !z3);
                edit.putBoolean("not_send_read_state", !z3);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (z3) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
                MoboConstants.m1379a();
                this.f1117a.actionBar.changeGhostModeVisibility();
                if (this.f1117a.f1138a != null) {
                    this.f1117a.f1138a.invalidateViews();
                }
                MessagesController.getInstance().reRunUpdateTimerProc();
                this.f1117a.parentLayout.rebuildAllFragmentViews(false);
                if (this.f1117a.getParentActivity() != null) {
                    PhotoViewer.getInstance().destroyPhotoViewer();
                    PhotoViewer.getInstance().setParentActivity(this.f1117a.getParentActivity());
                }
            } else if (i == this.f1117a.f1142e) {
                z3 = MoboConstants.f1339f;
                edit.putBoolean("hide_typing_state", !z3);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    textCheckCell = (TextCheckCell) view;
                    if (z3) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                }
            } else if (i == this.f1117a.f1143f) {
                Builder builder;
                if (MoboConstants.f1338e) {
                    builder = new Builder(this.f1117a.getParentActivity());
                    builder.setTitle(LocaleController.getString("GhostMode", C0338R.string.GhostMode)).setMessage(LocaleController.getString("GhostModeReadStateAlert", C0338R.string.GhostModeReadStateAlert));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                    this.f1117a.showDialog(builder.create());
                    return;
                }
                z3 = MoboConstants.f1340g;
                edit.putBoolean("not_send_read_state", !z3);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (!z3) {
                        z2 = true;
                    }
                    r10.setChecked(z2);
                }
                SettingManager settingManager = new SettingManager();
                if (!(z3 || settingManager.m944b("notSendReadAlertDisplayed"))) {
                    settingManager.m943a("notSendReadAlertDisplayed", true);
                    builder = new Builder(this.f1117a.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("NotSendReadStateAlert", C0338R.string.NotSendReadStateAlert));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), null);
                    this.f1117a.showDialog(builder.create());
                }
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.i.a */
    private class GhostModeSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ GhostModeSettingsActivity f1118a;
        private Context f1119b;

        public GhostModeSettingsActivity(GhostModeSettingsActivity ghostModeSettingsActivity, Context context) {
            this.f1118a = ghostModeSettingsActivity;
            this.f1119b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f1118a.f1144g;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == this.f1118a.f1141d || i == this.f1118a.f1143f) ? 8 : (i == this.f1118a.f1142e || i == this.f1118a.f1140c) ? 3 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f1119b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f1119b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f1119b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f1119b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        if (i == this.f1118a.f1140c) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("HidePhone", C0338R.string.HidePhone), MoboConstants.ag, true);
                        } else if (i == this.f1118a.f1142e) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("HideTypingState", C0338R.string.HideTypingState), MoboConstants.f1339f, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f1119b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        if (i == this.f1118a.f1141d) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("GhostMode", C0338R.string.GhostMode), LocaleController.getString("GhostModeHelp", C0338R.string.GhostModeHelp), MoboConstants.f1338e, true);
                        } else if (i == this.f1118a.f1143f) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("GhostModeHelp", C0338R.string.NotSendReadState), LocaleController.getString("NotSendReadStateGlobalDetail", C0338R.string.NotSendReadStateGlobalDetail), MoboConstants.f1340g, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f1119b) : view;
                    } else {
                        if (itemViewType != 6) {
                            return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f1119b) : view;
                        } else {
                            textSettingsCell = view == null ? new TextDetailSettingsCell(this.f1119b) : view;
                            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                            return textSettingsCell;
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
            return i == this.f1118a.f1141d || i == this.f1118a.f1142e || i == this.f1118a.f1143f || i == this.f1118a.f1140c;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GhostModeSettings", C0338R.string.GhostModeSettings));
        this.actionBar.setActionBarMenuOnItemClick(new GhostModeSettingsActivity(this));
        this.f1139b = new GhostModeSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f1138a = new ListView(context);
        initThemeBackground(this.f1138a);
        this.f1138a.setDivider(null);
        this.f1138a.setDividerHeight(0);
        this.f1138a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f1138a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f1138a, LayoutHelper.createFrame(-1, -1, 51));
        this.f1138a.setAdapter(this.f1139b);
        this.f1138a.setOnItemClickListener(new GhostModeSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.f1144g = 0;
        int i = this.f1144g;
        this.f1144g = i + 1;
        this.f1141d = i;
        i = this.f1144g;
        this.f1144g = i + 1;
        this.f1143f = i;
        i = this.f1144g;
        this.f1144g = i + 1;
        this.f1142e = i;
        i = this.f1144g;
        this.f1144g = i + 1;
        this.f1140c = i;
        return true;
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}

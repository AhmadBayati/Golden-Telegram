package com.hanista.mobogram.mobo.dialogdm;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
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
import java.util.Locale;

/* renamed from: com.hanista.mobogram.mobo.dialogdm.e */
public class DialogDmSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListView f630a;
    private DialogDmSettingsActivity f631b;
    private int f632c;
    private int f633d;
    private int f634e;
    private int f635f;
    private int f636g;
    private int f637h;
    private int f638i;

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.e.1 */
    class DialogDmSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ DialogDmSettingsActivity f624a;

        DialogDmSettingsActivity(DialogDmSettingsActivity dialogDmSettingsActivity) {
            this.f624a = dialogDmSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f624a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.e.2 */
    class DialogDmSettingsActivity implements OnItemClickListener {
        final /* synthetic */ DialogDmSettingsActivity f627a;

        /* renamed from: com.hanista.mobogram.mobo.dialogdm.e.2.1 */
        class DialogDmSettingsActivity implements OnTimeSetListener {
            final /* synthetic */ DialogDmSettingsActivity f625a;

            DialogDmSettingsActivity(DialogDmSettingsActivity dialogDmSettingsActivity) {
                this.f625a = dialogDmSettingsActivity;
            }

            public void onTimeSet(TimePicker timePicker, int i, int i2) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("schedule_dialog_dm_alarm_start_hour", i);
                edit.putInt("schedule_dialog_dm_alarm_start_minute", i2);
                edit.commit();
                MoboConstants.m1379a();
                DialogDmAlarmReceiver.m537a(this.f625a.f627a.getParentActivity());
                if (this.f625a.f627a.f630a != null) {
                    this.f625a.f627a.f630a.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.dialogdm.e.2.2 */
        class DialogDmSettingsActivity implements OnTimeSetListener {
            final /* synthetic */ DialogDmSettingsActivity f626a;

            DialogDmSettingsActivity(DialogDmSettingsActivity dialogDmSettingsActivity) {
                this.f626a = dialogDmSettingsActivity;
            }

            public void onTimeSet(TimePicker timePicker, int i, int i2) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("schedule_dialog_dm_alarm_end_hour", i);
                edit.putInt("schedule_dialog_dm_alarm_end_minute", i2);
                edit.commit();
                MoboConstants.m1379a();
                DialogDmAlarmReceiver.m537a(this.f626a.f627a.getParentActivity());
                if (this.f626a.f627a.f630a != null) {
                    this.f626a.f627a.f630a.invalidateViews();
                }
            }
        }

        DialogDmSettingsActivity(DialogDmSettingsActivity dialogDmSettingsActivity) {
            this.f627a = dialogDmSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            boolean z2;
            TextCheckCell textCheckCell;
            if (i == this.f627a.f632c) {
                z2 = MoboConstants.ao;
                edit.putBoolean("schedule_dialog_dm_alarm", !z2);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    textCheckCell = (TextCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                }
                if (this.f627a.f630a != null) {
                    this.f627a.f630a.invalidateViews();
                }
                MoboConstants.m1379a();
                DialogDmAlarmReceiver.m537a(this.f627a.getParentActivity());
            } else if (i == this.f627a.f634e) {
                if (this.f627a.getParentActivity() != null) {
                    new TimePickerDialog(this.f627a.getParentActivity(), new DialogDmSettingsActivity(this), MoboConstants.ap, MoboConstants.aq, false).show();
                } else {
                    return;
                }
            } else if (i == this.f627a.f635f) {
                z2 = MoboConstants.at;
                edit.putBoolean("dialog_dm_turn_on_wifi_on_start", !z2);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    textCheckCell = (TextCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                }
            } else if (i == this.f627a.f636g) {
                z2 = MoboConstants.au;
                edit.putBoolean("dialog_dm_turn_off_wifi_on_end", !z2);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    textCheckCell = (TextCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                }
            } else if (i == this.f627a.f637h) {
                if (this.f627a.getParentActivity() != null) {
                    new TimePickerDialog(this.f627a.getParentActivity(), new DialogDmSettingsActivity(this), MoboConstants.ar, MoboConstants.as, false).show();
                } else {
                    return;
                }
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.e.a */
    private class DialogDmSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ DialogDmSettingsActivity f628a;
        private Context f629b;

        public DialogDmSettingsActivity(DialogDmSettingsActivity dialogDmSettingsActivity, Context context) {
            this.f628a = dialogDmSettingsActivity;
            this.f629b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f628a.f638i;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == this.f628a.f632c || i == this.f628a.f635f || i == this.f628a.f636g) ? 3 : (i == this.f628a.f634e || i == this.f628a.f637h) ? 6 : i == this.f628a.f633d ? 7 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f629b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f629b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f629b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f629b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                        if (i == this.f628a.f632c) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("DownloadManagerScheduler", C0338R.string.DownloadManagerScheduler), MoboConstants.ao, true);
                        } else if (i == this.f628a.f635f) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("TurnOnWifiOnStart", C0338R.string.TurnOnWifiOnStart), MoboConstants.at, true);
                        } else if (i == this.f628a.f636g) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("TurnOffWifiOnEnd", C0338R.string.TurnOffWifiOnEnd), MoboConstants.au, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f629b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f629b) : view;
                    } else {
                        if (itemViewType == 5) {
                            if (view != null) {
                                return view;
                            }
                            textSettingsCell = new TextInfoCell(this.f629b);
                            try {
                                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                ((TextInfoCell) textSettingsCell).setText(String.format(Locale.US, "Mobogram for Android v%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)}));
                                return textSettingsCell;
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                                return textSettingsCell;
                            }
                        } else if (itemViewType != 6) {
                            return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f629b) : view;
                        } else {
                            textSettingsCell = view == null ? new TextDetailSettingsCell(this.f629b) : view;
                            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                            if (i == this.f628a.f634e) {
                                textDetailSettingsCell.setMultilineDetail(false);
                                textDetailSettingsCell.setTextAndValue(LocaleController.getString("StartTime", C0338R.string.StartTime), MoboConstants.ap + ":" + MoboConstants.aq, true);
                            } else if (i == this.f628a.f637h) {
                                textDetailSettingsCell.setMultilineDetail(false);
                                textDetailSettingsCell.setTextAndValue(LocaleController.getString("EndTime", C0338R.string.EndTime), MoboConstants.ar + ":" + MoboConstants.as, true);
                            }
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
            return i == this.f628a.f632c || i == this.f628a.f634e || i == this.f628a.f637h || i == this.f628a.f635f || i == this.f628a.f636g;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SETTINGS", C0338R.string.SETTINGS));
        this.actionBar.setActionBarMenuOnItemClick(new DialogDmSettingsActivity(this));
        this.f631b = new DialogDmSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f630a = new ListView(context);
        initThemeBackground(this.f630a);
        this.f630a.setDivider(null);
        this.f630a.setDividerHeight(0);
        this.f630a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f630a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f630a, LayoutHelper.createFrame(-1, -1, 51));
        this.f630a.setAdapter(this.f631b);
        this.f630a.setOnItemClickListener(new DialogDmSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.f630a.invalidateViews();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        this.f638i = 0;
        int i = this.f638i;
        this.f638i = i + 1;
        this.f632c = i;
        i = this.f638i;
        this.f638i = i + 1;
        this.f633d = i;
        i = this.f638i;
        this.f638i = i + 1;
        this.f634e = i;
        i = this.f638i;
        this.f638i = i + 1;
        this.f637h = i;
        i = this.f638i;
        this.f638i = i + 1;
        this.f635f = i;
        i = this.f638i;
        this.f638i = i + 1;
        this.f636g = i;
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}

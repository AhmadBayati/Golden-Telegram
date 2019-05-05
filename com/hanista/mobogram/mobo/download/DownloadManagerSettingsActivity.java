package com.hanista.mobogram.mobo.download;

import android.app.Dialog;
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
import com.hanista.mobogram.messenger.MediaController;
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

/* renamed from: com.hanista.mobogram.mobo.download.b */
public class DownloadManagerSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListView f779a;
    private DownloadManagerSettingsActivity f780b;
    private int f781c;
    private int f782d;
    private int f783e;
    private int f784f;
    private int f785g;
    private int f786h;
    private int f787i;

    /* renamed from: com.hanista.mobogram.mobo.download.b.1 */
    class DownloadManagerSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ DownloadManagerSettingsActivity f773a;

        DownloadManagerSettingsActivity(DownloadManagerSettingsActivity downloadManagerSettingsActivity) {
            this.f773a = downloadManagerSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f773a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.b.2 */
    class DownloadManagerSettingsActivity implements OnItemClickListener {
        final /* synthetic */ DownloadManagerSettingsActivity f776a;

        /* renamed from: com.hanista.mobogram.mobo.download.b.2.1 */
        class DownloadManagerSettingsActivity implements OnTimeSetListener {
            final /* synthetic */ DownloadManagerSettingsActivity f774a;

            DownloadManagerSettingsActivity(DownloadManagerSettingsActivity downloadManagerSettingsActivity) {
                this.f774a = downloadManagerSettingsActivity;
            }

            public void onTimeSet(TimePicker timePicker, int i, int i2) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("schedule_download_alarm_start_hour", i);
                edit.putInt("schedule_download_alarm_start_minute", i2);
                edit.commit();
                MoboConstants.m1379a();
                DownloadAlarmReceiver.m618a(this.f774a.f776a.getParentActivity());
                if (this.f774a.f776a.f779a != null) {
                    this.f774a.f776a.f779a.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.download.b.2.2 */
        class DownloadManagerSettingsActivity implements OnTimeSetListener {
            final /* synthetic */ DownloadManagerSettingsActivity f775a;

            DownloadManagerSettingsActivity(DownloadManagerSettingsActivity downloadManagerSettingsActivity) {
                this.f775a = downloadManagerSettingsActivity;
            }

            public void onTimeSet(TimePicker timePicker, int i, int i2) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("schedule_download_alarm_end_hour", i);
                edit.putInt("schedule_download_alarm_end_minute", i2);
                edit.commit();
                MoboConstants.m1379a();
                DownloadAlarmReceiver.m618a(this.f775a.f776a.getParentActivity());
                if (this.f775a.f776a.f779a != null) {
                    this.f775a.f776a.f779a.invalidateViews();
                }
            }
        }

        DownloadManagerSettingsActivity(DownloadManagerSettingsActivity downloadManagerSettingsActivity) {
            this.f776a = downloadManagerSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            boolean z2;
            TextCheckCell textCheckCell;
            if (i == this.f776a.f781c) {
                z2 = MoboConstants.f1313F;
                edit.putBoolean("schedule_download_alarm", !z2);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    textCheckCell = (TextCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                }
                if (this.f776a.f779a != null) {
                    this.f776a.f779a.invalidateViews();
                }
                MoboConstants.m1379a();
                DownloadAlarmReceiver.m618a(this.f776a.getParentActivity());
            } else if (i == this.f776a.f783e) {
                if (this.f776a.getParentActivity() != null) {
                    new TimePickerDialog(this.f776a.getParentActivity(), new DownloadManagerSettingsActivity(this), MoboConstants.f1314G, MoboConstants.f1315H, false).show();
                } else {
                    return;
                }
            } else if (i == this.f776a.f784f) {
                z2 = MoboConstants.f1318K;
                edit.putBoolean("turn_on_wifi_on_start", !z2);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    textCheckCell = (TextCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                }
            } else if (i == this.f776a.f785g) {
                z2 = MoboConstants.f1319L;
                edit.putBoolean("turn_off_wifi_on_end", !z2);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    textCheckCell = (TextCheckCell) view;
                    if (z2) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                }
            } else if (i == this.f776a.f786h) {
                if (this.f776a.getParentActivity() != null) {
                    new TimePickerDialog(this.f776a.getParentActivity(), new DownloadManagerSettingsActivity(this), MoboConstants.f1316I, MoboConstants.f1317J, false).show();
                } else {
                    return;
                }
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.download.b.a */
    private class DownloadManagerSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ DownloadManagerSettingsActivity f777a;
        private Context f778b;

        public DownloadManagerSettingsActivity(DownloadManagerSettingsActivity downloadManagerSettingsActivity, Context context) {
            this.f777a = downloadManagerSettingsActivity;
            this.f778b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f777a.f787i;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == this.f777a.f781c || i == this.f777a.f784f || i == this.f777a.f785g) ? 3 : (i == this.f777a.f783e || i == this.f777a.f786h) ? 6 : i == this.f777a.f782d ? 7 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View textSettingsCell;
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f778b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f778b) : view;
                } else {
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f778b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f778b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                        if (i == this.f777a.f781c) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("DownloadManagerScheduler", C0338R.string.DownloadManagerScheduler), MoboConstants.f1313F, true);
                        } else if (i == this.f777a.f784f) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("TurnOnWifiOnStart", C0338R.string.TurnOnWifiOnStart), MoboConstants.f1318K, true);
                        } else if (i == this.f777a.f785g) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("TurnOffWifiOnEnd", C0338R.string.TurnOffWifiOnEnd), MoboConstants.f1319L, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f778b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f778b) : view;
                    } else {
                        if (itemViewType == 5) {
                            if (view != null) {
                                return view;
                            }
                            textSettingsCell = new TextInfoCell(this.f778b);
                            try {
                                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                ((TextInfoCell) textSettingsCell).setText(String.format(Locale.US, "Mobogram for Android v%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)}));
                                return textSettingsCell;
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                                return textSettingsCell;
                            }
                        } else if (itemViewType != 6) {
                            return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f778b) : view;
                        } else {
                            textSettingsCell = view == null ? new TextDetailSettingsCell(this.f778b) : view;
                            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                            if (i == this.f777a.f783e) {
                                textDetailSettingsCell.setMultilineDetail(false);
                                textDetailSettingsCell.setTextAndValue(LocaleController.getString("StartTime", C0338R.string.StartTime), MoboConstants.f1314G + ":" + MoboConstants.f1315H, true);
                            } else if (i == this.f777a.f786h) {
                                textDetailSettingsCell.setMultilineDetail(false);
                                textDetailSettingsCell.setTextAndValue(LocaleController.getString("EndTime", C0338R.string.EndTime), MoboConstants.f1316I + ":" + MoboConstants.f1317J, true);
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
            return i == this.f777a.f781c || i == this.f777a.f783e || i == this.f777a.f786h || i == this.f777a.f784f || i == this.f777a.f785g;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("DownloadManagerSettings", C0338R.string.DownloadManagerSettings));
        this.actionBar.setActionBarMenuOnItemClick(new DownloadManagerSettingsActivity(this));
        this.f780b = new DownloadManagerSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f779a = new ListView(context);
        initThemeBackground(this.f779a);
        this.f779a.setDivider(null);
        this.f779a.setDividerHeight(0);
        this.f779a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f779a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f779a, LayoutHelper.createFrame(-1, -1, 51));
        this.f779a.setAdapter(this.f780b);
        this.f779a.setOnItemClickListener(new DownloadManagerSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.f779a.invalidateViews();
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        this.f787i = 0;
        int i = this.f787i;
        this.f787i = i + 1;
        this.f781c = i;
        i = this.f787i;
        this.f787i = i + 1;
        this.f782d = i;
        i = this.f787i;
        this.f787i = i + 1;
        this.f783e = i;
        i = this.f787i;
        this.f787i = i + 1;
        this.f786h = i;
        i = this.f787i;
        this.f787i = i + 1;
        this.f784f = i;
        i = this.f787i;
        this.f787i = i + 1;
        this.f785g = i;
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

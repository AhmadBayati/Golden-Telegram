package com.hanista.mobogram.mobo;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.Builder;
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
import com.hanista.mobogram.ui.LaunchActivity;
import java.util.Locale;

/* renamed from: com.hanista.mobogram.mobo.t */
public class ViewSettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private ListView f2537a;
    private ViewSettingsActivity f2538b;
    private int f2539c;
    private int f2540d;
    private int f2541e;
    private int f2542f;
    private int f2543g;
    private int f2544h;
    private int f2545i;
    private int f2546j;
    private int f2547k;
    private int f2548l;
    private int f2549m;
    private int f2550n;

    /* renamed from: com.hanista.mobogram.mobo.t.1 */
    class ViewSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ViewSettingsActivity f2529a;

        ViewSettingsActivity(ViewSettingsActivity viewSettingsActivity) {
            this.f2529a = viewSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f2529a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.t.2 */
    class ViewSettingsActivity implements OnItemClickListener {
        final /* synthetic */ ViewSettingsActivity f2532a;

        /* renamed from: com.hanista.mobogram.mobo.t.2.1 */
        class ViewSettingsActivity implements OnClickListener {
            final /* synthetic */ ViewSettingsActivity f2530a;

            ViewSettingsActivity(ViewSettingsActivity viewSettingsActivity) {
                this.f2530a = viewSettingsActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                String str = "touch_contact_avatar";
                if (i == 0) {
                    edit.putInt(str, 0);
                } else if (i == 1) {
                    edit.putInt(str, 1);
                } else if (i == 2) {
                    edit.putInt(str, 2);
                }
                edit.commit();
                if (this.f2530a.f2532a.f2537a != null) {
                    this.f2530a.f2532a.f2537a.invalidateViews();
                }
                MoboConstants.m1379a();
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.t.2.2 */
        class ViewSettingsActivity implements OnClickListener {
            final /* synthetic */ ViewSettingsActivity f2531a;

            ViewSettingsActivity(ViewSettingsActivity viewSettingsActivity) {
                this.f2531a = viewSettingsActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                String str = "touch_group_avatar";
                if (i == 0) {
                    edit.putInt(str, 0);
                } else if (i == 1) {
                    edit.putInt(str, 1);
                } else if (i == 2) {
                    edit.putInt(str, 2);
                }
                edit.commit();
                if (this.f2531a.f2532a.f2537a != null) {
                    this.f2531a.f2532a.f2537a.invalidateViews();
                }
                MoboConstants.m1379a();
            }
        }

        ViewSettingsActivity(ViewSettingsActivity viewSettingsActivity) {
            this.f2532a = viewSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = true;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
            Editor edit = sharedPreferences.edit();
            if (i == this.f2532a.f2539c) {
                this.f2532a.presentFragment(new FontSelectActivity());
            } else if (i == this.f2532a.f2540d) {
                r4 = MoboConstants.f1324Q;
                edit.putBoolean("tablet_mode", !r4);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (r4) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
                MoboConstants.m1379a();
                this.f2532a.m2507a();
            } else if (i == this.f2532a.f2541e) {
                r4 = sharedPreferences.getBoolean("shamsi_for_all_locales", true);
                edit.putBoolean("shamsi_for_all_locales", !r4);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    TextCheckCell textCheckCell = (TextCheckCell) view;
                    if (r4) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                }
            } else if (i == this.f2532a.f2542f) {
                r4 = sharedPreferences.getBoolean("show_contact_status", true);
                edit.putBoolean("show_contact_status", !r4);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (r4) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f2532a.f2543g) {
                r0 = new Builder(this.f2532a.getParentActivity());
                r0.setTitle(LocaleController.getString("TouchOnContactAvatar", C0338R.string.TouchOnContactAvatar));
                r0.setItems(new CharSequence[]{LocaleController.getString("Disabled", C0338R.string.Disabled), LocaleController.getString("OpenProfile", C0338R.string.OpenProfile), LocaleController.getString("OpenProfilePhotos", C0338R.string.OpenProfilePhotos)}, new ViewSettingsActivity(this));
                this.f2532a.showDialog(r0.create());
            } else if (i == this.f2532a.f2544h) {
                r0 = new Builder(this.f2532a.getParentActivity());
                r0.setTitle(LocaleController.getString("TouchOnGroupAvatar", C0338R.string.TouchOnGroupAvatar));
                r0.setItems(new CharSequence[]{LocaleController.getString("Disabled", C0338R.string.Disabled), LocaleController.getString("OpenProfile", C0338R.string.OpenProfile), LocaleController.getString("OpenProfilePhotos", C0338R.string.OpenProfilePhotos)}, new ViewSettingsActivity(this));
                this.f2532a.showDialog(r0.create());
            } else if (i == this.f2532a.f2545i) {
                r4 = sharedPreferences.getBoolean("separate_mutual_contacts", true);
                edit.putBoolean("separate_mutual_contacts", !r4);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (r4) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
            } else if (i == this.f2532a.f2546j) {
                r4 = MoboConstants.f1347n;
                edit.putBoolean("show_last_seen_icon", !r4);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (r4) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
                MoboConstants.m1379a();
                this.f2532a.parentLayout.rebuildAllFragmentViews(false);
            } else if (i == this.f2532a.f2547k) {
                r4 = sharedPreferences.getBoolean("show_ghost_icon", true);
                edit.putBoolean("show_ghost_icon", !r4);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (r4) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
                MoboConstants.m1379a();
                this.f2532a.parentLayout.rebuildAllFragmentViews(false);
            } else if (i == this.f2532a.f2548l) {
                r4 = sharedPreferences.getBoolean("show_ghost_state_icon", true);
                edit.putBoolean("show_ghost_state_icon", !r4);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (r4) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
                MoboConstants.m1379a();
                this.f2532a.actionBar.changeGhostModeVisibility();
            } else if (i == this.f2532a.f2549m) {
                r4 = sharedPreferences.getBoolean("show_multi_user_options_in_menu", true);
                edit.putBoolean("show_multi_user_options_in_menu", !r4);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    r10 = (TextDetailCheckCell) view;
                    if (r4) {
                        z = false;
                    }
                    r10.setChecked(z);
                }
                MoboConstants.m1379a();
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.t.3 */
    class ViewSettingsActivity implements OnClickListener {
        final /* synthetic */ ViewSettingsActivity f2533a;

        ViewSettingsActivity(ViewSettingsActivity viewSettingsActivity) {
            this.f2533a = viewSettingsActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            this.f2533a.m2509b();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.t.4 */
    class ViewSettingsActivity implements OnCancelListener {
        final /* synthetic */ ViewSettingsActivity f2534a;

        ViewSettingsActivity(ViewSettingsActivity viewSettingsActivity) {
            this.f2534a = viewSettingsActivity;
        }

        public void onCancel(DialogInterface dialogInterface) {
            this.f2534a.m2509b();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.t.a */
    private class ViewSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ ViewSettingsActivity f2535a;
        private Context f2536b;

        public ViewSettingsActivity(ViewSettingsActivity viewSettingsActivity, Context context) {
            this.f2535a = viewSettingsActivity;
            this.f2536b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f2535a.f2550n;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == this.f2535a.f2539c ? 2 : i == this.f2535a.f2541e ? 3 : (i == this.f2535a.f2543g || i == this.f2535a.f2544h) ? 6 : (i == this.f2535a.f2545i || i == this.f2535a.f2546j || i == this.f2535a.f2549m || i == this.f2535a.f2547k || i == this.f2535a.f2548l || i == this.f2535a.f2542f || i == this.f2535a.f2540d) ? 8 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f2536b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f2536b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f2536b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        if (i == this.f2535a.f2539c) {
                            textSettingsCell2.setTextAndValue(LocaleController.getString("Font", C0338R.string.Font), FontUtil.m1176a().m1159b(), true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f2536b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        r3 = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                        if (i == this.f2535a.f2541e) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("ShamsiForAllLocales", C0338R.string.ShamsiForAllLocales), r3.getBoolean("shamsi_for_all_locales", true), true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f2536b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        r3 = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                        if (i == this.f2535a.f2540d) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("TabletMode", C0338R.string.TabletMode), LocaleController.getString("TabletModeDetail", C0338R.string.TabletModeDetail), MoboConstants.f1324Q, true);
                        } else if (i == this.f2535a.f2542f) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowContactStatus", C0338R.string.ShowContactStatus), LocaleController.getString("ShowContactStatusDetail", C0338R.string.ShowContactStatusDetail), r3.getBoolean("show_contact_status", true), true);
                        } else if (i == this.f2535a.f2545i) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("SeparateMutualContacts", C0338R.string.SeparateMutualContacts), LocaleController.getString("SeparateMutualContactsDetail", C0338R.string.SeparateMutualContactsDetail), r3.getBoolean("separate_mutual_contacts", true), true);
                        } else if (i == this.f2535a.f2546j) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowLastSeenIcon", C0338R.string.ShowLastSeenIcon), LocaleController.getString("ShowLastSeenIconDetail", C0338R.string.ShowLastSeenIconDetail), MoboConstants.f1347n, true);
                        } else if (i == this.f2535a.f2547k) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowGhostIcon", C0338R.string.ShowGhostIcon), LocaleController.getString("ShowGhostIconDetail", C0338R.string.ShowGhostIconDetail), r3.getBoolean("show_ghost_icon", true), true);
                        } else if (i == this.f2535a.f2548l) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowGhostStateIcon", C0338R.string.ShowGhostStateIcon), LocaleController.getString("ShowGhostStateIconDetail", C0338R.string.ShowGhostStateIconDetail), r3.getBoolean("show_ghost_state_icon", true), true);
                        } else if (i == this.f2535a.f2549m) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowMultiUserOptsInMenu", C0338R.string.ShowMultiUserOptsInMenu), LocaleController.getString("ShowMultiUserOptsInMenuDetail", C0338R.string.ShowMultiUserOptsInMenuDetail), r3.getBoolean("show_multi_user_options_in_menu", true), true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f2536b) : view;
                    } else {
                        if (itemViewType == 5) {
                            if (view != null) {
                                return view;
                            }
                            textSettingsCell = new TextInfoCell(this.f2536b);
                            try {
                                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                ((TextInfoCell) textSettingsCell).setText(String.format(Locale.US, "Mobogram for Android v%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)}));
                                return textSettingsCell;
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                                return textSettingsCell;
                            }
                        } else if (itemViewType != 6) {
                            return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f2536b) : view;
                        } else {
                            textSettingsCell = view == null ? new TextDetailSettingsCell(this.f2536b) : view;
                            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                            if (i == this.f2535a.f2543g || i == this.f2535a.f2544h) {
                                textDetailSettingsCell.setMultilineDetail(false);
                                int i2 = i == this.f2535a.f2543g ? MoboConstants.aB : MoboConstants.aC;
                                String string = i == this.f2535a.f2543g ? LocaleController.getString("TouchOnContactAvatar", C0338R.string.TouchOnContactAvatar) : LocaleController.getString("TouchOnGroupAvatar", C0338R.string.TouchOnGroupAvatar);
                                if (i2 == 0) {
                                    textDetailSettingsCell.setTextAndValue(string, LocaleController.getString("Disabled", C0338R.string.Disabled), true);
                                } else if (i2 == 1) {
                                    textDetailSettingsCell.setTextAndValue(string, LocaleController.getString("OpenProfile", C0338R.string.OpenProfile), true);
                                } else if (i2 == 2) {
                                    textDetailSettingsCell.setTextAndValue(string, LocaleController.getString("OpenProfilePhotos", C0338R.string.OpenProfilePhotos), true);
                                }
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
            return i == this.f2535a.f2539c || i == this.f2535a.f2541e || i == this.f2535a.f2545i || i == this.f2535a.f2546j || i == this.f2535a.f2547k || i == this.f2535a.f2548l || i == this.f2535a.f2542f || i == this.f2535a.f2540d || i == this.f2535a.f2549m || i == this.f2535a.f2543g || i == this.f2535a.f2544h;
        }
    }

    private void m2507a() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("RestartForApplyChanges", C0338R.string.RestartForApplyChanges));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new ViewSettingsActivity(this));
        builder.setOnCancelListener(new ViewSettingsActivity(this));
        builder.create().show();
    }

    private void m2509b() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 1234567, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ViewSetting", C0338R.string.ViewSetting));
        this.actionBar.setActionBarMenuOnItemClick(new ViewSettingsActivity(this));
        this.f2538b = new ViewSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f2537a = new ListView(context);
        initThemeBackground(this.f2537a);
        this.f2537a.setDivider(null);
        this.f2537a.setDividerHeight(0);
        this.f2537a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f2537a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f2537a, LayoutHelper.createFrame(-1, -1, 51));
        this.f2537a.setAdapter(this.f2538b);
        this.f2537a.setOnItemClickListener(new ViewSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.f2537a.invalidateViews();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.f2550n = 0;
        int i = this.f2550n;
        this.f2550n = i + 1;
        this.f2539c = i;
        if (ApplicationLoader.applicationContext.getResources().getBoolean(C0338R.bool.isTablet)) {
            i = this.f2550n;
            this.f2550n = i + 1;
            this.f2540d = i;
        }
        i = this.f2550n;
        this.f2550n = i + 1;
        this.f2541e = i;
        i = this.f2550n;
        this.f2550n = i + 1;
        this.f2542f = i;
        i = this.f2550n;
        this.f2550n = i + 1;
        this.f2543g = i;
        i = this.f2550n;
        this.f2550n = i + 1;
        this.f2544h = i;
        i = this.f2550n;
        this.f2550n = i + 1;
        this.f2545i = i;
        i = this.f2550n;
        this.f2550n = i + 1;
        this.f2546j = i;
        i = this.f2550n;
        this.f2550n = i + 1;
        this.f2547k = i;
        i = this.f2550n;
        this.f2550n = i + 1;
        this.f2548l = i;
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        if (this.f2538b != null) {
            this.f2538b.notifyDataSetChanged();
        }
        initThemeActionBar();
    }
}

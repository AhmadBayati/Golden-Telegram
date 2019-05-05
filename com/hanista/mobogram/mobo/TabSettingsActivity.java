package com.hanista.mobogram.mobo;

import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.mobo.p019r.TabData;
import com.hanista.mobogram.mobo.p019r.TabsActivity;
import com.hanista.mobogram.mobo.p019r.TabsUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* renamed from: com.hanista.mobogram.mobo.q */
public class TabSettingsActivity extends BaseFragment {
    private ListView f2388a;
    private TabSettingsActivity f2389b;
    private int f2390c;
    private int f2391d;
    private int f2392e;
    private int f2393f;
    private int f2394g;
    private int f2395h;
    private int f2396i;
    private int f2397j;
    private int f2398k;
    private int f2399l;
    private int f2400m;
    private int f2401n;
    private int f2402o;

    /* renamed from: com.hanista.mobogram.mobo.q.1 */
    class TabSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ TabSettingsActivity f2372a;

        TabSettingsActivity(TabSettingsActivity tabSettingsActivity) {
            this.f2372a = tabSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f2372a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.q.2 */
    class TabSettingsActivity implements OnItemClickListener {
        final /* synthetic */ TabSettingsActivity f2379a;

        /* renamed from: com.hanista.mobogram.mobo.q.2.1 */
        class TabSettingsActivity implements OnClickListener {
            final /* synthetic */ List f2373a;
            final /* synthetic */ TabSettingsActivity f2374b;

            TabSettingsActivity(TabSettingsActivity tabSettingsActivity, List list) {
                this.f2374b = tabSettingsActivity;
                this.f2373a = list;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putBoolean("last_selected_tab", false);
                if (i == this.f2373a.size()) {
                    edit.putBoolean("last_selected_tab", true);
                } else {
                    edit.putInt("default_tab", ((TabData) this.f2373a.get(i)).m2225a());
                }
                edit.commit();
                if (this.f2374b.f2379a.f2388a != null) {
                    this.f2374b.f2379a.f2388a.invalidateViews();
                }
                MoboConstants.m1379a();
                this.f2374b.f2379a.parentLayout.rebuildAllFragmentViews(false);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.q.2.2 */
        class TabSettingsActivity implements View.OnClickListener {
            final /* synthetic */ boolean[] f2375a;
            final /* synthetic */ TabSettingsActivity f2376b;

            TabSettingsActivity(TabSettingsActivity tabSettingsActivity, boolean[] zArr) {
                this.f2376b = tabSettingsActivity;
                this.f2375a = zArr;
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                this.f2375a[intValue] = !this.f2375a[intValue];
                checkBoxCell.setChecked(this.f2375a[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.q.2.3 */
        class TabSettingsActivity implements View.OnClickListener {
            final /* synthetic */ boolean[] f2377a;
            final /* synthetic */ TabSettingsActivity f2378b;

            TabSettingsActivity(TabSettingsActivity tabSettingsActivity, boolean[] zArr) {
                this.f2378b = tabSettingsActivity;
                this.f2377a = zArr;
            }

            public void onClick(View view) {
                try {
                    if (this.f2378b.f2379a.visibleDialog != null) {
                        this.f2378b.f2379a.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                int i = 0;
                for (int i2 = 0; i2 < 6; i2++) {
                    if (this.f2377a[i2]) {
                        if (i2 == 0) {
                            i |= 1;
                        } else if (i2 == 1) {
                            i |= 2;
                        } else if (i2 == 2) {
                            i |= 4;
                        } else if (i2 == 3) {
                            i |= 8;
                        } else if (i2 == 4) {
                            i |= 16;
                        } else if (i2 == 5) {
                            i |= 32;
                        }
                    }
                }
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("auto_dl_favorites_mask", i);
                edit.commit();
                if (this.f2378b.f2379a.f2388a != null) {
                    this.f2378b.f2379a.f2388a.invalidateViews();
                }
                MoboConstants.m1379a();
            }
        }

        TabSettingsActivity(TabSettingsActivity tabSettingsActivity) {
            this.f2379a = tabSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
            Editor edit = sharedPreferences.edit();
            boolean z;
            if (i == this.f2379a.f2390c) {
                z = sharedPreferences.getBoolean("show_tab", true);
                edit.putBoolean("show_tab", !z);
                if (MoboConstants.f1344k == 0) {
                    edit.putInt("visible_tabs", NalUnitUtil.EXTENDED_SAR);
                }
                edit.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!z);
                }
                if (this.f2379a.f2388a != null) {
                    this.f2379a.f2388a.invalidateViews();
                }
                MoboConstants.m1379a();
                this.f2379a.parentLayout.rebuildAllFragmentViews(false);
            } else if (i == this.f2379a.f2392e) {
                if (this.f2379a.getParentActivity() != null) {
                    this.f2379a.presentFragment(new TabsActivity());
                } else {
                    return;
                }
            } else if (i == this.f2379a.f2393f) {
                Builder builder = new Builder(this.f2379a.getParentActivity());
                builder.setTitle(LocaleController.getString("DefaultTab", C0338R.string.DefaultTab));
                List<TabData> a = TabsUtil.m2260a(true, false);
                List arrayList = new ArrayList();
                for (TabData d : a) {
                    arrayList.add(d.m2232d());
                }
                arrayList.add(LocaleController.getString("LastSelectedTab", C0338R.string.LastSelectedTab));
                builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new TabSettingsActivity(this, a));
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                this.f2379a.showDialog(builder.create());
            } else if (i == this.f2379a.f2394g) {
                z = MoboConstants.f1328U;
                edit.putBoolean("show_admin_chats_in_creator", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
                this.f2379a.m2196a();
            } else if (i == this.f2379a.f2395h) {
                z = MoboConstants.f1335b;
                edit.putBoolean("swipe_on_tabs", !z);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
                this.f2379a.parentLayout.rebuildAllFragmentViews(false);
            } else if (i == this.f2379a.f2396i) {
                z = MoboConstants.f1331X;
                edit.putBoolean("show_tabs_in_bottom", !z);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
                this.f2379a.parentLayout.rebuildAllFragmentViews(false);
            } else if (i == this.f2379a.f2399l) {
                z = MoboConstants.f1332Y;
                edit.putBoolean("show_tabs_unread_count", !z);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
                this.f2379a.parentLayout.rebuildAllFragmentViews(false);
            } else if (i == this.f2379a.f2400m) {
                z = MoboConstants.f1333Z;
                edit.putBoolean("count_muted_messages", !z);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
                this.f2379a.parentLayout.rebuildAllFragmentViews(false);
            } else if (i == this.f2379a.f2401n) {
                z = MoboConstants.aa;
                edit.putBoolean("count_chats_instead_of_messages", !z);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
                this.f2379a.parentLayout.rebuildAllFragmentViews(false);
            } else if (i == this.f2379a.f2397j) {
                boolean[] zArr = new boolean[6];
                BottomSheet.Builder builder2 = new BottomSheet.Builder(this.f2379a.getParentActivity());
                int i2 = MoboConstants.f1346m;
                builder2.setApplyTopPadding(false);
                builder2.setApplyBottomPadding(false);
                View linearLayout = new LinearLayout(this.f2379a.getParentActivity());
                linearLayout.setOrientation(1);
                for (int i3 = 0; i3 < 6; i3++) {
                    String str = null;
                    if (i3 == 0) {
                        zArr[i3] = (i2 & 1) != 0;
                        str = LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto);
                    } else if (i3 == 1) {
                        zArr[i3] = (i2 & 2) != 0;
                        str = LocaleController.getString("AttachAudio", C0338R.string.AttachAudio);
                    } else if (i3 == 2) {
                        zArr[i3] = (i2 & 4) != 0;
                        str = LocaleController.getString("AttachVideo", C0338R.string.AttachVideo);
                    } else if (i3 == 3) {
                        zArr[i3] = (i2 & 8) != 0;
                        str = LocaleController.getString("AttachDocument", C0338R.string.AttachDocument);
                    } else if (i3 == 4) {
                        zArr[i3] = (i2 & 16) != 0;
                        str = LocaleController.getString("AttachMusic", C0338R.string.AttachMusic);
                    } else if (i3 == 5) {
                        if (VERSION.SDK_INT < 11) {
                        } else {
                            zArr[i3] = (i2 & 32) != 0;
                            str = LocaleController.getString("AttachGif", C0338R.string.AttachGif);
                        }
                    }
                    View checkBoxCell = new CheckBoxCell(this.f2379a.getParentActivity());
                    checkBoxCell.setTag(Integer.valueOf(i3));
                    checkBoxCell.setBackgroundResource(C0338R.drawable.list_selector);
                    linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                    checkBoxCell.setText(str, TtmlNode.ANONYMOUS_REGION_ID, zArr[i3], true);
                    checkBoxCell.setOnClickListener(new TabSettingsActivity(this, zArr));
                }
                View bottomSheetCell = new BottomSheetCell(this.f2379a.getParentActivity(), 1);
                bottomSheetCell.setBackgroundResource(C0338R.drawable.list_selector);
                bottomSheetCell.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
                bottomSheetCell.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                bottomSheetCell.setOnClickListener(new TabSettingsActivity(this, zArr));
                linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 48));
                builder2.setCustomView(linearLayout);
                this.f2379a.showDialog(builder2.create());
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.q.3 */
    class TabSettingsActivity implements OnClickListener {
        final /* synthetic */ TabSettingsActivity f2380a;

        TabSettingsActivity(TabSettingsActivity tabSettingsActivity) {
            this.f2380a = tabSettingsActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            this.f2380a.m2198b();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.q.4 */
    class TabSettingsActivity implements OnCancelListener {
        final /* synthetic */ TabSettingsActivity f2381a;

        TabSettingsActivity(TabSettingsActivity tabSettingsActivity) {
            this.f2381a = tabSettingsActivity;
        }

        public void onCancel(DialogInterface dialogInterface) {
            this.f2381a.m2198b();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.q.a */
    private class TabSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ TabSettingsActivity f2382a;
        private Context f2383b;

        public TabSettingsActivity(TabSettingsActivity tabSettingsActivity, Context context) {
            this.f2382a = tabSettingsActivity;
            this.f2383b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f2382a.f2402o;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == this.f2382a.f2392e ? 2 : (i == this.f2382a.f2390c || i == this.f2382a.f2395h || i == this.f2382a.f2396i || i == this.f2382a.f2399l || i == this.f2382a.f2400m || i == this.f2382a.f2401n) ? 3 : (i == this.f2382a.f2393f || i == this.f2382a.f2397j) ? 6 : (i == this.f2382a.f2391d || i == this.f2382a.f2398k) ? 7 : i == this.f2382a.f2394g ? 8 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f2383b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f2383b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f2383b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        if (i == this.f2382a.f2392e) {
                            textSettingsCell2.setText(LocaleController.getString("TabsOrderAndVisibility", C0338R.string.TabsOrderAndVisibility), true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f2383b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        r3 = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                        if (i == this.f2382a.f2390c) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("ShowTab", C0338R.string.ShowTab), r3.getBoolean("show_tab", true), true);
                        } else if (i == this.f2382a.f2395h) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("SwipeOnTabs", C0338R.string.SwipeOnTabs), MoboConstants.f1335b, true);
                        } else if (i == this.f2382a.f2396i) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("ShowTabsInBottom", C0338R.string.ShowTabsInBottom), MoboConstants.f1331X, true);
                        } else if (i == this.f2382a.f2399l) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("ShowTabsUnreadCount", C0338R.string.ShowTabsUnreadCount), MoboConstants.f1332Y, true);
                        } else if (i == this.f2382a.f2400m) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("CountMutedMessages", C0338R.string.CountMutedMessages), MoboConstants.f1333Z, true);
                        } else if (i == this.f2382a.f2401n) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("CountChatsInsteadOfMessages", C0338R.string.CountChatsInsteadOfMessages), MoboConstants.aa, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f2383b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        if (i == this.f2382a.f2394g) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowChatsAdminToCreatorTab", C0338R.string.ShowChatsAdminToCreatorTab), LocaleController.getString("ShowChatsAdminToCreatorTabDetail", C0338R.string.ShowChatsAdminToCreatorTabDetail), MoboConstants.f1328U, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f2383b) : view;
                    } else {
                        if (itemViewType == 5) {
                            if (view != null) {
                                return view;
                            }
                            textSettingsCell = new TextInfoCell(this.f2383b);
                            try {
                                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                ((TextInfoCell) textSettingsCell).setText(String.format(Locale.US, "Mobogram for Android v%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)}));
                                return textSettingsCell;
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                                return textSettingsCell;
                            }
                        } else if (itemViewType != 6) {
                            return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f2383b) : view;
                        } else {
                            textSettingsCell = view == null ? new TextDetailSettingsCell(this.f2383b) : view;
                            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                            r3 = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
                            int i2;
                            if (i == this.f2382a.f2393f) {
                                textDetailSettingsCell.setMultilineDetail(false);
                                i2 = MoboConstants.f1329V;
                                if (!r3.getBoolean("last_selected_tab", false)) {
                                    List<TabData> a = TabsUtil.m2260a(false, false);
                                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("DefaultTab", C0338R.string.DefaultTab), LocaleController.getString("DefaultTab", C0338R.string.DefaultTab), true);
                                    for (TabData tabData : a) {
                                        if (tabData.m2225a() == i2) {
                                            textDetailSettingsCell.setTextAndValue(LocaleController.getString("DefaultTab", C0338R.string.DefaultTab), tabData.m2232d(), true);
                                            break;
                                        }
                                    }
                                }
                                textDetailSettingsCell.setTextAndValue(LocaleController.getString("DefaultTab", C0338R.string.DefaultTab), LocaleController.getString("LastSelectedTab", C0338R.string.LastSelectedTab), true);
                            } else if (i == this.f2382a.f2397j) {
                                i2 = MoboConstants.f1346m;
                                String string = LocaleController.getString("AutoDownloadFavorites", C0338R.string.AutoDownloadFavorites);
                                String str = TtmlNode.ANONYMOUS_REGION_ID;
                                if ((i2 & 1) != 0) {
                                    str = str + LocaleController.getString("AttachPhoto", C0338R.string.AttachPhoto);
                                }
                                if ((i2 & 2) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("AttachAudio", C0338R.string.AttachAudio);
                                }
                                if ((i2 & 4) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("AttachVideo", C0338R.string.AttachVideo);
                                }
                                if ((i2 & 8) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("AttachDocument", C0338R.string.AttachDocument);
                                }
                                if ((i2 & 16) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("AttachMusic", C0338R.string.AttachMusic);
                                }
                                if ((i2 & 32) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("AttachGif", C0338R.string.AttachGif);
                                }
                                if (str.length() == 0) {
                                    str = LocaleController.getString("NoMediaAutoDownload", C0338R.string.NoMediaAutoDownload);
                                }
                                textDetailSettingsCell.setTextAndValue(string, str, true);
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
            return i == this.f2382a.f2390c || i == this.f2382a.f2393f || i == this.f2382a.f2394g || i == this.f2382a.f2396i || i == this.f2382a.f2397j || i == this.f2382a.f2392e || i == this.f2382a.f2395h || i == this.f2382a.f2399l || i == this.f2382a.f2400m || i == this.f2382a.f2401n;
        }
    }

    private void m2196a() {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("RestartForApplyChanges", C0338R.string.RestartForApplyChanges));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new TabSettingsActivity(this));
        builder.setOnCancelListener(new TabSettingsActivity(this));
        builder.create().show();
    }

    private void m2198b() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 1234567, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("TabSettings", C0338R.string.TabSettings));
        this.actionBar.setActionBarMenuOnItemClick(new TabSettingsActivity(this));
        this.f2389b = new TabSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f2388a = new ListView(context);
        initThemeBackground(this.f2388a);
        this.f2388a.setDivider(null);
        this.f2388a.setDividerHeight(0);
        this.f2388a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f2388a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f2388a, LayoutHelper.createFrame(-1, -1, 51));
        this.f2388a.setAdapter(this.f2389b);
        this.f2388a.setOnItemClickListener(new TabSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.f2402o = 0;
        int i = this.f2402o;
        this.f2402o = i + 1;
        this.f2390c = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2391d = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2392e = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2393f = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2394g = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2395h = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2396i = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2397j = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2398k = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2399l = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2400m = i;
        i = this.f2402o;
        this.f2402o = i + 1;
        this.f2401n = i;
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

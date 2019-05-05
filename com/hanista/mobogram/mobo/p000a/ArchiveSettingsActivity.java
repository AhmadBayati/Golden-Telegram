package com.hanista.mobogram.mobo.p000a;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.Builder;
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

/* renamed from: com.hanista.mobogram.mobo.a.e */
public class ArchiveSettingsActivity extends BaseFragment {
    private ListView f134a;
    private ArchiveSettingsActivity f135b;
    private int f136c;
    private int f137d;
    private int f138e;
    private int f139f;
    private int f140g;
    private int f141h;
    private int f142i;

    /* renamed from: com.hanista.mobogram.mobo.a.e.1 */
    class ArchiveSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ArchiveSettingsActivity f124a;

        ArchiveSettingsActivity(ArchiveSettingsActivity archiveSettingsActivity) {
            this.f124a = archiveSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f124a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.a.e.2 */
    class ArchiveSettingsActivity implements OnItemClickListener {
        final /* synthetic */ ArchiveSettingsActivity f129a;

        /* renamed from: com.hanista.mobogram.mobo.a.e.2.1 */
        class ArchiveSettingsActivity implements OnClickListener {
            final /* synthetic */ boolean[] f125a;
            final /* synthetic */ ArchiveSettingsActivity f126b;

            ArchiveSettingsActivity(ArchiveSettingsActivity archiveSettingsActivity, boolean[] zArr) {
                this.f126b = archiveSettingsActivity;
                this.f125a = zArr;
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                this.f125a[intValue] = !this.f125a[intValue];
                checkBoxCell.setChecked(this.f125a[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.a.e.2.2 */
        class ArchiveSettingsActivity implements OnClickListener {
            final /* synthetic */ boolean[] f127a;
            final /* synthetic */ ArchiveSettingsActivity f128b;

            ArchiveSettingsActivity(ArchiveSettingsActivity archiveSettingsActivity, boolean[] zArr) {
                this.f128b = archiveSettingsActivity;
                this.f127a = zArr;
            }

            public void onClick(View view) {
                try {
                    if (this.f128b.f129a.visibleDialog != null) {
                        this.f128b.f129a.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                int i = 0;
                for (int i2 = 0; i2 < 5; i2++) {
                    if (this.f127a[i2]) {
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
                        }
                    }
                }
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("archive_button_mask", i);
                edit.commit();
                MoboConstants.m1379a();
                if (this.f128b.f129a.f134a != null) {
                    this.f128b.f129a.f134a.invalidateViews();
                }
            }
        }

        ArchiveSettingsActivity(ArchiveSettingsActivity archiveSettingsActivity) {
            this.f129a = archiveSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            boolean z;
            if (i == this.f129a.f136c) {
                z = MoboConstants.aF;
                edit.putBoolean("archive_feature", !z);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!z);
                }
                if (this.f129a.f134a != null) {
                    this.f129a.f134a.invalidateViews();
                }
                MoboConstants.m1379a();
                this.f129a.m242a();
            } else if (i == this.f129a.f141h) {
                z = MoboConstants.aJ;
                edit.putBoolean("archive_show_my_chat", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
                this.f129a.m242a();
            } else if (i == this.f129a.f139f) {
                z = MoboConstants.aH;
                edit.putBoolean("archive_button_for_my_msg", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f129a.f140g) {
                z = MoboConstants.aI;
                edit.putBoolean("archive_categorizing", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f129a.f138e) {
                boolean[] zArr = new boolean[5];
                Builder builder = new Builder(this.f129a.getParentActivity());
                int i2 = MoboConstants.aG;
                builder.setApplyTopPadding(false);
                View linearLayout = new LinearLayout(this.f129a.getParentActivity());
                linearLayout.setOrientation(1);
                for (int i3 = 0; i3 < 5; i3++) {
                    String str = null;
                    if (i3 == 0) {
                        zArr[i3] = (i2 & 1) != 0;
                        str = LocaleController.getString("Contacts", C0338R.string.Contacts);
                    } else if (i3 == 1) {
                        zArr[i3] = (i2 & 2) != 0;
                        str = LocaleController.getString("Groups", C0338R.string.Groups);
                    } else if (i3 == 2) {
                        zArr[i3] = (i2 & 4) != 0;
                        str = LocaleController.getString("SuperGroups", C0338R.string.SuperGroups);
                    } else if (i3 == 3) {
                        zArr[i3] = (i2 & 8) != 0;
                        str = LocaleController.getString("Channels", C0338R.string.Channels);
                    } else if (i3 == 4) {
                        zArr[i3] = (i2 & 16) != 0;
                        str = LocaleController.getString("Robots", C0338R.string.Robots);
                    }
                    View checkBoxCell = new CheckBoxCell(this.f129a.getParentActivity());
                    checkBoxCell.setTag(Integer.valueOf(i3));
                    checkBoxCell.setBackgroundResource(C0338R.drawable.list_selector);
                    linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                    checkBoxCell.setText(str, TtmlNode.ANONYMOUS_REGION_ID, zArr[i3], true);
                    checkBoxCell.setOnClickListener(new ArchiveSettingsActivity(this, zArr));
                }
                View bottomSheetCell = new BottomSheetCell(this.f129a.getParentActivity(), 1);
                bottomSheetCell.setBackgroundResource(C0338R.drawable.list_selector);
                bottomSheetCell.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
                bottomSheetCell.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                bottomSheetCell.setOnClickListener(new ArchiveSettingsActivity(this, zArr));
                linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 48));
                builder.setCustomView(linearLayout);
                this.f129a.showDialog(builder.create());
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.a.e.3 */
    class ArchiveSettingsActivity implements DialogInterface.OnClickListener {
        final /* synthetic */ ArchiveSettingsActivity f130a;

        ArchiveSettingsActivity(ArchiveSettingsActivity archiveSettingsActivity) {
            this.f130a = archiveSettingsActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            this.f130a.m244b();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.a.e.4 */
    class ArchiveSettingsActivity implements OnCancelListener {
        final /* synthetic */ ArchiveSettingsActivity f131a;

        ArchiveSettingsActivity(ArchiveSettingsActivity archiveSettingsActivity) {
            this.f131a = archiveSettingsActivity;
        }

        public void onCancel(DialogInterface dialogInterface) {
            this.f131a.m244b();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.a.e.a */
    private class ArchiveSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ ArchiveSettingsActivity f132a;
        private Context f133b;

        public ArchiveSettingsActivity(ArchiveSettingsActivity archiveSettingsActivity, Context context) {
            this.f132a = archiveSettingsActivity;
            this.f133b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f132a.f142i;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == this.f132a.f136c ? 3 : i == this.f132a.f138e ? 6 : i == this.f132a.f137d ? 7 : (i == this.f132a.f141h || i == this.f132a.f140g || i == this.f132a.f139f) ? 8 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f133b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f133b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f133b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f133b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        if (i == this.f132a.f136c) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("ArchiveFeature", C0338R.string.ArchiveFeature), MoboConstants.aF, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f133b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        if (i == this.f132a.f141h) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowMyChatInChatList", C0338R.string.ShowMyChatInChatList), LocaleController.getString("ShowMyChatInChatListDetail", C0338R.string.ShowMyChatInChatListDetail), MoboConstants.aJ, true);
                        } else if (i == this.f132a.f139f) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowButtonForMyMessages", C0338R.string.ShowButtonForMyMessages), LocaleController.getString("ShowButtonForMyMessagesDetail", C0338R.string.ShowButtonForMyMessagesDetail), MoboConstants.aH, true);
                        } else if (i == this.f132a.f140g) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("CategorizingFavoriteMessages", C0338R.string.CategorizingFavoriteMessages), LocaleController.getString("CategorizingFavoriteMessagesDetail", C0338R.string.CategorizingFavoriteMessagesDetail), MoboConstants.aI, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f133b) : view;
                    } else {
                        if (itemViewType == 5) {
                            return view == null ? new TextInfoCell(this.f133b) : view;
                        } else {
                            if (itemViewType != 6) {
                                return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f133b) : view;
                            } else {
                                textSettingsCell = view == null ? new TextDetailSettingsCell(this.f133b) : view;
                                TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                                if (i == this.f132a.f138e) {
                                    String string = LocaleController.getString("ShowFavoriteButton", C0338R.string.ShowFavoriteButton);
                                    int i2 = MoboConstants.aG;
                                    String str = TtmlNode.ANONYMOUS_REGION_ID;
                                    if ((i2 & 1) != 0) {
                                        str = str + LocaleController.getString("Contacts", C0338R.string.Contacts);
                                    }
                                    if ((i2 & 2) != 0) {
                                        if (str.length() != 0) {
                                            str = str + ", ";
                                        }
                                        str = str + LocaleController.getString("Groups", C0338R.string.Groups);
                                    }
                                    if ((i2 & 4) != 0) {
                                        if (str.length() != 0) {
                                            str = str + ", ";
                                        }
                                        str = str + LocaleController.getString("SuperGroups", C0338R.string.SuperGroups);
                                    }
                                    if ((i2 & 8) != 0) {
                                        if (str.length() != 0) {
                                            str = str + ", ";
                                        }
                                        str = str + LocaleController.getString("Channels", C0338R.string.Channels);
                                    }
                                    if ((i2 & 16) != 0) {
                                        if (str.length() != 0) {
                                            str = str + ", ";
                                        }
                                        str = str + LocaleController.getString("Robots", C0338R.string.Robots);
                                    }
                                    if (str.length() == 0) {
                                        str = LocaleController.getString("NothingSelected", C0338R.string.NothingSelected);
                                    }
                                    textDetailSettingsCell.setTextAndValue(string, str, true);
                                }
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
            return i == this.f132a.f136c || i == this.f132a.f141h || i == this.f132a.f140g || i == this.f132a.f138e || i == this.f132a.f139f;
        }
    }

    private void m242a() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("RestartForApplyChanges", C0338R.string.RestartForApplyChanges));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new ArchiveSettingsActivity(this));
        builder.setOnCancelListener(new ArchiveSettingsActivity(this));
        builder.create().show();
    }

    private void m244b() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 1234567, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ArchiveSettings", C0338R.string.ArchiveSettings));
        this.actionBar.setActionBarMenuOnItemClick(new ArchiveSettingsActivity(this));
        this.f135b = new ArchiveSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f134a = new ListView(context);
        initThemeBackground(this.f134a);
        this.f134a.setDivider(null);
        this.f134a.setDividerHeight(0);
        this.f134a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f134a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f134a, LayoutHelper.createFrame(-1, -1, 51));
        this.f134a.setAdapter(this.f135b);
        this.f134a.setOnItemClickListener(new ArchiveSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.m71a().m170d();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.f142i = 0;
        int i = this.f142i;
        this.f142i = i + 1;
        this.f136c = i;
        i = this.f142i;
        this.f142i = i + 1;
        this.f137d = i;
        i = this.f142i;
        this.f142i = i + 1;
        this.f138e = i;
        i = this.f142i;
        this.f142i = i + 1;
        this.f139f = i;
        i = this.f142i;
        this.f142i = i + 1;
        this.f140g = i;
        i = this.f142i;
        this.f142i = i + 1;
        this.f141h = i;
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

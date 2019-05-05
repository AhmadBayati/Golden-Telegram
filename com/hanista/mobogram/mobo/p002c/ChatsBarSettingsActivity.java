package com.hanista.mobogram.mobo.p002c;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
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
import com.hanista.mobogram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import com.hanista.mobogram.mobo.MoboConstants;
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
import com.hanista.mobogram.ui.Components.NumberPicker;
import java.util.Locale;

/* renamed from: com.hanista.mobogram.mobo.c.b */
public class ChatsBarSettingsActivity extends BaseFragment {
    private ListView f298a;
    private ChatsBarSettingsActivity f299b;
    private int f300c;
    private int f301d;
    private int f302e;
    private int f303f;
    private int f304g;
    private int f305h;
    private int f306i;
    private int f307j;

    /* renamed from: com.hanista.mobogram.mobo.c.b.1 */
    class ChatsBarSettingsActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ChatsBarSettingsActivity f282a;

        ChatsBarSettingsActivity(ChatsBarSettingsActivity chatsBarSettingsActivity) {
            this.f282a = chatsBarSettingsActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f282a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.b.2 */
    class ChatsBarSettingsActivity implements OnItemClickListener {
        final /* synthetic */ ChatsBarSettingsActivity f295a;

        /* renamed from: com.hanista.mobogram.mobo.c.b.2.1 */
        class ChatsBarSettingsActivity implements OnClickListener {
            final /* synthetic */ NumberPicker f283a;
            final /* synthetic */ ChatsBarSettingsActivity f284b;

            ChatsBarSettingsActivity(ChatsBarSettingsActivity chatsBarSettingsActivity, NumberPicker numberPicker) {
                this.f284b = chatsBarSettingsActivity;
                this.f283a = numberPicker;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("chat_bar_count", this.f283a.getValue());
                edit.commit();
                MoboConstants.m1379a();
                if (this.f284b.f295a.f298a != null) {
                    this.f284b.f295a.f298a.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.c.b.2.2 */
        class ChatsBarSettingsActivity implements OnClickListener {
            final /* synthetic */ NumberPicker f285a;
            final /* synthetic */ ChatsBarSettingsActivity f286b;

            ChatsBarSettingsActivity(ChatsBarSettingsActivity chatsBarSettingsActivity, NumberPicker numberPicker) {
                this.f286b = chatsBarSettingsActivity;
                this.f285a = numberPicker;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("chat_bar_height", this.f285a.getValue());
                edit.commit();
                MoboConstants.m1379a();
                if (this.f286b.f295a.f298a != null) {
                    this.f286b.f295a.f298a.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.c.b.2.3 */
        class ChatsBarSettingsActivity implements View.OnClickListener {
            final /* synthetic */ boolean[] f287a;
            final /* synthetic */ ChatsBarSettingsActivity f288b;

            ChatsBarSettingsActivity(ChatsBarSettingsActivity chatsBarSettingsActivity, boolean[] zArr) {
                this.f288b = chatsBarSettingsActivity;
                this.f287a = zArr;
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                this.f287a[intValue] = !this.f287a[intValue];
                checkBoxCell.setChecked(this.f287a[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.c.b.2.4 */
        class ChatsBarSettingsActivity implements View.OnClickListener {
            final /* synthetic */ boolean[] f289a;
            final /* synthetic */ ChatsBarSettingsActivity f290b;

            ChatsBarSettingsActivity(ChatsBarSettingsActivity chatsBarSettingsActivity, boolean[] zArr) {
                this.f290b = chatsBarSettingsActivity;
                this.f289a = zArr;
            }

            public void onClick(View view) {
                try {
                    if (this.f290b.f295a.visibleDialog != null) {
                        this.f290b.f295a.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                int i = 0;
                for (int i2 = 0; i2 < 3; i2++) {
                    if (this.f289a[i2]) {
                        if (i2 == 0) {
                            i |= 1;
                        } else if (i2 == 1) {
                            i |= 2;
                        } else if (i2 == 2) {
                            i |= 4;
                        }
                    }
                }
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putInt("chat_bar_chat_state", i);
                if (i == 0) {
                    edit.putBoolean("chat_bar_show", false);
                } else {
                    edit.putBoolean("chat_bar_show", true);
                }
                edit.commit();
                MoboConstants.m1379a();
                if (this.f290b.f295a.f298a != null) {
                    this.f290b.f295a.f298a.invalidateViews();
                }
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.c.b.2.5 */
        class ChatsBarSettingsActivity implements View.OnClickListener {
            final /* synthetic */ boolean[] f291a;
            final /* synthetic */ ChatsBarSettingsActivity f292b;

            ChatsBarSettingsActivity(ChatsBarSettingsActivity chatsBarSettingsActivity, boolean[] zArr) {
                this.f292b = chatsBarSettingsActivity;
                this.f291a = zArr;
            }

            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                int intValue = ((Integer) checkBoxCell.getTag()).intValue();
                this.f291a[intValue] = !this.f291a[intValue];
                checkBoxCell.setChecked(this.f291a[intValue], true);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.c.b.2.6 */
        class ChatsBarSettingsActivity implements View.OnClickListener {
            final /* synthetic */ boolean[] f293a;
            final /* synthetic */ ChatsBarSettingsActivity f294b;

            ChatsBarSettingsActivity(ChatsBarSettingsActivity chatsBarSettingsActivity, boolean[] zArr) {
                this.f294b = chatsBarSettingsActivity;
                this.f293a = zArr;
            }

            public void onClick(View view) {
                try {
                    if (this.f294b.f295a.visibleDialog != null) {
                        this.f294b.f295a.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                int i = 0;
                for (int i2 = 0; i2 < 5; i2++) {
                    if (this.f293a[i2]) {
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
                edit.putInt("chat_bar_chat_types", i);
                if (i == 0) {
                    edit.putBoolean("chat_bar_show", false);
                } else {
                    edit.putBoolean("chat_bar_show", true);
                }
                edit.commit();
                MoboConstants.m1379a();
                if (this.f294b.f295a.f298a != null) {
                    this.f294b.f295a.f298a.invalidateViews();
                }
            }
        }

        ChatsBarSettingsActivity(ChatsBarSettingsActivity chatsBarSettingsActivity) {
            this.f295a = chatsBarSettingsActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            boolean z;
            if (i == this.f295a.f300c) {
                z = MoboConstants.av;
                edit.putBoolean("chat_bar_show", !z);
                if (MoboConstants.ax == 0) {
                    edit.putInt("chat_bar_chat_types", 31);
                }
                if (MoboConstants.aw == 0) {
                    edit.putInt("chat_bar_chat_state", 7);
                }
                edit.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
                if (this.f295a.f298a != null) {
                    this.f295a.f298a.invalidateViews();
                }
            } else if (i == this.f295a.f304g) {
                if (this.f295a.getParentActivity() != null) {
                    r0 = new Builder(this.f295a.getParentActivity());
                    r0.setTitle(LocaleController.getString("BarChatsCount", C0338R.string.BarChatsCount));
                    r1 = new NumberPicker(this.f295a.getParentActivity());
                    r1.setMinValue(2);
                    r1.setMaxValue(500);
                    r1.setValue(MoboConstants.ay);
                    r0.setView(r1);
                    r0.setNegativeButton(LocaleController.getString("Done", C0338R.string.Done), new ChatsBarSettingsActivity(this, r1));
                    this.f295a.showDialog(r0.create());
                } else {
                    return;
                }
            } else if (i == this.f295a.f306i) {
                if (this.f295a.getParentActivity() != null) {
                    r0 = new Builder(this.f295a.getParentActivity());
                    r0.setTitle(LocaleController.getString("BarHeight", C0338R.string.BarHeight));
                    r1 = new NumberPicker(this.f295a.getParentActivity());
                    r1.setMinValue(40);
                    r1.setMaxValue(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                    r1.setValue(MoboConstants.aA);
                    r0.setView(r1);
                    r0.setNegativeButton(LocaleController.getString("Done", C0338R.string.Done), new ChatsBarSettingsActivity(this, r1));
                    this.f295a.showDialog(r0.create());
                } else {
                    return;
                }
            } else if (i == this.f295a.f302e) {
                r2 = new boolean[3];
                r3 = new BottomSheet.Builder(this.f295a.getParentActivity());
                r4 = MoboConstants.aw;
                r3.setApplyTopPadding(false);
                r5 = new LinearLayout(this.f295a.getParentActivity());
                r5.setOrientation(1);
                for (r1 = 0; r1 < 3; r1++) {
                    r0 = null;
                    if (r1 == 0) {
                        r2[r1] = (r4 & 1) != 0;
                        r0 = LocaleController.getString("UnreadUnmuted", C0338R.string.UnreadUnmuted);
                    } else if (r1 == 1) {
                        r2[r1] = (r4 & 2) != 0;
                        r0 = LocaleController.getString("UnreadMuted", C0338R.string.UnreadMuted);
                    } else if (r1 == 2) {
                        r2[r1] = (r4 & 4) != 0;
                        r0 = LocaleController.getString("Read", C0338R.string.Read);
                    }
                    r6 = new CheckBoxCell(this.f295a.getParentActivity());
                    r6.setTag(Integer.valueOf(r1));
                    r6.setBackgroundResource(C0338R.drawable.list_selector);
                    r5.addView(r6, LayoutHelper.createLinear(-1, 48));
                    r6.setText(r0, TtmlNode.ANONYMOUS_REGION_ID, r2[r1], true);
                    r6.setOnClickListener(new ChatsBarSettingsActivity(this, r2));
                }
                r0 = new BottomSheetCell(this.f295a.getParentActivity(), 1);
                r0.setBackgroundResource(C0338R.drawable.list_selector);
                r0.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
                r0.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                r0.setOnClickListener(new ChatsBarSettingsActivity(this, r2));
                r5.addView(r0, LayoutHelper.createLinear(-1, 48));
                r3.setCustomView(r5);
                this.f295a.showDialog(r3.create());
            } else if (i == this.f295a.f305h) {
                z = MoboConstants.az;
                edit.putBoolean("chat_bar_open_as_default", !z);
                edit.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!z);
                }
                MoboConstants.m1379a();
            } else if (i == this.f295a.f303f) {
                r2 = new boolean[5];
                r3 = new BottomSheet.Builder(this.f295a.getParentActivity());
                r4 = MoboConstants.ax;
                r3.setApplyTopPadding(false);
                r5 = new LinearLayout(this.f295a.getParentActivity());
                r5.setOrientation(1);
                for (r1 = 0; r1 < 5; r1++) {
                    r0 = null;
                    if (r1 == 0) {
                        r2[r1] = (r4 & 1) != 0;
                        r0 = LocaleController.getString("Contacts", C0338R.string.Contacts);
                    } else if (r1 == 1) {
                        r2[r1] = (r4 & 2) != 0;
                        r0 = LocaleController.getString("Groups", C0338R.string.Groups);
                    } else if (r1 == 2) {
                        r2[r1] = (r4 & 4) != 0;
                        r0 = LocaleController.getString("SuperGroups", C0338R.string.SuperGroups);
                    } else if (r1 == 3) {
                        r2[r1] = (r4 & 8) != 0;
                        r0 = LocaleController.getString("Channels", C0338R.string.Channels);
                    } else if (r1 == 4) {
                        r2[r1] = (r4 & 16) != 0;
                        r0 = LocaleController.getString("Robots", C0338R.string.Robots);
                    }
                    r6 = new CheckBoxCell(this.f295a.getParentActivity());
                    r6.setTag(Integer.valueOf(r1));
                    r6.setBackgroundResource(C0338R.drawable.list_selector);
                    r5.addView(r6, LayoutHelper.createLinear(-1, 48));
                    r6.setText(r0, TtmlNode.ANONYMOUS_REGION_ID, r2[r1], true);
                    r6.setOnClickListener(new ChatsBarSettingsActivity(this, r2));
                }
                r0 = new BottomSheetCell(this.f295a.getParentActivity(), 1);
                r0.setBackgroundResource(C0338R.drawable.list_selector);
                r0.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
                r0.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                r0.setOnClickListener(new ChatsBarSettingsActivity(this, r2));
                r5.addView(r0, LayoutHelper.createLinear(-1, 48));
                r3.setCustomView(r5);
                this.f295a.showDialog(r3.create());
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.c.b.a */
    private class ChatsBarSettingsActivity extends BaseFragmentAdapter {
        final /* synthetic */ ChatsBarSettingsActivity f296a;
        private Context f297b;

        public ChatsBarSettingsActivity(ChatsBarSettingsActivity chatsBarSettingsActivity, Context context) {
            this.f296a = chatsBarSettingsActivity;
            this.f297b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f296a.f307j;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == this.f296a.f304g || i == this.f296a.f306i) ? 2 : i == this.f296a.f300c ? 3 : (i == this.f296a.f303f || i == this.f296a.f302e) ? 6 : i == this.f296a.f301d ? 7 : i == this.f296a.f305h ? 8 : 2;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                return view == null ? new EmptyCell(this.f297b) : view;
            } else {
                if (itemViewType == 1) {
                    return view == null ? new TextInfoPrivacyCell(this.f297b) : view;
                } else {
                    View textSettingsCell;
                    if (itemViewType == 2) {
                        textSettingsCell = view == null ? new TextSettingsCell(this.f297b) : view;
                        TextSettingsCell textSettingsCell2 = (TextSettingsCell) textSettingsCell;
                        if (i == this.f296a.f304g) {
                            textSettingsCell2.setTextAndValue(LocaleController.getString("BarChatsCount", C0338R.string.BarChatsCount), String.format("%d", new Object[]{Integer.valueOf(MoboConstants.ay)}), true);
                        } else if (i == this.f296a.f306i) {
                            textSettingsCell2.setTextAndValue(LocaleController.getString("BarHeight", C0338R.string.BarHeight), String.format("%d", new Object[]{Integer.valueOf(MoboConstants.aA)}), true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 3) {
                        textSettingsCell = view == null ? new TextCheckCell(this.f297b) : view;
                        TextCheckCell textCheckCell = (TextCheckCell) textSettingsCell;
                        if (i == this.f296a.f300c) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("ShowRecentChatsBar", C0338R.string.ShowRecentChatsBar), MoboConstants.av, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 8) {
                        textSettingsCell = view == null ? new TextDetailCheckCell(this.f297b) : view;
                        TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textSettingsCell;
                        if (i == this.f296a.f305h) {
                            textDetailCheckCell.setTextAndCheck(LocaleController.getString("OpenAsDefault", C0338R.string.OpenAsDefault), LocaleController.getString("OpenAsDefaultDetail", C0338R.string.OpenAsDefaultDetail), MoboConstants.az, true);
                        }
                        return textSettingsCell;
                    } else if (itemViewType == 4) {
                        return view == null ? new HeaderCell(this.f297b) : view;
                    } else {
                        if (itemViewType == 5) {
                            if (view != null) {
                                return view;
                            }
                            textSettingsCell = new TextInfoCell(this.f297b);
                            try {
                                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                                ((TextInfoCell) textSettingsCell).setText(String.format(Locale.US, "Mobogram for Android v%s (%d)", new Object[]{packageInfo.versionName, Integer.valueOf(packageInfo.versionCode)}));
                                return textSettingsCell;
                            } catch (Throwable e) {
                                FileLog.m18e("tmessages", e);
                                return textSettingsCell;
                            }
                        } else if (itemViewType != 6) {
                            return (itemViewType == 7 && view == null) ? new ShadowSectionCell(this.f297b) : view;
                        } else {
                            textSettingsCell = view == null ? new TextDetailSettingsCell(this.f297b) : view;
                            TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textSettingsCell;
                            String string;
                            int i2;
                            String str;
                            if (i == this.f296a.f303f) {
                                string = LocaleController.getString("ChatTypesToShowInBar", C0338R.string.ChatTypesToShowInBar);
                                i2 = MoboConstants.ax;
                                str = TtmlNode.ANONYMOUS_REGION_ID;
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
                            } else if (i == this.f296a.f302e) {
                                string = LocaleController.getString("ChatState", C0338R.string.ChatState);
                                i2 = MoboConstants.aw;
                                str = TtmlNode.ANONYMOUS_REGION_ID;
                                if ((i2 & 1) != 0) {
                                    str = str + LocaleController.getString("UnreadUnmuted", C0338R.string.UnreadUnmuted);
                                }
                                if ((i2 & 2) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("UnreadMuted", C0338R.string.UnreadMuted);
                                }
                                if ((i2 & 4) != 0) {
                                    if (str.length() != 0) {
                                        str = str + ", ";
                                    }
                                    str = str + LocaleController.getString("Read", C0338R.string.Read);
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
            return i == this.f296a.f300c || i == this.f296a.f306i || i == this.f296a.f302e || i == this.f296a.f305h || i == this.f296a.f303f || i == this.f296a.f304g;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("RecentChatsBar", C0338R.string.RecentChatsBar));
        this.actionBar.setActionBarMenuOnItemClick(new ChatsBarSettingsActivity(this));
        this.f299b = new ChatsBarSettingsActivity(this, context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.f298a = new ListView(context);
        initThemeBackground(this.f298a);
        this.f298a.setDivider(null);
        this.f298a.setDividerHeight(0);
        this.f298a.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.f298a, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.f298a, LayoutHelper.createFrame(-1, -1, 51));
        this.f298a.setAdapter(this.f299b);
        this.f298a.setOnItemClickListener(new ChatsBarSettingsActivity(this));
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.f307j = 0;
        int i = this.f307j;
        this.f307j = i + 1;
        this.f300c = i;
        i = this.f307j;
        this.f307j = i + 1;
        this.f301d = i;
        i = this.f307j;
        this.f307j = i + 1;
        this.f303f = i;
        i = this.f307j;
        this.f307j = i + 1;
        this.f302e = i;
        i = this.f307j;
        this.f307j = i + 1;
        this.f304g = i;
        i = this.f307j;
        this.f307j = i + 1;
        this.f305h = i;
        i = this.f307j;
        this.f307j = i + 1;
        this.f306i = i;
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

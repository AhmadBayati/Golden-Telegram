package com.hanista.mobogram.mobo;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.mobo.p015o.MenuData;
import com.hanista.mobogram.mobo.p015o.MenuUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenuItem;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailSettingsCell;
import com.hanista.mobogram.ui.Cells.TextInfoPrivacyCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.j */
public class HiddenActivity extends BaseFragment implements NotificationCenterDelegate {
    private HiddenActivity f1178a;
    private ListView f1179b;
    private TextView f1180c;
    private EditText f1181d;
    private TextView f1182e;
    private ActionBarMenuItem f1183f;
    private int f1184g;
    private int f1185h;
    private int f1186i;
    private String f1187j;
    private int f1188k;
    private int f1189l;
    private int f1190m;
    private int f1191n;
    private int f1192o;
    private int f1193p;
    private int f1194q;
    private int f1195r;
    private int f1196s;

    /* renamed from: com.hanista.mobogram.mobo.j.1 */
    class HiddenActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ HiddenActivity f1145a;

        HiddenActivity(HiddenActivity hiddenActivity) {
            this.f1145a = hiddenActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f1145a.finishFragment();
            } else if (i == 1) {
                if (this.f1145a.f1186i == 0) {
                    this.f1145a.m1245c();
                } else if (this.f1145a.f1186i == 1) {
                    this.f1145a.m1247d();
                }
            } else if (i == 2) {
                this.f1145a.f1185h = 0;
                this.f1145a.m1243b();
            } else if (i == 3) {
                this.f1145a.f1185h = 1;
                this.f1145a.m1243b();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.2 */
    class HiddenActivity implements OnEditorActionListener {
        final /* synthetic */ HiddenActivity f1146a;

        HiddenActivity(HiddenActivity hiddenActivity) {
            this.f1146a = hiddenActivity;
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (this.f1146a.f1186i == 0) {
                this.f1146a.m1245c();
                return true;
            } else if (this.f1146a.f1186i != 1) {
                return false;
            } else {
                this.f1146a.m1247d();
                return true;
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.3 */
    class HiddenActivity implements TextWatcher {
        final /* synthetic */ HiddenActivity f1147a;

        HiddenActivity(HiddenActivity hiddenActivity) {
            this.f1147a = hiddenActivity;
        }

        public void afterTextChanged(Editable editable) {
            if (this.f1147a.f1181d.length() != 4) {
                return;
            }
            if (this.f1147a.f1184g == 2 && HiddenConfig.f1401d == 0) {
                this.f1147a.m1247d();
            } else if (this.f1147a.f1184g != 1 || this.f1147a.f1185h != 0) {
            } else {
                if (this.f1147a.f1186i == 0) {
                    this.f1147a.m1245c();
                } else if (this.f1147a.f1186i == 1) {
                    this.f1147a.m1247d();
                }
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.4 */
    class HiddenActivity implements OnCreateContextMenuListener {
        final /* synthetic */ HiddenActivity f1148a;

        HiddenActivity(HiddenActivity hiddenActivity) {
            this.f1148a = hiddenActivity;
        }

        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo) {
            contextMenu.clear();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.5 */
    class HiddenActivity implements Callback {
        final /* synthetic */ HiddenActivity f1149a;

        HiddenActivity(HiddenActivity hiddenActivity) {
            this.f1149a = hiddenActivity;
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        public void onDestroyActionMode(ActionMode actionMode) {
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.6 */
    class HiddenActivity implements OnClickListener {
        final /* synthetic */ HiddenActivity f1150a;

        HiddenActivity(HiddenActivity hiddenActivity) {
            this.f1150a = hiddenActivity;
        }

        public void onClick(View view) {
            this.f1150a.f1183f.toggleSubMenu();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.7 */
    class HiddenActivity implements OnItemClickListener {
        final /* synthetic */ HiddenActivity f1155a;

        /* renamed from: com.hanista.mobogram.mobo.j.7.1 */
        class HiddenActivity implements DialogInterface.OnClickListener {
            final /* synthetic */ TextCheckCell f1151a;
            final /* synthetic */ HiddenActivity f1152b;

            HiddenActivity(HiddenActivity hiddenActivity, TextCheckCell textCheckCell) {
                this.f1152b = hiddenActivity;
                this.f1151a = textCheckCell;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                HiddenConfig.f1399b = TtmlNode.ANONYMOUS_REGION_ID;
                HiddenConfig.m1400c();
                HiddenConfig.m1394a();
                int childCount = this.f1152b.f1155a.f1179b.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = this.f1152b.f1155a.f1179b.getChildAt(i2);
                    if (childAt instanceof TextSettingsCell) {
                        ((TextSettingsCell) childAt).setTextColor(-3750202);
                        break;
                    }
                }
                this.f1151a.setChecked(HiddenConfig.f1399b.length() != 0);
                this.f1152b.f1155a.parentLayout.rebuildAllFragmentViews(false);
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.j.7.2 */
        class HiddenActivity implements DialogInterface.OnClickListener {
            final /* synthetic */ List f1153a;
            final /* synthetic */ HiddenActivity f1154b;

            HiddenActivity(HiddenActivity hiddenActivity, List list) {
                this.f1154b = hiddenActivity;
                this.f1153a = list;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                int b = i == 0 ? 100 : i == 1 ? 101 : ((MenuData) this.f1153a.get(i - 2)).m1973b();
                edit.putInt("hidden_chats_entering_method", b);
                edit.commit();
                if (this.f1154b.f1155a.f1179b != null) {
                    this.f1154b.f1155a.f1179b.invalidateViews();
                }
                MoboConstants.m1379a();
            }
        }

        HiddenActivity(HiddenActivity hiddenActivity) {
            this.f1155a = hiddenActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i == this.f1155a.f1189l) {
                this.f1155a.presentFragment(new HiddenActivity(1));
            } else if (i == this.f1155a.f1188k) {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                if (HiddenConfig.f1399b.length() != 0) {
                    Builder builder = new Builder(this.f1155a.getParentActivity());
                    builder.setMessage(LocaleController.getString("HideChatsDisableWarn", C0338R.string.HideChatsDisableWarn));
                    builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new HiddenActivity(this, textCheckCell));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                    this.f1155a.showDialog(builder.create());
                    return;
                }
                this.f1155a.presentFragment(new HiddenActivity(1));
            } else if (i == this.f1155a.f1191n) {
                r1 = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                r2 = MoboConstants.f1312E;
                r1.putBoolean("show_hidden_chats_in_share", !r2);
                r1.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!r2);
                }
                MoboConstants.m1379a();
            } else if (i == this.f1155a.f1193p) {
                Builder builder2 = new Builder(this.f1155a.getParentActivity());
                builder2.setTitle(LocaleController.getString("LongPressOn", C0338R.string.LongPressOn));
                List<MenuData> a = MenuUtil.m1994a(true);
                List arrayList = new ArrayList();
                List arrayList2 = new ArrayList();
                arrayList2.add(LocaleController.getString("SearchButton", C0338R.string.SearchButton));
                arrayList2.add(LocaleController.getString("FloatingButton", C0338R.string.FloatingButton));
                for (MenuData menuData : a) {
                    if (menuData.m1978g() == 3) {
                        arrayList.add(menuData);
                        arrayList2.add(LocaleController.formatString("Item", C0338R.string.Item, menuData.m1976e()));
                    }
                }
                builder2.setItems((CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]), new HiddenActivity(this, arrayList));
                builder2.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                this.f1155a.showDialog(builder2.create());
            } else if (i == this.f1155a.f1195r) {
                r1 = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                r2 = MoboConstants.an;
                r1.putBoolean("hidden_chats_show_notifications", !r2);
                r1.commit();
                if (view instanceof TextDetailCheckCell) {
                    ((TextDetailCheckCell) view).setChecked(!r2);
                }
                MoboConstants.m1379a();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.8 */
    class HiddenActivity implements Runnable {
        final /* synthetic */ HiddenActivity f1156a;

        HiddenActivity(HiddenActivity hiddenActivity) {
            this.f1156a = hiddenActivity;
        }

        public void run() {
            if (this.f1156a.f1181d != null) {
                this.f1156a.f1181d.requestFocus();
                AndroidUtilities.showKeyboard(this.f1156a.f1181d);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.9 */
    class HiddenActivity implements OnPreDrawListener {
        final /* synthetic */ HiddenActivity f1157a;

        HiddenActivity(HiddenActivity hiddenActivity) {
            this.f1157a = hiddenActivity;
        }

        public boolean onPreDraw() {
            this.f1157a.f1179b.getViewTreeObserver().removeOnPreDrawListener(this);
            this.f1157a.m1252f();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.j.a */
    private class HiddenActivity extends BaseFragmentAdapter {
        final /* synthetic */ HiddenActivity f1158a;
        private Context f1159b;

        public HiddenActivity(HiddenActivity hiddenActivity, Context context) {
            this.f1158a = hiddenActivity;
            this.f1159b = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public int getCount() {
            return this.f1158a.f1196s;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return (i == this.f1158a.f1188k || i == this.f1158a.f1191n) ? 0 : i == this.f1158a.f1189l ? 1 : (i == this.f1158a.f1190m || i == this.f1158a.f1192o || i == this.f1158a.f1194q) ? 2 : i == this.f1158a.f1193p ? 6 : i == this.f1158a.f1195r ? 8 : 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int itemViewType = getItemViewType(i);
            View textCheckCell;
            if (itemViewType == 0) {
                if (view == null) {
                    textCheckCell = new TextCheckCell(this.f1159b);
                    textCheckCell.setBackgroundColor(-1);
                } else {
                    textCheckCell = view;
                }
                TextCheckCell textCheckCell2 = (TextCheckCell) textCheckCell;
                if (i == this.f1158a.f1188k) {
                    textCheckCell2.setTextAndCheck(LocaleController.getString("HideChatsActivation", C0338R.string.HideChatsActivation), HiddenConfig.f1399b.length() > 0, true);
                    return textCheckCell;
                } else if (i != this.f1158a.f1191n) {
                    return textCheckCell;
                } else {
                    textCheckCell2.setTextAndCheck(LocaleController.getString("ShowHiddenChatsInShareList", C0338R.string.ShowHiddenChatsInShareList), MoboConstants.f1312E, true);
                    return textCheckCell;
                }
            } else if (itemViewType == 1) {
                if (view == null) {
                    textCheckCell = new TextSettingsCell(this.f1159b);
                    textCheckCell.setBackgroundColor(-1);
                } else {
                    textCheckCell = view;
                }
                TextSettingsCell textSettingsCell = (TextSettingsCell) textCheckCell;
                if (i != this.f1158a.f1189l) {
                    return textCheckCell;
                }
                textSettingsCell.setText(LocaleController.getString("ChangePasscode", C0338R.string.ChangePasscode), false);
                textSettingsCell.setTextColor(HiddenConfig.f1399b.length() == 0 ? -3750202 : Theme.MSG_TEXT_COLOR);
                return textCheckCell;
            } else if (itemViewType == 2) {
                textCheckCell = view == null ? new TextInfoPrivacyCell(this.f1159b) : view;
                if (i == this.f1158a.f1190m) {
                    ((TextInfoPrivacyCell) textCheckCell).setText(LocaleController.getString("HideChatsHelp", C0338R.string.HideChatsHelp));
                    if (this.f1158a.f1192o != -1) {
                        textCheckCell.setBackgroundResource(C0338R.drawable.greydivider);
                        return textCheckCell;
                    }
                    textCheckCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textCheckCell;
                } else if (i == this.f1158a.f1192o) {
                    ((TextInfoPrivacyCell) textCheckCell).setText(LocaleController.getString("ShowHiddenChatsInShareListDetail", C0338R.string.ShowHiddenChatsInShareListDetail));
                    textCheckCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textCheckCell;
                } else if (i != this.f1158a.f1194q) {
                    return textCheckCell;
                } else {
                    ((TextInfoPrivacyCell) textCheckCell).setText(LocaleController.getString("AccessingMethodDetail", C0338R.string.AccessingMethodDetail));
                    textCheckCell.setBackgroundResource(C0338R.drawable.greydivider_bottom);
                    return textCheckCell;
                }
            } else if (itemViewType == 6) {
                if (view == null) {
                    textCheckCell = new TextDetailSettingsCell(this.f1159b);
                    textCheckCell.setBackgroundColor(-1);
                } else {
                    textCheckCell = view;
                }
                TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) textCheckCell;
                textDetailSettingsCell.setTextAndValue(LocaleController.getString("AccessingMethod", C0338R.string.AccessingMethod), TtmlNode.ANONYMOUS_REGION_ID, true);
                if (i != this.f1158a.f1193p) {
                    return textCheckCell;
                }
                textDetailSettingsCell.setMultilineDetail(false);
                int i2 = MoboConstants.am;
                if (i2 == 100) {
                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("AccessingMethod", C0338R.string.AccessingMethod), LocaleController.formatString("LongPressOnSomething", C0338R.string.LongPressOnSomething, LocaleController.getString("SearchButton", C0338R.string.SearchButton)), true);
                    return textCheckCell;
                } else if (i2 == 101) {
                    textDetailSettingsCell.setTextAndValue(LocaleController.getString("AccessingMethod", C0338R.string.AccessingMethod), LocaleController.formatString("LongPressOnSomething", C0338R.string.LongPressOnSomething, LocaleController.getString("FloatingButton", C0338R.string.FloatingButton)), true);
                    return textCheckCell;
                } else {
                    for (MenuData b : MenuUtil.m1994a(false)) {
                        if (b.m1973b() == i2) {
                            textDetailSettingsCell.setTextAndValue(LocaleController.getString("AccessingMethod", C0338R.string.AccessingMethod), LocaleController.formatString("LongPressOnItem", C0338R.string.LongPressOnItem, ((MenuData) r6.next()).m1976e()), true);
                            return textCheckCell;
                        }
                    }
                    return textCheckCell;
                }
            } else if (itemViewType != 8) {
                return view;
            } else {
                if (view == null) {
                    textCheckCell = new TextDetailCheckCell(this.f1159b);
                    textCheckCell.setBackgroundColor(-1);
                } else {
                    textCheckCell = view;
                }
                TextDetailCheckCell textDetailCheckCell = (TextDetailCheckCell) textCheckCell;
                if (i != this.f1158a.f1195r) {
                    return textCheckCell;
                }
                textDetailCheckCell.setTextAndCheck(LocaleController.getString("ShowHiddenChatsNotifications", C0338R.string.ShowHiddenChatsNotifications), LocaleController.getString("ShowHiddenChatsNotificationsDetail", C0338R.string.ShowHiddenChatsNotificationsDetail), MoboConstants.an, true);
                return textCheckCell;
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
            return i == this.f1158a.f1188k || i == this.f1158a.f1191n || i == this.f1158a.f1193p || i == this.f1158a.f1195r || (HiddenConfig.f1399b.length() != 0 && i == this.f1158a.f1189l);
        }
    }

    public HiddenActivity(int i) {
        this.f1185h = 0;
        this.f1186i = 0;
        this.f1184g = i;
    }

    private void m1242a() {
        this.f1196s = 0;
        int i = this.f1196s;
        this.f1196s = i + 1;
        this.f1188k = i;
        i = this.f1196s;
        this.f1196s = i + 1;
        this.f1189l = i;
        i = this.f1196s;
        this.f1196s = i + 1;
        this.f1190m = i;
        i = this.f1196s;
        this.f1196s = i + 1;
        this.f1193p = i;
        i = this.f1196s;
        this.f1196s = i + 1;
        this.f1194q = i;
        i = this.f1196s;
        this.f1196s = i + 1;
        this.f1191n = i;
        i = this.f1196s;
        this.f1196s = i + 1;
        this.f1192o = i;
        i = this.f1196s;
        this.f1196s = i + 1;
        this.f1195r = i;
    }

    private void m1243b() {
        if (this.f1182e != null) {
            if (this.f1185h == 0) {
                this.f1182e.setText(LocaleController.getString("PasscodePIN", C0338R.string.PasscodePIN));
            } else if (this.f1185h == 1) {
                this.f1182e.setText(LocaleController.getString("PasscodePassword", C0338R.string.PasscodePassword));
            }
        }
        if ((this.f1184g == 1 && this.f1185h == 0) || (this.f1184g == 2 && HiddenConfig.f1401d == 0)) {
            this.f1181d.setFilters(new InputFilter[]{new LengthFilter(4)});
            this.f1181d.setInputType(3);
            this.f1181d.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        } else if ((this.f1184g == 1 && this.f1185h == 1) || (this.f1184g == 2 && HiddenConfig.f1401d == 1)) {
            this.f1181d.setFilters(new InputFilter[0]);
            this.f1181d.setKeyListener(null);
            this.f1181d.setInputType(129);
        }
        this.f1181d.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void m1245c() {
        if (this.f1181d.getText().length() == 0 || (this.f1185h == 0 && this.f1181d.getText().length() != 4)) {
            m1250e();
            return;
        }
        if (this.f1185h == 0) {
            this.actionBar.setTitle(LocaleController.getString("PasscodePIN", C0338R.string.PasscodePIN));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PasscodePassword", C0338R.string.PasscodePassword));
        }
        this.f1183f.setVisibility(8);
        this.f1180c.setText(LocaleController.getString("ReEnterYourPasscode", C0338R.string.ReEnterYourPasscode));
        this.f1187j = this.f1181d.getText().toString();
        this.f1181d.setText(TtmlNode.ANONYMOUS_REGION_ID);
        this.f1186i = 1;
    }

    private void m1247d() {
        if (this.f1181d.getText().length() == 0) {
            m1250e();
        } else if (this.f1184g == 1) {
            if (this.f1187j.equals(this.f1181d.getText().toString())) {
                try {
                    HiddenConfig.f1400c = new byte[16];
                    Utilities.random.nextBytes(HiddenConfig.f1400c);
                    Object bytes = MoboUtils.m1713c(this.f1187j).getBytes(C0700C.UTF8_NAME);
                    Object obj = new byte[(bytes.length + 32)];
                    System.arraycopy(HiddenConfig.f1400c, 0, obj, 0, 16);
                    System.arraycopy(bytes, 0, obj, 16, bytes.length);
                    System.arraycopy(HiddenConfig.f1400c, 0, obj, bytes.length + 16, 16);
                    HiddenConfig.f1399b = Utilities.bytesToHex(Utilities.computeSHA256(obj, 0, obj.length));
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                HiddenConfig.f1401d = this.f1185h;
                HiddenConfig.m1394a();
                this.parentLayout.rebuildAllFragmentViews(false);
                finishFragment();
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                this.f1181d.clearFocus();
                AndroidUtilities.hideKeyboard(this.f1181d);
                return;
            }
            try {
                Toast.makeText(getParentActivity(), LocaleController.getString("PasscodeDoNotMatch", C0338R.string.PasscodeDoNotMatch), 0).show();
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
            AndroidUtilities.shakeView(this.f1180c, 2.0f, 0);
            this.f1181d.setText(TtmlNode.ANONYMOUS_REGION_ID);
        } else if (this.f1184g != 2) {
        } else {
            if (HiddenConfig.m1396a(MoboUtils.m1713c(this.f1181d.getText().toString()))) {
                this.f1181d.clearFocus();
                AndroidUtilities.hideKeyboard(this.f1181d);
                presentFragment(new HiddenActivity(0), true);
                return;
            }
            this.f1181d.setText(TtmlNode.ANONYMOUS_REGION_ID);
            m1250e();
        }
    }

    private void m1250e() {
        if (getParentActivity() != null) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(this.f1180c, 2.0f, 0);
        }
    }

    private void m1252f() {
        if (this.f1183f != null) {
            if (!AndroidUtilities.isTablet()) {
                LayoutParams layoutParams = (LayoutParams) this.f1183f.getLayoutParams();
                layoutParams.topMargin = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                this.f1183f.setLayoutParams(layoutParams);
            }
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.f1182e.setTextSize(20.0f);
            } else {
                this.f1182e.setTextSize(18.0f);
            }
        }
    }

    public View createView(Context context) {
        if (this.f1184g != 3) {
            this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new HiddenActivity(this));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        LayoutParams layoutParams;
        if (this.f1184g != 0) {
            ActionBarMenu createMenu = this.actionBar.createMenu();
            if (ThemeUtil.m2490b()) {
                Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_done);
                drawable.setColorFilter(AdvanceTheme.f2492c, Mode.MULTIPLY);
                createMenu.addItemWithWidth(1, drawable, AndroidUtilities.dp(56.0f));
            } else {
                createMenu.addItemWithWidth(1, (int) C0338R.drawable.ic_done, AndroidUtilities.dp(56.0f));
            }
            this.f1180c = new TextView(context);
            this.f1180c.setTypeface(FontUtil.m1176a().m1161d());
            this.f1180c.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
            if (this.f1184g != 1) {
                this.f1180c.setText(LocaleController.getString("EnterCurrentPasscode", C0338R.string.EnterCurrentPasscode));
            } else if (HiddenConfig.f1399b.length() != 0) {
                this.f1180c.setText(LocaleController.getString("EnterNewPasscode", C0338R.string.EnterNewPasscode));
            } else {
                this.f1180c.setText(LocaleController.getString("EnterNewFirstPasscode", C0338R.string.EnterNewFirstPasscode));
            }
            this.f1180c.setTextSize(1, 18.0f);
            this.f1180c.setGravity(1);
            frameLayout.addView(this.f1180c);
            LayoutParams layoutParams2 = (LayoutParams) this.f1180c.getLayoutParams();
            layoutParams2.width = -2;
            layoutParams2.height = -2;
            layoutParams2.gravity = 1;
            layoutParams2.topMargin = AndroidUtilities.dp(38.0f);
            this.f1180c.setLayoutParams(layoutParams2);
            this.f1181d = new EditText(context);
            this.f1181d.setTextSize(1, 20.0f);
            this.f1181d.setTextColor(Theme.MSG_TEXT_COLOR);
            this.f1181d.setMaxLines(1);
            this.f1181d.setLines(1);
            this.f1181d.setGravity(1);
            this.f1181d.setSingleLine(true);
            if (this.f1184g == 1) {
                this.f1186i = 0;
                this.f1181d.setImeOptions(5);
            } else {
                this.f1186i = 1;
                this.f1181d.setImeOptions(6);
            }
            this.f1181d.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.f1181d.setTypeface(Typeface.DEFAULT);
            AndroidUtilities.clearCursorDrawable(this.f1181d);
            frameLayout.addView(this.f1181d);
            layoutParams = (LayoutParams) this.f1181d.getLayoutParams();
            layoutParams.topMargin = AndroidUtilities.dp(90.0f);
            layoutParams.height = AndroidUtilities.dp(36.0f);
            layoutParams.leftMargin = AndroidUtilities.dp(40.0f);
            layoutParams.gravity = 51;
            layoutParams.rightMargin = AndroidUtilities.dp(40.0f);
            layoutParams.width = -1;
            this.f1181d.setLayoutParams(layoutParams);
            this.f1181d.setOnEditorActionListener(new HiddenActivity(this));
            this.f1181d.addTextChangedListener(new HiddenActivity(this));
            if (VERSION.SDK_INT < 11) {
                this.f1181d.setOnCreateContextMenuListener(new HiddenActivity(this));
            } else {
                this.f1181d.setCustomSelectionActionModeCallback(new HiddenActivity(this));
            }
            if (this.f1184g == 1) {
                this.f1183f = new ActionBarMenuItem(context, createMenu, ThemeUtil.m2485a().m2294h());
                this.f1183f.setSubMenuOpenSide(1);
                this.f1183f.addSubItem(2, LocaleController.getString("PasscodePIN", C0338R.string.PasscodePIN), 0);
                this.f1183f.addSubItem(3, LocaleController.getString("PasscodePassword", C0338R.string.PasscodePassword), 0);
                this.actionBar.addView(this.f1183f);
                layoutParams = (LayoutParams) this.f1183f.getLayoutParams();
                layoutParams.height = -1;
                layoutParams.width = -2;
                layoutParams.rightMargin = AndroidUtilities.dp(40.0f);
                layoutParams.leftMargin = AndroidUtilities.isTablet() ? AndroidUtilities.dp(64.0f) : AndroidUtilities.dp(56.0f);
                layoutParams.gravity = 51;
                this.f1183f.setLayoutParams(layoutParams);
                this.f1183f.setOnClickListener(new HiddenActivity(this));
                this.f1182e = new TextView(context);
                this.f1182e.setGravity(3);
                this.f1182e.setSingleLine(true);
                this.f1182e.setLines(1);
                this.f1182e.setMaxLines(1);
                this.f1182e.setEllipsize(TruncateAt.END);
                this.f1182e.setTextColor(-1);
                this.f1182e.setTypeface(FontUtil.m1176a().m1160c());
                this.f1182e.setCompoundDrawablesWithIntrinsicBounds(0, 0, C0338R.drawable.ic_arrow_drop_down, 0);
                this.f1182e.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                this.f1182e.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
                this.f1183f.addView(this.f1182e);
                layoutParams = (LayoutParams) this.f1182e.getLayoutParams();
                layoutParams.width = -2;
                layoutParams.height = -2;
                layoutParams.leftMargin = AndroidUtilities.dp(16.0f);
                layoutParams.gravity = 16;
                layoutParams.bottomMargin = AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.f1182e.setLayoutParams(layoutParams);
            } else {
                this.actionBar.setTitle(LocaleController.getString("Passcode", C0338R.string.Passcode));
            }
            m1243b();
        } else {
            this.actionBar.setTitle(LocaleController.getString("HideChats", C0338R.string.HideChatsSetting));
            frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
            this.f1179b = new ListView(context);
            this.f1179b.setDivider(null);
            this.f1179b.setDividerHeight(0);
            this.f1179b.setVerticalScrollBarEnabled(false);
            this.f1179b.setDrawSelectorOnTop(true);
            frameLayout.addView(this.f1179b);
            layoutParams = (LayoutParams) this.f1179b.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.gravity = 48;
            this.f1179b.setLayoutParams(layoutParams);
            ListView listView = this.f1179b;
            ListAdapter hiddenActivity = new HiddenActivity(this, context);
            this.f1178a = hiddenActivity;
            listView.setAdapter(hiddenActivity);
            this.f1179b.setOnItemClickListener(new HiddenActivity(this));
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.didSetPasscode && this.f1184g == 0) {
            m1242a();
            if (this.f1178a != null) {
                this.f1178a.notifyDataSetChanged();
            }
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.f1179b != null) {
            this.f1179b.getViewTreeObserver().addOnPreDrawListener(new HiddenActivity(this));
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        m1242a();
        if (this.f1184g == 0) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.f1184g == 0) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.f1178a != null) {
            this.f1178a.notifyDataSetChanged();
        }
        if (this.f1184g != 0) {
            AndroidUtilities.runOnUIThread(new HiddenActivity(this), 200);
        }
        m1252f();
        initThemeActionBar();
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.f1184g != 0) {
            AndroidUtilities.showKeyboard(this.f1181d);
        }
    }
}

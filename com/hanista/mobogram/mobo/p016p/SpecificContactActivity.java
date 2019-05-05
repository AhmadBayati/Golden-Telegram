package com.hanista.mobogram.mobo.p016p;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.BottomSheet;
import com.hanista.mobogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Cells.CheckBoxCell;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextDetailCheckCell;
import com.hanista.mobogram.ui.ChatActivity;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.ContactsActivity;
import com.hanista.mobogram.ui.ContactsActivity.ContactsActivityDelegate;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.p.b */
public class SpecificContactActivity extends BaseFragment implements NotificationCenterDelegate, ContactsActivityDelegate {
    private ListView f2048a;
    private SpecificContactAdapter f2049b;
    private TextDetailCheckCell f2050c;
    private TextCheckCell f2051d;
    private SettingManager f2052e;
    private DataBaseAccess f2053f;
    private TextView f2054g;
    private int f2055h;

    /* renamed from: com.hanista.mobogram.mobo.p.b.1 */
    class SpecificContactActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ SpecificContactActivity f2033a;

        SpecificContactActivity(SpecificContactActivity specificContactActivity) {
            this.f2033a = specificContactActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f2033a.finishFragment();
            } else if (i == 2) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("returnAsResult", true);
                BaseFragment contactsActivity = new ContactsActivity(bundle);
                contactsActivity.setDelegate(this.f2033a);
                this.f2033a.presentFragment(contactsActivity);
            } else if (i == 3) {
                this.f2033a.presentFragment(new SpecificContactSettingsActivity());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.b.2 */
    class SpecificContactActivity implements OnClickListener {
        final /* synthetic */ int f2034a;
        final /* synthetic */ SpecificContactActivity f2035b;

        SpecificContactActivity(SpecificContactActivity specificContactActivity, int i) {
            this.f2035b = specificContactActivity;
            this.f2034a = i;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            SpecificContact a = this.f2035b.f2049b.m2015a(this.f2034a);
            if (i == 0) {
                this.f2035b.m2006a(a);
            } else if (i == 1) {
                this.f2035b.f2053f.m899l(a.m1997a());
                SpecificContactBiz.m2017a();
                this.f2035b.m2004a();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.b.3 */
    class SpecificContactActivity implements OnTouchListener {
        final /* synthetic */ SpecificContactActivity f2036a;

        SpecificContactActivity(SpecificContactActivity specificContactActivity) {
            this.f2036a = specificContactActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.b.4 */
    class SpecificContactActivity implements View.OnClickListener {
        final /* synthetic */ SpecificContactActivity f2037a;

        SpecificContactActivity(SpecificContactActivity specificContactActivity) {
            this.f2037a = specificContactActivity;
        }

        public void onClick(View view) {
            boolean z = true;
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            boolean z2 = MoboConstants.f1323P;
            edit.putBoolean("specific_contact_notification", !z2);
            if (z2) {
                edit.putBoolean("specific_contact_service_enabled", false);
                this.f2037a.f2050c.setChecked(false);
                this.f2037a.f2050c.setEnabled(false);
            } else {
                this.f2037a.f2050c.setEnabled(true);
            }
            edit.commit();
            if (view instanceof TextCheckCell) {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                if (z2) {
                    z = false;
                }
                textCheckCell.setChecked(z);
            }
            MoboConstants.m1379a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.b.5 */
    class SpecificContactActivity implements View.OnClickListener {
        final /* synthetic */ SpecificContactActivity f2039a;

        /* renamed from: com.hanista.mobogram.mobo.p.b.5.1 */
        class SpecificContactActivity implements OnClickListener {
            final /* synthetic */ SpecificContactActivity f2038a;

            SpecificContactActivity(SpecificContactActivity specificContactActivity) {
                this.f2038a = specificContactActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                boolean z = true;
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                boolean z2 = MoboConstants.f1322O;
                edit.putBoolean("specific_contact_service_enabled", !z2);
                edit.commit();
                TextDetailCheckCell a = this.f2038a.f2039a.f2050c;
                if (z2) {
                    z = false;
                }
                a.setChecked(z);
                MoboConstants.m1379a();
            }
        }

        SpecificContactActivity(SpecificContactActivity specificContactActivity) {
            this.f2039a = specificContactActivity;
        }

        public void onClick(View view) {
            Builder builder = new Builder(this.f2039a.getParentActivity());
            builder.setMessage(LocaleController.formatString("SpecificContactServiceAlert", C0338R.string.SpecificContactServiceAlert, new Object[0]));
            builder.setTitle(LocaleController.getString("SpecificContactService", C0338R.string.SpecificContactService));
            builder.setPositiveButton(LocaleController.getString(MoboConstants.f1322O ? "DisableIt" : "EnableIt", MoboConstants.f1322O ? C0338R.string.DisableIt : C0338R.string.EnableIt), new SpecificContactActivity(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
            this.f2039a.showDialog(builder.create());
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.b.6 */
    class SpecificContactActivity implements OnItemClickListener {
        final /* synthetic */ SpecificContactActivity f2040a;

        SpecificContactActivity(SpecificContactActivity specificContactActivity) {
            this.f2040a = specificContactActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            SpecificContact a = this.f2040a.f2049b.m2015a(i);
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", a.m1999b());
            this.f2040a.presentFragment(new ChatActivity(bundle), false);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.b.7 */
    class SpecificContactActivity implements OnItemLongClickListener {
        final /* synthetic */ SpecificContactActivity f2041a;

        SpecificContactActivity(SpecificContactActivity specificContactActivity) {
            this.f2041a = specificContactActivity;
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            this.f2041a.m2011b(i);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.b.8 */
    class SpecificContactActivity implements View.OnClickListener {
        final /* synthetic */ boolean[] f2042a;
        final /* synthetic */ SpecificContactActivity f2043b;

        SpecificContactActivity(SpecificContactActivity specificContactActivity, boolean[] zArr) {
            this.f2043b = specificContactActivity;
            this.f2042a = zArr;
        }

        public void onClick(View view) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            int intValue = ((Integer) checkBoxCell.getTag()).intValue();
            this.f2042a[intValue] = !this.f2042a[intValue];
            checkBoxCell.setChecked(this.f2042a[intValue], true);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.b.9 */
    class SpecificContactActivity implements View.OnClickListener {
        final /* synthetic */ boolean[] f2044a;
        final /* synthetic */ SpecificContact f2045b;
        final /* synthetic */ Dialog f2046c;
        final /* synthetic */ SpecificContactActivity f2047d;

        SpecificContactActivity(SpecificContactActivity specificContactActivity, boolean[] zArr, SpecificContact specificContact, Dialog dialog) {
            this.f2047d = specificContactActivity;
            this.f2044a = zArr;
            this.f2045b = specificContact;
            this.f2046c = dialog;
        }

        public void onClick(View view) {
            int i = 0;
            for (int i2 = 0; i2 < 5; i2++) {
                if (this.f2044a[i2]) {
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
            if (i == 0) {
                Toast.makeText(this.f2047d.getParentActivity(), LocaleController.getString("YouMustSelectAtLeastOne", C0338R.string.YouMustSelectAtLeastOne), 1).show();
                return;
            }
            this.f2045b.m2000b(i);
            this.f2047d.f2053f.m845a(this.f2045b);
            SpecificContactBiz.m2017a();
            this.f2047d.m2004a();
            if (this.f2046c != null) {
                this.f2046c.dismiss();
            }
        }
    }

    public SpecificContactActivity(Bundle bundle) {
        super(bundle);
    }

    private void m2004a() {
        this.f2049b.m2016a(this.f2053f.m909q());
        this.f2049b.notifyDataSetChanged();
    }

    private void m2005a(int i) {
        if (this.f2048a != null) {
            int childCount = this.f2048a.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.f2048a.getChildAt(i2);
                if (childAt instanceof SpecificContactCell) {
                    ((SpecificContactCell) childAt).m2021a(i);
                }
            }
        }
    }

    private void m2006a(SpecificContact specificContact) {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        boolean[] zArr = new boolean[6];
        int c = specificContact.m2001c();
        builder.setApplyTopPadding(false);
        View linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(1);
        for (int i = 0; i < 5; i++) {
            String str = null;
            if (i == 0) {
                zArr[i] = (c & 1) != 0;
                str = LocaleController.getString("NotifyOnOnline", C0338R.string.NotifyOnOnline);
            } else if (i == 1) {
                zArr[i] = (c & 2) != 0;
                str = LocaleController.getString("NotifyOnOffline", C0338R.string.NotifyOnOffline);
            } else if (i == 2) {
                zArr[i] = (c & 4) != 0;
                str = LocaleController.getString("NotifyOnPhotoChange", C0338R.string.NotifyOnPhotoChange);
            } else if (i == 3) {
                zArr[i] = (c & 8) != 0;
                str = LocaleController.getString("NotifyOnNameChange", C0338R.string.NotifyOnNameChange);
            } else if (i == 4) {
                zArr[i] = (c & 16) != 0;
                str = LocaleController.getString("NotifyOnPhoneChange", C0338R.string.NotifyOnPhoneChange);
            }
            View checkBoxCell = new CheckBoxCell(getParentActivity());
            checkBoxCell.setTag(Integer.valueOf(i));
            checkBoxCell.setBackgroundResource(C0338R.drawable.list_selector);
            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
            checkBoxCell.setText(str, TtmlNode.ANONYMOUS_REGION_ID, zArr[i], true);
            checkBoxCell.setOnClickListener(new SpecificContactActivity(this, zArr));
        }
        View bottomSheetCell = new BottomSheetCell(getParentActivity(), 1);
        bottomSheetCell.setBackgroundResource(C0338R.drawable.list_selector);
        bottomSheetCell.setTextAndIcon(LocaleController.getString("Save", C0338R.string.Save).toUpperCase(), 0);
        bottomSheetCell.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
        linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 48));
        builder.setCustomView(linearLayout);
        Dialog create = builder.create();
        bottomSheetCell.setOnClickListener(new SpecificContactActivity(this, zArr, specificContact, create));
        create.show();
    }

    private void m2010b() {
        SettingManager settingManager = new SettingManager();
        if (!settingManager.m944b("specificContactHelpDisplayed")) {
            settingManager.m943a("specificContactHelpDisplayed", true);
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("SpecificContacts", C0338R.string.SpecificContacts)).setMessage(LocaleController.getString("SpecificContactHelp", C0338R.string.SpecificContactHelp));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new OnClickListener() {
                final /* synthetic */ com.hanista.mobogram.mobo.p016p.SpecificContactActivity f2032a;

                {
                    this.f2032a = r1;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private void m2011b(int i) {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        List arrayList = new ArrayList();
        arrayList.add(LocaleController.getString("Edit", C0338R.string.Edit));
        arrayList.add(LocaleController.getString("Delete", C0338R.string.Delete));
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new SpecificContactActivity(this, i));
        showDialog(builder.create());
    }

    private void m2013c() {
        if (ThemeUtil.m2490b()) {
            this.actionBar.setBackgroundColor(AdvanceTheme.bc);
            int i = AdvanceTheme.bd;
            if (i > 0) {
                Orientation orientation;
                switch (i) {
                    case VideoPlayer.STATE_PREPARING /*2*/:
                        orientation = Orientation.LEFT_RIGHT;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        orientation = Orientation.TL_BR;
                        break;
                    case VideoPlayer.STATE_READY /*4*/:
                        orientation = Orientation.BL_TR;
                        break;
                    default:
                        orientation = Orientation.TOP_BOTTOM;
                        break;
                }
                int i2 = AdvanceTheme.be;
                this.actionBar.setBackgroundDrawable(new GradientDrawable(orientation, new int[]{r1, i2}));
            }
            this.actionBar.setTitleColor(AdvanceTheme.bb);
            getParentActivity().getResources().getDrawable(C0338R.drawable.plus).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_settings).setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
        }
    }

    public View createView(Context context) {
        this.fragmentView = new FrameLayout(context);
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SpecificContacts", C0338R.string.SpecificContacts));
        this.actionBar.setActionBarMenuOnItemClick(new SpecificContactActivity(this));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addItem(2, (int) C0338R.drawable.plus);
        createMenu.addItem(3, (int) C0338R.drawable.ic_ab_settings);
        this.f2052e = new SettingManager();
        this.f2053f = new DataBaseAccess();
        View linearLayout = new LinearLayout(context);
        linearLayout.setVisibility(4);
        linearLayout.setOrientation(1);
        ((FrameLayout) this.fragmentView).addView(linearLayout, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 17, 10.0f, BitmapDescriptorFactory.HUE_GREEN, 10.0f, 10.0f));
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 17;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnTouchListener(new SpecificContactActivity(this));
        this.f2054g = new TextView(context);
        this.f2054g.setTextColor(-8355712);
        this.f2054g.setTextSize(1, 20.0f);
        this.f2054g.setGravity(17);
        this.f2054g.setTypeface(FontUtil.m1176a().m1161d());
        this.f2054g.setText(LocaleController.getString("NoSpecificContact", C0338R.string.NoSpecificContact));
        linearLayout.addView(this.f2054g);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.f2054g.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        this.f2054g.setLayoutParams(layoutParams2);
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams2);
        this.f2051d = new TextCheckCell(context);
        this.f2051d.setTextAndCheck(LocaleController.getString("SpecificContactNotification", C0338R.string.SpecificContactNotification), MoboConstants.f1323P, true);
        this.f2051d.setOnClickListener(new SpecificContactActivity(this));
        ((FrameLayout) this.fragmentView).addView(this.f2051d, LayoutHelper.createFrame(-1, BitmapDescriptorFactory.HUE_YELLOW, 48, 10.0f, 10.0f, 10.0f, 10.0f));
        this.f2050c = new TextDetailCheckCell(context);
        this.f2050c.setTextAndCheck(LocaleController.getString("SpecificContactService", C0338R.string.SpecificContactService), LocaleController.getString("SpecificContactServiceDetail", C0338R.string.SpecificContactServiceDetail), MoboConstants.f1322O, true);
        this.f2050c.setEnabled(MoboConstants.f1323P);
        this.f2050c.setOnClickListener(new SpecificContactActivity(this));
        ((FrameLayout) this.fragmentView).addView(this.f2050c, LayoutHelper.createFrame(-1, 80.0f, 48, 10.0f, 70.0f, 10.0f, 10.0f));
        this.f2048a = new ListView(context);
        initThemeBackground(this.f2048a);
        this.f2048a.setEmptyView(linearLayout);
        this.f2048a.setVerticalScrollBarEnabled(false);
        this.f2048a.setDivider(null);
        this.f2048a.setDividerHeight(0);
        this.f2049b = new SpecificContactAdapter(context, this.f2053f.m909q());
        this.f2048a.setAdapter(this.f2049b);
        if (VERSION.SDK_INT >= 11) {
            this.f2048a.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        }
        ((FrameLayout) this.fragmentView).addView(this.f2048a, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 48, 10.0f, 150.0f, 10.0f, 10.0f));
        initThemeBackground(this.fragmentView);
        layoutParams = (LayoutParams) this.f2048a.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.f2048a.setLayoutParams(layoutParams);
        this.f2048a.setOnItemClickListener(new SpecificContactActivity(this));
        this.f2048a.setOnItemLongClickListener(new SpecificContactActivity(this));
        if (this.f2055h != 0) {
            didSelectContact(MessagesController.getInstance().getUser(Integer.valueOf(this.f2055h)), null);
        }
        m2010b();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if ((intValue & 2) != 0 || (intValue & 1) != 0 || (intValue & 4) != 0) {
                m2005a(intValue);
            }
        }
    }

    public void didSelectContact(User user, String str) {
        if (this.f2053f.m864c(user.id) != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("ThisContactWasSelectedBefore", C0338R.string.ThisContactWasSelectedBefore), 1).show();
            return;
        }
        SpecificContact specificContact = new SpecificContact();
        specificContact.m1998a(user.id);
        specificContact.m2000b(5);
        m2006a(specificContact);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        if (this.arguments != null) {
            this.f2055h = getArguments().getInt("user_id", 0);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public void onResume() {
        super.onResume();
        m2013c();
    }
}

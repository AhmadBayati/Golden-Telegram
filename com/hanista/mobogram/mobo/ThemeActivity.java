package com.hanista.mobogram.mobo;

import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.mobo.component.ColorSelectorDialog.ColorSelectorDialog;
import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p020s.Theme;
import com.hanista.mobogram.mobo.p020s.ThemeCell;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.TextCheckCell;
import com.hanista.mobogram.ui.Cells.TextColorCell;
import com.hanista.mobogram.ui.Cells.TextSettingsCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.LaunchActivity;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.r */
public class ThemeActivity extends BaseFragment {
    private ListView f2449a;
    private TextSettingsCell f2450b;
    private TextColorCell f2451c;
    private TextCheckCell f2452d;
    private SettingManager f2453e;

    /* renamed from: com.hanista.mobogram.mobo.r.1 */
    class ThemeActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ ThemeActivity f2403a;

        ThemeActivity(ThemeActivity themeActivity) {
            this.f2403a = themeActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f2403a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.2 */
    class ThemeActivity implements OnClickListener {
        final /* synthetic */ ThemeActivity f2404a;

        ThemeActivity(ThemeActivity themeActivity) {
            this.f2404a = themeActivity;
        }

        public void onClick(View view) {
            this.f2404a.m2265a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.3 */
    class ThemeActivity implements OnClickListener {
        final /* synthetic */ ThemeActivity f2405a;

        ThemeActivity(ThemeActivity themeActivity) {
            this.f2405a = themeActivity;
        }

        public void onClick(View view) {
            this.f2405a.m2266a(view);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.4 */
    class ThemeActivity implements OnClickListener {
        final /* synthetic */ SharedPreferences f2406a;
        final /* synthetic */ ThemeActivity f2407b;

        ThemeActivity(ThemeActivity themeActivity, SharedPreferences sharedPreferences) {
            this.f2407b = themeActivity;
            this.f2406a = sharedPreferences;
        }

        public void onClick(View view) {
            boolean z = true;
            Editor edit = this.f2406a.edit();
            boolean z2 = this.f2406a.getBoolean("theme_back_white", false);
            edit.putBoolean("theme_back_white", !z2);
            edit.commit();
            TextCheckCell b = this.f2407b.f2452d;
            if (z2) {
                z = false;
            }
            b.setChecked(z);
            this.f2407b.m2270b();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.5 */
    class ThemeActivity implements OnItemClickListener {
        final /* synthetic */ ThemeActivity f2410a;

        /* renamed from: com.hanista.mobogram.mobo.r.5.1 */
        class ThemeActivity implements DialogInterface.OnClickListener {
            final /* synthetic */ ThemeActivity f2408a;

            ThemeActivity(ThemeActivity themeActivity) {
                this.f2408a = themeActivity;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                this.f2408a.f2410a.m2271c();
            }
        }

        /* renamed from: com.hanista.mobogram.mobo.r.5.2 */
        class ThemeActivity implements OnCancelListener {
            final /* synthetic */ ThemeActivity f2409a;

            ThemeActivity(ThemeActivity themeActivity) {
                this.f2409a = themeActivity;
            }

            public void onCancel(DialogInterface dialogInterface) {
                this.f2409a.f2410a.m2271c();
            }
        }

        ThemeActivity(ThemeActivity themeActivity) {
            this.f2410a = themeActivity;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            ThemeUtil.m2486a((int) j);
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
            edit.remove("selectedBackground");
            edit.remove("selectedColor");
            edit.commit();
            ApplicationLoader.reloadWallpaper();
            edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            edit.remove("main_theme_color");
            edit.commit();
            Builder builder = new Builder(this.f2410a.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("ThemeChangedSuccessful", C0338R.string.ThemeChangedSuccessful));
            builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new ThemeActivity(this));
            builder.setOnCancelListener(new ThemeActivity(this));
            builder.create().show();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.6 */
    class ThemeActivity implements ColorSelectorDialog {
        final /* synthetic */ ThemeActivity f2411a;

        ThemeActivity(ThemeActivity themeActivity) {
            this.f2411a = themeActivity;
        }

        public void m2222a(int i) {
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
            edit.putInt("main_theme_color", i);
            edit.commit();
            ThemeUtil.m2492c();
            this.f2411a.f2451c.setTextAndColor(LocaleController.getString("MainThemeColor", C0338R.string.MainThemeColor), ThemeUtil.m2485a().m2289c(), true);
            this.f2411a.m2270b();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.7 */
    class ThemeActivity implements DialogInterface.OnClickListener {
        final /* synthetic */ ThemeActivity f2412a;

        ThemeActivity(ThemeActivity themeActivity) {
            this.f2412a = themeActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            this.f2412a.m2271c();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.8 */
    class ThemeActivity implements OnCancelListener {
        final /* synthetic */ ThemeActivity f2413a;

        ThemeActivity(ThemeActivity themeActivity) {
            this.f2413a = themeActivity;
        }

        public void onCancel(DialogInterface dialogInterface) {
            this.f2413a.m2271c();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.r.a */
    class ThemeActivity extends BaseFragmentAdapter {
        List<Theme> f2414a;
        final /* synthetic */ ThemeActivity f2415b;

        public ThemeActivity(ThemeActivity themeActivity, List<Theme> list) {
            this.f2415b = themeActivity;
            this.f2414a = list;
        }

        public int getCount() {
            return this.f2414a.size();
        }

        public Object getItem(int i) {
            return this.f2414a.get(i);
        }

        public long getItemId(int i) {
            return (long) ((Theme) this.f2414a.get(i)).m2287a();
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            return new ThemeCell(this.f2415b.getParentActivity(), (Theme) this.f2414a.get(i));
        }
    }

    public ThemeActivity(Bundle bundle) {
        super(bundle);
    }

    private void m2265a() {
        try {
            String str = "com.hanista.moboplus";
            Intent launchIntentForPackage = getParentActivity().getPackageManager().getLaunchIntentForPackage(str);
            if (launchIntentForPackage == null) {
                launchIntentForPackage = new Intent("android.intent.action.VIEW", Uri.parse("bazaar://details?id=" + str));
            }
            startActivityForResult(launchIntentForPackage, 503);
        } catch (Throwable e) {
            startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("https://cafebazaar.ir/app/com.hanista.moboplus")), 503);
            FileLog.m18e("tmessages", e);
        }
    }

    private void m2266a(View view) {
        ((LayoutInflater) getParentActivity().getSystemService("layout_inflater")).inflate(C0338R.layout.colordialog, null, false);
        new com.hanista.mobogram.mobo.component.ColorSelectorDialog(getParentActivity(), new ThemeActivity(this), ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).getInt("main_theme_color", ThemeUtil.m2485a().m2289c()), 0, 0, false).show();
    }

    private void m2270b() {
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName)).setMessage(LocaleController.getString("RestartForApplyChanges", C0338R.string.RestartForApplyChanges));
        builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new ThemeActivity(this));
        builder.setOnCancelListener(new ThemeActivity(this));
        builder.create().show();
    }

    private void m2271c() {
        ((AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(ApplicationLoader.applicationContext, 1234567, new Intent(ApplicationLoader.applicationContext, LaunchActivity.class), 268435456));
        System.exit(0);
    }

    public View createView(Context context) {
        this.fragmentView = new FrameLayout(context);
        this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Themes", C0338R.string.Themes));
        this.actionBar.setActionBarMenuOnItemClick(new ThemeActivity(this));
        this.f2453e = new SettingManager();
        this.f2450b = new TextSettingsCell(context);
        this.f2450b.setText(LocaleController.getString("DownloadAdvanceTheme", C0338R.string.DownloadAdvanceTheme), true);
        this.f2450b.setOnClickListener(new ThemeActivity(this));
        ((FrameLayout) this.fragmentView).addView(this.f2450b, LayoutHelper.createFrame(-1, BitmapDescriptorFactory.HUE_YELLOW, 48, 10.0f, 10.0f, 10.0f, 10.0f));
        this.f2451c = new TextColorCell(context);
        this.f2451c.setTextAndColor(LocaleController.getString("MainThemeColor", C0338R.string.MainThemeColor), ThemeUtil.m2485a().m2289c(), true);
        this.f2451c.setOnClickListener(new ThemeActivity(this));
        ((FrameLayout) this.fragmentView).addView(this.f2451c, LayoutHelper.createFrame(-1, BitmapDescriptorFactory.HUE_YELLOW, 48, 10.0f, BitmapDescriptorFactory.HUE_YELLOW, 10.0f, 10.0f));
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0);
        this.f2452d = new TextCheckCell(context);
        this.f2452d.setTextAndCheck(LocaleController.getString("WhiteBackground", C0338R.string.WhiteBackground), sharedPreferences.getBoolean("theme_back_white", true), true);
        ((FrameLayout) this.fragmentView).addView(this.f2452d, LayoutHelper.createFrame(-1, BitmapDescriptorFactory.HUE_YELLOW, 48, 10.0f, BitmapDescriptorFactory.HUE_GREEN, 10.0f, 10.0f));
        this.f2452d.setOnClickListener(new ThemeActivity(this, sharedPreferences));
        if (ThemeUtil.m2490b()) {
            this.f2451c.setVisibility(8);
            this.f2452d.setVisibility(8);
        }
        this.f2449a = new ListView(context);
        initThemeBackground(this.f2449a);
        initThemeBackground(this.fragmentView);
        this.f2449a.setVerticalScrollBarEnabled(false);
        this.f2449a.setDivider(null);
        this.f2449a.setDividerHeight(0);
        this.f2449a.setFastScrollEnabled(true);
        this.f2449a.setScrollBarStyle(33554432);
        this.f2449a.setCacheColorHint(0);
        this.f2449a.setScrollingCacheEnabled(false);
        ((FrameLayout) this.fragmentView).addView(this.f2449a, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, 48, 10.0f, ThemeUtil.m2490b() ? BitmapDescriptorFactory.HUE_YELLOW : BitmapDescriptorFactory.HUE_CYAN, 10.0f, 10.0f));
        LayoutParams layoutParams = (LayoutParams) this.f2449a.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.f2449a.setLayoutParams(layoutParams);
        this.f2449a.setDividerHeight(20);
        this.f2449a.setAdapter(new ThemeActivity(this, ThemeUtil.m2494d()));
        this.f2449a.setOnItemClickListener(new ThemeActivity(this));
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        initThemeActionBar();
    }
}

package com.hanista.mobogram.mobo;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.StatFs;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.ImageLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p008i.FontUtil;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.ActionBarMenu;
import com.hanista.mobogram.ui.ActionBar.BackDrawable;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Adapters.BaseFragmentAdapter;
import com.hanista.mobogram.ui.Cells.SharedDocumentCell;
import com.hanista.mobogram.ui.Components.LayoutHelper;
import com.hanista.mobogram.ui.Components.NumberTextView;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

/* renamed from: com.hanista.mobogram.mobo.p */
public class StorageSelectActivity extends BaseFragment {
    private static final Pattern f2104m;
    private ListView f2105a;
    private StorageSelectActivity f2106b;
    private NumberTextView f2107c;
    private TextView f2108d;
    private File f2109e;
    private ArrayList<StorageSelectActivity> f2110f;
    private boolean f2111g;
    private ArrayList<Object> f2112h;
    private long f2113i;
    private ArrayList<View> f2114j;
    private boolean f2115k;
    private BroadcastReceiver f2116l;

    /* renamed from: com.hanista.mobogram.mobo.p.1 */
    class StorageSelectActivity extends BroadcastReceiver {
        final /* synthetic */ StorageSelectActivity f2009a;

        /* renamed from: com.hanista.mobogram.mobo.p.1.1 */
        class StorageSelectActivity implements Runnable {
            final /* synthetic */ StorageSelectActivity f2008a;

            StorageSelectActivity(StorageSelectActivity storageSelectActivity) {
                this.f2008a = storageSelectActivity;
            }

            public void run() {
                try {
                    if (this.f2008a.f2009a.f2109e == null) {
                        this.f2008a.f2009a.m2036b();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        StorageSelectActivity(StorageSelectActivity storageSelectActivity) {
            this.f2009a = storageSelectActivity;
        }

        public void onReceive(Context context, Intent intent) {
            Runnable storageSelectActivity = new StorageSelectActivity(this);
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                this.f2009a.f2105a.postDelayed(storageSelectActivity, 1000);
            } else {
                storageSelectActivity.run();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.2 */
    class StorageSelectActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ StorageSelectActivity f2010a;

        StorageSelectActivity(StorageSelectActivity storageSelectActivity) {
            this.f2010a = storageSelectActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f2010a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.3 */
    class StorageSelectActivity implements OnTouchListener {
        final /* synthetic */ StorageSelectActivity f2011a;

        StorageSelectActivity(StorageSelectActivity storageSelectActivity) {
            this.f2011a = storageSelectActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.4 */
    class StorageSelectActivity implements OnTouchListener {
        final /* synthetic */ StorageSelectActivity f2012a;

        StorageSelectActivity(StorageSelectActivity storageSelectActivity) {
            this.f2012a = storageSelectActivity;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.5 */
    class StorageSelectActivity implements OnScrollListener {
        final /* synthetic */ StorageSelectActivity f2013a;

        StorageSelectActivity(StorageSelectActivity storageSelectActivity) {
            this.f2013a = storageSelectActivity;
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            this.f2013a.f2115k = i != 0;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.6 */
    class StorageSelectActivity implements OnItemClickListener {
        final /* synthetic */ Context f2016a;
        final /* synthetic */ StorageSelectActivity f2017b;

        /* renamed from: com.hanista.mobogram.mobo.p.6.1 */
        class StorageSelectActivity implements OnClickListener {
            final /* synthetic */ File f2014a;
            final /* synthetic */ StorageSelectActivity f2015b;

            StorageSelectActivity(StorageSelectActivity storageSelectActivity, File file) {
                this.f2015b = storageSelectActivity;
                this.f2014a = file;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                edit.putString("selected_storage", this.f2014a.getAbsolutePath());
                edit.commit();
                ImageLoader.getInstance().checkMediaPaths();
                this.f2015b.f2017b.finishFragment();
            }
        }

        StorageSelectActivity(StorageSelectActivity storageSelectActivity, Context context) {
            this.f2017b = storageSelectActivity;
            this.f2016a = context;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            if (i >= 0 && i < this.f2017b.f2110f.size()) {
                File file = ((StorageSelectActivity) this.f2017b.f2110f.get(i)).f2026f;
                if (file == null) {
                    return;
                }
                if (MoboUtils.m1703a(file)) {
                    Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("moboconfig", 0).edit();
                    edit.putString("selected_storage", file.getAbsolutePath());
                    edit.commit();
                    ImageLoader.getInstance().checkMediaPaths();
                    this.f2017b.finishFragment();
                    return;
                }
                String absolutePath = file.getAbsolutePath();
                if (absolutePath.endsWith("/")) {
                    absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
                }
                File file2 = new File(absolutePath + "/Android/data/" + this.f2016a.getPackageName() + "/files/");
                if (VERSION.SDK_INT < 19 || file2 == null || !MoboUtils.m1703a(file2)) {
                    Toast.makeText(this.f2017b.getParentActivity(), LocaleController.getString("StorageIsntWritable", C0338R.string.StorageIsntWritable), 1).show();
                    return;
                }
                Builder builder = new Builder(this.f2017b.getParentActivity());
                builder.setMessage(LocaleController.formatString("StorageToExtFilesDirAlert", C0338R.string.StorageToExtFilesDirAlert, file2.getAbsolutePath()));
                builder.setTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0338R.string.OK), new StorageSelectActivity(this, file2));
                builder.setNegativeButton(LocaleController.getString("Cancel", C0338R.string.Cancel), null);
                this.f2017b.showDialog(builder.create());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.7 */
    class StorageSelectActivity implements OnPreDrawListener {
        final /* synthetic */ StorageSelectActivity f2018a;

        StorageSelectActivity(StorageSelectActivity storageSelectActivity) {
            this.f2018a = storageSelectActivity;
        }

        public boolean onPreDraw() {
            this.f2018a.f2105a.getViewTreeObserver().removeOnPreDrawListener(this);
            this.f2018a.m2034a();
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.a */
    private class StorageSelectActivity extends BaseFragmentAdapter {
        final /* synthetic */ StorageSelectActivity f2019a;
        private Context f2020b;

        public StorageSelectActivity(StorageSelectActivity storageSelectActivity, Context context) {
            this.f2019a = storageSelectActivity;
            this.f2020b = context;
        }

        public int getCount() {
            return this.f2019a.f2110f.size();
        }

        public Object getItem(int i) {
            return this.f2019a.f2110f.get(i);
        }

        public long getItemId(int i) {
            return 0;
        }

        public int getItemViewType(int i) {
            return ((StorageSelectActivity) this.f2019a.f2110f.get(i)).f2023c.length() > 0 ? 0 : 1;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
            View sharedDocumentCell = view == null ? new SharedDocumentCell(this.f2020b) : view;
            SharedDocumentCell sharedDocumentCell2 = (SharedDocumentCell) sharedDocumentCell;
            StorageSelectActivity storageSelectActivity = (StorageSelectActivity) this.f2019a.f2110f.get(i);
            File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            if (externalFilesDir == null || !MoboConstants.m1381b().equals(externalFilesDir.getAbsoluteFile()) || storageSelectActivity.f2026f == null || !externalFilesDir.getAbsolutePath().startsWith(storageSelectActivity.f2026f.getAbsolutePath())) {
                boolean z2 = storageSelectActivity.f2026f != null && storageSelectActivity.f2026f.equals(MoboConstants.m1381b());
                if (this.f2019a.f2115k) {
                    z = false;
                }
                sharedDocumentCell2.setChecked(z2, z);
            } else {
                sharedDocumentCell2.setChecked(true, !this.f2019a.f2115k);
                storageSelectActivity.f2023c = "(" + externalFilesDir.getAbsolutePath() + ")";
            }
            if (storageSelectActivity.f2021a != 0) {
                ((SharedDocumentCell) sharedDocumentCell).setTextAndValueAndTypeAndThumb(storageSelectActivity.f2022b, storageSelectActivity.f2023c, null, null, storageSelectActivity.f2021a);
            } else {
                ((SharedDocumentCell) sharedDocumentCell).setTextAndValueAndTypeAndThumb(storageSelectActivity.f2022b, storageSelectActivity.f2023c, storageSelectActivity.f2024d.toUpperCase().substring(0, Math.min(storageSelectActivity.f2024d.length(), 4)), storageSelectActivity.f2025e, 0);
            }
            return sharedDocumentCell;
        }

        public int getViewTypeCount() {
            return 2;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.p.b */
    private class StorageSelectActivity {
        int f2021a;
        String f2022b;
        String f2023c;
        String f2024d;
        String f2025e;
        File f2026f;
        final /* synthetic */ StorageSelectActivity f2027g;

        private StorageSelectActivity(StorageSelectActivity storageSelectActivity) {
            this.f2027g = storageSelectActivity;
            this.f2023c = TtmlNode.ANONYMOUS_REGION_ID;
            this.f2024d = TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    static {
        f2104m = Pattern.compile("/");
    }

    public StorageSelectActivity() {
        this.f2110f = new ArrayList();
        this.f2111g = false;
        this.f2112h = new ArrayList();
        this.f2113i = 1610612736;
        this.f2114j = new ArrayList();
        this.f2116l = new StorageSelectActivity(this);
    }

    private String m2033a(String str) {
        try {
            StatFs statFs = new StatFs(str);
            long blockCount = ((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize());
            long blockSize = ((long) statFs.getBlockSize()) * ((long) statFs.getAvailableBlocks());
            if (blockCount == 0) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            return LocaleController.formatString("FreeOfTotal", C0338R.string.FreeOfTotal, AndroidUtilities.formatFileSize(blockSize), AndroidUtilities.formatFileSize(blockCount));
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return str;
        }
    }

    private void m2034a() {
        if (this.f2107c != null) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.f2107c.setTextSize(20);
            } else {
                this.f2107c.setTextSize(18);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.SuppressLint({"NewApi"})
    private void m2036b() {
        /*
        r7 = this;
        r6 = 2131166207; // 0x7f0703ff float:1.7946653E38 double:1.0529360085E-314;
        r5 = 2130837836; // 0x7f02014c float:1.7280637E38 double:1.0527737716E-314;
        r2 = 0;
        r7.f2109e = r2;
        r0 = r7.f2110f;
        r0.clear();
        r3 = new java.util.HashSet;
        r3.<init>();
        r0 = android.os.Environment.getExternalStorageDirectory();
        r0 = r0.getPath();
        r1 = android.os.Build.VERSION.SDK_INT;
        r4 = 9;
        if (r1 < r4) goto L_0x0027;
    L_0x0021:
        r1 = android.os.Environment.isExternalStorageRemovable();
        if (r1 == 0) goto L_0x0027;
    L_0x0027:
        r1 = android.os.Environment.getExternalStorageState();
        r4 = "mounted";
        r4 = r1.equals(r4);
        if (r4 != 0) goto L_0x003d;
    L_0x0034:
        r4 = "mounted_ro";
        r1 = r1.equals(r4);
        if (r1 == 0) goto L_0x0067;
    L_0x003d:
        r1 = new com.hanista.mobogram.mobo.p$b;
        r1.<init>(r2);
        r4 = android.os.Environment.isExternalStorageRemovable();
        if (r4 == 0) goto L_0x0174;
    L_0x0048:
        r4 = "SdCard";
        r4 = com.hanista.mobogram.messenger.LocaleController.getString(r4, r6);
        r1.f2022b = r4;
        r1.f2021a = r5;
    L_0x0053:
        r4 = r7.m2033a(r0);
        r1.f2023c = r4;
        r4 = android.os.Environment.getExternalStorageDirectory();
        r1.f2026f = r4;
        r4 = r7.f2110f;
        r4.add(r1);
        r3.add(r0);
    L_0x0067:
        r1 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x01bc, all -> 0x01b9 }
        r0 = new java.io.FileReader;	 Catch:{ Exception -> 0x01bc, all -> 0x01b9 }
        r4 = "/proc/mounts";
        r0.<init>(r4);	 Catch:{ Exception -> 0x01bc, all -> 0x01b9 }
        r1.<init>(r0);	 Catch:{ Exception -> 0x01bc, all -> 0x01b9 }
    L_0x0074:
        r0 = r1.readLine();	 Catch:{ Exception -> 0x015a }
        if (r0 == 0) goto L_0x019b;
    L_0x007a:
        r2 = "vfat";
        r2 = r0.contains(r2);	 Catch:{ Exception -> 0x015a }
        if (r2 != 0) goto L_0x008c;
    L_0x0083:
        r2 = "/mnt";
        r2 = r0.contains(r2);	 Catch:{ Exception -> 0x015a }
        if (r2 == 0) goto L_0x0074;
    L_0x008c:
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m16e(r2, r0);	 Catch:{ Exception -> 0x015a }
        r2 = new java.util.StringTokenizer;	 Catch:{ Exception -> 0x015a }
        r4 = " ";
        r2.<init>(r0, r4);	 Catch:{ Exception -> 0x015a }
        r2.nextToken();	 Catch:{ Exception -> 0x015a }
        r2 = r2.nextToken();	 Catch:{ Exception -> 0x015a }
        r4 = r3.contains(r2);	 Catch:{ Exception -> 0x015a }
        if (r4 != 0) goto L_0x0074;
    L_0x00a7:
        r4 = "/dev/block/vold";
        r4 = r0.contains(r4);	 Catch:{ Exception -> 0x015a }
        if (r4 == 0) goto L_0x0074;
    L_0x00b0:
        r4 = "/mnt/secure";
        r4 = r0.contains(r4);	 Catch:{ Exception -> 0x015a }
        if (r4 != 0) goto L_0x0074;
    L_0x00b9:
        r4 = "/mnt/asec";
        r4 = r0.contains(r4);	 Catch:{ Exception -> 0x015a }
        if (r4 != 0) goto L_0x0074;
    L_0x00c2:
        r4 = "/mnt/obb";
        r4 = r0.contains(r4);	 Catch:{ Exception -> 0x015a }
        if (r4 != 0) goto L_0x0074;
    L_0x00cb:
        r4 = "/dev/mapper";
        r4 = r0.contains(r4);	 Catch:{ Exception -> 0x015a }
        if (r4 != 0) goto L_0x0074;
    L_0x00d4:
        r4 = "tmpfs";
        r0 = r0.contains(r4);	 Catch:{ Exception -> 0x015a }
        if (r0 != 0) goto L_0x0074;
    L_0x00dd:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x015a }
        r0.<init>(r2);	 Catch:{ Exception -> 0x015a }
        r0 = r0.isDirectory();	 Catch:{ Exception -> 0x015a }
        if (r0 != 0) goto L_0x01bf;
    L_0x00e8:
        r0 = 47;
        r0 = r2.lastIndexOf(r0);	 Catch:{ Exception -> 0x015a }
        r4 = -1;
        if (r0 == r4) goto L_0x01bf;
    L_0x00f1:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x015a }
        r4.<init>();	 Catch:{ Exception -> 0x015a }
        r5 = "/storage/";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x015a }
        r0 = r0 + 1;
        r0 = r2.substring(r0);	 Catch:{ Exception -> 0x015a }
        r0 = r4.append(r0);	 Catch:{ Exception -> 0x015a }
        r0 = r0.toString();	 Catch:{ Exception -> 0x015a }
        r4 = new java.io.File;	 Catch:{ Exception -> 0x015a }
        r4.<init>(r0);	 Catch:{ Exception -> 0x015a }
        r4 = r4.isDirectory();	 Catch:{ Exception -> 0x015a }
        if (r4 == 0) goto L_0x01bf;
    L_0x0116:
        r3.add(r0);	 Catch:{ Exception -> 0x015a }
        r2 = new com.hanista.mobogram.mobo.p$b;	 Catch:{ Exception -> 0x0151 }
        r4 = 0;
        r2.<init>(r4);	 Catch:{ Exception -> 0x0151 }
        r4 = r0.toLowerCase();	 Catch:{ Exception -> 0x0151 }
        r5 = "sd";
        r4 = r4.contains(r5);	 Catch:{ Exception -> 0x0151 }
        if (r4 == 0) goto L_0x0187;
    L_0x012c:
        r4 = "SdCard";
        r5 = 2131166207; // 0x7f0703ff float:1.7946653E38 double:1.0529360085E-314;
        r4 = com.hanista.mobogram.messenger.LocaleController.getString(r4, r5);	 Catch:{ Exception -> 0x0151 }
        r2.f2022b = r4;	 Catch:{ Exception -> 0x0151 }
    L_0x0138:
        r4 = 2130837836; // 0x7f02014c float:1.7280637E38 double:1.0527737716E-314;
        r2.f2021a = r4;	 Catch:{ Exception -> 0x0151 }
        r4 = r7.m2033a(r0);	 Catch:{ Exception -> 0x0151 }
        r2.f2023c = r4;	 Catch:{ Exception -> 0x0151 }
        r4 = new java.io.File;	 Catch:{ Exception -> 0x0151 }
        r4.<init>(r0);	 Catch:{ Exception -> 0x0151 }
        r2.f2026f = r4;	 Catch:{ Exception -> 0x0151 }
        r0 = r7.f2110f;	 Catch:{ Exception -> 0x0151 }
        r0.add(r2);	 Catch:{ Exception -> 0x0151 }
        goto L_0x0074;
    L_0x0151:
        r0 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r0);	 Catch:{ Exception -> 0x015a }
        goto L_0x0074;
    L_0x015a:
        r0 = move-exception;
    L_0x015b:
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r0);	 Catch:{ all -> 0x0194 }
        if (r1 == 0) goto L_0x0166;
    L_0x0163:
        r1.close();	 Catch:{ Exception -> 0x01a9 }
    L_0x0166:
        r0 = r7.f2105a;
        com.hanista.mobogram.messenger.AndroidUtilities.clearDrawableAnimation(r0);
        r0 = 1;
        r7.f2115k = r0;
        r0 = r7.f2106b;
        r0.notifyDataSetChanged();
        return;
    L_0x0174:
        r4 = "InternalStorage";
        r5 = 2131165755; // 0x7f07023b float:1.7945736E38 double:1.052935785E-314;
        r4 = com.hanista.mobogram.messenger.LocaleController.getString(r4, r5);
        r1.f2022b = r4;
        r4 = 2130837997; // 0x7f0201ed float:1.7280964E38 double:1.052773851E-314;
        r1.f2021a = r4;
        goto L_0x0053;
    L_0x0187:
        r4 = "ExternalStorage";
        r5 = 2131165631; // 0x7f0701bf float:1.7945485E38 double:1.052935724E-314;
        r4 = com.hanista.mobogram.messenger.LocaleController.getString(r4, r5);	 Catch:{ Exception -> 0x0151 }
        r2.f2022b = r4;	 Catch:{ Exception -> 0x0151 }
        goto L_0x0138;
    L_0x0194:
        r0 = move-exception;
    L_0x0195:
        if (r1 == 0) goto L_0x019a;
    L_0x0197:
        r1.close();	 Catch:{ Exception -> 0x01b1 }
    L_0x019a:
        throw r0;
    L_0x019b:
        if (r1 == 0) goto L_0x0166;
    L_0x019d:
        r1.close();	 Catch:{ Exception -> 0x01a1 }
        goto L_0x0166;
    L_0x01a1:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x0166;
    L_0x01a9:
        r0 = move-exception;
        r1 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r1, r0);
        goto L_0x0166;
    L_0x01b1:
        r1 = move-exception;
        r2 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r2, r1);
        goto L_0x019a;
    L_0x01b9:
        r0 = move-exception;
        r1 = r2;
        goto L_0x0195;
    L_0x01bc:
        r0 = move-exception;
        r1 = r2;
        goto L_0x015b;
    L_0x01bf:
        r0 = r2;
        goto L_0x0116;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.mobo.p.b():void");
    }

    private void m2039c() {
        if (ThemeUtil.m2490b()) {
            Drawable backDrawable = new BackDrawable(false);
            ((BackDrawable) backDrawable).setColor(AdvanceTheme.bi);
            this.actionBar.setBackgroundColor(AdvanceTheme.f2491b);
            this.actionBar.setBackButtonDrawable(backDrawable);
        }
    }

    public View createView(Context context) {
        if (!this.f2111g) {
            this.f2111g = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
            intentFilter.addAction("android.intent.action.MEDIA_EJECT");
            intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
            intentFilter.addAction("android.intent.action.MEDIA_NOFS");
            intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
            intentFilter.addAction("android.intent.action.MEDIA_SHARED");
            intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
            intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            intentFilter.addDataScheme("file");
            ApplicationLoader.applicationContext.registerReceiver(this.f2116l, intentFilter);
        }
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        m2039c();
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("DefaultStorage", C0338R.string.DefaultStorage));
        this.actionBar.setActionBarMenuOnItemClick(new StorageSelectActivity(this));
        this.f2114j.clear();
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        this.f2107c = new NumberTextView(createActionMode.getContext());
        this.f2107c.setTextSize(18);
        this.f2107c.setTypeface(FontUtil.m1176a().m1160c());
        this.f2107c.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        this.f2107c.setOnTouchListener(new StorageSelectActivity(this));
        createActionMode.addView(this.f2107c, LayoutHelper.createLinear(0, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 65, 0, 0, 0));
        this.f2114j.add(createActionMode.addItem(3, C0338R.drawable.ic_ab_done_gray, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.fragmentView = getParentActivity().getLayoutInflater().inflate(C0338R.layout.document_select_layout, null, false);
        this.f2106b = new StorageSelectActivity(this, context);
        this.f2108d = (TextView) this.fragmentView.findViewById(C0338R.id.searchEmptyView);
        this.f2108d.setOnTouchListener(new StorageSelectActivity(this));
        this.f2105a = (ListView) this.fragmentView.findViewById(C0338R.id.listView);
        this.f2105a.setEmptyView(this.f2108d);
        this.f2105a.setAdapter(this.f2106b);
        this.f2105a.setOnScrollListener(new StorageSelectActivity(this));
        this.f2105a.setOnItemClickListener(new StorageSelectActivity(this, context));
        m2036b();
        return this.fragmentView;
    }

    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.f2105a != null) {
            this.f2105a.getViewTreeObserver().addOnPreDrawListener(new StorageSelectActivity(this));
        }
    }

    public void onFragmentDestroy() {
        try {
            if (this.f2111g) {
                ApplicationLoader.applicationContext.unregisterReceiver(this.f2116l);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        if (this.f2106b != null) {
            this.f2106b.notifyDataSetChanged();
        }
        m2034a();
    }
}

package com.hanista.mobogram.mobo.lock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.lock.PatternView.C0911a;
import com.hanista.mobogram.mobo.lock.PatternView.C0912b;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import com.hanista.mobogram.ui.ActionBar.BaseFragment;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import com.hanista.mobogram.ui.PasscodeActivity;
import java.io.Serializable;

/* renamed from: com.hanista.mobogram.mobo.lock.a */
public class LockActivity extends BaseFragment implements OnClickListener {
    public static final String f1510a;
    public static final String f1511b;
    public static final String f1512c;
    private static final String f1513e;
    private static final String f1514f;
    private static final String f1515g;
    private View f1516A;
    private ImageView f1517B;
    private TextView f1518C;
    private TextView f1519D;
    private LockPreferences f1520E;
    private LockActivity f1521d;
    private long f1522h;
    private long f1523i;
    private String f1524j;
    private Animation f1525k;
    private Animation f1526l;
    private ImageView f1527m;
    private RelativeLayout f1528n;
    private LinearLayout f1529o;
    private Intent f1530p;
    private LayoutParams f1531q;
    private Button f1532r;
    private LockActivity f1533s;
    private PatternView f1534t;
    private ViewGroup f1535u;
    private String f1536v;
    private String f1537w;
    private C0912b f1538x;
    private Button f1539y;
    private LockActivity f1540z;

    /* renamed from: com.hanista.mobogram.mobo.lock.a.1 */
    class LockActivity extends ActionBarMenuOnItemClick {
        final /* synthetic */ LockActivity f1496a;

        LockActivity(LockActivity lockActivity) {
            this.f1496a = lockActivity;
        }

        public void onItemClick(int i) {
            if (i == -1) {
                this.f1496a.finishFragment();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.a.a */
    private static abstract class LockActivity implements AnimationListener {
        private LockActivity() {
        }

        public void onAnimationEnd(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.a.2 */
    class LockActivity extends LockActivity {
        final /* synthetic */ LockActivity f1497a;

        LockActivity(LockActivity lockActivity) {
            this.f1497a = lockActivity;
            super();
        }

        public void onAnimationEnd(Animation animation) {
            this.f1497a.m1546p();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.a.b */
    private enum LockActivity {
        BACK,
        CANCEL
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.a.c */
    private class LockActivity implements C0912b {
        final /* synthetic */ LockActivity f1501a;

        private LockActivity(LockActivity lockActivity) {
            this.f1501a = lockActivity;
        }

        public void m1517a() {
        }

        public void m1518b() {
        }

        public void m1519c() {
            if (LockActivity.f1510a.equals(this.f1501a.f1524j)) {
                this.f1501a.m1527b();
            } else if (LockActivity.f1511b.equals(this.f1501a.f1524j)) {
                this.f1501a.f1518C.setText(C0338R.string.pattern_detected);
            }
        }

        public void m1520d() {
            this.f1501a.f1522h = System.nanoTime();
            this.f1501a.f1534t.m1515d();
            this.f1501a.f1534t.setDisplayMode(C0911a.Correct);
            if (!LockActivity.f1511b.equals(this.f1501a.f1524j)) {
                return;
            }
            if (this.f1501a.f1540z == LockActivity.CONTINUE) {
                this.f1501a.f1518C.setText(C0338R.string.pattern_change_head);
            } else {
                this.f1501a.f1518C.setText(C0338R.string.pattern_change_confirm);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.a.d */
    private enum LockActivity {
        CONFIRM,
        CONTINUE
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.a.e */
    private enum LockActivity {
        SHOWING,
        SHOWN,
        HIDING,
        HIDDEN
    }

    static {
        f1513e = LockActivity.class.getName();
        f1510a = f1513e + ".action.compare";
        f1511b = f1513e + ".action.create";
        f1512c = f1513e + ".extra.target_packagename";
        f1514f = f1513e + ".extra.options";
        f1515g = LockActivity.class.getSimpleName();
    }

    public LockActivity(int i) {
        this.f1521d = LockActivity.HIDDEN;
        if (i == 1) {
            this.f1530p = new Intent(ApplicationLoader.applicationContext, LockActivity.class);
            this.f1530p.setAction(f1511b);
            Serializable lockPreferences = new LockPreferences(ApplicationLoader.applicationContext);
            lockPreferences.f1541a = 2;
            this.f1530p.putExtra(f1514f, lockPreferences);
        } else if (i == 2) {
            this.f1530p = LockActivity.m1523a(ApplicationLoader.applicationContext, ApplicationLoader.applicationContext.getPackageName());
        }
    }

    public LockActivity(int i, int i2) {
        this.f1521d = LockActivity.HIDDEN;
        if (i == 1) {
            this.f1530p = new Intent(ApplicationLoader.applicationContext, LockActivity.class);
            this.f1530p.setAction(f1511b);
            Serializable lockPreferences = new LockPreferences(ApplicationLoader.applicationContext);
            lockPreferences.f1541a = 2;
            lockPreferences.f1545e = i2;
            this.f1530p.putExtra(f1514f, lockPreferences);
        } else if (i == 2) {
            this.f1530p = LockActivity.m1523a(ApplicationLoader.applicationContext, ApplicationLoader.applicationContext.getPackageName());
        }
    }

    @SuppressLint({"InlinedApi"})
    private static int m1521a() {
        return VERSION.SDK_INT < 9 ? 0 : 6;
    }

    public static Intent m1523a(Context context, String str) {
        Intent intent = new Intent(context, LockActivity.class);
        intent.setAction(f1510a);
        intent.putExtra(f1512c, str);
        return intent;
    }

    @SuppressLint({"InflateParams"})
    private void m1525a(int i, int i2) {
        Toast.makeText(getParentActivity(), i, i2).show();
    }

    private boolean m1526a(String str, String str2) {
        return (str != null || str2 == null) ? (str == null || str2 != null) ? str.equals(str2) || MoboUtils.m1713c(str).equals(MoboUtils.m1713c(str2)) : false : false;
    }

    private void m1527b() {
        if (m1526a(this.f1534t.getPatternString(), this.f1520E.f1553m)) {
            presentFragment(new PasscodeActivity(0), true);
        } else if (this.f1520E.f1555o) {
            m1525a((int) C0338R.string.locker_invalid_pattern, 0);
            this.f1534t.m1514c();
        } else {
            this.f1534t.setDisplayMode(C0911a.Wrong);
            this.f1534t.m1511a(600);
        }
    }

    private void m1530c() {
        if (this.f1520E.f1541a == 2) {
            m1532d();
        }
    }

    private void m1532d() {
        Object patternString = this.f1534t.getPatternString();
        if (patternString.equals(this.f1536v)) {
            PrefUtils prefUtils = new PrefUtils(getParentActivity());
            prefUtils.m1579a((int) C0338R.string.pref_key_pattern, patternString);
            prefUtils.m1578a((int) C0338R.string.pref_key_lock_type, (int) C0338R.string.pref_val_lock_type_pattern);
            prefUtils.m1579a((int) C0338R.string.pref_key_pattern_size, String.valueOf(this.f1520E.f1545e));
            prefUtils.m1583b();
            try {
                UserConfig.passcodeSalt = new byte[16];
                Utilities.random.nextBytes(UserConfig.passcodeSalt);
                patternString = patternString.getBytes(C0700C.UTF8_NAME);
                Object obj = new byte[(patternString.length + 32)];
                System.arraycopy(UserConfig.passcodeSalt, 0, obj, 0, 16);
                System.arraycopy(patternString, 0, obj, 16, patternString.length);
                System.arraycopy(UserConfig.passcodeSalt, 0, obj, patternString.length + 16, 16);
                UserConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(obj, 0, obj.length));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            UserConfig.passcodeType = 2;
            UserConfig.saveConfig(false);
            finishFragment();
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
            return;
        }
        m1525a((int) C0338R.string.pattern_change_not_match, 0);
        this.f1534t.setDisplayMode(C0911a.Wrong);
        m1541k();
    }

    private int m1533e() {
        return getParentActivity().getString(C0338R.string.pref_val_orientation_portrait).equals(this.f1520E.f1542b) ? 1 : getParentActivity().getString(C0338R.string.pref_val_orientation_landscape).equals(this.f1520E.f1542b) ? LockActivity.m1521a() : getParentActivity().getString(C0338R.string.pref_val_orientation_auto_rotate).equals(this.f1520E.f1542b) ? 4 : -1;
    }

    private void m1535f() {
        if (this.f1521d == LockActivity.HIDING) {
            this.f1525k.setAnimationListener(null);
            this.f1525k.cancel();
            this.f1525k = null;
        } else if (this.f1521d == LockActivity.SHOWING) {
            this.f1526l.setAnimationListener(null);
            this.f1526l.cancel();
            this.f1526l = null;
        }
    }

    @SuppressLint({"InflateParams"})
    private View m1537g() {
        View inflate = LayoutInflater.from(getParentActivity()).inflate(C0338R.layout.layout_alias_locker, null);
        this.f1528n = (RelativeLayout) inflate.findViewById(C0338R.id.lock_container);
        this.f1517B = (ImageView) inflate.findViewById(C0338R.id.lock_iv_background);
        inflate.setFocusable(true);
        inflate.setFocusableInTouchMode(true);
        this.f1519D = (TextView) inflate.findViewById(C0338R.id.lock_tv_title);
        this.f1518C = (TextView) inflate.findViewById(C0338R.id.lock_tv_footer);
        this.f1527m = (ImageView) inflate.findViewById(C0338R.id.lock_iv_app_icon);
        this.f1535u = (ViewGroup) inflate.findViewById(C0338R.id.lock_lockview);
        this.f1529o = (LinearLayout) inflate.findViewById(C0338R.id.lock_footer_buttons);
        this.f1532r = (Button) inflate.findViewById(C0338R.id.lock_footer_b_left);
        this.f1539y = (Button) inflate.findViewById(C0338R.id.lock_footer_b_right);
        this.f1539y.setOnClickListener(this);
        this.f1532r.setOnClickListener(this);
        this.f1538x = new LockActivity();
        return inflate;
    }

    private void m1538h() {
        m1540j();
        this.f1516A.findViewById(C0338R.id.lock_ad_container).setVisibility(8);
        switch (this.f1520E.f1541a) {
            case VideoPlayer.STATE_PREPARING /*2*/:
                m1543m();
                break;
        }
        if (f1510a.equals(this.f1524j)) {
            this.f1527m.setVisibility(0);
            this.f1529o.setVisibility(8);
            ApplicationInfo a = MoboUtils.m1693a(this.f1537w, getParentActivity());
            if (a != null) {
                CharSequence charSequence = a.loadLabel(getParentActivity().getPackageManager()).toString();
                MoboUtils.m1701a(this.f1527m, a.loadIcon(getParentActivity().getPackageManager()));
                this.f1519D.setText(charSequence);
                if (this.f1520E.f1544d == null || this.f1520E.f1544d.length() == 0) {
                    this.f1518C.setVisibility(8);
                    return;
                }
                this.f1518C.setVisibility(0);
                this.f1518C.setText(this.f1520E.f1544d.replace("%s", charSequence));
                return;
            }
            this.f1527m.setVisibility(8);
        } else if (f1511b.equals(this.f1524j)) {
            this.f1527m.setVisibility(8);
            this.f1529o.setVisibility(0);
            m1541k();
        }
    }

    private boolean m1539i() {
        if (this.f1530p == null) {
            return false;
        }
        this.f1524j = this.f1530p.getAction();
        if (this.f1524j == null) {
            Log.w(f1515g, "Finishing: No action specified");
            return false;
        }
        if (this.f1530p.hasExtra(f1514f)) {
            this.f1520E = (LockPreferences) this.f1530p.getSerializableExtra(f1514f);
        } else {
            this.f1520E = new LockPreferences(getParentActivity());
        }
        this.f1537w = this.f1530p.getStringExtra(f1512c);
        if (f1511b.equals(this.f1524j)) {
            this.f1520E.f1554n = false;
        }
        this.f1531q = new LayoutParams(-1, -1, 2002, 394272, -3);
        this.f1531q.screenOrientation = m1533e();
        return true;
    }

    private void m1540j() {
        MoboUtils.m1701a(this.f1517B, ApplicationLoader.getCachedWallpaper());
    }

    private void m1541k() {
        if (this.f1520E.f1541a == 2) {
            this.f1534t.setInStealthMode(false);
            this.f1534t.m1511a(600);
            this.f1519D.setText(C0338R.string.pattern_change_tit);
            this.f1518C.setText(C0338R.string.pattern_change_head);
            this.f1536v = null;
        }
        this.f1532r.setText(C0338R.string.cancel);
        this.f1539y.setText(C0338R.string.button_continue);
        this.f1533s = LockActivity.CANCEL;
        this.f1540z = LockActivity.CONTINUE;
    }

    private void m1542l() {
        if (this.f1520E.f1541a == 2) {
            this.f1536v = this.f1534t.getPatternString();
            if (this.f1536v.length() != 0) {
                this.f1518C.setText(C0338R.string.pattern_change_confirm);
                this.f1534t.m1514c();
            } else {
                return;
            }
        }
        this.f1532r.setText(C0338R.string.button_back);
        this.f1539y.setText(C0338R.string.button_confirm);
        this.f1533s = LockActivity.BACK;
        this.f1540z = LockActivity.CONFIRM;
    }

    private boolean m1543m() {
        this.f1535u.removeAllViews();
        LayoutInflater.from(getParentActivity()).inflate(C0338R.layout.view_lock_pattern, this.f1535u, true);
        this.f1534t = (PatternView) this.f1535u.findViewById(C0338R.id.patternView);
        this.f1534t.setOnPatternListener(this.f1538x);
        this.f1534t.setSelectedBitmap(this.f1520E.f1556p);
        MoboUtils.m1701a(this.f1534t, getParentActivity().getResources().getDrawable(C0338R.drawable.passwordview_button_background));
        this.f1534t.setSize(this.f1520E.f1545e);
        this.f1534t.setTactileFeedbackEnabled(this.f1520E.f1543c.booleanValue());
        this.f1534t.setInStealthMode(this.f1520E.f1554n);
        this.f1534t.setInErrorStealthMode(this.f1520E.f1555o);
        this.f1534t.m1516e();
        this.f1534t.setVisibility(0);
        this.f1520E.f1541a = 2;
        return true;
    }

    private void m1544n() {
        if (this.f1521d == LockActivity.HIDING || this.f1521d == LockActivity.SHOWING) {
            m1535f();
        }
        m1539i();
        this.f1516A = m1537g();
        m1538h();
        this.f1521d = LockActivity.SHOWING;
        m1545o();
    }

    private void m1545o() {
        if (this.f1520E.f1547g == 0 || this.f1520E.f1549i == 0) {
            m1546p();
            return;
        }
        this.f1526l = AnimationUtils.loadAnimation(getParentActivity(), this.f1520E.f1547g);
        this.f1526l.setAnimationListener(new LockActivity(this));
        this.f1526l.setDuration((long) this.f1520E.f1549i);
        this.f1526l.setFillEnabled(true);
        this.f1528n.startAnimation(this.f1526l);
    }

    private void m1546p() {
        this.f1523i = System.nanoTime();
        this.f1521d = LockActivity.SHOWN;
        this.f1526l = null;
    }

    public View createView(Context context) {
        if (ThemeUtil.m2490b()) {
            Drawable drawable = getParentActivity().getResources().getDrawable(C0338R.drawable.ic_ab_back);
            drawable.setColorFilter(AdvanceTheme.ba, Mode.MULTIPLY);
            this.actionBar.setBackButtonDrawable(drawable);
        } else {
            this.actionBar.setBackButtonImage(C0338R.drawable.ic_ab_back);
        }
        this.actionBar.setVisibility(8);
        this.actionBar.setActionBarMenuOnItemClick(new LockActivity(this));
        m1544n();
        this.fragmentView = this.f1516A;
        return this.fragmentView;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case C0338R.id.lock_footer_b_left /*2131558402*/:
                if (!f1511b.equals(this.f1524j)) {
                    return;
                }
                if (this.f1533s == LockActivity.BACK) {
                    m1541k();
                } else {
                    finishFragment();
                }
            case C0338R.id.lock_footer_b_right /*2131558403*/:
                if (!f1511b.equals(this.f1524j)) {
                    return;
                }
                if (this.f1540z == LockActivity.CONTINUE) {
                    m1542l();
                } else {
                    m1530c();
                }
            default:
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        Log.d(f1515g, "onConfigChange");
        if (this.f1521d == LockActivity.SHOWING || this.f1521d == LockActivity.SHOWN) {
            m1544n();
        }
    }

    public boolean onFragmentCreate() {
        this.swipeBackEnabled = false;
        return super.onFragmentCreate();
    }
}

package com.hanista.mobogram.mobo.lock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.lock.PatternView.C0911a;
import com.hanista.mobogram.mobo.lock.PatternView.C0912b;
import com.hanista.mobogram.ui.Components.VideoPlayer;

/* renamed from: com.hanista.mobogram.mobo.lock.c */
public class PatternFrame extends FrameLayout {
    public static final String f1572a;
    public static final String f1573b;
    public static final String f1574c;
    public static final String f1575d;
    private static final String f1576g;
    private static final String f1577h;
    private static final String f1578i;
    private static final String f1579j;
    private PatternFrame f1580A;
    private View f1581B;
    private TextView f1582C;
    private TextView f1583D;
    private LockPreferences f1584E;
    private PatternFrame f1585e;
    private PatternFrame f1586f;
    private long f1587k;
    private long f1588l;
    private String f1589m;
    private Animation f1590n;
    private Animation f1591o;
    private ImageView f1592p;
    private RelativeLayout f1593q;
    private LinearLayout f1594r;
    private Intent f1595s;
    private LayoutParams f1596t;
    private PatternFrame f1597u;
    private PatternView f1598v;
    private ViewGroup f1599w;
    private String f1600x;
    private String f1601y;
    private C0912b f1602z;

    /* renamed from: com.hanista.mobogram.mobo.lock.c.a */
    private static abstract class PatternFrame implements AnimationListener {
        public void onAnimationEnd(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.c.1 */
    class PatternFrame extends PatternFrame {

        /* renamed from: com.hanista.mobogram.mobo.lock.c.1.1 */
        class PatternFrame implements Runnable {
            final /* synthetic */ PatternFrame f1559a;

            PatternFrame(PatternFrame patternFrame) {
                this.f1559a = patternFrame;
            }

            public void run() {
            }
        }

        public void onAnimationEnd(Animation animation) {
            new Handler().post(new PatternFrame(this));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.c.b */
    private enum PatternFrame {
        BACK,
        CANCEL
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.c.c */
    private class PatternFrame implements C0912b {
        final /* synthetic */ PatternFrame f1563a;

        private PatternFrame(PatternFrame patternFrame) {
            this.f1563a = patternFrame;
        }

        public void m1549a() {
        }

        public void m1550b() {
        }

        public void m1551c() {
            if (PatternFrame.f1572a.equals(this.f1563a.f1589m)) {
                this.f1563a.m1558b();
            }
        }

        public void m1552d() {
            this.f1563a.f1587k = System.nanoTime();
            this.f1563a.f1598v.m1515d();
            this.f1563a.f1598v.setDisplayMode(C0911a.Correct);
            if (!PatternFrame.f1573b.equals(this.f1563a.f1589m)) {
                return;
            }
            if (this.f1563a.f1580A == PatternFrame.CONTINUE) {
                this.f1563a.f1582C.setText(C0338R.string.pattern_change_head);
            } else {
                this.f1563a.f1582C.setText(C0338R.string.pattern_change_confirm);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.c.d */
    public interface PatternFrame {
        void didAcceptedPattern(String str);
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.c.e */
    private enum PatternFrame {
        CONFIRM,
        CONTINUE
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.c.f */
    private enum PatternFrame {
        SHOWING,
        SHOWN,
        HIDING,
        HIDDEN
    }

    static {
        f1576g = PatternFrame.class.getName();
        f1572a = f1576g + ".action.compare";
        f1573b = f1576g + ".action.create";
        f1574c = f1576g + ".action.extra_lock";
        f1575d = f1576g + ".extra.target_packagename";
        f1577h = f1576g + ".extra.options";
        f1578i = f1576g + ".action.hide";
        f1579j = PatternFrame.class.getSimpleName();
    }

    public PatternFrame(Context context) {
        super(context);
        this.f1586f = PatternFrame.HIDDEN;
        m1573a();
    }

    @SuppressLint({"InflateParams"})
    private void m1555a(int i, int i2) {
        Toast.makeText(getContext(), i, i2).show();
    }

    private void m1556a(boolean z) {
        m1563d();
    }

    private boolean m1557a(String str, String str2) {
        return (str != null || str2 == null) ? (str == null || str2 != null) ? str.equals(str2) || MoboUtils.m1713c(str).equals(MoboUtils.m1713c(str2)) : false : false;
    }

    private void m1558b() {
        String patternString = this.f1598v.getPatternString();
        if (m1557a(patternString, this.f1584E.f1553m) || this.f1584E.f1553m == null) {
            m1561c();
            if (this.f1585e != null) {
                this.f1585e.didAcceptedPattern(patternString);
            }
            this.f1598v.m1514c();
        } else if (this.f1584E.f1555o) {
            m1555a((int) C0338R.string.locker_invalid_pattern, 0);
            this.f1598v.m1514c();
        } else {
            this.f1598v.setDisplayMode(C0911a.Wrong);
            this.f1598v.m1511a(600);
        }
    }

    private void m1561c() {
        long nanoTime = System.nanoTime();
        long j = (nanoTime - this.f1588l) / C0700C.MICROS_PER_SECOND;
        Log.d(f1579j, "Time in screen: " + j + ", interacting:" + ((nanoTime - this.f1587k) / C0700C.MICROS_PER_SECOND));
        if (this.f1601y == null || this.f1601y.equals(getContext().getPackageName())) {
            m1556a(true);
        } else {
            m1556a(true);
        }
    }

    private void m1563d() {
        if (this.f1586f == PatternFrame.HIDING || this.f1586f == PatternFrame.HIDDEN) {
            Log.w(f1579j, "called hideView not hiding (mViewState=" + this.f1586f + ")");
            return;
        }
        if (this.f1586f == PatternFrame.SHOWING) {
            m1565e();
        }
        this.f1586f = PatternFrame.HIDING;
    }

    private void m1565e() {
        if (this.f1586f == PatternFrame.HIDING) {
            this.f1590n.setAnimationListener(null);
            this.f1590n.cancel();
            this.f1590n = null;
        } else if (this.f1586f == PatternFrame.SHOWING) {
            this.f1591o.setAnimationListener(null);
            this.f1591o.cancel();
            this.f1591o = null;
        }
    }

    @SuppressLint({"InflateParams"})
    private View m1566f() {
        View inflate = LayoutInflater.from(getContext()).inflate(C0338R.layout.layout_alias_locker, null);
        this.f1593q = (RelativeLayout) inflate.findViewById(C0338R.id.lock_container);
        inflate.setFocusable(true);
        inflate.setFocusableInTouchMode(true);
        this.f1583D = (TextView) inflate.findViewById(C0338R.id.lock_tv_title);
        this.f1583D.setTextColor(-1);
        this.f1582C = (TextView) inflate.findViewById(C0338R.id.lock_tv_footer);
        this.f1592p = (ImageView) inflate.findViewById(C0338R.id.lock_iv_app_icon);
        this.f1599w = (ViewGroup) inflate.findViewById(C0338R.id.lock_lockview);
        this.f1594r = (LinearLayout) inflate.findViewById(C0338R.id.lock_footer_buttons);
        this.f1602z = new PatternFrame();
        return inflate;
    }

    private void m1567g() {
        if (!this.f1584E.f1557q) {
            this.f1581B.findViewById(C0338R.id.lock_ad_container).setVisibility(8);
            Log.w(f1579j, "Not requesting ads!!!n!!!");
        }
        switch (this.f1584E.f1541a) {
            case VideoPlayer.STATE_PREPARING /*2*/:
                m1570j();
                break;
        }
        if (f1572a.equals(this.f1589m)) {
            this.f1592p.setVisibility(0);
            this.f1594r.setVisibility(8);
            ApplicationInfo a = MoboUtils.m1693a(this.f1601y, getContext());
            if (a != null) {
                CharSequence string = LocaleController.getString("AppName", C0338R.string.AppName);
                MoboUtils.m1701a(this.f1592p, a.loadIcon(getContext().getPackageManager()));
                this.f1583D.setText(string);
                if (this.f1584E.f1544d == null || this.f1584E.f1544d.length() == 0) {
                    this.f1582C.setVisibility(8);
                    return;
                }
                this.f1582C.setVisibility(0);
                this.f1582C.setText(this.f1584E.f1544d.replace("%s", string));
                return;
            }
            this.f1592p.setVisibility(8);
        } else if (f1573b.equals(this.f1589m)) {
            this.f1592p.setVisibility(8);
            this.f1594r.setVisibility(0);
            m1569i();
        }
    }

    private boolean m1568h() {
        if (this.f1595s == null) {
            this.f1595s = new Intent();
            this.f1595s.setAction(f1572a);
            this.f1595s.putExtra(f1575d, getContext().getPackageName());
        }
        this.f1589m = this.f1595s.getAction();
        if (this.f1589m == null) {
            Log.w(f1579j, "Finishing: No action specified");
            return false;
        }
        if (this.f1595s.hasExtra(f1577h)) {
            this.f1584E = (LockPreferences) this.f1595s.getSerializableExtra(f1577h);
        } else {
            this.f1584E = new LockPreferences(getContext());
        }
        this.f1601y = this.f1595s.getStringExtra(f1575d);
        if (f1573b.equals(this.f1589m) || this.f1601y == getContext().getPackageName()) {
            this.f1584E.f1557q = false;
        } else {
            this.f1584E.f1557q = true;
        }
        if (f1573b.equals(this.f1589m)) {
            this.f1584E.f1554n = false;
        }
        this.f1596t = new LayoutParams(-1, -1);
        return true;
    }

    private void m1569i() {
        if (this.f1584E.f1541a == 2) {
            this.f1598v.setInStealthMode(false);
            this.f1598v.m1511a(600);
            this.f1583D.setText(C0338R.string.pattern_change_tit);
            this.f1582C.setText(C0338R.string.pattern_change_head);
            this.f1600x = null;
        }
        this.f1597u = PatternFrame.CANCEL;
        this.f1580A = PatternFrame.CONTINUE;
    }

    private boolean m1570j() {
        this.f1599w.removeAllViews();
        LayoutInflater.from(getContext()).inflate(C0338R.layout.view_lock_pattern, this.f1599w, true);
        this.f1598v = (PatternView) this.f1599w.findViewById(C0338R.id.patternView);
        this.f1598v.setOnPatternListener(this.f1602z);
        this.f1598v.setSelectedBitmap(this.f1584E.f1556p);
        MoboUtils.m1701a(this.f1598v, getResources().getDrawable(C0338R.drawable.passwordview_button_background));
        this.f1598v.setSize(this.f1584E.f1545e);
        this.f1598v.setTactileFeedbackEnabled(this.f1584E.f1543c.booleanValue());
        this.f1598v.setInStealthMode(this.f1584E.f1554n);
        this.f1598v.setInErrorStealthMode(this.f1584E.f1555o);
        this.f1598v.m1516e();
        this.f1598v.setVisibility(0);
        this.f1584E.f1541a = 2;
        return true;
    }

    private void m1571k() {
        m1572l();
    }

    private void m1572l() {
        this.f1588l = System.nanoTime();
        this.f1586f = PatternFrame.SHOWN;
        this.f1591o = null;
    }

    public void m1573a() {
        m1568h();
        this.f1596t = new LayoutParams(-1, -1);
        this.f1581B = m1566f();
        addView(this.f1581B, this.f1596t);
        m1567g();
        this.f1586f = PatternFrame.SHOWING;
        m1571k();
    }

    public void setDelegate(PatternFrame patternFrame) {
        this.f1585e = patternFrame;
    }
}

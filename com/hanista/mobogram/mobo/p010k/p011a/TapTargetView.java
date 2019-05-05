package com.hanista.mobogram.mobo.p010k.p011a;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.internal.view.SupportMenu;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewManager;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.hanista.mobogram.messenger.exoplayer.DefaultLoadControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.k.a.d.AnonymousClass12;
import com.hanista.mobogram.mobo.p010k.p011a.FloatValueAnimatorBuilder.FloatValueAnimatorBuilder;
import com.hanista.mobogram.mobo.p010k.p011a.TapTarget;
import com.hanista.mobogram.ui.ActionBar.Theme;

@SuppressLint({"ViewConstructor"})
/* renamed from: com.hanista.mobogram.mobo.k.a.d */
public class TapTargetView extends View {
    boolean f1247A;
    boolean f1248B;
    boolean f1249C;
    Rect f1250D;
    Rect f1251E;
    Path f1252F;
    float f1253G;
    int f1254H;
    int[] f1255I;
    int f1256J;
    float f1257K;
    int f1258L;
    float f1259M;
    int f1260N;
    int f1261O;
    int f1262P;
    float f1263Q;
    float f1264R;
    int f1265S;
    int f1266T;
    Bitmap f1267U;
    TapTargetView f1268V;
    @Nullable
    ViewOutlineProvider f1269W;
    final int f1270a;
    final FloatValueAnimatorBuilder aa;
    final ValueAnimator ab;
    final ValueAnimator ac;
    final ValueAnimator ad;
    private boolean ae;
    private final ValueAnimator af;
    private ValueAnimator[] ag;
    final int f1271b;
    final int f1272c;
    final int f1273d;
    final int f1274e;
    final int f1275f;
    final int f1276g;
    final int f1277h;
    @Nullable
    final ViewGroup f1278i;
    final ViewManager f1279j;
    final TapTarget f1280k;
    final Rect f1281l;
    final TextPaint f1282m;
    final TextPaint f1283n;
    final Paint f1284o;
    final Paint f1285p;
    final Paint f1286q;
    final Paint f1287r;
    final Paint f1288s;
    CharSequence f1289t;
    StaticLayout f1290u;
    @Nullable
    CharSequence f1291v;
    @Nullable
    StaticLayout f1292w;
    boolean f1293x;
    boolean f1294y;
    boolean f1295z;

    /* renamed from: com.hanista.mobogram.mobo.k.a.d.a */
    public static class TapTargetView {
        public void m1310a(TapTargetView tapTargetView) {
            tapTargetView.m1341b(true);
        }

        public void m1311a(TapTargetView tapTargetView, boolean z) {
        }

        public void m1312b(TapTargetView tapTargetView) {
            tapTargetView.m1341b(false);
        }

        public void m1313c(TapTargetView tapTargetView) {
            m1310a(tapTargetView);
        }
    }

    /* compiled from: TapTargetView */
    /* renamed from: com.hanista.mobogram.mobo.k.a.d.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ TapTarget f1235a;
        final /* synthetic */ ViewGroup f1236b;
        final /* synthetic */ com.hanista.mobogram.mobo.p010k.p011a.TapTargetView f1237c;

        /* renamed from: com.hanista.mobogram.mobo.k.a.d.12.1 */
        class TapTargetView implements Runnable {
            final /* synthetic */ AnonymousClass12 f1234a;

            TapTargetView(AnonymousClass12 anonymousClass12) {
                this.f1234a = anonymousClass12;
            }

            public void run() {
                int[] iArr = new int[2];
                this.f1234a.f1237c.f1281l.set(this.f1234a.f1235a.m1297a());
                this.f1234a.f1237c.getLocationOnScreen(iArr);
                this.f1234a.f1237c.f1281l.offset(-iArr[0], -iArr[1]);
                if (this.f1234a.f1236b != null) {
                    Rect rect = new Rect();
                    this.f1234a.f1236b.getWindowVisibleDisplayFrame(rect);
                    this.f1234a.f1237c.f1265S = rect.top;
                    this.f1234a.f1237c.f1266T = rect.bottom;
                }
                this.f1234a.f1237c.m1343c();
                this.f1234a.f1237c.m1345e();
                this.f1234a.f1237c.ab.start();
                this.f1234a.f1237c.f1249C = true;
                this.f1234a.f1237c.requestFocus();
            }
        }

        AnonymousClass12(com.hanista.mobogram.mobo.p010k.p011a.TapTargetView tapTargetView, TapTarget tapTarget, ViewGroup viewGroup) {
            this.f1237c = tapTargetView;
            this.f1235a = tapTarget;
            this.f1236b = viewGroup;
        }

        public void run() {
            this.f1237c.m1344d();
            this.f1235a.m1301a(new com.hanista.mobogram.mobo.p010k.p011a.TapTargetView.12.TapTargetView(this));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.d.1 */
    class TapTargetView implements FloatValueAnimatorBuilder {
        final /* synthetic */ TapTargetView f1238a;

        TapTargetView(TapTargetView tapTargetView) {
            this.f1238a = tapTargetView;
        }

        public void m1323a(float f) {
            float f2 = ((float) this.f1238a.f1254H) * f;
            int i = f2 > this.f1238a.f1253G ? 1 : 0;
            if (i == 0) {
                this.f1238a.m1346f();
            }
            this.f1238a.f1253G = f2;
            this.f1238a.f1256J = (int) Math.min(244.79999f, (f * 1.5f) * 244.79999f);
            this.f1238a.f1252F.reset();
            this.f1238a.f1252F.addCircle((float) this.f1238a.f1255I[0], (float) this.f1238a.f1255I[1], this.f1238a.f1253G, Direction.CW);
            this.f1238a.f1260N = (int) Math.min(255.0f, (f * 1.5f) * 255.0f);
            if (i != 0) {
                this.f1238a.f1259M = ((float) this.f1238a.f1271b) * Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, f * 1.5f);
            } else {
                this.f1238a.f1259M = ((float) this.f1238a.f1271b) * f;
                TapTargetView tapTargetView = this.f1238a;
                tapTargetView.f1257K *= f;
            }
            this.f1238a.f1261O = (int) (this.f1238a.m1333a(f, 0.7f) * 255.0f);
            if (i != 0) {
                this.f1238a.m1346f();
            }
            this.f1238a.m1338a(this.f1238a.f1250D);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.d.2 */
    class TapTargetView implements OnClickListener {
        final /* synthetic */ TapTargetView f1239a;

        TapTargetView(TapTargetView tapTargetView) {
            this.f1239a = tapTargetView;
        }

        public void onClick(View view) {
            if (this.f1239a.f1268V != null && this.f1239a.f1255I != null) {
                if (this.f1239a.f1281l.contains((int) this.f1239a.f1263Q, (int) this.f1239a.f1264R)) {
                    this.f1239a.f1268V.m1310a(this.f1239a);
                } else if (this.f1239a.f1248B) {
                    this.f1239a.f1268V.m1312b(this.f1239a);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.d.3 */
    class TapTargetView implements OnLongClickListener {
        final /* synthetic */ TapTargetView f1240a;

        TapTargetView(TapTargetView tapTargetView) {
            this.f1240a = tapTargetView;
        }

        public boolean onLongClick(View view) {
            if (this.f1240a.f1268V == null || !this.f1240a.f1281l.contains((int) this.f1240a.f1263Q, (int) this.f1240a.f1264R)) {
                return false;
            }
            this.f1240a.f1268V.m1313c(this.f1240a);
            return true;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.d.4 */
    class TapTargetView extends ViewOutlineProvider {
        final /* synthetic */ TapTargetView f1241a;

        TapTargetView(TapTargetView tapTargetView) {
            this.f1241a = tapTargetView;
        }

        @TargetApi(21)
        public void getOutline(View view, Outline outline) {
            if (this.f1241a.f1255I != null) {
                outline.setOval((int) (((float) this.f1241a.f1255I[0]) - this.f1241a.f1253G), (int) (((float) this.f1241a.f1255I[1]) - this.f1241a.f1253G), (int) (((float) this.f1241a.f1255I[0]) + this.f1241a.f1253G), (int) (((float) this.f1241a.f1255I[1]) + this.f1241a.f1253G));
                outline.setAlpha(((float) this.f1241a.f1256J) / 255.0f);
                if (VERSION.SDK_INT >= 22) {
                    outline.offset(0, this.f1241a.f1277h);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.d.5 */
    class TapTargetView implements FloatValueAnimatorBuilder {
        final /* synthetic */ TapTargetView f1242a;

        TapTargetView(TapTargetView tapTargetView) {
            this.f1242a = tapTargetView;
        }

        public void m1324a() {
            this.f1242a.ac.start();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.d.6 */
    class TapTargetView implements FloatValueAnimatorBuilder {
        final /* synthetic */ TapTargetView f1243a;

        TapTargetView(TapTargetView tapTargetView) {
            this.f1243a = tapTargetView;
        }

        public void m1325a(float f) {
            this.f1243a.aa.m1287a(f);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.d.7 */
    class TapTargetView implements FloatValueAnimatorBuilder {
        final /* synthetic */ TapTargetView f1244a;

        TapTargetView(TapTargetView tapTargetView) {
            this.f1244a = tapTargetView;
        }

        public void m1326a(float f) {
            float a = this.f1244a.m1333a(f, 0.5f);
            this.f1244a.f1257K = (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT + a) * ((float) this.f1244a.f1271b);
            this.f1244a.f1258L = (int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - a) * 255.0f);
            this.f1244a.f1259M = ((float) this.f1244a.f1271b) + (this.f1244a.m1332a(f) * ((float) this.f1244a.f1272c));
            this.f1244a.m1346f();
            this.f1244a.m1338a(this.f1244a.f1250D);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.d.8 */
    class TapTargetView implements FloatValueAnimatorBuilder {
        final /* synthetic */ TapTargetView f1245a;

        TapTargetView(TapTargetView tapTargetView) {
            this.f1245a = tapTargetView;
        }

        public void m1327a() {
            this.f1245a.f1279j.removeView(this.f1245a);
            this.f1245a.m1336a();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.k.a.d.9 */
    class TapTargetView implements FloatValueAnimatorBuilder {
        final /* synthetic */ TapTargetView f1246a;

        TapTargetView(TapTargetView tapTargetView) {
            this.f1246a = tapTargetView;
        }

        public void m1328a(float f) {
            this.f1246a.aa.m1287a(f);
        }
    }

    public TapTargetView(Context context, ViewManager viewManager, @Nullable ViewGroup viewGroup, TapTarget tapTarget, @Nullable TapTargetView tapTargetView) {
        super(context);
        this.ae = false;
        this.aa = new TapTargetView(this);
        this.ab = new FloatValueAnimatorBuilder().m1294b(250).m1290a(250).m1291a(new AccelerateDecelerateInterpolator()).m1293a(new TapTargetView(this)).m1292a(new TapTargetView(this)).m1288a();
        this.ac = new FloatValueAnimatorBuilder().m1294b(1000).m1289a(-1).m1291a(new AccelerateDecelerateInterpolator()).m1293a(new TapTargetView(this)).m1288a();
        this.ad = new FloatValueAnimatorBuilder(true).m1294b(250).m1291a(new AccelerateDecelerateInterpolator()).m1293a(new TapTargetView(this)).m1292a(new TapTargetView(this)).m1288a();
        this.af = new FloatValueAnimatorBuilder().m1294b(250).m1291a(new AccelerateDecelerateInterpolator()).m1293a(new FloatValueAnimatorBuilder() {
            final /* synthetic */ com.hanista.mobogram.mobo.p010k.p011a.TapTargetView f1233a;

            {
                this.f1233a = r1;
            }

            public void m1322a(float f) {
                float min = Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 2.0f * f);
                this.f1233a.f1253G = ((float) this.f1233a.f1254H) * ((DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD * min) + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                this.f1233a.f1256J = (int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - min) * 255.0f);
                this.f1233a.f1252F.reset();
                this.f1233a.f1252F.addCircle((float) this.f1233a.f1255I[0], (float) this.f1233a.f1255I[1], this.f1233a.f1253G, Direction.CW);
                this.f1233a.f1259M = (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f) * ((float) this.f1233a.f1271b);
                this.f1233a.f1260N = (int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f) * 255.0f);
                this.f1233a.f1257K = (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT + f) * ((float) this.f1233a.f1271b);
                this.f1233a.f1258L = (int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f) * ((float) this.f1233a.f1258L));
                this.f1233a.f1261O = (int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - min) * 255.0f);
                this.f1233a.m1346f();
                this.f1233a.m1338a(this.f1233a.f1250D);
            }
        }).m1292a(new FloatValueAnimatorBuilder() {
            final /* synthetic */ com.hanista.mobogram.mobo.p010k.p011a.TapTargetView f1232a;

            {
                this.f1232a = r1;
            }

            public void m1321a() {
                this.f1232a.f1279j.removeView(this.f1232a);
                this.f1232a.m1336a();
            }
        }).m1288a();
        this.ag = new ValueAnimator[]{this.ab, this.ac, this.af, this.ad};
        if (tapTarget == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        this.f1280k = tapTarget;
        this.f1279j = viewManager;
        this.f1278i = viewGroup;
        if (tapTargetView == null) {
            tapTargetView = new TapTargetView();
        }
        this.f1268V = tapTargetView;
        this.f1289t = tapTarget.f1207a;
        this.f1291v = tapTarget.f1208b;
        this.f1270a = UiUtil.m1348a(context, 20);
        this.f1275f = UiUtil.m1348a(context, 40);
        this.f1271b = UiUtil.m1348a(context, 44);
        this.f1273d = UiUtil.m1348a(context, 40);
        this.f1274e = UiUtil.m1348a(context, 8);
        this.f1276g = UiUtil.m1348a(context, 88);
        this.f1277h = UiUtil.m1348a(context, 8);
        this.f1272c = (int) (0.1f * ((float) this.f1271b));
        this.f1252F = new Path();
        this.f1281l = new Rect();
        this.f1250D = new Rect();
        this.f1282m = new TextPaint();
        this.f1282m.setTextSize((float) UiUtil.m1350b(context, tapTarget.f1217k));
        this.f1282m.setTypeface(Typeface.create("sans-serif-medium", 0));
        this.f1282m.setAntiAlias(true);
        this.f1283n = new TextPaint();
        this.f1283n.setTextSize((float) UiUtil.m1350b(context, tapTarget.f1218l));
        this.f1283n.setTypeface(Typeface.create(Typeface.SANS_SERIF, 0));
        this.f1283n.setAntiAlias(true);
        this.f1283n.setAlpha(137);
        this.f1284o = new Paint();
        this.f1284o.setAntiAlias(true);
        this.f1284o.setAlpha(244);
        this.f1285p = new Paint();
        this.f1285p.setAntiAlias(true);
        this.f1285p.setAlpha(50);
        this.f1285p.setShadowLayer(10.0f, 0.0f, 25.0f, Theme.MSG_TEXT_COLOR);
        this.f1286q = new Paint();
        this.f1286q.setAntiAlias(true);
        this.f1287r = new Paint();
        this.f1287r.setAntiAlias(true);
        this.f1288s = new Paint();
        this.f1288s.setColor(SupportMenu.CATEGORY_MASK);
        this.f1288s.setStyle(Style.STROKE);
        m1337a(context);
        ViewUtil.m1353a(this, new AnonymousClass12(this, tapTarget, viewGroup));
        setFocusableInTouchMode(true);
        setClickable(true);
        setOnClickListener(new TapTargetView(this));
        setOnLongClickListener(new TapTargetView(this));
    }

    public static TapTargetView m1329a(Activity activity, TapTarget tapTarget, TapTargetView tapTargetView) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity is null");
        }
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        View tapTargetView2 = new TapTargetView(activity, viewGroup, (ViewGroup) viewGroup.findViewById(16908290), tapTarget, tapTargetView);
        viewGroup.addView(tapTargetView2, layoutParams);
        return tapTargetView2;
    }

    public static TapTargetView m1330a(Dialog dialog, TapTarget tapTarget, TapTargetView tapTargetView) {
        if (dialog == null) {
            throw new IllegalArgumentException("Dialog is null");
        }
        Context context = dialog.getContext();
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = 2;
        layoutParams.format = 1;
        layoutParams.flags = 0;
        layoutParams.gravity = 8388659;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = -1;
        layoutParams.height = -1;
        View tapTargetView2 = new TapTargetView(context, windowManager, null, tapTarget, tapTargetView);
        windowManager.addView(tapTargetView2, layoutParams);
        return tapTargetView2;
    }

    double m1331a(int i, int i2, int i3, int i4) {
        return Math.sqrt(Math.pow((double) (i3 - i), 2.0d) + Math.pow((double) (i4 - i2), 2.0d));
    }

    float m1332a(float f) {
        return f < 0.5f ? f / 0.5f : (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f) / 0.5f;
    }

    float m1333a(float f, float f2) {
        return f < f2 ? 0.0f : (f - f2) / (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - f2);
    }

    int m1334a(int i, int i2, Rect rect) {
        return (int) Math.max(m1331a(i, i2, rect.left, rect.top), Math.max(m1331a(i, i2, rect.right, rect.top), Math.max(m1331a(i, i2, rect.left, rect.bottom), m1331a(i, i2, rect.right, rect.bottom))));
    }

    int m1335a(int i, int i2, Rect rect, Rect rect2) {
        Rect rect3 = new Rect(rect2);
        rect3.inset((-this.f1270a) / 2, (-this.f1270a) / 2);
        return Math.max(m1334a(i, i2, rect), m1334a(i, i2, rect3)) + this.f1275f;
    }

    void m1336a() {
        m1339a(true);
    }

    protected void m1337a(Context context) {
        boolean z = true;
        int i = Theme.MSG_TEXT_COLOR;
        this.f1295z = this.f1280k.f1222p;
        this.f1247A = this.f1280k.f1220n;
        this.f1248B = this.f1280k.f1221o;
        if (this.f1247A && VERSION.SDK_INT >= 21) {
            this.f1269W = new TapTargetView(this);
            setOutlineProvider(this.f1269W);
            setElevation((float) this.f1277h);
        }
        if (!(this.f1247A && this.f1269W == null) && VERSION.SDK_INT >= 18) {
            setLayerType(2, null);
        } else {
            setLayerType(1, null);
        }
        Resources.Theme theme = context.getTheme();
        if (UiUtil.m1349a(context, "isLightTheme") != 0) {
            z = false;
        }
        this.f1293x = z;
        if (this.f1280k.f1212f != -1) {
            this.f1284o.setColor(this.f1280k.f1212f);
        } else if (theme != null) {
            this.f1284o.setColor(UiUtil.m1349a(context, "colorPrimary"));
        } else {
            this.f1284o.setColor(-1);
        }
        if (this.f1280k.f1213g != -1) {
            this.f1286q.setColor(UiUtil.m1351c(context, this.f1280k.f1213g));
        } else {
            this.f1286q.setColor(this.f1293x ? Theme.MSG_TEXT_COLOR : -1);
        }
        if (this.f1280k.f1223q) {
            this.f1286q.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        }
        this.f1287r.setColor(this.f1286q.getColor());
        if (this.f1280k.f1214h != -1) {
            this.f1262P = UiUtil.m1347a(UiUtil.m1351c(context, this.f1280k.f1214h), 0.3f);
        } else {
            this.f1262P = -1;
        }
        if (this.f1280k.f1215i != -1) {
            this.f1282m.setColor(UiUtil.m1351c(context, this.f1280k.f1215i));
        } else {
            TextPaint textPaint = this.f1282m;
            if (!this.f1293x) {
                i = -1;
            }
            textPaint.setColor(i);
        }
        if (this.f1280k.f1216j != -1) {
            this.f1283n.setColor(UiUtil.m1351c(context, this.f1280k.f1216j));
        } else {
            this.f1283n.setColor(this.f1282m.getColor());
        }
        if (this.f1280k.f1211e != null) {
            this.f1282m.setTypeface(this.f1280k.f1211e);
            this.f1283n.setTypeface(this.f1280k.f1211e);
        }
    }

    void m1338a(Rect rect) {
        invalidate(rect);
        if (this.f1269W != null && VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
    }

    void m1339a(boolean z) {
        if (!this.ae) {
            this.ae = true;
            for (ValueAnimator valueAnimator : this.ag) {
                valueAnimator.cancel();
                valueAnimator.removeAllUpdateListeners();
            }
            this.f1249C = false;
            if (this.f1268V != null) {
                this.f1268V.m1311a(this, z);
            }
        }
    }

    boolean m1340a(int i) {
        return this.f1266T > 0 ? i < this.f1276g || i > this.f1266T - this.f1276g : i < this.f1276g || i > getHeight() - this.f1276g;
    }

    public void m1341b(boolean z) {
        this.ac.cancel();
        this.ab.cancel();
        if (z) {
            this.af.start();
        } else {
            this.ad.start();
        }
    }

    public boolean m1342b() {
        return !this.ae && this.f1249C;
    }

    void m1343c() {
        Drawable drawable = this.f1280k.f1210d;
        if (!this.f1295z || drawable == null) {
            this.f1267U = null;
            return;
        }
        this.f1267U = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(this.f1267U);
        drawable.setColorFilter(new PorterDuffColorFilter(this.f1284o.getColor(), Mode.SRC_ATOP));
        drawable.draw(canvas);
        drawable.setColorFilter(null);
    }

    void m1344d() {
        int measuredWidth = getMeasuredWidth() - (this.f1273d * 2);
        if (measuredWidth < 0) {
            measuredWidth = 100;
        }
        this.f1290u = new StaticLayout(this.f1289t, this.f1282m, measuredWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        if (this.f1291v != null) {
            this.f1292w = new StaticLayout(this.f1291v, this.f1283n, measuredWidth, Alignment.ALIGN_NORMAL, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 0.0f, false);
        } else {
            this.f1292w = null;
        }
    }

    void m1345e() {
        this.f1251E = getTextBounds();
        this.f1255I = getOuterCircleCenterPoint();
        this.f1254H = m1335a(this.f1255I[0], this.f1255I[1], this.f1251E, this.f1281l);
    }

    void m1346f() {
        this.f1250D.left = (int) Math.max(0.0f, ((float) this.f1255I[0]) - this.f1253G);
        this.f1250D.top = (int) Math.min(0.0f, ((float) this.f1255I[1]) - this.f1253G);
        this.f1250D.right = (int) Math.min((float) getWidth(), (((float) this.f1255I[0]) + this.f1253G) + ((float) this.f1275f));
        this.f1250D.bottom = (int) Math.min((float) getHeight(), (((float) this.f1255I[1]) + this.f1253G) + ((float) this.f1275f));
    }

    int[] getOuterCircleCenterPoint() {
        if (m1340a(this.f1281l.centerY())) {
            return new int[]{this.f1281l.centerX(), this.f1281l.centerY()};
        }
        int max = this.f1270a + (Math.max(this.f1281l.width(), this.f1281l.height()) / 2);
        int totalTextHeight = getTotalTextHeight();
        int i = ((this.f1281l.centerY() - this.f1271b) - this.f1270a) - totalTextHeight > 0 ? 1 : 0;
        int min = Math.min(this.f1273d, this.f1281l.left - max);
        int max2 = Math.max(getWidth() - this.f1273d, max + this.f1281l.right);
        i = i != 0 ? (((this.f1281l.centerY() - this.f1271b) - this.f1270a) - totalTextHeight) + this.f1290u.getHeight() : ((this.f1281l.centerY() + this.f1271b) + this.f1270a) + this.f1290u.getHeight();
        return new int[]{(min + max2) / 2, i};
    }

    Rect getTextBounds() {
        int totalTextHeight = getTotalTextHeight();
        int totalTextWidth = getTotalTextWidth();
        int centerY = ((this.f1281l.centerY() - this.f1271b) - this.f1270a) - totalTextHeight;
        if (centerY <= this.f1265S) {
            centerY = (this.f1281l.centerY() + this.f1271b) + this.f1270a;
        }
        return new Rect(this.f1273d, centerY, totalTextWidth + this.f1273d, totalTextHeight + centerY);
    }

    int getTotalTextHeight() {
        return this.f1292w == null ? this.f1290u.getHeight() + this.f1274e : (this.f1290u.getHeight() + this.f1292w.getHeight()) + this.f1274e;
    }

    int getTotalTextWidth() {
        return this.f1292w == null ? this.f1290u.getWidth() : Math.max(this.f1290u.getWidth(), this.f1292w.getWidth());
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        m1339a(false);
    }

    protected void onDraw(Canvas canvas) {
        if (!this.ae && this.f1255I != null) {
            int save;
            if (this.f1265S > 0 && this.f1266T > 0) {
                canvas.clipRect(0, this.f1265S, getWidth(), this.f1266T);
            }
            if (this.f1262P != -1) {
                canvas.drawColor(this.f1262P);
            }
            this.f1284o.setAlpha(this.f1256J);
            if (this.f1247A && this.f1269W == null) {
                save = canvas.save();
                canvas.clipPath(this.f1252F, Op.DIFFERENCE);
                this.f1285p.setAlpha((int) (DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD * ((float) this.f1256J)));
                canvas.drawCircle((float) this.f1255I[0], (float) this.f1255I[1], this.f1253G, this.f1285p);
                canvas.restoreToCount(save);
            }
            canvas.drawCircle((float) this.f1255I[0], (float) this.f1255I[1], this.f1253G, this.f1284o);
            this.f1286q.setAlpha(this.f1260N);
            if (this.f1258L > 0) {
                this.f1287r.setAlpha(this.f1258L);
                canvas.drawCircle((float) this.f1281l.centerX(), (float) this.f1281l.centerY(), this.f1257K, this.f1287r);
            }
            canvas.drawCircle((float) this.f1281l.centerX(), (float) this.f1281l.centerY(), this.f1259M, this.f1286q);
            save = canvas.save();
            canvas.clipPath(this.f1252F);
            canvas.translate((float) this.f1251E.left, (float) this.f1251E.top);
            this.f1282m.setAlpha(this.f1261O);
            this.f1290u.draw(canvas);
            if (this.f1292w != null) {
                canvas.translate(0.0f, (float) (this.f1290u.getHeight() + this.f1274e));
                this.f1283n.setAlpha((int) (0.54f * ((float) this.f1261O)));
                this.f1292w.draw(canvas);
            }
            canvas.restoreToCount(save);
            save = canvas.save();
            if (this.f1267U != null) {
                canvas.translate((float) (this.f1281l.centerX() - (this.f1267U.getWidth() / 2)), (float) (this.f1281l.centerY() - (this.f1267U.getHeight() / 2)));
                canvas.drawBitmap(this.f1267U, 0.0f, 0.0f, this.f1286q);
            } else if (this.f1280k.f1210d != null) {
                canvas.translate((float) (this.f1281l.centerX() - (this.f1280k.f1210d.getBounds().width() / 2)), (float) (this.f1281l.centerY() - (this.f1280k.f1210d.getBounds().height() / 2)));
                this.f1280k.f1210d.setAlpha(this.f1286q.getAlpha());
                this.f1280k.f1210d.draw(canvas);
            }
            canvas.restoreToCount(save);
            if (this.f1294y) {
                canvas.drawRect(this.f1251E, this.f1288s);
                canvas.drawRect(this.f1281l, this.f1288s);
                canvas.drawCircle((float) this.f1255I[0], (float) this.f1255I[1], 10.0f, this.f1288s);
                canvas.drawCircle((float) this.f1255I[0], (float) this.f1255I[1], (float) (this.f1254H - this.f1275f), this.f1288s);
                canvas.drawCircle((float) this.f1281l.centerX(), (float) this.f1281l.centerY(), (float) (this.f1271b + this.f1270a), this.f1288s);
            }
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (!m1342b() || !this.f1248B || i != 4) {
            return false;
        }
        keyEvent.startTracking();
        return true;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (!m1342b() || !this.f1248B || i != 4 || !keyEvent.isTracking() || keyEvent.isCanceled()) {
            return false;
        }
        if (this.f1268V != null) {
            this.f1268V.m1312b(this);
        } else {
            new TapTargetView().m1312b(this);
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.f1263Q = motionEvent.getX();
        this.f1264R = motionEvent.getY();
        return super.onTouchEvent(motionEvent);
    }

    public void setDrawDebug(boolean z) {
        if (this.f1294y != z) {
            this.f1294y = z;
            postInvalidate();
        }
    }
}

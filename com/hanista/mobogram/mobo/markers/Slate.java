package com.hanista.mobogram.mobo.markers;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.markers.SpotFilter.SpotFilter;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.VideoPlayer;

/* renamed from: com.hanista.mobogram.mobo.markers.f */
public class Slate extends View {
    private static Paint f1820D;
    private static final float[] f1821I;
    private int f1822A;
    private boolean f1823B;
    private Slate[] f1824C;
    private Bitmap f1825E;
    private int f1826F;
    private Paint f1827G;
    private int f1828H;
    int f1829a;
    Spot f1830b;
    float f1831c;
    float f1832d;
    RectF f1833e;
    final Rect f1834f;
    private float f1835g;
    private float f1836h;
    private float f1837i;
    private TiledBitmapCanvas f1838j;
    private final Paint[] f1839k;
    private Bitmap f1840l;
    private Bitmap f1841m;
    private Rect f1842n;
    private Bitmap f1843o;
    private Rect f1844p;
    private PressureCooker f1845q;
    private boolean f1846r;
    private boolean f1847s;
    private Region f1848t;
    private Paint f1849u;
    private Paint f1850v;
    private Matrix f1851w;
    private Matrix f1852x;
    private float f1853y;
    private float f1854z;

    /* renamed from: com.hanista.mobogram.mobo.markers.f.a */
    private class Slate implements SpotFilter {
        final float[] f1799a;
        final /* synthetic */ Slate f1800b;
        private SpotFilter f1801c;
        private Slate f1802d;
        private float f1803e;
        private int f1804f;

        public Slate(Slate slate) {
            this.f1800b = slate;
            this.f1803e = Face.UNCOMPUTED_PROBABILITY;
            this.f1804f = 0;
            this.f1799a = new float[2];
            this.f1801c = new SpotFilter(6, 0.65f, 0.9f, this);
            this.f1802d = new Slate(slate);
        }

        public float m1826a() {
            return this.f1803e;
        }

        public void m1827a(int i) {
            this.f1802d.m1836a(i);
        }

        public void m1828a(long j) {
            this.f1803e = Face.UNCOMPUTED_PROBABILITY;
            this.f1801c.m1873a();
            this.f1802d.m1835a();
        }

        public void m1829a(Spot spot) {
            float a = Slate.m1839a(this.f1800b.f1836h, this.f1800b.f1837i, (float) Math.pow((double) (spot.f1860f == 2 ? spot.f1858d : this.f1800b.f1845q.m1819a(spot.f1858d)), (double) this.f1800b.f1835g));
            this.f1799a[0] = spot.f1855a - this.f1800b.f1853y;
            this.f1799a[1] = spot.f1856b - this.f1800b.f1854z;
            this.f1800b.f1852x.mapPoints(this.f1799a);
            this.f1800b.m1844a(this.f1802d.m1834a(this.f1800b.f1838j, this.f1799a[0], this.f1799a[1], a));
        }

        public int m1830b() {
            return this.f1804f;
        }

        public void m1831b(int i) {
            this.f1802d.m1838b(i);
        }

        public void m1832b(Spot spot) {
            this.f1801c.m1874b(spot);
            this.f1803e = spot.f1858d;
            this.f1804f = spot.f1860f;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.f.b */
    private class Slate {
        int f1805a;
        final /* synthetic */ Slate f1806b;
        private float f1807c;
        private float f1808d;
        private float f1809e;
        private float f1810f;
        private float[] f1811g;
        private int f1812h;
        private int f1813i;
        private int f1814j;
        private Path f1815k;
        private PathMeasure f1816l;
        private Paint f1817m;
        private final RectF f1818n;
        private final RectF f1819o;

        public Slate(Slate slate) {
            this.f1806b = slate;
            this.f1807c = 0.0f;
            this.f1808d = 0.0f;
            this.f1809e = 0.0f;
            this.f1810f = Face.UNCOMPUTED_PROBABILITY;
            this.f1811g = new float[2];
            this.f1814j = 0;
            this.f1815k = new Path();
            this.f1816l = new PathMeasure();
            this.f1817m = new Paint(1);
            this.f1805a = NalUnitUtil.EXTENDED_SAR;
            this.f1818n = new RectF();
            this.f1819o = new RectF();
        }

        final float m1833a(float f, float f2, float f3, float f4) {
            float f5 = f3 - f;
            float f6 = f4 - f2;
            return (float) Math.sqrt((double) ((f5 * f5) + (f6 * f6)));
        }

        public RectF m1834a(CanvasLite canvasLite, float f, float f2, float f3) {
            RectF rectF = this.f1819o;
            rectF.setEmpty();
            if (this.f1810f < 0.0f) {
                m1837a(canvasLite, f, f2, f3, rectF);
            } else {
                this.f1809e = m1833a(this.f1807c, this.f1808d, f, f2);
                float f4 = 0.0f;
                while (f4 <= this.f1809e) {
                    float f5 = f4 == 0.0f ? 0.0f : f4 / this.f1809e;
                    float a = Slate.m1839a(this.f1810f, f3, f5);
                    m1837a(canvasLite, Slate.m1839a(this.f1807c, f, f5), Slate.m1839a(this.f1808d, f2, f5), a, rectF);
                    f4 = a <= 16.0f ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT + f4 : (float) (((double) f4) + Math.sqrt((0.10000000149011612d * Math.pow((double) (a - 16.0f), 2.0d)) + 1.0d));
                }
            }
            this.f1807c = f;
            this.f1808d = f2;
            this.f1810f = f3;
            return rectF;
        }

        public void m1835a() {
            float[] fArr = this.f1811g;
            this.f1811g[1] = 0.0f;
            fArr[0] = 0.0f;
            this.f1808d = 0.0f;
            this.f1807c = 0.0f;
            this.f1810f = Face.UNCOMPUTED_PROBABILITY;
        }

        public void m1836a(int i) {
            this.f1812h = i;
            if (i == 0) {
                this.f1817m.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
                this.f1817m.setColor(Theme.MSG_TEXT_COLOR);
                return;
            }
            this.f1817m.setXfermode(null);
            this.f1817m.setColor(Theme.MSG_TEXT_COLOR);
            this.f1817m.setAlpha(this.f1805a);
            this.f1817m.setColorFilter(new PorterDuffColorFilter(i, Mode.SRC_ATOP));
        }

        final void m1837a(CanvasLite canvasLite, float f, float f2, float f3, RectF rectF) {
            switch (this.f1814j) {
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    canvasLite.m1813a(f - f3, f2 - f3, f + f3, f2 + f3, this.f1817m);
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    this.f1818n.set(f - f3, f2 - f3, f + f3, f2 + f3);
                    if (this.f1806b.f1841m != null && this.f1806b.f1842n != null) {
                        canvasLite.m1815a(this.f1806b.f1841m, this.f1806b.f1842n, this.f1818n, this.f1817m);
                        break;
                    }
                    throw new RuntimeException("Slate.drawStrokePoint: no airbrush bitmap - frame=" + this.f1806b.f1842n);
                case VideoPlayer.STATE_READY /*4*/:
                    this.f1818n.set(f - f3, f2 - f3, f + f3, f2 + f3);
                    if (this.f1806b.f1843o != null && this.f1806b.f1844p != null) {
                        canvasLite.m1815a(this.f1806b.f1843o, this.f1806b.f1844p, this.f1818n, this.f1817m);
                        break;
                    }
                    throw new RuntimeException("Slate.drawStrokePoint: no fountainpen bitmap - frame=" + this.f1806b.f1844p);
                default:
                    canvasLite.m1814a(f, f2, f3, this.f1817m);
                    break;
            }
            rectF.union(f - f3, f2 - f3, f + f3, f2 + f3);
        }

        public void m1838b(int i) {
            this.f1813i = i;
            switch (i) {
                case VideoPlayer.TRACK_DEFAULT /*0*/:
                    this.f1814j = 0;
                    this.f1805a = NalUnitUtil.EXTENDED_SAR;
                    break;
                case VideoPlayer.TYPE_AUDIO /*1*/:
                    this.f1814j = 0;
                    this.f1805a = 16;
                    break;
                case VideoPlayer.STATE_PREPARING /*2*/:
                    this.f1814j = 3;
                    this.f1805a = TLRPC.USER_FLAG_UNUSED;
                    break;
                case VideoPlayer.STATE_BUFFERING /*3*/:
                    this.f1814j = 4;
                    this.f1805a = NalUnitUtil.EXTENDED_SAR;
                    break;
            }
            m1836a(this.f1812h);
        }
    }

    static {
        f1820D = new Paint(7);
        f1821I = new float[9];
    }

    public Slate(Context context) {
        super(context);
        this.f1835g = 2.0f;
        this.f1829a = 0;
        this.f1839k = new Paint[10];
        this.f1848t = new Region();
        this.f1851w = new Matrix();
        this.f1852x = new Matrix();
        this.f1853y = 0.0f;
        this.f1854z = 0.0f;
        this.f1830b = new Spot();
        this.f1826F = 0;
        this.f1828H = 0;
        this.f1831c = Face.UNCOMPUTED_PROBABILITY;
        this.f1832d = Face.UNCOMPUTED_PROBABILITY;
        this.f1833e = new RectF();
        this.f1834f = new Rect();
        m1854g();
    }

    public static float m1839a(float f, float f2, float f3) {
        return ((f2 - f) * f3) + f;
    }

    public static float m1840a(Matrix matrix) {
        matrix.getValues(f1821I);
        return f1821I[0];
    }

    @SuppressLint({"NewApi"})
    static final int m1841a(MotionEvent motionEvent, int i) {
        return Slate.m1852f() ? motionEvent.getToolType(i) : (VERSION.SDK_INT < 8 || !"flyer".equals(Build.HARDWARE) || motionEvent.getSize(i) > 0.1f) ? 1 : 2;
    }

    private void m1843a(Canvas canvas) {
        if (this.f1825E == null) {
            int width = canvas.getWidth() - 128;
            int length = (this.f1824C.length * 24) + 12;
            this.f1825E = Bitmap.createBitmap(width, length, Config.ARGB_8888);
            if (this.f1825E == null) {
                throw new RuntimeException("drawStrokeDebugInfo: couldn't create debug bitmap (" + width + "x" + length + ")");
            }
            this.f1827G = new Paint(1);
        }
        Canvas canvas2 = new Canvas(this.f1825E);
        canvas2.save();
        canvas2.clipRect(new Rect(0, 0, 55, canvas2.getHeight()));
        canvas2.drawColor(0, Mode.CLEAR);
        canvas2.restore();
        int height = canvas2.getHeight() - 6;
        Slate[] slateArr = this.f1824C;
        int length2 = slateArr.length;
        int i = 0;
        int i2 = height;
        while (i < length2) {
            String str;
            Slate slate = slateArr[i];
            float a = slate.m1826a();
            if (a >= 0.85f && a <= 1.25f) {
                this.f1827G.setColor(-13369549);
            } else if (a < 0.85f) {
                this.f1827G.setColor(-8355712);
            } else {
                this.f1827G.setColor(-32768);
            }
            if (a < 0.0f) {
                str = "--";
            } else {
                String str2 = "%s %.4f";
                Object[] objArr = new Object[2];
                objArr[0] = slate.m1830b() == 2 ? "S" : "F";
                objArr[1] = Float.valueOf(a);
                str = String.format(str2, objArr);
            }
            canvas2.drawText(str, (float) 4, (float) (i2 - 2), this.f1827G);
            if (this.f1826F + 55 > canvas2.getWidth()) {
                this.f1826F = 0;
                canvas2.save();
                canvas2.clipRect(new Rect(30, 0, canvas2.getWidth(), canvas2.getHeight()));
                canvas2.drawColor(0, Mode.CLEAR);
                canvas2.restore();
            }
            if (a >= 0.0f) {
                canvas2.drawRect((float) (this.f1826F + 55), (float) (i2 - ((int) (24.0f * a))), (float) ((this.f1826F + 55) + 4), (float) i2, this.f1827G);
            } else {
                canvas2.drawPoint((float) ((this.f1826F + 55) + 4), (float) i2, this.f1827G);
            }
            i++;
            i2 -= 30;
        }
        this.f1826F += 4;
        canvas.drawBitmap(this.f1825E, 96.0f, 64.0f, null);
        invalidate(new Rect(96, 64, canvas.getWidth() + 96, canvas.getHeight() + 64));
    }

    private void m1844a(RectF rectF) {
        rectF.roundOut(this.f1834f);
        this.f1834f.inset(-4, -4);
        invalidate();
    }

    public static float m1846b(float f, float f2, float f3) {
        return f3 < f ? f : f3 > f2 ? f2 : f3;
    }

    static final boolean m1852f() {
        return VERSION.SDK_INT >= 14;
    }

    @SuppressLint({"NewApi"})
    private void m1854g() {
        this.f1847s = true;
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService("activity");
        if (VERSION.SDK_INT >= 11) {
            this.f1822A = activityManager.getLargeMemoryClass();
        } else {
            this.f1822A = activityManager.getMemoryClass();
        }
        this.f1823B = this.f1822A <= 16;
        Log.v("Slate", "Slate.init: memClass=" + this.f1822A + (this.f1823B ? " (LOW)" : TtmlNode.ANONYMOUS_REGION_ID));
        Resources resources = getContext().getResources();
        Options options = new Options();
        options.inPreferredConfig = Config.ALPHA_8;
        if (this.f1823B) {
            options.inSampleSize = 4;
        }
        this.f1841m = BitmapFactory.decodeResource(resources, C0338R.drawable.airbrush_light, options);
        if (this.f1841m == null) {
            Log.e("Slate", "SmoothStroker: Couldn't load airbrush bitmap");
        }
        this.f1842n = new Rect(0, 0, this.f1841m.getWidth(), this.f1841m.getHeight());
        this.f1843o = BitmapFactory.decodeResource(resources, C0338R.drawable.fountainpen, options);
        if (this.f1843o == null) {
            Log.e("Slate", "SmoothStroker: Couldn't load fountainpen bitmap");
        }
        this.f1844p = new Rect(0, 0, this.f1843o.getWidth(), this.f1843o.getHeight());
        this.f1824C = new Slate[10];
        for (int i = 0; i < this.f1824C.length; i++) {
            this.f1824C[i] = new Slate(this);
        }
        this.f1845q = new PressureCooker(getContext());
        setFocusable(true);
        if (VERSION.SDK_INT >= 11) {
            setLayerType(2, null);
        }
        this.f1850v = new Paint();
        this.f1850v.setColor(1080057952);
        this.f1849u = new Paint();
        this.f1839k[0] = new Paint();
        this.f1839k[0].setStyle(Style.STROKE);
        this.f1839k[0].setStrokeWidth(2.0f);
        this.f1839k[0].setARGB(NalUnitUtil.EXTENDED_SAR, 0, NalUnitUtil.EXTENDED_SAR, NalUnitUtil.EXTENDED_SAR);
        this.f1839k[1] = new Paint(this.f1839k[0]);
        this.f1839k[1].setARGB(NalUnitUtil.EXTENDED_SAR, NalUnitUtil.EXTENDED_SAR, 0, TLRPC.USER_FLAG_UNUSED);
        this.f1839k[2] = new Paint(this.f1839k[0]);
        this.f1839k[2].setARGB(NalUnitUtil.EXTENDED_SAR, 0, NalUnitUtil.EXTENDED_SAR, 0);
        this.f1839k[3] = new Paint(this.f1839k[0]);
        this.f1839k[3].setARGB(NalUnitUtil.EXTENDED_SAR, 30, 30, NalUnitUtil.EXTENDED_SAR);
        this.f1839k[4] = new Paint();
        this.f1839k[4].setStyle(Style.FILL);
        this.f1839k[4].setARGB(NalUnitUtil.EXTENDED_SAR, TLRPC.USER_FLAG_UNUSED, TLRPC.USER_FLAG_UNUSED, TLRPC.USER_FLAG_UNUSED);
    }

    public Bitmap m1860a(boolean z) {
        Bitmap bitmap = getBitmap();
        Bitmap createBitmap = bitmap != null ? Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig()) : null;
        if (createBitmap != null) {
            Canvas canvas = new Canvas(createBitmap);
            if (this.f1828H != 0 && z) {
                canvas.drawColor(this.f1828H);
            }
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
        }
        return createBitmap;
    }

    public void m1861a(float f, float f2) {
        this.f1853y = f;
        this.f1854z = f2;
    }

    public void m1862a(Bitmap bitmap) {
        if (this.f1838j == null) {
            this.f1840l = bitmap;
            return;
        }
        m1869d();
        Matrix matrix = new Matrix();
        matrix.setRectToRect(new RectF(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight()), new RectF(0.0f, 0.0f, (float) this.f1838j.m1891a(), (float) this.f1838j.m1900b()), ScaleToFit.CENTER);
        this.f1838j.m1896a(bitmap, matrix, f1820D);
        invalidate();
    }

    public boolean m1863a() {
        return this.f1847s;
    }

    public float[] m1864a(float[] fArr) {
        if (fArr == null) {
            fArr = new float[2];
        }
        fArr[0] = this.f1853y;
        fArr[1] = this.f1854z;
        return fArr;
    }

    public void m1865b() {
        this.f1854z = 0.0f;
        this.f1853y = 0.0f;
        Matrix matrix = new Matrix();
        matrix.postScale(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        setZoom(matrix);
        invalidate();
    }

    public void m1866b(float f, float f2) {
        m1861a(f, f2);
        invalidate();
    }

    public void m1867c() {
        if (this.f1838j != null) {
            m1869d();
            this.f1838j.m1895a(0, Mode.SRC);
            invalidate();
        } else if (this.f1840l != null) {
            this.f1840l.recycle();
            this.f1840l = null;
        }
        this.f1847s = true;
        m1865b();
    }

    public void m1868c(float f, float f2) {
        this.f1836h = f * 0.5f;
        this.f1837i = f2 * 0.5f;
    }

    public void m1869d() {
        if (this.f1838j == null) {
            Throwable th = new Throwable();
            th.fillInStackTrace();
            Log.v("Slate", "commitStroke before mTiledCanvas inited", th);
            return;
        }
        this.f1838j.m1903d();
    }

    public void m1870e() {
        if (this.f1838j == null) {
            Log.v("Slate", "undo before mTiledCanvas inited");
        }
        this.f1838j.m1901b(-1);
        invalidate();
    }

    public Bitmap getBitmap() {
        if (this.f1838j == null) {
            return null;
        }
        m1869d();
        return this.f1838j.m1902c();
    }

    public int getDebugFlags() {
        return this.f1829a;
    }

    public float getDrawingDensity() {
        return DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    }

    public Matrix getZoom() {
        return this.f1851w;
    }

    public Matrix getZoomInv() {
        return this.f1852x;
    }

    public float getZoomPosX() {
        return this.f1853y;
    }

    public float getZoomPosY() {
        return this.f1854z;
    }

    public void invalidate(Rect rect) {
        if (rect.isEmpty()) {
            Log.w("Slate", "invalidating empty rect!");
        }
        super.invalidate(rect);
    }

    protected void onDraw(Canvas canvas) {
        if (this.f1838j != null) {
            canvas.save(1);
            if (!(this.f1853y == 0.0f && this.f1854z == 0.0f && this.f1851w.isIdentity())) {
                canvas.translate(this.f1853y, this.f1854z);
                canvas.concat(this.f1851w);
                canvas.drawRect(-20000.0f, -20000.0f, 20000.0f, 0.0f, this.f1850v);
                canvas.drawRect(-20000.0f, 0.0f, 0.0f, (float) this.f1838j.m1900b(), this.f1850v);
                canvas.drawRect((float) this.f1838j.m1891a(), 0.0f, 20000.0f, (float) this.f1838j.m1900b(), this.f1850v);
                canvas.drawRect(-20000.0f, (float) this.f1838j.m1900b(), 20000.0f, 20000.0f, this.f1850v);
            }
            if (!this.f1848t.isEmpty()) {
                canvas.clipRegion(this.f1848t);
                this.f1848t.setEmpty();
            }
            this.f1849u.setFilterBitmap(Slate.m1840a(this.f1851w) < 3.0f);
            this.f1838j.m1898a(canvas, 0.0f, 0.0f, this.f1849u, false);
            if ((this.f1829a & 1) != 0) {
                m1843a(canvas);
            }
            canvas.restore();
            if ((this.f1829a & 2) != 0) {
                this.f1845q.m1821a(canvas);
            }
        }
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        if (this.f1838j == null) {
            int i5 = i * 1;
            int i6 = i2 * 1;
            int i7 = (i5 * i6) * 4;
            int i8 = 10;
            int i9 = (this.f1822A * TLRPC.MESSAGE_FLAG_HAS_VIEWS) * TLRPC.MESSAGE_FLAG_HAS_VIEWS;
            if (i7 * 12 > i9) {
                i8 = (i9 / i7) - 2;
            }
            if (i8 < 1) {
                i8 = 1;
            }
            Log.v("Slate", String.format("About to init tiled %dx canvas: %dx%d x 32bpp x %d = %d bytes (ceiling: %d)", new Object[]{Integer.valueOf(1), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i8), Integer.valueOf(((i5 * i6) * 4) * i8), Integer.valueOf(i9)}));
            this.f1838j = new TiledBitmapCanvas(i5, i6, Config.ARGB_8888, TLRPC.USER_FLAG_UNUSED2, i8);
            if (this.f1838j == null) {
                throw new RuntimeException("onSizeChanged: Unable to allocate main buffer (" + i + "x" + i2 + ")");
            }
            Bitmap bitmap = this.f1840l;
            if (bitmap != null) {
                this.f1840l = null;
                m1862a(bitmap);
            }
            m1865b();
        }
    }

    @SuppressLint({"NewApi"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = VERSION.SDK_INT >= 8 ? motionEvent.getActionMasked() : motionEvent.getAction();
        int historySize = motionEvent.getHistorySize();
        int pointerCount = motionEvent.getPointerCount();
        long eventTime = motionEvent.getEventTime();
        this.f1847s = false;
        if (actionMasked == 0) {
            m1869d();
        }
        if (this.f1846r) {
            return false;
        }
        int actionIndex;
        if (actionMasked == 0 || actionMasked == 5 || actionMasked == 1 || actionMasked == 6) {
            actionIndex = VERSION.SDK_INT >= 8 ? motionEvent.getActionIndex() : 0;
            this.f1830b.m1871a(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex), motionEvent.getSize(actionIndex), motionEvent.getPressure(actionIndex) + motionEvent.getSize(actionIndex), eventTime, Slate.m1841a(motionEvent, actionIndex));
            this.f1824C[motionEvent.getPointerId(actionIndex)].m1832b(this.f1830b);
            if (actionMasked == 1 || actionMasked == 6) {
                this.f1824C[motionEvent.getPointerId(actionIndex)].m1828a(eventTime);
            }
        } else if (actionMasked == 2) {
            if (this.f1831c >= 0.0f) {
                this.f1833e.set(this.f1831c - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1832d - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1831c + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1832d + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            }
            for (int i = 0; i < historySize; i++) {
                for (int i2 = 0; i2 < pointerCount; i2++) {
                    this.f1830b.m1871a(motionEvent.getHistoricalX(i2, i), motionEvent.getHistoricalY(i2, i), motionEvent.getHistoricalSize(i2, i), motionEvent.getHistoricalPressure(i2, i) + motionEvent.getHistoricalSize(i2, i), motionEvent.getHistoricalEventTime(i), Slate.m1841a(motionEvent, i2));
                    if ((this.f1829a & 1) != 0) {
                        if (this.f1831c >= 0.0f) {
                            this.f1831c = this.f1830b.f1855a;
                            this.f1832d = this.f1830b.f1856b;
                            this.f1833e.union(this.f1831c - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1832d - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1831c + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1832d + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        } else {
                            this.f1831c = this.f1830b.f1855a;
                            this.f1832d = this.f1830b.f1856b;
                            this.f1833e.union(this.f1831c - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1832d - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1831c + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1832d + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        }
                    }
                    this.f1824C[motionEvent.getPointerId(i2)].m1832b(this.f1830b);
                }
            }
            for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                this.f1830b.m1871a(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex), motionEvent.getSize(actionIndex), motionEvent.getPressure(actionIndex) + motionEvent.getSize(actionIndex), eventTime, Slate.m1841a(motionEvent, actionIndex));
                if ((this.f1829a & 1) != 0) {
                    if (this.f1831c >= 0.0f) {
                        this.f1831c = this.f1830b.f1855a;
                        this.f1832d = this.f1830b.f1856b;
                        this.f1833e.union(this.f1831c - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1832d - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1831c + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1832d + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    } else {
                        this.f1831c = this.f1830b.f1855a;
                        this.f1832d = this.f1830b.f1856b;
                        this.f1833e.union(this.f1831c - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1832d - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1831c + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f1832d + DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    }
                }
                this.f1824C[motionEvent.getPointerId(actionIndex)].m1832b(this.f1830b);
            }
            if ((this.f1829a & 1) != 0) {
                Rect rect = new Rect();
                this.f1833e.roundOut(rect);
                invalidate(rect);
            }
        }
        if (actionMasked == 3 || actionMasked == 1) {
            for (actionMasked = 0; actionMasked < pointerCount; actionMasked++) {
                this.f1824C[motionEvent.getPointerId(actionMasked)].m1828a(eventTime);
            }
            this.f1832d = Face.UNCOMPUTED_PROBABILITY;
            this.f1831c = Face.UNCOMPUTED_PROBABILITY;
        }
        return true;
    }

    public void setDebugFlags(int i) {
        if (i != this.f1829a) {
            this.f1829a = i;
            this.f1838j.m1899a((i & 8) != 0);
            invalidate();
        }
    }

    public void setDrawingBackground(int i) {
        this.f1828H = i;
        setBackgroundColor(i);
        invalidate();
    }

    public void setPenColor(int i) {
        for (Slate a : this.f1824C) {
            a.m1827a(i);
        }
    }

    public void setPenType(int i) {
        for (Slate b : this.f1824C) {
            b.m1831b(i);
        }
    }

    public void setZoom(Matrix matrix) {
        this.f1851w.set(matrix);
        this.f1851w.invert(this.f1852x);
    }

    public void setZoomMode(boolean z) {
        this.f1846r = z;
    }

    public void setZoomPos(float[] fArr) {
        setZoomPosNoInval(fArr);
        invalidate();
    }

    public void setZoomPosNoInval(float[] fArr) {
        m1861a(fArr[0], fArr[1]);
    }
}

package com.hanista.mobogram.mobo.markers;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import com.hanista.mobogram.tgnet.TLRPC;
import java.util.ArrayList;

/* renamed from: com.hanista.mobogram.mobo.markers.i */
public class TiledBitmapCanvas implements CanvasLite {
    static final int[] f1880a;
    private static Paint f1881n;
    private static Paint f1882o;
    private static Paint f1883p;
    private boolean f1884b;
    private int f1885c;
    private int f1886d;
    private TiledBitmapCanvas[] f1887e;
    private int f1888f;
    private int f1889g;
    private int f1890h;
    private int f1891i;
    private Config f1892j;
    private int f1893k;
    private int f1894l;
    private boolean f1895m;
    private int f1896q;

    /* renamed from: com.hanista.mobogram.mobo.markers.i.a */
    private class TiledBitmapCanvas {
        int f1873a;
        int f1874b;
        int f1875c;
        int f1876d;
        boolean f1877e;
        ArrayList<TiledBitmapCanvas> f1878f;
        final /* synthetic */ TiledBitmapCanvas f1879g;

        /* renamed from: com.hanista.mobogram.mobo.markers.i.a.a */
        private class TiledBitmapCanvas {
            int f1869a;
            Canvas f1870b;
            Bitmap f1871c;
            final /* synthetic */ TiledBitmapCanvas f1872d;

            public TiledBitmapCanvas(TiledBitmapCanvas tiledBitmapCanvas, int i) {
                this.f1872d = tiledBitmapCanvas;
                this.f1869a = i;
                this.f1871c = Bitmap.createBitmap(tiledBitmapCanvas.f1879g.f1885c, tiledBitmapCanvas.f1879g.f1885c, tiledBitmapCanvas.f1879g.f1892j);
                if (this.f1871c != null) {
                    this.f1870b = new Canvas(this.f1871c);
                    this.f1870b.translate((float) ((-tiledBitmapCanvas.f1873a) * tiledBitmapCanvas.f1879g.f1885c), (float) ((-tiledBitmapCanvas.f1874b) * tiledBitmapCanvas.f1879g.f1885c));
                }
            }
        }

        public TiledBitmapCanvas(TiledBitmapCanvas tiledBitmapCanvas, int i, int i2, int i3) {
            this.f1879g = tiledBitmapCanvas;
            this.f1873a = i;
            this.f1874b = i2;
            this.f1878f = new ArrayList(tiledBitmapCanvas.f1886d);
            this.f1876d = i3;
            m1877c(i3);
            if (this.f1875c < 0) {
                throw new OutOfMemoryError("Could not create bitmap for tile " + i + "," + i2);
            }
        }

        private String m1876b() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("bot=");
            stringBuffer.append(this.f1876d);
            stringBuffer.append(" top=");
            stringBuffer.append(this.f1875c);
            stringBuffer.append(" [");
            for (int i = 0; i < this.f1878f.size(); i++) {
                if (i > 0) {
                    stringBuffer.append(" ");
                }
                stringBuffer.append(((TiledBitmapCanvas) this.f1878f.get(i)).f1869a);
            }
            stringBuffer.append("]");
            return stringBuffer.toString();
        }

        private TiledBitmapCanvas m1877c(int i) {
            TiledBitmapCanvas tiledBitmapCanvas;
            int size = this.f1878f.size();
            TiledBitmapCanvas tiledBitmapCanvas2;
            if (size == this.f1879g.f1886d) {
                tiledBitmapCanvas2 = (TiledBitmapCanvas) this.f1878f.get(size - 1);
                this.f1878f.remove(size - 1);
                tiledBitmapCanvas2.f1869a = i;
                this.f1876d = ((TiledBitmapCanvas) this.f1878f.get(size - 2)).f1869a;
                tiledBitmapCanvas2.f1871c.eraseColor(0);
                tiledBitmapCanvas = tiledBitmapCanvas2;
            } else {
                tiledBitmapCanvas2 = new TiledBitmapCanvas(this, i);
                if (tiledBitmapCanvas2.f1871c == null) {
                    return null;
                }
                tiledBitmapCanvas = tiledBitmapCanvas2;
            }
            if (this.f1878f.size() > 0) {
                tiledBitmapCanvas.f1870b.drawBitmap(((TiledBitmapCanvas) this.f1878f.get(0)).f1871c, (float) (this.f1873a * this.f1879g.f1885c), (float) (this.f1874b * this.f1879g.f1885c), null);
            }
            this.f1878f.add(0, tiledBitmapCanvas);
            this.f1875c = i;
            return this.f1879g.f1884b ? tiledBitmapCanvas : tiledBitmapCanvas;
        }

        private int m1878d(int i) {
            if (i >= this.f1875c) {
                return 0;
            }
            if (i < this.f1876d) {
                return -1;
            }
            int i2 = (this.f1879g.f1884b && this.f1878f.size() > 0 && this.f1875c == ((TiledBitmapCanvas) this.f1878f.get(0)).f1869a) ? 1 : 1;
            for (i2 = 1; i2 < this.f1878f.size(); i2++) {
                if (((TiledBitmapCanvas) this.f1878f.get(i2)).f1869a <= i) {
                    return i2;
                }
            }
            throw new RuntimeException(String.format("internal inconsistency: couldn't findVersion %d for tile (%d,%d) %s", new Object[]{Integer.valueOf(i), Integer.valueOf(this.f1873a), Integer.valueOf(this.f1874b), m1876b()}));
        }

        private TiledBitmapCanvas m1879e(int i) {
            if (i == this.f1875c) {
                return (TiledBitmapCanvas) this.f1878f.get(0);
            }
            if (i > this.f1875c) {
                return m1877c(i);
            }
            int d = m1878d(i);
            return d >= 0 ? (TiledBitmapCanvas) this.f1878f.get(d) : null;
        }

        public Bitmap m1880a() {
            return ((TiledBitmapCanvas) this.f1878f.get(0)).f1871c;
        }

        public Canvas m1881a(int i) {
            return m1879e(i).f1870b;
        }

        public void m1882b(int i) {
            int d = m1878d(i);
            if (d >= 0 && d > 0) {
                this.f1878f.subList(0, d).clear();
                d = this.f1875c;
                if (this.f1879g.f1884b) {
                    this.f1875c = ((TiledBitmapCanvas) this.f1878f.get(0)).f1869a;
                } else {
                    this.f1875c = ((TiledBitmapCanvas) this.f1878f.get(0)).f1869a;
                }
            }
        }
    }

    static {
        f1881n = new Paint(0);
        f1882o = new Paint(0);
        f1883p = new Paint(0);
        f1882o.setColor(TLRPC.MESSAGE_FLAG_MEGAGROUP);
        f1882o.setStrokeWidth(3.0f);
        f1882o.setStyle(Style.STROKE);
        f1883p.setColor(TLRPC.MESSAGE_FLAG_MEGAGROUP);
        f1883p.setTextSize(20.0f);
        f1880a = new int[]{1090453504, 1090518784, 1073807104, 1073742079, 1090453759, 1084882944, 1084926464, 1073785344, 1073741994, 1084883114, 1081540608, 1081571072, 1073772288, 1073741943, 1081540727};
    }

    public TiledBitmapCanvas(int i, int i2, Config config, int i3, int i4) {
        this.f1884b = false;
        this.f1885c = TLRPC.USER_FLAG_UNUSED2;
        this.f1886d = 10;
        this.f1893k = 0;
        this.f1894l = 0;
        this.f1895m = false;
        this.f1896q = 0;
        this.f1888f = i;
        this.f1889g = i2;
        this.f1892j = config;
        this.f1885c = i3;
        this.f1886d = i4;
        m1886a(null);
    }

    public static final int m1883a(int i, int i2) {
        return i2 > i ? i2 : i;
    }

    private Canvas m1885a(TiledBitmapCanvas tiledBitmapCanvas) {
        this.f1895m = true;
        return tiledBitmapCanvas.m1881a(this.f1893k);
    }

    private void m1886a(Bitmap bitmap) {
        int i = 1;
        this.f1890h = (this.f1888f % this.f1885c == 0 ? 0 : 1) + (this.f1888f / this.f1885c);
        int i2 = this.f1889g / this.f1885c;
        if (this.f1889g % this.f1885c == 0) {
            i = 0;
        }
        this.f1891i = i2 + i;
        this.f1887e = new TiledBitmapCanvas[(this.f1890h * this.f1891i)];
        Paint paint = new Paint();
        for (i2 = 0; i2 < this.f1891i; i2++) {
            for (i = 0; i < this.f1890h; i++) {
                int i3 = (this.f1890h * i2) + i;
                TiledBitmapCanvas tiledBitmapCanvas = new TiledBitmapCanvas(this, i, i2, this.f1893k);
                this.f1887e[i3] = tiledBitmapCanvas;
                if (bitmap != null) {
                    m1885a(tiledBitmapCanvas).drawBitmap(bitmap, 0.0f, 0.0f, paint);
                }
            }
        }
    }

    public static final int m1887b(int i, int i2) {
        return i2 < i ? i2 : i;
    }

    public int m1891a() {
        return this.f1888f;
    }

    public Bitmap m1892a(int i) {
        Bitmap createBitmap = Bitmap.createBitmap(this.f1888f, this.f1889g, this.f1892j);
        Canvas canvas = new Canvas(createBitmap);
        if (i != 0) {
            canvas.drawColor(i);
        }
        m1898a(canvas, 0.0f, 0.0f, null, false);
        return createBitmap;
    }

    public void m1893a(float f, float f2, float f3, float f4, Paint paint) {
        int a = TiledBitmapCanvas.m1883a(0, (int) Math.floor((double) ((f - 4.0f) / ((float) this.f1885c))));
        int a2 = TiledBitmapCanvas.m1883a(0, (int) Math.floor((double) ((f2 - 4.0f) / ((float) this.f1885c))));
        int b = TiledBitmapCanvas.m1887b(this.f1890h - 1, (int) Math.floor((double) ((4.0f + f3) / ((float) this.f1885c))));
        int b2 = TiledBitmapCanvas.m1887b(this.f1891i - 1, (int) Math.floor((double) ((4.0f + f4) / ((float) this.f1885c))));
        for (int i = a2; i <= b2; i++) {
            for (int i2 = a; i2 <= b; i2++) {
                TiledBitmapCanvas tiledBitmapCanvas = this.f1887e[(this.f1890h * i) + i2];
                m1885a(tiledBitmapCanvas).drawRect(f, f2, f3, f4, paint);
                tiledBitmapCanvas.f1877e = true;
            }
        }
    }

    public void m1894a(float f, float f2, float f3, Paint paint) {
        float f4 = f3 + 4.0f;
        int a = TiledBitmapCanvas.m1883a(0, (int) Math.floor((double) ((f - f4) / ((float) this.f1885c))));
        int a2 = TiledBitmapCanvas.m1883a(0, (int) Math.floor((double) ((f2 - f4) / ((float) this.f1885c))));
        int b = TiledBitmapCanvas.m1887b(this.f1890h - 1, (int) Math.floor((double) ((f + f4) / ((float) this.f1885c))));
        int b2 = TiledBitmapCanvas.m1887b(this.f1891i - 1, (int) Math.floor((double) ((f4 + f2) / ((float) this.f1885c))));
        for (int i = a2; i <= b2; i++) {
            for (a2 = a; a2 <= b; a2++) {
                TiledBitmapCanvas tiledBitmapCanvas = this.f1887e[(this.f1890h * i) + a2];
                m1885a(tiledBitmapCanvas).drawCircle(f, f2, f3, paint);
                tiledBitmapCanvas.f1877e = true;
            }
        }
    }

    public void m1895a(int i, Mode mode) {
        for (TiledBitmapCanvas tiledBitmapCanvas : this.f1887e) {
            m1885a(tiledBitmapCanvas).drawColor(i, mode);
            tiledBitmapCanvas.f1877e = true;
        }
    }

    public void m1896a(Bitmap bitmap, Matrix matrix, Paint paint) {
        RectF rectF = new RectF(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
        matrix.mapRect(rectF);
        int a = TiledBitmapCanvas.m1883a(0, (int) Math.floor((double) ((rectF.left - 4.0f) / ((float) this.f1885c))));
        int a2 = TiledBitmapCanvas.m1883a(0, (int) Math.floor((double) ((rectF.top - 4.0f) / ((float) this.f1885c))));
        int b = TiledBitmapCanvas.m1887b(this.f1890h - 1, (int) Math.floor((double) ((rectF.right + 4.0f) / ((float) this.f1885c))));
        int b2 = TiledBitmapCanvas.m1887b(this.f1891i - 1, (int) Math.floor((double) ((rectF.bottom + 4.0f) / ((float) this.f1885c))));
        for (int i = a2; i <= b2; i++) {
            for (a2 = a; a2 <= b; a2++) {
                TiledBitmapCanvas tiledBitmapCanvas = this.f1887e[(this.f1890h * i) + a2];
                m1885a(tiledBitmapCanvas).drawBitmap(bitmap, matrix, paint);
                tiledBitmapCanvas.f1877e = true;
            }
        }
    }

    public void m1897a(Bitmap bitmap, Rect rect, RectF rectF, Paint paint) {
        int a = TiledBitmapCanvas.m1883a(0, (int) Math.floor((double) ((rectF.left - 4.0f) / ((float) this.f1885c))));
        int a2 = TiledBitmapCanvas.m1883a(0, (int) Math.floor((double) ((rectF.top - 4.0f) / ((float) this.f1885c))));
        int b = TiledBitmapCanvas.m1887b(this.f1890h - 1, (int) Math.floor((double) ((rectF.right + 4.0f) / ((float) this.f1885c))));
        int b2 = TiledBitmapCanvas.m1887b(this.f1891i - 1, (int) Math.floor((double) ((rectF.bottom + 4.0f) / ((float) this.f1885c))));
        for (int i = a2; i <= b2; i++) {
            for (a2 = a; a2 <= b; a2++) {
                TiledBitmapCanvas tiledBitmapCanvas = this.f1887e[(this.f1890h * i) + a2];
                m1885a(tiledBitmapCanvas).drawBitmap(bitmap, rect, rectF, paint);
                tiledBitmapCanvas.f1877e = true;
            }
        }
    }

    public void m1898a(Canvas canvas, float f, float f2, Paint paint, boolean z) {
        Rect rect = new Rect(0, 0, this.f1885c, this.f1885c);
        Rect rect2 = new Rect(0, 0, this.f1885c, this.f1885c);
        canvas.save();
        canvas.translate(-f, -f2);
        canvas.clipRect(0, 0, this.f1888f, this.f1889g);
        for (int i = 0; i < this.f1891i; i++) {
            for (int i2 = 0; i2 < this.f1890h; i2++) {
                rect2.offsetTo(this.f1885c * i2, this.f1885c * i);
                TiledBitmapCanvas tiledBitmapCanvas = this.f1887e[(this.f1890h * i) + i2];
                if (!z || tiledBitmapCanvas.f1877e) {
                    canvas.drawBitmap(tiledBitmapCanvas.m1880a(), rect, rect2, paint);
                    tiledBitmapCanvas.f1877e = false;
                    if (this.f1884b) {
                        this.f1896q++;
                        f1881n.setColor(f1880a[tiledBitmapCanvas.f1875c % f1880a.length]);
                        canvas.drawRect(rect2, f1881n);
                        canvas.drawText(String.format("%d,%d v%d", new Object[]{Integer.valueOf(tiledBitmapCanvas.f1873a), Integer.valueOf(tiledBitmapCanvas.f1874b), Integer.valueOf(tiledBitmapCanvas.f1875c)}), (float) (rect2.left + 4), (float) (rect2.bottom - 4), f1883p);
                    }
                }
            }
        }
        canvas.restore();
    }

    public void m1899a(boolean z) {
        this.f1884b = z;
    }

    public int m1900b() {
        return this.f1889g;
    }

    public void m1901b(int i) {
        int i2 = (this.f1895m ? this.f1893k : this.f1893k - 1) + i;
        if (i2 < this.f1894l) {
            if (i2 != this.f1894l) {
                i2 = this.f1894l;
            } else {
                return;
            }
        }
        int i3 = this.f1884b ? 0 : 0;
        while (i3 < this.f1887e.length) {
            TiledBitmapCanvas tiledBitmapCanvas = this.f1887e[i3];
            if (tiledBitmapCanvas.f1876d == i2) {
                tiledBitmapCanvas.m1882b(i2);
                tiledBitmapCanvas.f1877e = true;
                i3++;
            } else {
                tiledBitmapCanvas.m1882b(i2);
                tiledBitmapCanvas.f1877e = true;
                i3++;
            }
        }
        this.f1893k = i2 + 1;
        this.f1895m = false;
    }

    public Bitmap m1902c() {
        return m1892a(0);
    }

    public void m1903d() {
        if (this.f1895m) {
            this.f1893k++;
            if (this.f1893k - this.f1894l > this.f1886d) {
                this.f1894l++;
            }
            this.f1895m = false;
        }
    }
}

package com.hanista.mobogram.mobo.lock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PatternView extends View {
    private static int f1460a;
    private final Path f1461A;
    private final Rect f1462B;
    private int f1463C;
    private int f1464D;
    private final Matrix f1465E;
    private final int f1466F;
    private final int f1467G;
    private final int f1468H;
    private int f1469I;
    private final Runnable f1470J;
    private boolean f1471b;
    private final Paint f1472c;
    private final Paint f1473d;
    private C0912b f1474e;
    private ArrayList<Cell> f1475f;
    private boolean[][] f1476g;
    private float f1477h;
    private float f1478i;
    private long f1479j;
    private C0911a f1480k;
    private boolean f1481l;
    private boolean f1482m;
    private boolean f1483n;
    private boolean f1484o;
    private boolean f1485p;
    private final float f1486q;
    private final float f1487r;
    private float f1488s;
    private float f1489t;
    private Bitmap f1490u;
    private Bitmap f1491v;
    private Bitmap f1492w;
    private int f1493x;
    private Bitmap f1494y;
    private Bitmap f1495z;

    /* renamed from: com.hanista.mobogram.mobo.lock.PatternView.1 */
    class C09081 implements Runnable {
        final /* synthetic */ PatternView f1447a;

        C09081(PatternView patternView) {
            this.f1447a = patternView;
        }

        public void run() {
            this.f1447a.m1514c();
        }
    }

    public static class Cell implements Parcelable {
        public static final Creator<Cell> CREATOR;
        static Cell[][] f1448c;
        int f1449a;
        int f1450b;

        /* renamed from: com.hanista.mobogram.mobo.lock.PatternView.Cell.1 */
        static class C09091 implements Creator<Cell> {
            C09091() {
            }

            public Cell m1471a(Parcel parcel) {
                return new Cell(null);
            }

            public Cell[] m1472a(int i) {
                return new Cell[i];
            }

            public /* synthetic */ Object createFromParcel(Parcel parcel) {
                return m1471a(parcel);
            }

            public /* synthetic */ Object[] newArray(int i) {
                return m1472a(i);
            }
        }

        static {
            CREATOR = new C09091();
        }

        private Cell(int i, int i2) {
            m1474b(i, i2);
            this.f1449a = i;
            this.f1450b = i2;
        }

        private Cell(Parcel parcel) {
            m1476a(parcel);
        }

        public static synchronized Cell m1473a(int i, int i2) {
            Cell cell;
            synchronized (Cell.class) {
                m1474b(i, i2);
                cell = f1448c[i][i2];
            }
            return cell;
        }

        private static void m1474b(int i, int i2) {
            if (i < 0 || i > PatternView.f1460a - 1) {
                throw new IllegalArgumentException("row must be in range 0-" + (PatternView.f1460a - 1));
            } else if (i2 < 0 || i2 > PatternView.f1460a - 1) {
                throw new IllegalArgumentException("column must be in range 0-" + (PatternView.f1460a - 1));
            }
        }

        public int m1475a() {
            return this.f1449a;
        }

        public void m1476a(Parcel parcel) {
            this.f1450b = parcel.readInt();
            this.f1449a = parcel.readInt();
        }

        public int m1477b() {
            return this.f1450b;
        }

        public int m1478c() {
            return (this.f1449a * PatternView.f1460a) + this.f1450b;
        }

        public int describeContents() {
            return 0;
        }

        public boolean equals(Object obj) {
            return obj instanceof Cell ? m1477b() == ((Cell) obj).m1477b() && m1475a() == ((Cell) obj).m1475a() : super.equals(obj);
        }

        public String toString() {
            return "(r=" + m1475a() + ",c=" + m1477b() + ")";
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(m1477b());
            parcel.writeInt(m1475a());
        }
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        private final String f1451a;
        private final int f1452b;
        private final boolean f1453c;
        private final boolean f1454d;
        private final boolean f1455e;

        /* renamed from: com.hanista.mobogram.mobo.lock.PatternView.SavedState.1 */
        static class C09101 implements Creator<SavedState> {
            C09101() {
            }

            public SavedState m1479a(Parcel parcel) {
                return new SavedState(null);
            }

            public SavedState[] m1480a(int i) {
                return new SavedState[i];
            }

            public /* synthetic */ Object createFromParcel(Parcel parcel) {
                return m1479a(parcel);
            }

            public /* synthetic */ Object[] newArray(int i) {
                return m1480a(i);
            }
        }

        static {
            CREATOR = new C09101();
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.f1451a = parcel.readString();
            this.f1452b = parcel.readInt();
            this.f1453c = ((Boolean) parcel.readValue(null)).booleanValue();
            this.f1454d = ((Boolean) parcel.readValue(null)).booleanValue();
            this.f1455e = ((Boolean) parcel.readValue(null)).booleanValue();
        }

        private SavedState(Parcelable parcelable, String str, int i, boolean z, boolean z2, boolean z3) {
            super(parcelable);
            this.f1451a = str;
            this.f1452b = i;
            this.f1453c = z;
            this.f1454d = z2;
            this.f1455e = z3;
        }

        public String m1481a() {
            return this.f1451a;
        }

        public int m1482b() {
            return this.f1452b;
        }

        public boolean m1483c() {
            return this.f1453c;
        }

        public boolean m1484d() {
            return this.f1454d;
        }

        public boolean m1485e() {
            return this.f1455e;
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeString(this.f1451a);
            parcel.writeInt(this.f1452b);
            parcel.writeValue(Boolean.valueOf(this.f1453c));
            parcel.writeValue(Boolean.valueOf(this.f1454d));
            parcel.writeValue(Boolean.valueOf(this.f1455e));
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.PatternView.a */
    public enum C0911a {
        Correct,
        Animate,
        Wrong
    }

    /* renamed from: com.hanista.mobogram.mobo.lock.PatternView.b */
    public interface C0912b {
        void m1486a();

        void m1487b();

        void m1488c();

        void m1489d();
    }

    static {
        f1460a = 3;
    }

    public PatternView(Context context) {
        this(context, null);
    }

    public PatternView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f1471b = false;
        this.f1472c = new Paint();
        this.f1473d = new Paint();
        this.f1477h = Face.UNCOMPUTED_PROBABILITY;
        this.f1478i = Face.UNCOMPUTED_PROBABILITY;
        this.f1480k = C0911a.Correct;
        this.f1481l = true;
        this.f1482m = false;
        this.f1483n = false;
        this.f1484o = true;
        this.f1485p = false;
        this.f1486q = 0.1f;
        this.f1487r = 0.6f;
        this.f1493x = C0338R.drawable.pattern_btn_touched;
        this.f1461A = new Path();
        this.f1462B = new Rect();
        this.f1465E = new Matrix();
        this.f1466F = 0;
        this.f1467G = 0;
        this.f1468H = 0;
        this.f1469I = 0;
        this.f1470J = new C09081(this);
        setClickable(true);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0338R.styleable.PatternView);
        try {
            this.f1469I = obtainStyledAttributes.getDimensionPixelSize(0, 0);
            this.f1493x = obtainStyledAttributes.getResourceId(1, C0338R.drawable.pattern_circle_white);
            this.f1473d.setAntiAlias(true);
            this.f1473d.setDither(true);
            this.f1473d.setColor(-1);
            this.f1473d.setAlpha(TLRPC.USER_FLAG_UNUSED);
            this.f1473d.setStyle(Style.STROKE);
            this.f1473d.setStrokeJoin(Join.ROUND);
            this.f1473d.setStrokeCap(Cap.ROUND);
            m1502g();
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    private int m1490a(float f) {
        float f2 = this.f1489t;
        float f3 = f2 * 0.6f;
        float f4 = ((f2 - f3) / 2.0f) + 0.0f;
        for (int i = 0; i < f1460a; i++) {
            float f5 = (((float) i) * f2) + f4;
            if (f >= f5 && f <= f5 + f3) {
                return i;
            }
        }
        return -1;
    }

    private Bitmap m1491a(int i) {
        return BitmapFactory.decodeResource(getContext().getResources(), i);
    }

    private Cell m1492a(float f, float f2) {
        Cell b = m1498b(f, f2);
        if (b == null) {
            return null;
        }
        Cell cell;
        ArrayList arrayList = new ArrayList();
        if (!this.f1475f.isEmpty()) {
            cell = (Cell) this.f1475f.get(this.f1475f.size() - 1);
            int i = b.f1449a - cell.f1449a;
            int i2 = b.f1450b - cell.f1450b;
            int i3 = i > 0 ? 1 : -1;
            int i4 = i2 > 0 ? 1 : -1;
            int i5;
            if (i == 0) {
                for (i5 = 1; i5 < Math.abs(i2); i5++) {
                    arrayList.add(new Cell(cell.f1450b + (i5 * i4), null));
                }
            } else if (i2 == 0) {
                for (i5 = 1; i5 < Math.abs(i); i5++) {
                    arrayList.add(new Cell(cell.f1450b, null));
                }
            } else if (Math.abs(i2) == Math.abs(i)) {
                for (i5 = 1; i5 < Math.abs(i); i5++) {
                    arrayList.add(new Cell(cell.f1450b + (i5 * i4), null));
                }
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            cell = (Cell) it.next();
            if (!(cell == null || this.f1476g[cell.f1449a][cell.f1450b])) {
                m1495a(cell);
            }
        }
        m1495a(b);
        if (this.f1484o) {
            performHapticFeedback(1, 3);
        }
        return b;
    }

    private void m1493a(Canvas canvas, int i, int i2, boolean z) {
        Bitmap bitmap;
        Bitmap bitmap2;
        if (!z || ((this.f1482m && this.f1480k == C0911a.Correct) || (this.f1483n && this.f1480k == C0911a.Wrong))) {
            bitmap = this.f1492w;
            bitmap2 = this.f1490u;
        } else if (this.f1485p) {
            bitmap = this.f1494y;
            bitmap2 = this.f1491v;
        } else if (this.f1480k == C0911a.Wrong) {
            bitmap = this.f1495z;
            bitmap2 = this.f1490u;
        } else if (this.f1480k == C0911a.Correct || this.f1480k == C0911a.Animate) {
            bitmap = this.f1494y;
            bitmap2 = this.f1490u;
        } else {
            throw new IllegalStateException("unknown display mode " + this.f1480k);
        }
        int i3 = this.f1463C;
        int i4 = this.f1464D;
        i3 = (int) ((this.f1488s - ((float) i3)) / 2.0f);
        i4 = (int) ((this.f1489t - ((float) i4)) / 2.0f);
        float min = Math.min(this.f1488s / ((float) this.f1463C), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        float min2 = Math.min(this.f1489t / ((float) this.f1464D), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.f1465E.setTranslate((float) (i3 + i), (float) (i4 + i2));
        this.f1465E.preTranslate((float) (this.f1463C / 2), (float) (this.f1464D / 2));
        this.f1465E.preScale(min, min2);
        this.f1465E.preTranslate((float) ((-this.f1463C) / 2), (float) ((-this.f1464D) / 2));
        canvas.drawBitmap(bitmap, this.f1465E, this.f1472c);
        canvas.drawBitmap(bitmap2, this.f1465E, this.f1472c);
    }

    private void m1494a(MotionEvent motionEvent) {
        int historySize = motionEvent.getHistorySize();
        int i = 0;
        while (i < historySize + 1) {
            float historicalX = i < historySize ? motionEvent.getHistoricalX(i) : motionEvent.getX();
            float historicalY = i < historySize ? motionEvent.getHistoricalY(i) : motionEvent.getY();
            int size = this.f1475f.size();
            Cell a = m1492a(historicalX, historicalY);
            int size2 = this.f1475f.size();
            if (a != null && size2 == 1) {
                this.f1485p = true;
                m1504i();
            }
            if (Math.abs(historicalX - this.f1477h) + Math.abs(historicalY - this.f1478i) > this.f1488s * 0.01f) {
                float f = this.f1477h;
                float f2 = this.f1478i;
                this.f1477h = historicalX;
                this.f1478i = historicalY;
                if (!this.f1485p || size2 <= 0) {
                    invalidate();
                } else {
                    float f3;
                    float f4;
                    float f5;
                    ArrayList arrayList = this.f1475f;
                    float f6 = (this.f1488s * 0.1f) * 0.5f;
                    Cell cell = (Cell) arrayList.get(size2 - 1);
                    float b = m1496b(cell.f1450b);
                    float c = m1500c(cell.f1449a);
                    Rect rect = this.f1462B;
                    if (b < historicalX) {
                        f3 = historicalX;
                        f4 = b;
                    } else {
                        f3 = b;
                        f4 = historicalX;
                    }
                    if (c < historicalY) {
                        historicalX = c;
                    } else {
                        historicalX = historicalY;
                        historicalY = c;
                    }
                    rect.set((int) (f4 - f6), (int) (historicalX - f6), (int) (f3 + f6), (int) (historicalY + f6));
                    if (b < f) {
                        historicalY = f;
                    } else {
                        historicalY = b;
                        b = f;
                    }
                    if (c < f2) {
                        f5 = f2;
                        f2 = c;
                        c = f5;
                    }
                    rect.union((int) (b - f6), (int) (f2 - f6), (int) (historicalY + f6), (int) (c + f6));
                    if (a != null) {
                        historicalX = m1496b(a.f1450b);
                        historicalY = m1500c(a.f1449a);
                        if (size2 >= 2) {
                            cell = (Cell) arrayList.get((size2 - 1) - (size2 - size));
                            f2 = m1496b(cell.f1450b);
                            c = m1500c(cell.f1449a);
                            if (historicalX < f2) {
                                f5 = f2;
                                f2 = historicalX;
                                historicalX = f5;
                            }
                            if (historicalY < c) {
                                f5 = historicalX;
                                historicalX = historicalY;
                                historicalY = f5;
                            } else {
                                f5 = historicalY;
                                historicalY = historicalX;
                                historicalX = c;
                                c = f5;
                            }
                        } else {
                            c = historicalY;
                            f2 = historicalX;
                            f5 = historicalY;
                            historicalY = historicalX;
                            historicalX = f5;
                        }
                        f = this.f1488s / 2.0f;
                        b = this.f1489t / 2.0f;
                        rect.set((int) (f2 - f), (int) (historicalX - b), (int) (historicalY + f), (int) (c + b));
                    }
                    invalidate(rect);
                }
            }
            i++;
        }
    }

    private void m1495a(Cell cell) {
        this.f1476g[cell.m1475a()][cell.m1477b()] = true;
        this.f1475f.add(cell);
        m1503h();
    }

    private float m1496b(int i) {
        return (0.0f + (((float) i) * this.f1488s)) + (this.f1488s / 2.0f);
    }

    private int m1497b(float f) {
        float f2 = this.f1488s;
        float f3 = f2 * 0.6f;
        float f4 = ((f2 - f3) / 2.0f) + 0.0f;
        for (int i = 0; i < f1460a; i++) {
            float f5 = (((float) i) * f2) + f4;
            if (f >= f5 && f <= f5 + f3) {
                return i;
            }
        }
        return -1;
    }

    private Cell m1498b(float f, float f2) {
        int a = m1490a(f2);
        if (a < 0) {
            return null;
        }
        int b = m1497b(f);
        return (b < 0 || this.f1476g[a][b]) ? null : Cell.m1473a(a, b);
    }

    private void m1499b(MotionEvent motionEvent) {
        m1507l();
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Cell a = m1492a(x, y);
        if (a != null) {
            this.f1485p = true;
            this.f1480k = C0911a.Correct;
            m1504i();
        } else {
            this.f1485p = false;
            m1506k();
        }
        if (a != null) {
            float b = m1496b(a.f1450b);
            float c = m1500c(a.f1449a);
            float f = this.f1488s / 2.0f;
            float f2 = this.f1489t / 2.0f;
            invalidate((int) (b - f), (int) (c - f2), (int) (b + f), (int) (c + f2));
        }
        this.f1477h = x;
        this.f1478i = y;
    }

    private float m1500c(int i) {
        return (0.0f + (((float) i) * this.f1489t)) + (this.f1489t / 2.0f);
    }

    private void m1502g() {
        int i = 0;
        this.f1490u = m1491a((int) C0338R.drawable.pattern_btn_touched);
        this.f1491v = this.f1490u;
        this.f1492w = m1491a((int) C0338R.drawable.pattern_button_untouched);
        this.f1494y = m1491a(this.f1493x);
        this.f1495z = m1491a((int) C0338R.drawable.indicator_code_lock_point_area_red_holo);
        Bitmap[] bitmapArr = new Bitmap[]{this.f1490u, this.f1494y, this.f1495z};
        int length = bitmapArr.length;
        while (i < length) {
            Bitmap bitmap = bitmapArr[i];
            this.f1463C = Math.max(this.f1463C, bitmap.getWidth());
            this.f1464D = Math.max(this.f1464D, bitmap.getHeight());
            i++;
        }
    }

    private static int getMatrixSize() {
        return f1460a * f1460a;
    }

    private void m1503h() {
        if (this.f1474e != null) {
            this.f1474e.m1486a();
        }
    }

    private void m1504i() {
        if (this.f1474e != null) {
            this.f1474e.m1489d();
        }
    }

    private void m1505j() {
        if (this.f1474e != null) {
            this.f1474e.m1488c();
        }
    }

    private void m1506k() {
        if (this.f1474e != null) {
            this.f1474e.m1487b();
        }
    }

    private void m1507l() {
        this.f1475f.clear();
        m1508m();
        this.f1480k = C0911a.Correct;
        invalidate();
    }

    private void m1508m() {
        for (int i = 0; i < f1460a; i++) {
            for (int i2 = 0; i2 < f1460a; i2++) {
                this.f1476g[i][i2] = false;
            }
        }
    }

    private void m1509n() {
        if (!this.f1475f.isEmpty()) {
            this.f1485p = false;
            m1505j();
            invalidate();
        }
    }

    void m1510a() {
        this.f1475f = new ArrayList(getMatrixSize());
        this.f1476g = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{f1460a, f1460a});
        Cell.f1448c = (Cell[][]) Array.newInstance(Cell.class, new int[]{f1460a, f1460a});
        for (int i = 0; i < f1460a; i++) {
            for (int i2 = 0; i2 < f1460a; i2++) {
                Cell.f1448c[i][i2] = new Cell(i2, null);
            }
        }
    }

    public void m1511a(long j) {
        m1515d();
        postDelayed(this.f1470J, j);
    }

    void m1512a(C0911a c0911a, List<Cell> list) {
        this.f1475f.clear();
        this.f1475f.addAll(list);
        m1508m();
        for (Cell cell : list) {
            this.f1476g[cell.m1475a()][cell.m1477b()] = true;
        }
        setDisplayMode(c0911a);
    }

    String m1513b() {
        if (this.f1475f == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        int size = this.f1475f.size();
        StringBuilder stringBuilder = new StringBuilder(size);
        int length = String.valueOf(getMatrixSize()).length();
        for (int i = 0; i < size; i++) {
            int c = ((Cell) this.f1475f.get(i)).m1478c();
            stringBuilder.append(String.format("%0" + length + "d", new Object[]{Integer.valueOf(c)}));
        }
        return stringBuilder.toString();
    }

    public void m1514c() {
        m1515d();
        m1507l();
        m1506k();
    }

    public void m1515d() {
        removeCallbacks(this.f1470J);
    }

    public void m1516e() {
        m1514c();
    }

    public float getFingerDistance() {
        return (((float) ((this.f1463C + this.f1464D) / 2)) / ((getResources().getDisplayMetrics().xdpi + getResources().getDisplayMetrics().ydpi) / 2.0f)) * ((float) this.f1475f.size());
    }

    public List<Cell> getPattern() {
        return (List) this.f1475f.clone();
    }

    public String getPatternString() {
        return m1513b();
    }

    protected int getSuggestedMinimumHeight() {
        return f1460a * this.f1463C;
    }

    protected int getSuggestedMinimumWidth() {
        return f1460a * this.f1463C;
    }

    protected void onDraw(Canvas canvas) {
        int elapsedRealtime;
        int i;
        Cell cell;
        Object obj;
        float b;
        float b2;
        ArrayList arrayList = this.f1475f;
        int size = arrayList.size();
        boolean[][] zArr = this.f1476g;
        if (this.f1480k == C0911a.Animate) {
            elapsedRealtime = ((int) (SystemClock.elapsedRealtime() - this.f1479j)) % ((size + 1) * 700);
            int i2 = elapsedRealtime / 700;
            m1508m();
            for (i = 0; i < i2; i++) {
                cell = (Cell) arrayList.get(i);
                zArr[cell.m1475a()][cell.m1477b()] = true;
            }
            obj = (i2 <= 0 || i2 >= size) ? null : 1;
            if (obj != null) {
                float f = ((float) (elapsedRealtime % 700)) / 700.0f;
                cell = (Cell) arrayList.get(i2 - 1);
                b = m1496b(cell.f1450b);
                float c = m1500c(cell.f1449a);
                cell = (Cell) arrayList.get(i2);
                b2 = (m1496b(cell.f1450b) - b) * f;
                float c2 = (m1500c(cell.f1449a) - c) * f;
                this.f1477h = b + b2;
                this.f1478i = c2 + c;
            }
            invalidate();
        }
        b = this.f1488s;
        b2 = this.f1489t;
        this.f1473d.setStrokeWidth((0.1f * b) * 0.5f);
        Path path = this.f1461A;
        path.rewind();
        for (int i3 = 0; i3 < f1460a; i3++) {
            float f2 = (((float) i3) * b2) + 0.0f;
            for (i = 0; i < f1460a; i++) {
                m1493a(canvas, (int) (0.0f + (((float) i) * b)), (int) f2, zArr[i3][i]);
            }
        }
        obj = ((this.f1482m || this.f1480k != C0911a.Correct) && (this.f1483n || this.f1480k != C0911a.Wrong)) ? null : 1;
        boolean z = (this.f1472c.getFlags() & 2) != 0;
        this.f1472c.setFilterBitmap(true);
        if (obj != null) {
            Object obj2 = null;
            for (elapsedRealtime = 0; elapsedRealtime < size; elapsedRealtime++) {
                cell = (Cell) arrayList.get(elapsedRealtime);
                if (!zArr[cell.f1449a][cell.f1450b]) {
                    break;
                }
                obj2 = 1;
                f2 = m1496b(cell.f1450b);
                c2 = m1500c(cell.f1449a);
                if (elapsedRealtime == 0) {
                    path.moveTo(f2, c2);
                } else {
                    path.lineTo(f2, c2);
                }
            }
            if ((this.f1485p || this.f1480k == C0911a.Animate) && r3 != null && size > 1) {
                path.lineTo(this.f1477h, this.f1478i);
            }
            canvas.drawPath(path, this.f1473d);
        }
        this.f1472c.setFilterBitmap(z);
    }

    protected void onMeasure(int i, int i2) {
        int min = Math.min(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
        if (this.f1469I != 0) {
            min = Math.min(min, this.f1469I);
        }
        setMeasuredDimension(min, min);
    }

    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        m1512a(C0911a.Correct, PatternUtils.m1574a(savedState.m1481a()));
        this.f1480k = C0911a.values()[savedState.m1482b()];
        this.f1481l = savedState.m1483c();
        this.f1482m = savedState.m1484d();
        this.f1484o = savedState.m1485e();
    }

    protected Parcelable onSaveInstanceState() {
        return new SavedState(m1513b(), this.f1480k.ordinal(), this.f1481l, this.f1482m, this.f1484o, null);
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        this.f1488s = ((float) ((i + 0) - 0)) / ((float) f1460a);
        this.f1489t = ((float) ((i2 + 0) - 0)) / ((float) f1460a);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.f1481l || !isEnabled()) {
            return false;
        }
        switch (motionEvent.getAction()) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                m1499b(motionEvent);
                return true;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                m1509n();
                return true;
            case VideoPlayer.STATE_PREPARING /*2*/:
                m1494a(motionEvent);
                return true;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                this.f1485p = false;
                m1507l();
                m1506k();
                return true;
            default:
                return false;
        }
    }

    public void setDisplayMode(C0911a c0911a) {
        this.f1480k = c0911a;
        if (c0911a == C0911a.Animate) {
            if (this.f1475f.size() == 0) {
                throw new IllegalStateException("you must have a pattern to animate if you want to set the display mode to animate");
            }
            this.f1479j = SystemClock.elapsedRealtime();
            Cell cell = (Cell) this.f1475f.get(0);
            this.f1477h = m1496b(cell.m1477b());
            this.f1478i = m1500c(cell.m1475a());
            m1508m();
        }
        invalidate();
    }

    public void setInErrorStealthMode(boolean z) {
        this.f1483n = z;
    }

    public void setInStealthMode(boolean z) {
        this.f1482m = z;
    }

    public void setOnPatternListener(C0912b c0912b) {
        this.f1474e = c0912b;
    }

    public void setSelectedBitmap(int i) {
        this.f1493x = i;
        m1502g();
    }

    public void setSize(int i) {
        f1460a = i;
        m1510a();
    }

    public void setTactileFeedbackEnabled(boolean z) {
        this.f1484o = z;
    }
}

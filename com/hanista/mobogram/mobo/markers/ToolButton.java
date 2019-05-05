package com.hanista.mobogram.mobo.markers;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.InputDeviceCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.VideoPlayer;

public class ToolButton extends View implements OnClickListener, OnLongClickListener {
    private static boolean f1763e;
    protected Paint f1764a;
    protected ColorStateList f1765b;
    protected ColorStateList f1766c;
    SharedPreferences f1767d;
    private C0914a f1768f;

    /* renamed from: com.hanista.mobogram.mobo.markers.ToolButton.a */
    public static class C0914a {
        public void m1737a(ToolButton toolButton) {
        }

        public void m1738a(ToolButton toolButton, float f, float f2) {
        }

        public void m1739a(ToolButton toolButton, int i) {
        }

        public void m1740b(ToolButton toolButton) {
        }

        public void m1741b(ToolButton toolButton, int i) {
        }

        public void m1742c(ToolButton toolButton) {
        }

        public void m1743c(ToolButton toolButton, int i) {
        }
    }

    public static class PenToolButton extends ToolButton {
        public float f1769e;
        public float f1770f;

        public PenToolButton(Context context, AttributeSet attributeSet) {
            this(context, attributeSet, 0);
        }

        public PenToolButton(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            if (!isInEditMode()) {
                TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0338R.styleable.PenToolButton, i, 0);
                m1805a(this.d.getFloat(getId() + ":min", obtainStyledAttributes.getDimension(1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)), this.d.getFloat(getId() + ":max", obtainStyledAttributes.getDimension(0, 10.0f)));
                obtainStyledAttributes.recycle();
            }
        }

        public void m1805a(float f, float f2) {
            if (f != this.f1769e || f2 != this.f1770f) {
                this.f1769e = f;
                this.f1770f = f2;
                invalidate();
                if (isSelected()) {
                    m1806b();
                }
                Editor edit = this.d.edit();
                edit.putFloat(getId() + ":min", f);
                edit.putFloat(getId() + ":max", f2);
                if (VERSION.SDK_INT >= 9) {
                    edit.apply();
                } else {
                    edit.commit();
                }
            }
        }

        void m1806b() {
            super.m1802b();
            C0914a callback = getCallback();
            if (callback != null) {
                callback.m1738a(this, this.f1769e, this.f1770f);
            }
        }

        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            this.a.setColor(this.b.getColorForState(getDrawableState(), this.b.getDefaultColor()));
            Object obj = getHeight() > getWidth() ? 1 : null;
            float f = this.f1769e * 0.5f;
            float f2 = 0.5f * this.f1770f;
            float width = (float) ((obj != null ? getWidth() : getHeight()) / 2);
            float f3 = f > width ? width : f;
            float f4 = f2 > width ? width : f2;
            float paddingTop = ((float) (obj != null ? getPaddingTop() : getPaddingLeft())) + f3;
            f = ((float) (obj != null ? getHeight() - getPaddingBottom() : getWidth() - getPaddingRight())) - f4;
            float height = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / ((float) (obj != null ? getHeight() : getWidth()));
            float f5 = (width - f4) * 0.5f;
            for (float f6 = 0.0f; f6 < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT; f6 += height) {
                float a = Slate.m1839a(paddingTop, f, f6);
                f2 = (float) (((double) width) + (((double) f5) * Math.sin(((double) (2.0f * f6)) * 3.141592653589793d)));
                float a2 = Slate.m1839a(f3, f4, f6);
                float f7 = obj != null ? f2 : a;
                if (obj == null) {
                    a = f2;
                }
                canvas.drawCircle(f7, a, a2, this.a);
            }
            canvas.drawCircle(obj != null ? width : f, obj != null ? f : width, f4, this.a);
            if (f4 == width) {
                this.a.setColor(-1);
                this.a.setTextAlign(Align.CENTER);
                this.a.setTextSize(f4 / 2.0f);
                String format = String.format("%.0f", new Object[]{Float.valueOf(this.f1770f)});
                a = obj != null ? width : f;
                f7 = -5.0f + (f4 / 4.0f);
                if (obj == null) {
                    f = width;
                }
                canvas.drawText(format, a, f7 + f, this.a);
            }
        }

        public boolean onLongClick(View view) {
            Builder builder = new Builder(getContext());
            View inflate = inflate(getContext(), C0338R.layout.pen_editor, null);
            builder.setView(inflate);
            ((PenWidthEditorView) inflate.findViewById(C0338R.id.editor)).setTool((PenToolButton) view);
            builder.create().show();
            return true;
        }
    }

    public static class PenTypeButton extends ToolButton {
        public int f1771e;
        public Bitmap f1772f;
        public Rect f1773g;
        private RectF f1774h;

        public PenTypeButton(Context context, AttributeSet attributeSet) {
            this(context, attributeSet, 0);
        }

        public PenTypeButton(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.f1774h = new RectF();
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0338R.styleable.PenTypeButton, i, 0);
            this.f1771e = obtainStyledAttributes.getInt(0, 0);
            obtainStyledAttributes.recycle();
        }

        void m1807b() {
            super.m1802b();
            C0914a callback = getCallback();
            if (callback != null) {
                callback.m1741b(this, this.f1771e);
            }
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.f1771e == 2) {
                this.f1772f = BitmapFactory.decodeResource(getResources(), C0338R.drawable.airbrush_dark);
                if (this.f1772f == null) {
                    throw new RuntimeException("PenTypeButton: could not load airbrush bitmap");
                }
                this.f1773g = new Rect(0, 0, this.f1772f.getWidth(), this.f1772f.getHeight());
            } else if (this.f1771e == 3) {
                this.f1772f = BitmapFactory.decodeResource(getResources(), C0338R.drawable.fountainpen);
                if (this.f1772f == null) {
                    throw new RuntimeException("PenTypeButton: could not load fountainpen bitmap");
                }
                this.f1773g = new Rect(0, 0, this.f1772f.getWidth(), this.f1772f.getHeight());
            }
        }

        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.a != null) {
                float width = ((float) getWidth()) * 0.5f;
                float height = ((float) getHeight()) * 0.5f;
                float min = ((float) Math.min((getWidth() - getPaddingLeft()) - getPaddingRight(), (getHeight() - getPaddingTop()) - getPaddingBottom())) * 0.5f;
                int colorForState = this.b.getColorForState(getDrawableState(), this.b.getDefaultColor());
                this.a.setColor(colorForState);
                this.a.setColorFilter(new PorterDuffColorFilter(colorForState, Mode.SRC_ATOP));
                this.f1774h.set(width - min, height - min, width + min, height + min);
                switch (this.f1771e) {
                    case VideoPlayer.TYPE_AUDIO /*1*/:
                        this.a.setAlpha(TLRPC.USER_FLAG_UNUSED);
                        canvas.drawCircle(width, height, min, this.a);
                    case VideoPlayer.STATE_PREPARING /*2*/:
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        this.a.setAlpha(NalUnitUtil.EXTENDED_SAR);
                        if (this.f1772f != null) {
                            canvas.drawBitmap(this.f1772f, this.f1773g, this.f1774h, this.a);
                        }
                    default:
                        this.a.setAlpha(NalUnitUtil.EXTENDED_SAR);
                        canvas.drawCircle(width, height, min, this.a);
                }
            }
        }
    }

    public static class SwatchButton extends ToolButton {
        public int f1775e;
        final int f1776f;
        final int f1777g;
        private Drawable f1778h;

        public SwatchButton(Context context, AttributeSet attributeSet) {
            this(context, attributeSet, 0);
        }

        public SwatchButton(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.f1776f = -1;
            this.f1777g = -4144960;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0338R.styleable.SwatchButton, i, 0);
            this.f1775e = obtainStyledAttributes.getColor(0, InputDeviceCompat.SOURCE_ANY);
            obtainStyledAttributes.recycle();
        }

        void m1808b() {
            super.m1802b();
            C0914a callback = getCallback();
            if (callback != null) {
                callback.m1739a(this, this.f1775e);
            }
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.f1778h = getResources().getDrawable(C0338R.drawable.transparent_tool);
        }

        public void onDraw(Canvas canvas) {
            int i = -1;
            super.onDraw(canvas);
            if (this.a != null) {
                int paddingLeft = getPaddingLeft();
                if ((this.f1775e & Theme.MSG_TEXT_COLOR) == 0) {
                    this.f1778h.setBounds(canvas.getClipBounds());
                    this.f1778h.draw(canvas);
                } else {
                    canvas.drawColor(this.f1775e);
                }
                if (isSelected() || isPressed()) {
                    this.a.setStyle(Style.STROKE);
                    this.a.setStrokeWidth((float) paddingLeft);
                    Paint paint = this.a;
                    if (this.f1775e == -1) {
                        i = -4144960;
                    }
                    paint.setColor(i);
                    i = paddingLeft / 2;
                    canvas.drawRect((float) i, (float) i, (float) (getWidth() - i), (float) (getHeight() - i), this.a);
                }
            }
        }

        public boolean onLongClick(View view) {
            C0914a callback = getCallback();
            if (callback != null) {
                callback.m1743c(this, this.f1775e);
            }
            return true;
        }
    }

    public static class ZoomToolButton extends ToolButton {
        public Bitmap f1779e;
        public Rect f1780f;
        public final RectF f1781g;

        public ZoomToolButton(Context context, AttributeSet attributeSet) {
            this(context, attributeSet, 0);
        }

        public ZoomToolButton(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.f1781g = new RectF();
            this.f1779e = BitmapFactory.decodeResource(getResources(), C0338R.drawable.grabber);
            this.f1780f = new Rect(0, 0, this.f1779e.getWidth(), this.f1779e.getHeight());
        }

        void m1809b() {
            super.m1802b();
            C0914a callback = getCallback();
            if (callback != null) {
                callback.m1740b(this);
            }
        }

        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.a != null) {
                float width = ((float) getWidth()) * 0.5f;
                float height = ((float) getHeight()) * 0.5f;
                float min = ((float) Math.min((getWidth() - getPaddingLeft()) - getPaddingRight(), (getHeight() - getPaddingTop()) - getPaddingBottom())) * 0.5f;
                int colorForState = this.b.getColorForState(getDrawableState(), this.b.getDefaultColor());
                this.a.setColor(colorForState);
                this.a.setColorFilter(new PorterDuffColorFilter(colorForState, Mode.SRC_ATOP));
                this.f1781g.set(width - min, height - min, width + min, height + min);
                canvas.drawBitmap(this.f1779e, this.f1780f, this.f1781g, this.a);
            }
        }

        public boolean onLongClick(View view) {
            C0914a callback = getCallback();
            if (callback != null) {
                callback.m1742c(this);
            }
            return true;
        }
    }

    static {
        f1763e = false;
    }

    public ToolButton(Context context) {
        this(context, null, 0);
    }

    public ToolButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ToolButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f1767d = context.getSharedPreferences("ToolButton", 0);
        setClickable(true);
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    public void m1801a() {
        m1802b();
        m1804d();
    }

    void m1802b() {
    }

    void m1803c() {
        setSelected(false);
        setPressed(false);
    }

    void m1804d() {
        setPressed(false);
        setSelected(true);
    }

    C0914a getCallback() {
        return this.f1768f;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.f1764a = new Paint(1);
        this.f1765b = getResources().getColorStateList(C0338R.color.pentool_fg);
        this.f1766c = getResources().getColorStateList(C0338R.color.pentool_bg);
    }

    public void onClick(View view) {
        m1802b();
        m1804d();
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(this.f1766c.getColorForState(getDrawableState(), this.f1766c.getDefaultColor()));
    }

    public boolean onLongClick(View view) {
        return false;
    }

    void setCallback(C0914a c0914a) {
        this.f1768f = c0914a;
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        invalidate();
    }

    public void setPressed(boolean z) {
        super.setPressed(z);
        invalidate();
    }
}

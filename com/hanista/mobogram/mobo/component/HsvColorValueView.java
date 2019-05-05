package com.hanista.mobogram.mobo.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class HsvColorValueView extends FrameLayout {
    private Paint f365a;
    private Shader f366b;
    private Shader f367c;
    private float f368d;
    private Bitmap f369e;
    private Drawable f370f;
    private ImageView f371g;
    private int f372h;
    private float f373i;
    private float f374j;
    private C0902a f375k;
    private boolean f376l;

    /* renamed from: com.hanista.mobogram.mobo.component.HsvColorValueView.a */
    public interface C0902a {
        void m451a(HsvColorValueView hsvColorValueView, float f, float f2, boolean z);
    }

    public HsvColorValueView(Context context) {
        super(context);
        this.f368d = 0.0f;
        this.f369e = null;
        this.f372h = -1;
        this.f373i = 0.0f;
        this.f374j = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.f376l = false;
        m453a();
    }

    public HsvColorValueView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f368d = 0.0f;
        this.f369e = null;
        this.f372h = -1;
        this.f373i = 0.0f;
        this.f374j = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.f376l = false;
        m453a();
    }

    public HsvColorValueView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f368d = 0.0f;
        this.f369e = null;
        this.f372h = -1;
        this.f373i = 0.0f;
        this.f374j = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.f376l = false;
        m453a();
    }

    private int m452a(int i) {
        return i - (getBackgroundOffset() * 2);
    }

    private void m453a() {
        this.f370f = getContext().getResources().getDrawable(C0338R.drawable.color_selector);
        this.f371g = new ImageView(getContext());
        this.f371g.setImageDrawable(this.f370f);
        addView(this.f371g, new LayoutParams(this.f370f.getIntrinsicWidth(), this.f370f.getIntrinsicHeight()));
        setWillNotDraw(false);
    }

    private void m454a(int i, int i2, boolean z) {
        int backgroundOffset = getBackgroundOffset();
        this.f373i = ((float) (i - backgroundOffset)) / ((float) getBackgroundSize());
        this.f374j = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - (((float) (i2 - backgroundOffset)) / ((float) getBackgroundSize()));
        m455a(z);
    }

    private void m455a(boolean z) {
        if (this.f375k != null) {
            this.f375k.m451a(this, this.f373i, this.f374j, z);
        }
    }

    private void m456b() {
        if (this.f365a == null) {
            this.f365a = new Paint();
        }
        int height = getHeight();
        if (height <= 0) {
            height = getMeasuredHeight();
        }
        if (height <= 0) {
            height = this.f372h;
        }
        int a = m452a(height);
        if (this.f369e == null && a > 0) {
            this.f366b = new LinearGradient(0.0f, 0.0f, 0.0f, (float) a, -1, Theme.MSG_TEXT_COLOR, TileMode.CLAMP);
            float f = (float) a;
            float f2 = 0.0f;
            float f3 = 0.0f;
            this.f367c = new LinearGradient(0.0f, f2, f, f3, -1, Color.HSVToColor(new float[]{DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, this.f368d}), TileMode.CLAMP);
            this.f365a.setShader(new ComposeShader(this.f366b, this.f367c, Mode.MULTIPLY));
            this.f369e = Bitmap.createBitmap(a, a, Config.ARGB_8888);
            new Canvas(this.f369e).drawRect(0.0f, 0.0f, (float) a, (float) a, this.f365a);
        }
    }

    private void m457b(int i, int i2, boolean z) {
        m454a(i, i2, z);
        m458c();
    }

    private void m458c() {
        int backgroundOffset = getBackgroundOffset();
        int ceil = (int) Math.ceil((double) (((float) this.f371g.getHeight()) / 2.0f));
        int max = (Math.max(0, Math.min(getBackgroundSize(), (int) (((float) getBackgroundSize()) * this.f373i))) + backgroundOffset) - ceil;
        backgroundOffset = (backgroundOffset + Math.max(0, Math.min(getBackgroundSize(), (int) (((float) getBackgroundSize()) * (DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.f374j))))) - ceil;
        this.f371g.layout(max, backgroundOffset, this.f371g.getWidth() + max, this.f371g.getHeight() + backgroundOffset);
    }

    private void m459d() {
        if (this.f369e != null) {
            m458c();
        }
    }

    public int getBackgroundOffset() {
        return (int) Math.ceil((double) (((float) this.f370f.getIntrinsicHeight()) / 2.0f));
    }

    public int getBackgroundSize() {
        m456b();
        return this.f369e != null ? this.f369e.getHeight() : 0;
    }

    public float getSaturation() {
        return this.f373i;
    }

    public float getValue() {
        return this.f374j;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        m456b();
        canvas.drawBitmap(this.f369e, (float) getBackgroundOffset(), (float) getBackgroundOffset(), this.f365a);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        m458c();
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.f372h = Math.min(getMeasuredHeight(), getMeasuredWidth());
        setMeasuredDimension(this.f372h, this.f372h);
        if (this.f369e != null && this.f369e.getHeight() != m452a(this.f372h)) {
            this.f369e.recycle();
            this.f369e = null;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.f376l = true;
            return true;
        } else if (motionEvent.getAction() == 1) {
            this.f376l = false;
            m457b(((int) motionEvent.getX()) - getBackgroundOffset(), ((int) motionEvent.getY()) - getBackgroundOffset(), true);
            return true;
        } else if (motionEvent.getAction() != 2 || !this.f376l) {
            return super.onTouchEvent(motionEvent);
        } else {
            m457b(((int) motionEvent.getX()) - getBackgroundOffset(), ((int) motionEvent.getY()) - getBackgroundOffset(), false);
            return true;
        }
    }

    public void setHue(float f) {
        this.f368d = f;
        this.f369e = null;
        invalidate();
    }

    public void setOnSaturationOrValueChanged(C0902a c0902a) {
        this.f375k = c0902a;
    }

    public void setSaturation(float f) {
        this.f373i = f;
        m459d();
    }

    public void setValue(float f) {
        this.f374j = f;
        m459d();
    }
}

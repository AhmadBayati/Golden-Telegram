package com.hanista.mobogram.mobo.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.ui.ActionBar.Theme;

public class HsvAlphaSelectorView extends LinearLayout {
    private Drawable f356a;
    private ImageView f357b;
    private int f358c;
    private ImageView f359d;
    private int f360e;
    private int f361f;
    private boolean f362g;
    private C0901a f363h;
    private boolean f364i;

    /* renamed from: com.hanista.mobogram.mobo.component.HsvAlphaSelectorView.a */
    public interface C0901a {
        void m445a(HsvAlphaSelectorView hsvAlphaSelectorView, int i);
    }

    public HsvAlphaSelectorView(Context context) {
        super(context);
        this.f358c = 0;
        this.f360e = 0;
        this.f361f = -1;
        this.f362g = true;
        this.f364i = false;
        m446a();
    }

    public HsvAlphaSelectorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f358c = 0;
        this.f360e = 0;
        this.f361f = -1;
        this.f362g = true;
        this.f364i = false;
        m446a();
    }

    private void m446a() {
        this.f356a = getContext().getResources().getDrawable(C0338R.drawable.color_seekselector);
        m447b();
    }

    private void m447b() {
        setOrientation(0);
        setGravity(1);
        setWillNotDraw(false);
        this.f357b = new ImageView(getContext());
        this.f357b.setImageDrawable(this.f356a);
        addView(this.f357b, new LayoutParams(this.f356a.getIntrinsicWidth(), this.f356a.getIntrinsicHeight()));
        this.f359d = new ImageView(getContext());
        this.f359d.setBackgroundDrawable(getContext().getResources().getDrawable(C0338R.drawable.transparentbackrepeat));
        this.f359d.setScaleType(ScaleType.FIT_XY);
        ViewGroup.LayoutParams layoutParams = new LayoutParams(-1, -1);
        layoutParams.setMargins(0, getOffset(), 0, getSelectorOffset());
        addView(this.f359d, layoutParams);
    }

    private void m448c() {
        int height = (int) ((((float) (255 - this.f360e)) / 255.0f) * ((float) this.f359d.getHeight()));
        int selectorOffset = getSelectorOffset();
        int top = this.f359d.getTop();
        this.f357b.layout(0, (height + top) - selectorOffset, this.f357b.getWidth(), ((height + top) - selectorOffset) + this.f357b.getHeight());
    }

    private void m449d() {
        if (this.f359d.getHeight() <= 0) {
            this.f362g = true;
            invalidate();
            return;
        }
        Paint paint = new Paint();
        if (null == null) {
            int i = this.f361f & ViewCompat.MEASURED_SIZE_MASK;
            float f = 0.0f;
            float f2 = 0.0f;
            paint.setShader(new LinearGradient(0.0f, (float) this.f359d.getHeight(), f, f2, i, this.f361f | Theme.MSG_TEXT_COLOR, TileMode.CLAMP));
            Bitmap createBitmap = Bitmap.createBitmap(this.f359d.getWidth(), this.f359d.getHeight(), Config.ARGB_8888);
            new Canvas(createBitmap).drawRect(0.0f, 0.0f, (float) this.f359d.getWidth(), (float) this.f359d.getHeight(), paint);
            this.f359d.setImageBitmap(createBitmap);
        }
    }

    private void m450e() {
        if (this.f363h != null) {
            this.f363h.m445a(this, this.f360e);
        }
    }

    private int getOffset() {
        return Math.max(this.f358c, (int) Math.ceil(((double) this.f356a.getIntrinsicHeight()) / 2.0d));
    }

    private int getSelectorOffset() {
        return (int) Math.ceil((double) (((float) this.f357b.getHeight()) / 2.0f));
    }

    private void setPosition(int i) {
        this.f360e = 255 - Math.min(NalUnitUtil.EXTENDED_SAR, Math.max(0, (int) ((((float) (i - this.f359d.getTop())) / ((float) this.f359d.getHeight())) * 255.0f)));
        m448c();
        m450e();
    }

    public float getAlpha() {
        return (float) this.f360e;
    }

    protected void onDraw(Canvas canvas) {
        if (this.f362g) {
            this.f362g = false;
            m449d();
        }
        super.onDraw(canvas);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        m448c();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.f364i = true;
            setPosition((int) motionEvent.getY());
            return true;
        } else if (motionEvent.getAction() == 1) {
            this.f364i = false;
            return true;
        } else if (!this.f364i || motionEvent.getAction() != 2) {
            return super.onTouchEvent(motionEvent);
        } else {
            setPosition((int) motionEvent.getY());
            return true;
        }
    }

    public void setAlpha(int i) {
        if (this.f360e != i) {
            this.f360e = i;
            m448c();
        }
    }

    public void setColor(int i) {
        if (this.f361f != i) {
            this.f361f = i;
            m449d();
        }
    }

    public void setMinContentOffset(int i) {
        this.f358c = i;
        ViewGroup.LayoutParams layoutParams = new LayoutParams(this.f359d.getLayoutParams());
        layoutParams.setMargins(0, getOffset(), 0, getSelectorOffset());
        this.f359d.setLayoutParams(layoutParams);
    }

    public void setOnAlphaChangedListener(C0901a c0901a) {
        this.f363h = c0901a;
    }
}

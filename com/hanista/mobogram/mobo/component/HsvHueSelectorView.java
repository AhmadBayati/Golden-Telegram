package com.hanista.mobogram.mobo.component;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.hanista.mobogram.C0338R;

public class HsvHueSelectorView extends LinearLayout {
    private Drawable f377a;
    private ImageView f378b;
    private int f379c;
    private ImageView f380d;
    private float f381e;
    private C0903a f382f;
    private boolean f383g;

    /* renamed from: com.hanista.mobogram.mobo.component.HsvHueSelectorView.a */
    public interface C0903a {
        void m460a(HsvHueSelectorView hsvHueSelectorView, float f);
    }

    public HsvHueSelectorView(Context context) {
        super(context);
        this.f379c = 0;
        this.f381e = 0.0f;
        this.f383g = false;
        m461a();
    }

    public HsvHueSelectorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f379c = 0;
        this.f381e = 0.0f;
        this.f383g = false;
        m461a();
    }

    private void m461a() {
        this.f377a = getContext().getResources().getDrawable(C0338R.drawable.color_seekselector);
        m462b();
    }

    private void m462b() {
        setOrientation(0);
        setGravity(1);
        this.f378b = new ImageView(getContext());
        this.f378b.setImageDrawable(this.f377a);
        addView(this.f378b, new LayoutParams(this.f377a.getIntrinsicWidth(), this.f377a.getIntrinsicHeight()));
        this.f380d = new ImageView(getContext());
        this.f380d.setImageDrawable(getContext().getResources().getDrawable(C0338R.drawable.color_hue));
        this.f380d.setScaleType(ScaleType.FIT_XY);
        ViewGroup.LayoutParams layoutParams = new LayoutParams(-1, -1);
        layoutParams.setMargins(0, getOffset(), 0, getSelectorOffset());
        addView(this.f380d, layoutParams);
    }

    private void m463c() {
        int height = (int) (((360.0f - this.f381e) / 360.0f) * ((float) this.f380d.getHeight()));
        this.f378b.layout(0, (getOffset() + height) - getSelectorOffset(), this.f378b.getWidth(), ((height + getOffset()) - getSelectorOffset()) + this.f378b.getHeight());
    }

    private void m464d() {
        if (this.f382f != null) {
            this.f382f.m460a(this, this.f381e);
        }
    }

    private int getOffset() {
        return Math.max(this.f379c, getSelectorOffset());
    }

    private int getSelectorOffset() {
        return (int) Math.ceil((double) (((float) this.f377a.getIntrinsicHeight()) / 2.0f));
    }

    private void setPosition(int i) {
        this.f381e = Math.max(Math.min(360.0f - ((((float) (i - getOffset())) / ((float) this.f380d.getHeight())) * 360.0f), 360.0f), 0.0f);
        m463c();
        m464d();
    }

    public float getHue() {
        return this.f381e;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        m463c();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.f383g = true;
            setPosition((int) motionEvent.getY());
            return true;
        } else if (motionEvent.getAction() == 1) {
            this.f383g = false;
            return true;
        } else if (!this.f383g || motionEvent.getAction() != 2) {
            return super.onTouchEvent(motionEvent);
        } else {
            setPosition((int) motionEvent.getY());
            return true;
        }
    }

    public void setHue(float f) {
        if (this.f381e != f) {
            this.f381e = f;
            m463c();
        }
    }

    public void setMinContentOffset(int i) {
        this.f379c = i;
        ViewGroup.LayoutParams layoutParams = new LayoutParams(this.f380d.getLayoutParams());
        layoutParams.setMargins(0, getOffset(), 0, getSelectorOffset());
        this.f380d.setLayoutParams(layoutParams);
    }

    public void setOnHueChangedListener(C0903a c0903a) {
        this.f382f = c0903a;
    }
}

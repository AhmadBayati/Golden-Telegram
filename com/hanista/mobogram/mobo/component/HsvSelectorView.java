package com.hanista.mobogram.mobo.component;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.mobo.component.HsvAlphaSelectorView.C0901a;
import com.hanista.mobogram.mobo.component.HsvColorValueView.C0902a;
import com.hanista.mobogram.mobo.component.HsvHueSelectorView.C0903a;
import com.hanista.mobogram.ui.ActionBar.Theme;

/* renamed from: com.hanista.mobogram.mobo.component.d */
public class HsvSelectorView extends LinearLayout {
    private HsvAlphaSelectorView f422a;
    private HsvHueSelectorView f423b;
    private HsvColorValueView f424c;
    private int f425d;
    private HsvSelectorView f426e;

    /* renamed from: com.hanista.mobogram.mobo.component.d.a */
    public interface HsvSelectorView {
        void m420a(int i);
    }

    /* renamed from: com.hanista.mobogram.mobo.component.d.1 */
    class HsvSelectorView implements C0901a {
        final /* synthetic */ HsvSelectorView f419a;

        HsvSelectorView(HsvSelectorView hsvSelectorView) {
            this.f419a = hsvSelectorView;
        }

        public void m482a(HsvAlphaSelectorView hsvAlphaSelectorView, int i) {
            this.f419a.m489a(this.f419a.m486a(true), true);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.d.2 */
    class HsvSelectorView implements C0902a {
        final /* synthetic */ HsvSelectorView f420a;

        HsvSelectorView(HsvSelectorView hsvSelectorView) {
            this.f420a = hsvSelectorView;
        }

        public void m483a(HsvColorValueView hsvColorValueView, float f, float f2, boolean z) {
            this.f420a.f422a.setColor(this.f420a.m486a(false));
            this.f420a.m489a(this.f420a.m486a(true), z);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.d.3 */
    class HsvSelectorView implements C0903a {
        final /* synthetic */ HsvSelectorView f421a;

        HsvSelectorView(HsvSelectorView hsvSelectorView) {
            this.f421a = hsvSelectorView;
        }

        public void m484a(HsvHueSelectorView hsvHueSelectorView, float f) {
            this.f421a.f424c.setHue(f);
            this.f421a.f422a.setColor(this.f421a.m486a(false));
            this.f421a.m489a(this.f421a.m486a(true), true);
        }
    }

    public HsvSelectorView(Context context) {
        super(context);
        m488a();
    }

    private int m486a(boolean z) {
        return Color.HSVToColor(z ? (int) this.f422a.getAlpha() : NalUnitUtil.EXTENDED_SAR, new float[]{this.f423b.getHue(), this.f424c.getSaturation(), this.f424c.getValue()});
    }

    private void m488a() {
        m492b();
    }

    private void m489a(int i, boolean z) {
        this.f425d = i;
        if (z) {
            m493c();
        }
    }

    private void m492b() {
        View inflate = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0338R.layout.color_hsvview, null);
        addView(inflate, new LayoutParams(-1, -1));
        this.f422a = (HsvAlphaSelectorView) inflate.findViewById(C0338R.id.color_hsv_alpha);
        this.f424c = (HsvColorValueView) inflate.findViewById(C0338R.id.color_hsv_value);
        this.f423b = (HsvHueSelectorView) inflate.findViewById(C0338R.id.color_hsv_hue);
        this.f422a.setOnAlphaChangedListener(new HsvSelectorView(this));
        this.f424c.setOnSaturationOrValueChanged(new HsvSelectorView(this));
        this.f423b.setOnHueChangedListener(new HsvSelectorView(this));
        setColor(Theme.MSG_TEXT_COLOR);
    }

    private void m493c() {
        if (this.f426e != null) {
            this.f426e.m420a(this.f425d);
        }
    }

    public int getColor() {
        return this.f425d;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        ViewGroup.LayoutParams layoutParams = new LayoutParams(this.f422a.getLayoutParams());
        ViewGroup.LayoutParams layoutParams2 = new LayoutParams(this.f423b.getLayoutParams());
        layoutParams.height = this.f424c.getHeight();
        layoutParams2.height = this.f424c.getHeight();
        this.f423b.setMinContentOffset(this.f424c.getBackgroundOffset());
        this.f422a.setMinContentOffset(this.f424c.getBackgroundOffset());
        this.f422a.setLayoutParams(layoutParams);
        this.f423b.setLayoutParams(layoutParams2);
        super.onMeasure(i, i2);
    }

    public void setColor(int i) {
        boolean z = true;
        this.f422a.setAlpha(Color.alpha(i));
        float[] fArr = new float[3];
        Color.colorToHSV(Theme.MSG_TEXT_COLOR | i, fArr);
        this.f423b.setHue(fArr[0]);
        this.f424c.setHue(fArr[0]);
        this.f424c.setSaturation(fArr[1]);
        this.f424c.setValue(fArr[2]);
        this.f422a.setColor(i);
        if (this.f425d == i) {
            z = false;
        }
        m489a(i, z);
    }

    public void setOnColorChangedListener(HsvSelectorView hsvSelectorView) {
        this.f426e = hsvSelectorView;
    }
}

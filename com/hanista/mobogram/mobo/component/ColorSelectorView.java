package com.hanista.mobogram.mobo.component;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.mobo.component.HexSelectorView.HexSelectorView;
import com.hanista.mobogram.mobo.component.HsvSelectorView.HsvSelectorView;
import com.hanista.mobogram.mobo.component.RgbSelectorView.RgbSelectorView;

public class ColorSelectorView extends LinearLayout {
    private RgbSelectorView f343a;
    private HsvSelectorView f344b;
    private HexSelectorView f345c;
    private TabHost f346d;
    private int f347e;
    private int f348f;
    private int f349g;
    private C0898b f350h;

    /* renamed from: com.hanista.mobogram.mobo.component.ColorSelectorView.1 */
    class C08941 implements HsvSelectorView {
        final /* synthetic */ ColorSelectorView f339a;

        C08941(ColorSelectorView colorSelectorView) {
            this.f339a = colorSelectorView;
        }

        public void m421a(int i) {
            this.f339a.setColor(i);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.ColorSelectorView.2 */
    class C08952 implements RgbSelectorView {
        final /* synthetic */ ColorSelectorView f340a;

        C08952(ColorSelectorView colorSelectorView) {
            this.f340a = colorSelectorView;
        }

        public void m423a(int i) {
            this.f340a.setColor(i);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.ColorSelectorView.3 */
    class C08963 implements HexSelectorView {
        final /* synthetic */ ColorSelectorView f341a;

        C08963(ColorSelectorView colorSelectorView) {
            this.f341a = colorSelectorView;
        }

        public void m425a(int i) {
            this.f341a.setColor(i);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.ColorSelectorView.a */
    class C0897a implements TabContentFactory {
        final /* synthetic */ ColorSelectorView f342a;

        C0897a(ColorSelectorView colorSelectorView) {
            this.f342a = colorSelectorView;
        }

        public View createTabContent(String str) {
            return "HSV".equals(str) ? this.f342a.f344b : "RGB".equals(str) ? this.f342a.f343a : "HEX".equals(str) ? this.f342a.f345c : null;
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.ColorSelectorView.b */
    public interface C0898b {
        void m426a(int i);
    }

    public ColorSelectorView(Context context) {
        super(context);
        this.f347e = 0;
        this.f348f = 0;
        m429a();
    }

    public ColorSelectorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f347e = 0;
        this.f348f = 0;
        m429a();
    }

    private static View m427a(Context context, String str) {
        View inflate = LayoutInflater.from(context).inflate(C0338R.layout.tabs_bg, null);
        ((TextView) inflate.findViewById(C0338R.id.tabsText)).setText(str);
        return inflate;
    }

    private void m429a() {
        View inflate = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0338R.layout.color_colorselectview, null);
        addView(inflate, new LayoutParams(-1, -1));
        this.f344b = new HsvSelectorView(getContext());
        this.f344b.setLayoutParams(new LayoutParams(-1, -1));
        this.f344b.setOnColorChangedListener(new C08941(this));
        this.f343a = new RgbSelectorView(getContext());
        this.f343a.setLayoutParams(new LayoutParams(-1, -1));
        this.f343a.setOnColorChangedListener(new C08952(this));
        this.f345c = new HexSelectorView(getContext());
        this.f345c.setLayoutParams(new LayoutParams(-1, -1));
        this.f345c.setOnColorChangedListener(new C08963(this));
        this.f346d = (TabHost) inflate.findViewById(C0338R.id.colorview_tabColors);
        this.f346d.setup();
        TabContentFactory c0897a = new C0897a(this);
        TabSpec content = this.f346d.newTabSpec("HSV").setIndicator(m427a(this.f346d.getContext(), "HSV")).setContent(c0897a);
        TabSpec content2 = this.f346d.newTabSpec("RGB").setIndicator(m427a(this.f346d.getContext(), "RGB")).setContent(c0897a);
        TabSpec content3 = this.f346d.newTabSpec("HEX").setIndicator(m427a(this.f346d.getContext(), "HEX")).setContent(c0897a);
        this.f346d.addTab(content);
        this.f346d.addTab(content2);
        this.f346d.addTab(content3);
    }

    private void m430a(int i, View view) {
        if (this.f349g != i) {
            this.f349g = i;
            if (view != this.f344b) {
                this.f344b.setColor(i);
            }
            if (view != this.f343a) {
                this.f343a.setColor(i);
            }
            if (view != this.f345c) {
                this.f345c.setColor(i);
            }
            m432b();
        }
    }

    private void m432b() {
        if (this.f350h != null) {
            this.f350h.m426a(getColor());
        }
    }

    public int getColor() {
        return this.f349g;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if ("HSV".equals(this.f346d.getCurrentTabTag())) {
            this.f347e = getMeasuredHeight();
            this.f348f = getMeasuredWidth();
        }
        setMeasuredDimension(this.f348f, this.f347e);
    }

    public void setColor(int i) {
        m430a(i, null);
    }

    public void setDialog(Dialog dialog) {
        this.f345c.setDialog(dialog);
    }

    public void setOnColorChangedListener(C0898b c0898b) {
        this.f350h = c0898b;
    }
}

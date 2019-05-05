package com.hanista.mobogram.mobo.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.util.NalUnitUtil;
import com.hanista.mobogram.mobo.component.ColorSelectorView.C0898b;
import com.hanista.mobogram.mobo.component.HistorySelectorView.C0900a;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.ColorPickerView.OnColorChangedListener;

/* renamed from: com.hanista.mobogram.mobo.component.a */
public class ColorSelectorDialog extends Dialog implements OnClickListener, OnColorChangedListener {
    private ColorSelectorView f388a;
    private ColorSelectorDialog f389b;
    private int f390c;
    private int f391d;
    private Button f392e;
    private Button f393f;
    private HistorySelectorView f394g;
    private int f395h;
    private int f396i;
    private boolean f397j;

    /* renamed from: com.hanista.mobogram.mobo.component.a.1 */
    class ColorSelectorDialog implements OnClickListener {
        final /* synthetic */ ColorSelectorDialog f384a;

        ColorSelectorDialog(ColorSelectorDialog colorSelectorDialog) {
            this.f384a = colorSelectorDialog;
        }

        public void onClick(View view) {
            this.f384a.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.a.2 */
    class ColorSelectorDialog implements OnClickListener {
        final /* synthetic */ ColorSelectorDialog f385a;

        ColorSelectorDialog(ColorSelectorDialog colorSelectorDialog) {
            this.f385a = colorSelectorDialog;
        }

        public void onClick(View view) {
            if (this.f385a.f389b != null) {
                this.f385a.f389b.m467a(this.f385a.f391d);
            }
            this.f385a.f394g.m444a(this.f385a.f391d);
            this.f385a.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.a.3 */
    class ColorSelectorDialog implements C0898b {
        final /* synthetic */ ColorSelectorDialog f386a;

        ColorSelectorDialog(ColorSelectorDialog colorSelectorDialog) {
            this.f386a = colorSelectorDialog;
        }

        public void m465a(int i) {
            this.f386a.m470a(i);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.a.4 */
    class ColorSelectorDialog implements C0900a {
        final /* synthetic */ ColorSelectorDialog f387a;

        ColorSelectorDialog(ColorSelectorDialog colorSelectorDialog) {
            this.f387a = colorSelectorDialog;
        }

        public void m466a(int i) {
            this.f387a.m470a(i);
            this.f387a.f388a.setColor(i);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.component.a.a */
    public interface ColorSelectorDialog {
        void m467a(int i);
    }

    public ColorSelectorDialog(Context context, ColorSelectorDialog colorSelectorDialog, int i, int i2, int i3, boolean z) {
        super(context, C0338R.style.myBackgroundStyle);
        this.f389b = colorSelectorDialog;
        this.f390c = i;
        this.f395h = i2;
        this.f396i = i3;
        this.f397j = z;
    }

    private int m468a(int i, boolean z) {
        return Color.argb(z ? Color.alpha(i) : NalUnitUtil.EXTENDED_SAR, Color.red(i), Color.green(i), Color.blue(i));
    }

    private void m470a(int i) {
        this.f393f.setBackgroundColor(i);
        this.f393f.setTextColor((i ^ -1) | Theme.MSG_TEXT_COLOR);
        this.f391d = m468a(i, this.f397j);
    }

    public int m475a() {
        return this.f391d;
    }

    public void onClick(View view) {
        if (this.f389b != null) {
            this.f389b.m467a(this.f391d);
        }
        this.f394g.m444a(this.f391d);
        dismiss();
    }

    public void onColorChanged(int i) {
        if (this.f389b != null) {
            this.f389b.m467a(m475a());
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0338R.layout.colordialog);
        if (this.f395h == 2) {
            getWindow().setGravity(5);
            LayoutParams attributes = getWindow().getAttributes();
            attributes.x = this.f396i;
            getWindow().setAttributes(attributes);
        } else if (this.f395h == 1) {
        }
        this.f392e = (Button) findViewById(C0338R.id.button_old);
        this.f392e.setOnClickListener(new ColorSelectorDialog(this));
        this.f393f = (Button) findViewById(C0338R.id.button_new);
        this.f393f.setOnClickListener(new ColorSelectorDialog(this));
        this.f388a = (ColorSelectorView) findViewById(C0338R.id.content);
        this.f388a.setDialog(this);
        this.f388a.setOnColorChangedListener(new ColorSelectorDialog(this));
        this.f394g = (HistorySelectorView) findViewById(C0338R.id.historyselector);
        this.f394g.setOnColorChangedListener(new ColorSelectorDialog(this));
        this.f392e.setBackgroundColor(this.f390c);
        this.f392e.setTextColor((this.f390c ^ -1) | Theme.MSG_TEXT_COLOR);
        this.f388a.setColor(this.f390c);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4) {
            System.out.println("TOuch outside the dialog ******************** ");
            dismiss();
        }
        return super.onTouchEvent(motionEvent);
    }
}

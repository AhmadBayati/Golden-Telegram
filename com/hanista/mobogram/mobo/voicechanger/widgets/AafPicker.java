package com.hanista.mobogram.mobo.voicechanger.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.mobo.voicechanger.AAF;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public final class AafPicker extends RadioGroup implements OnClickListener {
    private AAF f2642a;
    private final RadioButton[] f2643b;
    private PropertyChangeListener f2644c;

    public AafPicker(Context context) {
        super(context);
        this.f2643b = new RadioButton[AAF.m2526a()];
        m2591a(context);
    }

    public AafPicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f2643b = new RadioButton[AAF.m2526a()];
        m2591a(context);
    }

    private void m2591a(Context context) {
        View.inflate(context, C0338R.layout.aafpicker, this);
        AAF[] values = AAF.values();
        for (int i = 0; i < AAF.m2526a(); i++) {
            RadioButton radioButton = (RadioButton) findViewWithTag(values[i].toString());
            this.f2643b[i] = radioButton;
            radioButton.setOnClickListener(this);
        }
        this.f2642a = AAF.m2527a(0);
        this.f2643b[0].setChecked(true);
    }

    public AAF getAaf() {
        return this.f2642a;
    }

    public void onClick(View view) {
        Object tag = view.getTag();
        this.f2642a = AAF.valueOf(tag.toString());
        for (RadioButton radioButton : this.f2643b) {
            if (!radioButton.getTag().equals(tag)) {
                radioButton.setChecked(false);
            }
        }
        if (this.f2644c != null) {
            this.f2644c.propertyChange(new PropertyChangeEvent(this, "aaf", null, this.f2642a));
        }
    }

    public void setAaf(AAF aaf) {
        if (this.f2642a != aaf) {
            this.f2642a = aaf;
            for (int i = 0; i < this.f2643b.length; i++) {
                this.f2643b[i].setChecked(this.f2643b[i].getTag().toString().equals(aaf.toString()));
            }
        }
    }

    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.f2644c = propertyChangeListener;
    }
}

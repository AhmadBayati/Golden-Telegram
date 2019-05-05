package com.hanista.mobogram.mobo.voicechanger.widgets;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;

public class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener {
    private final int f2647a;
    private final int f2648b;
    private final int f2649c;
    private final int f2650d;
    private final String f2651e;
    private int f2652f;
    private TextView f2653g;

    public SeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f2653g = null;
        this.f2647a = attributeSet.getAttributeIntValue("http://voicesmith.jurihock.de", "minValue", 0);
        this.f2648b = attributeSet.getAttributeIntValue("http://voicesmith.jurihock.de", "maxValue", 100);
        this.f2649c = attributeSet.getAttributeIntValue("http://voicesmith.jurihock.de", "incValue", 1);
        this.f2650d = attributeSet.getAttributeIntValue("http://schemas.android.com/apk/res/android", "defaultValue", 0);
        this.f2651e = attributeSet.getAttributeValue("http://voicesmith.jurihock.de", "valueFormat");
    }

    public SeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f2653g = null;
        this.f2647a = attributeSet.getAttributeIntValue("http://voicesmith.jurihock.de", "minValue", 0);
        this.f2648b = attributeSet.getAttributeIntValue("http://voicesmith.jurihock.de", "maxValue", 100);
        this.f2649c = attributeSet.getAttributeIntValue("http://voicesmith.jurihock.de", "incValue", 1);
        this.f2650d = attributeSet.getAttributeIntValue("http://schemas.android.com/apk/res/android", "defaultValue", 0);
        this.f2651e = attributeSet.getAttributeValue("http://voicesmith.jurihock.de", "valueFormat");
    }

    private String m2593a(String str) {
        if (this.f2651e == null) {
            return str;
        }
        return String.format(this.f2651e, new Object[]{str});
    }

    public CharSequence getSummary() {
        return m2593a(getPersistedString(Integer.toString(this.f2650d)));
    }

    protected View onCreateDialogView() {
        this.f2652f = Integer.parseInt(getPersistedString(Integer.toString(this.f2650d)));
        int dimension = (int) getContext().getResources().getDimension(C0338R.dimen.LayoutMargin);
        View linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new LayoutParams(-1, -2));
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dimension, dimension, dimension, dimension);
        this.f2653g = new TextView(getContext());
        this.f2653g.setText(m2593a(Integer.toString(this.f2652f)));
        this.f2653g.setGravity(17);
        this.f2653g.setPadding(0, 0, 0, dimension);
        linearLayout.addView(this.f2653g);
        View seekBar = new SeekBar(getContext());
        seekBar.setMax(this.f2648b - this.f2647a);
        seekBar.setProgress(this.f2652f - this.f2647a);
        seekBar.setOnSeekBarChangeListener(this);
        linearLayout.addView(seekBar);
        return linearLayout;
    }

    protected void onDialogClosed(boolean z) {
        super.onDialogClosed(z);
        if (z) {
            String num = Integer.toString(this.f2652f);
            persistString(num);
            callChangeListener(num);
            notifyChanged();
        }
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        this.f2652f = this.f2647a + i;
        if (z && this.f2649c > 1) {
            this.f2652f = (this.f2652f / this.f2649c) * this.f2649c;
            seekBar.setProgress(this.f2652f - this.f2647a);
        }
        this.f2653g.setText(m2593a(Integer.toString(this.f2652f)));
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}

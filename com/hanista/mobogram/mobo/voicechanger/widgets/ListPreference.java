package com.hanista.mobogram.mobo.voicechanger.widgets;

import android.content.Context;
import android.util.AttributeSet;

public class ListPreference extends android.preference.ListPreference {
    public ListPreference(Context context) {
        super(context);
    }

    public ListPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CharSequence getSummary() {
        return getEntry();
    }

    protected void onDialogClosed(boolean z) {
        if (z) {
            notifyChanged();
        }
        super.onDialogClosed(z);
    }
}

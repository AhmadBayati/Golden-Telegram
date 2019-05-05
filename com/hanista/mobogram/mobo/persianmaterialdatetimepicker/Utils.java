package com.hanista.mobogram.mobo.persianmaterialdatetimepicker;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.view.View;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.persianmaterialdatetimepicker.p017a.PersianCalendarUtils;

/* renamed from: com.hanista.mobogram.mobo.persianmaterialdatetimepicker.c */
public class Utils {
    public static int m2070a(int i, int i2) {
        return i < 6 ? 31 : (i < 11 || PersianCalendarUtils.m2061a(i2)) ? 30 : 29;
    }

    public static ObjectAnimator m2071a(View view, float f, float f2) {
        Keyframe ofFloat = Keyframe.ofFloat(0.0f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        Keyframe ofFloat2 = Keyframe.ofFloat(0.275f, f);
        Keyframe ofFloat3 = Keyframe.ofFloat(0.69f, f2);
        Keyframe ofFloat4 = Keyframe.ofFloat(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        PropertyValuesHolder ofKeyframe = PropertyValuesHolder.ofKeyframe("scaleX", new Keyframe[]{ofFloat, ofFloat2, ofFloat3, ofFloat4});
        PropertyValuesHolder ofKeyframe2 = PropertyValuesHolder.ofKeyframe("scaleY", new Keyframe[]{ofFloat, ofFloat2, ofFloat3, ofFloat4});
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{ofKeyframe, ofKeyframe2});
        ofPropertyValuesHolder.setDuration(544);
        return ofPropertyValuesHolder;
    }

    @SuppressLint({"NewApi"})
    public static void m2072a(View view, CharSequence charSequence) {
        if (Utils.m2073a() && view != null && charSequence != null) {
            view.announceForAccessibility(charSequence);
        }
    }

    public static boolean m2073a() {
        return VERSION.SDK_INT >= 16;
    }
}

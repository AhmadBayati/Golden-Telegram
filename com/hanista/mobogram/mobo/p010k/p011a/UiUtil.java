package com.hanista.mobogram.mobo.p010k.p011a;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;

/* renamed from: com.hanista.mobogram.mobo.k.a.e */
class UiUtil {
    static int m1347a(int i, float f) {
        if (f > DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        } else if (f <= 0.0f) {
            f = 0.0f;
        }
        return (((int) (((float) (i >>> 24)) * f)) << 24) | (ViewCompat.MEASURED_SIZE_MASK & i);
    }

    static int m1348a(Context context, int i) {
        return (int) TypedValue.applyDimension(1, (float) i, context.getResources().getDisplayMetrics());
    }

    static int m1349a(Context context, String str) {
        Theme theme = context.getTheme();
        if (theme == null) {
            return -1;
        }
        TypedValue typedValue = new TypedValue();
        int identifier = context.getResources().getIdentifier(str, "attr", context.getPackageName());
        if (identifier == 0) {
            return -1;
        }
        theme.resolveAttribute(identifier, typedValue, true);
        return typedValue.data;
    }

    static int m1350b(Context context, int i) {
        return (int) TypedValue.applyDimension(2, (float) i, context.getResources().getDisplayMetrics());
    }

    static int m1351c(Context context, int i) {
        return VERSION.SDK_INT >= 23 ? context.getColor(i) : context.getResources().getColor(i);
    }
}

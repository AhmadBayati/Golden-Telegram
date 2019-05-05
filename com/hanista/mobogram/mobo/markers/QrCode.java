package com.hanista.mobogram.mobo.markers;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.os.Build.VERSION;
import android.widget.ImageView;

/* renamed from: com.hanista.mobogram.mobo.markers.e */
class QrCode {
    static void m1824a(Activity activity) {
        Builder builder = VERSION.SDK_INT >= 11 ? new Builder(activity, 16973914) : new Builder(activity);
        builder.setTitle(null);
        builder.setCancelable(true);
        builder.setView(new ImageView(activity));
        builder.create().show();
    }
}

package com.hanista.mobogram.mobo.markers;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.tgnet.TLRPC;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/* renamed from: com.hanista.mobogram.mobo.markers.a */
class About {
    static char[] f1785a;

    /* renamed from: com.hanista.mobogram.mobo.markers.a.1 */
    static class About implements OnClickListener {
        final /* synthetic */ MarkersActivity f1782a;

        About(MarkersActivity markersActivity) {
            this.f1782a = markersActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            this.f1782a.clickSiteLink(null);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.a.2 */
    static class About implements OnClickListener {
        final /* synthetic */ MarkersActivity f1783a;

        About(MarkersActivity markersActivity) {
            this.f1783a = markersActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            this.f1783a.clickMarketLink(null);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.markers.a.3 */
    static class About implements OnClickListener {
        final /* synthetic */ MarkersActivity f1784a;

        About(MarkersActivity markersActivity) {
            this.f1784a = markersActivity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            QrCode.m1824a(this.f1784a);
        }
    }

    static {
        f1785a = new char[TLRPC.MESSAGE_FLAG_HAS_VIEWS];
    }

    static String m1810a(Activity activity) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        try {
            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return packageInfo != null ? packageInfo.versionName : str;
        } catch (NameNotFoundException e) {
            return str;
        }
    }

    static String m1811a(Context context, String str) {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(str)));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    return stringBuffer.toString();
                }
                stringBuffer.append(readLine);
            }
        } catch (IOException e) {
            return null;
        }
    }

    static void m1812a(MarkersActivity markersActivity) {
        Builder builder = new Builder(markersActivity);
        builder.setTitle(null);
        builder.setCancelable(true);
        View inflate = ((LayoutInflater) markersActivity.getSystemService("layout_inflater")).inflate(C0338R.layout.about_box, null);
        TextView textView = (TextView) inflate.findViewById(C0338R.id.title);
        textView.setTypeface(Typeface.create("sans-serif-light", 0));
        textView.setText(markersActivity.getString(C0338R.string.AppName) + " " + About.m1810a((Activity) markersActivity));
        ((WebView) inflate.findViewById(C0338R.id.html)).loadDataWithBaseURL("file:///android_asset/", About.m1811a(markersActivity, "about.html"), "text/html", "utf-8", null);
        builder.setView(inflate);
        builder.setNegativeButton("Website", new About(markersActivity));
        builder.setNeutralButton("on Play Store", new About(markersActivity));
        builder.setPositiveButton("QR code", new About(markersActivity));
        builder.create().show();
    }
}

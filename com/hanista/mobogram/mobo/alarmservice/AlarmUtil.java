package com.hanista.mobogram.mobo.alarmservice;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/* renamed from: com.hanista.mobogram.mobo.alarmservice.a */
public class AlarmUtil {

    /* renamed from: com.hanista.mobogram.mobo.alarmservice.a.1 */
    static class AlarmUtil implements OnDismissListener {
        final /* synthetic */ Activity f146a;

        AlarmUtil(Activity activity) {
            this.f146a = activity;
        }

        public void onDismiss(DialogInterface dialogInterface) {
            this.f146a.finish();
            System.exit(0);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.alarmservice.a.2 */
    static class AlarmUtil implements OnCancelListener {
        final /* synthetic */ Activity f147a;

        AlarmUtil(Activity activity) {
            this.f147a = activity;
        }

        public void onCancel(DialogInterface dialogInterface) {
            dialogInterface.dismiss();
            this.f147a.finish();
            System.exit(0);
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.alarmservice.a.3 */
    static class AlarmUtil implements OnClickListener {
        final /* synthetic */ AlarmResponse f148a;
        final /* synthetic */ Activity f149b;

        AlarmUtil(AlarmResponse alarmResponse, Activity activity) {
            this.f148a = alarmResponse;
            this.f149b = activity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (!(this.f148a.getPositiveBtnAction() == null || this.f148a.getPositiveBtnUrl() == null)) {
                Intent intent = new Intent();
                intent.setAction(this.f148a.getPositiveBtnAction());
                intent.setData(Uri.parse(this.f148a.getPositiveBtnUrl()));
                this.f149b.startActivity(intent);
            }
            if (!this.f148a.getExitOnDismiss().booleanValue()) {
                dialogInterface.dismiss();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.alarmservice.a.4 */
    static class AlarmUtil implements OnClickListener {
        final /* synthetic */ AlarmResponse f150a;
        final /* synthetic */ Activity f151b;

        AlarmUtil(AlarmResponse alarmResponse, Activity activity) {
            this.f150a = alarmResponse;
            this.f151b = activity;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (!(this.f150a.getNegativeBtnAction() == null || this.f150a.getNegativeBtnUrl() == null)) {
                Intent intent = new Intent();
                intent.setAction(this.f150a.getNegativeBtnAction());
                intent.setData(Uri.parse(this.f150a.getNegativeBtnUrl()));
                this.f151b.startActivity(intent);
            }
            if (this.f150a.getExitOnDismiss().booleanValue()) {
                this.f151b.finish();
                System.exit(0);
                return;
            }
            dialogInterface.dismiss();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.alarmservice.a.a */
    static class AlarmUtil extends AsyncTask<Void, Void, Void> {
        private ImageView f152a;
        private String f153b;
        private Bitmap f154c;

        public AlarmUtil(ImageView imageView, String str) {
            this.f152a = imageView;
            this.f153b = str;
        }

        protected Void m272a(Void... voidArr) {
            try {
                this.f154c = AlarmUtil.m274a(this.f153b);
            } catch (Exception e) {
            }
            return null;
        }

        protected void m273a(Void voidR) {
            super.onPostExecute(voidR);
            if (this.f152a != null && this.f154c != null) {
                try {
                    this.f152a.setImageBitmap(this.f154c);
                } catch (Exception e) {
                }
            }
        }

        protected /* synthetic */ Object doInBackground(Object[] objArr) {
            return m272a((Void[]) objArr);
        }

        protected /* synthetic */ void onPostExecute(Object obj) {
            m273a((Void) obj);
        }
    }

    public static Bitmap m274a(String str) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void m275a(Activity activity) {
        try {
            int a = MoboUtils.m1692a((Context) activity);
            DataBaseAccess dataBaseAccess = new DataBaseAccess();
            AlarmResponse b = dataBaseAccess.m856b(a);
            if (b != null && b.getDisplayCount().intValue() < b.getShowCount().intValue()) {
                Builder builder = new Builder(activity);
                View inflate = ((LayoutInflater) activity.getSystemService("layout_inflater")).inflate(C0338R.layout.alarm_dialog, null);
                builder.setTitle(b.getTitle());
                builder.setView(inflate);
                TextView textView = (TextView) inflate.findViewById(C0338R.id.txt_alarm);
                ImageView imageView = (ImageView) inflate.findViewById(C0338R.id.img_alarm);
                textView.setText(b.getMessage());
                if (b.getImageUrl() != null) {
                    new AlarmUtil(imageView, b.getImageUrl()).execute(new Void[0]);
                } else {
                    imageView.setVisibility(8);
                }
                if (b.getExitOnDismiss().booleanValue()) {
                    if (VERSION.SDK_INT > 16) {
                        builder.setOnDismissListener(new AlarmUtil(activity));
                    }
                    builder.setOnCancelListener(new AlarmUtil(activity));
                }
                if (b.getPositiveBtnText() != null) {
                    builder.setPositiveButton(b.getPositiveBtnText(), new AlarmUtil(b, activity));
                }
                if (b.getNegativeBtnText() != null) {
                    builder.setNegativeButton(b.getNegativeBtnText(), new AlarmUtil(b, activity));
                }
                builder.show();
                if (b.getDisplayCount() == null) {
                    b.setDisplayCount(Integer.valueOf(0));
                }
                b.setDisplayCount(Integer.valueOf(b.getDisplayCount().intValue() + 1));
                dataBaseAccess.m839a(b);
            }
        } catch (Throwable e) {
            Log.e("AlarmUtil", e.getMessage(), e);
        }
    }
}

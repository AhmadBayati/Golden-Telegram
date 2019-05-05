package com.hanista.mobogram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;

public class VideoEncodingService extends Service implements NotificationCenterDelegate {
    private Builder builder;
    private int currentProgress;
    private String path;

    public VideoEncodingService() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileUploadProgressChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.stopEncodingService);
    }

    public void didReceivedNotification(int i, Object... objArr) {
        String str;
        if (i == NotificationCenter.FileUploadProgressChanged) {
            str = (String) objArr[0];
            if (this.path != null && this.path.equals(str)) {
                Boolean bool = (Boolean) objArr[2];
                this.currentProgress = (int) (((Float) objArr[1]).floatValue() * 100.0f);
                this.builder.setProgress(100, this.currentProgress, this.currentProgress == 0);
                NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
            }
        } else if (i == NotificationCenter.stopEncodingService) {
            str = (String) objArr[0];
            if (str == null || str.equals(this.path)) {
                stopSelf();
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        stopForeground(true);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileUploadProgressChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stopEncodingService);
        FileLog.m16e("tmessages", "destroy video service");
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        boolean z = false;
        this.path = intent.getStringExtra("path");
        boolean booleanExtra = intent.getBooleanExtra("gif", false);
        if (this.path == null) {
            stopSelf();
        } else {
            FileLog.m16e("tmessages", "start video service");
            if (this.builder == null) {
                this.builder = new Builder(ApplicationLoader.applicationContext);
                this.builder.setSmallIcon(17301640);
                this.builder.setWhen(System.currentTimeMillis());
                this.builder.setContentTitle(LocaleController.getString("AppName", C0338R.string.AppName));
                if (booleanExtra) {
                    this.builder.setTicker(LocaleController.getString("SendingGif", C0338R.string.SendingGif));
                    this.builder.setContentText(LocaleController.getString("SendingGif", C0338R.string.SendingGif));
                } else {
                    this.builder.setTicker(LocaleController.getString("SendingVideo", C0338R.string.SendingVideo));
                    this.builder.setContentText(LocaleController.getString("SendingVideo", C0338R.string.SendingVideo));
                }
            }
            this.currentProgress = 0;
            Builder builder = this.builder;
            int i3 = this.currentProgress;
            if (this.currentProgress == 0) {
                z = true;
            }
            builder.setProgress(100, i3, z);
            startForeground(4, this.builder.build());
            NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(4, this.builder.build());
        }
        return 2;
    }
}

package com.hanista.mobogram.mobo.download;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboConstants;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DownloadAlarmReceiver extends BroadcastReceiver {
    public static void m618a(Context context) {
        if (MoboConstants.f1313F) {
            m619a(context, "com.hanista.mobogram.download.start");
            m619a(context, "com.hanista.mobogram.download.stop");
            m620a(context, "com.hanista.mobogram.download.start", MoboConstants.f1314G, MoboConstants.f1315H);
            m620a(context, "com.hanista.mobogram.download.stop", MoboConstants.f1316I, MoboConstants.f1317J);
            return;
        }
        m619a(context, "com.hanista.mobogram.download.start");
        m619a(context, "com.hanista.mobogram.download.stop");
        Intent intent = new Intent(context, DownloadManagerService.class);
        intent.setAction("com.hanista.mobogram.download.stop");
        context.startService(intent);
    }

    public static void m619a(Context context, String str) {
        Intent intent = new Intent(context, DownloadAlarmReceiver.class);
        intent.setAction(str);
        ((AlarmManager) context.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).cancel(PendingIntent.getBroadcast(context, 0, intent, 0));
    }

    public static void m620a(Context context, String str, int i, int i2) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompatApi24.CATEGORY_ALARM);
        Intent intent = new Intent(context, DownloadAlarmReceiver.class);
        intent.setAction(str);
        intent.putExtra("com.hanista.mobogram.download.use.wifi", true);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, C0700C.SAMPLE_FLAG_DECODE_ONLY);
        Calendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(System.currentTimeMillis());
        Calendar gregorianCalendar2 = new GregorianCalendar();
        gregorianCalendar2.set(6, gregorianCalendar.get(6));
        gregorianCalendar2.set(11, i);
        gregorianCalendar2.set(12, i2);
        gregorianCalendar2.set(13, 0);
        gregorianCalendar2.set(5, gregorianCalendar.get(5));
        gregorianCalendar2.set(2, gregorianCalendar.get(2));
        if (gregorianCalendar2.getTimeInMillis() < gregorianCalendar.getTimeInMillis()) {
            gregorianCalendar2.add(6, 1);
        }
        alarmManager.setRepeating(0, gregorianCalendar2.getTimeInMillis(), 86400000, broadcast);
    }

    public void onReceive(Context context, Intent intent) {
        Intent intent2 = new Intent(context, DownloadManagerService.class);
        intent2.setAction(intent.getAction());
        intent2.putExtras(intent.getExtras());
        context.startService(intent2);
    }
}

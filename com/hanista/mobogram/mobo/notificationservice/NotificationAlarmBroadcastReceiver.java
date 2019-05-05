package com.hanista.mobogram.mobo.notificationservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hanista.mobogram.messenger.exoplayer.C0700C;

public class NotificationAlarmBroadcastReceiver extends BroadcastReceiver {
    public void m1950a(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompatApi24.CATEGORY_ALARM);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 60, new Intent(context, NotificationServiceBroadcastReceiver.class), C0700C.SAMPLE_FLAG_DECODE_ONLY);
        alarmManager.cancel(broadcast);
        alarmManager.setRepeating(0, System.currentTimeMillis(), 43200000, broadcast);
    }

    public void onReceive(Context context, Intent intent) {
        m1950a(context);
    }
}

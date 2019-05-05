package com.hanista.mobogram.mobo.dialogdm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboConstants;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DialogDmAlarmReceiver extends BroadcastReceiver {
    public static void m537a(Context context) {
        if (MoboConstants.ao) {
            m538a(context, "com.hanista.mobogram.dialogdm.start");
            m538a(context, "com.hanista.mobogram.dialogdm.stop");
            m539a(context, "com.hanista.mobogram.dialogdm.start", MoboConstants.ap, MoboConstants.aq);
            m539a(context, "com.hanista.mobogram.dialogdm.stop", MoboConstants.ar, MoboConstants.as);
            return;
        }
        m538a(context, "com.hanista.mobogram.dialogdm.start");
        m538a(context, "com.hanista.mobogram.dialogdm.stop");
        Intent intent = new Intent(context, DialogDmService.class);
        intent.setAction("com.hanista.mobogram.dialogdm.stop");
        context.startService(intent);
    }

    public static void m538a(Context context, String str) {
        Intent intent = new Intent(context, DialogDmAlarmReceiver.class);
        intent.setAction(str);
        ((AlarmManager) context.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).cancel(PendingIntent.getBroadcast(context, 0, intent, 0));
    }

    public static void m539a(Context context, String str, int i, int i2) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompatApi24.CATEGORY_ALARM);
        Intent intent = new Intent(context, DialogDmAlarmReceiver.class);
        intent.setAction(str);
        intent.putExtra("com.hanista.mobogram.dialogdm.use.wifi", true);
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
        Intent intent2 = new Intent(context, DialogDmService.class);
        intent2.setAction(intent.getAction());
        intent2.putExtras(intent.getExtras());
        context.startService(intent2);
    }
}

package com.hanista.mobogram.mobo.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadAlarmAutoStart extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        DownloadAlarmReceiver.m618a(context);
    }
}

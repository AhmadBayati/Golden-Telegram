package com.hanista.mobogram.mobo.alarmservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import com.google.gson.Gson;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.mobo.MoboUtils;
import com.hanista.mobogram.mobo.notificationservice.RestClient;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import java.util.Random;

public class AlarmService extends IntentService {
    public AlarmService() {
        super("AlarmService");
    }

    public void m271a() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), 0);
        if (System.currentTimeMillis() - Long.valueOf(sharedPreferences.getLong("alarmLastCheck", 0)).longValue() > 7200000) {
            sharedPreferences.edit().putLong("alarmLastCheck", System.currentTimeMillis()).commit();
            String str = "alarm.jsp";
            int a = MoboUtils.m1692a((Context) this);
            DataBaseAccess dataBaseAccess = new DataBaseAccess();
            AlarmResponse b = dataBaseAccess.m856b(a);
            long longValue = b != null ? b.getId().longValue() : 0;
            long clientUserId = (long) UserConfig.getClientUserId();
            if (clientUserId == 0) {
                clientUserId = (long) VERSION.SDK_INT;
            }
            RestClient restClient = new RestClient("http://hanista.com:PORT/app-general/service/mobogram/".replace("PORT", (new Random(clientUserId + System.currentTimeMillis()).nextInt(5) + 8081) + TtmlNode.ANONYMOUS_REGION_ID));
            Object alarmRequest = new AlarmRequest(MoboUtils.m1720f(this), Integer.valueOf(a), Integer.valueOf(VERSION.SDK_INT), getPackageName(), Long.valueOf(longValue));
            Gson gson = new Gson();
            b = (AlarmResponse) gson.fromJson(restClient.m1966a(str, gson.toJson(alarmRequest)), AlarmResponse.class);
            if (b != null && b.getId() != null && b.getId().longValue() > 0) {
                b.setTargetVersion(Integer.valueOf(a));
                dataBaseAccess.m839a(b);
            }
        }
    }

    protected void onHandleIntent(Intent intent) {
        try {
            if (MoboUtils.m1718e(this)) {
                m271a();
            }
        } catch (Exception e) {
        }
    }
}

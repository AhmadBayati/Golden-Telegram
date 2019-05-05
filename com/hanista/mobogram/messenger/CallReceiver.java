package com.hanista.mobogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.hanista.mobogram.PhoneFormat.PhoneFormat;

public class CallReceiver extends BroadcastReceiver {

    /* renamed from: com.hanista.mobogram.messenger.CallReceiver.1 */
    class C03501 extends PhoneStateListener {
        C03501() {
        }

        public void onCallStateChanged(int i, String str) {
            super.onCallStateChanged(i, str);
            if (i == 1 && str != null && str.length() > 0) {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceiveCall, PhoneFormat.stripExceptNumbers(str));
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        ((TelephonyManager) context.getSystemService("phone")).listen(new C03501(), 32);
    }
}

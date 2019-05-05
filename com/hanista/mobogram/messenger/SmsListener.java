package com.hanista.mobogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsListener extends BroadcastReceiver {
    private SharedPreferences preferences;

    /* renamed from: com.hanista.mobogram.messenger.SmsListener.1 */
    class C06821 implements Runnable {
        final /* synthetic */ Matcher val$matcher;

        C06821(Matcher matcher) {
            this.val$matcher = matcher;
        }

        public void run() {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, this.val$matcher.group(0));
        }
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED") && AndroidUtilities.isWaitingForSms()) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                try {
                    Object[] objArr = (Object[]) extras.get("pdus");
                    SmsMessage[] smsMessageArr = new SmsMessage[objArr.length];
                    CharSequence charSequence = TtmlNode.ANONYMOUS_REGION_ID;
                    for (int i = 0; i < smsMessageArr.length; i++) {
                        smsMessageArr[i] = SmsMessage.createFromPdu((byte[]) objArr[i]);
                        charSequence = charSequence + smsMessageArr[i].getMessageBody();
                    }
                    Matcher matcher = Pattern.compile("[0-9]+").matcher(charSequence);
                    if (matcher.find() && matcher.group(0).length() >= 3) {
                        AndroidUtilities.runOnUIThread(new C06821(matcher));
                    }
                } catch (Throwable th) {
                    FileLog.m18e("tmessages", th);
                }
            }
        }
    }
}

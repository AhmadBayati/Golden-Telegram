package com.hanista.mobogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

public class AutoMessageReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
        if (resultsFromIntent != null) {
            CharSequence charSequence = resultsFromIntent.getCharSequence(NotificationsController.EXTRA_VOICE_REPLY);
            if (charSequence != null && charSequence.length() != 0) {
                long longExtra = intent.getLongExtra("dialog_id", 0);
                int intExtra = intent.getIntExtra("max_id", 0);
                if (longExtra != 0 && intExtra != 0) {
                    SendMessagesHelper.getInstance().sendMessage(charSequence.toString(), longExtra, null, null, true, null, null, null);
                    MessagesController.getInstance().markDialogAsRead(longExtra, intExtra, intExtra, 0, true, false);
                }
            }
        }
    }
}

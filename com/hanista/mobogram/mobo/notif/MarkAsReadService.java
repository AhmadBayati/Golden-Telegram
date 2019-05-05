package com.hanista.mobogram.mobo.notif;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationsController;
import java.util.Iterator;

public class MarkAsReadService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Iterator it = NotificationsController.getInstance().getPushMessages().iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            MessagesController.getInstance().markDialogAsRead(messageObject.getDialogId(), messageObject.getId(), Math.max(0, messageObject.getId()), messageObject.messageOwner.date, true, true);
        }
        return 2;
    }
}

package com.hanista.mobogram.messenger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.CarExtender;
import android.support.v4.app.NotificationCompat.CarExtender.UnreadConversation;
import android.support.v4.app.NotificationCompat.Extender;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.RequestDelegate;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.EncryptedChat;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.TL_account_updateNotifySettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputNotifyPeer;
import com.hanista.mobogram.tgnet.TLRPC.TL_inputPeerNotifySettings;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChannelCreate;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatAddUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatCreate;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatEditTitle;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionEmpty;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionGameScore;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionPinMessage;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionUserJoined;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaContact;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaDocument;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGame;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaGeo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaVenue;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageService;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.LaunchActivity;
import com.hanista.mobogram.ui.PopupNotificationActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.aspectj.lang.JoinPoint;

public class NotificationsController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController Instance;
    private AlarmManager alarmManager;
    protected AudioManager audioManager;
    private ArrayList<MessageObject> delayedPushMessages;
    private boolean inChatSoundEnabled;
    private int lastBadgeCount;
    private int lastOnlineFromOtherDevice;
    private long lastSoundOutPlay;
    private long lastSoundPlay;
    private String launcherClassName;
    private Runnable notificationDelayRunnable;
    private WakeLock notificationDelayWakelock;
    private NotificationManagerCompat notificationManager;
    private DispatchQueue notificationsQueue;
    private boolean notifyCheck;
    private long opened_dialog_id;
    private int personal_count;
    public ArrayList<MessageObject> popupMessages;
    public ArrayList<MessageObject> popupReplyMessages;
    private HashMap<Long, Integer> pushDialogs;
    private HashMap<Long, Integer> pushDialogsOverrideMention;
    private ArrayList<MessageObject> pushMessages;
    private HashMap<Long, MessageObject> pushMessagesDict;
    private HashMap<Long, Point> smartNotificationsDialogs;
    private int soundIn;
    private boolean soundInLoaded;
    private int soundOut;
    private boolean soundOutLoaded;
    private SoundPool soundPool;
    private int soundRecord;
    private boolean soundRecordLoaded;
    private int total_unread_count;
    private HashMap<Long, Integer> wearNotificationsIds;

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ HashMap val$dialogs;
        final /* synthetic */ ArrayList val$messages;

        /* renamed from: com.hanista.mobogram.messenger.NotificationsController.10.1 */
        class C06031 implements Runnable {
            C06031() {
            }

            public void run() {
                NotificationsController.this.popupMessages.clear();
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
            }
        }

        AnonymousClass10(ArrayList arrayList, HashMap hashMap) {
            this.val$messages = arrayList;
            this.val$dialogs = hashMap;
        }

        public void run() {
            long j;
            boolean z = false;
            NotificationsController.this.pushDialogs.clear();
            NotificationsController.this.pushMessages.clear();
            NotificationsController.this.pushMessagesDict.clear();
            NotificationsController.this.total_unread_count = 0;
            NotificationsController.this.personal_count = 0;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            HashMap hashMap = new HashMap();
            if (this.val$messages != null) {
                Iterator it = this.val$messages.iterator();
                while (it.hasNext()) {
                    Message message = (Message) it.next();
                    long j2 = (long) message.id;
                    if (message.to_id.channel_id != 0) {
                        j2 |= ((long) message.to_id.channel_id) << 32;
                    }
                    if (!NotificationsController.this.pushMessagesDict.containsKey(Long.valueOf(j2))) {
                        MessageObject messageObject = new MessageObject(message, null, false);
                        if (NotificationsController.this.isPersonalMessage(messageObject)) {
                            NotificationsController.this.personal_count = NotificationsController.this.personal_count + 1;
                        }
                        long dialogId = messageObject.getDialogId();
                        j = messageObject.messageOwner.mentioned ? (long) messageObject.messageOwner.from_id : dialogId;
                        Boolean bool = (Boolean) hashMap.get(Long.valueOf(j));
                        if (bool == null) {
                            int access$1900 = NotificationsController.this.getNotifyOverride(sharedPreferences, j);
                            boolean z2 = access$1900 != 2 && ((sharedPreferences.getBoolean("EnableAll", true) && (((int) j) >= 0 || sharedPreferences.getBoolean("EnableGroup", true))) || access$1900 != 0);
                            bool = Boolean.valueOf(z2);
                            hashMap.put(Long.valueOf(j), bool);
                        }
                        if (bool.booleanValue() && !(j == NotificationsController.this.opened_dialog_id && ApplicationLoader.isScreenOn)) {
                            NotificationsController.this.pushMessagesDict.put(Long.valueOf(j2), messageObject);
                            NotificationsController.this.pushMessages.add(0, messageObject);
                            if (dialogId != j) {
                                NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(dialogId), Integer.valueOf(1));
                            }
                        }
                    }
                }
            }
            for (Entry entry : this.val$dialogs.entrySet()) {
                j = ((Long) entry.getKey()).longValue();
                Boolean bool2 = (Boolean) hashMap.get(Long.valueOf(j));
                if (bool2 == null) {
                    boolean z3;
                    boolean access$19002 = NotificationsController.this.getNotifyOverride(sharedPreferences, j);
                    Integer num = (Integer) NotificationsController.this.pushDialogsOverrideMention.get(Long.valueOf(j));
                    if (num == null || num.intValue() != 1) {
                        z3 = access$19002;
                    } else {
                        NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(j), Integer.valueOf(0));
                        z3 = true;
                    }
                    z3 = !z3 && ((sharedPreferences.getBoolean("EnableAll", true) && (((int) j) >= 0 || sharedPreferences.getBoolean("EnableGroup", true))) || z3);
                    bool2 = Boolean.valueOf(z3);
                    hashMap.put(Long.valueOf(j), bool2);
                }
                if (bool2.booleanValue()) {
                    access$1900 = ((Integer) entry.getValue()).intValue();
                    NotificationsController.this.pushDialogs.put(Long.valueOf(j), Integer.valueOf(access$1900));
                    NotificationsController.this.total_unread_count = access$1900 + NotificationsController.this.total_unread_count;
                }
            }
            if (NotificationsController.this.total_unread_count == 0) {
                AndroidUtilities.runOnUIThread(new C06031());
            }
            NotificationsController notificationsController = NotificationsController.this;
            if (SystemClock.uptimeMillis() / 1000 < 60) {
                z = true;
            }
            notificationsController.showOrUpdateNotification(z);
            if (sharedPreferences.getBoolean("badgeNumber", true)) {
                NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ int val$count;

        /* renamed from: com.hanista.mobogram.messenger.NotificationsController.11.1 */
        class C06041 implements Runnable {
            C06041() {
            }

            public void run() {
                try {
                    Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    intent.putExtra("badge_count", AnonymousClass11.this.val$count);
                    intent.putExtra("badge_count_package_name", ApplicationLoader.applicationContext.getPackageName());
                    intent.putExtra("badge_count_class_name", NotificationsController.this.launcherClassName);
                    ApplicationLoader.applicationContext.sendBroadcast(intent);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        AnonymousClass11(int i) {
            this.val$count = i;
        }

        public void run() {
            if (NotificationsController.this.lastBadgeCount != this.val$count) {
                NotificationsController.this.lastBadgeCount = this.val$count;
                try {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("tag", "com.hanista.mobogram/com.hanista.mobogram.ui.LaunchActivity");
                    contentValues.put("count", Integer.valueOf(this.val$count));
                    ApplicationLoader.applicationContext.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), contentValues);
                } catch (Throwable th) {
                }
                try {
                    if (NotificationsController.this.launcherClassName == null) {
                        NotificationsController.this.launcherClassName = NotificationsController.getLauncherClassName(ApplicationLoader.applicationContext);
                    }
                    if (NotificationsController.this.launcherClassName != null) {
                        AndroidUtilities.runOnUIThread(new C06041());
                    }
                } catch (Throwable th2) {
                    FileLog.m18e("tmessages", th2);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.1 */
    class C06071 implements Runnable {
        C06071() {
        }

        public void run() {
            FileLog.m16e("tmessages", "delay reached");
            if (!NotificationsController.this.delayedPushMessages.isEmpty()) {
                NotificationsController.this.showOrUpdateNotification(true);
                NotificationsController.this.delayedPushMessages.clear();
            }
            try {
                if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
                    NotificationsController.this.notificationDelayWakelock.release();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.2 */
    class C06082 implements Runnable {
        C06082() {
        }

        public void run() {
            NotificationsController.this.opened_dialog_id = 0;
            NotificationsController.this.total_unread_count = 0;
            NotificationsController.this.personal_count = 0;
            NotificationsController.this.pushMessages.clear();
            NotificationsController.this.pushMessagesDict.clear();
            NotificationsController.this.pushDialogs.clear();
            NotificationsController.this.wearNotificationsIds.clear();
            NotificationsController.this.delayedPushMessages.clear();
            NotificationsController.this.notifyCheck = false;
            NotificationsController.this.lastBadgeCount = 0;
            try {
                if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
                    NotificationsController.this.notificationDelayWakelock.release();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            NotificationsController.this.setBadge(0);
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
            edit.clear();
            edit.commit();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.3 */
    class C06093 implements Runnable {
        final /* synthetic */ long val$dialog_id;

        C06093(long j) {
            this.val$dialog_id = j;
        }

        public void run() {
            NotificationsController.this.opened_dialog_id = this.val$dialog_id;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.4 */
    class C06104 implements Runnable {
        final /* synthetic */ int val$time;

        C06104(int i) {
            this.val$time = i;
        }

        public void run() {
            FileLog.m16e("tmessages", "set last online from other device = " + this.val$time);
            NotificationsController.this.lastOnlineFromOtherDevice = this.val$time;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.5 */
    class C06125 implements Runnable {

        /* renamed from: com.hanista.mobogram.messenger.NotificationsController.5.1 */
        class C06111 implements Runnable {
            final /* synthetic */ ArrayList val$popupArray;

            C06111(ArrayList arrayList) {
                this.val$popupArray = arrayList;
            }

            public void run() {
                NotificationsController.this.popupReplyMessages = this.val$popupArray;
                Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                intent.putExtra("force", true);
                intent.setFlags(268763140);
                ApplicationLoader.applicationContext.startActivity(intent);
                ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            }
        }

        C06125() {
        }

        public void run() {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < NotificationsController.this.pushMessages.size(); i++) {
                MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessages.get(i);
                long dialogId = messageObject.getDialogId();
                if (!((messageObject.messageOwner.mentioned && (messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) || ((int) dialogId) == 0 || (messageObject.messageOwner.to_id.channel_id != 0 && !messageObject.isMegagroup()))) {
                    arrayList.add(0, messageObject);
                }
            }
            if (!arrayList.isEmpty() && !AndroidUtilities.needShowPasscode(false)) {
                AndroidUtilities.runOnUIThread(new C06111(arrayList));
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.6 */
    class C06146 implements Runnable {
        final /* synthetic */ SparseArray val$deletedMessages;
        final /* synthetic */ ArrayList val$popupArray;

        /* renamed from: com.hanista.mobogram.messenger.NotificationsController.6.1 */
        class C06131 implements Runnable {
            C06131() {
            }

            public void run() {
                NotificationsController.this.popupMessages = C06146.this.val$popupArray;
            }
        }

        C06146(SparseArray sparseArray, ArrayList arrayList) {
            this.val$deletedMessages = sparseArray;
            this.val$popupArray = arrayList;
        }

        public void run() {
            int access$400 = NotificationsController.this.total_unread_count;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            for (int i = 0; i < this.val$deletedMessages.size(); i++) {
                int keyAt = this.val$deletedMessages.keyAt(i);
                long j = (long) (-keyAt);
                ArrayList arrayList = (ArrayList) this.val$deletedMessages.get(keyAt);
                Integer num = (Integer) NotificationsController.this.pushDialogs.get(Long.valueOf(j));
                Integer valueOf = num == null ? Integer.valueOf(0) : num;
                Integer num2 = valueOf;
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    long intValue = ((long) ((Integer) arrayList.get(i2)).intValue()) | (((long) keyAt) << 32);
                    MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessagesDict.get(Long.valueOf(intValue));
                    if (messageObject != null) {
                        NotificationsController.this.pushMessagesDict.remove(Long.valueOf(intValue));
                        NotificationsController.this.delayedPushMessages.remove(messageObject);
                        NotificationsController.this.pushMessages.remove(messageObject);
                        if (NotificationsController.this.isPersonalMessage(messageObject)) {
                            NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                        }
                        if (this.val$popupArray != null) {
                            this.val$popupArray.remove(messageObject);
                        }
                        num2 = Integer.valueOf(num2.intValue() - 1);
                    }
                }
                if (num2.intValue() <= 0) {
                    num2 = Integer.valueOf(0);
                    NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(j));
                }
                if (!num2.equals(valueOf)) {
                    NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - valueOf.intValue();
                    NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + num2.intValue();
                    NotificationsController.this.pushDialogs.put(Long.valueOf(j), num2);
                }
                if (num2.intValue() == 0) {
                    NotificationsController.this.pushDialogs.remove(Long.valueOf(j));
                    NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(j));
                    if (!(this.val$popupArray == null || !NotificationsController.this.pushMessages.isEmpty() || this.val$popupArray.isEmpty())) {
                        this.val$popupArray.clear();
                    }
                }
            }
            if (this.val$popupArray != null) {
                AndroidUtilities.runOnUIThread(new C06131());
            }
            if (access$400 != NotificationsController.this.total_unread_count) {
                if (NotificationsController.this.notifyCheck) {
                    NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance().getCurrentTime());
                } else {
                    NotificationsController.this.delayedPushMessages.clear();
                    NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                }
            }
            NotificationsController.this.notifyCheck = false;
            if (sharedPreferences.getBoolean("badgeNumber", true)) {
                NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.7 */
    class C06167 implements Runnable {
        final /* synthetic */ long val$dialog_id;
        final /* synthetic */ SparseArray val$inbox;
        final /* synthetic */ boolean val$isPopup;
        final /* synthetic */ int val$max_date;
        final /* synthetic */ int val$max_id;
        final /* synthetic */ ArrayList val$popupArray;

        /* renamed from: com.hanista.mobogram.messenger.NotificationsController.7.1 */
        class C06151 implements Runnable {
            C06151() {
            }

            public void run() {
                NotificationsController.this.popupMessages = C06167.this.val$popupArray;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
            }
        }

        C06167(ArrayList arrayList, SparseArray sparseArray, long j, int i, int i2, boolean z) {
            this.val$popupArray = arrayList;
            this.val$inbox = sparseArray;
            this.val$dialog_id = j;
            this.val$max_id = i;
            this.val$max_date = i2;
            this.val$isPopup = z;
        }

        public void run() {
            int i;
            int i2;
            MessageObject messageObject;
            int size = this.val$popupArray != null ? this.val$popupArray.size() : 0;
            if (this.val$inbox != null) {
                for (i = 0; i < this.val$inbox.size(); i++) {
                    int keyAt = this.val$inbox.keyAt(i);
                    long longValue = ((Long) this.val$inbox.get(keyAt)).longValue();
                    i2 = 0;
                    while (i2 < NotificationsController.this.pushMessages.size()) {
                        messageObject = (MessageObject) NotificationsController.this.pushMessages.get(i2);
                        if (messageObject.getDialogId() == ((long) keyAt) && messageObject.getId() <= ((int) longValue)) {
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                this.this$0.personal_count = NotificationsController.this.personal_count - 1;
                            }
                            if (this.val$popupArray != null) {
                                this.val$popupArray.remove(messageObject);
                            }
                            long j = (long) messageObject.messageOwner.id;
                            if (messageObject.messageOwner.to_id.channel_id != 0) {
                                j |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                            }
                            NotificationsController.this.pushMessagesDict.remove(Long.valueOf(j));
                            NotificationsController.this.delayedPushMessages.remove(messageObject);
                            NotificationsController.this.pushMessages.remove(i2);
                            i2--;
                        }
                        i2++;
                    }
                }
                if (!(this.val$popupArray == null || !NotificationsController.this.pushMessages.isEmpty() || this.val$popupArray.isEmpty())) {
                    this.val$popupArray.clear();
                }
            }
            if (!(this.val$dialog_id == 0 || (this.val$max_id == 0 && this.val$max_date == 0))) {
                i = 0;
                while (i < NotificationsController.this.pushMessages.size()) {
                    messageObject = (MessageObject) NotificationsController.this.pushMessages.get(i);
                    if (messageObject.getDialogId() == this.val$dialog_id) {
                        Object obj;
                        if (this.val$max_date != 0) {
                            if (messageObject.messageOwner.date <= this.val$max_date) {
                                obj = 1;
                            }
                            obj = null;
                        } else if (this.val$isPopup) {
                            if (messageObject.getId() == this.val$max_id || this.val$max_id < 0) {
                                i2 = 1;
                            }
                            obj = null;
                        } else {
                            if (messageObject.getId() <= this.val$max_id || this.val$max_id < 0) {
                                i2 = 1;
                            }
                            obj = null;
                        }
                        if (obj != null) {
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                this.this$0.personal_count = NotificationsController.this.personal_count - 1;
                            }
                            NotificationsController.this.pushMessages.remove(i);
                            NotificationsController.this.delayedPushMessages.remove(messageObject);
                            if (this.val$popupArray != null) {
                                this.val$popupArray.remove(messageObject);
                            }
                            long j2 = (long) messageObject.messageOwner.id;
                            if (messageObject.messageOwner.to_id.channel_id != 0) {
                                j2 |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                            }
                            NotificationsController.this.pushMessagesDict.remove(Long.valueOf(j2));
                            i--;
                        }
                    }
                    i++;
                }
                if (!(this.val$popupArray == null || !NotificationsController.this.pushMessages.isEmpty() || this.val$popupArray.isEmpty())) {
                    this.val$popupArray.clear();
                }
            }
            if (this.val$popupArray != null && size != this.val$popupArray.size()) {
                AndroidUtilities.runOnUIThread(new C06151());
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.8 */
    class C06188 implements Runnable {
        final /* synthetic */ boolean val$isLast;
        final /* synthetic */ ArrayList val$messageObjects;
        final /* synthetic */ ArrayList val$popupArray;

        /* renamed from: com.hanista.mobogram.messenger.NotificationsController.8.1 */
        class C06171 implements Runnable {
            final /* synthetic */ int val$popupFinal;

            C06171(int i) {
                this.val$popupFinal = i;
            }

            public void run() {
                NotificationsController.this.popupMessages = C06188.this.val$popupArray;
                if (!ApplicationLoader.mainInterfacePaused && (ApplicationLoader.isScreenOn || UserConfig.isWaitingForPasscodeEnter)) {
                    return;
                }
                if (this.val$popupFinal == 3 || ((this.val$popupFinal == 1 && ApplicationLoader.isScreenOn) || (this.val$popupFinal == 2 && !ApplicationLoader.isScreenOn))) {
                    Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                    intent.setFlags(268763140);
                    ApplicationLoader.applicationContext.startActivity(intent);
                }
            }
        }

        C06188(ArrayList arrayList, ArrayList arrayList2, boolean z) {
            this.val$popupArray = arrayList;
            this.val$messageObjects = arrayList2;
            this.val$isLast = z;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r22 = this;
            r6 = 0;
            r0 = r22;
            r4 = r0.val$popupArray;
            r16 = r4.size();
            r17 = new java.util.HashMap;
            r17.<init>();
            r4 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;
            r5 = "Notifications";
            r7 = 0;
            r18 = r4.getSharedPreferences(r5, r7);
            r4 = "PinnedMessages";
            r5 = 1;
            r0 = r18;
            r19 = r0.getBoolean(r4, r5);
            r5 = 0;
            r4 = 0;
            r7 = r6;
            r6 = r4;
        L_0x0026:
            r0 = r22;
            r4 = r0.val$messageObjects;
            r4 = r4.size();
            if (r6 >= r4) goto L_0x018d;
        L_0x0030:
            r0 = r22;
            r4 = r0.val$messageObjects;
            r4 = r4.get(r6);
            r4 = (com.hanista.mobogram.messenger.MessageObject) r4;
            r8 = r4.messageOwner;
            r8 = r8.id;
            r8 = (long) r8;
            r10 = r4.messageOwner;
            r10 = r10.to_id;
            r10 = r10.channel_id;
            if (r10 == 0) goto L_0x0052;
        L_0x0047:
            r10 = r4.messageOwner;
            r10 = r10.to_id;
            r10 = r10.channel_id;
            r10 = (long) r10;
            r12 = 32;
            r10 = r10 << r12;
            r8 = r8 | r10;
        L_0x0052:
            r0 = r22;
            r10 = com.hanista.mobogram.messenger.NotificationsController.this;
            r10 = r10.pushMessagesDict;
            r11 = java.lang.Long.valueOf(r8);
            r10 = r10.containsKey(r11);
            if (r10 == 0) goto L_0x0068;
        L_0x0064:
            r4 = r6 + 1;
            r6 = r4;
            goto L_0x0026;
        L_0x0068:
            r12 = r4.getDialogId();
            r0 = r22;
            r10 = com.hanista.mobogram.messenger.NotificationsController.this;
            r10 = r10.opened_dialog_id;
            r10 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1));
            if (r10 != 0) goto L_0x0084;
        L_0x0078:
            r10 = com.hanista.mobogram.messenger.ApplicationLoader.isScreenOn;
            if (r10 == 0) goto L_0x0084;
        L_0x007c:
            r0 = r22;
            r4 = com.hanista.mobogram.messenger.NotificationsController.this;
            r4.playInChatSound();
            goto L_0x0064;
        L_0x0084:
            r10 = r4.messageOwner;
            r10 = r10.mentioned;
            if (r10 == 0) goto L_0x01c5;
        L_0x008a:
            if (r19 != 0) goto L_0x0094;
        L_0x008c:
            r10 = r4.messageOwner;
            r10 = r10.action;
            r10 = r10 instanceof com.hanista.mobogram.tgnet.TLRPC.TL_messageActionPinMessage;
            if (r10 != 0) goto L_0x0064;
        L_0x0094:
            r5 = r4.messageOwner;
            r5 = r5.from_id;
            r10 = (long) r5;
        L_0x0099:
            r0 = r22;
            r5 = com.hanista.mobogram.messenger.NotificationsController.this;
            r5 = r5.isPersonalMessage(r4);
            if (r5 == 0) goto L_0x00aa;
        L_0x00a3:
            r0 = r22;
            r5 = com.hanista.mobogram.messenger.NotificationsController.this;
            r15.this$0.personal_count = r5.personal_count + 1;
        L_0x00aa:
            r14 = 1;
            r5 = java.lang.Long.valueOf(r10);
            r0 = r17;
            r5 = r0.get(r5);
            r5 = (java.lang.Boolean) r5;
            r7 = (int) r10;
            if (r7 >= 0) goto L_0x0171;
        L_0x00ba:
            r7 = 1;
            r15 = r7;
        L_0x00bc:
            r7 = (int) r10;
            if (r7 != 0) goto L_0x0175;
        L_0x00bf:
            r7 = 0;
        L_0x00c0:
            if (r5 != 0) goto L_0x0104;
        L_0x00c2:
            r0 = r22;
            r5 = com.hanista.mobogram.messenger.NotificationsController.this;
            r0 = r18;
            r5 = r5.getNotifyOverride(r0, r10);
            r20 = 2;
            r0 = r20;
            if (r5 == r0) goto L_0x018a;
        L_0x00d2:
            r20 = "EnableAll";
            r21 = 1;
            r0 = r18;
            r1 = r20;
            r2 = r21;
            r20 = r0.getBoolean(r1, r2);
            if (r20 == 0) goto L_0x00f4;
        L_0x00e3:
            if (r15 == 0) goto L_0x00f6;
        L_0x00e5:
            r15 = "EnableGroup";
            r20 = 1;
            r0 = r18;
            r1 = r20;
            r15 = r0.getBoolean(r15, r1);
            if (r15 != 0) goto L_0x00f6;
        L_0x00f4:
            if (r5 == 0) goto L_0x018a;
        L_0x00f6:
            r5 = 1;
        L_0x00f7:
            r5 = java.lang.Boolean.valueOf(r5);
            r15 = java.lang.Long.valueOf(r10);
            r0 = r17;
            r0.put(r15, r5);
        L_0x0104:
            r15 = r5;
            if (r7 == 0) goto L_0x01c2;
        L_0x0107:
            r5 = r4.messageOwner;
            r5 = r5.to_id;
            r5 = r5.channel_id;
            if (r5 == 0) goto L_0x01c2;
        L_0x010f:
            r5 = r4.isMegagroup();
            if (r5 != 0) goto L_0x01c2;
        L_0x0115:
            r5 = 0;
        L_0x0116:
            r7 = r15.booleanValue();
            if (r7 == 0) goto L_0x016e;
        L_0x011c:
            if (r5 == 0) goto L_0x0130;
        L_0x011e:
            r7 = java.lang.Long.valueOf(r10);
            r7 = com.hanista.mobogram.mobo.p012l.HiddenConfig.m1399b(r7);
            if (r7 != 0) goto L_0x0130;
        L_0x0128:
            r0 = r22;
            r7 = r0.val$popupArray;
            r15 = 0;
            r7.add(r15, r4);
        L_0x0130:
            r0 = r22;
            r7 = com.hanista.mobogram.messenger.NotificationsController.this;
            r7 = r7.delayedPushMessages;
            r7.add(r4);
            r0 = r22;
            r7 = com.hanista.mobogram.messenger.NotificationsController.this;
            r7 = r7.pushMessages;
            r15 = 0;
            r7.add(r15, r4);
            r0 = r22;
            r7 = com.hanista.mobogram.messenger.NotificationsController.this;
            r7 = r7.pushMessagesDict;
            r8 = java.lang.Long.valueOf(r8);
            r7.put(r8, r4);
            r4 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1));
            if (r4 == 0) goto L_0x016e;
        L_0x015a:
            r0 = r22;
            r4 = com.hanista.mobogram.messenger.NotificationsController.this;
            r4 = r4.pushDialogsOverrideMention;
            r7 = java.lang.Long.valueOf(r12);
            r8 = 1;
            r8 = java.lang.Integer.valueOf(r8);
            r4.put(r7, r8);
        L_0x016e:
            r7 = r14;
            goto L_0x0064;
        L_0x0171:
            r7 = 0;
            r15 = r7;
            goto L_0x00bc;
        L_0x0175:
            if (r15 == 0) goto L_0x0186;
        L_0x0177:
            r7 = "popupGroup";
        L_0x017a:
            r20 = 0;
            r0 = r18;
            r1 = r20;
            r7 = r0.getInt(r7, r1);
            goto L_0x00c0;
        L_0x0186:
            r7 = "popupAll";
            goto L_0x017a;
        L_0x018a:
            r5 = 0;
            goto L_0x00f7;
        L_0x018d:
            if (r7 == 0) goto L_0x019a;
        L_0x018f:
            r0 = r22;
            r4 = com.hanista.mobogram.messenger.NotificationsController.this;
            r0 = r22;
            r6 = r0.val$isLast;
            r4.notifyCheck = r6;
        L_0x019a:
            r0 = r22;
            r4 = r0.val$popupArray;
            r4 = r4.isEmpty();
            if (r4 != 0) goto L_0x01c1;
        L_0x01a4:
            r0 = r22;
            r4 = r0.val$popupArray;
            r4 = r4.size();
            r0 = r16;
            if (r0 == r4) goto L_0x01c1;
        L_0x01b0:
            r4 = 0;
            r4 = com.hanista.mobogram.messenger.AndroidUtilities.needShowPasscode(r4);
            if (r4 != 0) goto L_0x01c1;
        L_0x01b7:
            r4 = new com.hanista.mobogram.messenger.NotificationsController$8$1;
            r0 = r22;
            r4.<init>(r5);
            com.hanista.mobogram.messenger.AndroidUtilities.runOnUIThread(r4);
        L_0x01c1:
            return;
        L_0x01c2:
            r5 = r7;
            goto L_0x0116;
        L_0x01c5:
            r10 = r12;
            goto L_0x0099;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.NotificationsController.8.run():void");
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.NotificationsController.9 */
    class C06209 implements Runnable {
        final /* synthetic */ HashMap val$dialogsToUpdate;
        final /* synthetic */ ArrayList val$popupArray;

        /* renamed from: com.hanista.mobogram.messenger.NotificationsController.9.1 */
        class C06191 implements Runnable {
            C06191() {
            }

            public void run() {
                NotificationsController.this.popupMessages = C06209.this.val$popupArray;
            }
        }

        C06209(HashMap hashMap, ArrayList arrayList) {
            this.val$dialogsToUpdate = hashMap;
            this.val$popupArray = arrayList;
        }

        public void run() {
            int access$400 = NotificationsController.this.total_unread_count;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            for (Entry entry : this.val$dialogsToUpdate.entrySet()) {
                Integer num;
                boolean z;
                Integer num2;
                int i;
                MessageObject messageObject;
                long j;
                long longValue = ((Long) entry.getKey()).longValue();
                boolean access$1900 = NotificationsController.this.getNotifyOverride(sharedPreferences, longValue);
                if (NotificationsController.this.notifyCheck) {
                    num = (Integer) NotificationsController.this.pushDialogsOverrideMention.get(Long.valueOf(longValue));
                    if (num != null && num.intValue() == 1) {
                        NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(longValue), Integer.valueOf(0));
                        z = true;
                        access$1900 = z && ((sharedPreferences.getBoolean("EnableAll", true) && (((int) longValue) >= 0 || sharedPreferences.getBoolean("EnableGroup", true))) || z);
                        num = (Integer) NotificationsController.this.pushDialogs.get(Long.valueOf(longValue));
                        num2 = (Integer) entry.getValue();
                        if (num2.intValue() == 0) {
                            NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(longValue));
                        }
                        if (num2.intValue() < 0) {
                            if (num != null) {
                                num2 = Integer.valueOf(num2.intValue() + num.intValue());
                            }
                        }
                        if ((access$1900 || num2.intValue() == 0) && num != null) {
                            NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - num.intValue();
                        }
                        if (num2.intValue() == 0) {
                            NotificationsController.this.pushDialogs.remove(Long.valueOf(longValue));
                            NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(longValue));
                            i = 0;
                            while (i < NotificationsController.this.pushMessages.size()) {
                                messageObject = (MessageObject) NotificationsController.this.pushMessages.get(i);
                                if (messageObject.getDialogId() != longValue) {
                                    if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                        this.this$0.personal_count = NotificationsController.this.personal_count - 1;
                                    }
                                    NotificationsController.this.pushMessages.remove(i);
                                    i--;
                                    NotificationsController.this.delayedPushMessages.remove(messageObject);
                                    j = (long) messageObject.messageOwner.id;
                                    if (messageObject.messageOwner.to_id.channel_id != 0) {
                                        j |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                                    }
                                    NotificationsController.this.pushMessagesDict.remove(Long.valueOf(j));
                                    if (this.val$popupArray != null) {
                                        this.val$popupArray.remove(messageObject);
                                    }
                                }
                                i++;
                            }
                            if (!(this.val$popupArray == null || !NotificationsController.this.pushMessages.isEmpty() || this.val$popupArray.isEmpty())) {
                                this.val$popupArray.clear();
                            }
                        } else if (access$1900) {
                            NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + num2.intValue();
                            NotificationsController.this.pushDialogs.put(Long.valueOf(longValue), num2);
                        }
                    }
                }
                z = access$1900;
                if (z) {
                }
                num = (Integer) NotificationsController.this.pushDialogs.get(Long.valueOf(longValue));
                num2 = (Integer) entry.getValue();
                if (num2.intValue() == 0) {
                    NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(longValue));
                }
                if (num2.intValue() < 0) {
                    if (num != null) {
                        num2 = Integer.valueOf(num2.intValue() + num.intValue());
                    }
                }
                NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - num.intValue();
                if (num2.intValue() == 0) {
                    NotificationsController.this.pushDialogs.remove(Long.valueOf(longValue));
                    NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(longValue));
                    i = 0;
                    while (i < NotificationsController.this.pushMessages.size()) {
                        messageObject = (MessageObject) NotificationsController.this.pushMessages.get(i);
                        if (messageObject.getDialogId() != longValue) {
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                this.this$0.personal_count = NotificationsController.this.personal_count - 1;
                            }
                            NotificationsController.this.pushMessages.remove(i);
                            i--;
                            NotificationsController.this.delayedPushMessages.remove(messageObject);
                            j = (long) messageObject.messageOwner.id;
                            if (messageObject.messageOwner.to_id.channel_id != 0) {
                                j |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                            }
                            NotificationsController.this.pushMessagesDict.remove(Long.valueOf(j));
                            if (this.val$popupArray != null) {
                                this.val$popupArray.remove(messageObject);
                            }
                        }
                        i++;
                    }
                    this.val$popupArray.clear();
                } else if (access$1900) {
                    NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + num2.intValue();
                    NotificationsController.this.pushDialogs.put(Long.valueOf(longValue), num2);
                }
            }
            if (this.val$popupArray != null) {
                AndroidUtilities.runOnUIThread(new C06191());
            }
            if (access$400 != NotificationsController.this.total_unread_count) {
                if (NotificationsController.this.notifyCheck) {
                    NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance().getCurrentTime());
                } else {
                    NotificationsController.this.delayedPushMessages.clear();
                    NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                }
            }
            NotificationsController.this.notifyCheck = false;
            if (sharedPreferences.getBoolean("badgeNumber", true)) {
                NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
            }
        }
    }

    static {
        Instance = null;
    }

    public NotificationsController() {
        this.notificationsQueue = new DispatchQueue("notificationsQueue");
        this.pushMessages = new ArrayList();
        this.delayedPushMessages = new ArrayList();
        this.pushMessagesDict = new HashMap();
        this.smartNotificationsDialogs = new HashMap();
        this.notificationManager = null;
        this.pushDialogs = new HashMap();
        this.wearNotificationsIds = new HashMap();
        this.pushDialogsOverrideMention = new HashMap();
        this.popupMessages = new ArrayList();
        this.popupReplyMessages = new ArrayList();
        this.opened_dialog_id = 0;
        this.total_unread_count = 0;
        this.personal_count = 0;
        this.notifyCheck = false;
        this.lastOnlineFromOtherDevice = 0;
        this.inChatSoundEnabled = true;
        this.notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
        this.inChatSoundEnabled = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnableInChatSound", true);
        try {
            this.audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompatApi24.CATEGORY_ALARM);
        } catch (Throwable e2) {
            FileLog.m18e("tmessages", e2);
        }
        try {
            this.notificationDelayWakelock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, JoinPoint.SYNCHRONIZATION_LOCK);
            this.notificationDelayWakelock.setReferenceCounted(false);
        } catch (Throwable e22) {
            FileLog.m18e("tmessages", e22);
        }
        this.notificationDelayRunnable = new C06071();
    }

    private void dismissNotification() {
        try {
            this.notificationManager.cancel(1);
            this.pushMessages.clear();
            this.pushMessagesDict.clear();
            for (Entry value : this.wearNotificationsIds.entrySet()) {
                this.notificationManager.cancel(((Integer) value.getValue()).intValue());
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
                }
            });
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public static NotificationsController getInstance() {
        NotificationsController notificationsController = Instance;
        if (notificationsController == null) {
            synchronized (MessagesController.class) {
                notificationsController = Instance;
                if (notificationsController == null) {
                    notificationsController = new NotificationsController();
                    Instance = notificationsController;
                }
            }
        }
        return notificationsController;
    }

    private static String getLauncherClassName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent, 0)) {
                if (resolveInfo.activityInfo.applicationInfo.packageName.equalsIgnoreCase(context.getPackageName())) {
                    return resolveInfo.activityInfo.name;
                }
            }
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
        }
        return null;
    }

    private int getNotifyOverride(SharedPreferences sharedPreferences, long j) {
        int i = sharedPreferences.getInt("notify2_" + j, 0);
        return (i != 3 || sharedPreferences.getInt("notifyuntil_" + j, 0) < ConnectionsManager.getInstance().getCurrentTime()) ? i : 2;
    }

    private String getStringForMessage(MessageObject messageObject, boolean z) {
        long j;
        String str;
        User user;
        Object obj;
        Chat chat;
        Chat chat2;
        long j2 = messageObject.messageOwner.dialog_id;
        int i = messageObject.messageOwner.to_id.chat_id != 0 ? messageObject.messageOwner.to_id.chat_id : messageObject.messageOwner.to_id.channel_id;
        int i2 = messageObject.messageOwner.to_id.user_id;
        int i3 = i2 == 0 ? (messageObject.isFromUser() || messageObject.getId() < 0) ? messageObject.messageOwner.from_id : -i : i2 == UserConfig.getClientUserId() ? messageObject.messageOwner.from_id : i2;
        if (j2 == 0) {
            if (i != 0) {
                j = (long) (-i);
            } else if (i3 != 0) {
                j = (long) i3;
            }
            str = null;
            if (i3 <= 0) {
                user = MessagesController.getInstance().getUser(Integer.valueOf(i3));
                if (user != null) {
                    str = UserObject.getUserName(user);
                }
                obj = str;
            } else {
                chat = MessagesController.getInstance().getChat(Integer.valueOf(-i3));
                String str2 = chat == null ? chat.title : null;
            }
            if (obj == null) {
                return null;
            }
            if (i == 0) {
                chat2 = MessagesController.getInstance().getChat(Integer.valueOf(i));
                if (chat2 == null) {
                    return null;
                }
                chat = chat2;
            } else {
                chat = null;
            }
            if (((int) j) != 0 || AndroidUtilities.needShowPasscode(false) || UserConfig.isWaitingForPasscodeEnter || HiddenConfig.m1399b(Long.valueOf(j))) {
                return (HiddenConfig.m1399b(Long.valueOf(j)) || MoboConstants.an) ? LocaleController.getString("YouHaveNewMessage", C0338R.string.YouHaveNewMessage) : null;
            } else {
                String userName;
                if (i != 0 || i3 == 0) {
                    if (i != 0) {
                        if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnablePreviewGroup", true)) {
                            if (messageObject.messageOwner instanceof TL_messageService) {
                                if (messageObject.messageOwner.action instanceof TL_messageActionChatAddUser) {
                                    i = messageObject.messageOwner.action.user_id;
                                    if (i == 0 && messageObject.messageOwner.action.users.size() == 1) {
                                        i = ((Integer) messageObject.messageOwner.action.users.get(0)).intValue();
                                    }
                                    User user2;
                                    if (i == 0) {
                                        StringBuilder stringBuilder = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                                        for (i2 = 0; i2 < messageObject.messageOwner.action.users.size(); i2++) {
                                            user2 = MessagesController.getInstance().getUser((Integer) messageObject.messageOwner.action.users.get(i2));
                                            if (user2 != null) {
                                                userName = UserObject.getUserName(user2);
                                                if (stringBuilder.length() != 0) {
                                                    stringBuilder.append(", ");
                                                }
                                                stringBuilder.append(userName);
                                            }
                                        }
                                        return LocaleController.formatString("NotificationGroupAddMember", C0338R.string.NotificationGroupAddMember, obj, chat.title, stringBuilder.toString());
                                    } else if (messageObject.messageOwner.to_id.channel_id != 0 && !chat.megagroup) {
                                        return LocaleController.formatString("ChannelAddedByNotification", C0338R.string.ChannelAddedByNotification, obj, chat.title);
                                    } else if (i == UserConfig.getClientUserId()) {
                                        return LocaleController.formatString("NotificationInvitedToGroup", C0338R.string.NotificationInvitedToGroup, obj, chat.title);
                                    } else {
                                        user2 = MessagesController.getInstance().getUser(Integer.valueOf(i));
                                        if (user2 == null) {
                                            return null;
                                        }
                                        if (i3 != user2.id) {
                                            return LocaleController.formatString("NotificationGroupAddMember", C0338R.string.NotificationGroupAddMember, obj, chat.title, UserObject.getUserName(user2));
                                        } else if (chat.megagroup) {
                                            return LocaleController.formatString("NotificationGroupAddSelfMega", C0338R.string.NotificationGroupAddSelfMega, obj, chat.title);
                                        } else {
                                            return LocaleController.formatString("NotificationGroupAddSelf", C0338R.string.NotificationGroupAddSelf, obj, chat.title);
                                        }
                                    }
                                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatJoinedByLink) {
                                    return LocaleController.formatString("NotificationInvitedToGroupByLink", C0338R.string.NotificationInvitedToGroupByLink, obj, chat.title);
                                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatEditTitle) {
                                    return LocaleController.formatString("NotificationEditedGroupName", C0338R.string.NotificationEditedGroupName, obj, messageObject.messageOwner.action.title);
                                } else if ((messageObject.messageOwner.action instanceof TL_messageActionChatEditPhoto) || (messageObject.messageOwner.action instanceof TL_messageActionChatDeletePhoto)) {
                                    if (messageObject.messageOwner.to_id.channel_id == 0 || chat.megagroup) {
                                        return LocaleController.formatString("NotificationEditedGroupPhoto", C0338R.string.NotificationEditedGroupPhoto, obj, chat.title);
                                    }
                                    return LocaleController.formatString("ChannelPhotoEditNotification", C0338R.string.ChannelPhotoEditNotification, chat.title);
                                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatDeleteUser) {
                                    if (messageObject.messageOwner.action.user_id == UserConfig.getClientUserId()) {
                                        return LocaleController.formatString("NotificationGroupKickYou", C0338R.string.NotificationGroupKickYou, obj, chat.title);
                                    } else if (messageObject.messageOwner.action.user_id == i3) {
                                        return LocaleController.formatString("NotificationGroupLeftMember", C0338R.string.NotificationGroupLeftMember, obj, chat.title);
                                    } else {
                                        if (MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.action.user_id)) == null) {
                                            return null;
                                        }
                                        return LocaleController.formatString("NotificationGroupKickMember", C0338R.string.NotificationGroupKickMember, obj, chat.title, UserObject.getUserName(MessagesController.getInstance().getUser(Integer.valueOf(messageObject.messageOwner.action.user_id))));
                                    }
                                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatCreate) {
                                    return messageObject.messageText.toString();
                                } else {
                                    if (messageObject.messageOwner.action instanceof TL_messageActionChannelCreate) {
                                        return messageObject.messageText.toString();
                                    }
                                    if (messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                                        return LocaleController.formatString("ActionMigrateFromGroupNotify", C0338R.string.ActionMigrateFromGroupNotify, chat.title);
                                    } else if (messageObject.messageOwner.action instanceof TL_messageActionChannelMigrateFrom) {
                                        return LocaleController.formatString("ActionMigrateFromGroupNotify", C0338R.string.ActionMigrateFromGroupNotify, messageObject.messageOwner.action.title);
                                    } else if (messageObject.messageOwner.action instanceof TL_messageActionPinMessage) {
                                        if (messageObject.replyMessageObject != null) {
                                            MessageObject messageObject2 = messageObject.replyMessageObject;
                                            if (messageObject2.isMusic()) {
                                                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedMusic", C0338R.string.NotificationActionPinnedMusic, obj, chat.title);
                                                }
                                                return LocaleController.formatString("NotificationActionPinnedMusicChannel", C0338R.string.NotificationActionPinnedMusicChannel, chat.title);
                                            } else if (messageObject2.isVideo()) {
                                                if (VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(messageObject2.messageOwner.media.caption)) {
                                                    userName = "\ud83d\udcf9 " + messageObject2.messageOwner.media.caption;
                                                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                        return LocaleController.formatString("NotificationActionPinnedText", C0338R.string.NotificationActionPinnedText, obj, userName, chat.title);
                                                    }
                                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", C0338R.string.NotificationActionPinnedTextChannel, chat.title, userName);
                                                } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedVideo", C0338R.string.NotificationActionPinnedVideo, obj, chat.title);
                                                } else {
                                                    return LocaleController.formatString("NotificationActionPinnedVideoChannel", C0338R.string.NotificationActionPinnedVideoChannel, chat.title);
                                                }
                                            } else if (messageObject2.isGif()) {
                                                if (VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(messageObject2.messageOwner.media.caption)) {
                                                    userName = "\ud83c\udfac " + messageObject2.messageOwner.media.caption;
                                                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                        return LocaleController.formatString("NotificationActionPinnedText", C0338R.string.NotificationActionPinnedText, obj, userName, chat.title);
                                                    }
                                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", C0338R.string.NotificationActionPinnedTextChannel, chat.title, userName);
                                                } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedGif", C0338R.string.NotificationActionPinnedGif, obj, chat.title);
                                                } else {
                                                    return LocaleController.formatString("NotificationActionPinnedGifChannel", C0338R.string.NotificationActionPinnedGifChannel, chat.title);
                                                }
                                            } else if (messageObject2.isVoice()) {
                                                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedVoice", C0338R.string.NotificationActionPinnedVoice, obj, chat.title);
                                                }
                                                return LocaleController.formatString("NotificationActionPinnedVoiceChannel", C0338R.string.NotificationActionPinnedVoiceChannel, chat.title);
                                            } else if (messageObject2.isSticker()) {
                                                if (messageObject.getStickerEmoji() != null) {
                                                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                        return LocaleController.formatString("NotificationActionPinnedStickerEmoji", C0338R.string.NotificationActionPinnedStickerEmoji, obj, chat.title, userName);
                                                    }
                                                    return LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", C0338R.string.NotificationActionPinnedStickerEmojiChannel, chat.title, userName);
                                                } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedSticker", C0338R.string.NotificationActionPinnedSticker, obj, chat.title);
                                                } else {
                                                    return LocaleController.formatString("NotificationActionPinnedStickerChannel", C0338R.string.NotificationActionPinnedStickerChannel, chat.title);
                                                }
                                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaDocument) {
                                                if (VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(messageObject2.messageOwner.media.caption)) {
                                                    userName = "\ud83d\udcce " + messageObject2.messageOwner.media.caption;
                                                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                        return LocaleController.formatString("NotificationActionPinnedText", C0338R.string.NotificationActionPinnedText, obj, userName, chat.title);
                                                    }
                                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", C0338R.string.NotificationActionPinnedTextChannel, chat.title, userName);
                                                } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedFile", C0338R.string.NotificationActionPinnedFile, obj, chat.title);
                                                } else {
                                                    return LocaleController.formatString("NotificationActionPinnedFileChannel", C0338R.string.NotificationActionPinnedFileChannel, chat.title);
                                                }
                                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaGeo) {
                                                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedGeo", C0338R.string.NotificationActionPinnedGeo, obj, chat.title);
                                                }
                                                return LocaleController.formatString("NotificationActionPinnedGeoChannel", C0338R.string.NotificationActionPinnedGeoChannel, chat.title);
                                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaContact) {
                                                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedContact", C0338R.string.NotificationActionPinnedContact, obj, chat.title);
                                                }
                                                return LocaleController.formatString("NotificationActionPinnedContactChannel", C0338R.string.NotificationActionPinnedContactChannel, chat.title);
                                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaPhoto) {
                                                if (VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(messageObject2.messageOwner.media.caption)) {
                                                    userName = "\ud83d\uddbc " + messageObject2.messageOwner.media.caption;
                                                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                        return LocaleController.formatString("NotificationActionPinnedText", C0338R.string.NotificationActionPinnedText, obj, userName, chat.title);
                                                    }
                                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", C0338R.string.NotificationActionPinnedTextChannel, chat.title, userName);
                                                } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedPhoto", C0338R.string.NotificationActionPinnedPhoto, obj, chat.title);
                                                } else {
                                                    return LocaleController.formatString("NotificationActionPinnedPhotoChannel", C0338R.string.NotificationActionPinnedPhotoChannel, chat.title);
                                                }
                                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                                                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedGame", C0338R.string.NotificationActionPinnedGame, obj, chat.title);
                                                }
                                                return LocaleController.formatString("NotificationActionPinnedGameChannel", C0338R.string.NotificationActionPinnedGameChannel, chat.title);
                                            } else if (messageObject2.messageText != null && messageObject2.messageText.length() > 0) {
                                                CharSequence charSequence = messageObject2.messageText;
                                                if (charSequence.length() > 20) {
                                                    charSequence = charSequence.subSequence(0, 20) + "...";
                                                }
                                                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                    return LocaleController.formatString("NotificationActionPinnedText", C0338R.string.NotificationActionPinnedText, obj, charSequence, chat.title);
                                                }
                                                return LocaleController.formatString("NotificationActionPinnedTextChannel", C0338R.string.NotificationActionPinnedTextChannel, chat.title, charSequence);
                                            } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                                return LocaleController.formatString("NotificationActionPinnedNoText", C0338R.string.NotificationActionPinnedNoText, obj, chat.title);
                                            } else {
                                                return LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0338R.string.NotificationActionPinnedNoTextChannel, chat.title);
                                            }
                                        } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                            return LocaleController.formatString("NotificationActionPinnedNoText", C0338R.string.NotificationActionPinnedNoText, obj, chat.title);
                                        } else {
                                            return LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0338R.string.NotificationActionPinnedNoTextChannel, obj, chat.title);
                                        }
                                    } else if (messageObject.messageOwner.action instanceof TL_messageActionGameScore) {
                                        return messageObject.messageText.toString();
                                    }
                                }
                            } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                                if (messageObject.isMediaEmpty()) {
                                    if (z || messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) {
                                        return LocaleController.formatString("NotificationMessageGroupNoText", C0338R.string.NotificationMessageGroupNoText, obj, chat.title);
                                    }
                                    return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, messageObject.messageOwner.message);
                                } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                                    if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                        return LocaleController.formatString("NotificationMessageGroupPhoto", C0338R.string.NotificationMessageGroupPhoto, obj, chat.title);
                                    }
                                    return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83d\uddbc " + messageObject.messageOwner.media.caption);
                                } else if (messageObject.isVideo()) {
                                    if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                        return LocaleController.formatString("NotificationMessageGroupVideo", C0338R.string.NotificationMessageGroupVideo, obj, chat.title);
                                    }
                                    return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83d\udcf9 " + messageObject.messageOwner.media.caption);
                                } else if (messageObject.isVoice()) {
                                    return LocaleController.formatString("NotificationMessageGroupAudio", C0338R.string.NotificationMessageGroupAudio, obj, chat.title);
                                } else if (messageObject.isMusic()) {
                                    return LocaleController.formatString("NotificationMessageGroupMusic", C0338R.string.NotificationMessageGroupMusic, obj, chat.title);
                                } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                                    return LocaleController.formatString("NotificationMessageGroupContact", C0338R.string.NotificationMessageGroupContact, obj, chat.title);
                                } else if (messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                                    return LocaleController.formatString("NotificationMessageGroupGame", C0338R.string.NotificationMessageGroupGame, obj, chat.title, messageObject.messageOwner.media.game.title);
                                } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                                    return LocaleController.formatString("NotificationMessageGroupMap", C0338R.string.NotificationMessageGroupMap, obj, chat.title);
                                } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                                    if (messageObject.isSticker()) {
                                        if (messageObject.getStickerEmoji() != null) {
                                            return LocaleController.formatString("NotificationMessageGroupStickerEmoji", C0338R.string.NotificationMessageGroupStickerEmoji, obj, chat.title, messageObject.getStickerEmoji());
                                        }
                                        return LocaleController.formatString("NotificationMessageGroupSticker", C0338R.string.NotificationMessageGroupSticker, obj, chat.title);
                                    } else if (messageObject.isGif()) {
                                        if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                            return LocaleController.formatString("NotificationMessageGroupGif", C0338R.string.NotificationMessageGroupGif, obj, chat.title);
                                        }
                                        return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83c\udfac " + messageObject.messageOwner.media.caption);
                                    } else if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                        return LocaleController.formatString("NotificationMessageGroupDocument", C0338R.string.NotificationMessageGroupDocument, obj, chat.title);
                                    } else {
                                        return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83d\udcce " + messageObject.messageOwner.media.caption);
                                    }
                                }
                            } else if (messageObject.messageOwner.post) {
                                if (messageObject.isMediaEmpty()) {
                                    if (z || messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) {
                                        return LocaleController.formatString("ChannelMessageNoText", C0338R.string.ChannelMessageNoText, obj, chat.title);
                                    }
                                    return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, messageObject.messageOwner.message);
                                } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                                    if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                        return LocaleController.formatString("ChannelMessagePhoto", C0338R.string.ChannelMessagePhoto, obj, chat.title);
                                    }
                                    return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83d\uddbc " + messageObject.messageOwner.media.caption);
                                } else if (messageObject.isVideo()) {
                                    if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                        return LocaleController.formatString("ChannelMessageVideo", C0338R.string.ChannelMessageVideo, obj, chat.title);
                                    }
                                    return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83d\udcf9 " + messageObject.messageOwner.media.caption);
                                } else if (messageObject.isVoice()) {
                                    return LocaleController.formatString("ChannelMessageAudio", C0338R.string.ChannelMessageAudio, obj, chat.title);
                                } else if (messageObject.isMusic()) {
                                    return LocaleController.formatString("ChannelMessageMusic", C0338R.string.ChannelMessageMusic, obj, chat.title);
                                } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                                    return LocaleController.formatString("ChannelMessageContact", C0338R.string.ChannelMessageContact, obj, chat.title);
                                } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                                    return LocaleController.formatString("ChannelMessageMap", C0338R.string.ChannelMessageMap, obj, chat.title);
                                } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                                    if (messageObject.isSticker()) {
                                        if (messageObject.getStickerEmoji() != null) {
                                            return LocaleController.formatString("ChannelMessageStickerEmoji", C0338R.string.ChannelMessageStickerEmoji, obj, chat.title, messageObject.getStickerEmoji());
                                        }
                                        return LocaleController.formatString("ChannelMessageSticker", C0338R.string.ChannelMessageSticker, obj, chat.title);
                                    } else if (messageObject.isGif()) {
                                        if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                            return LocaleController.formatString("ChannelMessageGIF", C0338R.string.ChannelMessageGIF, obj, chat.title);
                                        }
                                        return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83c\udfac " + messageObject.messageOwner.media.caption);
                                    } else if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                        return LocaleController.formatString("ChannelMessageDocument", C0338R.string.ChannelMessageDocument, obj, chat.title);
                                    } else {
                                        return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83d\udcce " + messageObject.messageOwner.media.caption);
                                    }
                                }
                            } else if (messageObject.isMediaEmpty()) {
                                if (z || messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) {
                                    return LocaleController.formatString("ChannelMessageGroupNoText", C0338R.string.ChannelMessageGroupNoText, obj, chat.title);
                                }
                                return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, messageObject.messageOwner.message);
                            } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                                if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                    return LocaleController.formatString("ChannelMessageGroupPhoto", C0338R.string.ChannelMessageGroupPhoto, obj, chat.title);
                                }
                                return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83d\uddbc " + messageObject.messageOwner.media.caption);
                            } else if (messageObject.isVideo()) {
                                if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                    return LocaleController.formatString("ChannelMessageGroupVideo", C0338R.string.ChannelMessageGroupVideo, obj, chat.title);
                                }
                                return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83d\udcf9 " + messageObject.messageOwner.media.caption);
                            } else if (messageObject.isVoice()) {
                                return LocaleController.formatString("ChannelMessageGroupAudio", C0338R.string.ChannelMessageGroupAudio, obj, chat.title);
                            } else if (messageObject.isMusic()) {
                                return LocaleController.formatString("ChannelMessageGroupMusic", C0338R.string.ChannelMessageGroupMusic, obj, chat.title);
                            } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                                return LocaleController.formatString("ChannelMessageGroupContact", C0338R.string.ChannelMessageGroupContact, obj, chat.title);
                            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                                return LocaleController.formatString("ChannelMessageGroupMap", C0338R.string.ChannelMessageGroupMap, obj, chat.title);
                            } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                                if (messageObject.isSticker()) {
                                    if (messageObject.getStickerEmoji() != null) {
                                        return LocaleController.formatString("ChannelMessageGroupStickerEmoji", C0338R.string.ChannelMessageGroupStickerEmoji, obj, chat.title, messageObject.getStickerEmoji());
                                    }
                                    return LocaleController.formatString("ChannelMessageGroupSticker", C0338R.string.ChannelMessageGroupSticker, obj, chat.title);
                                } else if (messageObject.isGif()) {
                                    if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                        return LocaleController.formatString("ChannelMessageGroupGif", C0338R.string.ChannelMessageGroupGif, obj, chat.title);
                                    }
                                    return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83c\udfac " + messageObject.messageOwner.media.caption);
                                } else if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                                    return LocaleController.formatString("ChannelMessageGroupDocument", C0338R.string.ChannelMessageGroupDocument, obj, chat.title);
                                } else {
                                    return LocaleController.formatString("NotificationMessageGroupText", C0338R.string.NotificationMessageGroupText, obj, chat.title, "\ud83d\udcce " + messageObject.messageOwner.media.caption);
                                }
                            }
                        } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            return LocaleController.formatString("NotificationMessageGroupNoText", C0338R.string.NotificationMessageGroupNoText, obj, chat.title);
                        } else {
                            return LocaleController.formatString("ChannelMessageNoText", C0338R.string.ChannelMessageNoText, obj, chat.title);
                        }
                    }
                } else if (!ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnablePreviewAll", true)) {
                    return LocaleController.formatString("NotificationMessageNoText", C0338R.string.NotificationMessageNoText, obj);
                } else if (messageObject.messageOwner instanceof TL_messageService) {
                    if (messageObject.messageOwner.action instanceof TL_messageActionUserJoined) {
                        return LocaleController.formatString("NotificationContactJoined", C0338R.string.NotificationContactJoined, obj);
                    } else if (messageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                        return LocaleController.formatString("NotificationContactNewPhoto", C0338R.string.NotificationContactNewPhoto, obj);
                    } else if (messageObject.messageOwner.action instanceof TL_messageActionLoginUnknownLocation) {
                        userName = LocaleController.formatString("formatDateAtTime", C0338R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(((long) messageObject.messageOwner.date) * 1000), LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000));
                        return LocaleController.formatString("NotificationUnrecognizedDevice", C0338R.string.NotificationUnrecognizedDevice, UserConfig.getCurrentUser().first_name, userName, messageObject.messageOwner.action.title, messageObject.messageOwner.action.address);
                    } else if (messageObject.messageOwner.action instanceof TL_messageActionGameScore) {
                        return messageObject.messageText.toString();
                    }
                } else if (messageObject.isMediaEmpty()) {
                    if (z) {
                        return LocaleController.formatString("NotificationMessageNoText", C0338R.string.NotificationMessageNoText, obj);
                    } else if (messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) {
                        return LocaleController.formatString("NotificationMessageNoText", C0338R.string.NotificationMessageNoText, obj);
                    } else {
                        return LocaleController.formatString("NotificationMessageText", C0338R.string.NotificationMessageText, obj, messageObject.messageOwner.message);
                    }
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                    if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                        return LocaleController.formatString("NotificationMessagePhoto", C0338R.string.NotificationMessagePhoto, obj);
                    }
                    return LocaleController.formatString("NotificationMessageText", C0338R.string.NotificationMessageText, obj, "\ud83d\uddbc " + messageObject.messageOwner.media.caption);
                } else if (messageObject.isVideo()) {
                    if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                        return LocaleController.formatString("NotificationMessageVideo", C0338R.string.NotificationMessageVideo, obj);
                    }
                    return LocaleController.formatString("NotificationMessageText", C0338R.string.NotificationMessageText, obj, "\ud83d\udcf9 " + messageObject.messageOwner.media.caption);
                } else if (messageObject.isGame()) {
                    return LocaleController.formatString("NotificationMessageGame", C0338R.string.NotificationMessageGame, obj, messageObject.messageOwner.media.game.title);
                } else if (messageObject.isVoice()) {
                    return LocaleController.formatString("NotificationMessageAudio", C0338R.string.NotificationMessageAudio, obj);
                } else if (messageObject.isMusic()) {
                    return LocaleController.formatString("NotificationMessageMusic", C0338R.string.NotificationMessageMusic, obj);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                    return LocaleController.formatString("NotificationMessageContact", C0338R.string.NotificationMessageContact, obj);
                } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                    return LocaleController.formatString("NotificationMessageMap", C0338R.string.NotificationMessageMap, obj);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                    if (messageObject.isSticker()) {
                        if (messageObject.getStickerEmoji() != null) {
                            return LocaleController.formatString("NotificationMessageStickerEmoji", C0338R.string.NotificationMessageStickerEmoji, obj, messageObject.getStickerEmoji());
                        }
                        return LocaleController.formatString("NotificationMessageSticker", C0338R.string.NotificationMessageSticker, obj);
                    } else if (messageObject.isGif()) {
                        if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                            return LocaleController.formatString("NotificationMessageGif", C0338R.string.NotificationMessageGif, obj);
                        }
                        return LocaleController.formatString("NotificationMessageText", C0338R.string.NotificationMessageText, obj, "\ud83c\udfac " + messageObject.messageOwner.media.caption);
                    } else if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.media.caption)) {
                        return LocaleController.formatString("NotificationMessageDocument", C0338R.string.NotificationMessageDocument, obj);
                    } else {
                        return LocaleController.formatString("NotificationMessageText", C0338R.string.NotificationMessageText, obj, "\ud83d\udcce " + messageObject.messageOwner.media.caption);
                    }
                }
                return null;
            }
        }
        j = j2;
        str = null;
        if (i3 <= 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(-i3));
            if (chat == null) {
            }
        } else {
            user = MessagesController.getInstance().getUser(Integer.valueOf(i3));
            if (user != null) {
                str = UserObject.getUserName(user);
            }
            obj = str;
        }
        if (obj == null) {
            return null;
        }
        if (i == 0) {
            chat = null;
        } else {
            chat2 = MessagesController.getInstance().getChat(Integer.valueOf(i));
            if (chat2 == null) {
                return null;
            }
            chat = chat2;
        }
        if (((int) j) != 0) {
        }
        if (HiddenConfig.m1399b(Long.valueOf(j))) {
        }
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        return messageObject.messageOwner.to_id != null && messageObject.messageOwner.to_id.chat_id == 0 && messageObject.messageOwner.to_id.channel_id == 0 && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty));
    }

    private void playInChatSound() {
        if (this.inChatSoundEnabled && !MediaController.m71a().m180h()) {
            try {
                if (this.audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            try {
                if (getNotifyOverride(ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0), this.opened_dialog_id) != 2) {
                    this.notificationsQueue.postRunnable(new Runnable() {

                        /* renamed from: com.hanista.mobogram.messenger.NotificationsController.13.1 */
                        class C06051 implements OnLoadCompleteListener {
                            C06051() {
                            }

                            public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                                if (i2 == 0) {
                                    soundPool.play(i, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 1, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                }
                            }
                        }

                        public void run() {
                            if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundPlay) > 500) {
                                try {
                                    if (NotificationsController.this.soundPool == null) {
                                        NotificationsController.this.soundPool = new SoundPool(3, 1, 0);
                                        NotificationsController.this.soundPool.setOnLoadCompleteListener(new C06051());
                                    }
                                    if (NotificationsController.this.soundIn == 0 && !NotificationsController.this.soundInLoaded) {
                                        NotificationsController.this.soundInLoaded = true;
                                        NotificationsController.this.soundIn = NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, C0338R.raw.sound_in, 1);
                                    }
                                    if (NotificationsController.this.soundIn != 0) {
                                        NotificationsController.this.soundPool.play(NotificationsController.this.soundIn, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 1, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                    }
                                } catch (Throwable e) {
                                    FileLog.m18e("tmessages", e);
                                }
                            }
                        }
                    });
                }
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
    }

    private void scheduleNotificationDelay(boolean z) {
        try {
            FileLog.m16e("tmessages", "delay notification start, onlineReason = " + z);
            this.notificationDelayWakelock.acquire(10000);
            AndroidUtilities.cancelRunOnUIThread(this.notificationDelayRunnable);
            AndroidUtilities.runOnUIThread(this.notificationDelayRunnable, (long) (z ? 3000 : PointerIconCompat.TYPE_DEFAULT));
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    private void scheduleNotificationRepeat() {
        try {
            PendingIntent service = PendingIntent.getService(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class), 0);
            int i = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getInt("repeat_messages", 60);
            if (i <= 0 || this.personal_count <= 0) {
                this.alarmManager.cancel(service);
            } else {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) ((i * 60) * PointerIconCompat.TYPE_DEFAULT)), service);
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    private void setBadge(int i) {
        this.notificationsQueue.postRunnable(new AnonymousClass11(i));
    }

    @SuppressLint({"InlinedApi"})
    private void showExtraNotifications(Builder builder, boolean z) {
        if (VERSION.SDK_INT >= 18) {
            int i;
            ArrayList arrayList = new ArrayList();
            HashMap hashMap = new HashMap();
            for (i = 0; i < this.pushMessages.size(); i++) {
                MessageObject messageObject = (MessageObject) this.pushMessages.get(i);
                long dialogId = messageObject.getDialogId();
                if (((int) dialogId) != 0) {
                    ArrayList arrayList2 = (ArrayList) hashMap.get(Long.valueOf(dialogId));
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                        hashMap.put(Long.valueOf(dialogId), arrayList2);
                        arrayList.add(0, Long.valueOf(dialogId));
                    }
                    arrayList2.add(messageObject);
                }
            }
            HashMap hashMap2 = new HashMap();
            hashMap2.putAll(this.wearNotificationsIds);
            this.wearNotificationsIds.clear();
            for (i = 0; i < arrayList.size(); i++) {
                long longValue = ((Long) arrayList.get(i)).longValue();
                ArrayList arrayList3 = (ArrayList) hashMap.get(Long.valueOf(longValue));
                int id = ((MessageObject) arrayList3.get(0)).getId();
                int i2 = ((MessageObject) arrayList3.get(0)).messageOwner.date;
                Chat chat;
                User user;
                CharSequence string;
                TLObject tLObject;
                Integer num;
                Integer valueOf;
                UnreadConversation.Builder latestTimestamp;
                Intent intent;
                Action action;
                MessagingStyle messagingStyle;
                Object[] objArr;
                Style conversationTitle;
                String str;
                int size;
                MessageObject messageObject2;
                String stringForMessage;
                CharSequence replace;
                PendingIntent activity;
                Extender wearableExtender;
                Builder category;
                BitmapDrawable imageFromMemory;
                float dp;
                Options options;
                Bitmap decodeFile;
                if (longValue > 0) {
                    User user2 = MessagesController.getInstance().getUser(Integer.valueOf((int) longValue));
                    if (user2 != null) {
                        chat = null;
                        user = user2;
                        if (!AndroidUtilities.needShowPasscode(false) || UserConfig.isWaitingForPasscodeEnter || HiddenConfig.m1399b(Long.valueOf(longValue))) {
                            string = LocaleController.getString("AppName", C0338R.string.AppName);
                            tLObject = null;
                        } else {
                            Object obj;
                            String userName = chat != null ? chat.title : UserObject.getUserName(user);
                            if (chat != null) {
                                if (!(chat.photo == null || chat.photo.photo_small == null || chat.photo.photo_small.volume_id == 0 || chat.photo.photo_small.local_id == 0)) {
                                    obj = userName;
                                    tLObject = chat.photo.photo_small;
                                }
                            } else if (!(user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0)) {
                                obj = userName;
                                tLObject = user.photo.photo_small;
                            }
                            obj = userName;
                            tLObject = null;
                        }
                        num = (Integer) hashMap2.get(Long.valueOf(longValue));
                        if (num != null) {
                            valueOf = Integer.valueOf((int) longValue);
                        } else {
                            hashMap2.remove(Long.valueOf(longValue));
                            valueOf = num;
                        }
                        latestTimestamp = new UnreadConversation.Builder(string).setLatestTimestamp(((long) i2) * 1000);
                        intent = new Intent();
                        intent.addFlags(32);
                        intent.setAction("com.hanista.mobogram.ACTION_MESSAGE_HEARD");
                        intent.putExtra("dialog_id", longValue);
                        intent.putExtra("max_id", id);
                        latestTimestamp.setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, valueOf.intValue(), intent, C0700C.SAMPLE_FLAG_DECODE_ONLY));
                        if (!ChatObject.isChannel(chat) || AndroidUtilities.needShowPasscode(false) || UserConfig.isWaitingForPasscodeEnter || HiddenConfig.m1399b(Long.valueOf(longValue))) {
                            action = null;
                        } else {
                            intent = new Intent();
                            intent.addFlags(32);
                            intent.setAction("com.hanista.mobogram.ACTION_MESSAGE_REPLY");
                            intent.putExtra("dialog_id", longValue);
                            intent.putExtra("max_id", id);
                            latestTimestamp.setReplyAction(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, valueOf.intValue(), intent, C0700C.SAMPLE_FLAG_DECODE_ONLY), new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", C0338R.string.Reply)).build());
                            intent = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                            intent.putExtra("dialog_id", longValue);
                            intent.putExtra("max_id", id);
                            action = new Action.Builder(C0338R.drawable.ic_reply_icon, chat != null ? LocaleController.formatString("ReplyToGroup", C0338R.string.ReplyToGroup, string) : LocaleController.formatString("ReplyToUser", C0338R.string.ReplyToUser, string), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, valueOf.intValue(), intent, C0700C.SAMPLE_FLAG_DECODE_ONLY)).addRemoteInput(new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", C0338R.string.Reply)).build()).build();
                        }
                        num = (Integer) this.pushDialogs.get(Long.valueOf(longValue));
                        if (num == null) {
                            num = Integer.valueOf(0);
                        }
                        messagingStyle = new MessagingStyle(null);
                        objArr = new Object[2];
                        objArr[0] = string;
                        objArr[1] = LocaleController.formatPluralString("NewMessages", Math.max(num.intValue(), arrayList3.size()));
                        conversationTitle = messagingStyle.setConversationTitle(String.format("%1$s (%2$s)", objArr));
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        for (size = arrayList3.size() - 1; size >= 0; size--) {
                            messageObject2 = (MessageObject) arrayList3.get(size);
                            stringForMessage = getStringForMessage(messageObject2, false);
                            if (stringForMessage != null) {
                                replace = chat == null ? stringForMessage.replace(" @ " + string, TtmlNode.ANONYMOUS_REGION_ID) : stringForMessage.replace(string + ": ", TtmlNode.ANONYMOUS_REGION_ID).replace(string + " ", TtmlNode.ANONYMOUS_REGION_ID);
                                if (str.length() > 0) {
                                    str = str + "\n\n";
                                }
                                str = str + replace;
                                latestTimestamp.addMessage(replace);
                                conversationTitle.addMessage(replace, ((long) messageObject2.messageOwner.date) * 1000, null);
                            }
                        }
                        intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                        intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                        intent.setFlags(TLRPC.MESSAGE_FLAG_EDITED);
                        if (chat != null) {
                            intent.putExtra("chatId", chat.id);
                        } else if (user != null) {
                            intent.putExtra("userId", user.id);
                        }
                        activity = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, C0700C.ENCODING_PCM_32BIT);
                        wearableExtender = new WearableExtender();
                        if (action != null) {
                            wearableExtender.addAction(action);
                        }
                        category = new Builder(ApplicationLoader.applicationContext).setContentTitle(string).setSmallIcon(C0338R.drawable.notification).setGroup("messages").setContentText(str).setAutoCancel(true).setNumber(arrayList3.size()).setColor(-13851168).setGroupSummary(false).setWhen(((long) ((MessageObject) arrayList3.get(0)).messageOwner.date) * 1000).setStyle(conversationTitle).setContentIntent(activity).extend(wearableExtender).extend(new CarExtender().setUnreadConversation(latestTimestamp.build())).setCategory(NotificationCompatApi24.CATEGORY_MESSAGE);
                        if (tLObject != null) {
                            imageFromMemory = ImageLoader.getInstance().getImageFromMemory(tLObject, null, "50_50");
                            if (imageFromMemory == null) {
                                category.setLargeIcon(imageFromMemory.getBitmap());
                            } else {
                                try {
                                    dp = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                                    options = new Options();
                                    options.inSampleSize = dp >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ? 1 : (int) dp;
                                    decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(tLObject, true).toString(), options);
                                    if (decodeFile != null) {
                                        category.setLargeIcon(decodeFile);
                                    }
                                } catch (Throwable th) {
                                }
                            }
                        }
                        if (chat == null && user != null && user.phone != null && user.phone.length() > 0) {
                            category.addPerson("tel:+" + user.phone);
                        }
                        this.notificationManager.notify(valueOf.intValue(), category.build());
                        this.wearNotificationsIds.put(Long.valueOf(longValue), valueOf);
                    }
                } else {
                    Chat chat2 = MessagesController.getInstance().getChat(Integer.valueOf(-((int) longValue)));
                    if (chat2 != null) {
                        chat = chat2;
                        user = null;
                        if (AndroidUtilities.needShowPasscode(false)) {
                        }
                        string = LocaleController.getString("AppName", C0338R.string.AppName);
                        tLObject = null;
                        num = (Integer) hashMap2.get(Long.valueOf(longValue));
                        if (num != null) {
                            hashMap2.remove(Long.valueOf(longValue));
                            valueOf = num;
                        } else {
                            valueOf = Integer.valueOf((int) longValue);
                        }
                        latestTimestamp = new UnreadConversation.Builder(string).setLatestTimestamp(((long) i2) * 1000);
                        intent = new Intent();
                        intent.addFlags(32);
                        intent.setAction("com.hanista.mobogram.ACTION_MESSAGE_HEARD");
                        intent.putExtra("dialog_id", longValue);
                        intent.putExtra("max_id", id);
                        latestTimestamp.setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, valueOf.intValue(), intent, C0700C.SAMPLE_FLAG_DECODE_ONLY));
                        if (ChatObject.isChannel(chat)) {
                        }
                        action = null;
                        num = (Integer) this.pushDialogs.get(Long.valueOf(longValue));
                        if (num == null) {
                            num = Integer.valueOf(0);
                        }
                        messagingStyle = new MessagingStyle(null);
                        objArr = new Object[2];
                        objArr[0] = string;
                        objArr[1] = LocaleController.formatPluralString("NewMessages", Math.max(num.intValue(), arrayList3.size()));
                        conversationTitle = messagingStyle.setConversationTitle(String.format("%1$s (%2$s)", objArr));
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                        for (size = arrayList3.size() - 1; size >= 0; size--) {
                            messageObject2 = (MessageObject) arrayList3.get(size);
                            stringForMessage = getStringForMessage(messageObject2, false);
                            if (stringForMessage != null) {
                                if (chat == null) {
                                }
                                if (str.length() > 0) {
                                    str = str + "\n\n";
                                }
                                str = str + replace;
                                latestTimestamp.addMessage(replace);
                                conversationTitle.addMessage(replace, ((long) messageObject2.messageOwner.date) * 1000, null);
                            }
                        }
                        intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                        intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                        intent.setFlags(TLRPC.MESSAGE_FLAG_EDITED);
                        if (chat != null) {
                            intent.putExtra("chatId", chat.id);
                        } else if (user != null) {
                            intent.putExtra("userId", user.id);
                        }
                        activity = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, C0700C.ENCODING_PCM_32BIT);
                        wearableExtender = new WearableExtender();
                        if (action != null) {
                            wearableExtender.addAction(action);
                        }
                        category = new Builder(ApplicationLoader.applicationContext).setContentTitle(string).setSmallIcon(C0338R.drawable.notification).setGroup("messages").setContentText(str).setAutoCancel(true).setNumber(arrayList3.size()).setColor(-13851168).setGroupSummary(false).setWhen(((long) ((MessageObject) arrayList3.get(0)).messageOwner.date) * 1000).setStyle(conversationTitle).setContentIntent(activity).extend(wearableExtender).extend(new CarExtender().setUnreadConversation(latestTimestamp.build())).setCategory(NotificationCompatApi24.CATEGORY_MESSAGE);
                        if (tLObject != null) {
                            imageFromMemory = ImageLoader.getInstance().getImageFromMemory(tLObject, null, "50_50");
                            if (imageFromMemory == null) {
                                dp = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                                options = new Options();
                                if (dp >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                                }
                                options.inSampleSize = dp >= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ? 1 : (int) dp;
                                decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(tLObject, true).toString(), options);
                                if (decodeFile != null) {
                                    category.setLargeIcon(decodeFile);
                                }
                            } else {
                                category.setLargeIcon(imageFromMemory.getBitmap());
                            }
                        }
                        category.addPerson("tel:+" + user.phone);
                        this.notificationManager.notify(valueOf.intValue(), category.build());
                        this.wearNotificationsIds.put(Long.valueOf(longValue), valueOf);
                    }
                }
            }
            for (Entry value : hashMap2.entrySet()) {
                this.notificationManager.cancel(((Integer) value.getValue()).intValue());
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r32) {
        /*
        r31 = this;
        r4 = com.hanista.mobogram.messenger.UserConfig.isClientActivated();
        if (r4 == 0) goto L_0x0010;
    L_0x0006:
        r0 = r31;
        r4 = r0.pushMessages;
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x0014;
    L_0x0010:
        r31.dismissNotification();
    L_0x0013:
        return;
    L_0x0014:
        r4 = com.hanista.mobogram.tgnet.ConnectionsManager.getInstance();	 Catch:{ Exception -> 0x0046 }
        r4.resumeNetworkMaybe();	 Catch:{ Exception -> 0x0046 }
        r0 = r31;
        r4 = r0.pushMessages;	 Catch:{ Exception -> 0x0046 }
        r5 = 0;
        r4 = r4.get(r5);	 Catch:{ Exception -> 0x0046 }
        r4 = (com.hanista.mobogram.messenger.MessageObject) r4;	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0046 }
        r6 = "Notifications";
        r7 = 0;
        r16 = r5.getSharedPreferences(r6, r7);	 Catch:{ Exception -> 0x0046 }
        r5 = "dismissDate";
        r6 = 0;
        r0 = r16;
        r23 = r0.getInt(r5, r6);	 Catch:{ Exception -> 0x0046 }
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.date;	 Catch:{ Exception -> 0x0046 }
        r0 = r23;
        if (r5 > r0) goto L_0x004e;
    L_0x0042:
        r31.dismissNotification();	 Catch:{ Exception -> 0x0046 }
        goto L_0x0013;
    L_0x0046:
        r4 = move-exception;
        r5 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r5, r4);
        goto L_0x0013;
    L_0x004e:
        r24 = r4.getDialogId();	 Catch:{ Exception -> 0x0046 }
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.mentioned;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x090b;
    L_0x0058:
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.from_id;	 Catch:{ Exception -> 0x0046 }
        r6 = (long) r5;	 Catch:{ Exception -> 0x0046 }
        r14 = r6;
    L_0x005e:
        r4.getId();	 Catch:{ Exception -> 0x0046 }
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.to_id;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.chat_id;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x0581;
    L_0x0069:
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.to_id;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.chat_id;	 Catch:{ Exception -> 0x0046 }
        r22 = r5;
    L_0x0071:
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.to_id;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.user_id;	 Catch:{ Exception -> 0x0046 }
        if (r5 != 0) goto L_0x058b;
    L_0x0079:
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.from_id;	 Catch:{ Exception -> 0x0046 }
        r21 = r5;
    L_0x007f:
        r5 = com.hanista.mobogram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x0046 }
        r6 = java.lang.Integer.valueOf(r21);	 Catch:{ Exception -> 0x0046 }
        r26 = r5.getUser(r6);	 Catch:{ Exception -> 0x0046 }
        r5 = 0;
        if (r22 == 0) goto L_0x0903;
    L_0x008e:
        r5 = com.hanista.mobogram.messenger.MessagesController.getInstance();	 Catch:{ Exception -> 0x0046 }
        r6 = java.lang.Integer.valueOf(r22);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.getChat(r6);	 Catch:{ Exception -> 0x0046 }
        r20 = r5;
    L_0x009c:
        r7 = 0;
        r12 = 0;
        r11 = 0;
        r9 = 0;
        r10 = -16711936; // 0xffffffffff00ff00 float:-1.7146522E38 double:NaN;
        r8 = 0;
        r6 = 0;
        r0 = r31;
        r1 = r16;
        r5 = r0.getNotifyOverride(r1, r14);	 Catch:{ Exception -> 0x0046 }
        if (r32 == 0) goto L_0x00d4;
    L_0x00af:
        r13 = 2;
        if (r5 == r13) goto L_0x00d4;
    L_0x00b2:
        r13 = "EnableAll";
        r17 = 1;
        r0 = r16;
        r1 = r17;
        r13 = r0.getBoolean(r13, r1);	 Catch:{ Exception -> 0x0046 }
        if (r13 == 0) goto L_0x00d2;
    L_0x00c1:
        if (r22 == 0) goto L_0x00d5;
    L_0x00c3:
        r13 = "EnableGroup";
        r17 = 1;
        r0 = r16;
        r1 = r17;
        r13 = r0.getBoolean(r13, r1);	 Catch:{ Exception -> 0x0046 }
        if (r13 != 0) goto L_0x00d5;
    L_0x00d2:
        if (r5 != 0) goto L_0x00d5;
    L_0x00d4:
        r12 = 1;
    L_0x00d5:
        if (r12 != 0) goto L_0x08ff;
    L_0x00d7:
        r5 = (r24 > r14 ? 1 : (r24 == r14 ? 0 : -1));
        if (r5 != 0) goto L_0x08ff;
    L_0x00db:
        if (r20 == 0) goto L_0x08ff;
    L_0x00dd:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r5.<init>();	 Catch:{ Exception -> 0x0046 }
        r13 = "smart_max_count_";
        r5 = r5.append(r13);	 Catch:{ Exception -> 0x0046 }
        r0 = r24;
        r5 = r5.append(r0);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0046 }
        r13 = 2;
        r0 = r16;
        r13 = r0.getInt(r5, r13);	 Catch:{ Exception -> 0x0046 }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r5.<init>();	 Catch:{ Exception -> 0x0046 }
        r14 = "smart_delay_";
        r5 = r5.append(r14);	 Catch:{ Exception -> 0x0046 }
        r0 = r24;
        r5 = r5.append(r0);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0046 }
        r14 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r0 = r16;
        r14 = r0.getInt(r5, r14);	 Catch:{ Exception -> 0x0046 }
        if (r13 == 0) goto L_0x08ff;
    L_0x011a:
        r0 = r31;
        r5 = r0.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0046 }
        r15 = java.lang.Long.valueOf(r24);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.get(r15);	 Catch:{ Exception -> 0x0046 }
        r5 = (android.graphics.Point) r5;	 Catch:{ Exception -> 0x0046 }
        if (r5 != 0) goto L_0x0599;
    L_0x012a:
        r5 = new android.graphics.Point;	 Catch:{ Exception -> 0x0046 }
        r13 = 1;
        r14 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0046 }
        r18 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r14 = r14 / r18;
        r14 = (int) r14;	 Catch:{ Exception -> 0x0046 }
        r5.<init>(r13, r14);	 Catch:{ Exception -> 0x0046 }
        r0 = r31;
        r13 = r0.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0046 }
        r14 = java.lang.Long.valueOf(r24);	 Catch:{ Exception -> 0x0046 }
        r13.put(r14, r5);	 Catch:{ Exception -> 0x0046 }
        r19 = r12;
    L_0x0146:
        r5 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0046 }
        r27 = r5.getPath();	 Catch:{ Exception -> 0x0046 }
        if (r19 != 0) goto L_0x08f5;
    L_0x014e:
        r5 = "EnableInAppSounds";
        r8 = 1;
        r0 = r16;
        r14 = r0.getBoolean(r5, r8);	 Catch:{ Exception -> 0x0046 }
        r5 = "EnableInAppVibrate";
        r8 = 1;
        r0 = r16;
        r15 = r0.getBoolean(r5, r8);	 Catch:{ Exception -> 0x0046 }
        r5 = "EnableInAppPreview";
        r8 = 1;
        r0 = r16;
        r9 = r0.getBoolean(r5, r8);	 Catch:{ Exception -> 0x0046 }
        r5 = "EnableInAppPriority";
        r8 = 0;
        r0 = r16;
        r17 = r0.getBoolean(r5, r8);	 Catch:{ Exception -> 0x0046 }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r5.<init>();	 Catch:{ Exception -> 0x0046 }
        r8 = "vibrate_";
        r5 = r5.append(r8);	 Catch:{ Exception -> 0x0046 }
        r0 = r24;
        r5 = r5.append(r0);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0046 }
        r8 = 0;
        r0 = r16;
        r5 = r0.getInt(r5, r8);	 Catch:{ Exception -> 0x0046 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r8.<init>();	 Catch:{ Exception -> 0x0046 }
        r12 = "priority_";
        r8 = r8.append(r12);	 Catch:{ Exception -> 0x0046 }
        r0 = r24;
        r8 = r8.append(r0);	 Catch:{ Exception -> 0x0046 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x0046 }
        r12 = 3;
        r0 = r16;
        r8 = r0.getInt(r8, r12);	 Catch:{ Exception -> 0x0046 }
        r12 = 0;
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r13.<init>();	 Catch:{ Exception -> 0x0046 }
        r18 = "sound_path_";
        r0 = r18;
        r13 = r13.append(r0);	 Catch:{ Exception -> 0x0046 }
        r0 = r24;
        r13 = r13.append(r0);	 Catch:{ Exception -> 0x0046 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x0046 }
        r18 = 0;
        r0 = r16;
        r1 = r18;
        r13 = r0.getString(r13, r1);	 Catch:{ Exception -> 0x0046 }
        if (r22 == 0) goto L_0x05e4;
    L_0x01d5:
        if (r13 == 0) goto L_0x05d5;
    L_0x01d7:
        r0 = r27;
        r6 = r13.equals(r0);	 Catch:{ Exception -> 0x0046 }
        if (r6 == 0) goto L_0x05d5;
    L_0x01df:
        r11 = 0;
    L_0x01e0:
        r6 = "vibrate_group";
        r10 = 0;
        r0 = r16;
        r6 = r0.getInt(r6, r10);	 Catch:{ Exception -> 0x0046 }
        r10 = "priority_group";
        r13 = 1;
        r0 = r16;
        r13 = r0.getInt(r10, r13);	 Catch:{ Exception -> 0x0046 }
        r10 = "GroupLed";
        r18 = -16711936; // 0xffffffffff00ff00 float:-1.7146522E38 double:NaN;
        r0 = r16;
        r1 = r18;
        r10 = r0.getInt(r10, r1);	 Catch:{ Exception -> 0x0046 }
    L_0x0202:
        r18 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r18.<init>();	 Catch:{ Exception -> 0x0046 }
        r28 = "color_";
        r0 = r18;
        r1 = r28;
        r18 = r0.append(r1);	 Catch:{ Exception -> 0x0046 }
        r0 = r18;
        r1 = r24;
        r18 = r0.append(r1);	 Catch:{ Exception -> 0x0046 }
        r18 = r18.toString();	 Catch:{ Exception -> 0x0046 }
        r0 = r16;
        r1 = r18;
        r18 = r0.contains(r1);	 Catch:{ Exception -> 0x0046 }
        if (r18 == 0) goto L_0x024a;
    L_0x0228:
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r10.<init>();	 Catch:{ Exception -> 0x0046 }
        r18 = "color_";
        r0 = r18;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x0046 }
        r0 = r24;
        r10 = r10.append(r0);	 Catch:{ Exception -> 0x0046 }
        r10 = r10.toString();	 Catch:{ Exception -> 0x0046 }
        r18 = 0;
        r0 = r16;
        r1 = r18;
        r10 = r0.getInt(r10, r1);	 Catch:{ Exception -> 0x0046 }
    L_0x024a:
        r16 = 3;
        r0 = r16;
        if (r8 == r0) goto L_0x08e4;
    L_0x0250:
        r13 = 4;
        if (r6 != r13) goto L_0x025a;
    L_0x0253:
        r6 = 1;
        r12 = 0;
        r30 = r6;
        r6 = r12;
        r12 = r30;
    L_0x025a:
        r13 = 2;
        if (r6 != r13) goto L_0x0266;
    L_0x025d:
        r13 = 1;
        if (r5 == r13) goto L_0x026e;
    L_0x0260:
        r13 = 3;
        if (r5 == r13) goto L_0x026e;
    L_0x0263:
        r13 = 5;
        if (r5 == r13) goto L_0x026e;
    L_0x0266:
        r13 = 2;
        if (r6 == r13) goto L_0x026c;
    L_0x0269:
        r13 = 2;
        if (r5 == r13) goto L_0x026e;
    L_0x026c:
        if (r5 == 0) goto L_0x08e1;
    L_0x026e:
        r6 = com.hanista.mobogram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0046 }
        if (r6 != 0) goto L_0x027b;
    L_0x0272:
        if (r14 != 0) goto L_0x0275;
    L_0x0274:
        r11 = 0;
    L_0x0275:
        if (r15 != 0) goto L_0x0278;
    L_0x0277:
        r5 = 2;
    L_0x0278:
        if (r17 != 0) goto L_0x0623;
    L_0x027a:
        r8 = 0;
    L_0x027b:
        if (r12 == 0) goto L_0x0630;
    L_0x027d:
        r6 = 2;
        if (r5 == r6) goto L_0x0630;
    L_0x0280:
        r0 = r31;
        r6 = r0.audioManager;	 Catch:{ Exception -> 0x0629 }
        r6 = r6.getRingerMode();	 Catch:{ Exception -> 0x0629 }
        if (r6 == 0) goto L_0x028e;
    L_0x028a:
        r12 = 1;
        if (r6 == r12) goto L_0x028e;
    L_0x028d:
        r5 = 2;
    L_0x028e:
        r14 = r8;
        r15 = r9;
        r16 = r10;
        r17 = r11;
        r18 = r5;
    L_0x0296:
        r6 = new android.content.Intent;	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0046 }
        r8 = com.hanista.mobogram.ui.LaunchActivity.class;
        r6.<init>(r5, r8);	 Catch:{ Exception -> 0x0046 }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r5.<init>();	 Catch:{ Exception -> 0x0046 }
        r8 = "com.tmessages.openchat";
        r5 = r5.append(r8);	 Catch:{ Exception -> 0x0046 }
        r8 = java.lang.Math.random();	 Catch:{ Exception -> 0x0046 }
        r5 = r5.append(r8);	 Catch:{ Exception -> 0x0046 }
        r8 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r5 = r5.append(r8);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0046 }
        r6.setAction(r5);	 Catch:{ Exception -> 0x0046 }
        r5 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r6.setFlags(r5);	 Catch:{ Exception -> 0x0046 }
        r0 = r24;
        r5 = (int) r0;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x06b3;
    L_0x02cc:
        r0 = r31;
        r5 = r0.pushDialogs;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0046 }
        r8 = 1;
        if (r5 != r8) goto L_0x02eb;
    L_0x02d7:
        r5 = java.lang.Long.valueOf(r24);	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.mobo.p012l.HiddenConfig.m1399b(r5);	 Catch:{ Exception -> 0x0046 }
        if (r5 != 0) goto L_0x02eb;
    L_0x02e1:
        if (r22 == 0) goto L_0x063a;
    L_0x02e3:
        r5 = "chatId";
        r0 = r22;
        r6.putExtra(r5, r0);	 Catch:{ Exception -> 0x0046 }
    L_0x02eb:
        r5 = 0;
        r5 = com.hanista.mobogram.messenger.AndroidUtilities.needShowPasscode(r5);	 Catch:{ Exception -> 0x0046 }
        if (r5 != 0) goto L_0x0300;
    L_0x02f2:
        r5 = com.hanista.mobogram.messenger.UserConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0046 }
        if (r5 != 0) goto L_0x0300;
    L_0x02f6:
        r5 = java.lang.Long.valueOf(r24);	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.mobo.p012l.HiddenConfig.m1399b(r5);	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x0646;
    L_0x0300:
        r5 = 0;
        r13 = r5;
    L_0x0302:
        r5 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0046 }
        r7 = 0;
        r8 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r7 = android.app.PendingIntent.getActivity(r5, r7, r6, r8);	 Catch:{ Exception -> 0x0046 }
        r5 = 1;
        r0 = r24;
        r6 = (int) r0;	 Catch:{ Exception -> 0x0046 }
        if (r6 == 0) goto L_0x0331;
    L_0x0311:
        r0 = r31;
        r6 = r0.pushDialogs;	 Catch:{ Exception -> 0x0046 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0046 }
        r8 = 1;
        if (r6 > r8) goto L_0x0331;
    L_0x031c:
        r6 = 0;
        r6 = com.hanista.mobogram.messenger.AndroidUtilities.needShowPasscode(r6);	 Catch:{ Exception -> 0x0046 }
        if (r6 != 0) goto L_0x0331;
    L_0x0323:
        r6 = com.hanista.mobogram.messenger.UserConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0046 }
        if (r6 != 0) goto L_0x0331;
    L_0x0327:
        r6 = java.lang.Long.valueOf(r24);	 Catch:{ Exception -> 0x0046 }
        r6 = com.hanista.mobogram.mobo.p012l.HiddenConfig.m1399b(r6);	 Catch:{ Exception -> 0x0046 }
        if (r6 == 0) goto L_0x06cc;
    L_0x0331:
        r5 = "AppName";
        r6 = 2131166486; // 0x7f070516 float:1.7947219E38 double:1.0529361463E-314;
        r6 = com.hanista.mobogram.messenger.LocaleController.getString(r5, r6);	 Catch:{ Exception -> 0x0046 }
        r5 = 0;
        r11 = r5;
        r12 = r6;
    L_0x033e:
        r0 = r31;
        r5 = r0.pushDialogs;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0046 }
        r6 = 1;
        if (r5 != r6) goto L_0x06de;
    L_0x0349:
        r5 = "NewMessages";
        r0 = r31;
        r6 = r0.total_unread_count;	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.messenger.LocaleController.formatPluralString(r5, r6);	 Catch:{ Exception -> 0x0046 }
        r10 = r5;
    L_0x0355:
        r5 = new android.support.v4.app.NotificationCompat$Builder;	 Catch:{ Exception -> 0x0046 }
        r6 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0046 }
        r5.<init>(r6);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.setContentTitle(r12);	 Catch:{ Exception -> 0x0046 }
        r6 = 2130838273; // 0x7f020301 float:1.7281524E38 double:1.0527739875E-314;
        r5 = r5.setSmallIcon(r6);	 Catch:{ Exception -> 0x0046 }
        r6 = 1;
        r5 = r5.setAutoCancel(r6);	 Catch:{ Exception -> 0x0046 }
        r0 = r31;
        r6 = r0.total_unread_count;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.setNumber(r6);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.setContentIntent(r7);	 Catch:{ Exception -> 0x0046 }
        r6 = "messages";
        r5 = r5.setGroup(r6);	 Catch:{ Exception -> 0x0046 }
        r6 = 1;
        r5 = r5.setGroupSummary(r6);	 Catch:{ Exception -> 0x0046 }
        r6 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2485a();	 Catch:{ Exception -> 0x0046 }
        r6 = r6.m2289c();	 Catch:{ Exception -> 0x0046 }
        r21 = r5.setColor(r6);	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.mobo.p020s.ThemeUtil.m2490b();	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x039d;
    L_0x0396:
        r5 = com.hanista.mobogram.mobo.p020s.AdvanceTheme.f2491b;	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.setColor(r5);	 Catch:{ Exception -> 0x0046 }
    L_0x039d:
        r5 = "msg";
        r0 = r21;
        r0.setCategory(r5);	 Catch:{ Exception -> 0x0046 }
        if (r20 != 0) goto L_0x03d6;
    L_0x03a7:
        if (r26 == 0) goto L_0x03d6;
    L_0x03a9:
        r0 = r26;
        r5 = r0.phone;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x03d6;
    L_0x03af:
        r0 = r26;
        r5 = r0.phone;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.length();	 Catch:{ Exception -> 0x0046 }
        if (r5 <= 0) goto L_0x03d6;
    L_0x03b9:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r5.<init>();	 Catch:{ Exception -> 0x0046 }
        r6 = "tel:+";
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0046 }
        r0 = r26;
        r6 = r0.phone;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.append(r6);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.addPerson(r5);	 Catch:{ Exception -> 0x0046 }
    L_0x03d6:
        r7 = 2;
        r6 = 0;
        r0 = r31;
        r5 = r0.pushMessages;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0046 }
        r8 = 1;
        if (r5 != r8) goto L_0x0751;
    L_0x03e3:
        r0 = r31;
        r5 = r0.pushMessages;	 Catch:{ Exception -> 0x0046 }
        r6 = 0;
        r5 = r5.get(r6);	 Catch:{ Exception -> 0x0046 }
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;	 Catch:{ Exception -> 0x0046 }
        r6 = 0;
        r0 = r31;
        r6 = r0.getStringForMessage(r5, r6);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.silent;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x0716;
    L_0x03fb:
        r7 = 1;
    L_0x03fc:
        if (r6 == 0) goto L_0x0013;
    L_0x03fe:
        if (r11 == 0) goto L_0x08de;
    L_0x0400:
        if (r20 == 0) goto L_0x0719;
    L_0x0402:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r5.<init>();	 Catch:{ Exception -> 0x0046 }
        r8 = " @ ";
        r5 = r5.append(r8);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.append(r12);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0046 }
        r8 = "";
        r5 = r6.replace(r5, r8);	 Catch:{ Exception -> 0x0046 }
    L_0x041d:
        r0 = r21;
        r0.setContentText(r5);	 Catch:{ Exception -> 0x0046 }
        r8 = new android.support.v4.app.NotificationCompat$BigTextStyle;	 Catch:{ Exception -> 0x0046 }
        r8.<init>();	 Catch:{ Exception -> 0x0046 }
        r5 = r8.bigText(r5);	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.setStyle(r5);	 Catch:{ Exception -> 0x0046 }
        r5 = r6;
    L_0x0431:
        r6 = new android.content.Intent;	 Catch:{ Exception -> 0x0046 }
        r8 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0046 }
        r9 = com.hanista.mobogram.messenger.NotificationDismissReceiver.class;
        r6.<init>(r8, r9);	 Catch:{ Exception -> 0x0046 }
        r8 = "messageDate";
        r4 = r4.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r4 = r4.date;	 Catch:{ Exception -> 0x0046 }
        r6.putExtra(r8, r4);	 Catch:{ Exception -> 0x0046 }
        r4 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0046 }
        r8 = 1;
        r9 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r4 = android.app.PendingIntent.getBroadcast(r4, r8, r6, r9);	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.setDeleteIntent(r4);	 Catch:{ Exception -> 0x0046 }
        if (r13 == 0) goto L_0x046b;
    L_0x0454:
        r4 = com.hanista.mobogram.messenger.ImageLoader.getInstance();	 Catch:{ Exception -> 0x0046 }
        r6 = 0;
        r8 = "50_50";
        r4 = r4.getImageFromMemory(r13, r6, r8);	 Catch:{ Exception -> 0x0046 }
        if (r4 == 0) goto L_0x0831;
    L_0x0462:
        r4 = r4.getBitmap();	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.setLargeIcon(r4);	 Catch:{ Exception -> 0x0046 }
    L_0x046b:
        if (r32 == 0) goto L_0x0470;
    L_0x046d:
        r4 = 1;
        if (r7 != r4) goto L_0x0864;
    L_0x0470:
        r4 = -1;
        r0 = r21;
        r0.setPriority(r4);	 Catch:{ Exception -> 0x0046 }
    L_0x0476:
        r4 = 1;
        if (r7 == r4) goto L_0x08c3;
    L_0x0479:
        if (r19 != 0) goto L_0x08c3;
    L_0x047b:
        r4 = com.hanista.mobogram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0046 }
        if (r4 != 0) goto L_0x0481;
    L_0x047f:
        if (r15 == 0) goto L_0x04b5;
    L_0x0481:
        r4 = r5.length();	 Catch:{ Exception -> 0x0046 }
        r6 = 100;
        if (r4 <= r6) goto L_0x08d0;
    L_0x0489:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r4.<init>();	 Catch:{ Exception -> 0x0046 }
        r6 = 0;
        r7 = 100;
        r5 = r5.substring(r6, r7);	 Catch:{ Exception -> 0x0046 }
        r6 = 10;
        r7 = 32;
        r5 = r5.replace(r6, r7);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.trim();	 Catch:{ Exception -> 0x0046 }
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x0046 }
        r5 = "...";
        r4 = r4.append(r5);	 Catch:{ Exception -> 0x0046 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0046 }
    L_0x04b0:
        r0 = r21;
        r0.setTicker(r4);	 Catch:{ Exception -> 0x0046 }
    L_0x04b5:
        r4 = com.hanista.mobogram.messenger.MediaController.m71a();	 Catch:{ Exception -> 0x0046 }
        r4 = r4.m180h();	 Catch:{ Exception -> 0x0046 }
        if (r4 != 0) goto L_0x04de;
    L_0x04bf:
        if (r17 == 0) goto L_0x04de;
    L_0x04c1:
        r4 = "NoSound";
        r0 = r17;
        r4 = r0.equals(r4);	 Catch:{ Exception -> 0x0046 }
        if (r4 != 0) goto L_0x04de;
    L_0x04cc:
        r0 = r17;
        r1 = r27;
        r4 = r0.equals(r1);	 Catch:{ Exception -> 0x0046 }
        if (r4 == 0) goto L_0x0884;
    L_0x04d6:
        r4 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0046 }
        r5 = 5;
        r0 = r21;
        r0.setSound(r4, r5);	 Catch:{ Exception -> 0x0046 }
    L_0x04de:
        if (r16 == 0) goto L_0x04eb;
    L_0x04e0:
        r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r21;
        r1 = r16;
        r0.setLights(r1, r4, r5);	 Catch:{ Exception -> 0x0046 }
    L_0x04eb:
        r4 = 2;
        r0 = r18;
        if (r0 == r4) goto L_0x04fa;
    L_0x04f0:
        r4 = com.hanista.mobogram.messenger.MediaController.m71a();	 Catch:{ Exception -> 0x0046 }
        r4 = r4.m180h();	 Catch:{ Exception -> 0x0046 }
        if (r4 == 0) goto L_0x0890;
    L_0x04fa:
        r4 = 2;
        r4 = new long[r4];	 Catch:{ Exception -> 0x0046 }
        r4 = {0, 0};	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.setVibrate(r4);	 Catch:{ Exception -> 0x0046 }
    L_0x0505:
        r0 = r31;
        r1 = r21;
        r2 = r32;
        r0.showExtraNotifications(r1, r2);	 Catch:{ Exception -> 0x0046 }
        r4 = com.hanista.mobogram.mobo.MoboConstants.aM;	 Catch:{ Exception -> 0x0046 }
        if (r4 == 0) goto L_0x0536;
    L_0x0512:
        r4 = new android.content.Intent;	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0046 }
        r6 = com.hanista.mobogram.mobo.notif.MarkAsReadService.class;
        r4.<init>(r5, r6);	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0046 }
        r6 = 0;
        r7 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r4 = android.app.PendingIntent.getService(r5, r6, r4, r7);	 Catch:{ Exception -> 0x0046 }
        r5 = 2130837943; // 0x7f0201b7 float:1.7280854E38 double:1.0527738245E-314;
        r6 = "MarkAsRead";
        r7 = 2131166724; // 0x7f070604 float:1.7947701E38 double:1.052936264E-314;
        r6 = com.hanista.mobogram.messenger.LocaleController.getString(r6, r7);	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.addAction(r5, r6, r4);	 Catch:{ Exception -> 0x0046 }
    L_0x0536:
        r4 = com.hanista.mobogram.mobo.MoboConstants.aL;	 Catch:{ Exception -> 0x0046 }
        if (r4 == 0) goto L_0x0570;
    L_0x053a:
        r0 = r31;
        r4 = r0.pushMessages;	 Catch:{ Exception -> 0x0046 }
        r0 = r31;
        r4 = r0.replyButtonNeed(r4);	 Catch:{ Exception -> 0x0046 }
        if (r4 == 0) goto L_0x0570;
    L_0x0546:
        r4 = new android.content.Intent;	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0046 }
        r6 = com.hanista.mobogram.ui.PopupNotificationActivity.class;
        r4.<init>(r5, r6);	 Catch:{ Exception -> 0x0046 }
        r5 = 268763140; // 0x10050004 float:2.6229637E-29 double:1.327866343E-315;
        r4.setFlags(r5);	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0046 }
        r6 = 0;
        r7 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r4 = android.app.PendingIntent.getActivity(r5, r6, r4, r7);	 Catch:{ Exception -> 0x0046 }
        r5 = 2130837944; // 0x7f0201b8 float:1.7280856E38 double:1.052773825E-314;
        r6 = "Reply";
        r7 = 2131166158; // 0x7f0703ce float:1.7946553E38 double:1.0529359842E-314;
        r6 = com.hanista.mobogram.messenger.LocaleController.getString(r6, r7);	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.addAction(r5, r6, r4);	 Catch:{ Exception -> 0x0046 }
    L_0x0570:
        r0 = r31;
        r4 = r0.notificationManager;	 Catch:{ Exception -> 0x0046 }
        r5 = 1;
        r6 = r21.build();	 Catch:{ Exception -> 0x0046 }
        r4.notify(r5, r6);	 Catch:{ Exception -> 0x0046 }
        r31.scheduleNotificationRepeat();	 Catch:{ Exception -> 0x0046 }
        goto L_0x0013;
    L_0x0581:
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.to_id;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.channel_id;	 Catch:{ Exception -> 0x0046 }
        r22 = r5;
        goto L_0x0071;
    L_0x058b:
        r6 = com.hanista.mobogram.messenger.UserConfig.getClientUserId();	 Catch:{ Exception -> 0x0046 }
        if (r5 != r6) goto L_0x0907;
    L_0x0591:
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.from_id;	 Catch:{ Exception -> 0x0046 }
        r21 = r5;
        goto L_0x007f;
    L_0x0599:
        r15 = r5.y;	 Catch:{ Exception -> 0x0046 }
        r14 = r14 + r15;
        r14 = (long) r14;	 Catch:{ Exception -> 0x0046 }
        r18 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0046 }
        r28 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r18 = r18 / r28;
        r14 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1));
        if (r14 >= 0) goto L_0x05ba;
    L_0x05a9:
        r13 = 1;
        r14 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0046 }
        r18 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r14 = r14 / r18;
        r14 = (int) r14;	 Catch:{ Exception -> 0x0046 }
        r5.set(r13, r14);	 Catch:{ Exception -> 0x0046 }
        r19 = r12;
        goto L_0x0146;
    L_0x05ba:
        r14 = r5.x;	 Catch:{ Exception -> 0x0046 }
        if (r14 >= r13) goto L_0x05d0;
    L_0x05be:
        r13 = r14 + 1;
        r14 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0046 }
        r18 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r14 = r14 / r18;
        r14 = (int) r14;	 Catch:{ Exception -> 0x0046 }
        r5.set(r13, r14);	 Catch:{ Exception -> 0x0046 }
        r19 = r12;
        goto L_0x0146;
    L_0x05d0:
        r5 = 1;
        r19 = r5;
        goto L_0x0146;
    L_0x05d5:
        if (r13 != 0) goto L_0x08f2;
    L_0x05d7:
        r6 = "GroupSoundPath";
        r0 = r16;
        r1 = r27;
        r11 = r0.getString(r6, r1);	 Catch:{ Exception -> 0x0046 }
        goto L_0x01e0;
    L_0x05e4:
        if (r21 == 0) goto L_0x08ea;
    L_0x05e6:
        if (r13 == 0) goto L_0x0615;
    L_0x05e8:
        r0 = r27;
        r6 = r13.equals(r0);	 Catch:{ Exception -> 0x0046 }
        if (r6 == 0) goto L_0x0615;
    L_0x05f0:
        r11 = 0;
    L_0x05f1:
        r6 = "vibrate_messages";
        r10 = 0;
        r0 = r16;
        r6 = r0.getInt(r6, r10);	 Catch:{ Exception -> 0x0046 }
        r10 = "priority_group";
        r13 = 1;
        r0 = r16;
        r13 = r0.getInt(r10, r13);	 Catch:{ Exception -> 0x0046 }
        r10 = "MessagesLed";
        r18 = -16711936; // 0xffffffffff00ff00 float:-1.7146522E38 double:NaN;
        r0 = r16;
        r1 = r18;
        r10 = r0.getInt(r10, r1);	 Catch:{ Exception -> 0x0046 }
        goto L_0x0202;
    L_0x0615:
        if (r13 != 0) goto L_0x08e7;
    L_0x0617:
        r6 = "GlobalSoundPath";
        r0 = r16;
        r1 = r27;
        r11 = r0.getString(r6, r1);	 Catch:{ Exception -> 0x0046 }
        goto L_0x05f1;
    L_0x0623:
        r6 = 2;
        if (r8 != r6) goto L_0x027b;
    L_0x0626:
        r8 = 1;
        goto L_0x027b;
    L_0x0629:
        r6 = move-exception;
        r12 = "tmessages";
        com.hanista.mobogram.messenger.FileLog.m18e(r12, r6);	 Catch:{ Exception -> 0x0046 }
    L_0x0630:
        r14 = r8;
        r15 = r9;
        r16 = r10;
        r17 = r11;
        r18 = r5;
        goto L_0x0296;
    L_0x063a:
        if (r21 == 0) goto L_0x02eb;
    L_0x063c:
        r5 = "userId";
        r0 = r21;
        r6.putExtra(r5, r0);	 Catch:{ Exception -> 0x0046 }
        goto L_0x02eb;
    L_0x0646:
        r0 = r31;
        r5 = r0.pushDialogs;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0046 }
        r8 = 1;
        if (r5 != r8) goto L_0x06c9;
    L_0x0651:
        if (r20 == 0) goto L_0x0682;
    L_0x0653:
        r0 = r20;
        r5 = r0.photo;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x06c9;
    L_0x0659:
        r0 = r20;
        r5 = r0.photo;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.photo_small;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x06c9;
    L_0x0661:
        r0 = r20;
        r5 = r0.photo;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.photo_small;	 Catch:{ Exception -> 0x0046 }
        r8 = r5.volume_id;	 Catch:{ Exception -> 0x0046 }
        r10 = 0;
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r5 == 0) goto L_0x06c9;
    L_0x066f:
        r0 = r20;
        r5 = r0.photo;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.photo_small;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.local_id;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x06c9;
    L_0x0679:
        r0 = r20;
        r5 = r0.photo;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.photo_small;	 Catch:{ Exception -> 0x0046 }
        r13 = r5;
        goto L_0x0302;
    L_0x0682:
        if (r26 == 0) goto L_0x06c9;
    L_0x0684:
        r0 = r26;
        r5 = r0.photo;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x06c9;
    L_0x068a:
        r0 = r26;
        r5 = r0.photo;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.photo_small;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x06c9;
    L_0x0692:
        r0 = r26;
        r5 = r0.photo;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.photo_small;	 Catch:{ Exception -> 0x0046 }
        r8 = r5.volume_id;	 Catch:{ Exception -> 0x0046 }
        r10 = 0;
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r5 == 0) goto L_0x06c9;
    L_0x06a0:
        r0 = r26;
        r5 = r0.photo;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.photo_small;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.local_id;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x06c9;
    L_0x06aa:
        r0 = r26;
        r5 = r0.photo;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.photo_small;	 Catch:{ Exception -> 0x0046 }
        r13 = r5;
        goto L_0x0302;
    L_0x06b3:
        r0 = r31;
        r5 = r0.pushDialogs;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0046 }
        r8 = 1;
        if (r5 != r8) goto L_0x06c9;
    L_0x06be:
        r5 = "encId";
        r8 = 32;
        r8 = r24 >> r8;
        r8 = (int) r8;	 Catch:{ Exception -> 0x0046 }
        r6.putExtra(r5, r8);	 Catch:{ Exception -> 0x0046 }
    L_0x06c9:
        r13 = r7;
        goto L_0x0302;
    L_0x06cc:
        if (r20 == 0) goto L_0x06d6;
    L_0x06ce:
        r0 = r20;
        r6 = r0.title;	 Catch:{ Exception -> 0x0046 }
        r11 = r5;
        r12 = r6;
        goto L_0x033e;
    L_0x06d6:
        r6 = com.hanista.mobogram.messenger.UserObject.getUserName(r26);	 Catch:{ Exception -> 0x0046 }
        r11 = r5;
        r12 = r6;
        goto L_0x033e;
    L_0x06de:
        r5 = "NotificationMessagesPeopleDisplayOrder";
        r6 = 2131166030; // 0x7f07034e float:1.7946294E38 double:1.052935921E-314;
        r8 = 2;
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0046 }
        r9 = 0;
        r10 = "NewMessages";
        r0 = r31;
        r0 = r0.total_unread_count;	 Catch:{ Exception -> 0x0046 }
        r21 = r0;
        r0 = r21;
        r10 = com.hanista.mobogram.messenger.LocaleController.formatPluralString(r10, r0);	 Catch:{ Exception -> 0x0046 }
        r8[r9] = r10;	 Catch:{ Exception -> 0x0046 }
        r9 = 1;
        r10 = "FromChats";
        r0 = r31;
        r0 = r0.pushDialogs;	 Catch:{ Exception -> 0x0046 }
        r21 = r0;
        r21 = r21.size();	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r10 = com.hanista.mobogram.messenger.LocaleController.formatPluralString(r10, r0);	 Catch:{ Exception -> 0x0046 }
        r8[r9] = r10;	 Catch:{ Exception -> 0x0046 }
        r5 = com.hanista.mobogram.messenger.LocaleController.formatString(r5, r6, r8);	 Catch:{ Exception -> 0x0046 }
        r10 = r5;
        goto L_0x0355;
    L_0x0716:
        r7 = 0;
        goto L_0x03fc;
    L_0x0719:
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r5.<init>();	 Catch:{ Exception -> 0x0046 }
        r5 = r5.append(r12);	 Catch:{ Exception -> 0x0046 }
        r8 = ": ";
        r5 = r5.append(r8);	 Catch:{ Exception -> 0x0046 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x0046 }
        r8 = "";
        r5 = r6.replace(r5, r8);	 Catch:{ Exception -> 0x0046 }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r8.<init>();	 Catch:{ Exception -> 0x0046 }
        r8 = r8.append(r12);	 Catch:{ Exception -> 0x0046 }
        r9 = " ";
        r8 = r8.append(r9);	 Catch:{ Exception -> 0x0046 }
        r8 = r8.toString();	 Catch:{ Exception -> 0x0046 }
        r9 = "";
        r5 = r5.replace(r8, r9);	 Catch:{ Exception -> 0x0046 }
        goto L_0x041d;
    L_0x0751:
        r0 = r21;
        r0.setContentText(r10);	 Catch:{ Exception -> 0x0046 }
        r22 = new android.support.v4.app.NotificationCompat$InboxStyle;	 Catch:{ Exception -> 0x0046 }
        r22.<init>();	 Catch:{ Exception -> 0x0046 }
        r0 = r22;
        r0.setBigContentTitle(r12);	 Catch:{ Exception -> 0x0046 }
        r5 = 10;
        r0 = r31;
        r8 = r0.pushMessages;	 Catch:{ Exception -> 0x0046 }
        r8 = r8.size();	 Catch:{ Exception -> 0x0046 }
        r24 = java.lang.Math.min(r5, r8);	 Catch:{ Exception -> 0x0046 }
        r5 = 0;
        r8 = r5;
    L_0x0770:
        r0 = r24;
        if (r8 >= r0) goto L_0x0826;
    L_0x0774:
        r0 = r31;
        r5 = r0.pushMessages;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.get(r8);	 Catch:{ Exception -> 0x0046 }
        r5 = (com.hanista.mobogram.messenger.MessageObject) r5;	 Catch:{ Exception -> 0x0046 }
        r9 = 0;
        r0 = r31;
        r9 = r0.getStringForMessage(r5, r9);	 Catch:{ Exception -> 0x0046 }
        if (r9 == 0) goto L_0x08d7;
    L_0x0787:
        r0 = r5.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r25 = r0;
        r0 = r25;
        r0 = r0.date;	 Catch:{ Exception -> 0x0046 }
        r25 = r0;
        r0 = r25;
        r1 = r23;
        if (r0 > r1) goto L_0x079f;
    L_0x0797:
        r5 = r6;
        r6 = r7;
    L_0x0799:
        r7 = r8 + 1;
        r8 = r7;
        r7 = r6;
        r6 = r5;
        goto L_0x0770;
    L_0x079f:
        r25 = 2;
        r0 = r25;
        if (r7 != r0) goto L_0x08d3;
    L_0x07a5:
        r5 = r5.messageOwner;	 Catch:{ Exception -> 0x0046 }
        r5 = r5.silent;	 Catch:{ Exception -> 0x0046 }
        if (r5 == 0) goto L_0x07e5;
    L_0x07ab:
        r5 = 1;
    L_0x07ac:
        r6 = r5;
        r5 = r9;
    L_0x07ae:
        r0 = r31;
        r7 = r0.pushDialogs;	 Catch:{ Exception -> 0x0046 }
        r7 = r7.size();	 Catch:{ Exception -> 0x0046 }
        r25 = 1;
        r0 = r25;
        if (r7 != r0) goto L_0x08db;
    L_0x07bc:
        if (r11 == 0) goto L_0x08db;
    L_0x07be:
        if (r20 == 0) goto L_0x07e7;
    L_0x07c0:
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r7.<init>();	 Catch:{ Exception -> 0x0046 }
        r25 = " @ ";
        r0 = r25;
        r7 = r7.append(r0);	 Catch:{ Exception -> 0x0046 }
        r7 = r7.append(r12);	 Catch:{ Exception -> 0x0046 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0046 }
        r25 = "";
        r0 = r25;
        r7 = r9.replace(r7, r0);	 Catch:{ Exception -> 0x0046 }
    L_0x07df:
        r0 = r22;
        r0.addLine(r7);	 Catch:{ Exception -> 0x0046 }
        goto L_0x0799;
    L_0x07e5:
        r5 = 0;
        goto L_0x07ac;
    L_0x07e7:
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r7.<init>();	 Catch:{ Exception -> 0x0046 }
        r7 = r7.append(r12);	 Catch:{ Exception -> 0x0046 }
        r25 = ": ";
        r0 = r25;
        r7 = r7.append(r0);	 Catch:{ Exception -> 0x0046 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x0046 }
        r25 = "";
        r0 = r25;
        r7 = r9.replace(r7, r0);	 Catch:{ Exception -> 0x0046 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0046 }
        r9.<init>();	 Catch:{ Exception -> 0x0046 }
        r9 = r9.append(r12);	 Catch:{ Exception -> 0x0046 }
        r25 = " ";
        r0 = r25;
        r9 = r9.append(r0);	 Catch:{ Exception -> 0x0046 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0046 }
        r25 = "";
        r0 = r25;
        r7 = r7.replace(r9, r0);	 Catch:{ Exception -> 0x0046 }
        goto L_0x07df;
    L_0x0826:
        r0 = r22;
        r0.setSummaryText(r10);	 Catch:{ Exception -> 0x0046 }
        r21.setStyle(r22);	 Catch:{ Exception -> 0x0046 }
        r5 = r6;
        goto L_0x0431;
    L_0x0831:
        r4 = 1126170624; // 0x43200000 float:160.0 double:5.564022167E-315;
        r6 = 1112014848; // 0x42480000 float:50.0 double:5.49408334E-315;
        r6 = com.hanista.mobogram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Throwable -> 0x085f }
        r6 = (float) r6;	 Catch:{ Throwable -> 0x085f }
        r4 = r4 / r6;
        r6 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x085f }
        r6.<init>();	 Catch:{ Throwable -> 0x085f }
        r8 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r8 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r8 >= 0) goto L_0x0862;
    L_0x0846:
        r4 = 1;
    L_0x0847:
        r6.inSampleSize = r4;	 Catch:{ Throwable -> 0x085f }
        r4 = 1;
        r4 = com.hanista.mobogram.messenger.FileLoader.getPathToAttach(r13, r4);	 Catch:{ Throwable -> 0x085f }
        r4 = r4.toString();	 Catch:{ Throwable -> 0x085f }
        r4 = android.graphics.BitmapFactory.decodeFile(r4, r6);	 Catch:{ Throwable -> 0x085f }
        if (r4 == 0) goto L_0x046b;
    L_0x0858:
        r0 = r21;
        r0.setLargeIcon(r4);	 Catch:{ Throwable -> 0x085f }
        goto L_0x046b;
    L_0x085f:
        r4 = move-exception;
        goto L_0x046b;
    L_0x0862:
        r4 = (int) r4;
        goto L_0x0847;
    L_0x0864:
        if (r14 != 0) goto L_0x086e;
    L_0x0866:
        r4 = 0;
        r0 = r21;
        r0.setPriority(r4);	 Catch:{ Exception -> 0x0046 }
        goto L_0x0476;
    L_0x086e:
        r4 = 1;
        if (r14 != r4) goto L_0x0879;
    L_0x0871:
        r4 = 1;
        r0 = r21;
        r0.setPriority(r4);	 Catch:{ Exception -> 0x0046 }
        goto L_0x0476;
    L_0x0879:
        r4 = 2;
        if (r14 != r4) goto L_0x0476;
    L_0x087c:
        r4 = 2;
        r0 = r21;
        r0.setPriority(r4);	 Catch:{ Exception -> 0x0046 }
        goto L_0x0476;
    L_0x0884:
        r4 = android.net.Uri.parse(r17);	 Catch:{ Exception -> 0x0046 }
        r5 = 5;
        r0 = r21;
        r0.setSound(r4, r5);	 Catch:{ Exception -> 0x0046 }
        goto L_0x04de;
    L_0x0890:
        r4 = 1;
        r0 = r18;
        if (r0 != r4) goto L_0x08a2;
    L_0x0895:
        r4 = 4;
        r4 = new long[r4];	 Catch:{ Exception -> 0x0046 }
        r4 = {0, 100, 0, 100};	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.setVibrate(r4);	 Catch:{ Exception -> 0x0046 }
        goto L_0x0505;
    L_0x08a2:
        if (r18 == 0) goto L_0x08a9;
    L_0x08a4:
        r4 = 4;
        r0 = r18;
        if (r0 != r4) goto L_0x08b1;
    L_0x08a9:
        r4 = 2;
        r0 = r21;
        r0.setDefaults(r4);	 Catch:{ Exception -> 0x0046 }
        goto L_0x0505;
    L_0x08b1:
        r4 = 3;
        r0 = r18;
        if (r0 != r4) goto L_0x0505;
    L_0x08b6:
        r4 = 2;
        r4 = new long[r4];	 Catch:{ Exception -> 0x0046 }
        r4 = {0, 1000};	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.setVibrate(r4);	 Catch:{ Exception -> 0x0046 }
        goto L_0x0505;
    L_0x08c3:
        r4 = 2;
        r4 = new long[r4];	 Catch:{ Exception -> 0x0046 }
        r4 = {0, 0};	 Catch:{ Exception -> 0x0046 }
        r0 = r21;
        r0.setVibrate(r4);	 Catch:{ Exception -> 0x0046 }
        goto L_0x0505;
    L_0x08d0:
        r4 = r5;
        goto L_0x04b0;
    L_0x08d3:
        r5 = r6;
        r6 = r7;
        goto L_0x07ae;
    L_0x08d7:
        r5 = r6;
        r6 = r7;
        goto L_0x0799;
    L_0x08db:
        r7 = r9;
        goto L_0x07df;
    L_0x08de:
        r5 = r6;
        goto L_0x041d;
    L_0x08e1:
        r5 = r6;
        goto L_0x026e;
    L_0x08e4:
        r8 = r13;
        goto L_0x0250;
    L_0x08e7:
        r11 = r13;
        goto L_0x05f1;
    L_0x08ea:
        r30 = r6;
        r6 = r11;
        r11 = r13;
        r13 = r30;
        goto L_0x0202;
    L_0x08f2:
        r11 = r13;
        goto L_0x01e0;
    L_0x08f5:
        r14 = r6;
        r15 = r8;
        r16 = r10;
        r17 = r9;
        r18 = r11;
        goto L_0x0296;
    L_0x08ff:
        r19 = r12;
        goto L_0x0146;
    L_0x0903:
        r20 = r5;
        goto L_0x009c;
    L_0x0907:
        r21 = r5;
        goto L_0x007f;
    L_0x090b:
        r14 = r24;
        goto L_0x005e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    public static void updateServerNotificationsSettings(long j) {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        if (((int) j) != 0) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            TLObject tL_account_updateNotifySettings = new TL_account_updateNotifySettings();
            tL_account_updateNotifySettings.settings = new TL_inputPeerNotifySettings();
            tL_account_updateNotifySettings.settings.sound = "default";
            int i = sharedPreferences.getInt("notify2_" + j, 0);
            if (i == 3) {
                tL_account_updateNotifySettings.settings.mute_until = sharedPreferences.getInt("notifyuntil_" + j, 0);
            } else {
                tL_account_updateNotifySettings.settings.mute_until = i != 2 ? 0 : ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            tL_account_updateNotifySettings.settings.show_previews = sharedPreferences.getBoolean("preview_" + j, true);
            tL_account_updateNotifySettings.settings.silent = sharedPreferences.getBoolean("silent_" + j, false);
            tL_account_updateNotifySettings.peer = new TL_inputNotifyPeer();
            ((TL_inputNotifyPeer) tL_account_updateNotifySettings.peer).peer = MessagesController.getInputPeer((int) j);
            ConnectionsManager.getInstance().sendRequest(tL_account_updateNotifySettings, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                }
            });
        }
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        this.notificationsQueue.postRunnable(new C06082());
    }

    protected void forceShowPopupForReply() {
        this.notificationsQueue.postRunnable(new C06125());
    }

    public ArrayList<MessageObject> getPushMessages() {
        return this.pushMessages;
    }

    public boolean hasMessagesToReply() {
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = (MessageObject) this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            if ((!messageObject.messageOwner.mentioned || !(messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) && ((int) dialogId) != 0 && (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
                return true;
            }
        }
        return false;
    }

    public void playOutChatSound() {
        if (this.inChatSoundEnabled && !MediaController.m71a().m180h()) {
            try {
                if (this.audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            this.notificationsQueue.postRunnable(new Runnable() {

                /* renamed from: com.hanista.mobogram.messenger.NotificationsController.15.1 */
                class C06061 implements OnLoadCompleteListener {
                    C06061() {
                    }

                    public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                        if (i2 == 0) {
                            soundPool.play(i, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 1, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        }
                    }
                }

                public void run() {
                    try {
                        if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundOutPlay) > 100) {
                            NotificationsController.this.lastSoundOutPlay = System.currentTimeMillis();
                            if (NotificationsController.this.soundPool == null) {
                                NotificationsController.this.soundPool = new SoundPool(3, 1, 0);
                                NotificationsController.this.soundPool.setOnLoadCompleteListener(new C06061());
                            }
                            if (NotificationsController.this.soundOut == 0 && !NotificationsController.this.soundOutLoaded) {
                                NotificationsController.this.soundOutLoaded = true;
                                NotificationsController.this.soundOut = NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, C0338R.raw.sound_out, 1);
                            }
                            if (NotificationsController.this.soundOut != 0) {
                                NotificationsController.this.soundPool.play(NotificationsController.this.soundOut, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 1, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
            });
        }
    }

    public void processDialogsUpdateRead(HashMap<Long, Integer> hashMap) {
        this.notificationsQueue.postRunnable(new C06209(hashMap, this.popupMessages.isEmpty() ? null : new ArrayList(this.popupMessages)));
    }

    public void processLoadedUnreadMessages(HashMap<Long, Integer> hashMap, ArrayList<Message> arrayList, ArrayList<User> arrayList2, ArrayList<Chat> arrayList3, ArrayList<EncryptedChat> arrayList4) {
        MessagesController.getInstance().putUsers(arrayList2, true);
        MessagesController.getInstance().putChats(arrayList3, true);
        MessagesController.getInstance().putEncryptedChats(arrayList4, true);
        this.notificationsQueue.postRunnable(new AnonymousClass10(arrayList, hashMap));
    }

    public void processNewMessages(ArrayList<MessageObject> arrayList, boolean z) {
        if (!arrayList.isEmpty()) {
            this.notificationsQueue.postRunnable(new C06188(new ArrayList(this.popupMessages), arrayList, z));
        }
    }

    public void processReadMessages(SparseArray<Long> sparseArray, long j, int i, int i2, boolean z) {
        this.notificationsQueue.postRunnable(new C06167(this.popupMessages.isEmpty() ? null : new ArrayList(this.popupMessages), sparseArray, j, i2, i, z));
    }

    public void removeDeletedMessagesFromNotifications(SparseArray<ArrayList<Integer>> sparseArray) {
        this.notificationsQueue.postRunnable(new C06146(sparseArray, this.popupMessages.isEmpty() ? null : new ArrayList(this.popupMessages)));
    }

    public void removeNotificationsForDialog(long j) {
        getInstance().processReadMessages(null, j, 0, ConnectionsManager.DEFAULT_DATACENTER_ID, false);
        HashMap hashMap = new HashMap();
        hashMap.put(Long.valueOf(j), Integer.valueOf(0));
        getInstance().processDialogsUpdateRead(hashMap);
    }

    protected void repeatNotificationMaybe() {
        this.notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                int i = Calendar.getInstance().get(11);
                if (i < 11 || i > 22) {
                    NotificationsController.this.scheduleNotificationRepeat();
                    return;
                }
                NotificationsController.this.notificationManager.cancel(1);
                NotificationsController.this.showOrUpdateNotification(true);
            }
        });
    }

    public boolean replyButtonNeed(ArrayList<MessageObject> arrayList) {
        if (arrayList.isEmpty()) {
            return false;
        }
        ArrayList arrayList2 = new ArrayList();
        HashMap hashMap = new HashMap();
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        for (int i = 0; i < arrayList.size(); i++) {
            boolean z;
            MessageObject messageObject = (MessageObject) arrayList.get(i);
            long dialogId = messageObject.getDialogId();
            if (messageObject.messageOwner.mentioned) {
                dialogId = (long) messageObject.messageOwner.from_id;
            }
            Boolean bool = (Boolean) hashMap.get(Long.valueOf(dialogId));
            boolean z2 = ((int) dialogId) < 0;
            boolean z3 = ((int) dialogId) != 0;
            if (bool == null) {
                int notifyOverride = getNotifyOverride(sharedPreferences, dialogId);
                z = notifyOverride != 2 && ((sharedPreferences.getBoolean("EnableAll", true) && (!z2 || sharedPreferences.getBoolean("EnableGroup", true))) || notifyOverride != 0);
                bool = Boolean.valueOf(z);
                hashMap.put(Long.valueOf(dialogId), bool);
            }
            Boolean bool2 = bool;
            z = (!z3 || messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup()) ? z3 : false;
            if (bool2.booleanValue() && z && !HiddenConfig.m1399b(Long.valueOf(dialogId))) {
                arrayList2.add(0, messageObject);
            }
        }
        if (arrayList2.isEmpty() || AndroidUtilities.needShowPasscode(false) || UserConfig.isWaitingForPasscodeEnter) {
            return false;
        }
        this.popupMessages = arrayList2;
        return true;
    }

    public void setBadgeEnabled(boolean z) {
        setBadge(z ? this.total_unread_count : 0);
    }

    public void setInChatSoundEnabled(boolean z) {
        this.inChatSoundEnabled = z;
    }

    public void setLastOnlineFromOtherDevice(int i) {
        this.notificationsQueue.postRunnable(new C06104(i));
    }

    public void setOpenedDialogId(long j) {
        this.notificationsQueue.postRunnable(new C06093(j));
    }
}

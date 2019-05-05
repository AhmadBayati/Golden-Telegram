package com.hanista.mobogram.mobo.download;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.app.NotificationManagerCompat;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.FileDownloadProgressListener;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.ui.Cells.ChatMessageCell;
import com.hanista.mobogram.ui.LaunchActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class DownloadManagerService extends Service implements FileDownloadProgressListener, NotificationCenterDelegate {
    public static boolean f640b;
    private static PendingIntent f641h;
    protected ArrayList<MessageObject> f642a;
    private boolean f643c;
    private boolean f644d;
    private int f645e;
    private boolean f646f;
    private int f647g;

    /* renamed from: com.hanista.mobogram.mobo.download.DownloadManagerService.1 */
    class C09071 implements Comparator<MessageObject> {
        final /* synthetic */ DownloadManagerService f639a;

        C09071(DownloadManagerService downloadManagerService) {
            this.f639a = downloadManagerService;
        }

        public int m621a(MessageObject messageObject, MessageObject messageObject2) {
            return (messageObject == null || messageObject.messageOwner == null) ? -1 : (messageObject2 == null || messageObject2.messageOwner == null) ? 1 : messageObject.messageOwner.date != messageObject2.messageOwner.date ? messageObject.messageOwner.date < messageObject2.messageOwner.date ? 1 : messageObject.messageOwner.date > messageObject2.messageOwner.date ? -1 : 0 : 0;
        }

        public /* synthetic */ int compare(Object obj, Object obj2) {
            return m621a((MessageObject) obj, (MessageObject) obj2);
        }
    }

    public DownloadManagerService() {
        this.f642a = new ArrayList();
    }

    private Builder m622a(String str) {
        int i = 0;
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setAction("com.tmessages.opendownloadmanager" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
        intent.setFlags(TLRPC.MESSAGE_FLAG_EDITED);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, C0700C.SAMPLE_FLAG_DECODE_ONLY);
        CharSequence string = LocaleController.getString("MobogramDownloadService", C0338R.string.MobogramDownloadService);
        Builder builder = new Builder(this);
        builder.setSmallIcon(C0338R.drawable.ic_download_notification);
        builder.setContentTitle(string);
        builder.setContentText(str);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(activity);
        builder.setOngoing(true);
        builder.setPriority(2);
        Style inboxStyle = new InboxStyle();
        inboxStyle.setBigContentTitle(string);
        String[] split = str.split("-");
        int length = split.length;
        while (i < length) {
            inboxStyle.addLine(split[i]);
            i++;
        }
        inboxStyle.setSummaryText(LocaleController.formatPluralString("DownloadedCount", this.f647g));
        builder.setStyle(inboxStyle);
        return builder;
    }

    private void m623a() {
        try {
            if (MoboConstants.f1318K && this.f643c) {
                ((WifiManager) getSystemService("wifi")).setWifiEnabled(true);
            }
        } catch (Exception e) {
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.downloadServiceStarted, new Object[0]);
        m630c();
        this.f645e = ConnectionsManager.getInstance().generateClassGuid();
        this.f646f = true;
        DownloadMessagesStorage.m783a().m810a(1, 300, 0, 0, this.f645e, 3, 0, 0);
    }

    private void m624a(int i) {
        if (this.f646f) {
            NotificationManagerCompat.from(this).notify(11259186, m622a(LocaleController.formatString("DownloadFileProgress", C0338R.string.DownloadFileProgress, Integer.valueOf(m633e() - 1), "%" + i)).build());
        }
    }

    private static void m625a(Context context) {
        ((AlarmManager) context.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).setRepeating(3, SystemClock.elapsedRealtime(), 0, m628b(context));
    }

    private void m626a(ArrayList<MessageObject> arrayList) {
        Collections.sort(arrayList, new C09071(this));
    }

    private boolean m627a(MessageObject messageObject) {
        if (messageObject.type == 9) {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, false, false);
        } else if (messageObject.type == 3) {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, true, false);
        } else if (messageObject.type == 14) {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, true, false);
        } else if (messageObject.type == 8) {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, true, false);
        } else if (messageObject.isVoice()) {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, true, false);
        } else if (!(messageObject.messageOwner.media instanceof TL_messageMediaPhoto)) {
            return false;
        } else {
            ChatMessageCell chatMessageCell = new ChatMessageCell(this);
            chatMessageCell.setMessageObject(messageObject);
            chatMessageCell.photoImage.setImage(chatMessageCell.currentPhotoObject.location, chatMessageCell.currentPhotoFilter, chatMessageCell.currentPhotoObjectThumb != null ? chatMessageCell.currentPhotoObjectThumb.location : null, chatMessageCell.currentPhotoFilter, chatMessageCell.currentPhotoObject.size, null, false);
        }
        return true;
    }

    private static PendingIntent m628b(Context context) {
        if (f641h == null) {
            Intent intent = new Intent(context, DownloadManagerService.class);
            intent.setAction("com.hanista.mobogram.download.start");
            f641h = PendingIntent.getService(context, 1193135, intent, 0);
        }
        return f641h;
    }

    private void m629b() {
        NotificationManagerCompat.from(this).cancel(11259186);
    }

    private void m630c() {
        startForeground(11259186, m622a(LocaleController.getString("DownloadingFiles", C0338R.string.DownloadingFiles)).build());
    }

    private static void m631c(Context context) {
        ((AlarmManager) context.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).cancel(m628b(context));
    }

    private void m632d() {
        try {
            if (MoboConstants.f1319L && this.f643c) {
                ((WifiManager) getSystemService("wifi")).setWifiEnabled(false);
            }
        } catch (Exception e) {
        }
        this.f646f = false;
        Iterator it = this.f642a.iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            if (messageObject.messageOwner.media != null) {
                FileLoader.getInstance().cancelLoadFile(messageObject.messageOwner.media.document);
            }
        }
        m629b();
        m631c(this);
        this.f644d = true;
        stopForeground(true);
        stopSelf();
        f640b = false;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.downloadServiceStoped, new Object[0]);
    }

    private int m633e() {
        Iterator it = this.f642a.iterator();
        int i = 0;
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            File pathToMessage = FileLoader.getPathToMessage(messageObject.messageOwner);
            int i2 = (pathToMessage == null || pathToMessage.exists() || messageObject.messageOwner.media == null) ? i : i + 1;
            i = i2;
        }
        return i;
    }

    private void m634f() {
        Iterator it = this.f642a.iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            File pathToMessage = FileLoader.getPathToMessage(messageObject.messageOwner);
            if (pathToMessage != null && !pathToMessage.exists() && m627a(messageObject)) {
                String attachFileName;
                String attachFileName2 = FileLoader.getAttachFileName(messageObject.messageOwner.media.document);
                if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                    ChatMessageCell chatMessageCell = new ChatMessageCell(this);
                    chatMessageCell.setMessageObject(messageObject);
                    attachFileName = FileLoader.getAttachFileName(chatMessageCell.currentPhotoObject);
                } else {
                    attachFileName = attachFileName2;
                }
                MediaController.m71a().m151a(attachFileName, (FileDownloadProgressListener) this);
                return;
            }
        }
        m632d();
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.messagesDidLoaded) {
            if (((Integer) objArr[10]).intValue() == this.f645e) {
                ArrayList arrayList = (ArrayList) objArr[2];
                m626a(arrayList);
                this.f642a.clear();
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    this.f642a.add((MessageObject) arrayList.get(i2));
                }
            }
            if (this.f646f) {
                m634f();
            }
        }
    }

    public int getObserverTag() {
        return 0;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.messagesDidLoaded);
    }

    public void onDestroy() {
        super.onDestroy();
        f640b = false;
        stopForeground(true);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDidLoaded);
        if (!this.f644d) {
            m625a((Context) this);
        }
        this.f644d = false;
    }

    public void onFailedDownload(String str) {
        m624a(0);
        if (this.f646f) {
            m634f();
        }
    }

    public void onProgressDownload(String str, float f) {
        m624a((int) (100.0f * f));
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null && intent.getBooleanExtra("com.hanista.mobogram.download.use.wifi", false)) {
            this.f643c = true;
        }
        if (intent == null || "com.hanista.mobogram.download.start".equals(intent.getAction())) {
            f640b = true;
            m623a();
        } else if ("com.hanista.mobogram.download.stop".equals(intent.getAction())) {
            f640b = false;
            m632d();
        }
        return 1;
    }

    public void onSuccessDownload(String str) {
        this.f647g++;
        m624a(0);
        if (this.f646f) {
            m634f();
        }
    }
}

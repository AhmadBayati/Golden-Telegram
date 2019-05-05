package com.hanista.mobogram.mobo.dialogdm;

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
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ChatObject;
import com.hanista.mobogram.messenger.FileLoader;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.MediaController;
import com.hanista.mobogram.messenger.MediaController.FileDownloadProgressListener;
import com.hanista.mobogram.messenger.MessageObject;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.MessagesStorage;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.UserObject;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import com.hanista.mobogram.tgnet.TLRPC.TL_messageMediaPhoto;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.Cells.ChatMessageCell;
import com.hanista.mobogram.ui.LaunchActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

public class DialogDmService extends Service implements FileDownloadProgressListener, NotificationCenterDelegate {
    private static PendingIntent f528G;
    public static boolean f529b;
    private boolean f530A;
    private boolean f531B;
    private int f532C;
    private List<DialogDm> f533D;
    private DialogDm f534E;
    private int f535F;
    protected ArrayList<MessageObject> f536a;
    protected Chat f537c;
    protected User f538d;
    private boolean f539e;
    private boolean f540f;
    private int f541g;
    private boolean f542h;
    private int f543i;
    private int f544j;
    private int f545k;
    private ArrayList<Integer> f546l;
    private int f547m;
    private long f548n;
    private int f549o;
    private boolean[] f550p;
    private boolean[] f551q;
    private boolean[] f552r;
    private int[] f553s;
    private int[] f554t;
    private boolean f555u;
    private HashMap<Integer, MessageObject>[] f556v;
    private HashMap<String, ArrayList<MessageObject>> f557w;
    private int[] f558x;
    private int[] f559y;
    private boolean f560z;

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.DialogDmService.1 */
    class C09041 implements Runnable {
        final /* synthetic */ Semaphore f523a;
        final /* synthetic */ DialogDmService f524b;

        C09041(DialogDmService dialogDmService, Semaphore semaphore) {
            this.f524b = dialogDmService;
            this.f523a = semaphore;
        }

        public void run() {
            this.f524b.f537c = MessagesStorage.getInstance().getChat(this.f524b.f543i);
            this.f523a.release();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.DialogDmService.2 */
    class C09052 implements Runnable {
        final /* synthetic */ Semaphore f525a;
        final /* synthetic */ DialogDmService f526b;

        C09052(DialogDmService dialogDmService, Semaphore semaphore) {
            this.f526b = dialogDmService;
            this.f525a = semaphore;
        }

        public void run() {
            this.f526b.f538d = MessagesStorage.getInstance().getUser(this.f526b.f544j);
            this.f525a.release();
        }
    }

    /* renamed from: com.hanista.mobogram.mobo.dialogdm.DialogDmService.3 */
    class C09063 implements Runnable {
        final /* synthetic */ DialogDmService f527a;

        C09063(DialogDmService dialogDmService) {
            this.f527a = dialogDmService;
        }

        public void run() {
            if (this.f527a.f549o == 0) {
            }
        }
    }

    public DialogDmService() {
        this.f536a = new ArrayList();
        this.f543i = 0;
        this.f544j = 0;
        this.f546l = new ArrayList();
        this.f549o = 0;
        this.f550p = new boolean[]{true, true};
        this.f551q = new boolean[2];
        this.f552r = new boolean[2];
        this.f553s = new int[]{ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID};
        this.f554t = new int[]{TLRPC.MESSAGE_FLAG_MEGAGROUP, TLRPC.MESSAGE_FLAG_MEGAGROUP};
        this.f555u = true;
        this.f556v = new HashMap[]{new HashMap(), new HashMap()};
        this.f557w = new HashMap();
        this.f558x = new int[]{TLRPC.MESSAGE_FLAG_MEGAGROUP, TLRPC.MESSAGE_FLAG_MEGAGROUP};
        this.f559y = new int[2];
        this.f530A = true;
    }

    private Builder m541a(String str) {
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setAction("com.tmessages.opendialogdm" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
        intent.setFlags(TLRPC.MESSAGE_FLAG_EDITED);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, C0700C.SAMPLE_FLAG_DECODE_ONLY);
        r0 = this.f537c != null ? LocaleController.getString("Downloading", C0338R.string.Downloading) + " " + this.f537c.title : this.f538d != null ? LocaleController.getString("Downloading", C0338R.string.Downloading) + " " + UserObject.getUserName(this.f538d) : LocaleController.getString("ChatsDownloadService", C0338R.string.ChatsDownloadService);
        Builder builder = new Builder(this);
        builder.setSmallIcon(C0338R.drawable.ic_dialogdm_notification);
        builder.setContentTitle(r0);
        builder.setContentText(str);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(activity);
        builder.setOngoing(true);
        builder.setPriority(2);
        Style inboxStyle = new InboxStyle();
        inboxStyle.setBigContentTitle(r0);
        for (CharSequence addLine : str.split("-")) {
            inboxStyle.addLine(addLine);
        }
        inboxStyle.setSummaryText(LocaleController.formatPluralString("DownloadedCount", this.f535F));
        builder.setStyle(inboxStyle);
        return builder;
    }

    private void m542a() {
        try {
            if (MoboConstants.at && this.f539e) {
                ((WifiManager) getSystemService("wifi")).setWifiEnabled(true);
            }
        } catch (Exception e) {
        }
        this.f533D = new DataBaseAccess().m912s();
        if (this.f533D.size() == 0) {
            m554e();
            return;
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogDmServiceStarted, new Object[0]);
        m553d();
        this.f541g = ConnectionsManager.getInstance().generateClassGuid();
        this.f542h = true;
        this.f532C = -1;
        m549b();
    }

    private void m543a(int i) {
        if (this.f542h) {
            NotificationManagerCompat.from(this).notify(11259188, m541a(LocaleController.formatString("DialogDmProgress", C0338R.string.DialogDmProgress, Integer.valueOf((this.f533D.size() - this.f532C) - 1), "%" + i)).build());
        }
    }

    private static void m544a(Context context) {
        ((AlarmManager) context.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).setRepeating(3, SystemClock.elapsedRealtime(), 0, m548b(context));
    }

    private void m545a(boolean z) {
        int size = this.f536a.size();
        if (size > 0) {
            MessagesController instance;
            long j;
            int i;
            int i2;
            int i3;
            boolean isChannel;
            int i4;
            int size2 = this.f536a.size();
            if (!(-1 > (z ? 25 : 5) || this.f560z || this.f551q[0])) {
                this.f560z = true;
                this.f546l.add(Integer.valueOf(this.f545k));
                boolean z2;
                if (this.f557w.size() != 0) {
                    instance = MessagesController.getInstance();
                    j = this.f548n;
                    i = this.f553s[0];
                    z2 = !this.f552r[0];
                    i2 = this.f559y[0];
                    i3 = this.f541g;
                    isChannel = ChatObject.isChannel(this.f537c);
                    i4 = this.f545k;
                    this.f545k = i4 + 1;
                    instance.loadMessages(j, 50, i, z2, i2, i3, 0, 0, isChannel, i4);
                } else {
                    instance = MessagesController.getInstance();
                    j = this.f548n;
                    z2 = !this.f552r[0];
                    i2 = this.f559y[0];
                    i3 = this.f541g;
                    isChannel = ChatObject.isChannel(this.f537c);
                    i4 = this.f545k;
                    this.f545k = i4 + 1;
                    instance.loadMessages(j, 50, 0, z2, i2, i3, 0, 0, isChannel, i4);
                }
            }
            if (!this.f531B && -1 + size >= size2 - 10 && !this.f550p[0]) {
                this.f546l.add(Integer.valueOf(this.f545k));
                instance = MessagesController.getInstance();
                j = this.f548n;
                i = this.f554t[0];
                i2 = this.f558x[0];
                i3 = this.f541g;
                isChannel = ChatObject.isChannel(this.f537c);
                i4 = this.f545k;
                this.f545k = i4 + 1;
                instance.loadMessages(j, 50, i, true, i2, i3, 1, 0, isChannel, i4);
                this.f531B = true;
            }
        }
    }

    private boolean m546a(MessageObject messageObject) {
        if (messageObject.messageOwner.media == null || messageObject.messageOwner.media.document == null) {
            if (messageObject.messageOwner.media == null || !(messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || (this.f534E.m563c() & 1) == 0) {
                return false;
            }
            ChatMessageCell chatMessageCell = new ChatMessageCell(this);
            chatMessageCell.setMessageObject(messageObject);
            chatMessageCell.photoImage.setImage(chatMessageCell.currentPhotoObject.location, chatMessageCell.currentPhotoFilter, chatMessageCell.currentPhotoObjectThumb != null ? chatMessageCell.currentPhotoObjectThumb.location : null, chatMessageCell.currentPhotoFilter, chatMessageCell.currentPhotoObject.size, null, false);
            return true;
        } else if ((!messageObject.isMusic() || (this.f534E.m563c() & 16) == 0) && (((!messageObject.isNewGif() && !messageObject.isGif()) || (this.f534E.m563c() & 32) == 0) && ((!messageObject.isVoice() || (this.f534E.m563c() & 2) == 0) && ((!messageObject.isVideo() || (this.f534E.m563c() & 4) == 0) && (messageObject.isVideo() || messageObject.isVoice() || messageObject.isNewGif() || messageObject.isGif() || messageObject.isMusic() || (this.f534E.m563c() & 8) == 0))))) {
            return false;
        } else {
            FileLoader.getInstance().loadFile(messageObject.messageOwner.media.document, false, false);
            return true;
        }
    }

    private static PendingIntent m548b(Context context) {
        if (f528G == null) {
            Intent intent = new Intent(context, DialogDmService.class);
            intent.setAction("com.hanista.mobogram.dialogdm.start");
            f528G = PendingIntent.getService(context, 1201327, intent, 0);
        }
        return f528G;
    }

    private void m549b() {
        this.f532C++;
        if (this.f532C >= this.f533D.size()) {
            m554e();
            return;
        }
        this.f534E = (DialogDm) this.f533D.get(this.f532C);
        this.f548n = this.f534E.m561b();
        this.f536a = new ArrayList();
        this.f537c = null;
        this.f545k = 0;
        this.f546l = new ArrayList();
        this.f547m = 0;
        this.f549o = 0;
        this.f550p = new boolean[]{true, true};
        this.f551q = new boolean[2];
        this.f552r = new boolean[2];
        this.f553s = new int[]{ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID};
        this.f554t = new int[]{TLRPC.MESSAGE_FLAG_MEGAGROUP, TLRPC.MESSAGE_FLAG_MEGAGROUP};
        this.f555u = true;
        this.f556v = new HashMap[]{new HashMap(), new HashMap()};
        this.f557w = new HashMap();
        this.f558x = new int[]{TLRPC.MESSAGE_FLAG_MEGAGROUP, TLRPC.MESSAGE_FLAG_MEGAGROUP};
        this.f559y = new int[2];
        this.f560z = false;
        this.f530A = true;
        this.f531B = false;
        int i = (int) this.f548n;
        int i2 = (int) (this.f548n >> 32);
        if (i != 0) {
            if (i2 == 1) {
                this.f543i = i;
            } else if (i > 0) {
                this.f544j = i;
            } else if (i < 0) {
                this.f543i = -i;
            }
        }
        Semaphore semaphore;
        if (this.f543i != 0) {
            this.f537c = MessagesController.getInstance().getChat(Integer.valueOf(this.f543i));
            if (this.f537c == null) {
                semaphore = new Semaphore(0);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09041(this, semaphore));
                try {
                    semaphore.acquire();
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
                if (this.f537c != null) {
                    MessagesController.getInstance().putChat(this.f537c, true);
                }
            }
        } else if (this.f544j != 0) {
            this.f538d = MessagesController.getInstance().getUser(Integer.valueOf(this.f544j));
            if (this.f538d == null) {
                semaphore = new Semaphore(0);
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new C09052(this, semaphore));
                try {
                    semaphore.acquire();
                } catch (Throwable e2) {
                    FileLog.m18e("tmessages", e2);
                }
                if (this.f538d != null) {
                    MessagesController.getInstance().putUser(this.f538d, true);
                } else {
                    return;
                }
            }
        } else {
            return;
        }
        if (ChatObject.isChannel(this.f537c)) {
            MessagesController.getInstance().startShortPoll(this.f543i, false);
        }
        this.f546l.add(Integer.valueOf(this.f545k));
        MessagesController instance = MessagesController.getInstance();
        long j = this.f548n;
        int i3 = this.f541g;
        boolean isChannel = ChatObject.isChannel(this.f537c);
        int i4 = this.f545k;
        this.f545k = i4 + 1;
        instance.loadMessages(j, 20, 0, true, 0, i3, 2, 0, isChannel, i4);
        if (this.f537c != null) {
            MessagesController.getInstance().loadChatInfo(this.f537c.id, null, ChatObject.isChannel(this.f537c));
        }
    }

    private void m551c() {
        NotificationManagerCompat.from(this).cancel(11259188);
    }

    private static void m552c(Context context) {
        ((AlarmManager) context.getSystemService(NotificationCompatApi24.CATEGORY_ALARM)).cancel(m548b(context));
    }

    private void m553d() {
        startForeground(11259188, m541a(LocaleController.getString("DownloadingFiles", C0338R.string.DownloadingFiles)).build());
    }

    private void m554e() {
        try {
            if (MoboConstants.au && this.f539e) {
                ((WifiManager) getSystemService("wifi")).setWifiEnabled(false);
            }
        } catch (Exception e) {
        }
        this.f542h = false;
        Iterator it = this.f536a.iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            if (messageObject.messageOwner.media != null) {
                FileLoader.getInstance().cancelLoadFile(messageObject.messageOwner.media.document);
            }
        }
        m551c();
        m552c((Context) this);
        this.f540f = true;
        stopForeground(true);
        stopSelf();
        f529b = false;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogDmServiceStoped, new Object[0]);
    }

    private void m555f() {
        int i = -this.f557w.size();
        int c = DialogDmUtil.m617c(this.f534E.m564d());
        Iterator it = this.f536a.iterator();
        int i2 = i;
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) it.next();
            if (i2 == c) {
                break;
            }
            i2++;
            File pathToMessage = FileLoader.getPathToMessage(messageObject.messageOwner);
            if (pathToMessage != null && !pathToMessage.exists() && m546a(messageObject)) {
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
        if (this.f536a.size() - this.f557w.size() >= c || this.f551q[0]) {
            m549b();
        } else {
            m545a(false);
        }
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.messagesDidLoaded) {
            if (((Integer) objArr[10]).intValue() == this.f541g) {
                int indexOf = this.f546l.indexOf(Integer.valueOf(((Integer) objArr[11]).intValue()));
                if (indexOf != -1) {
                    this.f546l.remove(indexOf);
                    ArrayList arrayList = (ArrayList) objArr[2];
                    this.f547m++;
                    int i2 = ((Long) objArr[0]).longValue() == this.f548n ? 0 : 1;
                    int intValue = ((Integer) objArr[1]).intValue();
                    boolean booleanValue = ((Boolean) objArr[3]).booleanValue();
                    int intValue2 = ((Integer) objArr[4]).intValue();
                    ((Integer) objArr[7]).intValue();
                    int intValue3 = ((Integer) objArr[8]).intValue();
                    if (intValue2 != 0) {
                        this.f549o = ((Integer) objArr[5]).intValue();
                    }
                    this.f550p[i2] = this.f549o == 0;
                    if ((intValue3 == 1 || intValue3 == 3) && i2 == 1) {
                        boolean[] zArr = this.f551q;
                        this.f552r[0] = true;
                        zArr[0] = true;
                        this.f550p[0] = false;
                        this.f554t[0] = 0;
                    }
                    if (this.f547m == 1 && arrayList.size() > 20) {
                        this.f547m++;
                    }
                    if (this.f555u) {
                        if (!this.f550p[i2]) {
                            this.f536a.clear();
                            this.f557w.clear();
                            for (int i3 = 0; i3 < 2; i3++) {
                                this.f556v[i3].clear();
                                this.f553s[i3] = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                this.f554t[i3] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                                this.f558x[i3] = TLRPC.MESSAGE_FLAG_MEGAGROUP;
                                this.f559y[i3] = 0;
                            }
                        }
                        this.f555u = false;
                    }
                    if (intValue3 == 1) {
                        Collections.reverse(arrayList);
                    }
                    int i4 = 0;
                    int i5 = 0;
                    while (i4 < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) arrayList.get(i4);
                        if (this.f556v[i2].containsKey(Integer.valueOf(messageObject.getId()))) {
                            intValue2 = i5;
                        } else {
                            if (i2 == 1) {
                                messageObject.setIsRead();
                            }
                            if (i2 == 0 && ChatObject.isChannel(this.f537c) && messageObject.getId() == 1) {
                                this.f551q[i2] = true;
                                this.f552r[i2] = true;
                            }
                            if (messageObject.getId() > 0) {
                                this.f553s[i2] = Math.min(messageObject.getId(), this.f553s[i2]);
                                this.f554t[i2] = Math.max(messageObject.getId(), this.f554t[i2]);
                            }
                            if (messageObject.messageOwner.date != 0) {
                                this.f558x[i2] = Math.max(this.f558x[i2], messageObject.messageOwner.date);
                                if (this.f559y[i2] == 0 || messageObject.messageOwner.date < this.f559y[i2]) {
                                    this.f559y[i2] = messageObject.messageOwner.date;
                                }
                            }
                            if (messageObject.type >= 0) {
                                if (i2 == 1 && (messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo)) {
                                    intValue2 = i5;
                                } else {
                                    ArrayList arrayList2;
                                    if (messageObject.isOut() || messageObject.isUnread()) {
                                        this.f556v[i2].put(Integer.valueOf(messageObject.getId()), messageObject);
                                        arrayList2 = (ArrayList) this.f557w.get(messageObject.dateKey);
                                    } else {
                                        this.f556v[i2].put(Integer.valueOf(messageObject.getId()), messageObject);
                                        arrayList2 = (ArrayList) this.f557w.get(messageObject.dateKey);
                                    }
                                    if (arrayList2 == null) {
                                        arrayList2 = new ArrayList();
                                        this.f557w.put(messageObject.dateKey, arrayList2);
                                        Message message = new Message();
                                        message.message = LocaleController.formatDateChat((long) messageObject.messageOwner.date);
                                        message.id = 0;
                                        MessageObject messageObject2 = new MessageObject(message, null, false);
                                        messageObject2.type = 10;
                                        messageObject2.contentType = 1;
                                        if (intValue3 == 1) {
                                            this.f536a.add(0, messageObject2);
                                        } else {
                                            this.f536a.add(messageObject2);
                                        }
                                        i5++;
                                    }
                                    i5++;
                                    if (intValue3 == 1) {
                                        arrayList2.add(messageObject);
                                        this.f536a.add(0, messageObject);
                                    }
                                    if (intValue3 != 1) {
                                        arrayList2.add(messageObject);
                                        this.f536a.add(this.f536a.size() - 1, messageObject);
                                    }
                                    if (messageObject.getId() == this.f549o) {
                                        this.f550p[i2] = true;
                                    }
                                }
                            }
                            intValue2 = i5;
                        }
                        i4++;
                        i5 = intValue2;
                    }
                    if (intValue3 == 0 && i5 == 0) {
                        this.f547m--;
                    }
                    if (this.f550p[i2] && i2 != 1) {
                        this.f549o = 0;
                    }
                    if (intValue3 == 1) {
                        if (!(arrayList.size() == intValue || booleanValue)) {
                            this.f550p[i2] = true;
                            if (i2 != 1) {
                                this.f549o = 0;
                                indexOf = i5 - 1;
                            }
                        }
                        this.f531B = false;
                    } else {
                        if (arrayList.size() < intValue && intValue3 != 3) {
                            if (booleanValue) {
                                if (intValue3 != 2) {
                                    this.f552r[i2] = true;
                                }
                            } else if (intValue3 != 2) {
                                this.f551q[i2] = true;
                            }
                        }
                        this.f560z = false;
                    }
                    if (this.f530A && this.f536a.size() > 0) {
                        if (i2 == 0) {
                            ((MessageObject) this.f536a.get(0)).getId();
                            AndroidUtilities.runOnUIThread(new C09063(this), 700);
                        }
                        this.f530A = false;
                    }
                } else {
                    return;
                }
            }
            if (this.f542h) {
                m555f();
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
        f529b = false;
        stopForeground(true);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messagesDidLoaded);
        if (!this.f540f) {
            m544a((Context) this);
        }
        this.f540f = false;
    }

    public void onFailedDownload(String str) {
        m543a(0);
        if (this.f542h) {
            m555f();
        }
    }

    public void onProgressDownload(String str, float f) {
        m543a((int) (100.0f * f));
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null && intent.getBooleanExtra("com.hanista.mobogram.dialogdm.use.wifi", false)) {
            this.f539e = true;
        }
        if (intent == null || "com.hanista.mobogram.dialogdm.start".equals(intent.getAction())) {
            f529b = true;
            m542a();
        } else if ("com.hanista.mobogram.dialogdm.stop".equals(intent.getAction())) {
            f529b = false;
            m554e();
        }
        return 1;
    }

    public void onSuccessDownload(String str) {
        this.f535F++;
        m543a(0);
        if (this.f542h) {
            m555f();
        }
    }
}

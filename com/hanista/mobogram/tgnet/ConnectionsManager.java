package com.hanista.mobogram.tgnet;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.ApplicationLoader;
import com.hanista.mobogram.messenger.BuildVars;
import com.hanista.mobogram.messenger.ContactsController;
import com.hanista.mobogram.messenger.FileLog;
import com.hanista.mobogram.messenger.MessagesController;
import com.hanista.mobogram.messenger.NotificationCenter;
import com.hanista.mobogram.messenger.UserConfig;
import com.hanista.mobogram.messenger.Utilities;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.mobo.MoboConstants;
import com.hanista.mobogram.mobo.p005f.DialogSettings;
import com.hanista.mobogram.mobo.p005f.DialogSettingsUtil;
import com.hanista.mobogram.tgnet.TLRPC.InputPeer;
import com.hanista.mobogram.tgnet.TLRPC.TL_channels_readHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_config;
import com.hanista.mobogram.tgnet.TLRPC.TL_error;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_readEncryptedHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_readHistory;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_readMessageContents;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_setEncryptedTyping;
import com.hanista.mobogram.tgnet.TLRPC.TL_messages_setTyping;
import com.hanista.mobogram.tgnet.TLRPC.Updates;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.aspectj.lang.JoinPoint;

public class ConnectionsManager {
    public static final int ConnectionStateConnected = 3;
    public static final int ConnectionStateConnecting = 1;
    public static final int ConnectionStateUpdating = 4;
    public static final int ConnectionStateWaitingForNetwork = 2;
    public static final int ConnectionTypeDownload = 2;
    public static final int ConnectionTypeDownload2 = 65538;
    public static final int ConnectionTypeGeneric = 1;
    public static final int ConnectionTypePush = 8;
    public static final int ConnectionTypeUpload = 4;
    public static final int DEFAULT_DATACENTER_ID = Integer.MAX_VALUE;
    private static volatile ConnectionsManager Instance = null;
    public static final int RequestFlagCanCompress = 4;
    public static final int RequestFlagEnableUnauthorized = 1;
    public static final int RequestFlagFailOnServerErrors = 2;
    public static final int RequestFlagForceDownload = 32;
    public static final int RequestFlagInvokeAfter = 64;
    public static final int RequestFlagNeedQuickAck = 128;
    public static final int RequestFlagTryDifferentDc = 16;
    public static final int RequestFlagWithoutLogin = 8;
    private boolean appPaused;
    private int connectionState;
    private boolean isUpdating;
    private int lastClassGuid;
    private long lastPauseTime;
    private AtomicInteger lastRequestToken;
    private WakeLock wakeLock;

    /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ boolean val$value;

        AnonymousClass11(boolean z) {
            this.val$value = z;
        }

        public void run() {
            if (ConnectionsManager.this.isUpdating != this.val$value) {
                ConnectionsManager.this.isUpdating = this.val$value;
                if (ConnectionsManager.this.connectionState == ConnectionsManager.ConnectionStateConnected) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
                }
            }
        }
    }

    /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.1 */
    class C09361 implements Runnable {
        final /* synthetic */ int val$connetionType;
        final /* synthetic */ int val$datacenterId;
        final /* synthetic */ int val$flags;
        final /* synthetic */ boolean val$immediate;
        final /* synthetic */ TLObject val$object;
        final /* synthetic */ RequestDelegate val$onComplete;
        final /* synthetic */ QuickAckDelegate val$onQuickAck;
        final /* synthetic */ int val$requestToken;

        /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.1.1 */
        class C09351 implements RequestDelegateInternal {

            /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.1.1.1 */
            class C09341 implements Runnable {
                final /* synthetic */ TL_error val$finalError;
                final /* synthetic */ TLObject val$finalResponse;

                C09341(TLObject tLObject, TL_error tL_error) {
                    this.val$finalResponse = tLObject;
                    this.val$finalError = tL_error;
                }

                public void run() {
                    C09361.this.val$onComplete.run(this.val$finalResponse, this.val$finalError);
                    if (this.val$finalResponse != null) {
                        this.val$finalResponse.freeResources();
                    }
                }
            }

            C09351() {
            }

            public void run(int i, int i2, String str) {
                TLObject deserializeResponse;
                TL_error tL_error = null;
                if (i != 0) {
                    try {
                        AbstractSerializedData wrap = NativeByteBuffer.wrap(i);
                        wrap.reused = true;
                        deserializeResponse = C09361.this.val$object.deserializeResponse(wrap, wrap.readInt32(true), true);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                        return;
                    }
                } else if (str != null) {
                    TL_error tL_error2 = new TL_error();
                    tL_error2.code = i2;
                    tL_error2.text = str;
                    FileLog.m16e("tmessages", C09361.this.val$object + " got error " + tL_error2.code + " " + tL_error2.text);
                    TL_error tL_error3 = tL_error2;
                    deserializeResponse = null;
                    tL_error = tL_error3;
                } else {
                    deserializeResponse = null;
                }
                FileLog.m15d("tmessages", "java received " + deserializeResponse + " error = " + tL_error);
                Utilities.stageQueue.postRunnable(new C09341(deserializeResponse, tL_error));
            }
        }

        C09361(TLObject tLObject, int i, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, int i2, int i3, int i4, boolean z) {
            this.val$object = tLObject;
            this.val$requestToken = i;
            this.val$onComplete = requestDelegate;
            this.val$onQuickAck = quickAckDelegate;
            this.val$flags = i2;
            this.val$datacenterId = i3;
            this.val$connetionType = i4;
            this.val$immediate = z;
        }

        public void run() {
            FileLog.m15d("tmessages", "send request " + this.val$object + " with token = " + this.val$requestToken);
            try {
                AbstractSerializedData nativeByteBuffer = new NativeByteBuffer(this.val$object.getObjectSize());
                this.val$object.serializeToStream(nativeByteBuffer);
                this.val$object.freeResources();
                ConnectionsManager.native_sendRequest(nativeByteBuffer.address, new C09351(), this.val$onQuickAck, this.val$flags, this.val$datacenterId, this.val$connetionType, this.val$immediate, this.val$requestToken);
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.2 */
    class C09372 extends BroadcastReceiver {
        C09372() {
        }

        public void onReceive(Context context, Intent intent) {
            ConnectionsManager.this.checkConnection();
        }
    }

    /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.3 */
    static class C09383 implements Runnable {
        C09383() {
        }

        public void run() {
            if (ConnectionsManager.getInstance().wakeLock.isHeld()) {
                FileLog.m15d("tmessages", "release wakelock");
                ConnectionsManager.getInstance().wakeLock.release();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.4 */
    static class C09394 implements Runnable {
        final /* synthetic */ TLObject val$message;

        C09394(TLObject tLObject) {
            this.val$message = tLObject;
        }

        public void run() {
            MessagesController.getInstance().processUpdates((Updates) this.val$message, false);
        }
    }

    /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.5 */
    static class C09405 implements Runnable {
        C09405() {
        }

        public void run() {
            MessagesController.getInstance().updateTimerProc();
        }
    }

    /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.6 */
    static class C09416 implements Runnable {
        C09416() {
        }

        public void run() {
            MessagesController.getInstance().getDifference();
        }
    }

    /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.7 */
    static class C09427 implements Runnable {
        final /* synthetic */ int val$state;

        C09427(int i) {
            this.val$state = i;
        }

        public void run() {
            ConnectionsManager.getInstance().connectionState = this.val$state;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didUpdatedConnectionState, new Object[0]);
        }
    }

    /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.8 */
    static class C09438 implements Runnable {
        C09438() {
        }

        public void run() {
            if (UserConfig.getClientUserId() != 0) {
                UserConfig.clearConfig();
                MessagesController.getInstance().performLogout(false);
            }
        }
    }

    /* renamed from: com.hanista.mobogram.tgnet.ConnectionsManager.9 */
    static class C09449 implements Runnable {
        final /* synthetic */ TL_config val$message;

        C09449(TL_config tL_config) {
            this.val$message = tL_config;
        }

        public void run() {
            MessagesController.getInstance().updateConfig(this.val$message);
        }
    }

    static {
        Instance = null;
    }

    public ConnectionsManager() {
        this.lastPauseTime = System.currentTimeMillis();
        this.appPaused = true;
        this.lastClassGuid = RequestFlagEnableUnauthorized;
        this.isUpdating = false;
        this.connectionState = native_getConnectionState();
        this.lastRequestToken = new AtomicInteger(RequestFlagEnableUnauthorized);
        this.wakeLock = null;
        try {
            this.wakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(RequestFlagEnableUnauthorized, JoinPoint.SYNCHRONIZATION_LOCK);
            this.wakeLock.setReferenceCounted(false);
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public static ConnectionsManager getInstance() {
        ConnectionsManager connectionsManager = Instance;
        if (connectionsManager == null) {
            synchronized (ConnectionsManager.class) {
                connectionsManager = Instance;
                if (connectionsManager == null) {
                    connectionsManager = new ConnectionsManager();
                    Instance = connectionsManager;
                }
            }
        }
        return connectionsManager;
    }

    public static boolean isConnectedToWiFi() {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getNetworkInfo(RequestFlagEnableUnauthorized);
            if (networkInfo != null && networkInfo.getState() == State.CONNECTED) {
                return true;
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return false;
    }

    public static boolean isNetworkOnline() {
        if (MoboConstants.aO) {
            return false;
        }
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity");
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && (activeNetworkInfo.isConnectedOrConnecting() || activeNetworkInfo.isAvailable())) {
                return true;
            }
            activeNetworkInfo = connectivityManager.getNetworkInfo(0);
            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                return true;
            }
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(RequestFlagEnableUnauthorized);
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
            return true;
        }
    }

    private boolean isRequestProhibited(TLObject tLObject) {
        if (((tLObject instanceof TL_messages_readHistory) || (tLObject instanceof TL_channels_readHistory) || (tLObject instanceof TL_messages_readEncryptedHistory) || (tLObject instanceof TL_messages_readMessageContents)) && (MoboConstants.f1338e || MoboConstants.f1340g)) {
            return true;
        }
        if (((tLObject instanceof TL_messages_setTyping) || (tLObject instanceof TL_messages_setEncryptedTyping)) && MoboConstants.f1339f) {
            return true;
        }
        long j = 0;
        InputPeer inputPeer;
        DialogSettings a;
        if (tLObject instanceof TL_messages_setTyping) {
            inputPeer = ((TL_messages_setTyping) tLObject).peer;
            if (inputPeer.channel_id > 0) {
                j = (long) (-inputPeer.channel_id);
            } else if (inputPeer.chat_id > 0) {
                j = (long) (-inputPeer.chat_id);
            } else if (inputPeer.user_id > 0) {
                j = (long) inputPeer.user_id;
            }
            a = DialogSettingsUtil.m997a(j);
            if (a != null && a.m982e()) {
                return true;
            }
        } else if (tLObject instanceof TL_messages_setEncryptedTyping) {
            r3 = ((TL_messages_setEncryptedTyping) tLObject).peer;
            if (r3.chat_id > 0) {
                j = ((long) r3.chat_id) << 32;
            }
            a = DialogSettingsUtil.m997a(j);
            if (a != null && a.m982e()) {
                return true;
            }
        } else if (tLObject instanceof TL_messages_readHistory) {
            inputPeer = ((TL_messages_readHistory) tLObject).peer;
            if (inputPeer.channel_id > 0) {
                j = (long) (-inputPeer.channel_id);
            } else if (inputPeer.chat_id > 0) {
                j = (long) (-inputPeer.chat_id);
            } else if (inputPeer.user_id > 0) {
                j = (long) inputPeer.user_id;
            }
            a = DialogSettingsUtil.m997a(j);
            if (a != null && a.m983f()) {
                return true;
            }
        } else if (tLObject instanceof TL_channels_readHistory) {
            a = DialogSettingsUtil.m997a((long) (-((TL_channels_readHistory) tLObject).channel.channel_id));
            if (a != null && a.m983f()) {
                return true;
            }
        } else if (tLObject instanceof TL_messages_readEncryptedHistory) {
            r3 = ((TL_messages_readEncryptedHistory) tLObject).peer;
            if (r3.chat_id > 0) {
                j = ((long) r3.chat_id) << 32;
            }
            a = DialogSettingsUtil.m997a(j);
            if (a != null && a.m983f()) {
                return true;
            }
        } else if (tLObject instanceof TL_messages_readMessageContents) {
            a = DialogSettingsUtil.m997a(((TL_messages_readMessageContents) tLObject).dialogId);
            if (a != null && a.m983f()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRoaming() {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isRoaming();
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
        return false;
    }

    public static native void native_applyDatacenterAddress(int i, String str, int i2);

    public static native void native_bindRequestToGuid(int i, int i2);

    public static native void native_cancelRequest(int i, boolean z);

    public static native void native_cancelRequestsForGuid(int i);

    public static native void native_cleanUp();

    public static native int native_getConnectionState();

    public static native int native_getCurrentTime();

    public static native long native_getCurrentTimeMillis();

    public static native int native_getTimeDifference();

    public static native void native_init(int i, int i2, int i3, String str, String str2, String str3, String str4, String str5, String str6, int i4, boolean z);

    public static native void native_pauseNetwork();

    public static native void native_resumeNetwork(boolean z);

    public static native void native_sendRequest(int i, RequestDelegateInternal requestDelegateInternal, QuickAckDelegate quickAckDelegate, int i2, int i3, int i4, boolean z, int i5);

    public static native void native_setJava(boolean z);

    public static native void native_setNetworkAvailable(boolean z);

    public static native void native_setPushConnectionEnabled(boolean z);

    public static native void native_setUseIpv6(boolean z);

    public static native void native_setUserId(int i);

    public static native void native_switchBackend();

    public static native void native_updateDcSettings();

    public static void onConnectionStateChanged(int i) {
        AndroidUtilities.runOnUIThread(new C09427(i));
    }

    public static void onInternalPushReceived() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                try {
                    if (!ConnectionsManager.getInstance().wakeLock.isHeld()) {
                        ConnectionsManager.getInstance().wakeLock.acquire(10000);
                        FileLog.m15d("tmessages", "acquire wakelock");
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        });
    }

    public static void onLogout() {
        AndroidUtilities.runOnUIThread(new C09438());
    }

    public static void onSessionCreated() {
        Utilities.stageQueue.postRunnable(new C09416());
    }

    public static void onUnparsedMessageReceived(int i) {
        try {
            NativeByteBuffer wrap = NativeByteBuffer.wrap(i);
            wrap.reused = true;
            TLObject TLdeserialize = TLClassStore.Instance().TLdeserialize(wrap, wrap.readInt32(true), true);
            if (TLdeserialize instanceof Updates) {
                FileLog.m15d("tmessages", "java received " + TLdeserialize);
                AndroidUtilities.runOnUIThread(new C09383());
                Utilities.stageQueue.postRunnable(new C09394(TLdeserialize));
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    public static void onUpdate() {
        Utilities.stageQueue.postRunnable(new C09405());
    }

    public static void onUpdateConfig(int i) {
        try {
            AbstractSerializedData wrap = NativeByteBuffer.wrap(i);
            wrap.reused = true;
            TL_config TLdeserialize = TL_config.TLdeserialize(wrap, wrap.readInt32(true), true);
            if (TLdeserialize != null) {
                Utilities.stageQueue.postRunnable(new C09449(TLdeserialize));
            }
        } catch (Throwable e) {
            FileLog.m18e("tmessages", e);
        }
    }

    @SuppressLint({"NewApi"})
    protected static boolean useIpv6Address() {
        if (VERSION.SDK_INT < 19) {
            return false;
        }
        NetworkInterface networkInterface;
        InetAddress address;
        if (BuildVars.DEBUG_VERSION) {
            try {
                Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements()) {
                    networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                    if (!(!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.getInterfaceAddresses().isEmpty())) {
                        FileLog.m16e("tmessages", "valid interface: " + networkInterface);
                        List interfaceAddresses = networkInterface.getInterfaceAddresses();
                        for (int i = 0; i < interfaceAddresses.size(); i += RequestFlagEnableUnauthorized) {
                            address = ((InterfaceAddress) interfaceAddresses.get(i)).getAddress();
                            if (BuildVars.DEBUG_VERSION) {
                                FileLog.m16e("tmessages", "address: " + address.getHostAddress());
                            }
                            if (!(address.isLinkLocalAddress() || address.isLoopbackAddress() || address.isMulticastAddress() || !BuildVars.DEBUG_VERSION)) {
                                FileLog.m16e("tmessages", "address is good");
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                FileLog.m18e("tmessages", th);
            }
        }
        try {
            Enumeration networkInterfaces2 = NetworkInterface.getNetworkInterfaces();
            boolean z = false;
            boolean z2 = false;
            while (networkInterfaces2.hasMoreElements()) {
                networkInterface = (NetworkInterface) networkInterfaces2.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    List interfaceAddresses2 = networkInterface.getInterfaceAddresses();
                    int i2 = 0;
                    while (i2 < interfaceAddresses2.size()) {
                        boolean z3;
                        address = ((InterfaceAddress) interfaceAddresses2.get(i2)).getAddress();
                        if (!(address.isLinkLocalAddress() || address.isLoopbackAddress())) {
                            if (address.isMulticastAddress()) {
                                z3 = z;
                                z = z2;
                            } else if (address instanceof Inet6Address) {
                                z3 = true;
                                z = z2;
                            } else if ((address instanceof Inet4Address) && !address.getHostAddress().startsWith("192.0.0.")) {
                                z3 = z;
                                z = true;
                            }
                            i2 += RequestFlagEnableUnauthorized;
                            z2 = z;
                            z = z3;
                        }
                        z3 = z;
                        z = z2;
                        i2 += RequestFlagEnableUnauthorized;
                        z2 = z;
                        z = z3;
                    }
                }
            }
            if (!z2 && r1) {
                return true;
            }
        } catch (Throwable th2) {
            FileLog.m18e("tmessages", th2);
        }
        return false;
    }

    public void applyCountryPortNumber(String str) {
    }

    public void applyDatacenterAddress(int i, String str, int i2) {
        native_applyDatacenterAddress(i, str, i2);
    }

    public void bindRequestToGuid(int i, int i2) {
        native_bindRequestToGuid(i, i2);
    }

    public void cancelRequest(int i, boolean z) {
        native_cancelRequest(i, z);
    }

    public void cancelRequestsForGuid(int i) {
        native_cancelRequestsForGuid(i);
    }

    public void checkConnection() {
        native_setUseIpv6(useIpv6Address());
        native_setNetworkAvailable(isNetworkOnline());
    }

    public void cleanup() {
        native_cleanUp();
    }

    public int generateClassGuid() {
        int i = this.lastClassGuid;
        this.lastClassGuid = i + RequestFlagEnableUnauthorized;
        return i;
    }

    public int getConnectionState() {
        return (this.connectionState == ConnectionStateConnected && this.isUpdating) ? RequestFlagCanCompress : this.connectionState;
    }

    public int getCurrentTime() {
        return native_getCurrentTime();
    }

    public long getCurrentTimeMillis() {
        return native_getCurrentTimeMillis();
    }

    public long getPauseTime() {
        return this.lastPauseTime;
    }

    public int getTimeDifference() {
        return native_getTimeDifference();
    }

    public void init(int i, int i2, int i3, String str, String str2, String str3, String str4, String str5, String str6, int i4, boolean z) {
        native_init(i, i2, i3, str, str2, str3, str4, str5, str6, i4, z);
        checkConnection();
        ApplicationLoader.applicationContext.registerReceiver(new C09372(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void resumeNetworkMaybe() {
        native_resumeNetwork(true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate) {
        return sendRequest(tLObject, requestDelegate, null, 0);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, int i) {
        return sendRequest(tLObject, requestDelegate, null, i, DEFAULT_DATACENTER_ID, RequestFlagEnableUnauthorized, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, int i, int i2) {
        return sendRequest(tLObject, requestDelegate, null, i, DEFAULT_DATACENTER_ID, i2, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, int i) {
        return sendRequest(tLObject, requestDelegate, quickAckDelegate, i, DEFAULT_DATACENTER_ID, RequestFlagEnableUnauthorized, true);
    }

    public int sendRequest(TLObject tLObject, RequestDelegate requestDelegate, QuickAckDelegate quickAckDelegate, int i, int i2, int i3, boolean z) {
        if (isRequestProhibited(tLObject)) {
            return 0;
        }
        int andIncrement = this.lastRequestToken.getAndIncrement();
        Utilities.stageQueue.postRunnable(new C09361(tLObject, andIncrement, requestDelegate, quickAckDelegate, i, i2, i3, z));
        return andIncrement;
    }

    public void setAppPaused(boolean z, boolean z2) {
        if (!z2) {
            this.appPaused = z;
            FileLog.m15d("tmessages", "app paused = " + z);
        }
        if (z) {
            if (!MoboConstants.f1322O) {
                if (this.lastPauseTime == 0) {
                    this.lastPauseTime = System.currentTimeMillis();
                }
                native_pauseNetwork();
            }
        } else if (!this.appPaused || MoboConstants.f1322O) {
            FileLog.m16e("tmessages", "reset app pause time");
            if (this.lastPauseTime != 0 && System.currentTimeMillis() - this.lastPauseTime > HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS) {
                ContactsController.getInstance().checkContacts();
            }
            this.lastPauseTime = 0;
            native_resumeNetwork(false);
        }
    }

    public void setIsUpdating(boolean z) {
        AndroidUtilities.runOnUIThread(new AnonymousClass11(z));
    }

    public void setPushConnectionEnabled(boolean z) {
        native_setPushConnectionEnabled(z);
    }

    public void setUserId(int i) {
        native_setUserId(i);
    }

    public void switchBackend() {
        native_switchBackend();
    }

    public void updateDcSettings() {
        native_updateDcSettings();
    }
}

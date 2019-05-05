package com.hanista.mobogram.messenger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.mobo.p004e.DataBaseAccess;
import com.hanista.mobogram.mobo.p012l.HiddenConfig;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.SerializedData;
import com.hanista.mobogram.tgnet.TLRPC.User;
import java.io.File;

public class UserConfig {
    public static boolean appLocked;
    public static int autoLockIn;
    public static boolean blockedUsersLoaded;
    public static String contactsHash;
    private static User currentUser;
    public static boolean draftsLoaded;
    public static boolean isRobot;
    public static boolean isWaitingForPasscodeEnter;
    public static int lastBroadcastId;
    public static int lastContactsSyncTime;
    public static int lastHintsSyncTime;
    public static int lastLocalId;
    public static int lastPauseTime;
    public static int lastSendMessageId;
    public static String lastUpdateVersion;
    public static long migrateOffsetAccess;
    public static int migrateOffsetChannelId;
    public static int migrateOffsetChatId;
    public static int migrateOffsetDate;
    public static int migrateOffsetId;
    public static int migrateOffsetUserId;
    public static String passcodeHash;
    public static byte[] passcodeSalt;
    public static int passcodeType;
    public static String pushString;
    public static boolean registeredForPush;
    public static boolean saveIncomingPhotos;
    private static final Object sync;
    public static boolean useFingerprint;

    /* renamed from: com.hanista.mobogram.messenger.UserConfig.1 */
    static class C06841 implements Runnable {
        final /* synthetic */ File val$configFile;

        C06841(File file) {
            this.val$configFile = file;
        }

        public void run() {
            UserConfig.saveConfig(true, this.val$configFile);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.UserConfig.2 */
    static class C06852 implements Runnable {
        final /* synthetic */ File val$configFile;

        C06852(File file) {
            this.val$configFile = file;
        }

        public void run() {
            UserConfig.saveConfig(true, this.val$configFile);
        }
    }

    static {
        pushString = TtmlNode.ANONYMOUS_REGION_ID;
        lastSendMessageId = -210000;
        lastLocalId = -210000;
        lastBroadcastId = -1;
        contactsHash = TtmlNode.ANONYMOUS_REGION_ID;
        sync = new Object();
        passcodeHash = TtmlNode.ANONYMOUS_REGION_ID;
        passcodeSalt = new byte[0];
        autoLockIn = 3600;
        useFingerprint = true;
        migrateOffsetId = -1;
        migrateOffsetDate = -1;
        migrateOffsetUserId = -1;
        migrateOffsetChatId = -1;
        migrateOffsetChannelId = -1;
        migrateOffsetAccess = -1;
    }

    public static boolean checkPasscode(String str) {
        boolean z = false;
        Object bytes;
        Object obj;
        if (passcodeSalt.length == 0) {
            z = Utilities.MD5(str).equals(passcodeHash);
            if (z) {
                try {
                    passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(passcodeSalt);
                    bytes = str.getBytes(C0700C.UTF8_NAME);
                    obj = new byte[(bytes.length + 32)];
                    System.arraycopy(passcodeSalt, 0, obj, 0, 16);
                    System.arraycopy(bytes, 0, obj, 16, bytes.length);
                    System.arraycopy(passcodeSalt, 0, obj, bytes.length + 16, 16);
                    passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(obj, 0, obj.length));
                    saveConfig(false);
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        } else {
            try {
                bytes = str.getBytes(C0700C.UTF8_NAME);
                obj = new byte[(bytes.length + 32)];
                System.arraycopy(passcodeSalt, 0, obj, 0, 16);
                System.arraycopy(bytes, 0, obj, 16, bytes.length);
                System.arraycopy(passcodeSalt, 0, obj, bytes.length + 16, 16);
                z = passcodeHash.equals(Utilities.bytesToHex(Utilities.computeSHA256(obj, 0, obj.length)));
            } catch (Throwable e2) {
                FileLog.m18e("tmessages", e2);
            }
        }
        return z;
    }

    public static void clearConfig() {
        currentUser = null;
        registeredForPush = false;
        contactsHash = TtmlNode.ANONYMOUS_REGION_ID;
        lastSendMessageId = -210000;
        lastBroadcastId = -1;
        saveIncomingPhotos = false;
        blockedUsersLoaded = false;
        migrateOffsetId = -1;
        migrateOffsetDate = -1;
        migrateOffsetUserId = -1;
        migrateOffsetChatId = -1;
        migrateOffsetChannelId = -1;
        migrateOffsetAccess = -1;
        appLocked = false;
        passcodeType = 0;
        passcodeHash = TtmlNode.ANONYMOUS_REGION_ID;
        passcodeSalt = new byte[0];
        autoLockIn = 3600;
        lastPauseTime = 0;
        useFingerprint = true;
        draftsLoaded = true;
        isWaitingForPasscodeEnter = false;
        lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
        lastContactsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 82800;
        lastHintsSyncTime = ((int) (System.currentTimeMillis() / 1000)) - 90000;
        isRobot = false;
        saveConfig(true);
        HiddenConfig.m1402e();
        new DataBaseAccess().m831A();
    }

    public static int getClientUserId() {
        int i;
        synchronized (sync) {
            i = currentUser != null ? currentUser.id : 0;
        }
        return i;
    }

    public static User getCurrentUser() {
        User user;
        synchronized (sync) {
            user = currentUser;
        }
        return user;
    }

    public static int getNewMessageId() {
        int i;
        synchronized (sync) {
            i = lastSendMessageId;
            lastSendMessageId--;
        }
        return i;
    }

    public static boolean isClientActivated() {
        boolean z;
        synchronized (sync) {
            z = currentUser != null;
        }
        return z;
    }

    public static void loadConfig() {
        synchronized (sync) {
            File file = new File(ApplicationLoader.getFilesDirFixed(), "user.dat");
            if (file.exists()) {
                try {
                    AbstractSerializedData serializedData = new SerializedData(file);
                    int readInt32 = serializedData.readInt32(false);
                    if (readInt32 == 1) {
                        currentUser = User.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                        MessagesStorage.lastDateValue = serializedData.readInt32(false);
                        MessagesStorage.lastPtsValue = serializedData.readInt32(false);
                        MessagesStorage.lastSeqValue = serializedData.readInt32(false);
                        registeredForPush = serializedData.readBool(false);
                        pushString = serializedData.readString(false);
                        lastSendMessageId = serializedData.readInt32(false);
                        lastLocalId = serializedData.readInt32(false);
                        contactsHash = serializedData.readString(false);
                        serializedData.readString(false);
                        saveIncomingPhotos = serializedData.readBool(false);
                        MessagesStorage.lastQtsValue = serializedData.readInt32(false);
                        MessagesStorage.lastSecretVersion = serializedData.readInt32(false);
                        if (serializedData.readInt32(false) == 1) {
                            MessagesStorage.secretPBytes = serializedData.readByteArray(false);
                        }
                        MessagesStorage.secretG = serializedData.readInt32(false);
                        Utilities.stageQueue.postRunnable(new C06841(file));
                    } else if (readInt32 == 2) {
                        currentUser = User.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
                        registeredForPush = sharedPreferences.getBoolean("registeredForPush", false);
                        pushString = sharedPreferences.getString("pushString2", TtmlNode.ANONYMOUS_REGION_ID);
                        lastSendMessageId = sharedPreferences.getInt("lastSendMessageId", -210000);
                        lastLocalId = sharedPreferences.getInt("lastLocalId", -210000);
                        contactsHash = sharedPreferences.getString("contactsHash", TtmlNode.ANONYMOUS_REGION_ID);
                        saveIncomingPhotos = sharedPreferences.getBoolean("saveIncomingPhotos", false);
                    }
                    if (lastLocalId > -210000) {
                        lastLocalId = -210000;
                    }
                    if (lastSendMessageId > -210000) {
                        lastSendMessageId = -210000;
                    }
                    serializedData.cleanup();
                    Utilities.stageQueue.postRunnable(new C06852(file));
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            } else {
                SharedPreferences sharedPreferences2 = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
                registeredForPush = sharedPreferences2.getBoolean("registeredForPush", false);
                pushString = sharedPreferences2.getString("pushString2", TtmlNode.ANONYMOUS_REGION_ID);
                lastSendMessageId = sharedPreferences2.getInt("lastSendMessageId", -210000);
                lastLocalId = sharedPreferences2.getInt("lastLocalId", -210000);
                contactsHash = sharedPreferences2.getString("contactsHash", TtmlNode.ANONYMOUS_REGION_ID);
                saveIncomingPhotos = sharedPreferences2.getBoolean("saveIncomingPhotos", false);
                lastBroadcastId = sharedPreferences2.getInt("lastBroadcastId", -1);
                blockedUsersLoaded = sharedPreferences2.getBoolean("blockedUsersLoaded", false);
                passcodeHash = sharedPreferences2.getString("passcodeHash1", TtmlNode.ANONYMOUS_REGION_ID);
                appLocked = sharedPreferences2.getBoolean("appLocked", false);
                passcodeType = sharedPreferences2.getInt("passcodeType", 0);
                autoLockIn = sharedPreferences2.getInt("autoLockIn", 3600);
                lastPauseTime = sharedPreferences2.getInt("lastPauseTime", 0);
                useFingerprint = sharedPreferences2.getBoolean("useFingerprint", true);
                lastUpdateVersion = sharedPreferences2.getString("lastUpdateVersion2", "3.5");
                lastContactsSyncTime = sharedPreferences2.getInt("lastContactsSyncTime", ((int) (System.currentTimeMillis() / 1000)) - 82800);
                lastHintsSyncTime = sharedPreferences2.getInt("lastHintsSyncTime", ((int) (System.currentTimeMillis() / 1000)) - 90000);
                draftsLoaded = sharedPreferences2.getBoolean("draftsLoaded", false);
                isRobot = sharedPreferences2.getBoolean("isRobot", false);
                migrateOffsetId = sharedPreferences2.getInt("migrateOffsetId", 0);
                if (migrateOffsetId != -1) {
                    migrateOffsetDate = sharedPreferences2.getInt("migrateOffsetDate", 0);
                    migrateOffsetUserId = sharedPreferences2.getInt("migrateOffsetUserId", 0);
                    migrateOffsetChatId = sharedPreferences2.getInt("migrateOffsetChatId", 0);
                    migrateOffsetChannelId = sharedPreferences2.getInt("migrateOffsetChannelId", 0);
                    migrateOffsetAccess = sharedPreferences2.getLong("migrateOffsetAccess", 0);
                }
                String string = sharedPreferences2.getString("user", null);
                if (string != null) {
                    byte[] decode = Base64.decode(string, 0);
                    if (decode != null) {
                        AbstractSerializedData serializedData2 = new SerializedData(decode);
                        currentUser = User.TLdeserialize(serializedData2, serializedData2.readInt32(false), false);
                        serializedData2.cleanup();
                    }
                }
                String string2 = sharedPreferences2.getString("passcodeSalt", TtmlNode.ANONYMOUS_REGION_ID);
                if (string2.length() > 0) {
                    passcodeSalt = Base64.decode(string2, 0);
                } else {
                    passcodeSalt = new byte[0];
                }
            }
        }
    }

    public static void saveConfig(boolean z) {
        saveConfig(z, null);
    }

    public static void saveConfig(boolean z, File file) {
        synchronized (sync) {
            try {
                Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit();
                edit.putBoolean("registeredForPush", registeredForPush);
                edit.putString("pushString2", pushString);
                edit.putInt("lastSendMessageId", lastSendMessageId);
                edit.putInt("lastLocalId", lastLocalId);
                edit.putString("contactsHash", contactsHash);
                edit.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
                edit.putInt("lastBroadcastId", lastBroadcastId);
                edit.putBoolean("blockedUsersLoaded", blockedUsersLoaded);
                edit.putString("passcodeHash1", passcodeHash);
                edit.putString("passcodeSalt", passcodeSalt.length > 0 ? Base64.encodeToString(passcodeSalt, 0) : TtmlNode.ANONYMOUS_REGION_ID);
                edit.putBoolean("appLocked", appLocked);
                edit.putInt("passcodeType", passcodeType);
                edit.putInt("autoLockIn", autoLockIn);
                edit.putInt("lastPauseTime", lastPauseTime);
                edit.putString("lastUpdateVersion2", lastUpdateVersion);
                edit.putInt("lastContactsSyncTime", lastContactsSyncTime);
                edit.putBoolean("useFingerprint", useFingerprint);
                edit.putInt("lastHintsSyncTime", lastHintsSyncTime);
                edit.putBoolean("draftsLoaded", draftsLoaded);
                edit.putBoolean("isRobot", isRobot);
                edit.putInt("migrateOffsetId", migrateOffsetId);
                if (migrateOffsetId != -1) {
                    edit.putInt("migrateOffsetDate", migrateOffsetDate);
                    edit.putInt("migrateOffsetUserId", migrateOffsetUserId);
                    edit.putInt("migrateOffsetChatId", migrateOffsetChatId);
                    edit.putInt("migrateOffsetChannelId", migrateOffsetChannelId);
                    edit.putLong("migrateOffsetAccess", migrateOffsetAccess);
                }
                if (currentUser == null) {
                    edit.remove("user");
                } else if (z) {
                    AbstractSerializedData serializedData = new SerializedData();
                    currentUser.serializeToStream(serializedData);
                    edit.putString("user", Base64.encodeToString(serializedData.toByteArray(), 0));
                    serializedData.cleanup();
                }
                edit.commit();
                if (file != null) {
                    file.delete();
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
        }
    }

    public static void setCurrentUser(User user) {
        synchronized (sync) {
            currentUser = user;
        }
    }
}

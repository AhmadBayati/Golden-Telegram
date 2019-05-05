package com.hanista.mobogram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Iterator;

public class NotificationCenter {
    public static final int FileDidFailUpload;
    public static final int FileDidFailedLoad;
    public static final int FileDidLoaded;
    public static final int FileDidUpload;
    public static final int FileLoadProgressChanged;
    public static final int FileNewChunkAvailable;
    public static final int FilePreparingFailed;
    public static final int FilePreparingStarted;
    public static final int FileUploadProgressChanged;
    private static volatile NotificationCenter Instance;
    public static final int albumsDidLoaded;
    public static final int appDidLogout;
    public static final int audioDidReset;
    public static final int audioDidSent;
    public static final int audioDidStarted;
    public static final int audioPlayStateChanged;
    public static final int audioProgressDidChanged;
    public static final int audioRouteChanged;
    public static final int blockedUsersDidLoaded;
    public static final int botInfoDidLoaded;
    public static final int botKeyboardDidLoaded;
    public static final int cameraInitied;
    public static final int categoriesInfoDidLoaded;
    public static final int chatDidCreated;
    public static final int chatDidFailCreate;
    public static final int chatInfoCantLoad;
    public static final int chatInfoDidLoaded;
    public static final int chatSearchResultsAvailable;
    public static final int closeChats;
    public static final int closeOtherAppActivities;
    public static final int contactsDidLoaded;
    public static final int dialogDmServiceStarted;
    public static final int dialogDmServiceStoped;
    public static final int dialogPhotosLoaded;
    public static final int dialogsNeedReload;
    public static final int didCreatedNewDeleteTask;
    public static final int didLoadedPinnedMessage;
    public static final int didLoadedReplyMessages;
    public static final int didReceiveCall;
    public static final int didReceiveSmsCode;
    public static final int didReceivedNewMessages;
    public static final int didReceivedWebpages;
    public static final int didReceivedWebpagesInUpdates;
    public static final int didReplacedPhotoInMemCache;
    public static final int didSetNewWallpapper;
    public static final int didSetPasscode;
    public static final int didSetTwoStepPassword;
    public static final int didUpdatedConnectionState;
    public static final int didUpdatedMessagesViews;
    public static final int downloadServiceStarted;
    public static final int downloadServiceStoped;
    public static final int emojiDidLoaded;
    public static final int encryptedChatCreated;
    public static final int encryptedChatUpdated;
    public static final int featuredStickersDidLoaded;
    public static final int httpFileDidFailedLoad;
    public static final int httpFileDidLoaded;
    public static final int locationPermissionGranted;
    public static final int mainUserInfoChanged;
    public static final int mediaCountDidLoaded;
    public static final int mediaDidLoaded;
    public static final int menuSettingsChanged;
    public static final int messageReceivedByAck;
    public static final int messageReceivedByServer;
    public static final int messageSendError;
    public static final int messageThumbGenerated;
    public static final int messagesDeleted;
    public static final int messagesDidLoaded;
    public static final int messagesRead;
    public static final int messagesReadContent;
    public static final int messagesReadEncrypted;
    public static final int musicDidLoaded;
    public static final int needReloadArchivedStickers;
    public static final int needReloadRecentDialogsSearch;
    public static final int needShowAlert;
    public static final int newDraftReceived;
    public static final int newSessionReceived;
    public static final int notificationsSettingsUpdated;
    public static final int openedChatChanged;
    public static final int peerSettingsDidLoaded;
    public static final int powerChanged;
    public static final int privacyRulesUpdated;
    public static final int pushMessagesUpdated;
    public static final int recentDocumentsDidLoaded;
    public static final int recentImagesDidLoaded;
    public static final int recordProgressChanged;
    public static final int recordStartError;
    public static final int recordStarted;
    public static final int recordStopped;
    public static final int reloadHints;
    public static final int reloadInlineHints;
    public static final int removeAllMessagesFromDialog;
    public static final int replaceMessagesObjects;
    public static final int screenStateChanged;
    public static final int screenshotTook;
    public static final int stickersDidLoaded;
    public static final int stopEncodingService;
    private static int totalEvents;
    public static final int updateInterfaces;
    public static final int updateMessageMedia;
    public static final int userInfoDidLoaded;
    public static final int wallpaperChanged;
    public static final int wallpapersDidLoaded;
    public static final int wasUnableToFindCurrentLocation;
    private SparseArray<ArrayList<Object>> addAfterBroadcast;
    private int[] allowedNotifications;
    private boolean animationInProgress;
    private int broadcasting;
    private ArrayList<DelayedPost> delayedPosts;
    private SparseArray<ArrayList<Object>> observers;
    private SparseArray<ArrayList<Object>> removeAfterBroadcast;

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int i, Object... objArr);
    }

    private class DelayedPost {
        private Object[] args;
        private int id;

        private DelayedPost(int i, Object[] objArr) {
            this.id = i;
            this.args = objArr;
        }
    }

    static {
        totalEvents = 1;
        int i = totalEvents;
        totalEvents = i + 1;
        didReceivedNewMessages = i;
        i = totalEvents;
        totalEvents = i + 1;
        updateInterfaces = i;
        i = totalEvents;
        totalEvents = i + 1;
        dialogsNeedReload = i;
        i = totalEvents;
        totalEvents = i + 1;
        closeChats = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesDeleted = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesRead = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        messageReceivedByAck = i;
        i = totalEvents;
        totalEvents = i + 1;
        messageReceivedByServer = i;
        i = totalEvents;
        totalEvents = i + 1;
        messageSendError = i;
        i = totalEvents;
        totalEvents = i + 1;
        contactsDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatDidCreated = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatDidFailCreate = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatInfoDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatInfoCantLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        mediaDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        mediaCountDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        encryptedChatUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesReadEncrypted = i;
        i = totalEvents;
        totalEvents = i + 1;
        encryptedChatCreated = i;
        i = totalEvents;
        totalEvents = i + 1;
        dialogPhotosLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        removeAllMessagesFromDialog = i;
        i = totalEvents;
        totalEvents = i + 1;
        notificationsSettingsUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        pushMessagesUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        blockedUsersDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        openedChatChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        stopEncodingService = i;
        i = totalEvents;
        totalEvents = i + 1;
        didCreatedNewDeleteTask = i;
        i = totalEvents;
        totalEvents = i + 1;
        mainUserInfoChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        privacyRulesUpdated = i;
        i = totalEvents;
        totalEvents = i + 1;
        updateMessageMedia = i;
        i = totalEvents;
        totalEvents = i + 1;
        recentImagesDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        replaceMessagesObjects = i;
        i = totalEvents;
        totalEvents = i + 1;
        didSetPasscode = i;
        i = totalEvents;
        totalEvents = i + 1;
        didSetTwoStepPassword = i;
        i = totalEvents;
        totalEvents = i + 1;
        screenStateChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        didLoadedReplyMessages = i;
        i = totalEvents;
        totalEvents = i + 1;
        didLoadedPinnedMessage = i;
        i = totalEvents;
        totalEvents = i + 1;
        newSessionReceived = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReceivedWebpages = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReceivedWebpagesInUpdates = i;
        i = totalEvents;
        totalEvents = i + 1;
        stickersDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        featuredStickersDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReplacedPhotoInMemCache = i;
        i = totalEvents;
        totalEvents = i + 1;
        messagesReadContent = i;
        i = totalEvents;
        totalEvents = i + 1;
        botInfoDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        userInfoDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        botKeyboardDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        chatSearchResultsAvailable = i;
        i = totalEvents;
        totalEvents = i + 1;
        musicDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        needShowAlert = i;
        i = totalEvents;
        totalEvents = i + 1;
        didUpdatedMessagesViews = i;
        i = totalEvents;
        totalEvents = i + 1;
        needReloadRecentDialogsSearch = i;
        i = totalEvents;
        totalEvents = i + 1;
        locationPermissionGranted = i;
        i = totalEvents;
        totalEvents = i + 1;
        peerSettingsDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        wasUnableToFindCurrentLocation = i;
        i = totalEvents;
        totalEvents = i + 1;
        reloadHints = i;
        i = totalEvents;
        totalEvents = i + 1;
        reloadInlineHints = i;
        i = totalEvents;
        totalEvents = i + 1;
        newDraftReceived = i;
        i = totalEvents;
        totalEvents = i + 1;
        recentDocumentsDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        cameraInitied = i;
        i = totalEvents;
        totalEvents = i + 1;
        needReloadArchivedStickers = i;
        i = totalEvents;
        totalEvents = i + 1;
        didSetNewWallpapper = i;
        i = totalEvents;
        totalEvents = i + 1;
        httpFileDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        httpFileDidFailedLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        messageThumbGenerated = i;
        i = totalEvents;
        totalEvents = i + 1;
        wallpapersDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        closeOtherAppActivities = i;
        i = totalEvents;
        totalEvents = i + 1;
        didUpdatedConnectionState = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReceiveSmsCode = i;
        i = totalEvents;
        totalEvents = i + 1;
        didReceiveCall = i;
        i = totalEvents;
        totalEvents = i + 1;
        emojiDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        appDidLogout = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileDidUpload = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileDidFailUpload = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileUploadProgressChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileLoadProgressChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileDidFailedLoad = i;
        i = totalEvents;
        totalEvents = i + 1;
        FilePreparingStarted = i;
        i = totalEvents;
        totalEvents = i + 1;
        FileNewChunkAvailable = i;
        i = totalEvents;
        totalEvents = i + 1;
        FilePreparingFailed = i;
        i = totalEvents;
        totalEvents = i + 1;
        audioProgressDidChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        audioDidReset = i;
        i = totalEvents;
        totalEvents = i + 1;
        audioPlayStateChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        recordProgressChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        recordStarted = i;
        i = totalEvents;
        totalEvents = i + 1;
        recordStartError = i;
        i = totalEvents;
        totalEvents = i + 1;
        recordStopped = i;
        i = totalEvents;
        totalEvents = i + 1;
        screenshotTook = i;
        i = totalEvents;
        totalEvents = i + 1;
        albumsDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        audioDidSent = i;
        i = totalEvents;
        totalEvents = i + 1;
        audioDidStarted = i;
        i = totalEvents;
        totalEvents = i + 1;
        audioRouteChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        categoriesInfoDidLoaded = i;
        i = totalEvents;
        totalEvents = i + 1;
        downloadServiceStarted = i;
        i = totalEvents;
        totalEvents = i + 1;
        downloadServiceStoped = i;
        i = totalEvents;
        totalEvents = i + 1;
        menuSettingsChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        dialogDmServiceStarted = i;
        i = totalEvents;
        totalEvents = i + 1;
        dialogDmServiceStoped = i;
        i = totalEvents;
        totalEvents = i + 1;
        wallpaperChanged = i;
        i = totalEvents;
        totalEvents = i + 1;
        powerChanged = i;
        Instance = null;
    }

    public NotificationCenter() {
        this.observers = new SparseArray();
        this.removeAfterBroadcast = new SparseArray();
        this.addAfterBroadcast = new SparseArray();
        this.delayedPosts = new ArrayList(10);
        this.broadcasting = 0;
    }

    public static NotificationCenter getInstance() {
        NotificationCenter notificationCenter = Instance;
        if (notificationCenter == null) {
            synchronized (NotificationCenter.class) {
                notificationCenter = Instance;
                if (notificationCenter == null) {
                    notificationCenter = new NotificationCenter();
                    Instance = notificationCenter;
                }
            }
        }
        return notificationCenter;
    }

    public void addObserver(Object obj, int i) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("addObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            r0 = (ArrayList) this.addAfterBroadcast.get(i);
            if (r0 == null) {
                r0 = new ArrayList();
                this.addAfterBroadcast.put(i, r0);
            }
            r0.add(obj);
        } else {
            r0 = (ArrayList) this.observers.get(i);
            if (r0 == null) {
                SparseArray sparseArray = this.observers;
                r0 = new ArrayList();
                sparseArray.put(i, r0);
            }
            if (!r0.contains(obj)) {
                r0.add(obj);
            }
        }
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgress;
    }

    public void postNotificationName(int i, Object... objArr) {
        boolean z = false;
        if (this.allowedNotifications != null) {
            for (int i2 : this.allowedNotifications) {
                if (i2 == i) {
                    z = true;
                    break;
                }
            }
        }
        postNotificationNameInternal(i, z, objArr);
    }

    public void postNotificationNameInternal(int i, boolean z, Object... objArr) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("postNotificationName allowed only from MAIN thread");
        } else if (z || !this.animationInProgress) {
            int i2;
            this.broadcasting++;
            ArrayList arrayList = (ArrayList) this.observers.get(i);
            if (!(arrayList == null || arrayList.isEmpty())) {
                for (i2 = 0; i2 < arrayList.size(); i2++) {
                    ((NotificationCenterDelegate) arrayList.get(i2)).didReceivedNotification(i, objArr);
                }
            }
            this.broadcasting--;
            if (this.broadcasting == 0) {
                int i3;
                int keyAt;
                if (this.removeAfterBroadcast.size() != 0) {
                    for (i3 = 0; i3 < this.removeAfterBroadcast.size(); i3++) {
                        keyAt = this.removeAfterBroadcast.keyAt(i3);
                        arrayList = (ArrayList) this.removeAfterBroadcast.get(keyAt);
                        for (i2 = 0; i2 < arrayList.size(); i2++) {
                            removeObserver(arrayList.get(i2), keyAt);
                        }
                    }
                    this.removeAfterBroadcast.clear();
                }
                if (this.addAfterBroadcast.size() != 0) {
                    for (i3 = 0; i3 < this.addAfterBroadcast.size(); i3++) {
                        keyAt = this.addAfterBroadcast.keyAt(i3);
                        arrayList = (ArrayList) this.addAfterBroadcast.get(keyAt);
                        for (i2 = 0; i2 < arrayList.size(); i2++) {
                            addObserver(arrayList.get(i2), keyAt);
                        }
                    }
                    this.addAfterBroadcast.clear();
                }
            }
        } else {
            this.delayedPosts.add(new DelayedPost(i, objArr, null));
            if (BuildVars.DEBUG_VERSION) {
                FileLog.m16e("tmessages", "delay post notification " + i + " with args count = " + objArr.length);
            }
        }
    }

    public void removeObserver(Object obj, int i) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("removeObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            r0 = (ArrayList) this.removeAfterBroadcast.get(i);
            if (r0 == null) {
                r0 = new ArrayList();
                this.removeAfterBroadcast.put(i, r0);
            }
            r0.add(obj);
        } else {
            r0 = (ArrayList) this.observers.get(i);
            if (r0 != null) {
                r0.remove(obj);
            }
        }
    }

    public void setAllowedNotificationsDutingAnimation(int[] iArr) {
        this.allowedNotifications = iArr;
    }

    public void setAnimationInProgress(boolean z) {
        this.animationInProgress = z;
        if (!this.animationInProgress && !this.delayedPosts.isEmpty()) {
            Iterator it = this.delayedPosts.iterator();
            while (it.hasNext()) {
                DelayedPost delayedPost = (DelayedPost) it.next();
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            this.delayedPosts.clear();
        }
    }
}

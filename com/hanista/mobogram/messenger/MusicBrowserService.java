package com.hanista.mobogram.messenger;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.media.MediaDescription;
import android.media.MediaMetadata.Builder;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.MediaSession.QueueItem;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.service.media.MediaBrowserService;
import android.service.media.MediaBrowserService.BrowserRoot;
import android.service.media.MediaBrowserService.Result;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import com.hanista.mobogram.SQLite.SQLiteCursor;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.audioinfo.AudioInfo;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.tgnet.AbstractSerializedData;
import com.hanista.mobogram.tgnet.TLObject;
import com.hanista.mobogram.tgnet.TLRPC.Chat;
import com.hanista.mobogram.tgnet.TLRPC.Message;
import com.hanista.mobogram.tgnet.TLRPC.TL_fileLocation;
import com.hanista.mobogram.tgnet.TLRPC.User;
import com.hanista.mobogram.ui.LaunchActivity;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@TargetApi(21)
public class MusicBrowserService extends MediaBrowserService implements NotificationCenterDelegate {
    public static final String ACTION_CMD = "com.example.android.mediabrowserservice.ACTION_CMD";
    private static final String AUTO_APP_PACKAGE_NAME = "com.google.android.projection.gearhead";
    public static final String CMD_NAME = "CMD_NAME";
    public static final String CMD_PAUSE = "CMD_PAUSE";
    private static final String MEDIA_ID_ROOT = "__ROOT__";
    private static final String SLOT_RESERVATION_QUEUE = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE";
    private static final String SLOT_RESERVATION_SKIP_TO_NEXT = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT";
    private static final String SLOT_RESERVATION_SKIP_TO_PREV = "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS";
    private static final int STOP_DELAY = 30000;
    private RectF bitmapRect;
    private HashMap<Integer, Chat> chats;
    private boolean chatsLoaded;
    private DelayedStopHandler delayedStopHandler;
    private ArrayList<Integer> dialogs;
    private int lastSelectedDialog;
    private boolean loadingChats;
    private MediaSession mediaSession;
    private HashMap<Integer, ArrayList<MessageObject>> musicObjects;
    private HashMap<Integer, ArrayList<QueueItem>> musicQueues;
    private Paint roundPaint;
    private boolean serviceStarted;
    private HashMap<Integer, User> users;

    /* renamed from: com.hanista.mobogram.messenger.MusicBrowserService.1 */
    class C05991 implements Runnable {
        final /* synthetic */ String val$parentMediaId;
        final /* synthetic */ Result val$result;

        /* renamed from: com.hanista.mobogram.messenger.MusicBrowserService.1.1 */
        class C05981 implements Runnable {
            C05981() {
            }

            public void run() {
                MusicBrowserService.this.chatsLoaded = true;
                MusicBrowserService.this.loadingChats = false;
                MusicBrowserService.this.loadChildrenImpl(C05991.this.val$parentMediaId, C05991.this.val$result);
                if (MusicBrowserService.this.lastSelectedDialog == 0 && !MusicBrowserService.this.dialogs.isEmpty()) {
                    MusicBrowserService.this.lastSelectedDialog = ((Integer) MusicBrowserService.this.dialogs.get(0)).intValue();
                }
                if (MusicBrowserService.this.lastSelectedDialog != 0) {
                    ArrayList arrayList = (ArrayList) MusicBrowserService.this.musicObjects.get(Integer.valueOf(MusicBrowserService.this.lastSelectedDialog));
                    ArrayList arrayList2 = (ArrayList) MusicBrowserService.this.musicQueues.get(Integer.valueOf(MusicBrowserService.this.lastSelectedDialog));
                    if (!(arrayList == null || arrayList.isEmpty())) {
                        MusicBrowserService.this.mediaSession.setQueue(arrayList2);
                        if (MusicBrowserService.this.lastSelectedDialog > 0) {
                            User user = (User) MusicBrowserService.this.users.get(Integer.valueOf(MusicBrowserService.this.lastSelectedDialog));
                            if (user != null) {
                                MusicBrowserService.this.mediaSession.setQueueTitle(ContactsController.formatName(user.first_name, user.last_name));
                            } else {
                                MusicBrowserService.this.mediaSession.setQueueTitle("DELETED USER");
                            }
                        } else {
                            Chat chat = (Chat) MusicBrowserService.this.chats.get(Integer.valueOf(-MusicBrowserService.this.lastSelectedDialog));
                            if (chat != null) {
                                MusicBrowserService.this.mediaSession.setQueueTitle(chat.title);
                            } else {
                                MusicBrowserService.this.mediaSession.setQueueTitle("DELETED CHAT");
                            }
                        }
                        MessageObject messageObject = (MessageObject) arrayList.get(0);
                        Builder builder = new Builder();
                        builder.putLong("android.media.metadata.DURATION", (long) (messageObject.getDuration() * PointerIconCompat.TYPE_DEFAULT));
                        builder.putString("android.media.metadata.ARTIST", messageObject.getMusicAuthor());
                        builder.putString("android.media.metadata.TITLE", messageObject.getMusicTitle());
                        MusicBrowserService.this.mediaSession.setMetadata(builder.build());
                    }
                }
                MusicBrowserService.this.updatePlaybackState(null);
            }
        }

        C05991(String str, Result result) {
            this.val$parentMediaId = str;
            this.val$result = result;
        }

        public void run() {
            try {
                int longValue;
                Iterable arrayList = new ArrayList();
                Iterable arrayList2 = new ArrayList();
                SQLiteCursor queryFinalized = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT DISTINCT uid FROM media_v2 WHERE uid != 0 AND mid > 0 AND type = %d", new Object[]{Integer.valueOf(4)}), new Object[0]);
                while (queryFinalized.next()) {
                    longValue = (int) queryFinalized.longValue(0);
                    if (longValue != 0) {
                        MusicBrowserService.this.dialogs.add(Integer.valueOf(longValue));
                        if (longValue > 0) {
                            arrayList.add(Integer.valueOf(longValue));
                        } else {
                            arrayList2.add(Integer.valueOf(-longValue));
                        }
                    }
                }
                queryFinalized.dispose();
                if (!MusicBrowserService.this.dialogs.isEmpty()) {
                    String join = TextUtils.join(",", MusicBrowserService.this.dialogs);
                    SQLiteCursor queryFinalized2 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT uid, data, mid FROM media_v2 WHERE uid IN (%s) AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC", new Object[]{join, Integer.valueOf(4)}), new Object[0]);
                    while (queryFinalized2.next()) {
                        AbstractSerializedData byteBufferValue = queryFinalized2.byteBufferValue(1);
                        if (byteBufferValue != null) {
                            Message TLdeserialize = Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                            byteBufferValue.reuse();
                            if (MessageObject.isMusicMessage(TLdeserialize)) {
                                int intValue = queryFinalized2.intValue(0);
                                TLdeserialize.id = queryFinalized2.intValue(2);
                                TLdeserialize.dialog_id = (long) intValue;
                                ArrayList arrayList3 = (ArrayList) MusicBrowserService.this.musicObjects.get(Integer.valueOf(intValue));
                                ArrayList arrayList4 = (ArrayList) MusicBrowserService.this.musicQueues.get(Integer.valueOf(intValue));
                                if (arrayList3 == null) {
                                    arrayList3 = new ArrayList();
                                    MusicBrowserService.this.musicObjects.put(Integer.valueOf(intValue), arrayList3);
                                    arrayList4 = new ArrayList();
                                    MusicBrowserService.this.musicQueues.put(Integer.valueOf(intValue), arrayList4);
                                }
                                MessageObject messageObject = new MessageObject(TLdeserialize, null, false);
                                arrayList3.add(0, messageObject);
                                MediaDescription.Builder mediaId = new MediaDescription.Builder().setMediaId(intValue + "_" + arrayList3.size());
                                mediaId.setTitle(messageObject.getMusicTitle());
                                mediaId.setSubtitle(messageObject.getMusicAuthor());
                                arrayList4.add(0, new QueueItem(mediaId.build(), (long) arrayList4.size()));
                            }
                        }
                    }
                    queryFinalized2.dispose();
                    if (!arrayList.isEmpty()) {
                        ArrayList arrayList5 = new ArrayList();
                        MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", arrayList), arrayList5);
                        for (longValue = 0; longValue < arrayList5.size(); longValue++) {
                            User user = (User) arrayList5.get(longValue);
                            MusicBrowserService.this.users.put(Integer.valueOf(user.id), user);
                        }
                    }
                    if (!arrayList2.isEmpty()) {
                        ArrayList arrayList6 = new ArrayList();
                        MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", arrayList2), arrayList6);
                        for (longValue = 0; longValue < arrayList6.size(); longValue++) {
                            Chat chat = (Chat) arrayList6.get(longValue);
                            MusicBrowserService.this.chats.put(Integer.valueOf(chat.id), chat);
                        }
                    }
                }
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
            }
            AndroidUtilities.runOnUIThread(new C05981());
        }
    }

    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MusicBrowserService> mWeakReference;

        private DelayedStopHandler(MusicBrowserService musicBrowserService) {
            this.mWeakReference = new WeakReference(musicBrowserService);
        }

        public void handleMessage(android.os.Message message) {
            MusicBrowserService musicBrowserService = (MusicBrowserService) this.mWeakReference.get();
            if (musicBrowserService == null) {
                return;
            }
            if (MediaController.m71a().m182j() == null || MediaController.m71a().m191s()) {
                musicBrowserService.stopSelf();
                musicBrowserService.serviceStarted = false;
            }
        }
    }

    private final class MediaSessionCallback extends Callback {
        private MediaSessionCallback() {
        }

        public void onPause() {
            MusicBrowserService.this.handlePauseRequest();
        }

        public void onPlay() {
            MessageObject j = MediaController.m71a().m182j();
            if (j == null) {
                onPlayFromMediaId(MusicBrowserService.this.lastSelectedDialog + "_" + 0, null);
            } else {
                MediaController.m71a().m158a(j);
            }
        }

        public void onPlayFromMediaId(String str, Bundle bundle) {
            String[] split = str.split("_");
            if (split.length == 2) {
                try {
                    int parseInt = Integer.parseInt(split[0]);
                    int parseInt2 = Integer.parseInt(split[1]);
                    ArrayList arrayList = (ArrayList) MusicBrowserService.this.musicObjects.get(Integer.valueOf(parseInt));
                    ArrayList arrayList2 = (ArrayList) MusicBrowserService.this.musicQueues.get(Integer.valueOf(parseInt));
                    if (arrayList != null && parseInt2 >= 0 && parseInt2 < arrayList.size()) {
                        MusicBrowserService.this.lastSelectedDialog = parseInt;
                        ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("auto_lastSelectedDialog", parseInt).commit();
                        MediaController.m71a().m161a(arrayList, (MessageObject) arrayList.get(parseInt2), false);
                        MusicBrowserService.this.mediaSession.setQueue(arrayList2);
                        if (parseInt > 0) {
                            User user = (User) MusicBrowserService.this.users.get(Integer.valueOf(parseInt));
                            if (user != null) {
                                MusicBrowserService.this.mediaSession.setQueueTitle(ContactsController.formatName(user.first_name, user.last_name));
                            } else {
                                MusicBrowserService.this.mediaSession.setQueueTitle("DELETED USER");
                            }
                            MusicBrowserService.this.handlePlayRequest();
                        }
                        Chat chat = (Chat) MusicBrowserService.this.chats.get(Integer.valueOf(-parseInt));
                        if (chat != null) {
                            MusicBrowserService.this.mediaSession.setQueueTitle(chat.title);
                        } else {
                            MusicBrowserService.this.mediaSession.setQueueTitle("DELETED CHAT");
                        }
                        MusicBrowserService.this.handlePlayRequest();
                    }
                } catch (Throwable e) {
                    FileLog.m18e("tmessages", e);
                }
            }
        }

        public void onPlayFromSearch(String str, Bundle bundle) {
            if (str != null && str.length() != 0) {
                CharSequence toLowerCase = str.toLowerCase();
                for (int i = 0; i < MusicBrowserService.this.dialogs.size(); i++) {
                    int intValue = ((Integer) MusicBrowserService.this.dialogs.get(i)).intValue();
                    if (intValue > 0) {
                        User user = (User) MusicBrowserService.this.users.get(Integer.valueOf(intValue));
                        if (user != null && ((user.first_name != null && user.first_name.startsWith(toLowerCase)) || (user.last_name != null && user.last_name.startsWith(toLowerCase)))) {
                            onPlayFromMediaId(intValue + "_" + 0, null);
                            return;
                        }
                    }
                    Chat chat = (Chat) MusicBrowserService.this.chats.get(Integer.valueOf(-intValue));
                    if (!(chat == null || chat.title == null || !chat.title.toLowerCase().contains(toLowerCase))) {
                        onPlayFromMediaId(intValue + "_" + 0, null);
                        return;
                    }
                }
            }
        }

        public void onSeekTo(long j) {
            MessageObject j2 = MediaController.m71a().m182j();
            if (j2 != null) {
                MediaController.m71a().m159a(j2, ((float) (j / 1000)) / ((float) j2.getDuration()));
            }
        }

        public void onSkipToNext() {
            MediaController.m71a().m184l();
        }

        public void onSkipToPrevious() {
            MediaController.m71a().m185m();
        }

        public void onSkipToQueueItem(long j) {
            MediaController.m71a().m168c((int) j);
            MusicBrowserService.this.handlePlayRequest();
        }

        public void onStop() {
            MusicBrowserService.this.handleStopRequest(null);
        }
    }

    public MusicBrowserService() {
        this.dialogs = new ArrayList();
        this.users = new HashMap();
        this.chats = new HashMap();
        this.musicObjects = new HashMap();
        this.musicQueues = new HashMap();
        this.delayedStopHandler = new DelayedStopHandler();
    }

    private Bitmap createRoundBitmap(File file) {
        try {
            Options options = new Options();
            options.inSampleSize = 2;
            Bitmap decodeFile = BitmapFactory.decodeFile(file.toString(), options);
            if (decodeFile != null) {
                Bitmap createBitmap = Bitmap.createBitmap(decodeFile.getWidth(), decodeFile.getHeight(), Config.ARGB_8888);
                createBitmap.eraseColor(0);
                Canvas canvas = new Canvas(createBitmap);
                Shader bitmapShader = new BitmapShader(decodeFile, TileMode.CLAMP, TileMode.CLAMP);
                if (this.roundPaint == null) {
                    this.roundPaint = new Paint(1);
                    this.bitmapRect = new RectF();
                }
                this.roundPaint.setShader(bitmapShader);
                this.bitmapRect.set(0.0f, 0.0f, (float) decodeFile.getWidth(), (float) decodeFile.getHeight());
                canvas.drawRoundRect(this.bitmapRect, (float) decodeFile.getWidth(), (float) decodeFile.getHeight(), this.roundPaint);
                return createBitmap;
            }
        } catch (Throwable th) {
            FileLog.m18e("tmessages", th);
        }
        return null;
    }

    private long getAvailableActions() {
        long j = 3076;
        if (MediaController.m71a().m182j() == null) {
            return 3076;
        }
        if (!MediaController.m71a().m191s()) {
            j = 3076 | 2;
        }
        return (j | 16) | 32;
    }

    private void handlePauseRequest() {
        MediaController.m71a().m166b(MediaController.m71a().m182j());
        this.delayedStopHandler.removeCallbacksAndMessages(null);
        this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000);
    }

    private void handlePlayRequest() {
        this.delayedStopHandler.removeCallbacksAndMessages(null);
        if (!this.serviceStarted) {
            startService(new Intent(getApplicationContext(), MusicBrowserService.class));
            this.serviceStarted = true;
        }
        if (!this.mediaSession.isActive()) {
            this.mediaSession.setActive(true);
        }
        MessageObject j = MediaController.m71a().m182j();
        if (j != null) {
            Builder builder = new Builder();
            builder.putLong("android.media.metadata.DURATION", (long) (j.getDuration() * PointerIconCompat.TYPE_DEFAULT));
            builder.putString("android.media.metadata.ARTIST", j.getMusicAuthor());
            builder.putString("android.media.metadata.TITLE", j.getMusicTitle());
            AudioInfo n = MediaController.m71a().m186n();
            if (n != null) {
                Bitmap cover = n.getCover();
                if (cover != null) {
                    builder.putBitmap("android.media.metadata.ALBUM_ART", cover);
                }
            }
            this.mediaSession.setMetadata(builder.build());
        }
    }

    private void handleStopRequest(String str) {
        this.delayedStopHandler.removeCallbacksAndMessages(null);
        this.delayedStopHandler.sendEmptyMessageDelayed(0, 30000);
        updatePlaybackState(str);
        stopSelf();
        this.serviceStarted = false;
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
    }

    private void loadChildrenImpl(String str, Result<List<MediaItem>> result) {
        int i = 0;
        List arrayList = new ArrayList();
        MediaDescription.Builder mediaId;
        if (MEDIA_ID_ROOT.equals(str)) {
            while (i < this.dialogs.size()) {
                TLObject tLObject;
                int intValue = ((Integer) this.dialogs.get(i)).intValue();
                mediaId = new MediaDescription.Builder().setMediaId("__CHAT_" + intValue);
                if (intValue > 0) {
                    TLObject tLObject2;
                    User user = (User) this.users.get(Integer.valueOf(intValue));
                    if (user != null) {
                        mediaId.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                        if (user.photo != null && (user.photo.photo_small instanceof TL_fileLocation)) {
                            tLObject2 = user.photo.photo_small;
                        }
                        tLObject2 = null;
                    } else {
                        mediaId.setTitle("DELETED USER");
                        tLObject2 = null;
                    }
                    tLObject = tLObject2;
                } else {
                    Chat chat = (Chat) this.chats.get(Integer.valueOf(-intValue));
                    if (chat != null) {
                        mediaId.setTitle(chat.title);
                        if (chat.photo != null && (chat.photo.photo_small instanceof TL_fileLocation)) {
                            tLObject = chat.photo.photo_small;
                        }
                    } else {
                        mediaId.setTitle("DELETED CHAT");
                    }
                    tLObject = null;
                }
                Bitmap createRoundBitmap;
                if (tLObject != null) {
                    createRoundBitmap = createRoundBitmap(FileLoader.getPathToAttach(tLObject, true));
                    if (createRoundBitmap != null) {
                        mediaId.setIconBitmap(createRoundBitmap);
                    }
                } else {
                    createRoundBitmap = null;
                }
                if (tLObject == null || r0 == null) {
                    mediaId.setIconUri(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/drawable/contact_blue"));
                }
                arrayList.add(new MediaItem(mediaId.build(), 1));
                i++;
            }
        } else if (str != null && str.startsWith("__CHAT_")) {
            int parseInt;
            try {
                parseInt = Integer.parseInt(str.replace("__CHAT_", TtmlNode.ANONYMOUS_REGION_ID));
            } catch (Throwable e) {
                FileLog.m18e("tmessages", e);
                parseInt = 0;
            }
            ArrayList arrayList2 = (ArrayList) this.musicObjects.get(Integer.valueOf(parseInt));
            if (arrayList2 != null) {
                for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                    MessageObject messageObject = (MessageObject) arrayList2.get(i2);
                    mediaId = new MediaDescription.Builder().setMediaId(parseInt + "_" + i2);
                    mediaId.setTitle(messageObject.getMusicTitle());
                    mediaId.setSubtitle(messageObject.getMusicAuthor());
                    arrayList.add(new MediaItem(mediaId.build(), 2));
                }
            }
        }
        result.sendResult(arrayList);
    }

    private void updatePlaybackState(String str) {
        long j = -1;
        MessageObject j2 = MediaController.m71a().m182j();
        if (j2 != null) {
            j = (long) (j2.audioProgressSec * PointerIconCompat.TYPE_DEFAULT);
        }
        PlaybackState.Builder actions = new PlaybackState.Builder().setActions(getAvailableActions());
        int i = j2 == null ? 1 : MediaController.m71a().m192t() ? 6 : MediaController.m71a().m191s() ? 2 : 3;
        if (str != null) {
            actions.setErrorMessage(str);
            i = 7;
        }
        actions.setState(i, j, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, SystemClock.elapsedRealtime());
        if (j2 != null) {
            actions.setActiveQueueItemId((long) MediaController.m71a().m183k());
        } else {
            actions.setActiveQueueItemId(0);
        }
        this.mediaSession.setPlaybackState(actions.build());
    }

    public void didReceivedNotification(int i, Object... objArr) {
        updatePlaybackState(null);
        handlePlayRequest();
    }

    public void onCreate() {
        super.onCreate();
        ApplicationLoader.postInitApplication();
        this.lastSelectedDialog = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getInt("auto_lastSelectedDialog", 0);
        this.mediaSession = new MediaSession(this, "MusicService");
        setSessionToken(this.mediaSession.getSessionToken());
        this.mediaSession.setCallback(new MediaSessionCallback());
        this.mediaSession.setFlags(3);
        Context applicationContext = getApplicationContext();
        this.mediaSession.setSessionActivity(PendingIntent.getActivity(applicationContext, 99, new Intent(applicationContext, LaunchActivity.class), C0700C.SAMPLE_FLAG_DECODE_ONLY));
        Bundle bundle = new Bundle();
        bundle.putBoolean(SLOT_RESERVATION_QUEUE, true);
        bundle.putBoolean(SLOT_RESERVATION_SKIP_TO_PREV, true);
        bundle.putBoolean(SLOT_RESERVATION_SKIP_TO_NEXT, true);
        this.mediaSession.setExtras(bundle);
        updatePlaybackState(null);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
    }

    public void onDestroy() {
        handleStopRequest(null);
        this.delayedStopHandler.removeCallbacksAndMessages(null);
        this.mediaSession.release();
    }

    public BrowserRoot onGetRoot(String str, int i, Bundle bundle) {
        return (str == null || !(PointerIconCompat.TYPE_DEFAULT == i || Process.myUid() == i || str.equals("com.google.android.mediasimulator") || str.equals(AUTO_APP_PACKAGE_NAME))) ? null : new BrowserRoot(MEDIA_ID_ROOT, null);
    }

    public void onLoadChildren(String str, Result<List<MediaItem>> result) {
        if (this.chatsLoaded) {
            loadChildrenImpl(str, result);
            return;
        }
        result.detach();
        if (!this.loadingChats) {
            this.loadingChats = true;
            MessagesStorage.getInstance().getStorageQueue().postRunnable(new C05991(str, result));
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }
}

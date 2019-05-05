package com.hanista.mobogram.messenger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.NotificationCenter.NotificationCenterDelegate;
import com.hanista.mobogram.messenger.audioinfo.AudioInfo;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.LaunchActivity;

public class MusicPlayerService extends Service implements NotificationCenterDelegate {
    public static final String NOTIFY_CLOSE = "com.hanista.mobogram.android.musicplayer.close";
    public static final String NOTIFY_NEXT = "com.hanista.mobogram.android.musicplayer.next";
    public static final String NOTIFY_PAUSE = "com.hanista.mobogram.android.musicplayer.pause";
    public static final String NOTIFY_PLAY = "com.hanista.mobogram.android.musicplayer.play";
    public static final String NOTIFY_PREVIOUS = "com.hanista.mobogram.android.musicplayer.previous";
    private static boolean supportBigNotifications;
    private static boolean supportLockScreenControls;
    private AudioManager audioManager;
    private RemoteControlClient remoteControlClient;

    /* renamed from: com.hanista.mobogram.messenger.MusicPlayerService.1 */
    class C06001 implements Runnable {
        C06001() {
        }

        public void run() {
            MusicPlayerService.this.stopSelf();
        }
    }

    static {
        boolean z = true;
        supportBigNotifications = VERSION.SDK_INT >= 16;
        if (VERSION.SDK_INT < 14) {
            z = false;
        }
        supportLockScreenControls = z;
    }

    @SuppressLint({"NewApi"})
    private void createNotification(MessageObject messageObject) {
        Object musicTitle = messageObject.getMusicTitle();
        Object musicAuthor = messageObject.getMusicAuthor();
        AudioInfo n = MediaController.m71a().m186n();
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), C0338R.layout.player_small_notification);
        RemoteViews remoteViews2 = null;
        if (supportBigNotifications) {
            remoteViews2 = new RemoteViews(getApplicationContext().getPackageName(), C0338R.layout.player_big_notification);
        }
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openplayer");
        intent.setFlags(TLRPC.MESSAGE_FLAG_EDITED);
        Notification build = new Builder(getApplicationContext()).setSmallIcon(C0338R.drawable.player).setContentIntent(PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 0)).setContentTitle(musicTitle).build();
        build.contentView = remoteViews;
        if (supportBigNotifications) {
            build.bigContentView = remoteViews2;
        }
        setListeners(remoteViews);
        if (supportBigNotifications) {
            setListeners(remoteViews2);
        }
        Bitmap smallCover = n != null ? n.getSmallCover() : null;
        if (smallCover != null) {
            build.contentView.setImageViewBitmap(C0338R.id.player_album_art, smallCover);
            if (supportBigNotifications) {
                build.bigContentView.setImageViewBitmap(C0338R.id.player_album_art, smallCover);
            }
        } else {
            build.contentView.setImageViewResource(C0338R.id.player_album_art, C0338R.drawable.nocover_small);
            if (supportBigNotifications) {
                build.bigContentView.setImageViewResource(C0338R.id.player_album_art, C0338R.drawable.nocover_big);
            }
        }
        if (MediaController.m71a().m192t()) {
            build.contentView.setViewVisibility(C0338R.id.player_pause, 8);
            build.contentView.setViewVisibility(C0338R.id.player_play, 8);
            build.contentView.setViewVisibility(C0338R.id.player_next, 8);
            build.contentView.setViewVisibility(C0338R.id.player_previous, 8);
            build.contentView.setViewVisibility(C0338R.id.player_progress_bar, 0);
            if (supportBigNotifications) {
                build.bigContentView.setViewVisibility(C0338R.id.player_pause, 8);
                build.bigContentView.setViewVisibility(C0338R.id.player_play, 8);
                build.bigContentView.setViewVisibility(C0338R.id.player_next, 8);
                build.bigContentView.setViewVisibility(C0338R.id.player_previous, 8);
                build.bigContentView.setViewVisibility(C0338R.id.player_progress_bar, 0);
            }
        } else {
            build.contentView.setViewVisibility(C0338R.id.player_progress_bar, 8);
            build.contentView.setViewVisibility(C0338R.id.player_next, 0);
            build.contentView.setViewVisibility(C0338R.id.player_previous, 0);
            if (supportBigNotifications) {
                build.bigContentView.setViewVisibility(C0338R.id.player_next, 0);
                build.bigContentView.setViewVisibility(C0338R.id.player_previous, 0);
                build.bigContentView.setViewVisibility(C0338R.id.player_progress_bar, 8);
            }
            if (MediaController.m71a().m191s()) {
                build.contentView.setViewVisibility(C0338R.id.player_pause, 8);
                build.contentView.setViewVisibility(C0338R.id.player_play, 0);
                if (supportBigNotifications) {
                    build.bigContentView.setViewVisibility(C0338R.id.player_pause, 8);
                    build.bigContentView.setViewVisibility(C0338R.id.player_play, 0);
                }
            } else {
                build.contentView.setViewVisibility(C0338R.id.player_pause, 0);
                build.contentView.setViewVisibility(C0338R.id.player_play, 8);
                if (supportBigNotifications) {
                    build.bigContentView.setViewVisibility(C0338R.id.player_pause, 0);
                    build.bigContentView.setViewVisibility(C0338R.id.player_play, 8);
                }
            }
        }
        build.contentView.setTextViewText(C0338R.id.player_song_name, musicTitle);
        build.contentView.setTextViewText(C0338R.id.player_author_name, musicAuthor);
        if (supportBigNotifications) {
            build.bigContentView.setTextViewText(C0338R.id.player_song_name, musicTitle);
            build.bigContentView.setTextViewText(C0338R.id.player_author_name, musicAuthor);
        }
        build.flags |= 2;
        startForeground(5, build);
        if (this.remoteControlClient != null) {
            MetadataEditor editMetadata = this.remoteControlClient.editMetadata(true);
            editMetadata.putString(2, musicAuthor);
            editMetadata.putString(7, musicTitle);
            if (!(n == null || n.getCover() == null)) {
                try {
                    editMetadata.putBitmap(100, n.getCover());
                } catch (Throwable th) {
                    FileLog.m18e("tmessages", th);
                }
            }
            editMetadata.apply();
        }
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (i == NotificationCenter.audioPlayStateChanged) {
            MessageObject j = MediaController.m71a().m182j();
            if (j != null) {
                createNotification(j);
            } else {
                stopSelf();
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        this.audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        super.onCreate();
    }

    @SuppressLint({"NewApi"})
    public void onDestroy() {
        super.onDestroy();
        if (this.remoteControlClient != null) {
            MetadataEditor editMetadata = this.remoteControlClient.editMetadata(true);
            editMetadata.clear();
            editMetadata.apply();
            this.audioManager.unregisterRemoteControlClient(this.remoteControlClient);
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
    }

    @SuppressLint({"NewApi"})
    public int onStartCommand(Intent intent, int i, int i2) {
        try {
            MessageObject j = MediaController.m71a().m182j();
            if (j == null) {
                AndroidUtilities.runOnUIThread(new C06001());
            } else {
                if (supportLockScreenControls) {
                    ComponentName componentName = new ComponentName(getApplicationContext(), MusicPlayerReceiver.class.getName());
                    try {
                        if (this.remoteControlClient == null) {
                            this.audioManager.registerMediaButtonEventReceiver(componentName);
                            Intent intent2 = new Intent("android.intent.action.MEDIA_BUTTON");
                            intent2.setComponent(componentName);
                            this.remoteControlClient = new RemoteControlClient(PendingIntent.getBroadcast(this, 0, intent2, 0));
                            this.audioManager.registerRemoteControlClient(this.remoteControlClient);
                        }
                        this.remoteControlClient.setTransportControlFlags(PsExtractor.PRIVATE_STREAM_1);
                    } catch (Throwable e) {
                        FileLog.m18e("tmessages", e);
                    }
                }
                createNotification(j);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return 1;
    }

    public void setListeners(RemoteViews remoteViews) {
        remoteViews.setOnClickPendingIntent(C0338R.id.player_previous, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PREVIOUS), C0700C.SAMPLE_FLAG_DECODE_ONLY));
        remoteViews.setOnClickPendingIntent(C0338R.id.player_close, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_CLOSE), C0700C.SAMPLE_FLAG_DECODE_ONLY));
        remoteViews.setOnClickPendingIntent(C0338R.id.player_pause, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PAUSE), C0700C.SAMPLE_FLAG_DECODE_ONLY));
        remoteViews.setOnClickPendingIntent(C0338R.id.player_next, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_NEXT), C0700C.SAMPLE_FLAG_DECODE_ONLY));
        remoteViews.setOnClickPendingIntent(C0338R.id.player_play, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PLAY), C0700C.SAMPLE_FLAG_DECODE_ONLY));
    }
}

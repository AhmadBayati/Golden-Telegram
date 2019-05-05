package com.hanista.mobogram.messenger.exoplayer;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.hanista.mobogram.messenger.exoplayer.ExoPlayer.ExoPlayerComponent;
import com.hanista.mobogram.messenger.exoplayer.ExoPlayer.Listener;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

final class ExoPlayerImpl implements ExoPlayer {
    private static final String TAG = "ExoPlayerImpl";
    private final Handler eventHandler;
    private final ExoPlayerImplInternal internalPlayer;
    private final CopyOnWriteArraySet<Listener> listeners;
    private int pendingPlayWhenReadyAcks;
    private boolean playWhenReady;
    private int playbackState;
    private final int[] selectedTrackIndices;
    private final MediaFormat[][] trackFormats;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.ExoPlayerImpl.1 */
    class C07021 extends Handler {
        C07021() {
        }

        public void handleMessage(Message message) {
            ExoPlayerImpl.this.handleEvent(message);
        }
    }

    @SuppressLint({"HandlerLeak"})
    public ExoPlayerImpl(int i, int i2, int i3) {
        Log.i(TAG, "Init 1.5.10");
        this.playWhenReady = false;
        this.playbackState = 1;
        this.listeners = new CopyOnWriteArraySet();
        this.trackFormats = new MediaFormat[i][];
        this.selectedTrackIndices = new int[i];
        this.eventHandler = new C07021();
        this.internalPlayer = new ExoPlayerImplInternal(this.eventHandler, this.playWhenReady, this.selectedTrackIndices, i2, i3);
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void blockingSendMessage(ExoPlayerComponent exoPlayerComponent, int i, Object obj) {
        this.internalPlayer.blockingSendMessage(exoPlayerComponent, i, obj);
    }

    public int getBufferedPercentage() {
        long j = 100;
        long bufferedPosition = getBufferedPosition();
        long duration = getDuration();
        if (bufferedPosition == -1 || duration == -1) {
            return 0;
        }
        if (duration != 0) {
            j = (100 * bufferedPosition) / duration;
        }
        return (int) j;
    }

    public long getBufferedPosition() {
        return this.internalPlayer.getBufferedPosition();
    }

    public long getCurrentPosition() {
        return this.internalPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return this.internalPlayer.getDuration();
    }

    public boolean getPlayWhenReady() {
        return this.playWhenReady;
    }

    public Looper getPlaybackLooper() {
        return this.internalPlayer.getPlaybackLooper();
    }

    public int getPlaybackState() {
        return this.playbackState;
    }

    public int getSelectedTrack(int i) {
        return this.selectedTrackIndices[i];
    }

    public int getTrackCount(int i) {
        return this.trackFormats[i] != null ? this.trackFormats[i].length : 0;
    }

    public MediaFormat getTrackFormat(int i, int i2) {
        return this.trackFormats[i][i2];
    }

    void handleEvent(Message message) {
        Iterator it;
        switch (message.what) {
            case VideoPlayer.TYPE_AUDIO /*1*/:
                System.arraycopy(message.obj, 0, this.trackFormats, 0, this.trackFormats.length);
                this.playbackState = message.arg1;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((Listener) it.next()).onPlayerStateChanged(this.playWhenReady, this.playbackState);
                }
            case VideoPlayer.STATE_PREPARING /*2*/:
                this.playbackState = message.arg1;
                it = this.listeners.iterator();
                while (it.hasNext()) {
                    ((Listener) it.next()).onPlayerStateChanged(this.playWhenReady, this.playbackState);
                }
            case VideoPlayer.STATE_BUFFERING /*3*/:
                this.pendingPlayWhenReadyAcks--;
                if (this.pendingPlayWhenReadyAcks == 0) {
                    it = this.listeners.iterator();
                    while (it.hasNext()) {
                        ((Listener) it.next()).onPlayWhenReadyCommitted();
                    }
                }
            case VideoPlayer.STATE_READY /*4*/:
                ExoPlaybackException exoPlaybackException = (ExoPlaybackException) message.obj;
                Iterator it2 = this.listeners.iterator();
                while (it2.hasNext()) {
                    ((Listener) it2.next()).onPlayerError(exoPlaybackException);
                }
            default:
        }
    }

    public boolean isPlayWhenReadyCommitted() {
        return this.pendingPlayWhenReadyAcks == 0;
    }

    public void prepare(TrackRenderer... trackRendererArr) {
        Arrays.fill(this.trackFormats, null);
        this.internalPlayer.prepare(trackRendererArr);
    }

    public void release() {
        this.internalPlayer.release();
        this.eventHandler.removeCallbacksAndMessages(null);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void seekTo(long j) {
        this.internalPlayer.seekTo(j);
    }

    public void sendMessage(ExoPlayerComponent exoPlayerComponent, int i, Object obj) {
        this.internalPlayer.sendMessage(exoPlayerComponent, i, obj);
    }

    public void setPlayWhenReady(boolean z) {
        if (this.playWhenReady != z) {
            this.playWhenReady = z;
            this.pendingPlayWhenReadyAcks++;
            this.internalPlayer.setPlayWhenReady(z);
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((Listener) it.next()).onPlayerStateChanged(z, this.playbackState);
            }
        }
    }

    public void setSelectedTrack(int i, int i2) {
        if (this.selectedTrackIndices[i] != i2) {
            this.selectedTrackIndices[i] = i2;
            this.internalPlayer.setRendererSelectedTrack(i, i2);
        }
    }

    public void stop() {
        this.internalPlayer.stop();
    }
}

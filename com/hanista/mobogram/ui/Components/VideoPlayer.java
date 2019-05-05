package com.hanista.mobogram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaCodec.CryptoException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PointerIconCompat;
import android.view.Surface;
import com.hanista.mobogram.messenger.exoplayer.DummyTrackRenderer;
import com.hanista.mobogram.messenger.exoplayer.ExoPlaybackException;
import com.hanista.mobogram.messenger.exoplayer.ExoPlayer;
import com.hanista.mobogram.messenger.exoplayer.ExoPlayer.Factory;
import com.hanista.mobogram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import com.hanista.mobogram.messenger.exoplayer.MediaCodecSelector;
import com.hanista.mobogram.messenger.exoplayer.MediaCodecTrackRenderer.DecoderInitializationException;
import com.hanista.mobogram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import com.hanista.mobogram.messenger.exoplayer.MediaCodecVideoTrackRenderer.EventListener;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.SampleSource;
import com.hanista.mobogram.messenger.exoplayer.TrackRenderer;
import com.hanista.mobogram.messenger.exoplayer.audio.AudioCapabilities;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorSampleSource;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.Allocator;
import com.hanista.mobogram.messenger.exoplayer.upstream.DefaultAllocator;
import com.hanista.mobogram.messenger.exoplayer.upstream.DefaultUriDataSource;
import com.hanista.mobogram.messenger.exoplayer.util.PlayerControl;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressLint({"NewApi"})
public class VideoPlayer implements com.hanista.mobogram.messenger.exoplayer.ExoPlayer.Listener, EventListener {
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    public static final int RENDERER_COUNT = 2;
    public static final int STATE_BUFFERING = 3;
    public static final int STATE_ENDED = 5;
    public static final int STATE_IDLE = 1;
    public static final int STATE_PREPARING = 2;
    public static final int STATE_READY = 4;
    public static final int TRACK_DEFAULT = 0;
    public static final int TRACK_DISABLED = -1;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 0;
    private TrackRenderer audioRenderer;
    private boolean backgrounded;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState;
    private final CopyOnWriteArrayList<Listener> listeners;
    private final Handler mainHandler;
    private final ExoPlayer player;
    private final PlayerControl playerControl;
    private final RendererBuilder rendererBuilder;
    private int rendererBuildingState;
    private Surface surface;
    private TrackRenderer videoRenderer;
    private int videoTrackToRestore;

    public interface RendererBuilder {
        void buildRenderers(VideoPlayer videoPlayer);

        void cancel();
    }

    public static class ExtractorRendererBuilder implements RendererBuilder {
        private static final int BUFFER_SEGMENT_COUNT = 256;
        private static final int BUFFER_SEGMENT_SIZE = 262144;
        private final Context context;
        private final Uri uri;
        private final String userAgent;

        /* renamed from: com.hanista.mobogram.ui.Components.VideoPlayer.ExtractorRendererBuilder.1 */
        class C14971 extends MediaCodecVideoTrackRenderer {
            C14971(Context context, SampleSource sampleSource, MediaCodecSelector mediaCodecSelector, int i, long j, Handler handler, EventListener eventListener, int i2) {
                super(context, sampleSource, mediaCodecSelector, i, j, handler, eventListener, i2);
            }

            protected void doSomeWork(long j, long j2, boolean z) {
                super.doSomeWork(j, j2, z);
            }
        }

        public ExtractorRendererBuilder(Context context, String str, Uri uri) {
            this.context = context;
            this.userAgent = str;
            this.uri = uri;
        }

        public void buildRenderers(VideoPlayer videoPlayer) {
            Allocator defaultAllocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
            Handler mainHandler = videoPlayer.getMainHandler();
            TrackRenderer[] trackRendererArr = new TrackRenderer[VideoPlayer.STATE_PREPARING];
            ExtractorSampleSource extractorSampleSource = new ExtractorSampleSource(this.uri, new DefaultUriDataSource(this.context, this.userAgent), defaultAllocator, 67108864, mainHandler, null, VideoPlayer.TRACK_DEFAULT, new Extractor[VideoPlayer.TRACK_DEFAULT]);
            trackRendererArr[VideoPlayer.TRACK_DEFAULT] = new C14971(this.context, extractorSampleSource, MediaCodecSelector.DEFAULT, VideoPlayer.TYPE_AUDIO, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS, mainHandler, videoPlayer, 50);
            trackRendererArr[VideoPlayer.TYPE_AUDIO] = new MediaCodecAudioTrackRenderer((SampleSource) extractorSampleSource, MediaCodecSelector.DEFAULT, null, true, mainHandler, null, AudioCapabilities.getCapabilities(this.context), (int) VideoPlayer.STATE_BUFFERING);
            videoPlayer.onRenderers(trackRendererArr);
        }

        public void cancel() {
        }
    }

    public interface Listener {
        void onError(Exception exception);

        void onStateChanged(boolean z, int i);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    public VideoPlayer(RendererBuilder rendererBuilder) {
        this.rendererBuilder = rendererBuilder;
        this.player = Factory.newInstance(STATE_PREPARING, PointerIconCompat.TYPE_DEFAULT, Factory.DEFAULT_MIN_REBUFFER_MS);
        this.player.addListener(this);
        this.playerControl = new PlayerControl(this.player);
        this.mainHandler = new Handler();
        this.listeners = new CopyOnWriteArrayList();
        this.lastReportedPlaybackState = TYPE_AUDIO;
        this.rendererBuildingState = TYPE_AUDIO;
    }

    private void maybeReportPlayerState() {
        boolean playWhenReady = this.player.getPlayWhenReady();
        int playbackState = getPlaybackState();
        if (this.lastReportedPlayWhenReady != playWhenReady || this.lastReportedPlaybackState != playbackState) {
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((Listener) it.next()).onStateChanged(playWhenReady, playbackState);
            }
            this.lastReportedPlayWhenReady = playWhenReady;
            this.lastReportedPlaybackState = playbackState;
        }
    }

    private void pushSurface(boolean z) {
        if (this.videoRenderer != null) {
            if (z) {
                this.player.blockingSendMessage(this.videoRenderer, TYPE_AUDIO, this.surface);
            } else {
                this.player.sendMessage(this.videoRenderer, TYPE_AUDIO, this.surface);
            }
        }
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void blockingClearSurface() {
        this.surface = null;
        pushSurface(true);
    }

    public boolean getBackgrounded() {
        return this.backgrounded;
    }

    public int getBufferedPercentage() {
        return this.player.getBufferedPercentage();
    }

    public long getCurrentPosition() {
        return this.player.getCurrentPosition();
    }

    public long getDuration() {
        return this.player.getDuration();
    }

    Handler getMainHandler() {
        return this.mainHandler;
    }

    public boolean getPlayWhenReady() {
        return this.player.getPlayWhenReady();
    }

    Looper getPlaybackLooper() {
        return this.player.getPlaybackLooper();
    }

    public int getPlaybackState() {
        if (this.rendererBuildingState == STATE_PREPARING) {
            return STATE_PREPARING;
        }
        int playbackState = this.player.getPlaybackState();
        return (this.rendererBuildingState == STATE_BUFFERING && playbackState == TYPE_AUDIO) ? STATE_PREPARING : playbackState;
    }

    public PlayerControl getPlayerControl() {
        return this.playerControl;
    }

    public int getSelectedTrack(int i) {
        return this.player.getSelectedTrack(i);
    }

    public Surface getSurface() {
        return this.surface;
    }

    public int getTrackCount(int i) {
        return this.player.getTrackCount(i);
    }

    public MediaFormat getTrackFormat(int i, int i2) {
        return this.player.getTrackFormat(i, i2);
    }

    public void onCryptoError(CryptoException cryptoException) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onError(cryptoException);
        }
    }

    public void onDecoderInitializationError(DecoderInitializationException decoderInitializationException) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onError(decoderInitializationException);
        }
    }

    public void onDecoderInitialized(String str, long j, long j2) {
    }

    public void onDrawnToSurface(Surface surface) {
    }

    public void onDroppedFrames(int i, long j) {
    }

    public void onPlayWhenReadyCommitted() {
    }

    public void onPlayerError(ExoPlaybackException exoPlaybackException) {
        this.rendererBuildingState = TYPE_AUDIO;
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onError(exoPlaybackException);
        }
    }

    public void onPlayerStateChanged(boolean z, int i) {
        maybeReportPlayerState();
    }

    void onRenderers(TrackRenderer[] trackRendererArr) {
        for (int i = TRACK_DEFAULT; i < STATE_PREPARING; i += TYPE_AUDIO) {
            if (trackRendererArr[i] == null) {
                trackRendererArr[i] = new DummyTrackRenderer();
            }
        }
        this.videoRenderer = trackRendererArr[TRACK_DEFAULT];
        this.audioRenderer = trackRendererArr[TYPE_AUDIO];
        pushSurface(false);
        this.player.prepare(trackRendererArr);
        this.rendererBuildingState = STATE_BUFFERING;
    }

    void onRenderersError(Exception exception) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onError(exception);
        }
        this.rendererBuildingState = TYPE_AUDIO;
        maybeReportPlayerState();
    }

    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onVideoSizeChanged(i, i2, i3, f);
        }
    }

    public void prepare() {
        if (this.rendererBuildingState == STATE_BUFFERING) {
            this.player.stop();
        }
        this.rendererBuilder.cancel();
        this.videoRenderer = null;
        this.audioRenderer = null;
        this.rendererBuildingState = STATE_PREPARING;
        maybeReportPlayerState();
        this.rendererBuilder.buildRenderers(this);
    }

    public void release() {
        this.rendererBuilder.cancel();
        this.rendererBuildingState = TYPE_AUDIO;
        this.surface = null;
        this.player.release();
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void seekTo(long j) {
        this.player.seekTo(j);
    }

    public void setBackgrounded(boolean z) {
        if (this.backgrounded != z) {
            this.backgrounded = z;
            if (z) {
                this.videoTrackToRestore = getSelectedTrack(TRACK_DEFAULT);
                setSelectedTrack(TRACK_DEFAULT, TRACK_DISABLED);
                blockingClearSurface();
                return;
            }
            setSelectedTrack(TRACK_DEFAULT, this.videoTrackToRestore);
        }
    }

    public void setMute(boolean z) {
        if (this.audioRenderer != null) {
            if (z) {
                this.player.sendMessage(this.audioRenderer, TYPE_AUDIO, Float.valueOf(0.0f));
            } else {
                this.player.sendMessage(this.audioRenderer, TYPE_AUDIO, Float.valueOf(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        }
    }

    public void setPlayWhenReady(boolean z) {
        this.player.setPlayWhenReady(z);
    }

    public void setSelectedTrack(int i, int i2) {
        this.player.setSelectedTrack(i, i2);
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
        pushSurface(false);
    }
}

package com.hanista.mobogram.messenger.exoplayer.audio;

import android.annotation.TargetApi;
import android.media.AudioTimestamp;
import android.media.PlaybackParams;
import android.os.ConditionVariable;
import android.os.SystemClock;
import android.util.Log;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.util.Ac3Util;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.DtsUtil;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.messenger.volley.Request;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public final class AudioTrack {
    private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
    public static final long CURRENT_POSITION_NOT_SET = Long.MIN_VALUE;
    private static final long MAX_AUDIO_TIMESTAMP_OFFSET_US = 5000000;
    private static final long MAX_BUFFER_DURATION_US = 750000;
    private static final long MAX_LATENCY_US = 5000000;
    private static final int MAX_PLAYHEAD_OFFSET_COUNT = 10;
    private static final long MIN_BUFFER_DURATION_US = 250000;
    private static final int MIN_PLAYHEAD_OFFSET_SAMPLE_INTERVAL_US = 30000;
    private static final int MIN_TIMESTAMP_SAMPLE_INTERVAL_US = 500000;
    private static final long PASSTHROUGH_BUFFER_DURATION_US = 250000;
    public static final int RESULT_BUFFER_CONSUMED = 2;
    public static final int RESULT_POSITION_DISCONTINUITY = 1;
    public static final int SESSION_ID_NOT_SET = 0;
    private static final int START_IN_SYNC = 1;
    private static final int START_NEED_SYNC = 2;
    private static final int START_NOT_SET = 0;
    private static final String TAG = "AudioTrack";
    public static boolean enablePreV21AudioSessionWorkaround;
    public static boolean failOnSpuriousAudioTimestamp;
    private final AudioCapabilities audioCapabilities;
    private boolean audioTimestampSet;
    private android.media.AudioTrack audioTrack;
    private final AudioTrackUtil audioTrackUtil;
    private int bufferBytesRemaining;
    private int bufferSize;
    private long bufferSizeUs;
    private int channelConfig;
    private int framesPerEncodedSample;
    private Method getLatencyMethod;
    private android.media.AudioTrack keepSessionIdAudioTrack;
    private long lastPlayheadSampleTimeUs;
    private long lastTimestampSampleTimeUs;
    private long latencyUs;
    private int nextPlayheadOffsetIndex;
    private boolean passthrough;
    private int pcmFrameSize;
    private int playheadOffsetCount;
    private final long[] playheadOffsets;
    private final ConditionVariable releasingConditionVariable;
    private ByteBuffer resampledBuffer;
    private long resumeSystemTimeUs;
    private int sampleRate;
    private long smoothedPlayheadOffsetUs;
    private int sourceEncoding;
    private int startMediaTimeState;
    private long startMediaTimeUs;
    private final int streamType;
    private long submittedEncodedFrames;
    private long submittedPcmBytes;
    private int targetEncoding;
    private byte[] temporaryBuffer;
    private int temporaryBufferOffset;
    private boolean useResampledBuffer;
    private float volume;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.audio.AudioTrack.1 */
    class C07171 extends Thread {
        final /* synthetic */ android.media.AudioTrack val$toRelease;

        C07171(android.media.AudioTrack audioTrack) {
            this.val$toRelease = audioTrack;
        }

        public void run() {
            try {
                this.val$toRelease.flush();
                this.val$toRelease.release();
            } finally {
                AudioTrack.this.releasingConditionVariable.open();
            }
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.audio.AudioTrack.2 */
    class C07182 extends Thread {
        final /* synthetic */ android.media.AudioTrack val$toRelease;

        C07182(android.media.AudioTrack audioTrack) {
            this.val$toRelease = audioTrack;
        }

        public void run() {
            this.val$toRelease.release();
        }
    }

    private static class AudioTrackUtil {
        protected android.media.AudioTrack audioTrack;
        private long endPlaybackHeadPosition;
        private long lastRawPlaybackHeadPosition;
        private boolean needsPassthroughWorkaround;
        private long passthroughWorkaroundPauseOffset;
        private long rawPlaybackHeadWrapCount;
        private int sampleRate;
        private long stopPlaybackHeadPosition;
        private long stopTimestampUs;

        private AudioTrackUtil() {
        }

        public long getPlaybackHeadPosition() {
            if (this.stopTimestampUs != -1) {
                return Math.min(this.endPlaybackHeadPosition, ((((SystemClock.elapsedRealtime() * 1000) - this.stopTimestampUs) * ((long) this.sampleRate)) / C0700C.MICROS_PER_SECOND) + this.stopPlaybackHeadPosition);
            }
            int playState = this.audioTrack.getPlayState();
            if (playState == AudioTrack.START_IN_SYNC) {
                return 0;
            }
            long playbackHeadPosition = 4294967295L & ((long) this.audioTrack.getPlaybackHeadPosition());
            if (this.needsPassthroughWorkaround) {
                if (playState == AudioTrack.START_NEED_SYNC && playbackHeadPosition == 0) {
                    this.passthroughWorkaroundPauseOffset = this.lastRawPlaybackHeadPosition;
                }
                playbackHeadPosition += this.passthroughWorkaroundPauseOffset;
            }
            if (this.lastRawPlaybackHeadPosition > playbackHeadPosition) {
                this.rawPlaybackHeadWrapCount++;
            }
            this.lastRawPlaybackHeadPosition = playbackHeadPosition;
            return playbackHeadPosition + (this.rawPlaybackHeadWrapCount << 32);
        }

        public long getPlaybackHeadPositionUs() {
            return (getPlaybackHeadPosition() * C0700C.MICROS_PER_SECOND) / ((long) this.sampleRate);
        }

        public float getPlaybackSpeed() {
            return DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }

        public long getTimestampFramePosition() {
            throw new UnsupportedOperationException();
        }

        public long getTimestampNanoTime() {
            throw new UnsupportedOperationException();
        }

        public void handleEndOfStream(long j) {
            this.stopPlaybackHeadPosition = getPlaybackHeadPosition();
            this.stopTimestampUs = SystemClock.elapsedRealtime() * 1000;
            this.endPlaybackHeadPosition = j;
            this.audioTrack.stop();
        }

        public void pause() {
            if (this.stopTimestampUs == -1) {
                this.audioTrack.pause();
            }
        }

        public void reconfigure(android.media.AudioTrack audioTrack, boolean z) {
            this.audioTrack = audioTrack;
            this.needsPassthroughWorkaround = z;
            this.stopTimestampUs = -1;
            this.lastRawPlaybackHeadPosition = 0;
            this.rawPlaybackHeadWrapCount = 0;
            this.passthroughWorkaroundPauseOffset = 0;
            if (audioTrack != null) {
                this.sampleRate = audioTrack.getSampleRate();
            }
        }

        public void setPlaybackParameters(PlaybackParams playbackParams) {
            throw new UnsupportedOperationException();
        }

        public boolean updateTimestamp() {
            return false;
        }
    }

    @TargetApi(19)
    private static class AudioTrackUtilV19 extends AudioTrackUtil {
        private final AudioTimestamp audioTimestamp;
        private long lastRawTimestampFramePosition;
        private long lastTimestampFramePosition;
        private long rawTimestampFramePositionWrapCount;

        public AudioTrackUtilV19() {
            super();
            this.audioTimestamp = new AudioTimestamp();
        }

        public long getTimestampFramePosition() {
            return this.lastTimestampFramePosition;
        }

        public long getTimestampNanoTime() {
            return this.audioTimestamp.nanoTime;
        }

        public void reconfigure(android.media.AudioTrack audioTrack, boolean z) {
            super.reconfigure(audioTrack, z);
            this.rawTimestampFramePositionWrapCount = 0;
            this.lastRawTimestampFramePosition = 0;
            this.lastTimestampFramePosition = 0;
        }

        public boolean updateTimestamp() {
            boolean timestamp = this.audioTrack.getTimestamp(this.audioTimestamp);
            if (timestamp) {
                long j = this.audioTimestamp.framePosition;
                if (this.lastRawTimestampFramePosition > j) {
                    this.rawTimestampFramePositionWrapCount++;
                }
                this.lastRawTimestampFramePosition = j;
                this.lastTimestampFramePosition = j + (this.rawTimestampFramePositionWrapCount << 32);
            }
            return timestamp;
        }
    }

    @TargetApi(23)
    private static class AudioTrackUtilV23 extends AudioTrackUtilV19 {
        private PlaybackParams playbackParams;
        private float playbackSpeed;

        public AudioTrackUtilV23() {
            this.playbackSpeed = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        }

        private void maybeApplyPlaybackParams() {
            if (this.audioTrack != null && this.playbackParams != null) {
                this.audioTrack.setPlaybackParams(this.playbackParams);
            }
        }

        public float getPlaybackSpeed() {
            return this.playbackSpeed;
        }

        public void reconfigure(android.media.AudioTrack audioTrack, boolean z) {
            super.reconfigure(audioTrack, z);
            maybeApplyPlaybackParams();
        }

        public void setPlaybackParameters(PlaybackParams playbackParams) {
            if (playbackParams == null) {
                playbackParams = new PlaybackParams();
            }
            PlaybackParams allowDefaults = playbackParams.allowDefaults();
            this.playbackParams = allowDefaults;
            this.playbackSpeed = allowDefaults.getSpeed();
            maybeApplyPlaybackParams();
        }
    }

    public static final class InitializationException extends Exception {
        public final int audioTrackState;

        public InitializationException(int i, int i2, int i3, int i4) {
            super("AudioTrack init failed: " + i + ", Config(" + i2 + ", " + i3 + ", " + i4 + ")");
            this.audioTrackState = i;
        }
    }

    public static final class InvalidAudioTrackTimestampException extends RuntimeException {
        public InvalidAudioTrackTimestampException(String str) {
            super(str);
        }
    }

    public static final class WriteException extends Exception {
        public final int errorCode;

        public WriteException(int i) {
            super("AudioTrack write failed: " + i);
            this.errorCode = i;
        }
    }

    static {
        enablePreV21AudioSessionWorkaround = false;
        failOnSpuriousAudioTimestamp = false;
    }

    public AudioTrack() {
        this(null, 3);
    }

    public AudioTrack(AudioCapabilities audioCapabilities, int i) {
        this.audioCapabilities = audioCapabilities;
        this.streamType = i;
        this.releasingConditionVariable = new ConditionVariable(true);
        if (Util.SDK_INT >= 18) {
            try {
                this.getLatencyMethod = android.media.AudioTrack.class.getMethod("getLatency", (Class[]) null);
            } catch (NoSuchMethodException e) {
            }
        }
        if (Util.SDK_INT >= 23) {
            this.audioTrackUtil = new AudioTrackUtilV23();
        } else if (Util.SDK_INT >= 19) {
            this.audioTrackUtil = new AudioTrackUtilV19();
        } else {
            this.audioTrackUtil = new AudioTrackUtil();
        }
        this.playheadOffsets = new long[MAX_PLAYHEAD_OFFSET_COUNT];
        this.volume = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.startMediaTimeState = START_NOT_SET;
    }

    private void checkAudioTrackInitialized() {
        int state = this.audioTrack.getState();
        if (state != START_IN_SYNC) {
            try {
                this.audioTrack.release();
            } catch (Exception e) {
            } finally {
                this.audioTrack = null;
            }
            throw new InitializationException(state, this.sampleRate, this.channelConfig, this.bufferSize);
        }
    }

    private long durationUsToFrames(long j) {
        return (((long) this.sampleRate) * j) / C0700C.MICROS_PER_SECOND;
    }

    private long framesToDurationUs(long j) {
        return (C0700C.MICROS_PER_SECOND * j) / ((long) this.sampleRate);
    }

    private static int getEncodingForMimeType(String str) {
        int i = -1;
        switch (str.hashCode()) {
            case -1095064472:
                if (str.equals(MimeTypes.AUDIO_DTS)) {
                    i = START_NEED_SYNC;
                    break;
                }
                break;
            case 187078296:
                if (str.equals(MimeTypes.AUDIO_AC3)) {
                    i = START_NOT_SET;
                    break;
                }
                break;
            case 1504578661:
                if (str.equals(MimeTypes.AUDIO_E_AC3)) {
                    i = START_IN_SYNC;
                    break;
                }
                break;
            case 1505942594:
                if (str.equals(MimeTypes.AUDIO_DTS_HD)) {
                    i = 3;
                    break;
                }
                break;
        }
        switch (i) {
            case START_NOT_SET /*0*/:
                return 5;
            case START_IN_SYNC /*1*/:
                return 6;
            case START_NEED_SYNC /*2*/:
                return 7;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                return 8;
            default:
                return START_NOT_SET;
        }
    }

    private static int getFramesPerEncodedSample(int i, ByteBuffer byteBuffer) {
        if (i == 7 || i == 8) {
            return DtsUtil.parseDtsAudioSampleCount(byteBuffer);
        }
        if (i == 5) {
            return Ac3Util.getAc3SyncframeAudioSampleCount();
        }
        if (i == 6) {
            return Ac3Util.parseEAc3SyncframeAudioSampleCount(byteBuffer);
        }
        throw new IllegalStateException("Unexpected audio encoding: " + i);
    }

    private long getSubmittedFrames() {
        return this.passthrough ? this.submittedEncodedFrames : pcmBytesToFrames(this.submittedPcmBytes);
    }

    private boolean hasCurrentPositionUs() {
        return isInitialized() && this.startMediaTimeState != 0;
    }

    private void maybeSampleSyncParams() {
        long playbackHeadPositionUs = this.audioTrackUtil.getPlaybackHeadPositionUs();
        if (playbackHeadPositionUs != 0) {
            long nanoTime = System.nanoTime() / 1000;
            if (nanoTime - this.lastPlayheadSampleTimeUs >= 30000) {
                this.playheadOffsets[this.nextPlayheadOffsetIndex] = playbackHeadPositionUs - nanoTime;
                this.nextPlayheadOffsetIndex = (this.nextPlayheadOffsetIndex + START_IN_SYNC) % MAX_PLAYHEAD_OFFSET_COUNT;
                if (this.playheadOffsetCount < MAX_PLAYHEAD_OFFSET_COUNT) {
                    this.playheadOffsetCount += START_IN_SYNC;
                }
                this.lastPlayheadSampleTimeUs = nanoTime;
                this.smoothedPlayheadOffsetUs = 0;
                for (int i = START_NOT_SET; i < this.playheadOffsetCount; i += START_IN_SYNC) {
                    this.smoothedPlayheadOffsetUs += this.playheadOffsets[i] / ((long) this.playheadOffsetCount);
                }
            }
            if (!needsPassthroughWorkarounds() && nanoTime - this.lastTimestampSampleTimeUs >= 500000) {
                this.audioTimestampSet = this.audioTrackUtil.updateTimestamp();
                if (this.audioTimestampSet) {
                    long timestampNanoTime = this.audioTrackUtil.getTimestampNanoTime() / 1000;
                    long timestampFramePosition = this.audioTrackUtil.getTimestampFramePosition();
                    if (timestampNanoTime < this.resumeSystemTimeUs) {
                        this.audioTimestampSet = false;
                    } else if (Math.abs(timestampNanoTime - nanoTime) > MAX_LATENCY_US) {
                        r0 = "Spurious audio timestamp (system clock mismatch): " + timestampFramePosition + ", " + timestampNanoTime + ", " + nanoTime + ", " + playbackHeadPositionUs;
                        if (failOnSpuriousAudioTimestamp) {
                            throw new InvalidAudioTrackTimestampException(r0);
                        }
                        Log.w(TAG, r0);
                        this.audioTimestampSet = false;
                    } else if (Math.abs(framesToDurationUs(timestampFramePosition) - playbackHeadPositionUs) > MAX_LATENCY_US) {
                        r0 = "Spurious audio timestamp (frame position mismatch): " + timestampFramePosition + ", " + timestampNanoTime + ", " + nanoTime + ", " + playbackHeadPositionUs;
                        if (failOnSpuriousAudioTimestamp) {
                            throw new InvalidAudioTrackTimestampException(r0);
                        }
                        Log.w(TAG, r0);
                        this.audioTimestampSet = false;
                    }
                }
                if (!(this.getLatencyMethod == null || this.passthrough)) {
                    try {
                        this.latencyUs = (((long) ((Integer) this.getLatencyMethod.invoke(this.audioTrack, (Object[]) null)).intValue()) * 1000) - this.bufferSizeUs;
                        this.latencyUs = Math.max(this.latencyUs, 0);
                        if (this.latencyUs > MAX_LATENCY_US) {
                            Log.w(TAG, "Ignoring impossibly large audio latency: " + this.latencyUs);
                            this.latencyUs = 0;
                        }
                    } catch (Exception e) {
                        this.getLatencyMethod = null;
                    }
                }
                this.lastTimestampSampleTimeUs = nanoTime;
            }
        }
    }

    private boolean needsPassthroughWorkarounds() {
        return Util.SDK_INT < 23 && (this.targetEncoding == 5 || this.targetEncoding == 6);
    }

    private boolean overrideHasPendingData() {
        return needsPassthroughWorkarounds() && this.audioTrack.getPlayState() == START_NEED_SYNC && this.audioTrack.getPlaybackHeadPosition() == 0;
    }

    private long pcmBytesToFrames(long j) {
        return j / ((long) this.pcmFrameSize);
    }

    private void releaseKeepSessionIdAudioTrack() {
        if (this.keepSessionIdAudioTrack != null) {
            android.media.AudioTrack audioTrack = this.keepSessionIdAudioTrack;
            this.keepSessionIdAudioTrack = null;
            new C07182(audioTrack).start();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.nio.ByteBuffer resampleTo16BitPcm(java.nio.ByteBuffer r3, int r4, int r5, int r6, java.nio.ByteBuffer r7) {
        /*
        r2 = 0;
        switch(r6) {
            case -2147483648: goto L_0x0029;
            case 3: goto L_0x000a;
            case 1073741824: goto L_0x002e;
            default: goto L_0x0004;
        };
    L_0x0004:
        r0 = new java.lang.IllegalStateException;
        r0.<init>();
        throw r0;
    L_0x000a:
        r0 = r5 * 2;
    L_0x000c:
        if (r7 == 0) goto L_0x0014;
    L_0x000e:
        r1 = r7.capacity();
        if (r1 >= r0) goto L_0x0018;
    L_0x0014:
        r7 = java.nio.ByteBuffer.allocateDirect(r0);
    L_0x0018:
        r7.position(r2);
        r7.limit(r0);
        r0 = r4 + r5;
        switch(r6) {
            case -2147483648: goto L_0x0045;
            case 3: goto L_0x0031;
            case 1073741824: goto L_0x005c;
            default: goto L_0x0023;
        };
    L_0x0023:
        r0 = new java.lang.IllegalStateException;
        r0.<init>();
        throw r0;
    L_0x0029:
        r0 = r5 / 3;
        r0 = r0 * 2;
        goto L_0x000c;
    L_0x002e:
        r0 = r5 / 2;
        goto L_0x000c;
    L_0x0031:
        if (r4 >= r0) goto L_0x0073;
    L_0x0033:
        r7.put(r2);
        r1 = r3.get(r4);
        r1 = r1 & 255;
        r1 = r1 + -128;
        r1 = (byte) r1;
        r7.put(r1);
        r4 = r4 + 1;
        goto L_0x0031;
    L_0x0045:
        if (r4 >= r0) goto L_0x0073;
    L_0x0047:
        r1 = r4 + 1;
        r1 = r3.get(r1);
        r7.put(r1);
        r1 = r4 + 2;
        r1 = r3.get(r1);
        r7.put(r1);
        r4 = r4 + 3;
        goto L_0x0045;
    L_0x005c:
        if (r4 >= r0) goto L_0x0073;
    L_0x005e:
        r1 = r4 + 2;
        r1 = r3.get(r1);
        r7.put(r1);
        r1 = r4 + 3;
        r1 = r3.get(r1);
        r7.put(r1);
        r4 = r4 + 4;
        goto L_0x005c;
    L_0x0073:
        r7.position(r2);
        return r7;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.audio.AudioTrack.resampleTo16BitPcm(java.nio.ByteBuffer, int, int, int, java.nio.ByteBuffer):java.nio.ByteBuffer");
    }

    private void resetSyncParams() {
        this.smoothedPlayheadOffsetUs = 0;
        this.playheadOffsetCount = START_NOT_SET;
        this.nextPlayheadOffsetIndex = START_NOT_SET;
        this.lastPlayheadSampleTimeUs = 0;
        this.audioTimestampSet = false;
        this.lastTimestampSampleTimeUs = 0;
    }

    private void setAudioTrackVolume() {
        if (!isInitialized()) {
            return;
        }
        if (Util.SDK_INT >= 21) {
            setAudioTrackVolumeV21(this.audioTrack, this.volume);
        } else {
            setAudioTrackVolumeV3(this.audioTrack, this.volume);
        }
    }

    @TargetApi(21)
    private static void setAudioTrackVolumeV21(android.media.AudioTrack audioTrack, float f) {
        audioTrack.setVolume(f);
    }

    private static void setAudioTrackVolumeV3(android.media.AudioTrack audioTrack, float f) {
        audioTrack.setStereoVolume(f, f);
    }

    @TargetApi(21)
    private static int writeNonBlockingV21(android.media.AudioTrack audioTrack, ByteBuffer byteBuffer, int i) {
        return audioTrack.write(byteBuffer, i, START_IN_SYNC);
    }

    public void configure(String str, int i, int i2, int i3) {
        configure(str, i, i2, i3, START_NOT_SET);
    }

    public void configure(String str, int i, int i2, int i3, int i4) {
        int i5;
        switch (i) {
            case START_IN_SYNC /*1*/:
                i5 = BUFFER_MULTIPLICATION_FACTOR;
                break;
            case START_NEED_SYNC /*2*/:
                i5 = 12;
                break;
            case VideoPlayer.STATE_BUFFERING /*3*/:
                i5 = 28;
                break;
            case BUFFER_MULTIPLICATION_FACTOR /*4*/:
                i5 = 204;
                break;
            case VideoPlayer.STATE_ENDED /*5*/:
                i5 = 220;
                break;
            case Request.Method.TRACE /*6*/:
                i5 = 252;
                break;
            case Request.Method.PATCH /*7*/:
                i5 = 1276;
                break;
            case TLRPC.USER_FLAG_USERNAME /*8*/:
                i5 = C0700C.CHANNEL_OUT_7POINT1_SURROUND;
                break;
            default:
                throw new IllegalArgumentException("Unsupported channel count: " + i);
        }
        boolean z = !MimeTypes.AUDIO_RAW.equals(str);
        if (z) {
            i3 = getEncodingForMimeType(str);
        } else if (!(i3 == 3 || i3 == START_NEED_SYNC || i3 == TLRPC.MESSAGE_FLAG_MEGAGROUP || i3 == C0700C.ENCODING_PCM_32BIT)) {
            throw new IllegalArgumentException("Unsupported PCM encoding: " + i3);
        }
        if (!isInitialized() || this.sourceEncoding != i3 || this.sampleRate != i2 || this.channelConfig != i5) {
            reset();
            this.sourceEncoding = i3;
            this.passthrough = z;
            this.sampleRate = i2;
            this.channelConfig = i5;
            if (!z) {
                i3 = START_NEED_SYNC;
            }
            this.targetEncoding = i3;
            this.pcmFrameSize = i * START_NEED_SYNC;
            if (i4 != 0) {
                this.bufferSize = i4;
            } else if (!z) {
                int minBufferSize = android.media.AudioTrack.getMinBufferSize(i2, i5, this.targetEncoding);
                Assertions.checkState(minBufferSize != -2);
                int i6 = minBufferSize * BUFFER_MULTIPLICATION_FACTOR;
                i5 = ((int) durationUsToFrames(PASSTHROUGH_BUFFER_DURATION_US)) * this.pcmFrameSize;
                minBufferSize = (int) Math.max((long) minBufferSize, durationUsToFrames(MAX_BUFFER_DURATION_US) * ((long) this.pcmFrameSize));
                if (i6 >= i5) {
                    i5 = i6 > minBufferSize ? minBufferSize : i6;
                }
                this.bufferSize = i5;
            } else if (this.targetEncoding == 5 || this.targetEncoding == 6) {
                this.bufferSize = 20480;
            } else {
                this.bufferSize = 49152;
            }
            this.bufferSizeUs = z ? -1 : framesToDurationUs(pcmBytesToFrames((long) this.bufferSize));
        }
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public long getBufferSizeUs() {
        return this.bufferSizeUs;
    }

    public long getCurrentPositionUs(boolean z) {
        if (!hasCurrentPositionUs()) {
            return CURRENT_POSITION_NOT_SET;
        }
        if (this.audioTrack.getPlayState() == 3) {
            maybeSampleSyncParams();
        }
        long nanoTime = System.nanoTime() / 1000;
        if (this.audioTimestampSet) {
            return framesToDurationUs(durationUsToFrames((long) (((float) (nanoTime - (this.audioTrackUtil.getTimestampNanoTime() / 1000))) * this.audioTrackUtil.getPlaybackSpeed())) + this.audioTrackUtil.getTimestampFramePosition()) + this.startMediaTimeUs;
        }
        nanoTime = this.playheadOffsetCount == 0 ? this.audioTrackUtil.getPlaybackHeadPositionUs() + this.startMediaTimeUs : (nanoTime + this.smoothedPlayheadOffsetUs) + this.startMediaTimeUs;
        return !z ? nanoTime - this.latencyUs : nanoTime;
    }

    public int handleBuffer(ByteBuffer byteBuffer, int i, int i2, long j) {
        int i3;
        if (needsPassthroughWorkarounds()) {
            if (this.audioTrack.getPlayState() == START_NEED_SYNC) {
                return START_NOT_SET;
            }
            if (this.audioTrack.getPlayState() == START_IN_SYNC && this.audioTrackUtil.getPlaybackHeadPosition() != 0) {
                return START_NOT_SET;
            }
        }
        if (this.bufferBytesRemaining != 0) {
            i3 = START_NOT_SET;
        } else if (i2 == 0) {
            return START_NEED_SYNC;
        } else {
            this.useResampledBuffer = this.targetEncoding != this.sourceEncoding;
            if (this.useResampledBuffer) {
                Assertions.checkState(this.targetEncoding == START_NEED_SYNC);
                this.resampledBuffer = resampleTo16BitPcm(byteBuffer, i, i2, this.sourceEncoding, this.resampledBuffer);
                byteBuffer = this.resampledBuffer;
                i = this.resampledBuffer.position();
                i2 = this.resampledBuffer.limit();
            }
            this.bufferBytesRemaining = i2;
            byteBuffer.position(i);
            if (this.passthrough && this.framesPerEncodedSample == 0) {
                this.framesPerEncodedSample = getFramesPerEncodedSample(this.targetEncoding, byteBuffer);
            }
            if (this.startMediaTimeState == 0) {
                this.startMediaTimeUs = Math.max(0, j);
                this.startMediaTimeState = START_IN_SYNC;
                i3 = START_NOT_SET;
            } else {
                long framesToDurationUs = this.startMediaTimeUs + framesToDurationUs(getSubmittedFrames());
                if (this.startMediaTimeState == START_IN_SYNC && Math.abs(framesToDurationUs - j) > 200000) {
                    Log.e(TAG, "Discontinuity detected [expected " + framesToDurationUs + ", got " + j + "]");
                    this.startMediaTimeState = START_NEED_SYNC;
                }
                if (this.startMediaTimeState == START_NEED_SYNC) {
                    this.startMediaTimeUs += j - framesToDurationUs;
                    this.startMediaTimeState = START_IN_SYNC;
                    i3 = START_IN_SYNC;
                } else {
                    i3 = START_NOT_SET;
                }
            }
            if (Util.SDK_INT < 21) {
                if (this.temporaryBuffer == null || this.temporaryBuffer.length < i2) {
                    this.temporaryBuffer = new byte[i2];
                }
                byteBuffer.get(this.temporaryBuffer, START_NOT_SET, i2);
                this.temporaryBufferOffset = START_NOT_SET;
            }
        }
        int i4 = START_NOT_SET;
        if (Util.SDK_INT < 21) {
            int playbackHeadPosition = this.bufferSize - ((int) (this.submittedPcmBytes - (this.audioTrackUtil.getPlaybackHeadPosition() * ((long) this.pcmFrameSize))));
            if (playbackHeadPosition > 0) {
                i4 = this.audioTrack.write(this.temporaryBuffer, this.temporaryBufferOffset, Math.min(this.bufferBytesRemaining, playbackHeadPosition));
                if (i4 >= 0) {
                    this.temporaryBufferOffset += i4;
                }
            }
        } else {
            if (this.useResampledBuffer) {
                byteBuffer = this.resampledBuffer;
            }
            i4 = writeNonBlockingV21(this.audioTrack, byteBuffer, this.bufferBytesRemaining);
        }
        if (i4 < 0) {
            throw new WriteException(i4);
        }
        this.bufferBytesRemaining -= i4;
        if (!this.passthrough) {
            this.submittedPcmBytes += (long) i4;
        }
        if (this.bufferBytesRemaining != 0) {
            return i3;
        }
        if (this.passthrough) {
            this.submittedEncodedFrames += (long) this.framesPerEncodedSample;
        }
        return i3 | START_NEED_SYNC;
    }

    public void handleDiscontinuity() {
        if (this.startMediaTimeState == START_IN_SYNC) {
            this.startMediaTimeState = START_NEED_SYNC;
        }
    }

    public void handleEndOfStream() {
        if (isInitialized()) {
            this.audioTrackUtil.handleEndOfStream(getSubmittedFrames());
        }
    }

    public boolean hasPendingData() {
        return isInitialized() && (getSubmittedFrames() > this.audioTrackUtil.getPlaybackHeadPosition() || overrideHasPendingData());
    }

    public int initialize() {
        return initialize(START_NOT_SET);
    }

    public int initialize(int i) {
        this.releasingConditionVariable.block();
        if (i == 0) {
            this.audioTrack = new android.media.AudioTrack(this.streamType, this.sampleRate, this.channelConfig, this.targetEncoding, this.bufferSize, START_IN_SYNC);
        } else {
            this.audioTrack = new android.media.AudioTrack(this.streamType, this.sampleRate, this.channelConfig, this.targetEncoding, this.bufferSize, START_IN_SYNC, i);
        }
        checkAudioTrackInitialized();
        int audioSessionId = this.audioTrack.getAudioSessionId();
        if (enablePreV21AudioSessionWorkaround && Util.SDK_INT < 21) {
            if (!(this.keepSessionIdAudioTrack == null || audioSessionId == this.keepSessionIdAudioTrack.getAudioSessionId())) {
                releaseKeepSessionIdAudioTrack();
            }
            if (this.keepSessionIdAudioTrack == null) {
                this.keepSessionIdAudioTrack = new android.media.AudioTrack(this.streamType, 4000, BUFFER_MULTIPLICATION_FACTOR, START_NEED_SYNC, START_NEED_SYNC, START_NOT_SET, audioSessionId);
            }
        }
        this.audioTrackUtil.reconfigure(this.audioTrack, needsPassthroughWorkarounds());
        setAudioTrackVolume();
        return audioSessionId;
    }

    public boolean isInitialized() {
        return this.audioTrack != null;
    }

    public boolean isPassthroughSupported(String str) {
        return this.audioCapabilities != null && this.audioCapabilities.supportsEncoding(getEncodingForMimeType(str));
    }

    public void pause() {
        if (isInitialized()) {
            resetSyncParams();
            this.audioTrackUtil.pause();
        }
    }

    public void play() {
        if (isInitialized()) {
            this.resumeSystemTimeUs = System.nanoTime() / 1000;
            this.audioTrack.play();
        }
    }

    public void release() {
        reset();
        releaseKeepSessionIdAudioTrack();
    }

    public void reset() {
        if (isInitialized()) {
            this.submittedPcmBytes = 0;
            this.submittedEncodedFrames = 0;
            this.framesPerEncodedSample = START_NOT_SET;
            this.bufferBytesRemaining = START_NOT_SET;
            this.startMediaTimeState = START_NOT_SET;
            this.latencyUs = 0;
            resetSyncParams();
            if (this.audioTrack.getPlayState() == 3) {
                this.audioTrack.pause();
            }
            android.media.AudioTrack audioTrack = this.audioTrack;
            this.audioTrack = null;
            this.audioTrackUtil.reconfigure(null, false);
            this.releasingConditionVariable.close();
            new C07171(audioTrack).start();
        }
    }

    public void setPlaybackParams(PlaybackParams playbackParams) {
        this.audioTrackUtil.setPlaybackParameters(playbackParams);
    }

    public void setVolume(float f) {
        if (this.volume != f) {
            this.volume = f;
            setAudioTrackVolume();
        }
    }
}

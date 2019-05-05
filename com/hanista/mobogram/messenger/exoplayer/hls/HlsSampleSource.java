package com.hanista.mobogram.messenger.exoplayer.hls;

import android.os.Handler;
import android.os.SystemClock;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.LoadControl;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.MediaFormatHolder;
import com.hanista.mobogram.messenger.exoplayer.SampleHolder;
import com.hanista.mobogram.messenger.exoplayer.SampleSource;
import com.hanista.mobogram.messenger.exoplayer.SampleSource.SampleSourceReader;
import com.hanista.mobogram.messenger.exoplayer.chunk.BaseChunkSampleSourceEventListener;
import com.hanista.mobogram.messenger.exoplayer.chunk.Chunk;
import com.hanista.mobogram.messenger.exoplayer.chunk.ChunkOperationHolder;
import com.hanista.mobogram.messenger.exoplayer.chunk.Format;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Callback;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Loadable;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public final class HlsSampleSource implements SampleSource, SampleSourceReader, Callback {
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private static final long NO_RESET_PENDING = Long.MIN_VALUE;
    private static final int PRIMARY_TYPE_AUDIO = 2;
    private static final int PRIMARY_TYPE_NONE = 0;
    private static final int PRIMARY_TYPE_TEXT = 1;
    private static final int PRIMARY_TYPE_VIDEO = 3;
    private final int bufferSizeContribution;
    private final ChunkOperationHolder chunkOperationHolder;
    private final HlsChunkSource chunkSource;
    private int[] chunkSourceTrackIndices;
    private long currentLoadStartTimeMs;
    private Chunk currentLoadable;
    private IOException currentLoadableException;
    private int currentLoadableExceptionCount;
    private long currentLoadableExceptionTimestamp;
    private TsChunk currentTsLoadable;
    private Format downstreamFormat;
    private MediaFormat[] downstreamMediaFormats;
    private long downstreamPositionUs;
    private int enabledTrackCount;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final int eventSourceId;
    private boolean[] extractorTrackEnabledStates;
    private int[] extractorTrackIndices;
    private final LinkedList<HlsExtractorWrapper> extractors;
    private long lastSeekPositionUs;
    private final LoadControl loadControl;
    private boolean loadControlRegistered;
    private Loader loader;
    private boolean loadingFinished;
    private final int minLoadableRetryCount;
    private boolean[] pendingDiscontinuities;
    private long pendingResetPositionUs;
    private boolean prepared;
    private TsChunk previousTsLoadable;
    private int remainingReleaseCount;
    private int trackCount;
    private boolean[] trackEnabledStates;
    private MediaFormat[] trackFormats;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.hls.HlsSampleSource.1 */
    class C07431 implements Runnable {
        final /* synthetic */ Format val$format;
        final /* synthetic */ long val$length;
        final /* synthetic */ long val$mediaEndTimeUs;
        final /* synthetic */ long val$mediaStartTimeUs;
        final /* synthetic */ int val$trigger;
        final /* synthetic */ int val$type;

        C07431(long j, int i, int i2, Format format, long j2, long j3) {
            this.val$length = j;
            this.val$type = i;
            this.val$trigger = i2;
            this.val$format = format;
            this.val$mediaStartTimeUs = j2;
            this.val$mediaEndTimeUs = j3;
        }

        public void run() {
            HlsSampleSource.this.eventListener.onLoadStarted(HlsSampleSource.this.eventSourceId, this.val$length, this.val$type, this.val$trigger, this.val$format, HlsSampleSource.this.usToMs(this.val$mediaStartTimeUs), HlsSampleSource.this.usToMs(this.val$mediaEndTimeUs));
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.hls.HlsSampleSource.2 */
    class C07442 implements Runnable {
        final /* synthetic */ long val$bytesLoaded;
        final /* synthetic */ long val$elapsedRealtimeMs;
        final /* synthetic */ Format val$format;
        final /* synthetic */ long val$loadDurationMs;
        final /* synthetic */ long val$mediaEndTimeUs;
        final /* synthetic */ long val$mediaStartTimeUs;
        final /* synthetic */ int val$trigger;
        final /* synthetic */ int val$type;

        C07442(long j, int i, int i2, Format format, long j2, long j3, long j4, long j5) {
            this.val$bytesLoaded = j;
            this.val$type = i;
            this.val$trigger = i2;
            this.val$format = format;
            this.val$mediaStartTimeUs = j2;
            this.val$mediaEndTimeUs = j3;
            this.val$elapsedRealtimeMs = j4;
            this.val$loadDurationMs = j5;
        }

        public void run() {
            HlsSampleSource.this.eventListener.onLoadCompleted(HlsSampleSource.this.eventSourceId, this.val$bytesLoaded, this.val$type, this.val$trigger, this.val$format, HlsSampleSource.this.usToMs(this.val$mediaStartTimeUs), HlsSampleSource.this.usToMs(this.val$mediaEndTimeUs), this.val$elapsedRealtimeMs, this.val$loadDurationMs);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.hls.HlsSampleSource.3 */
    class C07453 implements Runnable {
        final /* synthetic */ long val$bytesLoaded;

        C07453(long j) {
            this.val$bytesLoaded = j;
        }

        public void run() {
            HlsSampleSource.this.eventListener.onLoadCanceled(HlsSampleSource.this.eventSourceId, this.val$bytesLoaded);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.hls.HlsSampleSource.4 */
    class C07464 implements Runnable {
        final /* synthetic */ IOException val$e;

        C07464(IOException iOException) {
            this.val$e = iOException;
        }

        public void run() {
            HlsSampleSource.this.eventListener.onLoadError(HlsSampleSource.this.eventSourceId, this.val$e);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.hls.HlsSampleSource.5 */
    class C07475 implements Runnable {
        final /* synthetic */ Format val$format;
        final /* synthetic */ long val$positionUs;
        final /* synthetic */ int val$trigger;

        C07475(Format format, int i, long j) {
            this.val$format = format;
            this.val$trigger = i;
            this.val$positionUs = j;
        }

        public void run() {
            HlsSampleSource.this.eventListener.onDownstreamFormatChanged(HlsSampleSource.this.eventSourceId, this.val$format, this.val$trigger, HlsSampleSource.this.usToMs(this.val$positionUs));
        }
    }

    public interface EventListener extends BaseChunkSampleSourceEventListener {
    }

    public HlsSampleSource(HlsChunkSource hlsChunkSource, LoadControl loadControl, int i) {
        this(hlsChunkSource, loadControl, i, null, null, PRIMARY_TYPE_NONE);
    }

    public HlsSampleSource(HlsChunkSource hlsChunkSource, LoadControl loadControl, int i, Handler handler, EventListener eventListener, int i2) {
        this(hlsChunkSource, loadControl, i, handler, eventListener, i2, PRIMARY_TYPE_VIDEO);
    }

    public HlsSampleSource(HlsChunkSource hlsChunkSource, LoadControl loadControl, int i, Handler handler, EventListener eventListener, int i2, int i3) {
        this.chunkSource = hlsChunkSource;
        this.loadControl = loadControl;
        this.bufferSizeContribution = i;
        this.minLoadableRetryCount = i3;
        this.eventHandler = handler;
        this.eventListener = eventListener;
        this.eventSourceId = i2;
        this.pendingResetPositionUs = NO_RESET_PENDING;
        this.extractors = new LinkedList();
        this.chunkOperationHolder = new ChunkOperationHolder();
    }

    private void buildTracks(HlsExtractorWrapper hlsExtractorWrapper) {
        Object obj = null;
        int i = -1;
        int trackCount = hlsExtractorWrapper.getTrackCount();
        int i2 = PRIMARY_TYPE_NONE;
        while (i2 < trackCount) {
            String str = hlsExtractorWrapper.getMediaFormat(i2).mimeType;
            Object obj2 = MimeTypes.isVideo(str) ? PRIMARY_TYPE_VIDEO : MimeTypes.isAudio(str) ? PRIMARY_TYPE_AUDIO : MimeTypes.isText(str) ? PRIMARY_TYPE_TEXT : null;
            if (obj2 > obj) {
                i = i2;
            } else if (obj2 != obj || i == -1) {
                obj2 = obj;
            } else {
                i = -1;
                obj2 = obj;
            }
            i2 += PRIMARY_TYPE_TEXT;
            obj = obj2;
        }
        int trackCount2 = this.chunkSource.getTrackCount();
        Object obj3 = i != -1 ? PRIMARY_TYPE_TEXT : null;
        this.trackCount = trackCount;
        if (obj3 != null) {
            this.trackCount += trackCount2 - 1;
        }
        this.trackFormats = new MediaFormat[this.trackCount];
        this.trackEnabledStates = new boolean[this.trackCount];
        this.pendingDiscontinuities = new boolean[this.trackCount];
        this.downstreamMediaFormats = new MediaFormat[this.trackCount];
        this.chunkSourceTrackIndices = new int[this.trackCount];
        this.extractorTrackIndices = new int[this.trackCount];
        this.extractorTrackEnabledStates = new boolean[trackCount];
        long durationUs = this.chunkSource.getDurationUs();
        int i3 = PRIMARY_TYPE_NONE;
        int i4 = PRIMARY_TYPE_NONE;
        while (i4 < trackCount) {
            MediaFormat copyWithDurationUs = hlsExtractorWrapper.getMediaFormat(i4).copyWithDurationUs(durationUs);
            String muxedAudioLanguage = MimeTypes.isAudio(copyWithDurationUs.mimeType) ? this.chunkSource.getMuxedAudioLanguage() : MimeTypes.APPLICATION_EIA608.equals(copyWithDurationUs.mimeType) ? this.chunkSource.getMuxedCaptionLanguage() : null;
            if (i4 == i) {
                int i5 = PRIMARY_TYPE_NONE;
                while (i5 < trackCount2) {
                    this.extractorTrackIndices[i3] = i4;
                    this.chunkSourceTrackIndices[i3] = i5;
                    Variant fixedTrackVariant = this.chunkSource.getFixedTrackVariant(i5);
                    int i6 = i3 + PRIMARY_TYPE_TEXT;
                    this.trackFormats[i3] = fixedTrackVariant == null ? copyWithDurationUs.copyAsAdaptive(null) : copyWithFixedTrackInfo(copyWithDurationUs, fixedTrackVariant.format, muxedAudioLanguage);
                    i5 += PRIMARY_TYPE_TEXT;
                    i3 = i6;
                }
                i2 = i3;
            } else {
                this.extractorTrackIndices[i3] = i4;
                this.chunkSourceTrackIndices[i3] = -1;
                i2 = i3 + PRIMARY_TYPE_TEXT;
                this.trackFormats[i3] = copyWithDurationUs.copyWithLanguage(muxedAudioLanguage);
            }
            i4 += PRIMARY_TYPE_TEXT;
            i3 = i2;
        }
    }

    private void clearCurrentLoadable() {
        this.currentTsLoadable = null;
        this.currentLoadable = null;
        this.currentLoadableException = null;
        this.currentLoadableExceptionCount = PRIMARY_TYPE_NONE;
    }

    private void clearState() {
        for (int i = PRIMARY_TYPE_NONE; i < this.extractors.size(); i += PRIMARY_TYPE_TEXT) {
            ((HlsExtractorWrapper) this.extractors.get(i)).clear();
        }
        this.extractors.clear();
        clearCurrentLoadable();
        this.previousTsLoadable = null;
    }

    private static MediaFormat copyWithFixedTrackInfo(MediaFormat mediaFormat, Format format, String str) {
        return mediaFormat.copyWithFixedTrackInfo(format.id, format.bitrate, format.width == -1 ? -1 : format.width, format.height == -1 ? -1 : format.height, str == null ? format.language : str);
    }

    private void discardSamplesForDisabledTracks(HlsExtractorWrapper hlsExtractorWrapper, long j) {
        if (hlsExtractorWrapper.isPrepared()) {
            for (int i = PRIMARY_TYPE_NONE; i < this.extractorTrackEnabledStates.length; i += PRIMARY_TYPE_TEXT) {
                if (!this.extractorTrackEnabledStates[i]) {
                    hlsExtractorWrapper.discardUntil(i, j);
                }
            }
        }
    }

    private HlsExtractorWrapper getCurrentExtractor() {
        HlsExtractorWrapper hlsExtractorWrapper = (HlsExtractorWrapper) this.extractors.getFirst();
        while (this.extractors.size() > PRIMARY_TYPE_TEXT && !haveSamplesForEnabledTracks(hlsExtractorWrapper)) {
            ((HlsExtractorWrapper) this.extractors.removeFirst()).clear();
            hlsExtractorWrapper = (HlsExtractorWrapper) this.extractors.getFirst();
        }
        return hlsExtractorWrapper;
    }

    private long getNextLoadPositionUs() {
        return isPendingReset() ? this.pendingResetPositionUs : (this.loadingFinished || (this.prepared && this.enabledTrackCount == 0)) ? -1 : this.currentTsLoadable != null ? this.currentTsLoadable.endTimeUs : this.previousTsLoadable.endTimeUs;
    }

    private long getRetryDelayMillis(long j) {
        return Math.min((j - 1) * 1000, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
    }

    private boolean haveSamplesForEnabledTracks(HlsExtractorWrapper hlsExtractorWrapper) {
        if (!hlsExtractorWrapper.isPrepared()) {
            return false;
        }
        int i = PRIMARY_TYPE_NONE;
        while (i < this.extractorTrackEnabledStates.length) {
            if (this.extractorTrackEnabledStates[i] && hlsExtractorWrapper.hasSamples(i)) {
                return true;
            }
            i += PRIMARY_TYPE_TEXT;
        }
        return false;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != NO_RESET_PENDING;
    }

    private boolean isTsChunk(Chunk chunk) {
        return chunk instanceof TsChunk;
    }

    private void maybeStartLoading() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long nextLoadPositionUs = getNextLoadPositionUs();
        Object obj = this.currentLoadableException != null ? PRIMARY_TYPE_TEXT : PRIMARY_TYPE_NONE;
        boolean z = this.loader.isLoading() || obj != null;
        boolean update = this.loadControl.update(this, this.downstreamPositionUs, nextLoadPositionUs, z);
        if (obj != null) {
            if (elapsedRealtime - this.currentLoadableExceptionTimestamp >= getRetryDelayMillis((long) this.currentLoadableExceptionCount)) {
                this.currentLoadableException = null;
                this.loader.startLoading(this.currentLoadable, this);
            }
        } else if (!this.loader.isLoading() && update) {
            if (!this.prepared || this.enabledTrackCount != 0) {
                this.chunkSource.getChunkOperation(this.previousTsLoadable, this.pendingResetPositionUs != NO_RESET_PENDING ? this.pendingResetPositionUs : this.downstreamPositionUs, this.chunkOperationHolder);
                update = this.chunkOperationHolder.endOfStream;
                Chunk chunk = this.chunkOperationHolder.chunk;
                this.chunkOperationHolder.clear();
                if (update) {
                    this.loadingFinished = true;
                    this.loadControl.update(this, this.downstreamPositionUs, -1, false);
                } else if (chunk != null) {
                    this.currentLoadStartTimeMs = elapsedRealtime;
                    this.currentLoadable = chunk;
                    if (isTsChunk(this.currentLoadable)) {
                        TsChunk tsChunk = (TsChunk) this.currentLoadable;
                        if (isPendingReset()) {
                            this.pendingResetPositionUs = NO_RESET_PENDING;
                        }
                        HlsExtractorWrapper hlsExtractorWrapper = tsChunk.extractorWrapper;
                        if (this.extractors.isEmpty() || this.extractors.getLast() != hlsExtractorWrapper) {
                            hlsExtractorWrapper.init(this.loadControl.getAllocator());
                            this.extractors.addLast(hlsExtractorWrapper);
                        }
                        notifyLoadStarted(tsChunk.dataSpec.length, tsChunk.type, tsChunk.trigger, tsChunk.format, tsChunk.startTimeUs, tsChunk.endTimeUs);
                        this.currentTsLoadable = tsChunk;
                    } else {
                        notifyLoadStarted(this.currentLoadable.dataSpec.length, this.currentLoadable.type, this.currentLoadable.trigger, this.currentLoadable.format, -1, -1);
                    }
                    this.loader.startLoading(this.currentLoadable, this);
                }
            }
        }
    }

    private void notifyDownstreamFormatChanged(Format format, int i, long j) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07475(format, i, j));
        }
    }

    private void notifyLoadCanceled(long j) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07453(j));
        }
    }

    private void notifyLoadCompleted(long j, int i, int i2, Format format, long j2, long j3, long j4, long j5) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07442(j, i, i2, format, j2, j3, j4, j5));
        }
    }

    private void notifyLoadError(IOException iOException) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07464(iOException));
        }
    }

    private void notifyLoadStarted(long j, int i, int i2, Format format, long j2, long j3) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07431(j, i, i2, format, j2, j3));
        }
    }

    private void restartFrom(long j) {
        this.pendingResetPositionUs = j;
        this.loadingFinished = false;
        if (this.loader.isLoading()) {
            this.loader.cancelLoading();
            return;
        }
        clearState();
        maybeStartLoading();
    }

    private void seekToInternal(long j) {
        this.lastSeekPositionUs = j;
        this.downstreamPositionUs = j;
        Arrays.fill(this.pendingDiscontinuities, true);
        this.chunkSource.seek();
        restartFrom(j);
    }

    private void setTrackEnabledState(int i, boolean z) {
        boolean z2 = false;
        int i2 = PRIMARY_TYPE_TEXT;
        Assertions.checkState(this.trackEnabledStates[i] != z);
        int i3 = this.extractorTrackIndices[i];
        if (this.extractorTrackEnabledStates[i3] != z) {
            z2 = true;
        }
        Assertions.checkState(z2);
        this.trackEnabledStates[i] = z;
        this.extractorTrackEnabledStates[i3] = z;
        i3 = this.enabledTrackCount;
        if (!z) {
            i2 = -1;
        }
        this.enabledTrackCount = i3 + i2;
    }

    public boolean continueBuffering(int i, long j) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.trackEnabledStates[i]);
        this.downstreamPositionUs = j;
        if (!this.extractors.isEmpty()) {
            discardSamplesForDisabledTracks(getCurrentExtractor(), this.downstreamPositionUs);
        }
        maybeStartLoading();
        if (this.loadingFinished) {
            return true;
        }
        if (isPendingReset() || this.extractors.isEmpty()) {
            return false;
        }
        for (int i2 = PRIMARY_TYPE_NONE; i2 < this.extractors.size(); i2 += PRIMARY_TYPE_TEXT) {
            HlsExtractorWrapper hlsExtractorWrapper = (HlsExtractorWrapper) this.extractors.get(i2);
            if (!hlsExtractorWrapper.isPrepared()) {
                return false;
            }
            if (hlsExtractorWrapper.hasSamples(this.extractorTrackIndices[i])) {
                return true;
            }
        }
        return false;
    }

    public void disable(int i) {
        Assertions.checkState(this.prepared);
        setTrackEnabledState(i, false);
        if (this.enabledTrackCount == 0) {
            this.chunkSource.reset();
            this.downstreamPositionUs = NO_RESET_PENDING;
            if (this.loadControlRegistered) {
                this.loadControl.unregister(this);
                this.loadControlRegistered = false;
            }
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
                return;
            }
            clearState();
            this.loadControl.trimAllocator();
        }
    }

    public void enable(int i, long j) {
        Assertions.checkState(this.prepared);
        setTrackEnabledState(i, true);
        this.downstreamMediaFormats[i] = null;
        this.pendingDiscontinuities[i] = false;
        this.downstreamFormat = null;
        boolean z = this.loadControlRegistered;
        if (!this.loadControlRegistered) {
            this.loadControl.register(this, this.bufferSizeContribution);
            this.loadControlRegistered = true;
        }
        if (this.chunkSource.isLive()) {
            j = 0;
        }
        int i2 = this.chunkSourceTrackIndices[i];
        if (i2 != -1 && i2 != this.chunkSource.getSelectedTrackIndex()) {
            this.chunkSource.selectTrack(i2);
            seekToInternal(j);
        } else if (this.enabledTrackCount == PRIMARY_TYPE_TEXT) {
            this.lastSeekPositionUs = j;
            if (z && this.downstreamPositionUs == j) {
                maybeStartLoading();
                return;
            }
            this.downstreamPositionUs = j;
            restartFrom(j);
        }
    }

    public long getBufferedPositionUs() {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.enabledTrackCount > 0);
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        if (this.loadingFinished) {
            return -3;
        }
        long largestParsedTimestampUs = ((HlsExtractorWrapper) this.extractors.getLast()).getLargestParsedTimestampUs();
        long max = this.extractors.size() > PRIMARY_TYPE_TEXT ? Math.max(largestParsedTimestampUs, ((HlsExtractorWrapper) this.extractors.get(this.extractors.size() - 2)).getLargestParsedTimestampUs()) : largestParsedTimestampUs;
        return max == NO_RESET_PENDING ? this.downstreamPositionUs : max;
    }

    public MediaFormat getFormat(int i) {
        Assertions.checkState(this.prepared);
        return this.trackFormats[i];
    }

    public int getTrackCount() {
        Assertions.checkState(this.prepared);
        return this.trackCount;
    }

    public void maybeThrowError() {
        if (this.currentLoadableException != null && this.currentLoadableExceptionCount > this.minLoadableRetryCount) {
            throw this.currentLoadableException;
        } else if (this.currentLoadable == null) {
            this.chunkSource.maybeThrowError();
        }
    }

    public void onLoadCanceled(Loadable loadable) {
        notifyLoadCanceled(this.currentLoadable.bytesLoaded());
        if (this.enabledTrackCount > 0) {
            restartFrom(this.pendingResetPositionUs);
            return;
        }
        clearState();
        this.loadControl.trimAllocator();
    }

    public void onLoadCompleted(Loadable loadable) {
        boolean z = true;
        Assertions.checkState(loadable == this.currentLoadable);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = elapsedRealtime - this.currentLoadStartTimeMs;
        this.chunkSource.onChunkLoadCompleted(this.currentLoadable);
        if (isTsChunk(this.currentLoadable)) {
            if (this.currentLoadable != this.currentTsLoadable) {
                z = false;
            }
            Assertions.checkState(z);
            this.previousTsLoadable = this.currentTsLoadable;
            notifyLoadCompleted(this.currentLoadable.bytesLoaded(), this.currentTsLoadable.type, this.currentTsLoadable.trigger, this.currentTsLoadable.format, this.currentTsLoadable.startTimeUs, this.currentTsLoadable.endTimeUs, elapsedRealtime, j);
        } else {
            notifyLoadCompleted(this.currentLoadable.bytesLoaded(), this.currentLoadable.type, this.currentLoadable.trigger, this.currentLoadable.format, -1, -1, elapsedRealtime, j);
        }
        clearCurrentLoadable();
        maybeStartLoading();
    }

    public void onLoadError(Loadable loadable, IOException iOException) {
        if (this.chunkSource.onChunkLoadError(this.currentLoadable, iOException)) {
            if (this.previousTsLoadable == null && !isPendingReset()) {
                this.pendingResetPositionUs = this.lastSeekPositionUs;
            }
            clearCurrentLoadable();
        } else {
            this.currentLoadableException = iOException;
            this.currentLoadableExceptionCount += PRIMARY_TYPE_TEXT;
            this.currentLoadableExceptionTimestamp = SystemClock.elapsedRealtime();
        }
        notifyLoadError(iOException);
        maybeStartLoading();
    }

    public boolean prepare(long j) {
        if (this.prepared) {
            return true;
        }
        if (!this.chunkSource.prepare()) {
            return false;
        }
        if (!this.extractors.isEmpty()) {
            while (true) {
                HlsExtractorWrapper hlsExtractorWrapper = (HlsExtractorWrapper) this.extractors.getFirst();
                if (!hlsExtractorWrapper.isPrepared()) {
                    if (this.extractors.size() <= PRIMARY_TYPE_TEXT) {
                        break;
                    }
                    ((HlsExtractorWrapper) this.extractors.removeFirst()).clear();
                } else {
                    buildTracks(hlsExtractorWrapper);
                    this.prepared = true;
                    maybeStartLoading();
                    return true;
                }
            }
        }
        if (this.loader == null) {
            this.loader = new Loader("Loader:HLS");
            this.loadControl.register(this, this.bufferSizeContribution);
            this.loadControlRegistered = true;
        }
        if (!this.loader.isLoading()) {
            this.pendingResetPositionUs = j;
            this.downstreamPositionUs = j;
        }
        maybeStartLoading();
        return false;
    }

    public int readData(int i, long j, MediaFormatHolder mediaFormatHolder, SampleHolder sampleHolder) {
        int i2 = PRIMARY_TYPE_NONE;
        Assertions.checkState(this.prepared);
        this.downstreamPositionUs = j;
        if (this.pendingDiscontinuities[i] || isPendingReset()) {
            return -2;
        }
        HlsExtractorWrapper currentExtractor = getCurrentExtractor();
        if (!currentExtractor.isPrepared()) {
            return -2;
        }
        Format format = currentExtractor.format;
        if (!format.equals(this.downstreamFormat)) {
            notifyDownstreamFormatChanged(format, currentExtractor.trigger, currentExtractor.startTimeUs);
        }
        this.downstreamFormat = format;
        if (this.extractors.size() > PRIMARY_TYPE_TEXT) {
            currentExtractor.configureSpliceTo((HlsExtractorWrapper) this.extractors.get(PRIMARY_TYPE_TEXT));
        }
        int i3 = this.extractorTrackIndices[i];
        int i4 = PRIMARY_TYPE_NONE;
        while (this.extractors.size() > i4 + PRIMARY_TYPE_TEXT && !currentExtractor.hasSamples(i3)) {
            int i5 = i4 + PRIMARY_TYPE_TEXT;
            HlsExtractorWrapper hlsExtractorWrapper = (HlsExtractorWrapper) this.extractors.get(i5);
            if (!hlsExtractorWrapper.isPrepared()) {
                return -2;
            }
            int i6 = i5;
            currentExtractor = hlsExtractorWrapper;
            i4 = i6;
        }
        MediaFormat mediaFormat = currentExtractor.getMediaFormat(i3);
        if (mediaFormat != null) {
            if (mediaFormat.equals(this.downstreamMediaFormats[i])) {
                this.downstreamMediaFormats[i] = mediaFormat;
            } else {
                mediaFormatHolder.format = mediaFormat;
                this.downstreamMediaFormats[i] = mediaFormat;
                return -4;
            }
        }
        if (!currentExtractor.getSample(i3, sampleHolder)) {
            return this.loadingFinished ? -1 : -2;
        } else {
            i4 = sampleHolder.timeUs < this.lastSeekPositionUs ? PRIMARY_TYPE_TEXT : PRIMARY_TYPE_NONE;
            i5 = sampleHolder.flags;
            if (i4 != 0) {
                i2 = C0700C.SAMPLE_FLAG_DECODE_ONLY;
            }
            sampleHolder.flags = i5 | i2;
            return -3;
        }
    }

    public long readDiscontinuity(int i) {
        if (!this.pendingDiscontinuities[i]) {
            return NO_RESET_PENDING;
        }
        this.pendingDiscontinuities[i] = false;
        return this.lastSeekPositionUs;
    }

    public SampleSourceReader register() {
        this.remainingReleaseCount += PRIMARY_TYPE_TEXT;
        return this;
    }

    public void release() {
        Assertions.checkState(this.remainingReleaseCount > 0);
        int i = this.remainingReleaseCount - 1;
        this.remainingReleaseCount = i;
        if (i == 0 && this.loader != null) {
            if (this.loadControlRegistered) {
                this.loadControl.unregister(this);
                this.loadControlRegistered = false;
            }
            this.loader.release();
            this.loader = null;
        }
    }

    public void seekToUs(long j) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.enabledTrackCount > 0);
        if (this.chunkSource.isLive()) {
            j = 0;
        }
        long j2 = isPendingReset() ? this.pendingResetPositionUs : this.downstreamPositionUs;
        this.downstreamPositionUs = j;
        this.lastSeekPositionUs = j;
        if (j2 != j) {
            seekToInternal(j);
        }
    }

    long usToMs(long j) {
        return j / 1000;
    }
}

package com.hanista.mobogram.messenger.exoplayer.extractor;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.SparseArray;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.MediaFormatHolder;
import com.hanista.mobogram.messenger.exoplayer.ParserException;
import com.hanista.mobogram.messenger.exoplayer.SampleHolder;
import com.hanista.mobogram.messenger.exoplayer.SampleSource;
import com.hanista.mobogram.messenger.exoplayer.SampleSource.SampleSourceReader;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.Allocator;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Callback;
import com.hanista.mobogram.messenger.exoplayer.upstream.Loader.Loadable;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ExtractorSampleSource implements SampleSource, SampleSourceReader, ExtractorOutput, Callback {
    private static final List<Class<? extends Extractor>> DEFAULT_EXTRACTOR_CLASSES;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE = 6;
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_ON_DEMAND = 3;
    private static final int MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA = -1;
    private static final long NO_RESET_PENDING = Long.MIN_VALUE;
    private final Allocator allocator;
    private IOException currentLoadableException;
    private int currentLoadableExceptionCount;
    private long currentLoadableExceptionTimestamp;
    private final DataSource dataSource;
    private long downstreamPositionUs;
    private volatile DrmInitData drmInitData;
    private int enabledTrackCount;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final int eventSourceId;
    private int extractedSampleCount;
    private int extractedSampleCountAtStartOfLoad;
    private final ExtractorHolder extractorHolder;
    private boolean havePendingNextSampleUs;
    private long lastSeekPositionUs;
    private ExtractingLoadable loadable;
    private Loader loader;
    private boolean loadingFinished;
    private long maxTrackDurationUs;
    private MediaFormat[] mediaFormats;
    private final int minLoadableRetryCount;
    private boolean[] pendingDiscontinuities;
    private boolean[] pendingMediaFormat;
    private long pendingNextSampleUs;
    private long pendingResetPositionUs;
    private boolean prepared;
    private int remainingReleaseCount;
    private final int requestedBufferSize;
    private final SparseArray<InternalTrackOutput> sampleQueues;
    private long sampleTimeOffsetUs;
    private volatile SeekMap seekMap;
    private boolean[] trackEnabledStates;
    private volatile boolean tracksBuilt;
    private final Uri uri;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorSampleSource.1 */
    class C07331 implements Runnable {
        C07331() {
        }

        public void run() {
            ExtractorSampleSource.this.extractorHolder.release();
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorSampleSource.2 */
    class C07342 implements Runnable {
        final /* synthetic */ IOException val$e;

        C07342(IOException iOException) {
            this.val$e = iOException;
        }

        public void run() {
            ExtractorSampleSource.this.eventListener.onLoadError(ExtractorSampleSource.this.eventSourceId, this.val$e);
        }
    }

    public interface EventListener {
        void onLoadError(int i, IOException iOException);
    }

    private static class ExtractingLoadable implements Loadable {
        private final Allocator allocator;
        private final DataSource dataSource;
        private final ExtractorHolder extractorHolder;
        private volatile boolean loadCanceled;
        private boolean pendingExtractorSeek;
        private final PositionHolder positionHolder;
        private final int requestedBufferSize;
        private final Uri uri;

        public ExtractingLoadable(Uri uri, DataSource dataSource, ExtractorHolder extractorHolder, Allocator allocator, int i, long j) {
            this.uri = (Uri) Assertions.checkNotNull(uri);
            this.dataSource = (DataSource) Assertions.checkNotNull(dataSource);
            this.extractorHolder = (ExtractorHolder) Assertions.checkNotNull(extractorHolder);
            this.allocator = (Allocator) Assertions.checkNotNull(allocator);
            this.requestedBufferSize = i;
            this.positionHolder = new PositionHolder();
            this.positionHolder.position = j;
            this.pendingExtractorSeek = true;
        }

        public void cancelLoad() {
            this.loadCanceled = true;
        }

        public boolean isLoadCanceled() {
            return this.loadCanceled;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void load() {
            /*
            r15 = this;
            r12 = -1;
            r8 = 0;
            r11 = 1;
            r7 = 0;
            r9 = r7;
        L_0x0006:
            if (r9 != 0) goto L_0x007e;
        L_0x0008:
            r0 = r15.loadCanceled;
            if (r0 != 0) goto L_0x007e;
        L_0x000c:
            r0 = r15.positionHolder;	 Catch:{ all -> 0x0068 }
            r2 = r0.position;	 Catch:{ all -> 0x0068 }
            r10 = r15.dataSource;	 Catch:{ all -> 0x0068 }
            r0 = new com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;	 Catch:{ all -> 0x0068 }
            r1 = r15.uri;	 Catch:{ all -> 0x0068 }
            r4 = -1;
            r6 = 0;
            r0.<init>(r1, r2, r4, r6);	 Catch:{ all -> 0x0068 }
            r4 = r10.open(r0);	 Catch:{ all -> 0x0068 }
            r0 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1));
            if (r0 == 0) goto L_0x0025;
        L_0x0024:
            r4 = r4 + r2;
        L_0x0025:
            r0 = new com.hanista.mobogram.messenger.exoplayer.extractor.DefaultExtractorInput;	 Catch:{ all -> 0x0068 }
            r1 = r15.dataSource;	 Catch:{ all -> 0x0068 }
            r0.<init>(r1, r2, r4);	 Catch:{ all -> 0x0068 }
            r1 = r15.extractorHolder;	 Catch:{ all -> 0x007f }
            r2 = r1.selectExtractor(r0);	 Catch:{ all -> 0x007f }
            r1 = r15.pendingExtractorSeek;	 Catch:{ all -> 0x007f }
            if (r1 == 0) goto L_0x003c;
        L_0x0036:
            r2.seek();	 Catch:{ all -> 0x007f }
            r1 = 0;
            r15.pendingExtractorSeek = r1;	 Catch:{ all -> 0x007f }
        L_0x003c:
            r1 = r9;
        L_0x003d:
            if (r1 != 0) goto L_0x0052;
        L_0x003f:
            r3 = r15.loadCanceled;	 Catch:{ all -> 0x0085 }
            if (r3 != 0) goto L_0x0052;
        L_0x0043:
            r3 = r15.allocator;	 Catch:{ all -> 0x0085 }
            r4 = r15.requestedBufferSize;	 Catch:{ all -> 0x0085 }
            r3.blockWhileTotalBytesAllocatedExceeds(r4);	 Catch:{ all -> 0x0085 }
            r3 = r15.positionHolder;	 Catch:{ all -> 0x0085 }
            r9 = r2.read(r0, r3);	 Catch:{ all -> 0x0085 }
            r1 = r9;
            goto L_0x003d;
        L_0x0052:
            if (r1 != r11) goto L_0x005c;
        L_0x0054:
            r0 = r7;
        L_0x0055:
            r1 = r15.dataSource;
            r1.close();
            r9 = r0;
            goto L_0x0006;
        L_0x005c:
            if (r0 == 0) goto L_0x0066;
        L_0x005e:
            r2 = r15.positionHolder;
            r4 = r0.getPosition();
            r2.position = r4;
        L_0x0066:
            r0 = r1;
            goto L_0x0055;
        L_0x0068:
            r0 = move-exception;
            r1 = r8;
            r2 = r9;
        L_0x006b:
            if (r2 != r11) goto L_0x0073;
        L_0x006d:
            r1 = r15.dataSource;
            r1.close();
            throw r0;
        L_0x0073:
            if (r1 == 0) goto L_0x006d;
        L_0x0075:
            r2 = r15.positionHolder;
            r4 = r1.getPosition();
            r2.position = r4;
            goto L_0x006d;
        L_0x007e:
            return;
        L_0x007f:
            r1 = move-exception;
            r2 = r9;
            r14 = r0;
            r0 = r1;
            r1 = r14;
            goto L_0x006b;
        L_0x0085:
            r2 = move-exception;
            r14 = r2;
            r2 = r1;
            r1 = r0;
            r0 = r14;
            goto L_0x006b;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.hanista.mobogram.messenger.exoplayer.extractor.ExtractorSampleSource.ExtractingLoadable.load():void");
        }
    }

    private static final class ExtractorHolder {
        private Extractor extractor;
        private final ExtractorOutput extractorOutput;
        private final Extractor[] extractors;

        public ExtractorHolder(Extractor[] extractorArr, ExtractorOutput extractorOutput) {
            this.extractors = extractorArr;
            this.extractorOutput = extractorOutput;
        }

        public void release() {
            if (this.extractor != null) {
                this.extractor.release();
                this.extractor = null;
            }
        }

        public Extractor selectExtractor(ExtractorInput extractorInput) {
            if (this.extractor != null) {
                return this.extractor;
            }
            Extractor[] extractorArr = this.extractors;
            int length = extractorArr.length;
            int i = 0;
            loop0:
            while (i < length) {
                Extractor extractor = extractorArr[i];
                try {
                    if (extractor.sniff(extractorInput)) {
                        this.extractor = extractor;
                        extractorInput.resetPeekPosition();
                        break loop0;
                    }
                    i++;
                } catch (EOFException e) {
                    i++;
                } finally {
                    extractorInput.resetPeekPosition();
                }
            }
            if (this.extractor == null) {
                throw new UnrecognizedInputFormatException(this.extractors);
            }
            this.extractor.init(this.extractorOutput);
            return this.extractor;
        }
    }

    private class InternalTrackOutput extends DefaultTrackOutput {
        public InternalTrackOutput(Allocator allocator) {
            super(allocator);
        }

        public void sampleMetadata(long j, int i, int i2, int i3, byte[] bArr) {
            super.sampleMetadata(j, i, i2, i3, bArr);
            ExtractorSampleSource.this.extractedSampleCount = ExtractorSampleSource.this.extractedSampleCount + 1;
        }
    }

    public static final class UnrecognizedInputFormatException extends ParserException {
        public UnrecognizedInputFormatException(Extractor[] extractorArr) {
            super("None of the available extractors (" + Util.getCommaDelimitedSimpleClassNames(extractorArr) + ") could read the stream.");
        }
    }

    static {
        DEFAULT_EXTRACTOR_CLASSES = new ArrayList();
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.extractor.webm.WebmExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.extractor.mp4.FragmentedMp4Extractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e2) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.extractor.mp4.Mp4Extractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e3) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.extractor.mp3.Mp3Extractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e4) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.extractor.ts.AdtsExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e5) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.extractor.ts.TsExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e6) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.extractor.flv.FlvExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e7) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.extractor.ogg.OggExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e8) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.extractor.ts.PsExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e9) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.extractor.wav.WavExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e10) {
        }
        try {
            DEFAULT_EXTRACTOR_CLASSES.add(Class.forName("com.hanista.mobogram.messenger.exoplayer.ext.flac.FlacExtractor").asSubclass(Extractor.class));
        } catch (ClassNotFoundException e11) {
        }
    }

    public ExtractorSampleSource(Uri uri, DataSource dataSource, Allocator allocator, int i, int i2, Handler handler, EventListener eventListener, int i3, Extractor... extractorArr) {
        this.uri = uri;
        this.dataSource = dataSource;
        this.eventListener = eventListener;
        this.eventHandler = handler;
        this.eventSourceId = i3;
        this.allocator = allocator;
        this.requestedBufferSize = i;
        this.minLoadableRetryCount = i2;
        if (extractorArr == null || extractorArr.length == 0) {
            extractorArr = new Extractor[DEFAULT_EXTRACTOR_CLASSES.size()];
            int i4 = 0;
            while (i4 < extractorArr.length) {
                try {
                    extractorArr[i4] = (Extractor) ((Class) DEFAULT_EXTRACTOR_CLASSES.get(i4)).newInstance();
                    i4++;
                } catch (Throwable e) {
                    throw new IllegalStateException("Unexpected error creating default extractor", e);
                } catch (Throwable e2) {
                    throw new IllegalStateException("Unexpected error creating default extractor", e2);
                }
            }
        }
        this.extractorHolder = new ExtractorHolder(extractorArr, this);
        this.sampleQueues = new SparseArray();
        this.pendingResetPositionUs = NO_RESET_PENDING;
    }

    public ExtractorSampleSource(Uri uri, DataSource dataSource, Allocator allocator, int i, int i2, Extractor... extractorArr) {
        this(uri, dataSource, allocator, i, i2, null, null, 0, extractorArr);
    }

    public ExtractorSampleSource(Uri uri, DataSource dataSource, Allocator allocator, int i, Handler handler, EventListener eventListener, int i2, Extractor... extractorArr) {
        this(uri, dataSource, allocator, i, MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA, handler, eventListener, i2, extractorArr);
    }

    public ExtractorSampleSource(Uri uri, DataSource dataSource, Allocator allocator, int i, Extractor... extractorArr) {
        this(uri, dataSource, allocator, i, MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA, extractorArr);
    }

    private void clearState() {
        for (int i = 0; i < this.sampleQueues.size(); i++) {
            ((InternalTrackOutput) this.sampleQueues.valueAt(i)).clear();
        }
        this.loadable = null;
        this.currentLoadableException = null;
        this.currentLoadableExceptionCount = 0;
    }

    private ExtractingLoadable createLoadableFromPositionUs(long j) {
        return new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this.allocator, this.requestedBufferSize, this.seekMap.getPosition(j));
    }

    private ExtractingLoadable createLoadableFromStart() {
        return new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this.allocator, this.requestedBufferSize, 0);
    }

    private void discardSamplesForDisabledTracks(long j) {
        for (int i = 0; i < this.trackEnabledStates.length; i++) {
            if (!this.trackEnabledStates[i]) {
                ((InternalTrackOutput) this.sampleQueues.valueAt(i)).discardUntil(j);
            }
        }
    }

    private long getRetryDelayMillis(long j) {
        return Math.min((j - 1) * 1000, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);
    }

    private boolean haveFormatsForAllTracks() {
        for (int i = 0; i < this.sampleQueues.size(); i++) {
            if (!((InternalTrackOutput) this.sampleQueues.valueAt(i)).hasFormat()) {
                return false;
            }
        }
        return true;
    }

    private boolean isCurrentLoadableExceptionFatal() {
        return this.currentLoadableException instanceof UnrecognizedInputFormatException;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != NO_RESET_PENDING;
    }

    private void maybeStartLoading() {
        int i = 0;
        if (!this.loadingFinished && !this.loader.isLoading()) {
            if (this.currentLoadableException == null) {
                this.sampleTimeOffsetUs = 0;
                this.havePendingNextSampleUs = false;
                if (this.prepared) {
                    Assertions.checkState(isPendingReset());
                    if (this.maxTrackDurationUs == -1 || this.pendingResetPositionUs < this.maxTrackDurationUs) {
                        this.loadable = createLoadableFromPositionUs(this.pendingResetPositionUs);
                        this.pendingResetPositionUs = NO_RESET_PENDING;
                    } else {
                        this.loadingFinished = true;
                        this.pendingResetPositionUs = NO_RESET_PENDING;
                        return;
                    }
                }
                this.loadable = createLoadableFromStart();
                this.extractedSampleCountAtStartOfLoad = this.extractedSampleCount;
                this.loader.startLoading(this.loadable, this);
            } else if (!isCurrentLoadableExceptionFatal()) {
                Assertions.checkState(this.loadable != null);
                if (SystemClock.elapsedRealtime() - this.currentLoadableExceptionTimestamp >= getRetryDelayMillis((long) this.currentLoadableExceptionCount)) {
                    this.currentLoadableException = null;
                    if (!this.prepared) {
                        while (i < this.sampleQueues.size()) {
                            ((InternalTrackOutput) this.sampleQueues.valueAt(i)).clear();
                            i++;
                        }
                        this.loadable = createLoadableFromStart();
                    } else if (!this.seekMap.isSeekable() && this.maxTrackDurationUs == -1) {
                        while (i < this.sampleQueues.size()) {
                            ((InternalTrackOutput) this.sampleQueues.valueAt(i)).clear();
                            i++;
                        }
                        this.loadable = createLoadableFromStart();
                        this.pendingNextSampleUs = this.downstreamPositionUs;
                        this.havePendingNextSampleUs = true;
                    }
                    this.extractedSampleCountAtStartOfLoad = this.extractedSampleCount;
                    this.loader.startLoading(this.loadable, this);
                }
            }
        }
    }

    private void notifyLoadError(IOException iOException) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new C07342(iOException));
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

    public boolean continueBuffering(int i, long j) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.trackEnabledStates[i]);
        this.downstreamPositionUs = j;
        discardSamplesForDisabledTracks(this.downstreamPositionUs);
        if (this.loadingFinished) {
            return true;
        }
        maybeStartLoading();
        if (isPendingReset()) {
            return false;
        }
        return !((InternalTrackOutput) this.sampleQueues.valueAt(i)).isEmpty();
    }

    public void disable(int i) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.trackEnabledStates[i]);
        this.enabledTrackCount += MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA;
        this.trackEnabledStates[i] = false;
        if (this.enabledTrackCount == 0) {
            this.downstreamPositionUs = NO_RESET_PENDING;
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
                return;
            }
            clearState();
            this.allocator.trim(0);
        }
    }

    public void drmInitData(DrmInitData drmInitData) {
        this.drmInitData = drmInitData;
    }

    public void enable(int i, long j) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(!this.trackEnabledStates[i]);
        this.enabledTrackCount++;
        this.trackEnabledStates[i] = true;
        this.pendingMediaFormat[i] = true;
        this.pendingDiscontinuities[i] = false;
        if (this.enabledTrackCount == 1) {
            if (!this.seekMap.isSeekable()) {
                j = 0;
            }
            this.downstreamPositionUs = j;
            this.lastSeekPositionUs = j;
            restartFrom(j);
        }
    }

    public void endTracks() {
        this.tracksBuilt = true;
    }

    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return -3;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long j = NO_RESET_PENDING;
        for (int i = 0; i < this.sampleQueues.size(); i++) {
            j = Math.max(j, ((InternalTrackOutput) this.sampleQueues.valueAt(i)).getLargestParsedTimestampUs());
        }
        return j == NO_RESET_PENDING ? this.downstreamPositionUs : j;
    }

    public MediaFormat getFormat(int i) {
        Assertions.checkState(this.prepared);
        return this.mediaFormats[i];
    }

    public int getTrackCount() {
        return this.sampleQueues.size();
    }

    public void maybeThrowError() {
        if (this.currentLoadableException != null) {
            if (isCurrentLoadableExceptionFatal()) {
                throw this.currentLoadableException;
            }
            int i = this.minLoadableRetryCount != MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA ? this.minLoadableRetryCount : (this.seekMap == null || this.seekMap.isSeekable()) ? DEFAULT_MIN_LOADABLE_RETRY_COUNT_ON_DEMAND : DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE;
            if (this.currentLoadableExceptionCount > i) {
                throw this.currentLoadableException;
            }
        }
    }

    public void onLoadCanceled(Loadable loadable) {
        if (this.enabledTrackCount > 0) {
            restartFrom(this.pendingResetPositionUs);
            return;
        }
        clearState();
        this.allocator.trim(0);
    }

    public void onLoadCompleted(Loadable loadable) {
        this.loadingFinished = true;
    }

    public void onLoadError(Loadable loadable, IOException iOException) {
        this.currentLoadableException = iOException;
        this.currentLoadableExceptionCount = this.extractedSampleCount > this.extractedSampleCountAtStartOfLoad ? 1 : this.currentLoadableExceptionCount + 1;
        this.currentLoadableExceptionTimestamp = SystemClock.elapsedRealtime();
        notifyLoadError(iOException);
        maybeStartLoading();
    }

    public boolean prepare(long j) {
        if (this.prepared) {
            return true;
        }
        if (this.loader == null) {
            this.loader = new Loader("Loader:ExtractorSampleSource");
        }
        maybeStartLoading();
        if (this.seekMap == null || !this.tracksBuilt || !haveFormatsForAllTracks()) {
            return false;
        }
        int size = this.sampleQueues.size();
        this.trackEnabledStates = new boolean[size];
        this.pendingDiscontinuities = new boolean[size];
        this.pendingMediaFormat = new boolean[size];
        this.mediaFormats = new MediaFormat[size];
        this.maxTrackDurationUs = -1;
        for (int i = 0; i < size; i++) {
            MediaFormat format = ((InternalTrackOutput) this.sampleQueues.valueAt(i)).getFormat();
            this.mediaFormats[i] = format;
            if (format.durationUs != -1 && format.durationUs > this.maxTrackDurationUs) {
                this.maxTrackDurationUs = format.durationUs;
            }
        }
        this.prepared = true;
        return true;
    }

    public int readData(int i, long j, MediaFormatHolder mediaFormatHolder, SampleHolder sampleHolder) {
        this.downstreamPositionUs = j;
        if (this.pendingDiscontinuities[i] || isPendingReset()) {
            return -2;
        }
        InternalTrackOutput internalTrackOutput = (InternalTrackOutput) this.sampleQueues.valueAt(i);
        if (this.pendingMediaFormat[i]) {
            mediaFormatHolder.format = internalTrackOutput.getFormat();
            mediaFormatHolder.drmInitData = this.drmInitData;
            this.pendingMediaFormat[i] = false;
            return -4;
        } else if (!internalTrackOutput.getSample(sampleHolder)) {
            return this.loadingFinished ? MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA : -2;
        } else {
            sampleHolder.flags = ((sampleHolder.timeUs > this.lastSeekPositionUs ? 1 : (sampleHolder.timeUs == this.lastSeekPositionUs ? 0 : -1)) < 0 ? C0700C.SAMPLE_FLAG_DECODE_ONLY : 0) | sampleHolder.flags;
            if (this.havePendingNextSampleUs) {
                this.sampleTimeOffsetUs = this.pendingNextSampleUs - sampleHolder.timeUs;
                this.havePendingNextSampleUs = false;
            }
            sampleHolder.timeUs += this.sampleTimeOffsetUs;
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
        this.remainingReleaseCount++;
        return this;
    }

    public void release() {
        Assertions.checkState(this.remainingReleaseCount > 0);
        int i = this.remainingReleaseCount + MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA;
        this.remainingReleaseCount = i;
        if (i == 0 && this.loader != null) {
            this.loader.release(new C07331());
            this.loader = null;
        }
    }

    public void seekMap(SeekMap seekMap) {
        this.seekMap = seekMap;
    }

    public void seekToUs(long j) {
        int i = 0;
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.enabledTrackCount > 0);
        if (!this.seekMap.isSeekable()) {
            j = 0;
        }
        long j2 = isPendingReset() ? this.pendingResetPositionUs : this.downstreamPositionUs;
        this.downstreamPositionUs = j;
        this.lastSeekPositionUs = j;
        if (j2 != j) {
            int i2 = 0;
            int i3 = !isPendingReset();
            while (i3 != 0 && i2 < this.sampleQueues.size()) {
                i3 &= ((InternalTrackOutput) this.sampleQueues.valueAt(i2)).skipToKeyframeBefore(j);
                i2++;
            }
            if (i3 == 0) {
                restartFrom(j);
            }
            while (i < this.pendingDiscontinuities.length) {
                this.pendingDiscontinuities[i] = true;
                i++;
            }
        }
    }

    public TrackOutput track(int i) {
        InternalTrackOutput internalTrackOutput = (InternalTrackOutput) this.sampleQueues.get(i);
        if (internalTrackOutput != null) {
            return internalTrackOutput;
        }
        TrackOutput internalTrackOutput2 = new InternalTrackOutput(this.allocator);
        this.sampleQueues.put(i, internalTrackOutput2);
        return internalTrackOutput2;
    }
}

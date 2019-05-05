package com.hanista.mobogram.messenger.exoplayer.smoothstreaming;

import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Base64;
import android.util.SparseArray;
import com.hanista.mobogram.messenger.exoplayer.BehindLiveWindowException;
import com.hanista.mobogram.messenger.exoplayer.MediaFormat;
import com.hanista.mobogram.messenger.exoplayer.chunk.Chunk;
import com.hanista.mobogram.messenger.exoplayer.chunk.ChunkExtractorWrapper;
import com.hanista.mobogram.messenger.exoplayer.chunk.ChunkOperationHolder;
import com.hanista.mobogram.messenger.exoplayer.chunk.ChunkSource;
import com.hanista.mobogram.messenger.exoplayer.chunk.ContainerMediaChunk;
import com.hanista.mobogram.messenger.exoplayer.chunk.Format;
import com.hanista.mobogram.messenger.exoplayer.chunk.Format.DecreasingBandwidthComparator;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatEvaluator.Evaluation;
import com.hanista.mobogram.messenger.exoplayer.chunk.MediaChunk;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData.Mapped;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import com.hanista.mobogram.messenger.exoplayer.extractor.Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.mp4.FragmentedMp4Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.mp4.Track;
import com.hanista.mobogram.messenger.exoplayer.extractor.mp4.TrackEncryptionBox;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource;
import com.hanista.mobogram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.ProtectionElement;
import com.hanista.mobogram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.StreamElement;
import com.hanista.mobogram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.TrackElement;
import com.hanista.mobogram.messenger.exoplayer.smoothstreaming.SmoothStreamingTrackSelector.Output;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.CodecSpecificDataUtil;
import com.hanista.mobogram.messenger.exoplayer.util.ManifestFetcher;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.ui.Components.VideoPlayer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SmoothStreamingChunkSource implements ChunkSource, Output {
    private static final int INITIALIZATION_VECTOR_SIZE = 8;
    private static final int MINIMUM_MANIFEST_REFRESH_PERIOD_MS = 5000;
    private final FormatEvaluator adaptiveFormatEvaluator;
    private SmoothStreamingManifest currentManifest;
    private int currentManifestChunkOffset;
    private final DataSource dataSource;
    private final Mapped drmInitData;
    private ExposedTrack enabledTrack;
    private final Evaluation evaluation;
    private final SparseArray<ChunkExtractorWrapper> extractorWrappers;
    private IOException fatalError;
    private final boolean live;
    private final long liveEdgeLatencyUs;
    private final ManifestFetcher<SmoothStreamingManifest> manifestFetcher;
    private final SparseArray<MediaFormat> mediaFormats;
    private boolean needManifestRefresh;
    private boolean prepareCalled;
    private final TrackEncryptionBox[] trackEncryptionBoxes;
    private final SmoothStreamingTrackSelector trackSelector;
    private final ArrayList<ExposedTrack> tracks;

    private static final class ExposedTrack {
        private final Format[] adaptiveFormats;
        private final int adaptiveMaxHeight;
        private final int adaptiveMaxWidth;
        private final int elementIndex;
        private final Format fixedFormat;
        public final MediaFormat trackFormat;

        public ExposedTrack(MediaFormat mediaFormat, int i, Format format) {
            this.trackFormat = mediaFormat;
            this.elementIndex = i;
            this.fixedFormat = format;
            this.adaptiveFormats = null;
            this.adaptiveMaxWidth = -1;
            this.adaptiveMaxHeight = -1;
        }

        public ExposedTrack(MediaFormat mediaFormat, int i, Format[] formatArr, int i2, int i3) {
            this.trackFormat = mediaFormat;
            this.elementIndex = i;
            this.adaptiveFormats = formatArr;
            this.adaptiveMaxWidth = i2;
            this.adaptiveMaxHeight = i3;
            this.fixedFormat = null;
        }

        public boolean isAdaptive() {
            return this.adaptiveFormats != null;
        }
    }

    public SmoothStreamingChunkSource(SmoothStreamingManifest smoothStreamingManifest, SmoothStreamingTrackSelector smoothStreamingTrackSelector, DataSource dataSource, FormatEvaluator formatEvaluator) {
        this(null, smoothStreamingManifest, smoothStreamingTrackSelector, dataSource, formatEvaluator, 0);
    }

    private SmoothStreamingChunkSource(ManifestFetcher<SmoothStreamingManifest> manifestFetcher, SmoothStreamingManifest smoothStreamingManifest, SmoothStreamingTrackSelector smoothStreamingTrackSelector, DataSource dataSource, FormatEvaluator formatEvaluator, long j) {
        this.manifestFetcher = manifestFetcher;
        this.currentManifest = smoothStreamingManifest;
        this.trackSelector = smoothStreamingTrackSelector;
        this.dataSource = dataSource;
        this.adaptiveFormatEvaluator = formatEvaluator;
        this.liveEdgeLatencyUs = 1000 * j;
        this.evaluation = new Evaluation();
        this.tracks = new ArrayList();
        this.extractorWrappers = new SparseArray();
        this.mediaFormats = new SparseArray();
        this.live = smoothStreamingManifest.isLive;
        ProtectionElement protectionElement = smoothStreamingManifest.protectionElement;
        if (protectionElement != null) {
            byte[] protectionElementKeyId = getProtectionElementKeyId(protectionElement.data);
            this.trackEncryptionBoxes = new TrackEncryptionBox[1];
            this.trackEncryptionBoxes[0] = new TrackEncryptionBox(true, INITIALIZATION_VECTOR_SIZE, protectionElementKeyId);
            this.drmInitData = new Mapped();
            this.drmInitData.put(protectionElement.uuid, new SchemeInitData(MimeTypes.VIDEO_MP4, protectionElement.data));
            return;
        }
        this.trackEncryptionBoxes = null;
        this.drmInitData = null;
    }

    public SmoothStreamingChunkSource(ManifestFetcher<SmoothStreamingManifest> manifestFetcher, SmoothStreamingTrackSelector smoothStreamingTrackSelector, DataSource dataSource, FormatEvaluator formatEvaluator, long j) {
        this(manifestFetcher, (SmoothStreamingManifest) manifestFetcher.getManifest(), smoothStreamingTrackSelector, dataSource, formatEvaluator, j);
    }

    private static long getLiveSeekPosition(SmoothStreamingManifest smoothStreamingManifest, long j) {
        long j2 = Long.MIN_VALUE;
        for (StreamElement streamElement : smoothStreamingManifest.streamElements) {
            if (streamElement.chunkCount > 0) {
                j2 = Math.max(j2, streamElement.getStartTimeUs(streamElement.chunkCount - 1) + streamElement.getChunkDurationUs(streamElement.chunkCount - 1));
            }
        }
        return j2 - j;
    }

    private static int getManifestTrackIndex(StreamElement streamElement, Format format) {
        TrackElement[] trackElementArr = streamElement.tracks;
        for (int i = 0; i < trackElementArr.length; i++) {
            if (trackElementArr[i].format.equals(format)) {
                return i;
            }
        }
        throw new IllegalStateException("Invalid format: " + format);
    }

    private static int getManifestTrackKey(int i, int i2) {
        boolean z = i <= AccessibilityNodeInfoCompat.ACTION_CUT && i2 <= AccessibilityNodeInfoCompat.ACTION_CUT;
        Assertions.checkState(z);
        return (i << 16) | i2;
    }

    private static byte[] getProtectionElementKeyId(byte[] bArr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bArr.length; i += 2) {
            stringBuilder.append((char) bArr[i]);
        }
        String stringBuilder2 = stringBuilder.toString();
        byte[] decode = Base64.decode(stringBuilder2.substring(stringBuilder2.indexOf("<KID>") + 5, stringBuilder2.indexOf("</KID>")), 0);
        swap(decode, 0, 3);
        swap(decode, 1, 2);
        swap(decode, 4, 5);
        swap(decode, 6, 7);
        return decode;
    }

    private MediaFormat initManifestTrack(SmoothStreamingManifest smoothStreamingManifest, int i, int i2) {
        int manifestTrackKey = getManifestTrackKey(i, i2);
        MediaFormat mediaFormat = (MediaFormat) this.mediaFormats.get(manifestTrackKey);
        if (mediaFormat != null) {
            return mediaFormat;
        }
        MediaFormat createAudioFormat;
        int i3;
        long j = this.live ? -1 : smoothStreamingManifest.durationUs;
        StreamElement streamElement = smoothStreamingManifest.streamElements[i];
        Format format = streamElement.tracks[i2].format;
        Object[] objArr = streamElement.tracks[i2].csd;
        switch (streamElement.type) {
            case VideoPlayer.TRACK_DEFAULT /*0*/:
                createAudioFormat = MediaFormat.createAudioFormat(format.id, format.mimeType, format.bitrate, -1, j, format.audioChannels, format.audioSamplingRate, objArr != null ? Arrays.asList(objArr) : Collections.singletonList(CodecSpecificDataUtil.buildAacAudioSpecificConfig(format.audioSamplingRate, format.audioChannels)), format.language);
                i3 = Track.TYPE_soun;
                break;
            case VideoPlayer.TYPE_AUDIO /*1*/:
                createAudioFormat = MediaFormat.createVideoFormat(format.id, format.mimeType, format.bitrate, -1, j, format.width, format.height, Arrays.asList(objArr));
                i3 = Track.TYPE_vide;
                break;
            case VideoPlayer.STATE_PREPARING /*2*/:
                createAudioFormat = MediaFormat.createTextFormat(format.id, format.mimeType, format.bitrate, j, format.language);
                i3 = Track.TYPE_text;
                break;
            default:
                throw new IllegalStateException("Invalid type: " + streamElement.type);
        }
        Extractor fragmentedMp4Extractor = new FragmentedMp4Extractor(3, new Track(i2, i3, streamElement.timescale, -1, j, createAudioFormat, this.trackEncryptionBoxes, i3 == Track.TYPE_vide ? 4 : -1, null, null));
        this.mediaFormats.put(manifestTrackKey, createAudioFormat);
        this.extractorWrappers.put(manifestTrackKey, new ChunkExtractorWrapper(fragmentedMp4Extractor));
        return createAudioFormat;
    }

    private static MediaChunk newMediaChunk(Format format, Uri uri, String str, ChunkExtractorWrapper chunkExtractorWrapper, DrmInitData drmInitData, DataSource dataSource, int i, long j, long j2, int i2, MediaFormat mediaFormat, int i3, int i4) {
        return new ContainerMediaChunk(dataSource, new DataSpec(uri, 0, -1, str), i2, format, j, j2, i, j, chunkExtractorWrapper, mediaFormat, i3, i4, drmInitData, true, -1);
    }

    private static void swap(byte[] bArr, int i, int i2) {
        byte b = bArr[i];
        bArr[i] = bArr[i2];
        bArr[i2] = b;
    }

    public void adaptiveTrack(SmoothStreamingManifest smoothStreamingManifest, int i, int[] iArr) {
        int i2 = -1;
        if (this.adaptiveFormatEvaluator != null) {
            StreamElement streamElement = smoothStreamingManifest.streamElements[i];
            Format[] formatArr = new Format[iArr.length];
            int i3 = -1;
            MediaFormat mediaFormat = null;
            for (int i4 = 0; i4 < formatArr.length; i4++) {
                int i5 = iArr[i4];
                formatArr[i4] = streamElement.tracks[i5].format;
                MediaFormat initManifestTrack = initManifestTrack(smoothStreamingManifest, i, i5);
                if (mediaFormat == null || initManifestTrack.height > i2) {
                    mediaFormat = initManifestTrack;
                }
                i3 = Math.max(i3, initManifestTrack.width);
                i2 = Math.max(i2, initManifestTrack.height);
            }
            Arrays.sort(formatArr, new DecreasingBandwidthComparator());
            this.tracks.add(new ExposedTrack(mediaFormat.copyAsAdaptive(null), i, formatArr, i3, i2));
        }
    }

    public void continueBuffering(long j) {
        if (this.manifestFetcher != null && this.currentManifest.isLive && this.fatalError == null) {
            SmoothStreamingManifest smoothStreamingManifest = (SmoothStreamingManifest) this.manifestFetcher.getManifest();
            if (!(this.currentManifest == smoothStreamingManifest || smoothStreamingManifest == null)) {
                StreamElement streamElement = this.currentManifest.streamElements[this.enabledTrack.elementIndex];
                int i = streamElement.chunkCount;
                StreamElement streamElement2 = smoothStreamingManifest.streamElements[this.enabledTrack.elementIndex];
                if (i == 0 || streamElement2.chunkCount == 0) {
                    this.currentManifestChunkOffset += i;
                } else {
                    long startTimeUs = streamElement.getStartTimeUs(i - 1) + streamElement.getChunkDurationUs(i - 1);
                    long startTimeUs2 = streamElement2.getStartTimeUs(0);
                    if (startTimeUs <= startTimeUs2) {
                        this.currentManifestChunkOffset += i;
                    } else {
                        this.currentManifestChunkOffset = streamElement.getChunkIndex(startTimeUs2) + this.currentManifestChunkOffset;
                    }
                }
                this.currentManifest = smoothStreamingManifest;
                this.needManifestRefresh = false;
            }
            if (this.needManifestRefresh && SystemClock.elapsedRealtime() > this.manifestFetcher.getManifestLoadStartTimestamp() + HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS) {
                this.manifestFetcher.requestRefresh();
            }
        }
    }

    public void disable(List<? extends MediaChunk> list) {
        if (this.enabledTrack.isAdaptive()) {
            this.adaptiveFormatEvaluator.disable();
        }
        if (this.manifestFetcher != null) {
            this.manifestFetcher.disable();
        }
        this.evaluation.format = null;
        this.fatalError = null;
    }

    public void enable(int i) {
        this.enabledTrack = (ExposedTrack) this.tracks.get(i);
        if (this.enabledTrack.isAdaptive()) {
            this.adaptiveFormatEvaluator.enable();
        }
        if (this.manifestFetcher != null) {
            this.manifestFetcher.enable();
        }
    }

    public void fixedTrack(SmoothStreamingManifest smoothStreamingManifest, int i, int i2) {
        this.tracks.add(new ExposedTrack(initManifestTrack(smoothStreamingManifest, i, i2), i, smoothStreamingManifest.streamElements[i].tracks[i2].format));
    }

    public final void getChunkOperation(List<? extends MediaChunk> list, long j, ChunkOperationHolder chunkOperationHolder) {
        if (this.fatalError != null) {
            chunkOperationHolder.chunk = null;
            return;
        }
        this.evaluation.queueSize = list.size();
        if (this.enabledTrack.isAdaptive()) {
            this.adaptiveFormatEvaluator.evaluate(list, j, this.enabledTrack.adaptiveFormats, this.evaluation);
        } else {
            this.evaluation.format = this.enabledTrack.fixedFormat;
            this.evaluation.trigger = 2;
        }
        Format format = this.evaluation.format;
        chunkOperationHolder.queueSize = this.evaluation.queueSize;
        if (format == null) {
            chunkOperationHolder.chunk = null;
        } else if (chunkOperationHolder.queueSize != list.size() || chunkOperationHolder.chunk == null || !chunkOperationHolder.chunk.format.equals(format)) {
            chunkOperationHolder.chunk = null;
            StreamElement streamElement = this.currentManifest.streamElements[this.enabledTrack.elementIndex];
            if (streamElement.chunkCount != 0) {
                int chunkIndex;
                if (list.isEmpty()) {
                    if (this.live) {
                        j = getLiveSeekPosition(this.currentManifest, this.liveEdgeLatencyUs);
                    }
                    chunkIndex = streamElement.getChunkIndex(j);
                } else {
                    chunkIndex = (((MediaChunk) list.get(chunkOperationHolder.queueSize - 1)).chunkIndex + 1) - this.currentManifestChunkOffset;
                }
                if (!this.live || chunkIndex >= 0) {
                    if (this.currentManifest.isLive) {
                        if (chunkIndex >= streamElement.chunkCount) {
                            this.needManifestRefresh = true;
                            return;
                        } else if (chunkIndex == streamElement.chunkCount - 1) {
                            this.needManifestRefresh = true;
                        }
                    } else if (chunkIndex >= streamElement.chunkCount) {
                        chunkOperationHolder.endOfStream = true;
                        return;
                    }
                    Object obj = (this.currentManifest.isLive || chunkIndex != streamElement.chunkCount - 1) ? null : 1;
                    long startTimeUs = streamElement.getStartTimeUs(chunkIndex);
                    long chunkDurationUs = obj != null ? -1 : startTimeUs + streamElement.getChunkDurationUs(chunkIndex);
                    int i = chunkIndex + this.currentManifestChunkOffset;
                    int manifestTrackIndex = getManifestTrackIndex(streamElement, format);
                    int manifestTrackKey = getManifestTrackKey(this.enabledTrack.elementIndex, manifestTrackIndex);
                    chunkOperationHolder.chunk = newMediaChunk(format, streamElement.buildRequestUri(manifestTrackIndex, chunkIndex), null, (ChunkExtractorWrapper) this.extractorWrappers.get(manifestTrackKey), this.drmInitData, this.dataSource, i, startTimeUs, chunkDurationUs, this.evaluation.trigger, (MediaFormat) this.mediaFormats.get(manifestTrackKey), this.enabledTrack.adaptiveMaxWidth, this.enabledTrack.adaptiveMaxHeight);
                    return;
                }
                this.fatalError = new BehindLiveWindowException();
            } else if (this.currentManifest.isLive) {
                this.needManifestRefresh = true;
            } else {
                chunkOperationHolder.endOfStream = true;
            }
        }
    }

    public final MediaFormat getFormat(int i) {
        return ((ExposedTrack) this.tracks.get(i)).trackFormat;
    }

    public int getTrackCount() {
        return this.tracks.size();
    }

    public void maybeThrowError() {
        if (this.fatalError != null) {
            throw this.fatalError;
        }
        this.manifestFetcher.maybeThrowError();
    }

    public void onChunkLoadCompleted(Chunk chunk) {
    }

    public void onChunkLoadError(Chunk chunk, Exception exception) {
    }

    public boolean prepare() {
        if (!this.prepareCalled) {
            this.prepareCalled = true;
            try {
                this.trackSelector.selectTracks(this.currentManifest, this);
            } catch (IOException e) {
                this.fatalError = e;
            }
        }
        return this.fatalError == null;
    }
}

package com.hanista.mobogram.messenger.exoplayer.hls;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.exoplayer.BehindLiveWindowException;
import com.hanista.mobogram.messenger.exoplayer.chunk.Chunk;
import com.hanista.mobogram.messenger.exoplayer.chunk.ChunkOperationHolder;
import com.hanista.mobogram.messenger.exoplayer.chunk.DataChunk;
import com.hanista.mobogram.messenger.exoplayer.chunk.Format;
import com.hanista.mobogram.messenger.exoplayer.chunk.Format.DecreasingBandwidthComparator;
import com.hanista.mobogram.messenger.exoplayer.extractor.mp3.Mp3Extractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.AdtsExtractor;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.TsExtractor;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsMediaPlaylist.Segment;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsTrackSelector.Output;
import com.hanista.mobogram.messenger.exoplayer.upstream.BandwidthMeter;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSource;
import com.hanista.mobogram.messenger.exoplayer.upstream.DataSpec;
import com.hanista.mobogram.messenger.exoplayer.upstream.HttpDataSource.InvalidResponseCodeException;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.UriUtil;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import com.hanista.mobogram.tgnet.ConnectionsManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class HlsChunkSource implements Output {
    private static final String AAC_FILE_EXTENSION = ".aac";
    private static final float BANDWIDTH_FRACTION = 0.8f;
    public static final long DEFAULT_MAX_BUFFER_TO_SWITCH_DOWN_MS = 20000;
    public static final long DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS = 5000;
    public static final long DEFAULT_PLAYLIST_BLACKLIST_MS = 60000;
    private static final double LIVE_VARIANT_SWITCH_SAFETY_EXTRA_SECS = 2.0d;
    private static final String MP3_FILE_EXTENSION = ".mp3";
    private static final String TAG = "HlsChunkSource";
    private static final String VTT_FILE_EXTENSION = ".vtt";
    private static final String WEBVTT_FILE_EXTENSION = ".webvtt";
    private final BandwidthMeter bandwidthMeter;
    private final String baseUri;
    private final DataSource dataSource;
    private long durationUs;
    private byte[] encryptionIv;
    private String encryptionIvString;
    private byte[] encryptionKey;
    private Uri encryptionKeyUri;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private IOException fatalError;
    private final boolean isMaster;
    private boolean live;
    private final HlsMasterPlaylist masterPlaylist;
    private final long maxBufferDurationToSwitchDownUs;
    private final long minBufferDurationToSwitchUpUs;
    private final HlsPlaylistParser playlistParser;
    private boolean prepareCalled;
    private byte[] scratchSpace;
    private int selectedTrackIndex;
    private int selectedVariantIndex;
    private final PtsTimestampAdjusterProvider timestampAdjusterProvider;
    private final HlsTrackSelector trackSelector;
    private final ArrayList<ExposedTrack> tracks;
    private long[] variantBlacklistTimes;
    private long[] variantLastPlaylistLoadTimesMs;
    private HlsMediaPlaylist[] variantPlaylists;
    private Variant[] variants;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource.1 */
    class C07411 implements Runnable {
        final /* synthetic */ byte[] val$rawResponse;

        C07411(byte[] bArr) {
            this.val$rawResponse = bArr;
        }

        public void run() {
            HlsChunkSource.this.eventListener.onMediaPlaylistLoadCompleted(this.val$rawResponse);
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.hls.HlsChunkSource.2 */
    class C07422 implements Comparator<Variant> {
        private final Comparator<Format> formatComparator;

        C07422() {
            this.formatComparator = new DecreasingBandwidthComparator();
        }

        public int compare(Variant variant, Variant variant2) {
            return this.formatComparator.compare(variant.format, variant2.format);
        }
    }

    private static final class EncryptionKeyChunk extends DataChunk {
        public final String iv;
        private byte[] result;
        public final int variantIndex;

        public EncryptionKeyChunk(DataSource dataSource, DataSpec dataSpec, byte[] bArr, String str, int i) {
            super(dataSource, dataSpec, 3, 0, null, -1, bArr);
            this.iv = str;
            this.variantIndex = i;
        }

        protected void consume(byte[] bArr, int i) {
            this.result = Arrays.copyOf(bArr, i);
        }

        public byte[] getResult() {
            return this.result;
        }
    }

    public interface EventListener {
        void onMediaPlaylistLoadCompleted(byte[] bArr);
    }

    private static final class ExposedTrack {
        private final int adaptiveMaxHeight;
        private final int adaptiveMaxWidth;
        private final int defaultVariantIndex;
        private final Variant[] variants;

        public ExposedTrack(Variant variant) {
            this.variants = new Variant[]{variant};
            this.defaultVariantIndex = 0;
            this.adaptiveMaxWidth = -1;
            this.adaptiveMaxHeight = -1;
        }

        public ExposedTrack(Variant[] variantArr, int i, int i2, int i3) {
            this.variants = variantArr;
            this.defaultVariantIndex = i;
            this.adaptiveMaxWidth = i2;
            this.adaptiveMaxHeight = i3;
        }
    }

    private static final class MediaPlaylistChunk extends DataChunk {
        private final HlsPlaylistParser playlistParser;
        private final String playlistUrl;
        private byte[] rawResponse;
        private HlsMediaPlaylist result;
        public final int variantIndex;

        public MediaPlaylistChunk(DataSource dataSource, DataSpec dataSpec, byte[] bArr, HlsPlaylistParser hlsPlaylistParser, int i, String str) {
            super(dataSource, dataSpec, 4, 0, null, -1, bArr);
            this.variantIndex = i;
            this.playlistParser = hlsPlaylistParser;
            this.playlistUrl = str;
        }

        protected void consume(byte[] bArr, int i) {
            this.rawResponse = Arrays.copyOf(bArr, i);
            this.result = (HlsMediaPlaylist) this.playlistParser.parse(this.playlistUrl, new ByteArrayInputStream(this.rawResponse));
        }

        public byte[] getRawResponse() {
            return this.rawResponse;
        }

        public HlsMediaPlaylist getResult() {
            return this.result;
        }
    }

    public HlsChunkSource(boolean z, DataSource dataSource, HlsPlaylist hlsPlaylist, HlsTrackSelector hlsTrackSelector, BandwidthMeter bandwidthMeter, PtsTimestampAdjusterProvider ptsTimestampAdjusterProvider) {
        this(z, dataSource, hlsPlaylist, hlsTrackSelector, bandwidthMeter, ptsTimestampAdjusterProvider, DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS, DEFAULT_MAX_BUFFER_TO_SWITCH_DOWN_MS, null, null);
    }

    public HlsChunkSource(boolean z, DataSource dataSource, HlsPlaylist hlsPlaylist, HlsTrackSelector hlsTrackSelector, BandwidthMeter bandwidthMeter, PtsTimestampAdjusterProvider ptsTimestampAdjusterProvider, long j, long j2) {
        this(z, dataSource, hlsPlaylist, hlsTrackSelector, bandwidthMeter, ptsTimestampAdjusterProvider, j, j2, null, null);
    }

    public HlsChunkSource(boolean z, DataSource dataSource, HlsPlaylist hlsPlaylist, HlsTrackSelector hlsTrackSelector, BandwidthMeter bandwidthMeter, PtsTimestampAdjusterProvider ptsTimestampAdjusterProvider, long j, long j2, Handler handler, EventListener eventListener) {
        this.isMaster = z;
        this.dataSource = dataSource;
        this.trackSelector = hlsTrackSelector;
        this.bandwidthMeter = bandwidthMeter;
        this.timestampAdjusterProvider = ptsTimestampAdjusterProvider;
        this.eventListener = eventListener;
        this.eventHandler = handler;
        this.minBufferDurationToSwitchUpUs = 1000 * j;
        this.maxBufferDurationToSwitchDownUs = 1000 * j2;
        this.baseUri = hlsPlaylist.baseUri;
        this.playlistParser = new HlsPlaylistParser();
        this.tracks = new ArrayList();
        if (hlsPlaylist.type == 0) {
            this.masterPlaylist = (HlsMasterPlaylist) hlsPlaylist;
            return;
        }
        Format format = new Format("0", MimeTypes.APPLICATION_M3U8, -1, -1, Face.UNCOMPUTED_PROBABILITY, -1, -1, -1, null, null);
        List arrayList = new ArrayList();
        arrayList.add(new Variant(this.baseUri, format));
        this.masterPlaylist = new HlsMasterPlaylist(this.baseUri, arrayList, Collections.emptyList(), Collections.emptyList(), null, null);
    }

    private boolean allVariantsBlacklisted() {
        for (long j : this.variantBlacklistTimes) {
            if (j == 0) {
                return false;
            }
        }
        return true;
    }

    private void clearEncryptionData() {
        this.encryptionKeyUri = null;
        this.encryptionKey = null;
        this.encryptionIvString = null;
        this.encryptionIv = null;
    }

    private void clearStaleBlacklistedVariants() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int i = 0;
        while (i < this.variantBlacklistTimes.length) {
            if (this.variantBlacklistTimes[i] != 0 && elapsedRealtime - this.variantBlacklistTimes[i] > DEFAULT_PLAYLIST_BLACKLIST_MS) {
                this.variantBlacklistTimes[i] = 0;
            }
            i++;
        }
    }

    private int getLiveNextChunkSequenceNumber(int i, int i2, int i3) {
        if (i2 == i3) {
            return i + 1;
        }
        int i4;
        HlsMediaPlaylist hlsMediaPlaylist = this.variantPlaylists[i2];
        HlsMediaPlaylist hlsMediaPlaylist2 = this.variantPlaylists[i3];
        double d = 0.0d;
        for (i4 = i - hlsMediaPlaylist.mediaSequence; i4 < hlsMediaPlaylist.segments.size(); i4++) {
            d += ((Segment) hlsMediaPlaylist.segments.get(i4)).durationSecs;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        d = ((d + (((double) (elapsedRealtime - this.variantLastPlaylistLoadTimesMs[i2])) / 1000.0d)) + LIVE_VARIANT_SWITCH_SAFETY_EXTRA_SECS) - (((double) (elapsedRealtime - this.variantLastPlaylistLoadTimesMs[i3])) / 1000.0d);
        if (d < 0.0d) {
            return (hlsMediaPlaylist2.mediaSequence + hlsMediaPlaylist2.segments.size()) + 1;
        }
        for (i4 = hlsMediaPlaylist2.segments.size() - 1; i4 >= 0; i4--) {
            d -= ((Segment) hlsMediaPlaylist2.segments.get(i4)).durationSecs;
            if (d < 0.0d) {
                return hlsMediaPlaylist2.mediaSequence + i4;
            }
        }
        return hlsMediaPlaylist2.mediaSequence - 1;
    }

    private int getLiveStartChunkSequenceNumber(int i) {
        HlsMediaPlaylist hlsMediaPlaylist = this.variantPlaylists[i];
        return (hlsMediaPlaylist.segments.size() > 3 ? hlsMediaPlaylist.segments.size() - 3 : 0) + hlsMediaPlaylist.mediaSequence;
    }

    private int getNextVariantIndex(TsChunk tsChunk, long j) {
        clearStaleBlacklistedVariants();
        long bitrateEstimate = this.bandwidthMeter.getBitrateEstimate();
        if (this.variantBlacklistTimes[this.selectedVariantIndex] != 0) {
            return getVariantIndexForBandwidth(bitrateEstimate);
        }
        if (tsChunk == null) {
            return this.selectedVariantIndex;
        }
        if (bitrateEstimate == -1) {
            return this.selectedVariantIndex;
        }
        int variantIndexForBandwidth = getVariantIndexForBandwidth(bitrateEstimate);
        if (variantIndexForBandwidth == this.selectedVariantIndex) {
            return this.selectedVariantIndex;
        }
        long adjustedEndTimeUs = (tsChunk.getAdjustedEndTimeUs() - tsChunk.getDurationUs()) - j;
        return this.variantBlacklistTimes[this.selectedVariantIndex] == 0 ? (variantIndexForBandwidth <= this.selectedVariantIndex || adjustedEndTimeUs >= this.maxBufferDurationToSwitchDownUs) ? (variantIndexForBandwidth >= this.selectedVariantIndex || adjustedEndTimeUs <= this.minBufferDurationToSwitchUpUs) ? this.selectedVariantIndex : variantIndexForBandwidth : variantIndexForBandwidth : variantIndexForBandwidth;
    }

    private int getVariantIndex(Format format) {
        for (int i = 0; i < this.variants.length; i++) {
            if (this.variants[i].format.equals(format)) {
                return i;
            }
        }
        throw new IllegalStateException("Invalid format: " + format);
    }

    private int getVariantIndexForBandwidth(long j) {
        boolean z = false;
        if (j == -1) {
            j = 0;
        }
        int i = (int) (((float) j) * BANDWIDTH_FRACTION);
        int i2 = -1;
        for (int i3 = 0; i3 < this.variants.length; i3++) {
            if (this.variantBlacklistTimes[i3] == 0) {
                if (this.variants[i3].format.bitrate <= i) {
                    return i3;
                }
                i2 = i3;
            }
        }
        if (i2 != -1) {
            z = true;
        }
        Assertions.checkState(z);
        return i2;
    }

    private EncryptionKeyChunk newEncryptionKeyChunk(Uri uri, String str, int i) {
        return new EncryptionKeyChunk(this.dataSource, new DataSpec(uri, 0, -1, null, 1), this.scratchSpace, str, i);
    }

    private MediaPlaylistChunk newMediaPlaylistChunk(int i) {
        Uri resolveToUri = UriUtil.resolveToUri(this.baseUri, this.variants[i].url);
        return new MediaPlaylistChunk(this.dataSource, new DataSpec(resolveToUri, 0, -1, null, 1), this.scratchSpace, this.playlistParser, i, resolveToUri.toString());
    }

    private void setEncryptionData(Uri uri, String str, byte[] bArr) {
        Object toByteArray = new BigInteger(str.toLowerCase(Locale.getDefault()).startsWith("0x") ? str.substring(2) : str, 16).toByteArray();
        Object obj = new byte[16];
        int length = toByteArray.length > 16 ? toByteArray.length - 16 : 0;
        System.arraycopy(toByteArray, length, obj, (obj.length - toByteArray.length) + length, toByteArray.length - length);
        this.encryptionKeyUri = uri;
        this.encryptionKey = bArr;
        this.encryptionIvString = str;
        this.encryptionIv = obj;
    }

    private void setMediaPlaylist(int i, HlsMediaPlaylist hlsMediaPlaylist) {
        this.variantLastPlaylistLoadTimesMs[i] = SystemClock.elapsedRealtime();
        this.variantPlaylists[i] = hlsMediaPlaylist;
        this.live |= hlsMediaPlaylist.live;
        this.durationUs = this.live ? -1 : hlsMediaPlaylist.durationUs;
    }

    private boolean shouldRerequestLiveMediaPlaylist(int i) {
        return SystemClock.elapsedRealtime() - this.variantLastPlaylistLoadTimesMs[i] >= ((long) ((this.variantPlaylists[i].targetDurationSecs * PointerIconCompat.TYPE_DEFAULT) / 2));
    }

    public void adaptiveTrack(HlsMasterPlaylist hlsMasterPlaylist, Variant[] variantArr) {
        int i = -1;
        Arrays.sort(variantArr, new C07422());
        int computeDefaultVariantIndex = computeDefaultVariantIndex(hlsMasterPlaylist, variantArr, this.bandwidthMeter);
        int i2 = -1;
        for (Variant variant : variantArr) {
            Format format = variant.format;
            i2 = Math.max(format.width, i2);
            i = Math.max(format.height, i);
        }
        if (i2 <= 0) {
            i2 = 1920;
        }
        if (i <= 0) {
            i = 1080;
        }
        this.tracks.add(new ExposedTrack(variantArr, computeDefaultVariantIndex, i2, i));
    }

    protected int computeDefaultVariantIndex(HlsMasterPlaylist hlsMasterPlaylist, Variant[] variantArr, BandwidthMeter bandwidthMeter) {
        int i = 0;
        int i2 = ConnectionsManager.DEFAULT_DATACENTER_ID;
        int i3 = 0;
        while (i < variantArr.length) {
            int indexOf = hlsMasterPlaylist.variants.indexOf(variantArr[i]);
            if (indexOf < i2) {
                i2 = indexOf;
                i3 = i;
            }
            i++;
        }
        return i3;
    }

    public void fixedTrack(HlsMasterPlaylist hlsMasterPlaylist, Variant variant) {
        this.tracks.add(new ExposedTrack(variant));
    }

    public void getChunkOperation(TsChunk tsChunk, long j, ChunkOperationHolder chunkOperationHolder) {
        int variantIndex = tsChunk == null ? -1 : getVariantIndex(tsChunk.format);
        int nextVariantIndex = getNextVariantIndex(tsChunk, j);
        boolean z = (tsChunk == null || variantIndex == nextVariantIndex) ? false : true;
        HlsMediaPlaylist hlsMediaPlaylist = this.variantPlaylists[nextVariantIndex];
        if (hlsMediaPlaylist == null) {
            chunkOperationHolder.chunk = newMediaPlaylistChunk(nextVariantIndex);
            return;
        }
        int binarySearchFloor;
        this.selectedVariantIndex = nextVariantIndex;
        if (!this.live) {
            binarySearchFloor = tsChunk == null ? Util.binarySearchFloor(hlsMediaPlaylist.segments, Long.valueOf(j), true, true) + hlsMediaPlaylist.mediaSequence : z ? Util.binarySearchFloor(hlsMediaPlaylist.segments, Long.valueOf(tsChunk.startTimeUs), true, true) + hlsMediaPlaylist.mediaSequence : tsChunk.getNextChunkIndex();
        } else if (tsChunk == null) {
            binarySearchFloor = getLiveStartChunkSequenceNumber(this.selectedVariantIndex);
        } else {
            binarySearchFloor = getLiveNextChunkSequenceNumber(tsChunk.chunkIndex, variantIndex, this.selectedVariantIndex);
            if (binarySearchFloor < hlsMediaPlaylist.mediaSequence) {
                this.fatalError = new BehindLiveWindowException();
                return;
            }
        }
        variantIndex = binarySearchFloor - hlsMediaPlaylist.mediaSequence;
        if (variantIndex < hlsMediaPlaylist.segments.size()) {
            long j2;
            HlsExtractorWrapper hlsExtractorWrapper;
            Segment segment = (Segment) hlsMediaPlaylist.segments.get(variantIndex);
            Uri resolveToUri = UriUtil.resolveToUri(hlsMediaPlaylist.baseUri, segment.url);
            if (segment.isEncrypted) {
                Uri resolveToUri2 = UriUtil.resolveToUri(hlsMediaPlaylist.baseUri, segment.encryptionKeyUri);
                if (!resolveToUri2.equals(this.encryptionKeyUri)) {
                    chunkOperationHolder.chunk = newEncryptionKeyChunk(resolveToUri2, segment.encryptionIV, this.selectedVariantIndex);
                    return;
                } else if (!Util.areEqual(segment.encryptionIV, this.encryptionIvString)) {
                    setEncryptionData(resolveToUri2, segment.encryptionIV, this.encryptionKey);
                }
            } else {
                clearEncryptionData();
            }
            DataSpec dataSpec = new DataSpec(resolveToUri, segment.byterangeOffset, segment.byterangeLength, null);
            if (!this.live) {
                j2 = segment.startTimeUs;
            } else if (tsChunk == null) {
                j2 = 0;
            } else {
                j2 = tsChunk.getAdjustedEndTimeUs() - (z ? tsChunk.getDurationUs() : 0);
            }
            long j3 = j2 + ((long) (segment.durationSecs * 1000000.0d));
            Format format = this.variants[this.selectedVariantIndex].format;
            String lastPathSegment = resolveToUri.getLastPathSegment();
            if (lastPathSegment.endsWith(AAC_FILE_EXTENSION)) {
                hlsExtractorWrapper = new HlsExtractorWrapper(0, format, j2, new AdtsExtractor(j2), z, -1, -1);
            } else if (lastPathSegment.endsWith(MP3_FILE_EXTENSION)) {
                hlsExtractorWrapper = new HlsExtractorWrapper(0, format, j2, new Mp3Extractor(j2), z, -1, -1);
            } else if (lastPathSegment.endsWith(WEBVTT_FILE_EXTENSION) || lastPathSegment.endsWith(VTT_FILE_EXTENSION)) {
                PtsTimestampAdjuster adjuster = this.timestampAdjusterProvider.getAdjuster(this.isMaster, segment.discontinuitySequenceNumber, j2);
                if (adjuster != null) {
                    hlsExtractorWrapper = new HlsExtractorWrapper(0, format, j2, new WebvttExtractor(adjuster), z, -1, -1);
                } else {
                    return;
                }
            } else if (tsChunk != null && tsChunk.discontinuitySequenceNumber == segment.discontinuitySequenceNumber && format.equals(tsChunk.format)) {
                hlsExtractorWrapper = tsChunk.extractorWrapper;
            } else {
                PtsTimestampAdjuster adjuster2 = this.timestampAdjusterProvider.getAdjuster(this.isMaster, segment.discontinuitySequenceNumber, j2);
                if (adjuster2 != null) {
                    nextVariantIndex = 0;
                    String str = format.codecs;
                    if (!TextUtils.isEmpty(str)) {
                        if (MimeTypes.getAudioMediaMimeType(str) != MimeTypes.AUDIO_AAC) {
                            nextVariantIndex = 2;
                        }
                        if (MimeTypes.getVideoMediaMimeType(str) != MimeTypes.VIDEO_H264) {
                            nextVariantIndex |= 4;
                        }
                    }
                    ExposedTrack exposedTrack = (ExposedTrack) this.tracks.get(this.selectedTrackIndex);
                    hlsExtractorWrapper = new HlsExtractorWrapper(0, format, j2, new TsExtractor(adjuster2, nextVariantIndex), z, exposedTrack.adaptiveMaxWidth, exposedTrack.adaptiveMaxHeight);
                } else {
                    return;
                }
            }
            chunkOperationHolder.chunk = new TsChunk(this.dataSource, dataSpec, 0, format, j2, j3, binarySearchFloor, segment.discontinuitySequenceNumber, hlsExtractorWrapper, this.encryptionKey, this.encryptionIv);
        } else if (hlsMediaPlaylist.live) {
            if (shouldRerequestLiveMediaPlaylist(this.selectedVariantIndex)) {
                chunkOperationHolder.chunk = newMediaPlaylistChunk(this.selectedVariantIndex);
            }
        } else {
            chunkOperationHolder.endOfStream = true;
        }
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public Variant getFixedTrackVariant(int i) {
        Variant[] access$000 = ((ExposedTrack) this.tracks.get(i)).variants;
        return access$000.length == 1 ? access$000[0] : null;
    }

    public String getMuxedAudioLanguage() {
        return this.masterPlaylist.muxedAudioLanguage;
    }

    public String getMuxedCaptionLanguage() {
        return this.masterPlaylist.muxedCaptionLanguage;
    }

    public int getSelectedTrackIndex() {
        return this.selectedTrackIndex;
    }

    public int getTrackCount() {
        return this.tracks.size();
    }

    public boolean isLive() {
        return this.live;
    }

    public void maybeThrowError() {
        if (this.fatalError != null) {
            throw this.fatalError;
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof MediaPlaylistChunk) {
            MediaPlaylistChunk mediaPlaylistChunk = (MediaPlaylistChunk) chunk;
            this.scratchSpace = mediaPlaylistChunk.getDataHolder();
            setMediaPlaylist(mediaPlaylistChunk.variantIndex, mediaPlaylistChunk.getResult());
            if (this.eventHandler != null && this.eventListener != null) {
                this.eventHandler.post(new C07411(mediaPlaylistChunk.getRawResponse()));
            }
        } else if (chunk instanceof EncryptionKeyChunk) {
            EncryptionKeyChunk encryptionKeyChunk = (EncryptionKeyChunk) chunk;
            this.scratchSpace = encryptionKeyChunk.getDataHolder();
            setEncryptionData(encryptionKeyChunk.dataSpec.uri, encryptionKeyChunk.iv, encryptionKeyChunk.getResult());
        }
    }

    public boolean onChunkLoadError(Chunk chunk, IOException iOException) {
        if (chunk.bytesLoaded() != 0) {
            return false;
        }
        if ((!(chunk instanceof TsChunk) && !(chunk instanceof MediaPlaylistChunk) && !(chunk instanceof EncryptionKeyChunk)) || !(iOException instanceof InvalidResponseCodeException)) {
            return false;
        }
        int i = ((InvalidResponseCodeException) iOException).responseCode;
        if (i != 404 && i != 410) {
            return false;
        }
        int variantIndex = chunk instanceof TsChunk ? getVariantIndex(((TsChunk) chunk).format) : chunk instanceof MediaPlaylistChunk ? ((MediaPlaylistChunk) chunk).variantIndex : ((EncryptionKeyChunk) chunk).variantIndex;
        boolean z = this.variantBlacklistTimes[variantIndex] != 0;
        this.variantBlacklistTimes[variantIndex] = SystemClock.elapsedRealtime();
        if (z) {
            Log.w(TAG, "Already blacklisted variant (" + i + "): " + chunk.dataSpec.uri);
            return false;
        } else if (allVariantsBlacklisted()) {
            Log.w(TAG, "Final variant not blacklisted (" + i + "): " + chunk.dataSpec.uri);
            this.variantBlacklistTimes[variantIndex] = 0;
            return false;
        } else {
            Log.w(TAG, "Blacklisted variant (" + i + "): " + chunk.dataSpec.uri);
            return true;
        }
    }

    public boolean prepare() {
        if (!this.prepareCalled) {
            this.prepareCalled = true;
            try {
                this.trackSelector.selectTracks(this.masterPlaylist, this);
                selectTrack(0);
            } catch (IOException e) {
                this.fatalError = e;
            }
        }
        return this.fatalError == null;
    }

    public void reset() {
        this.fatalError = null;
    }

    public void seek() {
        if (this.isMaster) {
            this.timestampAdjusterProvider.reset();
        }
    }

    public void selectTrack(int i) {
        this.selectedTrackIndex = i;
        ExposedTrack exposedTrack = (ExposedTrack) this.tracks.get(this.selectedTrackIndex);
        this.selectedVariantIndex = exposedTrack.defaultVariantIndex;
        this.variants = exposedTrack.variants;
        this.variantPlaylists = new HlsMediaPlaylist[this.variants.length];
        this.variantLastPlaylistLoadTimesMs = new long[this.variants.length];
        this.variantBlacklistTimes = new long[this.variants.length];
    }
}

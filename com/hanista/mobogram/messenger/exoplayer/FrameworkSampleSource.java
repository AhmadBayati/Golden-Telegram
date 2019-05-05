package com.hanista.mobogram.messenger.exoplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.exoplayer.SampleSource.SampleSourceReader;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData.Mapped;
import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import com.hanista.mobogram.messenger.exoplayer.extractor.mp4.PsshAtomUtil;
import com.hanista.mobogram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.MimeTypes;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@TargetApi(16)
@Deprecated
public final class FrameworkSampleSource implements SampleSource, SampleSourceReader {
    private static final int ALLOWED_FLAGS_MASK = 3;
    private static final int TRACK_STATE_DISABLED = 0;
    private static final int TRACK_STATE_ENABLED = 1;
    private static final int TRACK_STATE_FORMAT_SENT = 2;
    private final Context context;
    private MediaExtractor extractor;
    private final FileDescriptor fileDescriptor;
    private final long fileDescriptorLength;
    private final long fileDescriptorOffset;
    private final Map<String, String> headers;
    private long lastSeekPositionUs;
    private boolean[] pendingDiscontinuities;
    private long pendingSeekPositionUs;
    private IOException preparationError;
    private boolean prepared;
    private int remainingReleaseCount;
    private MediaFormat[] trackFormats;
    private int[] trackStates;
    private final Uri uri;

    public FrameworkSampleSource(Context context, Uri uri, Map<String, String> map) {
        Assertions.checkState(Util.SDK_INT >= 16);
        this.context = (Context) Assertions.checkNotNull(context);
        this.uri = (Uri) Assertions.checkNotNull(uri);
        this.headers = map;
        this.fileDescriptor = null;
        this.fileDescriptorOffset = 0;
        this.fileDescriptorLength = 0;
    }

    public FrameworkSampleSource(FileDescriptor fileDescriptor, long j, long j2) {
        Assertions.checkState(Util.SDK_INT >= 16);
        this.fileDescriptor = (FileDescriptor) Assertions.checkNotNull(fileDescriptor);
        this.fileDescriptorOffset = j;
        this.fileDescriptorLength = j2;
        this.context = null;
        this.uri = null;
        this.headers = null;
    }

    @SuppressLint({"InlinedApi"})
    private static MediaFormat createMediaFormat(MediaFormat mediaFormat) {
        String string = mediaFormat.getString("mime");
        String optionalStringV16 = getOptionalStringV16(mediaFormat, "language");
        int optionalIntegerV16 = getOptionalIntegerV16(mediaFormat, "max-input-size");
        int optionalIntegerV162 = getOptionalIntegerV16(mediaFormat, "width");
        int optionalIntegerV163 = getOptionalIntegerV16(mediaFormat, "height");
        int optionalIntegerV164 = getOptionalIntegerV16(mediaFormat, "rotation-degrees");
        int optionalIntegerV165 = getOptionalIntegerV16(mediaFormat, "channel-count");
        int optionalIntegerV166 = getOptionalIntegerV16(mediaFormat, "sample-rate");
        int optionalIntegerV167 = getOptionalIntegerV16(mediaFormat, "encoder-delay");
        int optionalIntegerV168 = getOptionalIntegerV16(mediaFormat, "encoder-padding");
        List arrayList = new ArrayList();
        int i = TRACK_STATE_DISABLED;
        while (true) {
            if (!mediaFormat.containsKey("csd-" + i)) {
                break;
            }
            ByteBuffer byteBuffer = mediaFormat.getByteBuffer("csd-" + i);
            Object obj = new byte[byteBuffer.limit()];
            byteBuffer.get(obj);
            arrayList.add(obj);
            byteBuffer.flip();
            i += TRACK_STATE_ENABLED;
        }
        MediaFormat mediaFormat2 = new MediaFormat(null, string, -1, optionalIntegerV16, mediaFormat.containsKey("durationUs") ? mediaFormat.getLong("durationUs") : -1, optionalIntegerV162, optionalIntegerV163, optionalIntegerV164, Face.UNCOMPUTED_PROBABILITY, optionalIntegerV165, optionalIntegerV166, optionalStringV16, PtsTimestampAdjuster.DO_NOT_OFFSET, arrayList, false, -1, -1, MimeTypes.AUDIO_RAW.equals(string) ? TRACK_STATE_FORMAT_SENT : -1, optionalIntegerV167, optionalIntegerV168);
        mediaFormat2.setFrameworkFormatV16(mediaFormat);
        return mediaFormat2;
    }

    @TargetApi(18)
    private DrmInitData getDrmInitDataV18() {
        Map psshInfo = this.extractor.getPsshInfo();
        if (psshInfo == null || psshInfo.isEmpty()) {
            return null;
        }
        Mapped mapped = new Mapped();
        for (UUID uuid : psshInfo.keySet()) {
            mapped.put(uuid, new SchemeInitData(MimeTypes.VIDEO_MP4, PsshAtomUtil.buildPsshAtom(uuid, (byte[]) psshInfo.get(uuid))));
        }
        return mapped;
    }

    @TargetApi(16)
    private static final int getOptionalIntegerV16(MediaFormat mediaFormat, String str) {
        return mediaFormat.containsKey(str) ? mediaFormat.getInteger(str) : -1;
    }

    @TargetApi(16)
    private static final String getOptionalStringV16(MediaFormat mediaFormat, String str) {
        return mediaFormat.containsKey(str) ? mediaFormat.getString(str) : null;
    }

    private void seekToUsInternal(long j, boolean z) {
        int i = TRACK_STATE_DISABLED;
        if (z || this.pendingSeekPositionUs != j) {
            this.lastSeekPositionUs = j;
            this.pendingSeekPositionUs = j;
            this.extractor.seekTo(j, TRACK_STATE_DISABLED);
            while (i < this.trackStates.length) {
                if (this.trackStates[i] != 0) {
                    this.pendingDiscontinuities[i] = true;
                }
                i += TRACK_STATE_ENABLED;
            }
        }
    }

    public boolean continueBuffering(int i, long j) {
        return true;
    }

    public void disable(int i) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.trackStates[i] != 0);
        this.extractor.unselectTrack(i);
        this.pendingDiscontinuities[i] = false;
        this.trackStates[i] = TRACK_STATE_DISABLED;
    }

    public void enable(int i, long j) {
        boolean z = true;
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.trackStates[i] == 0);
        this.trackStates[i] = TRACK_STATE_ENABLED;
        this.extractor.selectTrack(i);
        if (j == 0) {
            z = false;
        }
        seekToUsInternal(j, z);
    }

    public long getBufferedPositionUs() {
        Assertions.checkState(this.prepared);
        long cachedDuration = this.extractor.getCachedDuration();
        if (cachedDuration == -1) {
            return -1;
        }
        long sampleTime = this.extractor.getSampleTime();
        return sampleTime == -1 ? -3 : sampleTime + cachedDuration;
    }

    public MediaFormat getFormat(int i) {
        Assertions.checkState(this.prepared);
        return this.trackFormats[i];
    }

    public int getTrackCount() {
        Assertions.checkState(this.prepared);
        return this.trackStates.length;
    }

    public void maybeThrowError() {
        if (this.preparationError != null) {
            throw this.preparationError;
        }
    }

    public boolean prepare(long j) {
        if (!this.prepared) {
            if (this.preparationError != null) {
                return false;
            }
            this.extractor = new MediaExtractor();
            try {
                if (this.context != null) {
                    this.extractor.setDataSource(this.context, this.uri, this.headers);
                } else {
                    this.extractor.setDataSource(this.fileDescriptor, this.fileDescriptorOffset, this.fileDescriptorLength);
                }
                this.trackStates = new int[this.extractor.getTrackCount()];
                this.pendingDiscontinuities = new boolean[this.trackStates.length];
                this.trackFormats = new MediaFormat[this.trackStates.length];
                for (int i = TRACK_STATE_DISABLED; i < this.trackStates.length; i += TRACK_STATE_ENABLED) {
                    this.trackFormats[i] = createMediaFormat(this.extractor.getTrackFormat(i));
                }
                this.prepared = true;
            } catch (IOException e) {
                this.preparationError = e;
                return false;
            }
        }
        return true;
    }

    public int readData(int i, long j, MediaFormatHolder mediaFormatHolder, SampleHolder sampleHolder) {
        Assertions.checkState(this.prepared);
        Assertions.checkState(this.trackStates[i] != 0);
        if (this.pendingDiscontinuities[i]) {
            return -2;
        }
        if (this.trackStates[i] != TRACK_STATE_FORMAT_SENT) {
            mediaFormatHolder.format = this.trackFormats[i];
            mediaFormatHolder.drmInitData = Util.SDK_INT >= 18 ? getDrmInitDataV18() : null;
            this.trackStates[i] = TRACK_STATE_FORMAT_SENT;
            return -4;
        }
        int sampleTrackIndex = this.extractor.getSampleTrackIndex();
        if (sampleTrackIndex == i) {
            if (sampleHolder.data != null) {
                sampleTrackIndex = sampleHolder.data.position();
                sampleHolder.size = this.extractor.readSampleData(sampleHolder.data, sampleTrackIndex);
                sampleHolder.data.position(sampleTrackIndex + sampleHolder.size);
            } else {
                sampleHolder.size = TRACK_STATE_DISABLED;
            }
            sampleHolder.timeUs = this.extractor.getSampleTime();
            sampleHolder.flags = this.extractor.getSampleFlags() & ALLOWED_FLAGS_MASK;
            if (sampleHolder.isEncrypted()) {
                sampleHolder.cryptoInfo.setFromExtractorV16(this.extractor);
            }
            this.pendingSeekPositionUs = -1;
            this.extractor.advance();
            return -3;
        }
        return sampleTrackIndex < 0 ? -1 : -2;
    }

    public long readDiscontinuity(int i) {
        if (!this.pendingDiscontinuities[i]) {
            return Long.MIN_VALUE;
        }
        this.pendingDiscontinuities[i] = false;
        return this.lastSeekPositionUs;
    }

    public SampleSourceReader register() {
        this.remainingReleaseCount += TRACK_STATE_ENABLED;
        return this;
    }

    public void release() {
        Assertions.checkState(this.remainingReleaseCount > 0);
        int i = this.remainingReleaseCount - 1;
        this.remainingReleaseCount = i;
        if (i == 0 && this.extractor != null) {
            this.extractor.release();
            this.extractor = null;
        }
    }

    public void seekToUs(long j) {
        Assertions.checkState(this.prepared);
        seekToUsInternal(j, false);
    }
}

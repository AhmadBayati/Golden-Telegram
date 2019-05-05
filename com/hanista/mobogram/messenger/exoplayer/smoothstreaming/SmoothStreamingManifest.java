package com.hanista.mobogram.messenger.exoplayer.smoothstreaming;

import android.net.Uri;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.chunk.Format;
import com.hanista.mobogram.messenger.exoplayer.chunk.FormatWrapper;
import com.hanista.mobogram.messenger.exoplayer.util.Assertions;
import com.hanista.mobogram.messenger.exoplayer.util.UriUtil;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.List;
import java.util.UUID;

public class SmoothStreamingManifest {
    public final long durationUs;
    public final long dvrWindowLengthUs;
    public final boolean isLive;
    public final int lookAheadCount;
    public final int majorVersion;
    public final int minorVersion;
    public final ProtectionElement protectionElement;
    public final StreamElement[] streamElements;

    public static class ProtectionElement {
        public final byte[] data;
        public final UUID uuid;

        public ProtectionElement(UUID uuid, byte[] bArr) {
            this.uuid = uuid;
            this.data = bArr;
        }
    }

    public static class StreamElement {
        public static final int TYPE_AUDIO = 0;
        public static final int TYPE_TEXT = 2;
        public static final int TYPE_UNKNOWN = -1;
        public static final int TYPE_VIDEO = 1;
        private static final String URL_PLACEHOLDER_BITRATE = "{bitrate}";
        private static final String URL_PLACEHOLDER_START_TIME = "{start time}";
        private final String baseUri;
        public final int chunkCount;
        private final List<Long> chunkStartTimes;
        private final long[] chunkStartTimesUs;
        private final String chunkTemplate;
        public final int displayHeight;
        public final int displayWidth;
        public final String language;
        private final long lastChunkDurationUs;
        public final int maxHeight;
        public final int maxWidth;
        public final String name;
        public final int qualityLevels;
        public final String subType;
        public final long timescale;
        public final TrackElement[] tracks;
        public final int type;

        public StreamElement(String str, String str2, int i, String str3, long j, String str4, int i2, int i3, int i4, int i5, int i6, String str5, TrackElement[] trackElementArr, List<Long> list, long j2) {
            this.baseUri = str;
            this.chunkTemplate = str2;
            this.type = i;
            this.subType = str3;
            this.timescale = j;
            this.name = str4;
            this.qualityLevels = i2;
            this.maxWidth = i3;
            this.maxHeight = i4;
            this.displayWidth = i5;
            this.displayHeight = i6;
            this.language = str5;
            this.tracks = trackElementArr;
            this.chunkCount = list.size();
            this.chunkStartTimes = list;
            this.lastChunkDurationUs = Util.scaleLargeTimestamp(j2, C0700C.MICROS_PER_SECOND, j);
            this.chunkStartTimesUs = Util.scaleLargeTimestamps(list, C0700C.MICROS_PER_SECOND, j);
        }

        public Uri buildRequestUri(int i, int i2) {
            boolean z = true;
            Assertions.checkState(this.tracks != null);
            Assertions.checkState(this.chunkStartTimes != null);
            if (i2 >= this.chunkStartTimes.size()) {
                z = false;
            }
            Assertions.checkState(z);
            return UriUtil.resolveToUri(this.baseUri, this.chunkTemplate.replace(URL_PLACEHOLDER_BITRATE, Integer.toString(this.tracks[i].format.bitrate)).replace(URL_PLACEHOLDER_START_TIME, ((Long) this.chunkStartTimes.get(i2)).toString()));
        }

        public long getChunkDurationUs(int i) {
            return i == this.chunkCount + TYPE_UNKNOWN ? this.lastChunkDurationUs : this.chunkStartTimesUs[i + TYPE_VIDEO] - this.chunkStartTimesUs[i];
        }

        public int getChunkIndex(long j) {
            return Util.binarySearchFloor(this.chunkStartTimesUs, j, true, true);
        }

        public long getStartTimeUs(int i) {
            return this.chunkStartTimesUs[i];
        }
    }

    public static class TrackElement implements FormatWrapper {
        public final byte[][] csd;
        public final Format format;

        public TrackElement(int i, int i2, String str, byte[][] bArr, int i3, int i4, int i5, int i6, String str2) {
            this.csd = bArr;
            this.format = new Format(String.valueOf(i), str, i3, i4, Face.UNCOMPUTED_PROBABILITY, i6, i5, i2, str2);
        }

        public Format getFormat() {
            return this.format;
        }
    }

    public SmoothStreamingManifest(int i, int i2, long j, long j2, long j3, int i3, boolean z, ProtectionElement protectionElement, StreamElement[] streamElementArr) {
        this.majorVersion = i;
        this.minorVersion = i2;
        this.lookAheadCount = i3;
        this.isLive = z;
        this.protectionElement = protectionElement;
        this.streamElements = streamElementArr;
        this.dvrWindowLengthUs = j3 == 0 ? -1 : Util.scaleLargeTimestamp(j3, C0700C.MICROS_PER_SECOND, j);
        this.durationUs = j2 == 0 ? -1 : Util.scaleLargeTimestamp(j2, C0700C.MICROS_PER_SECOND, j);
    }
}

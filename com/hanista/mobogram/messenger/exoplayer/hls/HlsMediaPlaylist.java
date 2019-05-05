package com.hanista.mobogram.messenger.exoplayer.hls;

import java.util.List;

public final class HlsMediaPlaylist extends HlsPlaylist {
    public static final String ENCRYPTION_METHOD_AES_128 = "AES-128";
    public static final String ENCRYPTION_METHOD_NONE = "NONE";
    public final long durationUs;
    public final boolean live;
    public final int mediaSequence;
    public final List<Segment> segments;
    public final int targetDurationSecs;
    public final int version;

    public static final class Segment implements Comparable<Long> {
        public final long byterangeLength;
        public final long byterangeOffset;
        public final int discontinuitySequenceNumber;
        public final double durationSecs;
        public final String encryptionIV;
        public final String encryptionKeyUri;
        public final boolean isEncrypted;
        public final long startTimeUs;
        public final String url;

        public Segment(String str, double d, int i, long j, boolean z, String str2, String str3, long j2, long j3) {
            this.url = str;
            this.durationSecs = d;
            this.discontinuitySequenceNumber = i;
            this.startTimeUs = j;
            this.isEncrypted = z;
            this.encryptionKeyUri = str2;
            this.encryptionIV = str3;
            this.byterangeOffset = j2;
            this.byterangeLength = j3;
        }

        public int compareTo(Long l) {
            return this.startTimeUs > l.longValue() ? 1 : this.startTimeUs < l.longValue() ? -1 : 0;
        }
    }

    public HlsMediaPlaylist(String str, int i, int i2, int i3, boolean z, List<Segment> list) {
        super(str, 1);
        this.mediaSequence = i;
        this.targetDurationSecs = i2;
        this.version = i3;
        this.live = z;
        this.segments = list;
        if (list.isEmpty()) {
            this.durationUs = 0;
            return;
        }
        Segment segment = (Segment) list.get(list.size() - 1);
        this.durationUs = ((long) (segment.durationSecs * 1000000.0d)) + segment.startTimeUs;
    }
}

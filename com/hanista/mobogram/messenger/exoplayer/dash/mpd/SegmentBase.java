package com.hanista.mobogram.messenger.exoplayer.dash.mpd;

import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.List;

public abstract class SegmentBase {
    final RangedUri initialization;
    final long presentationTimeOffset;
    final long timescale;

    public static abstract class MultiSegmentBase extends SegmentBase {
        final long duration;
        final List<SegmentTimelineElement> segmentTimeline;
        final int startNumber;

        public MultiSegmentBase(RangedUri rangedUri, long j, long j2, int i, long j3, List<SegmentTimelineElement> list) {
            super(rangedUri, j, j2);
            this.startNumber = i;
            this.duration = j3;
            this.segmentTimeline = list;
        }

        public int getFirstSegmentNum() {
            return this.startNumber;
        }

        public abstract int getLastSegmentNum(long j);

        public final long getSegmentDurationUs(int i, long j) {
            return this.segmentTimeline != null ? (((SegmentTimelineElement) this.segmentTimeline.get(i - this.startNumber)).duration * C0700C.MICROS_PER_SECOND) / this.timescale : i == getLastSegmentNum(j) ? j - getSegmentTimeUs(i) : (this.duration * C0700C.MICROS_PER_SECOND) / this.timescale;
        }

        public int getSegmentNum(long j, long j2) {
            int firstSegmentNum = getFirstSegmentNum();
            int lastSegmentNum = getLastSegmentNum(j2);
            int i;
            if (this.segmentTimeline == null) {
                i = ((int) (j / ((this.duration * C0700C.MICROS_PER_SECOND) / this.timescale))) + this.startNumber;
                return i < firstSegmentNum ? firstSegmentNum : (lastSegmentNum == -1 || i <= lastSegmentNum) ? i : lastSegmentNum;
            } else {
                i = firstSegmentNum;
                while (i <= lastSegmentNum) {
                    int i2 = (i + lastSegmentNum) / 2;
                    long segmentTimeUs = getSegmentTimeUs(i2);
                    if (segmentTimeUs < j) {
                        i = i2 + 1;
                    } else if (segmentTimeUs <= j) {
                        return i2;
                    } else {
                        lastSegmentNum = i2 - 1;
                    }
                }
                if (i != firstSegmentNum) {
                    i = lastSegmentNum;
                }
                return i;
            }
        }

        public final long getSegmentTimeUs(int i) {
            return Util.scaleLargeTimestamp(this.segmentTimeline != null ? ((SegmentTimelineElement) this.segmentTimeline.get(i - this.startNumber)).startTime - this.presentationTimeOffset : ((long) (i - this.startNumber)) * this.duration, C0700C.MICROS_PER_SECOND, this.timescale);
        }

        public abstract RangedUri getSegmentUrl(Representation representation, int i);

        public boolean isExplicit() {
            return this.segmentTimeline != null;
        }
    }

    public static class SegmentList extends MultiSegmentBase {
        final List<RangedUri> mediaSegments;

        public SegmentList(RangedUri rangedUri, long j, long j2, int i, long j3, List<SegmentTimelineElement> list, List<RangedUri> list2) {
            super(rangedUri, j, j2, i, j3, list);
            this.mediaSegments = list2;
        }

        public int getLastSegmentNum(long j) {
            return (this.startNumber + this.mediaSegments.size()) - 1;
        }

        public RangedUri getSegmentUrl(Representation representation, int i) {
            return (RangedUri) this.mediaSegments.get(i - this.startNumber);
        }

        public boolean isExplicit() {
            return true;
        }
    }

    public static class SegmentTemplate extends MultiSegmentBase {
        private final String baseUrl;
        final UrlTemplate initializationTemplate;
        final UrlTemplate mediaTemplate;

        public SegmentTemplate(RangedUri rangedUri, long j, long j2, int i, long j3, List<SegmentTimelineElement> list, UrlTemplate urlTemplate, UrlTemplate urlTemplate2, String str) {
            super(rangedUri, j, j2, i, j3, list);
            this.initializationTemplate = urlTemplate;
            this.mediaTemplate = urlTemplate2;
            this.baseUrl = str;
        }

        public RangedUri getInitialization(Representation representation) {
            if (this.initializationTemplate == null) {
                return super.getInitialization(representation);
            }
            return new RangedUri(this.baseUrl, this.initializationTemplate.buildUri(representation.format.id, 0, representation.format.bitrate, 0), 0, -1);
        }

        public int getLastSegmentNum(long j) {
            if (this.segmentTimeline != null) {
                return (this.segmentTimeline.size() + this.startNumber) - 1;
            }
            if (j == -1) {
                return -1;
            }
            long j2 = (this.duration * C0700C.MICROS_PER_SECOND) / this.timescale;
            return (((int) Util.ceilDivide(j, j2)) + this.startNumber) - 1;
        }

        public RangedUri getSegmentUrl(Representation representation, int i) {
            return new RangedUri(this.baseUrl, this.mediaTemplate.buildUri(representation.format.id, i, representation.format.bitrate, this.segmentTimeline != null ? ((SegmentTimelineElement) this.segmentTimeline.get(i - this.startNumber)).startTime : ((long) (i - this.startNumber)) * this.duration), 0, -1);
        }
    }

    public static class SegmentTimelineElement {
        long duration;
        long startTime;

        public SegmentTimelineElement(long j, long j2) {
            this.startTime = j;
            this.duration = j2;
        }
    }

    public static class SingleSegmentBase extends SegmentBase {
        final long indexLength;
        final long indexStart;
        public final String uri;

        public SingleSegmentBase(RangedUri rangedUri, long j, long j2, String str, long j3, long j4) {
            super(rangedUri, j, j2);
            this.uri = str;
            this.indexStart = j3;
            this.indexLength = j4;
        }

        public SingleSegmentBase(String str) {
            this(null, 1, 0, str, 0, -1);
        }

        public RangedUri getIndex() {
            return this.indexLength <= 0 ? null : new RangedUri(this.uri, null, this.indexStart, this.indexLength);
        }
    }

    public SegmentBase(RangedUri rangedUri, long j, long j2) {
        this.initialization = rangedUri;
        this.timescale = j;
        this.presentationTimeOffset = j2;
    }

    public RangedUri getInitialization(Representation representation) {
        return this.initialization;
    }

    public long getPresentationTimeOffsetUs() {
        return Util.scaleLargeTimestamp(this.presentationTimeOffset, C0700C.MICROS_PER_SECOND, this.timescale);
    }
}

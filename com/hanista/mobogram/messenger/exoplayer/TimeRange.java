package com.hanista.mobogram.messenger.exoplayer;

import com.hanista.mobogram.messenger.exoplayer.util.Clock;

public interface TimeRange {

    public static final class DynamicTimeRange implements TimeRange {
        private final long bufferDepthUs;
        private final long elapsedRealtimeAtStartUs;
        private final long maxEndTimeUs;
        private final long minStartTimeUs;
        private final Clock systemClock;

        public DynamicTimeRange(long j, long j2, long j3, long j4, Clock clock) {
            this.minStartTimeUs = j;
            this.maxEndTimeUs = j2;
            this.elapsedRealtimeAtStartUs = j3;
            this.bufferDepthUs = j4;
            this.systemClock = clock;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            DynamicTimeRange dynamicTimeRange = (DynamicTimeRange) obj;
            return dynamicTimeRange.minStartTimeUs == this.minStartTimeUs && dynamicTimeRange.maxEndTimeUs == this.maxEndTimeUs && dynamicTimeRange.elapsedRealtimeAtStartUs == this.elapsedRealtimeAtStartUs && dynamicTimeRange.bufferDepthUs == this.bufferDepthUs;
        }

        public long[] getCurrentBoundsMs(long[] jArr) {
            long[] currentBoundsUs = getCurrentBoundsUs(jArr);
            currentBoundsUs[0] = currentBoundsUs[0] / 1000;
            currentBoundsUs[1] = currentBoundsUs[1] / 1000;
            return currentBoundsUs;
        }

        public long[] getCurrentBoundsUs(long[] jArr) {
            if (jArr == null || jArr.length < 2) {
                jArr = new long[2];
            }
            long min = Math.min(this.maxEndTimeUs, (this.systemClock.elapsedRealtime() * 1000) - this.elapsedRealtimeAtStartUs);
            long j = this.minStartTimeUs;
            if (this.bufferDepthUs != -1) {
                j = Math.max(j, min - this.bufferDepthUs);
            }
            jArr[0] = j;
            jArr[1] = min;
            return jArr;
        }

        public int hashCode() {
            return ((((((((int) this.minStartTimeUs) + 527) * 31) + ((int) this.maxEndTimeUs)) * 31) + ((int) this.elapsedRealtimeAtStartUs)) * 31) + ((int) this.bufferDepthUs);
        }

        public boolean isStatic() {
            return false;
        }
    }

    public static final class StaticTimeRange implements TimeRange {
        private final long endTimeUs;
        private final long startTimeUs;

        public StaticTimeRange(long j, long j2) {
            this.startTimeUs = j;
            this.endTimeUs = j2;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            StaticTimeRange staticTimeRange = (StaticTimeRange) obj;
            return staticTimeRange.startTimeUs == this.startTimeUs && staticTimeRange.endTimeUs == this.endTimeUs;
        }

        public long[] getCurrentBoundsMs(long[] jArr) {
            long[] currentBoundsUs = getCurrentBoundsUs(jArr);
            currentBoundsUs[0] = currentBoundsUs[0] / 1000;
            currentBoundsUs[1] = currentBoundsUs[1] / 1000;
            return currentBoundsUs;
        }

        public long[] getCurrentBoundsUs(long[] jArr) {
            if (jArr == null || jArr.length < 2) {
                jArr = new long[2];
            }
            jArr[0] = this.startTimeUs;
            jArr[1] = this.endTimeUs;
            return jArr;
        }

        public int hashCode() {
            return ((((int) this.startTimeUs) + 527) * 31) + ((int) this.endTimeUs);
        }

        public boolean isStatic() {
            return true;
        }
    }

    long[] getCurrentBoundsMs(long[] jArr);

    long[] getCurrentBoundsUs(long[] jArr);

    boolean isStatic();
}

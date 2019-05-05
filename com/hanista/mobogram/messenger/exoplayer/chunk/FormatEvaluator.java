package com.hanista.mobogram.messenger.exoplayer.chunk;

import com.hanista.mobogram.messenger.exoplayer.upstream.BandwidthMeter;
import java.util.List;
import java.util.Random;

public interface FormatEvaluator {

    public static final class AdaptiveEvaluator implements FormatEvaluator {
        public static final float DEFAULT_BANDWIDTH_FRACTION = 0.75f;
        public static final int DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS = 25000;
        public static final int DEFAULT_MAX_INITIAL_BITRATE = 800000;
        public static final int DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS = 10000;
        public static final int DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS = 25000;
        private final float bandwidthFraction;
        private final BandwidthMeter bandwidthMeter;
        private final long maxDurationForQualityDecreaseUs;
        private final int maxInitialBitrate;
        private final long minDurationForQualityIncreaseUs;
        private final long minDurationToRetainAfterDiscardUs;

        public AdaptiveEvaluator(BandwidthMeter bandwidthMeter) {
            this(bandwidthMeter, DEFAULT_MAX_INITIAL_BITRATE, DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS, DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS, DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS, DEFAULT_BANDWIDTH_FRACTION);
        }

        public AdaptiveEvaluator(BandwidthMeter bandwidthMeter, int i, int i2, int i3, int i4, float f) {
            this.bandwidthMeter = bandwidthMeter;
            this.maxInitialBitrate = i;
            this.minDurationForQualityIncreaseUs = ((long) i2) * 1000;
            this.maxDurationForQualityDecreaseUs = ((long) i3) * 1000;
            this.minDurationToRetainAfterDiscardUs = ((long) i4) * 1000;
            this.bandwidthFraction = f;
        }

        private Format determineIdealFormat(Format[] formatArr, long j) {
            long j2 = j == -1 ? (long) this.maxInitialBitrate : (long) (((float) j) * this.bandwidthFraction);
            for (Format format : formatArr) {
                if (((long) format.bitrate) <= j2) {
                    return format;
                }
            }
            return formatArr[formatArr.length - 1];
        }

        public void disable() {
        }

        public void enable() {
        }

        public void evaluate(List<? extends MediaChunk> list, long j, Format[] formatArr, Evaluation evaluation) {
            Format format;
            Object obj = null;
            long j2 = list.isEmpty() ? 0 : ((MediaChunk) list.get(list.size() - 1)).endTimeUs - j;
            Format format2 = evaluation.format;
            Format determineIdealFormat = determineIdealFormat(formatArr, this.bandwidthMeter.getBitrateEstimate());
            Object obj2 = (determineIdealFormat == null || format2 == null || determineIdealFormat.bitrate <= format2.bitrate) ? null : 1;
            if (!(determineIdealFormat == null || format2 == null || determineIdealFormat.bitrate >= format2.bitrate)) {
                obj = 1;
            }
            if (obj2 == null) {
                if (!(obj == null || format2 == null || j2 < this.maxDurationForQualityDecreaseUs)) {
                    format = format2;
                }
                format = determineIdealFormat;
            } else if (j2 < this.minDurationForQualityIncreaseUs) {
                format = format2;
            } else {
                if (j2 >= this.minDurationToRetainAfterDiscardUs) {
                    for (int i = 1; i < list.size(); i++) {
                        MediaChunk mediaChunk = (MediaChunk) list.get(i);
                        if (mediaChunk.startTimeUs - j >= this.minDurationToRetainAfterDiscardUs && mediaChunk.format.bitrate < determineIdealFormat.bitrate && mediaChunk.format.height < determineIdealFormat.height && mediaChunk.format.height < 720 && mediaChunk.format.width < 1280) {
                            evaluation.queueSize = i;
                            break;
                        }
                    }
                    format = determineIdealFormat;
                }
                format = determineIdealFormat;
            }
            if (!(format2 == null || format == format2)) {
                evaluation.trigger = 3;
            }
            evaluation.format = format;
        }
    }

    public static final class Evaluation {
        public Format format;
        public int queueSize;
        public int trigger;

        public Evaluation() {
            this.trigger = 1;
        }
    }

    public static final class FixedEvaluator implements FormatEvaluator {
        public void disable() {
        }

        public void enable() {
        }

        public void evaluate(List<? extends MediaChunk> list, long j, Format[] formatArr, Evaluation evaluation) {
            evaluation.format = formatArr[0];
        }
    }

    public static final class RandomEvaluator implements FormatEvaluator {
        private final Random random;

        public RandomEvaluator() {
            this.random = new Random();
        }

        public RandomEvaluator(int i) {
            this.random = new Random((long) i);
        }

        public void disable() {
        }

        public void enable() {
        }

        public void evaluate(List<? extends MediaChunk> list, long j, Format[] formatArr, Evaluation evaluation) {
            Format format = formatArr[this.random.nextInt(formatArr.length)];
            if (!(evaluation.format == null || evaluation.format.equals(format))) {
                evaluation.trigger = 3;
            }
            evaluation.format = format;
        }
    }

    void disable();

    void enable();

    void evaluate(List<? extends MediaChunk> list, long j, Format[] formatArr, Evaluation evaluation);
}

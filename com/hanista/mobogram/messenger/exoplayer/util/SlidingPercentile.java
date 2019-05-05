package com.hanista.mobogram.messenger.exoplayer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class SlidingPercentile {
    private static final Comparator<Sample> INDEX_COMPARATOR;
    private static final int MAX_RECYCLED_SAMPLES = 5;
    private static final int SORT_ORDER_BY_INDEX = 1;
    private static final int SORT_ORDER_BY_VALUE = 0;
    private static final int SORT_ORDER_NONE = -1;
    private static final Comparator<Sample> VALUE_COMPARATOR;
    private int currentSortOrder;
    private final int maxWeight;
    private int nextSampleIndex;
    private int recycledSampleCount;
    private final Sample[] recycledSamples;
    private final ArrayList<Sample> samples;
    private int totalWeight;

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.util.SlidingPercentile.1 */
    static class C07561 implements Comparator<Sample> {
        C07561() {
        }

        public int compare(Sample sample, Sample sample2) {
            return sample.index - sample2.index;
        }
    }

    /* renamed from: com.hanista.mobogram.messenger.exoplayer.util.SlidingPercentile.2 */
    static class C07572 implements Comparator<Sample> {
        C07572() {
        }

        public int compare(Sample sample, Sample sample2) {
            return sample.value < sample2.value ? SlidingPercentile.SORT_ORDER_NONE : sample2.value < sample.value ? SlidingPercentile.SORT_ORDER_BY_INDEX : SlidingPercentile.SORT_ORDER_BY_VALUE;
        }
    }

    private static class Sample {
        public int index;
        public float value;
        public int weight;

        private Sample() {
        }
    }

    static {
        INDEX_COMPARATOR = new C07561();
        VALUE_COMPARATOR = new C07572();
    }

    public SlidingPercentile(int i) {
        this.maxWeight = i;
        this.recycledSamples = new Sample[MAX_RECYCLED_SAMPLES];
        this.samples = new ArrayList();
        this.currentSortOrder = SORT_ORDER_NONE;
    }

    private void ensureSortedByIndex() {
        if (this.currentSortOrder != SORT_ORDER_BY_INDEX) {
            Collections.sort(this.samples, INDEX_COMPARATOR);
            this.currentSortOrder = SORT_ORDER_BY_INDEX;
        }
    }

    private void ensureSortedByValue() {
        if (this.currentSortOrder != 0) {
            Collections.sort(this.samples, VALUE_COMPARATOR);
            this.currentSortOrder = SORT_ORDER_BY_VALUE;
        }
    }

    public void addSample(int i, float f) {
        int i2;
        Sample sample;
        ensureSortedByIndex();
        if (this.recycledSampleCount > 0) {
            Sample[] sampleArr = this.recycledSamples;
            i2 = this.recycledSampleCount + SORT_ORDER_NONE;
            this.recycledSampleCount = i2;
            sample = sampleArr[i2];
        } else {
            sample = new Sample();
        }
        i2 = this.nextSampleIndex;
        this.nextSampleIndex = i2 + SORT_ORDER_BY_INDEX;
        sample.index = i2;
        sample.weight = i;
        sample.value = f;
        this.samples.add(sample);
        this.totalWeight += i;
        while (this.totalWeight > this.maxWeight) {
            i2 = this.totalWeight - this.maxWeight;
            sample = (Sample) this.samples.get(SORT_ORDER_BY_VALUE);
            if (sample.weight <= i2) {
                this.totalWeight -= sample.weight;
                this.samples.remove(SORT_ORDER_BY_VALUE);
                if (this.recycledSampleCount < MAX_RECYCLED_SAMPLES) {
                    Sample[] sampleArr2 = this.recycledSamples;
                    int i3 = this.recycledSampleCount;
                    this.recycledSampleCount = i3 + SORT_ORDER_BY_INDEX;
                    sampleArr2[i3] = sample;
                }
            } else {
                sample.weight -= i2;
                this.totalWeight -= i2;
            }
        }
    }

    public float getPercentile(float f) {
        ensureSortedByValue();
        float f2 = f * ((float) this.totalWeight);
        int i = SORT_ORDER_BY_VALUE;
        for (int i2 = SORT_ORDER_BY_VALUE; i2 < this.samples.size(); i2 += SORT_ORDER_BY_INDEX) {
            Sample sample = (Sample) this.samples.get(i2);
            i += sample.weight;
            if (((float) i) >= f2) {
                return sample.value;
            }
        }
        return this.samples.isEmpty() ? Float.NaN : ((Sample) this.samples.get(this.samples.size() + SORT_ORDER_NONE)).value;
    }
}

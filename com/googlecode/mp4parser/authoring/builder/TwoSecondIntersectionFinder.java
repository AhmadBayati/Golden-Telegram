package com.googlecode.mp4parser.authoring.builder;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import java.util.Arrays;

public class TwoSecondIntersectionFinder implements FragmentIntersectionFinder {
    private int fragmentLength;
    private Movie movie;

    public TwoSecondIntersectionFinder(Movie movie, int i) {
        this.fragmentLength = 2;
        this.movie = movie;
        this.fragmentLength = i;
    }

    public long[] sampleNumbers(Track track) {
        double d = 0.0d;
        for (Track track2 : this.movie.getTracks()) {
            double duration = (double) (track2.getDuration() / track2.getTrackMetaData().getTimescale());
            if (d < duration) {
                d = duration;
            }
        }
        int min = Math.min(((int) Math.ceil(d / ((double) this.fragmentLength))) - 1, track.getSamples().size());
        if (min < 1) {
            min = 1;
        }
        long[] jArr = new long[min];
        Arrays.fill(jArr, -1);
        jArr[0] = 1;
        int i = 0;
        long[] sampleDurations = track.getSampleDurations();
        int length = sampleDurations.length;
        min = 0;
        long j = 0;
        while (min < length) {
            long j2 = sampleDurations[min];
            int timescale = ((int) ((j / track.getTrackMetaData().getTimescale()) / ((long) this.fragmentLength))) + 1;
            if (timescale >= jArr.length) {
                break;
            }
            int i2 = i + 1;
            jArr[timescale] = (long) (i + 1);
            j += j2;
            min++;
            i = i2;
        }
        long j3 = (long) (i + 1);
        for (min = jArr.length - 1; min >= 0; min--) {
            if (jArr[min] == -1) {
                jArr[min] = j3;
            }
            j3 = jArr[min];
        }
        long[] jArr2 = new long[0];
        long[] jArr3 = jArr2;
        for (long j4 : jArr) {
            if (jArr3.length == 0 || jArr3[jArr3.length - 1] != j4) {
                jArr3 = Arrays.copyOf(jArr3, jArr3.length + 1);
                jArr3[jArr3.length - 1] = j4;
            }
        }
        return jArr3;
    }
}

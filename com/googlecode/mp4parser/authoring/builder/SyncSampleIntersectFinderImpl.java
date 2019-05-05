package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.OriginalFormatBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.util.Math;
import com.googlecode.mp4parser.util.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class SyncSampleIntersectFinderImpl implements FragmentIntersectionFinder {
    private static Logger LOG;
    private final int minFragmentDurationSeconds;
    private Movie movie;
    private Track referenceTrack;

    static {
        LOG = Logger.getLogger(SyncSampleIntersectFinderImpl.class.getName());
    }

    public SyncSampleIntersectFinderImpl(Movie movie, Track track, int i) {
        this.movie = movie;
        this.referenceTrack = track;
        this.minFragmentDurationSeconds = i;
    }

    private static long calculateTracktimesScalingFactor(Movie movie, Track track) {
        long j = 1;
        for (Track track2 : movie.getTracks()) {
            if (track2.getHandler().equals(track.getHandler()) && track2.getTrackMetaData().getTimescale() != track.getTrackMetaData().getTimescale()) {
                j = Math.lcm(j, track2.getTrackMetaData().getTimescale());
            }
        }
        return j;
    }

    static String getFormat(Track track) {
        Box sampleEntry = track.getSampleDescriptionBox().getSampleEntry();
        String type = sampleEntry.getType();
        return (type.equals(VisualSampleEntry.TYPE_ENCRYPTED) || type.equals(AudioSampleEntry.TYPE_ENCRYPTED) || type.equals(VisualSampleEntry.TYPE_ENCRYPTED)) ? ((OriginalFormatBox) Path.getPath(sampleEntry, "sinf/frma")).getDataFormat() : type;
    }

    public static List<long[]> getSyncSamplesTimestamps(Movie movie, Track track) {
        List<long[]> linkedList = new LinkedList();
        for (Track track2 : movie.getTracks()) {
            if (track2.getHandler().equals(track.getHandler())) {
                long[] syncSamples = track2.getSyncSamples();
                if (syncSamples != null && syncSamples.length > 0) {
                    linkedList.add(getTimes(track2, movie));
                }
            }
        }
        return linkedList;
    }

    private static long[] getTimes(Track track, Movie movie) {
        long[] syncSamples = track.getSyncSamples();
        long[] jArr = new long[syncSamples.length];
        long j = 0;
        int i = 0;
        long calculateTracktimesScalingFactor = calculateTracktimesScalingFactor(movie, track);
        for (int i2 = 1; ((long) i2) <= syncSamples[syncSamples.length - 1]; i2++) {
            if (((long) i2) == syncSamples[i]) {
                int i3 = i + 1;
                jArr[i] = j * calculateTracktimesScalingFactor;
                i = i3;
            }
            j += track.getSampleDurations()[i2 - 1];
        }
        return jArr;
    }

    public long[] getCommonIndices(long[] jArr, long[] jArr2, long j, long[]... jArr3) {
        List<Long> linkedList = new LinkedList();
        List linkedList2 = new LinkedList();
        for (int i = 0; i < jArr2.length; i++) {
            int i2 = 1;
            for (long[] binarySearch : jArr3) {
                i2 &= Arrays.binarySearch(binarySearch, jArr2[i]) >= 0 ? 1 : 0;
            }
            if (i2 != 0) {
                linkedList.add(Long.valueOf(jArr[i]));
                linkedList2.add(Long.valueOf(jArr2[i]));
            }
        }
        int i3;
        if (((double) linkedList.size()) < ((double) jArr.length) * 0.25d) {
            long longValue;
            String str = TtmlNode.ANONYMOUS_REGION_ID + String.format("%5d - Common:  [", new Object[]{Integer.valueOf(linkedList.size())});
            Object obj = str;
            for (Long longValue2 : linkedList) {
                longValue = longValue2.longValue();
                String stringBuilder = new StringBuilder(String.valueOf(obj)).append(String.format("%10d,", new Object[]{Long.valueOf(longValue)})).toString();
            }
            LOG.warning(new StringBuilder(String.valueOf(obj)).append("]").toString());
            obj = TtmlNode.ANONYMOUS_REGION_ID + String.format("%5d - In    :  [", new Object[]{Integer.valueOf(jArr.length)});
            for (long longValue3 : jArr) {
                obj = new StringBuilder(String.valueOf(obj)).append(String.format("%10d,", new Object[]{Long.valueOf(longValue3)})).toString();
            }
            LOG.warning(new StringBuilder(String.valueOf(obj)).append("]").toString());
            LOG.warning("There are less than 25% of common sync samples in the given track.");
            throw new RuntimeException("There are less than 25% of common sync samples in the given track.");
        }
        List list;
        if (((double) linkedList.size()) < ((double) jArr.length) * 0.5d) {
            LOG.fine("There are less than 50% of common sync samples in the given track. This is implausible but I'm ok to continue.");
        } else if (linkedList.size() < jArr.length) {
            LOG.finest("Common SyncSample positions vs. this tracks SyncSample positions: " + linkedList.size() + " vs. " + jArr.length);
        }
        List linkedList3 = new LinkedList();
        if (this.minFragmentDurationSeconds > 0) {
            Iterator it = linkedList2.iterator();
            long j2 = -1;
            for (Long longValue22 : linkedList) {
                if (!it.hasNext()) {
                    list = linkedList3;
                    break;
                }
                long longValue4 = longValue22.longValue();
                long longValue5 = ((Long) it.next()).longValue();
                if (j2 == -1 || (longValue5 - j2) / j >= ((long) this.minFragmentDurationSeconds)) {
                    linkedList3.add(Long.valueOf(longValue4));
                    j2 = longValue5;
                }
            }
            list = linkedList3;
        } else {
            list = linkedList;
        }
        long[] jArr4 = new long[list.size()];
        for (i3 = 0; i3 < jArr4.length; i3++) {
            jArr4[i3] = ((Long) list.get(i3)).longValue();
        }
        return jArr4;
    }

    public long[] sampleNumbers(Track track) {
        if ("vide".equals(track.getHandler())) {
            if (track.getSyncSamples() == null || track.getSyncSamples().length <= 0) {
                throw new RuntimeException("Video Tracks need sync samples. Only tracks other than video may have no sync samples.");
            }
            List syncSamplesTimestamps = getSyncSamplesTimestamps(this.movie, track);
            return getCommonIndices(track.getSyncSamples(), getTimes(track, this.movie), track.getTrackMetaData().getTimescale(), (long[][]) syncSamplesTimestamps.toArray(new long[syncSamplesTimestamps.size()][]));
        } else if ("soun".equals(track.getHandler())) {
            if (this.referenceTrack == null) {
                for (Track track2 : this.movie.getTracks()) {
                    if (track2.getSyncSamples() != null && "vide".equals(track2.getHandler()) && track2.getSyncSamples().length > 0) {
                        this.referenceTrack = track2;
                    }
                }
            }
            if (this.referenceTrack != null) {
                AudioSampleEntry audioSampleEntry;
                long j;
                double sampleRate;
                long[] sampleNumbers = sampleNumbers(this.referenceTrack);
                int size = this.referenceTrack.getSamples().size();
                long[] jArr = new long[sampleNumbers.length];
                long j2 = 192000;
                for (Track track22 : this.movie.getTracks()) {
                    if (getFormat(track).equals(getFormat(track22))) {
                        AudioSampleEntry audioSampleEntry2 = (AudioSampleEntry) track22.getSampleDescriptionBox().getSampleEntry();
                        if (audioSampleEntry2.getSampleRate() < 192000) {
                            j2 = audioSampleEntry2.getSampleRate();
                            double size2 = ((double) ((long) track22.getSamples().size())) / ((double) size);
                            long j3 = track22.getSampleDurations()[0];
                            for (r0 = 0; r0 < jArr.length; r0++) {
                                jArr[r0] = (long) Math.ceil((((double) (sampleNumbers[r0] - 1)) * size2) * ((double) j3));
                            }
                            audioSampleEntry = (AudioSampleEntry) track.getSampleDescriptionBox().getSampleEntry();
                            j = track.getSampleDurations()[0];
                            sampleRate = ((double) audioSampleEntry.getSampleRate()) / ((double) j2);
                            if (sampleRate == Math.rint(sampleRate)) {
                                throw new RuntimeException("Sample rates must be a multiple of the lowest sample rate to create a correct file!");
                            }
                            for (r0 = 0; r0 < jArr.length; r0++) {
                                jArr[r0] = (long) (1.0d + ((((double) jArr[r0]) * sampleRate) / ((double) j)));
                            }
                            return jArr;
                        }
                    }
                }
                audioSampleEntry = (AudioSampleEntry) track.getSampleDescriptionBox().getSampleEntry();
                j = track.getSampleDurations()[0];
                sampleRate = ((double) audioSampleEntry.getSampleRate()) / ((double) j2);
                if (sampleRate == Math.rint(sampleRate)) {
                    for (r0 = 0; r0 < jArr.length; r0++) {
                        jArr[r0] = (long) (1.0d + ((((double) jArr[r0]) * sampleRate) / ((double) j)));
                    }
                    return jArr;
                }
                throw new RuntimeException("Sample rates must be a multiple of the lowest sample rate to create a correct file!");
            }
            throw new RuntimeException("There was absolutely no Track with sync samples. I can't work with that!");
        } else {
            for (Track track222 : this.movie.getTracks()) {
                if (track222.getSyncSamples() != null && track222.getSyncSamples().length > 0) {
                    long[] sampleNumbers2 = sampleNumbers(track222);
                    long[] jArr2 = new long[sampleNumbers2.length];
                    double size3 = ((double) ((long) track.getSamples().size())) / ((double) track222.getSamples().size());
                    for (r0 = 0; r0 < jArr2.length; r0++) {
                        jArr2[r0] = ((long) Math.ceil(((double) (sampleNumbers2[r0] - 1)) * size3)) + 1;
                    }
                    return jArr2;
                }
            }
            throw new RuntimeException("There was absolutely no Track with sync samples. I can't work with that!");
        }
    }
}

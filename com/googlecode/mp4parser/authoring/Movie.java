package com.googlecode.mp4parser.authoring;

import com.googlecode.mp4parser.util.Matrix;
import java.util.LinkedList;
import java.util.List;

public class Movie {
    Matrix matrix;
    List<Track> tracks;

    public Movie() {
        this.matrix = Matrix.ROTATE_0;
        this.tracks = new LinkedList();
    }

    public Movie(List<Track> list) {
        this.matrix = Matrix.ROTATE_0;
        this.tracks = new LinkedList();
        this.tracks = list;
    }

    public static long gcd(long j, long j2) {
        return j2 == 0 ? j : gcd(j2, j % j2);
    }

    public void addTrack(Track track) {
        if (getTrackByTrackId(track.getTrackMetaData().getTrackId()) != null) {
            track.getTrackMetaData().setTrackId(getNextTrackId());
        }
        this.tracks.add(track);
    }

    public Matrix getMatrix() {
        return this.matrix;
    }

    public long getNextTrackId() {
        long j = 0;
        for (Track track : this.tracks) {
            j = j < track.getTrackMetaData().getTrackId() ? track.getTrackMetaData().getTrackId() : j;
        }
        return 1 + j;
    }

    public long getTimescale() {
        long timescale = ((Track) getTracks().iterator().next()).getTrackMetaData().getTimescale();
        long j = timescale;
        for (Track trackMetaData : getTracks()) {
            j = gcd(trackMetaData.getTrackMetaData().getTimescale(), j);
        }
        return j;
    }

    public Track getTrackByTrackId(long j) {
        for (Track track : this.tracks) {
            if (track.getTrackMetaData().getTrackId() == j) {
                return track;
            }
        }
        return null;
    }

    public List<Track> getTracks() {
        return this.tracks;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public void setTracks(List<Track> list) {
        this.tracks = list;
    }

    public String toString() {
        Object obj = "Movie{ ";
        for (Track track : this.tracks) {
            String stringBuilder = new StringBuilder(String.valueOf(obj)).append("track_").append(track.getTrackMetaData().getTrackId()).append(" (").append(track.getHandler()).append(") ").toString();
        }
        return new StringBuilder(String.valueOf(obj)).append('}').toString();
    }
}

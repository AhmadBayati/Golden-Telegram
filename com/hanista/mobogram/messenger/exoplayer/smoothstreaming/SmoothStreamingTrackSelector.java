package com.hanista.mobogram.messenger.exoplayer.smoothstreaming;

public interface SmoothStreamingTrackSelector {

    public interface Output {
        void adaptiveTrack(SmoothStreamingManifest smoothStreamingManifest, int i, int[] iArr);

        void fixedTrack(SmoothStreamingManifest smoothStreamingManifest, int i, int i2);
    }

    void selectTracks(SmoothStreamingManifest smoothStreamingManifest, Output output);
}

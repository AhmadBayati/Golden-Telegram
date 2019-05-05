package com.hanista.mobogram.messenger.exoplayer.hls;

public interface HlsTrackSelector {

    public interface Output {
        void adaptiveTrack(HlsMasterPlaylist hlsMasterPlaylist, Variant[] variantArr);

        void fixedTrack(HlsMasterPlaylist hlsMasterPlaylist, Variant variant);
    }

    void selectTracks(HlsMasterPlaylist hlsMasterPlaylist, Output output);
}

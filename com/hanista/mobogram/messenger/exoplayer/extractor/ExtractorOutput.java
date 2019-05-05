package com.hanista.mobogram.messenger.exoplayer.extractor;

import com.hanista.mobogram.messenger.exoplayer.drm.DrmInitData;

public interface ExtractorOutput {
    void drmInitData(DrmInitData drmInitData);

    void endTracks();

    void seekMap(SeekMap seekMap);

    TrackOutput track(int i);
}

package com.hanista.mobogram.messenger.exoplayer.smoothstreaming;

import android.content.Context;
import com.hanista.mobogram.messenger.exoplayer.chunk.VideoFormatSelectorUtil;
import com.hanista.mobogram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.TrackElement;
import com.hanista.mobogram.messenger.exoplayer.smoothstreaming.SmoothStreamingTrackSelector.Output;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.Arrays;
import java.util.List;

public final class DefaultSmoothStreamingTrackSelector implements SmoothStreamingTrackSelector {
    private final Context context;
    private final boolean filterProtectedHdContent;
    private final boolean filterVideoRepresentations;
    private final int streamElementType;

    private DefaultSmoothStreamingTrackSelector(int i, Context context, boolean z, boolean z2) {
        this.context = context;
        this.streamElementType = i;
        this.filterVideoRepresentations = z;
        this.filterProtectedHdContent = z2;
    }

    public static DefaultSmoothStreamingTrackSelector newAudioInstance() {
        return new DefaultSmoothStreamingTrackSelector(0, null, false, false);
    }

    public static DefaultSmoothStreamingTrackSelector newTextInstance() {
        return new DefaultSmoothStreamingTrackSelector(2, null, false, false);
    }

    public static DefaultSmoothStreamingTrackSelector newVideoInstance(Context context, boolean z, boolean z2) {
        return new DefaultSmoothStreamingTrackSelector(1, context, z, z2);
    }

    public void selectTracks(SmoothStreamingManifest smoothStreamingManifest, Output output) {
        for (int i = 0; i < smoothStreamingManifest.streamElements.length; i++) {
            TrackElement[] trackElementArr = smoothStreamingManifest.streamElements[i].tracks;
            if (smoothStreamingManifest.streamElements[i].type == this.streamElementType) {
                if (this.streamElementType == 1) {
                    int[] selectVideoFormatsForDefaultDisplay;
                    if (this.filterVideoRepresentations) {
                        Context context = this.context;
                        List asList = Arrays.asList(trackElementArr);
                        boolean z = this.filterProtectedHdContent && smoothStreamingManifest.protectionElement != null;
                        selectVideoFormatsForDefaultDisplay = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(context, asList, null, z);
                    } else {
                        selectVideoFormatsForDefaultDisplay = Util.firstIntegersArray(trackElementArr.length);
                    }
                    if (r5 > 1) {
                        output.adaptiveTrack(smoothStreamingManifest, i, selectVideoFormatsForDefaultDisplay);
                    }
                    for (int fixedTrack : selectVideoFormatsForDefaultDisplay) {
                        output.fixedTrack(smoothStreamingManifest, i, fixedTrack);
                    }
                } else {
                    for (int i2 = 0; i2 < trackElementArr.length; i2++) {
                        output.fixedTrack(smoothStreamingManifest, i, i2);
                    }
                }
            }
        }
    }
}

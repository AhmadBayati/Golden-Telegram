package com.hanista.mobogram.messenger.exoplayer.dash;

import android.content.Context;
import com.hanista.mobogram.messenger.exoplayer.chunk.VideoFormatSelectorUtil;
import com.hanista.mobogram.messenger.exoplayer.dash.DashTrackSelector.Output;
import com.hanista.mobogram.messenger.exoplayer.dash.mpd.AdaptationSet;
import com.hanista.mobogram.messenger.exoplayer.dash.mpd.MediaPresentationDescription;
import com.hanista.mobogram.messenger.exoplayer.dash.mpd.Period;
import com.hanista.mobogram.messenger.exoplayer.util.Util;
import java.util.List;

public final class DefaultDashTrackSelector implements DashTrackSelector {
    private final int adaptationSetType;
    private final Context context;
    private final boolean filterProtectedHdContent;
    private final boolean filterVideoRepresentations;

    private DefaultDashTrackSelector(int i, Context context, boolean z, boolean z2) {
        this.adaptationSetType = i;
        this.context = context;
        this.filterVideoRepresentations = z;
        this.filterProtectedHdContent = z2;
    }

    public static DefaultDashTrackSelector newAudioInstance() {
        return new DefaultDashTrackSelector(1, null, false, false);
    }

    public static DefaultDashTrackSelector newTextInstance() {
        return new DefaultDashTrackSelector(2, null, false, false);
    }

    public static DefaultDashTrackSelector newVideoInstance(Context context, boolean z, boolean z2) {
        return new DefaultDashTrackSelector(0, context, z, z2);
    }

    public void selectTracks(MediaPresentationDescription mediaPresentationDescription, int i, Output output) {
        Period period = mediaPresentationDescription.getPeriod(i);
        for (int i2 = 0; i2 < period.adaptationSets.size(); i2++) {
            AdaptationSet adaptationSet = (AdaptationSet) period.adaptationSets.get(i2);
            if (adaptationSet.type == this.adaptationSetType) {
                int i3;
                if (this.adaptationSetType == 0) {
                    int[] selectVideoFormatsForDefaultDisplay;
                    if (this.filterVideoRepresentations) {
                        Context context = this.context;
                        List list = adaptationSet.representations;
                        boolean z = this.filterProtectedHdContent && adaptationSet.hasContentProtection();
                        selectVideoFormatsForDefaultDisplay = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(context, list, null, z);
                    } else {
                        selectVideoFormatsForDefaultDisplay = Util.firstIntegersArray(adaptationSet.representations.size());
                    }
                    if (r6 > 1) {
                        output.adaptiveTrack(mediaPresentationDescription, i, i2, selectVideoFormatsForDefaultDisplay);
                    }
                    for (int fixedTrack : selectVideoFormatsForDefaultDisplay) {
                        output.fixedTrack(mediaPresentationDescription, i, i2, fixedTrack);
                    }
                } else {
                    for (i3 = 0; i3 < adaptationSet.representations.size(); i3++) {
                        output.fixedTrack(mediaPresentationDescription, i, i2, i3);
                    }
                }
            }
        }
    }
}

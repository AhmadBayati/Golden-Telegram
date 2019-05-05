package com.hanista.mobogram.messenger.exoplayer.hls;

import android.content.Context;
import android.text.TextUtils;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.hanista.mobogram.messenger.exoplayer.chunk.VideoFormatSelectorUtil;
import com.hanista.mobogram.messenger.exoplayer.hls.HlsTrackSelector.Output;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DefaultHlsTrackSelector implements HlsTrackSelector {
    private static final int TYPE_AUDIO = 1;
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_SUBTITLE = 2;
    private final Context context;
    private final int type;

    private DefaultHlsTrackSelector(Context context, int i) {
        this.context = context;
        this.type = i;
    }

    public static DefaultHlsTrackSelector newAudioInstance() {
        return new DefaultHlsTrackSelector(null, TYPE_AUDIO);
    }

    public static DefaultHlsTrackSelector newDefaultInstance(Context context) {
        return new DefaultHlsTrackSelector(context, TYPE_DEFAULT);
    }

    public static DefaultHlsTrackSelector newSubtitleInstance() {
        return new DefaultHlsTrackSelector(null, TYPE_SUBTITLE);
    }

    private static boolean variantHasExplicitCodecWithPrefix(Variant variant, String str) {
        Object obj = variant.format.codecs;
        if (TextUtils.isEmpty(obj)) {
            return false;
        }
        String[] split = obj.split("(\\s*,\\s*)|(\\s*$)");
        for (int i = TYPE_DEFAULT; i < split.length; i += TYPE_AUDIO) {
            if (split[i].startsWith(str)) {
                return true;
            }
        }
        return false;
    }

    public void selectTracks(HlsMasterPlaylist hlsMasterPlaylist, Output output) {
        int i = TYPE_DEFAULT;
        if (this.type == TYPE_AUDIO || this.type == TYPE_SUBTITLE) {
            List list = this.type == TYPE_AUDIO ? hlsMasterPlaylist.audios : hlsMasterPlaylist.subtitles;
            if (list != null && !list.isEmpty()) {
                while (i < list.size()) {
                    output.fixedTrack(hlsMasterPlaylist, (Variant) list.get(i));
                    i += TYPE_AUDIO;
                }
                return;
            }
            return;
        }
        ArrayList arrayList;
        ArrayList arrayList2 = new ArrayList();
        int[] selectVideoFormatsForDefaultDisplay = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(this.context, hlsMasterPlaylist.variants, null, false);
        for (int i2 = TYPE_DEFAULT; i2 < selectVideoFormatsForDefaultDisplay.length; i2 += TYPE_AUDIO) {
            arrayList2.add(hlsMasterPlaylist.variants.get(selectVideoFormatsForDefaultDisplay[i2]));
        }
        ArrayList arrayList3 = new ArrayList();
        Collection arrayList4 = new ArrayList();
        for (int i3 = TYPE_DEFAULT; i3 < arrayList2.size(); i3 += TYPE_AUDIO) {
            Variant variant = (Variant) arrayList2.get(i3);
            if (variant.format.height > 0 || variantHasExplicitCodecWithPrefix(variant, "avc")) {
                arrayList3.add(variant);
            } else if (variantHasExplicitCodecWithPrefix(variant, AudioSampleEntry.TYPE3)) {
                arrayList4.add(variant);
            }
        }
        if (arrayList3.isEmpty()) {
            if (arrayList4.size() < arrayList2.size()) {
                arrayList2.removeAll(arrayList4);
            }
            arrayList = arrayList2;
        } else {
            arrayList = arrayList3;
        }
        if (arrayList.size() > TYPE_AUDIO) {
            Variant[] variantArr = new Variant[arrayList.size()];
            arrayList.toArray(variantArr);
            output.adaptiveTrack(hlsMasterPlaylist, variantArr);
        }
        while (i < arrayList.size()) {
            output.fixedTrack(hlsMasterPlaylist, (Variant) arrayList.get(i));
            i += TYPE_AUDIO;
        }
    }
}
